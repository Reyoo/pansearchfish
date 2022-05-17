package top.findfish.crawler;


//

import org.junit.jupiter.api.Test;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import top.findfish.crawler.moviefind.ICrawlerCommonService;
import top.findfish.crawler.sqloperate.service.ISystemUserSearchMovieService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@SpringBootTest
class FindfishCrawlerApplicationTests {


    @Qualifier("jsoupAiDianyingServiceImpl")
    @Autowired
    ICrawlerCommonService jsoupAiDianyingServiceImpl;
//
//    @Qualifier("jsoupSumuServiceImpl")
//    @Autowired
//    ICrawlerCommonService jsoupSumuServiceImpl;
//
//    @Qualifier("jsoupUnreadServiceImpl")
//    @Autowired
//    ICrawlerCommonService jsoupUnreadServiceImpl;
//
//    @Qualifier("jsoupAiDianyingServiceImpl")
//    @Autowired
//    ICrawlerCommonService jsoupYouJiangServiceImpl;

//    private static GraphServiceClient<Request> graphClient = null;
//    private static TokenCredentialAuthProvider authProvider = null;

    @Qualifier("systemUserSearchMovieServiceImpl")
    @Autowired
    ISystemUserSearchMovieService systemUserSearchMovieService;
    @Autowired
    RedisTemplate redisTemplate;
//
//    @Autowired
//    RedissonClient redissonClient;

    @Test
    void contextLoads() throws Exception {
//        systemUserSearchMovieService.userSearchMovieCountInFindfish("奥特曼大战哥斯拉");

//        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(()->{
//            System.out.println("compute 1");
//            return 1;
//        });
//        CompletableFuture<Integer> future2 = future1.thenApply((p)->{
//            System.out.println("compute 2");
//            return p+10;
//        });
//        System.out.println("result: " + future2.join());

//        CompletableFuture<Integer> future = new CompletableFuture<>();
//        Integer integer = future.get();
//        System.out.println(integer.intValue());


        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            return 10086;
        });
        future1.whenComplete((result, error) -> {
            System.out.println("拨打"+result);
            error.printStackTrace();
        });



        CompletableFuture<List<String>> total = CompletableFuture.supplyAsync(() -> {
            // 第一个任务获取美术课需要带的东西，返回一个list
            List<String> stuff = new ArrayList<>();
            stuff.add("画笔");
            stuff.add("颜料");
            return stuff;
        }).thenCompose(list -> {
            // 向第二个任务传递参数list(上一个任务美术课所需的东西list)
            CompletableFuture<List<String>> insideFuture = CompletableFuture.supplyAsync(() -> {
                List<String> stuff = new ArrayList<>();
                // 第二个任务获取劳技课所需的工具
                stuff.add("剪刀");
                stuff.add("折纸");
                // 合并两个list，获取课程所需所有工具
                List<String> allStuff = Stream.of(list, stuff).flatMap(Collection::stream).collect(Collectors.toList());
                return allStuff;
            });
            return insideFuture;
        });
        System.out.println(total.join().size());





        CompletableFuture.supplyAsync(() -> 1)
                .whenComplete((result, error) -> {
                    System.out.println(result);
                    error.printStackTrace();
                })
                .handle((result, error) -> {
                    error.printStackTrace();
                    return error;
                })
                .thenApply(Object::toString)
                .thenApply(Integer::valueOf)
                .thenAccept((param) -> System.out.println("done"));



//      jsoupAiDianyingServiceImpl.saveOrFreshRealMovieUrl("摩登家庭","",false);
//      https://cloud.tencent.com/developer/article/1338265
//        int i = Integer.SIZE - 3;
        // 左移  等于乘以2的n次方  右移 等于除以2的n次方
//        int i1 = -1 << i;
//        System.out.println(i1);

