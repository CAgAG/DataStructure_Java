package SimpleTree;

/**
 * @author CAgAG
 * @version 1.0
 * @date 2021/6/28
 */
public class Node {
    // key 数据关键字
    public int key;
    // 数据的值
    public Object value;
    public Node leftChild;
    public Node rightChild;

    @Override
    public String toString() {
        return "Node{" +
                "key=" + key +
                ", value=" + value.toString() +
                ", leftChild=" + leftChild +
                ", rightChild=" + rightChild +
                '}';
    }
}
