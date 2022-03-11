package top.findfish.crawler.learnCode.proxy;

/**
 * TODO
 *
 * @author sun71
 * @version 1.0
 * @date 2021/10/25 21:01
 */
public class Test {

    public static void main(String[] args) {
        Target target = new TargetImpl();
        //返回的是代理对象，实现了Target接口，
        //实际调用方法的时候，是调用TargetProxy的invoke()方法
        Target targetProxy = (Target) TargetProxy.wrap(target);
        targetProxy.execute(" HelloWord ");
    }

}
