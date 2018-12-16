import javafx.animation.*;
import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 控制者类，是整个程序的核心。负责指挥调度除了Main类的其他所有类
 * @author Xu Xiang
 */
public class Controller {
    public Pane pane;                       //for JavaFX
    private Stage primaryStage;            //for JavaFX
    //private Map map;                        //地图类，上面存有生物体当前位置
    private Formation formation;            //阵型类，负责八种阵型的生成
    //private HistoryManager historyManager; //历史管理器类，负责历史的存储、保存和读取
    private ArrayList<Creature> goodGuys = new ArrayList<Creature>(); //存储好人
    private ArrayList<Creature> badGuys = new ArrayList<Creature>();  //存储坏人
    private boolean testFlag;              //标记目前是否为测试，用于避免触发javafx的相关代码
    private boolean inGameFlag;            //标记目前是否进入游戏状态或回放状态
    private boolean playbackFlag;          //标记目前是否为回放状态，用于区别一些和游戏状态不同的内容
    private int goodGuysLeft;               //记录当前好人存活数
    private int badGuysLeft;                //记录当前坏人存活数
    private ExecutorService exec;            //线程池，其中将有所有生物体的线程

    /**
     * 无参构造函数，一般都会通过该函数来构造一个Controller实例
     */
    @DevLog(initialTime = "2018/12/10 23:14", latestUpdateTime = "2018/12/14 21:52", revisionTime = 3)
    Controller(){
        //this.historyManager = new HistoryManager();
        this.testFlag = false;
        this.inGameFlag = false;
        this.playbackFlag = false;
        this.goodGuysLeft = 8;
        this.badGuysLeft = 8;
        this.exec = null;
    }

    /**
     * 有参构造函数，是在通过junit进行测试时才会调用，核心目的是避免在测试时触发javafx相关代码
     * @param testFlag 标记目前是否为测试
     */
    @DevLog(initialTime = "2018/12/10 23:14", latestUpdateTime = "2018/12/10 23:14", revisionTime = 1)
    Controller(boolean testFlag){
        this();
        this.testFlag = testFlag;
    }

    public void setTestFlag(boolean flag){
        testFlag = flag;
    }

    /**
     * 负责设置界面大小，并返回一个Pane的对象
     * @return 一个Pane的对象，交回给Main类，进行setScene
     */
    @DevLog(initialTime = "2018/12/9 12:00", latestUpdateTime = "2018/12/10 23:10", revisionTime = 2)
    public Pane initializeGUI(){
        //设置界面背景
        pane = new Pane();
        pane.setPrefSize(800, 800);
        this.setBackground();
        return pane;
    }

    /**
     * 初始化和游戏本身相关的内容，如地图类实例、阵型类实例、游戏人物等
     */
    @DevLog(initialTime = "2018/12/10 23:10", latestUpdateTime = "2018/12/15 20:57", revisionTime = 3)
    public void initializeGame(){
        //初始化地图
        //map = new Map(20);

        //初始化阵型类
        formation = new Formation(this);

        //初始化游戏人物 goodguys
        ArrayList<CalabashBros> calabashBros = new ArrayList<CalabashBros>();
        for(int i=0;i<7;i++)
            calabashBros.add(new CalabashBros(i));
        GrandFather grandFather = new GrandFather();
        goodGuys.addAll(calabashBros);
        goodGuys.add(grandFather);
        //初始化游戏人物 badguys
        Snake snake = new Snake();
        Scorpion scorpion = new Scorpion();
        ArrayList<Underling> underlings = new ArrayList<Underling>();
        for(int i=0;i<6;i++)
            underlings.add(new Underling(i));
        badGuys.add(scorpion);
        badGuys.addAll(underlings);
        badGuys.add(snake);
        //
        ArrayList<Creature> allGuys = new ArrayList<>(goodGuys);
        allGuys.addAll(badGuys);
        for(int i=0;i<16;i++)
            allGuys.get(i).history = HistoryManager.getInstance().getOnesHistory(i);
        //
        //Creature.myMap = map;
        Creature.myController = this;
        Creature.random = new Random();
    }

    /**
     * 由initializeGUI()调用，可以看作是初始化图形界面的一步。
     * 生成一个Textfield负责获得用户的键盘输入。设置为透明让用户不可见。
     * 设置界面背景，也即游戏背景。
     */
    private void setBackground(){
        TextField textBox = new TextField();
        textBox.setOpacity(0);
        pane.getChildren().add(textBox);
        Image backGroundImage = new Image("file:///../src/pictures/background.png");
        ImageView backGroundImageView = new ImageView();
        backGroundImageView.setImage(backGroundImage);
        backGroundImageView.setFitWidth(800);
        backGroundImageView.setFitHeight(800);
        pane.getChildren().add(backGroundImageView);
    }

