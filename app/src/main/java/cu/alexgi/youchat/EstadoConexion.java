package cu.alexgi.youchat;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

/*
Modo de uso
        EstadoConexion estadoConexion = new EstadoConexion(context);
        estadoConexion.setOnConexionListener(new EstadoConexion.OnConexionListener() {
            @Override
            public void conectado() {
                Toast.makeText(context, "Conectado", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void desconectado() {
                Toast.makeText(context, "Desconectado", Toast.LENGTH_SHORT).show();
            }
        });
        estadoConexion.comenzar();
 */

public class EstadoConexion {

    private Context context;
    private long tiempoEspera;
    private boolean conectado;
    private boolean estaCorriendo;

    public EstadoConexion(Context context) {
        this.context = context;
        tiempoEspera = 5000L;
        conectado = false;
    }

    public EstadoConexion(Context context, long tiempoEspera) {
        this.context = context;
        this.tiempoEspera = tiempoEspera;
        conectado = false;
    }

    public void comenzar(){
        estaCorriendo = true;
        verificarConexion();
    }

    public boolean estaConectado(){
        return conectado;
    }

    public void parar(){
        estaCorriendo = false;
    }

    public void setTiempoEspera(long tiempoEspera) {
        this.tiempoEspera = tiempoEspera;
    }

    private synchronized void verificarConexion(){
        ConnectivityManager conex;
        NetworkInfo state_conex;
        conex = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        state_conex = conex.getActiveNetworkInfo();
        if (state_conex != null && state_conex.isConnected()){
            if(!conectado){
                conectado = true;
                if(onConexionListener!=null)
                    onConexionListener.conectado();
            }
        }
        else if(conectado){
            conectado = false;
            if(onConexionListener!=null)
                onConexionListener.desconectado();
        }

        if(tiempoEspera>0){
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {
                    if(estaCorriendo)
                        verificarConexion();
                }
            },tiempoEspera);
        }
        else{
            new Handler().post(new Runnable(){
                @Override
                public void run() {
                    if(estaCorriendo)
                        verificarConexion();
                }
            });
        }
    }

    private OnConexionListener onConexionListener;
    public void setOnConexionListener(OnConexionListener onConexionListener){
        this.onConexionListener = onConexionListener;
    }
    public interface OnConexionListener{
        void conectado();
        void desconectado();
    }
}
