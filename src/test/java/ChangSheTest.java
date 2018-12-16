import org.junit.*;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;

/**
 * 对“长蛇”阵型测试。
 * @author Xu Xiang
 */
public class ChangSheTest {
    private Controller controllerForTest;
    private Formation formationForTest;
    @Before
    public void initialControllerForTest(){
        controllerForTest = new Controller(true);
        controllerForTest.initializeGame();
        formationForTest = new Formation(controllerForTest);
    }

    /**
     * 测试方位：右
     * 测试点位：[4,9]
     * 预期结果：正常布阵，位置见rightChangsheAnswers
     */
    @Test
    public void testChangSheOnRight(){
        ArrayList<Creature> creaturesForTest = controllerForTest.getBadGuys();
        formationForTest.ChangShe(creaturesForTest, 4, 9, "Right");
        int[][] rightChangsheAnswers = new int[][]{
                {4,9},{5,9},{6,9},{7,9},{8,9},{9,9},{10,9},{11,9}
        };
        for(int i=0;i<8;i++) {
            assertEquals(rightChangsheAnswers[i][0], creaturesForTest.get(i).getX());
            //y值无需测试
        }
    }
}
