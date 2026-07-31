package cu.alexgi.youchat;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.api.widget.Widget;
import com.yanzhenjie.album.impl.OnItemClickListener;
import com.yanzhenjie.album.widget.divider.Api21ItemDivider;
import com.yanzhenjie.album.widget.divider.Divider;

import java.util.ArrayList;

import cu.alexgi.youchat.adapters.AdaptadorDatosAlbum;
import cu.alexgi.youchat.swipebackfragment.BaseSwipeBackFragment;

import static cu.alexgi.youchat.MainActivity.context;

public class ImageActivity extends BaseSwipeBackFragment {

    private AdaptadorDatosAlbum mAdapter;
    private ArrayList<AlbumFile> mAlbumFiles;
    private View back;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.activity_album, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        Divider divider = new Api21ItemDivider(Color.TRANSPARENT, 10, 10);
        recyclerView.addItemDecoration(divider);

        mAdapter = new AdaptadorDatosAlbum(context, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                previewImage(position);
            }
        });
        recyclerView.setAdapter(mAdapter);
        mAlbumFiles=YouChatApplication.imagenesChatAlbumFile;
        mAdapter.notifyDataSetChanged(mAlbumFiles);

        back = view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
//                Navigation.findNavController(v).navigateUp();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void previewImage(int position) {
        if (mAlbumFiles == null || mAlbumFiles.size() == 0) {
            Utils.ShowToastAnimated(getActivity(),"No hay imágenes",R.raw.chats_infotip);
        } else {
            Album.galleryAlbum(this)
                    .checkable(false)
                    .checkedList(mAlbumFiles)
                    .currentPosition(position)
                    .widget(
                            Widget.newDarkBuilder(context)
                                    .title("Galería de chat")
                                    .build()
                    )
                    .start();
        }
    }

}