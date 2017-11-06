package GuideMainCode;

import GuideDataStructure.Graph;
import GuideDataStructure.Node;
import GuideDataStructure.Path;
import Util.Util;

import java.util.*;

/**
 * Created by ruan on 9/12/16.
 */
public  class GuiderXXX {
    private double L = 0;
    private static long Strat;
    private static long End;
    private boolean isFirstPath;
    private List<Path> allPathsInfomation;
    private Path bestPath;
    private Path shortestPath;
    private Graph graph;
    private boolean isRun;
    private double threshold = 0.2;

    //init data
    public GuiderXXX(Graph graph) {
        this.graph = graph;
        this.isFirstPath = true;
        this.isRun = true;
        allPathsInfomation = new ArrayList<>();

    }

    private void ShowBestPath(Path bestPath) {

        List stack = bestPath.nodes;
        List edges = bestPath.edgeIds;
        if (stack == null) {
            System.out.print("There must be no best path ~~~");
        } else {
            System.out.println("Here is a best path.");
            Iterator iterator = stack.iterator();
            while (iterator.hasNext()) {
                Node temp = (Node) iterator.next();
                System.out.print(temp.N + " <-");
            }

            System.out.println();
            System.out.println("Distaance cost:" + bestPath.G);
            System.out.println("Sum of utility:" + bestPath.U);

        }
        System.out.print("There is a path noted by edgeId");
        System.out.println();
        if (edges == null) {
            System.out.print("There must be no best path ~~~");
        } else {
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


        float tempCost = (float) node.G;
        float tempUtility = 0;
        while (node != null) {
            Node FromNode = node;
            path.add(node);
            node = node.parent;
            Node ToNode = node;
            if (ToNode != null) {
                tempUtility = (float) (tempUtility + FromNode.getAdjEdge().get(ToNode.N).utility);
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

    public void runA_star(Graph graph, Node s, Node d,List obs,double threshold) {
        this.threshold = threshold;
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
                    insertPq(priorityQueue, n, nn.clone(new_nn), temp_G, temp_H, temp_F);
                }

            }

        }

        ShowBestPath(bestPath);

        ShoAllwPathInfomation(allPathsInfomation);

    }

    public Path ReturnBestPath() {

        return bestPath;
    }

    public List<Path> getAllPathsInfomation() {
        return allPathsInfomation;
    }

    private void ShoAllwPathInfomation(List<Path> allPathsInfomation) {
        System.out.println("paths:" + (allPathsInfomation.size()));
        for (Path path : allPathsInfomation) {
            ArrayList stack = (ArrayList) path.nodes;
            if (stack == null) {
                System.out.print("impossible to reach");
            } else {
                Iterator iterator = stack.iterator();
                System.out.print("The distance is :" + path.G + "------");

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
