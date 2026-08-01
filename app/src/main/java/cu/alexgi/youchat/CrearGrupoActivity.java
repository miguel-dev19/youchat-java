package cu.alexgi.youchat;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cu.alexgi.youchat.adapters.AdaptadorDatosContactoSeleccionable;
import cu.alexgi.youchat.items.ItemContacto;
import cu.alexgi.youchat.items.ItemGrupo;
import cu.alexgi.youchat.items.ItemChat;
import cu.alexgi.youchat.SendMsg;
import cu.alexgi.youchat.swipebackfragment.BaseSwipeBackFragment;
import cu.alexgi.youchat.views_GI.FABGI;
import cu.alexgi.youchat.views_GI.ImageViewBarGI;

import static cu.alexgi.youchat.MainActivity.dbWorker;

public class CrearGrupoActivity extends BaseSwipeBackFragment {

    private ImageViewBarGI back;
    private FABGI fabCrear;
    private CircleImageView fotoGrupo;
    private EditText etNombreGrupo, etDescripcionGrupo;
    private RecyclerView listaContactos;
    private TextView tvCantSeleccionados;
    
    private AdaptadorDatosContactoSeleccionable adaptadorContactos;
    private ArrayList<ItemContacto> contactos;
    private ArrayList<String> seleccionados;

    public static CrearGrupoActivity newInstance() {
        return new CrearGrupoActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_crear_grupo, container, false);
        
        inicializarVistas(view);
        configurarListeners();
        cargarContactos();
        
        return view;
    }

    private void inicializarVistas(View view) {
        back = view.findViewById(R.id.back);
        fabCrear = view.findViewById(R.id.fab_crear);
        fotoGrupo = view.findViewById(R.id.foto_grupo);
        etNombreGrupo = view.findViewById(R.id.et_nombre_grupo);
        etDescripcionGrupo = view.findViewById(R.id.et_descripcion_grupo);
        listaContactos = view.findViewById(R.id.lista_contactos);
        tvCantSeleccionados = view.findViewById(R.id.tv_cant_seleccionados);
        
        seleccionados = new ArrayList<>();
    }

    private void configurarListeners() {
        back.setOnClickListener(v -> {
            if (getActivity() != null) getActivity().onBackPressed();
        });

        fabCrear.setOnClickListener(v -> crearGrupo());

        fotoGrupo.setOnClickListener(v -> {
            // TODO: Abrir selector de imagen para la foto del grupo
            Toast.makeText(getContext(), "Seleccionar foto del grupo", Toast.LENGTH_SHORT).show();
        });
    }

    private void cargarContactos() {
        contactos = dbWorker.obtenerContactosOrdenadosXNombre(true);
        
        // Filtrar solo contactos que usan YouChat (excluir grupos y canales)
        ArrayList<ItemContacto> contactosFiltrados = new ArrayList<>();
        for (ItemContacto c : contactos) {
            if (c.esUsuario() && c.isUsaYouchat()) {
                contactosFiltrados.add(c);
            }
        }
        
        adaptadorContactos = new AdaptadorDatosContactoSeleccionable(getContext(), 
                contactosFiltrados, seleccionados);
        listaContactos.setLayoutManager(new LinearLayoutManager(getContext()));
        listaContactos.setAdapter(adaptadorContactos);
        
        adaptadorContactos.setOnSeleccionChangeListener(this::actualizarContador);
    }

    private void actualizarContador() {
        tvCantSeleccionados.setText(seleccionados.size() + " seleccionados");
    }

    private void crearGrupo() {
        String nombre = etNombreGrupo.getText().toString().trim();
        
        if (nombre.isEmpty()) {
            Toast.makeText(getContext(), "Ingresa un nombre para el grupo", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (seleccionados.isEmpty()) {
            Toast.makeText(getContext(), "Selecciona al menos un miembro", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear ID único para el grupo
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        String idGrupo = "GRP" + sdf.format(new Date()) + YouChatApplication.correo.hashCode();
        
        String descripcion = etDescripcionGrupo.getText().toString().trim();
        
        // Crear el grupo
        ItemGrupo grupo = new ItemGrupo(idGrupo, nombre, YouChatApplication.correo);
        grupo.setDescripcion(descripcion);
        grupo.setFechaCreacion(sdf.format(new Date()));
        
        dbWorker.crearTablasGrupos();
        dbWorker.insertarGrupo(grupo);
        
        // Agregar al creador como admin
        dbWorker.agregarMiembroGrupo(idGrupo, YouChatApplication.correo, true);
        
        // Agregar a los seleccionados
        for (String correo : seleccionados) {
            dbWorker.agregarMiembroGrupo(idGrupo, correo, false);
        }
        
        // Crear contacto tipo grupo
        ItemContacto contactoGrupo = new ItemContacto(nombre, idGrupo);
        contactoGrupo.setTipo_contacto(ItemContacto.TIPO_GRUPO);
        dbWorker.insertarNuevoContacto(contactoGrupo);
        
        // Notificar a los miembros (opcional - via ChatService)
        notificarMiembros(idGrupo, nombre);
        
        Toast.makeText(getContext(), "Grupo \"" + nombre + "\" creado con éxito", Toast.LENGTH_SHORT).show();
        
        // Abrir el chat del grupo
        if (mAddFragmentListener != null) {
            Bundle bundle = new Bundle();
            bundle.putString("usuario", nombre);
            bundle.putString("correo", idGrupo);
            mAddFragmentListener.onAddFragment(CrearGrupoActivity.this, 
                ChatsActivity.newInstance(bundle));
        }
    }

    private void notificarMiembros(String idGrupo, String nombreGrupo) {
        if (YouChatApplication.estaAndandoChatService() && YouChatApplication.chatService.hayConex) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
            
            // Mensaje de sistema: creador creó el grupo
            String fechaEntera = sdf.format(new Date());
            String creadorNombre = dbWorker.obtenerNombre(YouChatApplication.correo);
            ItemChat msgCreador = new ItemChat(idGrupo, "1");
            msgCreador.setId("-sys-" + fechaEntera);
            msgCreador.setHora(Convertidor.conversionHora(fechaEntera));
            msgCreador.setFecha(Convertidor.conversionFecha(fechaEntera));
            msgCreador.setEmisor(YouChatApplication.correo);
            msgCreador.setMensaje(creadorNombre + " creó el grupo \"" + nombreGrupo + "\"");
            msgCreador.setTipo_mensaje(97); // Tipo mensaje de sistema
            dbWorker.insertarChat(msgCreador);
            
            for (String miembro : seleccionados) {
                // Enviar notificación a cada miembro
                String fechaEntera2 = sdf.format(new Date());
                ItemChat msgGrupo = new ItemChat(idGrupo, "1");
                msgGrupo.setId("-grp-" + miembro + "-" + fechaEntera2);
                msgGrupo.setHora(Convertidor.conversionHora(fechaEntera2));
                msgGrupo.setFecha(Convertidor.conversionFecha(fechaEntera2));
                msgGrupo.setEmisor(YouChatApplication.correo);
                YouChatApplication.chatService.enviarMensaje(msgGrupo, SendMsg.CATEGORY_CHAT);
            }
        }
    }
}
