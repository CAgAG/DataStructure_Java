package SimpleTree;

/**
 * @author CAgAG
 * @version 1.0
 * @date 2021/6/28
 */
public class Main {
    public static void main(String[] args) {
        int value;
        Tree theTree = new Tree();
        theTree.insert(50, 1.3);
        theTree.insert(25, 1.1);
        theTree.insert(75, 1.7);

        theTree.insert(12, 1.3);
        theTree.insert(37, 1.9);
        theTree.insert(43, 1.4);

        theTree.insert(87, 1.6);
        theTree.insert(93, 1.2);
        theTree.insert(97, 1.5);

        theTree.insert(30, 1.4);
        theTree.insert(19, 1.3);
        theTree.displayTree();

		theTree.preOrder(theTree.root);
		System.out.println();
		theTree.inOrder(theTree.root);
        System.out.println();
		theTree.postOrder(theTree.root);
        System.out.println();

        theTree.delete(25);
        theTree.displayTree();
    }
}
