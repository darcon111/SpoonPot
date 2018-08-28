package app.com.spoonpot.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Build;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
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
import app.com.spoonpot.clases.ImagenCircular.CircleImageView;
import app.com.spoonpot.config.AppPreferences;
import app.com.spoonpot.config.Constants;
import app.com.spoonpot.helpers.DataBase;
import app.com.spoonpot.holder.ChatMessage;
import app.com.spoonpot.holder.User;


import static app.com.spoonpot.helpers.DataBase.TABLE_MSG;

public class ChatActivity extends AppCompatActivity {

    private ArrayList<ChatMessage> mListChat;
    private DatabaseReference databaseChat;
    private ChatRecycleAdapter mChatAdapter;
    private RecyclerView mChatRecyclerView;
    private FirebaseUser user;
    private AutoCompleteTextView text;
    private String imgOrigen="";
    private String imgDestino="";
    private String firebase_code_o="";
    private String firebase_code_d="";
    private String name_destino="";
    private String name_origen="";
    private GridLayoutManager layoutManager;
    private ValueEventListener eventListenerChat;
    private String TAG = Tab1.class.getName();
    private CircleImageView imgChat;
    private TextView imgName;
    private AppPreferences app;
    private DataBase db;
    final User[] temp2 = {new User()};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

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
        imgName=(TextView) findViewById(R.id.textName);
        imgChat=(CircleImageView) findViewById(R.id.img);


       /* list plato*/
        mListChat = new ArrayList<ChatMessage>();
        /* data info user*/
        user = FirebaseAuth.getInstance().getCurrentUser();
        hideKeyboard(this);
        mChatRecyclerView = (RecyclerView) findViewById(R.id.chat);
        // Create a grid layout with two columns
        layoutManager = new GridLayoutManager(getApplicationContext(), 1);
        // Create a custom SpanSizeLookup where the first item spans both columns

        /*layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? 2 : 2;
            }
        });*/
        mChatRecyclerView.setLayoutManager(layoutManager);

        mChatAdapter=new ChatRecycleAdapter();
        mChatRecyclerView.setAdapter(mChatAdapter);

        //getting the reference of publications node
        databaseChat = FirebaseDatabase.getInstance().getReference("chats");
        databaseChat.keepSynced(true);
        text=(AutoCompleteTextView) findViewById(R.id.txtchat);

        DatabaseReference databaseUsers;
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        final Query userquery;


        userquery = databaseUsers
                    .orderByChild("firebaseId").equalTo(user.getUid());


