package cu.alexgi.youchat;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cu.alexgi.youchat.adapters.AdaptadorDatosStickers;
import cu.alexgi.youchat.items.ItemFolderSticker;


public class ViewStickersFragment extends Fragment {

    private static Context context;
    private static ItemFolderSticker folderSticker;

    public ViewStickersFragment() {
        // Required empty public constructor
    }

    public static ViewStickersFragment newInstance(Context c, ItemFolderSticker fs) {
        ViewStickersFragment fragment = new ViewStickersFragment();
        context = c;
        folderSticker = fs;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_stickers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView lista_view_sticker = view.findViewById(R.id.lista_view_sticker);

        AdaptadorDatosStickers adaptadorDatosStickers = new AdaptadorDatosStickers(folderSticker.getStickers());
        lista_view_sticker.setLayoutManager(new GridLayoutManager(context, 3));
        lista_view_sticker.setAdapter(adaptadorDatosStickers);
        lista_view_sticker.setHasFixedSize(true);
    }
}