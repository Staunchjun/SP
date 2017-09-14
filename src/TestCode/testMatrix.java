package TestCode;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

import java.util.Map;
import java.util.TreeMap;

public class testMatrix {
    public static void main(String[] args)
    {
        double[][] array = {{1.,2.,3},{4.,5.,6.},{7.,8.,10.}};
        Matrix A = new Matrix(array);
        EigenvalueDecomposition eig= A.eig();
        double[] eigs = eig.getRealEigenvalues();
        double[][] eig_vecs = eig.getV().transpose().getArray();
        //取前K大，使用TreeMap，按照key排序，从小到大取K个出来
        TreeMap<Double, double[]> treeMap = new TreeMap<>();
        for (int i = 0; i < eigs.length; i++) {
            treeMap.put(eigs[i],eig_vecs[i]);
        }
    }
}
