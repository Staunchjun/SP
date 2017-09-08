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
        TestPathGenerate2 testPathGenerate = new TestPathGenerate2(false);

        Map<String, Set<Integer>> history = testPathGenerate.history;
        Map<Integer, Set<Integer>> TNewCustomer = testPathGenerate.TNewCustomer;

        List<Double> errors = new ArrayList<Double>();
        for (Map.Entry<Integer, Set<Integer>> newCustomer : TNewCustomer.entrySet()) {
            // 初始化graph
            //Generating paths;
            Graph graph = InitMap.returnGraph();
            Set<Node> nodes = new HashSet<Node>();

            //Generating restBuy
            Node Jnode = null;
            int alyeadyBuy = 0;
            List<Integer> restBuy = new ArrayList<Integer>();
            int J = (int) (newCustomer.getValue().size() - newCustomer.getValue().size() * 0.5);
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
//            System.out.println();
//            System.out.println("已经路过的点:");
//            for (Node node : nodes) {
//                System.out.print(node.N + "  ");
//            }


            //Generating path datapoint
            Stack<Node> finalPath = new Stack<Node>();
            Node lastNode = graph.getNode(0);
            while (nodes.size() != 0 && nodes != null) {
                Node des = graph.getNode(nodes.iterator().next().N);
                List<Path> paths = Guider.getSingleDestPath(graph, lastNode, des, null, 0.1, false);
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
//                if (nodes.isEmpty()) {
//                    System.out.println("nodes 清空");
//                }
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
            //设置原始数据集,不开始聚类,直接对历史做平均。

            //设置原始数据集,并开始聚类
            List<Cluster> finalClusters = HCluster.startCluster(dataSet, testPathGenerate.K, 0.5);

            //查看聚类结果
            chekCluster(finalClusters);

            //计算所有簇类的概率分布
            Map<Integer, Map<String, Map<Integer, Double>>> clusterDistributions = getClusterDistributions(testPathGenerate, history, t, finalClusters, false);

            //计算路径聚类下的平均簇类
            double[] MeanCustomersProducts1 = getMeanCluster(testPathGenerate, clusterDistributions);

            //路径簇类和路径聚类下的平均簇类差值，这里不需要配对，因为平均簇类是一样的
            computeErrorByMean(clusterDistributions, true, MeanCustomersProducts1);

            //建立ErrorMap用于簇类配对(自身聚类后计算的概率分布和给定的用户的概率分布之差，error最小为一对)
            int errorsMapLength = clusterDistributions.size();
            double[][] errorsMap = createErrorMap(testPathGenerate, clusterDistributions, errorsMapLength);
            //根据ErrorMap进行一一配对，打印路径簇类和给定概率簇类的比较
            pairClusterByErrormap(errorsMapLength, errorsMap, true);

            //路径聚类下的平均簇类和给定顾客簇类的差值
            errorMeancustomerAndCustomer(testPathGenerate, true, MeanCustomersProducts1);

            //计算targer t 所在簇类的概率分布
            //System.out.println("target list node:" + t.getDataPointName() + " cluster:" + t.getCluster().getClusterName());
            Cluster cluster = t.getCluster();
            //每一个簇类的概率,商品不存在的补0
            Map<Integer, Double> ProductProbability = getClusterDistribution(history, t, cluster, false);
            for (int i = 0; i < testPathGenerate.N; i++) {
                if (!ProductProbability.containsKey(i)) {
                    ProductProbability.put(i, 0.0);
                }
            }
            if (ProductProbability == null) break;

            // 带有的总概率算预测接下来的路径推荐，
            // 看回最初的路径推荐算法
//            if (RecommendPath(testPathGenerate, Jnode, restBuy, node_j_1, ProductProbability,true)) continue;

            //下面做一個對比 ，已經知道了這個用戶是哪一類的顧客，拿到那一類顧客對所有商品的概率
            Compare(testPathGenerate, errors, newCustomer, ProductProbability, MeanCustomersProducts1);


            break;
        }
    }

    /**
     * 根据聚类出来的结果，计算平均cluster
     *
     * @param testPathGenerate
     * @param clusterDistributions
     * @return
     */
    private static double[] getMeanCluster(TestPathGenerate2 testPathGenerate, Map<Integer, Map<String, Map<Integer, Double>>> clusterDistributions) {
        double[] MeanCustomersProducts = new double[testPathGenerate.N];
        for (int i = 0; i < 7; i++) {
            Map<String, Map<Integer, Double>> clusterDistributionTemp = clusterDistributions.get(i);
            double[] distributionByPath = new double[testPathGenerate.N];
            for (Map.Entry<String, Map<Integer, Double>> e : clusterDistributionTemp.entrySet()) {
                Map<Integer, Double> ee = e.getValue();//e.getKey()拿到的是cluster的名字
                for (int j = 0; j < ee.size(); j++) {
                    MeanCustomersProducts[j] += ee.get(j);

                }
            }
            for (int j = 0; j < testPathGenerate.K; j++) {
                MeanCustomersProducts[j] = MeanCustomersProducts[j] / testPathGenerate.K;
            }

        }
        return MeanCustomersProducts;
    }

    /**
     * 已經知道了這個用戶是哪一類的顧客，拿到那一類顧客對所有商品的概率
     * 比较顾客簇类算出的概率和路径簇类算出的概率
     *
     * @param testPathGenerate
     * @param errors
     * @param newCustomer
     * @param productProbability
     */
    private static void Compare(TestPathGenerate2 testPathGenerate, List<Double> errors, Map.Entry<Integer, Set<Integer>> newCustomer, Map<Integer, Double> productProbability, double[] MeanCustomersProducts1) {
        int kType = newCustomer.getKey();
        double[] probability = testPathGenerate.CustomersProducts.get(kType);
        int count1 = 0;
        int errorNum = 0;
        double sumerror = 0;
        double sumErrorMean = 0;
        double sumErrorPath = 0;
        for (double p : probability) {
            System.out.println("商品：" + count1 + "顾客簇类算出的概率：" + p + " " + "路徑簇类算出的概率：" + productProbability.get(new Integer(count1)));
            if (productProbability.get(new Integer(count1)) != null) {
                sumerror = sumerror + Math.abs(productProbability.get(new Integer(count1)) - p);
                sumErrorMean = sumErrorMean + Math.abs(MeanCustomersProducts1[count1] - p);
                sumErrorPath = sumErrorPath + Math.abs(productProbability.get(new Integer(count1)) - MeanCustomersProducts1[count1]);
                errorNum++;
            }
            count1++;
        }
        System.out.println("errors:");
        errors.add(sumerror / errorNum);
        System.out.println("(单个簇类比较)顾客簇类和路径簇类的平均误差（每个商品）：" + sumerror / errorNum);
        System.out.println("(单个簇类比较)路径聚类下的平均簇类和顾客簇类的平均误差（每个商品）" + sumErrorMean / errorNum);
        System.out.println("(单个簇类比较)路径聚类下的平均簇类和路径簇类的平均误差（每个商品）" + sumErrorPath / errorNum);
    }

    /**
     * 开始路径推荐
     *
     * @param testPathGenerate
     * @param jnode
     * @param restBuy
     * @param node_j_1
     * @param productProbability
     * @return
     */
    private static boolean RecommendPath(TestPathGenerate2 testPathGenerate, Node jnode, List<Integer> restBuy, Node node_j_1, Map<Integer, Double> productProbability, boolean printlOrNot) {
        Graph graphWP = InitMap.returnGraphWP(productProbability, testPathGenerate);
        List<Path> paths = Guider.getSingleDestPath(graphWP, node_j_1, graphWP.getNode(jnode.N), null, 0.1, printlOrNot);


        System.out.println("用户要购买但是还没买的：");
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
     * 给定概率的顾客簇类和路径聚类下的平均簇类的比较
     *
     * @param testPathGenerate
     * @param printOrNot
     */
    private static void errorMeancustomerAndCustomer(TestPathGenerate2 testPathGenerate, boolean printOrNot, double[] MeanCustomersProducts1) {
        List<Double> errListWithMean = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            int productNum = MeanCustomersProducts1.length;
            Map<Integer, double[]> CustomersProducts = testPathGenerate.CustomersProducts;
            double[] pros = CustomersProducts.get(i);

            double error = 0;
            for (int k = 0; k < productNum; k++) {
                error += (Math.abs(pros[k] - MeanCustomersProducts1[k]));

            }
            errListWithMean.add(error / productNum);
        }
        if (printOrNot) {
            System.out.println("给定概率的顾客簇类和路径聚类下的平均簇类的比较:");
            double meanError2 = 0;
            for (Double d : errListWithMean) {
                System.out.println(d + " ");
                meanError2 += d;
            }
            System.out.println("平均误差：" + meanError2 + " ");
        }
    }

    /**
     * 进行簇类类别和用户类别的配对
     *
     * @param errorsMapLength
     * @param errorsMap
     */
    private static void pairClusterByErrormap(int errorsMapLength, double[][] errorsMap, boolean printOrNot) {
        Map<Integer, Integer> CPpair = new HashMap<Integer, Integer>();
        List<Double> errList = new ArrayList<>();
        List<Integer> PathVisited = new ArrayList<Integer>();
        List<Integer> CustomerVisited = new ArrayList<Integer>();
        for (int i = 0; i < errorsMapLength; i++) {
            double min = Integer.MAX_VALUE;
            int CustomerClusterIndex = 0;
            int PathClusterIndex = 0;
            for (int j = 0; j < errorsMapLength; j++) {

                for (int k = 0; k < errorsMapLength; k++) {
                    if (PathVisited.contains(j) || CustomerVisited.contains(k))
                        continue;
                    if (errorsMap[j][k] < min) {
                        min = errorsMap[j][k];
                        PathClusterIndex = j;
                        CustomerClusterIndex = k;
                    }
                }
            }
            CPpair.put(PathClusterIndex, CustomerClusterIndex);
            PathVisited.add(PathClusterIndex);
            CustomerVisited.add(CustomerClusterIndex);
            errList.add(min);
        }
        if (printOrNot) {
            System.out.println("配对结果(左边为路径聚合概率分布,右边为给定概率分布):");
            System.out.println(CPpair);
            System.out.println(errList);
            System.out.println("给定概率的顾客簇类和路径簇类的比较");
            double meanError = 0;
            for (Double d : errList) {
                System.out.println(d + " ");
                meanError += d;
            }
            System.out.println("平均误差：" + meanError + " ");
        }
    }

    /**
     * 二维误差数组，用于簇类配对
     *
     * @param testPathGenerate
     * @param clusterDistributions
     * @param errorsMapLength
     * @return
     */
    private static double[][] createErrorMap(TestPathGenerate2 testPathGenerate, Map<Integer, Map<String, Map<Integer, Double>>> clusterDistributions, int errorsMapLength) {
        double[][] errorsMap = new double[errorsMapLength][errorsMapLength];
        //i ->Path cluster j-> given customer cluster
        for (int i = 0; i < errorsMapLength; i++) {
            for (int j = 0; j < errorsMapLength; j++) {
                double[] distributionByCustomer = testPathGenerate.CustomersProducts.get(j);
                int productNum = distributionByCustomer.length;

                Map<String, Map<Integer, Double>> clusterDistributionTemp = clusterDistributions.get(i);
                double[] distributionByPath = new double[productNum];
                for (Map.Entry<String, Map<Integer, Double>> e : clusterDistributionTemp.entrySet()) {
                    Map<Integer, Double> ee = e.getValue();
                    for (Map.Entry<Integer, Double> eee : ee.entrySet()) {
                        distributionByPath[eee.getKey()] = eee.getValue();
                    }
                }

                double error = 0;
                for (int k = 0; k < productNum; k++) {

                    double pByPath = distributionByPath[k];
                    double pByCustomer = distributionByCustomer[k];
                    error += (Math.abs(pByCustomer - pByPath));

                }
                errorsMap[i][j] = error / productNum;
            }
        }
        return errorsMap;
    }

    /**
     * 路径簇类和路径聚类下的平均簇类差值
     *
     * @param clusterDistributions
     * @return
     */
    private static void computeErrorByMean(Map<Integer, Map<String, Map<Integer, Double>>> clusterDistributions, boolean printOrNot, double[] MeanCustomersProducts1) {
        List<Double> errListWithMean = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            int productNum = MeanCustomersProducts1.length;
            Map<String, Map<Integer, Double>> clusterDistributionTemp = clusterDistributions.get(i);
            double[] distributionByPath = new double[productNum];
            for (Map.Entry<String, Map<Integer, Double>> e : clusterDistributionTemp.entrySet()) {
                Map<Integer, Double> ee = e.getValue();
                for (Map.Entry<Integer, Double> eee : ee.entrySet()) {
                    distributionByPath[eee.getKey()] = eee.getValue();
                }
            }
            double error = 0;
            for (int k = 0; k < productNum; k++) {
                double pByPath = distributionByPath[k];
                double pByCustomer = MeanCustomersProducts1[k];
                error += (Math.abs(pByCustomer - pByPath));

            }
            errListWithMean.add(error / productNum);
        }
        if (printOrNot) {
            System.out.println("路径簇类和路径聚类下的平均簇类比较:");
            double meanError2 = 0;
            for (Double d : errListWithMean) {
                System.out.println(d + " ");
                meanError2 += d;
            }
            System.out.println("平均误差：" + meanError2 + " ");
        }
    }

    /**
     * 查看聚类结果
     *
     * @param finalClusters
     */
    private static void chekCluster(List<Cluster> finalClusters) {
        for (int m = 0; m < finalClusters.size(); m++) {
            System.out.println(finalClusters.get(m).getClusterName());
            for (DataPoint dataPoint : finalClusters.get(m).getDataPoints()) {
                System.out.println(dataPoint.getDataPointName() + ":" + dataPoint.getData());
            }
            System.out.println();
        }
    }

    /**
     * 得到所有簇类的概率分布
     *
     * @param testPathGenerate
     * @param history
     * @param t
     * @param finalClusters
     * @return
     */
    private static Map<Integer, Map<String, Map<Integer, Double>>> getClusterDistributions(TestPathGenerate2 testPathGenerate, Map<String, Set<Integer>> history, DataPoint t, List<Cluster> finalClusters, Boolean printOrNot) {
        Map<Integer, Map<String, Map<Integer, Double>>> clusterDistributions = new HashMap<Integer, Map<String, Map<Integer, Double>>>();
        int countCluster = 0;
        for (int m = 0; m < finalClusters.size(); m++) {
            if (printOrNot) {
                System.out.println("簇类名字：" + finalClusters.get(m).getClusterName());
            }
            Map<String, Map<Integer, Double>> clusterDistribution = new HashMap<String, Map<Integer, Double>>();
            Map<Integer, Double> productNum = getClusterDistribution(history, t, finalClusters.get(m), printOrNot);
            clusterDistribution.put((finalClusters.get(m).getClusterName()), productNum);
            //每一个簇类的概率,商品不存在的补0
            for (Map.Entry<String, Map<Integer, Double>> e : clusterDistribution.entrySet()) {
                for (int i = 0; i < testPathGenerate.N; i++) {
                    if (!e.getValue().containsKey(i)) {
                        e.getValue().put(i, 0.0);
                    }
                }
            }
            clusterDistributions.put(countCluster, clusterDistribution);
            countCluster++;
            if (printOrNot) {
                System.out.println();
            }
        }
        return clusterDistributions;
    }

    /**
     * 得到单个簇类的概率分布
     *
     * @param history
     * @param t
     * @param cluster
     * @return
     */

    private static Map<Integer, Double> getClusterDistribution(Map<String, Set<Integer>> history, DataPoint t, Cluster cluster, boolean printOrNot) {
        List<DataPoint> dps = cluster.getDataPoints();
        //统计每一条路径中所有已购买商品总数
        Map<Integer, Double> productNum = new HashMap();
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


