package Cluster;

import java.util.ArrayList;
import java.util.List;

/**
 * 谱聚类的数据结构
 */
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
