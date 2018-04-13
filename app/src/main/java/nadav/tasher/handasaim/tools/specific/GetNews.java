package nadav.tasher.handasaim.tools.specific;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class GetNews extends AsyncTask<String, String, ArrayList<GetNews.Link>> {
    private String ser;
    private GotNews gotlink;
    private boolean success;

    public GetNews(String service, GotNews gl) {
        ser = service;
        gotlink = gl;
    }

    @Override
    protected ArrayList<Link> doInBackground(String... strings) {
        try {
            ArrayList<Link> file = new ArrayList<>();
            Document docu = Jsoup.connect(ser).get();
            Elements ahs = docu.getAllElements().select("div.carousel-inner").select("div.item");
            for (int in = 0; in < ahs.size(); in++) {
                Link link = new Link();
                link.name = ahs.get(in).select("a").first().text();
                link.url = ahs.get(in).select("a").first().attr("href");
                link.imgurl = ahs.get(in).select("img").attr("src");
                boolean doesContain = false;
                for (int containC = 0; containC < file.size(); containC++) {
                    if (file.get(containC).name.equals(link.name)) {
                        doesContain = true;
                        break;
                    }
                }
                if (!link.name.equals("") && !doesContain) file.add(link);
            }
            success = true;
            return file;
        } catch (IOException e) {
            success = false;
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<Link> s) {
        if (success) {
            if (gotlink != null) gotlink.onNewsGet(s);
        } else {
            if (gotlink != null) gotlink.onFail(s);
        }
    }

    public interface GotNews {
        void onNewsGet(ArrayList<Link> link);

        void onFail(ArrayList<Link> e);
    }

    public static class Link {
        public String url, name, imgurl;
    }
}
