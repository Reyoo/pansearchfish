package top.findfish.crawler.moviefind.jsoup.youjiang;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.findfish.crawler.moviefind.ICrawlerCommonService;
import top.findfish.crawler.sqloperate.mapper.MovieNameAndUrlMapper;
import top.findfish.crawler.sqloperate.model.MovieNameAndUrlModel;
import top.findfish.crawler.sqloperate.service.IMovieNameAndUrlService;
import top.findfish.crawler.util.Constant;
import top.findfish.crawler.util.InvalidUrlCheckingService;

import java.net.URLEncoder;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 项目名: crawler
 * 文件名: JsoupYouJiangServiceImpl
 * 创建者: HS
 * 创建时间:2021/2/27 22:21
 * 描述: TODO
 */
@Service("jsoupYouJiangServiceImpl")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class JsoupYouJiangServiceImpl implements ICrawlerCommonService {

    private final RedisTemplate redisTemplate;

    private final InvalidUrlCheckingService invalidUrlCheckingService;
    private final IMovieNameAndUrlService movieNameAndUrlService;
    private final MovieNameAndUrlMapper movieNameAndUrlMapper;


    @Value("${user.youjiang.url}")
    String youjiangUrl;

    @Override
    public Set<String> firstFindUrl(String searchMovieName, String proxyIpAndPort) throws Exception {
        log.info("-------------->开始爬取 悠酱<--------------------");

        Set<String> movieList = new HashSet<>();
        String encode = URLEncoder.encode(searchMovieName.trim(), "UTF8");

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(youjiangUrl);
        stringBuffer.append("/?s=");
        stringBuffer.append(encode);

        try {

            Document document = Jsoup.connect(stringBuffer.toString()).get();
            Elements elements = document.getElementsByClass("post-content");
            for (Element element : elements) {
                String movieUrl = element.select("a").attr("href");
                //判断是一层链接还是两层链接
                if (StringUtils.isBlank(movieUrl)) {
                    movieList.add(stringBuffer.toString());
                } else {
                    movieList.add(movieUrl);
                }
            }

            return movieList;
        } catch (Exception e) {
            log.error("line 210 ->" + e.getMessage());
            return movieList;
        }
    }

    @Override
    public ArrayList<MovieNameAndUrlModel> getWangPanUrl(String secondUrlLxxh, String proxyIpAndPort) throws Exception {
        ArrayList<MovieNameAndUrlModel> list = new ArrayList();

//        Document document = JsoupFindfishUtils.getDocument(secondUrlLxxh, proxyIpAndPort);
        Document document = Jsoup.connect(secondUrlLxxh).get();
        String movieName = document.getElementsByTag("title").first().text();

        if (movieName.contains("- 酱酱家")) {
            movieName = movieName.split("- 酱酱家")[0];
        }


        Elements pTagAttr = document.select("a[href]");

        for (Element element : pTagAttr) {


            if (element.parentNode().childNodeSize()>1){

                if (element.parentNode().childNode(1).attr("href").contains("pan.baidu")){

                    MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();


                    if (element.parentNode().childNodeSize() == 3) {

                        movieNameAndUrlModel.setWangPanPassword(element.parentNode().childNode(2).toString().trim().replaceAll("&nbsp;",""));
                        setMovieName(element , movieNameAndUrlModel ,movieName , 0);
                    }else if (element.parentNode().childNodeSize() == 4 || element.parentNode().childNodeSize() == 6){


                        if (list.size()!=0 && list.size()%2 == 1){

                            String name = element.parentNode().childNode(0).toString().split(".视频")[0];

                            if (list.get(list.size()-1).getMovieName().contains(name)){
                                movieNameAndUrlModel.setWangPanPassword("");

                                if (element.parentNode().childNodeSize() == 4){
                                    setMovieName(element , movieNameAndUrlModel ,movieName , 2);
                                }else {
                                    setMovieName(element , movieNameAndUrlModel ,movieName , 3);
                                }

                            }

                        }else {
                            movieNameAndUrlModel.setWangPanPassword("");
                            setMovieName(element , movieNameAndUrlModel ,movieName , 0);
                        }
//
                    }

                    else if (element.parentNode().childNodeSize() == 5){


                        if (list.size()!=0 && list.size()%2 == 1){

                            String name = element.parentNode().childNode(0).toString().split(".视频")[0];

                            if (list.get(list.size()-1).getMovieName().contains(name)){


                                setMovieNameAndPassword(element , movieNameAndUrlModel , movieName , 4);


                            }
                        }else {

                            setMovieNameAndPassword(element , movieNameAndUrlModel , movieName , 2);
                            setMovieName(element , movieNameAndUrlModel ,movieName , 0);
                        }


                    }



                    else {
                        movieNameAndUrlModel.setWangPanPassword("");
                        setMovieName(element , movieNameAndUrlModel ,movieName , 0);
                    }

                    Elements p = document.select("p").select("href");

                    for (Element element1 : p) {
                        if (document.select("p").contains("pan.baidu")){
                            String text = element1.text();
                            System.out.println(text);
                        }
                    }


                    movieNameAndUrlModel.setWangPanUrl(element.select("a").attr("href"));
                    movieNameAndUrlModel.setMovieUrl(secondUrlLxxh);
                    list.add(movieNameAndUrlModel);


                }
            }

        }



        return list;
    }

    @Override
    public void saveOrFreshRealMovieUrl(String searchMovieName, String proxyIpAndPort) {
        List<MovieNameAndUrlModel> movieNameAndUrlModelList = new ArrayList<>();
        try {
            Set<String> set = firstFindUrl(searchMovieName, proxyIpAndPort);
            if (set.size() > 0) {
                for (String url : set) {
                    //由于包含模糊查询、这里记录到数据库中做插入更新操作
                    movieNameAndUrlModelList.addAll(getWangPanUrl(url,proxyIpAndPort));
                }

                //筛选爬虫链接
//                invalidUrlCheckingService.checkUrlMethod("url_movie_xiaoyou", movieNameAndUrlModelList);
                //插入更新可用数据
                movieNameAndUrlService.addOrUpdateMovieUrls(movieNameAndUrlModelList, Constant.YOUJIANG_TABLENAME);

                //更新后从数据库查询后删除 片名相同但更新中的 无效数据
                List<MovieNameAndUrlModel> movieNameAndUrlModels = movieNameAndUrlMapper.selectMovieUrlByLikeName(Constant.YOUJIANG_TABLENAME, searchMovieName);

                //筛选数据库链接
                redisTemplate.opsForValue().set("youjiang:"+ searchMovieName , invalidUrlCheckingService.checkDataBaseUrl(Constant.YOUJIANG_TABLENAME, movieNameAndUrlModels, proxyIpAndPort), Duration.ofHours(1L));

            }
        } catch (Exception e) {
            log.error("docment is null ->" + e.getMessage());
            redisTemplate.opsForHash().delete("use_proxy", proxyIpAndPort);
        }
    }


    public void setMovieName(Element element , MovieNameAndUrlModel movieNameAndUrlModel , String movieName , int i){

        //判断片名是否需要拼接
        int indexName = element.parentNode().childNode(i).toString().indexOf(".视频");
        if (indexName != -1) {
            movieNameAndUrlModel.setMovieName(movieName +"  『"+ element.parentNode().childNode(i).toString().substring(0, indexName).replaceAll("&nbsp;","").trim()+"』");
        }
        else if (!element.parentNode().childNode(i).toString().contains("视频")){
            movieNameAndUrlModel.setMovieName(movieName+"  『"+element.parentNode().childNode(i).toString().replaceAll("：","").trim()+"』");
        }
        else {
            movieNameAndUrlModel.setMovieName(movieName);
        }
    }

    public void setMovieNameAndPassword(Element element , MovieNameAndUrlModel movieNameAndUrlModel , String movieName ,int i){



        if (element.parentNode().childNode(i).toString().contains("提取码") || element.parentNode().childNode(i).toString().contains("密码")){
            movieNameAndUrlModel.setWangPanPassword(element.parentNode().childNode(i).toString().replaceAll("&nbsp;","").trim());
            setMovieName(element , movieNameAndUrlModel ,movieName , i-2);

        }else {
            setMovieName(element , movieNameAndUrlModel ,movieName , i-1);
            movieNameAndUrlModel.setWangPanPassword("");
        }
    }
}
