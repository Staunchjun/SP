package RecommendPath;

import DataStructure.Graph;
import DataStructure.Node;
import DataStructure.Path;
import MainCode.Guider;
import TestCode.InitMap;

import java.util.*;

/**
 * Created by Administrator on 2017/6/7.
 */
public class HCluster {
    // 聚类的主方法
    public static List<Cluster> startCluster(ArrayList<DataPoint> dp, int k, double err) {

        // 声明cluster类，存放类名和类簇中含有的样本
        List<Cluster> finalClusters = new ArrayList<Cluster>();
        // 初始化类簇，开始时认为每一个样本都是一个类簇并将初始化类簇赋值给最终类簇
        List<Cluster> originalClusters = initialCluster(dp);
        finalClusters = originalClusters;
        // flag为判断标志
        boolean flag = true;
        int it = 1;
        while (flag) {
            System.out.println("第" + it + "次迭代");
            // 临时表量，存放类簇间余弦相似度的最大值
            double max = -1;
            // mergeIndexA和mergeIndexB表示每一次迭代聚类最小的两个类簇，也就是每一次迭代要合并的两个类簇
            int mergeIndexA = 0;
            int mergeIndexB = 0;
            //迭代开始，分别去计算每个类簇之间的距离，将距离小的类簇合并
            for (int i = 0; i < finalClusters.size() - 1; i++) {
                for (int j = i + 1; j < finalClusters.size(); j++) {
                    // 得到任意的两个类簇
                    Cluster clusterA = finalClusters.get(i);
                    Cluster clusterB = finalClusters.get(j);
                    // 得到这两个类簇中的样本
                    List<DataPoint> dataPointsA = clusterA.getDataPoints();
                    List<DataPoint> dataPointsB = clusterB.getDataPoints();
                    /*
                     * 定义临时变量tempDis存储两个类簇的大小，这里采用的计算两个类簇的距离的方法是
                     * (平均距离)得到两个类簇中所有的样本的距离的和除以两个类簇中的样本数量的积，其中两个样本之间的距离用的是编辑距离。
                     * 注意：这个地方的类簇之间的距离可以 换成其他的计算方法
                     */
                    double tempDis = 0;
                    for (int m = 0; m < dataPointsA.size(); m++) {
                        for (int n = 0; n < dataPointsB.size(); n++) {
                            tempDis = tempDis + EditDistance.similarity(dataPointsA.get(m).getData(), dataPointsB.get(n).getData());
                        }
                    }
                    tempDis = tempDis / (dataPointsA.size() * dataPointsB.size());

                    if (tempDis >= max) {
                        max = tempDis;
                        mergeIndexA = i;
                        mergeIndexB = j;
                    }
                }
            }
            /*
             * 若是余弦相似度的最大值都小于给定的阈值， 那说明当前的类簇没有再进一步合并的必要了，
             * 当前的聚类可以作为结果了，否则的话合并余弦相似度值最大的两个类簇，继续进行迭代 注意：这个地方你可以设定别的聚类迭代的结束条件
             */
            if (finalClusters.size() <= k) {
                flag = false;
            } else {
                finalClusters = mergeCluster(finalClusters, mergeIndexA, mergeIndexB);
                System.out.println("完成合并.最大距离为:" + max);
            }
//            if (max <= err) {
//                flag = false;
//            } else {
//                finalClusters = mergeCluster(finalClusters, mergeIndexA, mergeIndexB);
//                System.out.println("完成合并.最大距离为:"+max);
//            }
            it++;
        }
        return finalClusters;
    }

    private static List<Cluster> mergeCluster(List<Cluster> finalClusters, int mergeIndexA, int mergeIndexB) {
        if (mergeIndexA != mergeIndexB) {
            // 将cluster[mergeIndexB]中的DataPoint加入到 cluster[mergeIndexA]
            Cluster clusterA = finalClusters.get(mergeIndexA);
            Cluster clusterB = finalClusters.get(mergeIndexB);

            List<DataPoint> dpA = clusterA.getDataPoints();
            List<DataPoint> dpB = clusterB.getDataPoints();

            for (DataPoint dp : dpB) {
                dp.setCluster(clusterA);
                dpA.add(dp);
            }
            clusterA.setDataPoints(dpA);
            finalClusters.remove(mergeIndexB);
        }
        return finalClusters;
    }

