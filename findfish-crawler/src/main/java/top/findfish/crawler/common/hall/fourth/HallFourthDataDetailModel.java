package top.findfish.crawler.common.hall.fourth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * *************************************************************************
 * <p/>
 *
 * @文件名称: HallFourthDataModel.java
 * @包 路 径： top.findfish.crawler.common.hall.fourth
 * @类描述:
 * @版本: V1.0
 * @Author：SunQi
 * @创建时间：2022/10/25 15:09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HallFourthDataDetailModel implements Serializable {

    String id;
    String name;
    String url;
    String type;
    String from;
    String content;
    String gmtCreate;
    String gmtShare;
    String fileCount;
    String creatorId;
    String creatorName;
    List<HallFourthFileInfoModel> fileInfos;

}
