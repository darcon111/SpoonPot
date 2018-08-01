package app.com.spoonpot.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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

import app.com.spoonpot.R;
import app.com.spoonpot.config.Constants;
import app.com.spoonpot.holder.Friend;
import app.com.spoonpot.holder.Plato;
import app.com.spoonpot.holder.User;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Belal on 2/3/2016.
 */

//Our class extending fragment
public class Tab1 extends Fragment {


    private ArrayList<Friend> mListFriend;
    private DatabaseReference databaseFriend;
    private FriendRecycleAdapter mFriendAdapter;
    private RecyclerView mFriendRecyclerView;
    private FirebaseUser user;
    private int restart=0;
    private String TAG = Tab1.class.getName();


    //Overriden method onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes
        return inflater.inflate(R.layout.tab1, container, false);
    }



    @Override
    public void onActivityCreated(Bundle state) {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/RobotoLight.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        super.onActivityCreated(state);


         /* list plato*/
        mListFriend = new ArrayList<Friend>();
        /* data info user*/
        user = FirebaseAuth.getInstance().getCurrentUser();

        mFriendRecyclerView = (RecyclerView) getView().findViewById(R.id.friend);
        // Create a grid layout with two columns
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        // Create a custom SpanSizeLookup where the first item spans both columns

        /*layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? 2 : 2;
            }
        });*/
        mFriendRecyclerView.setLayoutManager(layoutManager);

        mFriendAdapter=new FriendRecycleAdapter();
        mFriendRecyclerView.setAdapter(mFriendAdapter);

        //getting the reference of publications node
        databaseFriend = FirebaseDatabase.getInstance().getReference("friends");
        databaseFriend.keepSynced(true);




    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onStart() {
        super.onStart();

        if(restart==0) {
            restart++;

            Query query = databaseFriend
                    .orderByChild("user").equalTo(user.getUid());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //getting artist

                    mListFriend.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        //getting artist
                        Friend friend = postSnapshot.getValue(Friend.class);

                        mListFriend.add(friend);

                    }
                    if (mListFriend.size() > 0) {
                        //progress.setVisibility(View.GONE);
                        mFriendRecyclerView.setVisibility(View.VISIBLE);
                        mFriendAdapter.notifyDataSetChanged();
                        databaseFriend.removeEventListener(this);
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled", databaseError.toException());
                }
            });
        }


    }

              /* adapter*/

    public class FriendRecycleAdapter extends RecyclerView.Adapter<FriendRecycleHolder> {
        private int lastPosition = -1;
        @Override
        public FriendRecycleHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_tus_amigos, viewGroup, false);
            return new FriendRecycleHolder(v);
        }



        @Override
        public void onBindViewHolder(final FriendRecycleHolder productHolder, final int i) {

            productHolder.mTitle.setText(mListFriend.get(i).getNamefriend());






            /*Bitmap bitmap = Constants.decodeBase64(mListFriend.get(i).getImage());
            productHolder.mImage.setImageBitmap(bitmap);*/

            productHolder.mChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent=new Intent(getActivity(),ChatActivity.class);
                    intent.putExtra("userd",mListFriend.get(i).getFriend());
                    startActivity(intent);
                }
            });
            DatabaseReference databaseUsers;
            databaseUsers = FirebaseDatabase.getInstance().getReference("users");
            Query userquery = databaseUsers
                    .orderByChild("firebaseId").equalTo(mListFriend.get(i).getFriend());

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
                        if(temp[0].getValoracion()!=null)
                        {
                            productHolder.mBar.setRating(Float.parseFloat((temp[0].getValoracion())));
                        }else
                        {
                            productHolder.mBar.setRating(Float.parseFloat("0"));
                        }

                        if(!temp[0].getUrl_imagen().equals(""))
                        {


                            Glide.with(getActivity()).load(temp[0].getUrl_imagen())
                                    .thumbnail(1.0f)
                                    .centerCrop()
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

                        productHolder.mTitle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

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







            // Here you apply the animation when the view is bound
            //setAnimation(productHolder.itemView, i);

        }


        @Override
        public int getItemCount() {
            return mListFriend.size();
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

    public class FriendRecycleHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public ImageView mChat;
        public RatingBar mBar;
        public ImageView mUser;



        public FriendRecycleHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.txtname);
            mChat = (ImageView) itemView.findViewById(R.id.imgchat);
            mBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
            mUser=(ImageView) itemView.findViewById(R.id.img);
        }
    }


    protected void attachBaseContext(Context newBase) {
        super.onAttach(CalligraphyContextWrapper.wrap(newBase));
    }


}
