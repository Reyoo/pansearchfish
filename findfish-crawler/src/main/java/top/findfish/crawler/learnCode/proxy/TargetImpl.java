package top.findfish.crawler.learnCode.proxy;

/**
 * TODO
 *
 * @author sun71
 * @version 1.0
 * @date 2021/10/25 21:00
 */
public class TargetImpl implements Target {
    @Override
    public String execute(String name) {
        System.out.println("execute() "+ name);
        return name;
    }
}
