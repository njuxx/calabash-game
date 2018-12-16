import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;

/**
 * 对“衡轭”阵型测试。
 * @author Xu Xiang
 */
public class HengETest {
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
     * 测试点位：[8,10]
     * 预期结果：正常布阵，位置见leftHengeAnswers
     */
    @Test
    public void testHengEOnLeft(){
        ArrayList<Creature> creaturesForTest = controllerForTest.getGoodGuys();
        formationForTest.HengE(creaturesForTest, 8, 10, "Left");
        int[][] leftHengeAnswers = new int[][]{
                {8,10},{7,9},{6,10},{5,9},{4,10},{3,9},{2,10},{1,9}
        };
        for(int i=0;i<8;i++) {
            assertEquals(leftHengeAnswers[i][0], creaturesForTest.get(i).getX());
            assertEquals(leftHengeAnswers[i][1], creaturesForTest.get(i).getY());
        }
    }

    /**
     * 测试方位：右
     * 测试点位：[11,13]
     * 预期结果：正常布阵，位置见rightHengeAnswers
     */
    @Test
    public void testHengEOnRight(){
        ArrayList<Creature> creaturesForTest = controllerForTest.getBadGuys();
        formationForTest.HengE(creaturesForTest, 11, 13, "Right");
        int[][] rightHengeAnswers = new int[][]{
                {11,12},{12,13},{13,12},{14,13},{15,12},{16,13},{17,12},{18,13}
        };
        for(int i=0;i<8;i++) {
            assertEquals(rightHengeAnswers[i][0], creaturesForTest.get(i).getX());
            assertEquals(rightHengeAnswers[i][1], creaturesForTest.get(i).getY());
        }
    }

    //没什么用
    @After
    public void finishTest(){
        controllerForTest = null;
        formationForTest = null;
    }
}
