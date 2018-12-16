# Java程序设计 Final Project 说明文档

###### 161220153 许翔    2018/12/16

### 设计思路

- 生物体可以抽象为一种类型，葫芦娃、爷爷等生物体都可以继承自该类，对应各自的类型。通过将生物体这个类设定为抽象类，可以要求子类型负责实现不同生物体行为不同的方法，同时又保证了统一的调用方式。

- 葫芦娃的颜色、生物体的名称，都是确定的、有一定取值范围的，因此将这两个属性通过枚举类型来实现。

- 一个阵型类负责八种阵型的实现。

- 一个地图类来模拟游戏地图，保存生物体所在位置的信息。

- 应用JavaFX框架，需要一个继承于Application类的Main类作为入口。显然，需要一个角色来完成GUI、游戏逻辑和生物体线程之间的协调与配合，Controller类即负责这些内容。它类似于一个“中间人”或是“协调者”，完成的任务有设置游戏背景、初始化和游戏本身相关的变量（生物体、地图、阵型类等）、线程池的初始化和线程进池、记录游戏状态、显示与隐藏游戏人物及其相关的动画等。这个类是整个程序的核心。

- 每个生物体有且仅有一张图像对应，将此也通过枚举类型来实现，类型的成员变量就是生物体对应的图像信息（ImageView）。

- 需要记录、保存和读取游戏历史记录，则用一个历史管理器类来实现。

- 对于部分只有单例需求的类，采用设计模式中的单例模式实现。

- 单元测试针对的是阵型生成函数。绝大部分阵型函数都是通过一定的数学运算计算出所在位置，而不是直接指定位置，因此有测试价值。测试方法另行说明，具体可见“单元测试”。

### 类结构

整个工程共有如下的类：

- Main类：继承自Application类，含有程序的主入口

- Controller类：整个程序的核心，完成GUI、游戏逻辑和生物体线程之间的协调与配合等诸多功能

- Creature类：生物体类（抽象类），实现Runnable接口，让每一个生物体是一个线程

  - CalabashBros、GrandFather、Snake、Scorpion、Underling类：继承自Creature类，对应不同的生物体

- CalabashColor类：葫芦娃颜色类（枚举类型）

- CreatureName类：生物体名称类（枚举类型）

- CreaturePictures类：生物体图像类（枚举类型）

- Formation类：阵型类，负责八种阵型的实现

- Map类：地图类（单例），负责记录生物体的位置

- HistoryManager类：历史管理器类（单例），负责存储、保存和读取游戏历史记录

- DevLog类：自定义注解类

### 面向对象思想的体现

#### 封装

封装在整个工程中可以说是处处体现，每一个方法的实现都可以看作是一次封装。这里以生物体的移动为例：

```java
synchronized (myMap) {
        Creature creatureOnTargetBlock = myMap.getBlockCreature(nextX, nextY);
    if (creatureOnTargetBlock == null)
            moveCreatureOnMap(nextX, nextY);
}

public void moveCreatureOnMap(int x, int y){
        try{
            myController.moveCreature(this, x, y);
        myMap.moveCreature(this, x, y);
        this.x = x;
        this.y = y;
    }catch(NullPointerException npe){ ... }
}
```

当生物体判断了自己想要移动的位置没有其他生物体存在后，就会调用moveCreatureOnMap()函数，调用的时候并不关心其中实现的细节，这个函数完成的就是**封装**。实际上，这个函数要完成三个任务：通知控制者去移动生物体在GUI上的显示、通知地图类去更新地图存储的位置信息、修改自己记录的自己的位置。

使用**封装**，便于方法的可伸缩性、复用和维护。用户不必关心方法实现的具体细节；开发者只要不改变方法调用的方式，可以对内部实现进行修改和优化，而不影响用户的使用。

#### 继承与多态

