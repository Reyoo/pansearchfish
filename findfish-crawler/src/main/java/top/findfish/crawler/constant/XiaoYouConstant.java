package top.findfish.crawler.constant;

public enum XiaoYouConstant {


    XIAOYOU_URL_CHECKSTMP("STMP", "STMP"),
    XIAOYOU_URL_CHECKXUJ("xuj.cool/STMP/20", "xuj.cool/STMP/20"),
    XIAOYOU_STR_WITH_SGIN("- 小悠家", "- 小悠家"),
    XIAOYOU_SHIPIN("视频", "视频"),
    XIAOYOU_TIQUMA("码：","码："),
    XIAOYOU_TIQUMA_English("码:","码:"),
    XIAOYOU("xiaoyou::","xiaoyou::"),
    LILI_URL("http://a12.66perfect.com/","http://a12.66perfect.com/"),
    LILI_DOUBAN_URL("https://movie.douban.com","https://movie.douban.com"),
    LILI("lili::","lili::"),
    LiLi_STR_WITH_SGIN("– A12 Site", "– A12 Site"),
    XIAOYU("xiaoyu::","xiaoyu::"),
    HALL_FOURTH_CACHE("fourth::","fourth::"),
    AIDIANYING_STR_WITH_SGIN("- LXXH", "- 爱电影标题后缀");


    XiaoYouConstant(String type, String description) {
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
