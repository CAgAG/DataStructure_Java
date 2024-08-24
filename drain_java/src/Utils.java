import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static List<String> ListToTargetSize(List<String> originalList, int targetSize) {
        List<String> result = new ArrayList<>(originalList);
        int currentIndex = 0;

        while (result.size() < targetSize) {
            result.add(originalList.get(currentIndex % originalList.size()));
            currentIndex++;
        }

        return result;
    }

}
