package TestCode;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import RecommendPath.K_means;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class testMatrix {
    public static void main(String[] args)
    {
        double[][] array = {{1.,2.,3,1,1.,2.,3,1,8},
                            {4.,5.,6.,1,1.,2.,3,1,8},
                            {7.,8.,10.,1,1.,2.,3,1,8},
                            {11,12,13,1,1.,2.,3,1,8},
                            {11,12,13,1,7.,8.,10.,1,4},
                            {11,12,13,1,7.,8.,10.,1,5},
                            {11,12,13,1,7.,8.,10.,1,7},
                            {11,12,13,1,7.,8.,10.,1,9},
                            {11,12,13,1,11,12,13,1,8}

    };
        Matrix A = new Matrix(array);
        EigenvalueDecomposition eig= A.eig();
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
        K_means k_means = new K_means(3);
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

}
