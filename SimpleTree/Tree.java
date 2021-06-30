package SimpleTree;

import java.util.Stack;

/**
 * @author CAgAG
 * @version 1.0
 * @date 2021/6/28
 */
public class Tree {
    public Node root;

    public Tree() {
        this.root = null;
    }

    // 根据key值进行查询
    public Node find(int key) {
        Node cur = root;
        while (cur.key != key) {
            if (key < cur.key) {
                cur = cur.leftChild;
            } else {
                cur = cur.rightChild;
            }
            if (cur == null) {
                return null;
            }
        }
        return cur;
    }

    // 插入
    public void insert(int key, Object value) {
        Node node = new Node();
        node.key = key;
        node.value = value;

        if (root == null) {
            root = node;
        } else {
            Node cur = root;
            // 定义一个父节点，该父节点与current相关
            Node parent;
            while (true) {
                parent = cur;
                if (key < cur.key) {
                    cur = cur.leftChild;
                    if (cur == null) {
                        parent.leftChild = node;
                        return;
                    }
                } else {
                    cur = cur.rightChild;
                    if (cur == null) {
                        parent.rightChild = node;
                        return;
                    }
                }

            }
        }

    }

    // 根据key删除一个节点
    public boolean delete(int key) {
        Node cur = root;
        Node parent = root;
        boolean isLeftChild = true;

        while (cur.key != key) {
            parent = cur;
            if (key < cur.key) {
                isLeftChild = true;
                cur = cur.leftChild;
            } else {
                isLeftChild = false;
                cur = cur.rightChild;
            }
            if (cur == null) {
                return false;
            }

        }

        // 如果没有子节点，直接删除即可
        if (cur.leftChild == null && cur.rightChild == null) {
            // 如果删除的节点为root
            if (cur == root) {
                root = null;
            }
            //如果删除左子节点
            else if (isLeftChild) {
                parent.leftChild = null;
            }
            //如果删除右子节点
            else {
                parent.rightChild = null;
            }
        }
        // 如果没有右节点，用左子树代替当前节点即可
        else if (cur.rightChild == null) {
            if (cur == root) {
                root = cur.leftChild;
            } else if (isLeftChild) {
                parent.leftChild = cur.leftChild;
            } else {
                parent.rightChild = cur.leftChild;
            }
        }
        // 如果没有左节点，直接用右子树代替当前删除的节点
        else if (cur.leftChild == null) {
            if (cur == root) {
                root = cur.rightChild;
            } else if (isLeftChild) {
                parent.leftChild = cur.rightChild;
            } else {
                parent.rightChild = cur.rightChild;
            }
        }
        // 如果左右子节点均不为空，则需要寻找到节点继承者
        else {
            // 寻找继承者
            Node successor = getSuccessor(cur);
            if (cur == root) {
                root = successor;
            } else if (isLeftChild) {
                parent.leftChild = successor;
            } else {
                parent.rightChild = successor;
            }
            successor.leftChild = cur.leftChild;
        }
        return true;
    }

    private Node getSuccessor(Node delNode) {
        // 初始化继承者的父节点
        Node sucessorParent = delNode;
        // 初始化继承者几点
        Node sucessor = delNode;
        /*
        从当前节点开始寻找继承者，必须从右子树里寻找继承者，
		因为右子树比当前节点的值大
         */
        Node cur = delNode.rightChild;

        while (cur != null) {
            sucessorParent = sucessor;
            sucessor = cur;
            cur = cur.leftChild;
        }
        if (sucessor != delNode.rightChild) {
            sucessorParent.leftChild = sucessor.rightChild;
            sucessor.rightChild = delNode.rightChild;
        }
        return sucessor;
    }

    //遍历
    //前序遍历，从根节点开始遍历。
    public void preOrder(Node localRoot) {
        if (localRoot != null) {
            System.out.print(localRoot.key + "  ");
            preOrder(localRoot.leftChild);
            preOrder(localRoot.rightChild);
        }
    }

    //中序
    public void inOrder(Node localRoot) {
        if (localRoot != null) {
            inOrder(localRoot.leftChild);
            System.out.print(localRoot.key + "  ");
            inOrder(localRoot.rightChild);
        }
    }

    //后序
    public void postOrder(Node localRoot) {
        if (localRoot != null) {
            postOrder(localRoot.leftChild);
            postOrder(localRoot.rightChild);
            System.out.print(localRoot.key + "  ");
        }
    }

    //打印树
    public void displayTree() {
        Stack globalStack = new Stack();
        globalStack.push(root);
        int nBlanks = 32;
        getClass();
        boolean isRowEmpty = false;
        System.out.println("=========================================================================");
        while (! isRowEmpty) {
            Stack localStack = new Stack();
            isRowEmpty = true;
            for (int j = 0; j < nBlanks; j++) {
                System.out.print(" ");
            }

            while (! globalStack.isEmpty()) {
                Node temp = (Node) globalStack.pop();
                if (temp != null) {
                    System.out.print(temp.key);
                    localStack.push(temp.leftChild);
                    localStack.push(temp.rightChild);
                    if (temp.leftChild != null || temp.rightChild != null) {
                        isRowEmpty = false;
                    }
                } else {
                    System.out.print("--");
                    localStack.push(null);
                    localStack.push(null);
                }
                for (int j = 0; j < nBlanks * 2 - 2; j++) {
                    System.out.print(" ");
                }
            }
            System.out.println();
            nBlanks /= 2;
            while (! localStack.isEmpty()) {
                globalStack.push(localStack.pop());
            }
        }
        System.out.println("=========================================================================");
    }
}


