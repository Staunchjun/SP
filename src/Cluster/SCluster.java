package Cluster;

import Util.EditDistance;
import Util.Util;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.doublealgo.Transform;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import java.util.*;

/**
 * 谱聚类的算法
 */
public class SCluster {
    /**
     * 矩阵操作的东西还是引用jama包
     *
     * @param data 数据集
     * @param K    K个类别
     */
    private ArrayList<ScCluster> clusters;
    public double sparRatio;
    public ArrayList<ScDataPoint> data;
    public SCluster(ArrayList<ScDataPoint> data, int K,double threshold) {
        double hyperparameter = 1;
        //把这个 Graph 用邻接矩阵的形式表示出来，记为 W
        int len = data.size();
        System.out.println("构建邻接矩阵");
        double[][] W = new double[len][len];
        int spar = 0;
        System.out.println("构建邻接矩阵"+len);
        for (int i = 0; i < len; i++) {
//            System.out.println("现在运行第"+i+"轮");

            for (int j = i; j < len; j++) {
//                System.out.println("现在运行第"+i+"轮"+"第"+j+"次");
                double dis = EditDistance.similarity(data.get(i).getData(), data.get(j).getData());
                if (dis< threshold) {
                    dis = 0;
                    spar++;
                }
                W[i][j] = dis;
                W[j][i] = dis;
//                W[i][j] = Math.exp((0-dis*dis)/(2*hyperparameter*hyperparameter));
            }
        }
        sparRatio = spar*1.0/(len*len);

        //输出相似矩阵W
//        System.out.println("输出W矩阵");
//        for (int i = 0; i < len; i++) {
//            for (int j = 0; j < len; j++) {
//                System.out.printf("%2f ",W[i][j]);
//            }
//            System.out.println();
//        }
        //把 W 的每一列元素加起来得到 N 个数，把它们放在对角线上（其他地方都是零），组成一个 N*N的矩阵，记为 D 。
        double[][] D = new double[W.length][W.length];
        for (int i = 0; i < W.length; i++) {
            double sum = 0;
            for (int j = 0; j < W.length; j++) {
                sum += W[i][j];
                D[i][j] = 0;
            }
            D[i][i] = sum;
        }

        DoubleMatrix2D W_matrix = new DenseDoubleMatrix2D(W.length, W.length);
        DoubleMatrix2D D_matrix = new DenseDoubleMatrix2D(W.length, W.length);
        W_matrix.assign(W);
        D_matrix.assign(W);


        System.out.println("构建拉普拉斯矩阵");
        DoubleMatrix2D D_matrix_1div2 = Transform.pow(D_matrix, 0.5);
        DoubleMatrix2D TempMatrix = Transform.mult(D_matrix_1div2, W_matrix);
        DoubleMatrix2D L_matrix = Transform.mult(TempMatrix, D_matrix_1div2);

        //输出L矩阵
//        System.out.println("输出L矩阵");
//        for (int i = 0; i < len; i++) {
//            for (int j = 0; j < len; j++) {
//                System.out.printf("%2f ",L[i][j]);
//            }
//            System.out.println();
//        }

        //----------------------------------------------------
        // 这里使用python的脚本代码计算k个最小的特征值
        //----------------------------------------------------
        Util.write(L_matrix.toArray());
        try {
            String[] args = new String[] { "python", "sa.py", String.valueOf(K)};
            Process proc = Runtime.getRuntime().exec(args);
            proc.waitFor();
            System.out.println("python脚本正在运行");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("python脚本运行出错");
        }
        ArrayList<double[]> evs_a = Util.read();

        //----------------------------------------------------
        double[][] NK = new double[len][K];
        //产生N*K的矩阵
        int count = 0;
        for (double[] entry : evs_a) {
            if (count >= K) {
                break;
            }
            for (int i = 0; i < len; i++) {
                NK[i][count] = entry[i];
            }

            count++;
        }

        for (int i = 0; i < len; i++) {
            data.get(i).setTempData(NK[i]);
        }
        K_means k_means = new K_means(K, data);
        //执行算法
        k_means.execute();
        //得到聚类结果
        clusters = k_means.getClusters();

//        查看结果 并且为每个数据点进行标注
        for (int i = 0; i < clusters.size(); i++) {
//            System.out.println("第" + i + "个簇类");
            for (ScDataPoint scDataPoint : clusters.get(i).getScDataPoints()) {
//                System.out.println("路径是：" + scDataPoint.getData() + "名字是：" + scDataPoint.getDataPointName());
                scDataPoint.setScCluster(clusters.get(i));
            }
        }

    }

 public ArrayList<ScCluster> getCluster() {
        return clusters;
    }

}