    /**
     * Main类的start方法调用该方法。
     * 传入一个PrimaryStage方便在Controller中对图形界面进行操作
     * 在摆出初始阵型后就通过KeyBoardListener()来监听用户输入，进入游戏或回放
     * @param primaryStage
     */
    public void start(Stage primaryStage){
        this.primaryStage = primaryStage;
        setInitialFormation();
        keyBoardListener();
    }

    /**
     * 用户进入游戏时生成一个阵型。
     * 好坏双方的阵型随机决定。
     */
    private void setInitialFormation(){
        Random random = new Random();
        int formationNumber = random.nextInt(8);
        switch(formationNumber){
            case 0:formation.HeYi(goodGuys,8,10,"Left");break;
            case 1:formation.HengE(goodGuys,8,10,"Left");break;
            case 2:formation.YanXing(goodGuys,8,10,"Left");break;
            case 3:formation.ChangShe(goodGuys,8,10,"Left");break;
            case 4:formation.YuLin(goodGuys,8,10,"Left");break;
            case 5:formation.FangYuan(goodGuys,8,10,"Left");break;
            case 6:formation.YanYue(goodGuys,8,10,"Left");break;
            case 7:formation.FengShi(goodGuys,8,10,"Left");break;
        }
        formationNumber = random.nextInt(8);
        switch(formationNumber){
            case 0:formation.HeYi(badGuys,11,10,"Right");break;
            case 1:formation.HengE(badGuys,11,10,"Right");break;
            case 2:formation.YanXing(badGuys,11,10,"Right");break;
            case 3:formation.ChangShe(badGuys,11,10,"Right");break;
            case 4:formation.YuLin(badGuys,11,10,"Right");break;
            case 5:formation.FangYuan(badGuys,11,10,"Right");break;
            case 6:formation.YanYue(badGuys,11,10,"Right");break;
            case 7:formation.FengShi(badGuys,11,10,"Right");break;
        }
    }

    /**
     * 当用户按下空格，调用该函数。
     * 创建线程池，并将生物体（线程）逐个加入到线程池。
     */
    @DevLog(initialTime = "2018/12/11 20:10", latestUpdateTime = "2018/12/12 22:47", revisionTime = 2)
    private void startGame(){
        inGameFlag = true;
        exec = Executors.newCachedThreadPool();
        for(Creature c: getAllGuys()) {
            exec.execute(c);
        }
        exec.shutdown();
    }

    /**
     * 用户要求回放，加载完文件后调用该函数。通过对一些标记的设置，让生物体进入回放状态。
     * Platform.runLater()用于让子线程通过javafx线程更新javafx界面。子线程不能直接更新界面。
     */
    @DevLog(initialTime = "2018/12/11 20:10", latestUpdateTime = "2018/12/15 20:32", revisionTime = 3)
    public void playback(){
        goodGuysLeft = 8;       //重置这些信息是为了在游戏结束状态下选择回放时，恢复到游戏开始时的状态，下同
        badGuysLeft = 8;
        playbackFlag = true;    //标记目前为回放状态
        for(Creature c: getAllGuys()) {
            c.blood = 100;
            c.cycle = 0;
            Platform.runLater(new Runnable() {
                public void run() {
                    ImageView creatureImageView = getImageView(c);
                    creatureImageView.setOpacity(1);
                    creatureImageView.setRotate(0);
                }
            });
        }
        startGame();
    }

    /**
     * 设置阵型时会对每个生物体调用该函数，将每个生物体显示到对应的位置，并安放到地图的对应位置上。
     * 测试时，即testFlag为true时，通过条件判断避免触发javafx相关代码
     * @param creature 安放的生物体
     * @param x 目标位置
     * @param y 同上
     */
    @DevLog(initialTime = "2018/12/9 12:00", latestUpdateTime = "2018/12/11 19:32", revisionTime = 4)
    public void setCreature(Creature creature, int x, int y){ //x和y是地图坐标，不是像素坐标！
        if(!testFlag) {
            ImageView creatureImageView = getImageView(creature);
            creatureImageView.setX(x * 40);
            creatureImageView.setY(y * 40);
            pane.getChildren().add(creatureImageView);
        }
        creature.initialCreatureOnMap(x,y);
    }

