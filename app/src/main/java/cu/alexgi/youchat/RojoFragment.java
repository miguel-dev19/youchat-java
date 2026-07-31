package cu.alexgi.youchat;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;


public class RojoFragment extends Fragment {
    View Frag, fondo;
    LottieAnimationView anima;
    //ImageView img;
    Animation anim;
    static int pos;
    TextView title, text_pie;

    private OnFragmentInteractionListener mListener;

    public RojoFragment() {
        // Required empty public constructor
    }

    public static RojoFragment newInstance(int page) {
        RojoFragment fragment = new RojoFragment();
        pos = page;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Frag=inflater.inflate(R.layout.fragment_rojo, container, false);

        anim= AnimationUtils.loadAnimation(getContext(),R.anim.viewpager_secure);
        anima=Frag.findViewById(R.id.lottie_rojo);
        anima.playAnimation();

        title=Frag.findViewById(R.id.title);
        text_pie=Frag.findViewById(R.id.text_pie);
        fondo=Frag.findViewById(R.id.fondo);


        Utils.runOnUIThread(()->{
            switch (pos){
                case 1:
//                    fondo.setBackgroundColor(Color.parseColor("#f44336"));
                    anima.setAnimation(R.raw.new_login);
                    title.setText("Seguridad");
                    text_pie.setText(getResources().getString(R.string.viewpager_rojo));
                    break;
                case 2:

//                    fondo.setBackgroundColor(Color.parseColor("#4caf50"));
                    anima.setAnimation(R.raw.aasweeping_floor);
                    title.setText("Moderno");
                    text_pie.setText(getResources().getString(R.string.viewpager_verde));
                    break;
                case 3:
//                    fondo.setBackgroundColor(Color.parseColor("#ffc107"));
                    anima.setAnimation(R.raw.aafast_chat);
                    title.setText("Rapidez");
                    text_pie.setText(getResources().getString(R.string.viewpager_amarillo));
                    break;
            }
        });


        return Frag;
    }

    public void hacerAnim(){
        anim= AnimationUtils.loadAnimation(getContext(),R.anim.viewpager_secure);
        anima=Frag.findViewById(R.id.lottie_rojo);
        anima.playAnimation();

        /*img=Frag.findViewById(R.id.img_rojo);
        img.startAnimation(anim);*/
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        anima.removeAllAnimatorListeners();
        anima.pauseAnimation();
        anima=null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
