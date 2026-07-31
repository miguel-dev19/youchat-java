package cu.alexgi.youchat.items;

public class ItemReaccionEstado {

    private String idEstado;
    private String correo;
    private int tipoReaccion;
    private String hora;
    private String fecha;

    public ItemReaccionEstado(String idEstado, String correo, int tipoReaccion, String hora, String fecha) {
        this.idEstado = idEstado;
        this.correo = correo;
        this.tipoReaccion = tipoReaccion;
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

    public int getTipoReaccion() {
        return tipoReaccion;
    }

    public void setTipoReaccion(int tipoReaccion) {
        this.tipoReaccion = tipoReaccion;
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
