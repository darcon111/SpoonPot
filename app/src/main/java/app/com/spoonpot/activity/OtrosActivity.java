package app.com.spoonpot.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
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
import java.util.Comparator;
import java.util.Date;

import app.com.spoonpot.R;
import app.com.spoonpot.clases.AppController;
import app.com.spoonpot.clases.GPS;
import app.com.spoonpot.clases.ImagenCircular.CircleImageView;
import app.com.spoonpot.clases.InputFilterMinMax;
import app.com.spoonpot.config.AppPreferences;
import app.com.spoonpot.config.Constants;
import app.com.spoonpot.holder.Friend;
import app.com.spoonpot.holder.Like;
import app.com.spoonpot.holder.Notificacion;
import app.com.spoonpot.holder.Plato;
import app.com.spoonpot.holder.User;


import static app.com.spoonpot.config.Constants.compareDate;

public class OtrosActivity extends AppCompatActivity {

    private ArrayList<Like> mListLike;
    private DatabaseReference databaseLike;
    private DatabaseReference databaseUsers;
    private ArrayList<Plato> mListPlato;
    private User Utemp;
    private AppPreferences app;

    private RecyclerView  mUserRecyclerView;
    private PlatoRecycleAdapter mPlatoAdapter;
    private DatabaseReference databasePlatos;
    private static FirebaseDatabase mDatabase;
    private FirebaseUser user;
    private String TAG = OtrosActivity.class.getName();


    /* descripcion */
    private LinearLayout descri;
    private ImageView imagendescri;
    private TextView txtcosto,txtraciones,txthora,txtdes,txtname,txtalert1,txtalert2,txlike,txtnameplato,txtdistancia;
    private GoogleMap map;
    private MapView mapView;
    private ImageView imgshare,imgfriend,imgchat,imglike;
    private EditText cantidad;
    private CircleImageView imgUser;
    private Button btnre;
    private RatingBar bar;
    private LinearLayout contenedor;

    private ConstraintLayout principal;
    private Date fecha = new Date();
    final User[] temp = {new User()};


