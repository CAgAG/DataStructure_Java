package AdvancedSort;

import java.util.Arrays;

/**
 * @author CAgAG
 * @version 1.0
 * @date 2021/6/27
 */
public class MergeSort {
    public static void main(String[] args) {
        int[] arr = {1, 34, 45, 67, 7};
        System.out.println(Arrays.toString(mergeSort(arr)));
    }

    // 递归调用
    public static int[] mergeSort(int[] arr) {
        if (arr.length < 2) {
            return arr;
        }
        int mid = arr.length / 2;
        // [): 左闭右开
        int[] left = Arrays.copyOfRange(arr, 0, mid);
        int[] right = Arrays.copyOfRange(arr, mid, arr.length);
        return merge(mergeSort(left), mergeSort(right));
    }

    // 将2个排序好的数组进行合并
    public static int[] merge(int[] left, int[] right) {
        int[] result = new int[left.length + right.length];
        // i->left, j->right
        for (int index = 0, i = 0, j = 0; index < result.length; index++) {
            if (i >= left.length) {
                result[index] = right[j++];
            } else if (j >= right.length) {
                result[index] = left[i++];
            } else if (left[i] > right[j]) {
                result[index] = right[j++];
            } else {
                result[index] = left[i++];
            }
        }
        return result;
    }
}
