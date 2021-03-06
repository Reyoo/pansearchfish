package top.findfish.crawler.moviefind.checkurl.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import top.findfish.crawler.common.AjaxResult;
import top.findfish.crawler.sqloperate.model.MovieNameAndUrlModel;
import top.findfish.crawler.util.InvalidUrlCheckingService;

import java.util.List;

/**
 * @ProjectName: pansearch
 * @Package: com.libbytian.pan.system.controller
 * @ClassName: InvalidUrlCheckingController
 * @Author: sun71
 * @Description: 失效链接检测
 * @Date: 2020/12/5 21:13
 * @Version: 1.0
 */
@RestController
@RequestMapping(value = "/invalid")
public class InvalidUrlCheckingController {

    @Autowired
    InvalidUrlCheckingService invalidUrlCheckingService;

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 校验url 是否正常使用、这里应当做一个操作、即如果url  未启用接口
     * @param wangPanUrls
     * @return
     */
    @RequestMapping(path = "/url", method = RequestMethod.POST)
    public AjaxResult checkInvalidUrl(@RequestBody List<MovieNameAndUrlModel> wangPanUrls) {

        try {
            for (MovieNameAndUrlModel movieNameAndUrlModel : wangPanUrls) {
                boolean isValid = invalidUrlCheckingService.checkUrlByUrlStr(movieNameAndUrlModel.getWangPanUrl());
                if (isValid) {
                    return AjaxResult.success("链接失效");
                } else {
                    return AjaxResult.success("该链接请求正常");
                }
            }
        } catch (Exception e) {
            return AjaxResult.error("链接失效");
        }
        return AjaxResult.error("链接失效");
    }
}
