package app.com.spoonpot.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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
import java.util.Timer;
import java.util.TimerTask;

import app.com.spoonpot.R;

import app.com.spoonpot.clases.BottonNavigationView.BottomNavigationViewEx;
import app.com.spoonpot.clases.GPS;
import app.com.spoonpot.clases.ImagenCircular.CircleImageView;
import app.com.spoonpot.clases.InputFilterMinMax;
import app.com.spoonpot.clases.Screen;
import app.com.spoonpot.config.AppPreferences;
import app.com.spoonpot.config.Constants;
import app.com.spoonpot.helpers.BottomNavigationShiftHelper;
import app.com.spoonpot.holder.Friend;
import app.com.spoonpot.holder.Like;
import app.com.spoonpot.holder.Notificacion;
import app.com.spoonpot.holder.Pagos;
import app.com.spoonpot.holder.Plato;
import app.com.spoonpot.holder.User;
import app.com.spoonpot.holder.Valoraciones;
import q.rorbin.badgeview.QBadgeView;


import static app.com.spoonpot.config.Constants.compareDate;

public class ProfileActivity extends AppCompatActivity {

    private BottomNavigationViewEx bottomNavigationView;
    private TextView txtseguidores,txtseguidos, txtrecetas,txtname,txtbiografia;
    private CircleImageView img;
    private DatabaseReference databaseFriend;
    private int  seguidores=0;
    private int seguidos=0;
    private int recetas=0;
    private FirebaseUser user;
    private ArrayList<Plato> mListPlato;
    private String TAG = ProfileActivity.class.getName();
    private User Utemp;

    private PlatoRecycleAdapter mPlatoAdapter;
    private RecyclerView mUserRecyclerView;
    private RatingBar bar;
    private String perfil="";

    /* descripcion */
    private LinearLayout descri;
    private ImageView imagendescri;
    private TextView txtcosto,txtraciones,txthora,txtdes,txtnamedes,txtalert1,txtalert2,txlike,txtnameplato,txtdistancia;
    private GoogleMap map;
    private MapView mapView;
    private ImageView imgshare,imgfriend,imgchat,imglike;
    private EditText cantidad;
    private CircleImageView imgUser;
    private Button btnre;
    private RatingBar desbar;
    private LinearLayout one;
    private LinearLayout contenedor;

    private ArrayList<Like> mListLike;
    private DatabaseReference databaseLike;
    private DatabaseReference databasePlatos;
    private DatabaseReference databaseValoraciones;
    private DatabaseReference databasePagos;

    private ArrayList<Valoraciones> mListValorPendientes;
    private ArrayList<Valoraciones> mListValoraciones;
    private ArrayList<Pagos>        mListPagos;

    final User[] temp = {new User()};
    private ConstraintLayout principal;
    private LinearLayout contenedorpendientes;
    private LinearLayout contenedorValoracion;
    private LinearLayout contenedorPagos;

    private PendientesRecycleAdapter mValPendientesAdapter;
    private RecyclerView recyclerValPendientes;

    private ValoracionRecycleAdapter mValorAdapter;
    private RecyclerView recyclerValoraciones;

    private PagosRecycleAdapter mPagosAdapter;
    private RecyclerView recyclerPagos;

    DatabaseReference databaseUsers;


    private BottomNavigationMenuView menuView;
    private View iconView;

    private int fdescri=0;
    SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    Date myDate = new Date();
    String fecha_actual = timeStampFormat.format(myDate);
    private AppPreferences app;
    private Date fecha = new Date();
    private GPS gps = null;
    QBadgeView badgeView;
    private boolean like=false;
    private int idlike = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


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

        app = new AppPreferences(getApplicationContext());

       /* data info user*/
        user = FirebaseAuth.getInstance().getCurrentUser();

