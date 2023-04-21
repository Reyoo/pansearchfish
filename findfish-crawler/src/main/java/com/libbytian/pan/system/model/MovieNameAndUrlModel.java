package com.libbytian.pan.system.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;

/**
 * 项目名: top-findfish-findfish
 * 文件名: MovieNameAndUrlModel
 * 创建者: HS
 * 创建时间:2022/10/30 20:15
 * 描述: TODO
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Data
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
