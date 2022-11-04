package top.findfish.crawler.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.findfish.crawler.moviefind.jsoup.hall.fourth.HallFourthDataNodeModel;

import java.io.Serializable;

/**
 * *************************************************************************
 * <p/>
 *
 * @文件名称: HallFourthResult.java
 * @包 路 径： top.findfish.crawler.common
 * @类描述:
 * @版本: V1.0
 * @Author：SunQi
 * @创建时间：2022/10/25 15:07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HallFourthResult implements Serializable {


    Integer code;

    String msg;

    HallFourthDataNodeModel data;

}
