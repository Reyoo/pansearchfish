package top.findfish.crawler.proxy.service;


import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.stereotype.Component;
import top.findfish.crawler.util.FindFishUserAgentUtil;

import java.util.concurrent.TimeUnit;

/**
 * @author: QiSun
 * @date: 2021-01-27
 * @Description:
 */
@Slf4j
@Component
public class PhantomJsProxyCallService {


//    @Value("${phantomjs.deploy.linuxpath}")
//    String deployLinuxPath;
//
//    @Value("${phantomjs.deploy.winpath}")
//    String deployWindowsPath;

    public static boolean isWindowsOS() {
        boolean isWindowsOS = false;
        // 注：这里的system，系统指的是 JRE (runtime)system，不是指 OS
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().indexOf("windows") > -1) {
            isWindowsOS = true;
        }
        return isWindowsOS;
    }


    public  WebDriver  create(String href,String proxyIpAndPort) throws InterruptedException {

        Proxy proxy = new Proxy();
        proxy.setHttpProxy(proxyIpAndPort);
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        //ssl证书支持
        desiredCapabilities.setCapability("acceptSslCerts", false);
        desiredCapabilities.setJavascriptEnabled(true);
        //截屏支持，这里不需要
        desiredCapabilities.setCapability("takesScreenshot", false);
        //css搜索支持
        desiredCapabilities.setCapability("cssSelectorsEnabled", true);
        desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "userAgent",
                FindFishUserAgentUtil.randomUserAgent());
        desiredCapabilities.setCapability(CapabilityType.BROWSER_NAME, "Safari");

        desiredCapabilities.setCapability("platformName", "Android");
        desiredCapabilities.setCapability("platformVersion", "4.4");
        desiredCapabilities.setCapability("deviceName","Android Emulator");


        //js支持
        desiredCapabilities.setJavascriptEnabled(true);

        desiredCapabilities.setCapability(CapabilityType.PROXY, proxy);
        //驱动支持DesiredCapabilities
        //如果是windows系统
        if(isWindowsOS()){
            desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,"E:\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe");
        }else{
            desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,"/home/findfish/phantomjs-2.1.1-linux-x86_64/bin/phantomjs");
        }

        //创建无界面浏览器对象
        WebDriver driver = new PhantomJSDriver(desiredCapabilities);
        //这里注意，把窗口的大小调整为最大，如果不设置可能会出现元素不可用的问题
        driver.manage().window().maximize();
        //获取csdn主页
        driver.get(href);
        // 超过8秒即为超时，会抛出Exception
        driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
        return driver;

    }

}
