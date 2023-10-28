package com.libbytian.pan.system.controller;

import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.service.ISystemUserSearchMovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * 项目名: top-findfish-findfish
 * 文件名: CommonManagerController
 * 创建者: HS
 * 创建时间:2022/5/15 21:38
 * 描述: TODO
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/canReact")
@Slf4j
public class CanReactController {

    private final ISystemUserSearchMovieService iSystemUserSearchMovieService;

    /**
     * 后台输出满心
     *
     * @return
     */
    @RequestMapping(value = "/select", method = RequestMethod.GET)
    public AjaxResult getHotList() {
        int n = 6; // 控制心形的大小

        for (int i = n; i >= 0; i--) {
            for (int j = 0; j < n - i; j++) {
                System.out.print(" ");
            }
            for (int j = 0; j < 2 * i - 1; j++) {
                System.out.print("*");
            }
            for (int j = 0; j < n - i; j++) {
                System.out.print(" ");
            }
            for (int j = 0; j < n - i; j++) {
                System.out.print(" ");
            }
            for (int j = 0; j < 2 * i - 1; j++) {
                System.out.print("*");
            }
            System.out.println();
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                System.out.print(" ");
            }
            for (int j = 0; j < 2 * (n - i) - 1; j++) {
                System.out.print("*");
            }
            for (int j = 0; j < i; j++) {
                System.out.print(" ");
            }
            for (int j = 0; j < i; j++) {
                System.out.print(" ");
            }
            for (int j = 0; j < 2 * (n - i) - 1; j++) {
                System.out.print("*");
            }
            System.out.println();
        }
        return AjaxResult.success();

    }


    @RequestMapping(value = "/doubleFor", method = RequestMethod.GET)
    public AjaxResult getDoubleIdeaFor() {
        int arr[] = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        for (int i = 0; i < arr.length; i++) {
            for (int j = 1 +i; j < arr.length; j++) {
                int temp = 0;
                if (arr[j] > arr[i]) {
                    temp = arr[j];
                    arr[j] = arr[i];
                    arr[i] = temp;
                }
            }
        }
        Arrays.stream(arr).forEach(t-> System.out.printf(toString()));
        return AjaxResult.success();
    }

}
