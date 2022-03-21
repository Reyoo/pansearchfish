package top.findfish.crawler.sqloperate.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.findfish.crawler.sqloperate.model.MovieNameAndUrlModel;

import java.util.List;


/**
 * @author: QiSun
 * @date: 2020-12-06
 * @Description:
 */
@Mapper
public interface MovieNameAndUrlMapper extends BaseMapper<MovieNameAndUrlModel> {


    List<MovieNameAndUrlModel> selectMovieUrlByName(@Param(value = "tablename")  String tablename , @Param(value = "movieName") String movieName , @Param(value = "wangPanUrl") String wangPanUrl);


    List<MovieNameAndUrlModel> selectMovieUrlByLikeName(@Param(value = "tablename") String tablename , @Param(value = "movieName") String movieName);


    List<MovieNameAndUrlModel> selectMovieUrlByLikeNameGroup(@Param(value = "tablename") String tablename ,@Param(value = "movieName") String movieName);

    /**
     * 批量新增
     * @param movieNameAndUrlModels
     * @return
     */
    int insertMovieUrls(@Param(value = "tableName") String tableName,List<MovieNameAndUrlModel> movieNameAndUrlModels);

    /**
     * 新增
     * @param movieNameAndUrlModel
     * @return
     */
    int insertMovieUrl(@Param(value = "tableName") String tableName ,MovieNameAndUrlModel movieNameAndUrlModel);


    /**
     * 删除资源
     * @param movieNameAndUrlModel
     * @return
     */
    int deleteUrlMovieUrls(@Param(value = "tableName")  String tableName ,MovieNameAndUrlModel movieNameAndUrlModel);


    /**
     * 删除重复资源
     * @param tableName
     * @param normalWebUrl
     * @return
     */
    int deleteUnAviliableUrl(@Param(value = "tableName")  String tableName, @Param(value = "normalWebUrl") String normalWebUrl);


    /**
     * 更新电影资源
     * @param movieNameAndUrlModel
     * @return
     */
    int updateUrlMovieUrl(@Param(value = "tableName")  String tableName, MovieNameAndUrlModel movieNameAndUrlModel);


    /**
     * 校验重复资源
     */
    void checkRepeatMovie(@Param(value = "tableName")  String tableName);

    List<MovieNameAndUrlModel> selectMovieUrlByCondition(@Param(value = "tablename")  String tablename , @Param(value = "movieName")  String movieName ,
                                                         @Param(value = "wangPanUrl")  String wangPanUrl, @Param(value = "titleName")   String titleName,
                                                         @Param(value = "panSource")  String panSource);

}
