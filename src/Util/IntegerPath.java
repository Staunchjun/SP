package Util;

import GuideDataStructure.Node;
import GuideDataStructure.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2017/3/30.
 */
public class IntegerPath {
    /**
     * 整合路径
     *
     * @param paths 所有路径
     * @param StartNode 开始点
     * @return 所有路径集合
     */
    public static List<Path> IntegerPath (List<List<Path>> paths, Node StartNode)
    {
        List<Path> multiPaths = new ArrayList<Path>();
        double G=0;
        double U=0;
        for (List<Path> pathList:paths)
        {
            Collections.reverse(pathList);
            List<Node> FinalPath = new ArrayList<Node>();
//         把 这些都整合到一条路径上
            int i = pathList.size()-1;
            while(i >= 0)
            {
                Path path2 = pathList.get(i);
                int nodesSize = path2.getNodes().size();
                for (int k = 0;k < nodesSize;k++)
                {
                    FinalPath.add(path2.nodes.get(k));
                }
                FinalPath.remove(FinalPath.size()-1);
                FinalPath.add(StartNode);
                i--;
                G = G+path2.getG();
                U = U+path2.getU();
            }
            Path path = new Path(G,U,FinalPath);
            multiPaths.add(path);
            G=0;
            U=0;
        }
        return multiPaths;
    }
}
