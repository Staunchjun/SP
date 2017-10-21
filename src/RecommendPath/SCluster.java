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


        //这里求特征值可以通过幂迭代的方法求特征值，借此提升计算速度，采用幂迭代的扩展直接求出K个最小的特征值，减少计算量
        //inverse iteration 就是 对A矩阵求逆，然后跑power iteration 代码即可得到最小的特征值
        //这里求逆矩阵的方法可以自己写而不用导包
        Matrix L_matrix = new Matrix(L);
        Matrix Inverse_L_matrix =  L_matrix.inverse();
        double[][] Inverse_L = Inverse_L_matrix.getArray();

        ArrayList<double[]> ys = new ArrayList();
        ArrayList<Double> lamdbas = new ArrayList();

        for (int i = 0; i < K; i++) {
            cal(Inverse_L,i,ys,lamdbas);
        }



        double[][] NK = new double[len][K];
        //产生N*K的矩阵
        int count = 0;
        for (;count< ys.size();count++) {
            for (int i = 0; i < len; i++) {
                NK[i][count] = ys.get(count)[i];
            }
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


    /**
     * 幂迭代求特征值
     *
     * @param A
     * @return 0 为特征值 1 为特征向量
     */
    private ArrayList<double[]> PowerIteration(double[][] A) {
        int N = A.length;
        //先任取一个初始向量X
        double[] x = new double[N];
        for (int i = 0; i < N; i++) {
            x[i] = Math.random();
        }
        //初始化特征向量v ，u，p,e,delta
        double[] v = new double[N];
        double[] u = new double[N];
        double[] p = new double[N];
        for (int i = 0; i < N; i++) {
            v[i] = 0;
            u[i] = 0;
            p[i] = 0;
        }
        double e = 1e-10, delta = 1;
        int k = 0;
        while (delta >= e) {
            for (int q = 0; q < N; q++) p[q] = v[q];
            for (int i = 0; i < N; i++) {
                v[i] = 0;
                for (int j = 0; j < N; j++)
                    v[i] += A[i][j] * x[j];
            }
            for (int i = 0; i < N; i++) u[i] = v[i] / (slove(v));
            delta = Math.abs(slove(v) - slove(p));
            k++;
            for (int l = 0; l < N; l++) x[l] = u[l];
        }
        System.out.println("迭代次数：" + k);
        System.out.println("矩阵的特征值："+slove(v));
        System.out.println("矩阵的特征向量");
        for (int i = 0; i < N; i++) {
            System.out.println("（" + u[i] + "）");
        }
        ArrayList<double[]> arrayList = new ArrayList<double[]>();
        arrayList.add(u);
        arrayList.add(v);
        return arrayList;
    }

    /**
     *
     * @param v
     * @return
     */
    public double slove(double[] v)
    {
        //slove v[N]
        int N = v.length;
        double max = 0;
        for(int i=0;i<N-1;i++)
        {max=v[i]>v[i+1]?v[i]:v[i+1];}
        return max;
    }

    private void cal(double[][] inverse_l, int i, ArrayList<double[]> ys,ArrayList<Double> lamdbas) {
      /* k_matrix = m(matrix, k)
        cur_lambda, res = cal_lambda(k_matrix)
        lambda_[k] = cur_lambda
         #    res = f(k_matrix)
        for i in range(0, n-k):
        last_lambda = lambda_[n-i]
        res = g(res, last_lambda)
        y[k] = res
      */
        ArrayList<double[]> eigRec = PowerIteration(inverse_l);
        double eig = slove(eigRec.get(0));
        double[] eig_vec = eigRec.get(1);
        ys.add(eig_vec);
        lamdbas.add(eig);

        double[] res = eig_vec;
        for (int j = 0; j < inverse_l.length - i; j++) {
            double last_lambda = lamdbas.get(inverse_l.length-j);
            res = Deflation(inverse_l,last_lambda);
        }
        ys.add(res);
    }
    public double[][] A2A_2(double[][] A)
    {
        int len = A.length;
        Matrix A_matrix = new Matrix(A);
        double[][] y = new double[len][len];
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                if (i == 0 && j == 0)
                    y[i][j] = 1;
                else
                    y[i][j] = 0;
            }
        }
        Matrix y_matrix = new Matrix(y);
        double rho = A_matrix.norm2() / y_matrix.norm2();
        y_matrix = y_matrix.times(rho);
        Matrix v_matrix = A_matrix.minus(y_matrix).times((A_matrix.minus(y_matrix)).norm2());
        double[][] I = new double[len][len];
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                if (i == j)
                    I[i][j] = 1;
                else
                    I[i][j] = 0;
            }
        }
        Matrix H_matrix = v_matrix.times(v_matrix.transpose()).times(2);
        Matrix A_2_matrix = H_matrix.times(A_matrix).times(H_matrix.transpose());
        double[][] A_2 = A_2_matrix.getArray();
        return A_2;
    }


    // Find deflation matrix
     public  double[] Deflation(double[][] A,double eig)
    {
            double[][] A_2 = A2A_2(A);
            int len_b = A_2.length - 1;
            double[][] B_2 = new double[len_b][len_b];
            for (int i = 0; i < len_b; i++) {
                for (int j = 0; j < len_b; j++) {
                    B_2[i][j] = A_2[i + 1][j + 1];
                }
            }
            double[] b_1 = new double[len_b];
            for (int i = 0; i < len_b; i++) {
                b_1[i] = A_2[0][i + 1];
            }

            ArrayList<double[]> eigRec = PowerIteration(B_2);
            double lambda_2 = slove(eigRec.get(0));
            double[] y_2 = eigRec.get(1);
            double up_value = 0;
            for (int i = 0; i < b_1.length; i++) {
                up_value += b_1[i] * y_2[i];
            }
            double alpha = up_value / (eig - lambda_2);
            double[] z_2 = new double[A_2.length+1];
            for (int i = 0; i < A_2.length+1; i++) {
                if (i == 0) {
                    z_2[i] = alpha;
                } else {
                    z_2[i] = y_2[i - 1];
                }
            }
            return z_2;
    }


    public ArrayList<ScCluster> getCluster()
        {
            return clusters;
        }
}

