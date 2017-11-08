package Util;
import Jama.Matrix;
import java.util.ArrayList;
public class EigDec {
    /**
     * Weilandt 使用PowerMethod2
     * @param A  输入矩阵
     * @param k 迭代次数
     * @return
     */
    public ArrayList<Matrix> Weilandt(Matrix A, int k) {
        ArrayList<Matrix> evs = new ArrayList<>();
        int col = A.getColumnDimension();
        double[][] x = new double[col][1];
        for (int i = 0; i < col; i++) {
                x[i][0] = 1;
        }
        //Weilandt Deflation Method
        Matrix x_matrix = new Matrix(x);
        EigDec ed = new EigDec();
        Matrix B1 = A;
        FourTuple<Matrix, ArrayList<Double>, Integer, Integer> result11 = ed.PowerMethod2(B1, x_matrix, 0.0001, 2000);
        double r1 = result11.second.get(result11.fourth);
        System.out.println(r1);
        Matrix u1 = result11.first;
        Matrix x1 = B1.getMatrix(0, 0, 0, col-1).transpose().times(1 / r1 * u1.get(0, 0));
        Matrix v1 = u1;
        evs.add(v1);
        for (int i = 1; i < k; i++) {

            Matrix B2 = B1.minus(u1.times(r1).times(x1.transpose()));
            FourTuple<Matrix, ArrayList<Double>, Integer, Integer> result22 = ed.PowerMethod2(B2, x_matrix, 0.0001, 2000);
            double r2 = result22.second.get(result22.fourth);
            System.out.println(r2);
            Matrix u2 = result22.first;
            Matrix x2 = B2.getMatrix(i, i, 0, col-1).transpose().times(1 / r2 * u2.get(i, 0));
            Matrix v2 = u2.times(r2 - r1).plus(u1.times(r1).times(x1.transpose().times(u2)));
            x1 = x2;
            r1 = r2;
            B1 = B2;
            u1 = u2;
            evs.add(v2);
        }
        return evs;
    }

    /**
     * 幂迭代求特征值
     *
     * @param A 输入矩阵
     * @param x         初始化矩阵
     * @param tol      错误率
     * @param maxIter 迭代次数
     * @return FourTuple<Matrix, ArrayList<Double>, Integer, Integer>  xsol 特征向量 rv 特征值 flag是否收敛 k 迭代次数
     */
    public FourTuple<Matrix, ArrayList<Double>, Integer, Integer> PowerMethod2(Matrix A, Matrix x, double tol, int maxIter) {
        /**
         * xv(:,1)=x/norm(x,inf);
         k=1;
         flagct=0;
         */
        Matrix xv_1 = x.times(1 / x.normInf());
        ArrayList<Matrix> xv = new ArrayList<>(maxIter);
        xv.add(xv_1);
        int k = 1;
        int flagct = 0;
        int flag = -1;
        Matrix xsol = null;
        ArrayList<Double> rv = new ArrayList<>(maxIter);
        ArrayList<Double> xn = new ArrayList<>(maxIter);
        while (flagct == 0) {
            /**
             * y=A*xv(:,k);
             absy=abs(y);
             [r,I]=max(absy);
             rv(k)=y(I);
             xv(:,k+1)=y/y(I);
             xn(k)=norm(xv(:,k+1)-xv(:,k),inf);
             */
            double max_in_col = Integer.MIN_VALUE;
            int max_in_col_index = Integer.MIN_VALUE;

            Matrix y = A.times(xv.get(k - 1));
            double[][] y_arr = y.getArray();
            for (int j = 0; j < y_arr.length; j++) {
                if (y_arr[j][0] < 0) {
                    y_arr[j][0] = -y_arr[j][0];
                }

                if (y_arr[j][0] > max_in_col) {
                    max_in_col = y_arr[j][0];
                    max_in_col_index = j;
                }
            }
            rv.add(y.get(max_in_col_index, 0));
            xv.add(y.times(1 / y.get(max_in_col_index, 0)));
            xn.add((xv.get(k).minus(xv.get(k - 1))).normInf());
            if (xn.get(k - 1) > tol) {
                k = k + 1;
                if (k > maxIter) {
                    flagct = 1;
                    flag = 1;
                    xsol = xv.get(k - 1);
                    k = k - 2;
                    System.out.println("Power Method does not converge in kmax iterations");
                }
            } else {
                xsol = xv.get(k);
                flagct = 1;
                flag = 0;
                k = k - 1;
                System.out.println("Power Method converges");
            }

        }
        FourTuple<Matrix, ArrayList<Double>, Integer, Integer> res =
                new FourTuple<Matrix, ArrayList<Double>, Integer, Integer>(xsol, rv, flag, k);
        return res;
    }

