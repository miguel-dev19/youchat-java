package cu.alexgi.youchat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cu.alexgi.youchat.Charts.charts.BarChart;
import cu.alexgi.youchat.Charts.charts.Chart;
import cu.alexgi.youchat.Charts.components.AxisBase;
import cu.alexgi.youchat.Charts.components.XAxis;
import cu.alexgi.youchat.Charts.data.BarData;
import cu.alexgi.youchat.Charts.data.BarDataSet;
import cu.alexgi.youchat.Charts.data.BarEntry;
import cu.alexgi.youchat.Charts.formatter.IAxisValueFormatter;
import cu.alexgi.youchat.Charts.interfaces.datasets.IBarDataSet;
import cu.alexgi.youchat.Charts.interfaces.datasets.IDataSet;
import cu.alexgi.youchat.items.ItemEstadisticaPersonal;

import static cu.alexgi.youchat.MainActivity.context;

public class EstadisticasGeneralesFragment extends Fragment {

    private static ArrayList<ItemEstadisticaPersonal> datos_personales;
    private BarChart chart;
    private View ll_fondo;
    private TextView tv_resumen;

    public EstadisticasGeneralesFragment() {
        // Required empty public constructor
    }

    public static EstadisticasGeneralesFragment newInstance(ArrayList<ItemEstadisticaPersonal> x) {
        EstadisticasGeneralesFragment fragment = new EstadisticasGeneralesFragment();
        datos_personales = x;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_estadisticas_generales, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_resumen = view.findViewById(R.id.tv_resumen);
        ll_fondo = view.findViewById(R.id.ll_fondo);
        ll_fondo.setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_interior()));

        chart = view.findViewById(R.id.chart);
        chart.getDescription().setEnabled(false);
        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(60);
        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);

        String [] texto = new String[7];
        texto[0] = "Mensajes";
        texto[1] = "Imágenes";
        texto[2] = "Audios";
        texto[3] = "Archivos";
        texto[4] = "Stickers";
        texto[5] = "Now";
        texto[6] = "Post";

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
//        xAxis.setSpaceMin(3f);
        xAxis.setTextSize(9f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Log.e("VALUES", ""+(int) Math.abs(value % 7));
                return texto[(int) Math.abs(value % 7)];
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NestedScrollView neste = view.findViewById(R.id.neste);
            float scrollTotal = (float) Utils.dpToPx(MainActivity.context,230);
            View view_fondo = view.findViewById(R.id.view_fondo);
            neste.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    float per = (float)(scrollY/scrollTotal);
                    if(per<0) per=0;
                    else if(per>1) per=1;
                    view_fondo.setAlpha(per);
                }
            });
        }


//        YAxis leftAxis = chart.getAxisLeft();
//        leftAxis.setTypeface(tfLight);
//        leftAxis.setLabelCount(8, false);
//        leftAxis.setValueFormatter(custom);
//        leftAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART);
//        leftAxis.setSpaceTop(15f);
//        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
//
//        YAxis rightAxis = chart.getAxisRight();
//        rightAxis.setDrawGridLines(false);
//        rightAxis.setTypeface(tfLight);
//        rightAxis.setLabelCount(8, false);
//        rightAxis.setValueFormatter(custom);
//        rightAxis.setSpaceTop(15f);
//        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        chart.getAxisLeft().setDrawGridLines(false);
        // add a nice and smooth animation
        chart.animateY(1500);
        chart.setDrawValueAboveBar(true);


//        int endColor1 = ContextCompat.getColor(this, android.R.color.holo_blue_dark);
//        int endColor2 = ContextCompat.getColor(this, android.R.color.holo_purple);
//        int endColor3 = ContextCompat.getColor(this, android.R.color.holo_green_dark);
//        int endColor4 = ContextCompat.getColor(this, android.R.color.holo_red_dark);
//        int endColor5 = ContextCompat.getColor(this, android.R.color.holo_orange_dark);
        int [] colors = new int[8];


        colors[0] = ContextCompat.getColor(getContext(), R.color.card1);
        colors[1] = ContextCompat.getColor(getContext(), R.color.card2);
        colors[2] = ContextCompat.getColor(getContext(), R.color.card3);
        colors[3] = ContextCompat.getColor(getContext(), R.color.card4);
        colors[4] = ContextCompat.getColor(getContext(), R.color.card5);

        colors[5] = ContextCompat.getColor(getContext(), R.color.card6);
        colors[6] = ContextCompat.getColor(getContext(), R.color.card7);
