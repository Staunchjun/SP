package TestCode;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import RecommendPath.*;

import java.util.*;

public class testMatrix {
    public static void main(String[] args)
    {
       SCluster sCluster = new SCluster(HistoryData(),2);
    }
        //加载历史数据，并且添加要搜寻的数据
    public static ArrayList<ScDataPoint> HistoryData() {
        ArrayList<ScDataPoint> dataSet = new ArrayList<ScDataPoint>();
        ScDataPoint b = new ScDataPoint("9,10,11,12,8,4,","b");
        ScDataPoint b1 = new ScDataPoint("9,10,11,7,8,4,","b1");
        ScDataPoint b2 = new ScDataPoint("9,10,11,12,8,,7,3,4,","b2");
        ScDataPoint c = new ScDataPoint("9,5,1,2,3,4,","c");
        ScDataPoint c1 = new ScDataPoint("9,5,6,2,3,4,","c1");
        ScDataPoint c2 = new ScDataPoint("9,5,1,2,6,7,3,4,","c2");
        ScDataPoint c3 = new ScDataPoint("9,5,6,7,3,4,","c3");
        dataSet.add(b);
        dataSet.add(b1);
        dataSet.add(b2);
        dataSet.add(c);
        dataSet.add(c1);
        dataSet.add(c2);
        dataSet.add(c3);
        return dataSet;
    }


}