    /**
     *
     * @param A 输入矩阵
     * @param x 初始化矩阵
     * @param tol 错误率
     * @param maxIter 迭代次数
     * @return TwoTuple<Double, Matrix> 特征值 特征向量
     */
    public TwoTuple<Double, Matrix> PowerMethod(Matrix A, Matrix x, double tol, int maxIter) {
        int k = 1;
        int p = 1;
        int n = A.getColumnDimension();
        int ixp = 0;
        double ix = x.normInf();

        // 取出 p 位置上的元素；
        while (p <= n) {
            if (Math.abs(x.get(p - 1, 0)) == ix) {
                ixp = p;
                break;
            }
            p++;
        }
        x = x.times(1 / ix);
        while (k <= maxIter) {
            Matrix y = A.times(x);
            double mu = y.get(ixp - 1, 0);
            p = 1;
            double iy = y.normInf();
            int iyp = 0;
            while (p <= n) {
                if (Math.abs(y.get(p - 1, 0)) == iy) {
                    iyp = p;
                    break;
                }
                p++;
            }
            if (y.get(iyp - 1, 0) == 0) {
                System.out.println("A has the eigenvalue 0, repick x and restart");

                return new TwoTuple<Double, Matrix>(0.0, x);
            }
            double err = (x.minus(y.times(1 / iy))).normInf();
            x = y.times(1 / iy);
            if (err < tol) {
                double eigval = mu;
                return new TwoTuple<Double, Matrix>(eigval, x);
            }
            k++;
        }
        System.out.println("Max number of iterations exceeded");
        p = -1;
        return new TwoTuple<Double, Matrix>(p * 1.0, x);
    }

    /**
     *
     * @param A 输入矩阵
     * @param x 初始化矩阵
     * @param tol 错误率
     * @param maxIter 迭代次数
     * @return 特征向量
     */
    public Matrix AcceleratedPowerMethod(Matrix A, Matrix x, double tol, int maxIter) {
        int k = 1;
        double muZero = 0;
        double muOne = 0;
        int p = 1;
        int n = A.getColumnDimension();
        int r = A.getRowDimension();
        double ix = x.normInf();
        int ixp = 0;
        while (p <= n) {
            if (Math.abs(x.get(p - 1, 0)) == ix) {
                ixp = p;
                break;
            }
            p++;
        }
        x = x.times(1 / ix);
        while (k <= maxIter) {
            Matrix y = A.times(x);
            double mu = y.get(ixp - 1, 0);
            double muHat = muZero - (Math.pow(muOne - muZero, 2)) / (mu - 2 * muOne + muZero);
            p = 1;
            double iy = y.normInf();
            int iyp = 0;
            while (p <= n) {
                if (Math.abs(y.get(p - 1, 0)) == iy) {
                    System.out.println("A has the eigenvalue 0, repick x and restart");
                    return x;
                }
                double err = x.minus(y.times(1 / iy)).normInf();
                x = y.times(1 / iy);
                if (err < tol && k >= 4) {
                    System.out.println("eigvalue");
                    System.out.println(muHat);
                    System.out.println("eigvector");
                    System.out.println(x);
                    return x;
                }
                k++;
                muZero = muOne;
                muOne = mu;
            }
        }
        System.out.println("Max number of iterations exceeded");
        return x;
    }

