package nadav.tasher.handasaim.tools.online;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.URL;

public class PictureLoader extends AsyncTask<String, String, Bitmap> {

    private String furl;
    private GotImage ong;

    public PictureLoader(String url, GotImage og) {
        furl = url;
        ong = og;
    }

    @Override
    protected Bitmap doInBackground(String... comment) {
        try {
            URL url = new URL(furl);
            return BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            //                e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (ong != null) {
            ong.onGet(bitmap);
        }
    }

    public interface GotImage {
        void onGet(Bitmap image);
    }
}
