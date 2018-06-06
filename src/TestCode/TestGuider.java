package TestCode;

import GuideDataStructure.Edge;
import GuideDataStructure.Graph;
import GuideDataStructure.Node;
import GuideDataStructure.Path;
import GuideMainCode.Guider;
import RecommendPath.InitMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/21.
 */
public class TestGuider {
    public static void main(String[] args){
//       初始化graph
        Graph graph = InitMap.returnGraph();
        long start = System.currentTimeMillis();
//        设置多个目标点
//        ArrayList<Node> nodes = new ArrayList<Node>();
//        nodes.add(graph.getNode(0));
//        nodes.add(graph.getNode(15));
//        nodes.add(graph.getNode(12));
//        设置障碍点
        List<Node> obs = new ArrayList<>();
        obs.add(graph.getNode(11));
        obs.add(graph.getNode(21));
        obs.add(graph.getNode(31));
        obs.add(graph.getNode(13));
        obs.add(graph.getNode(23));
        obs.add(graph.getNode(33));
        obs.add(graph.getNode(15));
        obs.add(graph.getNode(25));
        obs.add(graph.getNode(35));
        obs.add(graph.getNode(17));
        obs.add(graph.getNode(37));
        obs.add(graph.getNode(18));
        obs.add(graph.getNode(38));
        obs.add(graph.getNode(51));
        obs.add(graph.getNode(52));
        obs.add(graph.getNode(71));
        obs.add(graph.getNode(72));
        obs.add(graph.getNode(91));
        obs.add(graph.getNode(92));
        obs.add(graph.getNode(54));
        obs.add(graph.getNode(64));
        obs.add(graph.getNode(74));
        obs.add(graph.getNode(56));
        obs.add(graph.getNode(66));
        obs.add(graph.getNode(76));
        obs.add(graph.getNode(58));
        obs.add(graph.getNode(68));
        obs.add(graph.getNode(78));
        obs.add(graph.getNode(94));
        obs.add(graph.getNode(95));
        obs.add(graph.getNode(96));
        obs.add(graph.getNode(98));
        obs.add(graph.getNode(9));

//        设置障碍边
//        List<Edge> ob = new ArrayList<Edge>();
//        ob.add(graph.getNode(4).getAdjEdge().get(8));
//       单目标
        List<Path> paths = Guider.getSingleDestPath(graph,graph.getNode(0),graph.getNode(99),obs,0.1,true);
        Path bestPath = paths.get(0);
        System.out.println("========SingleGoal path======");
        long end = System.currentTimeMillis();
        for (Node node:bestPath.getNodes())
        {
            System.out.print(node.N);
            System.out.print("<-");
        }
        System.out.println();
        System.out.println("=================================");
        System.out.println("===========Cost time=============");
        System.out.println("Total cost time is"+(end-start));
        System.out.println("=================================");
        //       多目标
//        List<Path> multiGoalPath = Guider.getMultiDestPath(nodes,graph.getNode(1),graph,ob,0.1,true);
//        System.out.println("========MultiGoal path======");
//        for (Path path:multiGoalPath)
//        {
//            System.out.println("G: "+path.G+" "+"U:  "+path.U);
//            for (Node node:path.getNodes())
//            {System.out.print(node.N);
//            System.out.print("<-");}
//            System.out.println();
//        }
//        System.out.println("=================================");
    }
}
