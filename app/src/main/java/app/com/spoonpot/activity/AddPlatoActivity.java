package app.com.spoonpot.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.rd.PageIndicatorView;
import com.rd.animation.type.AnimationType;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import app.com.spoonpot.R;

import app.com.spoonpot.clases.BottonNavigationView.BottomNavigationViewEx;
import app.com.spoonpot.clases.GPS;
import app.com.spoonpot.clases.Screen;
import app.com.spoonpot.config.AppPreferences;
import app.com.spoonpot.config.Constants;
import app.com.spoonpot.helpers.BottomNavigationShiftHelper;
import app.com.spoonpot.holder.Friend;
import app.com.spoonpot.holder.Notificacion;
import app.com.spoonpot.holder.Plato;
import app.com.spoonpot.holder.User;
import cn.pedant.SweetAlert.SweetAlertDialog;
import q.rorbin.badgeview.QBadgeView;


public class AddPlatoActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener,IPickResult {
    private ViewFlipper simpleViewSwitcher;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    private File outPutFile = null;
    private Bitmap bitmap;
    private ImageView img1;
    private String image = "";
    private float initialX;
    private BottomNavigationViewEx bottomNavigationView;
    private EditText txtplato,txtdes,txtcosto,txtraciones,txtofrezco,txthora,txtdia,txtopcional,txtsearch;

    private ViewFlipper vf1,vf2;

    private EditText txtplato2,txttipo2,txtraciones2,txtdes2,txthora2;

    private DatabaseReference databasePlato;
    private FirebaseUser user;
    private Plato plato=new Plato();
    private MenuItem item ;
    private GoogleMap map;
    private MapView mapView;
    private String lat,log;
    private GPS gps = null;
    private String TAG = AddPlatoActivity.class.getName();
    private DatabaseReference databaseUsers;
    private User datauser=new User();

    private String mCurrentPhotoPath;
    private AppPreferences app;
    private int pantalla=0;
    private int restart=0;
    private PageIndicatorView pageIndicatorView;
    private int flag=0;
    private TextView txttipoplato;

    private DatabaseReference databaseFriend;
    private ArrayList<Friend> mListFriend;


    private BottomNavigationMenuView menuView;
    private View iconView;
    QBadgeView badgeView;

