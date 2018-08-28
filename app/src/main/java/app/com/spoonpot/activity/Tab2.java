package app.com.spoonpot.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import app.com.spoonpot.R;
import app.com.spoonpot.clases.AppController;
import app.com.spoonpot.config.Constants;
import app.com.spoonpot.holder.Like;
import app.com.spoonpot.holder.Plato;


/**
 * Created by Belal on 2/3/2016.
 */

public class Tab2 extends Fragment {

    private ArrayList<Plato> mListPlato;
    private DatabaseReference databasePlatos;
    private PlatoRecycleAdapter mPlatoAdapter;
    private RecyclerView mUserRecyclerView;
    private FirebaseUser user;
    private int restart=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab2, container, false);






    }


    @Override
    public void onActivityCreated(Bundle state) {

        super.onActivityCreated(state);


         /* list plato*/
        mListPlato = new ArrayList<Plato>();
        /* data info user*/
        user = FirebaseAuth.getInstance().getCurrentUser();

        mUserRecyclerView = (RecyclerView) getView().findViewById(R.id.trecetas);
        // Create a grid layout with two columns
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        // Create a custom SpanSizeLookup where the first item spans both columns

        /*layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? 2 : 2;
            }
        });*/
        mUserRecyclerView.setLayoutManager(layoutManager);

        mPlatoAdapter=new PlatoRecycleAdapter();
        mUserRecyclerView.setAdapter(mPlatoAdapter);

        //getting the reference of publications node
        databasePlatos = FirebaseDatabase.getInstance().getReference("platos");
        databasePlatos.keepSynced(true);
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
                        if (publi.getUser().equals(user.getUid())) {
                            mListPlato.add(publi);
                        }
                    }

                    if (mListPlato.size() > 0) {
                        //progress.setVisibility(View.GONE);
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
    }


          /* adapter*/

    public class PlatoRecycleAdapter extends RecyclerView.Adapter<PlatoRecycleHolder> {
        private int lastPosition = -1;
        @Override
        public PlatoRecycleHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_tus_recetas, viewGroup, false);
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

                   // showDescri(i);
                }
            });

            productHolder.mPen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent =new Intent(getActivity(),AddPlatoActivity.class);
                    intent.putExtra("id",mListPlato.get(i).getId().toString());
                    intent.putExtra("username",mListPlato.get(i).getUsername());
                    startActivity(intent);
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

    public class PlatoRecycleHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public ImageView mImage;
        public ImageView mPen;




        public PlatoRecycleHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.txttitle);
            mImage = (ImageView) itemView.findViewById(R.id.imagen);
            mPen = (ImageView) itemView.findViewById(R.id.pen);

        }
    }



}
