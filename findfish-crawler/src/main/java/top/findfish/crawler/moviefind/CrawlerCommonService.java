package top.findfish.crawler.moviefind;

import top.findfish.crawler.sqloperate.model.MovieNameAndUrlModel;

import java.util.List;
import java.util.Set;

/**
 * 项目名: top-findfish-findfish
 * 文件名: CrawlerCommonService
 * 创建者: HS
 * 创建时间:2022/10/30 20:35
 * 描述: TODO
 */
public class CrawlerCommonService implements ICrawlerCommonService{
    @Override
    public Set<String> firstFindUrl(String searchMovieName, String proxyIpAndPort, Boolean useProxy) throws Exception {
        return null;
    }

    @Override
    public List<MovieNameAndUrlModel> getWangPanUrl(String secondUrlLxxh, String proxyIpAndPort, Boolean useProxy) throws Exception {
        return null;
    }

    @Override
    public void saveOrFreshRealMovieUrl(String searchMovieName, String proxyIpAndPort, Boolean useProxy) throws Exception {

    }

    @Override
    public void checkRepeatMovie() {

    }
}
