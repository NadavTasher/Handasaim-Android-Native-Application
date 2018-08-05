package nadav.tasher.handasaim.architecture.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import nadav.tasher.handasaim.R;
import nadav.tasher.lightool.communication.OnFinish;
import nadav.tasher.lightool.communication.SessionStatus;
import nadav.tasher.lightool.communication.network.Ping;
import nadav.tasher.lightool.communication.network.request.Post;
import nadav.tasher.lightool.communication.network.request.RequestParameter;

public class PreferenceManager {

    private Context context;

    private KeyManager keyManager;
    private CoreManager coreManager;
    private ServicesManager servicesManager;
    private UserManager userManager;

    public PreferenceManager(Context context) {
        this.context = context;
        this.keyManager = new KeyManager(context, context.getSharedPreferences(context.getResources().getString(R.string.preferences_keys), Context.MODE_PRIVATE));
        this.coreManager = new CoreManager(context, context.getSharedPreferences(context.getResources().getString(R.string.preferences_core), Context.MODE_PRIVATE));
        this.servicesManager = new ServicesManager(context, context.getSharedPreferences(context.getResources().getString(R.string.preferences_services), Context.MODE_PRIVATE));
        this.userManager = new UserManager(context, context.getSharedPreferences(context.getResources().getString(R.string.preferences_user), Context.MODE_PRIVATE));
    }

