package top.findfish.crawler.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import top.findfish.crawler.moviefind.ICrawlerCommonService;

/**
 * 接口调用测试类  对外不开放
 */

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/test")
@Slf4j
public class TestInterfaceController {

    @Qualifier("jsoupYouJiangServiceImpl")
    private final ICrawlerCommonService jsoupYouJiangServiceImpl;

    @RequestMapping(value = "/find/{name}", method = RequestMethod.GET)
    public void findNotifyByPage(@PathVariable("name") String name) throws Exception {

        jsoupYouJiangServiceImpl.saveOrFreshRealMovieUrl(name,"");
        System.out.println("---------------");
    }
}