        if(getIntent().getExtras()!=null) {
            if (getIntent().getExtras().getString("user", "0").equals("0")) {
                perfil = user.getUid();
            } else {
                perfil = getIntent().getExtras().getString("user", "0");
            }
        }else
        {
            perfil = user.getUid();
        }
        /*descripcion*/
        descri=(LinearLayout) findViewById(R.id.descri);
        imagendescri=(ImageView) findViewById(R.id.imagendes);
        txtcosto=(TextView) findViewById(R.id.txtcosto);
        txtraciones=(TextView) findViewById(R.id.txtraciones);
        txthora=(TextView) findViewById(R.id.txthora);
        txtdes=(TextView) findViewById(R.id.txtdes);
        txtnamedes=(TextView) findViewById(R.id.txtname);
        txtalert1=(TextView) findViewById(R.id.txtalert1);
        txtalert2=(TextView) findViewById(R.id.txtalert2);
        txlike=(TextView) findViewById(R.id.txtlike);
        txtnameplato=(TextView) findViewById(R.id.txtnameplato);
        txtdistancia = (TextView) findViewById(R.id.txtdistancia);
        imgshare=(ImageView) findViewById(R.id.imgshare);
        imgfriend=(ImageView)findViewById(R.id.imgfriend);
        imgchat=(ImageView) findViewById(R.id.imgchat);
        imglike=(ImageView) findViewById(R.id.imglike);
        imgUser=(CircleImageView) findViewById(R.id.imgUser);
        btnre=(Button) findViewById(R.id.btnre);
        desbar=(RatingBar) findViewById(R.id.ratingBar);
        cantidad=(EditText) findViewById(R.id.txtcan);
        principal=(ConstraintLayout) findViewById(R.id.principal);
        one=(LinearLayout) findViewById(R.id.one);
        contenedor=(LinearLayout) findViewById(R.id.contenedor);
        contenedorpendientes=(LinearLayout) findViewById(R.id.contenedorpendientes);
        contenedorValoracion=(LinearLayout) findViewById(R.id.contenedorValoracion);
        contenedorPagos=(LinearLayout) findViewById(R.id.contenedorPagos);

