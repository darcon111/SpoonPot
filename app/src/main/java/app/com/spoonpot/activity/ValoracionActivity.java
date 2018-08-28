package app.com.spoonpot.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import java.text.DecimalFormat;

import app.com.spoonpot.R;
import app.com.spoonpot.config.Constants;
import app.com.spoonpot.holder.Notificacion;
import app.com.spoonpot.holder.Pagos;
import app.com.spoonpot.holder.User;


public class ValoracionActivity extends AppCompatActivity {

    private Button valorar;
    private RatingBar ratingBar1,ratingBar2;
    private EditText comenPlato,comenCoci;
    private TextView txtplato,txtcome;
    private LinearLayout one;

    private DatabaseReference databaseValoracion;
    private DatabaseReference databaseUser;
    private DatabaseReference databasePagos;

    final User[] temp = {new User()};
    private String TAG = ValoracionActivity.class.getName();
    private ConstraintLayout principal;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valoracion);

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
        /* data info user*/
        user = FirebaseAuth.getInstance().getCurrentUser();

        valorar=(Button) findViewById(R.id.btnvalorar);
        ratingBar1=(RatingBar) findViewById(R.id.ratingBar1);
        ratingBar2=(RatingBar) findViewById(R.id.ratingBar2);
        comenPlato=(EditText) findViewById(R.id.comenPlato);
        comenCoci=(EditText) findViewById(R.id.comenCoci);
        principal=(ConstraintLayout) findViewById(R.id.principal);
        txtplato=(TextView) findViewById(R.id.txtplato);
        txtcome=(TextView) findViewById(R.id.txtcome);
        one=(LinearLayout) findViewById(R.id.one);

        if(getIntent().getExtras().getString("edit", "0").equals("0"))
        {
            valorar.setVisibility(View.GONE);
            comenPlato.setText(getIntent().getExtras().getString("comentarioPlato", "0"));
            comenCoci.setText(getIntent().getExtras().getString("txtcome", "0"));
            ratingBar1.setRating(getIntent().getExtras().getFloat("valoracionPlato", 0));
            ratingBar2.setRating(getIntent().getExtras().getFloat("valoracionCocinero", 0));

            comenPlato.setEnabled(false);
            comenCoci.setEnabled(false);
            ratingBar1.setEnabled(false);
            ratingBar2.setEnabled(false);
        }

        if(getIntent().getExtras().getInt("tipo", 0)==0)
        {
            txtcome.setText(getString(R.string.valorarc));
            one.setVisibility(View.GONE);
        }

        databaseValoracion = FirebaseDatabase.getInstance().getReference("valoraciones");
        databasePagos = FirebaseDatabase.getInstance().getReference("pagos");
        databaseUser = FirebaseDatabase.getInstance().getReference("users");

        final Query userquery = databaseUser
                .orderByChild("firebaseId").equalTo(getIntent().getExtras().getString("destino", "0"));

        userquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //getting artist

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    temp[0] = postSnapshot.getValue(User.class);
                    userquery.removeEventListener(this);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });

    }

    public void save(View v)
    {

        if(getIntent().getExtras().getInt("tipo", 0)==1)
        {
            databaseValoracion.child(getIntent().getExtras().getString("id", "0")).child("valoracionPlato").setValue(ratingBar1.getRating());
            databaseValoracion.child(getIntent().getExtras().getString("id", "0")).child("comentarioPlato").setValue(comenPlato.getText().toString());
        }

        databaseValoracion.child(getIntent().getExtras().getString("id", "0")).child("valoracionCocinero").setValue(ratingBar2.getRating());

        databaseValoracion.child(getIntent().getExtras().getString("id", "0")).child("comentarioCocinero").setValue(comenCoci.getText().toString());

        if(temp[0].getValoracion()==null)
        {
            databaseUser.child(temp[0].getId()).child("valoracion").setValue(String.valueOf(ratingBar2.getNumStars()));
        }else
        {

            if(temp[0].getValoracion().equals("0"))
            {
                databaseUser.child(temp[0].getId()).child("valoracion").setValue(String.valueOf(ratingBar2.getRating()));
            }else {
                float promedio = (Float.parseFloat(temp[0].getValoracion()) + ratingBar2.getRating()) / 2;
                DecimalFormat df = new DecimalFormat("###.#");
                String calificacion=String.valueOf(df.format(promedio).replace(",","."));
                databaseUser.child(temp[0].getId()).child("valoracion").setValue(calificacion);
            }
        }
        DatabaseReference databaseNoti;
        databaseNoti = FirebaseDatabase.getInstance().getReference("notificaciones");

        String id=databaseNoti.push().getKey();

        String type = "1";
        if(temp[0].getMobile()!=null)
        {
            type=temp[0].getMobile();
        }

        databaseNoti.child(id).setValue(new Notificacion(user.getUid(),
                temp[0].getFirebaseId(),
               getString(R.string.valorareci),
                "0",id,"",0));

        new Constants.PushTask().execute(getString(R.string.app_name),
                getString(R.string.valorareci),
                temp[0].getFirebase_code(),type,"N","");

        databasePagos.keepSynced(true);
        Query querypagos = databasePagos
                .orderByChild("referencia_valor").equalTo(getIntent().getExtras().getString("id", "0"));

        querypagos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //clearing the previous artist list
                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Pagos pag = postSnapshot.getValue(Pagos.class);
                    databasePagos.child(pag.getId()).removeValue();
                }
                databasePagos.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });


        Snackbar snackbar = Snackbar
                .make(principal, getString(R.string.calificacion), Snackbar.LENGTH_LONG);

        snackbar.show();

        finish();

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

}
