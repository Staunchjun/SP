package TestCode;
import Cluster.ScDataPoint;
import Jama.Matrix;
import Util.EigDec;
import Util.TwoTuple;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import org.python.util.PythonInterpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

public class testMatrix {
    public static void main(String[] args) throws Exception {

        write();
        Process proc = Runtime.getRuntime().exec("python  sa.py");
        proc.waitFor();
        read();
    }

    private static void testExtensionPowerMethod() {
        double[][] A = {{ 1, 2, 0.0, 1}, {2, 1, 0.0, 0.0}, {0, 0.0, 1.0, 0.0},{1, 0, 0,1}};
        double[][] x = {{1.0}, {1.0}, {1.0}, {1.0}};
        //Weilandt Deflation Method
        Matrix B1 = new Matrix(A);
        Matrix x_matrix = new Matrix(x);
        EigDec ed = new EigDec();
        ed.Weilandt(B1,4);
        TwoTuple<Double, Matrix> result = ed.PowerMethod(B1, x_matrix, 0.00001, 2000);
        double r1 = result.first;
        System.out.println(r1);
        Matrix u1 = result.second;
        Matrix x1 = u1.times(1 / Math.pow(u1.norm2(), 2));
        x1.print(1, 1);
        Matrix B2 = B1.minus(u1.times(r1).times(x1.transpose()));
        TwoTuple<Double, Matrix> result2 = ed.PowerMethod(B2, x_matrix, 0.00001, 2000);
        double r2 = result2.first;
        System.out.println(r2);
        Matrix u2 = result2.second;
        Matrix x2 = u2.times(1 / Math.pow(u2.norm2(), 2));
        x2.print(1, 1);
        Matrix B3 = B2.minus(u2.times(r2).times(x2.transpose()));
        TwoTuple<Double, Matrix> result3 = ed.PowerMethod(B3, x_matrix, 0.00001, 2000);
        double r3 = result3.first;
        System.out.println(r3);
        Matrix u3 = result3.second;
        Matrix x3 = u3.times(1 / Math.pow(u3.norm2(), 2));
        x3.print(1, 1);


        System.out.println("---------------------------------------------");
        ArrayList<Matrix> evs = ed.Weilandt(B1,3);
        for (Matrix a :
             evs) {
            a.print(5,5);

        }
        //Weilandt Deflation Method
//        FourTuple<Matrix, ArrayList<Double>, Integer, Integer> result11 = ed.PowerMethod2(B1, x_matrix, 0.0000000001, 100);
//        r1 = result11.second.get(result11.fourth);
//        System.out.println(r1);
//        u1 = result11.first;
//
//        x1 = B1.getMatrix(0, 0, 0, 2).transpose().times(1 / r1 * u1.get(0, 0));
//        Matrix v1 = u1;
//        Matrix v11 = B1.times(v1).minus(v1.times(r1));
//        v1.print(1,1);
////        v11.print(1,1);
//
//        B2 = B1.minus(u1.times(r1).times(x1.transpose()));
//        FourTuple<Matrix, ArrayList<Double>, Integer, Integer> result22 = ed.PowerMethod2(B2, x_matrix, 0.0000000001, 100);
//        r2 = result22.second.get(result22.fourth);
//        System.out.println(r2);
//        u2 = result22.first;
//
//        x2 = B2.getMatrix(1, 1, 0, 2).transpose().times(1 / r2 * u2.get(1, 0));
//        Matrix v2 = u2.times(r2-r1).plus(u1.times(r1).times(x1.transpose().times(u2)));
//        Matrix v22 = B1.times(v2).minus(v2.times(r2));
//        v2.print(1,1);
////        v22.print(1,1);
//
//        B3 = B2.minus(u2.times(r2).times(x2.transpose()));
//        FourTuple<Matrix, ArrayList<Double>, Integer, Integer>  result33 = ed.PowerMethod2(B3, x_matrix, 0.0000000001, 100);
//        r3 = result33.second.get(result33.fourth);;
//        System.out.println(r3);
//        u3 = result33.first;
//        Matrix v3 = u3.times(r3-r2).plus(u2.times(x2.transpose().times(u3).times(r2)));
//        Matrix v33 = B1.times(v3).minus(v3.times(r3));
//        v3.print(1,1);
////        v33.print(1,1);
    }

    public static void MainPIC(Matrix A, int k) {

        Matrix evec = Findevec(A, 0.5, 1e-3);
        Matrix defmat = Deflation(A, evec, 0.5, 1e-3);
        Matrix evec2 = Findevec(defmat, 0.5, 1e-3);
        Matrix defmat1 = Deflation(defmat, evec, 0.5, 1e-3);
        Matrix evec3 = Findevec(defmat1, 0.5, 1e-3);
        System.out.println(evec);
        System.out.println(evec2);
        System.out.println(evec3);

    }

    private static Matrix RandVector(int columnDimension) {
        double[][] x = new double[0][columnDimension];
        Random random = new Random(10);
        for (int i = 0; i < columnDimension; i++) {
            double a = random.nextInt(20000) / 10000;
            x[0][i] = a;
        }
        Matrix matrix = new Matrix(x);
        return matrix;
    }

    public static double eval1;

