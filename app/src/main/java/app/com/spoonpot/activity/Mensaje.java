package app.com.spoonpot.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import app.com.spoonpot.R;
import app.com.spoonpot.config.Constants;
import app.com.spoonpot.helpers.DataBase;
import app.com.spoonpot.holder.CabeceraMensaje;
import app.com.spoonpot.holder.ChatMessage;
import app.com.spoonpot.holder.Friend;
import app.com.spoonpot.holder.Plato;
import app.com.spoonpot.holder.User;


import static app.com.spoonpot.helpers.DataBase.TABLE_MSG;

/**
 * Created by Belal on 2/3/2016.
 */

//Our class extending fragment
public class Mensaje extends Fragment {


    private ArrayList<ChatMessage> mListMensaje;
    private DatabaseReference databaseMensaje;
    private MensajeRecycleAdapter mMensajeAdapter;
    private RecyclerView mMensajeRecyclerView;
    private FirebaseUser user;
    private ValueEventListener  listen;

    private String TAG = Mensaje.class.getName();
    private DataBase db;

    //Overriden method onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes
        return inflater.inflate(R.layout.tab1, container, false);
    }



    @Override
    public void onActivityCreated(Bundle state) {

        super.onActivityCreated(state);


         /* list plato*/
        mListMensaje= new ArrayList<ChatMessage>();
        /* data info user*/
        user = FirebaseAuth.getInstance().getCurrentUser();

        mMensajeRecyclerView = (RecyclerView) getView().findViewById(R.id.friend);
        // Create a grid layout with two columns
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        // Create a custom SpanSizeLookup where the first item spans both columns

        /*layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? 2 : 2;
            }
        });*/
        mMensajeRecyclerView.setLayoutManager(layoutManager);

        mMensajeAdapter=new MensajeRecycleAdapter();
        mMensajeRecyclerView.setAdapter(mMensajeAdapter);

        //getting the reference of publications node
        databaseMensaje = FirebaseDatabase.getInstance().getReference("chats");
        databaseMensaje.keepSynced(true);

        db = new DataBase(getContext());


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onStart() {
        super.onStart();


        listen= new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //clearing the previous artist list
                mListMensaje.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    ChatMessage temp = postSnapshot.getValue(ChatMessage.class);
                    //adding artist to the list
                    /*if( mListMensaje.size()==0) {
                        mListMensaje.add(temp);
                    }else{*/
                        if(temp.getUseridDestino().equals(user.getUid()) || temp.getUseridOrigen().equals(user.getUid()))
                        {

                            boolean add=true;
                            for(int x=0;x<mListMensaje.size();x++)
                            {
                                if( (mListMensaje.get(x).getUseridOrigen().equals(temp.getUseridOrigen()) && mListMensaje.get(x).getUseridDestino().equals(temp.getUseridDestino())) ||
                                        mListMensaje.get(x).getUseridOrigen().equals(temp.getUseridDestino()) && mListMensaje.get(x).getUseridDestino().equals(temp.getUseridOrigen())){
                                    add=false;
                                }


                            }
                            if(add) {

                                if(temp.getUseridDestino().equals(user.getUid()))
                                {
                                    String temporal="";
                                    temporal=temp.getUseridOrigen();
                                    temp.setUseridOrigen(temp.getUseridDestino());
                                    temp.setUseridDestino(temporal);

                                }
                                Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MSG
                                                + " WHERE firebaseid = ?",
                                        new String[] { temp.getUseridDestino() });
                                int orden=0;
                                if (cursor != null && cursor.getCount() > 0) {
                                    cursor.moveToFirst();
                                    orden = cursor.getInt(cursor
                                            .getColumnIndex("_id"));
                                    temp.setLeido(cursor.getInt(cursor
                                            .getColumnIndex("contador")));
                                }else
                                {
                                    temp.setLeido(0);
                                }
                                temp.setOrden(orden);
                                //temp.setLeido(Constants.validaMensaje(temp.getUseridDestino(),getContext()));
                                mListMensaje.add(temp);
                            }
                        }
                    //}

                    databaseMensaje.removeEventListener(this);
                }

                if(mListMensaje.size()>0)
                {
                    Comparator orden = Collections.reverseOrder(new SortbyOrden());
                    Collections.sort(mListMensaje, orden);
                    mMensajeRecyclerView.setVisibility(View.VISIBLE);
                    mMensajeAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseMensaje.addListenerForSingleValueEvent(listen);



    }

              /* adapter*/

    public class MensajeRecycleAdapter extends RecyclerView.Adapter<MensajeRecycleHolder> {
        private int lastPosition = -1;
        @Override
        public MensajeRecycleHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_mensajes, viewGroup, false);
            return new MensajeRecycleHolder(v);
        }



        @Override
        public void onBindViewHolder(final MensajeRecycleHolder productHolder, final int i) {

            if(mListMensaje.get(i).getLeido() == 1)
            {
                productHolder.mContededor.setCardBackgroundColor(getResources().getColor(R.color.chat));
            }


            DatabaseReference databaseUsers;
            databaseUsers = FirebaseDatabase.getInstance().getReference("users");
            Query userquery = databaseUsers
                    .orderByChild("firebaseId").equalTo(mListMensaje.get(i).getUseridDestino());

            final User[] temp = {new User()};
            userquery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //getting artist

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        //getting artist
                        temp[0] = postSnapshot.getValue(User.class);

                    }

                    if(temp[0]!=null)
                    {

                        productHolder.mTitle.setText(temp[0].getName()+" "+temp[0].getLastname());
                        if(!temp[0].getUrl_imagen().equals(""))
                        {
                            Glide.with(getActivity()).load(temp[0].getUrl_imagen())
                                    .thumbnail(1.0f)
                                    .crossFade()
                                    .override(65,65)
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


            productHolder.mChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mListMensaje.get(i).setLeido(0);
                    /*ChatMessage c= mListMensaje.get(i);
                    mListMensaje.remove(i);
                    mListMensaje.add(i,c);*/
                    mMensajeAdapter.notifyItemChanged(i);

                    Intent  intent= new Intent(getActivity(),ChatActivity.class);
                    intent.putExtra("userd",mListMensaje.get(i).getUseridDestino());
                    startActivity(intent);
                }
            });




            // Here you apply the animation when the view is bound
            //setAnimation(productHolder.itemView, i);

        }


        @Override
        public int getItemCount() {
            return mListMensaje.size();
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

    public class MensajeRecycleHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public ImageView mChat;
        public RatingBar mBar;
        public ImageView mUser;
        public CardView mContededor;



        public MensajeRecycleHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.txtname);
            mChat = (ImageView) itemView.findViewById(R.id.imgchat);
            mBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
            mUser=(ImageView) itemView.findViewById(R.id.img);
            mContededor = (CardView) itemView.findViewById(R.id.contenedor);
        }
    }


    class SortbyOrden implements Comparator<ChatMessage>
    {
        // Used for sorting in ascending order of
        // roll number
        public int compare(ChatMessage a, ChatMessage b)
        {
            return Math.round(a.getOrden()-b.getOrden());
        }
    }



}
