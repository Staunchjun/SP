package TestCode;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import RecommendPath.*;
import weka.clusterers.SpectralClusterer;
import weka.core.*;
import weka.core.converters.ConverterUtils;
import weka.core.neighboursearch.PerformanceStats;
import weka.gui.beans.DataSource;

import java.util.*;

public class testMatrix {
    public static void main(String[] args) throws Exception {
//        SCluster sCluster = new SCluster(HistoryData(), 2);
       MySpectrak mySpectrak = new MySpectrak();
        ArrayList<ScDataPoint> points = HistoryData();
       mySpectrak.buildClusterer(points);
       System.out.println("簇类数目 "+mySpectrak.numberOfClusters());
       System.out.println("聚类信息 "+mySpectrak.globalInfo());
       System.out.println("聚类信息如下");
        for (String s:mySpectrak.getOptions()) {
            System.out.println(s);
        }
        for (int i = 0; i < points.size(); i++) {
            System.out.println(points.get(i).getDataPointName()+" "+mySpectrak.cluster[i]);
        }
        String a = "9,10,11,12,8,4,";
        String b = "9,10,11,12,8,4,";
        System.out.println(EditDistance.similarity(a,b));

    System.out.println(1.0/56);

    }
        //加载历史数据，并且添加要搜寻的数据
    public static ArrayList<ScDataPoint> HistoryData() {
        ArrayList<ScDataPoint> dataSet = new ArrayList<ScDataPoint>();
        ScDataPoint b = new ScDataPoint("9,10,11,12,8,4,","b");
        ScDataPoint b1 = new ScDataPoint("9,10,11,7,8,4,","b1");
        ScDataPoint b2 = new ScDataPoint("9,10,11,12,8,7,3,4,","b2");
        ScDataPoint b3 = new ScDataPoint("9,10,11,12,8,7,3,1,","b3");
        ScDataPoint b4 = new ScDataPoint("9,10,11,12,8,7,3,2,","b4");
        ScDataPoint b5 = new ScDataPoint("9,10,11,12,8,7,3,3,","b5");
        ScDataPoint b6 = new ScDataPoint("9,10,11,12,8,7,3,7,","b6");
        ScDataPoint c = new ScDataPoint("9,5,1,2,3,4,","c");
        ScDataPoint c1 = new ScDataPoint("9,5,6,2,3,4,","c1");
        ScDataPoint c2 = new ScDataPoint("9,5,1,2,6,7,3,4,","c2");
        ScDataPoint c3 = new ScDataPoint("9,5,6,7,3,4,","c3");
        ScDataPoint D3 = new ScDataPoint("9,1,2,88,3,466,","d3");
        ScDataPoint D2 = new ScDataPoint("9,1,2,7,3,45,","d2");
        ScDataPoint D4 = new ScDataPoint("9,1,2,7,3,4,","d4");
        ScDataPoint D5 = new ScDataPoint("9,1,2,7,3,4,","d5");
        ScDataPoint D6 = new ScDataPoint("9,1,2,7,34,4,","d6");
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
