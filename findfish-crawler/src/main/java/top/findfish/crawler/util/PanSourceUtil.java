package top.findfish.crawler.util;

import top.findfish.crawler.constant.WebPageTagConstant;

/**
 * 项目名: top-findfish-findfish
 * 文件名: PanSourceUtil
 * 创建者: HS
 * 创建时间:2022/10/30 20:09
 * 描述: TODO
 */
public class PanSourceUtil {
    public static String panSource(String panUrl){
        if (panUrl.contains(WebPageTagConstant.BAIDU_WANGPAN.getType())){

            return WebPageTagConstant.BAIDU_WANGPAN.getDescription();

        }else if (panUrl.contains(WebPageTagConstant.KUAKE_WANGPAN.getType())){
            return WebPageTagConstant.KUAKE_WANGPAN.getDescription();
        }
        else {
            return WebPageTagConstant.KUAKE_WANGPAN.getDescription();
        }
    }
}
