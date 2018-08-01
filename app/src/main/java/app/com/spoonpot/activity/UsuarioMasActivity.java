package app.com.spoonpot.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.disklrucache.DiskLruCache;
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
import app.com.spoonpot.config.AppPreferences;
import app.com.spoonpot.config.Constants;
import app.com.spoonpot.holder.ChatMessage;
import app.com.spoonpot.holder.Friend;
import app.com.spoonpot.holder.Like;
import app.com.spoonpot.holder.Plato;
import app.com.spoonpot.holder.User;
import app.com.spoonpot.holder.User_mas;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class UsuarioMasActivity extends AppCompatActivity {

    private DatabaseReference databaseUser;
    private UserRecycleAdapter mUserAdapter;
    private RecyclerView mUserRecyclerView;
    private FirebaseUser user;
    private AppPreferences app;
    private ArrayList<User_mas> mListUser;

    private String TAG = UsuarioMasActivity.class.getName();
    private DatabaseReference databaseFriend;

    private TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/RobotoLight.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_usuario_mas);

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

           /* list plato*/
        mListUser= new ArrayList<User_mas>();
        /* data info user*/
        user = FirebaseAuth.getInstance().getCurrentUser();

        mUserRecyclerView = (RecyclerView) findViewById(R.id.usermas);
        // Create a grid layout with two columns
        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
        // Create a custom SpanSizeLookup where the first item spans both columns

        /*layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? 2 : 2;
            }
        });*/
        mUserRecyclerView.setLayoutManager(layoutManager);

        mUserAdapter=new UserRecycleAdapter();
        mUserRecyclerView.setAdapter(mUserAdapter);

        //getting the reference of publications node
        databaseUser = FirebaseDatabase.getInstance().getReference("users");
        databaseUser.keepSynced(true);

        name=(TextView) findViewById(R.id.textName);
        name.setText(R.string.usuriomas);

    }

              /* adapter*/

    public class UserRecycleAdapter extends RecyclerView.Adapter<UserRecycleHolder> {
        private int lastPosition = -1;
        @Override
        public UserRecycleHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_usuario_mas, viewGroup, false);
            return new UserRecycleHolder(v);
        }



        @Override
        public void onBindViewHolder(final UserRecycleHolder productHolder, final int i) {


            productHolder.mTitle.setText(mListUser.get(i).getName()+" "+mListUser.get(i).getLastname());
            if(!mListUser.get(i).getUrl_imagen().equals(""))
            {
                Glide.with(getApplicationContext()).load(mListUser.get(i).getUrl_imagen())
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
                    Intent intent= new Intent(getApplicationContext(), ProfileActivity.class);
                    intent.putExtra("user",mListUser.get(i).getFirebaseId());
                    startActivity(intent);
                }
            });



            productHolder.mSeguidores.setText(String.valueOf(mListUser.get(i).getSeguidores()));




            // Here you apply the animation when the view is bound
            //setAnimation(productHolder.itemView, i);

        }


        @Override
        public int getItemCount() {
            return mListUser.size();
        }


        private void setAnimation(View viewToAnimate, int position)
        {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition)
            {
                Animation animation;
                if(position % 2==0) {
                    animation= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_in);
                }else
                {
                    animation= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_in);
                }

                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }


    }

    public class UserRecycleHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public ImageView mUser;
        public TextView mSeguidores;



        public UserRecycleHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.txtname);
            mUser=(ImageView) itemView.findViewById(R.id.img);
            mSeguidores=(TextView) itemView.findViewById(R.id.txtseguidores);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseFriend = FirebaseDatabase.getInstance().getReference("friends");

        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous artist list
                mListUser.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    final User_mas use = postSnapshot.getValue(User_mas.class);
                    //adding artist to the list


                    Query likequery = databaseFriend
                            .orderByChild("friend").equalTo(use.getFirebaseId());

                    final int[] contador = {0};
                    likequery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                contador[0]++;
                            }
                            databaseFriend.removeEventListener(this);

                            use.setSeguidores(contador[0]);

                            mListUser.add(use);

                            if (mListUser.size() > 0) {
                                databaseUser.removeEventListener(this);
                                Comparator orden = Collections.reverseOrder(new SortbySeguidores());
                                Collections.sort(mListUser, orden);
                                mUserAdapter.notifyDataSetChanged();


                            } else {
                                //progress.setVisibility(View.GONE);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });





                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    class SortbySeguidores implements Comparator<User_mas>
    {
        // Used for sorting in ascending order of
        // roll number
        public int compare(User_mas a, User_mas b)
        {
            return a.getSeguidores() - b.getSeguidores();
        }
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


}
