package AdvancedSort;

import java.util.Arrays;

/**
 * @author CAgAG
 * @version 1.0
 * @date 2021/6/27
 */
public class CountSort {
    public static void main(String[] args) {
        int[] arr = {1, 32, 3, 21, 33, 20};
        System.out.println(Arrays.toString(countingSort(arr)));
    }

    public static int[] countingSort(int[] arr) {
        int min = arr[0];
        int max = arr[0];
        // 找到最大, 最小值
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
            if (arr[i] < min) {
                min = arr[i];
            }
        }

        // 额外数组
        int[] bucket = new int[max - min + 1];
        // 初始化计数器为0
        Arrays.fill(bucket, 0);
        for (int i = 0; i < arr.length; i++) {
            // 计数
            bucket[arr[i] - min]++;
        }

        // 回填数据, index->arr, i->bucket
        int index = 0, i = 0;
        while (index < arr.length) {
            if (bucket[i] != 0) {
                arr[index] = i + min;
                // 计数减一
                bucket[i]--;
                index++;
            } else {
                i++;
            }
        }
        return arr;
    }
}
