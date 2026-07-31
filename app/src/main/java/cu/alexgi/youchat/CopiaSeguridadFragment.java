package cu.alexgi.youchat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.SubjectTerm;

import cu.alexgi.youchat.BottomSheetDialogFragment.BottomSheetDialogFragment_Ayuda_ajustes;
import cu.alexgi.youchat.base_datos.BDConstantes;
import cu.alexgi.youchat.base_datos.DBWorker;
import cu.alexgi.youchat.items.ItemChat;
import cu.alexgi.youchat.swipebackfragment.BaseSwipeBackFragment;

import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.mainActivity;

@SuppressLint("RestrictedApi")
public class CopiaSeguridadFragment extends BaseSwipeBackFragment {

    private final String asuntoCopia = "oikawomare/CopiaSeguridad/"+YouChatApplication.correo;
    private boolean modoPreguntar;
    private View ir_atras, option_exportar_local,
            option_exportar_nube, option_importar_local,
            option_importar_nube, option_eliminar_nube;

    private AppCompatImageView icon_preguntar;
    private ColorStateList stateListAccent, stateListNone;
    private RadioGroup radioGroup_copia_seguridad;
    private LottieAnimationView animation;

    private CopiaSeguridadFragment copiaSeguridadFragment;
    private Dialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_copia_seguridad, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view) {
        stateListAccent = ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_accento()));
        stateListNone = ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getFont_barra()));

        copiaSeguridadFragment = this;
        modoPreguntar = false;

        view.findViewById(R.id.root).setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_interior()));

        animation = view.findViewById(R.id.animation);
        animation.setAnimation(R.raw.filter_new);
        ir_atras = view.findViewById(R.id.ir_atras);
        radioGroup_copia_seguridad = view.findViewById(R.id.radioGroup_copia_seguridad);
        option_exportar_local = view.findViewById(R.id.option_exportar_local);
        option_exportar_nube = view.findViewById(R.id.option_exportar_nube);
        option_importar_local = view.findViewById(R.id.option_importar_local);
        option_importar_nube = view.findViewById(R.id.option_importar_nube);
        option_eliminar_nube = view.findViewById(R.id.option_eliminar_nube);

        switch (YouChatApplication.opcionCopiaSeguridadAutomatica){
            case 1:
                radioGroup_copia_seguridad.check(R.id.radio1);
                break;
            case 2:
                radioGroup_copia_seguridad.check(R.id.radio2);
                break;
            case 3:
                radioGroup_copia_seguridad.check(R.id.radio3);
                break;
            case 4:
                radioGroup_copia_seguridad.check(R.id.radio4);
                break;
            default:
                YouChatApplication.setOpcionCopiaSeguridadAutomatica(1);
                radioGroup_copia_seguridad.check(R.id.radio1);
        }
        radioGroup_copia_seguridad.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radio1:
                        if(YouChatApplication.opcionCopiaSeguridadAutomatica!=1){
                            YouChatApplication.setOpcionCopiaSeguridadAutomatica(1);
                            WorkManager.getInstance(context).cancelAllWorkByTag("WORKER-COPIA-SEGURIDAD");
                        }
                        break;
                    case R.id.radio2:
                        if(YouChatApplication.opcionCopiaSeguridadAutomatica!=2){
                            YouChatApplication.setOpcionCopiaSeguridadAutomatica(2);
                            WorkManager.getInstance(context).cancelAllWorkByTag("WORKER-COPIA-SEGURIDAD");
                            PeriodicWorkRequest periodicWorkRequest =
                                    new PeriodicWorkRequest.Builder(WorkerServiceCopiaSeguridad.class,
                                            1, TimeUnit.DAYS)
                                    .addTag("WORKER-COPIA-SEGURIDAD")
                                    .build();
                            WorkManager.getInstance(context).enqueue(periodicWorkRequest);
                        }
                        break;
                    case R.id.radio3:
                        if(YouChatApplication.opcionCopiaSeguridadAutomatica!=3){
                            YouChatApplication.setOpcionCopiaSeguridadAutomatica(3);
                            WorkManager.getInstance(context).cancelAllWorkByTag("WORKER-COPIA-SEGURIDAD");
                            PeriodicWorkRequest periodicWorkRequest =
                                    new PeriodicWorkRequest.Builder(WorkerServiceCopiaSeguridad.class,
                                            3, TimeUnit.DAYS)
                                            .addTag("WORKER-COPIA-SEGURIDAD")
                                            .build();
                            WorkManager.getInstance(context).enqueue(periodicWorkRequest);
                        }
                        break;
                    case R.id.radio4:
                        if(YouChatApplication.opcionCopiaSeguridadAutomatica!=4){
                            YouChatApplication.setOpcionCopiaSeguridadAutomatica(4);
                            WorkManager.getInstance(context).cancelAllWorkByTag("WORKER-COPIA-SEGURIDAD");
                            PeriodicWorkRequest periodicWorkRequest =
                                    new PeriodicWorkRequest.Builder(WorkerServiceCopiaSeguridad.class,
                                            7, TimeUnit.DAYS)
                                            .addTag("WORKER-COPIA-SEGURIDAD")
                                            .build();
                            WorkManager.getInstance(context).enqueue(periodicWorkRequest);
                        }
                        break;
                }
            }
        });

        option_exportar_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modoPreguntar) {
                    modoPreguntar = false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Guardar localmente", getResources().getString(R.string.explicarCSGuardarLocal));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                } else {
                    Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(1);
                    View mview = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
                    dialog.setContentView(mview);

                    LinearLayout header = mview.findViewById(R.id.header);
                    ImageView icono_eliminar = mview.findViewById(R.id.icono_eliminar);
                    TextView text_icono = mview.findViewById(R.id.text_icono);
                    TextView text_eliminar = mview.findViewById(R.id.text_eliminar);
                    TextView btn_ok = mview.findViewById(R.id.btn_ok);
                    View btn_cancel = mview.findViewById(R.id.btn_cancel);

                    header.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
                    icono_eliminar.setImageResource(R.drawable.option_copia_seguridad);
                    text_icono.setText("Guardar localmente");
                    text_eliminar.setText("¿Quieres hacer una copia local de tus datos? Será remplazada cualquier otra copia que exista anteriormente");
                    btn_ok.setText("Guardar");

                    btn_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            exportarBaseDatos(false);
                        }
                    });
                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    dialog.setCancelable(true);
                    dialog.show();
                }
            }
        });
        option_exportar_nube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modoPreguntar) {
                    modoPreguntar = false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Guardar y subir a la nube", getResources().getString(R.string.explicarCSGuardarNube));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                } else {
                    Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(1);
                    View mview = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
                    dialog.setContentView(mview);

                    LinearLayout header = mview.findViewById(R.id.header);
                    ImageView icono_eliminar = mview.findViewById(R.id.icono_eliminar);
                    TextView text_icono = mview.findViewById(R.id.text_icono);
                    TextView text_eliminar = mview.findViewById(R.id.text_eliminar);
                    TextView btn_ok = mview.findViewById(R.id.btn_ok);
                    View btn_cancel = mview.findViewById(R.id.btn_cancel);

                    header.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
                    icono_eliminar.setImageResource(R.drawable.option_copia_seguridad);
                    text_icono.setText("Guardar y subir a la nube");
                    text_eliminar.setText("¿Quieres hacer una copia local de tus datos y luego subirlos a la nube? Será remplazada cualquier otra copia que exista anteriormente");
                    btn_ok.setText("Guardar");

                    btn_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            if(YouChatApplication.estaAndandoChatService()
                                    && YouChatApplication.chatService.hayConex){
                                exportarBaseDatos(true);
                            }
                            else Utils.mostrarToastDeConexion(mainActivity);

                        }
                    });
                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    dialog.setCancelable(true);
                    dialog.show();
                }
            }
        });
        option_importar_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modoPreguntar) {
                    modoPreguntar = false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Cargar localmente", getResources().getString(R.string.explicarCSCargarLocal));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                } else {
                    final Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(1);
                    View mview=getLayoutInflater().inflate(R.layout.dialog_confirm_db,null);
                    dialog.setContentView(mview);

                    View btn_cancel=mview.findViewById(R.id.btn_cancel);
                    View btn_ok=mview.findViewById(R.id.btn_ok);

                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    btn_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            importarBaseDatos();
                        }
                    });

                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    dialog.setCancelable(true);
                    dialog.show();
                }
            }
        });
        option_importar_nube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modoPreguntar) {
                    modoPreguntar = false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Descargar y cargar de la nube", getResources().getString(R.string.explicarCSCargarNube));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                } else {
                    final Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(1);
                    View mview=getLayoutInflater().inflate(R.layout.dialog_confirm_db,null);
                    dialog.setContentView(mview);

                    TextView text = mview.findViewById(R.id.text_eliminar);
                    text.setText("¿Desea descargar y luego cargar su copia de seguridad en la nube? " +
                            "Perderá todos los datos actuales que no estén guardados.");

                    View btn_cancel=mview.findViewById(R.id.btn_cancel);
                    View btn_ok=mview.findViewById(R.id.btn_ok);

                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    btn_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(YouChatApplication.estaAndandoChatService()
                                    && YouChatApplication.chatService.hayConex){
                                dialog.dismiss();
                                CargarMiCopiaNubeAsyncTask ccc = new CargarMiCopiaNubeAsyncTask();
                                ccc.execute();
                            }
                            else Utils.mostrarToastDeConexion(mainActivity);
                        }
                    });

                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    dialog.setCancelable(true);
                    dialog.show();
                }
            }
        });
        option_eliminar_nube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modoPreguntar) {
                    modoPreguntar = false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Eliminar mi copia de la nube", getResources().getString(R.string.explicarCSBorrarNube));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                } else {
                    if(YouChatApplication.estaAndandoChatService()
                            && YouChatApplication.chatService.hayConex){
                        BorrarMiCopiaNubeAsyncTask bbb = new BorrarMiCopiaNubeAsyncTask(false);
                        bbb.execute();
                    }
                    else Utils.mostrarToastDeConexion(mainActivity);

                }
            }
        });

        ir_atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modoPreguntar) {
                    modoPreguntar = false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Botón de ir atrás", "Con este botón se regresa a la pantalla de conversaciones");
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                } else mainActivity.atrasFragment();
            }
        });

        icon_preguntar = view.findViewById(R.id.icon_pregunta);
        icon_preguntar.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                modoPreguntar = !modoPreguntar;
                if (modoPreguntar) {

                    icon_preguntar.setSupportImageTintList(stateListAccent);
                    Utils.ShowToastAnimated(mainActivity, "Elija una opción para obtener su explicación", R.raw.chats_infotip);
                } else icon_preguntar.setSupportImageTintList(stateListNone);
                v.setEnabled(true);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void exportarBaseDatos(boolean aLaNube) {
        if(!new Permisos(mainActivity,context).requestPermissionAlmacenamiento())
            return;
        File sd = new File(YouChatApplication.RUTA_COPIA_BASE_DATOS);
        boolean exist = sd.exists();
        if(!exist)
            exist = sd.mkdirs();
        if(exist){
            String nombreBd = BDConstantes.NOMBRE_BASE_DATOS;
            try {
                File data = Environment.getDataDirectory();
                if (sd.canWrite()) {
                    String currentDBPath = "//data//"+mainActivity.getPackageName()+"//databases//"+nombreBd+"";
                    String backupDBPath = "YouChat_BDatos.dbyc";
                    File currentDB = new File(data, currentDBPath);
                    File backupDB = new File(sd, backupDBPath);
                    Utils.borrarFile(backupDB);
                    new DBWorker(context).insertarVersionBD(YouChatApplication.version_bd);

                    if (currentDB.exists()) {
                        String pass = Utils.MD5(YouChatApplication.correo+"YouChat");
                        if(pass!=null){
                            boolean exito = Utils.comprimirArchivo(currentDB,backupDB, pass);
                            if(exito){
                                if(aLaNube){
                                    BorrarMiCopiaNubeAsyncTask bbb = new BorrarMiCopiaNubeAsyncTask(true);
                                    bbb.execute();
                                }
                                Utils.ShowToastAnimated(mainActivity,"Copia de seguridad guardada con éxito",R.raw.contact_check);
                            }
                            else Utils.ShowToastAnimated(mainActivity,"Falló al intentar hacer la copia de seguridad",R.raw.error);
                        } else Utils.ShowToastAnimated(mainActivity,"Falló al intentar hacer la copia de seguridad",R.raw.error);
                    }
                    else Utils.ShowToastAnimated(mainActivity,"No existe ningún dato para guardar",R.raw.chats_infotip);

                }
            } catch (Exception e) {
                Utils.ShowToastAnimated(mainActivity,"Falló al intentar hacer la copia de seguridad",R.raw.error);
                e.printStackTrace();
                Log.e("exportarBaseDatos: ",e.toString() );
            }
        }
        else Utils.ShowToastAnimated(mainActivity,"Falló al intentar hacer la copia de seguridad",R.raw.error);
    }