//        011 | 100  按位或  111  7
//        System.out.println(3 | 4);
//
//        StringBuilder builder = new StringBuilder();
////        builder.append("REDIS_MAP:ACCOUNT_LOCK:").append("[256660d60d9e44c39a54e77994b2314f]");
////
////        redisTemplate.opsForValue().set(builder.toString(),"[256660d60d9e44c39a54e77994b2314f]");
//
//
//        String tenantId = "49c48436-d709-490c-84b6-d5dee12f0c39";
//        String appid = "d18ed23e-9ba2-4909-958c-6ea81d6bd996";
//        String clientSecret = "j-w8yX1X86WV6m7om~uy.d.DLV3WiP.Zhe";
//        final ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
//                .clientId(appid)
//                .authorityHost(AzureAuthorityHosts.AZURE_CHINA)
//                .clientSecret(clientSecret)
//                .tenantId(tenantId)
//                .build();
//        ArrayList<String> scopes = new ArrayList<>();
//        scopes.add("https://microsoftgraph.chinacloudapi.cn/.default");
//
//
//        final TokenCredentialAuthProvider tokenCredentialAuthProvider =
//                new TokenCredentialAuthProvider(scopes, clientSecretCredential);
//         GraphServiceClient graphClient =
//
//                GraphServiceClient
//                        .builder()
//                        .authenticationProvider(tokenCredentialAuthProvider)
//                        .buildClient();
//        graphClient.setServiceRoot("https://microsoftgraph.chinacloudapi.cn/v1.0");
//
//        User user = new User();
//        user.accountEnabled = true;
//        user.displayName = "test1223";
//        user.mailNickname = "test1223";
//        user.userPrincipalName = "test1223@email.swu.edu.cn";
//        user.usageLocation = "CN";
//        PasswordProfile passwordProfile = new PasswordProfile();
//        passwordProfile.forceChangePasswordNextSignIn = true;
//        passwordProfile.password = "123qwe!@#";
//        user.passwordProfile = passwordProfile;
//        System.out.println(graphClient.getServiceRoot());
//        User post = graphClient.users().buildRequest().post(user);
//
//
//
//        LinkedList<AssignedLicense> addLicensesList = new LinkedList<AssignedLicense>();
//        AssignedLicense addLicenses = new AssignedLicense();
//        LinkedList<UUID> disabledPlansList = new LinkedList<UUID>();
////        disabledPlansList.add(UUID.fromString("314c4481-f395-4525-be8b-2ec4bb1e9d91"));
//        addLicenses.disabledPlans = disabledPlansList;
//        addLicenses.skuId = UUID.fromString("314c4481-f395-4525-be8b-2ec4bb1e9d91");
//        addLicensesList.add(addLicenses);
//        LinkedList<UUID> removeLicensesList = new LinkedList<UUID>();
////        removeLicensesList.add(UUID.fromString("bea13e0c-3828-4daa-a392-28af7ff61a0f"));
//        graphClient.setServiceRoot("https://microsoftgraph.chinacloudapi.cn/v1.0");
//        graphClient.users("test1223@email.swu.edu.cn")
//                .assignLicense(UserAssignLicenseParameterSet
//                        .newBuilder()
//                        .withAddLicenses(addLicensesList)
//                        .withRemoveLicenses(removeLicensesList)
//                        .build())
//                .buildRequest()
//                .post();


        /***
         * clientSecret
         */
//        String username = "swu-ms-admin@office365.swu.edu.cn";
//        String password = "swu-ms@123,.";
//        final UsernamePasswordCredential usernamePasswordCredential = new UsernamePasswordCredentialBuilder()
//                .authorityHost(AzureAuthorityHosts.AZURE_CHINA)
//                .clientId("d18ed23e-9ba2-4909-958c-6ea81d6bd996")
//                .username(username)
//                .password(password)
//                .build();
//
//        ArrayList<String> arrayList = new ArrayList<>();
//        arrayList.add("https://login.chinacloudapi.cn/.default");
////        arrayList.add(AzureAuthorityHosts.AZURE_CHINA);
//        final TokenCredentialAuthProvider tokenCredentialAuthProvider = new TokenCredentialAuthProvider(arrayList, usernamePasswordCredential);
//        GraphServiceClient graphClient = GraphServiceClient.builder().authenticationProvider(tokenCredentialAuthProvider).buildClient();
//        User user = new User();
//        user.accountEnabled = true;
//        user.displayName = "aaa";
//        user.mailNickname = "Testaaa";
//        user.userPrincipalName = "testaaa@office365.swu.edu.cn";
////        user.userPrincipalName = "AdeleV@contoso.onmicrosoft.com";
//        PasswordProfile passwordProfile = new PasswordProfile();
//        passwordProfile.forceChangePasswordNextSignIn = true;
//        passwordProfile.password = "123qwe!@#";
//        user.passwordProfile = passwordProfile;
//
//        graphClient.users()
//                .buildRequest()
//                .post(user);


    }


    @Test
    void testLocalTime() throws Exception {

        /**时间在前*/
        LocalDate a = LocalDate.parse("2022/04/10", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        
        LocalDate b = LocalDate.parse("2022/04/20", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
//        Duration between = Duration.between(a, b);
        Period between = Period.between(a, b);
        Period between1 = Period.between(b, a);
        long l = between.getDays();
        System.out.println(l);
        int days = between1.getDays();
        System.out.println(days);

        /**时间在后*/



    }

}


