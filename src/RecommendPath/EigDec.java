package RecommendPath;

import Jama.Matrix;

public class EigDec {
    public TwoTuple<Double,Matrix> PowerMethod(Matrix A, Matrix x, double tol, int maxIter) {
        int k = 1;
        int p = 1;
        int n = A.getColumnDimension();
        int r = A.getRowDimension();
        int ixp = 0;
        double ix = x.normInf();
        // 取出 p 位置上的元素；
        int dif = p / r - p / r * r - 1;
        while (p <= n) {
            if (Math.abs(x.get(p / r, dif)) == ix) {
                ixp = p;
                break;
            }
            p++;
        }
        x = x.times(1 / ix);
        while (k <= maxIter) {
            Matrix y = A.times(x);
            double mu = y.get(ixp / r, ixp - ixp / r * r - 1);
            p = 1;
            double iy = y.normInf();
            int iyp = 0;
            while (p <= n) {
                if (Math.abs(y.get(p / r, p - p / r * r - 1)) == iy) {
                    iyp = p;
                    break;
                }
                p++;
            }
            if (y.get(iyp / r, iyp - iyp / r * r - 1) == 0) {
                System.out.println("A has the eigenvalue 0, repick x and restart");

                return new TwoTuple<Double,Matrix>(0.0,x);
            }
            double err = x.minus(y.times(1 / iy)).normInf();
            x = y.times(1 / iy);
            if (err < tol) {
                double eigval = mu;
                return new TwoTuple<Double,Matrix>(eigval,x);
            }
            k++;
        }
        System.out.println("Max number of iterations exceeded");
        p = -1;
        return new TwoTuple<Double,Matrix>(p*1.0,x);
    }

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
            if (Math.abs(x.get(p / r, p - p / r * r - 1)) == ix) {
                ixp = p;
                break;
            }
            p++;
        }
        x = x.times(1 / ix);
        while (k <= maxIter) {
            Matrix y = A.times(x);
            double mu = y.get(ixp / r, ixp - ixp / r * r - 1);
            double muHat = muZero - (Math.pow(muOne - muZero, 2)) / (mu - 2 * muOne + muZero);
            p = 1;
            double iy = y.normInf();
            int iyp = 0;
            while (p <= n) {
                if (Math.abs(y.get(p / r, p - p / r * r - 1)) == iy) {
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

    public TwoTuple<Double,double[]> Wielandt_Deflation(Matrix A, double eigValue, Matrix x, Matrix eigVec, double tol, int maxIter) {
        int i = 1;
        int n = A.getRowDimension();
        double m = getMaxFromMatrix(eigVec);
        double[][] B_arr = new double[n - 1][n - 1];
        double[] W_arr = new double[n];
        double[] U_arr = new double[n];
        while (i <= eigVec.getRowDimension()) {
            if (Math.abs(eigVec.get(i / n, i / n - i / n * n - 1)) == m) {
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
                            eigVec.getArray()[0][k] / eigVec.getArray()[0][i - 1] * A.getArray()[i - 1][j - 1];
                    B_arr[j - 1][k - 1] = A.getArray()[j - 1][k] -
                            eigVec.getArray()[0][j - 1] / eigVec.getArray()[0][i - 1] * A.getArray()[i - 1][k];
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
                            eigVec.getArray()[0][k] / eigVec.getArray()[0][i - 1] * A.getArray()[i][j - 1];
                    j++;
                }
                k++;
            }
        }
            TwoTuple<Double,Matrix> twoTuple = PowerMethod(new Matrix(B_arr), x, tol, maxIter);
            Matrix wPrime = twoTuple.second;
            double mu = twoTuple.first;
            if (mu == -1)
            {
                System.out.println("fail");
                return null;
            }
            if (i != 1) {
                int k = 1;
                while (k <= i - 1) {
                    W_arr[k - 1] = wPrime.getArray()[0][k - 1];
                    k++;
                }
            }
            W_arr[i - 1] = 0;
            if (1 != n) {
              int  k = i + 1;
                while (k <= n) {
                    W_arr[k - 1] = wPrime.getArray()[0][k - 2];
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
                U_arr[k - 1] = (mu - eigValue) * W_arr[k - 1] + eigVec.getArray()[0][k - 1] / eigVec.getArray()[0][i - 1] * sum;
                k++;

            }
            W_arr = U_arr;
            double val = mu;
            return new TwoTuple<>(val,W_arr);
        }

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

}

class TwoTuple<A, B> {

    public final A first;
    public final B second;

    public TwoTuple(A a, B b) {
        first = a;
        second = b;
    }

    public String toString() {
        return "("+first+","+second+")";
    }
}