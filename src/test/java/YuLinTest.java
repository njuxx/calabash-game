import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;

/**
 * 对“鱼鳞”阵型进行测试
 * @author Xu Xiang
 */
public class YuLinTest {
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
     * 测试点位：[12,6]
     * 预期结果：正常布阵，位置见leftYulinAnswers
     */
    @Test
    public void testYulinOnLeft(){
        ArrayList<Creature> creaturesForTest = controllerForTest.getGoodGuys();
        formationForTest.YuLin(creaturesForTest, 12, 6, "Left");
        int[][] leftYulinAnswers = new int[][]{
                {12,6},{8,6},{11,7},{10,4},{10,6},{10,8},{9,3},{9,5}
        };
        for(int i=0;i<8;i++) {
            assertEquals(leftYulinAnswers[i][0], creaturesForTest.get(i).getX());
            assertEquals(leftYulinAnswers[i][1], creaturesForTest.get(i).getY());
        }
    }

    /**
     * 测试方位：右
     * 测试点位：[9,13]
     * 预期结果：正常布阵，位置见rightYulinAnswers
     */
    @Test
    public void testYuLinOnRight(){
        ArrayList<Creature> creaturesForTest = controllerForTest.getBadGuys();
        formationForTest.YuLin(creaturesForTest, 9, 13, "Right");
        int[][] rightYulinAnswers = new int[][]{
                {9,13},{13,13},{10,14},{11,11},{11,13},{11,15},{12,10},{12,12}
        };
        for(int i=0;i<8;i++) {
            assertEquals(rightYulinAnswers[i][0], creaturesForTest.get(i).getX());
            assertEquals(rightYulinAnswers[i][1], creaturesForTest.get(i).getY());
        }
    }
}
