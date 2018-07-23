package nadav.tasher.handasaim.activities;

import android.app.Activity;
import android.os.Bundle;

import nadav.tasher.handasaim.architecture.app.Center;

public class TutorialActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Center.enter(this,HomeActivity.class);
    }
}
