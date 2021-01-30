package top.findfish.crawler.util;

public class FindfishStrUtil {


    public static String getsumSuMovieName(String movieName){
        String resultSearchName = movieName;
        if (movieName.contains("百度云")) {
            resultSearchName = movieName.split("百度云")[0].trim();
        }

        if (resultSearchName.contains("Powered by Discuz")) {
            resultSearchName = resultSearchName.split("Powered")[0].trim();
        }
        return resultSearchName;
    }
}