        mapView = (MapView) findViewById(R.id.mapView);
        try {
            mapView.onCreate(savedInstanceState);
        }catch (Exception e)
        {

        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map=googleMap;
                //changePosition();


            }
        });

        txtrecetas=(TextView) findViewById(R.id.txtrecetas);
        txtseguidos=(TextView) findViewById(R.id.txtseguidos);
        txtseguidores=(TextView) findViewById(R.id.txtseguidores);
        txtname=(TextView) findViewById(R.id.txtnamep);
        txtbiografia=(TextView) findViewById(R.id.txtbiografia);
        img=(CircleImageView) findViewById(R.id.img);
        bar=(RatingBar) findViewById(R.id.ratingBarp);
         /* list plato*/
        mListPlato = new ArrayList<Plato>();

        recyclerValoraciones=(RecyclerView) findViewById(R.id.rValoraciones);
        recyclerValPendientes=(RecyclerView) findViewById(R.id.rvalorapen);
        mValPendientesAdapter=new PendientesRecycleAdapter();
        mValorAdapter=new ValoracionRecycleAdapter();
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(this);
        recyclerValoraciones.setLayoutManager(layoutManager2);
        recyclerValPendientes.setLayoutManager(layoutManager3);

        recyclerValPendientes.setAdapter(mValPendientesAdapter);
        recyclerValoraciones.setAdapter(mValorAdapter);

        mUserRecyclerView = (RecyclerView) findViewById(R.id.tus_platos);
        // Create a grid layout with two columns
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        mUserRecyclerView.setLayoutManager(layoutManager);

        mPlatoAdapter=new PlatoRecycleAdapter();
        mUserRecyclerView.setAdapter(mPlatoAdapter);

        recyclerPagos=(RecyclerView) findViewById(R.id.rpagos);
        mPagosAdapter= new PagosRecycleAdapter();
        LinearLayoutManager layoutManager4 = new LinearLayoutManager(this);
        recyclerPagos.setLayoutManager(layoutManager4);
        recyclerPagos.setAdapter(mPagosAdapter);

           /* button navigation*/
        bottomNavigationView = (BottomNavigationViewEx) findViewById(R.id.bottom_navigation);
        bottomNavigationView.enableAnimation(false);
        bottomNavigationView.enableItemShiftingMode(false);
        bottomNavigationView.enableShiftingMode(false);
        BottomNavigationShiftHelper.disableShiftMode(bottomNavigationView, getApplicationContext());


        bottomNavigationView.getMenu().getItem(4).setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Intent intent = null;
                        switch (item.getItemId()) {
                            case R.id.nav_home:
                                finish();
                                break;
                            case R.id.nav_fav:
                                intent = new Intent(ProfileActivity.this, FavoritoActivity.class);
                                startActivity(intent);
                                finish();
                                break;
                            case R.id.nav_add:
                                intent = new Intent(ProfileActivity.this, AddPlatoActivity.class);
                                intent.putExtra("id",0);
                                intent.putExtra("username",Utemp.getName()+" "+Utemp.getLastname());
                                startActivity(intent);
                                finish();
                                break;
                            case R.id.nav_noti:
                                Log.e("value", "config");
                                intent = new Intent(ProfileActivity.this, NotificationActivity.class);
                                finish();
                                startActivity(intent);
                                break;
                            case R.id.nav_per:

                                Log.e("value", "config");
                                //intent = new Intent(MainActivity.this, PublicationActivity.class);

                                break;

                        }


                        return true;
                    }
                });

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

        databasePlatos = FirebaseDatabase.getInstance().getReference("platos");
        databaseValoraciones=FirebaseDatabase.getInstance().getReference("valoraciones");
        databaseValoraciones.keepSynced(true);

        gps = new GPS(ProfileActivity.this);
        if (!gps.canGetLocation()) {
            gps.showSettingsAlert();
        }
        badgeView = new QBadgeView(this);
        NotiTask();

        if(app.getPerfil().equals("0"))
        {
            Constants.explicativo(ProfileActivity.this,getString(R.string.experfil));
            app.setPerfil(1);
        }

    }



    @Override
    public void onStart() {
        super.onStart();

        databaseFriend = FirebaseDatabase.getInstance().getReference("friends");
        databaseFriend.keepSynced(true);

        databaseFriend.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous artist list


                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Friend temp = postSnapshot.getValue(Friend.class);
                    //adding artist to the list
                    if(temp.getFriend().equals(perfil))
                    {
                        seguidores++;
                    }else if(temp.getUser().equals(perfil))
                    {
                        seguidos++;
                    }
                }
                databaseFriend.removeEventListener(this);

                if(seguidores!=0)
                {
                    txtseguidores.setText(String.valueOf(seguidores)+"\n"+getString(R.string.seguidores2));
                }
                if(seguidos!=0)
                {
                    txtseguidos.setText(String.valueOf(seguidos)+"\n"+getString(R.string.seguidos2));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Query query = databasePlatos
                .orderByChild("user").equalTo(perfil);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //getting artist
                recetas=0;
                mListPlato.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Plato temp = postSnapshot.getValue(Plato.class);
                    if (gps.canGetLocation()) {
                        temp.setDistancia(Constants.distancia(gps.getLatitude(), gps.getLongitude(), (double) temp.getLag(), (double) temp.getLog()));
                    } else {
                        if (Utemp != null) {
                            if (Utemp.getLat() != null) {
                                temp.setDistancia(Constants.distancia(Double.parseDouble(Utemp.getLat()), Double.parseDouble(Utemp.getLog()), (double) temp.getLag(), (double) temp.getLog()));

                            } else {
                                temp.setDistancia(0);
                            }
                        } else {
                            temp.setDistancia(0);
                        }

                    }

                    mListPlato.add(temp);
                    recetas++;


                }
                databasePlatos.removeEventListener(this);
             if(recetas!=0)
             {
                 txtrecetas.setText(String.valueOf(recetas)+"\n"+getString(R.string.recetas2));
                 mPlatoAdapter.notifyDataSetChanged();
             }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });

        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        Query userquery = databaseUsers
                .orderByChild("firebaseId").equalTo(perfil);
        userquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //getting artist

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Utemp = postSnapshot.getValue(User.class);
                    databaseUsers.removeEventListener(this);
                }

                if(Utemp!=null)
                {
                    txtname.setText(Utemp.getName()+" "+Utemp.getLastname());
                    txtbiografia.setText(Utemp.getBiografia());
                    if(!Utemp.getUrl_imagen().equals(""))
                    {
                        Glide.with(getApplicationContext()).load(Utemp.getUrl_imagen())
                                .thumbnail(1.0f)
                                .override(65,65)
                                .crossFade()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(img);
                    }else
                    {
                        img.setImageResource(R.drawable.ic_user);
                    }
                    if(Utemp.getValoracion()!=null)
                    {
                        bar.setRating(Float.parseFloat(Utemp.getValoracion()));
                    }else
                    {
                        bar.setRating(0);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });


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



        mListValorPendientes = new ArrayList<Valoraciones>();
        mListValoraciones=new ArrayList<Valoraciones>();

        databaseValoraciones.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous artist list
                mListValorPendientes.clear();
                mListValoraciones.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Valoraciones val = postSnapshot.getValue(Valoraciones.class);
                    //adding artist to the list
                    if(val.getUserOrigen().equals(user.getUid()) && perfil.equals(user.getUid()) && val.getValoracionCocinero()==0 && Constants.compareDate(val.getFecha().replace("h",":"),fecha_actual))
                    {
                        mListValorPendientes.add(val);
                    }
                    else if(val.getUserDestino().equals(user.getUid()) && perfil.equals(user.getUid()) && val.getValoracionCocinero()!=0)
                    {
                        mListValoraciones.add(val);
                    }

                }
                databaseValoraciones.removeEventListener(this);
                if ( mListValorPendientes.size() > 0) {
                    //progress.setVisibility(View.GONE);
                    recyclerValPendientes.setVisibility(View.VISIBLE);

                    mValPendientesAdapter.notifyDataSetChanged();
                } else {
                    contenedorpendientes.setVisibility(View.GONE);
                }

                if(mListValoraciones.size()>0)
                {
                    recyclerValoraciones.setVisibility(View.VISIBLE);
                    mValorAdapter.notifyDataSetChanged();
                }else
                {
                    contenedorValoracion.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mListPagos = new ArrayList<Pagos>();


        if(user.getUid().equals(perfil)) {

            databasePagos = FirebaseDatabase.getInstance().getReference("pagos");
            databasePagos.keepSynced(true);
            Query querypagos = databasePagos
                    .orderByChild("userDestino").equalTo(user.getUid().toString());

            querypagos.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //clearing the previous artist list
                    mListLike.clear();
                    //iterating through all the nodes
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        //getting artist
                        Pagos pag = postSnapshot.getValue(Pagos.class);
                        //adding artist to the list
                        mListPagos.add(pag);
                    }
                    databasePagos.removeEventListener(this);

                    if (mListPagos.size() > 0) {
                        mPagosAdapter.notifyDataSetChanged();
                    } else {
                        contenedorPagos.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled", databaseError.toException());
                }
            });
        }else
        {
            contenedorPagos.setVisibility(View.GONE);
        }




    }


              /* adapter*/

    public class PlatoRecycleAdapter extends RecyclerView.Adapter<PlatoRecycleHolder> {
        private int lastPosition = -1;
        @Override
        public PlatoRecycleHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            View v;
            if(user.getUid().equals(perfil)) {
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_tus_recetas, viewGroup, false);
            }else{
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_plato_profile, viewGroup, false);
            }

            return new PlatoRecycleHolder(v);
        }



        @Override
        public void onBindViewHolder(final PlatoRecycleHolder productHolder, final int i) {

            productHolder.mTitle.setText(mListPlato.get(i).getTitle());
            Bitmap bitmap = Constants.decodeBase64(mListPlato.get(i).getImage());
            productHolder.mImage.setImageBitmap(bitmap);
            productHolder.mImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    showDescri(i);
                }
            });

            if(!user.getUid().equals(perfil)) {


                Float costo = Float.parseFloat(mListPlato.get(i).getCosto()) / Float.parseFloat(mListPlato.get(i).getRaciones());
                productHolder.mCosto.setText(String.valueOf(costo));
                if (!compareDate(mListPlato.get(i).getDia() + " " + mListPlato.get(i).getHora().replace("h", ":"), new SimpleDateFormat("yyyy-MM-dd HH:mm").format(fecha))) {
                    productHolder.mHora.setText(Constants.formatoFecha(mListPlato.get(i).getDia()));
                    productHolder.reloj.setImageResource(R.drawable.ic_reloj);
                } else {
                    productHolder.reloj.setImageResource(R.drawable.ic_reloj_rojo);
                    productHolder.mHora.setText("");
                }
                productHolder.mRacioles.setText(mListPlato.get(i).getOfrezco() + "/" + mListPlato.get(i).getRaciones());

            }else
            {
                productHolder.mPen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent =new Intent(getApplicationContext(),AddPlatoActivity.class);
                        intent.putExtra("id",mListPlato.get(i).getId().toString());
                        intent.putExtra("username",mListPlato.get(i).getUsername());
                        startActivity(intent);
                    }
                });
            }





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

        private void setAnimation(View viewToAnimate, int position)
        {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition)
            {
                Animation animation;
                if(position % 2==0) {
                    animation= AnimationUtils.loadAnimation(ProfileActivity.this, R.anim.left_in);
                }else
                {
                    animation= AnimationUtils.loadAnimation(ProfileActivity.this, R.anim.right_in);
                }

                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }


    }

    public class PlatoRecycleHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public ImageView mImage;
        public ImageView mPen;

        public TextView mCosto;
        public TextView mRacioles;
        public TextView mHora;
        public TextView mLike;
        public ImageView reloj;



        public PlatoRecycleHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.txttitle);
            mImage = (ImageView) itemView.findViewById(R.id.imagen);

            if(!user.getUid().equals(perfil)) {
                mCosto = (TextView) itemView.findViewById(R.id.txtcosto);
                mRacioles = (TextView) itemView.findViewById(R.id.txtraciones);
                mHora = (TextView) itemView.findViewById(R.id.txthora);
                reloj = (ImageView) itemView.findViewById(R.id.imgreloj);
            }else
            {
                mPen = (ImageView) itemView.findViewById(R.id.pen);
            }
        }
    }


                /* Pendientes*/

    public class PendientesRecycleAdapter extends RecyclerView.Adapter<PendientesRecycleHolder> {
        private int lastPosition = -1;
        @Override
        public PendientesRecycleHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_pendientes, viewGroup, false);
            return new PendientesRecycleHolder(v);
        }



        @Override
        public void onBindViewHolder(final PendientesRecycleHolder productHolder, final int i) {


            databaseUsers = FirebaseDatabase.getInstance().getReference("users");
            Query userquery = databaseUsers
                    .orderByChild("firebaseId").equalTo(mListValorPendientes.get(i).getUserDestino());

            final User[] temp = {new User()};
            userquery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //getting artist

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        //getting artist
                        temp[0] = postSnapshot.getValue(User.class);
                        databaseUsers.removeEventListener(this);
                    }

                    if(temp[0]!=null)
                    {

                        productHolder.mTitle.setText(temp[0].getName()+" "+temp[0].getLastname());
                        if(!temp[0].getUrl_imagen().equals(""))
                        {
                            Glide.with(getApplication()).load(temp[0].getUrl_imagen())
                                    .thumbnail(1.0f)
                                    .crossFade()
                                    .override(62,62)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(productHolder.mUser);
                        }else
                        {
                            productHolder.mUser.setImageResource(R.drawable.ic_user);
                        }

                        productHolder.mUser.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent= new Intent(getApplicationContext(), ProfileActivity.class);
                                intent.putExtra("user",temp[0].getFirebaseId());
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

            productHolder.mArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in= new Intent(ProfileActivity.this,ValoracionActivity.class);
                    in.putExtra("edit","1");
                    in.putExtra("id",mListValorPendientes.get(i).getId());
                    in.putExtra("destino",mListValorPendientes.get(i).getUserDestino());
                    in.putExtra("tipo",mListValorPendientes.get(i).getTipo());
                    startActivity(in);
                }
            });




            // Here you apply the animation when the view is bound
            //setAnimation(productHolder.itemView, i);

        }


        @Override
        public int getItemCount() {
            return mListValorPendientes.size();
        }

        public void removeItem(int position) {
            mListValorPendientes.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mListValorPendientes.size());
            //Signal.get().reset();


        }

        private void setAnimation(View viewToAnimate, int position)
        {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition)
            {
                Animation animation;
                if(position % 2==0) {
                    animation= AnimationUtils.loadAnimation(ProfileActivity.this, R.anim.left_in);
                }else
                {
                    animation= AnimationUtils.loadAnimation(ProfileActivity.this, R.anim.right_in);
                }

                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }


    }

    public class PendientesRecycleHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public ImageView mArrow;
        public ImageView mUser;




        public PendientesRecycleHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.txtname);
            mArrow = (ImageView) itemView.findViewById(R.id.arrow);
            mUser=(ImageView) itemView.findViewById(R.id.img);

        }
    }


                /* Valoraciones*/

    public class ValoracionRecycleAdapter extends RecyclerView.Adapter<ValoracionRecycleHolder> {
        private int lastPosition = -1;
        @Override
        public ValoracionRecycleHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_valoracion, viewGroup, false);
            return new ValoracionRecycleHolder(v);
        }



        @Override
        public void onBindViewHolder(final ValoracionRecycleHolder productHolder, final int i) {



            databaseUsers = FirebaseDatabase.getInstance().getReference("users");
            Query userquery = databaseUsers
                    .orderByChild("firebaseId").equalTo(mListValoraciones.get(i).getUserOrigen());

            final User[] temp = {new User()};
            userquery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //getting artist

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        //getting artist
                        temp[0] = postSnapshot.getValue(User.class);
                        databaseUsers.removeEventListener(this);
                    }

                    if(temp[0]!=null)
                    {

                        productHolder.mTitle.setText(temp[0].getName()+" "+temp[0].getLastname());
                        if(!temp[0].getUrl_imagen().equals(""))
                        {
                            Glide.with(getApplicationContext()).load(temp[0].getUrl_imagen())
                                    .thumbnail(1.0f)
                                    .crossFade()
                                    .override(62,62)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(productHolder.mUser);
                        }else
                        {
                            productHolder.mUser.setImageResource(R.drawable.ic_user);
                        }

                        productHolder.mUser.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent= new Intent(getApplicationContext(), ProfileActivity.class);
                                intent.putExtra("user",temp[0].getFirebaseId());
                                startActivity(intent);
                            }
                        });

                        productHolder.mBar.setRating(mListValoraciones.get(i).getValoracionCocinero());


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled", databaseError.toException());
                }
            });
            productHolder.mBar.setRating(mListValoraciones.get(i).getValoracionCocinero());


            productHolder.mArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in= new Intent(ProfileActivity.this,ValoracionActivity.class);
                    in.putExtra("edit","0");
                    in.putExtra("tipo",mListValoraciones.get(i).getTipo());
                    in.putExtra("txtcome",mListValoraciones.get(i).getComentarioCocinero());
                    in.putExtra("comentarioPlato",mListValoraciones.get(i).getComentarioPlato());
                    in.putExtra("valoracionPlato",mListValoraciones.get(i).getValoracionPlato());
                    in.putExtra("valoracionCocinero",mListValoraciones.get(i).getValoracionCocinero());
                    startActivity(in);
                }
            });




            // Here you apply the animation when the view is bound
            setAnimation(productHolder.itemView, i);

        }


        @Override
        public int getItemCount() {
            return mListValoraciones.size();
        }

        public void removeItem(int position) {
            mListValoraciones.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mListValoraciones.size());
            //Signal.get().reset();


        }

        private void setAnimation(View viewToAnimate, int position)
        {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition)
            {
                Animation animation;
                if(position % 2==0) {
                    animation= AnimationUtils.loadAnimation(ProfileActivity.this, R.anim.left_in);
                }else
                {
                    animation= AnimationUtils.loadAnimation(ProfileActivity.this, R.anim.right_in);
                }

                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }


    }

    public class ValoracionRecycleHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public ImageView mArrow;
        public RatingBar mBar;
        public ImageView mUser;




        public ValoracionRecycleHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            mArrow = (ImageView) itemView.findViewById(R.id.arrow);
            mBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
            mUser=(ImageView) itemView.findViewById(R.id.img);

        }
    }


                  /* Pagos*/

    public class PagosRecycleAdapter extends RecyclerView.Adapter<PagosRecycleHolder> {
        private int lastPosition = -1;
        @Override
        public PagosRecycleHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_pagos, viewGroup, false);
            return new PagosRecycleHolder(v);
        }



        @Override
        public void onBindViewHolder(final PagosRecycleHolder productHolder, final int i) {



            databaseUsers = FirebaseDatabase.getInstance().getReference("users");
            Query userquery = databaseUsers
                    .orderByChild("firebaseId").equalTo(mListPagos.get(i).getUserOrigen());

            final User[] temp = {new User()};
            userquery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //getting artist

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        //getting artist
                        temp[0] = postSnapshot.getValue(User.class);
                        databaseUsers.removeEventListener(this);
                    }

                    if(temp[0]!=null)
                    {

                        productHolder.mTitle.setText(temp[0].getName()+" "+temp[0].getLastname());
                        if(!temp[0].getUrl_imagen().equals(""))
                        {
                            Glide.with(getApplicationContext()).load(temp[0].getUrl_imagen())
                                    .thumbnail(1.0f)
                                    .crossFade()
                                    .override(62,62)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(productHolder.mUser);
                        }else
                        {
                            productHolder.mUser.setImageResource(R.drawable.ic_user);
                        }

                        productHolder.mUser.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent= new Intent(getApplicationContext(), ProfileActivity.class);
                                intent.putExtra("user",temp[0].getFirebaseId());
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

            productHolder.mMensaje.setText(mListPagos.get(i).getMensaje());



            // Here you apply the animation when the view is bound
            //setAnimation(productHolder.itemView, i);

        }


        @Override
        public int getItemCount() {
            return mListPagos.size();
        }

        public void removeItem(int position) {
            mListPagos.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mListPagos.size());
            //Signal.get().reset();


        }

        private void setAnimation(View viewToAnimate, int position)
        {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition)
            {
                Animation animation;
                if(position % 2==0) {
                    animation= AnimationUtils.loadAnimation(ProfileActivity.this, R.anim.left_in);
                }else
                {
                    animation= AnimationUtils.loadAnimation(ProfileActivity.this, R.anim.right_in);
                }

                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }


    }

    public class PagosRecycleHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public TextView mMensaje;
        public ImageView mUser;




        public PagosRecycleHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            mMensaje = (TextView) itemView.findViewById(R.id.txtmsg);
            mUser=(ImageView) itemView.findViewById(R.id.img);

        }
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(fdescri==1)
                {
                    descri.setVisibility(View.GONE);
                    one.setVisibility(View.VISIBLE);
                    fdescri=0;
                }else {
                    onBackPressed();
                    finish();
                }
                //------------
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if(fdescri==1)
        {
            descri.setVisibility(View.GONE);
            one.setVisibility(View.VISIBLE);
            fdescri=0;
        }else
        {
            super.onBackPressed();
            finish();
        }


    }

    public void showDescri(final int index)
    {
        int caducado = 0;
        if (compareDate(mListPlato.get(index).getDia() + " " + mListPlato.get(index).getHora().replace("h", ":"), new SimpleDateFormat("yyyy-MM-dd HH:mm").format(fecha))) {
            caducado = 1;
        }

        one.setVisibility(View.GONE);
        DecimalFormat df = new DecimalFormat("#.00");
        df.setMaximumFractionDigits(2);

        imagendescri.setImageBitmap(Constants.decodeBase64(mListPlato.get(index).getImage()));

        Float costo =Float.parseFloat(mListPlato.get(index).getCosto())/  Float.parseFloat(mListPlato.get(index).getRaciones());
        txtcosto.setText(df.format(costo).toString());
        txtraciones.setText(mListPlato.get(index).getOfrezco()+"/"+mListPlato.get(index).getRaciones());
        if(caducado==0) {
            txthora.setText(Constants.formatoFecha(mListPlato.get(index).getDia()) + " " + mListPlato.get(index).getHora());
            txthora.setTextColor(getApplicationContext().getResources().getColor(R.color.colorTheText));
        }else
        {
            txthora.setText(getString(R.string.agotada));
            txthora.setTextColor(getApplicationContext().getResources().getColor(R.color.error));
        }
        txtdes.setText(mListPlato.get(index).getDesc());
        txtnamedes.setText(mListPlato.get(index).getUsername());
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
                        desbar.setRating(Float.parseFloat(temp[0].getValoracion()));
                    }else
                    {
                        desbar.setRating(Float.parseFloat("0"));
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
                            Intent intent= new Intent(ProfileActivity.this, ProfileActivity.class);
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


                                Snackbar snackbar = Snackbar
                                        .make(principal, getString(R.string.friend_no), Snackbar.LENGTH_LONG);

                                snackbar.show();

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

                        /*databasenoti.setValue(new Notificacion(user.getUid(),
                                mListPlato.get(index).getUser(),
                                getString(R.string.reserva)+" "+mListPlato.get(index).getTitle(),
                                "1",databasenoti.push().getKey()
                        ));*/

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
                /*if(finalLike)
                {

                    databaseLike.child(mListLike.get(finalIdlike).getId()).removeValue();
                    databasePlatos.child(mListPlato.get(index).getId()).child("like").setValue(mListPlato.get(index).getLike() - 1);
                    mListLike.remove(finalIdlike);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        imglike.setImageDrawable(getDrawable(R.drawable.ic_like));
                    } else {
                        imglike.setImageDrawable(getResources().getDrawable(R.drawable.ic_like));
                    }

                    txlike.setText(String.valueOf(mListPlato.get(index).getLike() - 1));


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

                    txlike.setText(String.valueOf(mListPlato.get(index).getLike() + 1));
                    final DatabaseReference databaseNoti;
                    databaseNoti = FirebaseDatabase.getInstance().getReference("notificaciones");
                    id = databaseNoti.push().getKey();

                    databaseNoti.child(id).setValue(new Notificacion(user.getUid(),
                            temp[0].getFirebase_code(),
                            temp[0].getName()+" "+temp[0].getLastname()+" "+getString(R.string.like),
                            "0",id,"",0));

                    new Constants.PushTask().execute(getString(R.string.app_name),
                            temp[0].getName()+" "+temp[0].getLastname()+" "+getString(R.string.like),
                            temp[0].getFirebase_code(),"1","N","");



                }*/
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

        }

        changePosition(index);
        descri.setVisibility(View.VISIBLE);


        fdescri=1;

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
                    /*Utemp.getName()+" "+Utemp.getLastname()+" "+*/getString(R.string.like),
                    "0",id,"",0));

            new Constants.PushTask().execute(getString(R.string.app_name),
                    Utemp.getName()+" "+Utemp.getLastname()+" "+getString(R.string.like),
                    temp[0].getFirebase_code(),type,"N","");

             idlike=mListLike.size()-1;
             like=true;

        }
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

    private void changePosition(int index)
    {
        /*View view = this.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);*/

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        mapView.setVisibility(View.VISIBLE);

        // Add a marker in Sydney and move the camera
        LatLng posicion = new LatLng(mListPlato.get(index).getLag(), mListPlato.get(index).getLog());
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
