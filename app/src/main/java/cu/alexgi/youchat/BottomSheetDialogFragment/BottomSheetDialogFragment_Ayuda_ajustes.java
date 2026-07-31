package cu.alexgi.youchat.BottomSheetDialogFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import cu.alexgi.youchat.R;

public class BottomSheetDialogFragment_Ayuda_ajustes extends BottomSheetDialogFragment {

    private static String option, text;

    public static BottomSheetDialogFragment_Ayuda_ajustes newInstance(String o, String t) {
        option = o;
        text = t;
        return new BottomSheetDialogFragment_Ayuda_ajustes();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_dialog_ayuda_ajustes, container, false);
        TextView bs_ajustes_option = v.findViewById(R.id.bs_ajustes_option);
        TextView bs_ajustes_explicar = v.findViewById(R.id.bs_ajustes_explicar);

        bs_ajustes_option.setText(option);
        bs_ajustes_explicar.setText(text);

        return v;
    }
}
