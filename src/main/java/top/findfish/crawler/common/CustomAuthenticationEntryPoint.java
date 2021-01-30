package top.findfish.crawler.common;

/**
 * @author: QiSun
 * @date: 2020-09-29
 * @description:没有携带token或者token无效
 */

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {


        response.setCharacterEncoding("utf-8");
        response.setContentType("text/javascript;charset=utf-8");
        response.getWriter().print(JSONObject.toJSONString(AjaxResult.error("")));

    }

}