package top.findfish.crawler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import top.findfish.crawler.moviefind.ICrawlerCommonService;

@SpringBootTest
class FindfishCrawlerApplicationTests {

    @Qualifier("jsoupXiaoYouServiceImpl")
    @Autowired
    ICrawlerCommonService jsoupXiaoyouServiceImpl;

    @Test
    void contextLoads() throws Exception {
        jsoupXiaoyouServiceImpl.saveOrFreshRealMovieUrl("司藤", "");
    }

}
