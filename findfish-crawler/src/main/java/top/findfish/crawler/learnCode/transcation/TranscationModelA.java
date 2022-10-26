package top.findfish.crawler.learnCode.transcation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * *************************************************************************
 * <p/>
 *
 * @文件名称: TranscationModelA.java
 * @包 路 径： top.findfish.crawler.learnCode.transcation
 * @类描述: 身份证号校验
 * @版本: V1.0 @创建人：SunQi
 * @创建时间：2022/1/20 09:28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TranscationModelA {

    int id ;
    String key;
    String value;
}
