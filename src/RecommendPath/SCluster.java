package RecommendPath;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class SCluster {
    /**
     * 矩阵操作的东西还是引用jama包
     *
     * @param data 数据集
     * @param K    K个类别
     */
    private ArrayList<ScCluster> clusters;
    public SCluster(ArrayList<ScDataPoint> data, int K) {
        //把这个 Graph 用邻接矩阵的形式表示出来，记为 W
        int len = data.size();
        double[][] W = new double[len][len];
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                W[i][j] = EditDistance.similarity(data.get(i).getData(), data.get(j).getData());
            }
        }
//        //输出相似矩阵W
//        System.out.println("输出W矩阵");
//        for (int i = 0; i < len; i++) {
//            for (int j = 0; j < len; j++) {
//                System.out.printf("%2f ",W[i][j]);
//            }
//            System.out.println();
//        }
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
            for (int j = 0; j < W.length; j++) {
                if (i == j)
                {
                    L[i][j] = D[i] - L[i][j];
                }
                else {
                    L[i][j] = -L[i][j];
                }
            }
        }
        //输出L矩阵
//        System.out.println("输出L矩阵");
//        for (int i = 0; i < len; i++) {
//            for (int j = 0; j < len; j++) {
//                System.out.printf("%2f ",L[i][j]);
//            }
//            System.out.println();
//        }
        //求出 L 的前 k 个特征值（在本文中，除非特殊说明，否则“前 k 个”指按照特征值的大小从小到大的顺序）
        // \{\lambda\}_{i=1}^k 以及对应的特征向量 \{\mathbf{v}\}_{i=1}^k 。引用jama包
        Matrix L_matrix = new Matrix(L);
        //这里求特征值可以通过幂迭代的方法求特征值，借此提升计算速度
        EigenvalueDecomposition eig = L_matrix.eig();


        double[] eigs = eig.getRealEigenvalues();
        double[][] eig_vecs = eig.getV().transpose().getArray();
        //取前K大，使用TreeMap，按照key排序，从小到大取K个出来
        TreeMap<Double, double[]> treeMap = new TreeMap<>();
        for (int i = 0; i < eigs.length; i++) {
            treeMap.put(eigs[i], eig_vecs[i]);
        }
        double[][] NK = new double[len][K];
        //产生N*K的矩阵
        int count = 0;
        for (Map.Entry<Double, double[]> entry:treeMap.entrySet()) {
            if (count >= K)
            {
                break;
            }
            for (int i = 0; i < len; i++) {
                NK[i][count] = entry.getValue()[i];
            }

            count++;
        }
        for (int i = 0; i < len; i++) {
            data.get(i).setTempData(NK[i]);
        }
        K_means k_means = new K_means(K,data);
        //执行算法
        k_means.execute();
        //得到聚类结果
        clusters = k_means.getClusters();

        //查看结果
        for (int i = 0; i < clusters.size(); i++) {
            System.out.println("第"+i+"个簇类");
            for (ScDataPoint scDataPoint:clusters.get(i).getScDataPoints()
                 ) {
                System.out.println("路径是："+scDataPoint.getData()+"名字是："+scDataPoint.getDataPointName());
            }
        }


        }
        public ArrayList<ScCluster> getCluster()
        {
            return clusters;
        }
    }

