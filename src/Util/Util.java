package Util;

import GuideDataStructure.Node;
import GuideDataStructure.Path;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import org.python.antlr.ast.Str;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ruan on 16-10-22.
 */
public class Util {
    /**
     * 两个点之间的距离
     * @param p1 点
     * @param p2 点
     * @return 距离
     */
    public static double getDis(Node p1, Node p2) {
        double dis = Math.sqrt(Math.abs(p1.x - p2.x) * Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y) * Math.abs(p1.y - p2.y));
        return dis;
    }

    /**
     * 笛卡尔积
     *
     */
    public static class Descartes {
        public  static void descartes(List<List<Path>> dimvalue, List<List<Path>> result, int layer, List<Path> curList) {
//          最后一层之前的层数
            if (layer < dimvalue.size() - 1) {
//            得到每一层总元素的长度，如果当前层没有元素则移动到下一层
                if (dimvalue.get(layer).size() == 0) {

                    descartes(dimvalue, result, layer + 1, curList);
                } else {
//              如果当前层有元素，遍历当前层的元素并且为每个元素新建List来把当前的元素传递给下一层
                    for (int i = 0; i < dimvalue.get(layer).size(); i++) {

                        List<Path> list = new ArrayList<Path>(curList);

                        list.add(dimvalue.get(layer).get(i));

                        descartes(dimvalue, result, layer + 1, list);

                    }

                }
//         到达最后一层
            } else if (layer == dimvalue.size() - 1) {
//              如果最后一层没有任何元素则直接把curLisT放到集合
                if (dimvalue.get(layer).size() == 0) {
                    Collections.reverse(curList);
                    result.add(curList);
                } else {
//              如果最后一层有元素则分别为它们new新的List并且把之前传递得到的放进去
                    for (int i = 0; i < dimvalue.get(layer).size(); i++) {

                        List<Path> list = new ArrayList<Path>(curList);

                        list.add(dimvalue.get(layer).get(i));
                        Collections.reverse(list);
                        result.add(list);

                    }

                }

            }
        }
    }

    public static ArrayList<double[]> read() {
        System.out.println("正在读取vec_matrix.csv");
        String filePath = "vec_matrix.csv";
        ArrayList<double[]> evs = new ArrayList<>();
        try {
            // 创建CSV读对象
            CsvReader csvReader = new CsvReader(filePath);

            // 读表头
            csvReader.readHeaders();
            while (csvReader.readRecord()) {
                // 读一整行
                String[] strings = csvReader.getRawRecord().split(",");
                double[] doubles = new double[strings.length];
                for (int i = 0; i < strings.length; i++) {
                    doubles[i] = Double.valueOf(strings[i]);
                }

                // 读这行的某一列
//                System.out.println(csvReader.get("Link"));
                evs.add(doubles);
            }
//            System.out.println(csvReader.get("读取完毕"));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return evs;
    }

    public static void write(double[][] L_arr){
        System.out.println("正在写Lmatrix.csv");
        String filePath = "Lmatrix.csv";
        createFile(filePath);
        try {
            // 创建CSV写对象
            CsvWriter csvWriter = new CsvWriter(filePath,',', Charset.forName("GBK"));
            //CsvWriter csvWriter = new CsvWriter(filePath);

            // 写表头{2, 1, 0.0, 0.0}, {0, 0.0, 1.0, 0.0},{1, 0, 0,1}
            for (int i = 0; i < L_arr.length; i++) {
                String[] headers = new String[L_arr[0].length];
                for (int j = 0; j < L_arr[0].length; j++) {
                    headers[j] = String.valueOf(L_arr[i][j]);
                }
                csvWriter.writeRecord(headers);
                csvWriter.flush();
            }
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static boolean createFile(String destFileName) {
        Boolean bool = false;
        String filenameTemp = destFileName;//
        File file = new File(filenameTemp);
        try {
            if (!file.exists())
            {
                file.createNewFile();
                bool = true;
                System.out.println("成功创建文件");
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return bool;
    }
}
