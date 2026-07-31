package cu.alexgi.youchat.BottomSheetDialogFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import cu.alexgi.youchat.R;
import cu.alexgi.youchat.YouChatApplication;

public class BottomSheetDialogFragment_Cambios extends BottomSheetDialogFragment {

    public static BottomSheetDialogFragment_Cambios newInstance() {
        return new BottomSheetDialogFragment_Cambios();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_dialog_cambios, container, false);
        View ll_cambios_beta = v.findViewById(R.id.ll_cambios_beta);
        if(YouChatApplication.es_beta_tester)
            ll_cambios_beta.setVisibility(View.VISIBLE);
        else ll_cambios_beta.setVisibility(View.GONE);

        return v;
    }
}
