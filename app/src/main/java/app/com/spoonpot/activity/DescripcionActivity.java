package app.com.spoonpot.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.text.Line;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.com.spoonpot.R;
import app.com.spoonpot.clases.GPS;
import app.com.spoonpot.clases.ImagenCircular.CircleImageView;
import app.com.spoonpot.clases.InputFilterMinMax;
import app.com.spoonpot.config.Constants;
import app.com.spoonpot.holder.Friend;
import app.com.spoonpot.holder.Like;
import app.com.spoonpot.holder.Notificacion;
import app.com.spoonpot.holder.Plato;
import app.com.spoonpot.holder.User;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static app.com.spoonpot.config.Constants.compareDate;

public class DescripcionActivity extends AppCompatActivity {


    private LinearLayout descri;
    private ImageView imagendescri;
    private TextView txtcosto, txtraciones, txthora, txtdes, txtname, txtalert1, txtalert2, txlike, txtnameplato, txtdistancia;
    private GoogleMap map;
    private MapView mapView;
    private ImageView imgshare, imgfriend, imgchat, imglike;
    private EditText cantidad;
    private CircleImageView imgUser;
    private Button btnre,btnsugerir;
    private RatingBar bar;
    private LinearLayout contenedor;
    private DatabaseReference databasePlatos;
    private DatabaseReference databaseFriend;
    private DatabaseReference databaseUsers;
    private DatabaseReference databaseCocinero;
    private DatabaseReference databaseLike;


