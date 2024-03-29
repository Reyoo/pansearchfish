package top.findfish.crawler.sqloperate.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @ProjectName: pan
 * @Package: com.search.pan.system.model
 * @ClassName: MovieNameAndUrlModel
 * @Author: sun71
 * @Description: 电影名以及url实体类
 * @Date: 2020/8/30 16:26
 * @Version: 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieNameAndUrlModel extends Model<MovieNameAndUrlModel> {

    /**
     * 主键id
     */
    @TableId(value = "id")
    Integer id;
    @TableField("movie_name")
    String movieName;
    @TableField("movie_url")
    String movieUrl;
    @TableField("wangpan_url")
    String wangPanUrl;
    @TableField("wangpan_passwd")
    String wangPanPassword;
    @TableField("title_name")
    String titleName;
    @TableField("pan_source")
    String panSource;

}
