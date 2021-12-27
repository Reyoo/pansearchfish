package top.findfish.crawler;


import org.junit.jupiter.api.Test;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import top.findfish.crawler.moviefind.ICrawlerCommonService;


@SpringBootTest
class FindfishCrawlerApplicationTests {


    @Qualifier("jsoupUnreadServiceImpl")
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

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Test
    void contextLoads() throws Exception {

//        redisTemplate.opsForValue().set("111111111111111","22222222222222222");
//        System.out.println("-------------------------");
        jsoupAiDianyingServiceImpl.saveOrFreshRealMovieUrl("奥特曼","",false);

//        final DeviceCodeCredential credential = new DeviceCodeCredentialBuilder()
//                .clientId(appid)
//                .challengeConsumer(challenge -> System.out.println(challenge.getMessage()))
//                .build();
//
//        ArrayList<String> scopes = new ArrayList<>();
//        scopes.add("User.Read");
//        scopes.add("MailboxSettings.Read");
//        scopes.add("Calendars.ReadWrite");
//
//        authProvider = new TokenCredentialAuthProvider(scopes, credential);
//
//        // Create default logger to only log errors
//        DefaultLogger logger = new DefaultLogger();
//        logger.setLoggingLevel(LoggerLevel.ERROR);
//
//        // Build a Graph client
//        graphClient = GraphServiceClient.builder()
//                .authenticationProvider(authProvider)
//                .logger(logger)
//                .buildClient();
//
//            URL meUrl = new URL("https://graph.microsoft.com/v1.0/me");
//            System.out.println(authProvider.getAuthorizationTokenAsync(meUrl).get());

////         AiDianYing
////        UM_distinctid=17bbfcacc87e2-025931788650c1-110a1945-44c16-17bbfcacc8887a; __51vcke__JScx7wALK4nGB6Bu=3136fde5-5f32-5c7d-b69a-6c6f028a07bc; __51vuft__JScx7wALK4nGB6Bu=1633869449085; __51uvsct__JScx7wALK4nGB6Bu=2; __vtins__JScx7wALK4nGB6Bu=%7B%22sid%22%3A%20%22e0a2a127-c2d8-5208-8233-8a2e3decfe3b%22%2C%20%22vd%22%3A%202%2C%20%22stt%22%3A%201934%2C%20%22dr%22%3A%201934%2C%20%22expires%22%3A%201634127415410%2C%20%22ct%22%3A%201634125615410%7D
////        jsoupAiDianyingServiceImpl.saveOrFreshRealMovieUrl("海贼王", "");
//
//
//
////        String tenantId = "49c48436-d709-490c-84b6-d5dee12f0c39";
////        String appid = "78f0eff-5a31-4064-8bab-87fe8b399598";
////        String clientSecret = ".hqQ25~e7lW3Icf7RHP3DNwl-APj_-mUJe";
//
//        String username = "swu-ms-admin@office365.swu.edu.cn";
//        String password = "swu-ms@123,.";
//
//        final UsernamePasswordCredential usernamePasswordCredential = new UsernamePasswordCredentialBuilder()
//                .clientId("378f0eff-5a31-4064-8bab-87fe8b399598")
//                .username(username)
//                .password(password)
//                .build();
//
//
//        ArrayList<String> arrayList = new ArrayList<>();
//        arrayList.add("User.Read");
//        arrayList.add("MailboxSettings.Read");
//        arrayList.add("Calendars.ReadWrite");
//        final TokenCredentialAuthProvider tokenCredentialAuthProvider = new TokenCredentialAuthProvider(arrayList, usernamePasswordCredential);
//        GraphServiceClient graphClient = GraphServiceClient.builder().authenticationProvider( tokenCredentialAuthProvider ).buildClient();
//        User user = new User();
//        user.accountEnabled = true;
//        user.displayName = "aaa";
//        user.mailNickname = "Testaaa";
//        user.userPrincipalName = "testaaa@office365.swu.edu.cn";
//        user.userPrincipalName = "AdeleV@contoso.onmicrosoft.com";
//        PasswordProfile passwordProfile = new PasswordProfile();
//        passwordProfile.forceChangePasswordNextSignIn = true;
//        passwordProfile.password = "123qwe!@#";
//        user.passwordProfile = passwordProfile;
//
//        graphClient.users()
//                .buildRequest()
//                .post(user);
    }


}
