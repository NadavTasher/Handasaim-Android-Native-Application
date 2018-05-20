package nadav.tasher.handasaim.activities.framables;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import nadav.tasher.handasaim.R;
import nadav.tasher.handasaim.architecture.app.Framable;
import nadav.tasher.handasaim.architecture.development.SupportLibrary;
import nadav.tasher.handasaim.tools.architecture.KeyManager;
import nadav.tasher.handasaim.values.Values;
import nadav.tasher.jsons.Library;
import nadav.tasher.jsons.Script;
import nadav.tasher.jsons.Variable;
import nadav.tasher.lightool.info.Device;

public class Developer extends Framable {

    private Script s;

    public Developer(Activity a, SharedPreferences sp, KeyManager keyManager) {
        super(a, sp, keyManager);
    }

    @Override
    public void go() {
        LinearLayout all = new LinearLayout(getApplicationContext());
        all.setOrientation(LinearLayout.VERTICAL);
        all.setGravity(Gravity.CENTER);
        all.setPadding(10, 10, 10, 10);
        all.setBackground(Main.getGradient(getApplicationContext()));
        getWindow().setStatusBarColor(Main.getColorA(getApplicationContext()));
        getWindow().setNavigationBarColor(Main.getColorB(getApplicationContext()));
        Button loadScript = new Button(getApplicationContext());
        Button runSpecific = new Button(getApplicationContext());
        Button viewVariables = new Button(getApplicationContext());
        loadScript.setText(R.string.developer_load_new);
        runSpecific.setText(R.string.developer_run_specific);
        viewVariables.setText(R.string.developer_view_current_variables);
        loadScript.setAllCaps(false);
        runSpecific.setAllCaps(false);
        viewVariables.setAllCaps(false);
        loadScript.setBackground(Main.generateCoaster(getApplicationContext(), Values.classCoasterColor));
        runSpecific.setBackground(Main.generateCoaster(getApplicationContext(), Values.classCoasterColor));
        viewVariables.setBackground(Main.generateCoaster(getApplicationContext(), Values.classCoasterColor));
        loadScript.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupLoad();
            }
        });
        runSpecific.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupRun();
            }
        });
        viewVariables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupStatics();
            }
        });
        all.addView(loadScript);
        all.addView(runSpecific);
        all.addView(viewVariables);
        setContentView(all);
    }

    private void popupRun() {
        AlertDialog.Builder pop = new AlertDialog.Builder(a);
        pop.setCancelable(true);
        pop.setTitle("Run Function");
        pop.setMessage("Enter Your Function Name:");
        final EditText key = new EditText(getApplicationContext());
        FrameLayout f = new FrameLayout(getApplicationContext());
        f.setPadding(50, 10, 50, 10);
        f.addView(key);
        key.setHint("Name");
        pop.setView(f);
        key.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        pop.setPositiveButton("Run", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    s.run(key.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        pop.setNegativeButton("Close", null);
        pop.show();
    }

    private void popupStatics() {
        AlertDialog.Builder pop = new AlertDialog.Builder(a);
        pop.setCancelable(true);
        pop.setTitle("View Statics");
        StringBuilder b = new StringBuilder();
        if (s!=null&&s.statics != null) {
            for (int i = 0; i < s.statics.size(); i++) {
                b.append(s.statics.get(i).name).append(" - ").append(s.statics.get(i).bareValue).append("\n");
            }
        }
        pop.setMessage(b.toString());
        pop.setNegativeButton("Close", null);
        pop.show();
    }

    private void popupLoad() {
        AlertDialog.Builder pop = new AlertDialog.Builder(a);
        pop.setCancelable(true);
        pop.setTitle("Load Script");
        pop.setMessage("Enter Your JSONScripting Script:");
        final EditText key = new EditText(getApplicationContext());
        FrameLayout f = new FrameLayout(getApplicationContext());
        f.setPadding(50, 10, 50, 10);
        key.setHint("Script");
        key.setSingleLine(true);
        //        ScrollView sv=new ScrollView(getApplicationContext());
        //        sv.addView(key);
        //        sv.setFillViewport(true);
        //        sv.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,Device.screenY(Developer.this)/2));
        f.addView(key);
        pop.setView(f);
        key.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        pop.setPositiveButton("Load", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    sp.edit().putString("expirimental-beta-script", key.getText().toString()).apply();
                    Library[] libs = new Library[]{SupportLibrary.getFullSupport(a)};
                    s = new Script(key.getText().toString(), new ArrayList<>(Arrays.asList(libs)));
                    s.addStatic(new Variable("versioncode", String.valueOf(Device.getVersionCode(getApplicationContext(), getApplicationContext().getPackageName()))));
                    s.addStatic(new Variable("versionname", String.valueOf(Device.getVersionName(getApplicationContext(), getApplicationContext().getPackageName()))));
                    Toast.makeText(getApplicationContext(), "Script Loaded.", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed Loading Script.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        pop.setNegativeButton("Close", null);
        pop.show();
    }

    @Override
    public void onBackPressed() {
        Main main = new Main(a, sp, keyManager);
        main.start();
    }
}
