import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * 生物体对应图像的枚举类型
 * @author Xu Xiang
 */
public enum CreaturePictures {
    Calabash1("pictures/red.jpg"),
    Calabash2("pictures/orange.jpg"),
    Calabash3("pictures/yellow.jpg"),
    Calabash4("pictures/green.jpg"),
    Calabash5("pictures/cyan.jpg"),
    Calabash6("pictures/blue.jpg"),
    Calabash7("pictures/purple.jpg"),
    GrandFather("pictures/grandfather.jpg"),
    Scorpion("pictures/scorpion.jpg"),
    Snake("pictures/snake.jpg"),
    Underling1("pictures/underling.jpg"),
    Underling2("pictures/underling.jpg"),
    Underling3("pictures/underling.jpg"),
    Underling4("pictures/underling.jpg"),
    Underling5("pictures/underling.jpg"),
    Underling6("pictures/underling.jpg"),
    Undefined("pictures/");
    CreaturePictures(String filePath){
        Image image = new Image(filePath);
        imageView = new ImageView();
        imageView.setImage(image);
        imageView.setFitWidth(40);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
    }
    public ImageView imageView;
}
