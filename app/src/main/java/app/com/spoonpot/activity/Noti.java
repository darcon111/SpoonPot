package app.com.spoonpot.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

import app.com.spoonpot.R;
import app.com.spoonpot.config.AppPreferences;
import app.com.spoonpot.config.Constants;
import app.com.spoonpot.holder.Notificacion;
import app.com.spoonpot.holder.Pagos;
import app.com.spoonpot.holder.Plato;
import app.com.spoonpot.holder.User;
import app.com.spoonpot.holder.Valoraciones;

/**
 * Created by Belal on 2/3/2016.
 */

//Our class extending fragment
public class Noti extends Fragment {


    private ArrayList<Notificacion> mListNoti;
    private DatabaseReference databaseNoti;
    private DatabaseReference databaseValoracion;
    private DatabaseReference databasePlato;
    private DatabaseReference databasePagos;

    private NotiRecycleAdapter mNotiAdapter;
    private RecyclerView mNotiRecyclerView;
    private FirebaseUser user;
    private Paint p = new Paint();
    private String TAG = Noti.class.getName();
    private AppPreferences app;


    //Overriden method onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes
        return inflater.inflate(R.layout.tab2, container, false);
    }



    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        app = new AppPreferences(getContext());
         /* list plato*/
        mListNoti = new ArrayList<Notificacion>();
        /* data info user*/
        user = FirebaseAuth.getInstance().getCurrentUser();

        mNotiRecyclerView = (RecyclerView) getView().findViewById(R.id.trecetas);
        // Create a grid layout with two columns
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        // Create a custom SpanSizeLookup where the first item spans both columns

        /*layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? 2 : 2;
            }
        });*/
        mNotiRecyclerView.setLayoutManager(layoutManager);

        mNotiAdapter=new NotiRecycleAdapter();
        mNotiRecyclerView.setAdapter(mNotiAdapter);

        //getting the reference of publications node
        databaseNoti = FirebaseDatabase.getInstance().getReference("notificaciones");
        databaseNoti.keepSynced(true);

        databaseValoracion=FirebaseDatabase.getInstance().getReference("valoraciones");
        databaseValoracion.keepSynced(true);
        databasePlato=FirebaseDatabase.getInstance().getReference("platos");

        databasePagos=FirebaseDatabase.getInstance().getReference("pagos");
        databasePagos.keepSynced(true);
        initSwipe();


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onStart() {
        super.onStart();



        Query query = databaseNoti
                .orderByChild("useridDestino").equalTo(user.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //getting artist

                mListNoti.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Notificacion noti = postSnapshot.getValue(Notificacion.class);

                    mListNoti.add(noti);

                }
                if (mListNoti.size() > 0) {
                    //progress.setVisibility(View.GONE);
                    Collections.reverse(mListNoti);
                    mNotiRecyclerView.setVisibility(View.VISIBLE);
                    mNotiAdapter.notifyDataSetChanged();



                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });


    }

              /* adapter*/

    public class NotiRecycleAdapter extends RecyclerView.Adapter<NotiRecycleHolder> {
        private int lastPosition = -1;
        @Override
        public NotiRecycleHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_noti, viewGroup, false);
            return new NotiRecycleHolder(v);
        }



        @Override
        public void onBindViewHolder(final NotiRecycleHolder productHolder, final int i) {


            productHolder.mMensaje.setText(mListNoti.get(i).getMensaje());
            /*Bitmap bitmap = Constants.decodeBase64(mListFriend.get(i).getImage());
            productHolder.mImage.setImageBitmap(bitmap);

            productHolder.mImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // showDescri(i);
                }
            });*/
            final DatabaseReference databaseUsers;
            databaseUsers = FirebaseDatabase.getInstance().getReference("users");
            Query userquery = databaseUsers
                    .orderByChild("firebaseId").equalTo(mListNoti.get(i).getUseridOrigen());

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
                            Glide.with(getActivity()).load(temp[0].getUrl_imagen())
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
                                Intent intent= new Intent(getActivity(), ProfileActivity.class);
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


            if(mListNoti.get(i).getTipo().equals("1"))
            {

                productHolder.container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                productHolder.container.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        new AlertDialog.Builder(getActivity(), R.style.AlertDialog)
                                .setIcon(R.mipmap.ic_launcher)
                                .setTitle(getString(R.string.app_name))
                                .setMessage(getString(R.string.aprovate))
                                .setCancelable(false)
                                .setNegativeButton(getString(R.string.no)
                                        , new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {//un listener que al pulsar, cierre la aplicacion
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {


                                        Query userquery = databaseUsers
                                                .orderByChild("firebaseId").equalTo(mListNoti.get(i).getUseridDestino());

                                        final User[] temp2 = {new User()};
                                        final String[] id = new String[1];
                                        userquery.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                //getting artist

                                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                    //getting artist
                                                    temp2[0] = postSnapshot.getValue(User.class);
                                                    databaseUsers.removeEventListener(this);
                                                }
                                                if(temp2[0]!=null)
                                                {

                                                    String type = "1";
                                                    if(temp[0].getMobile()!=null)
                                                    {
                                                        type=temp[0].getMobile();
                                                    }

                                                    if(!temp2[0].getFirebase_code().equals("")) {
                                                        new Constants.PushTask().execute(temp[0].getName() + " " + temp[0].getLastname(),
                                                                mListNoti.get(i).getMensaje()+" "+getString(R.string.reserva_apro),
                                                                temp[0].getFirebase_code(),type,"N","");

                                                        id[0]=databaseNoti.push().getKey();
                                                        databaseNoti.child(id[0]).setValue(new Notificacion(user.getUid(),
                                                                temp[0].getFirebaseId(),
                                                                mListNoti.get(i).getMensaje()+" "+getString(R.string.reserva_apro),
                                                                "0",id[0],"",0));

                                                    }

                                                    final Plato[] plato = {new Plato()};
                                                    Query queryplato = databasePlato
                                                            .orderByChild("id").equalTo(mListNoti.get(i).getReferencia());

                                                    queryplato.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                                //getting artist
                                                                plato[0] = postSnapshot.getValue(Plato.class);
                                                                databasePlato.removeEventListener(this);
                                                            }
                                                            if(plato[0]!=null)
                                                            {

                                                                id[0] =databaseValoracion.push().getKey();

                                                                databaseValoracion.child(id[0]).setValue(new Valoraciones(
                                                                        temp2[0].getFirebaseId(),
                                                                        temp[0].getFirebaseId(),
                                                                        plato[0].getDia()+" "+plato[0].getHora(),
                                                                        0,0,"","",0, id[0]
                                                                ));
                                                                id[0] =databaseValoracion.push().getKey();
                                                                databaseValoracion.child(id[0]).setValue(new Valoraciones(
                                                                        temp[0].getFirebaseId(),
                                                                        temp2[0].getFirebaseId(),
                                                                        plato[0].getDia()+" "+plato[0].getHora(),
                                                                        0,0,"","",1, id[0]
                                                                ));

                                                                String referencia=id[0];
                                                                id[0]=databasePagos.push().getKey();
                                                                Float costo =Float.parseFloat(plato[0].getCosto()) /Float.parseFloat(plato[0].getRaciones());
                                                                BigDecimal valor= Constants.round(mListNoti.get(i).getCantidad()* costo,2);
                                                                databasePagos.child(id[0]).setValue(
                                                                        new Pagos(temp2[0].getFirebaseId(),
                                                                                temp[0].getFirebaseId(),
                                                                                getString(R.string.deuda)+" "+ valor,id[0],referencia
                                                                        )
                                                                );

                                                                int ofrezco=Integer.parseInt(plato[0].getOfrezco())-mListNoti.get(i).getCantidad();
                                                                databasePlato.child(plato[0].getId()).child("ofrezco").setValue(String.valueOf(ofrezco));

                                                                databaseNoti.child(mListNoti.get(i).getId()).removeValue();


                                                                mNotiAdapter.removeItem(i);

                                                                int count=0;
                                                                if(Integer.parseInt(app.getNoti()) > 0)
                                                                {
                                                                    count =Integer.parseInt(app.getNoti())-1;
                                                                    if(count<0)
                                                                    {
                                                                        count=0;
                                                                    }
                                                                }

                                                                app.setNoti(count);


                                                            }


                                                        }
                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                            Log.e(TAG, "onCancelled", databaseError.toException());
                                                        }
                                                    });

                                                    /*String id=databaseNoti.push().getKey();
                                                    databaseNoti.child(id).setValue(new Notificacion(user.getUid(),
                                                            temp2[0].getFirebaseId(),
                                                            getString(R.string.reserva_apro),
                                                            "0",id,""));*/

                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                Log.e(TAG, "onCancelled", databaseError.toException());
                                            }
                                        });



                                    }
                                })
                                .show();

                        return true;
                    }
                });
            }




            // Here you apply the animation when the view is bound
            //setAnimation(productHolder.itemView, i);

        }

        public void removeItem(int position) {
            mListNoti.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mListNoti.size());
            //Signal.get().reset();


        }

        @Override
        public int getItemCount() {
            return mListNoti.size();
        }


        private void setAnimation(View viewToAnimate, int position)
        {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition)
            {
                Animation animation;
                if(position % 2==0) {
                    animation= AnimationUtils.loadAnimation(getActivity(), R.anim.left_in);
                }else
                {
                    animation= AnimationUtils.loadAnimation(getActivity(), R.anim.right_in);
                }

                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }


    }

    public class NotiRecycleHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public TextView mMensaje;
        public ImageView mUser;
        public LinearLayout container;



        public NotiRecycleHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.txtname);
            mMensaje = (TextView) itemView.findViewById(R.id.txtmensaje);
            mUser=(ImageView) itemView.findViewById(R.id.img);
            container=(LinearLayout) itemView.findViewById(R.id.container);
        }
    }

    private void initSwipe(){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT){

                    new AlertDialog.Builder(getActivity(), R.style.AlertDialog)
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle(getString(R.string.app_name))
                            .setMessage(getString(R.string.delete))
                            .setCancelable(false)
                            .setNegativeButton(getString(R.string.no)
                                    , new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            mNotiAdapter.notifyDataSetChanged();
                                        }
                                    })
                            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {//un listener que al pulsar, cierre la aplicacion
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    databaseNoti.child(mListNoti.get(position).getId()).removeValue();
                                    mNotiAdapter.removeItem(position);

                                    int count=0;
                                    if(Integer.parseInt(app.getNoti()) > 0)
                                    {
                                        count =Integer.parseInt(app.getNoti())-1;
                                        if(count<0)
                                        {
                                            count=0;
                                        }
                                    }

                                    app.setNoti(count);


                                }
                            })
                            .show();


                } else {

                   /* if(!mListPublic.get(position).getApprove().equals("2")){
                        databasePublication.child(mListPublic.get(position).getId()).child("approve").setValue("2");
                        new Constants.PushTask().execute(getString(R.string.app_name),mListPublic.get(position).getTitle(),"1");
                    }else
                    {
                        mPublicationAdapter.notifyDataSetChanged();
                    }*/
                }
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE && dX <= 0){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    //if(dX < 0){
                        p.setColor((getActivity().getColor(R.color.error)));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                   /* } else {
                           // mNotiAdapter.notifyDataSetChanged();
                    }*/
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mNotiRecyclerView);
    }


}
