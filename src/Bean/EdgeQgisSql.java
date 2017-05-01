package Bean;

import java.util.List;

/**
 * Created by ruan on 16-10-22.
 */
public class EdgeQgisSql {
    /**
     * id : 19
     * sumutility : 12
     * the_geom : 0102000020E610000002000000D9710511586A3D40950BAB5323D340C0CCD0F774D04C3F40950BAB5323D340C0
     * AdjNode : [1,2]
     */

    private int id;
    private int Utility;
    private String the_geom;
    private List<Integer> Adj;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUtility() {
        return Utility;
    }

    public void setUtility(int sumutility) {
        this.Utility = sumutility;
    }

    public String getThe_geom() {
        return the_geom;
    }

    public void setThe_geom(String the_geom) {
        this.the_geom = the_geom;
    }

    public List<Integer> getAdj() {
        return Adj;
    }

    public void setAdj(List<Integer> Adj) {
        this.Adj = Adj;
    }
}
