package cu.alexgi.youchat.items;

import cu.alexgi.youchat.Utils;
import cu.alexgi.youchat.chatUtils.Util;

public class ItemTemas {
    /*
    * TIPO
     * 1- SISTEMA
     * 2- TEMA RECIBIDO
     * 3- TEMA CREADO
     */

    private String id;
    private String nombre;
    private int     tipo;

    private boolean oscuro;
    private String rutaImg;
    private String creador;

    private String color_barra;
    private String color_btn;
    private String color_fondo;
    private String color_texto;
    private String color_interior;
    private String color_msg_izq;
    private String color_msg_der;
    private String color_msg_fecha;
    private String color_accento;
    private String color_ico_gen;

    private String font_barra;
    private String font_msg_izq;
    private String font_msg_der;
    private String font_msg_fecha;
    private String font_texto_resaltado;

    private String color_dialogo;
    private String color_toast;
    private String color_burbuja;
    private String font_burbuja;

    private String color_barchat;
    private String font_barchat;

    private String sel_msj;
    private String status_bar;

    ///no van en la bd los de abajo
    private String color_btn_oscuro;
    private String font_barra_oscuro;
    private String color_texto_oscuro;
    private String font_barchat_oscuro;
    private String color_msg_izq_oscuro;
    private String color_msg_der_oscuro;

    private String color_msg_izq_audio;
    private String color_msg_der_audio;

    public ItemTemas(String id) {
        this.id = id;
    }

    public ItemTemas(String id, String nombre, int tipo, boolean oscuro,
                     String rutaImg, String creador, String color_barra,
                     String color_btn, String color_fondo, String color_texto,
                     String color_interior, String color_msg_izq, String color_msg_der,
                     String color_msg_fecha, String color_accento, String color_ico_gen,
                     String font_barra, String font_msg_izq, String font_msg_der,
                     String font_msg_fecha, String font_texto_resaltado, String color_dialogo,
                     String color_toast, String color_burbuja, String font_burbuja,
                     String color_barchat, String font_barchat, String sel_msj, String status_bar) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.oscuro = oscuro;
        this.rutaImg = rutaImg;
        this.creador = creador;
        this.color_barra = color_barra;
        this.color_btn = color_btn;
        this.color_fondo = color_fondo;
        this.color_texto = color_texto;
        this.color_interior = color_interior;
        this.color_msg_izq = color_msg_izq;
        this.color_msg_der = color_msg_der;
        this.color_msg_fecha = color_msg_fecha;
        this.color_accento = color_accento;
        this.color_ico_gen = color_ico_gen;
        this.font_barra = font_barra;
        this.font_msg_izq = font_msg_izq;
        this.font_msg_der = font_msg_der;
        this.font_msg_fecha = font_msg_fecha;
        this.font_texto_resaltado = font_texto_resaltado;
        this.color_dialogo = color_dialogo;
        this.color_toast = color_toast;
        this.color_burbuja = color_burbuja;
        this.font_burbuja = font_burbuja;
        this.color_barchat = color_barchat;
        this.font_barchat = font_barchat;
        this.sel_msj = sel_msj;
        this.status_bar = status_bar;

        color_btn_oscuro = Utils.obtenerOscuroDe(color_btn);
        font_barra_oscuro = Utils.obtenerOscuroDe(font_barra);
        color_texto_oscuro = Utils.obtenerOscuroDe(color_texto);
        font_barchat_oscuro = Utils.obtenerOscuroDe(font_barchat);
        color_msg_izq_oscuro = Utils.obtenerOscuroDe(color_msg_izq);
//        color_msg_izq_oscuro="#66"+color_msg_izq_oscuro.substring(3,9);
        color_msg_der_oscuro = Utils.obtenerOscuroDe(color_msg_der);
//        color_msg_der_oscuro="#66"+color_msg_der_oscuro.substring(3,9);

