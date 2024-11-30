package services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Objects;

public class ArticleParser {
    private String articleTitle;

    public String getArticleTitle() {
        return articleTitle;
    }

    public String getArticleContentFromUrl(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            String article = Objects.requireNonNull(doc.select("section").first()).text();
            String title = Objects.requireNonNull(doc.select("h1").first()).text();
            if (title.equals("News")) {
                title = doc.select("h1").get(1).text();
            }
            this.articleTitle = title;
            return article;
        } catch (IOException e) {
            e.printStackTrace();
            return "Error fetching article.";
        }
    }
}
