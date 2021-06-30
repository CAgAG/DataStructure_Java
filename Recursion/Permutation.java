package Recursion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author CAgAG
 * @version 1.0
 * @date 2021/6/27
 */
public class Permutation {
    // 字母的长度
    static int size;
    static char[] ch = new char[100];

    public static void shift(int n) {
        // n: 字母长度
        if (n == 1) {
            return;
        }
        // 向前移动n-1次， 循环n次
        for (int i = 0; i < n; i++) {
            System.out.println("==> " + "i:" + i +" n:" + n);
            shift(n - 1);

            if (n == 2) {
                // 词汇输出
                display();
            }
            // 移动函数
            move(n);
        }
    }

    private static void display() {
        for (int i = 0; i < size; i++) {
            System.out.print(ch[i]);
        }
        System.out.println();
    }

    private static void move(int n) {
        // 把最后的字母向前移动n-1位
        int j;
        int pos = size - n;
        char tp = ch[pos];
        for (j = pos + 1; j < size; j++) {
            ch[j - 1] = ch[j];
        }
        ch[j - 1] = tp;
    }

    public static void main(String[] args) throws IOException {
        InputStreamReader is = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(is);
        String s = br.readLine();
        size = s.length();
        for (int i = 0; i < size; i++) {
            ch[i] = s.charAt(i);
        }
        shift(size);
    }
}
