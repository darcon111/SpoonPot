package app.com.spoonpot.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

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


import static app.com.spoonpot.config.Constants.compareDate;

public class MapaActivity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener {


    private GoogleMap map;
    private MapView mapView;
    private DatabaseReference databaseUsers;
    private ArrayList<User> mListUser;
    private FirebaseUser user;
    private GPS gps = null;
    private DatabaseReference databasePlatos;
    private ArrayList<Plato> mListPlato;
    private String TAG = MapaActivity.class.getName();
    final User[] temp = {new User()};
    User Utemp;

    private ConstraintLayout principal;
    private static FirebaseDatabase mDatabase;

    /* descripcion */
    private LinearLayout descri;
    private ImageView imagendescri;
    private TextView txtcosto,txtraciones,txthora,txtdes,txtname,txtalert1,txtalert2,txlike,txtnameplato,txtdistancia;
    //private GoogleMap map;
    private MapView mapView2;
    private ImageView imgshare,imgfriend,imgchat,imglike;
    private EditText cantidad;
    private CircleImageView imgUser;
    private Button btnre;
    private RatingBar bar;
    private LinearLayout contenedor;

    private ArrayList<Like> mListLike;
    private DatabaseReference databaseLike;

    private boolean flag=false;
    private Date fecha = new Date();
    private boolean like=false;
    private int idlike = -1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        if (mDatabase == null) {
            try {
                mDatabase = FirebaseDatabase.getInstance();
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            }catch (DatabaseException e)
            {

            }

        }

        /* toolbar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbaruser);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

        gps = new GPS(MapaActivity.this);
        if (!gps.canGetLocation()) {
            gps.showSettingsAlert();
        }

        user = FirebaseAuth.getInstance().getCurrentUser();
        mapView = (MapView) findViewById(R.id.mapa);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map=googleMap;
                changePosition();
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                map.setOnMarkerClickListener(MapaActivity.this);
            }
        });

        principal=(ConstraintLayout) findViewById(R.id.principal);
        //getting the reference of artists node
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



        mListUser = new ArrayList<User>();
        databasePlatos = FirebaseDatabase.getInstance().getReference("platos");

        mListPlato = new ArrayList<Plato>();

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
                    if(publi.getFavorito()==0) {
                        mListPlato.add(publi);
                    }
                }

                if (mListPlato.size() > 0) {
                    //progress.setVisibility(View.GONE);
                    Collections.reverse(mListPlato);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        descri=(LinearLayout) findViewById(R.id.descri);
        imagendescri=(ImageView) findViewById(R.id.imagendes);
        txtcosto=(TextView) findViewById(R.id.txtcosto);
        txtraciones=(TextView) findViewById(R.id.txtraciones);
        txthora=(TextView) findViewById(R.id.txthora);
        txtdes=(TextView) findViewById(R.id.txtdes);
        txtname=(TextView) findViewById(R.id.txtname);
        txtalert1=(TextView) findViewById(R.id.txtalert1);
        txtalert2=(TextView) findViewById(R.id.txtalert2);
        txlike=(TextView) findViewById(R.id.txtlike);
        txtnameplato=(TextView) findViewById(R.id.txtnameplato);
        imgshare=(ImageView) findViewById(R.id.imgshare);
        txtdistancia=(TextView) findViewById(R.id.txtdistancia);
        imgfriend=(ImageView)findViewById(R.id.imgfriend);
        imgchat=(ImageView) findViewById(R.id.imgchat);
        imglike=(ImageView) findViewById(R.id.imglike);
        imgUser=(CircleImageView) findViewById(R.id.imgUser);
        btnre=(Button) findViewById(R.id.btnre);
        bar=(RatingBar) findViewById(R.id.ratingBar);
        cantidad=(EditText) findViewById(R.id.txtcan);
        contenedor=(LinearLayout) findViewById(R.id.contenedor);
        mapView2=(MapView) findViewById(R.id.mapView);

        mapView2.setVisibility(View.GONE);


    }

    private void addPosition(String  lat,String log,String tipo,String name,int position)
    {


        // Add a marker in Sydney and move the camera
        LatLng posicion = new LatLng(Double.parseDouble(lat), Double.parseDouble(log));

        Marker marker = map.addMarker(new MarkerOptions()
                .position(posicion)
                .title(name)
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cocinero2))
        );

        int height = 80;
        int width = 70;

        BitmapDrawable bitmapdraw=null;




        if(mListUser.get(position).getType().equals("1"))
        {
            bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.ic_comensal);
        }else
        {
            if(mListUser.get(position).getFirebaseId().equals(user.getUid()))
            {
                // marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cocinero3));
                bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.ic_cocinero3);
            }else {
                //marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cocinero2));
                bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.ic_cocinero2);
            }
        }

        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));

        marker.setTag(position);



        // create marker
        /*Marker marker = new MarkerOptions().position(new LatLng(Double.parseDouble(lat),
                Double.parseDouble(lat))).title(name);
        marker.setTag(0);
        // Changing marker icon
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_comensal));*/

