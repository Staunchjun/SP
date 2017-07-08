package RecommendPath;

import DataStructure.Graph;
import DataStructure.Node;
import DataStructure.Path;
import MainCode.Guider;
import TestCode.InitMap;

import java.util.*;

/**
 * Created by Administrator on 2017/7/7 0007.
 */
public class TT {
    public static void main(String[] args) {
        TestPathGenerate2 testPathGenerate = new TestPathGenerate2();

        Map<String, Set<Integer>> history = testPathGenerate.history;
        Map<Integer, Set<Integer>> TNewCustomer = testPathGenerate.TNewCustomer;

        List<Double> errors = new ArrayList<Double>();
        for (Map.Entry<Integer, Set<Integer>> newCustomer : TNewCustomer.entrySet()) {
            //Generating paths;
            // 初始化graph
            Graph graph = InitMap.returnGraph();
            Set<Node> nodes = new HashSet<Node>();
            int alyeadyBuy = 0;
            List<Integer> restBuy = new ArrayList<Integer>();
            int J = (int) (newCustomer.getValue().size() - newCustomer.getValue().size() * 0.5);
            Node Jnode = null;
            for (int i : newCustomer.getValue()) {
                if (alyeadyBuy <= J) {
                    if (alyeadyBuy == J - 1) {
                        Jnode = graph.getNode(testPathGenerate.pLocation.get(i));
                    }
                    nodes.add(graph.getNode(testPathGenerate.pLocation.get(i)));
                    alyeadyBuy++;
                } else {
                    restBuy.add(i);
                }
            }

            System.out.println();
            System.out.println("已经路过的点:");
            for (Node node : nodes) {
                System.out.print(node.N + "  ");
            }

            Stack<Node> finalPath = new Stack<Node>();
            Node lastNode = graph.getNode(0);
            while (nodes.size() != 0 && nodes != null) {
                Node des = graph.getNode(nodes.iterator().next().N);
                List<Path> paths = Guider.getSingleDestPath(graph, lastNode, des, null, 0.1);
                if (paths.isEmpty()) {
                    //出现了自己去自己,eg:  4->4
                    nodes.remove(graph.getNode(des.N));
                    continue;
                }
                Path bestPath = paths.get(0);
                Stack<Node> tempPath = new Stack<Node>();
                for (Node node : bestPath.getNodes()) {
                    tempPath.push(node);
                    if (nodes.contains(graph.getNode(node.N))) {
                        nodes.remove(graph.getNode(node.N));
                    }
                }
                if (nodes.isEmpty()) {
                    System.out.println("nodes 清空");
                }
                System.out.println();
                while (!tempPath.isEmpty()) {
                    if (!finalPath.isEmpty()) {
                        Node topNode = finalPath.peek();
                        if (topNode == tempPath.peek()) {
                            finalPath.pop();
                        }
                    }
                    finalPath.push(tempPath.pop());
                }
                if (nodes.isEmpty()) {
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
            for (HashMap.Entry<String, Set<Integer>> e : history.entrySet()) {
                if (e.getKey().length() != 0)
                    dataSet.add(new DataPoint(e.getKey(), "b" + count));
                count++;
            }
            //添加顾客J之前的路径;
            dataSet.add(t);

            //设置原始数据集
            List<Cluster> finalClusters = HCluster.startCluster(dataSet, testPathGenerate.K, 0.5);
            //查看结果
            for (int m = 0; m < finalClusters.size(); m++) {
                System.out.println(finalClusters.get(m).getClusterName());
                for (DataPoint dataPoint : finalClusters.get(m).getDataPoints()) {
                    System.out.println(dataPoint.getDataPointName() + ":" + dataPoint.getData());
                }
                System.out.println();
            }
            //计算每一个簇类的概率分布
            Map<String, Map<Integer, Double>> clusterDistribution = new HashMap<String, Map<Integer, Double>>();
            for (int m = 0; m < finalClusters.size(); m++) {
                System.out.println(finalClusters.get(m).getClusterName());
                Map<Integer, Double> productNum = getClusterDistribution(history, t, finalClusters.get(m));
                clusterDistribution.put(finalClusters.get(m).getClusterName(), productNum);
                System.out.println();
            }
            //计算t簇类的概率分布
            System.out.println("list node:" + t.getDataPointName() + " cluster:" + t.getCluster().getClusterName());
            Cluster cluster = t.getCluster();
            Map<Integer, Double> ProductProbability = getClusterDistribution(history, t, cluster);
            if (ProductProbability == null) break;

            //带有的总概率算预测接下来的路径推荐，
            // 看回最初的路径推荐算法
            Graph graphWP = InitMap.returnGraphWP(ProductProbability, testPathGenerate);
            List<Path> paths = Guider.getSingleDestPath(graphWP, node_j_1, graphWP.getNode(Jnode.N), null, 0.1);

            System.out.println("要购买但是还没买的：");
            for (int i : restBuy) {
                System.out.print(i + " ");
            }
            System.out.println();
            Map<Integer, Set<Integer>> shelf = testPathGenerate.shelf;

            for (Path p : paths) {
                for (Node node : p.getNodes()) {
                    System.out.print(node.N + "<-");
                }
                System.out.println();
                for (Node node : p.getNodes()) {
                    System.out.print(node.N + ":" + node.P + "    ");
                    Set<Integer> produccts = shelf.get(node.N);
                    for (int i : restBuy) {
                        if (produccts.contains(i))
                            System.out.print("包含要买的：" + i);
                    }
                    System.out.println();
                }
                System.out.println();
            }
            double MaxUtility = Integer.MIN_VALUE;
            Path bestPath = null;
            for (Path path : paths) {
                if (MaxUtility < path.U) {
                    MaxUtility = path.U;
                    bestPath = path;
                }
            }

            System.out.println();
            if (bestPath == null) {
                continue;
            }
            System.out.println("this is best path with highest utility  :" + bestPath.U);
            for (Node node : bestPath.getNodes()) {
                System.out.print(node.N + "<-");
            }
            System.out.println();

            //下面做一個對比 ，已經知道了這個用戶是哪一類的顧客，拿到那一類顧客對所有商品的概率
            int kType = newCustomer.getKey();
            double[] probability = testPathGenerate.CustomersProducts.get(kType);
            int count1 = 0;
            int errorNum = 0;
            double sumerror = 0;
            double sumErrorMean = 0;
            for (double p : probability) {
                System.out.println("商品：" + count1 + "路徑簇类算出的概率：" + p + " " + "顾客簇类算出的概率：" + ProductProbability.get(new Integer(count1)));
                if (ProductProbability.get(new Integer(count1)) != null) {
                    sumerror = sumerror + Math.abs(ProductProbability.get(new Integer(count1)) - p);
                    sumErrorMean = sumErrorMean + Math.abs(testPathGenerate.MeanCustomersProducts[count1] - p);
                    errorNum++;
                }
                count1++;
            }
            System.out.println("errors:");
            errors.add(sumerror / errorNum);
            System.out.println(sumerror / errorNum);
            System.out.println(sumErrorMean / errorNum);

            break;
        }
    }

    private static Map<Integer, Double> getClusterDistribution(Map<String, Set<Integer>> history, DataPoint t, Cluster cluster) {
        List<DataPoint> dps = cluster.getDataPoints();
        //统计每一条路径中所有已购买商品总数
        Map<Integer, Double> productNum = new HashMap<>();
        int sum = 0;
        for (DataPoint dataPoint : dps) {
                if (t.getData().equals(dataPoint.getData())) {
                    continue;
                }
            Set<Integer> products = history.get(dataPoint.getData());
            for (int product : products) {
                if (!productNum.containsKey(product)) {
                    productNum.put(product, 1.0);
                    sum += 1;
                } else {
                    double num = productNum.get(product);
                    productNum.put(product, ++num);
                    sum += 1;
                }
            }
        }
        //计算一个簇类中商品出现频率,計算所有商品出現的總數，頻率除總數可得到和為1的購買概率分佈。
        for (HashMap.Entry<Integer, Double> e : productNum.entrySet()) {
            double a = (double) e.getValue();
            productNum.put(e.getKey(), a / sum);
        }

        if (productNum.size() == 0 || productNum.isEmpty()) {
            return null;
        }
        for (HashMap.Entry<Integer, Double> e : productNum.entrySet()) {
            System.out.print("product id:" + e.getKey() + " probability:" + String.format("%4f", e.getValue()) + "   ");
        }
        System.out.println();
        return productNum;
    }
}


