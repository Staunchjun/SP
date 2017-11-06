package GuideMainCode;

import GuideDataStructure.Node;

import java.util.Comparator;

/**
 * Created by Administrator on 2017/3/22.
 */
class PointComparator  implements Comparator<Node> {
    @Override
    public int compare(Node o1, Node o2) {
        if (o1.F < o2.F) {
            return -1;
        } else if (o1.F == o2.F) {
            return 0;
        } else {
            return 1;
        }
    }
}