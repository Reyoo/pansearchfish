package top.findfish.crawler.common;


import cn.hutool.core.util.StrUtil;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

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
    private final RedisTemplate redisTemplate;


    @Value("${findfish.crawler.schedule.range}")
    String scheduleRange;

    Set<String> ipAndPorts = null;

    /**
     * 爱电影定时任务
     */
    //3.添加定时任务  双数小时  2，4，6，8，10...
    @Scheduled(cron = "0 30 1,3,5,7,9,11,13,15,17,19,21,23 * * ? ")
//    @Scheduled(cron = "0 30 19 * * ? ")

    //或直接指定时间间隔，例如：5秒
//    @Scheduled(fixedRate=5000)
    private void crawlerMovieTasks() throws InterruptedException {
        System.err.println("执行静态定时任务时间: " + LocalDateTime.now());
        LocalDateTime localDateTime = LocalDateTime.now();
        String endTime = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String begin = localDateTime.minusHours(Integer.valueOf(scheduleRange)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info("获取用户搜索范围起始时间：{}", begin);
        log.info("获取用户搜索范围结束时间：{}", endTime);

        //获取到用户查询的关键词实体类
        List<SystemUserSearchMovieModel> systemUserSearchMovieModelList = systemUserSearchMovieService.listUserSearchMovieBySearchDateRange(begin, endTime);
//        List<SystemUserSearchMovieModel> systemUserSearchMovieModelList = systemUserSearchMovieService.listUserSearchMovieBySearchDateRange("2021-04-18 12:15:15","2021-04-18 20:50:15");
        log.info("查询到 " + systemUserSearchMovieModelList.size() + " 条记录");
        int i = 1;

        String movieName = null;
        String ipAndPort = null;
        int randomIndex = 0;
        //执行爬虫
        for (SystemUserSearchMovieModel systemUserSearchMovieModel : systemUserSearchMovieModelList) {
            movieName=systemUserSearchMovieModel.getSearchName();
            List<String> ipAndPortList=new ArrayList();
            if(StrUtil.isNotBlank(movieName)){
                this.ipAndPorts = redisTemplate.opsForHash().keys("use_proxy");
                if(ipAndPorts!= null && ipAndPorts.size()>0){
                    randomIndex = new Random().nextInt(ipAndPorts.size());
                    ipAndPortList=new ArrayList<>(this.ipAndPorts);
                    ipAndPort = ipAndPortList.get(randomIndex);
                }else {
                    continue;
                }
                try {
                    jsoupAiDianyingServiceImpl.saveOrFreshRealMovieUrl(movieName, ipAndPort);
                    jsoupYouJiangServiceImpl.saveOrFreshRealMovieUrl(movieName,ipAndPort);
                    jsoupSumuServiceImpl.saveOrFreshRealMovieUrl(movieName, ipAndPort);
                    jsoupUnreadServiceImpl.saveOrFreshRealMovieUrl(movieName, ipAndPort);
                    jsoupXiaoyouServiceImpl.saveOrFreshRealMovieUrl(movieName, ipAndPort);
                    log.info("第 {} 次 查询", i++);
                }catch (Exception e){
                    log.error(e.getMessage());
                    this.ipAndPorts = redisTemplate.opsForHash().keys("use_proxy");
                    continue;
                }
            }
        }


        log.info("------------------> {} 定时任务完成", localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        log.info("词条数量为 {}", systemUserSearchMovieModelList.size());

    }


}
