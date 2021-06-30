package Recursion;

/**
 * @author CAgAG
 * @version 1.0
 * @date 2021/6/27
 */
public class Number {

    // 三角数字
    public static int sum(int n) {
        if (n == 1) {
            return 1;
        }
        return n + sum(n - 1);
    }

    // 阶乘
    public static int muti(int n) {
        if (n == 1) {
            return 1;
        }
        return n * muti(n - 1);
    }

    // 尾阶乘
    public static int tail_muti(int n, int result) {
        if (n == 1) {
            return result;
        }
        return tail_muti(n - 1, n * result);
    }

    public static void main(String[] args) {
        System.out.println(sum(5));
        System.out.println(muti(5));

        System.out.println(tail_muti(5, 1));
    }
}
