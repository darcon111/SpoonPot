package app.com.spoonpot.clases;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

import java.util.Random;

import app.com.spoonpot.R;

/**
 * Created by USUARIO-PC on 07/11/2017.
 */

public class Screen {


    public enum type_size {
        eUNDEFINED,
        eXLarge,
        eLarge,
        eNormal,
        eSmall
    }
    //----------------------------------

    public static type_size Size( Context context ){
        type_size vl_return = type_size.eUNDEFINED;
        if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_UNDEFINED) {

            vl_return = type_size.eUNDEFINED;
        }
        else if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {

            vl_return = type_size.eXLarge;
        }
        else if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            vl_return = type_size.eLarge;
        }
        else if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {

            vl_return = type_size.eNormal;
        }
        else if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {

            vl_return = type_size.eSmall;
        }

        return vl_return;
    }



    public static String Densidad(Activity vin_activity){
        String vl_return = "";
        int dips = 200;
        float pixelBoton = 0;
        float scaleDensity = 0;

        DisplayMetrics metrics = new DisplayMetrics();
        vin_activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        float scale = vin_activity.getResources().getDisplayMetrics().density;
        int vl_scala = 0;

        switch(metrics.densityDpi) {
            case DisplayMetrics.DENSITY_XXXHIGH: //XXXDPI -> 4
                scaleDensity = scale * 640;
                pixelBoton = dips * (scaleDensity / 640);
                break;

            case DisplayMetrics.DENSITY_XXHIGH: //XXDPI -> 3
                vl_return = "480 Densidad";
                scaleDensity = scale * 480;
                pixelBoton = dips * (scaleDensity / 480);
                break;

            case DisplayMetrics.DENSITY_XHIGH: //XDPI -> 2
                vl_return = "320 Densidad";
                scaleDensity = scale * 320;
                pixelBoton = dips * (scaleDensity / 320);
                break;

            case DisplayMetrics.DENSITY_HIGH: //HDPI -> 1.5
                vl_return = "240 Alta Densidad";
                scaleDensity = scale * 240;
                pixelBoton = dips * (scaleDensity / 240);
                break;

            case DisplayMetrics.DENSITY_MEDIUM: //MDPI -> 1
                vl_return = "160 Mediana Densidad";
                scaleDensity = scale * 160;
                pixelBoton = dips * (scaleDensity / 160);
                break;

            case DisplayMetrics.DENSITY_LOW:  //LDPI -> 0.75
                vl_return = "120 Baja Densidad";
                scaleDensity = scale * 120;
                pixelBoton = dips * (scaleDensity / 120);
                break;
        }
        return vl_return;
    }



    public static void transitions( Activity vin_activity, int vin_effect ){
        try{
            Random rnd = new Random();
            int vl_random_number = vin_effect;

            while ( vl_random_number <= 0 || vl_random_number > 5 || vin_effect > 0 ){
                if ( vin_effect == 0 ){
                    vl_random_number = (int) (rnd.nextDouble() * 5);
                }

                if ( vl_random_number == 5 ){
                    vin_activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    vin_effect = 0;

                }else if ( vl_random_number == 4 ){
                    vin_activity.overridePendingTransition(R.anim.zoom_forward_in, R.anim.zoom_forward_out);
                    vin_effect = 0;

                }else if ( vl_random_number == 3 ){
                    vin_activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    vin_effect = 0;

                }else if ( vl_random_number == 2 ){
                    vin_activity.overridePendingTransition(R.anim.zoom_back_in, R.anim.zoom_back_out);
                    vin_effect = 0;

                }else if ( vl_random_number == 1 ){
                    vin_activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    vin_effect = 0;
                }
            };
        } catch (Exception e){ }
    }

}
