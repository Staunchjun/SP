package Cluster;

import java.util.ArrayList;
import java.util.List;

public class HcCluster {
    /**
     * 层次聚类的数据结构
     */
    private List<HcDataPoint> hcDataPoints = new ArrayList<HcDataPoint>(); // 类簇中的样本点
    private String clusterName;

    public List<HcDataPoint> getHcDataPoints() {
        return hcDataPoints;
    }

    public void setHcDataPoints(List<HcDataPoint> hcDataPoints) {
        this.hcDataPoints = hcDataPoints;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }
}
