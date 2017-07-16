package RecommendPath;

import DataStructure.Graph;
import DataStructure.Node;
import DataStructure.Path;
import MainCode.Guider;
import TestCode.InitMap;
import Util.Util;

import java.util.*;

/**
 * Created by Administrator on 2017/6/28 0028.
 */
public class TestPathGenerate2 {
    //N types of product eg:P1,P2,P3...Pn;表明有多少种商品
    final static int N = 160;
    //K types of customer eg:C1,C2,C3...Cn;表明有多少类型的顾客
    final static int K = 7;
    //M paths 表示要运行多少次，产生多少路径
    final static int M = 100;
    //每个点有多少种类的商品。
    final static int NN = 10;
    //给定一个概率分布，这里的概率意思是每顾客对每一种商品的喜好程度，概率和为1。
    public static Map<Integer, double[]> CustomersProducts;
    //所有用户簇类平均的一个概率。
    public static double[] MeanCustomersProducts = new double[N];
    //T个新的顾客，只知道他们将要购物的清单列表
    final int T = 10;

    public static Map<Integer, Integer> pLocation;
    private static HashMap<Integer, Double> allProducts;
    public static Map<Integer, Set<Integer>> shelf;
    public static Map<String, Set<Integer>> history;
    public static Map<Integer, Set<Integer>> TNewCustomer;


    public TestPathGenerate2() {
        Random random = new Random();
        history = new HashMap<String, Set<Integer>>();
        TNewCustomer = new HashMap<Integer, Set<Integer>>();
        //初始化所有商品 所有概率为0
        InitProducts();
        //初始化，不用簇类的用户对喜欢不同类型的产品喜好不一样
        InitCustomersProducts();
        //平均用户簇类
        MeanCustomerCluster();
        //所有商品随机分配给16个点 0-15
        FillShelf();

        //***************Generate M Path********************
        for (int L = 0; L < M; L++) {
            //choose type of customer;
            int i = random.nextInt(K);
            //choose nb of products that Ci will buy;
            int nb = random.nextInt(N / 16);
            //choose which products are bought;
            Stack<Integer> shopList = new Stack<Integer>();
            for (int k = 0; k < nb; k++) {
                double[] productProbability = CustomersProducts.get(i);
                double meanPro = 1.0/N;
                int productId = random.nextInt(N);
                while (meanPro>productProbability[productId])
                {
                    productId = random.nextInt(N);
                }
                shopList.add(productId);
            }
            System.out.println();
            System.out.println("输出待购买商品列表:");
            for (Integer toBuy : shopList) {
                System.out.print(toBuy + " ");
            }
            System.out.println();
            GetPath(shopList);
        }
        //***************Generate M Path********************

        //***************Generate T Customer********************

        for (int L = 0; L < T; L++) {
            //choose type of customer;
            int i = random.nextInt(K);
            //choose nb of products that Ci will buy;
            int nb = random.nextInt(N / 4);
            //choose which products are bought;
            Set<Integer> shopList = new HashSet<Integer>();
            for (int k = 0; k < nb; k++) {
                double[] productProbability = CustomersProducts.get(i);
                double meanPro = 1.0/N;
                int productId = random.nextInt(N);
                while (meanPro>productProbability[productId])
                {
                    productId = random.nextInt(N);
                }
                shopList.add(productId);
            }
            System.out.println();
            System.out.println("输出待购买商品列表:");
            for (Integer toBuy : shopList) {
                System.out.print(toBuy + " ");
            }
            TNewCustomer.put(L, shopList);
        }
    }

    private void MeanCustomerCluster() {
        for (Map.Entry<Integer, double[]> e :CustomersProducts.entrySet()) {
                 double[] p =  e.getValue();
            for (int i = 0; i < p.length; i++) {
                MeanCustomersProducts[i] += p[i];
            }
        }
        for (int i = 0; i < MeanCustomersProducts.length; i++) {
            MeanCustomersProducts[i] = MeanCustomersProducts[i]/CustomersProducts.size();
        }
    }

