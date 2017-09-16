package RecommendPath;

public class ScDataPoint {
    private ScCluster ScCluster;
    private String data;
    private String dataPointName;
    private double[] tempData;

    public double[] getTempData() {
        return tempData;
    }

    public void setTempData(double[] tempData) {
        this.tempData = tempData;
    }

    public ScDataPoint(String data) {
        this.data = data;
    }

    public ScDataPoint(String data, String dataPointName, double[] tempData) {
        this.data = data;
        this.dataPointName = dataPointName;
        this.tempData = tempData;
    }

    public ScDataPoint(String data, String dataPointName) {
        this.data = data;
        this.dataPointName = dataPointName;
    }

    public ScDataPoint() {

    }

    public String getDataPointName() {
        return dataPointName;
    }

    public ScCluster getScCluster() {
        return ScCluster;
    }

    public void setScCluster(ScCluster ScCluster) {
        this.ScCluster = ScCluster;
    }

    public String getData() {
        return data;
    }
}
