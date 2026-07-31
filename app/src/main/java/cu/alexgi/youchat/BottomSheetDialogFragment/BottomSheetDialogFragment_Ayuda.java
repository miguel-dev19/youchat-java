package cu.alexgi.youchat.BottomSheetDialogFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import cu.alexgi.youchat.R;

public class BottomSheetDialogFragment_Ayuda extends BottomSheetDialogFragment {

    public static BottomSheetDialogFragment_Ayuda newInstance() {
        return new BottomSheetDialogFragment_Ayuda();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_dialog_ayuda, container, false);
        return v;
    }
}
