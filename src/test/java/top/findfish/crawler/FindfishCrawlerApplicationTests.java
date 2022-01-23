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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.UUID;


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
//
//    @Autowired
//    RedissonClient redissonClient;

    @Test
    void contextLoads() throws Exception {

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
}


