import java.util.ArrayList;
import java.util.Random;

/**
 * “生物体”抽象类，其中有reduceNumber()函数，强制要求子类型实现。
 * 这是因为该函数要根据生物体属于好坏哪一方来决定减少哪一方的数量。
 * @author Xu Xiang
 */
public abstract class Creature implements Runnable{
    protected CreatureName name;    //存储生物体名称
    protected int x;                //记忆自己所在的位置
    protected int y;                //同上
    protected boolean goodGuy;     //标记自己是好人还是坏人
    protected int blood;            //存储自己的血量，在本次实现中退化为0和100两个状态
    protected int cycle;            //标记自己目前的循环数，用于判断当前应该是杀敌还是移动
    protected ArrayList<String> history;//指向HistoryManager中自己对应的历史
    protected int historyPointer;  //读取自己的历史时的指针

    //static Map myMap;                   //指向地图，为所有生物体共享，由Controller负责初始化
    static Controller myController;     //指向Controller，为所有生物体共享，由Controller负责初始化
    static Random random;               //随机数生成器，为所有生物体共享，由Controller负责初始化

    @DevLog(initialTime = "2018/12/9 12:00", latestUpdateTime = "2018/12/15 21:31", revisionTime = 3)
    Creature(){
        this.name = CreatureName.Undefined;
        x=-1;
        y=-1;
        blood = 100;
        cycle = 0;
    }
    Creature(CreatureName cn){
        this();
        this.name = cn;
    }
    public CreatureName getName(){
        return this.name;
    }
    public int getX(){
        return this.x;
    }
    public int getY(){
        return this.y;
    }

    /**
     * 用于在进入游戏显示布阵时，记录自己的第一个位置并保存到历史。
     * 由Controller的setCreature()调用
     * @param x 初始化生物体的位置的横坐标
     * @param y 初始化生物体的位置的纵坐标
     */
    @DevLog(initialTime = "2018/12/11 19:30", latestUpdateTime = "2018/12/11 19:54", revisionTime = 3)
    public void initialCreatureOnMap(int x, int y){
        storeAction('M', x, y);
        Map.getInstance().setCreature(this,x,y);
        this.x = x;
        this.y = y;
    }

    /**
     * 用于游戏过程中自己移动时，分别在地图上修改自己的位置和要求javafx框架刷新自己的位置
     * 由自己的run()调用
     * @param x
     * @param y
     */
    @DevLog(initialTime = "2018/12/11 19:34", latestUpdateTime = "2018/12/11 19:53", revisionTime = 2)
    public void moveCreatureOnMap(int x, int y){
        try{
            myController.moveCreature(this, x, y);
            Map.getInstance().moveCreature(this, x, y);
            this.x = x;
            this.y = y;
        }catch(NullPointerException npe){
            System.out.println("生物体所在地图或控制者未进行初始化！");
            npe.printStackTrace();
        }
    }

    /**
     * 用于游戏过程中自己死亡时，分别在地图上“抹去”自己的位置和要求javafx框架“抹去”自己
     * 由自己的run()调用
     */
    @DevLog(initialTime = "2018/12/11 19:36", latestUpdateTime = "2018/12/11 19:53", revisionTime = 2)
    public void removeCreatureOnMap(){
        try{
            Map.getInstance().removeCreature(this.x,this.y);
            this.reduceNumber();
            myController.killCreature(this);
        }catch(NullPointerException npe){
            System.out.println("生物体所在地图或控制者未进行初始化！");
            npe.printStackTrace();
        }
    }

