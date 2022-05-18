package com.libbytian.pan;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.libbytian.pan.findmovie.aidianying.IFindMovieInAiDianYing;
import com.libbytian.pan.findmovie.lili.IFindMovieInLiLi;
import com.libbytian.pan.findmovie.unread.IFindMovieInUnread;
import com.libbytian.pan.findmovie.xiaoyou.IFindMovieInXiaoyou;
import com.libbytian.pan.findmovie.youjiang.IFindMovieInYoujiang;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.service.ISystemTemDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.regex.Pattern;

@SpringBootTest
class SearchpanApplicationTests {


    @Autowired
//    IFindMovieInXiaoyou findMovieInXiaoyou;
    IFindMovieInUnread findMovieUrl;

    @Autowired
    IFindMovieInYoujiang youjiang;

    @Autowired
    IFindMovieInAiDianYing aiDianYing;

    @Autowired
    IFindMovieInLiLi liLi;

    @Autowired
    IFindMovieInXiaoyou xiaoyou;


    @Autowired
    ISystemTemDetailsService iSystemTemDetailsService;

//    public static void main(String[] args) {
//        String appId = "wx61a17a682672e1b1";
//        //正则校验appid
//
//
//        Pattern p = Pattern.compile("^wx(?=.*\\d)(?=.*[a-z])[\\da-z]{16}$");
//        boolean matches = p.matcher(appId).matches();
//        System.out.println(matches);
//    }

    @Test
    void contextLoads() throws Exception {
//        findMovieUrl.findMovieUrl("阿凡达");
//        youjiang.findMovieUrl("阿凡达");
//        aiDianYing.findMovieUrl("阿凡达");
//        liLi.findMovieUrl("阿凡达");
//        xiaoyou.findMovieUrl("阿凡达");


//        Page page = new Page(1,10);
//        IPage<SystemTemDetailsModel> result = iSystemTemDetailsService.findTemDetailsPage(page, "1f193487adbb4ee8923e6bc72cc39e32");

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("08e3deea-b095-419c-9395-571aecf9256e");
        iSystemTemDetailsService.deleteTemplateDetails(arrayList,"张山");
    }


    @Test
    void testBig() throws Exception {
//        findMovieUrl.findMovieUrl("阿凡达");
//        youjiang.findMovieUrl("阿凡达");
//        aiDianYing.findMovieUrl("阿凡达");
//        liLi.findMovieUrl("阿凡达");
//        xiaoyou.findMovieUrl("阿凡达");


//        Page page = new Page(1,10);
//        IPage<SystemTemDetailsModel> result = iSystemTemDetailsService.findTemDetailsPage(page, "1f193487adbb4ee8923e6bc72cc39e32");

        BigDecimal bigDecimal = new BigDecimal("10001");
        BigDecimal bigDecimal1 = new BigDecimal("3000");
        BigDecimal divide = bigDecimal.divide(bigDecimal1, RoundingMode.DOWN);
        BigDecimal divide1 = bigDecimal.divide(bigDecimal1, RoundingMode.UP);
        System.out.println(divide.intValue());
        System.out.println(divide1.intValue());
    }
}