    /**
     * Wielandt_Deflation 使用PowerMethod1
     * @param A 输入矩阵
     * @param eigValue 特征值
     * @param x 初始化矩阵
     * @param eigVec    特征向量
     * @param tol 错误率
     * @param maxIter 迭代次数
     * @return 特征值 特征向量
     */
    public TwoTuple<Double, double[]> Wielandt_Deflation(Matrix A, double eigValue, Matrix x, Matrix eigVec, double tol, int maxIter) {
        int i = 1;
        int n = A.getRowDimension();
        double m = getMaxFromMatrix(eigVec);
        double[][] B_arr = new double[n - 1][n - 1];
        double[] W_arr = new double[n];
        double[] U_arr = new double[n];
        while (i <= eigVec.getRowDimension()) {
            if (Math.abs(eigVec.get(i / n, i - i / n * n - 1)) == m) {
                break;
            }
            i++;
        }
        if (i != 1) {
            int k = 1;
            while (k <= (i - 1)) {
                int j = 1;
                while (j <= (i - 1)) {
                    B_arr[k - 1][j - 1] = A.getArray()[k - 1][j - 1] -
                            eigVec.getArray()[0][i - 1] * A.getArray()[i - 1][j - 1];
                    j++;
                }
                k++;
            }
        }
        if (i != 1 && i != n) {
            int k = i;
            while (k <= (n - 1)) {
                int j = 1;
                while (j <= (i - 1)) {
                    B_arr[k - 1][j - 1] = A.getArray()[k][j - 1] -
                            eigVec.getArray()[k][0] / eigVec.getArray()[i - 1][0] * A.getArray()[i - 1][j - 1];
                    B_arr[j - 1][k - 1] = A.getArray()[j - 1][k] -
                            eigVec.getArray()[j - 1][0] / eigVec.getArray()[i - 1][0] * A.getArray()[i - 1][k];
                    j++;
                }
                k++;
            }
        }
        if (i != n) {
            int k = i;
            while (k <= n - 1) {
                int j = i;
                while (j <= n - 1) {

                    B_arr[k - 1][j - 1] = A.getArray()[k][j] -
                            eigVec.getArray()[k][0] / eigVec.getArray()[i - 1][0] * A.getArray()[i][j - 1];
                    j++;
                }
                k++;
            }
        }
        double[][] x1 = new double[B_arr.length][B_arr[0].length];
        for (int j = 0; j < B_arr.length; j++) {
            for (int k = 0; k < B_arr[0].length; k++) {
                x1[j][k] = 1;
            }
        }
        Matrix x1_m = new Matrix(x1);
        TwoTuple<Double, Matrix> twoTuple = PowerMethod(new Matrix(B_arr), x1_m, tol, maxIter);
        Matrix wPrime = twoTuple.second;
        double mu = twoTuple.first;
        if (mu == -1) {
            System.out.println("fail");
            return null;
        }
        if (i != 1) {
            int k = 1;
            while (k <= i - 1) {
                W_arr[k - 1] = wPrime.getArray()[k - 1][0];
                k++;
            }
        }
        W_arr[i - 1] = 0;
        if (1 != n) {
            int k = i + 1;
            while (k <= n) {
                W_arr[k - 1] = wPrime.getArray()[k - 2][0];
                k++;
            }
        }
        int k = 1;
        while (k <= n) {

            int j = 1;
            double sum = 0;
            while (j <= n) {
                sum = sum + A.getArray()[i - 1][j - 1] * W_arr[j - 1];
                j++;
            }
            U_arr[k - 1] = (mu - eigValue) * W_arr[k - 1] + eigVec.getArray()[k - 1][0] / eigVec.getArray()[i - 1][0] * sum;
            k++;

        }
        W_arr = U_arr;
        double val = mu;
        return new TwoTuple<Double, double[]>(val, W_arr);
    }

    /**
     * 获得矩阵中的最大值
     *
     * @param eigvec 输入矩阵
     * @return 最大值
     */
    public double getMaxFromMatrix(Matrix eigvec) {
        double[][] eigvec_arr = eigvec.getArray();
        double max = Integer.MIN_VALUE;
        for (int i = 0; i < eigvec_arr.length; i++) {
            for (int j = 0; j < eigvec_arr[0].length; j++) {
                double temp = eigvec_arr[i][j];
                if (temp > max) {
                    max = temp;
                }
            }

        }
        return max;
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

}

