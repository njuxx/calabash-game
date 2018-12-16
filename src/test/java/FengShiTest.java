import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;

/**
 * 对“锋矢”阵型测试。
 * @author Xu Xiang
 */
public class FengShiTest {
    private Controller controllerForTest;
    private Formation formationForTest;
    @Before
    public void initialControllerForTest(){
        controllerForTest = new Controller(true);
        controllerForTest.initializeGame();
        formationForTest = new Formation(controllerForTest);
    }

    /**
     * 测试方位：左
     * 测试点位：[9,9]
     * 预期结果：正常布阵，位置见leftFengshiAnswers
     */
    @Test
    public void testFengShiOnLeft(){
        ArrayList<Creature> creaturesForTest = controllerForTest.getGoodGuys();
        formationForTest.FengShi(creaturesForTest, 9, 9, "Left");
        int[][] leftFengshiAnswers = new int[][]{
                {8,9},{7,8},{7,10},{6,9},{5,7},{5,11},{4,9},{2,9}
        };
        for(int i=0;i<8;i++) {
            assertEquals(leftFengshiAnswers[i][0], creaturesForTest.get(i).getX());
            assertEquals(leftFengshiAnswers[i][1], creaturesForTest.get(i).getY());
        }
    }

    /**
     * 测试方位：右
     * 测试点位：[9,9]
     * 预期结果：布阵失败。所有生物体的位置信息应为[-1,-1]
     */
    @Test
    public void testFengShiOnRight(){
        ArrayList<Creature> creaturesForTest = controllerForTest.getBadGuys();
        formationForTest.FengShi(creaturesForTest, 9, 9, "Right");
        //int[][] rightFengshiAnswers = new int[][]{};
        for(int i=0;i<8;i++) {
            assertEquals(-1, creaturesForTest.get(i).getX());
            assertEquals(-1, creaturesForTest.get(i).getY());
        }
    }
}
