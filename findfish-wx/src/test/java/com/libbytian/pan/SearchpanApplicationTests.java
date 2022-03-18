package com.libbytian.pan;

import cn.hutool.core.collection.CollectionUtil;
import com.libbytian.pan.system.config.GuoWenDesignHttpRequestTool;
import com.libbytian.pan.system.model.SystemWxUserConfigModel;
import com.libbytian.pan.system.service.ISystemWxUserConfigService;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
class SearchpanApplicationTests {


    //	public static void main(String[] args) {
//		String appId = "wx61a17a682672e1b1";
//		//正则校验appid
// 		Pattern p = Pattern.compile("^wx(?=.*\\d)(?=.*[a-z])[\\da-z]{16}$");
//		boolean matches = p.matcher(appId).matches();
//		System.out.println(matches);
//	}
    @Autowired
    ISystemWxUserConfigService systemWxUserConfigService;


    static final String url = "http://sms.cchmi.com/ws/BatchSend2.aspx";
    static final String username = "zongheyonghu";
    static final String passwd = "nasoft123456";

    @Test
    void contextLoads() throws ExecutionException, InterruptedException {
//        List<SystemWxUserConfigModel> configModelList = systemWxUserConfigService.list();
//        if (CollectionUtil.isEmpty(configModelList)) {
//            throw new RuntimeException("大哥，拜托先看下项目首页的说明（readme文件），添加下相关配置，注意别配错了！");
//        }
//        WxMpService service = new WxMpServiceImpl();
//        service.setMultiConfigStorages(configModelList
//                .stream().map(a -> {
//                    WxMpDefaultConfigImpl configStorage;
//                    configStorage = new WxMpDefaultConfigImpl();
//                    configStorage.setAppId(a.getWxAppId());
//                    configStorage.setSecret(a.getWxSecret());
//                    configStorage.setToken(a.getWxToken());fa
//                    configStorage.setAesKey(a.getWxAesKey());
//                    return configStorage;
//                }).collect(Collectors.toConcurrentMap(WxMpDefaultConfigImpl::getAppId, b -> b, (o, n) -> o)));
//        System.out.println("执行完成");

        AtomicReference<Integer> flag = new AtomicReference<>(BigDecimal.ONE.intValue());
        String finalMessage = "您的手机验证码：【" + 666666 + "】 ，5分钟内有效，请勿泄漏给他人。【国家文物局综合行政管理平台】";
        String date = String.valueOf(System.currentTimeMillis());
        String param = "CorpID=".concat(username)
                .concat("&Pwd=").concat(passwd)
//                .concat("&Mobile=").concat("13151157051")
                .concat("&Mobile=").concat("16604358255")
                .concat("&Content=").concat(finalMessage)
                .concat("&SendTime=").concat(date);
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> GuoWenDesignHttpRequestTool.sendPost(url, param, "GB2312"));
        completableFuture.get();
        // 如果执行成功:
        completableFuture.thenAccept((result) -> {
            flag.set(BigDecimal.ZERO.intValue());
            return;
        });
        // 如果执行异常:
        completableFuture.exceptionally((e) -> {
            e.printStackTrace();
            return null;
        });
        flag.get();

    }
}




