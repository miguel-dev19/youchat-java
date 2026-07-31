package cu.alexgi.youchat.items;

public class ItemVistaEstado {
    private String idEstado;
    private String correo;
    private String hora;
    private String fecha;

    public ItemVistaEstado(String idEstado, String correo, String hora, String fecha) {
        this.idEstado = idEstado;
        this.correo = correo;
        this.hora = hora;
        this.fecha = fecha;
    }

    public String getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(String idEstado) {
        this.idEstado = idEstado;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
