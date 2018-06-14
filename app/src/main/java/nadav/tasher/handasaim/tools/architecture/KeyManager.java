package nadav.tasher.handasaim.tools.architecture;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import nadav.tasher.handasaim.values.Values;
import nadav.tasher.lightool.communication.OnFinish;
import nadav.tasher.lightool.communication.SessionStatus;
import nadav.tasher.lightool.communication.network.Ping;
import nadav.tasher.lightool.communication.network.request.Post;
import nadav.tasher.lightool.communication.network.request.RequestParameter;

public class KeyManager {

    private Context context;
    private SharedPreferences sp;
    public static final String TYPE_TEACHER_MODE="teacher_mode_v1";
    public static final String TYPE_MESSAGE_BOARD="message_board_v1";
    public static final String TYPE_BETA="beta_v1";
    public KeyManager(Context context){
        this.context=context;
        this.sp=context.getSharedPreferences(Values.keyPrefName,Context.MODE_PRIVATE);
    }

    public String getLoadedKey(String unlockType){
        return sp.getString(unlockType,"No Key Loaded");
    }

    public boolean isKeyLoaded(String unlockType){
        return sp.getString(unlockType,null)!=null;

    }

    public void loadKey(final String key){
        new Ping(Values.puzProvider, 10000, new Ping.OnEnd() {
            @Override
            public void onPing(boolean b) {
                if (b) {
                    RequestParameter[] requestParameters = new RequestParameter[]{new RequestParameter("deactivate", key)};
                    new Post(Values.keyProvider, requestParameters, new OnFinish() {
                        @Override
                        public void onFinish(SessionStatus sessionStatus) {
                            try {
                                JSONObject o = new JSONObject(sessionStatus.getExtra());
                                if (o.getBoolean("success")) {
                                    if (o.getString("key").equals(key)) {
                                        installKey(key,o.getInt("type"));
                                    } else {
                                        Toast.makeText(context, "Key comparison failed.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(context, "Key does not exist, or already used", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(context, "Key verification failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    ).execute();
                } else {
                    Toast.makeText(context, "Key provider unreachable.", Toast.LENGTH_SHORT).show();
                }
            }
        }).execute();
    }

    private void installKey(String key,int type){
        switch (type) {
            case -1:
                Toast.makeText(context, "Beta Mode Enabled.", Toast.LENGTH_SHORT).show();
                sp.edit().putString(TYPE_BETA, key).commit();
                break;
            case 0:
                Toast.makeText(context, "Dummy Key! This Key Is Useless!", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                sp.edit().putString(TYPE_MESSAGE_BOARD, key).commit();
                Toast.makeText(context, "News Splash Disabled.", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(context, "Teacher Mode Enabled.", Toast.LENGTH_SHORT).show();
                sp.edit().putString(TYPE_TEACHER_MODE,key).commit();
                break;
            default:
                break;
        }
    }
}