//    public void exportarBaseDatos2() {
//        if(!new Permisos(activity,context).requestPermissionAlmacenamiento())
//            return;
//        File sd = new File(YouChatApplication.RUTA_COPIA_BASE_DATOS);
//        boolean exist = sd.exists();
//        if(!exist)
//            exist = sd.mkdirs();
//        if(exist){
//            String nombreBd = BDConstantes.NOMBRE_BASE_DATOS;
//            try {
//                File data = Environment.getDataDirectory();
//
//                if (sd.canWrite()) {
//                    String currentDBPath = "//data//"+getActivity().getPackageName()+"//databases//"+nombreBd+"";
//                    String backupDBPath = "YouChat_BDatos.dbyc";
//                    File currentDB = new File(data, currentDBPath);
//                    File backupDB = new File(sd, backupDBPath);
//
//                    if (currentDB.exists()) {
//                        FileChannel src = new FileInputStream(currentDB).getChannel();
//                        FileChannel dst = new FileOutputStream(backupDB).getChannel();
//                        dst.transferFrom(src, 0, src.size());
//                        src.close();
//                        dst.close();
//                        Utils.ShowToastAnimated(activity,"Base de datos guardada con éxito",R.raw.contact_check);
////                        Toast.makeText(context, "Base de datos guardada con éxito en "
////                                +YouChatApplication.RUTA_COPIA_BASE_DATOS+"YouChat_BDatos.db", Toast.LENGTH_LONG).show();
//                    } else Utils.ShowToastAnimated(activity,"No existe ninguna base de datos para guardar",R.raw.chats_infotip);
//
//                }
//            } catch (Exception e) {
//                Utils.ShowToastAnimated(activity,"Falló al intentar guardar la base de datos",R.raw.error);
//                e.printStackTrace();
//            }
//        }
//        else Utils.ShowToastAnimated(activity,"Falló al intentar guardar la base de datos",R.raw.error);
//    }

    public void importarBaseDatos(){
        if(!new Permisos(mainActivity,context).requestPermissionAlmacenamiento()) return;
        File sd = new File(YouChatApplication.RUTA_COPIA_BASE_DATOS);
        boolean exist = sd.exists();
        if(!exist)
            exist = sd.mkdirs();
        if(exist){
            String nombreBd = BDConstantes.NOMBRE_BASE_DATOS;
            try {
//                        File data = Environment.getDataDirectory();
                if (sd.canWrite()) {
                    String currentDBPath = "/data/"+mainActivity.getPackageName()+"/databases/";
                    String backupDBPath = "YouChat_BDatos.dbyc";
//                            File currentDB = new File(data, currentDBPath);
                    File backupDB = new File(sd, backupDBPath);
                    if (backupDB.exists()) {
                        String pass = Utils.MD5(YouChatApplication.correo+"YouChat");
                        if(pass!=null){
                            int result = Utils.descomprimirArchivo(backupDB, Environment.getDataDirectory()+currentDBPath,nombreBd, pass);
                            if(result==1){
                                int versionBD=new DBWorker(context).obtenerVersionBD();
                                if(versionBD<5) versionBD=5;
                                if(versionBD!=YouChatApplication.version_bd){
                                    YouChatApplication.setVersion_bd(versionBD);
                                    new DBWorker(context);
                                }
                                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("ACTUALIZAR_USUARIOS"));
                                Utils.ShowToastAnimated(mainActivity,"Copia de seguridad cargada con éxito",R.raw.contact_check);
                            }
                            else if(result==2)
                                Utils.ShowToastAnimated(mainActivity,"Esta copia de seguridad no le pertenece a este correo",R.raw.error);
                            else if(result==3)
                                Utils.ShowToastAnimated(mainActivity,"Copia de seguridad no encontrada o archivo dañado",R.raw.error);
                            else
                                Utils.ShowToastAnimated(mainActivity,"Falló al intentar cargar la copia de seguridad",R.raw.error);
                        } else Utils.ShowToastAnimated(mainActivity,"Falló al intentar cargar la copia de seguridad",R.raw.error);
                    } else Utils.ShowToastAnimated(mainActivity,"No existe ninguna copia de seguridad para cargar",R.raw.chats_infotip);
                }
            } catch (Exception e) {
                Utils.ShowToastAnimated(mainActivity,"Falló al intentar cargar la copia de seguridad",R.raw.error);
                e.printStackTrace();
            }
        }
        else Utils.ShowToastAnimated(mainActivity,"Falló al intentar cargar la copia de seguridad",R.raw.error);
    }

