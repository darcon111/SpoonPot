package app.com.spoonpot.activity;

/**
 * Created by darioalarcon on 13/5/17.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import app.com.spoonpot.R;

public class ChefSlidePageFragment extends Fragment {


    /**
     * Key to insert the background color into the mapping of a Bundle.
     */
    private static final String BACKGROUND_IMAGEN = "IMAGEN1";
    private static final String BACKGROUND_IMAGEN2 = "IMAGEN2";
    private static final String texto = "texto";
    private static final String texto2 = "texto2";

    /**
     * Key to insert the index page into the mapping of a Bundle.
     */
    private static final String INDEX = "index";

    private int imagen1;
    private int imagen2;
    private String text;
    private String text2;
    private int index;

    /**
     * Instances a new fragment with a background color and an index page.
     *
     * @param imagen1
     *            background color
     * @param index
     *            index page
     * @return a new page
     */
    public static ChefSlidePageFragment newInstance(int img1, int img2, String text, String text2, int index) {

        // Instantiate a new fragment
        ChefSlidePageFragment fragment = new ChefSlidePageFragment();

        // Save the parameters
        Bundle bundle = new Bundle();
        bundle.putInt(BACKGROUND_IMAGEN, img1);
        bundle.putInt(BACKGROUND_IMAGEN2, img2);
        bundle.putString(texto, text);
        bundle.putString(texto2, text2);
        bundle.putInt(INDEX, index);

        fragment.setArguments(bundle);
        fragment.setRetainInstance(true);

        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Load parameters when the initial creation of the fragment is done
        this.imagen1 = (getArguments() != null) ? getArguments().getInt(
                BACKGROUND_IMAGEN) : R.drawable.tour1;

        this.imagen2 = (getArguments() != null) ? getArguments().getInt(
                BACKGROUND_IMAGEN2) : R.drawable.tour1;


        this.text = (getArguments() != null) ? getArguments().getString(
                texto) : getString(R.string.app_name);

        this.text2 = (getArguments() != null) ? getArguments().getString(
                texto2) : getString(R.string.app_name);

        this.index = (getArguments() != null) ? getArguments().getInt(INDEX)
                : -1;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_chef_slide_page, container, false);


        ImageView image1=(ImageView) rootView.findViewById(R.id.imagen1);
        image1.setBackground(getResources().getDrawable(this.imagen1));

        ImageView image2=(ImageView) rootView.findViewById(R.id.imagen2);
        image2.setBackground(getResources().getDrawable(this.imagen2));


        TextView txt=(TextView) rootView.findViewById(R.id.txt);
        txt.setText(this.text);

        TextView txt2=(TextView) rootView.findViewById(R.id.txt2);
        txt2.setText(this.text2);





        return rootView;

    }
}
