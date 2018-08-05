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
        try {
            Document docu = Jsoup.connect(url).get();
            Elements doc = docu.select("a");
            // TODO Remove This Line After Summer Vaccation
            file = "http://nockio.com/handasaim/schedulearchives/15-5.xls";
            // TODO Remove This ^
            for (int i = 0; (i < doc.size() && file == null); i++) {
                if (doc.get(i).attr("href").endsWith(".xls") || doc.get(i).attr("href").endsWith(".xlsx")) {
                    file = doc.get(i).attr("href");
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
