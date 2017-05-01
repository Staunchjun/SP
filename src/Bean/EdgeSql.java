package Bean;

/**
 * Created by Administrator on 2017/3/21.
 */
public class EdgeSql {
    /**
     * id : 19
     * Node_id1:1
     * Nodeid2:2
     * utility;0
     */
    private int id;
    private int Node_id1;
    private int Nodeid2;
    private int utility;
    public int getId() {
        return id;
    }

    public int getUtility() {
        return utility;
    }

    public void setUtility(int utility) {
        this.utility = utility;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNode_id1() {
        return Node_id1;
    }

    public void setNode_id1(int node_id1) {
        Node_id1 = node_id1;
    }

    public int getNodeid2() {
        return Nodeid2;
    }

    public void setNodeid2(int nodeid2) {
        Nodeid2 = nodeid2;
    }
}