    private void GetPath(Stack<Integer> shopList) {
        Set<Integer> shopListSet = new HashSet<Integer>();
        for (Integer i : shopList) {
            shopListSet.add(i);
        }
        //Get product location
        System.out.println();
        System.out.println("输出待购买商品位置:");
        for (Integer i : shopList)
            System.out.println("商品" + i + "的位置" + "->" + pLocation.get(i));
        System.out.println();

        //Generating paths;
        // 初始化graph
        Graph graph = InitMap.returnGraph();
        Set<Node> nodes = new HashSet<Node>();
        for (int i : shopList)
            nodes.add(graph.getNode(pLocation.get(i)));

        for (Node node : nodes) {
            System.out.print(node.N + "  ");
        }
        System.out.println("  ");
        Stack<Node> finalPath = new Stack<Node>();
        Node lastNode = graph.getNode(0);

        while (shopList.size() != 0 && shopList != null) {
            Node des = graph.getNode(pLocation.get(shopList.pop()));
            List<Path> paths = Guider.getSingleDestPath(graph, lastNode, des, null, 0.1);
            if (paths.isEmpty()) {
                //出现了自己去自己,eg:  4->4
                nodes.remove(graph.getNode(des.N));
                Set<Integer> products = shelf.get(des.N);
                for (Integer product : products) {
                    if (shopList.contains(product))
                        while (shopList.contains(product))
                            shopList.remove(product);
                    System.out.println("被去除的商品ID：" + product + "  位于" + des.N);
                }
                System.out.println();
                System.out.println("去除重复点。");
                for (Integer i : shopList) {
                    System.out.println("商品" + i + "的位置" + "->" + pLocation.get(i));
                }
                continue;
            }
            Path bestPath = paths.get(0);
            Stack<Node> tempPath = new Stack<Node>();
            for (Node node : bestPath.getNodes()) {
                tempPath.push(node);
                Set<Integer> products = shelf.get(node.N);
                for (Integer product : products) {
                    if (shopList.contains(product)) {
                        while (shopList.contains(product))
                            shopList.remove(product);
                        System.out.println("被去除的商品ID：" + product + "  位于" + node.N);

                    }
                }
                nodes.remove(graph.getNode(node.N));
            }
            System.out.println();
            System.out.println("去除路径中已包含点");
            for (Integer i : shopList) {
                System.out.println("商品" + i + "的位置" + "->" + pLocation.get(i));
            }
            for (Node node : nodes) {
                System.out.println("位置" + node.N);
            }
            if (shopList.isEmpty()) {
                System.out.println("shopList已经清空");
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
            if (shopList.isEmpty()) {
                break;
            }
            lastNode = graph.getNode(finalPath.peek().N);
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (Node node : finalPath) {
            System.out.print(node.N + "->");
            stringBuffer.append(node.N);
            stringBuffer.append(",");
        }
        history.put(stringBuffer.toString(), shopListSet);
    }

    private void InitCustomersProducts() {
        //Math.random()   随机生成0到1之间的数
        Random random = new Random();
        CustomersProducts = new HashMap<Integer, double[]>();
        for (int i = 0; i < K; i++) {
            double[] products = getRandDistArray(N, 1.0);
            CustomersProducts.put(new Integer(i), products);
        }
    }

    private static void FillShelf() {
        shelf = new HashMap<Integer, Set<Integer>>();
        pLocation = new HashMap<Integer, Integer>();
        for (int i = 0; i < 16; i++) {
            Set<Integer> products = new HashSet<Integer>();
            for (int j = 0; j < NN; j++) {
                while (true) {
                    Random random = new Random();
                    int productId = random.nextInt(N);
                    if (allProducts.containsKey(productId)) {
                        products.add(productId);
                        pLocation.put(productId, i);
                        allProducts.remove(productId);
                        break;
                    } else {
                        continue;
                    }
                }
            }
            shelf.put(i, products);
        }
        System.out.println();
        System.out.println("输出货架上的商品");
        for (Map.Entry e : shelf.entrySet()) {
            System.out.print(e.getKey());
            System.out.println(e.getValue());
        }
    }

    private static void InitProducts() {
        allProducts = new HashMap<Integer, Double>();
        for (int i = 0; i < N; i++) {
            allProducts.put(new Integer(i), 0.0);
        }
    }

    public static double[] getRandDistArray(int n, double m) {
        double randArray[] = new double[n];
        double sum = 0;

        // Generate n random numbers
        for (int i = 0; i < randArray.length; i++) {
            randArray[i] = Math.random();
            sum += randArray[i];
        }

        // Normalize sum to m
        for (int i = 0; i < randArray.length; i++) {
            randArray[i] /= sum;
            randArray[i] *= m;
        }
        return randArray;
    }
}
