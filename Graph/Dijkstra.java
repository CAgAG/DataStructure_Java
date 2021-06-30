package Graph;

/**
 * @author CAgAG
 * @version 1.0
 * @date 2021/6/30
 */
// DistPar类记录了当前顶点到起始顶点点的距离和当前顶点的父顶点（这个父节点，说的是有向图中的指向顶点）
class DistPar {
    public int distance; // 初始节点到此节点的位置
    public int parentVert; // 当前节点的上一节点

    public DistPar(int pv, int d) {
        distance = d;
        parentVert = pv;
    }
}

class Vertex {
    public char label; // 存储节点值
    public boolean isInTree;// 是否添加到了图中

    public Vertex(char lab) {
        label = lab;
        isInTree = false;
    }
}

class Graph {
    private final int MAX_VERTS = 20; // 最大顶点数
    private final int INFINITY = Integer.MAX_VALUE;// 最远距离...表示无法达到
    private Vertex vertexList[]; // 存储顶点的数组
    private int adjMat[][]; // 存储顶点之间的边界
    private int nVerts; // 顶点数量
    private int nTree; // 最小生成树中的顶点数量
    private DistPar sPath[]; // 最短路径数组
    private int currentVert; // 当前顶点索引
    private int startToCurrent; //到当前顶点的距离

    // 构造函数
    public Graph() {
        vertexList = new Vertex[MAX_VERTS];
        adjMat = new int[MAX_VERTS][MAX_VERTS];
        nVerts = 0;
        nTree = 0;
        for (int j = 0; j < MAX_VERTS; j++)
            for (int k = 0; k < MAX_VERTS; k++)
                adjMat[j][k] = INFINITY;
        sPath = new DistPar[MAX_VERTS];
    }

    // 添加顶点
    public void addVertex(char lab) {
        vertexList[nVerts++] = new Vertex(lab);
    }

    // 添加带权边
    public void addEdge(int start, int end, int weight) {
        adjMat[start][end] = weight; // (directed)
    }

    /**
     * path()方法执行真正的最短路径算法。
     */
    public void path() { //寻找所有最短路径
        /*
         * 源点总在vertexArray[]数组下标为0的位置，path()方法的第一个任务就是把这个顶点放入树中。
         * 算法执行过程中，将会把其他顶点也逐一放入树中。把顶点放入树中的操作是设置一下标志位即可。
         * 并把nTree变量增1，这个变量记录了树中有多少个顶点。
         */
        int startTree = 0; //从vertex 0开始
        vertexList[startTree].isInTree = true;
        nTree = 1;

        /*
         * path()方法把邻接矩阵的对应行表达的距离复制到sPath[]中，实际总是先从第0行复制
         * 为了简单，假定源点的下标总为0。最开始，所有sPath[]数组中的父节点字段为A，即源点。
         */
        for (int i = 0; i < nVerts; i++) {
            int tempDist = adjMat[startTree][i];
            //sPath中保存的都是到初始顶点的距离，所以父顶点默认都是初始顶点，后面程序中会将其修改
            sPath[i] = new DistPar(startTree, tempDist);
        }

        /*
         * 现在进入主循环，等到所有的顶点都放入树中，这个循环就结束，这个循环有三个基本动作：
         * 1. 选择sPath[]数组中的最小距离
         * 2. 把对应的顶点（这个最小距离所在列的题头）放入树中，这个顶点变成“当前顶点”currentVert
         * 3. 根据currentVert的变化，更新所有的sPath[]数组内容
         */
        while (nTree < nVerts) {
            //1. 选择sPath[]数组中的最小距离
            int indexMin = getMin(); //获得sPath中的最小路径值索引
            int minDist = sPath[indexMin].distance; //获得最小路径

            if (minDist == INFINITY) {
                System.out.println("There are unreachable vertices");
                break;
            }
            //2. 把对应的顶点（这个最小距离所在列的题头）放入树中，这个顶点变成“当前顶点”currentVert
            else { //reset currentVert
                currentVert = indexMin;
                startToCurrent = sPath[indexMin].distance;
            }
            vertexList[currentVert].isInTree = true;
            nTree++;
            //3. 根据currentVert的变化，更新所有的sPath[]数组内容
            adjust_sPath();
        }
        displayPaths();

        nTree = 0;
        for (int i = 0; i < nVerts; i++) {
            vertexList[i].isInTree = false;
        }
    }

    //获取sPath中最小路径的索引
    public int getMin() {
        int minDist = INFINITY;
        int indexMin = 0;
        for (int j = 1; j < nVerts; j++) {
            if (!vertexList[j].isInTree && sPath[j].distance < minDist) {
                minDist = sPath[j].distance;
                indexMin = j;
            }
        }
        return indexMin;
    }

    /*调整sPath中存储的对象的值，即顶点到初始顶点的距离，和顶点的父顶点
     * 这是Dijkstra算法的核心
     */
    private void adjust_sPath() {
        int column = 1;
        while (column < nVerts) {
            if (vertexList[column].isInTree) {
                column++;
                continue;
            }
            int currentToFringe = adjMat[currentVert][column]; //获得当前顶点到其他顶点的距离，其他顶点不满足isInTree
            int startToFringe = startToCurrent + currentToFringe; //计算其他顶点到初始顶点的距离=当前顶点到初始顶点距离+当前顶点到其他顶点的距离
            int sPathDist = sPath[column].distance; //获得column处顶点到起始顶点的距离，如果不与初始顶点相邻，默认值都是无穷大

            if (startToFringe < sPathDist) {
                sPath[column].parentVert = currentVert; //修改其父顶点
                sPath[column].distance = startToFringe; //以及到初始顶点的距离
            }
            column++;
        }
    }

    public void displayPaths() {
        for (int j = 0; j < nVerts; j++) {
            System.out.print(vertexList[j].label + "="); // B=
            if (sPath[j].distance == INFINITY)
                System.out.print("inf"); // inf
            else
                System.out.print(sPath[j].distance); // 50
            char parent = vertexList[sPath[j].parentVert].label;
            System.out.print("(" + parent + ") "); // (A)
        }
        System.out.println("");
    }
}

public class Dijkstra {
    public static void main(String[] args) {
        Graph theGraph = new Graph();
        theGraph.addVertex('A'); // 0 (start)
        theGraph.addVertex('B'); // 1
        theGraph.addVertex('C'); // 2
        theGraph.addVertex('D'); // 3
        theGraph.addVertex('E'); // 4

        theGraph.addEdge(0, 1, 50); // AB 50
        theGraph.addEdge(0, 3, 80); // AD 80
        theGraph.addEdge(1, 2, 60); // BC 60
        theGraph.addEdge(1, 3, 90); // BD 90
        theGraph.addEdge(2, 4, 40); // CE 40
        theGraph.addEdge(3, 2, 20); // DC 20
        theGraph.addEdge(3, 4, 70); // DE 70
        theGraph.addEdge(4, 1, 50); // EB 50

        System.out.println("从A到其他节点的所有路径：Shortest paths");
        theGraph.path(); // shortest paths
        System.out.println();
    }
}
