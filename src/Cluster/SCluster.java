package Cluster;

import Jama.Matrix;
import Util.EditDistance;
import Util.EigDec;
import Util.Util;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.doublealgo.Transform;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;


import java.io.IOException;
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

    public SCluster(ArrayList<ScDataPoint> data, int K) {
        double hyperparameter = 1;
        //把这个 Graph 用邻接矩阵的形式表示出来，记为 W
        int len = data.size();
        double[][] W = new double[len][len];
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {

                double dis = EditDistance.similarity(data.get(i).getData(), data.get(j).getData());
//                W[i][j] = dis;
                W[i][j] = Math.exp((0-dis*dis)/(2*hyperparameter*hyperparameter));
            }
        }

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

        //求出 L 的前 k 个特征值（在本文中，除非特殊说明，否则“前 k 个”指按照特征值的大小从小到大的顺序）
        // \{\lambda\}_{i=1}^k 以及对应的特征向量 \{\mathbf{v}\}_{i=1}^k 。引用jama包
        //这里求特征值可以通过幂迭代的方法求特征值，借此提升计算速度，采用幂迭代的扩展直接求出K个最小的特征值，减少计算量
        //inverse iteration 就是 对A矩阵求逆，然后跑power iteration 代码即可得到最小的特征值
        //这里求逆矩阵的方法可以自己写而不用导包
//        Matrix L_matrix = new Matrix(L);
//        Matrix Inverse_L_matrix =  L_matrix.inverse();
//        double[][] Inverse_L = Inverse_L_matrix.getArray();
//
//        ArrayList<double[]> ys = new ArrayList();
//        ArrayList<Double> lamdbas = new ArrayList();
//        ArrayList<double[][]> sub_matrix = new ArrayList();
//        ArrayList<double[]> eigRec = PowerIteration(inverse_l);
//        double eig = slove(eigRec.get(0));
//        double[] eig_vec = eigRec.get(1);
//        ys.add(eig_vec);
//        lamdbas.add(eig);
//        sub_matrix.add(Inverse_L);

//        for (int i = 1; i < K; i++) {
//            cal(i,ys,lamdbas,sub_matrix);
//        }
//        double[][] NK = new double[len][K];
//        //产生N*K的矩阵
//        int count = 0;
//        for (;count< ys.size();count++) {
//            for (int i = 0; i < len; i++) {
//                NK[i][count] = ys.get(count)[i];
//            }
//        }


//        EigenvalueDecomposition L_matrix_ed = new EigenvalueDecomposition(L_matrix);
//        double[] eigs = L_matrix_ed.getRealEigenvalues().toArray();
//        Algebra  alg = new Algebra();
//        double[][] eig_vecs = alg.transpose(L_matrix_ed.getV()).toArray();
//
//        //取前K大，使用TreeMap，按照key排序，从小到大取K个出来
//        TreeMap<Double, double[]> treeMap = new TreeMap<>(new Comparator<Double>() {
//            @Override
//            public int compare(Double o1, Double o2) {
//                int i = -1;
//                if (o1 < o2)
//                    i = 1;
//                return i;
//            }
//        });
        //----------------------------------------------------
        // 这里使用的是自己编写的幂迭代
        //----------------------------------------------------
//        Matrix Inverse_L_matrix = new Matrix(L_matrix.toArray());
//       double[][] Inverse_L = Inverse_L_matrix.getArray();
//        Matrix L = new Matrix(Inverse_L);
//        EigDec ed = new EigDec();
//        ArrayList<Matrix> evs = ed.Weilandt(L, K);

        //----------------------------------------------------
        // 这里使用python的脚本代码计算k个最小的特征值
        //----------------------------------------------------
        Util.write(L_matrix.toArray());
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec("python  sa.py");
            proc.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("python脚本运行出错");
        }
        ArrayList<double[]> evs_a = Util.read();

        //----------------------------------------------------
//        for (int i = 0; i < eigs.length; i++) {
//            treeMap.put(eigs[i], eig_vecs[i]);
//        }
        double[][] NK = new double[len][K];
        //产生N*K的矩阵
        int count = 0;
        for (double[] entry : evs_a) {
//        for (Map.Entry<Double, double[]> entry:treeMap.entrySet()) {
            if (count >= K) {
                break;
            }
            for (int i = 0; i < len; i++) {
                NK[i][count] = entry[i];
//                NK[i][count] = entry.getValue()[i];
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

        //查看结果
        for (int i = 0; i < clusters.size(); i++) {
            System.out.println("第" + i + "个簇类");
            for (ScDataPoint scDataPoint : clusters.get(i).getScDataPoints()
                    ) {
                System.out.println("路径是：" + scDataPoint.getData() + "名字是：" + scDataPoint.getDataPointName());
            }
        }
    }

    public ArrayList<ScCluster> getCluster() {
        return clusters;
    }

}

