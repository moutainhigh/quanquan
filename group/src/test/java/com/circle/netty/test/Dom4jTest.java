package com.circle.netty.test;

import com.circle.netty.http.Urls;
import org.dom4j.DocumentException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


/**
 * Url 配置解析
 * @author Created by cxx on 15-7-21.
 */
public class Dom4jTest {
    @Test
    public void testReadXml() throws DocumentException {
        List<Integer> ints = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            ints.add(i);
        }
        System.out.println(ints.subList(0,99));
    }
    @Test
    public void testUrls() throws DocumentException {
        Urls.create("config/uris_filter.xml");
        System.out.println(Urls.getUrls().uris());

        System.out.println("Desc : "+Urls.getUrls().get("/user/register").desc);
    }
}
