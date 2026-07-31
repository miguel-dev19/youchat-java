package cu.alexgi.youchat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;

public class AzulFragment extends Fragment {
    View Frag;
    View finalizar;
    ImageView img;
    Animation anim;
    LottieAnimationView anima;

    private OnFragmentInteractionListener mListener;
    public AzulFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Frag=inflater.inflate(R.layout.fragment_azul, container, false);
        finalizar=Frag.findViewById(R.id.finalizar);
        hacerAnim();

        finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YouChatApplication.setMark(1);
                startActivity(new Intent(getContext(),LoginActivity.class));
                getActivity().finish();
            }
        });

        return Frag;
    }

    public void hacerAnim(){
        anima=Frag.findViewById(R.id.lottie_azul);
        anima.playAnimation();
        /*img=Frag.findViewById(R.id.img_azul);
        anim= AnimationUtils.loadAnimation(getContext(),R.anim.viewpager_next);
        img.startAnimation(anim);*/
    }

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
        anima.pauseAnimation();
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