每种生物体都对应一个类。而这些生物体之间有很多的共同点，例如他们都有自己的位置坐标、自己的剩余血量、自己的名称等等；也有共通的方法，例如外界可以“询问”自己的位置、进行杀敌或移动等操作。同时，生物体之间也有不同点，例如只有葫芦娃有颜色等属性。因此，在“生物体”这个类中实现生物体共通的方法、添加共通的成员变量；不同生物**继承**“生物体”这个类，来添加独有的属性或方法。

又考虑到，我们在程序中不会直接实例化“生物体”这个类型；希望一些方法在不同生物体上有统一的调用方法，但是不同生物体的实现不同。为了解决这种需要，将“生物体”这个类定义为抽象类，其中规定具体实现不同的抽象方法，交给子类型实现，这样实现了**多态**。

```java
public abstract class Creature implements Runnable{ //定义一个抽象类
    protected CreatureName name;    //共通的属性
    protected int x;                
    protected int y;
    ... 

    Creature(){ ... }               //共通的方法
    public CreatureName getName(){ return this.name; }
    ...

    public abstract void reduceNumber();   //定义一个抽象方法，具体由子类型实现
}

class CalabashBros extends Creature{ //继承自父类型
    private int number; //子类型独有的一些属性
    private CalabashColor color; 

    CalabashBros(int number){
        super();  //这里也是继承的体现
        this.number = number;
        goodGuy = true;
        ...
    }
    public void reduceNumber(){ ... } //子类型负责实现父类型中的抽象方法
}
```

