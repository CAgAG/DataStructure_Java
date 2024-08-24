import drain_java.Drain;
import drain_java.Tokenizer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

class MyCallable implements Callable<List<Map<String, List<String>>>> {
    private List<String> messages;
    private Drain model;

    public MyCallable(List<String> messages) {
        SkipTokenizer my_tokenizer = MyTokenizer.skip_tokenizer();
        my_tokenizer.skip_n = 1;

        this.model = Drain.drainBuilder()
                .similarityThreshold(0.4)
                .maxChildPerNode(100)
                .depth(4)
                .delimiters(" ")
                .tokenizer(my_tokenizer)
                .build();
        this.messages = messages;
    }

    @Override
    public List<Map<String, List<String>>> call() throws Exception {
        for (String data : this.messages) {
            this.model.parseLogMessage(data);
        }
        Map<String, List<String>> group2template = this.model.getGroup2template();
        Map<String, List<String>> group2msg = this.model.getGroup2msg();

        List<Map<String, List<String>>> ret = new ArrayList<>();
        ret.add(group2template);
        ret.add(group2msg);
        return ret;
    }
}

class ThreadModel {
    private long merge_i = 0;

    // 平均分为n个子数组
    public static List<List<String>> splitList(List<String> list, int n) {
        List<List<String>> result = new ArrayList<>();
        int size = list.size();
        int subListSize = size / n;
        int remainder = size % n;

        int startIndex = 0;
        for (int i = 0; i < n; i++) {
            int endIndex = startIndex + subListSize;
            if (remainder > 0) {
                endIndex++;
                remainder--;
            }
            result.add(new ArrayList<>(list.subList(startIndex, endIndex)));
            startIndex = endIndex;
        }
        return result;
    }

    public static List<String> readFileToList(String filePath) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    // 模板对比
    public static List<String> templateListsEqual(List<String> list1, List<String> list2) {
        int m = list1.size();
        int n = list2.size();

        if (m < n) {
            for (int i = 0; i < list1.size(); i++) {
                String item1 = list1.get(i).trim();
                String item2 = list2.get(i).trim();

                if (!item1.equals(item2) && !item1.equals("<*>") && !item2.equals("<*>")) {
                    return new ArrayList<>();
                }
            }
            return list1;
        } else {
            for (int i = 0; i < list2.size(); i++) {
                String item1 = list1.get(i).trim();
                String item2 = list2.get(i).trim();

                if (!item1.equals(item2) && !item1.equals("<*>") && !item2.equals("<*>")) {
                    return new ArrayList<>();
                }
            }
            return list2;
        }
    }

    public List<Map<String, List<String>>> mergeData(Map<String, List<String>> left_g2t,
                                                     Map<String, List<String>> left_g2m,
                                                     Map<String, List<String>> right_g2t,
                                                     Map<String, List<String>> right_g2m) {
        List<Map<String, List<String>>> ret = new ArrayList<>();
        if (left_g2t.size() == 0) {
            ret.add(right_g2t);
            ret.add(right_g2m);
            return ret;
        }

        Map<String, Boolean> visited = new HashMap<>();
        // 遍历Map的键值对
        for (Map.Entry<String, List<String>> entry : left_g2t.entrySet()) {
            String left_group_id = entry.getKey();
            List<String> left_template = entry.getValue();

            for (Map.Entry<String, List<String>> entry2 : right_g2t.entrySet()) {
                String right_group_id = entry2.getKey();
                if (visited.getOrDefault(right_group_id, false)) {
                    continue;
                }
                List<String> right_template = entry2.getValue();

                List<String> templateListsEqualList = templateListsEqual(left_template, right_template);
                if (templateListsEqualList.size() != 0) {
                    visited.put(right_group_id, true);
                    left_g2m.get(left_group_id).addAll(right_g2m.get(right_group_id));
                    left_g2t.put(left_group_id, templateListsEqualList);
                }
            }
        }
        for (Map.Entry<String, List<String>> entry2 : right_g2t.entrySet()) {
            String right_group_id = entry2.getKey();
            if (visited.getOrDefault(right_group_id, false)) {
                continue;
            }
            List<String> right_template = entry2.getValue();

            String merge_group_id = String.format("merge_group_%d", this.merge_i);
            this.merge_i++;
            left_g2t.put(merge_group_id, right_template);
            left_g2m.put(merge_group_id, right_g2m.get(right_group_id));
        }
        ret.add(left_g2t);
        ret.add(left_g2m);
        return ret;
    }

