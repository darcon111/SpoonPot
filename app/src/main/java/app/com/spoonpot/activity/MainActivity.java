package app.com.spoonpot.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.IntentSender;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import app.com.spoonpot.R;
import app.com.spoonpot.adapter.MenuAdapter;
import app.com.spoonpot.clases.AppController;
import app.com.spoonpot.clases.BottonNavigationView.BottomNavigationViewEx;
import app.com.spoonpot.clases.GPS;
import app.com.spoonpot.clases.ImagenCircular.CircleImageView;
import app.com.spoonpot.clases.InputFilterMinMax;
import app.com.spoonpot.clases.Screen;
import app.com.spoonpot.config.AppPreferences;
import app.com.spoonpot.config.Constants;
import app.com.spoonpot.helpers.BottomNavigationShiftHelper;
import app.com.spoonpot.helpers.HidingScrollListener;
import app.com.spoonpot.helpers.UtilHelper;
import app.com.spoonpot.holder.Friend;
import app.com.spoonpot.holder.Like;
import app.com.spoonpot.holder.Notificacion;
import app.com.spoonpot.holder.Plato;
import app.com.spoonpot.holder.User;
import cn.pedant.SweetAlert.SweetAlertDialog;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;



import static app.com.spoonpot.config.Constants.compareDate;


public class MainActivity extends AppCompatActivity {

    private int PROFILE = R.drawable.ic_user;
    private RecyclerView mRecyclerView_main, mUserRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DrawerLayout Drawer;
    private String TAG = MainActivity.class.getName();
    private AppPreferences app;
    private String[] TITLES = new String[8];
    private int[] ICONS = new int[8];
    private ActionBarDrawerToggle mDrawerToggle;
    private String imagen, name=null;
    private static FirebaseDatabase mDatabase;

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private DatabaseReference databasePlatos;
    private PlatoRecycleAdapter mPlatoAdapter;
    private FirebaseAuth auth;


    private BottomNavigationViewEx bottomNavigationView;

    private FirebaseUser user;
    private String provider;
    private ArrayList<Like> mListLike;
    private DatabaseReference databaseLike;
    private DatabaseReference databaseUsers;
    private ArrayList<Plato> mListPlato;
    //private FloatingActionButton floating;
    private User Utemp;
    private Toolbar toolbar;
    private GPS gps = null;


    private FrameLayout principal,toolbar_container;
    private Date fecha = new Date();
    final User[] temp = {new User()};



    private QBadgeView badgeView;
    private SweetAlertDialog pDialog;

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        app = new AppPreferences(getApplicationContext());
        /* set language*/
        Constants.setLanguage(app.getLanguage(), getApplicationContext());

        if (mDatabase == null) {
            try {
                mDatabase = FirebaseDatabase.getInstance();
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            } catch (DatabaseException e) {

            }

        }

