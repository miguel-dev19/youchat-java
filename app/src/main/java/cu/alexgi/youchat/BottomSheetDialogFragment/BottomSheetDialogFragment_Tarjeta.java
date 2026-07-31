package cu.alexgi.youchat.BottomSheetDialogFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vanniktech.emoji.EmojiEditText;

import cu.alexgi.youchat.ChatsActivity;
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.adapters.AdaptadorColorTarjeta;

public class BottomSheetDialogFragment_Tarjeta extends BottomSheetDialogFragment {

    static ChatsActivity chatsActivity;

    private int colorTarjeta;
    private EmojiEditText texto_tarjeta;
    private MaterialCardView fondo_tarjeta;
    private FloatingActionButton fab_enviar_tarjeta;

    private RecyclerView color_tarjeta;
    private AdaptadorColorTarjeta adaptadorColorTarjeta;
    private LinearLayoutManager linearLayoutManager;


    public static BottomSheetDialogFragment_Tarjeta newInstance(ChatsActivity ca) {
        chatsActivity = ca;
        return new BottomSheetDialogFragment_Tarjeta();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_dialog_tarjeta, container, false);

        color_tarjeta = v.findViewById(R.id.color_tarjeta);
        fondo_tarjeta = v.findViewById(R.id.fondo_tarjeta);
        texto_tarjeta = v.findViewById(R.id.texto_tarjeta);
        fab_enviar_tarjeta=v.findViewById(R.id.fab_enviar_tarjeta);

        colorTarjeta=4;
        fondo_tarjeta.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.card5));

        //datos_Contacto = new ArrayList<>();


        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        color_tarjeta.setLayoutManager(linearLayoutManager);
        adaptadorColorTarjeta= new AdaptadorColorTarjeta(getActivity());
        adaptadorColorTarjeta.setOnColorPickerClickListener(new AdaptadorColorTarjeta.OnColorPickerClickListener() {
            @Override
            public void onColorPickerClickListener(int colorCode, int pos) {
                colorTarjeta=pos;
                fondo_tarjeta.setCardBackgroundColor(colorCode);
                /*dismiss();
                mProperties.onColorChanged(colorCode);*/
            }
        });
        color_tarjeta.setAdapter(adaptadorColorTarjeta);

        fab_enviar_tarjeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!texto_tarjeta.getText().toString().trim().equals("")){
                    dismiss();
                    chatsActivity.enviarTarjeta(texto_tarjeta.getText().toString().trim(), colorTarjeta);
                }
            }
        });
        return v;
    }
}
