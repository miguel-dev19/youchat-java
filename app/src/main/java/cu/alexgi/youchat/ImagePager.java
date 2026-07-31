package cu.alexgi.youchat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.chrisbanes.photoview.PhotoView;

import static cu.alexgi.youchat.MainActivity.mainActivity;

public class ImagePager extends Fragment {

    private static String ruta;

    public static ImagePager newInstance(String r) {
        ImagePager fragment = new ImagePager();
        ruta = r;
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_image_pager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(mainActivity!=null) mainActivity.cambiarColorStatusBar("#ff000000");
        Glide.with(MainActivity.context).load(ruta).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                .error(R.drawable.placeholder).into((PhotoView)view.findViewById(R.id.image_view));
    }

    @Override
    public void onDestroy() {
        if(mainActivity!=null) mainActivity.cambiarColorStatusBar(YouChatApplication.itemTemas.getStatus_bar());
        super.onDestroy();
    }
}
