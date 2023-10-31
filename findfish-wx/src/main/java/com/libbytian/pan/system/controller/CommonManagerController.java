package com.libbytian.pan.system.controller;

import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.SystemUserSearchMovieModel;
import com.libbytian.pan.system.service.ISystemUserSearchMovieService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 项目名: top-findfish-findfish
 * 文件名: CommonManagerController
 * 创建者: HS
 * 创建时间:2022/5/15 21:38
 * 描述: TODO
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/commonManager")
@Api(tags = "外部接口")
@Slf4j
public class CommonManagerController {

    private final ISystemUserSearchMovieService iSystemUserSearchMovieService;

    /**
     * 查询热榜
     *
     * @param date 0为当日，7为近7天，30为近30天
     * @return
     */
    @ApiOperation(value = "热榜查询")
    @RequestMapping(value = "/select/hotList", method = RequestMethod.GET)
    public AjaxResult getHotList(@RequestParam(defaultValue = "1") Integer date,
                                 @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        Map<String, Object> hotList = iSystemUserSearchMovieService.getFalseDataHotList(date, pageNum, pageSize);
        return AjaxResult.success(hotList);

    }


    @RequestMapping(value = "/select/everList", method = RequestMethod.GET)
    public AjaxResult getEveryList(@RequestParam(defaultValue = "1") Integer date,
                                   @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        Map<String, Object> everyList = iSystemUserSearchMovieService.getEveryList(date, pageNum, pageSize);
        return AjaxResult.success(everyList);

    }

    @RequestMapping(value = "/select/ohterList", method = RequestMethod.GET)
    public AjaxResult getOtherList() {
        List<SystemUserSearchMovieModel> otherList = iSystemUserSearchMovieService.getOtherList();
        return AjaxResult.success(otherList);

    }

}
