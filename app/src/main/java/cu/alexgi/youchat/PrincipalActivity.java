package cu.alexgi.youchat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import androidx.work.WorkManager;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiTextView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Callable;

import cu.alexgi.youchat.BottomSheetDialogFragment.BottomSheetDialogFragment_Ayuda;
import cu.alexgi.youchat.BottomSheetDialogFragment.BottomSheetDialogFragment_Cambios;
import cu.alexgi.youchat.adapters.AdaptadorDatosUsuario;
import cu.alexgi.youchat.base_datos.DBWorker;
import cu.alexgi.youchat.items.ItemChat;
import cu.alexgi.youchat.items.ItemEstado;
import cu.alexgi.youchat.items.ItemPost;
import cu.alexgi.youchat.items.ItemUsuario;
import cu.alexgi.youchat.swipebackfragment.BaseSwipeBackFragment;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static cu.alexgi.youchat.Acerca_de_Activity.openTelegram;
import static cu.alexgi.youchat.MainActivity.mainActivity;

public class PrincipalActivity extends BaseSwipeBackFragment{

    private static final String TAG = "PrincipalActivity";
    private CompositeDisposable disposable;

    //    private int colorActual;
    private Activity principalActivity;
    private PrincipalActivity principalActivityReal;
    private Context context;
//    public NavController navController;

    private ImageView slide_fondo, option_theme;
    private TextView texto_alerta;
    private boolean esOscuro;
    private static FloatingActionButton fab_contact, fab_status;
    private IntentFilter filter;
    private ResponseReceiver receiver;
    private DBWorker dbWorker;
    private Permisos permisos;
    private TextView estado_toolbar;
    private View abriruusariosPublicos, abrirBandeja, abrirNavegador_Covid, acerca_de, preguntasFrecuentes,
            historialCambios, contactenos, abrirAjustes, abrirSeguidores, personalizarChat,
            cerrarSesion, cancelar_buscar_usuario, eliminarItemModoSeleccionar, main_ms_anclar_principal,
            cancelarModoSeleccionarBtn, main_search_principal, dots, abrirSlide;
    private CircleImageView imageView_page_ajustes;

    private EmojiTextView user_ajustes;
    private TextView correo_ajustes, cant_seguidores_ajustes;
    private ImageView img_tipo_user;
    //private RelativeLayout contenedor_estados;

    private int ultAccion;

    //buscar main
    private LinearLayout layout_buscar_usuario;
    private EmojiEditText et_buscar_usuario;
    private boolean act_cancel_buscar;

    //seleccionar main
    public static LinearLayout ll_usuario;
    public static LinearLayout ll_modo_seleccionar;
    public int cant_seleccionados;
    private boolean necesitaActualizar, pasox2=false, necesitaActualizarBroadCast;
    public static TextView main_ms_cant;
    public static ImageView main_ms_anclar;

    public static final int NUM_PAGES = 3;
    private static ViewPager2 pager;
    public DrawerLayout drawer;
    private FragmentStateAdapter pagerAdapter;
    private static TabLayout tab;

    private PrincipalFragment principalFragment;
    public EstadosFragment estadosFragment;
    private PostFragment postFragment;

    private String aut_user,aut_pass;

    private View viewBackground;
    private TextView usuarios_cont;