//        colors[7] = ContextCompat.getColor(getContext(), R.color.card8);
//        colors[8] = ContextCompat.getColor(getContext(), R.color.card9);
//        colors[9] = ContextCompat.getColor(getContext(), R.color.card10);
//
//        colors[10] = ContextCompat.getColor(getContext(), R.color.card11);
//        colors[11] = ContextCompat.getColor(getContext(), R.color.card12);
//        colors[12] = ContextCompat.getColor(getContext(), R.color.card13);
//        colors[13] = ContextCompat.getColor(getContext(), R.color.card14);


//        texto[2] = "Audios";
//        texto[3] = "Archivos";
//        texto[4] = "Stickers";
//
//        texto[0] = "Mensajes";
//        texto[1] = "Imágenes";
//        texto[2] = "Audios";
//        texto[3] = "Archivos";
        chart.getLegend().setEnabled(false);//.setExtra(colors,texto);





        int cant_msg_env = 0;
        long cant_msg_env_mg = 0;
        int cant_msg_rec = 0;
        long cant_msg_rec_mg = 0;
        int cant_img_env = 0;
        long cant_img_env_mg = 0;
        int cant_img_rec = 0;
        long cant_img_rec_mg = 0;
        int cant_aud_env = 0;
        long cant_aud_env_mg = 0;
        int cant_aud_rec = 0;
        long cant_aud_rec_mg = 0;
        int cant_arc_env = 0;
        long cant_arc_env_mg = 0;
        int cant_arc_rec = 0;
        long cant_arc_rec_mg = 0;
        int cant_sti_env = 0;
        long cant_sti_env_mg = 0;
        int cant_sti_rec = 0;
        long cant_sti_rec_mg = 0;

        int cant_est_rec = 0;
        long cant_est_rec_mg = 0;
        int cant_act_per_rec = 0;
        long cant_act_per_rec_mg = 0;

        int l = datos_personales.size();
        for(int i=0; i<l; i++){
            ItemEstadisticaPersonal temp = datos_personales.get(i);
            cant_msg_env += temp.getCant_msg_env();
            cant_msg_env_mg += temp.getCant_msg_env_mg();
            cant_msg_rec += temp.getCant_msg_rec();
            cant_msg_rec_mg += temp.getCant_msg_rec_mg();
            cant_img_env += temp.getCant_img_env();
            cant_img_env_mg += temp.getCant_img_env_mg();
            cant_img_rec += temp.getCant_img_rec();
            cant_img_rec_mg += temp.getCant_img_rec_mg();
            cant_aud_env += temp.getCant_aud_env();
            cant_aud_env_mg += temp.getCant_aud_env_mg();
            cant_aud_rec += temp.getCant_aud_rec();
            cant_aud_rec_mg += temp.getCant_aud_rec_mg();
            cant_arc_env += temp.getCant_arc_env();
            cant_arc_env_mg += temp.getCant_arc_env_mg();
            cant_arc_rec += temp.getCant_arc_rec();
            cant_arc_rec_mg += temp.getCant_arc_rec_mg();
            cant_sti_env += temp.getCant_sti_env();
            cant_sti_env_mg += temp.getCant_sti_env_mg();
            cant_sti_rec += temp.getCant_sti_rec();
            cant_sti_rec_mg += temp.getCant_sti_rec_mg();
            cant_est_rec += temp.getCant_est_rec();
            cant_est_rec_mg += temp.getCant_est_rec_mg();
            cant_act_per_rec += temp.getCant_act_per_rec();
            cant_act_per_rec_mg += temp.getCant_act_per_rec_mg();
        }

        int cant_total = 0;
        long mega_total_env = 0;
        long mega_total_rec = 0;
        long mega_total = 0;

        TextView tv_msg_cant_env_mg = view.findViewById(R.id.tv_msg_cant_env_mg);
        TextView tv_msg_cant_rec_mg = view.findViewById(R.id.tv_msg_cant_rec_mg);
        TextView tv_msg_cant = view.findViewById(R.id.tv_msg_cant);
        tv_msg_cant_env_mg.setText(Utils.convertirBytes(cant_msg_env_mg));
        tv_msg_cant_rec_mg.setText(Utils.convertirBytes(cant_msg_rec_mg));
        tv_msg_cant.setText("Cantidad: "+(cant_msg_env+cant_msg_rec));
        cant_total +=(cant_msg_env+cant_msg_rec);
        mega_total_env +=cant_msg_env_mg;
        mega_total_rec +=cant_msg_rec_mg;

        TextView tv_img_cant_env_mg = view.findViewById(R.id.tv_img_cant_env_mg);
        TextView tv_img_cant_rec_mg = view.findViewById(R.id.tv_img_cant_rec_mg);
        TextView tv_img_cant = view.findViewById(R.id.tv_img_cant);
        tv_img_cant_env_mg.setText(Utils.convertirBytes(cant_img_env_mg));
        tv_img_cant_rec_mg.setText(Utils.convertirBytes(cant_img_rec_mg));
        tv_img_cant.setText("Cantidad: "+(cant_img_env+cant_img_rec));
        cant_total +=(cant_img_env+cant_img_rec);
        mega_total_env +=cant_img_env_mg;
        mega_total_rec +=cant_img_rec_mg;

        TextView tv_aud_cant_env_mg = view.findViewById(R.id.tv_aud_cant_env_mg);
        TextView tv_aud_cant_rec_mg = view.findViewById(R.id.tv_aud_cant_rec_mg);
        TextView tv_aud_cant = view.findViewById(R.id.tv_aud_cant);
        tv_aud_cant_env_mg.setText(Utils.convertirBytes(cant_aud_env_mg));
        tv_aud_cant_rec_mg.setText(Utils.convertirBytes(cant_aud_rec_mg));
        tv_aud_cant.setText("Cantidad: "+(cant_aud_env+cant_aud_rec));
        cant_total +=(cant_aud_env+cant_aud_rec);
        mega_total_env +=cant_aud_env_mg;
        mega_total_rec +=cant_aud_rec_mg;

        TextView tv_arc_cant_env_mg = view.findViewById(R.id.tv_arc_cant_env_mg);
        TextView tv_arc_cant_rec_mg = view.findViewById(R.id.tv_arc_cant_rec_mg);
        TextView tv_arc_cant = view.findViewById(R.id.tv_arc_cant);
        tv_arc_cant_env_mg.setText(Utils.convertirBytes(cant_arc_env_mg));
        tv_arc_cant_rec_mg.setText(Utils.convertirBytes(cant_arc_rec_mg));
        tv_arc_cant.setText("Cantidad: "+(cant_arc_env+cant_arc_rec));
        cant_total +=(cant_arc_env+cant_arc_rec);
        mega_total_env +=cant_arc_env_mg;
        mega_total_rec +=cant_arc_rec_mg;

        TextView tv_sti_cant_env_mg = view.findViewById(R.id.tv_sti_cant_env_mg);
        TextView tv_sti_cant_rec_mg = view.findViewById(R.id.tv_sti_cant_rec_mg);
        TextView tv_sti_cant = view.findViewById(R.id.tv_sti_cant);
        tv_sti_cant_env_mg.setText(Utils.convertirBytes(cant_sti_env_mg));
        tv_sti_cant_rec_mg.setText(Utils.convertirBytes(cant_sti_rec_mg));
        tv_sti_cant.setText("Cantidad: "+(cant_sti_env+cant_sti_rec));
        cant_total +=(cant_sti_env+cant_sti_rec);
        mega_total_env +=cant_sti_env_mg;
        mega_total_rec +=cant_sti_rec_mg;

        TextView tv_est_cant_env_mg = view.findViewById(R.id.tv_est_cant_env_mg);
        TextView tv_est_cant_rec_mg = view.findViewById(R.id.tv_est_cant_rec_mg);
        TextView tv_est_cant = view.findViewById(R.id.tv_est_cant);
        tv_est_cant_env_mg.setText(Utils.convertirBytes(YouChatApplication.mega_estados_subidos));
        tv_est_cant_rec_mg.setText(Utils.convertirBytes(cant_est_rec_mg));
        tv_est_cant.setText("Cantidad: "+(YouChatApplication.cant_estados_subidos+cant_est_rec));
        cant_total +=(YouChatApplication.cant_estados_subidos+cant_est_rec);
        mega_total_env +=YouChatApplication.mega_estados_subidos;
        mega_total_rec +=cant_est_rec_mg;

        TextView tv_pos_cant_env_mg = view.findViewById(R.id.tv_pos_cant_env_mg);
        TextView tv_pos_cant_rec_mg = view.findViewById(R.id.tv_pos_cant_rec_mg);
        TextView tv_pos_cant = view.findViewById(R.id.tv_pos_cant);
        tv_pos_cant_env_mg.setText(Utils.convertirBytes(YouChatApplication.mega_post_enviados));
        tv_pos_cant_rec_mg.setText(Utils.convertirBytes(YouChatApplication.mega_post_recibidos));
        tv_pos_cant.setText("Cantidad: "+(YouChatApplication.cant_post_rye));
        cant_total +=(YouChatApplication.cant_post_rye);
        mega_total_env +=YouChatApplication.mega_post_enviados;
        mega_total_rec +=YouChatApplication.mega_post_recibidos;

        TextView tv_buzon_cant_env_mg = view.findViewById(R.id.tv_buzon_cant_env_mg);
        TextView tv_buzon_cant_rec_mg = view.findViewById(R.id.tv_buzon_cant_rec_mg);
        TextView tv_buzon_cant = view.findViewById(R.id.tv_buzon_cant);
        tv_buzon_cant_env_mg.setText(Utils.convertirBytes(YouChatApplication.mega_buzon_enviados));
        tv_buzon_cant_rec_mg.setText(Utils.convertirBytes(YouChatApplication.mega_buzon_recibidos));
        tv_buzon_cant.setText("Cantidad: "+(YouChatApplication.cant_buzon_rye));
        cant_total +=(YouChatApplication.cant_buzon_rye);
        mega_total_env +=YouChatApplication.mega_buzon_enviados;
        mega_total_rec +=YouChatApplication.mega_buzon_recibidos;

        TextView tv_per_cant_env_mg = view.findViewById(R.id.tv_per_cant_env_mg);
        TextView tv_per_cant_rec_mg = view.findViewById(R.id.tv_per_cant_rec_mg);
        TextView tv_per_cant = view.findViewById(R.id.tv_per_cant);
        tv_per_cant_env_mg.setText(Utils.convertirBytes(YouChatApplication.mega_act_perfil_env));
        tv_per_cant_rec_mg.setText(Utils.convertirBytes(cant_act_per_rec_mg));
        tv_per_cant.setText("Cantidad: "+(YouChatApplication.cant_act_perfil_env+cant_act_per_rec));
        cant_total +=(YouChatApplication.cant_act_perfil_env+cant_act_per_rec);
        mega_total_env +=YouChatApplication.mega_act_perfil_env;
        mega_total_rec +=cant_act_per_rec_mg;

        TextView tv_avi_cant_env_mg = view.findViewById(R.id.tv_avi_cant_env_mg);
        TextView tv_avi_cant_rec_mg = view.findViewById(R.id.tv_avi_cant_rec_mg);
        TextView tv_avi_cant = view.findViewById(R.id.tv_avi_cant);
        tv_avi_cant_env_mg.setText(Utils.convertirBytes(YouChatApplication.mega_x_serv_aviso_en_linea_env));
        tv_avi_cant_rec_mg.setText(Utils.convertirBytes(YouChatApplication.mega_x_serv_aviso_en_linea_rec));
        tv_avi_cant.setText("Cantidad: "+(YouChatApplication.cant_aviso_en_linea));
        cant_total +=(YouChatApplication.cant_aviso_en_linea);
        mega_total_env +=YouChatApplication.mega_x_serv_aviso_en_linea_env;
        mega_total_rec +=YouChatApplication.mega_x_serv_aviso_en_linea_rec;

        TextView tv_cop_cant_env_mg = view.findViewById(R.id.tv_cop_cant_env_mg);
        TextView tv_cop_cant_rec_mg = view.findViewById(R.id.tv_cop_cant_rec_mg);
        TextView tv_cop_cant = view.findViewById(R.id.tv_cop_cant);
        tv_cop_cant_env_mg.setText(Utils.convertirBytes(YouChatApplication.mega_x_serv_bd_nube_env));
        tv_cop_cant_rec_mg.setText(Utils.convertirBytes(YouChatApplication.mega_x_serv_bd_nube_rec));
        tv_cop_cant.setText("Cantidad: "+(YouChatApplication.cant_bd_nube));
        cant_total +=(YouChatApplication.cant_bd_nube);
        mega_total_env +=YouChatApplication.mega_x_serv_bd_nube_env;
        mega_total_rec +=YouChatApplication.mega_x_serv_bd_nube_rec;

        TextView tv_lec_cant_env_mg = view.findViewById(R.id.tv_lec_cant_env_mg);
        TextView tv_lec_cant_rec_mg = view.findViewById(R.id.tv_lec_cant_rec_mg);
        TextView tv_lec_cant = view.findViewById(R.id.tv_lec_cant);
        tv_lec_cant_env_mg.setText(Utils.convertirBytes(YouChatApplication.mega_x_serv_confirmacion_lectura_env));
        tv_lec_cant_rec_mg.setText(Utils.convertirBytes(YouChatApplication.mega_x_serv_confirmacion_lectura_rec));
        tv_lec_cant.setText("Cantidad: "+(YouChatApplication.cant_confir_lectura));
        cant_total +=(YouChatApplication.cant_confir_lectura);
        mega_total_env +=YouChatApplication.mega_x_serv_confirmacion_lectura_env;
        mega_total_rec +=YouChatApplication.mega_x_serv_confirmacion_lectura_rec;

        TextView tv_din_cant_env_mg = view.findViewById(R.id.tv_din_cant_env_mg);
        TextView tv_din_cant_rec_mg = view.findViewById(R.id.tv_din_cant_rec_mg);
        TextView tv_din_cant = view.findViewById(R.id.tv_din_cant);
        tv_din_cant_env_mg.setText(Utils.convertirBytes(YouChatApplication.mega_x_serv_chat_dinamico_env));
        tv_din_cant_rec_mg.setText(Utils.convertirBytes(YouChatApplication.mega_x_serv_chat_dinamico_rec));
        tv_din_cant.setText("Cantidad: "+(YouChatApplication.cant_chat_din));
        cant_total +=(YouChatApplication.cant_chat_din);
        mega_total_env +=YouChatApplication.mega_x_serv_chat_dinamico_env;
        mega_total_rec +=YouChatApplication.mega_x_serv_chat_dinamico_rec;

        TextView tv_ree_cant_env_mg = view.findViewById(R.id.tv_ree_cant_env_mg);
        TextView tv_ree_cant_rec_mg = view.findViewById(R.id.tv_ree_cant_rec_mg);
        TextView tv_ree_cant = view.findViewById(R.id.tv_ree_cant);
        tv_ree_cant_env_mg.setText(Utils.convertirBytes(YouChatApplication.mega_reacciones_env));
        tv_ree_cant_rec_mg.setText(Utils.convertirBytes(YouChatApplication.mega_reacciones_rec));
        tv_ree_cant.setText("Cantidad: "+(YouChatApplication.cant_reacciones));
        cant_total +=(YouChatApplication.cant_reacciones);
        mega_total_env +=YouChatApplication.mega_reacciones_env;
        mega_total_rec +=YouChatApplication.mega_reacciones_rec;

        TextView tv_vis_cant_env_mg = view.findViewById(R.id.tv_vis_cant_env_mg);
        TextView tv_vis_cant_rec_mg = view.findViewById(R.id.tv_vis_cant_rec_mg);
        TextView tv_vis_cant = view.findViewById(R.id.tv_vis_cant);
        tv_vis_cant_env_mg.setText(Utils.convertirBytes(YouChatApplication.mega_vistos_estados_env));
        tv_vis_cant_rec_mg.setText(Utils.convertirBytes(YouChatApplication.mega_vistos_estados_rec));
        tv_vis_cant.setText("Cantidad: "+(YouChatApplication.cant_vistos_estados));
        cant_total +=(YouChatApplication.cant_vistos_estados);
        mega_total_env +=YouChatApplication.mega_vistos_estados_env;
        mega_total_rec +=YouChatApplication.mega_vistos_estados_rec;

        mega_total = mega_total_env+mega_total_rec;
        TextView tv_total_env_mg = view.findViewById(R.id.tv_total_env_mg);
        TextView tv_total_rec_mg = view.findViewById(R.id.tv_total_rec_mg);
        TextView tv_total = view.findViewById(R.id.tv_total);
        TextView tv_total_mg = view.findViewById(R.id.tv_total_mg);
        tv_total_mg.setText(Utils.convertirBytes(mega_total));
        tv_total_env_mg.setText(Utils.convertirBytes(mega_total_env));
        tv_total_rec_mg.setText(Utils.convertirBytes(mega_total_rec));
        tv_total.setText("Cantidad total: "+cant_total);




        ArrayList<BarEntry> values = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            float multi = 7;
