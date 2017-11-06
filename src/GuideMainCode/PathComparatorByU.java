package GuideMainCode;

import GuideDataStructure.Path;

import java.util.Comparator;

/**
 * Created by Administrator on 2017/3/22.
 */
class PathComparatorByU implements Comparator<Path> {
    @Override
    public int compare(Path o1, Path o2) {
        if (o1.U < o2.U) {
            return -1;
        } else if (o1.U == o2.U) {
            return 0;
        } else {
            return 1;
        }
    }
}