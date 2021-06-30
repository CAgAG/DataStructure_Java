package AdvancedSort;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author CAgAG
 * @version 1.0
 * @date 2021/6/27
 */
public class BucketSort {
    public static void main(String[] args) {
        ArrayList<Integer> arr = new ArrayList<Integer>();
        arr.add(5);
        arr.add(77);
        arr.add(11);
        arr.add(3);
        arr.add(76);
        arr.add(76);

        int bucketSize = 3;
        System.out.println(bucketSort(arr, bucketSize));
    }

    public static ArrayList<Integer> bucketSort(ArrayList<Integer> arr, int bucketSize) {
        if (arr == null || arr.size() < 2) {
            return arr;
        }
        int max = arr.get(0);
        int min = arr.get(0);
        // 找到最大, 最小值
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i) > max) {
                max = arr.get(i);
            }
            if (arr.get(i) < min) {
                min = arr.get(i);
            }
        }

        // +1避免bucketcount为0
        int bucketCount = (max - min) / bucketSize + 1;
        ArrayList<ArrayList<Integer>> bucketArr = new ArrayList<>(bucketCount);
        // 回填的list
        ArrayList<Integer> resultArr = new ArrayList<>();
        // 初始化桶
        for (int i = 0; i < bucketCount; i++) {
            // 区别于计数排序
            bucketArr.add(new ArrayList<Integer>());
        }
        // 循环原始得数据, 将原始数据进行填充 (arr.get(i) - min) / bucketSize
        for (int i = 0; i < arr.size(); i++) {
            bucketArr.get((arr.get(i) - min) / bucketSize).add(arr.get(i));
        }
        // 递归排序, 并回填到原数组
        for (int i = 0; i < bucketCount; i++) {
            // 有重复数据出现得时候的一个判断, 同时避免除0
            if (bucketSize == 1) {
                for (int j = 0; j < bucketArr.get(i).size(); j++) {
                    resultArr.add(bucketArr.get(i).get(j));
                }
            } else {
                if (bucketCount == 1) {
                    // 保证桶唯一, 同时避免递归死循环
                    bucketSize--;
                }
                // bucketSize不能为0 ==> (arr.get(i) - min) / bucketSize
                ArrayList<Integer> tp = bucketSort(bucketArr.get(i), bucketSize);
                // 将排序好的序列进行回填
                for (int j = 0; j < tp.size(); j++) {
                    resultArr.add(tp.get(j));
                }
            }
        }

        return resultArr;
    }
}
