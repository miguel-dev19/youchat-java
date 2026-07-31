package cu.alexgi.youchat.BottomSheetDialogFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.vanniktech.emoji.EmojiTextView;

import cu.alexgi.youchat.ChatsActivity;
import cu.alexgi.youchat.CircleImageView;
import cu.alexgi.youchat.R;
import cu.alexgi.youchat.Utils;
import cu.alexgi.youchat.items.ItemContacto;

import static android.app.Activity.RESULT_OK;
import static cu.alexgi.youchat.MainActivity.dbWorker;

public class BottomSheetDialogFragment_info_contact extends BottomSheetDialogFragment {

    private static Context context;

    private static ChatsActivity chatsActivity;
    private static String correoContacto;
    private static String nombreContacto;

    private String rutaImgContacto;
    private String aliasContacto;
    private String informacionContacto;
    private String telefonoContacto;
    private String generoContacto;
    private String fechaNacContacto;
    private String provinciaContacto;

    private CircleImageView info_contacto_imagen;
    private EmojiTextView info_contacto_alias, info_contacto_informacion, info_contacto_nombre;
    private TextView info_contacto_correo, info_contacto_telefono, info_contacto_genero;
    private TextView info_contacto_fecha_nacimiento, info_contacto_provincia;

    private View info_contacto_ll_informacion, info_contacto_ll_telefono, info_contacto_ll_genero;
    private View info_contacto_ll_fecha_nacimiento, info_contacto_ll_provincia, info_contacto_ll_alias;

    private View info_contacto_btn_agregar, info_contacto_btn_ir_chat, info_contacto_btn_seguir;

    public static BottomSheetDialogFragment_info_contact newInstance(Context c, String nc, String cc, ChatsActivity ca) {
        nombreContacto = nc;
        correoContacto = cc;
        context = c;
        chatsActivity = ca;
        return new BottomSheetDialogFragment_info_contact();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_dialog_info_contact, container, false);

        aliasContacto=rutaImgContacto=informacionContacto=telefonoContacto=generoContacto=fechaNacContacto=provinciaContacto="";

        info_contacto_imagen = v.findViewById(R.id.info_contacto_imagen);
        info_contacto_nombre = v.findViewById(R.id.info_contacto_nombre);
        info_contacto_correo = v.findViewById(R.id.info_contacto_correo);

        info_contacto_alias = v.findViewById(R.id.info_contacto_alias);
        info_contacto_informacion = v.findViewById(R.id.info_contacto_informacion);
        info_contacto_telefono = v.findViewById(R.id.info_contacto_telefono);
        info_contacto_genero = v.findViewById(R.id.info_contacto_genero);
        info_contacto_fecha_nacimiento = v.findViewById(R.id.info_contacto_fecha_nacimiento);
        info_contacto_provincia = v.findViewById(R.id.info_contacto_provincia);

        info_contacto_ll_alias = v.findViewById(R.id.info_contacto_ll_alias);
        info_contacto_ll_informacion = v.findViewById(R.id.info_contacto_ll_informacion);
        info_contacto_ll_telefono = v.findViewById(R.id.info_contacto_ll_telefono);
        info_contacto_ll_genero = v.findViewById(R.id.info_contacto_ll_genero);
        info_contacto_ll_fecha_nacimiento = v.findViewById(R.id.info_contacto_ll_fecha_nacimiento);
        info_contacto_ll_provincia = v.findViewById(R.id.info_contacto_ll_provincia);

        info_contacto_btn_agregar = v.findViewById(R.id.info_contacto_btn_agregar);
        info_contacto_btn_ir_chat = v.findViewById(R.id.info_contacto_btn_ir_chat);
        info_contacto_btn_seguir = v.findViewById(R.id.info_contacto_btn_seguir);

