package RecommendPath;

import Jama.Matrix;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.doublealgo.Transform;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.colt.matrix.linalg.EigenvalueDecomposition;

import java.util.ArrayList;
import java.util.Comparator;
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
        double hyperparameter = 1;
        //把这个 Graph 用邻接矩阵的形式表示出来，记为 W
        int len = data.size();
        double[][] W = new double[len][len];
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {

                double dis = EditDistance.similarity(data.get(i).getData(), data.get(j).getData());
                W[i][j] = dis;
//                W[i][j] = Math.exp((0-dis*dis)/(2*hyperparameter*hyperparameter));
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

        DoubleMatrix2D W_matrix = new DenseDoubleMatrix2D(W.length,W.length);
        DoubleMatrix2D D_matrix = new DenseDoubleMatrix2D(W.length,W.length);
        W_matrix.assign(W);
        D_matrix.assign(W);

        DoubleMatrix2D D_matrix_1div2 = Transform.pow(D_matrix,0.5);
        DoubleMatrix2D TempMatrix = Transform.mult(D_matrix_1div2,W_matrix);
        DoubleMatrix2D L_matrix = Transform.mult(TempMatrix,D_matrix_1div2);

//        // L = D-W
//        double[][] L = W.clone();
//        for (int i = 0; i < W.length; i++) {
//            for (int j = 0; j < W.length; j++) {
//
//                L[i][j] = D[i][j] - L[i][j];
//            }
//        }
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


//        Matrix L_matrix = new Matrix(L);
//        EigenvalueDecomposition eig = L_matrix.eig();
//        double[] eigs = eig.getRealEigenvalues();
//        double[][] eig_vecs = eig.getV().transpose().getArray();

        EigenvalueDecomposition L_matrix_ed = new EigenvalueDecomposition(L_matrix);
        double[] eigs = L_matrix_ed.getRealEigenvalues().toArray();
        Algebra  alg = new Algebra();
        double[][] eig_vecs = alg.transpose(L_matrix_ed.getV()).toArray();

        //取前K大，使用TreeMap，按照key排序，从小到大取K个出来
        TreeMap<Double, double[]> treeMap = new TreeMap<>(new Comparator<Double>() {
            @Override
            public int compare(Double o1, Double o2) {
                int i = -1;
                if (o1 < o2)
                    i = 1;
                return i;
            }
        });
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

    private void cal(int i, ArrayList<double[]> ys,ArrayList<Double> lamdbas,ArrayList<double[][]> sub_matrix) {
        i = i-1;
        double[][] inverse_l = sub_matrix.get(i);
        double last_lambda = lamdbas.get(i);
        double[] res = Deflation(inverse_l,last_lambda,lamdbas,sub_matrix);
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
     public  double[] Deflation(double[][] A,double eig,ArrayList<Double> lamdbas,ArrayList<double[][]> sub_matrix)
    {
        double[][] A_2 = A2A_2(A);
        sub_matrix.add(A_2);
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
        lamdbas.add(lambda_2);

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

