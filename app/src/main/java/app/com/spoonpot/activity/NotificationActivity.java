package app.com.spoonpot.activity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

import app.com.spoonpot.R;
import app.com.spoonpot.adapter.NotiMenAdapter;
import app.com.spoonpot.clases.BottonNavigationView.BottomNavigationViewEx;
import app.com.spoonpot.clases.Screen;
import app.com.spoonpot.config.AppPreferences;
import app.com.spoonpot.helpers.BottomNavigationShiftHelper;
import app.com.spoonpot.holder.User;
import q.rorbin.badgeview.QBadgeView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NotificationActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener{

    private TabHost host;
    private ViewPager viewPager;
    private BottomNavigationViewEx bottomNavigationView;
    private AppPreferences app;
    private User Utemp;
    private DatabaseReference databaseUsers;
    private FirebaseUser user;
    private String TAG = NotificationActivity.class.getName();

    TextView text;
    TabLayout.Tab tab1;
   TextView text2;
    TabLayout.Tab tab2;

    QBadgeView badgeView;
    QBadgeView badgeView2;
    final CounterClass timer=null;
    TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/RobotoLight.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

                /* toolbar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbaruser);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");


        app = new AppPreferences(getApplicationContext());
         /* data info user*/
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        databaseUsers.keepSynced(true);

        Query userquery = databaseUsers
                .orderByChild("firebaseId").equalTo(user.getUid());

        userquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //getting artist

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Utemp = postSnapshot.getValue(User.class);
                    databaseUsers.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });

        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        tab1 = tabLayout.getTabAt(0);
        text = new TextView(this);
        text.setTextColor(getResources().getColor(R.color.colorSecondaryText));
        text.setText(getString(R.string.noti));
        text.setGravity(Gravity.BOTTOM | Gravity.CENTER);
        //text.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        tab1.setCustomView(text);


        tab2 = tabLayout.getTabAt(1);
        text2 = new TextView(this);
        text2.setTextColor(getResources().getColor(R.color.colorSecondaryText));
        text2.setText(getString(R.string.sms));
        text2.setGravity(Gravity.BOTTOM | Gravity.CENTER);
        text2.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        tab2.setCustomView(text2);


        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.pager);


        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



        //Creating our pager adapter
        NotiMenAdapter adapter = new NotiMenAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        //Adding adapter to pager
        viewPager.setAdapter(adapter);

        //Adding onTabSelectedListener to swipe views
        tabLayout.setOnTabSelectedListener(this);

                 /* button navigation*/
        bottomNavigationView = (BottomNavigationViewEx)findViewById(R.id.bottom_navigation);
        bottomNavigationView.enableAnimation(false);
        bottomNavigationView.enableItemShiftingMode(false);
        bottomNavigationView.enableShiftingMode(false);
        BottomNavigationShiftHelper.disableShiftMode(bottomNavigationView,getApplicationContext());




        bottomNavigationView.getMenu().getItem(3).setChecked(true);
        //bottomNavigationView.removeAllViewsInLayout();
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        final Intent[] intent = {null};
                        switch (item.getItemId()) {
                            case R.id.nav_home:
                                finish();
                                break;
                            case R.id.nav_fav:
                                intent[0] = new Intent(NotificationActivity.this, FavoritoActivity.class);
                                startActivity(intent[0]);
                                break;
                            case R.id.nav_add:
                                intent[0] = new Intent(NotificationActivity.this, AddPlatoActivity.class);
                                intent[0].putExtra("id","0".toString());
                                intent[0].putExtra("username",Utemp.getName()+" "+Utemp.getLastname());
                                startActivity(intent[0]);
                                finish();
                                break;
                            case R.id.nav_noti:

                                break;

                            case R.id.nav_per:
                                intent[0] = new Intent(NotificationActivity.this, ProfileActivity.class);
                                startActivity(intent[0]);
                                finish();
                                Log.e("value", "config");
                                //intent = new Intent(MainActivity.this, PublicationActivity.class);

                                break;

                        }


                        return true;
                    }
                });


        badgeView = new QBadgeView(this);
        badgeView2 = new QBadgeView(this);


        NotiTask();




       // final CounterClass timer = new CounterClass(1000, 1000); //3 INTERVALOS DE 1 SEGUNDO
       // timer.start(); //EMPIEZA A CONTAR



    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {


        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                //------------
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }




    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("NewApi")
    public class CounterClass extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //MIENTRAS ESTÁ CONTANDO
        }

        @Override
        public void onFinish() {
            //Has finished counting


            //timer.start();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();


       /* if(!app.getNoti().equals("0")) {


            BadgeFactory.createOval(this)
                    //.setTextColor(R.color.colorDivider)
                    .setWidthAndHeight(18,18)
                    //.setBadgeBackground(R.color.error)
                    .setTextSize(12)
                    //.setBadgeGravity(Gravity.TOP|Gravity.RIGHT)
                    .setBadgeCount(app.getNoti())
                    .bind(text);
        }
        tab1.setCustomView(text);


        if(!app.getMensajes().equals("0")) {

            BadgeFactory.createOval(this)
                    //.setTextColor(R.color.colorDivider)
                    .setWidthAndHeight(18,18)
                    //.setBadgeBackground(R.color.error)
                    .setTextSize(12)
                    //.setBadgeGravity(Gravity.TOP|Gravity.RIGHT)
                    .setBadgeCount(app.getMensajes())
                    .bind(text2);

        }
        tab2.setCustomView(text2);*/




    }


    private void NotiTask() {

        final Handler handler = new Handler();
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            AsyncTaskRunner Task = new AsyncTaskRunner();
                            Task.execute();
                        } catch (Exception e) {
                            // error, do something
                        }
                    }
                });
            }
        };

        timer.schedule(task, 0, 1000);  // interval of one minute

    }


    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            int contador = Integer.parseInt(app.getNoti());
            badgeView.setBadgeNumber(contador);
            badgeView.setGravityOffset(4, 0, true);
            badgeView.bindTarget(tab1.getCustomView());


            int contador2 = Integer.parseInt(app.getMensajes());
            badgeView2.setBadgeNumber(contador2);
            badgeView2.setGravityOffset(20, 0, true);
            badgeView2.bindTarget(tab2.getCustomView());
        }


        @Override
        protected void onPreExecute() {

        }


        @Override
        protected void onProgressUpdate(String... text) {


        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


}
