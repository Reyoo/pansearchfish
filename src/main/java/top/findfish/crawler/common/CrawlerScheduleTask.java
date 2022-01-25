package top.findfish.crawler.common;


import cn.hutool.core.collection.CollectionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import top.findfish.crawler.moviefind.ICrawlerCommonService;
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
    private void crawlerMovieTasks() throws InterruptedException {

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
//        List<SystemUserSearchMovieModel> systemUserSearchMovieModelList = systemUserSearchMovieService.listUserSearchMovieBySearchDateRange("2022-1-1 00:00:15", "2022-1-10 10:01:16");

//        SystemUserSearchMovieModel movieModel = new SystemUserSearchMovieModel();
//        movieModel.setSearchName("钢铁侠");
//        List<SystemUserSearchMovieModel> systemUserSearchMovieModelList = new ArrayList<>();
//        systemUserSearchMovieModelList.add(movieModel);

        log.info("查询到 " + systemUserSearchMovieModelList.size() + " 条记录");

        String ipAndPort = null;
        final AtomicInteger[] randomIndex = {new AtomicInteger()};
        this.ipAndPorts = redisTemplate.opsForHash().keys("use_proxy");
        if (CollectionUtil.isNotEmpty(ipAndPorts)) {
            randomIndex[0].set(new Random().nextInt(ipAndPorts.size()));
            ArrayList<String> ipAndPortList =  new ArrayList<>(this.ipAndPorts);
            ipAndPort = ipAndPortList.get(randomIndex[0].get());
        }

        final String[] finalIpAndPort = {ipAndPort};
        //经观察，两台服务器分奇偶整小时爬取，资源更新速度适中，为减轻爬取目标服务器压力
        //不建议使用parallelStream()
        systemUserSearchMovieModelList.parallelStream().forEach(systemUserSearchMovieModel -> {
            map.forEach((k, v) -> {
                try {
                    v.saveOrFreshRealMovieUrl(systemUserSearchMovieModel.getSearchName(), finalIpAndPort[0], false);
                } catch (Exception e) {
                    randomIndex[0].set(new Random().nextInt(ipAndPorts.size()));
                    ArrayList<String> ipAndPortList =  new ArrayList<>(this.ipAndPorts);
                    finalIpAndPort[0] = ipAndPortList.get(randomIndex[0].get());
                    e.printStackTrace();
                }
            });

        });

        log.debug("------------------> {} 定时任务完成", localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        log.debug("词条数量为 {}", systemUserSearchMovieModelList.size());

    }


    /**
     * 奇数天 每日12：00后 将更新电视剧前一天的重复数据删除
     */
//    @Scheduled(cron = "0 0 12 1/2 * ? ")  //奇数天中午12点执行  SQ服务器用奇数
//    @Scheduled(cron = "0 0 12 2/2 * ? ")  //偶数天中午12点执行  HS服务器用偶数
    @Scheduled(cron = "0 0 12 1/2 * ? ")
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


}
