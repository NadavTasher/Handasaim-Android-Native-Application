package nadav.tasher.handasaim.architecture.app;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import nadav.tasher.handasaim.R;
import nadav.tasher.handasaim.architecture.appcore.components.Schedule;
import nadav.tasher.lightool.parts.Peer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

import static nadav.tasher.handasaim.architecture.app.Center.request;

public class PreferenceManager {

    private Context context;

    private KeyManager keyManager;
    private CoreManager coreManager;
    private UserManager userManager;

    public PreferenceManager(Context context) {
        this.context = context;
        this.keyManager = new KeyManager(context, context.getSharedPreferences(context.getResources().getString(R.string.preferences_keys), Context.MODE_MULTI_PROCESS));
        this.coreManager = new CoreManager(context, context.getSharedPreferences(context.getResources().getString(R.string.preferences_core), Context.MODE_MULTI_PROCESS));
        this.userManager = new UserManager(context, context.getSharedPreferences(context.getResources().getString(R.string.preferences_user), Context.MODE_MULTI_PROCESS));
    }

    public CoreManager getCoreManager() {
        return coreManager;
    }

    public UserManager getUserManager() {
        return userManager;
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

        public Peer<String> loadKey(final String key) {
            final Peer<String> log = new Peer<>();
            request(new Request.Builder().url(context.getResources().getString(R.string.provider_external_keys)).post(new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("exchange", key).build()).build(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                try {
                                    JSONObject o = new JSONObject(response.body().string());
                                    if (o.getBoolean(context.getString(R.string.key_response_parameter_approved))) {
                                        log.tell(installKey(key, o.getInt(context.getString(R.string.key_response_parameter_type))));
                                    } else {
                                        log.tell("Key does not exist, or already used");
                                    }
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                    log.tell("Key verification failed.");
                                }
                            } else {
                                log.tell("Key verification failed.");
                            }
                        } else {
                            log.tell("Key verification failed.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.tell("Key verification failed.");
                    }
                }
            });
            return log;
        }

        private String installKey(String key, int type) {
            switch (type) {
                case -1:
                    super.set(R.string.preferences_keys_type_beta, key);
                    return "Beta Mode Enabled.";
                case 1:
                    super.set(R.string.preferences_keys_type_news, key);
                    return "News Splash Disabled.";
                case 2:
                    super.set(R.string.preferences_keys_type_teachers, key);
                    return "Teacher Mode Enabled";
                default:
                    break;
            }
            return "Unknown Thing?";
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

        public void renewSchedule(int currentIndex) {
            try {
                JSONArray schedules = new JSONArray(super.get(R.string.preferences_core_json_array, new JSONArray().toString()));
                JSONObject toMove = schedules.getJSONObject(currentIndex);
                JSONArray newSchedules = new JSONArray();
                newSchedules.put(toMove);
                schedules.remove(currentIndex);
                for (int i = 0; i < schedules.length(); i++) {
                    newSchedules.put(schedules.getJSONObject(i));
                }
                super.set(R.string.preferences_core_json_array, newSchedules.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void renewSchedule(Schedule toRenew) {
            ArrayList<Schedule> schedules = getSchedules();
            boolean found = false;
            for (int i = 0; i < schedules.size() && !found; i++) {
                if (schedules.get(i).getOrigin().equals(toRenew.getOrigin())) {
                    renewSchedule(i);
                    found = true;
                }
            }
        }

        public Schedule getSchedule(int index) {
            try {
                JSONArray schedules = new JSONArray(super.get(R.string.preferences_core_json_array, new JSONArray().toString()));
                return (index < schedules.length()) ? Schedule.Builder.fromJSON(schedules.getJSONObject(index)).build() : null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public ArrayList<Schedule> getSchedules() {
            try {
                ArrayList<Schedule> schedules = new ArrayList<>();
                JSONArray schedulesJSON = new JSONArray(super.get(R.string.preferences_core_json_array, new JSONArray().toString()));
                for (int i = 0; i < schedulesJSON.length(); i++) {
                    schedules.add(Schedule.Builder.fromJSON(schedulesJSON.getJSONObject(i)).build());
                }
                return schedules;
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }

        public void addSchedule(Schedule schedule) {
            try {
                JSONArray schedules = new JSONArray(super.get(R.string.preferences_core_json_array, new JSONArray().toString()));
                JSONArray newSchedules = new JSONArray();
                newSchedules.put(schedule.toJSON());
                for (int i = 0; i < schedules.length(); i++) {
                    newSchedules.put(schedules.get(i));
                }
                // Cleanup
                for (int i = context.getResources().getInteger(R.integer.max_storage_schedule); i < newSchedules.length(); i++) {
                    newSchedules.remove(i);
                }
                super.set(R.string.preferences_core_json_array, newSchedules.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void clearSchedules() {
            try {
                JSONArray schedules = new JSONArray(super.get(R.string.preferences_core_json_array, new JSONArray().toString()));
                // Keep the first schedule
                for (int i = 1; i < schedules.length(); i++) {
                    schedules.remove(i);
                }
                super.set(R.string.preferences_core_json_array, schedules.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class UserManager extends Manager.PublicManager {

        public UserManager(Context context, SharedPreferences preferences) {
            super(context, preferences);
        }
    }
}