    private void cargarPreferenciasUserPass() {
        aut_user = YouChatApplication.correo;
        aut_pass = YouChatApplication.pass;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_principal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        navController = Navigation.findNavController(view);
        initComponent(view);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setExitTransition(new MaterialElevationScale( false ));
//        setReenterTransition(new MaterialElevationScale ( true ));

//        try {
//            //AlarmManager ff = AlarmManager.class.newInstance();
//            //ff.setRepeating(AlarmManager.ELAPSED_REALTIME, 10000, 60000, PendingIntent.getService(context, 10, new Intent(context, ChatService.class), 0));
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (java.lang.InstantiationException e) {
//            e.printStackTrace();
//        }
    }

    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START, true);
        } else if (principalFragment != null && principalFragment.modoSeleccionar) {
            principalFragment.cancelarModoSeleccionar(true, false);
        } else if (act_cancel_buscar) cancelar_buscar();

        else if (pager.getCurrentItem() == 0) getActivity().finish();
        else pager.setCurrentItem(0);
    }

    public void MostrarPreviewChat(ItemUsuario usuario) {
        principalFragment.MostrarPreviewChat(usuario);
    }

    public void irAChat(String usu, String cor) {
        Utils.runOnUIThread(()->{
            Bundle mibundle=new Bundle();
            mibundle.putString("usuario",usu);
            mibundle.putString("correo",cor);
            if(mAddFragmentListener!=null)
                mAddFragmentListener.onAddFragment(PrincipalActivity.this, ChatsActivity.newInstance(mibundle));
        });
    }

    public void irAChat(String usu, String cor, String body) {
        Utils.runOnUIThread(()->{
            Bundle mibundle=new Bundle();
            mibundle.putString("usuario",usu);
            mibundle.putString("correo",cor);
            mibundle.putString("body",body);
            if(mAddFragmentListener!=null)
                mAddFragmentListener.onAddFragment(PrincipalActivity.this, ChatsActivity.newInstance(mibundle));
        });
    }

    public void irAChat(Bundle mibundle) {
        Utils.runOnUIThread(()->{
            if(mAddFragmentListener!=null)
                mAddFragmentListener.onAddFragment(PrincipalActivity.this, ChatsActivity.newInstance(mibundle));
        });
    }

    public void irAPersonalizar(){
        Utils.runOnUIThread(()->{
            if(mAddFragmentListener!=null)
                mAddFragmentListener.onAddFragment(PrincipalActivity.this, new PersonalizarChat());
        });
    }

    public void irAperfil(String usu, String cor, boolean vieneDeChat) {
        Utils.runOnUIThread(()->{
            Bundle mibundle=new Bundle();
            mibundle.putString("usuario",usu);
            mibundle.putString("correo",cor);
            mibundle.putBoolean("vieneDeChat", false);
            if(mAddFragmentListener!=null)
                mAddFragmentListener.onAddFragment(PrincipalActivity.this, ViewPerfilActivity.newInstance(mibundle));
        });
    }

    public void cargarFondo() {
        Utils.runOnUIThread(()->{
            if(slide_fondo!=null)
                Utils.cargarFondo(context,slide_fondo);
        });
    }

    public void actualizarBadgeCantMensajesNuevos() {
        if(!YouChatApplication.activarBuzon){
            if(viewBackground!=null && viewBackground.getVisibility()==View.VISIBLE)
                viewBackground.setVisibility(View.GONE);
        }
        else{
            if(viewBackground!=null && usuarios_cont!=null){
                int cantNuevosMensajes = dbWorker.obtenerCantMensajeCorreoNoVistoTotal();
                if(cantNuevosMensajes==0) viewBackground.setVisibility(View.GONE);
                else {
                    viewBackground.setVisibility(View.VISIBLE);
                    usuarios_cont.setText(""+cantNuevosMensajes);
                }
            }
        }
    }

    public void abrirPreview(String ruta_img) {
        if(mAddFragmentListener!=null)
            mAddFragmentListener.onAddFragment(PrincipalActivity.this, ImagePager.newInstance(ruta_img), R.anim.show_layout_answer, R.anim.hide_layout_answer);
    }
    public void abrirNuevoPost(int cant) {
        if(mAddFragmentListener!=null)
            mAddFragmentListener.onAddFragment(PrincipalActivity.this, PublicarPostFragment.newInstance(cant), R.anim.show_layout_answer, R.anim.hide_layout_answer);
    }

    public void actualizarImgPostDescargada(String id, String ruta_dato) {
        if(postFragment!=null)
            postFragment.actualizarPostDescargado(id, ruta_dato);
    }

    public void actualizarDescargaFallidaImgPost(String id, boolean b) {
        if(postFragment!=null)
            postFragment.descargaImgFallida(id, b);
    }

    public void desactivarLupa() {
        main_search_principal.setEnabled(false);
        Utils.runOnUIThread(()->{
                main_search_principal.setEnabled(true);
        },500);
    }


    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter() {
            super(PrincipalActivity.this);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) return principalFragment;
            else if(position == 1) return estadosFragment;
            else return postFragment;
        }

        @Override
        public int getItemCount() {
            if(!YouChatApplication.activePost) return 2;
            return NUM_PAGES;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("PRINCIPAL","onResume");
        if(YouChatApplication.principalActivity==null)
            YouChatApplication.principalActivity = this;
        procesoActualizarVista();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        tab.clearOnTabSelectedListeners();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        YouChatApplication.principalActivity=null;
    }

    @SuppressLint("RestrictedApi")
    private synchronized void initComponent(View view) {
        disposable = new CompositeDisposable();

        principalActivityReal = this;
        YouChatApplication.principalActivity = this;
        context = getContext();
        dbWorker = new DBWorker(context);
        permisos = new Permisos(getActivity(), context);
        permisos.requestPermissionAlmacenamiento();

        Observable<ArrayList<ItemEstado>> observable = Observable.fromCallable(new Callable<ArrayList<ItemEstado>>() {
            @Override
            public ArrayList<ItemEstado> call() {
                return dbWorker.obtenerTodosLosEstados();
            }
        });
        Disposable subscriber = observable.
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Consumer<ArrayList<ItemEstado>>() {
                    @Override
                    public void accept(@NonNull ArrayList<ItemEstado> estadosAct) throws Exception {
                        try {
                            SimpleDateFormat kk = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
                            Date date = new Date();
                            String fechaEntera = kk.format(date);
                            String horaHoy = Convertidor.conversionHora(fechaEntera);

                            int intHoraHoy = Convertidor.createIntOfStringHora(horaHoy);

                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                            Date datHoy = new Date(sdf.parse(sdf.format(new Date())).getTime());

                            for (int i = 0; i < estadosAct.size(); i++) {
                                String fechaEstado = estadosAct.get(i).getFecha();
                                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                                Date datEstado = new Date(format.parse(fechaEstado).getTime());

                                long dif = (datHoy.getTime() - datEstado.getTime()) / 86400000;
                                if (dif >= 1) {
                                    if (dif == 1) {
                                        int intHoraEst = Convertidor.createIntOfStringHora(estadosAct.get(i).getHora());
                                        if (intHoraHoy > intHoraEst) {
                                            dbWorker.eliminarElEstadosDe(estadosAct.get(i).getId());
                                            if (estadosAct.get(i).esEstadoImagen()) {
                                                Utils.borrarFile(new File(estadosAct.get(i).getRuta_imagen()));
                                            }
                                        }
                                    } else {
                                        dbWorker.eliminarElEstadosDe(estadosAct.get(i).getId());
                                        if (estadosAct.get(i).esEstadoImagen()) {
                                            Utils.borrarFile(new File(estadosAct.get(i).getRuta_imagen()));
                                        }
                                    }
                                }
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
        disposable.add(subscriber);

        if(YouChatApplication.activePostBorrar24){
            Observable<ArrayList<ItemPost>> observable2 = Observable.fromCallable(new Callable<ArrayList<ItemPost>>() {
                @Override
                public ArrayList<ItemPost> call() {
                    return dbWorker.obtenerTodosPosts();
                }
            });
            Disposable subscriber2 = observable2.
                    subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Consumer<ArrayList<ItemPost>>() {
                        @Override
                        public void accept(@NonNull ArrayList<ItemPost> postAct) throws Exception {
                            try {
                                SimpleDateFormat kk = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
                                Date date = new Date();
                                String fechaEntera = kk.format(date);
                                String horaHoy = Convertidor.conversionHora(fechaEntera);

                                int intHoraHoy = Convertidor.createIntOfStringHora(horaHoy);

                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                Date datHoy = new Date(sdf.parse(sdf.format(new Date())).getTime());

                                int l = postAct.size();
                                for (int i = 0; i < l; i++) {
                                    String fechaEstado = postAct.get(i).getFecha();
                                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                                    Date datEstado = new Date(format.parse(fechaEstado).getTime());

                                    long dif = (datHoy.getTime() - datEstado.getTime()) / 86400000;
                                    if (dif >= 1) {
                                        if (dif == 1) {
                                            int intHoraEst = Convertidor.createIntOfStringHora(postAct.get(i).getHora());
                                            if (intHoraHoy > intHoraEst) {
                                                dbWorker.eliminarPost(postAct.get(i).getId());
                                            }
                                        } else {
                                            dbWorker.eliminarPost(postAct.get(i).getId());
                                        }
                                    }
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            disposable.add(subscriber2);
        }


        /*new Thread(() -> {
            try {
                ArrayList<ItemEstado> estadosAct = dbWorker.obtenerTodosLosEstados();
                SimpleDateFormat kk = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
                Date date = new Date();
                String fechaEntera = kk.format(date);
                String horaHoy = Convertidor.conversionHora(fechaEntera);

                int intHoraHoy = Convertidor.createIntOfStringHora(horaHoy);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date datHoy = new Date(sdf.parse(sdf.format(new Date())).getTime());

                for (int i = 0; i < estadosAct.size(); i++) {
                    String fechaEstado = estadosAct.get(i).getFecha();
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    Date datEstado = new Date(format.parse(fechaEstado).getTime());

                    long dif = (datHoy.getTime() - datEstado.getTime()) / 86400000;
                    if (dif >= 1) {
                        if (dif == 1) {
                            int intHoraEst = Convertidor.createIntOfStringHora(estadosAct.get(i).getHora());
                            if (intHoraHoy > intHoraEst) {
                                dbWorker.eliminarElEstadosDe(estadosAct.get(i).getId());
                                if (estadosAct.get(i).esEstadoImagen()) {
                                    File file = new File(estadosAct.get(i).getRuta_imagen());
                                    if (file.exists()) file.delete();
                                }
                            }
                        } else {
                            dbWorker.eliminarElEstadosDe(estadosAct.get(i).getId());
                            if (estadosAct.get(i).esEstadoImagen()) {
                                File file = new File(estadosAct.get(i).getRuta_imagen());
                                if (file.exists())
                                    file.delete();
                            }
                        }
                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }).start();*/

        principalFragment = new PrincipalFragment();
        estadosFragment = EstadosFragment.newInstance(this);
        postFragment = new PostFragment();


        drawer = view.findViewById(R.id.drawer_layout);
        NavigationView nav_view = view.findViewById(R.id.nav_view);
        nav_view.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_fondo()));
        if(mainActivity.abrirDrawer){
            mainActivity.abrirDrawer = false;
            drawer.openDrawer(GravityCompat.START, true);
        }
        else if(mainActivity.irAPersonalizar){
            mainActivity.irAPersonalizar = false;
//            drawer.openDrawer(GravityCompat.START, true);
            irAPersonalizar();
        }
        else if(mainActivity.irAReenviar){
            mainActivity.irAReenviar = false;
            irAReenviar(mainActivity.bundleReenviar);
            mainActivity.bundleReenviar = null;

        }
        else if(mainActivity.irANotificar){
            mainActivity.irANotificar = false;
            irAChat(mainActivity.bundleNotificacion);
            mainActivity.bundleNotificacion = null;
        }
        else if(mainActivity.irANotificarCorreo){
            mainActivity.irANotificarCorreo = false;
            irABandejaEntrada();
        }
        else if(mainActivity.irAChat){
            mainActivity.irAChat = false;
            if(mainActivity.bundleChat!=null){
                irAChat(mainActivity.bundleChat);
                mainActivity.bundleChat = null;
            }
        }

        pager = view.findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter();
        pager.setAdapter(pagerAdapter);
        tab = view.findViewById(R.id.tab);

        fab_contact = view.findViewById(R.id.fab_contact_img);
        fab_status = view.findViewById(R.id.fab_status_img);
        fab_status.hide();

        principalActivity = getActivity();
        ultAccion = 0;

        slide_fondo = view.findViewById(R.id.slide_fondo);
        ll_usuario = view.findViewById(R.id.ll_usuario_principal);
        layout_buscar_usuario = view.findViewById(R.id.ll_buscar_usuario_principal);
        ll_modo_seleccionar = view.findViewById(R.id.ll_modo_seleccionar_principal);
        main_ms_cant = view.findViewById(R.id.main_ms_cant_principal);
        main_ms_anclar = view.findViewById(R.id.main_ms_anclar_principal);
        et_buscar_usuario = view.findViewById(R.id.buscar_usuario_principal);
        viewBackground = view.findViewById(R.id.viewBackground);
        usuarios_cont = view.findViewById(R.id.usuarios_cont);

        estado_toolbar = view.findViewById(R.id.estado_toolbar);
        dots = view.findViewById(R.id.dots);

        View follow = view.findViewById(R.id.follow);
        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTelegram(mainActivity,"youchat_oficial");
            }
        });

        abrirNavegador_Covid = view.findViewById(R.id.abrirNavegador_Covid);
        abrirNavegador_Covid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gid.nat.cu/geocovid/")));
            }
        });

        NestedScrollView nested = view.findViewById(R.id.nested);
        nested.setSmoothScrollingEnabled(true);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                Log.e("PRINCIPAL", "nested Height: "+nested.getHeight()+" / screen: "+YouChatApplication.largoPantalla);
//                Log.e("PRINCIPAL", "nested Offset: "+nested.computeVerticalScrollOffset()+" / screen: "+YouChatApplication.largoPantalla);
//                Log.e("PRINCIPAL", "nested Range: "+nested.computeVerticalScrollRange()+" / screen: "+YouChatApplication.largoPantalla);
//                Log.e("PRINCIPAL", "nested Extent: "+nested.computeVerticalScrollExtent()+" / screen: "+YouChatApplication.largoPantalla);

                int top = YouChatApplication.largoPantalla - nested.computeVerticalScrollRange();
//                Log.e("PRINCIPAL", "top: "+top);
                if(top>0){
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, top,0, 0);
                    follow.setLayoutParams(layoutParams);
                }
            }
        });

        abrirBandeja = view.findViewById(R.id.abrirBandeja);
        abrirBandeja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setEnabled(false);
                if(!YouChatApplication.activarBuzon){
                    Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(1);
                    View mview = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
                    dialog.setContentView(mview);

                    LinearLayout header = mview.findViewById(R.id.header);
                    ImageView img = mview.findViewById(R.id.icono_eliminar);
                    TextView text_icono = mview.findViewById(R.id.text_icono);
                    TextView text_eliminar = mview.findViewById(R.id.text_eliminar);
                    View btn_cancel = mview.findViewById(R.id.btn_cancel);
                    TextView btn_ok = mview.findViewById(R.id.btn_ok);

                    header.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
                    img.setImageResource(R.drawable.message);
                    text_icono.setText("Activar Buzón");
                    text_eliminar.setText("¿Desea activar el buzón?\nRecibirá todos los correos de su bandeja de entrada.");
                    btn_ok.setText("ACTIVAR");

                    btn_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            YouChatApplication.setActivarBuzon(true);
                            if (principalFragment != null && principalFragment.isVisible() && principalFragment.modoSeleccionar)
                                principalFragment.cancelarModoSeleccionar(true, false);
                            if (act_cancel_buscar) cancelar_buscar();
                            if (mAddFragmentListener != null)
                                mAddFragmentListener.onAddFragment(PrincipalActivity.this, BandejaFragment.newInstance());
                            EsperarParaActivity();
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
                else {
                    if (principalFragment != null && principalFragment.isVisible() && principalFragment.modoSeleccionar)
                        principalFragment.cancelarModoSeleccionar(true, false);
                    if (act_cancel_buscar) cancelar_buscar();
                    if (mAddFragmentListener != null)
                        mAddFragmentListener.onAddFragment(PrincipalActivity.this, BandejaFragment.newInstance());
                    EsperarParaActivity();
                }
                view.setEnabled(true);
            }
        });

        acerca_de = view.findViewById(R.id.acerca_de);
        acerca_de.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setEnabled(false);
                if (principalFragment != null && principalFragment.isVisible() && principalFragment.modoSeleccionar)
                    principalFragment.cancelarModoSeleccionar(true, false);
                if (act_cancel_buscar) cancelar_buscar();
                if (mAddFragmentListener != null)
                    mAddFragmentListener.onAddFragment(PrincipalActivity.this, Acerca_de_Activity.newInstance());
                EsperarParaActivity();
                view.setEnabled(true);
            }
        });

        preguntasFrecuentes = view.findViewById(R.id.preguntasFrecuentes);
        preguntasFrecuentes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setEnabled(false);
                BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda.newInstance();
                bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyuda");
                view.setEnabled(true);
            }
        });

        historialCambios = view.findViewById(R.id.historialCambios);
        historialCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setEnabled(false);
                BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Cambios.newInstance();
                bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyuda");
                view.setEnabled(true);
            }
        });

        contactenos = view.findViewById(R.id.contactenos);
        contactenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setEnabled(false);
                BottomSheetDialog bottomSheetDialog= new BottomSheetDialog(context);
                bottomSheetDialog.setContentView(R.layout.dialog_contactenos);

                final EmojiEditText editext = bottomSheetDialog.findViewById(R.id.editext);
                MaterialCheckBox show_email = bottomSheetDialog.findViewById(R.id.show_email);

                final MaterialRadioButton sugerencia= bottomSheetDialog.findViewById(R.id.sugerencia);
                final MaterialRadioButton reporte= bottomSheetDialog.findViewById(R.id.reporte);
                final MaterialRadioButton duda= bottomSheetDialog.findViewById(R.id.duda);
                View aceptar = bottomSheetDialog.findViewById(R.id.aceptar);

                aceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(sugerencia.isChecked() || reporte.isChecked() || duda.isChecked())
                        {
                            String mensaje=editext.getText().toString().trim();
                            if(mensaje.replace(" ","").equals("") || mensaje.replace(" ","").length()==0) Utils.ShowToastAnimated(principalActivity, "El mensaje no puede estar vacío", R.raw.chats_infotip);
                            else if(mensaje.length()<=5) Utils.ShowToastAnimated(principalActivity, "Mensaje muy corto", R.raw.chats_infotip);
                            else if(mensaje.length()>500) Utils.ShowToastAnimated(principalActivity, "Mensaje muy largo", R.raw.chats_infotip);
                            else{
                                if(YouChatApplication.estaAndandoChatService()
                                        && YouChatApplication.chatService.hayConex){
                                    bottomSheetDialog.dismiss();
                                    cargarPreferenciasUserPass();

                                    String texto="";
                                    if(reporte.isChecked()) {
                                        texto="#reporte:\n";
                                    }
                                    else if(sugerencia.isChecked()) {
                                        texto="#sugerencia:\n";
                                    }
                                    else {
                                        texto="#duda:\n";
                                    }
                                    if(show_email.isChecked()) texto+=aut_user+"\n";
                                    texto+=mensaje;

                                    ItemChat msg=new ItemChat( "","");
                                    msg.setMensaje(texto);
                                    YouChatApplication.chatService.enviarMensaje(msg,SendMsg.CATEGORY_REPORTE_TELEGRAM);
                                    Utils.ShowToastAnimated(principalActivity,"Enviado correctamente",R.raw.contact_check);
                                }
                                else Utils.ShowToastAnimated(principalActivity,"Compruebe su conexión",R.raw.ic_ban);
                            }
                        }
                        else Utils.ShowToastAnimated(principalActivity, "Debe marcar una opción de las primeras", R.raw.chats_infotip);
                    }
                });

                bottomSheetDialog.show();
                View bottomSheetInternal = bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
                BottomSheetBehavior bsb = BottomSheetBehavior.from(bottomSheetInternal);
                bsb.setState(BottomSheetBehavior.STATE_EXPANDED);
                view.setEnabled(true);
            }
        });

        abrirAjustes = view.findViewById(R.id.abrirAjustes);
        abrirAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setEnabled(false);
                if (principalFragment != null && principalFragment.isVisible() && principalFragment.modoSeleccionar)
                    principalFragment.cancelarModoSeleccionar(true, false);
                if (act_cancel_buscar) cancelar_buscar();
                if(mAddFragmentListener!=null) mAddFragmentListener.onAddFragment(PrincipalActivity.this, new AjustesActivity());
                EsperarParaActivity();
                view.setEnabled(true);
            }
        });
        abriruusariosPublicos = view.findViewById(R.id.abriruusariosPublicos);
        abriruusariosPublicos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setEnabled(false);
                if (principalFragment != null && principalFragment.isVisible() && principalFragment.modoSeleccionar)
                    principalFragment.cancelarModoSeleccionar(true, false);
                if (act_cancel_buscar) cancelar_buscar();
                if(mAddFragmentListener!=null) mAddFragmentListener.onAddFragment(PrincipalActivity.this, new UsuariosPublicosFragment());
                EsperarParaActivity();
                view.setEnabled(true);
            }
        });

        abrirSeguidores = view.findViewById(R.id.abrirSeguidores);
        abrirSeguidores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setEnabled(false);
                if (principalFragment != null && principalFragment.isVisible() && principalFragment.modoSeleccionar)
                    principalFragment.cancelarModoSeleccionar(true, false);
                if (act_cancel_buscar) cancelar_buscar();
                if(mAddFragmentListener!=null) mAddFragmentListener.onAddFragment(PrincipalActivity.this, new SeguidoresActivity());
                EsperarParaActivity();
                view.setEnabled(true);
            }
        });

        personalizarChat = view.findViewById(R.id.personalizarChat);
        personalizarChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setEnabled(false);
                if (principalFragment != null && principalFragment.isVisible() && principalFragment.modoSeleccionar)
                    principalFragment.cancelarModoSeleccionar(true, false);
                if (act_cancel_buscar) cancelar_buscar();
                if(mAddFragmentListener!=null)
                    mAddFragmentListener.onAddFragment(PrincipalActivity.this, new PersonalizarChat());
                view.setEnabled(true);
            }
        });

        cerrarSesion = view.findViewById(R.id.cerrarSesion);
        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setEnabled(false);
                Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(1);
                View mview = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
                dialog.setContentView(mview);

                LinearLayout header = mview.findViewById(R.id.header);
                ImageView img = mview.findViewById(R.id.icono_eliminar);
                TextView text_icono = mview.findViewById(R.id.text_icono);
                TextView text_eliminar = mview.findViewById(R.id.text_eliminar);
                View btn_cancel = mview.findViewById(R.id.btn_cancel);
                TextView btn_ok = mview.findViewById(R.id.btn_ok);

                header.setBackgroundResource(R.color.theme);
                img.setImageResource(R.drawable.logout);
                text_icono.setText("Cerrar sesión");
                text_eliminar.setText("¿Desea cerrar sesión?\nSus mensajes no serán eliminados.");
                btn_ok.setText("Salir");

                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        WorkManager.getInstance(context).cancelAllWorkByTag("WORKER-VACIAR-BANDEJA");
                        YouChatApplication.setOpcionVaciadoAutomaticoMensajes(1);
                        YouChatApplication.setMark(1);
                        context.stopService(new Intent(context, ChatService.class));

                        Dialog dialogo = new Dialog(context);
                        dialogo.requestWindowFeature(1);
                        View mviewe = getLayoutInflater().inflate(R.layout.dialog_alert_progress, null);
                        dialogo.setContentView(mviewe);

                        texto_alerta = mviewe.findViewById(R.id.texto_alerta);
                        texto_alerta.setText("Cerrando sesión");

                        dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                        dialogo.setCancelable(false);
                        dialogo.show();
                        Utils.runOnUIThread(()->{
                            dialogo.dismiss();
                            getActivity().finish();
                            startActivity(new Intent(context, LoginActivity.class));
                        },3000);
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
                view.setEnabled(true);
            }
        });

        cancelar_buscar_usuario = view.findViewById(R.id.cancelar_buscar_usuario);
        cancelar_buscar_usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelar_buscar();
            }
        });

        eliminarItemModoSeleccionar = view.findViewById(R.id.eliminarItemModoSeleccionar);
        eliminarItemModoSeleccionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setEnabled(false);
                Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(1);
                View mview = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
                dialog.setContentView(mview);

                View btn_ok = mview.findViewById(R.id.btn_ok);
                View btn_cancel = mview.findViewById(R.id.btn_cancel);
                TextView text_eliminar = mview.findViewById(R.id.text_eliminar);

                cant_seleccionados = principalFragment.cant_seleccionados;

                if (cant_seleccionados == 1) text_eliminar.setText("¿Deseas eliminar esta conversación?");
                else text_eliminar.setText("¿Deseas eliminar " + cant_seleccionados + " conversaciones?");

                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        principalFragment.borrarItemSeleccionados();
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
                view.setEnabled(true);
            }
        });

        main_ms_anclar_principal = view.findViewById(R.id.main_ms_anclar_principal);
        main_ms_anclar_principal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anclarItemSeleccionados(principalFragment.esParaAnclar);
            }
        });

        cancelarModoSeleccionarBtn = view.findViewById(R.id.cancelarModoSeleccionarBtn);
        cancelarModoSeleccionarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                principalFragment.cancelarModoSeleccionar(true, false);
            }
        });

        main_search_principal = view.findViewById(R.id.main_search_principal);
        main_search_principal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_buscar_usuario.setText("");
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.show_layout_search);
                layout_buscar_usuario.startAnimation(anim);
                layout_buscar_usuario.setVisibility(View.VISIBLE);
                act_cancel_buscar = true;
                if(principalFragment!=null)
                    principalFragment.activarModoBuscar();
                ll_usuario.setVisibility(View.GONE);
                et_buscar_usuario.setFocusableInTouchMode(true);
                et_buscar_usuario.requestFocus();
                final InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(et_buscar_usuario, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        abrirSlide = view.findViewById(R.id.abrirSlide);
        abrirSlide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START, true);
                else drawer.openDrawer(GravityCompat.START, true);
            }
        });

        img_tipo_user = view.findViewById(R.id.img_tipo_user);
        user_ajustes = view.findViewById(R.id.user_ajustes);
        correo_ajustes = view.findViewById(R.id.correo_ajustes);
        cant_seguidores_ajustes = view.findViewById(R.id.cant_seguidores_ajustes);
        imageView_page_ajustes = view.findViewById(R.id.imageView_page_ajustes);
        option_theme = view.findViewById(R.id.option_theme);
        option_theme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setEnabled(false);
                if (esOscuro) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    YouChatApplication.setTemaApp(0);
