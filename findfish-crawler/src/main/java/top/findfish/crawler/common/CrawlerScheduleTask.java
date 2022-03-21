package top.findfish.crawler.common;


import cn.hutool.core.collection.CollectionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import top.findfish.crawler.moviefind.ICrawlerCommonService;
import top.findfish.crawler.moviefind.jsoup.JsoupFindfishUtils;
import top.findfish.crawler.sqloperate.mapper.MovieNameAndUrlMapper;
import top.findfish.crawler.sqloperate.model.SystemUserSearchMovieModel;
import top.findfish.crawler.sqloperate.service.ISystemUserSearchMovieService;
import top.findfish.crawler.util.WebPageConstant;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CrawlerScheduleTask {

    private final ISystemUserSearchMovieService systemUserSearchMovieService;

    @Qualifier("jsoupAiDianyingServiceImpl")
    private final ICrawlerCommonService jsoupAiDianyingServiceImpl;

    @Qualifier("jsoupSumuServiceImpl")
    private final ICrawlerCommonService jsoupSumuServiceImpl;

    @Qualifier("jsoupUnreadServiceImpl")
    private final ICrawlerCommonService jsoupUnreadServiceImpl;

    @Qualifier("jsoupXiaoYouServiceImpl")
    private final ICrawlerCommonService jsoupXiaoyouServiceImpl;

    @Qualifier("jsoupYouJiangServiceImpl")
    private final ICrawlerCommonService jsoupYouJiangServiceImpl;

    @Qualifier("jsoupLiLiServiceImpl")
    private final ICrawlerCommonService jsoupLiLiServiceImpl;

    private final RedisTemplate redisTemplate;

    private final MovieNameAndUrlMapper movieNameAndUrlMapper;

    @Value("${findfish.crawler.schedule.range}")
    String scheduleRange;

    Set<String> ipAndPorts = null;


//    @Scheduled(cron = "0 0 0/2 * * ? ") //偶数整点 2，4，6，8，10   HS服务器用偶数
//    @Scheduled(cron = "0 0 1/2 * * ? ") //奇数整点 1，3，5，7，9  SQ服务器用奇数

    @Scheduled(cron = "0 0 1/2 * * ? ")
    private void crawlerMovieTasks() throws Exception {

        Map<String, ICrawlerCommonService> map = new HashMap<>();
        map.put("小悠", jsoupXiaoyouServiceImpl);
        map.put("莉莉", jsoupLiLiServiceImpl);
        map.put("未读", jsoupUnreadServiceImpl);
        map.put("爱电影", jsoupAiDianyingServiceImpl);
//        map.put("社区动力", jsoupSumuServiceImpl);
//        map.put("悠酱", jsoupYouJiangServiceImpl);


        System.err.println("执行静态定时任务时间: " + LocalDateTime.now());
        LocalDateTime localDateTime = LocalDateTime.now();
        String endTime = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String begin = localDateTime.minusHours(Integer.valueOf(scheduleRange)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.debug("获取用户搜索范围起始时间：{}", begin);
        log.debug("获取用户搜索范围结束时间：{}", endTime);

        //获取到用户查询的关键词实体类
        List<SystemUserSearchMovieModel> systemUserSearchMovieModelList = systemUserSearchMovieService.listUserSearchMovieBySearchDateRange(begin, endTime);
//        List<SystemUserSearchMovieModel> systemUserSearchMovieModelList = systemUserSearchMovieService.listUserSearchMovieBySearchDateRange("2022-1-20 12:00:15", "2022-1-20 17:02:16");

//        SystemUserSearchMovieModel movieModel = new SystemUserSearchMovieModel();
//        movieModel.setSearchName("人世间");
//        List<SystemUserSearchMovieModel> systemUserSearchMovieModelList = new ArrayList<>();
//        systemUserSearchMovieModelList.add(movieModel);

        log.info("查询到 " + systemUserSearchMovieModelList.size() + " 条记录");

        final AtomicInteger[] randomIndex = {new AtomicInteger()};
        String proxyIpAndPort = this.getProxyIpAndPort();
        if (proxyIpAndPort != null){
            final String[] finalIpAndPort = {proxyIpAndPort};
            //经观察，两台服务器分奇偶整小时爬取，资源更新速度适中，为减轻爬取目标服务器压力
            //不建议使用parallelStream()
            systemUserSearchMovieModelList.parallelStream().forEach(systemUserSearchMovieModel -> {
                map.forEach((k, v) -> {
                    try {
                        v.saveOrFreshRealMovieUrl(systemUserSearchMovieModel.getSearchName(), finalIpAndPort[0], true);
                    } catch (Exception e) {
                        randomIndex[0].set(new Random().nextInt(ipAndPorts.size()));
                        ArrayList<String> ipAndPortList =  new ArrayList<>(this.ipAndPorts);
                        finalIpAndPort[0] = ipAndPortList.get(randomIndex[0].get());
                        e.printStackTrace();
                    }
                });

            });

            log.info("------------------> {} 定时任务完成", localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            log.info("词条数量为 {}", systemUserSearchMovieModelList.size());
        }else {
            log.info("======= Redis 中没有IP，请检查获取IP服务 ===========");
        }

    }


    /**
     * 奇数天 每日12：00后 将更新电视剧前一天的重复数据删除
     */
//    @Scheduled(cron = "0 0 12 1/2 * ? ")  //奇数天中午12点，晚上22点 执行  SQ服务器用奇数
//    @Scheduled(cron = "0 0 12 2/2 * ? ")  //偶数天中午12点，晚上22点 执行  HS服务器用偶数
    @Scheduled(cron = "0 0 12,22 1/2 * ? ")
    private void changeSubscribeStatus(){
        System.err.println("执行 删除重复数据 时间: " + LocalDateTime.now());
        movieNameAndUrlMapper.checkRepeatMovie(WebPageConstant.LiLi_TABLENAME);
        movieNameAndUrlMapper.checkRepeatMovie(WebPageConstant.XIAOYOU_TABLENAME);
        movieNameAndUrlMapper.checkRepeatMovie(WebPageConstant.WEIDU_TABLENAME);
        movieNameAndUrlMapper.checkRepeatMovie(WebPageConstant.AIDIANYING_TABLENAME);
        System.err.println("执行 删除重复数据 完毕: " + LocalDateTime.now());

//        定时清理查询片名表一个月前的数据，后续可能会使用
//        delete from user_movie_search where last_searchtime < date_add(curdate(),INTERVAL -1 month)

    }


    public String getProxyIpAndPort(){
        boolean result = false;
        String ipAndPort = null;
        while (!result){
            final AtomicInteger[] randomIndex = {new AtomicInteger()};
            this.ipAndPorts = redisTemplate.opsForHash().keys("use_proxy");
            if (CollectionUtil.isNotEmpty(ipAndPorts)) {

                randomIndex[0].set(new Random().nextInt(ipAndPorts.size()));
                ArrayList<String> ipAndPortList =  new ArrayList<>(this.ipAndPorts);
                int a = randomIndex[0].get();
                ipAndPort = ipAndPortList.get(a);
                //判断IP是否能成功访问莉莉
                String url = "http://a12.66perfect.com/?s=%E5%86%9B%E8%88%B0%E5%B2%9B";
                Document document = JsoupFindfishUtils.getDocument(url, ipAndPort, true);

                //判断IP是否能访问到路径
                //以莉莉为筛选条件
                if (document == null || !document.text().startsWith("A12 Site ")){
                    redisTemplate.opsForHash().delete("use_proxy", ipAndPort);
                    System.out.println("============  删除IP ："+ipAndPort);
                }else {
                    result = true;
                }

            }else {
                result = true;
            }
        }

        return  ipAndPort;

    }


}
