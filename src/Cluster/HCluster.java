package Cluster;

import Util.EditDistance;

import java.util.*;

/**
 * 层次聚类
 * Created by Administrator on 2017/6/7.
 */
public class HCluster {
    // 聚类的主方法
    public static List<HcCluster> startCluster(ArrayList<HcDataPoint> dp, int k, double err) {

        // 声明cluster类，存放类名和类簇中含有的样本
        List<HcCluster> finalHcClusters = new ArrayList<HcCluster>();
        // 初始化类簇，开始时认为每一个样本都是一个类簇并将初始化类簇赋值给最终类簇
        List<HcCluster> originalHcClusters = initialCluster(dp);
        finalHcClusters = originalHcClusters;
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
            for (int i = 0; i < finalHcClusters.size() - 1; i++) {
                for (int j = i + 1; j < finalHcClusters.size(); j++) {
                    // 得到任意的两个类簇
                    HcCluster hcClusterA = finalHcClusters.get(i);
                    HcCluster hcClusterB = finalHcClusters.get(j);
                    // 得到这两个类簇中的样本
                    List<HcDataPoint> hcDataPointsA = hcClusterA.getHcDataPoints();
                    List<HcDataPoint> hcDataPointsB = hcClusterB.getHcDataPoints();
                    /*
                     * 定义临时变量tempDis存储两个类簇的大小，这里采用的计算两个类簇的距离的方法是
                     * (平均距离)得到两个类簇中所有的样本的距离的和除以两个类簇中的样本数量的积，其中两个样本之间的距离用的是编辑距离。
                     * 注意：这个地方的类簇之间的距离可以 换成其他的计算方法
                     */
                    double tempDis = 1000000;
                    for (int m = 0; m < hcDataPointsA.size(); m++) {
                        for (int n = 0; n < hcDataPointsB.size(); n++) {

                            double maxDis = EditDistance.similarity(hcDataPointsA.get(m).getData(), hcDataPointsB.get(n).getData());
                            if (maxDis < tempDis)
                            {
                                tempDis = maxDis;
                            }
//                            tempDis = tempDis + EditDistance.similarity(hcDataPointsA.get(m).getData(), hcDataPointsB.get(n).getData());
                        }
                    }
//                    tempDis = tempDis / (hcDataPointsA.size() * hcDataPointsB.size());

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
            if (finalHcClusters.size() <= k) {
                flag = false;
            } else {
                finalHcClusters = mergeCluster(finalHcClusters, mergeIndexA, mergeIndexB);
                System.out.println("完成合并.最大距离为:" + max);
            }
//            if (max <= err) {
//                flag = false;
//            } else {
//                finalHcClusters = mergeCluster(finalHcClusters, mergeIndexA, mergeIndexB);
//                System.out.println("完成合并.最大距离为:"+max);
//            }
            it++;
        }
        return finalHcClusters;
    }

    private static List<HcCluster> mergeCluster(List<HcCluster> finalHcClusters, int mergeIndexA, int mergeIndexB) {
        if (mergeIndexA != mergeIndexB) {
            // 将cluster[mergeIndexB]中的DataPoint加入到 hcCluster[mergeIndexA]
            HcCluster hcClusterA = finalHcClusters.get(mergeIndexA);
            HcCluster hcClusterB = finalHcClusters.get(mergeIndexB);

            List<HcDataPoint> dpA = hcClusterA.getHcDataPoints();
            List<HcDataPoint> dpB = hcClusterB.getHcDataPoints();

            for (HcDataPoint dp : dpB) {
                dp.setHcCluster(hcClusterA);
                dpA.add(dp);
            }
            hcClusterA.setHcDataPoints(dpA);
            finalHcClusters.remove(mergeIndexB);
        }
        return finalHcClusters;
    }

    // 初始化类簇
    private static List<HcCluster> initialCluster(ArrayList<HcDataPoint> dpoints) {
        // 声明存放初始化类簇的链表
        List<HcCluster> originalHcClusters = new ArrayList<HcCluster>();

        for (int i = 0; i < dpoints.size(); i++) {
            // 得到每一个样本点
            HcDataPoint tempHcDataPoint = dpoints.get(i);
            // 声明一个临时的用于存放样本点的链表
            List<HcDataPoint> tempHcDataPoints = new ArrayList<HcDataPoint>();
            // 链表中加入刚才得到的样本点
            tempHcDataPoints.add(tempHcDataPoint);
            // 声明一个类簇，并且将给类簇设定名字、增加样本点
            HcCluster tempHcCluster = new HcCluster();
            tempHcCluster.setClusterName(String.valueOf(i));
            tempHcCluster.setHcDataPoints(tempHcDataPoints);
            // 将样本点的类簇设置为tempCluster
            tempHcDataPoint.setHcCluster(tempHcCluster);
            // 将新的类簇加入到初始化类簇链表中
            originalHcClusters.add(tempHcCluster);
        }

        return originalHcClusters;
    }

    //    //加载历史数据，并且添加要搜寻的数据
//    private static ArrayList<HcDataPoint> HistoryData(List<HcDataPoint> hcDataPoints) {
//        ArrayList<HcDataPoint> dataSet = new ArrayList<HcDataPoint>();
//        Customer customer = GenData.getCustomerData();
//        for (HashMap.Entry e : customer.getHistory().entrySet()) {
//            List<Product> products = (List<Product>) e.getValue();
//            for (Product product : products) {
//                System.out.print(product.getId() + ",");
//            }
//            System.out.println();
//            dataSet.add(new HcDataPoint((String) e.getKey()));
//        }
////        HcDataPoint b = new HcDataPoint("9,10,11,12,8,4,","b");
////        HcDataPoint b1 = new HcDataPoint("9,10,11,7,8,4,","b1");
////        HcDataPoint b2 = new HcDataPoint("9,10,11,12,8,,7,3,4,","b2");
////        HcDataPoint c = new HcDataPoint("9,5,1,2,3,4,","c");
////        HcDataPoint c1 = new HcDataPoint("9,5,6,2,3,4,","c1");
////        HcDataPoint c2 = new HcDataPoint("9,5,1,2,6,7,3,4,","c2");
////        HcDataPoint c3 = new HcDataPoint("9,5,6,7,3,4,","c3");
////        dataSet.add(b);
////        dataSet.add(b1);
////        dataSet.add(b2);
////        dataSet.add(c);
////        dataSet.add(c1);
////        dataSet.add(c2);
////        dataSet.add(c3);
//        for (HcDataPoint dp:hcDataPoints) {
//            dataSet.add(dp);
//        }
////        demo.HcDataPoint a = new demo.HcDataPoint("9,10,6,7,3,4,","a");
////        demo.HcDataPoint a1 = new demo.HcDataPoint("9,10,6,7,8,4,","a1");
////        demo.HcDataPoint a2 = new demo.HcDataPoint("9,5,6,7,3,4,","a2");
////        demo.HcDataPoint a3 = new demo.HcDataPoint("9,10,6,2,3,4,","a3");
////        dataSet.add(a);
////        dataSet.add(a1);
////        dataSet.add(a2);
////        dataSet.add(a3);
//        return dataSet;
//    }
//
//    //开始分类。并给出目标target的聚类。
//    private static List<HcDataPoint> SearchCluster(List<HcDataPoint> targets) {
//        ArrayList<HcDataPoint> dataset = HistoryData(targets);
//        //设置原始数据集
//        List<HcCluster> finalClusters = startCluster(dataset, 2, 0.5);
//        //查看结果
//        for (int m = 0; m < finalClusters.size(); m++) {
//            System.out.println(finalClusters.get(m).getClusterName());
//            for (HcDataPoint dataPoint : finalClusters.get(m).getHcDataPoints()) {
//                System.out.println(dataPoint.getDataPointName() + ":" + dataPoint.getData());
//            }
//            System.out.println();
//        }
//
//        return targets;
//    }
}



