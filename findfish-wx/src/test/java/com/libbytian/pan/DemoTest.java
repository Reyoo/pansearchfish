package com.libbytian.pan;

import com.libbytian.pan.wechat.handler.AbstractHandler;
import com.libbytian.pan.wechat.handler.LocationHandler;
import com.libbytian.pan.wechat.handler.MenuHandler;

import java.util.ArrayList;

public class DemoTest {
    private LocationHandler locationHandler;
    private MenuHandler menuHandler;
    public void testA(){
        ArrayList<AbstractHandler> abstractHandlers = new ArrayList<>();
        abstractHandlers.add(locationHandler);
        abstractHandlers.add(menuHandler);
    }
}
