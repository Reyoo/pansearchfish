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
    HTML_TAG_A("a", "a标签"),
    HTML_TAG_HREF("href", "href标签"),
    HTML_TAG_TITLE("title", "title标签"),
    HTML_TAG_P("p","p标签"),
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

}
