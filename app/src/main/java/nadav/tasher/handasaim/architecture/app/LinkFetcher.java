package nadav.tasher.handasaim.architecture.app;

import android.os.AsyncTask;

import nadav.tasher.handasaim.architecture.appcore.AppCore;

public class LinkFetcher extends AsyncTask<String, String, String> {
    private String schedulePage, homePage, github;
    private OnFinish onFinish;

    public LinkFetcher(String schedulePage, String homePage, String github, OnFinish onFinish) {
        this.schedulePage = schedulePage;
        this.homePage = homePage;
        this.github = github;
        this.onFinish = onFinish;
    }

    @Override
    protected String doInBackground(String... strings) {
        return AppCore.getLink(schedulePage, homePage, github);
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
