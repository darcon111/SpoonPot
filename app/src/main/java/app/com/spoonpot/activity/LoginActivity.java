package app.com.spoonpot.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.UserRecoverableException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.com.spoonpot.R;
import app.com.spoonpot.config.AppPreferences;
import app.com.spoonpot.config.Constants;
import app.com.spoonpot.holder.Plato;
import app.com.spoonpot.holder.User;
import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private AlertDialog.Builder message;
    private ProgressDialog progressDialog=null;
    /* *************************************
     *              GENERAL                *
     ***************************************/
    private TextView info;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CallbackManager mCallbackManager;
    private EditText txtemail,txtpass;

    /***************************************
     *              TWITTER                *
     ***************************************/
    public static final int RC_TWITTER_LOGIN = 2;

    private Button btn_twitter,btn_faceboook;

    private FirebaseUser user;
    private String provider;
    private String imagen,name;
    private AppPreferences app;
    private User Utemp;
    private DatabaseReference databaseUsers;
    private ArrayList<User> mListUser;
    private ValueEventListener  listen;
    private ConstraintLayout main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/RobotoLight.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        FacebookSdk.sdkInitialize(getApplicationContext());
        Fabric.with(this, new Crashlytics());
        super.onCreate(savedInstanceState);
        app = new AppPreferences(getApplicationContext());
        Constants.setLanguage(app.getLanguage(),getApplicationContext());
        /* Load the view and display it */
        setContentView(R.layout.activity_login);

        main=(ConstraintLayout) findViewById(R.id.main);

        try {
            PackageInfo info = getPackageManager().getPackageInfo("app.com.spoonpot", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        txtemail= (EditText) findViewById(R.id.txtemail);
        txtpass= (EditText) findViewById(R.id.txtpass);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        mListUser = new ArrayList<User>();

        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        databaseUsers.keepSynced(true);










        // ...
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out" );

                }
                // ...
            }
        };

        // Initialize Facebook Login button


        mCallbackManager = CallbackManager.Factory.create();
        final LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });

        btn_faceboook = (Button) findViewById(R.id.btn_facebook);
        btn_faceboook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.performClick();
                loginButton.setPressed(true);
                loginButton.invalidate();
                loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "facebook:onCancel");
                        // ...
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, "facebook:onError", error);
                        // ...
                    }
                });
                loginButton.setPressed(false);
                loginButton.invalidate();
            }
        });


         /* *************************************
         *                TWITTER              *
         ***************************************/
        btn_twitter = (Button) findViewById(R.id.btn_twitter);
        btn_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginWithTwitter();
            }
        });

           /* App permissions */
        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            if (checkPlayServices()) {
            }

        }
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (checkPlayServices()) {

            }

        }
        if (checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            if (checkPlayServices()) {

            }

        }

        /*databaseUsers.addValueEventListener(new ValueEventListener() {
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
        });*/


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

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        /*if (id == R.id.action_logout) {
            logout();
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().signOut();
        mAuth.addAuthStateListener(mAuthListener);



    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void auth(String token)
    {
        if(token.equals("")) {
            //authenticate user email
            mAuth.signInWithEmailAndPassword(txtemail.getText().toString(), txtpass.getText().toString())
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                            {
                                progressDialog.dismiss();
                                message = new AlertDialog.Builder(LoginActivity.this, R.style.AlertDialog);
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
                                message = new AlertDialog.Builder(LoginActivity.this, R.style.AlertDialog);
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


                                insertUser("");
                                databaseUsers.removeEventListener(listen);
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);

                                finish();
                            }
                        }
                    });
        }else
        {
            mAuth.signInWithCustomToken(token)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                // there was an error
                                Log.d(TAG, "error Login :" + task.getException().toString());
                                progressDialog.dismiss();
                                message = new AlertDialog.Builder(LoginActivity.this, R.style.AlertDialog);
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
                                insertUser("");
                                databaseUsers.removeEventListener(listen);
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
        }




    }


    public void insertUser(String imagen)
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
                data.setMobile("1");
                data.setDate_created(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date));
                data.setProvider(provider);

                //Saving
                databaseUsers.child(id).setValue(data);
                app.setUserId(id);


            }
        }else
        {
            app.setUserId(Utemp.getId());
            if(!imagen.equals("")) {
                databaseUsers.child(Utemp.getId()).child("url_imagen").setValue(imagen);


                UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(Uri.parse(imagen))
                        .build();

                user.updateProfile(profile)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User profile updated.");
                                }
                            }
                        });

            }


        }









    }

    public void login2(View v)
    {
        Intent intent = new Intent(LoginActivity.this, LoginEmailActivity.class);
        startActivity(intent);
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
        progressDialog = new ProgressDialog(LoginActivity.this);
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
                        }
                        else
                        {
                            progressDialog.dismiss();
                            /* correo verificacion */
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if(user.isEmailVerified()==false) {
                                user.sendEmailVerification();
                                message = new AlertDialog.Builder(LoginActivity.this, R.style.AlertDialog);
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
                            }else {
                                insertUser("");
                                databaseUsers.removeEventListener(listen);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            }
                        }

                        // ...
                    }
                });
    }


    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    private boolean checkPermission(String parametros) {
        int valor = ContextCompat.checkSelfPermission(LoginActivity.this, parametros);
        if (valor != PackageManager.PERMISSION_GRANTED) {
            requestPermission(parametros);
            return false;
        } else {
            return true;
        }
    }

    private boolean requestPermission(String parametro) {
        //parametro es el permiso a solicitar
        if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, parametro)) {
            Log.e(TAG, "Permission Granted, Now you can access location data.");
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{parametro}, PERMISSION_REQUEST_CODE);
            return false;
        } else {

            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{parametro}, PERMISSION_REQUEST_CODE);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //Snackbar.make(view,"Permission Granted, Now you can access location data.",Snackbar.LENGTH_LONG).show();
                    Log.e(TAG, "Permission Granted, Now you can access location data.");
                    restartActivity(this);

                } else {
                    Log.e(TAG, "Permission Denied, You cannot access location data.");

                    if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
                        message = new AlertDialog.Builder(LoginActivity.this,R.style.AppCompatAlertDialogStyle);
                    }
                    else {
                        message = new AlertDialog.Builder(LoginActivity.this);
                    }
                    message
                            .setTitle(R.string.error)
                            .setMessage(getText(R.string.permissions))
                            .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();

                                }
                            });

                    message.show();

                }
                break;
        }
    }

    public static void restartActivity(Activity actividad) {
        Intent intent = new Intent();
        intent.setClass(actividad, actividad.getClass());
        actividad.startActivity(intent);
        actividad.finish();
    }




    private void handleFacebookAccessToken(final AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {

                                auth(token.getToken().toString());
                        }else
                        {

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if(user.isEmailVerified()==false) {
                                user.sendEmailVerification();
                                message = new AlertDialog.Builder(LoginActivity.this, R.style.AlertDialog);
                                message
                                        .setTitle(R.string.app_name)
                                        .setMessage(getText(R.string.user_create))
                                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {
                                                LoginManager.getInstance().logOut();
                                            }
                                        });

                                message.show();
                            }else {
                                String imagen ="https://graph.facebook.com/"+token.getUserId()+"/picture?type=large";
                                insertUser(imagen);
                                databaseUsers.removeEventListener(listen);
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_TWITTER_LOGIN) {
            /*Map<String, String> options = new HashMap<String, String>();
            options.put("oauth_token", data.getStringExtra("oauth_token"));
            options.put("oauth_token_secret", data.getStringExtra("oauth_token_secret"));
            options.put("user_id", data.getStringExtra("user_id"));*/
            if(data!=null) {
                loginTwitter(data.getStringExtra("oauth_token"), data.getStringExtra("oauth_token_secret"));
            }
        }else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    /* ************************************
     *               TWITTER              *
     **************************************
     */
    private void loginWithTwitter() {
        startActivityForResult(new Intent(this, TwitterActivity.class), RC_TWITTER_LOGIN);
    }

    private void loginTwitter(final String token, final String secret)
    {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progressdialog);
        progressDialog.setCancelable(false);

        AuthCredential credential = TwitterAuthProvider.getCredential(token, secret);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {

                            auth(token);

                        }else
                        {
                            progressDialog.dismiss();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if(user.isEmailVerified()==false) {
                                user.sendEmailVerification();
                                message = new AlertDialog.Builder(LoginActivity.this, R.style.AlertDialog);
                                message
                                        .setTitle(R.string.app_name)
                                        .setMessage(getText(R.string.user_create))
                                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {

                                            }
                                        });

                                message.show();
                            }else {
                                databaseUsers.removeEventListener(listen);
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }


                        }

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
    public void clear()
    {
        txtemail.setText("");
        txtpass.setText("");
    }

    public void register(View v)
    {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void restablecer(View view)
    {

        final Dialog settingsDialog = new Dialog(this);
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View v=getLayoutInflater().inflate(R.layout.item_restablecer
                , null);

        Button btnclose=(Button) v.findViewById(R.id.close);
        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsDialog.dismiss();
            }
        });

        final EditText txtemail=(EditText) v.findViewById(R.id.txtemail);

        Button btnsend=(Button) v.findViewById(R.id.send);
        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!txtemail.getText().toString().equals("")) {
                    mAuth.sendPasswordResetEmail(txtemail.getText().toString());

                    settingsDialog.dismiss();

                    Snackbar snackbar = Snackbar
                            .make(main, "Por favor revise su email!!!", Snackbar.LENGTH_LONG);

                    // Changing action button text color
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);

                    snackbar.show();
                }
            }
        });

        settingsDialog.setContentView(v);
        settingsDialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(settingsDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        settingsDialog.show();
        settingsDialog.getWindow().setAttributes(lp);
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


}
