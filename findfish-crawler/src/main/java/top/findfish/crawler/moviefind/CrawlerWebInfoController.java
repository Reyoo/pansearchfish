package top.findfish.crawler.moviefind;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ProjectName: pansearch
 * @Package: com.libbytian.pan.crawler.controller
 * @ClassName: CrawlerWebInfoController
 * @Author: sun71
 * @Description: 获取未读影单信息
 * @Date: 2020/12/13 11:26
 * @Version: 1.0
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/initmovie")
@Slf4j
public class CrawlerWebInfoController {


//    @Qualifier("jsoupAiDianyingServiceImpl")
//    private final ICrawlerCommonService jsoupAiDianyingServiceImpl;
//
//    @Qualifier("jsoupSumuServiceImpl")
//    private final ICrawlerCommonService jsoupSumuServiceImpl;
//
//    @Qualifier("jsoupUnreadServiceImpl")
//    private final ICrawlerCommonService jsoupUnreadServiceImpl;

//    @Qualifier("jsoupXiaoYouServiceImpl")
//    private final ICrawlerCommonService jsoupXiaoyouServiceImpl;
//
//    @Qualifier("jsoupYouJiangServiceImpl")
//    private final ICrawlerCommonService jsoupYouJiangServiceImpl;
//
//    @Qualifier("initializeUrl")
//    private final ICrawlerCommonService initializeUrl;
//
//    private final GetProxyService getProxyService;

    private final RedisTemplate redisTemplate;
//
//
//

//
    /**
     * 调用电影PID 入库 触发接口类
     */
//    @RequestMapping(value = "/getall", method = RequestMethod.GET)
//    public AjaxResult loopGetMoviePid() {
//        try {
//            jsoupUnreadServiceImpl.saveOrFreshRealMovieUrl("摩登家庭","",false);
//            return AjaxResult.success();
//        } catch (Exception e) {
//            return AjaxResult.error();
//        }
//    }
//
//
//    //测试
//    @RequestMapping(value = "/textMovieName", method = RequestMethod.GET)
//    public AjaxResult textMovieName(@RequestParam String movieName) {
//        String ipAndPort = null;
//        Set<String> ipAndPorts = redisTemplate.opsForHash().keys("use_proxy");
//        if (ipAndPorts != null && ipAndPorts.size() > 0) {
//            int randomIndex = new Random().nextInt(ipAndPorts.size());
//            List ipAndPortList = new ArrayList<>(ipAndPorts);
//            ipAndPort = (String) ipAndPortList.get(randomIndex);
//            try {
//
//                jsoupYouJiangServiceImpl.saveOrFreshRealMovieUrl(movieName, ipAndPort,false);
//                jsoupXiaoyouServiceImpl.saveOrFreshRealMovieUrl(movieName, ipAndPort,false);
//                jsoupAiDianyingServiceImpl.saveOrFreshRealMovieUrl(movieName, ipAndPort,false);
//                jsoupUnreadServiceImpl.saveOrFreshRealMovieUrl(movieName, ipAndPort,false);
//                jsoupSumuServiceImpl.saveOrFreshRealMovieUrl(movieName, ipAndPort,false);
//
//                jsoupXiaoyouServiceImpl.checkRepeatMovie();
//                jsoupSumuServiceImpl.checkRepeatMovie();
//                jsoupAiDianyingServiceImpl.checkRepeatMovie();
//                jsoupYouJiangServiceImpl.checkRepeatMovie();
//                jsoupUnreadServiceImpl.checkRepeatMovie();
//
//
//
//                return AjaxResult.success();
//            } catch (Exception e) {
//                redisTemplate.opsForHash().delete("use_proxy", ipAndPort);
//
//            }
//        }
//        return AjaxResult.error();
//    }
//
//    //测试初始化URL
//    @RequestMapping(value = "/Initialize", method = RequestMethod.GET)
//    public AjaxResult textMovieName() throws Exception {
//
//        String proxyIpAndPort = "123";
//        String searchMovieName = "123";
//
//        for (int i = 56436; i <= 80000; i++) {
//            searchMovieName = "http://www.yjys2.store"+ "/" + i;
//            System.out.println("第"+i+"次查询爬取 "+searchMovieName);
//            initializeUrl.saveOrFreshRealMovieUrl(searchMovieName,  proxyIpAndPort,false);
//        }
//
//
//        return AjaxResult.success();
//
//    }

}