    /**
     * 生物体自己移动时，会调用该方法。该方法将指定生物体的图像通过动画移动到指定位置。
     * 同样的，生物体自己的线程不能直接使用javafx框架提供的方法来对界面进行修改，而是通过runlater来实现。
     * @param creature 移动的生物体
     * @param x 移动后的位置
     * @param y 同上
     */
    @DevLog(initialTime = "2018/12/9 12:00", latestUpdateTime = "2018/12/12 21:59", revisionTime = 7)
    public void moveCreature(final Creature creature, final int x, final int y){
        final int oldX = creature.x;
        final int oldY = creature.y;
        if(x==creature.getX()&&y==creature.getY()) //位置其实没有变
            return;
        Platform.runLater(new Runnable() {
            public void run() {
                ImageView creatureImageView = getImageView(creature);
                Timeline timeline = new Timeline();
                timeline.getKeyFrames().addAll(new KeyFrame(Duration.ZERO, new KeyValue(creatureImageView.xProperty(), oldX*40)),
                        new KeyFrame(new Duration(600), new KeyValue(creatureImageView.xProperty(), x*40)),
                        new KeyFrame(Duration.ZERO, new KeyValue(creatureImageView.yProperty(), oldY*40)),
                        new KeyFrame(new Duration(600), new KeyValue(creatureImageView.yProperty(), y*40))
                );
                timeline.play();
                //System.out.println(tempCreature.getName()+" X:"+creatureImageView.getX()+","+creatureImageView.getTranslateX());
                creatureImageView.setX(x*40);
                creatureImageView.setY(y*40);
            }
        });
    }

    /**
     * 当生物体被害时，会调用该方法。该方法将指定生物体的图像通过动画隐去。
     * 生物体的图像本身并没有被从界面上删掉，只是不可见了。
     * 这样做是为了在后续的回放时只需重新设置透明度，就可以继续使用该生物体的图像。
     * @param creature 被害的生物体
     */
    @DevLog(initialTime = "2018/12/11 19:00", latestUpdateTime = "2018/12/15 21:24", revisionTime = 4)
    public void killCreature(final Creature creature){
        Platform.runLater(new Runnable() {
            public void run() {
                ImageView creatureImageView = getImageView(creature);
                Timeline timeline = new Timeline();
                timeline.getKeyFrames().addAll(new KeyFrame(Duration.ZERO, new KeyValue(creatureImageView.rotateProperty(), 0)),
                        new KeyFrame(new Duration(400), new KeyValue(creatureImageView.rotateProperty(), 180)),
                        new KeyFrame(new Duration(0), new KeyValue(creatureImageView.opacityProperty(), 1)),
                        new KeyFrame(new Duration(500), new KeyValue(creatureImageView.opacityProperty(), 0))
                );
                timeline.play();
            }
        });
    }

    /**
     * 不同于killCreature函数，该函数会将指定生物体的图像真正的从界面上删除掉。
     * 该函数只会在部分阵型设置时调用。该函数可以看作与setCreature功能相对。
     * 与setCreature相同，测试时，会通过条件判断避免触发javafx相关代码
     * @param creature 待删除图像的生物体
     */
    @DevLog(initialTime = "2018/12/9 12:00", latestUpdateTime = "2018/12/12 23:26", revisionTime = 5)
    public void removeCreature(Creature creature){
        if(!testFlag) {
            ImageView creatureImageView = getImageView(creature);
            pane.getChildren().remove(creatureImageView);
        }
    }

    /**
     * 用于获得指定生物体的ImageView，便于进行一系列的显示。例如显示、动画、删除一个生物体都需要先获得ImageView。
     * @param creature 目标生物体
     * @return 该生物体的ImageView
     */
    private ImageView getImageView(Creature creature){
        ImageView creatureImageView = null;
        switch(creature.getName()){
            case Calabash1:     creatureImageView = CreaturePictures.Calabash1.imageView;break;
            case Calabash2:     creatureImageView = CreaturePictures.Calabash2.imageView;break;
            case Calabash3:     creatureImageView = CreaturePictures.Calabash3.imageView;break;
            case Calabash4:     creatureImageView = CreaturePictures.Calabash4.imageView;break;
            case Calabash5:     creatureImageView = CreaturePictures.Calabash5.imageView;break;
            case Calabash6:     creatureImageView = CreaturePictures.Calabash6.imageView;break;
            case Calabash7:     creatureImageView = CreaturePictures.Calabash7.imageView;break;
            case GrandFather:   creatureImageView = CreaturePictures.GrandFather.imageView;break;
            case Snake:          creatureImageView = CreaturePictures.Snake.imageView;break;
            case Scorpion:      creatureImageView = CreaturePictures.Scorpion.imageView;break;
            case Underling1:    creatureImageView = CreaturePictures.Underling1.imageView;break;
            case Underling2:     creatureImageView = CreaturePictures.Underling2.imageView;break;
            case Underling3:    creatureImageView = CreaturePictures.Underling3.imageView;break;
            case Underling4:     creatureImageView = CreaturePictures.Underling4.imageView;break;
            case Underling5:    creatureImageView = CreaturePictures.Underling5.imageView;break;
            case Underling6:     creatureImageView = CreaturePictures.Underling6.imageView;break;
            case Undefined:     creatureImageView = CreaturePictures.Undefined.imageView;break;
        }
        return creatureImageView;
    }

