package SimpleSort;

import java.util.Arrays;

/**
 * @author CAgAG
 * @version 1.0
 * @date 2021/6/26
 */
public class InsertSort {

    public static void sort(int[] arr) {
        int current;
        for (int i = 0; i < arr.length - 1; i++) {
            current = arr[i + 1];
            int pre_index = i;
            while (pre_index >= 0 && current < arr[pre_index]) {
                arr[pre_index + 1] = arr[pre_index];
                pre_index--;
            }
            arr[pre_index + 1] = current;
        }
    }

    public static void main(String[] args) {
        int[] arr = {11, 48, 15, 7};
        sort(arr);
        System.out.println(Arrays.toString(arr));
    }
}