        gps = new GPS(MainActivity.this);
        if (!gps.canGetLocation()) {
            gps.settingsrequest();
        }
                   /* toolbar*/
        toolbar = (Toolbar) findViewById(R.id.toolbaruser);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_list_white_24dp));
        } else {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_list_white_24dp));
        }

                /*main menu*/
        mRecyclerView_main = (RecyclerView) findViewById(R.id.RecyclerView_main); // Assigning the RecyclerView Object to the xml View
        mRecyclerView_main.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size


              /* menu main*/
        TITLES[0] = getString(R.string.help);
        TITLES[1] = getString(R.string.config);
        TITLES[2] = getString(R.string.share);
        TITLES[3] = getString(R.string.spoot);
        TITLES[4] = getString(R.string.what);
        TITLES[5] = getString(R.string.terminos);
        TITLES[6] = getString(R.string.condition);
        TITLES[7] = getString(R.string.end);

        ICONS[0] = R.drawable.ic_ayuda;
        ICONS[1] = R.drawable.ic_config;
        ICONS[2] = R.drawable.ic_share;
        ICONS[3] = R.drawable.ic_spoon_pot;
        ICONS[4] = R.drawable.what;
        ICONS[5] = R.drawable.ic_terminos;
        ICONS[6] = R.drawable.ic_condition;
        ICONS[7] = R.drawable.ic_exit;




        /* data info user*/
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        databaseUsers.keepSynced(true);

        Query userquery = databaseUsers
                .orderByChild("firebaseId").equalTo(user.getUid());

        userquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Utemp = postSnapshot.getValue(User.class);
                    databaseUsers.removeEventListener(this);


                    if (user != null) {
                        List<String> listProvider = user.getProviders();
                        provider = listProvider.get(0);

                        if(Utemp.getUrl_imagen()!=null){

                            if(!Utemp.getUrl_imagen().equals(""))
                            {
                                imagen=Utemp.getUrl_imagen().toString();
                            }else
                            {
                                if (user.getPhotoUrl() != null) {
                                    imagen = user.getPhotoUrl().toString();
                                }
                            }

                        }else {
                            // User is signed in
                            if (user.getPhotoUrl() != null) {
                                imagen = user.getPhotoUrl().toString();
                            }
                        }



                        if (Utemp == null) {
                            try {
                                name = user.getEmail().toString();
                            } catch (Exception e) {
                                if (user.getDisplayName() != null) {
                                    name = user.getDisplayName().toString();
                                }
                            }
                        } else {
                            if (!Utemp.getName().equals("")) {
                                name = Utemp.getName() + " " + Utemp.getLastname();
                            }
                        }


                        if (imagen == null) {
                            imagen = "";
                        }
                        if (name == null) {
                            name = "";
                        }
                    }




                }
                menu();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });


        if (user.isEmailVerified() == false) {

            pDialog= new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE);
            pDialog.setTitleText(getResources().getString(R.string.app_name));
            pDialog.setContentText(getString(R.string.error_email));
            pDialog.setConfirmText(getResources().getString(R.string.ok));
            pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    sDialog.dismissWithAnimation();
                    signOut();
                    finish();

                }
            });
            pDialog.show();

        }


        //getting the reference of publications node
        databasePlatos = FirebaseDatabase.getInstance().getReference("platos");
        databasePlatos.keepSynced(true);
        //get firebase auth instance
        auth = FirebaseAuth.getInstance();


        /* button navigation*/
        bottomNavigationView = (BottomNavigationViewEx) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setItemHeight(125);
        bottomNavigationView.enableAnimation(false);
        bottomNavigationView.enableItemShiftingMode(false);
        bottomNavigationView.enableShiftingMode(false);

        BottomNavigationShiftHelper.disableShiftMode(bottomNavigationView, this);
        /*switch (Screen.Size(getApplicationContext()) ){

            case eUNDEFINED:
               // screen=1;
                break;

            case eXLarge:
                bottomNavigationView.setItemHeight(180);
                break;

            case eLarge:
                //bottomNavigationView.setItemHeight(145);
                break;

            case eNormal:
               // screen=1; //ok

            case eSmall:
                //screen=1; //ok
                break;
        }*/

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        final Intent[] intent = {null};
                        switch (item.getItemId()) {
                            case R.id.nav_home:
                                mUserRecyclerView.setVisibility(View.VISIBLE);


                                break;
                            case R.id.nav_fav:
                                intent[0] = new Intent(MainActivity.this, FavoritoActivity.class);
                                startActivity(intent[0]);
                                break;
                            case R.id.nav_add:
                                if (Utemp.getName() != null) {
                                    if (Utemp.getName().equals("")) {


                                        pDialog= new SweetAlertDialog(MainActivity.this, SweetAlertDialog.NORMAL_TYPE);
                                        pDialog.setTitleText(getResources().getString(R.string.app_name));
                                        pDialog.setContentText(getString(R.string.profile));
                                        pDialog.setConfirmText(getResources().getString(R.string.ok));
                                        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismissWithAnimation();
                                                intent[0] = new Intent(MainActivity.this, RegisterActivity.class);
                                                intent[0].putExtra("id", Utemp.getId());
                                                startActivity(intent[0]);
                                            }
                                        });
                                        pDialog.setCancelText(getString(R.string.no));
                                        pDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismiss();
                                            }
                                        });
                                        pDialog.show();





                                    } else {
                                        intent[0] = new Intent(MainActivity.this, AddPlatoActivity.class);
                                        intent[0].putExtra("username", Utemp.getName() + " " + Utemp.getLastname());
                                        intent[0].putExtra("id", "0".toString());
                                        startActivity(intent[0]);
                                    }
                                }
                                break;
                            case R.id.nav_noti:
                                Log.e("value", "config");
                                intent[0] = new Intent(MainActivity.this, NotificationActivity.class);
                                startActivity(intent[0]);

                                break;

                            case R.id.nav_per:
                                intent[0] = new Intent(MainActivity.this, ProfileActivity.class);
                                startActivity(intent[0]);

                                Log.e("value", "config");
                                //intent = new Intent(MainActivity.this, PublicationActivity.class);

                                break;

                        }


                        return false;
                    }
                });













        /* list plato*/
        mListPlato = new ArrayList<Plato>();

        mUserRecyclerView = (RecyclerView) findViewById(R.id.platos_recycler_view);
        // Create a grid layout with two columns
        //GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        // Create a custom SpanSizeLookup where the first item spans both columns



        int paddingTop = UtilHelper.getToolbarHeight(MainActivity.this) + UtilHelper.getTabsHeight(MainActivity.this);
        mUserRecyclerView.setPadding(mUserRecyclerView.getPaddingLeft(), 150, mUserRecyclerView.getPaddingRight(), mUserRecyclerView.getPaddingBottom());


        mUserRecyclerView.setLayoutManager(layoutManager);

        mPlatoAdapter = new PlatoRecycleAdapter();
        mUserRecyclerView.setAdapter(mPlatoAdapter);
        mUserRecyclerView.addOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                hideViews();
            }

            @Override
            public void onShow() {
                showViews();
            }
        });



        principal = (FrameLayout) findViewById(R.id.principal);
        toolbar_container = (FrameLayout) findViewById(R.id.toolbar_container) ;



        /*update user */
        if (app.getFlag().equals("1")) {


            databaseUsers.child(app.getUserId()).child("firebase_code").setValue(app.getFirebasetoken());
            //app.setUserId(data[0].getId());
            app.setFlag("0");

        }




        badgeView = new QBadgeView(this);

        NotiTask();


        if(app.getInicio().equals("0"))
        {
            Constants.explicativo(MainActivity.this,getString(R.string.inicio));
            app.setInicio(1);
       }


    }


    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);
        Drawer.closeDrawers();
        super.onBackPressed();
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* secondary menu*/
        getMenuInflater().inflate(R.menu.menu_main, menu);


       /* MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);*/
        //menu.add(0, 2, 2, Constants.menuIconWithText(getResources().getDrawable(R.drawable.ic_lang), getResources().getString(R.string.language)));
        //menu.add(0, 3, 3, Constants.menuIconWithText(getResources().getDrawable(R.drawable.ic_exit), getResources().getString(R.string.end)));
        super.onCreateOptionsMenu(menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent i = new Intent(MainActivity.this, BusquedaActivity.class);
                startActivity(i);
                return true;
            case R.id.action_mapa:
                    mapa();
                return true;


        }

        return super.onOptionsItemSelected(item);

    }

      /* adapter*/

    public class PlatoRecycleAdapter extends RecyclerView.Adapter<PlatoRecycleHolder> {
        private int lastPosition = -1;

        @Override
        public PlatoRecycleHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_plato, viewGroup, false);
            return new PlatoRecycleHolder(v);
        }


        @Override
        public void onBindViewHolder(final PlatoRecycleHolder productHolder, final int i) {

            productHolder.mTitle.setText(mListPlato.get(i).getTitle());
            productHolder.mUser.setText(mListPlato.get(i).getUsername().toString());

            DecimalFormat df = new DecimalFormat("#.00");
            df.setMaximumFractionDigits(2);

            Float costo = Float.parseFloat(mListPlato.get(i).getCosto()) / Float.parseFloat(mListPlato.get(i).getRaciones());
            productHolder.mCosto.setText(df.format(costo).toString());
            if (!compareDate(mListPlato.get(i).getDia()+" "+mListPlato.get(i).getHora().replace("h",":"), new SimpleDateFormat("yyyy-MM-dd HH:mm").format(fecha))) {
                productHolder.mHora.setText(Constants.formatoFecha(mListPlato.get(i).getDia()));
                productHolder.reloj.setImageResource(R.drawable.ic_reloj);
            } else {
                productHolder.reloj.setImageResource(R.drawable.ic_reloj_rojo);
                productHolder.mHora.setText("");
            }


            //productHolder.mRacioles.setText(mListPlato.get(i).getOfrezco());
            productHolder.mRacioles.setText(mListPlato.get(i).getOfrezco() + "/" + mListPlato.get(i).getRaciones());

            //productHolder.mLike.setText(mListPlato.get(i).getLike());
            productHolder.mLike.setText(String.valueOf(mListPlato.get(i).getLike()));
            boolean like = false;
            int idlike = -1;
            for (int x = 0; x < mListLike.size(); x++) {
                if (mListLike.get(x).getUserid().equals(user.getUid())
                        && mListLike.get(x).getPlatoid().equals(mListPlato.get(i).getId())) {
                    like = true;
                    idlike = x;
                }
            }
            if (like) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    productHolder.mImageLike.setImageDrawable(getDrawable(R.drawable.ic_nolike));
                } else {
                    productHolder.mImageLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_nolike));
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    productHolder.mImageLike.setImageDrawable(getDrawable(R.drawable.ic_like));
                } else {
                    productHolder.mImageLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_like));
                }
            }
            final boolean finalLike = like;
            final int finalIdlike = idlike;
            productHolder.mImageLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        productHolder.mImageLike.setImageDrawable(getDrawable(R.drawable.ic_nolike));
                    } else {
                        productHolder.mImageLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_nolike));
                    }*/
                    if (finalLike) {

                        databaseLike.child(mListLike.get(finalIdlike).getId()).removeValue();
                        databasePlatos.child(mListPlato.get(i).getId()).child("like").setValue(mListPlato.get(i).getLike() - 1);
                        mListLike.remove(finalIdlike);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            productHolder.mImageLike.setImageDrawable(getDrawable(R.drawable.ic_like));
                        } else {
                            productHolder.mImageLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_like));
                        }
                    } else {
                        String id = databaseLike.push().getKey();
                        final Like templ = new Like();
                        templ.setId(id);
                        templ.setPlatoid(mListPlato.get(i).getId());
                        templ.setUserid(user.getUid());
                        databaseLike.child(id).setValue(templ);
                        mListLike.add(templ);
                        databasePlatos.child(mListPlato.get(i).getId()).child("like").setValue(mListPlato.get(i).getLike() + 1);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            productHolder.mImageLike.setImageDrawable(getDrawable(R.drawable.ic_nolike));
                        } else {
                            productHolder.mImageLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_nolike));
                        }

                        final DatabaseReference databaseNoti;
                        databaseNoti = FirebaseDatabase.getInstance().getReference("notificaciones");
                        id = databaseNoti.push().getKey();


                        final Query userquery = databaseUsers
                                .orderByChild("firebaseId").equalTo(mListPlato.get(i).getUser());

                        final String finalId = id;
                        userquery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //getting artist

                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    //getting artist
                                    temp[0] = postSnapshot.getValue(User.class);
                                    userquery.removeEventListener(this);
                                }

                                if (temp[0] != null) {

                                    String type = "1";
                                    if(temp[0].getMobile()!=null)
                                    {
                                        type=temp[0].getMobile();
                                    }

                                    databaseNoti.child(finalId).setValue(new Notificacion(user.getUid(),
                                             temp[0].getFirebaseId(),
                                            /*Utemp.getName() + " " + Utemp.getLastname() + " " +*/ getString(R.string.like),
                                            "0", finalId, "", 0));

                                    new Constants.PushTask().execute(getString(R.string.app_name),
                                            Utemp.getName() + " " + Utemp.getLastname() + " " + getString(R.string.like),
                                            temp[0].getFirebase_code(), type, "N","");




                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "onCancelled", databaseError.toException());
                            }
                        });


                    }
                }
            });


            Bitmap bitmap = Constants.decodeBase64(mListPlato.get(i).getImage());
            productHolder.mImage.setImageBitmap(bitmap);

            productHolder.mImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String idplato = mListPlato.get(i).getId();
                    Intent i = new Intent(MainActivity.this, DescripcionActivity.class);
                    i.putExtra("id",idplato);
                    startActivity(i);
                }
            });


            // Here you apply the animation when the view is bound
            //setAnimation(productHolder.itemView, i);

        }


        @Override
        public int getItemCount() {
            return mListPlato.size();
        }

        public void removeItem(int position) {
            mListPlato.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mListPlato.size());
            //Signal.get().reset();


        }

        private void setAnimation(View viewToAnimate, int position) {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition) {
                Animation animation;
                if (position % 2 == 0) {
                    animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.left_in);
                } else {
                    animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.right_in);
                }

                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }


    }

    public class PlatoRecycleHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public TextView mUser;
        public TextView mCosto;
        public TextView mRacioles;
        public TextView mHora;
        public ImageView mImageLike;
        public ImageView mImage;
        public TextView mLike;
        public ImageView reloj;


        public PlatoRecycleHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.txttitle);
            mUser = (TextView) itemView.findViewById(R.id.txtuser);
            mCosto = (TextView) itemView.findViewById(R.id.txtcosto);
            mRacioles = (TextView) itemView.findViewById(R.id.txtraciones);
            mHora = (TextView) itemView.findViewById(R.id.txthora);
            mImageLike = (ImageView) itemView.findViewById(R.id.imglike);
            mImage = (ImageView) itemView.findViewById(R.id.imagen);
            mLike = (TextView) itemView.findViewById(R.id.txlike);
            reloj = (ImageView) itemView.findViewById(R.id.imgreloj);

        }
    }



    //restart Activity
    public static void restartActivity(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, activity.getClass());
        activity.startActivity(intent);
        activity.finish();
    }


    //sign out method
    public void signOut() {
        // if(provider.equals("facebook.com")) {
        FirebaseAuth.getInstance().signOut();
        //}
        LoginManager.getInstance().logOut();

    }





    /* dialog language*/
    private void languageDialog() {
        final Dialog settingsDialog = new Dialog(this);
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View v = getLayoutInflater().inflate(R.layout.activity_language
                , null);

        ListView listView = (ListView) v.findViewById(R.id.listlanguage);
        String[] values = new String[]{getString(R.string.espanol), getString(R.string.english)};
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values);
        listView.setAdapter(spinnerArrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                if (position == 0) {

                    if (!app.getLanguage().equals("es")) {
                        Constants.setLanguage("es", getApplicationContext());
                        app.setLanguage("es");
                        restartActivity(MainActivity.this);
                    } else {
                        settingsDialog.dismiss();
                    }

                } else {

                    if (!app.getLanguage().equals("en")) {
                        Constants.setLanguage("en", getApplicationContext());
                        app.setLanguage("en");
                        restartActivity(MainActivity.this);
                    } else {
                        settingsDialog.dismiss();
                    }

                }
            }
        });

        settingsDialog.setContentView(v);
        settingsDialog.show();
    }





    @Override
    protected void onRestart() {
        super.onRestart();
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
    }


    @Override
    protected void onStart() {
        super.onStart();



        try {


            //load like
            databaseLike = FirebaseDatabase.getInstance().getReference("likes");
            databaseLike.keepSynced(true);
            Query likequery = databaseLike
                    .orderByChild("userid").equalTo(user.getUid().toString());
            mListLike = new ArrayList<Like>();

            likequery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //clearing the previous artist list
                    mListLike.clear();
                    //iterating through all the nodes
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        //getting artist
                        Like like = postSnapshot.getValue(Like.class);
                        //adding artist to the list
                        mListLike.add(like);
                    }
                    databaseLike.removeEventListener(this);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled", databaseError.toException());
                }
            });


            databasePlatos.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    //clearing the previous artist list
                    mListPlato.clear();

                    //iterating through all the nodes
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        //getting artist
                        Plato publi = postSnapshot.getValue(Plato.class);
                        //adding artist to the list
                        if (publi.getFavorito() == 0) {
                            if (gps.canGetLocation()) {
                                publi.setDistancia(Constants.distancia(gps.getLatitude(), gps.getLongitude(), (double) publi.getLag(), (double) publi.getLog()));
                            } else {
                                if (Utemp != null) {
                                    if (Utemp.getLat() != null) {
                                        publi.setDistancia(Constants.distancia(Double.parseDouble(Utemp.getLat()), Double.parseDouble(Utemp.getLog()), (double) publi.getLag(), (double) publi.getLog()));

                                    } else {
                                        publi.setDistancia(0);
                                    }
                                } else {
                                    publi.setDistancia(0);
                                }

                            }
                            mListPlato.add(publi);
                        }
                    }

                    if (mListPlato.size() > 0) {
                        //progress.setVisibility(View.GONE);

                        Comparator orden = Collections.reverseOrder(new SortbyDistancia2());
                        Collections.sort(mListPlato, orden);
                        //Collections.reverse(mListPlato);
                        mUserRecyclerView.setVisibility(View.VISIBLE);
                        mPlatoAdapter.notifyDataSetChanged();
                    } else {
                        //progress.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        /* other controller event*/
            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                    // A new comment has been added, add it to the displayed list
                    //Comment comment = dataSnapshot.getValue(Comment.class);

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so displayed the changed comment.
                    Plato change = dataSnapshot.getValue(Plato.class);
                    for (int i = 0; i < mListPlato.size(); i++) {
                        if (mListPlato.get(i).getId().equals(change.getId())) {
                            mListPlato.remove(i);
                            mListPlato.add(i, change);
                            mPlatoAdapter.notifyItemChanged(i);
                        }
                    }
                    String commentKey = dataSnapshot.getKey();

                    // ...
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so remove it.
                    String commentKey = dataSnapshot.getKey();

                    // ...
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                    // A comment has changed position, use the key to determine if we are
                    // displaying this comment and if so move it.
                    //Comment movedComment = dataSnapshot.getValue(Comment.class);
                    //String commentKey = dataSnapshot.getKey();

                    // ...
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                    //Toast.makeText(mContext, "Failed to load comments.",
                    //Toast.LENGTH_SHORT).show();
                }
            };

            databasePlatos.addChildEventListener(childEventListener);


            /* other controller event*/

            ChildEventListener childEventListenerUser = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                    // A new comment has been added, add it to the displayed list
                    //Comment comment = dataSnapshot.getValue(Comment.class);

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so displayed the changed comment.
                    Utemp = dataSnapshot.getValue(User.class);

                    // ...
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so remove it.
                    String commentKey = dataSnapshot.getKey();

                    // ...
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                    // A comment has changed position, use the key to determine if we are
                    // displaying this comment and if so move it.
                    //Comment movedComment = dataSnapshot.getValue(Comment.class);
                    //String commentKey = dataSnapshot.getKey();

                    // ...
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                    //Toast.makeText(mContext, "Failed to load comments.",
                    //Toast.LENGTH_SHORT).show();
                }
            };

            databaseUsers.addChildEventListener(childEventListenerUser);


        } catch (Exception e) {

        }


    }










    public void mapa()
    {
        Intent i = new Intent(this, MapaActivity.class);
        startActivity(i);
    }

    public void profile(View v)
    {
        if(Utemp!=null) {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            intent.putExtra("id", Utemp.getId());
            startActivity(intent);
        }
    }


    public void menu()
    {
        mAdapter = new MenuAdapter(TITLES, ICONS, name, PROFILE, imagen, MainActivity.this);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)

        mRecyclerView_main.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView

        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager

        mRecyclerView_main.setLayoutManager(mLayoutManager);

        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this, Drawer, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }

        }; // Drawer Toggle Object Made

        Drawer.addDrawerListener(mDrawerToggle);

        mRecyclerView_main.addOnItemTouchListener(new Constants.RecyclerTouchListener(getApplicationContext(), mRecyclerView_main, new Constants.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {

                Intent intent;

                switch (position) {
                    case 1:
                        intent = new Intent(MainActivity.this, HelpActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(MainActivity.this, ConfigActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(MainActivity.this, Share2Activity.class);
                        startActivity(intent);
                        break;
                    case 4:
                        intent = new Intent(MainActivity.this, SpoonPotActivity.class);
                        startActivity(intent);
                        break;
                    case 5:
                        intent = new Intent(MainActivity.this, ShareActivity.class);
                        startActivity(intent);
                        break;
                    case 6:
                        intent = new Intent(MainActivity.this, TerminosActivity.class);
                        startActivity(intent);
                        break;
                    case 7:
                        intent = new Intent(MainActivity.this, PoliticasActivity.class);
                        startActivity(intent);
                        break;
                    case 8:


                        pDialog= new SweetAlertDialog(MainActivity.this, SweetAlertDialog.NORMAL_TYPE);
                        pDialog.setTitleText(getResources().getString(R.string.app_name));
                        pDialog.setContentText(getString(R.string.exit));
                        pDialog.setConfirmText(getResources().getString(R.string.ok));
                        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                signOut();
                                finish();

                            }
                        });
                        pDialog.setCancelText(getString(R.string.no));
                        pDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        });
                        pDialog.show();


                        break;


                    default:
                        //Toast.makeText(vg_activity, "NO WINDOW", Toast.LENGTH_SHORT).show();
                        break;
                }

                mDrawerToggle.onDrawerClosed(mRecyclerView_main);
                Drawer.closeDrawers();
                return;
            }
        }));
    }


    class SortbyDistancia2 implements Comparator<Plato>
    {
        // Used for sorting in ascending order of
        // roll number
        public int compare(Plato a, Plato b)
        {
            return Math.round(a.getDistancia()-b.getDistancia());
        }
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

        timer.schedule(task, 0, 1100);  // interval of one minute

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



    private void hideViews() {
        toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
        mUserRecyclerView.setPadding(mUserRecyclerView.getPaddingLeft(), 0, mUserRecyclerView.getPaddingRight(), mUserRecyclerView.getPaddingBottom());
        //FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFabButton.getLayoutParams();
        //int fabBottomMargin = lp.bottomMargin;
        //mFabButton.animate().translationY(mFabButton.getHeight()+fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
    }

    private void showViews() {
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));

        //mFabButton.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
// Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:

                        break;
                    case Activity.RESULT_CANCELED:
                        gps.settingsrequest();//keep asking if imp or do whatever
                        break;
                }
                break;
        }
    }

}
