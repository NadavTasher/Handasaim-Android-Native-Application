package nadav.tasher.handasaim.tools.specific;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class NewsFetcher extends AsyncTask<String, String, ArrayList<NewsFetcher.Article>> {
    private String url;
    private OnFinish onFinish;

    public NewsFetcher(String url, OnFinish onFinish) {
        this.url = url;
        this.onFinish = onFinish;
    }

    @Override
    protected ArrayList<Article> doInBackground(String... strings) {
        try {
            ArrayList<Article> articles = new ArrayList<>();
            Document document = Jsoup.connect(url).get();
            Elements slides = document.getAllElements().select("div.slick-track").select("div.slick-slide");
            for (Element currentSlide:slides) {
                String title,url;
                url=currentSlide.select("a").first().attr("href");
                title=currentSlide.select("a").first().select("div.elementor-slide-content").first().select("div.elementor-slide-description").first().text();
                articles.add(new Article(title,url));
            }
            return articles;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<Article> arrayList) {
        if (onFinish != null) {
            if (arrayList != null) {
                onFinish.onNewsFetch(arrayList);
            } else {
                onFinish.onFail();
            }
        }
    }

    public interface OnFinish {
        void onNewsFetch(ArrayList<Article> articles);

        void onFail();
    }

    public static class Article {

        private String title,url;

        public Article (String title,String url){
            this.title=title;
            this.url=url;
        }

        public String getTitle() {
            return title;
        }

        public String getUrl() {
            return url;
        }
    }
}
