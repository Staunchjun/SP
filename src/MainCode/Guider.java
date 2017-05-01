package MainCode;

import DataStructure.Graph;
import DataStructure.Node;
import DataStructure.Path;
import Util.Util;
import java.util.*;

import static Util.IntegerPath.IntegerPath;

/**
 * Created by ruan on 9/12/16.
 */
public  class Guider {

//    ======================for multi goal ==========================================
    public static List<Path> getMultiDestPath(ArrayList<Node> targets, Node StartNode, Graph graph, List obstacles, double threshold) {
        PriorityQueue priorityQueue;
        List<Path> IntegerPaths = new ArrayList<Path>();
        List<List<Path>> PathsCollection = new ArrayList<List<Path>>();
        NodeDisComparator nodeDisComparator = new NodeDisComparator();
        priorityQueue = new PriorityQueue(targets.size(), nodeDisComparator);
        for (Node target : targets) {
            double Cost = Util.getDis(StartNode, target);
            priorityQueue.offer(new NodeDis(target.N, Cost));

        }
        Node S = StartNode;
        while (priorityQueue.peek() != null) {
            NodeDis nodeDis = (NodeDis) priorityQueue.poll();
            Node D = graph.getNode(nodeDis.NodeId);
            Algorithm algorithm = new Algorithm(graph);
            List<Path> paths = algorithm.getSingleDestPath(graph, S, D, obstacles, threshold);
            S = D;
            PathsCollection.add(paths);
        }
       List<List<Path>> result  = new ArrayList<List<Path>>();
//        笛卡尔乘积 永远需要用到的数据结构~~~
      Util.Descartes.descartes(PathsCollection, result, 0, new ArrayList<Path>());
      List<Path>  paths = IntegerPath(result,StartNode);
      Collections.sort(paths,new PathComparatorByG());
    return paths;
    }



    //    =======================for single goal===========================================
    public static List<Path> getSingleDestPath(Graph graph, Node node, Node node1, List ob, double v) {
        Algorithm algorithm = new Algorithm(graph);
        List<Path> paths = algorithm.getSingleDestPath(graph,graph.getNode(0),graph.getNode(12),ob,0.1);
        return paths;
    }


}
class NodeDis
{
    public double F;
    public int NodeId;
    public NodeDis(int nodeId,double f) {
        this.F = f;
        this.NodeId = nodeId;
    }
}
class NodeDisComparator  implements Comparator<NodeDis> {
    @Override
    public int compare(NodeDis o1, NodeDis o2) {
        if (o1.F < o2.F) {
            return -1;
        } else if (o1.F == o2.F) {
            return 0;
        } else {
            return 1;
        }
    }
}