//                    Intent trans = new Intent(context, MainActivity.class);
//                    Bundle mibundle = new Bundle();
//                    mibundle.putInt("cambio_de_tema", 1);
//                    trans.putExtras(mibundle);
//                    startActivity(trans);
//                    getActivity().finish();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    YouChatApplication.setTemaApp(1);
//                    Intent trans = new Intent(context, MainActivity.class);
//                    Bundle mibundle = new Bundle();
//                    mibundle.putInt("cambio_de_tema", 1);
//                    trans.putExtras(mibundle);
//                    startActivity(trans);
//                    getActivity().finish();
                }
                mainActivity.vaciarPilaCambioTema();
                view.setEnabled(true);
            }
        });
        esOscuro = YouChatApplication.temaApp == 1;
        if (esOscuro) option_theme.setImageResource(R.drawable.theme_sun);
        else option_theme.setImageResource(R.drawable.theme_moon);
        Utils.cargarFondo(context,slide_fondo);

        ImageView geocovid = view.findViewById(R.id.geocovid);
        Glide.with(context).load(R.drawable.geocovid).into(geocovid);

        if (YouChatApplication.estaAndandoChatService() && YouChatApplication.chatService.hayConex) {
            if (YouChatApplication.chatService.estadoConex == 4) accionActualizando();
            else accionConex();
        } else {
            if (YouChatApplication.estaAndandoChatService() && YouChatApplication.chatService.estadoConex == 3)
                accionConectando();
            else accionNoConex();
        }

        filter = new IntentFilter("ACTUALIZAR_USUARIOS");
        filter.addAction("CONEXION");
        receiver = new ResponseReceiver();
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter);
        necesitaActualizar = false;
        necesitaActualizarBroadCast = false;

        imageView_page_ajustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                if (principalFragment != null && principalFragment.isVisible() && principalFragment.modoSeleccionar)
                    principalFragment.cancelarModoSeleccionar(true, false);
                if (act_cancel_buscar) cancelar_buscar();
                if(mAddFragmentListener!=null) mAddFragmentListener.onAddFragment(PrincipalActivity.this, new ViewYouPerfilActivity());
                EsperarParaActivity();
                v.setEnabled(true);
            }
        });


        ll_usuario.setVisibility(View.VISIBLE);
        ll_modo_seleccionar.setVisibility(View.GONE);
        layout_buscar_usuario.setVisibility(View.GONE);
        cant_seleccionados = 0;
        et_buscar_usuario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public synchronized void afterTextChanged(Editable s) {
                if (layout_buscar_usuario.getVisibility() == View.VISIBLE) {
                    String espacio = s.toString();
                    if (espacio.replace(" ", "").length() > 0) BuscarUsuario(espacio);

                    else {
                        if (espacio.length() > 0) et_buscar_usuario.setText("");
                        principalFragment.adaptadorUsuario = new AdaptadorDatosUsuario(context, principalFragment.datos_Usuario, PrincipalActivity.this);
                        principalFragment.lista_usuario.setAdapter(principalFragment.adaptadorUsuario);
                    }
                }
            }
        });

        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                comprobarVista(position);
                Utils.ocultarKeyBoard(mainActivity);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