    private SweetAlertDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plato);

        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        user = FirebaseAuth.getInstance().getCurrentUser();
            /* toolbar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbaruser);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");


        txtplato=(EditText)findViewById(R.id.txtplato);
        txtdes=(EditText)findViewById(R.id.txtdes);
        txtcosto=(EditText)findViewById(R.id.txtcosto);
        txtraciones=(EditText)findViewById(R.id.txtraciones);
        txtofrezco=(EditText)findViewById(R.id.txtofrezco);
        txthora=(EditText)findViewById(R.id.txthora);
        txtdia=(EditText)findViewById(R.id.txtdia);
        txtopcional=(EditText)findViewById(R.id.txtopcional);


        gps = new GPS(AddPlatoActivity.this);
        if (!gps.canGetLocation()) {
            gps.showSettingsAlert();
        }
        gps.city();

        lat=String.valueOf(gps.getLatitude());
        log=String.valueOf(gps.getLongitude());

        mapView = (MapView) findViewById(R.id.mapView);
        //try {
        mapView.onCreate(savedInstanceState);
        //}catch (Exception e)
        //{

        // }


        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {


                map=googleMap;

                changePosition();


                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                    @Override
                    public void onMapClick(LatLng point) {
                        // TODO Auto-generated method stub

                        map.clear();
                        map.addMarker(new MarkerOptions().position(point));
                        lat=String.valueOf(point.latitude);
                        log=String.valueOf(point.longitude);
                    }
                });

            }
        });


        txtdia.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Calendar now = Calendar.getInstance();
                    DatePickerDialog dpd = DatePickerDialog.newInstance(
                            AddPlatoActivity.this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    );
                    dpd.show(getFragmentManager(), "Datepickerdialog");
                }
                Log.i("click text", "kakak");
                return false;
            }
        });

        txthora.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    Calendar now = Calendar.getInstance();
                    TimePickerDialog tpd = TimePickerDialog.newInstance(
                            AddPlatoActivity.this,
                            now.get(Calendar.HOUR_OF_DAY),
                            now.get(Calendar.MINUTE),
                            true);
                    tpd.show(getFragmentManager(), "Datepickerdialog");
                }
                Log.i("click text", "kakak");
                return false;
            }
        });

        vf1=(ViewFlipper) findViewById(R.id.simpleViewSwitcher);


        vf2=(ViewFlipper) findViewById(R.id.simpleViewSwitcher2);
        txtplato2=(EditText) findViewById(R.id.txtplato2);
        txttipo2=(EditText) findViewById(R.id.txttipo2);
        txtraciones2=(EditText) findViewById(R.id.txtraciones2);
        txtdes2=(EditText) findViewById(R.id.txtdes2);
        txthora2=(EditText) findViewById(R.id.txthora2);
        img1=(ImageView) findViewById(R.id.img1);
        txttipoplato=(TextView) findViewById(R.id.txttipoplato);





        String[] ITEMS = {"Arroz", "Carne", "Ensalada", "Pasta", "Pescado", "Pizza", "Sopa", "Postre"};
        int[] ico={R.drawable.ic_arroz,R.drawable.ic_carne,R.drawable.ic_ensalada,R.drawable.ic_pasta,R.drawable.ic_pescado,R.drawable.ic_pizza,R.drawable.ic_sopa,R.drawable.ic_postre};

        BoomMenuButton bmb=(BoomMenuButton)findViewById(R.id.bmb);
        TextOutsideCircleButton.Builder builder;
        for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++) {
             builder = new TextOutsideCircleButton.Builder()
                    .normalImageRes(ico[i])
                    .normalText(ITEMS[i])
                    .normalTextColor(getResources().getColor(R.color.colorTheText))
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {
                            //Toast.makeText(AddPlatoActivity.this, "Clicked " + index, Toast.LENGTH_SHORT).show();
                            flag++;
                            if(index==0)
                            {

                                txttipo2.setText("Arroz");
                                txttipoplato.setText("Arroz");
                            }else if(index==1)
                            {
                                txttipo2.setText("Carne");
                                txttipoplato.setText("Carne");
                            }
                            else if(index==2)
                            {
                                txttipo2.setText("Ensalada");
                                txttipoplato.setText("Ensalada");
                            }
                            else if(index==3)
                            {
                                txttipo2.setText("Pasta");
                                txttipoplato.setText("Pasta");
                            }
                            else if(index==4)
                            {
                                txttipo2.setText("Pescado");
                                txttipoplato.setText("Pescado");
                            }
                            else if(index==5)
                            {
                                txttipo2.setText("Pizza");
                                txttipoplato.setText("Pizza");
                            }
                            else if(index==6)
                            {
                                txttipo2.setText("Sopa");
                                txttipoplato.setText("Sopa");
                            }
                            else if(index==7)
                            {
                                txttipo2.setText("Postre");
                                txttipoplato.setText("Postre");
                            }


                        }
                    });
            bmb.addBuilder(builder);
        }
        bmb.setDimColor(getResources().getColor(R.color.colorPrimary));
        bmb.setNormalColor(getResources().getColor(R.color.colorPrimary));






        simpleViewSwitcher = (ViewFlipper) findViewById(R.id.simpleViewSwitcher); // get the reference of ViewSwitcher
        // Declare in and out animations and load them using AnimationUtils class
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        // set the animation type to ViewSwitcher
        simpleViewSwitcher.setInAnimation(in);
        simpleViewSwitcher.setOutAnimation(out);


        vf2.setInAnimation(in);
        vf2.setOutAnimation(out);



        // button navigation
        bottomNavigationView = (BottomNavigationViewEx) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setItemHeight(125);
        bottomNavigationView.enableAnimation(false);
        bottomNavigationView.enableItemShiftingMode(false);
        bottomNavigationView.enableShiftingMode(false);
        BottomNavigationShiftHelper.disableShiftMode(bottomNavigationView, getApplicationContext());


        bottomNavigationView.getMenu().getItem(2).setChecked(true);

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
                                intent = new Intent(AddPlatoActivity.this, FavoritoActivity.class);
                                startActivity(intent);
                                finish();
                                break;

                            case R.id.nav_noti:
                                Log.e("value", "config");
                                intent = new Intent(AddPlatoActivity.this, NotificationActivity.class);
                                startActivity(intent);
                                finish();
                                break;

                            case R.id.nav_per:
                                intent = new Intent(AddPlatoActivity.this, ProfileActivity.class);
                                startActivity(intent);
                                finish();
                                Log.e("value", "config");
                                //intent = new Intent(MainActivity.this, PublicationActivity.class);

                                break;

                        }


                        return true;
                    }
                });






        txtsearch=(EditText) findViewById(R.id.txtsearch);

        outPutFile = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");

        app = new AppPreferences(getApplicationContext());

        txtdes.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(pantalla==0) {
                    if (!hasFocus) {
                        //this if condition is true when edittext lost focus...
                        //check here for number is larger than 10 or not
                        if (!txtplato.getText().toString().trim().equals("")) {
                            if (!txtdes.getText().toString().trim().equals("")) {
                                next();
                            }
                        }
                    }
                }


            }
        });


        txtdes.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    if(!txtplato.getText().toString().trim().equals(""))
                    {
                        if(!txtdes.getText().toString().trim().equals(""))
                        {
                            // Ocultar el teclado
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(txtdes.getWindowToken(), 0);

                            next();
                        }
                    }

                    return true;
                }
                return false;
            }
        });



        txtofrezco.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(pantalla==1) {
                        if (!hasFocus) {
                            if (!txtcosto.getText().toString().trim().equals("")) {
                                if (!txtraciones.getText().toString().trim().equals("")) {

                                    if (!txtofrezco.getText().toString().trim().equals("")) {
                                        next();
                                    }
                                }
                            }
                        }
                }
            }
        });


        txtofrezco.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    if(!txtcosto.getText().toString().trim().equals(""))
                    {
                        if(!txtraciones.getText().toString().trim().equals(""))
                        {

                            if(!txtofrezco.getText().toString().trim().equals("")) {

                                // Ocultar el teclado
                                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(txtofrezco.getWindowToken(), 0);

                                    next();

                            }
                        }
                    }

                    return true;
                }
                return false;
            }
        });




        pageIndicatorView = (PageIndicatorView) this.findViewById(R.id.pageIndicatorView);
        //pageIndicatorView.setViewPager(pager);
        pageIndicatorView.setInteractiveAnimation(true);
        pageIndicatorView.setAnimationType(AnimationType.THIN_WORM);

        pageIndicatorView.setCount(4);
        pageIndicatorView.setProgress(0,1);
        pageIndicatorView.setSelectedColor(getResources().getColor(R.color.colorPrimary));
        pageIndicatorView.setUnselectedColor(getResources().getColor(R.color.colorTheText));


        badgeView = new QBadgeView(this);
        NotiTask();



        if(app.getPlato().equals("0"))
        {
            Constants.explicativo(AddPlatoActivity.this,getString(R.string.plato));
            app.setPlato(1);
        }

    }

    public void next()
    {
        pantalla++;
        pageIndicatorView.setProgress(pantalla,1);
        if(pantalla==2)
        {
            changePosition();
        }
        simpleViewSwitcher.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_in));
        simpleViewSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_out));
        simpleViewSwitcher.showNext();
    }

    @Override
    public boolean onTouchEvent(MotionEvent touchevent) {


            switch (touchevent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = touchevent.getX();

                    break;
                case MotionEvent.ACTION_UP:
                    float finalX = touchevent.getX();
                    if (initialX > finalX) {

                            Boolean valor=true;
                        if(simpleViewSwitcher.getDisplayedChild()==0)
                        {
                            if(flag==0)
                            {
                                valor=false;
                            }
                            if(txtplato.getText().toString().equals(""))
                            {
                                valor=false;
                            }
                            if(txtdes.getText().toString().equals(""))
                            {
                                valor=false;
                            }
                        }else if(simpleViewSwitcher.getDisplayedChild()==1)
                        {
                            if(txtcosto.getText().toString().equals(""))
                            {
                                valor=false;
                            }
                            if(txtraciones.getText().toString().equals(""))
                            {
                                valor=false;
                            }
                            if(txtofrezco.getText().toString().equals(""))
                            {
                                valor=false;
                            }
                        }else if(simpleViewSwitcher.getDisplayedChild()==2)
                        {
                            if(txthora.getText().toString().equals(""))
                            {
                                valor=false;
                            }
                            if(txtdia.getText().toString().equals(""))
                            {
                                valor=false;
                            }
                        }
                         if(valor) {
                             simpleViewSwitcher.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_in));
                             simpleViewSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_out));
                             simpleViewSwitcher.showNext();
                             pantalla++;
                             if(pantalla==2)
                             {
                                 changePosition();
                             }
                             if(pantalla>3)
                             {
                                 pantalla=0;
                             }
                             pageIndicatorView.setProgress(pantalla,1);
                         }




                    } else if (initialX < finalX) {
                        if(simpleViewSwitcher.getDisplayedChild()!=0) {
                            simpleViewSwitcher.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_in));
                            simpleViewSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_out));
                            simpleViewSwitcher.showPrevious();
                            pantalla--;
                            pageIndicatorView.setProgress(pantalla,1);
                        }
                    }
                    break;
                case MotionEvent.ACTION_MOVE:

                    break;

            }
            return false;

    }




    public void showFileChooser(View v) {

        PickImageDialog.build(new PickSetup()
                        .setTitle(getResources().getString(R.string.image))
                        .setTitleColor(getResources().getColor(R.color.colorDivider))
                        .setCameraButtonText(getResources().getString(R.string.camera))
                        .setGalleryButtonText(getResources().getString(R.string.sd))
                        .setButtonTextColor(getResources().getColor(R.color.colorDivider))
                        .setBackgroundColor(getResources().getColor(R.color.colorPrimary))
                        .setCancelText(getResources().getString(R.string.cancel))
                        .setCancelTextColor(getResources().getColor(R.color.colorDivider))
                        .setGalleryIcon(R.drawable.ic_perm_media_white_24dp)
                        .setCameraIcon(R.drawable.ic_photo_camera_white_24dp)

        ).show(getSupportFragmentManager());


    }



    public void onPickResult(PickResult r) {
        if (r.getError() == null) {
            //If you want the Uri.
            //Mandatory to refresh image from Uri.
            //getImageView().setImageURI(null);

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                r.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(),  r.getBitmap(), "temp", null);
                performCrop(Uri.parse(path));

        } else {
            //Handle possible errors
            //TODO: do what you have to do with r.getError();
            Toast.makeText(this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == PICK_FROM_FILE) && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            //bitmap = ProcessImage.compressImage(filePath, getApplicationContext(), null);
            //Getting the Bitmap from Gallery
            performCrop(filePath);

        }
        if (requestCode == PICK_FROM_CAMERA && resultCode == RESULT_OK) {

            Uri imageUri = Uri.parse(mCurrentPhotoPath);
            // ScanFile so it will be appeared on Gallery
            MediaScannerConnection.scanFile(AddPlatoActivity.this,
                    new String[]{imageUri.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            performCrop(uri);
                        }
                    });


        }

        if(requestCode==CROP_FROM_CAMERA) {
            try {
                if(outPutFile.exists()){
                    //bitmap = decodeFile(outPutFile);

                    InputStream ims = new FileInputStream(outPutFile);
                    bitmap=BitmapFactory.decodeStream(ims);

                    //imagen.setImageBitmap(bitmap);

                    image = Constants.getStringImage(bitmap);
                    save();
                    //imagen.setVisibility(View.VISIBLE);

                }
                else {
                    Toast.makeText(getApplicationContext(), "Error while save image", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }


    private void performCrop(Uri uri) {

        int x=dpToPx(280);
        int y=dpToPx(280);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", x);
        intent.putExtra("outputY", y);
        //intent.putExtra("scale", true);
        intent.putExtra("noFaceDetection", true);
        //intent.putExtra("return-data", true);
        //Create output file here
        try {
            /*mImageCaptureUri = FileProvider.getUriForFile(AddPlatoActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    createImageFile());*/
            outPutFile =createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }



        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outPutFile));
        startActivityForResult(intent, CROP_FROM_CAMERA);
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }



    public void save()
    {
        /*String error="";
        if(spinner.getSelectedItemPosition()==0)
        {
            error+=getText(R.string.error_tipo).toString()+"\n";
        }
        if(txtplato.getText().toString().equals(""))
        {
            error+=getText(R.string.error_plato).toString()+"\n";
        }
        if(txtdes.getText().toString().equals(""))
        {
            error+=getText(R.string.error_des).toString()+"\n";
        }
        if(txtcosto.getText().toString().equals(""))
        {
            error+=getText(R.string.error_costo).toString()+"\n";
        }
        if(txtraciones.getText().toString().equals(""))
        {
            error+=getText(R.string.error_raciones).toString()+"\n";
        }
        if(txtraciones.getText().toString().equals(""))
        {
            error+=getText(R.string.error_raciones).toString()+"\n";
        }
        if(txtofrezco.getText().toString().equals(""))
        {
            error+=getText(R.string.error_ofrezco).toString()+"\n";
        }
        if(txthora.getText().toString().equals(""))
        {
            error+=getText(R.string.error_hora).toString()+"\n";
        }
        if(txtdia.getText().toString().equals(""))
        {
            error+=getText(R.string.error_dia).toString()+"\n";
        }
        if(image.equals(""))
        {
            error+=getText(R.string.error_imagen).toString()+"\n";
        }


        if(!error.equals(""))
        {
            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
                mensaje = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialog));
            }
            else {
                mensaje = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialog));
            }
            mensaje
                    .setTitle(getText(R.string.error))
                    .setMessage(error)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

            mensaje.show();
            return;
        }*/

        vf1.setVisibility(View.GONE);
        vf2.setVisibility(View.VISIBLE);
        pageIndicatorView.setVisibility(View.GONE);
        txtplato2.setText(txtplato.getText().toString());
        txtraciones2.setText(txtraciones.getText().toString());
        txtdes2.setText(txtdes.getText().toString());
        txthora2.setText(txthora.getText().toString());
        //txtdire2.setText(txtdire.getText().toString());


        plato.setTitle(txtplato.getText().toString());
        plato.setTipo(txttipo2.getText().toString());
        plato.setDesc(txtdes.getText().toString());
        plato.setCosto(txtcosto.getText().toString());
        plato.setDia(txtdia.getText().toString());
        plato.setHora(txthora.getText().toString());
        plato.setOfrezco(txtofrezco.getText().toString());
        plato.setUser(user.getUid().toString());
        plato.setImage(image);
        plato.setLag((float) Double.parseDouble(lat));
        plato.setLog((float) Double.parseDouble(log));



        plato.setOpcional(txtopcional.getText().toString());
        plato.setRaciones(txtraciones.getText().toString());
        img1.setImageBitmap(bitmap);
        item.setVisible(false);


    }

    public void publicar(View v)
    {

        databasePlato = FirebaseDatabase.getInstance().getReference("platos");


        plato.setUsername(getIntent().getExtras().getString("username"));
        plato.setFavorito(0);
        if(getIntent().getExtras().getString("id").equals("0")) {
            //Saving
            String id = databasePlato.push().getKey();
            plato.setId(id);
            plato.setLike(0);
            databasePlato.child(id).setValue(plato);

        }else
        {
                databasePlato.child(getIntent().getExtras().getString("id")).updateChildren(plato.toMap());
                for(int x=0;x<mListFriend.size();x++)
                {

                    String type = "1";
                    if( mListFriend.get(x).getMobile()!=null)
                    {
                        type= mListFriend.get(x).getMobile();
                    }

                    new Constants.PushTask().execute(datauser.getName()+" "+datauser.getLastname(),
                            getString(R.string.add_plato),
                            mListFriend.get(x).getFriendFirebaseCode(),type,"N","");

                    DatabaseReference databasenoti;
                    databasenoti = FirebaseDatabase.getInstance().getReference("notificaciones");


                    String id=databasenoti.push().getKey();
                    databasenoti.child(id).setValue(new Notificacion(user.getUid(),
                            mListFriend.get(x).getFriend(),
                            getString(R.string.add_plato),
                            "0",id,"",0));
                }


        }







        databaseUsers.child(app.getUserId()).child("type").setValue("2");


        pDialog= new SweetAlertDialog(AddPlatoActivity.this, SweetAlertDialog.SUCCESS_TYPE);
        pDialog.setTitleText(getResources().getString(R.string.app_name));
        pDialog.setContentText(getString(R.string.addplato));
        pDialog.setConfirmText(getResources().getString(R.string.ok));
        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismissWithAnimation();
                finish();
            }
        });
        pDialog.show();

    }

    public void favorito(View v)
    {
        databasePlato = FirebaseDatabase.getInstance().getReference("platos");


        plato.setUsername(getIntent().getExtras().getString("username"));
        plato.setFavorito(1);
        if(getIntent().getExtras().getString("id").equals("0")) {
            //Saving
            String id = databasePlato.push().getKey();
            plato.setId(id);
            plato.setLike(0);
            databasePlato.child(id).setValue(plato);
            databaseUsers = FirebaseDatabase.getInstance().getReference("users");
            databaseUsers.child(app.getUserId()).child("type").setValue("2");
        }else
        {
            //databasePlato.child(getIntent().getExtras().getString("id")).setValue(plato);
            databasePlato.child(getIntent().getExtras().getString("id")).updateChildren(plato.toMap());


        }

        pDialog= new SweetAlertDialog(AddPlatoActivity.this, SweetAlertDialog.SUCCESS_TYPE);
        pDialog.setTitleText(getResources().getString(R.string.app_name));
        pDialog.setContentText(getString(R.string.addplato));
        pDialog.setConfirmText(getResources().getString(R.string.ok));
        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismissWithAnimation();
                finish();
            }
        });
        pDialog.show();




    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //--> ARROW BACK
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
    public boolean onCreateOptionsMenu(Menu menu) {
        /* secondary menu*/
        //getMenuInflater().inflate(R.menu.menu_save, menu);
        //item = menu.findItem(R.id.action_save);
        return true;
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear = (++monthOfYear);
        String month = monthOfYear <= 9 ? "0" + monthOfYear : "" + monthOfYear;
        String day = dayOfMonth <= 9 ? "0" + dayOfMonth : "" + dayOfMonth;

        String date = year + "-" + month + "-" + day;
        txtdia.setText(date);

        if(!txthora.getText().toString().trim().equals(""))
        {
                next();
        }
    }


    private void changePosition()
    {


        /*getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );*/

        //mapView.setVisibility(View.VISIBLE);

        // Add a marker in Sydney and move the camera
        LatLng posicion = new LatLng(Double.parseDouble(lat), Double.parseDouble(log));
        map.clear();
        map.addMarker(new MarkerOptions().position(posicion).title("Me"));
        map.moveCamera(CameraUpdateFactory.newLatLng(posicion));

        // Move the camera instantly to Sydney with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(posicion, 15));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomIn());

        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(posicion)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }


    private void eventSearch(final String search){


        //Showing the progress dialog
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progressdialog);
        progressDialog.setCancelable(false);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_GOOGLE+"?address="+search.replaceAll(" ", "%20")+"",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responde) {
                        Log.d(TAG, responde);

                        //Showing toast message of the response

                        JSONObject res= null;
                        try {
                            res = new JSONObject(responde);

                            lat = ((JSONArray)res.get("results")).getJSONObject(0).getJSONObject("geometry")
                                    .getJSONObject("location").get("lat").toString();
                            log = ((JSONArray)res.get("results")).getJSONObject(0).getJSONObject("geometry")
                                    .getJSONObject("location").get("lng").toString();

                            if(lat!=null && log!=null) {
                                changePosition();
                            }else
                            {
                                new AlertDialog.Builder(AddPlatoActivity.this, R.style.AlertDialog)
                                        .setIcon(R.mipmap.ic_launcher)
                                        .setTitle(getString(R.string.error))
                                        .setMessage(getString(R.string.error_location))
                                        .setCancelable(false)
                                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which){

                                            }
                                        })
                                        .show();
                            }

                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        progressDialog.dismiss();

                        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
                            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                            volleyError = error;
                        }

                        //Showing toast
                        Log.d(TAG, volleyError.toString());
                        Toast.makeText(AddPlatoActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();


                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();
                //Adding parameters
                params.put("address", search);
                //returning parameters
                return params;
            }
        };


        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue volleyQueue = Volley.newRequestQueue(this);
        volleyQueue.add(stringRequest);
        DiskBasedCache cache = new DiskBasedCache(getCacheDir(), 500 * 1024 * 1024);
        volleyQueue = new RequestQueue(cache, new BasicNetwork(new HurlStack()));
        volleyQueue.start();




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
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
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

    public void search(View v)
    {
        // Ocultar el teclado
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txtsearch.getWindowToken(), 0);
        if(!txtsearch.getText().toString().equals(getString(R.string.location2))) {
            eventSearch(txtsearch.getText().toString());
        }
    }



    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    public void onStart() {
        super.onStart();

        String id=getIntent().getExtras().getString("id","0");
        if(!id.equals("0") && restart==0)
        {
            restart=1;
            //load like
            databasePlato = FirebaseDatabase.getInstance().getReference("platos");
            databasePlato.keepSynced(true);
            Query likequery = databasePlato
                    .orderByChild("id").equalTo(id);
            final Plato[] mListTemp = {new Plato()};

            likequery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //clearing the previous artist list

                    //iterating through all the nodes
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        //getting artist
                        mListTemp[0] = postSnapshot.getValue(Plato.class);
                        //adding artist to the list
                        //mListLike.add(like);
                    }

                    txtplato.setText(mListTemp[0].getTitle());
                    txtdes.setText(mListTemp[0].getDesc());
                    txtcosto.setText(mListTemp[0].getCosto());
                    txtraciones.setText(mListTemp[0].getRaciones());
                    txtofrezco.setText(mListTemp[0].getOfrezco());
                    txthora.setText(mListTemp[0].getHora());
                    txtdia.setText(mListTemp[0].getDia());
                    txtopcional.setText(mListTemp[0].getOpcional());





                        flag++;
                    if(mListTemp[0].getTipo().equals("Arroz"))
                    {
                        txttipo2.setText("Arroz");
                        txttipoplato.setText("Arroz");

                    }else if(mListTemp[0].getTipo().equals("Carne"))
                    {
                        txttipo2.setText("Carne");
                        txttipoplato.setText("Carne");
                    }else if(mListTemp[0].getTipo().equals("Ensalada"))
                    {
                        txttipo2.setText("Ensalada");
                        txttipoplato.setText("Ensalada");
                    }else if(mListTemp[0].getTipo().equals("Pasta"))
                    {
                        txttipo2.setText("Pasta");
                        txttipoplato.setText("Pasta");
                    }else if(mListTemp[0].getTipo().equals("Pescado"))
                    {
                        txttipo2.setText("Pescado");
                        txttipoplato.setText("Pescado");
                    }
                    else if(mListTemp[0].getTipo().equals("Pizza"))
                    {
                        txttipo2.setText("Pizza");
                        txttipoplato.setText("Pizza");
                    }
                    else if(mListTemp[0].getTipo().equals("Sopa"))
                    {
                        txttipo2.setText("Sopa");
                        txttipoplato.setText("Sopa");
                    }else
                    {
                        txttipo2.setText("Postre");
                        txttipoplato.setText("Postre");
                    }

                    lat=String.valueOf(mListTemp[0].getLag());
                    log=String.valueOf(mListTemp[0].getLog());


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled", databaseError.toException());
                }
            });






        }


        databaseFriend = FirebaseDatabase.getInstance().getReference("friends");
        mListFriend = new ArrayList<Friend>();

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
                    databaseFriend.removeEventListener(this);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });


        Query queryuser = databaseUsers
                .orderByChild("firebaseId").equalTo(user.getUid());


        queryuser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //getting artist


                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    User use = postSnapshot.getValue(User.class);

                   datauser=use;
                    databaseFriend.removeEventListener(this);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });


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

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String time = hourOfDay+"h"+minute;
        txthora.setText(time);
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
