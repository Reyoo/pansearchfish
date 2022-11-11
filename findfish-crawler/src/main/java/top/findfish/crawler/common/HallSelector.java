package top.findfish.crawler.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.findfish.crawler.moviefind.ICrawlerCommonService;

import java.util.Map;

/**
 * *************************************************************************
 * <p/>
 *
 * @文件名称: HallSelector.java
 * @包 路 径： top.findfish.crawler.common
 * @类描述:
 * @版本: V1.0
 * @Author：SunQi
 * @创建时间：2022/11/11 11:37
 */
@Component
public class HallSelector {

    @Autowired
    private HallMovieAliasConfig hallMovieAliasConfig;

    @Autowired
    public Map<String, ICrawlerCommonService> hallSelectorMap;

    public ICrawlerCommonService getByName(String hallName) {
        String name = hallMovieAliasConfig.of(hallName);
        if (name == null) {
            return hallSelectorMap.get(HallMovieAliasConfig.DEFAULT_HALL_NAME);
        }
        ICrawlerCommonService strategy = hallSelectorMap.get(name);
        if (strategy == null) {
            return hallSelectorMap.get(HallMovieAliasConfig.DEFAULT_HALL_NAME);
        }
        return strategy;

    }

}
