package GuideDataStructure;
import java.util.List;

/**
 * Created by ruan on 16-9-14.
 */
public class Path {
    public double G = 0;
    public double U = 0;
    public List<Node> nodes;
    public List<Integer> edgeIds;

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Integer> getEdgeIds() {
        return edgeIds;
    }

    public double getG() {
        return G;
    }

    public void setG(double g) {
        G = g;
    }

    public double getU() {
        return U;
    }

    public void setU(double u) {
        U = u;
    }

    public Path(double g, double u) {
        G = g;
        U = u;
    }

    public Path(double g, double u, List<Node> nodes) {
        G = g;
        U = u;
        this.nodes = nodes;
    }

    public Path(double g, double u, List<Node> nodes, List<Integer> edgeIds) {
        G = g;
        U = u;
        this.nodes = nodes;
        this.edgeIds = edgeIds;
    }
}
