package cu.alexgi.youchat.items;

public class ItemNotificacion {

    private String usuario;
    private String correo;
    private String mensaje;
    private String rutaImg;
    private int cant;

    public ItemNotificacion(String usuario, String correo, String mensaje, String rutaImg, int cant) {
        this.usuario = usuario;
        this.correo = correo;
        this.mensaje = mensaje;
        this.rutaImg = rutaImg;
        this.cant = cant;
    }

    public int getCant() {
        return cant;
    }

    public void setCant(int cant) {
        this.cant = cant;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
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

    public String getRutaImg() {
        return rutaImg;
    }

    public void setRutaImg(String rutaImg) {
        this.rutaImg = rutaImg;
    }
}
