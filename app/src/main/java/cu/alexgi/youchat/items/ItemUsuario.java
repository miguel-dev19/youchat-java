package cu.alexgi.youchat.items;

public class ItemUsuario {

    private String correo;

    private boolean esAnclado;
    private int cant_mensajes;

    private int ult_msg_tipo;
    private String ult_msg_texto;
    private int ult_msg_estado;
    private String ult_msg_orden;

    //no visible
    private boolean estaSeleccionado;

    public ItemUsuario(String correo){
        this.correo = correo;

        esAnclado=false;

        this.cant_mensajes = 0;
        this.ult_msg_tipo = 0;
        this.ult_msg_texto = "";
        this.ult_msg_estado = 0;
        this.ult_msg_orden = "";

        estaSeleccionado=false;
    }

    public ItemUsuario(String correo, int anclado, int cant_mensajes){
        this.correo = correo;

        if(anclado==1) esAnclado=true;
        else esAnclado=false;
        this.cant_mensajes = cant_mensajes;

        this.ult_msg_tipo = 0;
        this.ult_msg_texto = "";
        this.ult_msg_estado = 0;
        this.ult_msg_orden = "";

        estaSeleccionado=false;
    }

    public ItemUsuario(String correo, int anclado, int cant_mensajes,
                       int ult_msg_tipo, String ult_msg_texto, int ult_msg_estado, String ult_msg_orden) {
        this.correo = correo;

        if(anclado==1) esAnclado=true;
        else esAnclado=false;

        this.cant_mensajes = cant_mensajes;
        this.ult_msg_tipo = ult_msg_tipo;
        this.ult_msg_texto = ult_msg_texto;
        this.ult_msg_estado = ult_msg_estado;
        this.ult_msg_orden = ult_msg_orden;

        estaSeleccionado=false;
    }

    public boolean EsAnclado() {
        return esAnclado;
    }

    public void setEsAnclado(boolean esAnclado) {
        this.esAnclado = esAnclado;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public int getCant_mensajes() {
        return cant_mensajes;
    }

    public void setCant_mensajes(int cant_mensajes) {
        this.cant_mensajes = cant_mensajes;
    }

    public int getUlt_msg_tipo() {
        return ult_msg_tipo;
    }

    public void setUlt_msg_tipo(int ult_msg_tipo) {
        this.ult_msg_tipo = ult_msg_tipo;
    }

    public String getUlt_msg_texto() {
        return ult_msg_texto;
    }

    public void setUlt_msg_texto(String ult_msg_texto) {
        this.ult_msg_texto = ult_msg_texto;
    }

    public int getUlt_msg_estado() {
        return ult_msg_estado;
    }

    public void setUlt_msg_estado(int ult_msg_estado) {
        this.ult_msg_estado = ult_msg_estado;
    }

    public String getUlt_msg_orden() {
        return ult_msg_orden;
    }

    public void setUlt_msg_orden(String ult_msg_orden) {
        this.ult_msg_orden = ult_msg_orden;
    }

    public boolean isEstaSeleccionado() {
        return estaSeleccionado;
    }

    public void setEstaSeleccionado(boolean estaSeleccionado) {
        this.estaSeleccionado = estaSeleccionado;
    }
}
