package Graph;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * @author CAgAG
 * @version 1.0
 * @date 2021/6/29
 */
public class SeqGraph {
    private final int MAX_VERTS = 20;// 表示顶点的最大个数

    private int nVerts; // 顶点个数, 配合添加 vertexList
    private Vertex vertexList[]; // 用来存储顶点的数组, 主要是为了打印 label

    private int adjMat[][]; // 用邻接矩阵来存储 边,数组元素0表示没有边界，1表示有边界

    private Stack<Integer> theStack; // 用栈实现深度优先搜索
    private Queue<Integer> queue; // 用队列实现广度优先搜索

    /**
     * 顶点类
     */
    class Vertex {
        public char label; // 顶点名字的存储，比如说存储A顶点
        public boolean wasVisited;//这个属性非常重要，因为他在搜索过程中能够使我们明白哪些顶点被访问过，防止我们重复的去搜索遍历顶点

        public Vertex(char label) {
            this.label = label;
            wasVisited = false;
        }
    }

    public SeqGraph() {
        vertexList = new Vertex[MAX_VERTS];
        adjMat = new int[MAX_VERTS][MAX_VERTS];
        nVerts = 0;// 初始化顶点个数为0
        // 初始化邻接矩阵所有元素都为0，即所有顶点都没有边
        for (int i = 0; i < MAX_VERTS; i++) {
            for (int j = 0; j < MAX_VERTS; j++) {
                adjMat[i][j] = 0;
            }
        }
        theStack = new Stack();
        queue = new LinkedList();
    }

    // 将顶点添加到数组中，是否访问标志置为wasVisited=false(未访问)
    public void addVertex(char lab) {
        vertexList[nVerts++] = new Vertex(lab);
    }

    // 注意用邻接矩阵表示边，是对称的，两部分都要赋值
    public void addEdge(int start, int end) {
        adjMat[start][end] = 1; //邻接矩阵中，1表示有边，可以到达顶点。0表示两个顶点之间没有边，更加准确的说是不可达
        adjMat[end][start] = 1; //如果我不写此句代码，那么这个实现就是有向图的实现了。所以呢，有向图与无向图的关键就在此处
    }

    // 打印某个顶点表示的值
    public void displayVertex(int v) {
        System.out.print(vertexList[v].label);
    }

    // 找到与某一顶点邻接且未被访问的顶点
    public int getAdjUnvisitedVertex(int v) {
        for (int i = 0; i < nVerts; i++) {
            // v顶点与i顶点相邻（邻接矩阵值为1）且未被访问 wasVisited==false
            if (adjMat[v][i] == 1 && !vertexList[i].wasVisited) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 深度优先搜索算法: 1、用peek()方法检查栈顶的顶点
     * 2、用getAdjUnvisitedVertex()方法找到当前栈顶点邻接且未被访问的顶点
     * 3、第二步方法返回值不等于-1则找到下一个未访问的邻接顶点，访问这个顶点，并入栈 如果第二步方法返回值等于 -1，则没有找到，出栈
     */
    public void depthFirstSearch() {
        // 从第一个顶点开始访问
        vertexList[0].wasVisited = true; // 访问之后标记为true
        displayVertex(0);// 打印访问的第一个顶点
        theStack.push(0);// 将第一个顶点放入栈中

        while (!theStack.isEmpty()) {
            // 找到栈当前顶点邻接且未被访问的顶点
            int v = getAdjUnvisitedVertex(theStack.peek());
            if (v == -1) { // 如果当前顶点值为-1，则表示没有邻接且未被访问顶点，那么出栈顶点
                theStack.pop();
            } else { // 否则访问下一个邻接顶点
                vertexList[v].wasVisited = true;
                displayVertex(v);
                theStack.push(v);
            }
        }

        // 栈访问完毕，重置所有标记位wasVisited=false
        for (int i = 0; i < nVerts; i++) {
            vertexList[i].wasVisited = false;
        }
    }

    /**
     * 广度优先搜索算法： 1、用remove()方法检查栈顶的顶点 2、试图找到这个顶点还未访问的邻节点 3、 如果没有找到，该顶点出列 4、
     * 如果找到这样的顶点，访问这个顶点，并把它放入队列中
     */
    public void breadthFirstSearch() {
        vertexList[0].wasVisited = true;
        displayVertex(0);
        queue.offer(0);
        int v2;

        while (!queue.isEmpty()) {
            int v1 = queue.poll();
            while ((v2 = getAdjUnvisitedVertex(v1)) != -1) {
                vertexList[v2].wasVisited = true;
                displayVertex(v2);
                queue.offer(v2);
            }
        }

        // 搜索完毕，初始化，以便于下次搜索
        for (int i = 0; i < nVerts; i++) {
            vertexList[i].wasVisited = false;
        }
    }

    //最小生成树代码实现，参照DFS实现
    public void mst() {
        vertexList[0].wasVisited = true;
        theStack.push(0);

        while (!theStack.isEmpty()) {
            int currentVertex = theStack.peek();
            int v = getAdjUnvisitedVertex(currentVertex);
            if (v == -1) {
                theStack.pop();
            } else {
                vertexList[v].wasVisited = true;
                theStack.push(v);

                displayVertex(currentVertex);
                displayVertex(v);
                System.out.print(" ");
            }
        }

        //搜索完毕，初始化，以便于下次搜索
        for (int i = 0; i < nVerts; i++) {
            vertexList[i].wasVisited = false;
        }
    }

    public static void main(String[] args) {
        SeqGraph graph = new SeqGraph();
        graph.addVertex('A');
        graph.addVertex('B');
        graph.addVertex('C');
        graph.addVertex('D');
        graph.addVertex('E');

        graph.addEdge(0, 1);// AB
        graph.addEdge(1, 2);// BC
        graph.addEdge(0, 3);// AD
        graph.addEdge(3, 4);// DE

        System.out.println("深度优先搜索算法 :");
        graph.depthFirstSearch();// ABCDE

        System.out.println();
        System.out.println("----------------------");

        System.out.println("广度优先搜索算法 :");
        graph.breadthFirstSearch();// ABDCE

        System.out.println();
        System.out.println("----------------------");

        System.out.println("最小生成树 :");
        graph.mst();
    }
}












