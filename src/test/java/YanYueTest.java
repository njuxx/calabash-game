import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;

/**
 * 对“偃月”阵型测试。
 * @author Xu Xiang
 */
public class YanYueTest {
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
     * 测试点位：[8,11]
     * 预期结果：正常布阵，位置见rightYanyueAnswers
     */
    @Test
    public void testYanYueOnRight(){
        ArrayList<Creature> creaturesForTest = controllerForTest.getBadGuys();
        formationForTest.YanYue(creaturesForTest, 8, 11, "Right");
        int[][] rightYanyueAnswers = new int[][]{
                {9,12},{9,11},{9,10},{11,12},{11,11},{11,10},{12,13},{12,9}
        };
        for(int i=0;i<8;i++) {
            assertEquals(rightYanyueAnswers[i][0], creaturesForTest.get(i).getX());
            assertEquals(rightYanyueAnswers[i][1], creaturesForTest.get(i).getY());
        }
    }
}
