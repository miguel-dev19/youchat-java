package cu.alexgi.youchat.items;

import android.util.Log;

import com.vanniktech.emoji.EmojiInformation;
import com.vanniktech.emoji.EmojiUtils;

import cu.alexgi.youchat.YouChatApplication;

public class ItemChat {

    final public static int ESTADO_ESPERANDO = 1;
    final public static int ESTADO_ERROR = 2;
    final public static int ESTADO_ENVIADO = 3;
    final public static int ESTADO_RECIBIDO = 4;
    final public static int ESTADO_VISTO = 5;

    public static String YOUCHAT = "youchat";
    public static String KEY_CAT = "msg_cat";
    public static String KEY_VERSION = "version_contacto";
    public static String KEY_CANT_SEGUIDORES = "cant_seguidores";

    public static String KEY_LECTURA = "lectura";
    public static String KEY_ID = "msg_id";
    public static String KEY_TIPO= "msg_tipo";
    public static String KEY_CORREO= "msg_correo";
    public static String KEY_HORA = "msg_hora";
    public static String KEY_FECHA= "msg_fecha";
    public static String KEY_ID_MSG_RESP = "msg_id_resp";
    public static String KEY_REENVIADO = "msg_reenviado";
    public static String KEY_ORDEN = "msg_orden";

    //para la nueva version
//    public static String YOUCHAT = "a";
//    public static String KEY_CAT = "b";
//    public static String KEY_VERSION = "c";
//    public static String KEY_CANT_SEGUIDORES = "d";
//
//    public static String KEY_LECTURA = "e";
//    public static String KEY_ID = "f";
//    public static String KEY_TIPO= "g";
//    public static String KEY_CORREO= "h";
//    public static String KEY_HORA = "i";
//    public static String KEY_FECHA= "j";
//    public static String KEY_ID_MSG_RESP = "k";
//    public static String KEY_REENVIADO = "l";
//    public static String KEY_ORDEN = "m";

    public static String KEY_EDITADO = "n";
    public static String KEY_TIPO_LETRA_EST = "tl";
    public static String KEY_ESTA_ENCRIPTADO = "ee";

    ///para el perfil

    public static String PERFIL_KEY_TIENE_ALIAS= "perfil_alias";
    public static String PERFIL_KEY_TIENE_INFO= "perfil_info";
    public static String PERFIL_KEY_TIENE_IMG= "perfil_imagen";

    public static String PERFIL_KEY_TELEFONO= "perfil_telefono";
    public static String PERFIL_KEY_GENERO= "perfil_genero";
    public static String PERFIL_KEY_FECHA_NAC = "perfil_fecha_nac";
    public static String PERFIL_KEY_PROVINCIA= "perfil_provincia";
    public static String PERFIL_KEY_SEGUIDORES= "perfil_cant_seguidores";

    //para la nueva version
//    public static String PERFIL_KEY_TIENE_ALIAS= "n";
//    public static String PERFIL_KEY_TIENE_INFO= "o";
//    public static String PERFIL_KEY_TIENE_IMG= "p";
//
//    public static String PERFIL_KEY_TELEFONO= "q";
//    public static String PERFIL_KEY_GENERO= "r";
//    public static String PERFIL_KEY_FECHA_NAC = "s";
//    public static String PERFIL_KEY_PROVINCIA= "t";
//    public static String PERFIL_KEY_SEGUIDORES= "u";

    public static String PIE_DE_FIRMA = "YouChat";

    //id tipo_msg estado correo mensaje nombre_img ruta_img hora fecha id_msg_resp usuario_resp orden

    private String id;
    private int tipo_mensaje;

    private int estado;
    /*
     * 1-espera
     * 2-error al enviar
     * 3-enviado
     * 4-recibido
     * */

    private String correo;
    private String mensaje;

    private String ruta_Dato;

    private String hora;
    private String fecha;

    private String id_msg_resp;

    private String emisor;
    private boolean esReenviado;
    private String orden;

    //bdv5
    private boolean editado;

    //bdv7
    private String      id_mensaje;
    private int         peso;
    private boolean     descargado;

    //no va en base de datos
    private boolean seleccionado;

    public ItemChat(String correo, String texto) {
        this.id = "-r-";
        this.tipo_mensaje = 2;
        this.estado = 1;
        this.correo = correo;
        this.mensaje = "";
        this.ruta_Dato = "";
        this.hora = "";
        this.fecha = "";
        this.id_msg_resp = texto;
        this.emisor = "";
        this.esReenviado = false;
        this.orden = "";
        editado = false;
        id_mensaje = "";
        peso=0;
        descargado=true;

        seleccionado = false;
    }

