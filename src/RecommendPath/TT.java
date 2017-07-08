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
    public static void  main(String[] args) {
        TestPathGenerate2 testPathGenerate = new TestPathGenerate2();

        Map<String, Set<Integer>> history = testPathGenerate.history;
        Map<Integer, Set<Integer>> TNewCustomer = testPathGenerate.TNewCustomer;

        for (Map.Entry<Integer, Set<Integer>> newCustomer : TNewCustomer.entrySet()) {
            //Generating paths;
            // 初始化graph
            Graph graph = InitMap.returnGraph();
            Set<Node> nodes = new HashSet<Node>();
            int alyeadyBuy = 0;
            List<Integer> restBuy = new ArrayList<Integer>();
            int J = (int) (newCustomer.getValue().size()-newCustomer.getValue().size()*0.5);
            Node Jnode = null;
            for (int i : newCustomer.getValue()) {
                if (alyeadyBuy <=J) {
                    if (alyeadyBuy == J-1) {
                        Jnode = graph.getNode(testPathGenerate.pLocation.get(i));
                    }
                    nodes.add(graph.getNode(testPathGenerate.pLocation.get(i)));
                    alyeadyBuy++;
                }
                else {
                    restBuy.add(i);
                }
            }

            System.out.println();
            System.out.println("已经路过的点:");
            for (Node node:nodes) {
                System.out.print(node.N+"  ");
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
                dataSet.add(new DataPoint(e.getKey(), "b" + count));
                count++;
            }
            //添加顾客J之前的路径;
            dataSet.add(t);

            //设置原始数据集
            List<Cluster> finalClusters = HCluster.startCluster(dataSet, 7, 0.5);
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
                Set<Integer> products = history.get(dataPoint.getData());
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

            //带有的总概率算预测接下来的路径推荐，
            // 看回最初的路径推荐算法
            Graph graphWP =InitMap.returnGraphWP(productNum,testPathGenerate);
            List<Path> paths = Guider.getSingleDestPath(graphWP,node_j_1, graphWP.getNode(Jnode.N), null, 0.1);

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
                            System.out.print("包含要买的："+i);
                    }
                    System.out.println();
                }
                System.out.println();
            }
            Path bestPath = paths.get(0);
            break;
        }
        }
    }