        color_msg_izq_audio = color_msg_der_audio = color_btn;
        int[] rgb_color_msg_izq_audio = Utils.obtenerRGBde(color_msg_izq_audio);
        int[] rgb_color_msg_der_audio = Utils.obtenerRGBde(color_msg_der_audio);
        int[] rgb_color_msg_izq = Utils.obtenerRGBde(color_msg_izq);
        int[] rgb_color_msg_der = Utils.obtenerRGBde(color_msg_der);

        if(Utils.compararIgualdadRGBs(rgb_color_msg_izq_audio,rgb_color_msg_izq))
            color_msg_izq_audio = Utils.obtenerInversoDe(color_msg_izq);
//        color_msg_izq_audio = color_msg_izq_oscuro;
        if(Utils.compararIgualdadRGBs(rgb_color_msg_der_audio,rgb_color_msg_der))
            color_msg_der_audio = Utils.obtenerInversoDe(color_msg_der);
//        color_msg_der_audio = color_msg_der_oscuro;
    }

    public String getColor_msg_izq_audio() {
        return color_msg_izq_audio;
    }

    public void setColor_msg_izq_audio(String color_msg_izq_audio) {
        this.color_msg_izq_audio = color_msg_izq_audio;
    }

    public String getColor_msg_der_audio() {
        return color_msg_der_audio;
    }

    public void setColor_msg_der_audio(String color_msg_der_audio) {
        this.color_msg_der_audio = color_msg_der_audio;
    }

    public String getColor_msg_izq_oscuro() {
        return color_msg_izq_oscuro;
    }

    public void setColor_msg_izq_oscuro(String color_msg_izq_oscuro) {
        this.color_msg_izq_oscuro = color_msg_izq_oscuro;
    }

    public String getColor_msg_der_oscuro() {
        return color_msg_der_oscuro;
    }

    public void setColor_msg_der_oscuro(String color_msg_der_oscuro) {
        this.color_msg_der_oscuro = color_msg_der_oscuro;
    }

    public String getFont_barchat_oscuro() {
        return font_barchat_oscuro;
    }

    public void setFont_barchat_oscuro(String font_barchat_oscuro) {
        this.font_barchat_oscuro = font_barchat_oscuro;
    }

    public String getColor_btn_oscuro() {
        return color_btn_oscuro;
    }

    public void setColor_btn_oscuro(String color_btn_oscuro) {
        this.color_btn_oscuro = color_btn_oscuro;
    }

    public String getFont_barra_oscuro() {
        return font_barra_oscuro;
    }

    public void setFont_barra_oscuro(String font_barra_oscuro) {
        this.font_barra_oscuro = font_barra_oscuro;
    }

    public String getColor_texto_oscuro() {
        return color_texto_oscuro;
    }

    public void setColor_texto_oscuro(String color_texto_oscuro) {
        this.color_texto_oscuro = color_texto_oscuro;
    }

    public String getSel_msj() {
        return sel_msj;
    }

    public String getStatus_bar() {
        return status_bar;
    }

    public void setStatus_bar(String status_bar) {
        this.status_bar = status_bar;
    }

    public void setSel_msj(String sel_msj) {
        this.sel_msj = sel_msj;
    }

    public String getColor_barchat() {
        return color_barchat;
    }

    public void setColor_barchat(String color_barchat) {
        this.color_barchat = color_barchat;
    }

    public String getFont_barchat() {
        return font_barchat;
    }

    public void setFont_barchat(String font_barchat) {
        this.font_barchat = font_barchat;
    }

    public String getColor_dialogo() {
        return color_dialogo;
    }

    public void setColor_dialogo(String color_dialogo) {
        this.color_dialogo = color_dialogo;
    }

    public String getColor_toast() {
        return color_toast;
    }

    public void setColor_toast(String color_toast) {
        this.color_toast = color_toast;
    }

    public String getColor_burbuja() {
        return color_burbuja;
    }

    public void setColor_burbuja(String color_burbuja) {
        this.color_burbuja = color_burbuja;
    }

    public String getFont_burbuja() {
        return font_burbuja;
    }

    public void setFont_burbuja(String font_burbuja) {
        this.font_burbuja = font_burbuja;
    }

    public String getCreador() {
        return creador;
    }

    public void setCreador(String creador) {
        this.creador = creador;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public boolean isOscuro() {
        return oscuro;
    }

    public void setOscuro(boolean oscuro) {
        this.oscuro = oscuro;
    }

    public String getRutaImg() {
        return rutaImg;
    }

    public void setRutaImg(String rutaImg) {
        this.rutaImg = rutaImg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getColor_barra() {
        return color_barra;
    }

    public void setColor_barra(String color_barra) {
        this.color_barra = color_barra;
    }

    public String getColor_btn() {
        return color_btn;
    }

    public void setColor_btn(String color_btn) {
        this.color_btn = color_btn;
    }

    public String getColor_fondo() {
        return color_fondo;
    }

    public void setColor_fondo(String color_fondo) {
        this.color_fondo = color_fondo;
    }

    public String getColor_texto() {
        return color_texto;
    }

    public void setColor_texto(String color_texto) {
        this.color_texto = color_texto;
    }

    public String getColor_interior() {
        return color_interior;
    }

    public void setColor_interior(String color_interior) {
        this.color_interior = color_interior;
    }

    public String getColor_msg_izq() {
        return color_msg_izq;
    }

    public void setColor_msg_izq(String color_msg_izq) {
        this.color_msg_izq = color_msg_izq;
    }

    public String getColor_msg_der() {
        return color_msg_der;
    }

    public void setColor_msg_der(String color_msg_der) {
        this.color_msg_der = color_msg_der;
    }

    public String getColor_msg_fecha() {
        return color_msg_fecha;
    }

    public void setColor_msg_fecha(String color_msg_fecha) {
        this.color_msg_fecha = color_msg_fecha;
    }

    public String getColor_accento() {
        return color_accento;
    }

    public void setColor_accento(String color_accento) {
        this.color_accento = color_accento;
    }

    public String getColor_ico_gen() {
        return color_ico_gen;
    }

    public void setColor_ico_gen(String color_ico_gen) {
        this.color_ico_gen = color_ico_gen;
    }

    public String getFont_barra() {
        return font_barra;
    }

    public void setFont_barra(String font_barra) {
        this.font_barra = font_barra;
    }

    public String getFont_msg_izq() {
        return font_msg_izq;
    }

    public void setFont_msg_izq(String font_msg_izq) {
        this.font_msg_izq = font_msg_izq;
    }

    public String getFont_msg_der() {
        return font_msg_der;
    }

    public void setFont_msg_der(String font_msg_der) {
        this.font_msg_der = font_msg_der;
    }

    public String getFont_msg_fecha() {
        return font_msg_fecha;
    }

    public void setFont_msg_fecha(String font_msg_fecha) {
        this.font_msg_fecha = font_msg_fecha;
    }

    public String getFont_texto_resaltado() {
        return font_texto_resaltado;
    }

    public void setFont_texto_resaltado(String font_texto_resaltado) {
        this.font_texto_resaltado = font_texto_resaltado;
    }

    public boolean temaCorrecto(){
        if(id.isEmpty() || nombre.isEmpty()
        || creador.isEmpty()

        || !Utils.esUnColor(color_barra)
        || !Utils.esUnColor(color_btn)
        || !Utils.esUnColor(color_fondo)
        || !Utils.esUnColor(color_texto)
        || !Utils.esUnColor(color_interior)
        || !Utils.esUnColor(color_msg_izq)
        || !Utils.esUnColor(color_msg_der)
        || !Utils.esUnColor(color_msg_fecha)
        || !Utils.esUnColor(color_accento)
        || !Utils.esUnColor(color_ico_gen)

        || !Utils.esUnColor(font_barra)
        || !Utils.esUnColor(font_msg_izq)
        || !Utils.esUnColor(font_msg_der)
        || !Utils.esUnColor(font_msg_fecha)
        || !Utils.esUnColor(font_texto_resaltado)

        || !Utils.esUnColor(color_dialogo)
        || !Utils.esUnColor(color_toast)
        || !Utils.esUnColor(color_burbuja)
        || !Utils.esUnColor(font_burbuja)
        || !Utils.esUnColor(color_barchat)
        || !Utils.esUnColor(font_barchat)
        || !Utils.esUnColor(sel_msj)
        || !Utils.esUnColor(status_bar)
        ) return false;
        return true;
    }
    public String getInfo() {
        String info = "";
        info+="ID: "+id;
        info+="\nNombre: "+nombre;
        info+="\nTipo: "+tipo;
        info+="\nEsOscuro: "+(oscuro?"1":"0");
        info+="\nRutaImg: "+rutaImg;
        info+="\nCreador: "+creador;
        info+="\nColor barra: "+color_barra;
        info+="\nColor btn: "+color_btn;
        info+="\nColor fondo: "+color_fondo;
        info+="\nColor texto: "+color_texto;
        info+="\nColor interior: "+color_interior;
        info+="\nColor msg izq: "+color_msg_izq;
        info+="\nColor msg der: "+color_msg_der;
        info+="\nColor msg fecha: "+color_msg_fecha;
        info+="\nColor accento: "+color_accento;
        info+="\nColor ico gen: "+color_ico_gen;
        info+="\nFont barra: "+font_barra;
        info+="\nFont msg izq: "+font_msg_izq;
        info+="\nFont msg der: "+font_msg_der;
        info+="\nFont msg fecha: "+font_msg_fecha;
        info+="\nFont texto resaltado: "+font_texto_resaltado;
        info+="\nFont barra: "+font_barra;
        info+="\nFont msg izq: "+font_msg_izq;
        info+="\nFont msg der: "+font_msg_der;
        info+="\nFont msg fecha: "+font_msg_fecha;
        info+="\nColor dialogo: "+color_dialogo;
        info+="\nColor toast: "+color_toast;
        info+="\nColor burbuja: "+color_burbuja;
        info+="\nFont burbuja: "+font_burbuja;
        info+="\nColor barchat: "+color_barchat;
        info+="\nFont barchat: "+font_barchat;
        info+="\nColor sel msg: "+sel_msj;
        info+="\nColor status bar: "+status_bar;
        return info;
    }

    public String temaToMensaje() {
        String cad = "";
        cad+=id;//0
        cad+="<s,p>";
        cad+=nombre;//1
        cad+="<s,p>";
        cad+=oscuro?"1":"0";//2
        cad+="<s,p>";
        cad+=rutaImg;//3
        cad+="<s,p>";
        cad+=creador;//4
        cad+="<s,p>";
        cad+=color_barra;//5
        cad+="<s,p>";
        cad+=color_btn;//6
        cad+="<s,p>";
        cad+=color_fondo;//7
        cad+="<s,p>";
        cad+=color_texto;//8
        cad+="<s,p>";
        cad+=color_interior;//9
        cad+="<s,p>";
        cad+=color_msg_izq;//10
        cad+="<s,p>";
        cad+=color_msg_der;//11
        cad+="<s,p>";
        cad+=color_msg_fecha;//12
        cad+="<s,p>";
        cad+=color_accento;//13
        cad+="<s,p>";
        cad+=color_ico_gen;//14
        cad+="<s,p>";
        cad+=font_barra;//15
        cad+="<s,p>";
        cad+=font_msg_izq;//16
        cad+="<s,p>";
        cad+=font_msg_der;//17
        cad+="<s,p>";
        cad+=font_msg_fecha;//18
        cad+="<s,p>";
        cad+=font_texto_resaltado;//19
        cad+="<s,p>";
        cad+=color_dialogo;//20
        cad+="<s,p>";
        cad+=color_toast;//21
        cad+="<s,p>";
        cad+=color_burbuja;//22
        cad+="<s,p>";
        cad+=font_burbuja;//23
        cad+="<s,p>";
        cad+=color_barchat;//24
        cad+="<s,p>";
        cad+=font_barchat;//25
        cad+="<s,p>";
        cad+=sel_msj;//26
        cad+="<s,p>";
        cad+=status_bar;//27
        return cad;
    }
}
