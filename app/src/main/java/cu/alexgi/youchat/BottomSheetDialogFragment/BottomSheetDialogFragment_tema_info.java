package cu.alexgi.youchat.BottomSheetDialogFragment;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import cu.alexgi.youchat.R;
import cu.alexgi.youchat.Utils;
import cu.alexgi.youchat.YouChatApplication;

import static cu.alexgi.youchat.MainActivity.mainActivity;

public class BottomSheetDialogFragment_tema_info extends BottomSheetDialogFragment {

    private static String option, text, textCopy;

    public static BottomSheetDialogFragment_tema_info newInstance(String o, String t, String copy) {
        option = o;
        text = t;
        textCopy = copy;
        return new BottomSheetDialogFragment_tema_info();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_dialog_tema_info, container, false);
        TextView bs_ajustes_option = v.findViewById(R.id.bs_ajustes_option);
        TextView bs_ajustes_explicar = v.findViewById(R.id.bs_ajustes_explicar);
        View copy_tema_string = v.findViewById(R.id.copy_tema_string);

        bs_ajustes_option.setText(option);
        bs_ajustes_explicar.setText(text);

        copy_tema_string.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipData c = ClipData.newPlainText("YouChatCopy", textCopy);
                YouChatApplication.clipboard.setPrimaryClip(c);
                Utils.ShowToastAnimated(mainActivity,"Texto copiado al portapapeles",R.raw.voip_invite);

//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youchat.theme.cu/893252k35k289kj2g6"));
//                Bundle bundle = new Bundle();
//                bundle.putString("theme",textCopy);
//                intent.putExtras(bundle);
//                intent.setPackage("cu.alexgi.youchat");
//                startActivity(intent);
            }
        });
        return v;
    }
}
