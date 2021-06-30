package Combination;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author CAgAG
 * @version 1.0
 * @date 2021/6/27
 */
public class Java8Combine {

    /**
     * 排列组合（字符重复排列） - 有重复字符串的排列组合
     * 内存占用：需注意结果集大小对内存的占用（list:10位，length:8，结果集:[10 ^ 8 = 100000000]，内存占用:约7G）
     *
     * @param list   待排列组合字符集合
     * @param length 排列组合生成的字符串长度
     * @return 指定长度的排列组合后的字符串集合
     */
    public static List<String> permutation(List<String> list, int length) {
        Stream<String> stream = list.stream();
        for (int n = 1; n < length; n++) {
            stream = stream.flatMap(str -> list.stream().map(str::concat));
        }
        return stream.collect(Collectors.toList());
    }

    /**
     * 排列组合(字符不重复排列) - 无重复字符串的排列组合
     * 内存占用：需注意结果集大小对内存的占用（list:10位，length:8，结果集:[10! / (10-8)! = 1814400]）
     * @param list 待排列组合字符集合(忽略重复字符)
     * @param length 排列组合生成的字符串长度
     * @return 指定长度的排列组合后的字符串集合
     */
    public static List<String> permutationNoRepeat(List<String> list, int length) {
        Stream<String> stream = list.stream().distinct();
        for (int n = 1; n < length; n++) {
            stream = stream.flatMap(str -> list.stream()
                    .filter(temp -> !str.contains(temp))
                    .map(str::concat));
        }
        return stream.collect(Collectors.toList());
    }
}
