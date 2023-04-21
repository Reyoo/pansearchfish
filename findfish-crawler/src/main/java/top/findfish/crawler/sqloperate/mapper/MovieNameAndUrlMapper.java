package top.findfish.crawler.sqloperate.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


import java.util.List;


/**
 * @author: QiSun
 * @date: 2020-12-06
 * @Description:
 */
@Mapper
public interface MovieNameAndUrlMapper extends BaseMapper<MovieNameAndUrlModel> {


    List<MovieNameAndUrlModel> selectMovieUrlByName(@Param("tableName") String tableName, @Param("movieName") String movieName, @Param("titleName") String titleName, @Param("panSource") String panSource);


    List<MovieNameAndUrlModel> selectMovieUrlByLikeName(@Param("tableName") String tableName, @Param("movieName") String movieName);


    List<MovieNameAndUrlModel> selectMovieUrlByLikeNameGroup(@Param("tableName") String tableName, @Param("movieName") String movieName);

    /**
     * 批量新增
     *
     * @param movieNameAndUrlModels
     * @return
     */
    int insertMovieUrls(@Param("tableName") String tableName,  @Param("movieNameAndUrlModels") List<MovieNameAndUrlModel> movieNameAndUrlModels);

    /**
     * 新增
     *
     * @param movieNameAndUrlModel
     * @return
     */
    int insertMovieUrl(@Param("tableName") String tableName, @Param("movieNameAndUrlModel") MovieNameAndUrlModel movieNameAndUrlModel);


    /**
     * 删除资源
     *
     * @param movieNameAndUrlModel
     * @return
     */
    int deleteUrlMovieUrls(@Param("tableName") String tableName,  @Param("movieNameAndUrlModel")MovieNameAndUrlModel movieNameAndUrlModel);


    /**
     * 删除重复资源
     *
     * @param tableName
     * @param normalWebUrl
     * @return
     */
    int deleteUnAviliableUrl(@Param("tableName") String tableName, @Param("normalWebUrl") String normalWebUrl);


    /**
     * 更新电影资源
     *
     * @param movieNameAndUrlModel
     * @return
     */
    int updateUrlMovieUrl(@Param("tableName") String tableName, @Param("movieNameAndUrlModel") MovieNameAndUrlModel movieNameAndUrlModel);


    /**
     * 校验重复资源
     */
    void checkRepeatMovie(@Param("tableName") String tableName);

    /**
     * 清理热榜表 2个月以前的数据
     */
    void cleanHotList();

    List<MovieNameAndUrlModel> selectMovieUrlByCondition(@Param("tableName") String tablename, @Param("movieName") String movieName, @Param("wangPanUrl") String wangPanUrl,
                                                         @Param("titleName") String titleName, @Param("panSource") String panSource);

}

