package com.libbytian.pan.findmovie;

import com.libbytian.pan.system.model.MovieNameAndUrlModel;

import java.util.List;

public interface IFindMovieService {

    List<MovieNameAndUrlModel> getMoviesByName(String tbName,String movieName) throws Exception;

}
