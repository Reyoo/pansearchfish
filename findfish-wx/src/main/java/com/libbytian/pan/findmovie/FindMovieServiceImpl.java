package com.libbytian.pan.findmovie;

import com.libbytian.pan.system.mapper.MovieNameAndUrlMapper;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * *************************************************************************
 * <p/>
 *
 * @文件名称: AbstractFindMovieService.java
 * @包 路 径： com.libbytian.pan.findmovie
 * @版权所有：北京数字认证股份有限公司 (C) 2021
 * @类描述:
 * @版本: V1.0
 * @Author：SunQi
 * @创建时间：2022/10/27 16:05
 */

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class FindMovieServiceImpl implements IFindMovieService {

    private final MovieNameAndUrlMapper movieNameAndUrlMapper;

    @Override
    public List<MovieNameAndUrlModel> getMoviesByName(String tbName, String movieName) throws Exception {
        return movieNameAndUrlMapper.selectMovieUrlByLikeName(tbName, movieName);
    }
}
