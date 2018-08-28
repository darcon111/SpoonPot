package app.com.spoonpot.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;

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
import app.com.spoonpot.adapter.PagerAdapter;
import app.com.spoonpot.clases.BottonNavigationView.BottomNavigationViewEx;
import app.com.spoonpot.clases.Screen;
import app.com.spoonpot.config.AppPreferences;
import app.com.spoonpot.config.Constants;
import app.com.spoonpot.helpers.BottomNavigationShiftHelper;
import app.com.spoonpot.holder.User;
import q.rorbin.badgeview.QBadgeView;



public class FavoritoActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener{
    private TabHost host;
    private ViewPager viewPager;
    private BottomNavigationViewEx bottomNavigationView;
    private AppPreferences app;
    private User Utemp;
    private DatabaseReference databaseUsers;
    private FirebaseUser user;
    private String TAG = FavoritoActivity.class.getName();
    QBadgeView badgeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorito);


              /* toolbar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbaruser);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");


        app = new AppPreferences(getApplicationContext());
         /* data info user*/
        user = FirebaseAuth.getInstance().getCurrentUser();
        //Initializing the tablayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);



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
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

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


        bottomNavigationView.getMenu().getItem(1).setChecked(true);
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

                                break;
                            case R.id.nav_add:
                                intent[0] = new Intent(FavoritoActivity.this, AddPlatoActivity.class);
                                intent[0].putExtra("id","0".toString());
                                intent[0].putExtra("username",Utemp.getName()+" "+Utemp.getLastname());
                                startActivity(intent[0]);
                                finish();
                                break;
                            case R.id.nav_noti:
                                Log.e("value", "config");
                                intent[0] = new Intent(FavoritoActivity.this, NotificationActivity.class);
                                startActivity(intent[0]);
                                finish();
                                break;

                            case R.id.nav_per:
                                intent[0] = new Intent(FavoritoActivity.this, ProfileActivity.class);
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
        NotiTask();

        if(app.getFavorito().equals("0"))
        {
            Constants.explicativo(FavoritoActivity.this,getString(R.string.favorito));
            app.setFavorito(1);
        }

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

    @Override
    protected void onStart() {
        super.onStart();


        //Query userquery = databaseUsers
        //  .orderByChild("userid").endAt(user.getUid().toString());
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

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
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
            int contador = Integer.parseInt(app.getNoti())+ Integer.parseInt(app.getMensajes());
            badgeView.setBadgeNumber(contador);
            badgeView.bindTarget(bottomNavigationView.getBottomNavigationItemView(3));

        }


        @Override
        protected void onPreExecute() {

        }


        @Override
        protected void onProgressUpdate(String... text) {


        }
    }



}
