package top.findfish.crawler.moviefind.jsoup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.findfish.crawler.moviefind.ICrawlerCommonService;
import top.findfish.crawler.sqloperate.model.MovieNameAndUrlModel;
import top.findfish.crawler.sqloperate.service.IMovieNameAndUrlService;
import top.findfish.crawler.util.WebPageConstant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 项目名: crawler
 * 文件名: InitializeUrl
 * 创建者: HS
 * 创建时间:2021/2/28 3:08
 * 描述: TODO
 */
@Service("initializeUrl")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class InitializeUrl implements ICrawlerCommonService {

    private final IMovieNameAndUrlService movieNameAndUrlService;


    @Override
    public Set<String> firstFindUrl(String searchMovieName, String proxyIpAndPort) throws Exception {
        return null;
    }

    @Override
    public ArrayList<MovieNameAndUrlModel> getWangPanUrl(String secondUrlLxxh, String proxyIpAndPort) throws Exception {
        ArrayList<MovieNameAndUrlModel> list = new ArrayList();

//        Document document = JsoupFindfishUtils.getDocument(secondUrlLxxh, proxyIpAndPort);
        try {
            Document document = Jsoup.connect(secondUrlLxxh).get();
            String movieName = document.getElementsByTag("title").first().text();

            if (movieName.contains("- 酱酱家")) {
                movieName = movieName.split("- 酱酱家")[0];
            }


            Elements pTagAttr = document.select("a[href]");

            for (Element element : pTagAttr) {


                if (element.parentNode().childNodeSize() > 1) {

                    if (element.parentNode().childNode(1).attr("href").contains("pan.baidu")) {

                        MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();


                        if (element.parentNode().childNodeSize() == 3) {

                            movieNameAndUrlModel.setWangPanPassword(element.parentNode().childNode(2).toString().trim().replaceAll("&nbsp;", ""));
                            setMovieName(element, movieNameAndUrlModel, movieName, 0);
                        } else if (element.parentNode().childNodeSize() == 4 || element.parentNode().childNodeSize() == 6) {


                            if (list.size() != 0 && list.size() % 2 == 1) {

                                String name = element.parentNode().childNode(0).toString().split(".视频")[0];

                                if (list.get(list.size() - 1).getMovieName().contains(name)) {
                                    movieNameAndUrlModel.setWangPanPassword("");

                                    if (element.parentNode().childNodeSize() == 4) {
                                        setMovieName(element, movieNameAndUrlModel, movieName, 2);
                                    } else {
                                        setMovieName(element, movieNameAndUrlModel, movieName, 3);
                                    }

                                }

                            } else {
                                movieNameAndUrlModel.setWangPanPassword("");
                                setMovieName(element, movieNameAndUrlModel, movieName, 0);
                            }
//
                        } else if (element.parentNode().childNodeSize() == 5) {


                            if (list.size() != 0 && list.size() % 2 == 1) {

                                String name = element.parentNode().childNode(0).toString().split(".视频")[0];

                                if (list.get(list.size() - 1).getMovieName().contains(name)) {


                                    setMovieNameAndPassword(element, movieNameAndUrlModel, movieName, 4);


                                }
                            } else {

                                setMovieNameAndPassword(element, movieNameAndUrlModel, movieName, 2);
                                setMovieName(element, movieNameAndUrlModel, movieName, 0);
                            }


                        } else {
                            movieNameAndUrlModel.setWangPanPassword("");
                            setMovieName(element, movieNameAndUrlModel, movieName, 0);
                        }

                        Elements p = document.select("p").select("href");

                        for (Element element1 : p) {
                            if (document.select("p").contains("pan.baidu")) {
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
        } catch (IOException e) {
            log.error("line 210 ->" + e.getMessage());
            return list;
        }

        return list;
    }

    @Override
    public void saveOrFreshRealMovieUrl(String searchMovieName, String proxyIpAndPort) throws Exception {
        List<MovieNameAndUrlModel> movieNameAndUrlModelList = new ArrayList<>();

        String url = searchMovieName;

        movieNameAndUrlModelList.addAll(getWangPanUrl(url, proxyIpAndPort));

        //插入更新可用数据
        movieNameAndUrlService.addOrUpdateMovieUrls(movieNameAndUrlModelList, WebPageConstant.YOUJIANG_TABLENAME);

    }

    @Override
    public void checkRepeatMovie() {

    }

    public static void setMovieName(Element element, MovieNameAndUrlModel movieNameAndUrlModel, String movieName, int i) {

        //判断片名是否需要拼接
        int indexName = element.parentNode().childNode(i).toString().indexOf(".视频");
        if (indexName != -1) {
            movieNameAndUrlModel.setMovieName(movieName + "  『" + element.parentNode().childNode(i).toString().substring(0, indexName).replaceAll("&nbsp;", "").trim() + "』");
        } else if (!element.parentNode().childNode(i).toString().contains("视频")) {
            movieNameAndUrlModel.setMovieName(movieName + "  『" + element.parentNode().childNode(i).toString().replaceAll("：", "").trim() + "』");
        } else {
            movieNameAndUrlModel.setMovieName(movieName);
        }
    }

    public static void setMovieNameAndPassword(Element element, MovieNameAndUrlModel movieNameAndUrlModel, String movieName, int i) {

        if (element.parentNode().childNode(i).toString().contains("提取码") || element.parentNode().childNode(i).toString().contains("密码")) {
            movieNameAndUrlModel.setWangPanPassword(element.parentNode().childNode(i).toString().replaceAll("&nbsp;", "").trim());
            setMovieName(element, movieNameAndUrlModel, movieName, i - 2);

        } else {
            setMovieName(element, movieNameAndUrlModel, movieName, i - 1);
            movieNameAndUrlModel.setWangPanPassword("");
        }
    }
}
