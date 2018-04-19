package nadav.tasher.handasaim.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import nadav.tasher.handasaim.R;
import nadav.tasher.handasaim.values.Values;

public class Developer extends Activity {

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStageA();
    }

    private void initStageA(){
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
        sp = getSharedPreferences(Values.prefName, Context.MODE_PRIVATE);
        initStageB();
    }

    private void initStageB(){
        LinearLayout all=new LinearLayout(getApplicationContext());
        all.setOrientation(LinearLayout.VERTICAL);
        all.setGravity(Gravity.CENTER);
        all.setPadding(10,10,10,10);
        all.setBackground(Main.getGradient(getApplicationContext()));
        getWindow().setStatusBarColor(Main.getColorA(getApplicationContext()));
        getWindow().setNavigationBarColor(Main.getColorB(getApplicationContext()));
        Button loadScript=new Button(this);
        Button runSpecific=new Button(this);
        Button viewVariables=new Button(this);

        loadScript.setText(R.string.developer_load_new);
        runSpecific.setText(R.string.developer_run_specific);
        viewVariables.setText(R.string.developer_view_current_variables);

        loadScript.setAllCaps(false);
        runSpecific.setAllCaps(false);
        viewVariables.setAllCaps(false);

        loadScript.setBackground(Main.generateCoaster(getApplicationContext(),Values.classCoasterColor));
        runSpecific.setBackground(Main.generateCoaster(getApplicationContext(),Values.classCoasterColor));
        viewVariables.setBackground(Main.generateCoaster(getApplicationContext(),Values.classCoasterColor));

        loadScript.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Coming Soon",Toast.LENGTH_LONG).show();
            }
        });
        runSpecific.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Coming Soon",Toast.LENGTH_LONG).show();
            }
        });
        viewVariables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Coming Soon",Toast.LENGTH_LONG).show();
            }
        });

        all.addView(loadScript);
        all.addView(runSpecific);
        all.addView(viewVariables);

        setContentView(all);
    }

    public static void startMe(Activity c) {
        Intent intent = new Intent(c, Developer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        c.startActivity(intent);
        c.overridePendingTransition(R.anim.out, R.anim.in);
        c.finish();
    }

    @Override
    public void onBackPressed() {
        Main.returnToMe(this);
        super.onBackPressed();
    }
}
