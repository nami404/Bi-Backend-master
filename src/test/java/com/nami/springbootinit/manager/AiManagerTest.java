package com.nami.springbootinit.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.nami.springbootinit.manager.AiManager;

import javax.annotation.Resource;


@SpringBootTest
class AiManagerTest {

    @Resource
    private AiManager aiManager;

    @Test
    public void testXingHuo() {
        String c = "分析需求：\n" +
                "分析网站用户的增长情况 \n" +
                "请使用 柱状图 \n" +
                "原始数据：\n" +
                "日期,用户数\n" +
                "1号,10\n" +
                " 2号,20\n" +
                " 3号,30";
        String s = aiManager.sendMsgToXingHuo(true, c);
        System.out.println("s = " + s);
    }

    @Test
    public void testDeepseek() {
        String c = "分析需求：\n" +
                "分析网站用户的增长情况 \n" +
                "请使用 柱状图 \n" +
                "原始数据：\n" +
                "日期,用户数\n" +
                "1号,10\n" +
                " 2号,20\n" +
                " 3号,30";
        String s = aiManager.sendMsgToDeepseek(c);
        System.out.println("s = " + s);
    }
}