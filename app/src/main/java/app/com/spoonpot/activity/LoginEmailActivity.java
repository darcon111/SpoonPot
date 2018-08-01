package app.com.spoonpot.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.com.spoonpot.R;
import app.com.spoonpot.config.AppPreferences;
import app.com.spoonpot.config.Constants;
import app.com.spoonpot.holder.User;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginEmailActivity extends AppCompatActivity {
    private EditText txtemail,txtpass,txtrepass;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog=null;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private AlertDialog.Builder message;

    private ArrayList<User> mListUser;
    private ValueEventListener listen;
    private DatabaseReference databaseUsers;
    private FirebaseUser user;
    private User Utemp;
    private String provider;
    private String imagen,name;
    private AppPreferences app;
    private Switch onswitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/RobotoLight.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        app = new AppPreferences(getApplicationContext());
        Constants.setLanguage(app.getLanguage(),getApplicationContext());
        setContentView(R.layout.activity_login_email);

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

        txtemail= (EditText) findViewById(R.id.txtemail);
        txtpass= (EditText) findViewById(R.id.txtpass);
        txtrepass = (EditText) findViewById(R.id.txtrepass);
        onswitch = (Switch) findViewById(R.id.onswitch);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mListUser = new ArrayList<User>();
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        databaseUsers.keepSynced(true);

        listen= new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //clearing the previous artist list
                mListUser.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    User temp = postSnapshot.getValue(User.class);
                    //adding artist to the list

                    mListUser.add(temp);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseUsers.addValueEventListener(listen);
    }

    public void clear()
    {
        txtemail.setText("");
        txtpass.setText("");
        txtrepass.setText("");
    }

    public void auth(String token)
    {
        if(token.equals("")) {
            //authenticate user email
            mAuth.signInWithEmailAndPassword(txtemail.getText().toString(), txtpass.getText().toString())
                    .addOnCompleteListener(LoginEmailActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                            {
                                progressDialog.dismiss();
                                message = new AlertDialog.Builder(LoginEmailActivity.this, R.style.AlertDialog);
                                message
                                        .setTitle(R.string.app_name)
                                        .setMessage(getText(R.string.error_login_pass))
                                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {
                                                clear();

                                            }
                                        });

                                message.show();
                                return;
                            }

                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                progressDialog.dismiss();
                                message = new AlertDialog.Builder(LoginEmailActivity.this, R.style.AlertDialog);
                                message
                                        .setTitle(R.string.app_name)
                                        .setMessage(getText(R.string.error_user))
                                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {
                                                clear();

                                            }
                                        });

                                message.show();
                                return;
                            }




                            if (!task.isSuccessful()) {
                                progressDialog.dismiss();

                                /*mAuth.fetchProvidersForEmail(txtemail.getText().toString()).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                                        if(task.isSuccessful()){
                                            ///////// getProviders() will return size 1. if email ID is available.
                                            //task.getResult().getProviders();
                                            if(task.getResult().getProviders().size()>=1)
                                            {
                                                message = new AlertDialog.Builder(LoginActivity.this, R.style.AlertDialog);
                                                message
                                                        .setTitle(R.string.app_name)
                                                        .setMessage(getText(R.string.error_user))
                                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                LoginManager.getInstance().logOut();
                                                                FirebaseAuth.getInstance().signOut();
                                                                clear();
                                                            }
                                                        });

                                                message.show();
                                            }
                                        }
                                    }
                                });*/


                            } else {
                                progressDialog.dismiss();
                                insertUser();
                                databaseUsers.removeEventListener(listen);
                                Intent intent = new Intent(LoginEmailActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
        }else
        {
            mAuth.signInWithCustomToken(token)
                    .addOnCompleteListener(LoginEmailActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                // there was an error
                                Log.d(TAG, "error Login :" + task.getException().toString());
                                progressDialog.dismiss();
                                message = new AlertDialog.Builder(LoginEmailActivity.this, R.style.AlertDialog);
                                message
                                        .setTitle(R.string.app_name)
                                        .setMessage(getText(R.string.error_user))
                                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {
                                                LoginManager.getInstance().logOut();
                                                FirebaseAuth.getInstance().signOut();

                                            }
                                        });

                                message.show();

                            } else {
                                insertUser();
                                databaseUsers.removeEventListener(listen);
                                Intent intent = new Intent(LoginEmailActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
        }




    }

    public void login(View v)
    {
        if(txtemail.getText().toString().equals("") || !Constants.validateEmail(txtemail.getText().toString()))
        {
            txtemail.setError(getString(R.string.error_mail));
            return ;
        }
        if(txtpass.getText().toString().equals(""))
        {
            txtpass.setError(getString(R.string.error_pass));
            return ;
        }
        if(!txtpass.getText().toString().equals(txtrepass.getText().toString()))
        {
            txtrepass.setError(getString(R.string.error_repass));
            return ;
        }
        if(onswitch.isChecked()) {
            progressDialog = new ProgressDialog(LoginEmailActivity.this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progressdialog);
            progressDialog.setCancelable(false);

            mAuth.createUserWithEmailAndPassword(txtemail.getText().toString(), txtpass.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.


                            if (!task.isSuccessful()) {
                                auth("");
                            } else {
                                progressDialog.dismiss();
                            /* correo verificacion */
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user.isEmailVerified() == false) {
                                    user.sendEmailVerification();
                                    message = new AlertDialog.Builder(LoginEmailActivity.this, R.style.AlertDialog);
                                    message
                                            .setTitle(R.string.app_name)
                                            .setMessage(getText(R.string.user_create))
                                            .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    clear();
                                                }
                                            });

                                    message.show();
                                } else {
                                    insertUser();
                                    databaseUsers.removeEventListener(listen);
                                    Intent intent = new Intent(LoginEmailActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }

                            // ...
                        }
                    });
        }else
        {
            message = new AlertDialog.Builder(LoginEmailActivity.this, R.style.AlertDialog);
            message
                    .setTitle(R.string.app_name)
                    .setMessage(getText(R.string.acepte_terminos))
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            
                        }
                    });

            message.show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(LoginEmailActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(LoginEmailActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                //------------
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void insertUser()
    {



        user = FirebaseAuth.getInstance().getCurrentUser();

        if(mListUser.size()>0)
        {
            for(int i=0;i<mListUser.size();i++)
            {
                if(mListUser.get(i).getFirebaseId().equals(user.getUid()))
                {
                    Utemp=mListUser.get(i);
                }
            }
        }


       /* Query userquery = databaseUsers
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
        });*/




        if(Utemp==null)
        {
            if (user != null) {
                List<String> listProvider =user.getProviders();
                provider=listProvider.get(0);
                // User is signed in
                if(user.getPhotoUrl()!=null) {
                    imagen = user.getPhotoUrl().toString();
                }

                try{
                    name=user.getEmail().toString();
                }catch (Exception e)
                {
                    if(user.getDisplayName()!=null) {
                        name = user.getDisplayName().toString();
                    }
                }


                if(imagen==null){imagen="";}
                if(name==null){name="";}

                String id = databaseUsers.push().getKey();
                User data = new User();
                data.setId(id);
                data.setFirebaseId(user.getUid());
                data.setEmail(user.getEmail());
                if(imagen==null){
                    imagen="";
                }
                data.setUrl_imagen(imagen);
                data.setName("");
                data.setLastname("");
                data.setDate_created("");
                data.setFecha_nac("");
                data.setGenero("");
                data.setType("1");
                data.setLat("0");
                data.setLog("0");
                data.setFirebase_code("");
                Date date = new Date();
                data.setDate_created(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date));
                data.setProvider(provider);

                //Saving
                databaseUsers.child(id).setValue(data);
                app.setUserId(id);


            }
        }else
        {
            app.setUserId(Utemp.getId());
        }









    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void terminos(View v)
    {
        startActivity( new Intent(LoginEmailActivity.this,TerminosActivity.class));
    }

}
