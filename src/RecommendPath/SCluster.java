package RecommendPath;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SCluster
{
    /**
     *矩阵操作的东西还是引用jama包
     *
     * @param W 把这个 Graph 用邻接矩阵的形式表示出来，记为 W
     * @param K K个类别
     */

    public SCluster(double[][] W,int K)
    {

        //把 W 的每一列元素加起来得到 N 个数，把它们放在对角线上（其他地方都是零），组成一个 N*N的矩阵，记为 D 。
        double[] D = new double[W.length];
        for (int i = 0; i < W.length; i++) {
            double sum = 0;
            for (int j = 0; j < W.length; j++) {
                sum += W[j][i];
            }
            D[i] = sum;
        }
        // L = D-W
        double[][] L = W.clone();
        for (int i = 0; i < W.length; i++) {
            L[i][i] = L[i][i] - D[i];
        }
        //求出 L 的前 k 个特征值（在本文中，除非特殊说明，否则“前 k 个”指按照特征值的大小从小到大的顺序）
        // \{\lambda\}_{i=1}^k 以及对应的特征向量 \{\mathbf{v}\}_{i=1}^k 。引用jama包
        Matrix L_matrix = new Matrix(L);
        EigenvalueDecomposition eig= L_matrix.eig();
        double[] eigs = eig.getRealEigenvalues();
        double[][] eig_vecs = eig.getV().transpose().getArray();
        //取前K大，使用TreeMap，按照key排序，从小到大取K个出来
        TreeMap<Double, double[]> treeMap = new TreeMap<>();
        for (int i = 0; i < eigs.length; i++) {
            treeMap.put(eigs[i],eig_vecs[i]);
        }
        ArrayList<double[]> dataSet = new ArrayList<>();
        for (Map.Entry<Double, double[]> entry:treeMap.entrySet()) {
            dataSet.add(entry.getValue());
        }
        K_means k_means = new K_means(K);
        //设置原始数据集
        k_means.setDataSet(dataSet);
        //执行算法
        k_means.execute();
        //得到聚类结果
        ArrayList<ArrayList<double[]>> cluster=k_means.getCluster();
        //查看结果
        for(int i=0;i<cluster.size();i++)
        {
            k_means.printDataArray(cluster.get(i), "cluster["+i+"]");
        }

    }

    class DataPoint {
        private RecommendPath.Cluster cluster;
        private String data;
        private String dataPointName;

        public DataPoint(String data) {
            this.data = data;
        }

        public DataPoint(String data, String dataPointName) {
            this.data = data;
            this.dataPointName = dataPointName;
        }

        public DataPoint() {

        }

        public String getDataPointName() {
            return dataPointName;
        }

        public RecommendPath.Cluster getCluster() {
            return cluster;
        }

        public void setCluster(RecommendPath.Cluster cluster) {
            this.cluster = cluster;
        }

        public String getData() {
            return data;
        }
    }

    class Cluster {
        private List<RecommendPath.DataPoint> dataPoints = new ArrayList<RecommendPath.DataPoint>(); // 类簇中的样本点
        private String clusterName;

        public List<RecommendPath.DataPoint> getDataPoints() {
            return dataPoints;
        }

        public void setDataPoints(List<RecommendPath.DataPoint> dataPoints) {
            this.dataPoints = dataPoints;
        }

        public String getClusterName() {
            return clusterName;
        }

        public void setClusterName(String clusterName) {
            this.clusterName = clusterName;
        }
    }
}
