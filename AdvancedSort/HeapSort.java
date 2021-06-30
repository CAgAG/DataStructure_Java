package AdvancedSort;

import java.util.Arrays;

/**
 * @author CAgAG
 * @version 1.0
 * @date 2021/6/27
 */
public class HeapSort {
    private static int len;

    public static void main(String[] args) {
        int[] arr = {1, 32, 3, 21, 33, 20};
        System.out.println(Arrays.toString(heapSort(arr)));
    }

    public static int[] heapSort(int[] arr) {
        len = arr.length;
        // 构建大顶堆
        buildMaxHeap(arr);
        // 将顶部元素, 与无序区的最后一个元素交换位置
        while (len > 0) {
            // 0表示大顶元素, len-1表示最后一个元素
            swap(arr, 0, len - 1);
            // 无序区长度减一
            len--;
            // 继续调整
            adjustHeap(arr, 0);
        }
        return arr;
    }

    // 构建大顶堆
    public static void buildMaxHeap(int[] arr) {
        for (int i = len / 2; i >= 0; i--) {
            // 调整大顶堆
            adjustHeap(arr, i);
        }
    }

    // 调整大顶堆
    private static void adjustHeap(int[] arr, int i) {
        int maxIndex = i;
        // 左子树索引, 且左子树大于父节点, 最大指针指向左子树
        int tIndex = i * 2;
        if (tIndex < len && arr[tIndex] > arr[maxIndex]) {
            maxIndex = tIndex;
        }
        // 右子树索引, 且右子树大于父节点, 最大指针指向右子树
        tIndex = i * 2 + 1;
        if (tIndex < len && arr[tIndex] > arr[maxIndex]) {
            maxIndex = tIndex;
        }
        // maxIndex发生变化, 父节点不是最大值, 调整, 交换保证父节点始终是最大值
        if (maxIndex != i) {
            swap(arr, maxIndex, i);
            adjustHeap(arr, maxIndex);
        }
    }


    // 交换元素
    public static void swap(int[] arr, int i, int j) {
        int tp = arr[i];
        arr[i] = arr[j];
        arr[j] = tp;
    }
}
