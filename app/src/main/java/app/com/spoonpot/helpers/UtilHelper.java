package app.com.spoonpot.helpers;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.TextView;

import app.com.spoonpot.R;

public class UtilHelper {

    public static int dpToPx(Resources res, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
    }

    public static boolean isTooLarge(TextView text, String newText) {

        float textWidth = text.getPaint().measureText(newText);
        return (textWidth >= text.getMeasuredWidth());
    }

    public static int convertDpToPixel(Context context,float dp){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return Math.round(px);
    }

    public static int getScreenDensity(Context context) {
        int response = 0;
        int density = context.getResources().getDisplayMetrics().densityDpi;
        switch (density) {
            case DisplayMetrics.DENSITY_LOW:
                response = 36;
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                response = 48;
                break;
            case DisplayMetrics.DENSITY_HIGH:
                response = 72;
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                response = 96;
                break;
            default:
                response = 96;
        }
        return response;
    }

    public static boolean hasInternet(Context context) {
        String service = Context.CONNECTIVITY_SERVICE;
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(service);
        return connectivity.getActiveNetworkInfo() != null
                && connectivity.getActiveNetworkInfo().isAvailable()
                && connectivity.getActiveNetworkInfo().isConnected();
    }

    public static boolean isMyServiceRunning(Context context, String serviceName) {
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service
                    .getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean locationPovidersAvailable(Context context) {
        boolean gpsEnabled = false, netEnabled = false;
        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            gpsEnabled = true;
        }
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            netEnabled = true;
        }
        if (gpsEnabled || netEnabled) {
            return true;
        }
        return false;

    }


    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

    public static int getTabsHeight(Context context) {
        return (int) context.getResources().getDimension(R.dimen.tabsHeight);
    }

}