    public static Matrix Findevec(Matrix A, double eval, double tol) {
        double lambdaOld = -1000;
        Matrix x = RandVector(A.getRowDimension());
        double lambda = x.norm1();
        ;
        Matrix xnew;
        while (Math.abs((lambda - lambdaOld) / lambda) > tol) {
            lambdaOld = lambda;
            xnew = A.times(x);
            x = xnew;
            lambda = x.norm1();
        }
        eval1 = lambda;
        return x;
    }

    public static Matrix Deflation(Matrix A, Matrix X, double eval, double tol) {
        Matrix Z = A.transpose();
        Matrix y = Findevec(Z, eval1, tol);
        Matrix ytrans = y.transpose();
        Matrix norm = ytrans.times(X);
        y = y.times(eval / norm.getArray()[0][0]);
        Z = X.times(y.transpose());
        A = A.minus(Z);
        return A;
    }

    //加载历史数据，并且添加要搜寻的数据
    public static ArrayList<ScDataPoint> HistoryData() {
        ArrayList<ScDataPoint> dataSet = new ArrayList<ScDataPoint>();
        ScDataPoint b = new ScDataPoint("9,10,11,12,8,4,", "b");
        ScDataPoint b1 = new ScDataPoint("9,10,11,7,8,4,", "b1");
        ScDataPoint b2 = new ScDataPoint("9,10,11,12,8,7,3,4,", "b2");
        ScDataPoint b3 = new ScDataPoint("9,10,11,12,8,7,3,1,", "b3");
        ScDataPoint b4 = new ScDataPoint("9,10,11,12,8,7,3,2,", "b4");
        ScDataPoint b5 = new ScDataPoint("9,10,11,12,8,7,3,3,", "b5");
        ScDataPoint b6 = new ScDataPoint("9,10,11,12,8,7,3,7,", "b6");
        ScDataPoint c = new ScDataPoint("9,5,1,2,3,4,", "c");
        ScDataPoint c1 = new ScDataPoint("9,5,6,2,3,4,", "c1");
        ScDataPoint c2 = new ScDataPoint("9,5,1,2,6,7,3,4,", "c2");
        ScDataPoint c3 = new ScDataPoint("9,5,6,7,3,4,", "c3");
        ScDataPoint D3 = new ScDataPoint("9,1,2,88,3,466,", "d3");
        ScDataPoint D2 = new ScDataPoint("9,1,2,7,3,45,", "d2");
        ScDataPoint D4 = new ScDataPoint("9,1,2,7,3,4,", "d4");
        ScDataPoint D5 = new ScDataPoint("9,1,2,7,3,4,", "d5");
        ScDataPoint D6 = new ScDataPoint("9,1,2,7,34,4,", "d6");
        dataSet.add(b);
        dataSet.add(b1);
        dataSet.add(b2);
        dataSet.add(b3);
        dataSet.add(b4);
        dataSet.add(b5);
        dataSet.add(b6);
        dataSet.add(c);
        dataSet.add(c1);
        dataSet.add(c2);
        dataSet.add(c3);
        dataSet.add(D3);
        dataSet.add(D2);
        dataSet.add(D4);
        dataSet.add(D5);
        dataSet.add(D6);
        return dataSet;
    }

    /**
     * 幂迭代求特征值
     *
     * @param A
     * @return
     */
    private void PowerIteration(double[][] A) {
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
        System.out.println("矩阵的特征值：");
        for (int i = 0; i < N; i++) {
            System.out.println("（" + u[i] + "）");
        }

    }

    /**
     * @param v
     * @return
     */
    public double slove(double[] v) {
        //slove v[N]
        int N = v.length;
        double max = 0;
        for (int i = 0; i < N - 1; i++) {
            max = v[i] > v[i + 1] ? v[i] : v[i + 1];
        }
        return max;
    }

    public static void read(){

        String filePath = "vec_matrix.csv";

        try {
            // 创建CSV读对象
            CsvReader csvReader = new CsvReader(filePath);

            // 读表头
            csvReader.readHeaders();
            while (csvReader.readRecord()){
                // 读一整行
                System.out.println(csvReader.getRawRecord());
                // 读这行的某一列
                System.out.println(csvReader.get("Link"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void write(){

        String filePath = "Lmatrix.csv";
        createFile(filePath);
        try {
            // 创建CSV写对象
            CsvWriter csvWriter = new CsvWriter(filePath,',', Charset.forName("GBK"));
            //CsvWriter csvWriter = new CsvWriter(filePath);

            // 写表头{2, 1, 0.0, 0.0}, {0, 0.0, 1.0, 0.0},{1, 0, 0,1}
            String[] headers = {"1","2","0","1"};
            String[] header1s = {"2","1", "0.0", "0.0"};
            String[] header2s = {"0", "0.0", "1.0", "0.0"};
            String[] header3s = {"1", "0", "0","1"};
//            String[] content = {"12365","张山","34"};
            csvWriter.writeRecord(headers);
            csvWriter.writeRecord(header1s);
            csvWriter.writeRecord(header2s);
            csvWriter.writeRecord(header3s);
            csvWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static boolean createFile(String destFileName) {
        Boolean bool = false;
        String filenameTemp = destFileName;//
        File file = new File(filenameTemp);
        try {
            if (!file.exists())
            {
                file.createNewFile();
                bool = true;
                System.out.println("成功创建文件");
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return bool;
    }

}