    /**
     * Runnable要求实现的方法。线程运行时的主要函数。
     * 区分两种状态，一种是回放状态，一种是游戏状态，通过Controller的getPlayBackStatus()来分辨。
     * cycle用于区分自己应该是杀敌还是移动
     * 其中涉及到三个临界区：
     * 1.在杀敌时需要获得目标杀害对象的锁，以避免两个生物体同时杀同一个对象
     * 2.移动时需要获得地图的锁，以避免两个生物体同时移动到同一位置
     * 3.通过对生物体自己的monitor，调用wait()来阻塞当前线程，直到下一个行动时间或有其他生物杀害自己而被唤醒(notify())
     */
    @DevLog(initialTime = "2018/12/11 20:00", latestUpdateTime = "2018/12/14 20:18", revisionTime = 4)
    public void run(){
        try {
            while(blood>0) {
                    cycle++;
                if(myController.getPlayBackStatus()){
                    if(cycle%2==0 && getFirstLetterOfAction()=='K'){
                        blood=0;
                        break;
                    }
                    else if(cycle%2==1 && getFirstLetterOfAction()=='M'){
                        String action = getAction();
                        int nextX = Integer.parseInt(action.substring(1,3));
                        int nextY = Integer.parseInt(action.substring(3,5));
                        moveCreatureOnMap(nextX, nextY);
                    }
                }else {
                    if (cycle % 2 == 0) {
                        //杀周围
                        for (int i = x - 1; i <= x + 1; i++) {
                            for (int j = y - 1; j < y + 1; j++) {
                                if (i >= 0 && i < 20 && j >= 0 && j < 20) {
                                    Creature creatureOnTargetBlock = Map.getInstance().getBlockCreature(i, j);
                                    if (creatureOnTargetBlock != null) {
                                        synchronized (creatureOnTargetBlock) {
                                            creatureOnTargetBlock = Map.getInstance().getBlockCreature(i, j);
                                            if (creatureOnTargetBlock != null) {
                                                if (goodGuy != creatureOnTargetBlock.goodGuy) {
                                                    creatureOnTargetBlock.blood = 0;
                                                    creatureOnTargetBlock.notify();
                                                    blood = 100;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        //
                        Random random = new Random();
                        double randomNumber = random.nextDouble();
                        int nextX;
                        int nextY;
                        //决定下一个位置
                        if (this.x < 10) {
                            if (randomNumber < 0.6) nextX = x + 1;
                            else if (randomNumber < 0.9) nextX = x;
                            else nextX = x - 1;
                        } else {
                            if (randomNumber < 0.6) nextX = x - 1;
                            else if (randomNumber < 0.9) nextX = x;
                            else nextX = x + 1;
                        }
                        randomNumber = random.nextDouble();
                        if (this.y < 10) {
                            if (randomNumber < 0.6) nextY = y + 1;
                            else if (randomNumber < 0.9) nextY = y;
                            else nextY = y - 1;
                        } else {
                            if (randomNumber < 0.6) nextY = y - 1;
                            else if (randomNumber < 0.9) nextY = y;
                            else nextY = y + 1;
                        }
                        //走出边界处理
                        if (nextX < 0) nextX += 2;
                        else if (nextX >= 20) nextX -= 2;
                        if (nextY < 0) nextY += 2;
                        else if (nextY >= 20) nextY -= 2;
                        //同一个位置上只能站一个生物体
                        synchronized (Map.getInstance()) {
                            Creature creatureOnTargetBlock = Map.getInstance().getBlockCreature(nextX, nextY);
                            if (creatureOnTargetBlock == null) {
                                moveCreatureOnMap(nextX, nextY);
                            }
                        }
                        storeAction('M', x, y);
                    }
                }
                //对自己加锁，用于wait和notify
                synchronized (this) {
                    if(blood<=0)
                        break;
                    wait(600);
                }
            }
            //血槽为空，“自杀”
            if(myController.getGameStatus()){
                storeAction('K',-1,-1);
                removeCreatureOnMap();
                //此时inGameFlag被置成false
            }
            else {
                System.out.println(name+" 退出。");
                Thread.sleep(2000);
            }
            if(!myController.getPlayBackStatus() && !myController.getGameStatus()){
                storeAction('M', x, y);
                storeAction('M', x, y);
                storeAction('K',-1,-1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(name+"的线程被打断。");
        }
    }

    public abstract void reduceNumber();

    /**
     * 保存游戏记录相关。
     * @param flag flag有两种取值，"M"代表是移动记录，"K"代表是被杀害记录
     * @param x 移动到的位置，仅在移动时有实际意义
     * @param y 同上
     */
    private void storeAction(char flag, int x, int y){
        String stringX = String.valueOf(x);
        if(x<10&&x>=0) //将一位数扩充为两位数，规范存储
            stringX = '0'+stringX;
        String stringY = String.valueOf(y);
        if(y<10&&y>=0)
            stringY = '0'+stringY;
        history.add(flag+stringX+stringY);
    }

    /**
     * 每当回放前，HistoryManager会调用该函数来重置生物体的历史
     * @param history HistoryManager传出的对应该生物体的历史
     */
    public void setHistory(ArrayList<String> history){
        historyPointer=0;
        this.history = history;
    }

    /**
     * 回放时，用于获取一步操作
     * @return 一步操作对应的信息
     */
    private String getAction(){
        String str = history.get(0).substring(historyPointer,historyPointer+5);
        historyPointer += 5;
        return str;
    }

    /**
     * 回放时，用于获取下一步操作的类型（移动或被害）
     * @return 一步操作对应的操作标识
     */
    private char getFirstLetterOfAction(){
        return history.get(0).charAt(historyPointer);
    }
}

class CalabashBros extends Creature{
    private int number; //排行
    private CalabashColor color; //颜色

    CalabashBros(int number){
        super();
        this.number = number;
        switch (number){
            case 0:name = CreatureName.Calabash1; color = CalabashColor.RED; break;
            case 1:name = CreatureName.Calabash2; color = CalabashColor.ORANGE; break;
            case 2:name = CreatureName.Calabash3; color = CalabashColor.YELLOW; break;
            case 3:name = CreatureName.Calabash4; color = CalabashColor.GREEN; break;
            case 4:name = CreatureName.Calabash5; color = CalabashColor.CYAN; break;
            case 5:name = CreatureName.Calabash6; color = CalabashColor.BLUE; break;
            case 6:name = CreatureName.Calabash7; color = CalabashColor.PURPLE; break;
        }
        goodGuy = true;
    }

    public void reduceNumber(){
        myController.reduceAGoodGuy();
    }
}

class GrandFather extends Creature{
    GrandFather(){
        super();
        this.name = CreatureName.GrandFather;
        goodGuy = true;
    }

    public void reduceNumber(){
        myController.reduceAGoodGuy();
    }
}

class Snake extends Creature{
    Snake(){
        super();
        this.name = CreatureName.Snake;
        goodGuy = false;
    }
    public void reduceNumber(){
        myController.reduceABadGuy();
    }
}

class Scorpion extends Creature{
    Scorpion(){
        super();
        this.name = CreatureName.Scorpion;
        goodGuy = false;
    }
    public void reduceNumber(){
        myController.reduceABadGuy();
    }
}

class Underling extends Creature{
    private int number; //排行

    Underling(int number){
        super();
        this.number = number;
        switch (number) {
            case 0:name = CreatureName.Underling1;break;
            case 1:name = CreatureName.Underling2;break;
            case 2:name = CreatureName.Underling3;break;
            case 3:name = CreatureName.Underling4;break;
            case 4:name = CreatureName.Underling5;break;
            case 5:name = CreatureName.Underling6;break;
        }
        goodGuy = false;
    }
    public void reduceNumber(){
        myController.reduceABadGuy();
    }
}