//    public void importarBaseDatos2(){
//        final Dialog dialog = new Dialog(context);
//        dialog.requestWindowFeature(1);
//        View mview=getLayoutInflater().inflate(R.layout.dialog_confirm_db,null);
//        dialog.setContentView(mview);
//
//        View btn_cancel=mview.findViewById(R.id.btn_cancel);
//        View btn_ok=mview.findViewById(R.id.btn_ok);
//
//        btn_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//        btn_ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                if(!new Permisos(activity,context).requestPermissionAlmacenamiento()) return;
//                File sd = new File(YouChatApplication.RUTA_COPIA_BASE_DATOS);
//                boolean exist = sd.exists();
//                if(!exist)
//                    exist = sd.mkdirs();
//                if(exist){
//                    String nombreBd = BDConstantes.NOMBRE_BASE_DATOS;
//                    try {
//                        File data = Environment.getDataDirectory();
//
//                        if (sd.canWrite()) {
//                            String currentDBPath = "//data//"+getActivity().getPackageName()+"//databases//"+nombreBd+"";
//                            String backupDBPath = "YouChat_BDatos.dbyc";
//                            File currentDB = new File(data, currentDBPath);
//                            File backupDB = new File(sd, backupDBPath);
//
//                            if (currentDB.exists()) {
//                                FileChannel src = new FileOutputStream(currentDB).getChannel();
//                                FileChannel dst = new FileInputStream(backupDB).getChannel();
//                                src.transferFrom(dst, 0, dst.size());
//                                src.close();
//                                dst.close();
//                                Utils.ShowToastAnimated(activity,"Base de datos cargada con éxito",R.raw.contact_check);
//                            } else Utils.ShowToastAnimated(activity,"No existe ninguna base de datos para cargar",R.raw.chats_infotip);
//                        }
//                    } catch (Exception e) {
//                        Utils.ShowToastAnimated(activity,"Falló al intentar cargar la base de datos",R.raw.error);
//                        e.printStackTrace();
//                    }
//                }
//                else Utils.ShowToastAnimated(activity,"Falló al intentar cargar la base de datos",R.raw.error);
//            }
//        });
//
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
//        dialog.setCancelable(true);
//        dialog.show();
//    }

    private class BorrarMiCopiaNubeAsyncTask extends AsyncTask<String, String, String> {

        boolean exito, guardar;
        int l;

        public BorrarMiCopiaNubeAsyncTask(boolean save){
            exito = false;
            l=0;
            guardar = save;
            if(guardar) dialog = Utils.mostrarDialogCarga(copiaSeguridadFragment, context, "Subiendo copia...");
            else dialog = Utils.mostrarDialogCarga(copiaSeguridadFragment, context, "Eliminando copia...");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String user, pass;
                pass = YouChatApplication.chatService.getNxfaq();
                Session session;
                IMAPStore store;
                Properties props = new Properties();
                IMAPFolder inbox;
                props.setProperty("mail.imap.starttls.enable", "false");
                props.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.setProperty("mail.imap.socketFactory.fallback", "false");
                props.setProperty("mail.imap.port", "993");
                props.setProperty("mail.imap.socketFactory.port", "993");
                user = YouChatApplication.chatService.getGxdiag();
                session = Session.getInstance(props);
                session.setDebug(true);
                store = (IMAPStore) session.getStore("imap");
                store.connect("imap.gmail.com", user, pass);

                if(store.isConnected()) {
                    inbox = (IMAPFolder) store.getFolder("Inbox");
                    inbox.open(Folder.READ_WRITE);
                    if (inbox.isOpen()) {
                        SubjectTerm asunto = new SubjectTerm(asuntoCopia);
                        Message[] result = inbox.search(asunto);
                        l = result.length;
                        if(l>0){
                            for(int i=0; i<l; i++){
                                result[i].setFlag(Flags.Flag.DELETED,true);
                            }
                            inbox.expunge();
                        }
                        exito = true;
                        inbox.close(false);
                    }
                    store.close();
                }
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result){
            if(guardar){
                SendMsg sendMsg = new SendMsg(context);
                sendMsg.setOnEnvioMensajeListener(new SendMsg.OnEnvioMensajeListener() {
                    @Override
                    public void OnEnvioMensaje(ItemChat chat, String categoria, boolean envioCorrecto) {
                        if(envioCorrecto)
                            Utils.ShowToastAnimated(mainActivity,"Copia de seguridad guardada con éxito en la nube", R.raw.contact_check);
                        else
                            Utils.ShowToastAnimated(mainActivity,"Error al intentar enviar la copia de seguridad", R.raw.error);
                        Utils.cerrarDialogCarga(dialog);
                    }
                });
                sendMsg.enviarMsg(new ItemChat("",""),SendMsg.CATEGORY_COPIA_SEGURIDAD);
            }
            else if(exito){
                Utils.cerrarDialogCarga(dialog);
                if(l==0) Utils.ShowToastAnimated(mainActivity,"No existe ninguna copia de seguridad en la nube", R.raw.ic_delete);
                else Utils.ShowToastAnimated(mainActivity,"Copia de seguridad eliminada de la nube", R.raw.ic_delete);
            }
            else{
                Utils.cerrarDialogCarga(dialog);
                Utils.ShowToastAnimated(mainActivity,"Error al intentar conectar", R.raw.error);
            }
        }
    }

    private class CargarMiCopiaNubeAsyncTask extends AsyncTask<String, String, String> {

        boolean exito;
        int l;

        public CargarMiCopiaNubeAsyncTask(){
            exito = false;
            l=-1;
            dialog = Utils.mostrarDialogCarga(copiaSeguridadFragment, context, "Descargando copia...");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String user, pass;
                pass = YouChatApplication.chatService.getNxfaq();
                Session session;
                IMAPStore store;
                Properties props = new Properties();
                IMAPFolder inbox;
                props.setProperty("mail.imap.starttls.enable", "false");
                props.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.setProperty("mail.imap.socketFactory.fallback", "false");
                props.setProperty("mail.imap.port", "993");
                props.setProperty("mail.imap.socketFactory.port", "993");
                user = YouChatApplication.chatService.getGxdiag();
                session = Session.getInstance(props);
                session.setDebug(true);
                store = (IMAPStore) session.getStore("imap");
                store.connect("imap.gmail.com", user, pass);

                if(store.isConnected()) {
                    inbox = (IMAPFolder) store.getFolder("Inbox");
                    inbox.open(Folder.READ_WRITE);
                    if (inbox.isOpen()) {
                        SubjectTerm asunto = new SubjectTerm(asuntoCopia);
                        Message[] result = inbox.search(asunto);
                        l = result.length;
                        if(l>0){
                            Multipart multi = (Multipart) result[l-1].getContent();
                            if(multi.getCount()>0){
                                Part unaParte = multi.getBodyPart(0);
                                String ruta_Dato = YouChatApplication.RUTA_COPIA_BASE_DATOS
                                        + "YouChat_BDatos.dbyc";
                                File file = new File(YouChatApplication.RUTA_COPIA_BASE_DATOS);
                                File fileDest = new File(ruta_Dato);
                                if(fileDest.exists()) fileDest.delete();
                                boolean estaCreada = file.exists();
                                if (!estaCreada)
                                    estaCreada = file.mkdirs();
                                if (estaCreada) {
                                    MimeBodyPart mbp = (MimeBodyPart) unaParte;
                                    mbp.saveFile(ruta_Dato);
                                    exito = true;
                                    int tamMsg = unaParte.getSize();
                                    if (YouChatApplication.burbuja_datos && tamMsg>0) {
                                        YouChatApplication.consumoBajada += tamMsg;
                                        Intent WIDGET = new Intent("ACTUALIZAR_WIDGET");
                                        LocalBroadcastManager.getInstance(YouChatApplication.chatService).sendBroadcast(WIDGET);
                                    }
                                    YouChatApplication.addCant_bd_nube(1);
                                    YouChatApplication.addMega_x_serv_bd_nube_rec(tamMsg);
                                }
                            }

                            if(l>1){
                                for(int i=0; i<l-1; i++){
                                    result[i].setFlag(Flags.Flag.DELETED,true);
                                }
                                inbox.expunge();
                            }
                        }
                        inbox.close(false);
                    }
                    store.close();
                }
            } catch (MessagingException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result){
            if(exito){
                importarBaseDatos();
            }
            else{
                if(l==-1)
                    Utils.ShowToastAnimated(mainActivity,"Error al intentar conectar", R.raw.error);
                else Utils.ShowToastAnimated(mainActivity,"No existe ninguna copia asociada a este correo", R.raw.ic_ban);
            }
            Utils.cerrarDialogCarga(dialog);
        }
    }
}
