package top.findfish.crawler.sqloperate.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.findfish.crawler.sqloperate.model.MovieNameAndUrlModel;

import java.util.List;


/**
 * @author: QiSun
 * @date: 2020-12-06
 * @Description:
 */
@Mapper
public interface MovieNameAndUrlMapper extends BaseMapper<MovieNameAndUrlModel> {


    List<MovieNameAndUrlModel> selectMovieUrlByName(String tablename , String movieName);


    List<MovieNameAndUrlModel> selectMovieUrlByLikeName(String tablename , String movieName);

    /**
     * 批量新增
     * @param movieNameAndUrlModels
     * @return
     */
    int insertMovieUrls(String tableName,List<MovieNameAndUrlModel> movieNameAndUrlModels);

    /**
     * 新增
     * @param movieNameAndUrlModel
     * @return
     */
    int insertMovieUrl(String tableName ,MovieNameAndUrlModel movieNameAndUrlModel);


    /**
     * 删除资源
     * @param movieNameAndUrlModel
     * @return
     */
    int deleteUrlMovieUrls(String tableName ,MovieNameAndUrlModel movieNameAndUrlModel);


    /**
     * 删除重复资源
     * @param tableName
     * @param normalWebUrl
     * @return
     */
    int deleteUnAviliableUrl(String tableName,String normalWebUrl);


    /**
     * 更新电影资源
     * @param movieNameAndUrlModel
     * @return
     */
    int updateUrlMovieUrl(String tableName, MovieNameAndUrlModel movieNameAndUrlModel);



}
