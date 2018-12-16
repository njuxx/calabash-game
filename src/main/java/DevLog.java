import java.lang.annotation.*;

/**
 * 存储开发信息，分别是开发者名称、创建时间、最近修改时间、修改次数
 * 继承注释过的类型的子类也会保持同样注解
 * 注解会保留到运行时刻
 * @author Xu Xiang
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface DevLog {
    String developerName() default "Xu Xiang";
    String initialTime();
    String latestUpdateTime();
    int revisionTime() default 1;
}