        // adding marker
        //map.addMarker(marker);


    }

    @Override
    public void onBackPressed() {

        if(flag)
        {
            descri.setVisibility(View.GONE);
            mapView.setVisibility(View.VISIBLE);
            flag=false;
        }else
        {

            super.onBackPressed();
            finish();
        }


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

    public void onStart()
    {
        super.onStart();

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

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Integer position = (Integer) marker.getTag();
        if(position!=null) {
            showplato(mListUser.get(position).getFirebaseId());
        }
        return false;
    }


    public void showplato(String userid)
    {
        int flag=0;
        for (int x=0;x<mListPlato.size();x++)
        {
            if(mListPlato.get(x).getUser().equals(userid)){
                showDescri(x);
                flag=1;
                break;
            }
        }
        if(flag==0) {
            Snackbar snackbar = Snackbar
                    .make(principal, getString(R.string.noplato), Snackbar.LENGTH_LONG);
            snackbar.show();
        }


    }

    /*public void Dialog(final int index)
    {
        final Dialog settingsDialog = new Dialog(this,R.style.CustomDialog);
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View v=getLayoutInflater().inflate(R.layout.descri_plato_mapa
                , null);

        ImageView imagendescri;
        imagendescri=(ImageView)  v.findViewById(R.id.imagendes);

        TextView txtcosto,txtraciones,txthora,txtname,txtnameplato;
        txtcosto=(TextView) v.findViewById(R.id.txtcosto);
        txtraciones=(TextView) v.findViewById(R.id.txtraciones);
        txthora=(TextView) v.findViewById(R.id.txthora);
        txtname=(TextView) v.findViewById(R.id.txtname);
        txtnameplato=(TextView) v.findViewById(R.id.txtnameplato);

        final RatingBar bar;
        bar=(RatingBar) v.findViewById(R.id.ratingBar);




        imagendescri.setImageBitmap(Constants.decodeBase64(mListPlato.get(index).getImage()));


        imagendescri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDescri(index);
            }
        });



        DecimalFormat df = new DecimalFormat("#.00");
        df.setMaximumFractionDigits(2);

        Float costo =Float.parseFloat(mListPlato.get(index).getCosto())/  Float.parseFloat(mListPlato.get(index).getRaciones());
        txtcosto.setText(df.format(costo).toString());
        txtraciones.setText(mListPlato.get(index).getOfrezco()+"/"+mListPlato.get(index).getRaciones());
        txthora.setText(mListPlato.get(index).getDia()+" "+mListPlato.get(index).getHora());
        txtname.setText(mListPlato.get(index).getUsername());
        txtnameplato.setText(mListPlato.get(index).getTitle());

        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        final Query userquery = databaseUsers
                .orderByChild("firebaseId").equalTo(mListPlato.get(index).getUser());

        userquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //getting artist

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    temp[0] = postSnapshot.getValue(User.class);
                    userquery.removeEventListener(this);
                }

                if(temp[0]!=null)
                {

                    if(temp[0].getValoracion()!=null)
                    {
                        bar.setRating(Float.parseFloat(temp[0].getValoracion()));
                    }else
                    {
                        bar.setRating(Float.parseFloat("0"));
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });


        settingsDialog.setContentView(v);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(settingsDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        settingsDialog.show();
        settingsDialog.getWindow().setAttributes(lp);
    }*/


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                //finish();
                //------------
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void showDescri(final int index)
    {

        int caducado = 0;
        if (compareDate(mListPlato.get(index).getDia() + " " + mListPlato.get(index).getHora().replace("h", ":"), new SimpleDateFormat("yyyy-MM-dd HH:mm").format(fecha))) {
            caducado = 1;
        }

        mapView.setVisibility(View.GONE);
        DecimalFormat df = new DecimalFormat("#.00");
        df.setMaximumFractionDigits(2);

        imagendescri.setImageBitmap(Constants.decodeBase64(mListPlato.get(index).getImage()));

        Float costo =Float.parseFloat(mListPlato.get(index).getCosto())/  Float.parseFloat(mListPlato.get(index).getRaciones());
        txtcosto.setText(df.format(costo).toString());
        txtraciones.setText(mListPlato.get(index).getOfrezco()+"/"+mListPlato.get(index).getRaciones());

        if(caducado==0) {
            txthora.setText(mListPlato.get(index).getDia() + " " + mListPlato.get(index).getHora());
        }else
        {
            txthora.setText(getString(R.string.agotada));
            txthora.setTextColor(getApplicationContext().getResources().getColor(R.color.error));
        }
        txtdes.setText(mListPlato.get(index).getDesc());
        txtname.setText(mListPlato.get(index).getUsername());
        txtnameplato.setText(mListPlato.get(index).getTitle());
        cantidad.setFilters(new InputFilter[]{ new InputFilterMinMax("1", mListPlato.get(index).getOfrezco())});


        final Query userquery = databaseUsers
                .orderByChild("firebaseId").equalTo(mListPlato.get(index).getUser());

        userquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //getting artist

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    temp[0] = postSnapshot.getValue(User.class);
                    userquery.removeEventListener(this);
                }

                if(temp[0]!=null)
                {

                    if(temp[0].getValoracion()!=null)
                    {
                        bar.setRating(Float.parseFloat(temp[0].getValoracion()));
                    }else
                    {
                        bar.setRating(Float.parseFloat("0"));
                    }


                    if(!temp[0].getUrl_imagen().equals(""))
                    {
                        Glide.with(getApplicationContext()).load(temp[0].getUrl_imagen())
                                .thumbnail(1.0f)
                                .crossFade()
                                .override(65,65)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(imgUser);
                    }else
                    {
                        imgUser.setImageResource(R.drawable.ic_user);
                    }
                    imgUser.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent= new Intent(MapaActivity.this, ProfileActivity.class);
                            intent.putExtra("user",mListPlato.get(index).getUser());
                            startActivity(intent);
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });


        if(!mListPlato.get(index).getUser().equals(user.getUid()))
        {
            imgchat.setVisibility(View.VISIBLE);
            imgfriend.setVisibility(View.VISIBLE);
            btnre.setVisibility(View.VISIBLE);
            contenedor.setVisibility(View.VISIBLE);
            imgchat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent=new Intent(getApplicationContext(),ChatActivity.class);
                    intent.putExtra("userd",mListPlato.get(index).getUser());
                    startActivity(intent);
                }
            });

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
                                if(friend.getFriend().equals(mListPlato.get(index).getUser()))
                                {
                                    temp2[0]=friend;
                                    databaseFriend.removeEventListener(this);
                                }

                            }

                            if( temp2[0]==null)
                            {
                                String id = databaseFriend.push().getKey();
                                Friend fri = new Friend();
                                fri.setId(id);
                                fri.setFriend(mListPlato.get(index).getUser());
                                fri.setUser(user.getUid());
                                fri.setNamefriend(mListPlato.get(index).getUsername());
                                fri.setFriendFirebaseCode(temp[0].getFirebase_code());
                                databaseFriend.child(id).setValue(fri);

                                Snackbar snackbar = Snackbar
                                        .make(principal, getString(R.string.friend_no), Snackbar.LENGTH_LONG);

                                snackbar.show();


                                final DatabaseReference databaseNoti;
                                databaseNoti = FirebaseDatabase.getInstance().getReference("notificaciones");
                                id = databaseNoti.push().getKey();

                                String type = "1";
                                if(temp[0].getMobile()!=null)
                                {
                                    type=temp[0].getMobile();
                                }

                                databaseNoti.child(id).setValue(new Notificacion(user.getUid(),
                                        temp[0].getFirebaseId(),
                                        getString(R.string.seguidor),
                                        "0",id,"",0));

                                new Constants.PushTask().execute(getString(R.string.app_name),
                                        getString(R.string.seguidor),
                                        temp[0].getFirebase_code(),type,"N","");


                            }else
                            {
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




            if(caducado==0) {
            btnre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    btnre.setEnabled(false);
                    String id;
                    if(temp[0]!=null)
                    {

                        String type = "1";
                        if(temp[0].getMobile()!=null)
                        {
                            type=temp[0].getMobile();
                        }

                        new Constants.PushTask().execute(Utemp.getName()+" "+Utemp.getLastname(),
                                getString(R.string.reserva)+" "+mListPlato.get(index).getTitle(),
                                temp[0].getFirebase_code(),type,"N","");

                        DatabaseReference databasenoti;
                        databasenoti = FirebaseDatabase.getInstance().getReference("notificaciones");


                        id=databasenoti.push().getKey();
                        databasenoti.child(id).setValue(new Notificacion(user.getUid(),
                                mListPlato.get(index).getUser(),
                                getString(R.string.reserva)+" "+mListPlato.get(index).getTitle(),
                                "1",id,mListPlato.get(index).getId(),Integer.parseInt(cantidad.getText().toString())));

                        Snackbar snackbar = Snackbar
                                .make(principal, getString(R.string.reserva_ok), Snackbar.LENGTH_LONG);

                        snackbar.show();
                        btnre.setEnabled(true);
                    }














                }
            });
            }else
            {
                btnre.setVisibility(View.GONE);
                contenedor.setVisibility(View.GONE);
            }


        }else
        {
            imgchat.setVisibility(View.GONE);
            imgfriend.setVisibility(View.GONE);
            btnre.setVisibility(View.GONE);
            contenedor.setVisibility(View.GONE);
        }



        imgshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share(index);
            }
        });
        txlike.setText(String.valueOf(mListPlato.get(index).getLike()));

        for(int x=0;x<mListLike.size();x++)
        {
            if(mListLike.get(x).getUserid().equals(user.getUid())
                    && mListLike.get(x).getPlatoid().equals(mListPlato.get(index).getId()))
            {
                like=true;
                idlike=x;
            }
        }
        if(like)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imglike.setImageDrawable(getDrawable(R.drawable.ic_nolike));
            } else {
                imglike.setImageDrawable(getResources().getDrawable(R.drawable.ic_nolike));
            }
        }else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imglike.setImageDrawable(getDrawable(R.drawable.ic_like));
            } else {
                imglike.setImageDrawable(getResources().getDrawable(R.drawable.ic_like));
            }
        }


        imglike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imglike.setEnabled(false);
                Like(index);
                imglike.setEnabled(true);

            }
        });



        if(mListPlato.get(index).getOpcional().equals(""))
        {
            txtalert1.setVisibility(View.GONE);
            txtalert2.setVisibility(View.GONE);
        }else
        {
            txtalert2.setVisibility(View.VISIBLE);
        }

        if (mListPlato.get(index).getDistancia() != 0) {


            String[] dis=String.valueOf(mListPlato.get(index).getDistancia()).split("\\.");

            if(dis[0].equals("500"))
            {
                dis[0]=getString(R.string.amenos)+ " "+dis[0]+"m";
            }else if(dis[0].equals("26"))
            {
                dis[0]=getString(R.string.amas)+" 25km";
            }else
            {
                dis[0]=getString(R.string.amenos)+ " "+dis[0]+"km";
            }

            txtdistancia.setText(dis[0]);
            txtdistancia.setVisibility(View.VISIBLE);

        }else
        {
            txtdistancia.setVisibility(View.INVISIBLE);
        }

        //changePosition(index);
        descri.setVisibility(View.VISIBLE);
        flag=true;


    }

    private void changePosition()
    {

        int height = 80;
        int width = 70;

        BitmapDrawable bitmapdraw=null;
        bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.ic_cocinero3);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        // Add a marker in Sydney and move the camera
        LatLng posicion = new LatLng(gps.getLatitude(), gps.getLongitude());
        map.clear();
        map.addMarker(new MarkerOptions().position(posicion).title("Me").icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        map.moveCamera(CameraUpdateFactory.newLatLng(posicion));
        // Move the camera instantly to Sydney with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(posicion, 15));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomIn());

        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(posicion)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        databaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous artist list
                mListUser.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    User temp = postSnapshot.getValue(User.class);
                    //adding artist to the list
                    if(temp.getLat()!="" && temp.getLog()!="") {
                        if (!temp.getLat().equals("0") && !temp.getLog().equals("") && !temp.getFirebaseId().equals(user.getUid())) {

                            mListUser.add(temp);
                        }
                    }

                }
                if(mListUser.size()>0) {
                    for (int i=0;i<mListUser.size();i++)
                    {

                        addPosition(mListUser.get(i).getLat(),mListUser.get(i).getLog(),mListUser.get(i).getType(),mListUser.get(i).getName()+" "+mListUser.get(i).getLastname(),i);
                    }



                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    public void share(int index)
    {
        Intent intent;
        intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, mListPlato.get(index).getTitle());
        intent.setType("image/png");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        Bitmap bitmap = Constants.decodeBase64(mListPlato.get(index).getImage());
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file"+ String.valueOf(System.currentTimeMillis())+".png");
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


    public void Like(int index)
    {
        for(int x=0;x<mListLike.size();x++)
        {
            if(mListLike.get(x).getUserid().equals(user.getUid())
                    && mListLike.get(x).getPlatoid().equals(mListPlato.get(index).getId()))
            {
                like=true;
                idlike=x;
            }
        }


        if(like)
        {

            databaseLike.child(mListLike.get(idlike).getId()).removeValue();
            databasePlatos.child(mListPlato.get(index).getId()).child("like").setValue(mListPlato.get(index).getLike() - 1);
            mListLike.remove(idlike);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imglike.setImageDrawable(getDrawable(R.drawable.ic_like));
            } else {
                imglike.setImageDrawable(getResources().getDrawable(R.drawable.ic_like));
            }
            mListPlato.get(index).setLike(mListPlato.get(index).getLike()-1);
            txlike.setText(String.valueOf(mListPlato.get(index).getLike()));
            like=false;
            idlike=-1;


        }else {
            String id = databaseLike.push().getKey();
            Like ltemp = new Like();
            ltemp.setId(id);
            ltemp.setPlatoid(mListPlato.get(index).getId());
            ltemp.setUserid(user.getUid());
            databaseLike.child(id).setValue(ltemp);
            mListLike.add(ltemp);
            databasePlatos.child(mListPlato.get(index).getId()).child("like").setValue(mListPlato.get(index).getLike() + 1);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imglike.setImageDrawable(getDrawable(R.drawable.ic_nolike));
            } else {
                imglike.setImageDrawable(getResources().getDrawable(R.drawable.ic_nolike));
            }

            mListPlato.get(index).setLike(mListPlato.get(index).getLike()+1);
            txlike.setText(String.valueOf(mListPlato.get(index).getLike()));
            final DatabaseReference databaseNoti;
            databaseNoti = FirebaseDatabase.getInstance().getReference("notificaciones");
            id = databaseNoti.push().getKey();

            String type = "1";
            if(temp[0].getMobile()!=null)
            {
                type=temp[0].getMobile();
            }

            databaseNoti.child(id).setValue(new Notificacion(user.getUid(),
                    temp[0].getFirebaseId(),
                    Utemp.getName()+" "+Utemp.getLastname()+" "+getString(R.string.like),
                    "0",id,"",0));

            new Constants.PushTask().execute(getString(R.string.app_name),
                    Utemp.getName()+" "+Utemp.getLastname()+" "+getString(R.string.like),
                    temp[0].getFirebase_code(),type,"N","");

            idlike=mListLike.size()-1;
            like=true;

        }
    }



}
