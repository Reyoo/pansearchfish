package top.findfish.crawler.sqloperate.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.findfish.crawler.sqloperate.mapper.MovieNameAndUrlMapper;
import top.findfish.crawler.sqloperate.model.MovieNameAndUrlModel;
import top.findfish.crawler.sqloperate.service.IMovieNameAndUrlService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author: QiSun
 * @date: 2020-12-06
 * @Description:
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class MovieNameAndUrlServiceImpl extends ServiceImpl<MovieNameAndUrlMapper, MovieNameAndUrlModel> implements IMovieNameAndUrlService {


    private final MovieNameAndUrlMapper movieNameAndUrlMapper;

    @Override
    public List<MovieNameAndUrlModel> findMovieUrl(String tablename, String movieName,String wangPanUrl) throws Exception {
        return movieNameAndUrlMapper.selectMovieUrlByName(tablename, movieName, wangPanUrl);
    }

    /**
     * 插入更新操作、如果数据库中 不存在 则插入、如果存在 则更新  由于分表、每个爬虫资源影单单独一套 Controller Servcie Mapper
     *
     * @param movieNameAndUrlModels
     * @return
     * @throws Exception
     */
    @Override
    public void addOrUpdateMovieUrls(List<MovieNameAndUrlModel> movieNameAndUrlModels, String tableName) throws Exception {
        if (CollectionUtil.isEmpty(movieNameAndUrlModels)) {
            return;
        }

        movieNameAndUrlModels.parallelStream().forEach(t -> {
                    if (StrUtil.isBlank(t.getMovieName())) {
                        return;
                    }
                    t.setUpdateTime(LocalDateTime.now());
                    if (movieNameAndUrlMapper.selectMovieUrlByName(tableName, t.getMovieName().trim(),t.getWangPanUrl().trim()).size() > 0) {
//                如果查询到数据 则更新
                        movieNameAndUrlMapper.updateUrlMovieUrl(tableName, t);
                        log.info("更新电影列表-->" + t);

                    } else {
                        movieNameAndUrlMapper.insertMovieUrl(tableName, t);
                        log.info("插入电影列表-->" + t);
                    }
                }
        );
    }

    @Override
    public int dropMovieUrl(String tableName, MovieNameAndUrlModel movieNameAndUrlModel) throws Exception {
        return movieNameAndUrlMapper.deleteUrlMovieUrls(tableName, movieNameAndUrlModel);
    }

    @Override
    public void deleteUnAviliableUrl(List<MovieNameAndUrlModel> movieNameAndUrlModels, String tableName) {
        for (MovieNameAndUrlModel movieNameAndUrlModel : movieNameAndUrlModels) {
            movieNameAndUrlMapper.deleteUnAviliableUrl(tableName, movieNameAndUrlModel.getMovieUrl());
        }
    }
}
