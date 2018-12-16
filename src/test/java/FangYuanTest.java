import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;

/**
 * 对“方圆”阵型测试。
 * @author Xu Xiang
 */
public class FangYuanTest {
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
     * 测试点位：[9,7]
     * 预期结果：正常布阵，位置见leftFangyuanAnswers
     */
    @Test
    public void testFangYuanOnLeft(){
        ArrayList<Creature> creaturesForTest = controllerForTest.getGoodGuys();
        formationForTest.FangYuan(creaturesForTest, 9, 7, "Left");
        int[][] leftFangyuanAnswers = new int[][]{
                {9,7},{8,6},{8,8},{7,5},{7,9},{6,6},{6,8},{5,7}
        };
        for(int i=0;i<8;i++) {
            assertEquals(leftFangyuanAnswers[i][0], creaturesForTest.get(i).getX());
            assertEquals(leftFangyuanAnswers[i][1], creaturesForTest.get(i).getY());
        }
    }
}
