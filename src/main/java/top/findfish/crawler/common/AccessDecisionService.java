package top.findfish.crawler.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 配置路径访问限制,若你的用户角色比较简单,不需要存数据库,
 * 可以在ApplicationConfigurerAdapter里配置如
 * httpSecurity
 * .authorizeRequests()
 * .antMatchers("/order").....
 *
 * @author SunQi
 * @date 2019/4/10 10:33.
 */
@Component("accessDecisionService")
@Slf4j
public class AccessDecisionService {


    final static String INITMOVIE_URLS = "/initmovie/**";
    final static String INVALID_URLS = "/invalid/**";

    static List<String> whiteList = new ArrayList<String>();


    static {
        whiteList.add(INITMOVIE_URLS);
        whiteList.add(INVALID_URLS);
    }

    private AntPathMatcher antPathMatcher = new AntPathMatcher();


    public Boolean hasPermission(HttpServletRequest request, Authentication auth) {

        boolean flag = whiteList.parallelStream().anyMatch(url -> {
            return antPathMatcher.match(url, request.getRequestURI());
        });
        return flag;
    }

}
