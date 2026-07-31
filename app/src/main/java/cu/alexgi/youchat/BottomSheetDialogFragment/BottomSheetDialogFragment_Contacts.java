package cu.alexgi.youchat.BottomSheetDialogFragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.vanniktech.emoji.EmojiEditText;

import java.util.ArrayList;

import cu.alexgi.youchat.ChatsActivity;
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.YouChatApplication;
import cu.alexgi.youchat.adapters.AdaptadorDatosContactoEnviar;
import cu.alexgi.youchat.items.ItemContacto;

import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.dbWorker;

public class BottomSheetDialogFragment_Contacts extends BottomSheetDialogFragment {

    boolean ordenarXNombre;
    static ChatsActivity chatsActivity;

    private View cancel_buscar_contacto_chat;
    private EmojiEditText buscar_contacto_chat;

    private RecyclerView lista_contactos;
    private AdaptadorDatosContactoEnviar mAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<ItemContacto> datos_Contacto;
    private TextView list_empty;

    public static BottomSheetDialogFragment_Contacts newInstance(ChatsActivity ca) {
        chatsActivity = ca;
        return new BottomSheetDialogFragment_Contacts();
    }

    /*@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        ((BottomSheetDialog)dialog).getBehavior().addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                Log.e("BSDF Contact","-----------STATE-------------------------- "+newState);
                if(newState==BottomSheetBehavior.STATE_EXPANDED){
                    MaterialShapeDrawable newMaterialShapeDrawable = createMaterialShapeDrawable(view);
                    ViewCompat.setBackground(view,newMaterialShapeDrawable);
                }

            }

            @Override
            public void onSlide(@NonNull View view, float v) {
//                Log.e("BSDF Contact","------------------------------------- "+v);
            }
        });

        return dialog;
    }*/

    /*private MaterialShapeDrawable createMaterialShapeDrawable(View view) {
        ShapeAppearanceModel shapeAppearanceModel = ShapeAppearanceModel.builder(getContext(),0,R.style.CustomShapeAppearanceBottomSheetDialog).build();
        MaterialShapeDrawable CMSD = (MaterialShapeDrawable) view.getBackground();
        MaterialShapeDrawable NMSD = new MaterialShapeDrawable((shapeAppearanceModel));

        NMSD.initializeElevationOverlay(getContext());
        NMSD.setFillColor(CMSD.getFillColor());
        NMSD.setTintList(CMSD.getTintList());
        NMSD.setElevation(CMSD.getElevation());
        NMSD.setStrokeWidth(CMSD.getStrokeWidth());
        NMSD.setStrokeColor(CMSD.getStrokeColor());

        return NMSD;
    }*/


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_dialog_contacts, container, false);

        list_empty=v.findViewById(R.id.list_empty);
        buscar_contacto_chat=v.findViewById(R.id.buscar_contacto_chat);
        cancel_buscar_contacto_chat=v.findViewById(R.id.cancel_buscar_contacto_chat);
        cancel_buscar_contacto_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text=buscar_contacto_chat.getText().toString();
                if(text.equals(""))
                    dismiss();
                else {
                    lista_contactos.setVisibility(View.VISIBLE);
                    list_empty.setVisibility(View.GONE);

                    buscar_contacto_chat.setText("");
                    mAdapter = new AdaptadorDatosContactoEnviar(datos_Contacto);
                    lista_contactos.setAdapter(mAdapter);
                    mAdapter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dismiss();
                            ItemContacto contacto = datos_Contacto.get(lista_contactos.getChildAdapterPosition(v));
                            chatsActivity.enviarContacto(contacto);
                        }
                    });
                }
            }
        });
        lista_contactos=v.findViewById(R.id.lista_contactos);
        datos_Contacto = new ArrayList<>();

        ordenarXNombre= YouChatApplication.orden_contacto_nombre;
        linearLayoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
        //lista_contactos.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
        lista_contactos.setLayoutManager(linearLayoutManager);
        datos_Contacto = dbWorker.obtenerContactosOrdenadosXNombre(ordenarXNombre);
        mAdapter = new AdaptadorDatosContactoEnviar(datos_Contacto);
        lista_contactos.setAdapter(mAdapter);

        if(datos_Contacto.size()==0){
            buscar_contacto_chat.setEnabled(false);
            //mostrar cartel de que no existe ningun contacto
            lista_contactos.setVisibility(View.GONE);
            list_empty.setVisibility(View.VISIBLE);
        }

        mAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                ItemContacto contacto = datos_Contacto.get(lista_contactos.getChildAdapterPosition(v));
                chatsActivity.enviarContacto(contacto);
            }
        });

        buscar_contacto_chat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String espacio = s.toString();
                if(espacio.replace(" ","").length()>0){
                    Buscar(espacio);
                }
                else
                {
                    if(espacio.length()>0) buscar_contacto_chat.setText("");
                    mAdapter = new AdaptadorDatosContactoEnviar(datos_Contacto);
                    lista_contactos.setAdapter(mAdapter);
                    mAdapter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dismiss();
                            ItemContacto contacto = datos_Contacto.get(lista_contactos.getChildAdapterPosition(v));
                            chatsActivity.enviarContacto(contacto);
                        }
                    });
                }
            }
        });
        return v;
    }

    void Buscar(String s){
        final ArrayList<ItemContacto> datos_Contacto_buscar = new ArrayList<>();
        for(int i=0; i<datos_Contacto.size(); i++){
            if(datos_Contacto.get(i).getAlias().toLowerCase().contains(s.toLowerCase())
                    || datos_Contacto.get(i).getNombre_personal().toLowerCase().contains(s.toLowerCase())
                    || datos_Contacto.get(i).getCorreo().toLowerCase().contains(s.toLowerCase()))
                datos_Contacto_buscar.add(datos_Contacto.get(i));
        }
        if(datos_Contacto_buscar.size()==0){
            //mostrar cartel de que no existe ningun contacto
            lista_contactos.setVisibility(View.GONE);
            list_empty.setVisibility(View.VISIBLE);
        }
        else {
            lista_contactos.setVisibility(View.VISIBLE);
            list_empty.setVisibility(View.GONE);
            mAdapter = new AdaptadorDatosContactoEnviar(datos_Contacto_buscar);
            lista_contactos.setAdapter(mAdapter);
            mAdapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    ItemContacto contacto = datos_Contacto_buscar.get(lista_contactos.getChildAdapterPosition(v));
                    chatsActivity.enviarContacto(contacto);
                }
            });
        }
    }
}