![Creature的UML图](https://i.loli.net/2018/12/16/5c162da6b84fd.png)

使用**继承**，可以让一些具有共同特点的类型在拥有相同的数据成员和成员函数的同时，还可以定义自己特有的数据成员和成员函数。这样，一方面，它们的共通部分只需要编写一次代码，节省了开发时间和维护成本；另一方面，允许他们定义新的成员，保证了不同类型的特点可以体现。

使用**多态**，可以统一相近类型（即具有相同父类的子类）的方法调用的接口，且不影响体现子类自身的特殊性。

### 设计模式的使用

基于SOLID五个设计原则和CARP、LoD共七个设计原则，在程序中使用了一些设计模式。

#### 装饰器模式

```java
BufferedReader freader = new BufferedReader(new FileReader(file));
...
freader.readLine()
```

通过装饰器模式，可以扩展对象的功能。装饰器模式是进行I/O操作时常用的设计模式。例如，在进行游戏历史记录的读取时，通过对FileReader加一层BufferedReader装饰器，就可以通过调用readLine()方便地对原文件按行读取。

#### 适配器模式

```java
class CalabashBros extends Creature{
         public void reduceNumber(){ myController.reduceAGoodGuy(); }
}
```

使用到的是对象适配器，用来转发调用。前面说到，我们需要生物体实现reduceNumber()操作，但是这一操作实际上是由Controller实现。通过对象适配器，就可以提供一个方便调用的接口用来使用。

#### 组合模式

```java
Pane pane = new Pane();
pane.setOpacity(1);
...
TextField textBox = new TextField();
textBox.setOpacity(0);
pane.getChildren().add(textBox);
```

这段代码是在不同的地方中摘录的，但是可以看到Pane类的实例和被添加到他的子成员的TextField类的实例，都可以调用setOpacity()这一方法，这是组合模式的体现。组合模式可以把一组相似的对象当作一个单一的对象来使用，这样就让我们像处理单个元素那样来处理复杂元素。这种模式在实现GUI时经常使用。

#### 单例模式（饿汉式）

工程中尝试对Map类和HistoryManager类采用了单例模式实现。

```java
public class Map {
        private static final Map instance = new Map(20);
        public static Map getInstance(){ return instance; }
}
```

不同于懒汉式的只有当用户需要实例时才会去创建一个示例，饿汉式会在类加载初始化时就创建好一个静态对象供外部使用。使用单例模式，保证系统中应用该模式的类只有一个实例。

### 各种机制的使用

#### 异常处理

整个工程中许多地方需要进行异常处理，多涉及到文件I/O或是线程等待。

```java
try {
        fwriter = new FileWriter(file);
    ...
    fwriter.write(s);
    fwriter.flush();
} catch (Exception e) {
    throw new RuntimeException("保存历史文件时错误！");
} finally {
    try {
        fwriter.close();
    } catch (Exception e) {
        throw new RuntimeException("关闭输出流失败！");
    }
}
```

在这一组异常处理中，首先针对文件的写入操作添加了异常处理函数；在finally中又对关闭FileWriter时做了异常处理。异常处理都是向上一层调用抛出一个RuntimeException。

#### 泛型与集合类型

工程中也多处使用到了集合类型，以ArrayList为主。

```java
public class Controller {
      ...
      private ArrayList<Creature> goodGuys = new ArrayList<>();
      private ArrayList<Creature> badGuys = new ArrayList<>();
      ...
}
```

Controller中使用ArrayList这种集合类型来分别存储葫芦娃一方的所有生物体和蛇精一方的所有生物体。集合类型是基于泛型来实现的。

#### 注解

工程中使用了自定义注解，用来记录开发信息。

```java
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface DevLog {
    String developerName() default "Xu Xiang";
    String initialTime();
    String latestUpdateTime();
    int revisionTime() default 1;
}

@DevLog(initialTime = "2018/12/11 20:10", latestUpdateTime = "2018/12/12 22:47", revisionTime = 2)
    private void startGame(){ ... }
```

注解中存储了开发者名称、创建时间、最近修改时间和修改次数。使用@Documented，被注解标记的元素在javadoc生成的API文档上也会出现该注解；使用@Inherited，继承注释过的类型的子类也会保持同样注解；使用@Retention(RetentionPolicy.RUNTIME)，注解保留到运行时刻。

#### 输入输出

工程中涉及输入输出的部分，一是获取用户的键盘输入，二是读入和保存游戏历史记录。

获取用户的键盘输入，是通过JavaFX框架的Pane类的setOnKeyReleased()实现的。实际上，这个内容是输入到之前被加入到pane的TextField实例中。不过我们将TextField实例的透明度设置为了0，因此用户不会看到自己输入的内容，只会看到程序根据自己的输入内容有了对应的反应。

```java
public void keyBoardListener(){ pane.setOnKeyReleased( ... ); }
```

读入和保存游戏记录，使用的是FileWriter和FileReader类。特别地，在打开文件时，还基于装饰器模式，使用了BufferedReader，可以方便地按行读取。

而在读入或保存之前，还需要用户指定读取的文件或保存的文件名，这一交互过程使用了JavaFX框架提供的FileChooser类来实现。FileChooser类的showOpenDialog()/showSaveDialog()会显示一个对话框来让用户指定文件，该函数会返回一个File类的用户指定的文件。若用户未指定文件（放弃操作），则会返回null；否则，我们就可以基于这个File类的文件做后续的处理。

```java
private void showHistoryFileSaveDialog(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("打开历史游戏记录");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Calabash Game Files", "*.cgf"));
        File file = fileChooser.showSaveDialog(primaryStage);
    if(file!=null)
        historyManager.saveHistoryFile(file, getAllGuys());
}
```

#### lambda表达式

在获取键盘输入时，setOnKeyReleased()函数需要一个处理函数，但是并不关注是谁声明的、具有什么名字等等。而且，该函数的被调用次数会相当有限。这里，我们可以通过传入一个lambda表达式，来传入一个匿名函数。

```java
pane.setOnKeyReleased((KeyEvent ke) -> {
        if(!inGameFlag) {
                char c = ke.getText().charAt(0);
                switch (c) {
                    case ' ': startGame(); break;
                        case 'l': showHistoryFileOpenDialog(); break;
                case 's': showHistoryFileSaveDialog(); break;
                default: System.out.println(c);
            }
    }
});
```

### 单元测试

一共编写了12个单元测试用例，针对八种不同的阵型实现函数，部分实现了左右两侧的测试，部分实现了一侧的测试。绝大部分实现的是对正常布阵情况下的测试，也实现了对异常布阵情况下的测试（此时待布阵生物体的位置信息会记录\[-1,-1\]）。

编写测试的过程中，除了使用了@Test，也对@Before、@BeforeClass、@After、@AfterClass进行了尝试。

```java
public class ChangSheTest {
    @Before
    public void initialControllerForTest(){ ... }

    @Test
    public void testChangSheOnRight(){
        ArrayList<Creature> creaturesForTest = controllerForTest.getBadGuys();
        formationForTest.ChangShe(creaturesForTest, 4, 9, "Right");
        int[][] rightChangsheAnswers = new int[][]{
                {4,9},{5,9},{6,9},{7,9},{8,9},{9,9},{10,9},{11,9}
        };
        for(int i=0;i<8;i++) {
            assertEquals(rightChangsheAnswers[i][0], creaturesForTest.get(i).getX());
        }
    }
}
```

### 对于游戏规则的设置

- 游戏地图大小为20\*20

- 葫芦娃阵营和蛇精阵营的总生物数量均为8只。其中，葫芦娃阵营含7只葫芦娃和一只老爷爷，蛇精阵营含1只蛇精、1只蝎子精和6只小喽啰。

- 所有生物体的移动范围是上下左右（含斜方向）各1个距离内，按照一定概率决定移动方向，也有一定概率不移动。所有生物体行进速度相同。向各个方向移动的概率设定为下图。

|        | 逆心方向 | (横向方向) | 向心方向 |
| ------ | ---- | ------ | ---- |
| 逆心方向   | 0.01 | 0.03   | 0.06 |
| (纵向方向) | 0.03 | 0.09   | 0.18 |
| 向心方向   | 0.06 | 0.18   | 0.36 |

- 所有生物体的敌人检测范围是上下左右（含斜方向）各1个距离内。在一轮中，生物体可以同时杀死多个敌人。所有生物体的死亡概率相同。

- 历史游戏记录的存储文件为.cgf格式（Calabash Game Files），程序只能存储或打开这种格式的文件。

- 游戏状态下，一方生物均被杀后，另一方的剩余在场生物将停止移动，一直显示在界面上；回放状态下，回放完成后的一段时间之后，另一方的剩余在场生物也会从界面上消失。用户可以继续选择历史记录文件回放。

- 每打开一次程序，可以新运行一次游戏。

### 实现效果

进入游戏，双方会随机布阵：

![游戏布阵界面](https://i.loli.net/2018/12/16/5c166c635dc75.png)

文件选择框：

![文件选择框图](https://i.loli.net/2018/12/16/5c166ff0adddc.png)

### 历史版本更新记录

从第三次作业开始，不断更新、完善，最终实现目前的Final Project。以下列出每一次迭代的主要更新内容。

##### V1

- 各种生物体基于“生物体”类的继承实现：Creature类及其子类型

- 阵型布置方法实现：Formation类

- “造物主”角色和地图的实现：Coordinator类、Map类

##### V2

- 葫芦娃的颜色、生物体的名称修改为枚举类型：CalabashColor类、CreatureName类

- 部分既有类的属性、方法，类间关系调整

##### V3

- 引入泛型：将用来放置加油助威的老爷爷/蛇精的函数修改为泛型函数

- 引入容器：使用ArrayList容器，使用Iterator来遍历

##### V4

- 引入JavaFX框架：Main类

- “造物主”角色随着GUI框架的引入而增加相应功能（方法）：Coordinator类->Controller类

- 初步实现文件I/O和键盘I/O

- 每一个生物体需要对应一幅图像，使用枚举类型来实现：CreaturePictures类
