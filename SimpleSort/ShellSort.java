package SimpleSort;

import java.util.Arrays;

/**
 * @author CAgAG
 * @version 1.0
 * @date 2021/6/26
 */
public class ShellSort {

    public static void sort(int[] arr) {
        int length = arr.length;
        int tp;
        int gap = length / 2;
        while (gap > 0) {
            for (int i = gap; i < length; i++) {
                int pre_index = i - gap;
                tp = arr[i];
                // InsertSort
                while (pre_index >= 0 && arr[pre_index] < tp) {
                    arr[pre_index + gap] = arr[pre_index];
                    pre_index -= gap;
                }
                arr[pre_index + gap] = tp;
            }
            gap /= 2;
        }
    }

    public static void main(String[] args) {
        int[] arr = {11, 48, 15, 7};
        sort(arr);
        System.out.println(Arrays.toString(arr));
    }
}
