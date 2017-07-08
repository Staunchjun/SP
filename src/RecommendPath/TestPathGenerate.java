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
public class TestPathGenerate {
    //N types of product eg:P1,P2,P3...Pn;表明有多少种商品
    final static int N = 160;
    //K types of customer eg:C1,C2,C3...Cn;表明有多少类型的顾客
    final static int K = 7;
    //M paths 表示要运行多少次，产生多少路径
    final static int M = 100;
    //每个点有多少种类的商品。
    final static int NN = 10;
    //给定一个概率分布，这里的概率意思是每顾客对每一种商品的喜好程度，概率和为1。
    public static HashMap<Integer, double[]> CustomersProducts;


    public static Map<Integer, Integer> pLocation;
    private static HashMap<Integer, Double> allProducts;
    public static HashMap<Integer, Double> allCustomers;
    public static Map<Integer, Set<Integer>> shelf;
    public static Map<String, int[]> history;
    public static Map<int[], double[]> TNewCustomer;

    public TestPathGenerate() {
        Random random = new Random();
        history = new HashMap<String, int[]>();
        TNewCustomer = new HashMap<int[], double[]>();
//        初始化所有用户的概率分布，Cluster 所有概率为随机0-1
//        InitCustomers();

        //初始化所有商品 所有概率为0
        InitProducts();
        //初始化，不用簇类的用户对喜欢不同类型的产品喜好不一样
        InitCustomersProducts();
        //所有商品随机分配给16个点 0-15
        FillShelf();
        //choose type of customer;
        int i = random.nextInt(K);
        //choose nb of products that Ci will buy;
        int nb = random.nextInt(N / 4);
        //choose which products are bought;
        int[] shopList = new int[nb];
        for (int k = 0; k < nb; k++) {
            int productId = random.nextInt(N);
            shopList[k] = productId;
        }
        System.out.println();
        System.out.println("输出待购买商品列表:");
        for (int toBuy : shopList) {
            System.out.print(toBuy + " ");
        }
        System.out.println();
        //Generate M Path
        GetMPaths();
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

    private void InitCustomers() {
        //Math.random()   随机生成0到1之间的数
        allCustomers = new HashMap<Integer, Double>();
        for (int i = 0; i < K; i++) {
            allCustomers.put(new Integer(i), Math.random());
        }
    }

    public Map<String, int[]> TestPathGenerating() {
        //开始产生M条道路啦~~~~
        GetMPaths();
        return history;
    }

    public Map<int[], double[]> TestTNewCustomer() {
        //得到T个新用户将要购买的产品列表
        GetTNewCustomer(10);
        return TNewCustomer;
    }

    private void GetTNewCustomer(int T) {
        for (int i = 0; i < T; i++) {
            AssignProbability(allProducts);
            //对所有的product根据probability进行排序,从大到小
            List<Map.Entry<Integer, Double>> list = new ArrayList<Map.Entry<Integer, Double>>(allProducts.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
                @Override
                public int compare(Map.Entry<Integer, Double> o1,
                                   Map.Entry<Integer, Double> o2) {
                    return -o1.getValue().compareTo(o2.getValue());
                }
            });
            //K probability distribution
            //K, a customer going to buy K products
            System.out.println();
            System.out.print("输出待购买商品概率(由高到低):");
            System.out.println();
            int[] shopList = new int[K];
            double[] productProbability = new double[K];
            int count = 0;
            for (Map.Entry<Integer, Double> mapping : list) {
                System.out.println("商品" + mapping.getKey() + "的购买概率" + ":" + mapping.getValue());
                shopList[count] = mapping.getKey();
                productProbability[count] = mapping.getValue();
                count++;
                if (count == K) {
                    break;
                }
            }
            TNewCustomer.put(shopList, productProbability);
            System.out.println();
            System.out.print("输出待购买商品列表:");
            System.out.println();
            for (int toBuy : shopList) {
                System.out.print(toBuy + " ");
            }
        }
    }

