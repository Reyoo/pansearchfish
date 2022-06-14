package com.libbytian.pan.findmovie.xiaoyu;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.MovieNameAndUrlMapper;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 项目名: top-findfish-findfish
 * 文件名: FindMovieInXiaoyouImpl
 * 创建者: HS
 * 创建时间:2022/6/14 14:37
 * 描述: TODO
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class FindMovieInXiaoyuImpl extends ServiceImpl<MovieNameAndUrlMapper, MovieNameAndUrlModel> implements IFindMovieInXiaoyu{

    private final MovieNameAndUrlMapper movieNameAndUrlMapper;

    @Override
    public List<MovieNameAndUrlModel> findMovieUrl(String movieName) throws Exception {
        return  movieNameAndUrlMapper.selectMovieUrlByLikeName("url_movie_xiaoyu", movieName);
    }
}
