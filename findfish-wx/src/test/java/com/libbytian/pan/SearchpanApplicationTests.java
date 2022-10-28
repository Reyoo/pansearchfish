package com.libbytian.pan;

import com.libbytian.pan.findmovie.aidianying.IFindMovieInAiDianYing;
import com.libbytian.pan.findmovie.hall.fourth.IFindMovieHallFourth;
import com.libbytian.pan.findmovie.unread.IFindMovieInUnread;
import com.libbytian.pan.findmovie.xiaoyou.IFindMovieInXiaoyou;
import com.libbytian.pan.findmovie.youjiang.IFindMovieInYoujiang;
import com.libbytian.pan.system.service.ISystemTemDetailsService;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.UUID;

@SpringBootTest
class SearchpanApplicationTests {


//    @Autowired
////    IFindMovieInXiaoyou findMovieInXiaoyou;
//    IFindMovieInUnread findMovieUrl;
//
//    @Autowired
//    IFindMovieInYoujiang youjiang;
//
//    @Autowired
//    IFindMovieInAiDianYing aiDianYing;
//
//    @Autowired
//    IFindMovieHallFourth hallSecond;
//
//    @Autowired
//    IFindMovieInXiaoyou xiaoyou;
//
//
//    @Autowired
//    ISystemTemDetailsService iSystemTemDetailsService;

//    public static void main(String[] args) {
//        String appId = "wx61a17a682672e1b1";
//        //正则校验appid
//
//
//        Pattern p = Pattern.compile("^wx(?=.*\\d)(?=.*[a-z])[\\da-z]{16}$");
//        boolean matches = p.matcher(appId).matches();
//        System.out.println(matches);
//    }

    @Autowired
    RedisTemplate redisTemplate;

    @Test
    void contextLoads() throws Exception {

        redisTemplate.opsForValue().set("123123","123123");
        System.out.println("123123");
//        findMovieUrl.findMovieUrl("阿凡达");
//        youjiang.findMovieUrl("阿凡达");
//        aiDianYing.findMovieUrl("阿凡达");
//        liLi.findMovieUrl("阿凡达");
//        xiaoyou.findMovieUrl("阿凡达");


//        Page page = new Page(1,10);
//        IPage<SystemTemDetailsModel> result = iSystemTemDetailsService.findTemDetailsPage(page, "1f193487adbb4ee8923e6bc72cc39e32");

//        ArrayList<String> arrayList = new ArrayList<>();
//        arrayList.add("08e3deea-b095-419c-9395-571aecf9256e");
//        iSystemTemDetailsService.deleteTemplateDetails(arrayList,"张山");
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


    @Test
   void testaaa(){
//        SystemUserToRole systemUserToRole = new SystemUserToRole();
//        systemUserToRole.setRoleId("1");
//        systemUserToRole.setUserId("1");
//        systemUserToRole.setUserToRoleId("1");
//        systemUserToRole.setUserRoleStatus(true);
//        systemUserToRole.setChecked(true);
//        ThreadLocalTest.set(systemUserToRole);

//        for(int i=0;i<=10;i++){
//
//            Thread thread = new Thread(() ->{
//                SystemUserToRole systemUserToRole = new SystemUserToRole();
//                systemUserToRole.setRoleId("1");
//                systemUserToRole.setUserId("1");
//                systemUserToRole.setUserToRoleId("1");
//                systemUserToRole.setUserRoleStatus(true);
//                systemUserToRole.setChecked(true);
//                ThreadLocalTest.set(systemUserToRole);
//                SystemUserToRole s = new SystemUserToRole();
//                s.setRoleId("2");
//                s.setUserId("2");
//                s.setUserToRoleId("2");
//                s.setUserRoleStatus(false);
//                s.setChecked(false);
//                ThreadLocalTest.set(s);
//
//                System.out.println(Thread.currentThread().getName()+"  " +Thread.currentThread().getId() + " "+ThreadLocalTest.get());
//            });
//            thread.start();
//        }

//        System.out.println(generateValue());

//        upgradeCertChannel("0000000000","L31");

        String l2 = "l2";
        String l3 = "l3";
        String l31 = "l31";
        String l1 = "l1";

//        System.out.println(l1.compareTo(l2));
//        System.out.println(l2.compareTo(l2));
//        System.out.println(l3.compareTo(l2));
//
//        System.out.println(l2.compareTo(l1));
//        System.out.println(l2.compareTo(l2));
//        System.out.println(l2.compareTo(l3));

        System.out.println(l3.compareTo(l31));




//        String name = "bjt_13522215221";
//        System.out.println(name.length());
//        System.out.println(name+2);


        System.out.println(stest());

    }

    public static boolean stest(){

        String l2 = "l2";
        String l3 = "l3";
        String l31 = "l31";
        String l1 = "l1";
        System.out.println(l3.compareTo(l31));
        return l3.compareTo(l31) >0;
    }




    public static String upgradeCertChannel(String userCertChannel, String certLevel) {
        if (StringUtils.isBlank(userCertChannel)) {
            userCertChannel = "0000000000";
        }
        StringBuilder sb = new StringBuilder(userCertChannel);
        sb.replace(0, 1, "1");
        if (StringUtils.equals("L31", certLevel)) {
            sb.replace(1, 2, "1");
        } else if (StringUtils.equals("L32", certLevel)) {
            sb.replace(2, 3, "1");
        }
        return sb.toString();
    }



    public static String generateValue() {
        return generateValue(UUID.randomUUID().toString());
    }

    public static String generateValue(String param) {
        String s = UUID.fromString(UUID.nameUUIDFromBytes(param.getBytes()).toString()).toString();
        String replace = s.replace("-", "");
        return "new_".concat(replace);
    }
}




