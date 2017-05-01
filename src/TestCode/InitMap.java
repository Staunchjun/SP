package TestCode;

import Bean.EdgeSql;
import Bean.NodeSql;
import DataStructure.Graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/22.
 */
public class InitMap {
    public static Graph returnGraph()
    {int count = 0;
    NodeSql nodeSql = null;
    List<NodeSql> nodeSqls = new ArrayList<NodeSql>();
       for(int i=0;i<4;i++)
    {
        for(int k=0;k<4;k++)
        {
            nodeSql = new NodeSql();
            nodeSql.setId(count);
            nodeSql.setX(i);
            nodeSql.setY(k);
            count++;
            nodeSqls.add(nodeSql);
            nodeSql = null;
        }
    }

    List<EdgeSql> edgeSqls = new ArrayList<EdgeSql>();
    EdgeSql edgeSql = null;
    count=0;
        for(int n1=0;n1<=12;n1=n1+4)
    {
        int behind = n1;
        for(int n2=n1+1;n2<=n1+3;n2++)
        {
            edgeSql = new EdgeSql();
            edgeSql.setId(count);
            edgeSql.setNode_id1(behind);
            edgeSql.setNodeid2(n2);
            count++;
            edgeSqls.add(edgeSql);
            edgeSql = null;
            behind++;
        }
    }

        for(int n1=0;n1<=3;n1++)
    {
        int behind = n1;
        for(int n2=n1+4;n2<=15;n2=n2+4)
        {
            edgeSql = new EdgeSql();
            edgeSql.setId(count);
            edgeSql.setNode_id1(behind);
            edgeSql.setNodeid2(n2);
            count++;
            edgeSqls.add(edgeSql);
            edgeSql = null;
            behind = behind+4;
        }

    }

//        for (EdgeSql edgeSql1 :edgeSqls)
//    {
//        System.out.println(edgeSql1.getNode_id1()+"-----"+edgeSql1.getNodeid2());
//    }
//        for (NodeSql nodeSql1 :nodeSqls)
//    {
//        System.out.println(nodeSql1.getId()+":"+nodeSql1.getX()+"--"+nodeSql1.getY());
//    }
    Graph graph = new Graph(edgeSqls,nodeSqls);
    return graph;
    }
}
