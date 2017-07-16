package RecommendPath;

import java.util.Random;
import java.util.Scanner;

/**
 * Created by Administrator on 2017/7/16 0016.
 */

public class K_means {
    int[] ypo;// 点集
    int[] pacore = null;// old聚类中心
    int[] pacoren = null;// new聚类中心

    // 初试聚类中心，点集
    public void productpoint(int[][] shopLists) {
        int num = shopLists.length;
        ypo = new point[num];
        // 随机产生点
        for (int i = 0; i < num; i++) {

            float x = (int) (new Random().nextInt(10));
            float y = (int) (new Random().nextInt(10));

            ypo[i] = new point();// 对象创建
            ypo[i].setX(x);
            ypo[i].setY(y);

        }

        // 初始化聚类中心位置
        System.out.print("请输入初始化聚类中心个数（随机产生）：");
        int core = cina.nextInt();
        this.pacore = new point[core];// 存放聚类中心
        this.pacoren = new point[core];

        Random rand = new Random();
        int temp[] = new int[core];
        temp[0] = rand.nextInt(num);
        pacore[0] = new point();
        pacore[0].x = ypo[temp[0]].x;
        pacore[0].y = ypo[temp[0]].y;
        pacore[0].flage=0 ;
        // 避免产生重复的中心
        for (int i = 1; i < core; i++) {
            int flage = 0;
            int thistemp = rand.nextInt(num);
            for (int j = 0; j < i; j++) {
                if (temp[j] == thistemp) {
                    flage = 1;// 有重复
                    break;

                }
            }
            if (flage == 1) {
                i--;
            } else {
                pacore[i] = new point();
                pacore[i].x= ypo[thistemp].x;
                pacore[i].y = ypo[thistemp].y;
                pacore[i].flage = 0;// 0表示聚类中心
            }

        }
        System.out.println("初始聚类中心：");
        for (int i = 0; i < pacore.length; i++) {
            System.out.println(pacore[i].x + " " + pacore[i].y);
        }

    }

    // ///找出每个点属于哪个聚类中心
    public void searchbelong()// 找出每个点属于哪个聚类中心
    {

        for (int i = 0; i < ypo.length; i++) {
            double dist = 999;
            int lable = -1;
            for (int j = 0; j < pacore.length; j++) {

                double distance = distpoint(ypo[i], pacore[j]);
                if (distance < dist) {
                    dist = distance;
                    lable = j;
                    // po[i].flage = j + 1;// 1,2,3......

                }
            }
            ypo[i].flage = lable + 1;

        }

    }

    // 更新聚类中心
    public void calaverage() {

        for (int i = 0; i < pacore.length; i++) {
            System.out.println("以<" + pacore[i].x + "," + pacore[i].y
                    + ">为中心的点：");
            int numc = 0;
            point newcore = new point();
            for (int j = 0; j < ypo.length; j++) {

                if (ypo[j].flage == (i + 1)) {
                    System.out.println(ypo[j].x + "," + ypo[j].y);
                    numc += 1;
                    newcore.x += ypo[j].x;
                    newcore.y += ypo[j].y;

                }
            }
            // 新的聚类中心
            pacoren[i] = new point();
            pacoren[i].x = newcore.x / numc;
            pacoren[i].y = newcore.y / numc;
            pacoren[i].flage = 0;
            System.out.println("新的聚类中心：" + pacoren[i].x + "," + pacoren[i].y);

        }
    }

    public double distpoint(int[] px, int[] py) {

        int length = px.length;
        double sum = 0;
        for (int i = 0; i < length; i++) {
            sum = sum + Math.pow((px[i] - py[i]), 2);

        }
        return Math.sqrt(sum);
    }

    public void change_oldtonew(point[] old, point[] news) {
        for (int i = 0; i < old.length; i++) {
            old[i].x = news[i].x;
            old[i].y = news[i].y;
            old[i].flage = 0;// 表示为聚类中心的标志。
        }
    }

    public void movecore() {
        // this.productpoint();//初始化，样本集，聚类中心，
        this.searchbelong();
        this.calaverage();//
        double movedistance = 0;
        int biao = -1;//标志，聚类中心点的移动是否符合最小距离
        for (int i = 0; i < pacore.length; i++) {
            movedistance = distpoint(pacore[i], pacoren[i]);
            System.out.println("distcore:" + movedistance);//聚类中心的移动距离
            if (movedistance < 0.01) {
                biao = 0;

            } else {

                biao=1;//需要继续迭代，
                break;

            }
        }
        if (biao == 0) {
            System.out.print("迭代完毕！！！！！");
        } else {
            change_oldtonew(pacore, pacoren);
            movecore();
        }

    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        K_means kmean = new K_means();
        kmean.productpoint(null);
        kmean.movecore();
    }

}