        ItemContacto contacto = dbWorker.obtenerContacto(correoContacto);
        if(contacto!=null){
            info_contacto_btn_agregar.setVisibility(View.GONE);
            nombreContacto = contacto.getNombreMostrar();

            rutaImgContacto = contacto.getRuta_img();
            aliasContacto = contacto.getAlias();
            informacionContacto = contacto.getInfo();
            telefonoContacto = contacto.getTelefono();
            generoContacto = contacto.getGenero();
            fechaNacContacto = contacto.getFecha_nac();
            provinciaContacto = contacto.getProvincia();
        } else info_contacto_btn_agregar.setVisibility(View.VISIBLE);

        if(nombreContacto.equals(""))
            nombreContacto = correoContacto;

        if(!rutaImgContacto.equals(""))
            Glide.with(context)
                    .load(rutaImgContacto)
                    .error(R.drawable.profile_white)
                    .into(info_contacto_imagen);
        else info_contacto_imagen.setImageResource(R.drawable.profile_white);

        info_contacto_nombre.setText(nombreContacto);
        info_contacto_correo.setText(correoContacto);

        if(!aliasContacto.equals("")){
            info_contacto_ll_alias.setVisibility(View.VISIBLE);
            info_contacto_alias.setText(aliasContacto);
        } else info_contacto_ll_alias.setVisibility(View.GONE);

        if(!informacionContacto.equals("")){
            info_contacto_ll_informacion.setVisibility(View.VISIBLE);
            info_contacto_informacion.setText(informacionContacto);
        } else info_contacto_ll_informacion.setVisibility(View.GONE);

        if(!telefonoContacto.equals("")){
            info_contacto_ll_telefono.setVisibility(View.VISIBLE);
            info_contacto_telefono.setText(telefonoContacto);
        } else info_contacto_ll_telefono.setVisibility(View.GONE);

        if(!generoContacto.equals("")){
            info_contacto_ll_genero.setVisibility(View.VISIBLE);
            info_contacto_genero.setText(generoContacto);
        } else info_contacto_ll_genero.setVisibility(View.GONE);

        if(!fechaNacContacto.equals("")){
            info_contacto_ll_fecha_nacimiento.setVisibility(View.VISIBLE);
            info_contacto_fecha_nacimiento.setText(fechaNacContacto);
        } else info_contacto_ll_fecha_nacimiento.setVisibility(View.GONE);

        if(!provinciaContacto.equals("")){
            info_contacto_ll_provincia.setVisibility(View.VISIBLE);
            info_contacto_provincia.setText(provinciaContacto);
        } else info_contacto_ll_provincia.setVisibility(View.GONE);

        info_contacto_btn_agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dismiss();
                ItemContacto newContacto = new ItemContacto(nombreContacto, correoContacto);
                newContacto.setTelefono(telefonoContacto);
                //dbWorker.insertarNuevoContacto(newContacto);
                adicionarContactoAlTelefono(newContacto);
            }
        });
        info_contacto_btn_ir_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                chatsActivity.irAChat(correoContacto);
            }
        });
        if(!dbWorker.existeSiguiendoA(correoContacto)){
            info_contacto_btn_seguir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    chatsActivity.seguirA(correoContacto);
                }
            });
        }else info_contacto_btn_seguir.setVisibility(View.GONE);

        return v;
    }

    private void adicionarContactoAlTelefono(ItemContacto contacto){
        Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
        intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);

        String nombreC = contacto.getNombreMostrar();
        String correoC = contacto.getCorreo();

        intent.putExtra(ContactsContract.Intents.Insert.NAME, nombreC);
        intent.putExtra(ContactsContract.Intents.Insert.EMAIL, correoC);

        intent.putExtra("finishActivityOnSaveCompleted", true);
        startActivityForResult(intent, 35);
//        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 35)
        {
            if (resultCode == RESULT_OK) {
                ItemContacto newContacto = new ItemContacto(nombreContacto, correoContacto);
                newContacto.setUsaYouchat(false);
                newContacto.setTelefono(telefonoContacto);
                dbWorker.insertarNuevoContacto(newContacto);
                Utils.ShowToastAnimated(chatsActivity.getActivity(),"El contacto "+nombreContacto+" fue agregado con éxito",R.raw.contact_check);
            }
            dismiss();
        }
    }
}
