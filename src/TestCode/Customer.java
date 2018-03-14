package TestCode;

import RecommendPath.Product;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/9.
 */
public class Customer {
    private int id;
    private Map<String,List<Product>> history;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<String, List<Product>> getHistory() {
        return history;
    }

    public void setHistory(Map<String, List<Product>> history) {
        this.history = history;
    }

}