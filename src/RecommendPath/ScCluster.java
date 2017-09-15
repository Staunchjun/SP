package RecommendPath;

import java.util.ArrayList;
import java.util.List;

public class ScCluster {
    private List<ScDataPoint> ScDataPoints = new ArrayList<ScDataPoint>(); // 类簇中的样本点
    private String clusterName;

    public List<ScDataPoint> getScDataPoints() {
        return ScDataPoints;
    }

    public void setScDataPoints(List<ScDataPoint> ScDataPoints) {
        this.ScDataPoints = ScDataPoints;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }
}
