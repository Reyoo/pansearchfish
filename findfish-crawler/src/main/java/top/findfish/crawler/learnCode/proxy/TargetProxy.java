package top.findfish.crawler.learnCode.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * TODO
 *
 * @author sun71
 * @version 1.0
 * @date 2021/10/25 21:01
 */
public class TargetProxy implements InvocationHandler {

    private Object target;
    public TargetProxy(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println(" 拦截前。。。");
        Object result = method.invoke(target, args);
        System.out.println(" 拦截后。。。");
        return result;
    }

    public static Object wrap(Object target) {
        return Proxy.newProxyInstance(target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),new TargetProxy(target));
    }
}
