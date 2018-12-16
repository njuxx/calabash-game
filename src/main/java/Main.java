import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main类，含有程序的主入口
 * 负责初始化一个Controller，后续内容交给Controller来完成
 * @author Xu Xiang
 */
@DevLog(initialTime = "2018/12/9 12:00", latestUpdateTime = "2018/12/9 12:00", revisionTime = 1)
public class Main extends Application {
    final Controller controller = new Controller();
    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setScene(new Scene(controller.initializeGUI()));
        controller.initializeGame();
        primaryStage.setResizable(false);
        primaryStage.setTitle("葫芦娃大战妖精：空格开始，L读取历史，S保存历史");
        primaryStage.show();
        controller.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
