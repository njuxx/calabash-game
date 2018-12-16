import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;

/**
 * 对“雁行”阵型测试。
 * @author Xu Xiang
 */
public class YanXingTest {
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
     * 测试点位：[8,12]
     * 预期结果：正常布阵，位置见leftYanxingAnswers
     */
    @Test
    public void testYanXingOnLeft(){
        ArrayList<Creature> creaturesForTest = controllerForTest.getGoodGuys();
        formationForTest.YanXing(creaturesForTest, 8, 12, "Left");
        int[][] leftYanxingAnswers = new int[][]{
                {8,12},{7,13},{6,14},{5,15},{4,16},{3,17},{2,18},{1,19}
        };
        for(int i=0;i<8;i++) {
            assertEquals(leftYanxingAnswers[i][0], creaturesForTest.get(i).getX());
            assertEquals(leftYanxingAnswers[i][1], creaturesForTest.get(i).getY());
        }
    }
}
