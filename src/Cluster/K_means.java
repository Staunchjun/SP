package Cluster;

import java.util.ArrayList;
import java.util.Random;

/**
 * K均值聚类算法
 */
public class K_means {
    private int k;// 分成多少簇
    private int m;// 迭代次数
    private int dataSetLength;// 数据集元素个数，即数据集的长度
    private ArrayList<ScDataPoint> dataSet;// 数据集链表
    private ArrayList<double[]> center;// 中心链表
    private ArrayList<ScCluster> clusters; // 簇
    private ArrayList<Double> jc;// 误差平方和，k越接近dataSetLength，误差越小
    private Random random;


    /**
     * 获取结果分组
     *
     * @return 结果集
     */

    public ArrayList<ScCluster> getClusters() {
        return clusters;
    }

    /**
     * 构造函数，传入需要分成的簇数量
     *
     * @param dataSet 传入点集合
     * @param k       簇数量,若k<=0时，设置为1，若k大于数据源的长度时，置为数据源的长度
     */
    public K_means(int k, ArrayList<ScDataPoint> dataSet) {
        if (k <= 0) {
            k = 1;
        }
        this.k = k;
        this.dataSet = dataSet;
    }

    /**
     * 初始化
     */
    private void init() {
        m = 0;
        random = new Random();
        dataSetLength = dataSet.size();
        if (k > dataSetLength) {
            k = dataSetLength;
        }
        center = initCenters();
        clusters = initCluster();
        jc = new ArrayList<Double>();
    }


    /**
     * 初始化中心数据链表，分成多少簇就有多少个中心点
     *
     * @return 中心点集
     */
    private ArrayList<double[]> initCenters() {
        ArrayList<double[]> center = new ArrayList<double[]>();
        int[] randoms = new int[k];
        boolean flag;
        int temp = random.nextInt(dataSetLength);
        randoms[0] = temp;
        for (int i = 1; i < k; i++) {
            flag = true;
            while (flag) {
                temp = random.nextInt(dataSetLength);
                int j = 0;

                while (j < i) {
                    if (temp == randoms[j]) {
                        break;
                    }
                    j++;
                }
                if (j == i) {
                    flag = false;
                }
            }
            randoms[i] = temp;
        }

        for (int i = 0; i < k; i++) {
            center.add(dataSet.get(randoms[i]).getTempData());// 生成初始化中心链表
        }
        return center;
    }

    /**
     * 初始化簇集合
     *
     * @return 一个分为k簇的空数据的簇集合
     */
    private ArrayList<ScCluster> initCluster() {
        ArrayList<ScCluster> clusters = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            ScCluster cluster = new ScCluster();
            cluster.setClusterName("簇类ID" + k);
            clusters.add(cluster);
        }
        return clusters;
    }

    /**
     * 计算两个点之间的距离
     *
     * @param element 点1
     * @param center  点2
     * @return 距离
     */
    private double distance(double[] element, double[] center) {
        double distance = 0.0f;
        int len = element.length;
        double sum = 0;
        for (int i = 0; i < len; i++) {
            sum = element[i] - center[i];
            sum = sum * sum;
        }
        distance = (double) Math.sqrt(sum);
        return distance;
    }

    /**
     * 获取距离集合中最小距离的位置
     *
     * @param distance 距离数组
     * @return 最小距离在距离数组中的位置
     */
    private int minDistance(double[] distance) {
        double minDistance = distance[0];
        int minLocation = 0;
        for (int i = 1; i < distance.length; i++) {
            if (distance[i] < minDistance) {
                minDistance = distance[i];
                minLocation = i;
            } else if (distance[i] == minDistance) // 如果相等，随机返回一个位置
            {
                if (random.nextInt(10) < 5) {
                    minLocation = i;
                }
            }
        }

        return minLocation;
    }

    /**
     * 核心，将当前元素放到最小距离中心相关的簇中
     */
    private void clusterSet() {
        double[] distance = new double[k];
        for (int i = 0; i < dataSetLength; i++) {
            for (int j = 0; j < k; j++) {
                distance[j] = distance(dataSet.get(i).getTempData(), center.get(j));
            }
            int minLocation = minDistance(distance);
            ScCluster cluster = clusters.get(minLocation);
            cluster.getScDataPoints().add(dataSet.get(i));// 核心，将当前元素放到最小距离中心相关的簇中

        }
    }

    /**
     * 求两点误差平方的方法
     *
     * @param element 点1
     * @param center  点2
     * @return 误差平方
     */
    private double errorSquare(double[] element, double[] center) {
        int len = element.length;
        double errSquare = 0;
        for (int i = 0; i < len; i++) {
            errSquare = element[i] - center[i];
            errSquare = errSquare * errSquare;
        }

        return errSquare;
    }

    /**
     * 计算误差平方和准则函数方法
     */
    private void countRule() {
        double jcF = 0;
        for (int i = 0; i < clusters.size(); i++) {
            for (int j = 0; j < clusters.get(i).getScDataPoints().size(); j++) {
                jcF += errorSquare(clusters.get(i).getScDataPoints().get(j).getTempData(), center.get(i));

            }
        }
        jc.add(jcF);
    }

    /**
     * 设置新的簇中心方法
     */
    private void setNewCenter() {
        for (int i = 0; i < k; i++) {
            int n = clusters.get(i).getScDataPoints().size();
            int length = clusters.get(i).getScDataPoints().get(0).getTempData().length;
            if (n != 0) {
                double[] newCenter = new double[length];
                for (int j = 0; j < n; j++) {
                    for (int l = 0; l < length; l++) {
                        newCenter[l] += clusters.get(i).getScDataPoints().get(j).getTempData()[l];
                    }
                }
                // 设置一个平均值
                for (int j = 0; j < length; j++) {
                    newCenter[j] = newCenter[j] / n;
                }
                center.set(i, newCenter);
            }
        }
    }

    /**
     * Kmeans算法核心过程方法
     */
    private void kmeans() {
        init();
        // 循环分组，直到误差不变为止
        while (true) {
            clusterSet();
            countRule();
            // 误差不变了，分组完成
            if (m != 0) {
                if (jc.get(m) - jc.get(m - 1) == 0) {
                    break;
                }
            }
            setNewCenter();
            m++;
            System.out.println("第"+m+"次迭代");
            clusters.clear();
            clusters = initCluster();
        }

         System.out.println("note:the times of repeat:m="+m);//输出迭代次数
    }

    /**
     * 执行算法
     */
    public void execute() {
        long startTime = System.currentTimeMillis();
        System.out.println("kmeans begins");
        kmeans();
        long endTime = System.currentTimeMillis();
        System.out.println("kmeans running time=" + (endTime - startTime)
                + "ms");
        System.out.println("kmeans ends");
        System.out.println();
    }

}