    public CoreManager getCoreManager() {
        return coreManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public ServicesManager getServicesManager() {
        return servicesManager;
    }

    public KeyManager getKeyManager() {
        return keyManager;
    }

    public static class Manager {
        private SharedPreferences preferences;
        private Context context;

        public Manager(Context context, SharedPreferences preferences) {
            this.preferences = preferences;
            this.context = context;
        }

        private String fromResource(int res) {
            return context.getResources().getString(res);
        }

        private boolean get(int res, boolean defaultValue) {
            return preferences.getBoolean(fromResource(res), defaultValue);
        }

        private String get(int res, String defaultValue) {
            return preferences.getString(fromResource(res), defaultValue);
        }

        private int get(int res, int defaultValue) {
            return preferences.getInt(fromResource(res), defaultValue);
        }

        private float get(int res, float defaultValue) {
            return preferences.getFloat(fromResource(res), defaultValue);
        }

        private void set(int res, boolean value) {
            preferences.edit().putBoolean(fromResource(res), value).apply();
        }

        private void set(int res, String value) {
            preferences.edit().putString(fromResource(res), value).apply();
        }

        private void set(int res, int value) {
            preferences.edit().putInt(fromResource(res), value).apply();
        }

        private void set(int res, float value) {
            preferences.edit().putFloat(fromResource(res), value).apply();
        }

        public static class PublicManager extends Manager {

            public PublicManager(Context context, SharedPreferences preferences) {
                super(context, preferences);
            }

            public boolean get(int res, boolean defaultValue) {
                return super.get(res, defaultValue);
            }

            public int get(int res, int defaultValue) {
                return super.get(res, defaultValue);
            }

            public String get(int res, String defaultValue) {
                return super.get(res, defaultValue);
            }

            public float get(int res, float defaultValue) {
                return super.get(res, defaultValue);
            }

            public void set(int res, String value) {
                super.set(res, value);
            }

            public void set(int res, int value) {
                super.set(res, value);
            }

            public void set(int res, float value) {
                super.set(res, value);
            }

            public void set(int res, boolean value) {
                super.set(res, value);
            }
        }
    }

    public class KeyManager extends Manager {

        public KeyManager(Context context, SharedPreferences preferences) {
            super(context, preferences);
        }

        public String getLoadedKey(int unlockType) {
            return super.get(unlockType, "No Key Loaded");
        }

        public boolean isKeyLoaded(int unlockType) {
            return super.get(unlockType, null) != null;
        }

        public void loadKey(final String key) {
            new Ping(context.getResources().getString(R.string.provider_external), 10000, new Ping.OnEnd() {
                @Override
                public void onPing(boolean b) {
                    if (b) {
                        RequestParameter[] requestParameters = new RequestParameter[]{new RequestParameter("deactivate", key)};
                        new Post(context.getResources().getString(R.string.provider_external_keys), requestParameters, new OnFinish() {
                            @Override
                            public void onFinish(SessionStatus sessionStatus) {
                                if (sessionStatus.getStatus() == SessionStatus.FINISHED_SUCCESS) {
                                    if (sessionStatus.getExtra() != null) {
                                        if (!sessionStatus.getExtra().isEmpty()) {
                                            try {
                                                JSONObject o = new JSONObject(sessionStatus.getExtra());
                                                if (o.getBoolean("success")) {
                                                    if (o.getString("key").equals(key)) {
                                                        installKey(key, o.getInt("type"));
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
                                        } else {
                                            Toast.makeText(context, "Key provider reply error.", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(context, "Key provider reply error.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(context, "Key provider reply error.", Toast.LENGTH_SHORT).show();
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

        private void installKey(String key, int type) {
            switch (type) {
                case -1:
                    super.set(R.string.preferences_keys_type_beta, key);
                    Toast.makeText(context, "Beta Mode Enabled.", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    super.set(R.string.preferences_keys_type_news, key);
                    Toast.makeText(context, "News Splash Disabled.", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    super.set(R.string.preferences_keys_type_teachers, key);
                    Toast.makeText(context, "Teacher Mode Enabled.", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

    public class CoreManager extends Manager {

        public CoreManager(Context context, SharedPreferences preferences) {
            super(context, preferences);
        }

        public String getMode() {
            return super.get(R.string.preferences_core_mode, super.fromResource(R.string.core_mode_student));
        }

        public void setMode(int modeRes) {
            super.set(R.string.preferences_core_mode, super.fromResource(modeRes));
        }

        public String getDate() {
            return super.get(R.string.preferences_core_file_date, null);
        }

        public void setDate(String date) {
            super.set(R.string.preferences_core_file_date, date);
        }

        public String getFile() {
            return super.get(R.string.preferences_core_file_name, null);
        }

        public void setFile(String name) {
            super.set(R.string.preferences_core_file_name, name);
        }
    }

    public class ServicesManager extends Manager {

        public ServicesManager(Context context, SharedPreferences preferences) {
            super(context, preferences);
        }

        public boolean getPushDisplayedAlready(String id) {
            // Check If The Push Was Displayed Already.
            String jsonString = super.get(R.string.preferences_services_push_received_pushes, new JSONArray().toString());
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                boolean displayed = false;
                for (int i = 0; (i < jsonArray.length()) && !displayed; i++) {
                    if (jsonArray.getString(i).equals(id)) displayed = true;
                }
                return displayed;
            } catch (JSONException e) {
                e.printStackTrace();
                // If There's An Error, Don't Tell The App To Display The Push - This Will Annoy The User.
                return true;
            }
        }

        public void setPushDisplayedAlready(String id) {
            if (!getPushDisplayedAlready(id)) {
                String jsonString = super.get(R.string.preferences_services_push_received_pushes, new JSONArray().toString());
                JSONArray jsonArray;
                try {
                    jsonArray = new JSONArray(jsonString);
                } catch (JSONException e) {
                    e.printStackTrace();
                    // If There's An Error, Rewrite The Database.
                    jsonArray = new JSONArray();
                }
                jsonArray.put(id);
                super.set(R.string.preferences_services_push_received_pushes, jsonArray.toString());
            }
        }

        public boolean getScheduleNotifiedAlready(String date) {
            return super.get(R.string.preferences_services_refresh_file_date, "").equals(date);
        }

        public void setScheduleNotifiedAlready(String date) {
            if (!getScheduleNotifiedAlready(date)) {
                super.set(R.string.preferences_services_refresh_file_date, date);
            }
        }

        public void setChannel(int channel) {
            super.set(R.string.preferences_services_push_channel, channel);
        }

        public int getChannel(int defaultValue) {
            return super.get(R.string.preferences_services_push_channel, defaultValue);
        }
    }

    public class UserManager extends Manager.PublicManager {

        public UserManager(Context context, SharedPreferences preferences) {
            super(context, preferences);
        }
    }
}
