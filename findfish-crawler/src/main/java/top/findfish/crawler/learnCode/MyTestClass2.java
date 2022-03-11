package top.findfish.crawler.learnCode;

/**
 * TODO
 *
 * @author sun71
 * @version 1.0
 * @date 2021/10/29 15:45
 */
public class MyTestClass2 {

    private static final MyTestClass2 myTestClass2 = new MyTestClass2();

    private static int a = 0;
    private static int b;

    private MyTestClass2(){
        a++;
        b++;
    }



    public static MyTestClass2 getInstance(){
        return myTestClass2;
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }
}