package cu.alexgi.youchat.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;

import cu.alexgi.youchat.PublicarPostFragment;
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.Utils;

public class AdaptadorDatosStickerPubPost extends RecyclerView.Adapter<AdaptadorDatosStickerPubPost.ViewHolderDatos> {

    ArrayList<Integer> listaDatos;
    private PublicarPostFragment publicarPostFragment;

    public AdaptadorDatosStickerPubPost(PublicarPostFragment net) {
        publicarPostFragment = net;
        listaDatos = new ArrayList<>();
        for(int i=0; i<19; i++)
            listaDatos.add(i);
    }

    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_sticker_post,null, false);
        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
        holder.AsignarDatos(listaDatos.get(position));
        setAnimation(holder.itemView);
    }

    @Override
    public int getItemCount() {
        return listaDatos.size();
    }

    private void setAnimation(View v){
        Animation anim= AnimationUtils.loadAnimation(v.getContext(),R.anim.fade_in);
        v.startAnimation(anim);
    }


    public class ViewHolderDatos extends RecyclerView.ViewHolder {

        private LottieAnimationView stickerAnimation;

        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            stickerAnimation = itemView.findViewById(R.id.stickerAnimation);
        }

        public void AsignarDatos(int pos){
//            switch (pos){
//                case 0: stickerAnimation.setAnimation(R.raw.like1); break;
//                case 1: stickerAnimation.setAnimation(R.raw.encanta); break;
//                case 2: stickerAnimation.setAnimation(R.raw.sonroja); break;
//                case 3: stickerAnimation.setAnimation(R.raw.divierte); break;
//                case 4: stickerAnimation.setAnimation(R.raw.asombra); break;
//                case 5: stickerAnimation.setAnimation(R.raw.entristece); break;
//                case 6: stickerAnimation.setAnimation(R.raw.enoja); break;
//                case 7: stickerAnimation.setAnimation(R.raw.wallet_congrats); break;
//                case 8: stickerAnimation.setAnimation(R.raw.wallet_science); break;
//                case 9: stickerAnimation.setAnimation(R.raw.tsv_setup_intro); break;
//                case 10: stickerAnimation.setAnimation(R.raw.new_typing_in_the_office); break;
//                case 11: stickerAnimation.setAnimation(R.raw.night_good); break;
//                case 12: stickerAnimation.setAnimation(R.raw.halloweenkin6); break;
//                case 13: stickerAnimation.setAnimation(R.raw.day_women); break;
//                case 14: stickerAnimation.setAnimation(R.raw.wallet_allset); break;
//                case 15: stickerAnimation.setAnimation(R.raw.tsv_setup_mail); break;
//                case 16: stickerAnimation.setAnimation(R.raw.gift); break;
//                case 17: stickerAnimation.setAnimation(R.raw.tsv_setup_email_sent); break;
//                case 18: stickerAnimation.setAnimation(R.raw.tsv_setup_hint); break;
//                default: stickerAnimation.setAnimation(R.raw.like1);
//            }

            stickerAnimation.setAnimation(Utils.obtenerRawActualDePos(pos));
            stickerAnimation.playAnimation();

            stickerAnimation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    publicarPostFragment.ponerSticker(pos);
                }
            });

            stickerAnimation.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    stickerAnimation.playAnimation();
                    return true;
                }
            });
        }
    }
}