        final User[] temp = {new User()};
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
                    imgOrigen=temp[0].getUrl_imagen();
                    firebase_code_o=temp[0].getFirebase_code();
                    if(temp[0].getName().equals(""))
                    {
                        String []email =temp[0].getEmail().split("@");
                        name_origen=email[0];
                    }else
                    {
                        name_origen=temp[0].getName()+" "+temp[0].getLastname();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });

        final Query userquery2;


        userquery2 = databaseUsers
                .orderByChild("firebaseId").equalTo(getIntent().getExtras().getString("userd",""));



        userquery2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //getting artist

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    temp2[0] = postSnapshot.getValue(User.class);
                    userquery2.removeEventListener(this);
                }

                if(temp2[0]!=null)
                {
                    imgDestino=temp2[0].getUrl_imagen();
                    firebase_code_d=temp2[0].getFirebase_code();
                    if(temp2[0].getName().equals(""))
                    {
                        String []email =temp2[0].getEmail().split("@");
                        name_destino=email[0];

                    }else
                    {
                        name_destino=temp2[0].getName()+" "+temp2[0].getLastname();
                    }

                    imgName.setText(name_destino);
                    if(!temp2[0].getUrl_imagen().equals("")) {
                        Glide.with(getApplicationContext()).load(temp2[0].getUrl_imagen())
                                .thumbnail(1.0f)
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(imgChat);
                    }else
                    {
                        imgChat.setImageResource(R.drawable.ic_user);
                    }

                    imgChat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent= new Intent(getApplicationContext(), ProfileActivity.class);
                            if(temp[0].getFirebaseId().equals(user.getUid())) {
                                intent.putExtra("user", temp2[0].getFirebaseId());
                            }else
                            {
                                intent.putExtra("user", temp[0].getFirebaseId());
                            }
                            startActivity(intent);
                        }
                    });


                    db = new DataBase(getApplicationContext());

                    Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MSG
                                    + " WHERE firebaseid = ?",
                            new String[] { getIntent().getExtras().getString("userd","") });

                    if (cursor != null && cursor.getCount() > 0) {
                        cursor.moveToFirst();

                        if(cursor.getInt(cursor.getColumnIndex("contador"))==1) {
                            int cuenta = Integer.parseInt(app.getMensajes())-1;
                            app.setMensajes(cuenta);
                            ContentValues vals = new ContentValues();
                            vals.put("contador", 0);
                            db.update(TABLE_MSG,vals,"firebaseid = '"+cursor.getString(cursor
                                    .getColumnIndex("firebaseid"))+"'");
                        }



                        /*db.delete(TABLE_MSG, "firebaseid = '"+cursor.getString(cursor
                                .getColumnIndex("firebaseid"))+"'", null);*/
                    }
                    cursor.close();
                    cursor = null;



                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });





    }



    public void send(View v)
    {
        if(!text.getText().toString().trim().equals(""))
        {
            databaseChat.push().setValue(new ChatMessage(text.getText().toString(),
                    name_origen,
                    user.getUid(),
                    getIntent().getExtras().getString("userd","")));
            new Constants.PushTask().execute(name_origen,text.getText().toString(),firebase_code_d,"1","C",user.getUid());
            text.setText("");


        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        mListChat.clear();
        eventListenerChat= new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //clearing the previous artist list

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    ChatMessage chat = postSnapshot.getValue(ChatMessage.class);
                    //adding artist to the list
                    if((chat.getUseridOrigen().equals(user.getUid()) && chat.getUseridDestino().equals(getIntent().getExtras().getString("userd","")))
                            || (chat.getUseridOrigen().equals(getIntent().getExtras().getString("userd",""))  && chat.getUseridDestino().equals(user.getUid())))
                    {
                        mListChat.add(chat);



                    }

                }

                if (mListChat.size() > 0) {
                    //progress.setVisibility(View.GONE);
                    mChatRecyclerView.setVisibility(View.VISIBLE);
                    mChatAdapter.notifyDataSetChanged();
                    layoutManager.scrollToPosition(mListChat.size()-1);
                    text.setSelected(false);

                    int count=0;

                    if(Integer.parseInt(app.getMensajes()) > 0)
                    {
                        count =Integer.parseInt(app.getMensajes())-1;
                        if(count<0)
                        {
                            count=0;
                        }
                    }


                    app.setMensajes(count);

                } else {
                    //progress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };



        databaseChat.addValueEventListener(eventListenerChat);
        databaseChat.keepSynced(true);


    }


                  /* adapter*/

    public class ChatRecycleAdapter extends RecyclerView.Adapter<ChatRecycleHolder> {
        private int lastPosition = -1;
        @Override
        public ChatRecycleHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v;

            if(viewType==1)
            {
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat, viewGroup, false);
            }else
            {
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat2, viewGroup, false);
            }


            return new ChatRecycleHolder(v);
        }
        @Override
        public int getItemViewType(int position) {
            int viewType = 0; //Default is 1
           if(mListChat.get(position).getUseridOrigen().equals(user.getUid())) viewType = 1;
            return viewType;
        }


        @Override
        public void onBindViewHolder(final ChatRecycleHolder productHolder, final int i) {

            productHolder.mMensaje.setText(mListChat.get(i).getMessageText());
            /*Bitmap bitmap = Constants.decodeBase64(mListFriend.get(i).getImage());
            productHolder.mImage.setImageBitmap(bitmap);

            productHolder.mImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // showDescri(i);
                }
            });*/
            String url="";
            if(mListChat.get(i).getUseridOrigen().equals(user.getUid())) {

                url=imgOrigen;
            }else
            {

                url=imgDestino;
            }

            if(!url.equals(""))
            {
                Glide.with(getApplicationContext()).load(url)
                        .thumbnail(1.0f)
                        .crossFade()
                        .override(60,60)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(productHolder.mUser);

            }else
            {
                productHolder.mUser.setImageResource(R.drawable.ic_user);
            }

            // Here you apply the animation when the view is bound
            //setAnimation(productHolder.itemView, i);

        }


        @Override
        public int getItemCount() {
            return mListChat.size();
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

    public class ChatRecycleHolder extends RecyclerView.ViewHolder {
        public TextView mMensaje;
        public ImageView mUser;



        public ChatRecycleHolder(View itemView) {
            super(itemView);
            mMensaje = (TextView) itemView.findViewById(R.id.chat);
            mUser=(ImageView) itemView.findViewById(R.id.img);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        databaseChat.removeEventListener(eventListenerChat);
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
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}
