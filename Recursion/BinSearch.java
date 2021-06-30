package Recursion;

/**
 * @author CAgAG
 * @version 1.0
 * @date 2021/6/27
 */
public class BinSearch {

    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 4, 5};
        int key = 2;
        System.out.println(binSearch(arr, 0, arr.length - 1, key));
    }

    public static int binSearch(int[] arr, int low, int high, int key) {
        if (low <= high) {
            int mid = (low + high) / 2;
            if (key == arr[mid]) {
                return mid;
            } else if (key < arr[mid]) {
                return binSearch(arr, low, mid - 1, key);
            } else {
                return binSearch(arr, mid + 1, high, key);
            }

        } else {
            return -1;
        }
    }
}
