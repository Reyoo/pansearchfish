package com.libbytian.pan.findmovie.hall.fourth;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.MovieNameAndUrlMapper;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 项目名: pan
 * 文件名: FindMovieHall2Impl
 * 创建者: HS
 * 创建时间:2022/1/13 17:05
 * 描述: TODO
 * @author SunQi
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class FindMovieHallFourthImpl extends ServiceImpl<MovieNameAndUrlMapper, MovieNameAndUrlModel> implements IFindMovieHallFourth {

    private final MovieNameAndUrlMapper movieNameAndUrlMapper;

    @Override
    public List<MovieNameAndUrlModel> findMovieUrl(String movieName) throws Exception {
        return  movieNameAndUrlMapper.selectMovieUrlByLikeName("url_movie_fourth", movieName);
    }
}
