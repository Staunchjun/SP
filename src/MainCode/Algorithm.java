package MainCode;

import DataStructure.Edge;
import DataStructure.Graph;
import DataStructure.Node;
import DataStructure.Path;
import Util.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by Administrator on 2017/3/27.
 */
public class Algorithm {
    private double L = 0;
    private boolean isFirstPath;
    private List<Path> allPathsInfomation;
    private Path bestPath;
    private Path shortestPath;
    private Graph graph;
    private boolean isRun;
    private double threshold = 0.2;

    /**
     *
     * @param graph 地图传入
     */
    //init data
    public Algorithm(Graph graph) {
        this.graph = graph;
        this.isFirstPath = true;
        this.isRun = true;
        allPathsInfomation = new ArrayList<>();

    }

    /**
     *
     * @param graph  地图
     * @param s     起始点
     * @param d     目标点
     * @param obs   障碍点
     * @param threshold 产生路径閥值
     * @param printOrNot 打印信息
     * @return
     */
    public List<Path> getSingleDestPath(Graph graph, Node s, Node d,List obs,double threshold,boolean printOrNot) {
        this.threshold = threshold;
        boolean isEdge = false;
        boolean isNode = false;

        if (obs != null)
        {isEdge = obs.get(0) instanceof Edge;
            isNode = obs.get(0) instanceof Node;}


        PointComparator pointComparator = new PointComparator();
        PriorityQueue priorityQueue = new PriorityQueue(graph.getV(), pointComparator);

        s.G = 0;
        s.H = (float) Util.getDis(s, d);
        s.F = s.G + s.H;

        s.parent = null;
        priorityQueue.offer(s);


        while (!priorityQueue.isEmpty() && isRun) {
            Node n = (Node) priorityQueue.poll();
            for (Node nn : n.getNeighbors()) {
                // Make sure that next node will not arise again ,it means that we will not go back same place .
                boolean isObstacle = false;
                if (obs != null)
                {
                    if (isEdge) {
                        List<Edge> ob_e = (List<Edge>) obs;
                        for (Edge obstacle : ob_e) {
                            if (obstacle.id == n.getAdjEdge().get(nn.N).id) {
                                isObstacle = true;
                            }
                        }
                    }
                    else if (isNode)
                    {
                        List<Node> ob_n = (List<Node>) obs;
                        for (Node obstacle : ob_n) {
                            if (obstacle.N == nn.N) {
                                isObstacle = true;
                            }
                        }
                    }
                }

                if (!isObstacle){
                    if (!IsNParentContainNN(n, nn)) {

                        double temp_G = n.G + Util.getDis(n, nn);
                        double temp_H = Util.getDis(nn, d);
                        double temp_F = temp_G + temp_H;

                        if (nn.N == d.N) {

                            nn.G = temp_G;
                            nn.H = temp_H;
                            nn.F = temp_F;

                            d.parent = n;
                            SavePath(d);

                            continue;

                        }

                        Node new_nn = new Node();
                        //这里要把地图上的点提出来，因为地图上的点带有概率。根据传进去 的地图，传进去的有概率就有概率，没有就没有
                        Node graphNode = graph.getNode(nn.N);
                        insertPq(priorityQueue, n, graphNode.clone(new_nn), temp_G, temp_H, temp_F);
                    }

                }

            }
        }
        if (printOrNot) {
            ShowBestPath(bestPath);
            ShoAllwPathInfomation(allPathsInfomation);
        }
        return allPathsInfomation;
    }

    /**
     * 貌似这个方法不重要，直接paths.get(0)就是最优的路径了
     * @return 返回最优路径
     */
    public Path ReturnBestPath() {

        return bestPath;
    }

    private void ShowBestPath(Path bestPath) {
        if (bestPath == null)
        {
            System.out.print("There must be no best path ~~~");
            System.out.println("you just stay at original node");
            return;
        }
        if (bestPath.nodes == null) {
            System.out.print("There must be no best path +++++++++~~~");
            return;
        } else {
            System.out.println("Here is a best path.");
            List stack = bestPath.nodes;
            Iterator iterator = stack.iterator();
            while (iterator.hasNext()) {
                Node temp = (Node) iterator.next();
                System.out.print(temp.N + " <-");
            }

            System.out.println();
            System.out.println("Distaance cost:" + bestPath.G);
            System.out.println("Sum of utility:" + bestPath.U);

        }
        System.out.print("Here is a path noted by edgeId as followed : ");
        if (bestPath.edgeIds == null) {
            System.out.print("--------------There must be no best path --------");
        } else {
            List edges = bestPath.edgeIds;
            Iterator edge_iterator = edges.iterator();
            while (edge_iterator.hasNext()) {
                Integer temp = (Integer) edge_iterator.next();
                System.out.print(temp + " <-");
            }

            System.out.println();

        }
    }

    private void SavePath(Node node) {
        ArrayList<Node> path = new ArrayList();
        ArrayList<Integer> path_BY_edge = new ArrayList();

        double tempCost = node.G;
        double tempUtility = 0;
        while (node != null) {
            Node FromNode = node;
            path.add(node);
            node = node.parent;
            Node ToNode = node;
            if (ToNode != null) {
                //下面分别是点存概率和边存概率
//                tempUtility =  (tempUtility + FromNode.getAdjEdge().get(ToNode.N).utility);
                tempUtility =  tempUtility + FromNode.P;
                path_BY_edge.add(FromNode.getAdjEdge().get(ToNode.N).id);
            }

        }
        //tempPath is used to compare Cost and Utility
        Path tempPath = new Path(tempCost, tempUtility, path, path_BY_edge);

        // we compare the utility while running algorithm
        if (isFirstPath) {
            shortestPath = tempPath;
            bestPath = shortestPath;
            //threshold
            L = shortestPath.G * threshold;
            //we get the first path named shortest path,the second ,or third etc path will be longer~
            isFirstPath = false;
        } else {
            if (bestPath != null) {
                if (shortestPath.G + L < tempPath.G) {
                    isRun = false;
                } else if (shortestPath.G + L > tempPath.G && bestPath.U < tempPath.U) {
                    bestPath = tempPath;
                }
            } else {
                System.out.println("Occur some problem！");
            }
        }
        //store path (actually , there is that one more path will be store !!)
        allPathsInfomation.add(tempPath);

    }


    private void ShoAllwPathInfomation(List<Path> allPathsInfomation) {
        System.out.println();
        System.out.println("Sum of paths:" + (allPathsInfomation.size()));
        for (Path path : allPathsInfomation) {
            List stack =  path.nodes;
            if (stack == null) {
                System.out.print("impossible to reach");
            } else {
                Iterator iterator = stack.iterator();
                System.out.print("The distance is :" + path.G+"  the sum of probability"+String.format("%4f", path.U)+ "------");

                while (iterator.hasNext()) {
                    Node temp = (Node) iterator.next();
                    System.out.print(temp.N + " <-");

                }
                System.out.println();

            }
        }
    }

    private boolean IsNParentContainNN(Node n, Node nn) {
        while (n != null) {
            if (nn.N == n.N) {
                return true;
            }
            n = n.parent;
        }
        return false;
    }

    private void insertPq(PriorityQueue priorityQueue, Node n, Node next_n, double temp_G, double temp_H, double temp_F) {

        next_n.G = temp_G;
        next_n.H = temp_H;
        next_n.F = temp_F;
        next_n.parent = n;

        priorityQueue.offer(next_n);
    }

}
