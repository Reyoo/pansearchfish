package top.findfish.crawler.moviefind.jsoup.hall.yisou;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * *************************************************************************
 * <p/>
 *
 * @文件名称: HallFourthFileInfoModel.java
 * @包 路 径： top.findfish.crawler.common.hall.fourth
 * @类描述:
 * @版本: V1.0
 * @Author：SunQi
 * @创建时间：2022/10/25 15:08
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HallFourthFileInfoModel implements Serializable {


    String category;

    String fileExtension;

    String fileId;

    String fileName;

    String type;


}
