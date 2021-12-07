package top.findfish.crawler.util;

import top.findfish.crawler.common.FindfishConstant;

public class FindfishStrUtil {

    public static String getsumSuMovieName(String movieName){
        String resultSearchName = movieName;
        if (movieName.contains(FindfishConstant.BAIDUYUN_STR.getDescription())) {
            resultSearchName = movieName.split(FindfishConstant.BAIDUYUN_STR.getDescription())[0].trim();
        }

        if (resultSearchName.contains(FindfishConstant.POWERED_BY_DISCUZ.getDescription())) {
            resultSearchName = resultSearchName.split(FindfishConstant.POWERED_STR.getDescription())[0].trim();
        }
        return resultSearchName;
    }
}
