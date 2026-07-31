package cu.alexgi.youchat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cu.alexgi.youchat.CircleImageView;
import cu.alexgi.youchat.R;

/**
 * Created by Ahmed Adel on 5/8/17.
 */

public class AdaptadorColorTarjeta extends RecyclerView.Adapter<AdaptadorColorTarjeta.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<Integer> colorPickerColors;
    private OnColorPickerClickListener onColorPickerClickListener;

    AdaptadorColorTarjeta(@NonNull Context context, @NonNull List<Integer> colorPickerColors) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.colorPickerColors = colorPickerColors;
    }

    public AdaptadorColorTarjeta(@NonNull Context context) {
        this(context, getDefaultColors(context));
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.color_picker_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.colorPickerView.setCircleBackgroundColor(colorPickerColors.get(position));
    }

    @Override
    public int getItemCount() {
        return colorPickerColors.size();
    }

    /*private void buildColorPickerView(View view, int colorCode) {
        view.setVisibility(View.VISIBLE);

        ShapeDrawable biggerCircle = new ShapeDrawable(new OvalShape());
        biggerCircle.setIntrinsicHeight(20);
        biggerCircle.setIntrinsicWidth(20);
        biggerCircle.setBounds(new Rect(0, 0, 20, 20));
        biggerCircle.getPaint().setColor(colorCode);

        ShapeDrawable smallerCircle = new ShapeDrawable(new OvalShape());
        smallerCircle.setIntrinsicHeight(5);
        smallerCircle.setIntrinsicWidth(5);
        smallerCircle.setBounds(new Rect(0, 0, 5, 5));
        smallerCircle.getPaint().setColor(Color.WHITE);
        smallerCircle.setPadding(10, 10, 10, 10);
        Drawable[] drawables = {smallerCircle, biggerCircle};

        LayerDrawable layerDrawable = new LayerDrawable(drawables);

        view.setBackgroundDrawable(layerDrawable);
    }*/

    public void setOnColorPickerClickListener(OnColorPickerClickListener onColorPickerClickListener) {
        this.onColorPickerClickListener = onColorPickerClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView colorPickerView;

        public ViewHolder(View itemView) {
            super(itemView);
            colorPickerView = itemView.findViewById(R.id.color_picker_view);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onColorPickerClickListener != null)
                        onColorPickerClickListener.onColorPickerClickListener(colorPickerColors.get(getAdapterPosition()), getAdapterPosition());
                }
            });
        }
    }

    public interface OnColorPickerClickListener {
        void onColorPickerClickListener(int colorCode, int pos);
    }

    public static List<Integer> getDefaultColors(Context context) {
        ArrayList<Integer> colorPickerColors = new ArrayList<>();
        colorPickerColors.add(ContextCompat.getColor(context, R.color.card1));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.card2));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.card3));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.card4));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.card5));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.card6));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.card7));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.card8));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.card9));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.card10));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.card11));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.card12));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.card13));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.card14));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.card15));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.card16));
        return colorPickerColors;
    }
}
