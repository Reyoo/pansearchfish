package top.findfish.crawler.learnCode;

/**
 * TODO
 *
 * @author sun71
 * @version 1.0
 * @date 2021/10/21 21:51
 * @Description:
 * 是否 Lazy 初始化：是
 * <p>
 * 是否多线程安全：是
 * <p>
 * 实现难度：较复杂
 * <p>
 * 描述：这种方式采用双锁机制，安全且在多线程情况下能保持高性能。
 * getInstance() 的性能对应用程序很关键。
 */
public class SingletionLearn {

    public volatile static SingletionLearn singletionLearn;

    public SingletionLearn() {
    }


    public static SingletionLearn getInstance() {

        if (singletionLearn == null) {
            synchronized (SingletionLearn.class) {
                if (singletionLearn == null) {
                    singletionLearn = new SingletionLearn();
                }
            }
        }
        return singletionLearn;
    }

}
