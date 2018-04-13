package nadav.tasher.handasaim.activities;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import nadav.tasher.handasaim.R;
import nadav.tasher.handasaim.architecture.StudentClass;
import nadav.tasher.handasaim.architecture.Teacher;
import nadav.tasher.handasaim.tools.architecture.Starter;
import nadav.tasher.handasaim.tools.online.FileDownloader;
import nadav.tasher.handasaim.tools.specific.GetLink;
import nadav.tasher.handasaim.tools.specific.GetNews;
import nadav.tasher.handasaim.values.Filters;
import nadav.tasher.handasaim.values.Values;
import nadav.tasher.lightool.communication.OnFinish;
import nadav.tasher.lightool.communication.SessionStatus;
import nadav.tasher.lightool.communication.Tunnel;
import nadav.tasher.lightool.communication.network.Ping;
import nadav.tasher.lightool.communication.network.request.Post;
import nadav.tasher.lightool.communication.network.request.RequestParameter;
import nadav.tasher.lightool.graphics.ColorFadeAnimation;
import nadav.tasher.lightool.graphics.views.AppView;
import nadav.tasher.lightool.graphics.views.ColorPicker;
import nadav.tasher.lightool.graphics.views.DragNavigation;
import nadav.tasher.lightool.info.Device;
import nadav.tasher.lightool.tools.Animations;

import static nadav.tasher.handasaim.tools.architecture.AppCore.getBreak;
import static nadav.tasher.handasaim.tools.architecture.AppCore.getGrade;
import static nadav.tasher.handasaim.tools.architecture.AppCore.getRealEndTimeForHourNumber;
import static nadav.tasher.handasaim.tools.architecture.AppCore.getRealTimeForHourNumber;
import static nadav.tasher.handasaim.tools.architecture.AppCore.getTeacherSchudleForClasses;
import static nadav.tasher.handasaim.tools.architecture.AppCore.getTimeForLesson;
import static nadav.tasher.handasaim.tools.architecture.AppCore.minuteOfDay;
import static nadav.tasher.handasaim.tools.architecture.AppCore.readExcelFile;
import static nadav.tasher.handasaim.tools.architecture.AppCore.readExcelFileXLSX;
import static nadav.tasher.handasaim.tools.architecture.Starter.beginDND;

public class Main extends Activity {
    static int textColor = Values.fontColorDefault;
    static Tunnel<Integer> colorChangeTunnle = new Tunnel<>();
    static Tunnel<Integer> fontSizeChangeTunnle = new Tunnel<>();
    static Tunnel<Boolean> breakTimeTunnle = new Tunnel<>();
    private int colorA = Values.defaultColorA;
    private int colorB = Values.defaultColorB;
    private int keyentering = 0;
    private String day;
    private StudentClass currentClass;
    private Teacher currentTeacher;
    private MyGraphics.OptionHolder optionHolder;
    private MyGraphics.CurvedTextView ctv;
    private MyGraphics.CircleView circleView;
    private Drawable gradient, coaster, classCoaster, classCoasterMarked;
    private AppView mAppView;
    private ArrayList<StudentClass> classes;
    private ArrayList<Teacher> teachers;
    private String[] ees = new String[]{"Love is like the wind, you can't see it but you can feel it.", "I'm not afraid of death; I just don't want to be there when it happens.", "All you need is love. But a little chocolate now and then doesn't hurt.", "When the power of love overcomes the love of power the world will know peace.", "For every minute you are angry you lose sixty seconds of happiness.", "Yesterday is history, tomorrow is a mystery, today is a gift of God, which is why we call it the present.", "The fool doth think he is wise, but the wise man knows himself to be a fool.", "In three words I can sum up everything I've learned about life: it goes on.", "You only live once, but if you do it right, once is enough.", "Two things are infinite: the universe and human stupidity; and I'm not sure about the universe.", "Life is pleasant. Death is peaceful. It's the transition that's troublesome.", "There are only two ways to live your life. One is as though nothing is a miracle. The other is as though everything is a miracle.", "We are not retreating - we are advancing in another Direction.", "The difference between fiction and reality? Fiction has to make sense.", "The right to swing my fist ends where the other man's nose begins.", "Denial ain't just a river in Egypt.", "Every day I get up and look through the Forbes list of the richest people in America. If I'm not there, I go to work.", "Advice is what we ask for when we already know the answer but wish we didn't", "The nice thing about egotists is that they don't talk about other people.", "Obstacles are those frightful things you see when you take your eyes off your goal.", "You can avoid reality, but you cannot avoid the consequences of avoiding reality.", "You may not be interested in war, but war is interested in you.", "Don't stay in bed, unless you can make money in bed.", "C makes it easy to shoot yourself in the foot; C++ makes it harder, but when you do, it blows away your whole leg.", "I have not failed. I've just found 10,000 ways that won't work.", "Black holes are where God divided by zero.", "The significant problems we face cannot be solved at the same level of thinking we were at when we created them.", "Knowledge speaks, but wisdom listens.", "Sleep is an excellent way of listening to an opera.", "Success usually comes to those who are too busy to be looking for it"};
    private String[] infact = new String[]{"Every year more than 2500 left-handed people are killed from using right-handed products.", "In 1895 Hampshire police handed out the first ever speeding ticket, fining a man for doing 6mph!", "Over 1000 birds a year die from smashing into windows.", "Squirrels forget where they hide about half of their nuts.", "The average person walks the equivalent of twice around the world in a lifetime.", "A company in Taiwan makes dinnerware out of wheat, so you can eat your plate!", "An apple, potato, and onion all taste the same if you eat them with your nose plugged.", "Dying is illegal in the Houses of Parliaments – This has been voted as the most ridiculous law by the British citizens.", "The first alarm clock could only ring at 4am.", "If you leave everything to the last minute… it will only take a minute.", "Every human spent about half an hour as a single cell.", "The Twitter bird actually has a name – Larry.", "Sea otters hold hands when they sleep so they don’t drift away from each other.", "The French language has seventeen different words for ‘surrender’.", "The Titanic was the first ship to use the SOS signal.", "A baby octopus is about the size of a flea when it is born.", "You cannot snore and dream at the same time.", "A toaster uses almost half as much energy as a full-sized oven.", "If you consistently fart for 6 years & 9 months, enough gas is produced to create the energy of an atomic bomb!", "An eagle can kill a young deer and fly away with it.", "Polar bears can eat as many as 86 penguins in a single sitting.", "If Pinokio says “My Nose Will Grow Now”, it would cause a paradox.", "Bananas are curved because they grow towards the sun.", "Human saliva has a boiling point three times that of regular water.", "Cherophobia is the fear of fun.", "When hippos are upset, their sweat turns red.", "Pteronophobia is the fear of being tickled by feathers!", "Banging your head against a wall burns 150 calories an hour."};
    private boolean breakTime = true;

    public static int getFontSize(Context c) {
        final SharedPreferences sp = c.getSharedPreferences(Values.prefName, Context.MODE_PRIVATE);
        return sp.getInt(Values.fontSizeNumber, Values.fontSizeDefault);
    }

    public static int getColorA(Context c) {
        final SharedPreferences sp = c.getSharedPreferences(Values.prefName, Context.MODE_PRIVATE);
        return sp.getInt(Values.colorA, Values.defaultColorA);
    }

    public static int getColorB(Context c) {
        final SharedPreferences sp = c.getSharedPreferences(Values.prefName, Context.MODE_PRIVATE);
        return sp.getInt(Values.colorB, Values.defaultColorB);
    }

    public static Typeface getTypeface(Context c) {
        return Typeface.createFromAsset(c.getAssets(), Values.fontName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startApp();
    }

    private void loadTheme() {
        final SharedPreferences sp = getSharedPreferences(Values.prefName, Context.MODE_PRIVATE);
        textColor = sp.getInt(Values.fontColor, Values.fontColorDefault);
        colorA = sp.getInt(Values.colorA, colorA);
        colorB = sp.getInt(Values.colorB, colorB);
        gradient = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{colorA, colorB});
        coaster = generateCoaster(Color.argb(Values.circleAlpha, Color.red(colorA), Color.green(colorA), Color.blue(colorA)));
        classCoaster = generateCoaster(Values.classCoasterColor);
        classCoasterMarked = generateCoaster(Values.classCoasterMarkColor);
    }

    private void refreshTheme() {
        loadTheme();
        mAppView.setBackground(gradient);
        mAppView.setTopColor(colorA);
        mAppView.setBottomColor(colorB);
        mAppView.overlaySelf(getWindow());
        if (circleView != null)
            circleView.circle(Color.argb(Values.circleAlpha, Color.red(colorA), Color.green(colorA), Color.blue(colorA)));
        if (optionHolder != null)
            optionHolder.drawCircles(Color.argb(Values.circleAlpha, Color.red(colorA), Color.green(colorA), Color.blue(colorA)));
        //        masterNavigation.setBackgroundColor(secolor);
        taskDesc();
    }