    private static void GetMPaths() {
        List<String> paths = new ArrayList<String>();
        for (int i = 0; i < M; i++) {
            //为商品分配概率  //每一次分配概率都会刷新商品里面的概率值 这里耦合太高了
//            AssignProbability(allProducts);
            //对所有的product根据probability进行排序,从大到小
            List<Map.Entry<Integer, Double>> list = new ArrayList<Map.Entry<Integer, Double>>(allProducts.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
                @Override
                public int compare(Map.Entry<Integer, Double> o1,
                                   Map.Entry<Integer, Double> o2) {
                    return -o1.getValue().compareTo(o2.getValue());
                }
            });
            GenerateRamdomPaths(list);
        }
        System.out.println();
        System.out.println("输出自动产生的路径合集:");
        for (Map.Entry s : history.entrySet()) {
            System.out.println(s.getKey() + ":");
            for (int product : (int[]) s.getValue()) {
                System.out.print(product + " ");
            }
            System.out.println();
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
        System.out.print("输出货架上的商品");
        System.out.println();
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

    private static void AssignProbability(HashMap<Integer, Double> allProducts) {
        //Math.random()   随机生成0到1之间的数
        for (int i = 0; i < N; i++) {
            allProducts.put(new Integer(i), Math.random());
        }
    }

    private static void GenerateRamdomPaths(List<Map.Entry<Integer, Double>> list) {
        //K probability distribution
        //K, a customer going to buy K products
        System.out.println();
        System.out.print("输出待购买商品概率(由高到低):");
        System.out.println();
        int[] shopList = new int[K];
        int count = 0;
        for (Map.Entry<Integer, Double> mapping : list) {
            System.out.println("商品" + mapping.getKey() + "的购买概率" + ":" + mapping.getValue());
            shopList[count] = mapping.getKey();
            count++;
            if (count == K) {
                break;
            }
        }
        System.out.println();
        System.out.print("输出待购买商品列表:");
        System.out.println();
        for (int toBuy : shopList) {
            System.out.print(toBuy + " ");
        }
        //Get product location
        System.out.println();
        System.out.println();
        System.out.println("输出待购买商品位置:");
        for (int i : shopList)
            System.out.println("商品" + i + "的位置" + "->" + pLocation.get(i));
        System.out.println();

        //Generating paths;
        // 初始化graph
        Graph graph = InitMap.returnGraph();
        ArrayList<Node> nodes = new ArrayList<Node>();
        for (int i : shopList)
            nodes.add(graph.getNode(pLocation.get(i)));
        Stack<Node> finalPath = new Stack<Node>();
        Map<Integer, Double> pDis = new HashMap<Integer, Double>();
        //对所有的product根据离入口距离进行排序,从小到大
        Node lastNode = graph.getNode(0);
        List<Map.Entry<Integer, Double>> pDisSorted = CreateSort(pDis, nodes, graph, lastNode);
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
                pDisSorted = CreateSort(pDis, nodes, graph, des);
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
                    pDisSorted = CreateSort(pDis, nodes, graph, des);

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
        for (Node node : finalPath) {
            System.out.print(node.N + "->");
            stringBuffer.append(node.N);
            stringBuffer.append(",");
        }
        history.put(stringBuffer.toString(), shopList);
    }

    public static List<Map.Entry<Integer, Double>> CreateSort(Map<Integer, Double> a, ArrayList<Node> nodes, Graph graph, Node lastNode) {
        for (Node target : nodes) {
            double Cost = Util.getDis(lastNode, target);
            a.put(target.N, Cost);
        }
        List<Map.Entry<Integer, Double>> list1 = new ArrayList<Map.Entry<Integer, Double>>(a.entrySet());
        Collections.sort(list1, new Comparator<Map.Entry<Integer, Double>>() {
            @Override
            public int compare(Map.Entry<Integer, Double> o1,
                               Map.Entry<Integer, Double> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        return list1;
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
