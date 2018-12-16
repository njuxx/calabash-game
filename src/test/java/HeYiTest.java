import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;

/**
 * 对“鹤翼”阵型测试。
 * @author Xu Xiang
 */
@DevLog(initialTime = "2018/12/10 21:45", latestUpdateTime = "2018/12/15 22:33", revisionTime = 2)
public class HeYiTest {
    private static Controller controllerForTest;
    private static Formation formationForTest;
    @BeforeClass
    public static void initialControllerForTest(){
        controllerForTest = new Controller(true);
        controllerForTest.initializeGame();
        formationForTest = new Formation(controllerForTest);
    }

    /**
     * 测试方位：左
     * 测试点位：[8,10]
     * 预期结果：正常布阵，位置见leftHeyiAnswers
     */
    @Test
    public void testHeYiOnLeft(){
        ArrayList<Creature> creaturesForTest = controllerForTest.getGoodGuys();
        formationForTest.HeYi(creaturesForTest, 8, 10, "Left");
        int[][] leftHeyiAnswers = new int[][]{
                {8,10},{7,9},{7,11},{6,8},{6,12},{5,7},{5,13},{4,6}
        };
        for(int i=0;i<8;i++) {
            assertEquals(leftHeyiAnswers[i][0], creaturesForTest.get(i).getX());
            assertEquals(leftHeyiAnswers[i][1], creaturesForTest.get(i).getY());
        }
    }

    /**
     * 测试方位：右
     * 测试点位：[11,10]
     * 预期结果：正常布阵，位置见rightHeyiAnswers
     */
    @Test
    public void testHeYiOnRight(){
        ArrayList<Creature> creaturesForTest = controllerForTest.getBadGuys();
        formationForTest.HeYi(creaturesForTest, 11, 10, "Right");
        int[][] rightHeyiAnswers = new int[][]{
                {11,10},{12,9},{12,11},{13,8},{13,12},{14,7},{14,13},{15,6}
        };
        for(int i=0;i<8;i++) {
            assertEquals(rightHeyiAnswers[i][0], creaturesForTest.get(i).getX());
            assertEquals(rightHeyiAnswers[i][1], creaturesForTest.get(i).getY());
        }
    }

    //其实没什么用
    @AfterClass
    public static void finishTest(){
        controllerForTest = null;
        formationForTest = null;
    }
}