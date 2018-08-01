package app.com.spoonpot.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import app.com.spoonpot.R;
import app.com.spoonpot.config.AppPreferences;
import app.com.spoonpot.config.Constants;
import app.com.spoonpot.holder.User;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static app.com.spoonpot.config.Constants.setLanguage;

public class ConfigActivity extends AppCompatActivity {

    private DatabaseReference databaseUser;
    private User Utemp;
    private DatabaseReference databaseUsers;
    private FirebaseUser user;
    private String TAG = ConfigActivity.class.getName();
    private AppPreferences app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/RobotoLight.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        app = new AppPreferences(getApplicationContext());
        /* set language*/
        setLanguage(app.getLanguage(),getApplicationContext());

            /* toolbar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbaruser);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        databaseUsers.keepSynced(true);

        user = FirebaseAuth.getInstance().getCurrentUser();
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

    public void profile(View v)
    {
        if(Utemp!=null) {
            Intent intent = new Intent(ConfigActivity.this, RegisterActivity.class);
            intent.putExtra("id", Utemp.getId());
            startActivity(intent);
        }
    }

    public void noti(View v)
    {
        Intent intent = new Intent(ConfigActivity.this, ConfigNotiActivity.class);
        startActivity(intent);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /* dialog language*/
    public void languageDialog(View v) {
        final Dialog settingsDialog = new Dialog(this);
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View v1 = getLayoutInflater().inflate(R.layout.activity_language
                , null);

        ListView listView = (ListView) v1.findViewById(R.id.listlanguage);
        String[] values = new String[]{getString(R.string.espanol), getString(R.string.english)};
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values);
        listView.setAdapter(spinnerArrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                if (position == 0) {

                    if (!app.getLanguage().equals("es")) {
                        setLanguage("es", getApplicationContext());
                        app.setLanguage("es");
                        finish();
                        //restartActivity(ConfigActivity.this);
                        //setLanguage(app.getLanguage(),ConfigActivity.this);
                        Intent i = getBaseContext().getPackageManager()
                                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);

                    } else {
                        settingsDialog.dismiss();
                    }

                } else {

                    if (!app.getLanguage().equals("en")) {
                        setLanguage("en", getApplicationContext());
                        app.setLanguage("en");
                        //restartActivity(ConfigActivity.this);
                        //setLanguage(app.getLanguage(),ConfigActivity.this);
                        Intent i = getBaseContext().getPackageManager()
                                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    } else {
                        settingsDialog.dismiss();
                    }

                }
            }
        });

        settingsDialog.setContentView(v1);
        settingsDialog.show();
    }

    //restart Activity
    public static void restartActivity(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, activity.getClass());
        activity.startActivity(intent);
        activity.finish();
    }

}
