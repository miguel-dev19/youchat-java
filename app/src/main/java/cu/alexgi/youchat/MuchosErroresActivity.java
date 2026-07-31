package cu.alexgi.youchat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vanniktech.emoji.EmojiEditText;

import java.io.File;
import java.util.ArrayList;

import cu.alexgi.youchat.base_datos.BDConstantes;
import cu.alexgi.youchat.base_datos.DBWorker;
import cu.alexgi.youchat.items.ItemChat;

import static cu.alexgi.youchat.MainActivity.context;

public class MuchosErroresActivity extends AppCompatActivity {

    private Permisos permisos;
    private MuchosErroresActivity activity;
    private Context context;
    private SendMsg sendMsg;
    private DBWorker dbWorker;

    private MaterialCardView efab_hacer_copia, efab_enviar_reporte;
    private View view_reporte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muchos_errores);

        findViewById(R.id.root).setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_fondo()));

        activity=this;
        context=this;
        dbWorker = new DBWorker(context);

        ArrayList<String> mensajeError = dbWorker.obtenerPrimeraDescripcionError();
        if(mensajeError.size()>0){
            permisos = new Permisos(this, this);
            sendMsg = new SendMsg(context);
            sendMsg.setOnEnvioMensajeListener(new SendMsg.OnEnvioMensajeListener() {
                @Override
                public void OnEnvioMensaje(ItemChat chat, String categoria, boolean envioCorrecto) {
                    if(categoria.equals(SendMsg.CATEGORY_REPORTE_ERROR_TELEGRAM)){
                        if(envioCorrecto) {
                            Utils.ShowToastAnimated(activity, "Reporte enviado correctamente", R.raw.contact_check);
                            Animation anim = AnimationUtils.loadAnimation(context, R.anim.fade_out_fast);
                            anim.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {}
                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    view_reporte.setVisibility(View.GONE);
                                }
                                @Override
                                public void onAnimationRepeat(Animation animation) {}
                            });
                            view_reporte.startAnimation(anim);
                        }
                        else
                            Utils.ShowToastAnimated(activity,"Error al enviar el reporte",R.raw.error);
                    }
                }
            });

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    getWindow().setStatusBarColor(Color.parseColor(Utils.obtenerOscuroDe(YouChatApplication.itemTemas.getColor_barra())));
                    getWindow().setNavigationBarColor(Color.parseColor(Utils.obtenerOscuroDe(YouChatApplication.itemTemas.getColor_barra())));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            view_reporte = findViewById(R.id.view_reporte);
            efab_enviar_reporte = findViewById(R.id.efab_enviar_reporte);
            efab_hacer_copia = findViewById(R.id.efab_hacer_copia);
            efab_hacer_copia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(permisos.requestPermissionAlmacenamiento())
                        exportarBaseDatos();
                }
            });

            ((TextView)findViewById(R.id.btn_enviar)).setTextColor(Color.WHITE);
            efab_enviar_reporte.setStrokeColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
            efab_enviar_reporte.setCardBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
            efab_enviar_reporte.setRadius(YouChatApplication.curvaChat<=32?YouChatApplication.curvaChat:32);

            ((TextView)findViewById(R.id.btn_verificar)).setTextColor(Color.WHITE);
            efab_hacer_copia.setStrokeColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
            efab_hacer_copia.setCardBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
            efab_hacer_copia.setRadius(YouChatApplication.curvaChat<=32?YouChatApplication.curvaChat:32);

            TextView tv_descripcion_error_app = findViewById(R.id.tv_descripcion_error_app);
            tv_descripcion_error_app.setText(mensajeError.get(0));
            final EmojiEditText editext = findViewById(R.id.editext);
            MaterialCheckBox show_email = findViewById(R.id.show_email);

            efab_enviar_reporte.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setEnabled(false);
                    String mensaje=editext.getText().toString().trim();
                    if(Utils.hayConnex(context)){
                        String texto="#reporteErrorDeMuchos\n\n"+mensajeError.get(0)+"\n\n"+mensajeError.get(1)+"\n\n";
                        if(show_email.isChecked()) texto+=YouChatApplication.correo+"\n";
                        if(!mensaje.isEmpty()) texto+="Descripción del error:\n"+mensaje;

                        editext.setText("");

                        ItemChat msg=new ItemChat( "","");
                        msg.setMensaje(texto);
                        sendMsg.enviarMsg(msg,SendMsg.CATEGORY_REPORTE_ERROR_TELEGRAM);
                    }
                    else {
                        v.setEnabled(true);
                        Utils.ShowToastAnimated(activity,"Compruebe su conexión",R.raw.ic_ban);
                    }
                }
            });

            FloatingActionButton go_Main = findViewById(R.id.btn_editar_perfil);
            go_Main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    YouChatApplication.setMark(2);
                    dbWorker.eliminarDescripcionesError();
                    startActivity(new Intent(MuchosErroresActivity.this, WelcomePerfilActivity.class));
                    overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
                    finish();
                }
            });
        }
        else {
            YouChatApplication.setMark(2);
            startActivity(new Intent(MuchosErroresActivity.this, WelcomePerfilActivity.class));
            overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
            finish();
        }
    }

    public void exportarBaseDatos() {
        File sd = new File(YouChatApplication.RUTA_COPIA_BASE_DATOS);
        boolean exist = sd.exists();
        if(!exist)
            exist = sd.mkdirs();
        if(exist){
            String nombreBd = BDConstantes.NOMBRE_BASE_DATOS;
            try {
                File data = Environment.getDataDirectory();
                if (sd.canWrite()) {
                    String currentDBPath = "//data//"+getPackageName()+"//databases//"+nombreBd+"";
                    String backupDBPath = "YouChat_BDatos.dbyc";
                    File currentDB = new File(data, currentDBPath);
                    File backupDB = new File(sd, backupDBPath);
                    Utils.borrarFile(backupDB);
                    new DBWorker(context).insertarVersionBD(YouChatApplication.version_bd);

                    if (currentDB.exists()) {
                        String pass = Utils.MD5(YouChatApplication.correo+"YouChat");
                        if(pass!=null){
                            boolean exito = Utils.comprimirArchivo(currentDB,backupDB, pass);
                            if(exito) Utils.ShowToastAnimated(activity,"Base de datos guardada con éxito",R.raw.contact_check);
                            else Utils.ShowToastAnimated(activity,"Falló al intentar guardar la base de datos",R.raw.error);
                        } else Utils.ShowToastAnimated(activity,"Falló al intentar guardar la base de datos",R.raw.error);
                    }
                    else Utils.ShowToastAnimated(activity,"No existe ninguna base de datos para guardar",R.raw.chats_infotip);

                }
            } catch (Exception e) {
                Utils.ShowToastAnimated(activity,"Falló al intentar guardar la base de datos",R.raw.error);
                e.printStackTrace();
            }
        }
        else Utils.ShowToastAnimated(activity,"Falló al intentar guardar la base de datos",R.raw.error);
    }
}