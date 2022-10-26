package top.findfish.crawler.constant;

/**
 * *************************************************************************
 * <p/>
 *
 * @文件名称: FindfishConstant.java
 * @包 路 径： top.findfish.crawler.common
 * @类描述: 身份证号校验
 * @版本: V1.0 @创建人：SunQi
 * @创建时间：2021/11/29 13:29
 */
public enum FindfishConstant {



    BAIDUYUN_STR("BAIDUYUN_STR", "百度云"),
    POWERED_BY_DISCUZ("POWERED_BY_DISCUZ", "Powered by Discuz"),
    POWERED_STR("POWERED_STR", "Powered"),
    LINK_OVERDUE("LINK_OVERDUE","链接失效"),
    LINK_SUCCESS("LINK_SUCCESS","该链接请求正常");

    FindfishConstant(String type, String description) {
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
