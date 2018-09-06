package nadav.tasher.handasaim.architecture.app;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Pattern;

public class LinkFetcher extends AsyncTask<String, String, String> {
    private String schedulePage, homePage;
    private OnFinish onFinish;

    public LinkFetcher(String schedulePage, String homePage, OnFinish onFinish) {
        this.schedulePage = schedulePage;
        this.homePage = homePage;
        this.onFinish = onFinish;
    }

    @Override
    protected String doInBackground(String... strings) {
        String file = null;
//                file = "http://handasaim.co.il/wp-content/uploads/2018/08/5-9.xls";
        try {
            // Main Search At Schedule Page
            Document document = Jsoup.connect(schedulePage).get();
            Elements elements = document.select("a");
            for (int i = 0; (i < elements.size() && file == null); i++) {
                String attribute = elements.get(i).attr("href");
                if (attribute.endsWith(".xls") || attribute.endsWith(".xlsx")) {
                    file = attribute;
                }
            }
            // Fallback Search At Home Page
            if (file == null) {
                Document documentFallback = Jsoup.connect(homePage).get();
                Elements elementsFallback = documentFallback.select("a");
                for (int i = 0; (i < elementsFallback.size() && file == null); i++) {
                    String attribute = elementsFallback.get(i).attr("href");
                    //                    Log.i("LinkFallback",attribute);
                    if ((attribute.endsWith(".xls") || attribute.endsWith(".xlsx")) && Pattern.compile("(/.[^a-z]+\\..+)$").matcher(attribute).find()) {
                        file = attribute;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    @Override
    protected void onPostExecute(String s) {
        if (onFinish != null) {
            if (s != null) {
                onFinish.onLinkFetch(s);
            } else {
                onFinish.onFail();
            }
        }
    }

    public interface OnFinish {
        void onLinkFetch(String link);

        void onFail();
    }
}
