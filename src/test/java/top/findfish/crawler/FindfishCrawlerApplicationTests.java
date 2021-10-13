package top.findfish.crawler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import top.findfish.crawler.moviefind.ICrawlerCommonService;

import java.lang.reflect.Constructor;

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



    @Test
    void contextLoads() throws Exception {

//         AiDianYing
//        UM_distinctid=17bbfcacc87e2-025931788650c1-110a1945-44c16-17bbfcacc8887a; __51vcke__JScx7wALK4nGB6Bu=3136fde5-5f32-5c7d-b69a-6c6f028a07bc; __51vuft__JScx7wALK4nGB6Bu=1633869449085; __51uvsct__JScx7wALK4nGB6Bu=2; __vtins__JScx7wALK4nGB6Bu=%7B%22sid%22%3A%20%22e0a2a127-c2d8-5208-8233-8a2e3decfe3b%22%2C%20%22vd%22%3A%202%2C%20%22stt%22%3A%201934%2C%20%22dr%22%3A%201934%2C%20%22expires%22%3A%201634127415410%2C%20%22ct%22%3A%201634125615410%7D




        jsoupAiDianyingServiceImpl.saveOrFreshRealMovieUrl("海贼王", "");
    }




}
