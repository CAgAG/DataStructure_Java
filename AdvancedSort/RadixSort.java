package AdvancedSort;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author CAgAG
 * @version 1.0
 * @date 2021/6/27
 */
public class RadixSort {
    public static void main(String[] args) {
        int[] arr = {22, 11, 44, 35, 6, 734, 224, 1980, 1};
        System.out.println(Arrays.toString(radixSort(arr)));
    }

    public static int[] radixSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return arr;
        }
        int max = arr[0];
        for (int i = 0; i < arr.length; i++) {
            max = Math.max(arr[i], max);
        }
        // 确定最大数字有几位
        int maxDigit = 0;
        while (max != 0) {
            max /= 10;
            maxDigit++;
        }
        // 每一位的倍数差
        int mod = 10;
        // 定义一个除法的基准值
        int div = 1;
        ArrayList<ArrayList<Integer>> bucketList = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            bucketList.add(new ArrayList<Integer>());
        }

        for (int i = 0; i < maxDigit; i++, mod *= 10, div *= 10) {
            // 填入
            for (int j = 0; j < arr.length; j++) {
                // 去除位数: 个位, 十位 ...
                int num = (arr[j] % mod) / div;
                bucketList.get(num).add(arr[j]);
            }
            int index = 0;
            // 回填
            for (int j = 0; j < bucketList.size(); j++) {
                for (int k = 0; k < bucketList.get(j).size(); k++) {
                    arr[index++] = bucketList.get(j).get(k);
                }
                // 清空
                bucketList.get(j).clear();
            }
        }
        return arr;
    }

}
