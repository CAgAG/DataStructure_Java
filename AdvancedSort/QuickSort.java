package AdvancedSort;

import java.util.Arrays;

/**
 * @author CAgAG
 * @version 1.0
 * @date 2021/6/27
 */
public class QuickSort {
    public static void main(String[] args) {
        int[] arr = {1, 32, 3, 21, 33, 20};
        System.out.println(Arrays.toString(quickSort(arr, 0, arr.length - 1)));
    }

    // 快速排序
    public static int[] quickSort(int[] arr, int start, int end) {
        int smallIndex = partition(arr, start, end);
        if (smallIndex > start) {
            quickSort(arr, start, smallIndex - 1);
        }
        if (smallIndex < end) {
            quickSort(arr, smallIndex + 1, end);
        }
        return arr;
    }

    // 分区操作
    public static int partition(int[] arr, int start, int end) {
        int pivot = (int) (start + Math.random() * (end - start + 1));
        // 将基准值移到最后
        swap(arr, pivot, end);
        // 最终比基准值大的索引, 用户换位置
        int smallIndex = start - 1; // 初始化, -1相当于还没找到， ++后从start开始
        for (int i = start; i <= end; i++) {
            if (arr[i] <= arr[end]) { // end -> pivot, 将第i个元素与基准值进行对比, 比基准值小进入
                smallIndex++;
                if (i > smallIndex) {
                    swap(arr, smallIndex, i);
                }
            }
        }
        return smallIndex;
    }

    // 交换元素
    public static void swap(int[] arr, int i, int j) {
        int tp = arr[i];
        arr[i] = arr[j];
        arr[j] = tp;
    }

}
