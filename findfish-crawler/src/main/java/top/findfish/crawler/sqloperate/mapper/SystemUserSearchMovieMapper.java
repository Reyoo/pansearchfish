package top.findfish.crawler.sqloperate.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.findfish.crawler.sqloperate.model.SystemUserSearchMovieModel;

import java.util.List;


/**
 * 用户搜索电影名统计表
 */
@Mapper
public interface SystemUserSearchMovieMapper extends BaseMapper<SystemUserSearchMovieModel> {


    /**
     * 插入一条用户查询词条
     *
     * @param systemUserSearchMovieModel
     * @return
     */
    int insertUserSearchMovie(@Param("systemUserSearchMovieModel") SystemUserSearchMovieModel systemUserSearchMovieModel);


    /**
     * 查询用户查询词条
     *
     * @param searchName
     * @return
     */
    SystemUserSearchMovieModel getUserSearchMovieBySearchName(@Param(value = "searchName") String searchName);


    /**
     * 更新用户查询词条
     *
     * @param systemUserSearchMovieModel
     * @return
     */
    int updateUserSearchMovie(@Param("systemUserSearchMovieModel") SystemUserSearchMovieModel systemUserSearchMovieModel);


    /**
     * 根据用户查询词条的时间范围查询
     *
     * @param startTime
     * @param endTime
     * @return
     */
    List<SystemUserSearchMovieModel> listUserSearchMovieBySearchDateRange(@Param(value = "startTime") String startTime, @Param(value = "endTime") String endTime);

}
