package cu.alexgi.youchat.items;

public class ItemMensajeCorreo {

    private String id;
    private Long uid;
    private boolean esMio;
    private boolean esNuevo;
    private boolean esFavorito;
    private String correo;
    private String remitente;
    private String nombre;
    private String destinatario;
    private String asunto;
    private String texto;
    private int estado;
    private boolean esRespondido;
    private int peso;
    private String hora;
    private String fecha;
    private String orden;

    //no db
    private boolean seleccionado;

    public ItemMensajeCorreo(String id, Long uid, boolean esMio, boolean esNuevo,
                             boolean esFavorito, String correo, String remitente,
                             String nombre, String destinatario, String asunto,
                             String texto, int estado, boolean esRespondido,
                             int peso, String hora, String fecha, String orden) {
        this.id = id;
        this.uid = uid;
        this.esMio = esMio;
        this.esNuevo = esNuevo;
        this.esFavorito = esFavorito;
        this.correo = correo;
        this.remitente = remitente;
        this.nombre = nombre;
        this.destinatario = destinatario;
        this.asunto = asunto;
        this.texto = texto;
        this.estado = estado;
        this.esRespondido = esRespondido;
        this.peso = peso;
        this.hora = hora;
        this.fecha = fecha;
        this.orden = orden;

        seleccionado=false;
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    public String getRemitente() {
        return remitente;
    }

    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isEsFavorito() {
        return esFavorito;
    }

    public void setEsFavorito(boolean esFavorito) {
        this.esFavorito = esFavorito;
    }

    public boolean isEsMio() {
        return esMio;
    }

    public void setEsMio(boolean esMio) {
        this.esMio = esMio;
    }

    public boolean isEsNuevo() {
        return esNuevo;
    }

    public void setEsNuevo(boolean esNuevo) {
        this.esNuevo = esNuevo;
    }

    public boolean isEsRespondido() {
        return esRespondido;
    }

    public void setEsRespondido(boolean esRespondido) {
        this.esRespondido = esRespondido;
    }

    public int getPeso() {
        return peso;
    }

    public void setPeso(int peso) {
        this.peso = peso;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
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
}
