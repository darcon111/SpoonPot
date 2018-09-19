package app.com.spoonpot.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import app.com.spoonpot.R;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MyClusterRenderer extends DefaultClusterRenderer<MyItem> {

    private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());

    public MyClusterRenderer(Context context, GoogleMap map,
                             ClusterManager<MyItem> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(MyItem item,
                                               MarkerOptions markerOptions) {


        markerOptions.icon(item.getMarkerDescriptor());
    }

    @Override
    protected void onClusterItemRendered(MyItem clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<MyItem> cluster, MarkerOptions markerOptions){

        final Drawable clusterIcon = getApplicationContext().getResources().getDrawable(R.drawable.ic_cocinero1);
        clusterIcon.setColorFilter(getApplicationContext().getResources().getColor(android.R.color.holo_orange_light), PorterDuff.Mode.SRC_ATOP);

        mClusterIconGenerator.setBackground(clusterIcon);

        //modify padding for one or two digit numbers
        if (cluster.getSize() < 10) {
            mClusterIconGenerator.setContentPadding(40, 20, 0, 0);
        }
        else {
            mClusterIconGenerator.setContentPadding(30, 20, 0, 0);
        }

        Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }
}
