package GuideDataStructure;

import Bean.EdgeSql;
import Bean.NodeSql;
import RecommendPath.TestPathGenerate;

import java.util.*;

/**
 * Created by ruan on 9/12/16.
 */
public class Graph {

    private int V;
    private int E;
    private List<Node> nodes = null;

    public int getV() {
        return V;
    }

    public int getE() {
        return E;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public Node getNode(int index) {
        return nodes.get(index);
    }

    //This method create a graph by List<EdgeQgisSql> EdgeQgisSql, List<NodeQgisSql> NodesBean
//    public Graph(List<EdgeQgisSql> EdgeQgisSql, List<NodeQgisSql> NodesBean) {
//        V = NodesBean.size();// add node 0
//        E = EdgeQgisSql.size();
//
//        this.nodes = new ArrayList<Node>(V);
//        for (int j = 0; j < 1; j++) {
//            this.nodes.add(0, new Node());//add node 0 in case the error
//        }
//        //init nodes
//        for (int i = 1; i <= V; i++) {
//            this.nodes.add(i, new Node());
//        }
//        //read Node data
//        for (NodeQgisSql nodeSql : NodesBean) {
//            int n = nodeSql.getId();
//            Node node = this.nodes.get(n);
//            node.N = n;
//            String corrdinate = nodeSql.getSt_astext();
//            String[] strings = corrdinate.split(" ");
//            String[] X_array = strings[0].replace('(', 'A').split("A");
//            String x = X_array[1];
//            String[] Y_array = strings[1].replace(')', 'A').split("A");
//            String Y = Y_array[0];
//            node.x = new Double(x);
//            node.y = new Double(Y);
//        }
//        //read Edge data
//        for (EdgeQgisSql beann : EdgeQgisSql) {
//
//            List<Integer> adjNode = beann.getAdj();
//
//            int s = adjNode.get(0);//from
//            int d = adjNode.get(1);//to
//
//            this.nodes.get(s).addNeighbor(this.nodes.get(d));
//            this.nodes.get(s).addEdge(this.nodes.get(d).N, new Edge(this.nodes.get(s), this.nodes.get(d), beann.getUtility(), beann.getId()));
//            this.nodes.get(d).addNeighbor(this.nodes.get(s));
//            this.nodes.get(d).addEdge(this.nodes.get(s).N, new Edge(this.nodes.get(d), this.nodes.get(s), beann.getUtility(), beann.getId()));
//
//        }
//
//
//    }

    public Graph(List<EdgeSql> EdgeQgisSql, List<NodeSql> NodesBean) {
        V = NodesBean.size();// add node 0
        E = EdgeQgisSql.size();

        this.nodes = new ArrayList<Node>(V);

        //init nodes
//        for (int i = 0; i < V; i++) {
//            this.nodes.add(i, new Node());
//        }
        //read Node data
        for (NodeSql nodeSql : NodesBean) {
            int n = nodeSql.getId();
//            Node node = this.nodes.get(n);
            Node node = new Node();
            node.N = n;
            node.x = nodeSql.getX();
            node.y = nodeSql.getY();
            this.nodes.add(node);
        }
        //read Edge data
        for (EdgeSql beann : EdgeQgisSql) {
            int s = beann.getNode_id1();//from
            int d = beann.getNodeid2();//to

            this.nodes.get(s).addNeighbor(this.nodes.get(d));
            this.nodes.get(s).addEdge(this.nodes.get(d).N, new Edge(this.nodes.get(s), this.nodes.get(d), beann.getUtility(), beann.getId()));
            this.nodes.get(d).addNeighbor(this.nodes.get(s));
            this.nodes.get(d).addEdge(this.nodes.get(s).N, new Edge(this.nodes.get(d), this.nodes.get(s), beann.getUtility(), beann.getId()));
        }
    }

    public Graph(List<EdgeSql> EdgeQgisSql, List<NodeSql> NodesBean, Map<Integer, Double> list, TestPathGenerate testPathGenerate) {
        V = NodesBean.size();// add node 0
        E = EdgeQgisSql.size();

        this.nodes = new ArrayList<Node>(V);

        Map<Integer, Double> nodeP = new HashMap<>();

        for (Map.Entry<Integer, Double> e : list.entrySet()) {
            if (nodeP.containsKey(testPathGenerate.pLocation.get(e.getKey()))) {
                double p = nodeP.get(testPathGenerate.pLocation.get(e.getKey()));
                p += e.getValue();
                nodeP.put(testPathGenerate.pLocation.get(e.getKey()), p);
            } else {
                nodeP.put(testPathGenerate.pLocation.get(e.getKey()), e.getValue());
            }
        }
        //read Node data
        for (NodeSql nodeSql : NodesBean) {
            int n = nodeSql.getId();
            Node node = new Node();
            node.N = n;
            node.x = nodeSql.getX();
            node.y = nodeSql.getY();
            if (nodeP.containsKey(n)) {
                node.P = nodeP.get(n);
            } else {
                node.P = 0;
            }
            this.nodes.add(node);
        }
        //read Edge data
        for (EdgeSql beann : EdgeQgisSql) {
            int s = beann.getNode_id1();//from
            int d = beann.getNodeid2();//to

            this.nodes.get(s).addNeighbor(this.nodes.get(d));
            this.nodes.get(s).addEdge(this.nodes.get(d).N, new Edge(this.nodes.get(s), this.nodes.get(d), beann.getUtility(), beann.getId()));
            this.nodes.get(d).addNeighbor(this.nodes.get(s));
            this.nodes.get(d).addEdge(this.nodes.get(s).N, new Edge(this.nodes.get(d), this.nodes.get(s), beann.getUtility(), beann.getId()));
        }
    }
}
