/**
 * 葫芦娃颜色信息的枚举类型
 * @author Xu Xiang
 */
public enum CalabashColor{
    RED("红色"),ORANGE("橙色"),YELLOW("黄色"),GREEN("绿色"),CYAN("青色"),BLUE("蓝色"),PURPLE("紫色");
    CalabashColor(String c){
        calabashColor = new String(c);
    }
    private String calabashColor;
}