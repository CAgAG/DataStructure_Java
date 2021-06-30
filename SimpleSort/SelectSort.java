package SimpleSort;

import java.util.Arrays;

/**
 * @author CAgAG
 * @version 1.0
 * @date 2021/6/26
 */
public class SelectSort {

    public static void sort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }

        for (int i = 0; i < arr.length; i++) {
            int min_index = i;
            for (int j = i; j < arr.length; j++) {
                if (arr[j] < arr[min_index]) {
                    min_index = j;
                }
                int tp = arr[min_index];
                arr[min_index] = arr[i];
                arr[i] = tp;
            }
        }
    }

    public static void main(String[] args) {
        int[] arr = {11, 48, 15, 7};
        sort(arr);
        System.out.println(Arrays.toString(arr));
    }
}
