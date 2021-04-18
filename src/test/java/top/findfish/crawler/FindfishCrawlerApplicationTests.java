package top.findfish.crawler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import top.findfish.crawler.moviefind.ICrawlerCommonService;

@SpringBootTest
class FindfishCrawlerApplicationTests {


//    @Qualifier("jsoupAiDianyingServiceImpl")
//    @Autowired
//    ICrawlerCommonService jsoupAiDianyingServiceImpl;
//
//    @Qualifier("jsoupSumuServiceImpl")
//    @Autowired
//    ICrawlerCommonService jsoupSumuServiceImpl;
//
//    @Qualifier("jsoupUnreadServiceImpl")
//    @Autowired
//    ICrawlerCommonService jsoupUnreadServiceImpl;
//
    @Qualifier("jsoupYouJiangServiceImpl")
    @Autowired
    ICrawlerCommonService jsoupYouJiangServiceImpl;



    @Test
    void contextLoads() throws Exception {
        jsoupYouJiangServiceImpl.saveOrFreshRealMovieUrl("长歌行", "");
    }

}