    private void taskDesc() {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        ActivityManager.TaskDescription taskDesc;
        if (mAppView == null) {
            taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, (colorA));
        } else {
            taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, (mAppView.getDragNavigation().calculateOverlayedColor(colorA)));
        }
        setTaskDescription(taskDesc);
    }

    private void splash() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
        loadTheme();
        final Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(colorA);
        window.setNavigationBarColor(colorA);
        taskDesc();
        final LinearLayout ll = new LinearLayout(this);
        ll.setGravity(Gravity.CENTER);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackgroundColor(colorA);
        final ImageView icon = new ImageView(this);
        icon.setScaleType(ImageView.ScaleType.FIT_XY);
        icon.setImageDrawable(getDrawable(R.drawable.ic_icon));
        int is = (int) (Device.screenX(getApplicationContext()) * 0.8);
        icon.setLayoutParams(new LinearLayout.LayoutParams(is, is));
        ll.addView(icon);
        String curved = getString(R.string.app_name);
        ctv = new MyGraphics.CurvedTextView(this, curved, 50, Values.bakedIconColor, Device.screenX(this), (int) (Device.screenY(getApplicationContext()) * 0.3), (int) (Device.screenY(getApplicationContext()) * 0.15) / 2);
        ctv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (Device.screenY(getApplicationContext()) * 0.3)));
        ll.addView(ctv);
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(2500);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        ctv.startAnimation(rotateAnimation);
        ctv.setVisibility(View.VISIBLE);
        ctv.setAlpha(0);
        ObjectAnimator oa = ObjectAnimator.ofFloat(ctv, View.ALPHA, Animations.INVISIBLE_TO_VISIBLE);
        oa.setDuration(300);
        oa.start();
        setContentView(ll);
        ColorFadeAnimation cfa = new ColorFadeAnimation(colorB, colorA, new ColorFadeAnimation.ColorState() {
            @Override
            public void onColor(final int color) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ll.setBackgroundColor(color);
                        getWindow().setNavigationBarColor(color);
                        getWindow().setStatusBarColor(color);
                    }
                });
            }
        });
        cfa.start(2000);
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString("toast") != null) {
                Toast.makeText(this, getIntent().getExtras().getString("toast"), Toast.LENGTH_LONG).show();
            } else if (getIntent().getExtras().getString("popup") != null) {
                AlertDialog.Builder pop = new AlertDialog.Builder(this);
                pop.setCancelable(true);
                pop.setMessage(getIntent().getExtras().getString("popup"));
                pop.setPositiveButton("OK", null);
                pop.show();
            }
        }
    }

    private void newsSplash() {
        getWindow().setStatusBarColor(colorA);
        getWindow().setNavigationBarColor(colorB);
        final SharedPreferences sp = getSharedPreferences(Values.prefName, Context.MODE_PRIVATE);
        if (sp.getBoolean(Values.messageBoardSkipEnabler, false)) {
            view();
        } else {
            final LinearLayout full = new LinearLayout(getApplicationContext());
            full.setGravity(Gravity.CENTER);
            full.setOrientation(LinearLayout.VERTICAL);
            full.setPadding(10, 10, 10, 10);
            LinearLayout newsAll = new LinearLayout(getApplicationContext());
            newsAll.setGravity(Gravity.CENTER);
            final LinearLayout loadingTView = new LinearLayout(getApplicationContext());
            loadingTView.setGravity(Gravity.CENTER);
            loadingTView.setOrientation(LinearLayout.VERTICAL);
            //            loadingTView.setBackground(getDrawable(R.drawable.rounded_rect));
            final TextView loadingText = new TextView(getApplicationContext()), egg = new TextView(getApplicationContext());
            loadingText.setGravity(Gravity.CENTER);
            loadingText.setText(R.string.loading_text);
            loadingText.setTextColor(Color.LTGRAY);
            loadingText.setTypeface(getTypeface());
            loadingText.setTextSize(getFontSize() + 4);
            loadingTView.addView(loadingText);
            loadingTView.setPadding(20, 20, 20, 20);
            egg.setGravity(Gravity.CENTER);
            egg.setText(getEasterEgg());
            egg.setTextColor(Color.LTGRAY);
            egg.setTypeface(getTypeface());
            egg.setTextSize(getFontSize() - 8);
            //            egg.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            loadingTView.addView(egg);
            loadingTView.setGravity(Gravity.CENTER);
            //            loadingTView.setBackgroundColor(Color.BLACK);
            loadingTView.setLayoutParams(new LinearLayout.LayoutParams((int) (Device.screenX(getApplicationContext()) * 0.8), ViewGroup.LayoutParams.MATCH_PARENT));
            newsAll.setLayoutParams(new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            full.addView(loadingTView);
            //            newsAll.setBackgroundColor(Color.GREEN);
            final LinearLayout news = new LinearLayout(getApplicationContext());
            news.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.START);
            newsAll.setOrientation(LinearLayout.VERTICAL);
            news.setOrientation(LinearLayout.VERTICAL);
            //        news.setAlpha(0.5f);
            newsAll.addView(news);
            final ScrollView newsAllSV = new ScrollView(getApplicationContext());
            full.setBackground(gradient);
            newsAllSV.addView(newsAll);
            newsAllSV.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            full.addView(newsAllSV);
            full.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            news.setVisibility(View.GONE);
            setContentView(full);
            new GetMainSite(new GetMainSite.OnGet() {

                @Override
                public void onGet(final MainSite ms) {
                    if (ms != null) {
                        loadingTView.setVisibility(View.GONE);
                        if (ms.readMorePrics == null && ms.princSaying == null) {
                            view();
                        }
                        news.setVisibility(View.VISIBLE);
                        for (int n = 0; n < ms.news.size(); n++) {
                            final LinearLayout nt = new LinearLayout(getApplicationContext());
                            nt.setOrientation(LinearLayout.VERTICAL);
                            nt.setGravity(Gravity.CENTER);
                            Button newtopic = new Button(getApplicationContext());
                            nt.addView(newtopic);
                            nt.setPadding(10, 10, 10, 10);
                            newtopic.setText(ms.news.get(n).name);
                            newtopic.setEllipsize(TextUtils.TruncateAt.END);
                            newtopic.setTextColor(textColor);
                            newtopic.setTextSize(getFontSize() - 10);
                            newtopic.setPadding(20, 10, 20, 10);
                            newtopic.setEllipsize(TextUtils.TruncateAt.END);
                            newtopic.setLines(2);
                            newtopic.setBackground(null);
                            newtopic.setTypeface(getTypeface());
                            if (!ms.news.get(n).imgurl.equals("") || ms.news.get(n).imgurl != null) {
                                final int finalN1 = n;
                                new PictureLoader(ms.news.get(n).imgurl, new PictureLoader.GotImage() {

                                    @Override
                                    public void onGet(Bitmap image) {
                                        ImageView imageView = new ImageView(getApplicationContext());
                                        imageView.setImageBitmap(image);
                                        imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getApplicationContext()) / 3));
                                        imageView.setPadding(20, 20, 20, 40);
                                        imageView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                String url = ms.news.get(finalN1).url;
                                                Intent i = new Intent(Intent.ACTION_VIEW);
                                                i.setData(Uri.parse(url));
                                                startActivity(i);
                                            }
                                        });
                                        if (image != null) nt.addView(imageView);
                                    }
                                }).execute();
                            }
                            newtopic.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getApplicationContext()) / 8));
                            final int finalN = n;
                            newtopic.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String url = ms.news.get(finalN).url;
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(url));
                                    startActivity(i);
                                }
                            });
                            news.addView(nt);
                            newsAllSV.setScrollY(0);
                        }
                    } else {
                        view();
                    }
                }
            }).execute();
            new CountDownTimer((Values.waitTime + 1) * 1000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    if (millisUntilFinished <= 2000) {
                        view();
                    }
                }

                @Override
                public void onFinish() {
                    //                            waiting.setVisibility(View.GONE);
                    //                            nextButton.setVisibility(View.VISIBLE);
                }
            }.start();
        }
    }

    private void welcome() {
        getWindow().setStatusBarColor(colorA);
        getWindow().setNavigationBarColor(colorB);
        LinearLayout part1 = new LinearLayout(this);
        part1.setGravity(Gravity.CENTER);
        part1.setOrientation(LinearLayout.VERTICAL);
        part1.setBackground(gradient);
        //part1
        ImageView icon = new ImageView(this);
        final Button setup = new Button(this);
        final TextView welcome = new TextView(this);
        setup.setTypeface(getTypeface());
        setup.setAllCaps(false);
        welcome.setTypeface(getTypeface());
        setup.setText(R.string.begin_text);
        setup.setAllCaps(false);
        setup.setAlpha(0);
        setup.setBackgroundColor(Color.TRANSPARENT);
        setup.setTextSize((float) 30);
        welcome.setAlpha(0);
        welcome.setGravity(Gravity.CENTER);
        welcome.setTextSize((float) 29);
        welcome.setTextColor(Color.WHITE);
        welcome.setText(R.string.welcome);
        icon.setImageDrawable(getDrawable(R.drawable.ic_icon));
        int is = (int) (Device.screenX(getApplicationContext()) * 0.7);
        icon.setLayoutParams(new LinearLayout.LayoutParams(is, is));
        ObjectAnimator iconSlide = ObjectAnimator.ofFloat(icon, View.TRANSLATION_X, -Device.screenX(getApplicationContext()), 0);
        iconSlide.setDuration(1000);
        iconSlide.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                ObjectAnimator buttonAn = ObjectAnimator.ofFloat(setup, View.ALPHA, Animations.INVISIBLE_TO_VISIBLE);
                buttonAn.setDuration(500);
                buttonAn.start();
                ObjectAnimator welAn = ObjectAnimator.ofFloat(welcome, View.ALPHA, Animations.INVISIBLE_TO_VISIBLE);
                welAn.setDuration(500);
                welAn.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        iconSlide.start();
        part1.addView(welcome);
        part1.addView(icon);
        part1.addView(setup);
        setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeDefaults();
                beginDND(getApplicationContext());
                Starter.scheduleJobs(getApplicationContext());
                view();
            }
        });
        setContentView(part1);
    }

    private void checkInternet() {
        if (Device.isOnline(getApplicationContext())) {
            new Ping(Values.serviceProvider, 5000, new Ping.OnEnd() {
                @Override
                public void onPing(boolean b) {
                    if (b) {
                        openApp();
                    } else {
                        //                        popup("Server Error: No Response From Service Provider.");
                        checkInternet();
                    }
                }
            }).execute();
        } else {
            popup("No Internet Connection.");
        }
    }

    private void startApp() {
        splash();
        checkInternet();
    }

    private void popup(String text) {
        AlertDialog.Builder pop = new AlertDialog.Builder(this);
        pop.setCancelable(true);
        pop.setMessage(text);
        pop.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startApp();
            }
        });
        pop.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                startApp();
            }
        });
        pop.show();
    }

    private void loadKey(int type) {
        final SharedPreferences sp = getSharedPreferences(Values.prefName, Context.MODE_PRIVATE);
        switch (type) {
            case 1:
                sp.edit().putBoolean(Values.messageBoardSkipEnabler, true).commit();
                break;
            case 2:
                Toast.makeText(getApplicationContext(), "Teacher Mode Enabled.", Toast.LENGTH_SHORT).show();
                sp.edit().putBoolean(Values.teacherModeEnabler, true).commit();
                break;
            default:
                break;
        }
        Toast.makeText(getApplicationContext(), "Key loaded successfully.", Toast.LENGTH_SHORT).show();
    }

    private void checkAndLoadKey(final String key) {
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
                                        loadKey(o.getInt("type"));
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Key comparison failed.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Key does not exist, or already used", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Key verification failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    ).execute();
                } else {
                    Toast.makeText(getApplicationContext(), "Key provider unreachable.", Toast.LENGTH_SHORT).show();
                }
            }
        }).execute();
    }

    private void popupKeyEntering() {
        AlertDialog.Builder pop = new AlertDialog.Builder(this);
        pop.setCancelable(true);
        pop.setTitle("Enter Unlock Key");
        final EditText key = new EditText(this);
        key.setFilters(new InputFilter[]{Filters.codeFilter, new InputFilter.AllCaps()});
        pop.setView(key);
        key.setLayoutParams(new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        pop.setPositiveButton("Unlock", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                checkAndLoadKey(key.getText().toString().toUpperCase());
            }
        });
        pop.setNegativeButton("Close", null);
        pop.show();
    }

    private void aboutPopup() {
        AlertDialog.Builder ab = new AlertDialog.Builder(Main.this);
        ab.setTitle(R.string.app_name);
        ab.setMessage("Made By Nadav Tasher.\nVersion: " + Device.getVersionName(getApplicationContext(), getPackageName()) + "\nBuild: " + Device.getVersionCode(getApplicationContext(), getPackageName()));
        ab.setCancelable(true);
        ab.setPositiveButton("Close", null);
        keyentering++;
        if (keyentering == Values.maxKeyEntering) {
            keyentering = 0;
            ab.setNegativeButton("Enter Code", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    popupKeyEntering();
                }
            });
        } else {
            ab.setNegativeButton("Developing For H+", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Values.developingUrl));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
        }
        ab.show();
    }

    private void showEasterEgg() {
        Toast.makeText(getApplicationContext(), getEasterEgg(), Toast.LENGTH_LONG).show();
    }

    private void writeDefaults() {
        final SharedPreferences sp = getSharedPreferences(Values.prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor se = sp.edit();
        se.putInt(Values.fontSizeNumber, Values.fontSizeDefault);
        se.putBoolean(Values.pushService, Values.pushDefault);
        se.putBoolean(Values.breakTime, Values.breakTimeDefault);
        se.putBoolean(Values.autoMute, Values.autoMuteDefault);
        se.putInt(Values.fontColor, Values.fontColorDefault);
        se.putInt(Values.colorA, Values.defaultColorA);
        se.putInt(Values.colorB, Values.defaultColorB);
        se.putBoolean(Values.firstLaunch, false);
        se.apply();
    }

    private void view() {
        final SharedPreferences sp = getSharedPreferences(Values.prefName, Context.MODE_PRIVATE);
        final int x = Device.screenX(getApplicationContext());
        final int circlePadding = x / 30;
        final int circleSize = x / 4;
        final ScrollView contentScroll = new ScrollView(this);
        breakTime = sp.getBoolean(Values.breakTime, Values.breakTimeDefault);
        contentScroll.setVerticalScrollBarEnabled(false);
        contentScroll.setOverScrollMode(View.OVER_SCROLL_NEVER);
        LinearLayout circleHolder = new LinearLayout(this), optionAndCircleHolder = new LinearLayout(this);
        circleHolder.setOrientation(LinearLayout.HORIZONTAL);
        circleHolder.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        circleHolder.setGravity(Gravity.END | Gravity.BOTTOM);
        //        circleHolder.setPadding(circlePadding,circlePadding,circlePadding,circlePadding);
        optionAndCircleHolder.setOrientation(LinearLayout.VERTICAL);
        optionAndCircleHolder.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        optionAndCircleHolder.setGravity(Gravity.END | Gravity.BOTTOM);
        optionAndCircleHolder.setPadding(circlePadding, circlePadding, circlePadding, circlePadding);
        circleView = new MyGraphics.CircleView(this, circleSize);
        mAppView = new AppView(getApplicationContext(), getDrawable(R.drawable.ic_icon), Values.navColor);
        mAppView.getDragNavigation().setOnIconClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aboutPopup();
            }
        });
        MyGraphics.CircleView.CircleOption[] options = getCircleOptions(circleSize, circlePadding);
        optionHolder = new MyGraphics.OptionHolder(getApplicationContext(), classCoaster, options, circlePadding);
        //        optionHolder.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, options.length * circleSize + 2 * circlePadding));
        optionHolder.setVisibility(View.GONE);
        circleView.setOnStateChangedListener(new MyGraphics.CircleView.OnStateChangedListener() {
            @Override
            public void onOpen() {
                mAppView.getDragNavigation().setEnabled(false);
                optionHolder.emptyContent();
                optionHolder.fadeIn();
            }

            @Override
            public void onClose() {
                mAppView.getDragNavigation().setEnabled(true);
                //                optionHolder.emptyContent();
                optionHolder.fadeOutContent();
                optionHolder.fadeOut();
            }
        });
        mAppView.setBackground(gradient);
        mAppView.setTopColor(colorA);
        mAppView.setBottomColor(colorB);
        circleHolder.addView(circleView);
        optionAndCircleHolder.addView(optionHolder);
        optionAndCircleHolder.addView(circleHolder);
        mAppView.addView(optionAndCircleHolder);
        //        masterLayout.addView(optionHolder);
        //        circleView.setX(x - circleView.xy - circlePadding);
        //        circleView.setY(y - circleView.xy - getNavSize() / 2 - circlePadding);
        //        optionHolder.setY(y - (y - circleView.getY()) - (((options.length + 1) * circlePadding) / 2) - (options.length * circleSize + circlePadding));
        mAppView.getDragNavigation().setOnStateChangedListener(new DragNavigation.OnStateChangedListener() {
            @Override
            public void onOpen() {
                circleView.setEnabled(false);
                TextView load = new TextView(getApplicationContext());
                load.setTextSize(getFontSize());
                load.setTypeface(getTypeface());
                load.setTextColor(textColor);
                load.setText(R.string.loading_text);
                load.setGravity(Gravity.CENTER);
                mAppView.getDragNavigation().setContent(load);
                final int fontSize = getFontSize();
                final LinearLayout fullPage = new LinearLayout(getApplicationContext());
                final LinearLayout news = new LinearLayout(getApplicationContext());
                ScrollView npscroll = new ScrollView(getApplicationContext());
                npscroll.setOverScrollMode(View.OVER_SCROLL_NEVER);
                LinearLayout newsAndPush = new LinearLayout(getApplicationContext());
                fullPage.setOrientation(LinearLayout.VERTICAL);
                news.setOrientation(LinearLayout.VERTICAL);
                newsAndPush.setOrientation(LinearLayout.VERTICAL);
                fullPage.setGravity(Gravity.CENTER);
                news.setGravity(Gravity.CENTER);
                newsAndPush.setGravity(Gravity.CENTER);
                npscroll.addView(newsAndPush);
                newsAndPush.addView(news);
                TextView newsTitle;
                newsTitle = new TextView(getApplicationContext());
                newsTitle.setText(R.string.news);
                news.addView(newsTitle);
                newsTitle.setTextColor(textColor);
                newsTitle.setTextSize(fontSize);
                newsTitle.setGravity(Gravity.CENTER);
                newsTitle.setTypeface(getTypeface());
                fullPage.addView(npscroll);
                //                fullPage.setPadding(5, 5, 5, 5);
                fullPage.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                npscroll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                if (Device.isOnline(getApplicationContext())) {
                    new GetNews(Values.serviceProvider, new GetNews.GotNews() {
                        @Override
                        public void onNewsGet(final ArrayList<GetNews.Link> link) {
                            for (int i = 0; i < link.size(); i++) {
                                Button cls = new Button(getApplicationContext());
                                cls.setPadding(10, 10, 10, 10);
                                cls.setTextSize((float) fontSize - 10);
                                cls.setGravity(Gravity.CENTER);
                                cls.setText(link.get(i).name);
                                cls.setTextColor(textColor);
                                cls.setEllipsize(TextUtils.TruncateAt.END);
                                cls.setLines(2);
                                //                            cls.setBackgroundColor(Color.TRANSPARENT);
                                cls.setBackground(coaster);
                                cls.setTypeface(getTypeface());
                                cls.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (Device.screenY(getApplicationContext()) / 8)));
                                news.addView(cls);
                                if (!link.get(i).url.equals("")) {
                                    final int finalI = i;
                                    cls.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            String url = link.get(finalI).url;
                                            Intent i = new Intent(Intent.ACTION_VIEW);
                                            i.setData(Uri.parse(url));
                                            startActivity(i);
                                        }
                                    });
                                }
                            }
                            mAppView.getDragNavigation().setContent(fullPage);
                        }

                        @Override
                        public void onFail(ArrayList<GetNews.Link> e) {
                            TextView fail = new TextView(getApplicationContext());
                            fail.setTextSize(getFontSize());
                            fail.setTypeface(getTypeface());
                            fail.setTextColor(textColor);
                            fail.setText(R.string.news_load_failed);
                            fail.setGravity(Gravity.CENTER);
                            mAppView.getDragNavigation().setContent(fail);
                        }
                    }).execute("");
                }
            }

            @Override
            public void onClose() {
                mAppView.getDragNavigation().emptyContent();
                circleView.setEnabled(true);
            }
        });
        refreshTheme();
        StudentClass c = getFavoriteClass();
        if (classes != null) if (c != null) setStudentMode(c);
        setContentView(mAppView);
    }

    private StudentClass getFavoriteClass() {
        final SharedPreferences sp = getSharedPreferences(Values.prefName, Context.MODE_PRIVATE);
        int selectedClass = 0;
        if (sp.getString(Values.favoriteClass, null) != null) {
            if (classes != null) {
                for (int fc = 0; fc < classes.size(); fc++) {
                    if (sp.getString(Values.favoriteClass, "").equals(classes.get(fc).name)) {
                        return classes.get(fc);
                    }
                }
                return classes.get(selectedClass);
            } else {
                return null;
            }
        } else {
            return classes.get(selectedClass);
        }
    }

    private Teacher getFavoriteTeacher() {
        final SharedPreferences sp = getSharedPreferences(Values.prefName, Context.MODE_PRIVATE);
        int selectedTeacher = 0;
        if (sp.getString(Values.favoriteTeacher, null) != null) {
            if (teachers != null) {
                for (int fc = 0; fc < teachers.size(); fc++) {
                    if (sp.getString(Values.favoriteTeacher, "").equals(teachers.get(fc).mainName)) {
                        return teachers.get(fc);
                    }
                }
                return teachers.get(selectedTeacher);
            } else {
                return null;
            }
        } else {
            return teachers.get(selectedTeacher);
        }
    }

    private MyGraphics.CircleView.CircleOption[] getCircleOptions(int circleSize, int circlePadding) {
        MyGraphics.CircleView.CircleOption share = new MyGraphics.CircleView.CircleOption(getApplicationContext(), circleSize, circlePadding);
        share.setIcon(getDrawable(R.drawable.ic_share));
        share.setDesiredViewOnDemand(new MyGraphics.CircleView.CircleOption.OnDemand() {
            @Override
            public View demandView() {
                return generateShareView();
            }
        });
        MyGraphics.CircleView.CircleOption changeClass = new MyGraphics.CircleView.CircleOption(getApplicationContext(), circleSize, circlePadding);
        changeClass.setIcon(getDrawable(R.drawable.ic_class));
        changeClass.setDesiredViewOnDemand(new MyGraphics.CircleView.CircleOption.OnDemand() {
            @Override
            public View demandView() {
                return generateClassSwitchView();
            }
        });
        MyGraphics.CircleView.CircleOption settings = new MyGraphics.CircleView.CircleOption(getApplicationContext(), circleSize, circlePadding);
        settings.setIcon(getDrawable(R.drawable.ic_gear));
        settings.setDesiredView(getSettingsView());
        MyGraphics.CircleView.CircleOption[] options = new MyGraphics.CircleView.CircleOption[]{
                share, changeClass, settings
        };
        return options;
    }

    private LinearLayout generateShareView() {
        LinearLayout shareView = new LinearLayout(this);
        shareView.setGravity(Gravity.CENTER);
        shareView.setOrientation(LinearLayout.VERTICAL);
        TextView shareTitle = new TextView(this);
        shareTitle.setTextSize(getFontSize());
        shareTitle.setTypeface(getTypeface());
        shareTitle.setTextColor(textColor);
        shareTitle.setText(R.string.share_menu);
        shareTitle.setGravity(Gravity.CENTER);
        final Switch shareTimeSwitch = new Switch(this);
        shareTimeSwitch.setPadding(10, 0, 10, 0);
        shareTimeSwitch.setText(R.string.lesson_time);
        shareTimeSwitch.setTypeface(getTypeface());
        shareTimeSwitch.setTextSize(getFontSize() - 4);
        shareTimeSwitch.setTextColor(textColor);
        shareTimeSwitch.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        Button shareB = new Button(this);
        String shareText = getString(R.string.share) + " " + currentClass.name;
        shareB.setText(shareText);
        shareB.setBackground(coaster);
        shareB.setTextColor(textColor);
        shareB.setTextSize(getFontSize() - 7);
        shareB.setTypeface(getTypeface());
        shareB.setAllCaps(false);
        shareB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(currentClass.name + "\n" + scheduleForClassString(currentClass, shareTimeSwitch.isChecked()));
            }
        });
        shareView.addView(shareTitle);
        shareView.addView(shareTimeSwitch);
        shareView.addView(shareB);
        return shareView;
    }

    private ScrollView getSettingsView() {
        final SharedPreferences sp = getSharedPreferences(Values.prefName, Context.MODE_PRIVATE);
        ScrollView sv = new ScrollView(getApplicationContext());
        sv.setPadding(10, 10, 10, 10);
        LinearLayout settings = new LinearLayout(this);
        settings.setOrientation(LinearLayout.VERTICAL);
        settings.setGravity(Gravity.START);
        settings.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        final Switch autoMuteSwitch = new Switch(this), newScheduleSwitch = new Switch(this), breakTimeSwitch = new Switch(this), pushSwitch = new Switch(this);
        pushSwitch.setText(R.string.live_messages);
        newScheduleSwitch.setText(R.string.schedule_notification);
        breakTimeSwitch.setText(R.string.show_breaks);
        autoMuteSwitch.setText(R.string.auto_mute);
        pushSwitch.setChecked(sp.getBoolean(Values.pushService, Values.pushDefault));
        newScheduleSwitch.setChecked(sp.getBoolean(Values.scheduleService, Values.scheduleDefault));
        breakTimeSwitch.setChecked(sp.getBoolean(Values.breakTime, Values.breakTimeDefault));
        autoMuteSwitch.setChecked(sp.getBoolean(Values.autoMute, Values.autoMuteDefault));
        pushSwitch.setTextSize((float) (getFontSize() / 1.5));
        breakTimeSwitch.setTextSize((float) (getFontSize() / 1.5));
        autoMuteSwitch.setTextSize((float) (getFontSize() / 1.5));
        newScheduleSwitch.setTextSize((float) (getFontSize() / 1.5));
        pushSwitch.setTypeface(getTypeface());
        newScheduleSwitch.setTypeface(getTypeface());
        breakTimeSwitch.setTypeface(getTypeface());
        autoMuteSwitch.setTypeface(getTypeface());
        breakTimeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sp.edit().putBoolean(Values.breakTime, isChecked).apply();
                //                breakTime=sp.getBoolean(Values.breakTime,Values.breakTimeDefault);
                breakTime = isChecked;
                breakTimeTunnle.send(breakTime);
            }
        });
        autoMuteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    boolean granted = false;
                    if (nm != null) {
                        granted = nm.isNotificationPolicyAccessGranted();
                    }
                    if (!granted) {
                        AlertDialog.Builder pop = new AlertDialog.Builder(Main.this);
                        pop.setCancelable(true);
                        pop.setMessage("You need to enable 'Do Not Disturb' permissions for the app.");
                        pop.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (Build.VERSION.SDK_INT >= 23) {
                                    sp.edit().putBoolean(Values.autoMute, true).apply();
                                    startActivityForResult(new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS), 0);
                                }
                            }
                        });
                        pop.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                autoMuteSwitch.setChecked(false);
                                sp.edit().putBoolean(Values.autoMute, false).apply();
                            }
                        });
                        pop.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                autoMuteSwitch.setChecked(false);
                                sp.edit().putBoolean(Values.autoMute, false).apply();
                            }
                        });
                        pop.show();
                    } else {
                        sp.edit().putBoolean(Values.autoMute, true).apply();
                    }
                } else {
                    sp.edit().putBoolean(Values.autoMute, false).apply();
                }
            }
        });
        pushSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sp.edit().putBoolean(Values.pushService, isChecked).apply();
            }
        });
        newScheduleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sp.edit().putBoolean(Values.scheduleService, isChecked).apply();
            }
        });
        final TextView explainTextSize = new TextView(getApplicationContext());
        explainTextSize.setText(R.string.choose_text_size);
        explainTextSize.setTypeface(getTypeface());
        explainTextSize.setTextSize((float) (getFontSize() / 1.5));
        explainTextSize.setTextColor(textColor);
        explainTextSize.setGravity(Gravity.CENTER);
        final TextView explainTextColor = new TextView(getApplicationContext());
        explainTextColor.setText(R.string.choose_text_color);
        explainTextColor.setTypeface(getTypeface());
        explainTextColor.setTextSize((float) (getFontSize() / 1.5));
        explainTextColor.setTextColor(textColor);
        explainTextColor.setGravity(Gravity.CENTER);
        final TextView explainColorA = new TextView(getApplicationContext());
        explainColorA.setText(R.string.choose_first_color);
        explainColorA.setTypeface(getTypeface());
        explainColorA.setTextSize((float) (getFontSize() / 1.5));
        explainColorA.setTextColor(textColor);
        explainColorA.setGravity(Gravity.CENTER);
        final TextView explainColorB = new TextView(getApplicationContext());
        explainColorB.setText(R.string.choose_second_color);
        explainColorB.setTypeface(getTypeface());
        explainColorB.setTextSize((float) (getFontSize() / 1.5));
        explainColorB.setTextColor(textColor);
        explainColorB.setGravity(Gravity.CENTER);
        SeekBar fontSizeSeekBar = new SeekBar(this);
        fontSizeSeekBar.setMax(70);
        fontSizeSeekBar.setProgress(sp.getInt(Values.fontSizeNumber, Values.fontSizeDefault));
        fontSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    sp.edit().putInt(Values.fontSizeNumber, progress).apply();
                    refreshTheme();
                    fontSizeChangeTunnle.send(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        ColorPicker textColorPicker = new ColorPicker(this, textColor);
        textColorPicker.setOnColorChanged(new ColorPicker.OnColorChanged() {
            @Override
            public void onColorChange(int color) {
                sp.edit().putInt(Values.fontColor, color).apply();
                refreshTheme();
                colorChangeTunnle.send(textColor);
            }
        });
        ColorPicker colorApicker = new ColorPicker(this, colorA);
        colorApicker.setOnColorChanged(new ColorPicker.OnColorChanged() {
            @Override
            public void onColorChange(int color) {
                sp.edit().putInt(Values.colorA, color).apply();
                refreshTheme();
            }
        });
        ColorPicker colorBpicker = new ColorPicker(this, colorB);
        colorBpicker.setOnColorChanged(new ColorPicker.OnColorChanged() {
            @Override
            public void onColorChange(int color) {
                sp.edit().putInt(Values.colorB, color).apply();
                refreshTheme();
            }
        });
        textColorPicker.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getApplicationContext()) / 5));
        colorApicker.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getApplicationContext()) / 5));
        colorBpicker.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getApplicationContext()) / 5));
        fontSizeChangeTunnle.addReceiver(new Tunnel.OnTunnel<Integer>() {
            @Override
            public void onReceive(Integer response) {
                pushSwitch.setTextSize((float) (response / 1.5));
                breakTimeSwitch.setTextSize((float) (response / 1.5));
                autoMuteSwitch.setTextSize((float) (response / 1.5));
                newScheduleSwitch.setTextSize((float) (response / 1.5));
                explainColorA.setTextSize((float) (response / 1.5));
                explainColorB.setTextSize((float) (response / 1.5));
                explainTextColor.setTextSize((float) (response / 1.5));
                explainTextSize.setTextSize((float) (response / 1.5));
            }
        });
        colorChangeTunnle.addReceiver(new Tunnel.OnTunnel<Integer>() {
            @Override
            public void onReceive(Integer response) {
                pushSwitch.setTextColor(response);
                breakTimeSwitch.setTextColor(response);
                autoMuteSwitch.setTextColor(response);
                newScheduleSwitch.setTextColor(response);
                explainColorA.setTextColor(response);
                explainColorB.setTextColor(response);
                explainTextColor.setTextColor(response);
                explainTextSize.setTextColor(response);
            }
        });
        settings.addView(autoMuteSwitch);
        settings.addView(newScheduleSwitch);
        settings.addView(pushSwitch);
        settings.addView(breakTimeSwitch);
        settings.addView(explainTextSize);
        settings.addView(fontSizeSeekBar);
        settings.addView(explainTextColor);
        settings.addView(textColorPicker);
        settings.addView(explainColorA);
        settings.addView(colorApicker);
        settings.addView(explainColorB);
        settings.addView(colorBpicker);
        sv.addView(settings);
        sv.setVerticalScrollBarEnabled(false);
        return sv;
    }

    private ScrollView generateClassSwitchView() {
        final SharedPreferences sp = getSharedPreferences(Values.prefName, Context.MODE_PRIVATE);
        LinearLayout all = new LinearLayout(this);
        all.setOrientation(LinearLayout.VERTICAL);
        all.setGravity(Gravity.CENTER);
        LinearLayout students = new LinearLayout(this);
        students.setPadding(10, 10, 10, 10);
        students.setOrientation(LinearLayout.VERTICAL);
        students.setGravity(Gravity.CENTER);
        LinearLayout teachersv = new LinearLayout(this);
        teachersv.setPadding(10, 10, 10, 10);
        teachersv.setOrientation(LinearLayout.VERTICAL);
        teachersv.setGravity(Gravity.CENTER);
        TextView studentsTitle = new TextView(this);
        studentsTitle.setText(R.string.students_text);
        studentsTitle.setTypeface(getTypeface());
        studentsTitle.setTextSize(getFontSize() - 5);
        studentsTitle.setTextColor(textColor);
        studentsTitle.setGravity(Gravity.CENTER);
        TextView teachersTitle = new TextView(this);
        teachersTitle.setText(R.string.teachers_text);
        teachersTitle.setTypeface(getTypeface());
        teachersTitle.setTextSize(getFontSize() - 5);
        teachersTitle.setTextColor(textColor);
        teachersTitle.setGravity(Gravity.CENTER);
        students.addView(studentsTitle);
        for (int cs = 0; cs < classes.size(); cs++) {
            Button cls = new Button(getApplicationContext());
            cls.setTextSize((float) getFontSize());
            cls.setGravity(Gravity.CENTER);
            cls.setText(classes.get(cs).name);
            cls.setTextColor(textColor);
            cls.setBackground(coaster);
            cls.setPadding(10, 0, 10, 0);
            cls.setTypeface(getTypeface());
            cls.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (Device.screenY(getApplicationContext()) / 12)));
            students.addView(cls);
            final int finalCs = cs;
            cls.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sp.edit().putString(Values.favoriteClass, classes.get(finalCs).name).commit();
                    setStudentMode(classes.get(finalCs));
                }
            });
        }
        all.addView(students);
        if (sp.getBoolean(Values.teacherModeEnabler, false)) {
            teachersv.addView(teachersTitle);
            for (int cs = 0; cs < teachers.size(); cs++) {
                Button cls = new Button(getApplicationContext());
                cls.setTextSize((float) getFontSize());
                cls.setGravity(Gravity.CENTER);
                cls.setText(teachers.get(cs).mainName);
                cls.setTextColor(textColor);
                cls.setBackground(coaster);
                cls.setPadding(10, 0, 10, 0);
                cls.setTypeface(getTypeface());
                cls.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (Device.screenY(getApplicationContext()) / 12)));
                teachersv.addView(cls);
                final int finalCs = cs;
                cls.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sp.edit().putString(Values.favoriteTeacher, teachers.get(finalCs).mainName).commit();
                        setTeacherMode(teachers.get(finalCs));
                    }
                });
            }
            all.addView(teachersv);
        }
        ScrollView sv = new ScrollView(this);
        sv.addView(all);
        return sv;
    }

    private Drawable generateCoaster(int color) {
        GradientDrawable gd = (GradientDrawable) getDrawable(R.drawable.rounded_rect);
        if (gd != null) {
            gd.setColor(color);
        }
        return gd;
    }

    private void openApp() {
        final SharedPreferences sp = getSharedPreferences(Values.prefName, Context.MODE_PRIVATE);
        Starter.scheduleJobs(getApplicationContext());
        new GetLink(Values.scheduleProvider, new GetLink.GotLink() {

            @Override
            public void onLinkGet(String link) {
                if (link != null) {
                    String fileName = "hs.xls";
                    if (link.endsWith(".xlsx")) {
                        fileName = "hs.xlsx";
                    }
                    final String finalFileName = fileName;
                    String date = link.split("/")[link.split("/").length - 1].split("\\.")[0];
                    if (!sp.getString(Values.latestFileDate, "").equals(date)) {
                        sp.edit().putString(Values.latestFileName, fileName).commit();
                        sp.edit().putString(Values.latestFileDate, date).commit();
                        sp.edit().putString(Values.latestFileDateRefresher, date).commit();
                        new FileDownloader(link, new File(getApplicationContext().getFilesDir(), fileName), new FileDownloader.OnDownload() {
                            @Override
                            public void onFinish(final File file, final boolean be) {
                                parseAndLoad(file, true, finalFileName);
                            }

                            @Override
                            public void onProgressChanged(File file, int i) {
                            }
                        }).execute();
                    } else {
                        parseAndLoad(new File(getApplicationContext().getFilesDir(), fileName), true, finalFileName);
                    }
                } else {
                    //                    popup("Could Not Fetch Link, Please Try Disconnecting From Wi-Fi");
                    openApp();
                }
            }

            @Override
            public void onFail(String e) {
                //                popup("Failed");
                openApp();
            }
        }).execute();
    }

    private void parseAndLoad(File f, boolean b, String filename) {
        final SharedPreferences sp = getSharedPreferences(Values.prefName, Context.MODE_PRIVATE);
        if (b) {
            if (filename.endsWith(".xlsx")) {
                classes = readExcelFileXLSX(f);
                day = readExcelDayXLSX(f);
            } else {
                classes = readExcelFile(f);
                day = readExcelDay(f);
            }
            /////
            //                                file = new File(getApplicationContext().getFilesDir(), "hs.xlsx");
            //                                classes = readExcelFileXLSX(file);
            //                                day = readExcelDayXLSX(file);
            /////
            if (classes != null) {
                teachers = getTeacherSchudleForClasses(classes);
                if (!sp.getBoolean(Values.firstLaunch, true)) {
                    newsSplash();
                    //                                            welcome(classes, true);
                    beginDND(getApplicationContext());
                } else {
                    welcome();
                }
            }
        } else {
            openApp();
        }
    }
    //    private void showSchedule(final StudentClass c) {
    //        currentClass = c;
    //        circleView.text(c.name, day);
    //        LinearLayout hsplace = new LinearLayout(this);
    //        hsplace.setGravity(Gravity.START | Gravity.CENTER_HORIZONTAL);
    //        hsplace.setOrientation(LinearLayout.VERTICAL);
    //        content.removeAllViews();
    //        content.addView(hsplace);
    //        View ph = new View(this);
    //        ph.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, placeHold));
    //        hsplace.addView(ph);
    //        hsplace.addView(scheduleForClass(c));
    //    }

    private void share(String st) {
        Intent s = new Intent(Intent.ACTION_SEND);
        s.putExtra(Intent.EXTRA_TEXT, st);
        s.setType("text/plain");
        s.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(s, "Share With"));
    }

    private void setStudentMode(StudentClass c) {
        currentClass = c;
        circleView.text(c.name, day);
        displayLessonViews(scheduleForClass(c));
    }

    private void setTeacherMode(Teacher t) {
        currentTeacher = t;
        circleView.text(t.mainName.split(" ")[0], day);
        displayLessonViews(scheduleForTeacher(t));
    }

    private void displayLessonViews(ArrayList<MyGraphics.LessonView> lessonViews) {
        LinearLayout hsplace = new LinearLayout(this);
        hsplace.setGravity(Gravity.START | Gravity.CENTER_HORIZONTAL);
        hsplace.setOrientation(LinearLayout.VERTICAL);
        mAppView.setContent(hsplace);
        LinearLayout lessonViewHolder = new LinearLayout(this);
        lessonViewHolder.setGravity(Gravity.START | Gravity.CENTER_HORIZONTAL);
        lessonViewHolder.setOrientation(LinearLayout.VERTICAL);
        lessonViewHolder.setPadding(10, 10, 10, 10);
        for (int l = 0; l < lessonViews.size(); l++) {
            lessonViewHolder.addView(lessonViews.get(l));
        }
        hsplace.addView(lessonViewHolder);
    }

    private int getFontSize() {
        final SharedPreferences sp = getSharedPreferences(Values.prefName, Context.MODE_PRIVATE);
        return sp.getInt(Values.fontSizeNumber, Values.fontSizeDefault);
    }

    private String scheduleForClassString(StudentClass fclass, boolean showTime) {
        String allsubj = "";
        for (int s = 0; s < fclass.subjects.size(); s++) {
            String before;
            if (showTime) {
                before = "(" + getRealTimeForHourNumber(fclass.subjects.get(s).hour) + ") " + fclass.subjects.get(s).hour + ". ";
            } else {
                before = fclass.subjects.get(s).hour + ". ";
            }
            String total = before + fclass.subjects.get(s).name;
            if (fclass.subjects.get(s).name != null && !fclass.subjects.get(s).name.equals("")) {
                allsubj += total + "\n";
            }
        }
        return allsubj;
    }

    private String readExcelDay(File f) {
        try {
            POIFSFileSystem myFileSystem = new POIFSFileSystem(new FileInputStream(f));
            Workbook myWorkBook = new HSSFWorkbook(myFileSystem);
            Sheet mySheet = myWorkBook.getSheetAt(0);
            return mySheet.getRow(0).getCell(0).getStringCellValue();
        } catch (Exception e) {
            return null;
        }
    }

    private String readExcelDayXLSX(File f) {
        try {
            XSSFWorkbook myWorkBook = new XSSFWorkbook(new FileInputStream(f));
            Sheet mySheet = myWorkBook.getSheetAt(0);
            return mySheet.getRow(0).getCell(0).getStringCellValue();
        } catch (Exception e) {
            return null;
        }
    }

    private String getEasterEgg() {
        int which = new Random().nextInt(2);
        if (which == 0) {
            int newrand = new Random().nextInt(infact.length);
            return "Did You Know: " + infact[newrand];
        } else {
            int newrand = new Random().nextInt(ees.length);
            return "\"" + ees[newrand] + "\"";
        }
    }

    private Typeface getTypeface() {
        return Typeface.createFromAsset(getAssets(), Values.fontName);
    }

    private ArrayList<MyGraphics.LessonView> scheduleForClass(final StudentClass fclass) {
        ArrayList<MyGraphics.LessonView> lessons = new ArrayList<>();
        for (int s = 0; s < fclass.subjects.size(); s++) {
            if (getBreak(fclass.subjects.get(s).hour - 1) != -1) {
                final MyGraphics.LessonView breakt = new MyGraphics.LessonView(getApplicationContext(), classCoaster, classCoasterMarked, -1, "הפסקה", getBreak(fclass.subjects.get(s).hour - 1) + " דקות", "");
                if (fclass.subjects.get(s).name != null && !fclass.subjects.get(s).name.equals("")) {
                    lessons.add(breakt);
                }
                if (!breakTime) {
                    breakt.setVisibility(View.GONE);
                } else {
                    breakt.setVisibility(View.VISIBLE);
                }
                breakTimeTunnle.addReceiver(new Tunnel.OnTunnel<Boolean>() {
                    @Override
                    public void onReceive(Boolean response) {
                        if (!response) {
                            breakt.setVisibility(View.GONE);
                        } else {
                            breakt.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
            StudentClass.Subject.Time classTime = getTimeForLesson(fclass.subjects.get(s).hour);
            boolean isCurrent = false;
            Calendar c = Calendar.getInstance();
            int minute = c.get(Calendar.MINUTE);
            int hour = c.get(Calendar.HOUR_OF_DAY);
            if (minuteOfDay(hour, minute) > minuteOfDay(classTime.startH, classTime.startM) && minuteOfDay(hour, minute) <= minuteOfDay(classTime.finishH, classTime.finishM)) {
                isCurrent = true;
            }
            String txt = getRealTimeForHourNumber(fclass.subjects.get(s).hour) + "-" + getRealEndTimeForHourNumber(fclass.subjects.get(s).hour);
            String tcnm = (fclass.subjects.get(s).fullName.substring(fclass.subjects.get(s).fullName.indexOf("\n") + 1).trim().split("\\r?\\n")[0]).split(",")[0];
            MyGraphics.LessonView subject = new MyGraphics.LessonView(getApplicationContext(), classCoaster, classCoasterMarked, fclass.subjects.get(s).hour, fclass.subjects.get(s).name.replaceAll(",", "/"), txt, tcnm);
            if (isCurrent) subject.mark();
            if (fclass.subjects.get(s).name != null && !fclass.subjects.get(s).name.equals("")) {
                lessons.add(subject);
            }
        }
        return lessons;
    }

    private ArrayList<MyGraphics.LessonView> scheduleForTeacher(final Teacher fclass) {
        ArrayList<MyGraphics.LessonView> lessons = new ArrayList<>();
        for (int h = 0; h <= 12; h++) {
            ArrayList<Teacher.Lesson> currentLesson = new ArrayList<>();
            String currentText = "";
            boolean isCurrent = false;
            String lessonName = "";
            for (int s = 0; s < fclass.teaching.size(); s++) {
                if (fclass.teaching.get(s).hour == h) {
                    StudentClass.Subject.Time classTime = getTimeForLesson(h);
                    Calendar ca = Calendar.getInstance();
                    int minute = ca.get(Calendar.MINUTE);
                    int hour = ca.get(Calendar.HOUR_OF_DAY);
                    if (minuteOfDay(hour, minute) >= minuteOfDay(classTime.startH, classTime.startM) && minuteOfDay(hour, minute) <= minuteOfDay(classTime.finishH, classTime.finishM)) {
                        isCurrent = true;
                    }
                    if (currentText.equals("")) {
                        currentText += fclass.teaching.get(s).className;
                    } else {
                        currentText += ", " + fclass.teaching.get(s).className;
                    }
                    lessonName = fclass.teaching.get(s).lessonName;
                    currentLesson.add(fclass.teaching.get(s));
                }
            }
            if (currentLesson.size() > 1) {
                if (getGrade(currentLesson) != -1) {
                    currentText = getGrade(getGrade(currentLesson));
                }
            }
            if (currentLesson.size() >= 1) {
                if (getBreak(h - 1) != -1) {
                    final MyGraphics.LessonView breakt = new MyGraphics.LessonView(getApplicationContext(), classCoaster, classCoasterMarked, -1, "הפסקה", getBreak(h - 1) + " דקות", "");
                    lessons.add(breakt);
                    if (!breakTime) {
                        breakt.setVisibility(View.GONE);
                    } else {
                        breakt.setVisibility(View.VISIBLE);
                    }
                    breakTimeTunnle.addReceiver(new Tunnel.OnTunnel<Boolean>() {
                        @Override
                        public void onReceive(Boolean response) {
                            if (!response) {
                                breakt.setVisibility(View.GONE);
                            } else {
                                breakt.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
                String txt = getRealTimeForHourNumber(h) + "-" + getRealEndTimeForHourNumber(h);
                MyGraphics.LessonView lesson = new MyGraphics.LessonView(getApplicationContext(), classCoaster, classCoasterMarked, h, currentText, txt, lessonName);
                if (isCurrent) lesson.mark();
                lessons.add(lesson);
            }
        }
        return lessons;
    }

    static class MyGraphics {
        static class LessonView extends LinearLayout {
            static final String rtlMark = "\u200F";
            private String ln, tm, tc;
            private int num;
            private TextView lessonTv, timeTv, teacherTv;
            private LinearLayout top, bottom;
            private Drawable back, pressed;

            public LessonView(Context c) {
                super(c);
            }

            public LessonView(Context context, Drawable d, Drawable p, int number, String lessonName, String times, String teacher) {
                super(context);
                ln = rtlMark + lessonName;
                tm = rtlMark + times;
                tc = rtlMark + teacher;
                num = number;
                back = d;
                pressed = p;
                init();
            }

            public void mark() {
                setBackground(pressed);
            }

            private void init() {
                setOrientation(VERTICAL);
                setLayoutDirection(LAYOUT_DIRECTION_RTL);
                setGravity(Gravity.CENTER);
                lessonTv = new TextView(getContext());
                timeTv = new TextView(getContext());
                teacherTv = new TextView(getContext());
                top = new LinearLayout(getContext());
                bottom = new LinearLayout(getContext());
                top.setOrientation(LinearLayout.HORIZONTAL);
                bottom.setOrientation(LinearLayout.HORIZONTAL);
                top.setLayoutDirection(LAYOUT_DIRECTION_RTL);
                bottom.setLayoutDirection(LAYOUT_DIRECTION_RTL);
                lessonTv.setTextSize(Main.getFontSize(getContext()));
                teacherTv.setTextSize((float) (Main.getFontSize(getContext()) * 0.8));
                timeTv.setTextSize((float) (Main.getFontSize(getContext()) * 0.8));
                top.addView(lessonTv);
                bottom.addView(teacherTv);
                bottom.addView(timeTv);
                if (num != -1) {
                    String tx = num + ". " + ln;
                    lessonTv.setText(tx);
                } else {
                    lessonTv.setText(ln);
                }
                timeTv.setText(tm);
                teacherTv.setText(tc);
                lessonTv.setTextColor(Main.textColor);
                teacherTv.setTextColor(Main.textColor);
                timeTv.setTextColor(Main.textColor);
                timeTv.setTypeface(Main.getTypeface(getContext()));
                lessonTv.setTypeface(Main.getTypeface(getContext()));
                teacherTv.setTypeface(Main.getTypeface(getContext()));
                teacherTv.setSingleLine(true);
                timeTv.setSingleLine(true);
                lessonTv.setSingleLine(true);
                teacherTv.setEllipsize(TextUtils.TruncateAt.END);
                teacherTv.setSingleLine();
//                lessonTv.setEllipsize(TextUtils.TruncateAt.END);
                lessonTv.setSingleLine();
                timeTv.setGravity(Gravity.CENTER);
                teacherTv.setGravity(Gravity.CENTER);
                addView(top);
                addView(bottom);
                setBackground(back);
                setPadding(20, 10, 20, 10);
                teacherTv.setLayoutParams(new LayoutParams(Device.screenX(getContext()) / 2 - getPaddingRight(), ViewGroup.LayoutParams.WRAP_CONTENT));
                timeTv.setLayoutParams(new LayoutParams(Device.screenX(getContext()) / 2 - getPaddingLeft(), ViewGroup.LayoutParams.WRAP_CONTENT));
                setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getContext()) / 7));
                Main.colorChangeTunnle.addReceiver(new Tunnel.OnTunnel<Integer>() {
                    @Override
                    public void onReceive(Integer response) {
                        lessonTv.setTextColor(response);
                        teacherTv.setTextColor(response);
                        timeTv.setTextColor(response);
                    }
                });
                Main.fontSizeChangeTunnle.addReceiver(new Tunnel.OnTunnel<Integer>() {
                    @Override
                    public void onReceive(Integer response) {
                        lessonTv.setTextSize(response);
                        teacherTv.setTextSize((float) (response * 0.8));
                        timeTv.setTextSize((float) (response * 0.8));
                    }
                });
            }
        }

        static class CircleView extends FrameLayout {
            private int sqXY, xy;
            private OnStateChangedListener onstate;
            private boolean isOpened = false;

            public CircleView(Context context, int xy) {
                super(context);
                this.xy = xy;
                init();
            }

            public CircleView(Context c) {
                super(c);
            }

            private void init() {
                sqXY = (int) ((xy * Math.sqrt(2)) / 2);
                setLayoutParams(new LayoutParams(xy, xy));
                setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isOpened = !isOpened;
                        if (isOpened) {
                            if (onstate != null) onstate.onOpen();
                        } else {
                            if (onstate != null) onstate.onClose();
                        }
                    }
                });
            }

            public void circle(int color) {
                ShapeDrawable oval = new ShapeDrawable(new OvalShape());
                oval.setIntrinsicHeight(xy);
                oval.setIntrinsicWidth(xy);
                oval.getPaint().setColor(color);
                setBackground(oval);
            }

            public void text(String upper, String lower) {
                if (upper.length() > 4) {
                    upper = upper.substring(0, 4);
                }
                removeAllViews();
                LinearLayout texts = new LinearLayout(getContext());
                texts.setOrientation(LinearLayout.VERTICAL);
                texts.setGravity(Gravity.CENTER);
                texts.addView(getTextView(upper, Main.getFontSize(getContext()) + 4, xy));
                texts.addView(getTextView(lower, Main.getFontSize(getContext()) - 10, sqXY));
                addView(texts);
            }

            private TextView getTextView(String t, int s, int par) {
                final TextView v = new TextView(getContext());
                v.setTextColor(Main.textColor);
                v.setTextSize(s);
                v.setText(t);
                v.setGravity(Gravity.CENTER);
                v.setTypeface(Main.getTypeface(getContext()));
                v.setLayoutParams(new LinearLayout.LayoutParams(par, par / 2));
                Main.colorChangeTunnle.addReceiver(new Tunnel.OnTunnel<Integer>() {
                    @Override
                    public void onReceive(Integer response) {
                        v.setTextColor(response);
                    }
                });
                return v;
            }

            public void setOnStateChangedListener(OnStateChangedListener osc) {
                onstate = osc;
            }

            interface OnStateChangedListener {
                void onOpen();

                void onClose();
            }

            static class CircleOption extends LinearLayout {
                private int xy, sqXY, pad;
                private FrameLayout icon;
                private FrameLayout desiredView;
                private OnDemand demandView;

                public CircleOption(Context context, int xy, int padding) {
                    super(context);
                    this.xy = xy;
                    pad = padding;
                    init();
                }

                public CircleOption(Context c) {
                    super(c);
                }

                private void init() {
                    desiredView = new FrameLayout(getContext());
                    icon = new FrameLayout(getContext());
                    sqXY = (int) ((xy * Math.sqrt(2)) / 2);
                    addView(icon);
                    setOrientation(HORIZONTAL);
                    setGravity(Gravity.CENTER);
                    setLayoutDirection(LAYOUT_DIRECTION_RTL);
                    setPadding(pad, pad, 0, 0);
                    setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, xy + pad));
                    //                    setBackgroundColor(Color.rgb(new Random().nextInt(255),new Random().nextInt(255),new Random().nextInt(255)));
                }

                public void circle(int color) {
                    ShapeDrawable oval = new ShapeDrawable(new OvalShape());
                    oval.setIntrinsicHeight(xy);
                    oval.setIntrinsicWidth(xy);
                    oval.getPaint().setColor(color);
                    icon.setBackground(oval);
                    icon.setLayoutParams(new LinearLayout.LayoutParams(xy, xy));
                    int pad = (xy - sqXY) / 2;
                    icon.setPadding(pad, pad, pad, pad);
                }

                public void setIcon(Drawable d) {
                    icon.removeAllViews();
                    ImageView iv = new ImageView(getContext());
                    iv.setImageDrawable(d);
                    iv.setLayoutParams(new LayoutParams(sqXY, sqXY));
                    icon.addView(iv);
                }

                public void setDesiredView(View v) {
                    desiredView.removeAllViews();
                    desiredView.addView(v);
                }

                public void setDesiredViewOnDemand(OnDemand desiredView) {
                    demandView = desiredView;
                }

                public interface OnDemand {
                    View demandView();
                }
            }
        }

        static class OptionHolder extends LinearLayout {
            FrameLayout content;
            LinearLayout options;
            int sidePadding;
            Drawable back;
            CircleView.CircleOption[] circleOptions;

            public OptionHolder(Context c) {
                super(c);
            }

            public OptionHolder(Context context, Drawable back, CircleView.CircleOption[] circleOptions, int sidePadding) {
                super(context);
                this.circleOptions = circleOptions;
                this.sidePadding = sidePadding;
                this.back = back;
                init();
            }

            public void emptyContent() {
                content.removeAllViews();
                content.setVisibility(View.GONE);
            }

            public void fadeIn() {
                setVisibility(View.VISIBLE);
                for (int o = circleOptions.length - 1; o >= 0; o--) {
                    circleOptions[o].setAlpha(0);
                    final Handler handler = new Handler();
                    final int finalO = o;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ObjectAnimator.ofFloat(circleOptions[finalO], View.ALPHA, 0, 1).setDuration(250).start();
                        }
                    }, 250 * (circleOptions.length - 1 - o));
                }
            }

            public void fadeOut() {
                for (int o = 0; o < circleOptions.length; o++) {
                    circleOptions[o].setAlpha(1);
                    final Handler handler = new Handler();
                    final int finalO = o;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ObjectAnimator.ofFloat(circleOptions[finalO], View.ALPHA, 1, 0).setDuration(250).start();
                        }
                    }, 250 * o);
                }
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setVisibility(View.GONE);
                    }
                }, 250 * circleOptions.length);
            }

            public void fadeOutContent() {
                ObjectAnimator oa = ObjectAnimator.ofFloat(content, View.ALPHA, 1, 0);
                oa.setDuration(250);
                oa.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        emptyContent();
                        content.setAlpha(1);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                oa.start();
            }

            private void init() {
                content = new FrameLayout(getContext());
                options = new LinearLayout(getContext());
                options.setOrientation(LinearLayout.VERTICAL);
                options.setGravity(Gravity.CENTER);
                for (final CircleView.CircleOption current : circleOptions) {
                    current.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            content.removeAllViews();
                            if (current.demandView != null) {
                                content.addView(current.demandView.demandView());
                            } else {
                                content.addView(current.desiredView);
                            }
                            content.setVisibility(View.VISIBLE);
                        }
                    });
                    options.addView(current);
                }
                setLayoutDirection(LAYOUT_DIRECTION_RTL);
                addView(options);
                addView(content);
                //                content.setBackground(getContext().getDrawable(R.drawable.rounded_rect));
                content.setBackground(back);
                content.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                content.setPadding(15, 15, 15, 15);
                content.setVisibility(View.GONE);
                setPadding(0, 0, 0, sidePadding);
            }

            public void drawCircles(int color) {
                for (CircleView.CircleOption circleOption : circleOptions) {
                    circleOption.circle(Color.argb(Values.circleAlpha, Color.red(color), Color.green(color), Color.blue(color)));
                }
            }
        }

        static class CurvedTextView extends View {
            private Path circle;
            private Paint tPaint;
            private Paint cPaint;
            private String text;
            private float textSize;
            private int textColor, sizeX, sizeY, radius;

            public CurvedTextView(Context c) {
                super(c);
                this.text = "Example";
                circle = new Path();
                int sizeX = 100, sizeY = 100, radius = 50, textColor = Color.WHITE, textSize = 20;
                circle.addCircle(((sizeX - radius * 2) / 2) + radius, ((sizeY - radius * 2) / 2) + radius, radius, Path.Direction.CW);
                cPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                cPaint.setStyle(Paint.Style.STROKE);
                cPaint.setColor(Color.LTGRAY);
                cPaint.setStrokeWidth(3);
                tPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                tPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                tPaint.setColor(textColor);
                tPaint.setTextSize(textSize);
                tPaint.setTypeface(Typeface.createFromAsset(c.getAssets(), Values.fontName));
            }

            public CurvedTextView(Context context, String text, float textSize, int textColor, int sizeX, int sizeY, int radius) {
                super(context);
                this.text = text;
                this.textSize = textSize;
                this.textColor = textColor;
                this.sizeX = sizeX;
                this.sizeY = sizeY;
                this.radius = radius;
                init();
            }

            public void setText(String s) {
                this.text = s;
                init();
            }

            private void init() {
                circle = new Path();
                circle.addCircle(((sizeX - radius * 2) / 2) + radius, ((sizeY - radius * 2) / 2) + radius, radius, Path.Direction.CW);
                cPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                cPaint.setStyle(Paint.Style.STROKE);
                cPaint.setColor(Color.LTGRAY);
                cPaint.setStrokeWidth(3);
                tPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                tPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                tPaint.setColor(textColor);
                tPaint.setTextSize(textSize);
                tPaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), Values.fontName));
            }

            @Override
            protected void onDraw(Canvas canvas) {
                canvas.drawTextOnPath(text, circle, 0, 0, tPaint);
                invalidate();
            }
        }
    }
}