    public ItemChat(String id, int tipo_mensaje, String correo, String mensaje) {
        this.id = id;
        this.tipo_mensaje = tipo_mensaje;
        this.estado = 1;
        this.correo = correo;
        this.mensaje = mensaje;
        this.ruta_Dato = "";
        this.hora = "";
        this.fecha = "";
        this.id_msg_resp = "";
        this.emisor = "";
        this.esReenviado = false;
        this.orden = "";
        editado = false;
        id_mensaje = "";
        peso=0;
        descargado=true;

        seleccionado = false;
    }

    public ItemChat(String id, int tipo_men, int estado, String correo,
                    String mensaj, String ruta_Dato,
                    String hora, String fecha, String id_msg_res, String emisor,
                    boolean esReenviado, String orden, boolean eedit,
                    String id_mensaje, int peso, boolean descargado) {
        this.id = id;
        this.tipo_mensaje = tipo_men;
        this.estado = estado;
        this.correo = correo;
        this.mensaje = mensaj;
        this.ruta_Dato = ruta_Dato;
        this.hora = hora;
        this.fecha = fecha;
        this.id_msg_resp = id_msg_res;
        this.emisor = emisor;
        this.esReenviado = esReenviado;
        this.orden = orden;
        editado = eedit;
        this.id_mensaje = id_mensaje;
        this.peso = peso;
        this.descargado = descargado;

        seleccionado = false;

        comprobarTipoEnTexto();
    }

    public String getId_mensaje() {
        return id_mensaje;
    }

    public void setId_mensaje(String id_mensaje) {
        this.id_mensaje = id_mensaje;
    }

    public int getPeso() {
        return peso;
    }

    public void setPeso(int peso) {
        this.peso = peso;
    }

    public boolean isDescargado() {
        return descargado;
    }

    public void setDescargado(boolean descargado) {
        this.descargado = descargado;
    }

    public String getOrden() {
        return orden;
    }

    public void setOrden(String orden) {
        this.orden = orden;
    }

    public String getId_msg_resp() {
        return id_msg_resp;
    }