    /**
     * 监听键盘输入。只有不在游戏状态（inGameFlag为False）时输入才有效。
     * 使用到了lambda表达式。
     */
    @DevLog(initialTime = "2018/12/9 12:00", latestUpdateTime = "2018/12/11 20:10", revisionTime = 2)
    public void keyBoardListener(){
        pane.setOnKeyReleased((KeyEvent ke) -> {
            if(!inGameFlag) {
                char c = ke.getText().charAt(0);
                switch (c) {
                    case ' ':
                        startGame();
                        break;
                    case 'l':
                        showHistoryFileOpenDialog();
                        break;
                    case 's':
                        showHistoryFileSaveDialog();
                        break;
                    default:
                        System.out.println(c);
                }
            }
        });
    }

    /**
     * 使用javafx提供的FileChooser来实现文件选择框。当用户键盘输入"L"时调用该函数。
     * 指定游戏历史文件扩展名为.cgf。
     */
    private void showHistoryFileOpenDialog(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("打开历史游戏记录");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Calabash Game Files", "*.cgf"));
        File file = fileChooser.showOpenDialog(primaryStage);
        if(file!=null) {
            HistoryManager.getInstance().openHistoryFile(file, getAllGuys());
            playback();
        }
    }

    /**
     * 使用javafx提供的FileChooser来实现文件保存框。当用户键盘输入"S"时调用该函数。
     * 指定游戏历史文件扩展名为.cgf。
     */
    @DevLog(initialTime = "2018/12/15 19:46", latestUpdateTime = "2018/12/15 19:46", revisionTime = 1)
    private void showHistoryFileSaveDialog(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("打开历史游戏记录");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Calabash Game Files", "*.cgf"));
        File file = fileChooser.showSaveDialog(primaryStage);
        if(file!=null)
            HistoryManager.getInstance().saveHistoryFile(file, getAllGuys());
    }

    /**
     * 在测试时会调用，用于获得葫芦娃一方的生物体。
     * @return 葫芦娃一方的生物体ArrayList
     */
    @DevLog(initialTime = "2018/12/10 22:01", latestUpdateTime = "2018/12/10 22:01", revisionTime = 1)
    public ArrayList<Creature> getGoodGuys(){
        return goodGuys;
    }

    /**
     * 在测试时会调用，用于获得蛇精一方的生物体。
     * @return 蛇精一方的生物体ArrayList
     */
    public ArrayList<Creature> getBadGuys(){
        return badGuys;
    }

    /**
     * 用于获得全部生物体
     * @return 全部生物体的ArrayList
     */
    public ArrayList<Creature> getAllGuys(){
        ArrayList<Creature> allGuys = new ArrayList<>(goodGuys);
        allGuys.addAll(badGuys);
        return allGuys;
    }

    /**
     * 由生物体的线程在意识到自己被害时调用。葫芦娃一方生物体数量减一。
     * 添加临界区是为了防止两个被害生物体同时对goodGuysLeft操作进而造成数量错误。
     * 当一方的生物体全部被害时，游戏结束。对当前仍存活的生物体的血量清零，进而让生物体线程退出循环，结束运行。
     */
    public void reduceAGoodGuy(){
        synchronized (this) {
            if (goodGuysLeft > 0)
                goodGuysLeft--;
            System.out.println("Good left:" + goodGuysLeft);
        }
        if(goodGuysLeft==0 && (!playbackFlag || badGuysLeft==0)) {
            inGameFlag=false;
            for(Creature c: badGuys)
                c.blood=0;
        }
    }

    /**
     * 由生物体的线程在意识到自己被害时调用。蛇精一方生物体数量减一。
     * 添加临界区是为了防止两个被害生物体同时对badGuysLeft操作进而造成数量错误。
     * 当一方的生物体全部被害时，游戏结束。对当前仍存活的生物体的血量清零，进而让生物体线程退出循环，结束运行。
     */
    public void reduceABadGuy(){
        synchronized (this) {
            if (badGuysLeft > 0)
                badGuysLeft--;
            System.out.println("Bad left:" + badGuysLeft);
        }
        if(badGuysLeft==0 && (!playbackFlag || goodGuysLeft==0)) {
            inGameFlag=false;
            for(Creature c: goodGuys)
                c.blood=0;
        }
    }

    /**
     * 生物体会调用该函数来确定当前是否处于游戏状态。
     * @return 当前是否处于游戏状态的布尔变量
     */
    public boolean getGameStatus(){
        return inGameFlag;
    }

    /**
     * 生物体会调用该函数来确定当前是否处于回放状态。
     * @return 当前是否处于回放状态的布尔变量
     */
    public boolean getPlayBackStatus(){
        return playbackFlag;
    }
}
