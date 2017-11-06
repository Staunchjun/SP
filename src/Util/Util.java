package Util;

import DataStructure.Node;
import DataStructure.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
}