//                if(state==1) Utils.ocultarKeyBoard(mainActivity);
            }
        });

        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {}
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0 && principalFragment!=null){
                    if(principalFragment.cantNoVisto>0) principalFragment.scrollPosNoLeido();
                    else principalFragment.lista_usuario.scrollToPosition(0);
                }
                else if (tab.getPosition() == 1 && estadosFragment!=null) estadosFragment.lista_estado.scrollToPosition(0);
                else if (tab.getPosition() == 2 && postFragment!=null) postFragment.scrollToUp();
            }
        });

        fab_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (estadosFragment != null && estadosFragment.isVisible()) {
                    if (act_cancel_buscar) cancelar_buscar();
                    estadosFragment.nuevoEstadoTexto();
                }
            }
        });
    }

    public void irAReenviar(Bundle bundleReenviar) {
        Utils.runOnUIThread(()->{
            if(mAddFragmentListener!=null)
                mAddFragmentListener.onAddFragment(PrincipalActivity.this, ReenviarActivity.newInstance(bundleReenviar,false));
        });
    }

    public synchronized void irABandejaEntrada() {
        Utils.runOnUIThread(()->{
            if(mAddFragmentListener!=null)
                mAddFragmentListener.onAddFragment(PrincipalActivity.this, new BandejaFragment());
        });
    }

    private synchronized void comprobarVista(int position) {
        if(position!=0){
            if(main_search_principal.getVisibility()==View.VISIBLE){
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.hide_layout_search);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        main_search_principal.setVisibility(View.GONE);
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                main_search_principal.startAnimation(animation);
            }
            if (act_cancel_buscar) cancelar_buscar();
        }
        else if(main_search_principal.getVisibility()==View.GONE){
            Utils.runOnUIThread(()->{
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.show_layout_search);
                animation.setDuration(600);
                main_search_principal.setVisibility(View.VISIBLE);
                main_search_principal.startAnimation(animation);
            });
        }

        switch (position){
            case 0:
                Utils.runOnUIThread(()->{
                    fab_contact.setImageResource(R.drawable.chat);
                    fab_status.hide();
                    fab_contact.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (principalFragment != null && principalFragment.isVisible() && principalFragment.modoSeleccionar) principalFragment.cancelarModoSeleccionar(true, false);
                            if (act_cancel_buscar) cancelar_buscar();
                            if(mAddFragmentListener!=null) mAddFragmentListener.onAddFragment(PrincipalActivity.this, ContactActivity.newInstance(principalActivityReal));
                        }
                    });
                    fab_contact.setOnLongClickListener(null);
                });
                break;
            case 1:
                Utils.runOnUIThread(()->{
                    fab_contact.setImageResource(R.drawable.camera);
                    fab_status.show();
                    fab_contact.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (estadosFragment!=null && estadosFragment.isVisible()) {
                                if (act_cancel_buscar) cancelar_buscar();
                                estadosFragment.nuevoEstadoImg();
                            }
                        }
                    });
                    fab_contact.setOnLongClickListener(null);
                    if(YouChatApplication.estaAndandoChatService())
                        YouChatApplication.chatService.eliminarNotiNowEntrante();
                    ActualizarEstados(false);
                });
                break;
            case 2:
                Utils.runOnUIThread(()->{
                    fab_contact.setImageResource(R.drawable.menu_add);
                    fab_status.hide();
                    fab_contact.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (postFragment != null && postFragment.isVisible()) {
                                if (act_cancel_buscar) cancelar_buscar();
                                YouChatApplication.resetearCantPostSubidosHoy();
                                int x = Utils.calcularCantLimPostXDia(dbWorker.obtenerCantSeguidores())-YouChatApplication.cantPostSubidosHoy;
                                if(x>0)
                                    postFragment.crearPost(x);
                                else Utils.ShowToastAnimated(mainActivity,"Límite de Post diario alcanzado",R.raw.error);
                            }
                        }
                    });
                    fab_contact.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if (postFragment != null && postFragment.isVisible()) {
                                YouChatApplication.resetearCantPostSubidosHoy();
                                if (act_cancel_buscar) cancelar_buscar();
                                postFragment.sacarBottomSheetOpciones(Utils.calcularCantLimPostXDia(dbWorker.obtenerCantSeguidores()));
                            }
                            return true;
                        }
                    });

                    if(YouChatApplication.activePost) ActualizarPosts(false, true, true);
                });
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(disposable != null && !disposable.isDisposed()){
            disposable.dispose();
        }
    }