    public void setId_msg_resp(String id_msg_resp) {
        this.id_msg_resp = id_msg_resp;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTipo_mensaje(){
        if(tipo_mensaje==25
                || tipo_mensaje==27)
            return 1;
        else if(tipo_mensaje==26
                || tipo_mensaje==28)
            return 2;
        return tipo_mensaje;
    }

    public int getTipo_mensajeReal(){
        return tipo_mensaje;
    }

    public void setTipo_mensaje(int tipo_mensaje) {
        this.tipo_mensaje = tipo_mensaje;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getRuta_Dato() {
        return ruta_Dato;
    }

    public void setRuta_Dato(String ruta_Dato) {
        this.ruta_Dato = ruta_Dato;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public boolean EsReenviado() {
        return esReenviado;
    }

    public boolean estaSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    public int getReenviado(){
        if(esReenviado)
            return 1;
        return 0;
    }

    public boolean esEditado() {
        return editado;
    }

    public int getEditado() {
        if(editado) return 1;
        return 0;
    }

    public void setEditado(boolean editado) {
        this.editado = editado;
    }

    public boolean esReenviado(){
        return esReenviado;
    }

    public void setEsReenviado(boolean esReenviado) {
        this.esReenviado = esReenviado;
    }

    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String emisor) {
        this.emisor = emisor;
    }

    public boolean esMsg(){
        if(tipo_mensaje!=0 && tipo_mensaje!=81
                && tipo_mensaje!=83 && tipo_mensaje!=97
                && tipo_mensaje!=99)
            return true;
        return false;
    }

    public boolean puedeSwipear(){
        if(!esMsg()) return false;
        if(hayQReintentarEnviar()) return false;
        return true;
    }

    public boolean esAudio(){
        if(tipo_mensaje==7 || tipo_mensaje==8 || tipo_mensaje==9 || tipo_mensaje==10)
            return true;
        return false;
    }

    public boolean esAudioSimple(){
        if(tipo_mensaje==7 || tipo_mensaje==8)
            return true;
        return false;
    }

    public boolean esAudioRespondido(){
        if(tipo_mensaje==9 || tipo_mensaje==10)
            return true;
        return false;
    }
    public boolean esImagen(){
        if(tipo_mensaje==3 || tipo_mensaje==4)
            return true;
        return false;
    }
    public boolean esMsgTexto(){
        if(tipo_mensaje==1 || tipo_mensaje==2
                || tipo_mensaje==5 || tipo_mensaje==6
                || tipo_mensaje==17 || tipo_mensaje==18
                || tipo_mensaje==23 || tipo_mensaje==24
                || tipo_mensaje==25 || tipo_mensaje==26
                || tipo_mensaje==27 || tipo_mensaje==28)
            return true;
        return false;
    }
    public boolean esMsgTextoSimple(){
        if(tipo_mensaje==1 || tipo_mensaje==2)
            return true;
        return false;
    }
    public boolean esMsgTextoRespondido(){
        if(tipo_mensaje==5 || tipo_mensaje==6)
            return true;
        return false;
    }

    public boolean esEstadoRespondido(){
        if(tipo_mensaje==17 || tipo_mensaje==18)
            return true;
        return false;
    }

    public boolean esContacto(){
        if(tipo_mensaje==11 || tipo_mensaje==12)
            return true;
        return false;
    }

    public boolean esArchivo(){
        if(tipo_mensaje==13 || tipo_mensaje==14)
            return true;
        return false;
    }

    public boolean esTarjeta(){
        if(tipo_mensaje==15 || tipo_mensaje==16)
            return true;
        return false;
    }

    public boolean esTema(){
        if(tipo_mensaje==21 || tipo_mensaje==22)
            return true;
        return false;
    }

    public boolean esDer(){
        if(tipo_mensaje%2==0 && tipo_mensaje!=0)
            return true;
        return false;
    }

    public boolean esIzq(){
        if(tipo_mensaje%2==1 && tipo_mensaje!=97 && tipo_mensaje!=99)
            return true;
        return false;
    }

    public boolean esSticker(){
        if(tipo_mensaje==19 || tipo_mensaje==20)
            return true;
        return false;
    }

    public boolean esRespondidoPost(){
        if(tipo_mensaje==23 || tipo_mensaje==24)
            return true;
        return false;
    }

    public boolean hayQReintentarEnviar(){
        if(!esMsg()) return false;
        if(!esDer()) return false;
        if(estado==ESTADO_ESPERANDO || estado==ESTADO_ERROR) return true;
        return false;
    }

    public boolean esDeEstaVersionElTipo(int tipo){
        if((tipo>=0 && tipo<=28) || tipo==81 || tipo==83 || tipo==97 || tipo==99)
            return true;
        return false;
    }

    public boolean esDeEstaVersion(){
        return esDeEstaVersionElTipo(tipo_mensaje);
    }

    public synchronized void comprobarTipoEnTexto() {
        int tipo = getTipo_mensaje();
        if(tipo==1 || tipo==2){
            EmojiInformation emojiInformation = EmojiUtils.emojiInformation(mensaje);
            if (emojiInformation.isOnlyEmojis && emojiInformation.emojis.size() > 0 && emojiInformation.emojis.size()<=2){
                if(emojiInformation.emojis.size() == 1 && YouChatApplication.activeEmojisAnimChat){
//                    if(emojiInformation.emojis.get(0).emoji.getShortcodes()!=null
//                            && emojiInformation.emojis.get(0).emoji.getShortcodes().size()>0){
//                        Log.e("ItemChat: ", "emoji4: "+emojiInformation.emojis.get(0).emoji.getShortcodes().get(0));
////                        switch (emojiInformation.emojis.get(0).emoji.getShortcodes().get(0)){
////                            case "heart_eyes":
////                                tipo_mensaje+=26;
////                                id_msg_resp = "2";
////                                break;
////                            default:
////                                tipo_mensaje+=24;
////                        }
//
//                    }
//                    else tipo_mensaje+=24;

                    Log.e("ItemChat: ", "emoji: "+emojiInformation.emojis.get(0).hashCode());

                    switch (emojiInformation.emojis.get(0).hashCode()){
                        case -795324429://emoji like
                            tipo_mensaje=tipo+26;
                            id_msg_resp = "1";
                            break;
                        case 662613770://emoji encanta
                            tipo_mensaje=tipo+26;
                            id_msg_resp = "2";
                            break;
                        case -1913807687://emoji sonroja
                            tipo_mensaje=tipo+26;
                            id_msg_resp = "3";
                            break;
                        case 440370626://emoji divierte
                            tipo_mensaje=tipo+26;
                            id_msg_resp = "4";
                            break;
                        case -1286718624://emoji asombra
                            tipo_mensaje=tipo+26;
                            id_msg_resp = "5";
                            break;
                        case 452388073://emoji entristese
                            tipo_mensaje=tipo+26;
                            id_msg_resp = "6";
                            break;
                        case 232343194://emoji enoja
                            tipo_mensaje=tipo+26;
                            id_msg_resp = "7";
                            break;
                        case 1275416810://emoji fiesta
                            tipo_mensaje=tipo+26;
                            id_msg_resp = "8";
                            break;
                        case 406308796://corneta fiesta
                            tipo_mensaje=tipo+26;
                            id_msg_resp = "9";
                            break;
                        default:
                            tipo_mensaje=tipo+24;
                    }
                }
                else tipo_mensaje=tipo+24;
            }
            else tipo_mensaje = tipo;
        }
    }
}
