package cu.alexgi.youchat;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vanniktech.emoji.EmojiTextView;

import java.util.ArrayList;

import cu.alexgi.youchat.adapters.AdaptadorDatosMiembroGrupo;
import cu.alexgi.youchat.items.ItemGrupo;
import cu.alexgi.youchat.swipebackfragment.BaseSwipeBackFragment;
import cu.alexgi.youchat.views_GI.FABGI;
import cu.alexgi.youchat.views_GI.ImageViewBarGI;
import cu.alexgi.youchat.views_GI.TextViewBarGI;

import static cu.alexgi.youchat.MainActivity.dbWorker;

public class InfoGrupoActivity extends BaseSwipeBackFragment {

    private String idGrupo;
    private ItemGrupo grupo;
    private boolean esAdmin;

    private CircleImageView imgGrupo;
    private EmojiTextView infoGrupoNombre1, infoGrupoDescripcion;
    private TextViewBarGI infoGrupoNombre2;
    private TextView infoGrupoCreacion, infoGrupoCantMiembros;
    private RecyclerView infoGrupoListaMiembros;
    private ImageViewBarGI back;
    private FABGI fab;
    private ImageView ivFondoViewGrupo;

    private AdaptadorDatosMiembroGrupo adaptadorMiembros;
    private ArrayList<String> miembros;
    private ArrayList<String> admins;

    public static InfoGrupoActivity newInstance(Bundle bundle) {
        InfoGrupoActivity fragment = new InfoGrupoActivity();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_info_grupo, container, false);

        if (getArguments() != null) {
            idGrupo = getArguments().getString("id_grupo", "");
        }

        inicializarVistas(view);
        configurarListeners();
        cargarDatosGrupo();

        return view;
    }

    private void inicializarVistas(View view) {
        imgGrupo = view.findViewById(R.id.img_grupo);
        infoGrupoNombre1 = view.findViewById(R.id.info_grupo_nombre1);
        infoGrupoNombre2 = view.findViewById(R.id.info_grupo_nombre2);
        infoGrupoDescripcion = view.findViewById(R.id.info_grupo_descripcion);
        infoGrupoCreacion = view.findViewById(R.id.info_grupo_creacion);
        infoGrupoCantMiembros = view.findViewById(R.id.info_grupo_cant_miembros);
        infoGrupoListaMiembros = view.findViewById(R.id.info_grupo_lista_miembros);
        back = view.findViewById(R.id.back);
        fab = view.findViewById(R.id.fab);
        ivFondoViewGrupo = view.findViewById(R.id.iv_fondo_view_grupo);
    }

    private void configurarListeners() {
        back.setOnClickListener(v -> {
            if (getActivity() != null) getActivity().onBackPressed();
        });

        // Botón para ir al chat del grupo
        fab.setOnClickListener(v -> {
            if (grupo != null && mAddFragmentListener != null) {
                Bundle bundle = new Bundle();
                bundle.putString("usuario", grupo.getNombre());
                bundle.putString("correo", grupo.getIdGrupo());
                mAddFragmentListener.onAddFragment(InfoGrupoActivity.this, 
                    ChatsActivity.newInstance(bundle));
            }
        });

        // Click en nombre para editar (solo admin)
        infoGrupoNombre1.setOnClickListener(v -> {
            if (esAdmin) mostrarDialogoEditarNombre();
        });

        // Botón salir del grupo
        View btnSalir = getView().findViewById(R.id.btn_salir_grupo);
        if (btnSalir != null) {
            btnSalir.setOnClickListener(v -> mostrarDialogoSalirGrupo());
        }
    }

    private void cargarDatosGrupo() {
        if (idGrupo.isEmpty()) return;

        grupo = dbWorker.obtenerGrupo(idGrupo);
        if (grupo == null) {
            Toast.makeText(getContext(), "Grupo no encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        esAdmin = grupo.esAdmin(YouChatApplication.correo) || grupo.esCreador(YouChatApplication.correo);

        // Nombre del grupo
        infoGrupoNombre1.setText(grupo.getNombre());
        infoGrupoNombre2.setText(grupo.getNombre());

        // Descripción
        if (!grupo.getDescripcion().isEmpty()) {
            infoGrupoDescripcion.setText(grupo.getDescripcion());
            infoGrupoDescripcion.setVisibility(View.VISIBLE);
        }

        // Creador
        String creadorNombre = dbWorker.obtenerNombre(grupo.getCreador());
        infoGrupoCreacion.setText("Creado por " + creadorNombre);

        // Foto del grupo
        if (!grupo.getFotoGrupo().isEmpty()) {
            Glide.with(this).load(grupo.getFotoGrupo())
                    .error(R.drawable.contacts).into(imgGrupo);
        }

        // Miembros
        miembros = dbWorker.obtenerMiembrosGrupo(idGrupo);
        admins = dbWorker.obtenerAdminsGrupo(idGrupo);

        infoGrupoCantMiembros.setText(miembros.size() + " miembros");

        adaptadorMiembros = new AdaptadorDatosMiembroGrupo(getContext(), miembros, admins,
                grupo.getCreador(), esAdmin);
        infoGrupoListaMiembros.setLayoutManager(new LinearLayoutManager(getContext()));
        infoGrupoListaMiembros.setAdapter(adaptadorMiembros);
    }

    private void mostrarDialogoEditarNombre() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_confirm, null);
        builder.setView(dialogView);

        TextView textIcono = dialogView.findViewById(R.id.text_icono);
        TextView textEliminar = dialogView.findViewById(R.id.text_eliminar);
        final EditText input = dialogView.findViewById(R.id.et_input);
        
        textIcono.setText("Editar nombre");
        textEliminar.setText("Ingresa el nuevo nombre del grupo");
        input.setVisibility(View.VISIBLE);
        input.setText(grupo.getNombre());

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        dialogView.findViewById(R.id.btn_ok).setOnClickListener(v -> {
            String nuevoNombre = input.getText().toString().trim();
            if (!nuevoNombre.isEmpty()) {
                dbWorker.actualizarNombreGrupo(idGrupo, nuevoNombre);
                grupo.setNombre(nuevoNombre);
                infoGrupoNombre1.setText(nuevoNombre);
                infoGrupoNombre2.setText(nuevoNombre);
                Toast.makeText(getContext(), "Nombre actualizado", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        dialogView.findViewById(R.id.btn_cancel).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void mostrarDialogoSalirGrupo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_confirm, null);
        builder.setView(dialogView);

        TextView textIcono = dialogView.findViewById(R.id.text_icono);
        TextView textEliminar = dialogView.findViewById(R.id.text_eliminar);

        textIcono.setText("Salir del grupo");
        textEliminar.setText("¿Estás seguro que deseas salir de \"" + grupo.getNombre() + "\"?");

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        dialogView.findViewById(R.id.btn_ok).setOnClickListener(v -> {
            dbWorker.eliminarMiembroGrupo(idGrupo, YouChatApplication.correo);
            Toast.makeText(getContext(), "Has salido del grupo", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            if (getActivity() != null) getActivity().onBackPressed();
        });

        dialogView.findViewById(R.id.btn_cancel).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
