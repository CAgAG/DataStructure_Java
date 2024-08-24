import drain_java.Drain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;


public class Main {
    public static void main(String[] args) throws IOException {
        Drain drain = Drain.drainBuilder()
                .similarityThreshold(0.4)
                .maxChildPerNode(100)
                .depth(4)
                .delimiters(" ")
                .tokenizer(MyTokenizer.skip_tokenizer())
                .build();

        // 指定要读取的文件路径
        String filePath = "./src/HDFS_2k.log";

        try {
            // 创建File对象
            File file;
            file = new File(filePath);

            // 创建BufferedReader对象
            BufferedReader reader = new BufferedReader(new FileReader(file));

            // 逐行读取文件内容并打印
            String line;
            while ((line = reader.readLine()) != null) {
                drain.parseLogMessage(line);
            }

            // 关闭BufferedReader对象
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // group id: template
        // 假设已经有一个Map<String, List<String>>类型的变量group2msg
        Map<String, List<String>> group2template = drain.getGroup2template();
        // 遍历Map的键值对
        for (Map.Entry<String, List<String>> entry : group2template.entrySet()) {
            String key = entry.getKey(); // 获取键
            List<String> value = entry.getValue(); // 获取值（List<String>类型）

            // 输出键和值
            System.out.println("Key: " + key);
            System.out.print("Template: ");

            // 如果需要遍历List中的每个元素，可以再添加一层循环
            for (String msg : value) {
                System.out.print(msg + " ");
            }
            System.out.println();
        }

        System.out.println("=======================");
        // group id: msg
        // 假设已经有一个Map<String, List<String>>类型的变量group2msg
        Map<String, List<String>> group2msg = drain.getGroup2msg();
        // 遍历Map的键值对
        for (Map.Entry<String, List<String>> entry : group2msg.entrySet()) {
            String key = entry.getKey(); // 获取键
            List<String> value = entry.getValue(); // 获取值（List<String>类型）

            // 输出键和值
            System.out.print("Key: " + key);
            System.out.println(", Message length: " + value.size());

            // 如果需要遍历List中的每个元素，可以再添加一层循环
            for (String msg : value) {
                System.out.println("Message: " + msg);
            }
        }
    }
}