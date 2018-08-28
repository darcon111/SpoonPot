package app.com.spoonpot.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.ViewFlipper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kyleduo.switchbutton.SwitchButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import app.com.spoonpot.R;
import app.com.spoonpot.clases.GPS;
import app.com.spoonpot.config.AppPreferences;
import app.com.spoonpot.holder.User;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class RegisterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private String TAG = RegisterActivity.class.getName();
    private GPS gps = null;
    private String lat,log;
    private FirebaseUser user;
    private DatabaseReference databaseUsers;
    private EditText txtname,txtapellido,txtfecha,txtbiografia;

    private SwitchButton sb;
    private AppPreferences app;
    private User Utemp;

    private SweetAlertDialog pDialog;

 @Override
    protected void onCreate(Bundle savedInstanceState) {

         setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        app = new AppPreferences(getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbaruser);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.arrow));
        } else {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.arrow));
        }

     gps = new GPS(RegisterActivity.this);
     if (!gps.canGetLocation()) {
         gps.showSettingsAlert();
     }

       /* data info user*/
     user = FirebaseAuth.getInstance().getCurrentUser();


     txtname=(EditText) findViewById(R.id.txtnombre);
     txtapellido=(EditText) findViewById(R.id.txtapellidos);
     txtfecha=(EditText) findViewById(R.id.txtfecha);
     txtbiografia=(EditText) findViewById(R.id.txtbio);
     sb=(SwitchButton) findViewById(R.id.switch1);

     txtbiografia.setScroller( new Scroller(getApplicationContext()));
     txtbiografia.setMaxLines(10);
     txtbiografia.setVerticalScrollBarEnabled(true);
     txtbiografia.setMovementMethod(new ScrollingMovementMethod());
     txtbiografia.setOnTouchListener(new View.OnTouchListener() {
         @Override
         public boolean onTouch(View v, MotionEvent event) {
             v.getParent().requestDisallowInterceptTouchEvent(true);
             switch (event.getAction() & MotionEvent.ACTION_MASK){
                 case MotionEvent.ACTION_UP:
                     v.getParent().requestDisallowInterceptTouchEvent(false);
                     break;
             }
             return false;
         }
     });


     txtfecha.setOnTouchListener(new View.OnTouchListener() {
         @SuppressLint("ClickableViewAccessibility")
         @Override
         public boolean onTouch(View v, MotionEvent event) {
             // TODO Auto-generated method stub
             if (event.getAction() == MotionEvent.ACTION_DOWN) {
                 Calendar now = Calendar.getInstance();
                 DatePickerDialog dpd = DatePickerDialog.newInstance(
                         RegisterActivity.this,
                         now.get(Calendar.YEAR),
                         now.get(Calendar.MONTH),
                         now.get(Calendar.DAY_OF_MONTH)
                 );
                 dpd.show(getFragmentManager(), "Datepickerdialog");
             }
             Log.i("click text", "kakak");
             return false;
         }
     });

     databaseUsers = FirebaseDatabase.getInstance().getReference("users");




    }




    @Override
    protected void onStart() {
        super.onStart();

        Query userquery = databaseUsers
                .orderByChild("id").equalTo(app.getUserId());

        userquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //getting artist

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Utemp = postSnapshot.getValue(User.class);

                }

                if(Utemp!=null)
                {
                    txtname.setText(Utemp.getName());
                    txtapellido.setText(Utemp.getLastname());
                    txtfecha.setText(Utemp.getFecha_nac());
                    txtbiografia.setText(Utemp.getBiografia());
                    if(Utemp.getGenero().equals("1"))
                    {
                        sb.setChecked(true);
                    }else
                    {
                        sb.setChecked(false);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });



    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

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
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear = (++monthOfYear);
        String month = monthOfYear <= 9 ? "0" + monthOfYear : "" + monthOfYear;
        String day = dayOfMonth <= 9 ? "0" + dayOfMonth : "" + dayOfMonth;

        String date = year + "-" + month + "-" + day;
        txtfecha.setText(date);
    }

    @SuppressLint("RestrictedApi")
    public void save(View v)
    {
        String error="";
        if(txtname.getText().toString().trim().equals(""))
        {
            error+=getText(R.string.error_name).toString()+"\n";
        }
        if(txtapellido.getText().toString().trim().equals(""))
        {
            error+=getText(R.string.error_lastname).toString()+"\n";
        }
        if(txtfecha.getText().toString().trim().equals(""))
        {
            error+=getText(R.string.error_fecha).toString()+"\n";
        }
        if(txtbiografia.getText().toString().trim().equals(""))
        {
            error+=getText(R.string.error_biografia).toString()+"\n";
        }
        if(!error.equals(""))
        {



            pDialog= new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE);
            pDialog.setTitleText(getResources().getString(R.string.app_name));
            pDialog.setContentText(error);
            pDialog.setConfirmText(getResources().getString(R.string.ok));
            pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    sDialog.dismissWithAnimation();

                }
            });
            pDialog.show();

            return;
        }
        gps.getLocation();
        gps.city();

        lat=String.valueOf(gps.getLatitude());
        log=String.valueOf(gps.getLongitude());




       /* User user=new User();
        user.setId(getIntent().getExtras().getString("id"));
        user.setLat((float) Double.parseDouble(lat));
        user.setLog((float) Double.parseDouble(log));
        user.setName(txtname.getText().toString());
        user.setFecha_nac(txtfecha.getText().toString());
        user.setLastname(txtapellido.getText().toString());*/

        //databaseUsers.child(getIntent().getExtras().getString("id")).setValue(user);
        databaseUsers.child(getIntent().getExtras().getString("id")).child("lat").setValue(lat);
        databaseUsers.child(getIntent().getExtras().getString("id")).child("log").setValue(log);
        databaseUsers.child(getIntent().getExtras().getString("id")).child("name").setValue(txtname.getText().toString().trim());
        databaseUsers.child(getIntent().getExtras().getString("id")).child("lastname").setValue(txtapellido.getText().toString().trim());
        databaseUsers.child(getIntent().getExtras().getString("id")).child("fecha_nac").setValue(txtfecha.getText().toString().trim());
        if(sb.isChecked()) {
            databaseUsers.child(getIntent().getExtras().getString("id")).child("genero").setValue("1");
        }else
        {
            databaseUsers.child(getIntent().getExtras().getString("id")).child("genero").setValue("2");
        }
        databaseUsers.child(getIntent().getExtras().getString("id")).child("biografia").setValue(txtbiografia.getText().toString().trim());

        finish();

    }



}
