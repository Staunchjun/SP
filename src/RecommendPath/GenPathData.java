package RecommendPath;

import Util.Util;
import com.csvreader.CsvWriter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

public class GenPathData {
    public static void main(String[] args) {
        //产生路径数据，并且把根据假设的用户概率分布产生的用户路径数据存储于Csv文件当中，当然用户概率分布也存储于CSV中
            TestPathGenerate testPathGenerate = new TestPathGenerate(false, 1000, 3, 1360, 25);
//            Map<String, Set<Integer>> history = testPathGenerate.history;
//            Map<Integer, Set<Integer>> newCustomer = testPathGenerate.TNewCustomer;
//            String pathFile = "/Users/ruanwenjun/IdeaProjects/SP/src/csvData/Paths.csv";
//            String shopListFile = "/Users/ruanwenjun/IdeaProjects/SP/src/csvData/newCustomer.csv";
//            Util.createFile(pathFile);
//            try {
//                System.out.println("正在写用户路径Paths.csv");
//                // 创建CSV写对象
//                CsvWriter csvWriterSparsity = new CsvWriter(pathFile, ',', Charset.forName("GBK"));
//                /**
//                 * String 存放的是路径，Set 存放的是购买清单
//                 */
//                for (Map.Entry<String, Set<Integer>> path : history.entrySet()) {
//                    String[] headers = new String[2];
//                    headers[0] = path.getKey();
//                    StringBuilder stringBuilder = new StringBuilder();
//                    for (Integer i : path.getValue()) {
//                        stringBuilder.append(i);
//                        stringBuilder.append(",");
//                    }
//                    headers[1] = stringBuilder.toString();
//                    csvWriterSparsity.writeRecord(headers);
//                    csvWriterSparsity.flush();
//                }
//                csvWriterSparsity.close();
//            System.out.println("正在写新顾客的购买清单newCustomer.csv");
//            // 创建CSV写对象
//            csvWriterSparsity = new CsvWriter(shopListFile, ',', Charset.forName("GBK"));
//            /**
//             * Integer 存放的是顾客ID，Set 存放的是购买清单
//             */
//            for (Map.Entry<Integer,Set<Integer>> list:newCustomer.entrySet()) {
//                String[] headers = new String[2];
//                headers[0] = String.valueOf(list.getKey());
//                StringBuilder stringBuilder = new StringBuilder();
//                for (Integer i:list.getValue()) {
//                    stringBuilder.append(i);
//                    stringBuilder.append(",");
//                }
//                headers[1] = stringBuilder.toString();
//                csvWriterSparsity.writeRecord(headers);
//                csvWriterSparsity.flush();
//            }
//            csvWriterSparsity.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }


}
