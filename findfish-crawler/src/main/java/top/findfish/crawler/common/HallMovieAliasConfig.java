package top.findfish.crawler.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * *************************************************************************
 * <p/>
 *
 * @文件名称: HallMovieAliasConfig.java
 * @包 路 径： top.findfish.crawler.common
 * @类描述:
 * @版本: V1.0
 * @Author：SunQi
 * @创建时间：2022/11/11 15:34
 */
@Component
@PropertySource("classpath:application.yml")
@ConfigurationProperties(prefix = "movie")
public class HallMovieAliasConfig {

    private HashMap<String, String> aliasMap;

    public static final String DEFAULT_HALL_NAME = "jsoupHallFirstServiceImpl";

    public HashMap<String, String> getAliasMap() {
        return aliasMap;
    }

    public void setAliasMap(HashMap<String, String> aliasMap) {
        this.aliasMap = aliasMap;
    }

    public String of(String entNum) {
        return aliasMap.get(entNum);
    }


}
