import drain_java.Tokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

// 策略模式：算法输入的数据

public class MyTokenizer {
    public static SkipTokenizer skip_tokenizer() {
        return new SkipTokenizer();
    }

}

class SkipTokenizer extends Tokenizer {
    public int skip_n = 0;
    public int remain_n = 7;

    // TODO: 提取日志字符串的词到 List, delimiters: 分隔符
    public List<String> tokenize(String content, String delimiters) {
        StringTokenizer stringTokenizer = new StringTokenizer(content, delimiters);
        // 初始化
        List<String> tokens = new ArrayList<>();

        int cur_i = 0;
        while (stringTokenizer.hasMoreTokens()) {
            String trimmedToken = stringTokenizer.nextToken().trim();  // 去除空格
            trimmedToken = trimmedToken.replaceAll("^[\\[\\]{}<>]+|[\\[\\]{}<>]+$", "");  // 去除[]{}<>

            if (!trimmedToken.isEmpty()) {
                if (cur_i < this.skip_n) {
                    cur_i++;
                    continue;
                }
                if (cur_i >= this.skip_n + this.remain_n) {
                    break;
                }
                tokens.add(trimmedToken);
                cur_i++;
            }
        }

        return tokens;
    }
}