    private boolean flag=true;
    private TextView name;
    private GPS gps = null;
    private boolean like=false;
    private int idlike = -1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otros);

        app = new AppPreferences(getApplicationContext());
        /* set language*/
        Constants.setLanguage(app.getLanguage(),getApplicationContext());

               /* toolbar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbaruser);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");




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

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });


         /* list plato*/
        mListPlato = new ArrayList<Plato>();
        mUserRecyclerView = (RecyclerView) findViewById(R.id.otrosrecicler);
        // Create a grid layout with two columns
        //GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, 1);
        // Create a custom SpanSizeLookup where the first item spans both columns

        /*layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? 2 : 1;
            }
        });*/
        mUserRecyclerView.setLayoutManager(layoutManager);

        mPlatoAdapter=new PlatoRecycleAdapter();
        mUserRecyclerView.setAdapter(mPlatoAdapter);

        if (mDatabase == null) {
            try {
                mDatabase = FirebaseDatabase.getInstance();
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            }catch (DatabaseException e)
            {

            }

        }

        databasePlatos = FirebaseDatabase.getInstance().getReference("platos");

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
        txtdistancia = (TextView) findViewById(R.id.txtdistancia);
        imgshare=(ImageView) findViewById(R.id.imgshare);
        imgfriend=(ImageView)findViewById(R.id.imgfriend);
        imgchat=(ImageView) findViewById(R.id.imgchat);
        imglike=(ImageView) findViewById(R.id.imglike);
        imgUser=(CircleImageView) findViewById(R.id.imgUser);
        btnre=(Button) findViewById(R.id.btnre);
        bar=(RatingBar) findViewById(R.id.ratingBar);
        cantidad=(EditText) findViewById(R.id.txtcan);
        contenedor=(LinearLayout) findViewById(R.id.contenedor);

        name=(TextView) findViewById(R.id.textName);

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

        if(getIntent().getExtras().getString("busqueda").equals("0")) {

            name.setText(R.string.platomas);
        }else if(getIntent().getExtras().getString("busqueda").equals("1"))
        {
            name.setText(R.string.platoS);
        }else
        {

            if(getIntent().getExtras().getString("busqueda").equals("Arroz"))
            {
                name.setText(R.string.arroces);
            }else if(getIntent().getExtras().getString("busqueda").equals("Carne"))
            {
                name.setText(R.string.carnes);
            }else if(getIntent().getExtras().getString("busqueda").equals("Ensalada"))
            {
                name.setText(R.string.ensaladas);
            }
            else if(getIntent().getExtras().getString("busqueda").equals("Pasta"))
            {
                name.setText(R.string.pastas);
            }
            else if(getIntent().getExtras().getString("busqueda").equals("Pescado"))
            {
                name.setText(R.string.pescados);
            }
            else if(getIntent().getExtras().getString("busqueda").equals("Pizza"))
            {
                name.setText(R.string.pizzas);
            }
            else if(getIntent().getExtras().getString("busqueda").equals("Sopa"))
            {
                name.setText(R.string.sopas);
            }else
            {
                name.setText(R.string.postres);
            }



        }

        gps = new GPS(OtrosActivity.this);
        if (!gps.canGetLocation()) {
            gps.showSettingsAlert();
        }


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
            final AppController globalVariable = (AppController) getApplicationContext();
            productHolder.mTitle.setText(mListPlato.get(i).getTitle());
            productHolder.mUser.setText(mListPlato.get(i).getUsername().toString());

            Float costo = Float.parseFloat(mListPlato.get(i).getCosto()) / Float.parseFloat(mListPlato.get(i).getRaciones());
            productHolder.mCosto.setText(String.valueOf(costo));
            if (!compareDate(mListPlato.get(i).getDia()+" "+mListPlato.get(i).getHora().replace("h",":"), new SimpleDateFormat("yyyy-MM-dd HH:mm").format(fecha))) {
                productHolder.mHora.setText(Constants.formatoFecha(mListPlato.get(i).getDia()));
                productHolder.reloj.setImageResource(R.drawable.ic_reloj);
            } else {
                productHolder.reloj.setImageResource(R.drawable.ic_reloj_rojo);
                productHolder.mHora.setText("");
            }
            productHolder.mRacioles.setText(mListPlato.get(i).getOfrezco() + "/" + mListPlato.get(i).getRaciones());
            //productHolder.mRacioles.setText(mListPlato.get(i).getOfrezco());

            //productHolder.mLike.setText(mListPlato.get(i).getLike());
            productHolder.mLike.setText(String.valueOf(mListPlato.get(i).getLike()));
            boolean like=false;
            int idlike = -1;
            for(int x=0;x<mListLike.size();x++)
            {
                if(mListLike.get(x).getUserid().equals(user.getUid())
                        && mListLike.get(x).getPlatoid().equals(mListPlato.get(i).getId()))
                {
                    like=true;
                    idlike=x;
                }
            }
            if(like)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    productHolder.mImageLike.setImageDrawable(getDrawable(R.drawable.ic_nolike));
                } else {
                    productHolder.mImageLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_nolike));
                }
            }else
            {
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
                    if(finalLike)
                    {

                        databaseLike.child(mListLike.get(finalIdlike).getId()).removeValue();
                        databasePlatos.child(mListPlato.get(i).getId()).child("like").setValue(mListPlato.get(i).getLike() - 1);
                        mListLike.remove(finalIdlike);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            productHolder.mImageLike.setImageDrawable(getDrawable(R.drawable.ic_like));
                        } else {
                            productHolder.mImageLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_like));
                        }

                        txlike.setText(String.valueOf(mListPlato.get(i).getLike() - 1));

                    }else {
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
                        txlike.setText(String.valueOf(mListPlato.get(i).getLike() + 1));

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

                                if(temp[0]!=null)
                                {

                                    String type = "1";
                                    if(temp[0].getMobile()!=null)
                                    {
                                        type=temp[0].getMobile();
                                    }

                                    databaseNoti.child(finalId).setValue(new Notificacion(user.getUid(),
                                            temp[0].getFirebaseId(),
                                            /*Utemp.getName()+" "+Utemp.getLastname()+" "+*/getString(R.string.like),
                                            "0", finalId,"",0));

                                    new Constants.PushTask().execute(getString(R.string.app_name),
                                            Utemp.getName()+" "+Utemp.getLastname()+" "+getString(R.string.like),
                                            temp[0].getFirebase_code(),type,"N","");



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

                    showDescri(i);
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

        private void setAnimation(View viewToAnimate, int position)
        {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition)
            {
                Animation animation;
                if(position % 2==0) {
                    animation= AnimationUtils.loadAnimation(OtrosActivity.this, R.anim.left_in);
                }else
                {
                    animation= AnimationUtils.loadAnimation(OtrosActivity.this, R.anim.right_in);
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
            mCosto=(TextView) itemView.findViewById(R.id.txtcosto);
            mRacioles = (TextView) itemView.findViewById(R.id.txtraciones);
            mHora = (TextView) itemView.findViewById(R.id.txthora);
            mImageLike = (ImageView) itemView.findViewById(R.id.imglike);
            mImage = (ImageView) itemView.findViewById(R.id.imagen);
            mLike = (TextView) itemView.findViewById(R.id.txlike);
            reloj=(ImageView)itemView.findViewById(R.id.imgreloj);

        }
    }


    public void showDescri(final int index)
    {

        int caducado = 0;
        if (compareDate(mListPlato.get(index).getDia() + " " + mListPlato.get(index).getHora().replace("h", ":"), new SimpleDateFormat("yyyy-MM-dd HH:mm").format(fecha))) {
            caducado = 1;
        }

        mUserRecyclerView.setVisibility(View.GONE);
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
                            Intent intent= new Intent(OtrosActivity.this, ProfileActivity.class);
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
                        if (temp[0] != null) {

                            String type = "1";
                            if(temp[0].getMobile()!=null)
                            {
                                type=temp[0].getMobile();
                            }

                            new Constants.PushTask().execute(Utemp.getName() + " " + Utemp.getLastname(),
                                    getString(R.string.reserva) + " " + mListPlato.get(index).getTitle(),
                                    temp[0].getFirebase_code(), type, "N","");

                            DatabaseReference databasenoti;
                            databasenoti = FirebaseDatabase.getInstance().getReference("notificaciones");

                        /*databasenoti.setValue(new Notificacion(user.getUid(),
                                mListPlato.get(index).getUser(),
                                getString(R.string.reserva)+" "+mListPlato.get(index).getTitle(),
                                "1",databasenoti.push().getKey()
                        ));*/

                            id = databasenoti.push().getKey();
                            databasenoti.child(id).setValue(new Notificacion(user.getUid(),
                                    mListPlato.get(index).getUser(),
                                    getString(R.string.reserva) + " " + mListPlato.get(index).getTitle(),
                                    "1", id, mListPlato.get(index).getId(), Integer.parseInt(cantidad.getText().toString())));

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
        flag=false;


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

    @Override
    protected void onStart() {
        super.onStart();

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

        Query busquedaplato;

        if(getIntent().getExtras().getString("busqueda").equals("0")) {
            busquedaplato = databasePlatos
                    .orderByChild("favorito").equalTo(0);

        }else if(getIntent().getExtras().getString("busqueda").equals("1"))
        {
            busquedaplato = databasePlatos
                    .orderByChild("costo").equalTo("0");
        }else
        {
            busquedaplato = databasePlatos
                    .orderByChild("tipo").equalTo(getIntent().getExtras().getString("busqueda"));
        }

        busquedaplato.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mListPlato.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Plato publi = postSnapshot.getValue(Plato.class);
                    //adding artist to the list
                    if(publi.getFavorito()==0) {

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
                    if(getIntent().getExtras().getString("busqueda").equals("0")) {
                        Comparator orden = Collections.reverseOrder(new SortbyLike());
                        Collections.sort(mListPlato, orden);
                    }else
                    {
                        Collections.reverse(mListPlato);
                    }

                    mUserRecyclerView.setVisibility(View.VISIBLE);
                    mPlatoAdapter.notifyDataSetChanged();
                    databasePlatos.removeEventListener(this);
                } else {
                    //progress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(flag) {
                    onBackPressed();
                    finish();
                }else
                {
                    flag=true;
                    descri.setVisibility(View.GONE);
                    mUserRecyclerView.setVisibility(View.VISIBLE);
                }
                //------------
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class SortbyLike implements Comparator<Plato>
    {
        // Used for sorting in ascending order of
        // roll number
        public int compare(Plato a, Plato b)
        {
            return a.getLike() - b.getLike();
        }
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
