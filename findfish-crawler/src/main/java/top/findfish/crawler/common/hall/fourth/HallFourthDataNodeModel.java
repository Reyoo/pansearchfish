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
 * @文件名称: HallFourthDataNodeModel.java
 * @包 路 径： top.findfish.crawler.common.hall.fourth
 * @版权所有：北京数字认证股份有限公司 (C) 2021
 * @类描述:
 * @版本: V1.0
 * @Author：SunQi
 * @创建时间：2022/10/25 15:12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HallFourthDataNodeModel implements Serializable {

    Integer total;

    List<HallFourthDataDetailModel> list;
}


