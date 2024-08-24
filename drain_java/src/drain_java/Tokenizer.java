package drain_java;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Simple string tokenizer.
 */
public class Tokenizer {
    // TODO: 提取日志字符串的词到 List, delimiters: 分隔符
    public List<String> tokenize(String content, String delimiters) {
        StringTokenizer stringTokenizer = new StringTokenizer(content, delimiters);

        List<String> tokens = new ArrayList<>(stringTokenizer.countTokens());
        while (stringTokenizer.hasMoreTokens()) {
            String trimmedToken = stringTokenizer.nextToken().trim();
            if (!trimmedToken.isEmpty()) {
                tokens.add(trimmedToken);
            }
        }

        return tokens;
    }
}
