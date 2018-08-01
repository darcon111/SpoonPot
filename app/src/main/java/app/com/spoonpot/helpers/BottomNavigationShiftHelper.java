package app.com.spoonpot.helpers;

import android.content.Context;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;



import java.lang.reflect.Field;

import app.com.spoonpot.clases.Screen;

/**
 * Created by USUARIO-PC on 24/02/2017.
 */

public class BottomNavigationShiftHelper {

    private final static String TAG = "DEBUG_BOTTOM_NAV_UTIL";

    public static void disableShiftMode(BottomNavigationView view, Context context) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);


        int screen=1;
        switch (Screen.Size(context) ){

            case eUNDEFINED:
                screen=1;
                break;

            case eXLarge:
                screen=3; //ok
                break;

            case eLarge:
                screen=2; //ok
                break;

            case eNormal:
                screen=1; //ok

            case eSmall:
                screen=1; //ok
                break;
        }

        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                //item.setChecked(item.getItemData().isChecked());

                /* para poner los iconos mas grandes*/

                final View iconView = menuView.getChildAt(i).findViewById(android.support.design.R.id.icon);
                final ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();


                final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
                //if(screen==1) {
                    layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, displayMetrics);
                    layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, displayMetrics);
               /* }else if(screen==2)
                {
                    layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, displayMetrics);
                    layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, displayMetrics);
                }else
                {
                    layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, displayMetrics);
                    layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, displayMetrics);
                }*/


               iconView.setLayoutParams(layoutParams);

            }





        } catch (NoSuchFieldException e) {
            Log.d(TAG, "Unable to get shift mode field");
        } catch (IllegalAccessException e) {
            Log.d(TAG, "Unable to change value of shift mode");
        }
    }
}
