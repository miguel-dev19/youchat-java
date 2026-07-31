package cu.alexgi.youchat.items;

public class ItemUsuarioCorreo {
    private String correo;
    private String nombre;
    private String groupId;
    private String hora;
    private String fecha;
    private String orden;

    //no db
    private boolean seleccionado;

    public ItemUsuarioCorreo(String correo, String nombre, String groupId, String hora, String fecha, String orden) {
        this.correo = correo;
        this.nombre = nombre;
        this.groupId = groupId;
        this.hora = hora;
        this.fecha = fecha;
        this.orden = orden;

        seleccionado = false;
    }

    public ItemUsuarioCorreo(ItemMensajeCorreo mensaje) {
        this.correo = mensaje.getCorreo();
        this.nombre = mensaje.getNombre();
        this.groupId = "";
        this.hora = mensaje.getHora();
        this.fecha = mensaje.getFecha();
        this.orden = mensaje.getOrden();

        seleccionado = false;
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getNombre() {
        if(nombre==null || nombre.isEmpty())
            return correo;
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public String getOrden() {
        return orden;
    }

    public void setOrden(String orden) {
        this.orden = orden;
    }

    public void modificar(ItemMensajeCorreo mensaje) {
        this.hora = mensaje.getHora();
        this.fecha = mensaje.getFecha();
        this.orden = mensaje.getOrden();
    }
}
