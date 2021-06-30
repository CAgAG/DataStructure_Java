package SimpleSort;

import java.util.Arrays;

/**
 * @author CAgAG
 * @version 1.0
 * @date 2021/6/26
 */
public class BubbleSort {
    public static void sort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        for (int i = 0; i < arr.length - 1; i++) {
            boolean flag = true;
            for (int j = 0; j < arr.length - 1; j++) {
                if (arr[j] < arr[j + 1]) {
                    int tp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tp;
                    flag = false;
                }
            }
            if (flag) {
                return;
            }
        }

    }

    public static void main(String[] args) {
        int[] arr = {11, 48, 15, 7};
        sort(arr);
        System.out.println(Arrays.toString(arr));
    }
}
