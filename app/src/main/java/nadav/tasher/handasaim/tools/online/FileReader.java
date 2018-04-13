package nadav.tasher.handasaim.tools.online;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class FileReader extends AsyncTask<String, String, String> {
    private String furl;
    private OnRead oe;

    public FileReader(String url, OnRead onfile) {
        oe = onfile;
        furl = url;
    }

    @Override
    protected String doInBackground(String... comment) {
        String s = "";
        try {
            URL url = new URL(furl);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str;
            while ((str = in.readLine()) != null) {
                s += str + "\n";
            }
            in.close();
        } catch (IOException e) {
            s = null;
        }
        return s;
    }

    @Override
    protected void onPostExecute(String file_url) {
        if (oe != null) {
            oe.done(file_url);
        }
    }

    public interface OnRead {
        void done(String s);
    }
}