    public void loop() {
        int thread_n = 10; // 自定义线程个数
        ExecutorService executor = Executors.newFixedThreadPool(thread_n);
        CompletionService<List<Map<String, List<String>>>> completionService = new ExecutorCompletionService<>(executor);

        String filePath = "./src/HDFS_2k.log"; // 文件路径
        List<String> lines = readFileToList(filePath);
        // 复制数量
        lines = Utils.ListToTargetSize(lines, 1000000);

        List<List<String>> thread_list = splitList(lines, thread_n);
        for (int i = 0; i < thread_n; i++) {
            completionService.submit(new MyCallable(thread_list.get(i)));
        }

        List<Map<String, List<String>>> result;
        Map<String, List<String>> ret_group2template = new HashMap<>();
        Map<String, List<String>> ret_group2msg = new HashMap<>();
        // 记录开始时间
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < thread_n; i++) {
            try {
                Future<List<Map<String, List<String>>>> future = completionService.take(); // 获取已完成的任务的Future对象
                result = future.get(); // 获取任务的结果
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                break;
            }

            Map<String, List<String>> group2template = result.get(0);  // key: group_id: template
            Map<String, List<String>> group2msg = result.get(1);  // key: group_id: msg
            List<Map<String, List<String>>> ret = mergeData(ret_group2template, ret_group2msg, group2template, group2msg);
            ret_group2template = ret.get(0);
            ret_group2msg = ret.get(1);
        }
        // 记录结束时间
        long endTime = System.currentTimeMillis();
        // 计算运行时间（单位：毫秒）
        long elapsedTime = endTime - startTime;
        // 将毫秒转换为秒
        double elapsedTimeInSeconds = elapsedTime / 1000.0;
        // 输出运行时间（单位：秒）
        System.out.println("代码运行时间：" + elapsedTimeInSeconds + "秒");
        executor.shutdown(); // 关闭线程池

        // group id: template
        // 假设已经有一个Map<String, List<String>>类型的变量group2msg
        Map<String, List<String>> group2template = ret_group2template;
        // 遍历Map的键值对
        for (Map.Entry<String, List<String>> entry : group2template.entrySet()) {
            String key = entry.getKey(); // 获取键
            List<String> value = entry.getValue(); // 获取值（List<String>类型）

            // 输出键和值
            System.out.print("Key: " + key);
            System.out.print(" Template: ");

            // 如果需要遍历List中的每个元素，可以再添加一层循环
            for (String msg : value) {
                System.out.print(msg + " ");
            }
            System.out.println();
        }

        System.out.println("=======================");
        // group id: msg
        // 假设已经有一个Map<String, List<String>>类型的变量group2msg
        Map<String, List<String>> group2msg = ret_group2msg;
        // 遍历Map的键值对
        for (Map.Entry<String, List<String>> entry : group2msg.entrySet()) {
            String key = entry.getKey(); // 获取键
            List<String> value = entry.getValue(); // 获取值（List<String>类型）

            // 输出键和值
            System.out.print("Key: " + key);
            System.out.println(", Message length: " + value.size());
        }

    }
}

public class ThreadMain {
    public static void main(String[] args) {
        ThreadModel tm = new ThreadModel();

        tm.loop();
    }
}

/*
代码运行时间：1.855秒
Key: group_8 Template: 211541 18 INFO dfs.DataNode: 10.250.15.198:50010 Starting thread
Key: group_5 Template: <*> <*> INFO dfs.FSDataset: Deleting block <*>
Key: group_4 Template: <*> 13 INFO dfs.DataBlockScanner: Verification succeeded for
Key: group_7 Template: <*> <*> WARN dfs.DataNode$DataXceiver: <*> exception while
Key: group_6 Template: <*> <*> INFO dfs.DataNode$DataXceiver: <*> Served block
Key: group_1 Template: <*> <*> INFO dfs.FSNamesystem: BLOCK* <*> <*>
Key: group_0 Template: <*> <*> INFO dfs.DataNode$PacketResponder: PacketResponder <*> for
Key: group_3 Template: <*> <*> INFO dfs.DataNode$DataXceiver: Receiving block <*>
Key: group_2 Template: <*> <*> INFO <*> Received block <*>
=======================
Key: group_8, Message length: 500
Key: group_5, Message length: 131500
Key: group_4, Message length: 10000
Key: group_7, Message length: 40000
Key: group_6, Message length: 40000
Key: group_1, Message length: 329500
Key: group_0, Message length: 155500
Key: group_3, Message length: 146000
Key: group_2, Message length: 147000

Process finished with exit code 0
 */