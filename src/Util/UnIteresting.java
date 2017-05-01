package Util;

import DataStructure.Edge;
import DataStructure.Graph;
import DataStructure.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/3/26.
 */
public class UnIteresting {
    private float threshold;
    public UnIteresting(float threshold) {
        this.threshold = threshold;
    }

    public List<Edge> CreateObstacles(Graph graph)
    {
        List<Edge> UnIterestingEdge = new ArrayList<Edge>();
        List<Node> nodes = graph.getNodes();
        for (Node node : nodes)
        {
            Map<Integer, Edge> edgeMap = node.getAdjEdge();
            for (Map.Entry<Integer, Edge> entry:edgeMap.entrySet())
            {
//                Integer ToNode = entry.getKey();
                Edge edge = entry.getValue();
                if (edge.utility < threshold)
                {
                    UnIterestingEdge.add(edge);
                }
            }
        }
        return  UnIterestingEdge;
    }
}
