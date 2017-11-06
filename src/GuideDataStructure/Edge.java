package GuideDataStructure;

import Util.Util;

/**
 * Created by ruan on 16-10-10.
 */
public class Edge {
    public float cost, utility;
    private Node s;
    private Node d;
    public Integer id;
    public Edge(Node s, Node d, float utility) {
        cost = (float) Util.getDis(s, d);
        this.utility = utility;
    }

    public Edge(Node s, Node d, float utility, Integer id) {
        cost = (float) Util.getDis(s, d);
        this.utility = utility;
        this.id = id;
    }

    public Node other() {
        return d;
    }

}