    // 初始化类簇
    private static List<Cluster> initialCluster(ArrayList<DataPoint> dpoints) {
        // 声明存放初始化类簇的链表
        List<Cluster> originalClusters = new ArrayList<Cluster>();

        for (int i = 0; i < dpoints.size(); i++) {
            // 得到每一个样本点
            DataPoint tempDataPoint = dpoints.get(i);
            // 声明一个临时的用于存放样本点的链表
            List<DataPoint> tempDataPoints = new ArrayList<DataPoint>();
            // 链表中加入刚才得到的样本点
            tempDataPoints.add(tempDataPoint);
            // 声明一个类簇，并且将给类簇设定名字、增加样本点
            Cluster tempCluster = new Cluster();
            tempCluster.setClusterName("demo.Cluster " + String.valueOf(i));
            tempCluster.setDataPoints(tempDataPoints);
            // 将样本点的类簇设置为tempCluster
            tempDataPoint.setCluster(tempCluster);
            // 将新的类簇加入到初始化类簇链表中
            originalClusters.add(tempCluster);
        }

        return originalClusters;
    }

    //    //加载历史数据，并且添加要搜寻的数据
//    private static ArrayList<DataPoint> HistoryData(List<DataPoint> dataPoints) {
//        ArrayList<DataPoint> dataSet = new ArrayList<DataPoint>();
//        Customer customer = GenData.getCustomerData();
//        for (HashMap.Entry e : customer.getHistory().entrySet()) {
//            List<Product> products = (List<Product>) e.getValue();
//            for (Product product : products) {
//                System.out.print(product.getId() + ",");
//            }
//            System.out.println();
//            dataSet.add(new DataPoint((String) e.getKey()));
//        }
////        DataPoint b = new DataPoint("9,10,11,12,8,4,","b");
////        DataPoint b1 = new DataPoint("9,10,11,7,8,4,","b1");
////        DataPoint b2 = new DataPoint("9,10,11,12,8,,7,3,4,","b2");
////        DataPoint c = new DataPoint("9,5,1,2,3,4,","c");
////        DataPoint c1 = new DataPoint("9,5,6,2,3,4,","c1");
////        DataPoint c2 = new DataPoint("9,5,1,2,6,7,3,4,","c2");
////        DataPoint c3 = new DataPoint("9,5,6,7,3,4,","c3");
////        dataSet.add(b);
////        dataSet.add(b1);
////        dataSet.add(b2);
////        dataSet.add(c);
////        dataSet.add(c1);
////        dataSet.add(c2);
////        dataSet.add(c3);
//        for (DataPoint dp:dataPoints) {
//            dataSet.add(dp);
//        }
////        demo.DataPoint a = new demo.DataPoint("9,10,6,7,3,4,","a");
////        demo.DataPoint a1 = new demo.DataPoint("9,10,6,7,8,4,","a1");
////        demo.DataPoint a2 = new demo.DataPoint("9,5,6,7,3,4,","a2");
////        demo.DataPoint a3 = new demo.DataPoint("9,10,6,2,3,4,","a3");
////        dataSet.add(a);
////        dataSet.add(a1);
////        dataSet.add(a2);
////        dataSet.add(a3);
//        return dataSet;
//    }
//
//    //开始分类。并给出目标target的聚类。
//    private static List<DataPoint> SearchCluster(List<DataPoint> targets) {
//        ArrayList<DataPoint> dataset = HistoryData(targets);
//        //设置原始数据集
//        List<Cluster> finalClusters = startCluster(dataset, 2, 0.5);
//        //查看结果
//        for (int m = 0; m < finalClusters.size(); m++) {
//            System.out.println(finalClusters.get(m).getClusterName());
//            for (DataPoint dataPoint : finalClusters.get(m).getDataPoints()) {
//                System.out.println(dataPoint.getDataPointName() + ":" + dataPoint.getData());
//            }
//            System.out.println();
//        }
//
//        return targets;
//    }
}
class DataPoint {
    private Cluster cluster;
    private String data;
    private String dataPointName;

    public DataPoint(String data) {
        this.data = data;
    }

    public DataPoint(String data, String dataPointName) {
        this.data = data;
        this.dataPointName = dataPointName;
    }

    public DataPoint() {

    }

    public String getDataPointName() {
        return dataPointName;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public String getData() {
        return data;
    }
}

class Cluster {
    private List<DataPoint> dataPoints = new ArrayList<DataPoint>(); // 类簇中的样本点
    private String clusterName;

    public List<DataPoint> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(List<DataPoint> dataPoints) {
        this.dataPoints = dataPoints;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }
}