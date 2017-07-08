package RecommendPath;

import java.util.Map;

/**
 * Created by Administrator on 2017/7/7 0007.
 */
public class TT {
    public static void  main(String[] args)
    {
        TestPathGenerate testPathGenerate = new TestPathGenerate();
        for (Map.Entry<Integer, double[]> e:testPathGenerate.CustomersProducts.entrySet()) {
            System.out.println(e.getKey()+":");
            for (double d:e.getValue()) {
                System.out.println(d);
            }
        }

    }

}
