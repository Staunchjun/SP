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
    private static List<Cluster> startCluster(ArrayList<DataPoint> dp, int k, double err) {

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
    public static TestPathGenerate testPathGenerate = new TestPathGenerate();
    public static int J;

    public static void main(String[] args) {
        Map<String, int[]> history = testPathGenerate.TestPathGenerating();
        Map<int[], double[]> TNewCustomer = testPathGenerate.TestTNewCustomer();

        for (Map.Entry<int[], double[]> newCustomer : TNewCustomer.entrySet()) {
            //Generating paths;
            // 初始化graph
            Graph graph = InitMap.returnGraph();
            ArrayList<Node> nodes = new ArrayList<Node>();
            int alyeadyBuy = 0;
            List<Integer> restBuy = new ArrayList<Integer>();
            for (int i : newCustomer.getKey()) {
                if (alyeadyBuy <= J) {
                    nodes.add(graph.getNode(testPathGenerate.pLocation.get(i)));
                    alyeadyBuy++;
                } else {
                    restBuy.add(i);
                }
            }


            Stack<Node> finalPath = new Stack<Node>();
            Map<Integer, Double> pDis = new HashMap<Integer, Double>();
            //对所有的product根据离入口距离进行排序,从小到大
            Node lastNode = graph.getNode(0);
            List<Map.Entry<Integer, Double>> pDisSorted = testPathGenerate.CreateSort(pDis, nodes, graph, lastNode);
            System.out.println();
            System.out.println("根据离入口距离进行点排序:");
            for (Map.Entry entry : pDisSorted) {
                System.out.println(entry.getKey() + ":" + entry.getValue());
            }
            System.out.println();
            while (pDisSorted.size() != 0 && pDis != null) {
                Node des = graph.getNode(pDisSorted.get(0).getKey());
                List<Path> paths = Guider.getSingleDestPath(graph, lastNode, des, null, 0.1);
                if (paths.isEmpty()) {
                    //出现了自己去自己,eg:  4->4
                    pDis.remove(des.N);
                    nodes.remove(graph.getNode(des.N));
                    pDisSorted = testPathGenerate.CreateSort(pDis, nodes, graph, des);
                    System.out.println();
                    System.out.println("重新排序。去除重复点。");
                    for (Map.Entry entry : pDisSorted) {
                        System.out.println("起始点" + des.N + "->" + entry.getKey() + "的距离:" + entry.getValue());
                    }
                    System.out.println();
                    continue;
                }
                Path bestPath = paths.get(0);
                Stack<Node> tempPath = new Stack<Node>();
                for (Node node : bestPath.getNodes()) {
                    tempPath.push(node);
                    if (pDis.containsKey(node.N)) {
                        pDis.remove(node.N);
                        nodes.remove(graph.getNode(des.N));
                        pDisSorted = testPathGenerate.CreateSort(pDis, nodes, graph, des);

                        System.out.println();
                        System.out.println("重新排序，去除路径中已包含点");
                        for (Map.Entry entry : pDisSorted) {
                            System.out.println("起始点" + des.N + "->" + entry.getKey() + "的距离:" + entry.getValue());
                        }
                        if (pDisSorted.isEmpty()) {
                            System.out.println("pDisSorted 清空");
                        }
                        System.out.println();
                    }
                }
                while (!tempPath.isEmpty()) {
                    if (!finalPath.isEmpty()) {
                        Node topNode = finalPath.peek();
                        if (topNode == tempPath.peek()) {
                            finalPath.pop();
                        }
                    }
                    finalPath.push(tempPath.pop());
                }
                if (pDisSorted.isEmpty()) {
                    break;
                }
                lastNode = graph.getNode(finalPath.peek().N);
            }
            StringBuffer stringBuffer = new StringBuffer();
            Node node_j_1 = null;
            for (Node node : finalPath) {
                System.out.print(node.N + "->");
                stringBuffer.append(node.N);
                stringBuffer.append(",");
                node_j_1 = node;
            }

            DataPoint t = new DataPoint(stringBuffer.toString(), "t11");

            //加载历史数据，并且添加要搜寻的数据
            ArrayList<DataPoint> dataSet = new ArrayList<DataPoint>();
            int count = 0;
            for (HashMap.Entry<String, int[]> e : history.entrySet()) {
                dataSet.add(new DataPoint(e.getKey(), "b" + count));
                count++;
            }

            dataSet.add(t);

            //设置原始数据集
            List<Cluster> finalClusters = startCluster(dataSet, 7, 0.5);
            //查看结果
            for (int m = 0; m < finalClusters.size(); m++) {
                System.out.println(finalClusters.get(m).getClusterName());
                for (DataPoint dataPoint : finalClusters.get(m).getDataPoints()) {
                    System.out.println(dataPoint.getDataPointName() + ":" + dataPoint.getData());
                }
                System.out.println();
            }
            System.out.println("list node:" + t.getDataPointName() + " cluster:" + t.getCluster().getClusterName());
            Cluster cluster = t.getCluster();
            List<DataPoint> dps = cluster.getDataPoints();
            //统计每一条路径中所有已购买商品总数
            Map<Integer, Double> productNum = new HashMap<>();
            for (DataPoint dataPoint : dps) {
                if (t.getData().equals(dataPoint.getData())) {
                    continue;
                }
                int[] products = history.get(dataPoint.getData());
                for (int product : products) {
                    if (!productNum.containsKey(product)) {
                        productNum.put(product, 1.0);
                    } else {
                        double num = productNum.get(product);
                        productNum.put(product, ++num);
                    }
                }
            }
            //计算一个簇类中商品出现频率
            for (HashMap.Entry<Integer, Double> e : productNum.entrySet()) {
                double a = (double) e.getValue();
                productNum.put(e.getKey(), a / productNum.size());
            }

            if (productNum.size() == 0||productNum.isEmpty())
            {
                break;
            }
            for (HashMap.Entry<Integer, Double> e : productNum.entrySet()) {
                System.out.print("product id:" + e.getKey() + " probability:" + String.format("%4f", e.getValue()) + "   ");
            }
            System.out.println();
            //对所有的product根据probability进行排序,从大到小
            List<Map.Entry<Integer, Double>> list = new ArrayList<Map.Entry<Integer, Double>>(productNum.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
                @Override
                public int compare(Map.Entry<Integer, Double> o1,
                                   Map.Entry<Integer, Double> o2) {
                    return -o1.getValue().compareTo(o2.getValue());
                }
            });
            //进行排序 取概率最大的 并且进行路径推荐。最后看看推荐的路径中包含多少还没有购买的商品。
            //这里取的不应该是最大值，而是根据各个点，带有的总概率算预测接下来的路径推荐，
            // 看回最初的路径推荐算法
            Graph graphWP =InitMap.returnGraphWP(list,testPathGenerate);
            List<Path> paths = Guider.getSingleDestPath(graphWP,node_j_1, graphWP.getNode(testPathGenerate.pLocation.get(restBuy.get(0))), null, 0.1);

            System.out.println("要购买但是还没买的：");
            for (int i:restBuy) {
            System.out.print(i+" ");
            }
            System.out.println();
            Map<Integer, Set<Integer>> shelf = testPathGenerate.shelf;
            for (Path p:paths) {
                for (Node node:p.getNodes()) {
                    System.out.print(node.N+"<-");
                }
                System.out.println();
                for (Node node:p.getNodes()) {
                    System.out.print(node.N+":"+node.P+"    ");
                    Set<Integer> produccts= shelf.get(node.N);
                    for (int i:restBuy) {
                        if (produccts.contains(i))
                        System.out.println("包含要买的："+i);
                    }
                }
                System.out.println();
            }
            for (Node node:graphWP.getNodes()) {
                System.out.println(node.N+":"+node.P);
            }

//            Path bestPath = paths.get(0);
            break;
        }
    }
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