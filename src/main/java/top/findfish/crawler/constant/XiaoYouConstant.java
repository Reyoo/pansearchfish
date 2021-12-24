package top.findfish.crawler.constant;

public enum XiaoYouConstant {


    XIAOYOU_URL_CHECKSTMP("yuanxiao.net.cn/STMP/20", "yuanxiao.net.cn/STMP/20"),
    XIAOYOU_URL_CHECKXUJ("xuj.cool/STMP/20", "xuj.cool/STMP/20"),
    XIAOYOU_STR_WITH_SGIN("- 小悠家", "- 小悠家"),
    XIAOYOU_SHIPIN("视频", "视频"),
    XIAOYOU_TIQUMA("提取码：","提取码："),
    XIAOYOU("xiaoyou:","xiaoyou:");

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
