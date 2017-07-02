package RecommendPath;

import java.util.*;

/**
 * Created by Administrator on 2017/6/9.
 */
public class GenData {
    public static List<Product> GenerateFakeProduct(String path) {
        String[] nodes = path.split(",");
        List<Product> products = new ArrayList<>();
        for (String n : nodes) {
            int buyNum = new Random().nextInt(10);
            for (int j = 0; j < buyNum; j++) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(n);
                stringBuffer.append("_");
                stringBuffer.append(new Random().nextInt(20));
                Product TempProduct = new Product(stringBuffer.toString());
                products.add(TempProduct);
            }
        }
        return products;
    }

    public static Customer getCustomerData() {
        Customer customer = new Customer();
        Map<String, List<Product>> history = new HashMap<>();

        DataPoint b = new DataPoint("9,10,11,12,8,4,", "b");
        DataPoint b1 = new DataPoint("9,10,11,7,8,4,", "b1");
        DataPoint b2 = new DataPoint("9,10,11,12,8,,7,3,4,", "b2");
        DataPoint c = new DataPoint("9,5,1,2,3,4,", "c");
        DataPoint c1 = new DataPoint("9,5,6,2,3,4,", "c1");
        DataPoint c2 = new DataPoint("9,5,1,2,6,7,3,4,", "c2");
        DataPoint c3 = new DataPoint("9,5,6,7,3,4,", "c3");

        history.put(b.getData(), GenerateFakeProduct(b.getData()));
        history.put(b1.getData(), GenerateFakeProduct(b1.getData()));
        history.put(b2.getData(), GenerateFakeProduct(b2.getData()));
        history.put(c.getData(), GenerateFakeProduct(c.getData()));
        history.put(c1.getData(), GenerateFakeProduct(c1.getData()));
        history.put(c2.getData(), GenerateFakeProduct(c2.getData()));
        history.put(c3.getData(), GenerateFakeProduct(c3.getData()));
        customer.setHistory(history);

        return customer;
    }

//    public static Customer getCustomerDataAutomatic() {
//        Customer customer = new Customer();
//        Map<String, List<Product>> history = new HashMap<>();
//        Map<String, int[]> allHistory = TestPathGenerate.TestPathGenerating();
//        for (int i = 0; i < paths.size()-5; i++) {
//            history.put(paths.get(i), GenerateFakeProductAutomatic(paths.get(i)));
//        }
//        customer.setHistory(history);
//        return customer;
//    }
//
//    public static List<Product> GenerateFakeProductAutomatic(String path) {
//        String[] nodes = path.split(",");
//        List<Product> products = new ArrayList<>();
//        for (String n : nodes) {
//            Product tempProduct =
//            new Product(String.valueOf(TestPathGenerate.shelf.
//                        get(new Integer(n))));
//            products.add(tempProduct);
//        }
//        return products;
//    }
}

