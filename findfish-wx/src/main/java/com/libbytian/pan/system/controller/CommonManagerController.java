package com.libbytian.pan.system.controller;

import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.service.ISystemUserSearchMovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@Slf4j
public class CommonManagerController {

    private final ISystemUserSearchMovieService iSystemUserSearchMovieService;

    /**
     * 查询热榜
     * @param date 0为当日，7为近7天，30为近30天
     * @return
     */
    @RequestMapping(value = "/select/{date}", method = RequestMethod.GET)
    public AjaxResult getHotList(@PathVariable Integer date, @RequestParam(defaultValue = "1") Integer pageNum , @RequestParam(defaultValue = "10") Integer pageSize) {
        Map<String,Object> hotList = iSystemUserSearchMovieService.getHotList(date,pageNum,pageSize);
        return AjaxResult.success(hotList);

    }
}