//            float val = (float) (Math.random() * multi) + multi / 3;
            if(i==0) values.add(new BarEntry(i, cant_msg_env + cant_msg_rec));
            else if(i==1) values.add(new BarEntry(i, cant_img_env + cant_img_rec));
            else if(i==2) values.add(new BarEntry(i, cant_aud_env + cant_aud_rec));
            else if(i==3) values.add(new BarEntry(i, cant_arc_env + cant_arc_rec));
            else if(i==4) values.add(new BarEntry(i, cant_sti_env + cant_sti_rec));
            else if(i==5) values.add(new BarEntry(i, cant_est_rec + YouChatApplication.cant_estados_subidos));
            else if(i==6) values.add(new BarEntry(i, YouChatApplication.cant_post_rye));
        }

        BarDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, "");
            set1.setColors(colors);
            set1.setDrawValues(false);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            chart.setData(data);
            chart.setFitBars(true);
        }

        for (IBarDataSet set : chart.getData().getDataSets())
            ((BarDataSet)set).setBarBorderWidth(1.f);

        for (IDataSet set : chart.getData().getDataSets())
            set.setDrawValues(!set.isDrawValuesEnabled());

        chart.invalidate();
    }




    /*
    *

            case R.id.animateX: {
                chart.animateX(2000);
                break;
            }
            case R.id.actionSave: {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    saveToGallery();
                } else {
                    requestStoragePermission(chart);
                }
                break;
            }
    *
    */

    protected void saveToGallery(Chart chart, String name) {
        if(new Permisos(getActivity(), getContext()).requestPermissionAlmacenamiento()){
            if (chart.saveToGallery(name + "_" + System.currentTimeMillis(), 70))
                Utils.ShowToastAnimated(getActivity(), "Imagen guardada", R.raw.contact_check);
            else
                Utils.ShowToastAnimated(getActivity(), "Ha ocurrido un error", R.raw.error);
        }
    }

    public synchronized void tomarCapturaEstadisticasGral() {
        tv_resumen.setVisibility(View.VISIBLE);
        String fechaEntera = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a",
                Locale.getDefault()).format(new Date());
        String cad="Resumen "+fechaEntera+":\n";
        cad+="Calidad imagen: "+YouChatApplication.calidad+"%\n";
        cad+="Cantidad seguidores: "+MainActivity.dbWorker.obtenerCantSeguidores()+"\n";
        cad+="Cantidad seguidos: "+MainActivity.dbWorker.obtenerCantSiguiendoA()+"\n";
        cad+="Actualización de perfil: "+(YouChatApplication.actualizar_perfil?"si":"no")+"\n";
        tv_resumen.setText(cad);
        Utils.runOnUIThread(()->{
            Bitmap screenShoot = Utils.tomarImagenDeVista(ll_fondo);
            try {
                String orden = new SimpleDateFormat("yyyyMMddHHmmss",
                        Locale.getDefault()).format(new Date());
                File ruta = new File(YouChatApplication.RUTA_IMAGENES_ENVIADAS);
                if(!ruta.exists()) ruta.mkdirs();
                if(ruta.exists()){
                    File file = new File(YouChatApplication.RUTA_IMAGENES_ENVIADAS+"scrennshoot"+orden+".jpg");
                    FileOutputStream outputStream = new FileOutputStream(file);
                    screenShoot.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();

                    tv_resumen.setVisibility(View.GONE);
                    tv_resumen.setText("");

                    Uri uri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        uri = FileProvider.getUriForFile(context,
                                "cu.alexgi.youchat.fileprovider",file);
                    else uri = Uri.fromFile(file);

                    Intent mShareIntent = new Intent();
                    mShareIntent.setAction(Intent.ACTION_SEND);
                    mShareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mShareIntent.setType("image/*");
                    mShareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(mShareIntent);
                }
//                        startActivity(Intent.createChooser(mShareIntent,"Compartir captura con:"));
            } catch (IOException e) {
                e.printStackTrace();
                tv_resumen.setVisibility(View.GONE);
                tv_resumen.setText("");
            }
        }, 500);

    }
}