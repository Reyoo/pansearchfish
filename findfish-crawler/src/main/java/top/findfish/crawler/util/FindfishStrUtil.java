package top.findfish.crawler.util;

import top.findfish.crawler.constant.FindfishConstant;

public class FindfishStrUtil {

    public static String getsumSuMovieName(String movieName){
        String resultSearchName = movieName;
        if (FindfishConstant.BAIDUYUN_STR.getDescription().contains(movieName)) {
            resultSearchName = movieName.split(FindfishConstant.BAIDUYUN_STR.getDescription())[0].trim();
        }

        if (FindfishConstant.POWERED_BY_DISCUZ.getDescription().contains(resultSearchName)) {
            resultSearchName = resultSearchName.split(FindfishConstant.POWERED_STR.getDescription())[0].trim();
        }
        return resultSearchName;
    }
}
