# SP
## 导购路线规划需求

    1.一个起点，一个目的地，直接到达。
    最常规情况，顾客查找一个商品。希望跟着路线指引找到该商品。

    2.一个起点，多个目的地。依次到达所有目的地。
    顾客需要找到多个商品，希望根据路线指引能依次找到这些商品。

    3.可以设定为经过某些点/区域。
    引导顾客经过某些促销区域、或推断他感兴趣的区域。

    4.以不同的依据规划路线。如：
    a.路径最短；b.时间最短；c.途径某个位置点/主干道；d.避开某些位置点；e.优选本次购物没有经过的路径；

    5.路线偏离后重新计算路径。

###  可以编译成Jar包直接调用
###  使用方法
    InitMap.java-->用来初始化4*4地图的无向地图。
    test.java -->用来测试算法。

### warning:Interesting 值仍为0
### 导航路线接口文档
    地图类Graph 
    构造函数 Graph(List<EdgeSql> EdgeQgisSql, List<NodeSql> NodesBean)

    边EdgeSql
    private int id;
    private int nodeId1;
    private int nodeId2;
    private int utility;

    点 NodeSql 
    private int id;
    private int x;
    private int y;

    路线 Path
    public double G;
    public double U;
    public List<Node> nodes;
    public List<Integer> edgeIds;
    List<Node> getNodes();
    List<Integer> getEdgeIds();

    导航器 Guider
    //构造函数 Guider (Graph graph)
    推荐单目标路径List<Path> getSingleDestPath(Graph graph, NodeSql startNode, NodeSql destNode，  List<Node> obs,  double threshold );
    推荐多目标路径 List<Path> getMultiDestPath(Graph graph, NodeSql startNode, List<NodeSql> destNodes，  List<Node> obs,  double threshold );
