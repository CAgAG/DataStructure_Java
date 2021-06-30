package SimpleLink;

/**
 * @author CAgAG
 * @version 1.0
 * @date 2021/6/26
 */
public class LinkList {
    private LinkNode head;
    private LinkNode cur;
    private int length;

    public LinkList() {
        head = new LinkNode();
        head.data = length;
        head.next = null;
        cur = head;
        length = 0;
    }

    public void add(Object o) {
        LinkNode node = new LinkNode();
        node.data = o;
        node.next = cur.next;
        cur.next = node;
        cur = cur.next;
        length++;
    }

    public Object delete(int index) {
        if (index > length || index < 1) return null;
        LinkNode pre = head;
        LinkNode p = head.next;
        Object del_object = null;
        int i = 1;
        while (p != null) {
            if (i == index) {
                pre.next = p.next;
                del_object = p.data;
                break;
            }
            i++;
            pre = p;
            p = p.next;
        }
        length--;
        return del_object;
    }

    public LinkNode locateValue(int index) {
        if (index > length || index < 1) return null;
        LinkNode p = head.next;
        int i = 1;
        while (p != null) {
            if (i == index) {
                break;
            }
            i++;
            p = p.next;
        }
        return p;
    }

    public void show() {
        LinkNode p = head.next;
        while (p != null) {
            System.out.print(p.data.toString() + " ");
            p = p.next;
        }
        System.out.println();
    }

    public static void main(String[] args) {
        LinkList linkList = new LinkList();

        for (int i = 0; i < 12; i++) {
            linkList.add(i);
        }
        linkList.show();

        System.out.println("delete index: 2");
        linkList.delete(2);

        linkList.show();

        int l = 1;
        LinkNode locateValue = linkList.locateValue(l);
        System.out.println("locate value: " + locateValue.data.toString());
    }
}
