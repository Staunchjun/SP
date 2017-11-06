package GuideDataStructure;

import Util.Bag;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ruan on 9/12/16.
 */
public class Node {
    //Basic property
    public int N;//number
    public Bag<Node> adj;
    public Map<Integer, Edge> adjEdge;
    public double x; //coordinates x
    public double y;//coordinates y
    public double P = 0;//probability

    // For A*
    public double G = 0;//real cost
    public double H = 0;//"Direction" to end point
    public double F = 0;//G+H = F
    public Node parent;

    public Node() {
        adj = new Bag<Node>();
        adjEdge = new HashMap<Integer, Edge>();
    }

    public void addNeighbor(Node n) {
        adj.add(n);
    }

    public Bag<Node> getNeighbors() {
        return adj;
    }

    public Map<Integer, Edge> getAdjEdge() {
        return adjEdge;
    }

    public void addEdge(Integer edgeto, Edge edge) {
        adjEdge.put(edgeto, edge);
    }

    //Copy original node
    public Node clone(Node newNode) {
        newNode.N = N;
        newNode.adj = adj;
        newNode.adjEdge = adjEdge;
        newNode.x = x;
        newNode.y = y;
        newNode.P = P;
        newNode.G = G;
        newNode.H = H;
        newNode.F = F;
        newNode.parent = parent;

        return newNode;
    }

}
