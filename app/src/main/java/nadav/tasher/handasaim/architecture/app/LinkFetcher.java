package nadav.tasher.handasaim.architecture.app;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class LinkFetcher extends AsyncTask<String, String, String> {
    private String url;
    private OnFinish onFinish;

    public LinkFetcher(String url, OnFinish onFinish) {
        this.url = url;
        this.onFinish = onFinish;
    }

    @Override
    protected String doInBackground(String... strings) {
        String file = null;
        // TODO remove before release
        file = "http://nockio.com/h/schedulearchives/15-5.xls";
        try {
            Document document = Jsoup.connect(url).get();
            Elements elements = document.select("a");
            for (int i = 0; (i < elements.size() && file == null); i++) {
                if (elements.get(i).attr("href").endsWith(".xls") || elements.get(i).attr("href").endsWith(".xlsx")) {
                    file = elements.get(i).attr("href");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    @Override
    protected void onPostExecute(String s) {
        if(onFinish!=null) {
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
