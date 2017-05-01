package TestCode;

import DataStructure.Edge;
import DataStructure.Graph;
import DataStructure.Node;
import DataStructure.Path;
import MainCode.Guider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/21.
 */
public class Test {
    public static void main(String[] args){
//       初始化graph
        Graph graph = InitMap.returnGraph();
//        设置多个目标点
        ArrayList<Node> nodes = new ArrayList<Node>();
        nodes.add(graph.getNode(0));
        nodes.add(graph.getNode(15));
        nodes.add(graph.getNode(12));
//        设置障碍点
//        List<Node> ob = new ArrayList<Node>();
//        ob.add(graph.getNode(4));
//        设置障碍边
        List<Edge> ob = new ArrayList<Edge>();
        ob.add(graph.getNode(4).getAdjEdge().get(8));
//       单目标
        List<Path> paths = Guider.getSingleDestPath(graph,graph.getNode(0),graph.getNode(12),ob,0.1);
        Path bestPath = paths.get(0);
        System.out.println("========SingleGoal path======");
        for (Node node:bestPath.getNodes())
        {
            System.out.print(node.N);
            System.out.print("<-");
        }
        System.out.println();
        System.out.println("=================================");
 //       多目标
        List<Path> multiGoalPath = Guider.getMultiDestPath(nodes,graph.getNode(1),graph,ob,0.1);
        System.out.println("========MultiGoal path======");
        for (Path path:multiGoalPath)
        {
            System.out.println("G: "+path.G+" "+"U:  "+path.U);
            for (Node node:path.getNodes())
            {System.out.print(node.N);
            System.out.print("<-");}
            System.out.println();
        }
        System.out.println("=================================");
    }
}
