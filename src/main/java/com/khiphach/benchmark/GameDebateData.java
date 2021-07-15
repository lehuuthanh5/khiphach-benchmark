package com.khiphach.benchmark;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class GameDebateData {
    public void main(String... strings) throws IOException {
        Document document = Jsoup.connect("https://game-debate.com/sitemap/Shooter.php").userAgent("Mozilla").timeout(1000000).get();
        Set<String> link = getCollect(document);
        document = Jsoup.connect("https://game-debate.com/sitemap/Strategy.php").userAgent("Mozilla").timeout(1000000).get();
        link.addAll(getCollect(document));
        document = Jsoup.connect("https://game-debate.com/sitemap/RPG.php").userAgent("Mozilla").timeout(1000000).get();
        link.addAll(getCollect(document));
        document = Jsoup.connect("https://game-debate.com/sitemap/Arcade.php").userAgent("Mozilla").timeout(1000000).get();
        link.addAll(getCollect(document));
        document = Jsoup.connect("https://game-debate.com/sitemap/Sport.php").userAgent("Mozilla").timeout(1000000).get();
        link.addAll(getCollect(document));
        document = Jsoup.connect("https://game-debate.com/sitemap/Sport.php").userAgent("Mozilla").timeout(1000000).get();
        link.addAll(getCollect(document));
        document = Jsoup.connect("https://game-debate.com/sitemap/Stealth.php").userAgent("Mozilla").timeout(1000000).get();
        link.addAll(getCollect(document));
        document = Jsoup.connect("https://game-debate.com/sitemap/Management.php").userAgent("Mozilla").timeout(1000000).get();
        link.addAll(getCollect(document));
        document = Jsoup.connect("https://game-debate.com/sitemap/Racing.php").userAgent("Mozilla").timeout(1000000).get();
        link.addAll(getCollect(document));
        document = Jsoup.connect("https://game-debate.com/sitemap/Puzzler.php").userAgent("Mozilla").timeout(1000000).get();
        link.addAll(getCollect(document));
        document = Jsoup.connect("https://game-debate.com/sitemap/Horror.php").userAgent("Mozilla").timeout(1000000).get();
        link.addAll(getCollect(document));
        document = Jsoup.connect("https://game-debate.com/sitemap/Crime.php").userAgent("Mozilla").timeout(1000000).get();
        link.addAll(getCollect(document));
        document = Jsoup.connect("https://game-debate.com/sitemap/Historic.php").userAgent("Mozilla").timeout(1000000).get();
        link.addAll(getCollect(document));
        document = Jsoup.connect("https://game-debate.com/sitemap/Fantasy.php").userAgent("Mozilla").timeout(1000000).get();
        link.addAll(getCollect(document));
        ExecutorService executorService = Executors.newFixedThreadPool(50);
        link.forEach(s -> executorService.execute(() -> {
            System.out.println("Process Link: " + s);
            RestTemplate restTemplate = new RestTemplate();
            try {
                restTemplate.getForEntity("https://kp-benchmark.herokuapp.com/game/create-by-link?link=" + URLEncoder.encode(s, "UTF-8"), String.class);
            } catch (UnsupportedEncodingException e) {
                System.out.println("Link error: " + s + ", Exception: " + e.getMessage());
            }
        }));
    }

    private static Set<String> getCollect(Document document) {
        return document.getElementsByTag("a").stream().map(a -> a.attr("href"))
                .filter(text -> text.startsWith("../games/index.php"))
                .map(text -> text.replace("../games/index.php", "https://game-debate.com/games/index.php"))
                .collect(Collectors.toSet());
    }
}
