/**
 * 地图类，生成一个二维数组。
 * 当某个位置有生物体时，指向该生物体；否则指向null。
 * @author Xu Xiang
 */
public class Map {
    private static final Map instance = new Map(20);
    private Creature[][] map;

    public static Map getInstance(){
        return instance;
    }

    Map(int size){
        map = new Creature[size][size];
    }
    @DevLog(initialTime = "2018/12/9 12:00", latestUpdateTime = "2018/12/11 19:19", revisionTime = 2)
    public void setCreature(Creature creature, int x, int y){
        map[y][x] = creature;
    }
    @DevLog(initialTime = "2018/12/9 12:00", latestUpdateTime = "2018/12/11 19:19", revisionTime = 2)
    public void moveCreature(Creature creature, int x, int y){
        if(creature.getX()==x && creature.getY()==y)
            return;
        map[creature.getY()][creature.getX()] = null;
        map[y][x]=creature;
    }
    @DevLog(initialTime = "2018/12/9 12:00", latestUpdateTime = "2018/12/11 19:19", revisionTime = 2)
    public void removeCreature(int x, int y){
        map[y][x] = null;
    }
    @DevLog(initialTime = "2018/12/12 22:51", latestUpdateTime ="2018/12/12 22:51", revisionTime = 1)
    public Creature getBlockCreature(int x, int y){
        return map[y][x];
    }
}
