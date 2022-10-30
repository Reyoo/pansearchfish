package top.findfish.crawler.constant;

/**
 * *************************************************************************
 * <p/>
 *
 * @文件名称: WebPageTagConstant.java
 * @包 路 径： top.findfish.crawler.constant
 * @类描述: 身份证号校验
 * @版本: V1.0 @创建人：SunQi
 * @创建时间：2021/12/24 15:33
 */
public enum WebPageTagConstant {


    XIAOYOU_URL_PARAM("/?s=", "小优入参"),
    LILI_URL_PARAM("/?s=", "莉莉入参"),
    XIAOYU_URL_PARAM("/s/1/","小宇入参"),
    HTML_TAG_A("a", "a标签"),
    HTML_TAG_HREF("href", "href标签"),
    HTML_TAG_TITLE("title", "title标签"),
    HTML_TAG_P("p","p标签"),
    PANURL("https://pan.","https://pan."),
    PANVIP("https://pan.baidu.com/wap/vip/","百度网盘VIP充值"),
    TIQUMA_CHINA("提取码：","提取码："),
    SHIPIN_CHINA("视频：","：为中文符号"),
    BAIDU("baidu","百度"),
    BAIDU_WANGPAN("pan.baidu.com","百度网盘"),
    XUNLEI_YUNPAN("pan.xunlei.com","迅雷云盘"),
    KUAKE_WANGPAN("pan.quark.cn","夸克网盘"),
    ALI_SOURCE("ali","阿里云盘"),
    ONLINE_SHOW("在线播放","在线播放");


    WebPageTagConstant(String type, String description) {
        this.type = type;
        this.description = description;
    }

    // 成员变量
    private final String type;

    private final String description;

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public static String getValue(String key) {
        for (WebPageTagConstant ele : values()) {
            if (ele.getType().equals(key)) {
                return ele.getDescription();
            }
        }
        return null;
    }

}
