package app.com.spoonpot.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import app.com.spoonpot.R;
import app.com.spoonpot.adapter.TourFragmentPagerAdapter;
import app.com.spoonpot.config.AppPreferences;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ChefActivity extends AppCompatActivity {

    /**
     * The pager widget, which handles animation and allows swiping horizontally
     * to access previous and next pages.
     */
    ViewPager pager = null;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    TourFragmentPagerAdapter pagerAdapter;
    private AppPreferences app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/RobotoLight.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef);

        // Instantiate a ViewPager
        this.pager = (ViewPager) this.findViewById(R.id.pager);

        app = new AppPreferences(getApplicationContext());

        // Create an adapter with the fragments we show on the ViewPager
        TourFragmentPagerAdapter adapter = new TourFragmentPagerAdapter(
                getSupportFragmentManager());
        adapter.addFragment(ChefSlidePageFragment.newInstance(R.drawable.ic_chef1,R.drawable.ic_circuloschef1,getString(R.string.chef2),getString(R.string.chef3),1));
        adapter.addFragment(ChefSlidePageFragment.newInstance(R.drawable.ic_chef2,R.drawable.ic_circuloschef2,getString(R.string.chef4),getString(R.string.chef5),1));
        adapter.addFragment(ChefSlidePageFragment.newInstance(R.drawable.ic_chef3,R.drawable.ic_circuloschef3,getString(R.string.chef6),getString(R.string.chef7),1));
        adapter.addFragment(ChefSlidePageFragment.newInstance(R.drawable.ic_chef4,R.drawable.ic_circuloschef4,getString(R.string.chef8),getString(R.string.chef9),1));



        this.pager.setAdapter(adapter);

            /* toolbar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbaruser);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

    }

    @Override
    public void onBackPressed() {

        // Return to previous page when we press back button
        if (this.pager.getCurrentItem() == 0)
            super.onBackPressed();
        else
            this.pager.setCurrentItem(this.pager.getCurrentItem() - 1);

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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


}
