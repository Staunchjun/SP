package RecommendPath;

import DataStructure.Graph;
import DataStructure.Node;
import TestCode.InitMap;

import java.util.*;

/**
 * Created by Administrator on 2017/6/28 0028.
 */
public class TestPath {
    //N types of product
    static int N = 160;
    static HashMap<Integer, Double> allProducts;
    static int[] shopList ;
    static Map<Integer, Set<Integer>> shelf;
    static Map<Integer,Integer> pLocation;
    public static void main(String[] args) {
        Random random = new Random();
        //均值为0.方差为1 的高斯分布
        allProducts = new HashMap<Integer, Double>();
        for (int i = 0; i < N; i++) {
            allProducts.put(new Integer(i), Math.abs(Math.sqrt(1) * random.nextGaussian() + 0));}
        //对所有的product根据probability进行排序,从大到小
        List<Map.Entry<Integer, Double>> list = new ArrayList<Map.Entry<Integer, Double>>(allProducts.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
            @Override
            public int compare(Map.Entry<Integer, Double> o1,
                               Map.Entry<Integer, Double> o2) {
                return -o1.getValue().compareTo(o2.getValue());
            }});
        //k probability distribution
        //k, a customer going to buy k products
        int k = 10;
        shopList = new int[k];
        int count = 0;
        for (Map.Entry<Integer, Double> mapping : list) {
            System.out.println(mapping.getKey() + ":" + mapping.getValue());
            shopList[count] = mapping.getKey();
            count++;
            if (count == 10) {break;}}
        //所有商品随机分配给16个点 0-15
        shelf = new HashMap<Integer, Set<Integer>>();
        pLocation = new HashMap<Integer,Integer>();
        for (int i = 0; i < 16; i++) {
            Set<Integer> products = new HashSet<Integer>();
            for (int j = 0; j < 10; j++) {
                while (true) {
                    int productId = random.nextInt(N);
                    if (allProducts.containsKey(productId)) {
                        products.add(productId);
                        pLocation.put(productId,i);
                        allProducts.remove(productId);
                        break;
                    } else {
                        continue;
                    }
                }
            }
            shelf.put(i, products);
        }
        for (Map.Entry e : shelf.entrySet()) {
            System.out.print(e.getKey());
            System.out.println(e.getValue());
        }
        for (int toBuy : shopList) {
            System.out.print(toBuy + " ");
        }
        //Get product location
        System.out.println();
        System.out.print("输出位置");
        for (int i : shopList)
            System.out.println(i+" "+pLocation.get(i));
        //Generating paths;
        // 初始化graph
        Graph graph = InitMap.returnGraph();
        //   设置多个目标点
        ArrayList<Node> nodes = new ArrayList<Node>();
        nodes.add(graph.getNode(0));
        nodes.add(graph.getNode(15));
        nodes.add(graph.getNode(12));

    }
}
