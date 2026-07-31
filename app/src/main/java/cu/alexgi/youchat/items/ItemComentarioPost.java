package cu.alexgi.youchat.items;

public class ItemComentarioPost {

    private String id;
    private String id_post;
    /*
    tipo comentario post
    1 - texto
     */
    private int tipo_comentario_post;
    private String nombre;
    private String correo;
    private int tipo_usuario;
    private int icono;
    private String texto;
    private String ruta_dato;
    private int peso_dato;
    private String hora;
    private String fecha;
    private String orden;

    public ItemComentarioPost(String id, String id_post, int tipo_comentario_post,
                              String nombre, String correo, int tipo_usuario,
                              int icono, String texto, String ruta_dato,
                              int peso_dato, String hora, String fecha, String orden) {
        this.id = id;
        this.id_post = id_post;
        this.tipo_comentario_post = tipo_comentario_post;
        this.nombre = nombre;
        this.correo = correo;
        this.tipo_usuario = tipo_usuario;
        this.icono = icono;
        this.texto = texto;
        this.ruta_dato = ruta_dato;
        this.peso_dato = peso_dato;
        this.hora = hora;
        this.fecha = fecha;
        this.orden = orden;
    }

    public String getId_post() {
        return id_post;
    }

    public void setId_post(String id_post) {
        this.id_post = id_post;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTipo_comentario_post() {
        return tipo_comentario_post;
    }

    public void setTipo_comentario_post(int tipo_comentario_post) {
        this.tipo_comentario_post = tipo_comentario_post;
    }

    public String getNombre() {
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

    public int getTipo_usuario() {
        return tipo_usuario;
    }

    public void setTipo_usuario(int tipo_usuario) {
        this.tipo_usuario = tipo_usuario;
    }

    public int getIcono() {
        return icono;
    }

    public void setIcono(int icono) {
        this.icono = icono;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getRuta_dato() {
        return ruta_dato;
    }

    public void setRuta_dato(String ruta_dato) {
        this.ruta_dato = ruta_dato;
    }

    public int getPeso_dato() {
        return peso_dato;
    }

    public void setPeso_dato(int peso_dato) {
        this.peso_dato = peso_dato;
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

    public boolean esDeEstaVersion(){
        if(tipo_comentario_post>0 && tipo_comentario_post<=1) return true;
        else return false;
    }

    public static boolean esDeEstaVersion(int tipoPost) {
        if(tipoPost>0 && tipoPost<=1) return true;
        else return false;
    }

    public boolean esTipoTexto(){
        if(tipo_comentario_post==1) return true;
        else return false;
    }
}
