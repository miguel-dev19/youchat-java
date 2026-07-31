package cu.alexgi.youchat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cu.alexgi.youchat.base_datos.DBWorker;
import cu.alexgi.youchat.items.ItemChat;
import cu.alexgi.youchat.items.ItemUsuario;


public class FeedBack implements Thread.UncaughtExceptionHandler {

    private Context mContext;
    private String correo;
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    public FeedBack(Context context, String correo) {
        mContext = context.getApplicationContext();
        this.correo = correo;
        uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread t, Throwable ex) {

        try {
            getInfoPhone(ex);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        mContext.startService(new Intent(mContext, ChatService.class));

        if(uncaughtExceptionHandler!=null){
            uncaughtExceptionHandler.uncaughtException(t,ex);
        }
        else {
            if(!t.isInterrupted()) t.interrupt();
            Process.killProcess(Process.myPid());
        }
    }

    private void getInfoPhone(Throwable ex) throws PackageManager.NameNotFoundException {
        String time = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        String info = "", error = "";
        info+=time;
        info+="\n";
        info+="App Version: "+pi.versionName+" Code Version: "+pi.versionCode;
        info+="\n";
        info+="OS Version："+ Build.VERSION.RELEASE+" SDK: "+Build.VERSION.SDK_INT;
        info+="\n";
        info+="Marca: "+Build.MANUFACTURER;
        info+="\n";
        info+="Model: "+Build.MODEL;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            error+="\n";
//            error+="CPU ABI: "+Build.SUPPORTED_ABIS;
//        }
//        error+="Message:\n"+ex.toString();
//        if(ex.getLocalizedMessage()!=null){
//            error+="\n\n";
//            error+="LocalizedMessage:\n"+ex.getLocalizedMessage();
//        }
//        if(ex.getCause()!=null){
//            error+="\n\n";
//            error+="Cause:\n"+ex.getCause().toString();
//        }
        StringWriter stringWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
//        error+="\n\n";
        error+="Message Error:\n"+stringWriter.toString();

        DBWorker dbWorker = new DBWorker(mContext);
        if(correo.equals("alexgi@nauta.cu")
                || correo.equals("octaviog97@nauta.cu")
                || correo.equals("niuvis2019@nauta.cu")){
            String fechaEntera = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault()).format(new Date());
            String id="YCchat"+correo+""+fechaEntera;
            String hora = Convertidor.conversionHora(fechaEntera);
            String fecha = Convertidor.conversionFecha(fechaEntera);

            ItemChat newChat=new ItemChat( id,
                    2, 3, correo,
                    info+"\n\n"+error,
                    "",
                    hora, fecha, "", correo, false, fechaEntera,false,"",0,true);
            dbWorker.insertarChat(newChat);
            dbWorker.insertarNuevoUsuario(new ItemUsuario(correo));
            dbWorker.actualizarCantMensajesNoVistosX(correo,1);
            dbWorker.actualizarUltMsgUsuario(newChat);
        }

        dbWorker.insertarDescripcionError(info,error);
    }
}

        /*@Override
        public void uncaughtException (Thread t, Throwable e){
            Log.e("FeedBack", "" + t.getName());
            Log.e("FeedBack", "" + e.toString());
            t.interrupt();

            Process.killProcess(Process.myPid());
        }

        private void getInfoPhone () {
        }*/