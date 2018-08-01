package app.com.spoonpot.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import app.com.spoonpot.activity.Tab1;
import app.com.spoonpot.activity.Tab2;
import app.com.spoonpot.activity.Tab3;

/**
 * Created by Belal on 2/3/2016.
 */
//Extending FragmentStatePagerAdapter
public class PagerAdapter extends FragmentStatePagerAdapter {

    //integer to count number of tabs
    int tabCount;
    private int ITEM_ONE=1;


    //Constructor to the class
    public PagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        //Initializing tab count
        this.tabCount= tabCount;
    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        switch (position) {
            case 0:
                Tab1 tab1 = new Tab1();
                return tab1;
            /*case 1:
                Tab2 tab2 = new Tab2();
                return tab2;*/
            case 1:
                Tab3 tab3 = new Tab3();
                return tab3;
            default:
                return null;
        }
    }

    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return tabCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Usuarios1";
            case 1:
                return "Usuarios2";
            /*case 2:
                return "Usuarios2";*/
            default:
                return null;
        }
    }
}