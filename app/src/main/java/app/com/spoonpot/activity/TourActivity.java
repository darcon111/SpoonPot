package app.com.spoonpot.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.rd.PageIndicatorView;
import com.rd.animation.type.AnimationType;

import app.com.spoonpot.R;
import app.com.spoonpot.adapter.TourFragmentPagerAdapter;
import app.com.spoonpot.config.AppPreferences;


public class TourActivity extends FragmentActivity {
    private AppPreferences app;

    /**
     * The pager widget, which handles animation and allows swiping horizontally
     * to access previous and next pages.
     */
    ViewPager pager = null;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    TourFragmentPagerAdapter pagerAdapter;



    @Override
    protected void onCreate(Bundle arg0) {

        super.onCreate(arg0);
        this.setContentView(R.layout.activity_tour);

        // Instantiate a ViewPager
        this.pager = (ViewPager) this.findViewById(R.id.pager);

        app = new AppPreferences(getApplicationContext());

        // Create an adapter with the fragments we show on the ViewPager
        TourFragmentPagerAdapter adapter = new TourFragmentPagerAdapter(
                getSupportFragmentManager());
        adapter.addFragment(ScreenSlidePageFragment.newInstance(R.drawable.tour1,R.drawable.circulos1,R.drawable.close,getString(R.string.text1),1));
        adapter.addFragment(ScreenSlidePageFragment.newInstance(R.drawable.tour2,R.drawable.circulos2,R.drawable.close,getString(R.string.text2),2));
        adapter.addFragment(ScreenSlidePageFragment.newInstance(R.drawable.tour3,R.drawable.circulos3,R.drawable.close,getString(R.string.text3),3));
        adapter.addFragment(ScreenSlidePageFragment.newInstance(R.drawable.tour4,R.drawable.circulos4,R.drawable.close,getString(R.string.text4),4));
        adapter.addFragment(ScreenSlidePageFragment.newInstance(R.drawable.tour5,R.drawable.circulos5,R.drawable.close,getString(R.string.text5),5));


        this.pager.setAdapter(adapter);

        PageIndicatorView pageIndicatorView = (PageIndicatorView) this.findViewById(R.id.pageIndicatorView);
        pageIndicatorView.setViewPager(pager);

        pageIndicatorView.setInteractiveAnimation(true);
        pageIndicatorView.setAnimationType(AnimationType.THIN_WORM);


    }

    @Override
    public void onBackPressed() {

        // Return to previous page when we press back button
        if (this.pager.getCurrentItem() == 0)
            super.onBackPressed();
        else
            this.pager.setCurrentItem(this.pager.getCurrentItem() - 1);

    }

    public void close(View v)
    {

        app.setTour("1");
        Intent mainIntent = new Intent().setClass(
                TourActivity.this, LoginActivity.class);
        startActivity(mainIntent);

        // Close the activity so the user won't able to go back this
        // activity pressing Back button
        finish();
    }



}
