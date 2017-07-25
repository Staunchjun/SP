package RecommendPath;

import java.util.*;

public class K_means {
    private int k;// 分成多少簇
    private int m;// 迭代次数
    private int dataSetLength;// 数据集元素个数，即数据集的长度
    private ArrayList<int[]> dataSet;// 数据集链表
    private ArrayList<int[]> center;// 中心链表
    private ArrayList<ArrayList<int[]>> cluster; // 簇
    private ArrayList<Float> jc;// 误差平方和，k越接近dataSetLength，误差越小
    private Random random;

    public void setDataSet(ArrayList<ArrayList<Integer>> data) {
        //设置需分组的原始数据集
        dataSet = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            int[] list = new int[TestPathGenerate2.N];
            ArrayList<Integer> shopList = data.get(i);
            for (Integer id : shopList) {
                list[id] = 1;
            }
            dataSet.add(list);
        }
    }

    public ArrayList<ArrayList<int[]>> getCluster() {
        return cluster;
    }

    public K_means(int k) {
        //传入需要分成的簇数量
        if (k <= 0) {
            k = 1;
        }
        this.k = k;
    }

    private void init() {
        //初始化
        m = 0;
        random = new Random();
        if (dataSet == null || dataSet.size() == 0) {
            System.out.println("数据为空，请输入数据！！！！");
        } else {
            dataSetLength = dataSet.size();
            if (k > dataSetLength) {
                k = dataSetLength;
            }
            center = initCenters();
            cluster = initCluster();
            jc = new ArrayList<Float>();
        }
    }

    private ArrayList<int[]> initCenters() {
        //初始化中心数据链表，分成多少簇就有多少个中心点
        ArrayList<int[]> center = new ArrayList<int[]>();
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
            center.add(dataSet.get(randoms[i]));// 生成初始化中心链表
        }
        return center;
    }


    private ArrayList<ArrayList<int[]>> initCluster() {
        //初始化簇集合
        ArrayList<ArrayList<int[]>> cluster = new ArrayList<ArrayList<int[]>>();
        for (int i = 0; i < k; i++) {
            cluster.add(new ArrayList<int[]>());
        }

        return cluster;
    }


    private float distance(int[] element, int[] center) {
        //计算两个点之间的距离
        float distance = 0.0f;
        float z = 0;
        for (int i = 0; i < element.length; i++) {
            float res = element[i] - center[i];
            z += res * res;
        }
        distance = (float) Math.sqrt(z);
        return distance;
    }


    private int minDistance(float[] distance) {
        //获取距离集合中最小距离的位置
        float minDistance = distance[0];
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


    private void clusterSet() {
        //将当前元素放到最小距离中心相关的簇中
        float[] distance = new float[k];
        for (int i = 0; i < dataSetLength; i++) {
            for (int j = 0; j < k; j++) {
                distance[j] = distance(dataSet.get(i), center.get(j));
            }
            int minLocation = minDistance(distance);
            cluster.get(minLocation).add(dataSet.get(i));

        }
    }


    private float errorSquare(int[] element, int[] center) {
        //求两点误差平方的方法
        float distance = 0.0f;
        float z = 0;
        for (int i = 0; i < element.length; i++) {
            float res = element[i] - center[i];
            z += res * res;
        }
        distance = (float) Math.sqrt(z);
        return distance;
    }


    private void countRule() {
        //计算误差平方和准则函数方法
        float jcF = 0;
        for (int i = 0; i < cluster.size(); i++) {
            for (int j = 0; j < cluster.get(i).size(); j++) {
                jcF += errorSquare(cluster.get(i).get(j), center.get(i));

            }
        }
        jc.add(jcF);
    }

    private void setNewCenter() {
        //设置新的簇中心方法
        for (int i = 0; i < k; i++) {
            int n = cluster.get(i).size();
            if (n != 0) {
                int[] newCenter = new int[TestPathGenerate2.N];
                for (int j = 0; j < n; j++) {
                    for (int l = 0; l < TestPathGenerate2.N; l++) {
                        newCenter[l] += cluster.get(i).get(j)[l];
                    }
                }
                // 设置一个平均值
                for (int l = 0; l < TestPathGenerate2.N; l++) {
                    newCenter[l] = newCenter[l] / n;
                }
                center.set(i, newCenter);
            }
        }
    }

    public Map<Integer, Double> printDataArray(ArrayList<int[]> dataArray,
                               String dataArrayName) {
        System.out.println(dataArrayName);
        //打印数据
        for (int i = 0; i < dataArray.size(); i++) {
            System.out.print("print:(");
            for (int j = 0; j < dataArray.get(i).length; j++) {
                System.out.print(dataArray.get(i)[j] + ",");
            }
            System.out.print(")");
            System.out.println();
        }
        System.out.println("===================================");
        //统计 每个簇类 商品的概率
        Map<Integer, Double> pro = new HashMap<Integer, Double>();
        for (int[] list : dataArray) {
            for (int i = 0; i < list.length; i++) {
                if (list[i] == 1) {
                    if (pro.containsKey(i)) {
                        Double nu = pro.get(i);
                        nu++;
                        pro.put(i, nu);
                    } else {
                        pro.put(i, 1.0);
                    }
                }
            }
        }
        for (int i = 0; i < TestPathGenerate2.N; i++) {
            if (!pro.containsKey(i))
                pro.put(i,0.0);
        }
        for (Map.Entry<Integer, Double> e : pro.entrySet()) {
            pro.put(e.getKey(), e.getValue() / TestPathGenerate2.N);
        }
        return pro;
    }

    void kmeans() {
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
            cluster.clear();
            cluster = initCluster();
        }
    }

    public static void main(String[] args) {
        TestPathGenerate2 tes = new TestPathGenerate2();
        //初始化一个Kmean对象，将k置为3
        int num = tes.K;
//        System.out.println("输入要分为的类数：");
//        num = (new Scanner(System.in)).nextInt();
        K_means k = new K_means(num);
//        设置原始数据集
        k.setDataSet(tes.getShopLists());
        //执行算法
        k.kmeans();
        //得到聚类结果
        ArrayList<ArrayList<int[]>> cluster = k.getCluster();
        //查看结果
        Map<String,Map<Integer, Double>> clusterDistributions = new HashMap<>();
        for (int i = 0; i < cluster.size(); i++) {
            Map<Integer, Double> pro = k.printDataArray(cluster.get(i), "cluster[" + i + "]");
            clusterDistributions.put(String.valueOf(i),pro);
        }
        //对比cluster和用户簇类的相差多少，重复之前步骤
        //建立一个Error Map 配对簇类(自己的cluster后计算的概率分布和给定的用户的概率分布error)
        int errorsMapLength = clusterDistributions.size();
        double[][] errorsMap = new double[errorsMapLength][errorsMapLength];
        //i ->Path cluster j-> given customer cluster
        for (int i = 0; i < errorsMapLength; i++) {
            for (int j = 0; j < errorsMapLength; j++) {
                double[] distributionByCustomer = tes.CustomersProducts.get(j);
                int productNum = distributionByCustomer.length;
                Map<Integer, Double> clusterDistributionTemp = clusterDistributions.get(String.valueOf(i));
                double[] distributionByProduct = new double[productNum];
                for (Map.Entry<Integer, Double> e : clusterDistributionTemp.entrySet()) {
                        distributionByProduct[e.getKey()] = e.getValue();
                }
                double error = 0;
                for (int m = 0; m < productNum; m++) {
                    double pByPath = distributionByProduct[m];
                    double pByCustomer = distributionByCustomer[m];
                    error += (Math.abs(pByCustomer - pByPath));
                }
                errorsMap[i][j] = error / productNum;
            }
        }
        Map<Integer, Integer> CPpair = new HashMap<Integer, Integer>();
        List<Double> errList = new ArrayList<>();
        List<Integer> PathVisited = new ArrayList<Integer>();
        List<Integer> CustomerVisited = new ArrayList<Integer>();
        for (int i = 0; i < errorsMapLength; i++) {
            double min = Integer.MAX_VALUE;
            int CustomerClusterIndex = 0;
            int PathClusterIndex = 0;
            for (int j = 0; j < errorsMapLength; j++) {

                for (int n = 0; n < errorsMapLength; n++) {
                    if (PathVisited.contains(j) || CustomerVisited.contains(k))
                        continue;
                    if (errorsMap[j][n] < min) {
                        min = errorsMap[j][n];
                        PathClusterIndex = j;
                        CustomerClusterIndex = n;
                    }
                }
            }
            CPpair.put(PathClusterIndex, CustomerClusterIndex);
            PathVisited.add(PathClusterIndex);
            CustomerVisited.add(CustomerClusterIndex);
            errList.add(min);
        }
        System.out.println("配对结果(左边为商品聚合概率分布,右边为给定概率分布):");
        System.out.println(CPpair);
        System.out.println(errList);


        //各个簇类和平均Customer的差值
        List<Double> errListWithMean = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            int productNum = tes.MeanCustomersProducts.length;
            Map<Integer, Double> clusterDistributionTemp = clusterDistributions.get(String.valueOf(i));
            double[] distributionByPath = new double[productNum];
            for (Map.Entry<Integer, Double> e : clusterDistributionTemp.entrySet()) {
                    distributionByPath[e.getKey()] = e.getValue();
                }
            double error = 0;
            for (int o = 0; o < TestPathGenerate2.N; o++) {
                double pByPath = distributionByPath[o];
                double pByCustomer = tes.MeanCustomersProducts[o];
                error += (Math.abs(pByCustomer - pByPath));
            }
            errListWithMean.add(error / productNum);
        }

        double meanError = 0;
        for (Double d : errList) {
            meanError += d;
        }
        System.out.println("平均概率：");
        System.out.println(meanError/errList.size());

        System.out.println("每个簇类和平均顾客的比较:");
        double meanError2 = 0;
        for (Double d : errListWithMean) {
            meanError2 += d;
        }
        System.out.println("平均误差：" + meanError2/errListWithMean.size() + " ");
    }
}
