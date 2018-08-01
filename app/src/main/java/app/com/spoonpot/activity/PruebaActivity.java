package app.com.spoonpot.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import app.com.spoonpot.R;
import app.com.spoonpot.config.AppPreferences;
import app.com.spoonpot.config.Constants;
import app.com.spoonpot.holder.Link;

public class PruebaActivity extends AppCompatActivity {

    Button btn;
    AppPreferences app;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba);


        btn=(Button) findViewById(R.id.btn);

        app= new AppPreferences(this);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Constants.PushTask().execute("prueba","mensaje",app.getFirebasetoken(),"1","N","");

            }
        });

    }


    public void  main(View v)
    {
        Intent t= new Intent(PruebaActivity.this, MainActivity.class);
        startActivity(t);
        finish();

    }

    public void insert(View v)
    {
        DatabaseReference databaseLink;
        databaseLink = FirebaseDatabase.getInstance().getReference("liks");
        String id = databaseLink.push().getKey();
        databaseLink.child(id).setValue(new Link("dasd",1));

    }
}
