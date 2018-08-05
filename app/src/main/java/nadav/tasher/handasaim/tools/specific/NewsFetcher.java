package nadav.tasher.handasaim.tools.specific;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
            Elements slides = document.getAllElements().select("div.slick-slide").select("a");
            if (slides != null && !slides.isEmpty()) {
                for (Element currentSlide : slides) {
                    String title, url;
                    url = currentSlide.attr("href");
                    title = currentSlide.select("div.elementor-slide-content").select("div.elementor-slide-heading").text();
                    if (title != null && !title.isEmpty()) {
                        boolean contains = false;
                        for (Article article : articles) {
                            if (article.getTitle().equals(title)) {
                                contains = true;
                                break;
                            }
                        }
                        if (!contains)
                            articles.add(new Article(title, url));
                    }
                }
            }
            return articles;
        } catch (Exception e) {
            e.printStackTrace();
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

        private String title, url;

        public Article(String title, String url) {
            this.title = title;
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public String getUrl() {
            return url;
        }
    }
}
