package SimpleLink;

/**
 * @author CAgAG
 * @version 1.0
 * @date 2021/6/26
 */
public class DoubleLinkList {
    private DoubleLinkNode head;
    private DoubleLinkNode cur;
    private int length;

    public DoubleLinkList() {
        length = 0;
        head = new DoubleLinkNode();
        head.data = length;
        head.next = null;
        head.prev = null;
        cur = head;
    }

    public void add(Object o) {
        DoubleLinkNode node = new DoubleLinkNode();
        node.data = o;

        node.prev = cur;
        node.next = null;
        cur.next = node;

        cur = cur.next;
        length++;
    }

    public DoubleLinkNode delete(int index) {
        DoubleLinkNode pre = head;
        DoubleLinkNode p = head.next;
        int i = 1;
        DoubleLinkNode delNode = null;
        while (p != null) {
            if (i == index) {
                pre.next = p.next;
                p.next.prev = pre;
                delNode = p;
                break;
            }

            i++;
            pre = p;
            p = p.next;
        }
        length--;
        return delNode;
    }

    public void show() {
        DoubleLinkNode p = head.next;
        System.out.println("next: ");
        while (p != null) {
            System.out.print(p.data.toString() + " ");
            p = p.next;
        }
        System.out.println();
        System.out.println("prev: ");
        p = cur;
        while (p != head) {
            System.out.print(p.data.toString() + " ");
            p = p.prev;
        }
        System.out.println();
    }

    public static void main(String[] args) {
        DoubleLinkList doubleLinkList = new DoubleLinkList();

        for (int i = 0; i < 12; i++) {
            doubleLinkList.add(i);;
        }
        doubleLinkList.show();

        System.out.println("\ndelete index 2: ");
        doubleLinkList.delete(2);
        doubleLinkList.show();
    }

}