////////////////////////////////////////opciones dentro del slide//////////////////////////////////////inicio

    ////////////////////////////////////////opciones dentro del slide//////////////////////////////////////fin

    //////////////////////////////////////////////////buscar chat/////////////////////////////inicio
    public synchronized void BuscarUsuario(String espacio) {
        Utils.runOnUIThread(()->{
            final ArrayList<ItemUsuario> datos_usuario_buscar = new ArrayList<>();

            for (int i = 0; i < principalFragment.datos_Usuario.size(); i++)
                if (dbWorker.obtenerNombre(principalFragment.datos_Usuario.get(i).getCorreo()).toLowerCase().contains(espacio.toLowerCase())
                        || principalFragment.datos_Usuario.get(i).getCorreo().contains(espacio.toLowerCase()))
                    datos_usuario_buscar.add(principalFragment.datos_Usuario.get(i));
            principalFragment.adaptadorUsuario = new AdaptadorDatosUsuario(context, datos_usuario_buscar, PrincipalActivity.this);
            principalFragment.lista_usuario.setAdapter(principalFragment.adaptadorUsuario);

            principalFragment.adaptadorUsuario.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setEnabled(false);
                    int posAd = principalFragment.lista_usuario.getChildAdapterPosition(v);
                    if (posAd >= 0) {
                        String cor = datos_usuario_buscar.get(posAd).getCorreo();
                        String usu = dbWorker.obtenerNombre(cor);

                        if(mainActivity!=null)
                            Utils.ocultarKeyBoard(mainActivity);
                        Bundle mibundle = new Bundle();
                        mibundle.putString("usuario", usu);
                        mibundle.putString("correo", cor);
                        if(mAddFragmentListener!=null) mAddFragmentListener.onAddFragment(PrincipalActivity.this, ChatsActivity.newInstance(mibundle));
//                    navController.navigate(R.id.chatsActivity,mibundle);
                        if (act_cancel_buscar) cancelar_buscar();
                    }
                    v.setEnabled(true);
                }
            });
        });

    }

    public synchronized void cancelar_buscar() {
        Utils.runOnUIThread(()->{
            Animation anim = AnimationUtils.loadAnimation(context, R.anim.hide_layout_search);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    layout_buscar_usuario.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            layout_buscar_usuario.startAnimation(anim);

            et_buscar_usuario.setText("");
            ll_usuario.setVisibility(View.VISIBLE);
            act_cancel_buscar = false;
            if(principalFragment!=null)
                principalFragment.desactivarModoBuscar();
            Utils.ocultarKeyBoard(mainActivity);

//            if (necesitaActualizar) principalFragment.actualizarRecyclerUsuarios();
//            else {
//                principalFragment.adaptadorUsuario = new AdaptadorDatosUsuario(context, principalFragment.datos_Usuario, PrincipalActivity.this);
//                if(principalFragment.datos_Usuario.size()>0) principalFragment.lista_usuario.setAdapter(principalFragment.adaptadorUsuario);
//                principalFragment.AsignarEventosAlAdaptador();
//            }
        });
    }
    public static String getCad() {
        return "ÃÆ\u0084ÉÅÇ";
    }
    //////////////////////////////////////////////////buscar chat/////////////////////////////fin

    //////////////////////////////////////////////////modo seleccionar/////////////////////////////inicio

    public void anclarItemSeleccionados(boolean esParaAnclar) {
        int l = principalFragment.pos_seleccionadas.length;
        int cant = 0;
        for (int i = 0; i < l; i++) {
            if (principalFragment.pos_seleccionadas[i]) {
                if (principalFragment.datos_Usuario.get(i).EsAnclado() != esParaAnclar) {
                    principalFragment.datos_Usuario.get(i).setEsAnclado(esParaAnclar);
                    dbWorker.modificarUsuarioAnclado(principalFragment.datos_Usuario.get(i).getCorreo(), esParaAnclar ? 1 : 0);
                }
                cant++;
            }
        }
        if (cant == 1) {
            if (esParaAnclar)
                Utils.ShowToastAnimated(principalActivity, "Chat anclado", R.raw.ic_pin);
            else Utils.ShowToastAnimated(principalActivity, "Chat desanclado", R.raw.ic_unpin);
        } else {
            if (esParaAnclar)
                Utils.ShowToastAnimated(principalActivity, cant + " chats anclados", R.raw.ic_pin);
            else
                Utils.ShowToastAnimated(principalActivity, cant + " chats desanclados", R.raw.ic_unpin);
        }
        principalFragment.cancelarModoSeleccionar(false, true);
    }
    //////////////////////////////////////////////////modo seleccionar/////////////////////////////fin
    ////////////////////////////////////////opciones fuera del slide//////////////////////////////////////fin


    private synchronized void procesoActualizarVista() {
        fab_contact.show();
        if (YouChatApplication.estaAndandoChatService() && YouChatApplication.chatService.hayConex)
            accionConex();
        else accionNoConex();
        if (principalFragment != null && principalFragment.isVisible() && principalFragment.modoSeleccionar)
            principalFragment.cancelarModoSeleccionar(true, false);
        if (act_cancel_buscar) cancelar_buscar();
        user_ajustes.setText(YouChatApplication.alias);
        if(user_ajustes.getText().toString().trim().equals("")) user_ajustes.setText(YouChatApplication.correo);

        correo_ajustes.setText(YouChatApplication.correo);
        YouChatApplication.setCant_seguidores(dbWorker.obtenerCantSeguidores());
        cant_seguidores_ajustes.setText("" + YouChatApplication.cant_seguidores);

        ///VERIFICADO///
        if(YouChatApplication.comprobarOficialidad(YouChatApplication.correo)
        || YouChatApplication.es_beta_tester)
            img_tipo_user.setImageResource(R.drawable.verified_profile);
        else if(YouChatApplication.cant_seguidores>=YouChatApplication.usuMayor)
            img_tipo_user.setImageResource(R.drawable.vip_crown_line);
        else if(YouChatApplication.cant_seguidores>=YouChatApplication.usuMedio)
            img_tipo_user.setImageResource(R.drawable.vip_diamond_line);
        else if(YouChatApplication.cant_seguidores>=YouChatApplication.usuMenor)
            img_tipo_user.setImageResource(R.drawable.award_line);
        else img_tipo_user.setImageResource(R.drawable.star);
        ///VERIFICADO///

        File imgPerfil = new File(YouChatApplication.ruta_img_perfil);
        if (imgPerfil.exists())
            Glide.with(YouChatApplication.context)
                    .load(YouChatApplication.ruta_img_perfil)
                    .error(R.drawable.profile_white)
                    .into(imageView_page_ajustes);
        else imageView_page_ajustes.setImageResource(R.drawable.profile_white);

        actualizarBadgeCantMensajesNuevos();

        if (YouChatApplication.estaAndandoChatService() && YouChatApplication.chatService.hayConex) {
            if (YouChatApplication.chatService.estadoConex == 4) accionActualizando();
            else accionConex();
        } else {
            if (YouChatApplication.estaAndandoChatService() && YouChatApplication.chatService.estadoConex == 3) accionConectando();
            else accionNoConex();
        }
//        ActualizarUsuarios(true);
        ActualizarEstados(false);
        if(YouChatApplication.activePost) ActualizarPosts(false, false, false);
    }

    public synchronized void ActualizarUsuarios(boolean nec){
        if (principalFragment != null && principalFragment.isVisible() && tab.getSelectedTabPosition()==0)
            if(nec) principalFragment.actualizarRecyclerUsuarios();
    }
    public synchronized void ActualizarEstados(boolean hayNewNow){
        if (estadosFragment != null && estadosFragment.isVisible() && tab.getSelectedTabPosition()==1)
            estadosFragment.LlenarEstados();
        else if(hayNewNow) badgeInTabs(1, 2);
    }
    public synchronized void ActualizarProgressSubidaEstados(boolean subido){
        if (estadosFragment != null)
            estadosFragment.actualizarProgressSubidaNow(subido);
        else if(!subido)
            Utils.ShowToastAnimated(mainActivity,"Error al intentar subir el Now",R.raw.error);
    }
    public synchronized boolean estaEnTabNow(){
        if (estadosFragment != null && estadosFragment.isVisible() && tab.getSelectedTabPosition()==1)
            return true;
        return false;
    }
    public synchronized void ActualizarPosts(boolean hayNewPost, boolean hacerScroll, boolean guardar){
        if (postFragment != null && postFragment.isVisible() && tab.getSelectedTabPosition()==2)
            postFragment.actualizarListaPost(hacerScroll, guardar);
        else if(hayNewPost) badgeInTabs(1,3);
    }

    void EsperarParaActivity(){
        Utils.runOnUIThread(()->{
            if(drawer.isDrawerOpen(GravityCompat.START))
                drawer.closeDrawer(GravityCompat.START, true);
        });
    }

    public synchronized void detenerRefresh(){
        if (postFragment != null)
            postFragment.detenerRefresh();
    }

    private void accionConex() {
        estado_toolbar.setText(R.string.app_name);
        dots.setVisibility(View.GONE);
    }

    private void accionNoConex() {
        estado_toolbar.setText(R.string.network);
        dots.setVisibility(View.VISIBLE);
    }

    private void accionConectando() {
        estado_toolbar.setText(R.string.network_connect);
        dots.setVisibility(View.VISIBLE);
    }

    private void accionActualizando() {
        estado_toolbar.setText(R.string.network_update);
        dots.setVisibility(View.VISIBLE);
    }

    private class ResponseReceiver extends BroadcastReceiver {
        private ResponseReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                final String action = intent.getAction();
                final String accion_conexion = "CONEXION";
                final String accion_actualizar = "ACTUALIZAR_USUARIOS";;

                if (accion_conexion.equals(action)) {
                    int accion = intent.getIntExtra("tipo", 0);
                    if (accion == 1 && accion != ultAccion) {
                        ultAccion = accion;
                        accionNoConex();
                        if(YouChatApplication.burbuja_datos){
                            YouChatApplication.chatService.removeWidget();
                        }
                        if(principalFragment!=null && principalFragment.isVisible())
                            principalFragment.actualizarRecyclerUsuarios();
                    } else if (accion == 2 && accion != ultAccion) {
                        ultAccion = accion;
                        accionConex();
                        if(YouChatApplication.burbuja_datos){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
                                Intent permiso = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:" + context.getPackageName()));
                                startActivity(permiso);
                                //              startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
                            }
                            else YouChatApplication.chatService.buildWidget();
                        }
                        //if(pos==1 && Chats!=null) Chats.actualizarRecyclerUsuarios();
                    } else if (accion == 3 && accion != ultAccion) {
                        ultAccion = accion;
                        accionConectando();
                    } else if (accion == 4 && accion != ultAccion) {
                        ultAccion = accion;
                        accionActualizando();
                        if(YouChatApplication.burbuja_datos){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
                                Intent permiso = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
                                startActivity(permiso);
                                //              startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
                            }
                            else YouChatApplication.chatService.buildWidget();
                        }
                    }
                }
                else if (accion_actualizar.equals(action)) {
                    if(principalFragment!=null && principalFragment.isVisible())
                        principalFragment.actualizarRecyclerUsuarios();
                    else necesitaActualizarBroadCast = true;
                }
            }
        }
    }

    public synchronized void actualizarUltMsg(String correoAct){
        if(!correoAct.isEmpty()){
//            if (principalFragment != null && principalFragment.isVisible()
//                    && tab.getSelectedTabPosition()==0)
            if (principalFragment != null)
                principalFragment.actualizarEstadoUltMsgDe(correoAct);
        }
    }
    public synchronized void actualizarNewMsg(String correoAct, int cant){
        if(!correoAct.isEmpty()){
            if (principalFragment != null){
                if(cant>0){
                    if(YouChatApplication.chatsActivity!=null
                            && YouChatApplication.chatsActivity.getCorreo().equals(correoAct))
                        cant = 0;
                }
                principalFragment.actualizarNewMsgDe(correoAct, cant);
            }
        }
    }

    public synchronized void actualizarBadge(){
        if (principalFragment != null)
            principalFragment.actualizarBadge();
    }

    public synchronized void borrarCantMsgDe(String correo) {
        if (principalFragment != null){
            principalFragment.borrarCantMsgDe(correo);
        }
    }
    public synchronized void borrarUsuario(String correo) {
        if (principalFragment != null){
            principalFragment.borrarUsuario(correo);
        }
    }

    public synchronized void actualizarRecyclerUsuario() {
        if (principalFragment != null){
            principalFragment.actualizarRecyclerUsuarios();
        }
    }

    public void reloadTab() {
        pagerAdapter = new ScreenSlidePagerAdapter();
        pager.setAdapter(pagerAdapter);
        badgeInTabs(0,0);
    }

    public static synchronized void badgeInTabs(int number, int pos) {
        new TabLayoutMediator(tab, pager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position == 0) {
                    if (number == 0 && pos == 1) {
                        tab.getOrCreateBadge().setVisible(false);
                    }
                    else if (pos == 1) {
                        tab.getOrCreateBadge().setVisible(true);
                        tab.getOrCreateBadge().setNumber(number);
                        tab.getOrCreateBadge().setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getFont_barra()));
                        tab.getOrCreateBadge().setBadgeTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_barra()));
                    }

                    tab.setIcon(R.drawable.tab_chats);
                    tab.setText("Chats");
                }
                else if (position == 1){
                    if (number == 0 && pos == 2) tab.getOrCreateBadge().setVisible(false);
                    else if (pos == 2){
                        tab.getOrCreateBadge().setVisible(true);
                        tab.getOrCreateBadge().setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getFont_barra()));
                    }

                    tab.setIcon(R.drawable.activity_white);
                    tab.setText("Now");
                }
                else {
                    if (number == 0 && pos == 3) tab.getOrCreateBadge().setVisible(false);
                    else if (pos == 3){
                        tab.getOrCreateBadge().setVisible(true);
                        tab.getOrCreateBadge().setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getFont_barra()));
                    }

                    tab.setIcon(R.drawable.send_white);
                    tab.setText("Post");
                }
            }
        }).attach();
    }

    public synchronized void reintentarEnviarMensajesNoEnviados(){
        ArrayList<ItemChat> chatNoEnviados = dbWorker.obtenerMsgNoEnviados();
        int l = chatNoEnviados.size();
        if(l>0){
            for(int i=0; i<l; i++){
                int iFinal = i;
                if(YouChatApplication.estaAndandoChatService()){
                    YouChatApplication.chatService.enviarMensaje(chatNoEnviados.get(iFinal),
                            SendMsg.CATEGORY_CHAT);
                }
            }
        }
    }
}
