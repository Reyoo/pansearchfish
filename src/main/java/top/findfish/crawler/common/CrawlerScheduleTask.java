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
import top.findfish.crawler.sqloperate.model.SystemUserSearchMovieModel;
import top.findfish.crawler.sqloperate.service.ISystemUserSearchMovieService;

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

//    @Qualifier("jsoupYouJiangServiceImpl")
//    private final ICrawlerCommonService jsoupYouJiangServiceImpl;

    private final RedisTemplate redisTemplate;

    @Value("${findfish.crawler.schedule.range}")
    String scheduleRange;

    Set<String> ipAndPorts = null;


//        @Scheduled(cron = "30 26 16 * * ? ")
    @Scheduled(cron = "0 0 0/2 * * ? ")
    private void crawlerMovieTasks() throws InterruptedException {

        Map<String, ICrawlerCommonService> map = new HashMap<>();
        map.put("爱电影", jsoupAiDianyingServiceImpl);
        map.put("社区动力", jsoupSumuServiceImpl);
        map.put("未读", jsoupUnreadServiceImpl);
        map.put("小优", jsoupXiaoyouServiceImpl);

        System.err.println("执行静态定时任务时间: " + LocalDateTime.now());
        LocalDateTime localDateTime = LocalDateTime.now();
        String endTime = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String begin = localDateTime.minusHours(Integer.valueOf(scheduleRange)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.debug("获取用户搜索范围起始时间：{}", begin);
        log.debug("获取用户搜索范围结束时间：{}", endTime);

        //获取到用户查询的关键词实体类
//        List<SystemUserSearchMovieModel> systemUserSearchMovieModelList = systemUserSearchMovieService.listUserSearchMovieBySearchDateRange(begin, endTime);
        List<SystemUserSearchMovieModel> systemUserSearchMovieModelList = systemUserSearchMovieService.listUserSearchMovieBySearchDateRange("2021-12-27 00:00:15", "2021-12-27 23:10:15");
        log.info("查询到 " + systemUserSearchMovieModelList.size() + " 条记录");
        int i = 1;
        ;
        String ipAndPort = null;
        final AtomicInteger[] randomIndex = {new AtomicInteger()};
        this.ipAndPorts = redisTemplate.opsForHash().keys("use_proxy");
        if (CollectionUtil.isNotEmpty(ipAndPorts)) {
            randomIndex[0].set(new Random().nextInt(ipAndPorts.size()));
            ArrayList<String> ipAndPortList =  new ArrayList<>(this.ipAndPorts);
            ipAndPort = ipAndPortList.get(randomIndex[0].get());
        }

        final String[] finalIpAndPort = {ipAndPort};
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

        log.debug("------------------> {} 定时任务完成", localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        log.debug("词条数量为 {}", systemUserSearchMovieModelList.size());

    }


}
