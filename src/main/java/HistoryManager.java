import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * 历史管理器类，负责历史的存储、保存和恢复
 * @author Xu Xiang
 */
public class HistoryManager {
    private static final HistoryManager instance = new HistoryManager();
    private ArrayList<ArrayList<String>> historyArray; //每一个生物体的历史对应一个ArrayList<String>

    public HistoryManager(){
        historyArray = new ArrayList<>();
        for(int i=0;i<16;i++)
            historyArray.add(new ArrayList<String>());
    }

    public static HistoryManager getInstance(){
        return instance;
}

    /**
     * 获得指定下标的生物体的历史
     * @param i 生物体在所有生物体序列中的下标
     * @return 该生物体的历史
     */
    public ArrayList<String> getOnesHistory(int i){
        return historyArray.get(i);
    }

    /**
     * 打开历史文件，由javafx获得到用户选择的历史文件后调用该函数进行打开
     * @param file 用户选择的文件
     * @param allGuys 所有生物体的序列
     */
    public void openHistoryFile(File file, ArrayList<Creature> allGuys){
        BufferedReader freader = null;
        try {
            freader = new BufferedReader(new FileReader(file)); //装饰器模式
            Iterator<ArrayList<String>> iterator = historyArray.iterator();
            for(Creature c: allGuys) {
                ArrayList<String> history = iterator.next();
                history.clear();
                history.add(freader.readLine());
                c.setHistory(history);
            }
        }
        catch(Exception e){
            System.out.println("读入历史文件时错误！");
        }
        finally {
            try {
                freader.close();
            }
            catch(Exception e){
                throw new RuntimeException("关闭输入流失败");
            }
        }
    }

    /**
     * 保存历史文件，由javafx获得到用户指定的文件名后调用该函数进行保存。
     * 文本格式保存。
     * @param file 用户指定的文件名
     * @param allGuys 所有生物体的序列
     * @exception RuntimeException 保存遇到问题时抛出
     */
    public void saveHistoryFile(File file,ArrayList<Creature> allGuys) {
        FileWriter fwriter = null;
        try {
            fwriter = new FileWriter(file);
            Iterator<ArrayList<String>> iterator = historyArray.iterator();
            while(iterator.hasNext()){
                for(String s : iterator.next()){
                    fwriter.write(s);
                }
                fwriter.write("\n");
                fwriter.flush();
            }
        } catch (Exception e) {
            throw new RuntimeException("保存历史文件时错误！");
        } finally {
            try {
                fwriter.close();
            } catch (Exception e) {
                throw new RuntimeException("关闭输出流失败！");
            }
        }
    }
}
