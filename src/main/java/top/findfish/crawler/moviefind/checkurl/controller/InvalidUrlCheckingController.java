package top.findfish.crawler.moviefind.checkurl.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import top.findfish.crawler.common.AjaxResult;
import top.findfish.crawler.common.FindfishConstant;
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
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(value = "/invalid")
public class InvalidUrlCheckingController {

    private final InvalidUrlCheckingService invalidUrlCheckingService;

    private final RedisTemplate redisTemplate;


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
                    return AjaxResult.success(FindfishConstant.LINK_OVERDUE.getDescription());
                } else {
                    return AjaxResult.success(FindfishConstant.LINK_SUCCESS.getDescription());
                }
            }
        } catch (Exception e) {
            return AjaxResult.error(FindfishConstant.LINK_OVERDUE.getDescription());
        }
        return AjaxResult.error(FindfishConstant.LINK_OVERDUE.getDescription());
    }
}
