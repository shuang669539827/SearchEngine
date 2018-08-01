
package com.lcc.searchengine.spider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 *
 */
public class HuaxinPageProcesser implements PageProcessor {
    
    public static final String NEWS_LIST = "http://blog\\.csdn\\.net/.*/article/list/\\d+$";
    public static final String NEWS_POST = "http://blog\\.csdn\\.net/.*/article/details/\\d+$";
//    public static final String NEWS_SITE = "http://blog\\.csdn\\.net/isea533.*";
    public static final String NEWS_SITE = "http://blog\\.csdn\\.net/.*";

    @Override
    public void process(Page page) {
        Document document = Jsoup.parse(page.getHtml().toString());
        if (page.getUrl().regex(NEWS_LIST).match()) {
            Elements elements = document.select(".link_title a");
            for (Element element : elements) {
                page.addTargetRequest(element.attr("href"));
            }
            page.setSkip(true);
        } else if (page.getUrl().regex(NEWS_POST).match()) {
            String title = document.select("#mainBox > main > div.blog-content-box > div.article-title-box > h1").text();
            String url = page.getUrl().toString();
            String content = document.select("#article_content > div").text();
            page.putField("title", title);
            System.out.println("标题 ："+title);
            page.putField("url", url);
            page.putField("content", content);
        } else  {
            page.addTargetRequests(page.getHtml().links().regex(NEWS_SITE).all());
            page.setSkip(true);
        }
    }

    public static void main(String[] args) {
        Spider.create(new HuaxinPageProcesser()).addUrl("https://blog.csdn.net/")
                .addPipeline(new LucenePipeline())
                .thread(3).run();
    }

    @Override
    public Site getSite() {
        return Site.me().setDomain(" http://blog.csdn.net/isea533").setRetryTimes(8).setSleepTime(500);
    }
}
