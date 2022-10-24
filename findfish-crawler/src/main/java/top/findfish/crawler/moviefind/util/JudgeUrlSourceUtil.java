package top.findfish.crawler.moviefind.util;

/**
 * *************************************************************************
 * <p/>
 *
 * @文件名称: JudgeUrlSourceUtil.java
 * @包 路 径： top.findfish.crawler.moviefind.util
 * @版权所有：北京数字认证股份有限公司 (C) 2021
 * @类描述:
 * @版本: V1.0
 * @Author：SunQi
 * @创建时间：2022/10/24 13:52
 */
public class JudgeUrlSourceUtil {



    public static String  getSourceStr(String url){

        if(url.contains("baidu")){
            return "百度网盘";
        }

        if(url.contains("quark")){
            return "夸克网盘";
        }

        if(url.contains("xunlei")){
            return "迅雷网盘";
        }
        return "";

    }

}
