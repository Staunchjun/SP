package RecommendPath;

import Cluster.*;
import GuideDataStructure.Graph;
import GuideDataStructure.Node;
import GuideDataStructure.Path;
import GuideMainCode.Guider;
import com.csvreader.CsvReader;

import java.nio.charset.Charset;
import java.util.*;

public class recommend {
    public static void main(String[] args) throws Exception {
        System.out.println("现在开始读取数据");
        long start = System.currentTimeMillis();
        Map<String, Set<Integer>> history = new HashMap<String, Set<Integer>>();
        CsvReader reader = new CsvReader("/Users/ruanwenjun/IdeaProjects/SP/src/csvData/Paths.csv", ',', Charset.forName("GBK"));
        while (reader.readRecord()) {
            String s = reader.getValues()[1];
            String[] ss = s.split(",");
            Set<Integer> integerSet = new HashSet<>();
            for (String sss : ss) {
                integerSet.add(Integer.valueOf(sss));
            }

            history.put(reader.getValues()[0], integerSet);

        }
        reader.close();


        ArrayList<String[]> customerDistributionRaw = new ArrayList<String[]>();
        reader = new CsvReader("/Users/ruanwenjun/IdeaProjects/SP/src/csvData/CustomerDistribution.csv", ',', Charset.forName("GBK"));
        while (reader.readRecord()) {
            customerDistributionRaw.add(reader.getValues());
        }
        reader.close();

        Map<Integer, double[]> customerDistribution = new HashMap<>();
        for (String[] data : customerDistributionRaw) {
            String[] pro = data[1].split(",");
            double[] pros = new double[pro.length];
            for (int i = 0; i < pro.length; i++) {
                pros[i] = Double.valueOf(pro[i]);
            }
            customerDistribution.put(Integer.valueOf(data[0]), pros);
        }

        //================T new Customer =========================
        ArrayList<String[]> customerBuyListRaw = new ArrayList<String[]>();
        reader = new CsvReader("/Users/ruanwenjun/IdeaProjects/SP/src/csvData/newCustomer.csv", ',', Charset.forName("GBK"));
        while (reader.readRecord()) {
            customerBuyListRaw.add(reader.getValues());
        }
        reader.close();

        Map<Integer, Set<Integer>> customerBuyList = new HashMap<>();
        for (String[] data : customerBuyListRaw) {
            String[] proId = data[1].split(",");
            Set<Integer> list = new HashSet<>();
            for (String s : proId) {
                list.add(Integer.valueOf(s));
            }
            customerBuyList.put(Integer.valueOf(data[0]), list);
        }

        //================plocation =========================

        ArrayList<String[]> ListRaw = new ArrayList<String[]>();
        reader = new CsvReader("/Users/ruanwenjun/IdeaProjects/SP/src/csvData/Shelf.csv", ',', Charset.forName("GBK"));
        while (reader.readRecord()) {
            ListRaw.add(reader.getValues());
        }
        reader.close();
        Map<Integer, Set<Integer>> shelf = new HashMap<>();
        for (String[] data : ListRaw) {
            String[] proId = data[1].split(",");
            Set<Integer> pros = new HashSet<>();
            for (String s:proId) {
                pros.add(Integer.valueOf(s));
            }
            Integer locationId = Integer.valueOf(data[0]);
            shelf.put(locationId,pros);
        }


        Map<Integer, Integer> pLocation = new HashMap<>();
        for (String[] data : ListRaw) {
            String[] proId = data[1].split(",");
            Integer locationId = Integer.valueOf(data[0]);
            for (String s : proId) {
                pLocation.put(Integer.valueOf(s), locationId);
            }
        }


        for (Map.Entry<Integer, Set<Integer>> newCustomer : customerBuyList.entrySet()) {
            // 初始化graph
            //Generating paths;
            Graph graph = InitMap.returnGraph();
            /**
             * 障碍点
             */
            List<Node> obs = new ArrayList<>();
            obs.add(graph.getNode(11));
            obs.add(graph.getNode(21));
            obs.add(graph.getNode(31));
            obs.add(graph.getNode(13));
            obs.add(graph.getNode(23));
            obs.add(graph.getNode(33));
            obs.add(graph.getNode(15));
            obs.add(graph.getNode(25));
            obs.add(graph.getNode(35));
            obs.add(graph.getNode(17));
            obs.add(graph.getNode(37));
            obs.add(graph.getNode(18));
            obs.add(graph.getNode(38));
            obs.add(graph.getNode(51));
            obs.add(graph.getNode(52));
            obs.add(graph.getNode(71));
            obs.add(graph.getNode(72));
            obs.add(graph.getNode(91));
            obs.add(graph.getNode(92));
            obs.add(graph.getNode(54));
            obs.add(graph.getNode(64));
            obs.add(graph.getNode(56));
            obs.add(graph.getNode(66));
            obs.add(graph.getNode(76));
            obs.add(graph.getNode(58));
            obs.add(graph.getNode(68));
            obs.add(graph.getNode(78));
            obs.add(graph.getNode(94));
            obs.add(graph.getNode(95));
            obs.add(graph.getNode(96));
            obs.add(graph.getNode(98));
            obs.add(graph.getNode(9));

            Set<Node> nodes = new HashSet<Node>();
            //Generating restBuy
            Node Jnode = null;
            int alyeadyBuy = 0;
            List<Integer> restBuy = new ArrayList<Integer>();
            int J = (int) (newCustomer.getValue().size() - newCustomer.getValue().size() * 0.5);
            for (int i : newCustomer.getValue()) {
                if (alyeadyBuy <= J) {
                    if (alyeadyBuy == J - 1) {
                        Jnode = graph.getNode(pLocation.get(i));
                    }
                    nodes.add(graph.getNode(pLocation.get(i)));
                    alyeadyBuy++;
                } else {
                    restBuy.add(i);
                }
            }

            //Generating path datapoint
            Stack<Node> finalPath = new Stack<Node>();
            Node lastNode = graph.getNode(0);
            while (nodes.size() != 0 && nodes != null) {
                Node des = graph.getNode(nodes.iterator().next().N);
                List<Path> paths = Guider.getSingleDestPath(graph, lastNode, des, obs, 0.1, false);
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
//                System.out.print(node.N + "->");
                stringBuffer.append(node.N);
                stringBuffer.append(",");
                node_j_1 = node;
            }

            //add datapoint
            ScDataPoint t = new ScDataPoint(stringBuffer.toString(), "t11");

            //加载历史数据，并且添加要搜寻的数据
            ArrayList<ScDataPoint> dataSet = new ArrayList<ScDataPoint>();
            int count = 0;
            for (HashMap.Entry<String, Set<Integer>> e : history.entrySet()) {
                if (e.getKey().length() != 0)
                    dataSet.add(new ScDataPoint(e.getKey(), "b" + count));
                count++;
            }
            dataSet.add(t);
            System.out.println("聚类进行中");
            long startTime = System.currentTimeMillis();

            //设置原始数据集,并开始聚类
            SCluster sCluster = new SCluster(dataSet, customerDistribution.size(), 0.4);

            //得到所有聚类结果
            ArrayList<ScCluster> clusters = sCluster.getCluster();


            //计算targer t 所在簇类的概率分布
            System.out.println("target list node:" + t.getDataPointName() + " cluster:" + t.getScCluster().getClusterName());
            ScCluster cluster = t.getScCluster();

            //每一个簇类的概率,商品不存在的补0
            Map<Integer, Double> ProductProbability = getClusterDistribution(history,t,cluster, false);
            for (int i = 0; i < customerDistribution.get(0).length; i++) {
                if (!ProductProbability.containsKey(i)) {
                    ProductProbability.put(i, 0.0);
                }
            }
            if ( null == ProductProbability ) break;

            // 带有的总概率算预测接下来的路径推荐，
            // 看回最初的路径推荐算法
            RecommendPath(Jnode, restBuy, node_j_1, ProductProbability, true,shelf ,pLocation,obs);
        }
    }
    /**
     * 开始路径推荐
     *
     *
     * @param jnode
     * @param restBuy
     * @param node_j_1
     * @param productProbability
     * @return
     */
    private static boolean RecommendPath(Node jnode, List<Integer> restBuy, Node node_j_1,
                                         Map<Integer, Double> productProbability,
                                         boolean printlOrNot,Map<Integer, Set<Integer>> shelf ,
                                         Map<Integer, Integer> pLocation,List<Node> obs) {
        Graph graphWP = InitMap.returnGraphWP(productProbability,pLocation);
        List<Path> paths = Guider.getSingleDestPath(graphWP, node_j_1, graphWP.getNode(jnode.N), obs, 0.1, printlOrNot);
        System.out.println("用户要购买但是还没买的：");
        for (int i : restBuy) {
            System.out.print(i + " ");
        }
        System.out.println();
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
            return true;
        }
        System.out.println("this is best path with highest utility  :" + bestPath.U);
        for (Node node : bestPath.getNodes()) {
            System.out.print(node.N + "<-");
        }
        System.out.println();
        return false;
    }

    /**
     * 得到单个簇类的概率分布
     *
     * @param history   //     * @param t
     * @param hcCluster
     * @return
     */

    private static Map<Integer, Double> getClusterDistribution(Map<String, Set<Integer>> history,ScDataPoint t, ScCluster hcCluster, boolean printOrNot) {
        List<ScDataPoint> dps = hcCluster.getScDataPoints();
        //统计每一条路径中所有已购买商品总数
        Map<Integer, Double> productNum = new HashMap();
        int sum = 0;
        for (ScDataPoint hcDataPoint : dps) {
            if (t.getData().equals(hcDataPoint.getData())) {
                continue;
            }
            Set<Integer> products = history.get(hcDataPoint.getData());
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
            double a = e.getValue();
            productNum.put(e.getKey(), a / sum);
        }

        if (productNum.size() == 0 || productNum.isEmpty()) {
            return null;
        }
        if (printOrNot) {
            for (HashMap.Entry<Integer, Double> e : productNum.entrySet()) {
                System.out.print("product id:" + e.getKey() + " probability:" + String.format("%4f", e.getValue()) + "   ");
            }
            System.out.println();
        }
        return productNum;
    }
}



