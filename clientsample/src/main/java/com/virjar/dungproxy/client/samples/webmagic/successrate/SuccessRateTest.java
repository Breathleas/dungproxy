package com.virjar.dungproxy.client.samples.webmagic.successrate;

import java.io.IOException;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.virjar.dungproxy.client.ippool.config.ProxyConstant;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * Created by virjar on 17/1/14.<br/>
 * 360搜索的爬虫,是https的链接<br/>
 * 产生成功率的报告
 */
public class SuccessRateTest implements PageProcessor {
    private static Site site = Site.me()// .setHttpProxy(new HttpHost("127.0.0.1",8888))
            .setRetryTimes(3) // 就我的经验,这个重试一般用处不大
            .setTimeOut(30000)// 在使用代理的情况下,这个需要设置,可以考虑调大线程数目
            .setSleepTime(0)// 使用代理了之后,代理会通过切换IP来防止反扒。同时,使用代理本身qps降低了,所以这个可以小一些
            .setCycleRetryTimes(3)// 这个重试会换IP重试,是setRetryTimes的上一层的重试,不要怕三次重试解决一切问题。。
            .setUseGzip(true);

    public SuccessRateTest() throws IOException {
    }

    public static void main(String[] args) throws IOException {
        ProxyConstant.CLIENT_CONFIG_FILE_NAME = "proxyclient_soso.properties";// 加载360的配置

        Spider.create(new SuccessRateTest()).setDownloader(new SuccessRateTestDownloader()).thread(50)
                .addUrl("https://www.so.com/s?q=%E8%8C%89%E8%8E%89&pn=9")
                .addUrl("https://www.so.com/s?q=%E6%A0%80%E5%AD%90%E8%8A%B1&pn=9").start();

    }

    @Override
    public void process(Page page) {
        Elements a = page.getHtml().getDocument().getElementsByTag("a");
        for (Element el : a) {
            String href = el.absUrl("href");

            if (needAddToTarget(href)) {
                page.addTargetRequest(href);
            }
        }

    }

    private boolean needAddToTarget(String url) {
        if (url.endsWith(".png") || url.endsWith(".jpg") || url.endsWith(".gif")) {
            return false;
        }
        return url.contains("www.so.com");
    }

    @Override
    public Site getSite() {
        return site;
    }
}