    private FirebaseUser user;
    private String TAG = DescripcionActivity.class.getName();
    private Plato plato;
    private LinearLayout principal;
    private Date fecha = new Date();
    private User InfoUser,InfoCocinero;
    private ArrayList<Like> mListLike;
    private GPS gps = null;
    private boolean flagfriends = false;

    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/RobotoLight.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descripcion);

          /* toolbar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbaruser);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

        extras = getIntent().getExtras();

        descri = (LinearLayout) findViewById(R.id.descri);
        imagendescri = (ImageView) findViewById(R.id.imagendes);
        txtcosto = (TextView) findViewById(R.id.txtcosto);
        txtraciones = (TextView) findViewById(R.id.txtraciones);
        txthora = (TextView) findViewById(R.id.txthora);
        txtdes = (TextView) findViewById(R.id.txtdes);
        txtname = (TextView) findViewById(R.id.txtname);
        txtalert1 = (TextView) findViewById(R.id.txtalert1);
        txtalert2 = (TextView) findViewById(R.id.txtalert2);
        txlike = (TextView) findViewById(R.id.txtlike);
        txtnameplato = (TextView) findViewById(R.id.txtnameplato);
        imgshare = (ImageView) findViewById(R.id.imgshare);
        txtdistancia = (TextView) findViewById(R.id.txtdistancia);

        imgfriend = (ImageView) findViewById(R.id.imgfriend);
        imgchat = (ImageView) findViewById(R.id.imgchat);
        imglike = (ImageView) findViewById(R.id.imglike);
        imgUser = (CircleImageView) findViewById(R.id.imgUser);
        btnre = (Button) findViewById(R.id.btnre);
        btnsugerir = (Button) findViewById(R.id.btnsugerir);
        bar = (RatingBar) findViewById(R.id.ratingBar);
        cantidad = (EditText) findViewById(R.id.txtcan);
        contenedor = (LinearLayout) findViewById(R.id.contenedor);
        principal = (LinearLayout) findViewById(R.id.principal);

        mapView = (MapView) findViewById(R.id.mapView);
        try {
            mapView.onCreate(savedInstanceState);
        } catch (Exception e) {

        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                //changePosition();


            }
        });

        gps = new GPS(DescripcionActivity.this);
        if (!gps.canGetLocation()) {
            gps.showSettingsAlert();
        }

        /* data info user*/
        user = FirebaseAuth.getInstance().getCurrentUser();

        databaseFriend = FirebaseDatabase.getInstance().getReference("friends");
        databaseFriend.keepSynced(true);

        databasePlatos = FirebaseDatabase.getInstance().getReference("platos");
        databasePlatos.keepSynced(true);

        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        databaseUsers.keepSynced(true);

        databaseCocinero = FirebaseDatabase.getInstance().getReference("users");
        databaseCocinero.keepSynced(true);

        final Query queryplato = databasePlatos
                .orderByChild("id").equalTo(extras.getString("id"));
        queryplato.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    plato = postSnapshot.getValue(Plato.class);

                    if (gps.canGetLocation()) {
                        plato.setDistancia(Constants.distancia(gps.getLatitude(), gps.getLongitude(), (double) plato.getLag(), (double) plato.getLog()));
                    } else {
                        if (InfoUser != null) {
                            if (InfoUser.getLat() != null) {
                                plato.setDistancia(Constants.distancia(Double.parseDouble(InfoUser.getLat()), Double.parseDouble(InfoUser.getLog()), (double) plato.getLag(), (double) plato.getLog()));

                            } else {
                                plato.setDistancia(0);
                            }
                        } else {
                            plato.setDistancia(0);
                        }

                    }



                }

                //informacion del usuario
                Query userquery = databaseUsers
                        .orderByChild("firebaseId").equalTo(user.getUid());

                userquery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                            InfoUser = postSnapshot.getValue(User.class);
                            databaseUsers.removeEventListener(this);
                        }
                        //informacion del cocinero

                        Query usercocinero = databaseCocinero
                                .orderByChild("firebaseId").equalTo(plato.getUser());

                        usercocinero.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {


                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                    InfoCocinero = postSnapshot.getValue(User.class);
                                    databaseCocinero.removeEventListener(this);
                                }

                                //load like
                                databaseLike = FirebaseDatabase.getInstance().getReference("likes");
                                databaseLike.keepSynced(true);
                                Query likequery = databaseLike
                                        .orderByChild("platoid").equalTo(plato.getId());
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



                                        Query query = databaseFriend
                                                .orderByChild("user").equalTo(user.getUid());
                                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                //getting artist


                                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                    //getting artist
                                                    Friend friend = postSnapshot.getValue(Friend.class);
                                                    if (friend.getFriend().equals(plato.getUser())) {
                                                        //temp2[0] = friend;
                                                        databaseFriend.removeEventListener(this);
                                                        flagfriends = true;
                                                    }

                                                }

                                                //lleno informacion

                                                llenarInfo();

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                Log.e(TAG, "onCancelled", databaseError.toException());
                                            }
                                        });





                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e(TAG, "onCancelled", databaseError.toException());
                                    }
                                });




                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "onCancelled", databaseError.toException());
                            }
                        });




                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled", databaseError.toException());
                    }
                });




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




























    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();

    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }






    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();

    }

    private void changePosition() {
        /*View view = this.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);*/

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        mapView.setVisibility(View.VISIBLE);

        // Add a marker in Sydney and move the camera
        LatLng posicion = new LatLng(plato.getLag(), plato.getLog());
        map.clear();
        map.addMarker(new MarkerOptions().position(posicion).title(""));
        map.moveCamera(CameraUpdateFactory.newLatLng(posicion));

        // Move the camera instantly to Sydney with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(posicion, 14));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomIn());

        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(posicion)      // Sets the center of the map to Mountain View
                .zoom(15)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        //map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    }

    public void share() {
        Intent intent;
        intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, plato.getTitle());
        intent.setType("image/png");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        Bitmap bitmap = Constants.decodeBase64(plato.getImage());
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file" + String.valueOf(System.currentTimeMillis()) + ".png");
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
        startActivity(Intent.createChooser(intent, getString(R.string.share)));
    }



    private void llenarInfo() {
        int caducado = 0;
        if (compareDate(plato.getDia() + " " + plato.getHora().replace("h", ":"), new SimpleDateFormat("yyyy-MM-dd HH:mm").format(fecha))) {
            caducado = 1;
        }


        DecimalFormat df = new DecimalFormat("#.00");
        df.setMaximumFractionDigits(2);

        imagendescri.setImageBitmap(Constants.decodeBase64(plato.getImage()));

        Float costo = Float.parseFloat(plato.getCosto()) / Float.parseFloat(plato.getRaciones());
        txtcosto.setText(df.format(costo).toString());
        txtraciones.setText(plato.getOfrezco() + "/" + plato.getRaciones());

        if (caducado == 0) {
            txthora.setText(Constants.formatoFecha(plato.getDia()) + " " + plato.getHora());
            txthora.setTextColor(getApplicationContext().getResources().getColor(R.color.colorTheText));
        } else {
            txthora.setText(getString(R.string.agotada));
            txthora.setTextColor(getApplicationContext().getResources().getColor(R.color.error));
        }

        txtdes.setText(plato.getDesc());
        txtname.setText(plato.getUsername());
        txtnameplato.setText(plato.getTitle());
        cantidad.setFilters(new InputFilter[]{new InputFilterMinMax("1", plato.getOfrezco())});


        if (!InfoUser.getId().equals(InfoCocinero.getId())) {
            imgchat.setVisibility(View.VISIBLE);
            imgfriend.setVisibility(View.VISIBLE);
            btnre.setVisibility(View.VISIBLE);
            contenedor.setVisibility(View.VISIBLE);
            imgchat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                    intent.putExtra("userd", plato.getUser());
                    startActivity(intent);
                }
            });


            //valido si ya es amigo
            if (!flagfriends) {
                imgfriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final DatabaseReference databaseFriend;
                        databaseFriend = FirebaseDatabase.getInstance().getReference("friends");

                        final Friend[] temp2 = {new Friend()};

                        temp2[0] = null;

                        Query query = databaseFriend
                                .orderByChild("user").equalTo(user.getUid());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //getting artist


                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    //getting artist
                                    Friend friend = postSnapshot.getValue(Friend.class);
                                    if (friend.getFriend().equals(plato.getUser())) {
                                        temp2[0] = friend;
                                        databaseFriend.removeEventListener(this);
                                    }

                                }

                                if (InfoCocinero != null) {
                                    String id = databaseFriend.push().getKey();
                                    Friend fri = new Friend();
                                    fri.setId(id);
                                    fri.setFriend(plato.getUser());
                                    fri.setUser(user.getUid());
                                    fri.setNamefriend(plato.getUsername());
                                    fri.setFriendFirebaseCode(InfoCocinero.getFirebase_code());
                                    databaseFriend.child(id).setValue(fri);

                                    Snackbar snackbar = Snackbar
                                            .make(principal, getString(R.string.friend_no), Snackbar.LENGTH_LONG);

                                    snackbar.show();


                                    final DatabaseReference databaseNoti;
                                    databaseNoti = FirebaseDatabase.getInstance().getReference("notificaciones");
                                    id = databaseNoti.push().getKey();

                                    String type = "1";
                                    if (InfoCocinero.getMobile() != null) {
                                        type = InfoCocinero.getMobile();
                                    }
                                    databaseNoti.child(id).setValue(new Notificacion(user.getUid(),
                                            InfoCocinero.getFirebaseId(),
                                            getString(R.string.seguidor),
                                            "0", id, "", 0));

                                    new Constants.PushTask().execute(getString(R.string.app_name),
                                            getString(R.string.seguidor),
                                            InfoCocinero.getFirebase_code(), type, "N", "");

                                    imgfriend.setVisibility(View.GONE);


                                } else {
                                    Snackbar snackbar = Snackbar
                                            .make(principal, getString(R.string.friend_no), Snackbar.LENGTH_LONG);

                                    snackbar.show();
                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "onCancelled", databaseError.toException());
                            }
                        });


                    }
                });


            } else {
                imgfriend.setVisibility(View.GONE);
            }




        }else
        {
            imgchat.setVisibility(View.GONE);
            imgfriend.setVisibility(View.GONE);
            btnre.setVisibility(View.GONE);
            contenedor.setVisibility(View.GONE);
        }




            if (plato.getDistancia() != 0) {


                String[] dis = String.valueOf(plato.getDistancia()).split("\\.");

                if (dis[0].equals("500")) {
                    dis[0] = getString(R.string.amenos) + " " + dis[0] + "m";
                } else if (dis[0].equals("26")) {
                    dis[0] = getString(R.string.amas) + " 25km";
                } else {
                    dis[0] = getString(R.string.amenos) + " " + dis[0] + "km";
                }

                txtdistancia.setText(dis[0]);
                txtdistancia.setVisibility(View.VISIBLE);

            }

            changePosition();

            imgshare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    share();
                }
            });

            txlike.setText(String.valueOf(plato.getLike()));


            if (plato.getOpcional().equals("")) {
                txtalert1.setVisibility(View.GONE);
                txtalert2.setVisibility(View.GONE);
            } else {
                txtalert2.setVisibility(View.VISIBLE);
            }



            if (caducado == 0) {

                btnre.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        btnre.setEnabled(false);
                        String id;
                        if (InfoCocinero != null) {

                            String type = "1";
                            if (InfoCocinero.getMobile() != null) {
                                type = InfoCocinero.getMobile();
                            }

                            new Constants.PushTask().execute(InfoUser.getName() + " " + InfoUser.getLastname(),
                                    getString(R.string.reserva) + " " + plato.getTitle(),
                                    InfoCocinero.getFirebase_code(), type, "N", "");

                            DatabaseReference databasenoti;
                            databasenoti = FirebaseDatabase.getInstance().getReference("notificaciones");


                            id = databasenoti.push().getKey();
                            databasenoti.child(id).setValue(new Notificacion(user.getUid(),
                                    plato.getUser(),
                                    getString(R.string.reserva) + " " + plato.getTitle(),
                                    "1", id, plato.getId(), Integer.parseInt(cantidad.getText().toString())));

                            Snackbar snackbar = Snackbar
                                    .make(principal, getString(R.string.reserva_ok), Snackbar.LENGTH_LONG);

                            snackbar.show();
                            btnre.setEnabled(true);
                        }


                    }
                });

                btnsugerir.setVisibility(View.GONE);

            } else {
                btnre.setVisibility(View.GONE);
                contenedor.setVisibility(View.GONE);

            }

            if (!InfoCocinero.getId().equals(InfoUser.getId())) {

                btnsugerir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        btnsugerir.setEnabled(false);
                        String id;
                        if (InfoCocinero != null) {

                            String type = "1";
                            if (InfoCocinero.getMobile() != null) {
                                type = InfoCocinero.getMobile();
                            }

                            new Constants.PushTask().execute(InfoUser.getName() + " " + InfoUser.getLastname(),
                                    getString(R.string.deseo) + " " + plato.getTitle(),
                                    InfoCocinero.getFirebase_code(), type, "N", "");

                            DatabaseReference databasenoti;
                            databasenoti = FirebaseDatabase.getInstance().getReference("notificaciones");


                            id = databasenoti.push().getKey();
                            databasenoti.child(id).setValue(new Notificacion(user.getUid(),
                                    plato.getUser(),
                                    getString(R.string.deseo) + " " + plato.getTitle(),
                                    "1", id, plato.getId(), Integer.parseInt(cantidad.getText().toString())));

                            Snackbar snackbar = Snackbar
                                    .make(principal, getString(R.string.sugerir_enviada), Snackbar.LENGTH_LONG);

                            snackbar.show();
                            btnsugerir.setEnabled(true);
                        }


                    }
                });
            } else {
                btnsugerir.setVisibility(View.GONE);
            }



            if (!InfoCocinero.getUrl_imagen().equals("")) {
                Glide.with(this).load(InfoCocinero.getUrl_imagen()).into(imgUser);
            } else {
                imgUser.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_user));
            }


            boolean like = false;
            int idlike = -1;
            for (int x = 0; x < mListLike.size(); x++) {
                if (mListLike.get(x).getUserid().equals(user.getUid())) {
                    like = true;
                    idlike = x;
                }
            }
            if (like) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    imglike.setImageDrawable(getDrawable(R.drawable.ic_nolike));
                } else {
                    imglike.setImageDrawable(getResources().getDrawable(R.drawable.ic_nolike));
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    imglike.setImageDrawable(getDrawable(R.drawable.ic_like));
                } else {
                    imglike.setImageDrawable(getResources().getDrawable(R.drawable.ic_like));
                }
            }

            final boolean finalLike = like;
            final int finalIdlike = idlike;

            imglike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        productHolder.mImageLike.setImageDrawable(getDrawable(R.drawable.ic_nolike));
                    } else {
                        productHolder.mImageLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_nolike));
                    }*/
                    if (finalLike) {

                        databaseLike.child(mListLike.get(finalIdlike).getId()).removeValue();
                        databasePlatos.child(plato.getId()).child("like").setValue(plato.getLike() - 1);
                        mListLike.remove(finalIdlike);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            imglike.setImageDrawable(getDrawable(R.drawable.ic_like));
                        } else {
                            imglike.setImageDrawable(getResources().getDrawable(R.drawable.ic_like));
                        }
                        txlike.setText(String.valueOf(plato.getLike() - 1));


                    } else {
                        String id = databaseLike.push().getKey();
                        final Like templ = new Like();
                        templ.setId(id);
                        templ.setPlatoid(plato.getId());
                        templ.setUserid(user.getUid());
                        databaseLike.child(id).setValue(templ);
                        mListLike.add(templ);
                        databasePlatos.child(plato.getId()).child("like").setValue(plato.getLike() + 1);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            imglike.setImageDrawable(getDrawable(R.drawable.ic_nolike));
                        } else {
                            imglike.setImageDrawable(getResources().getDrawable(R.drawable.ic_nolike));
                        }
                        txlike.setText(String.valueOf(plato.getLike() + 1));

                        final DatabaseReference databaseNoti;
                        databaseNoti = FirebaseDatabase.getInstance().getReference("notificaciones");
                        id = databaseNoti.push().getKey();

                        String type = "1";
                        if (InfoCocinero.getMobile() != null) {
                            type = InfoCocinero.getMobile();
                        }
                        databaseNoti.child(id).setValue(new Notificacion(user.getUid(),
                                InfoCocinero.getFirebaseId(),
                                            /*Utemp.getName() + " " + Utemp.getLastname() + " " +*/ getString(R.string.like),
                                "0", id, "", 0));

                        new Constants.PushTask().execute(getString(R.string.app_name),
                                InfoUser.getName() + " " + InfoUser.getLastname() + " " + getString(R.string.like),
                                InfoCocinero.getFirebase_code(), type, "N", "");


                    }
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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


}
