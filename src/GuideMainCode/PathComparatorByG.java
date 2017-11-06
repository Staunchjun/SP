package GuideMainCode;
import GuideDataStructure.Path;

import java.util.Comparator;

/**
 * Created by Administrator on 2017/3/22.
 */
class PathComparatorByG implements Comparator<Path> {
    @Override
    public int compare(Path o1, Path o2) {
        if (o1.G < o2.G) {
            return -1;
        } else if (o1.G == o2.G) {
            return 0;
        } else {
            return 1;
        }
    }
}