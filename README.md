## 路径推荐算法概要
导购路线规划需求
基本寻优路径算法A*算法
聚类算法层次聚类Hierarchical Clustering->谱聚类算法Spectral Clustering
    
    最终路径推荐流程，通过聚合用户历史路径数据聚合出K类用户，计算出不同簇类用户对N种商品的兴趣概率P，
    判别新用户属于哪一簇类的用户，推路径荐最短的阈值下兴趣度最高的路径为最优路径推荐给用户。
算法优化：
1. 特征值求解通过幂迭代
2. 谱聚类底层K-means采用Geodesic K-means Clustering