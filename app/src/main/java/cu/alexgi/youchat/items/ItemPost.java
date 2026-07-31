package cu.alexgi.youchat.items;

import java.util.ArrayList;

import cu.alexgi.youchat.Convertidor;

public class ItemPost {

    private String id;
    private long uid;
    /*
    tipo post
    1 - texto
    2 - imagen
    3 - tema
    4 - tarjeta
     */
    private int tipo_post;
    private String nombre;
    private String correo;
    private int tipo_usuario;
    private int icono;
    private String texto;
    private boolean es_nuevo;
    private String ruta_dato;
    private int peso_dato;
    private String hora;
    private String fecha;
    private String orden;

    //no bd
    private ArrayList<ItemComentarioPost> comentarioPosts;
    private boolean mostrarMas;
    private boolean inputShow;

    public ItemPost(String id, long uid, int tipo_post, String nombre, String correo, int tipo_usuario, int icono, String texto, boolean es_nuevo, String ruta_dato, int peso_dato, String hora, String fecha, String orden) {
        this.id = id;
        this.uid = uid;
        this.tipo_post = tipo_post;
        this.nombre = nombre;
        this.correo = correo;
        this.tipo_usuario = tipo_usuario;
        this.icono = icono;
        this.texto = texto;
        this.es_nuevo = es_nuevo;
        this.ruta_dato = ruta_dato;
        this.peso_dato = peso_dato;
        this.hora = hora;
        this.fecha = fecha;
        this.orden = orden;

        comentarioPosts = new ArrayList<>();
        mostrarMas = false;
        inputShow = false;
    }

    public ArrayList<ItemComentarioPost> getComentarioPosts() {
        return comentarioPosts;
    }

    public ArrayList<ItemComentarioPost> getUlt3ComentarioPosts() {
        if(comentarioPosts.size()<=3)
            return comentarioPosts;
        else{
            ArrayList<ItemComentarioPost> ult3Comentario = new ArrayList<>();
            int l=comentarioPosts.size()-1;
            ult3Comentario.add(comentarioPosts.get(l-2));
            ult3Comentario.add(comentarioPosts.get(l-1));
            ult3Comentario.add(comentarioPosts.get(l));
            return ult3Comentario;
        }
    }

    public boolean puedeMostrarMas(){
        return mostrarMas;
    }

    public void setComentarioPosts(ArrayList<ItemComentarioPost> comentarioPosts) {
        this.comentarioPosts = comentarioPosts;
        if(comentarioPosts.size()>3) mostrarMas=true;
        else mostrarMas = false;
    }

    public ItemPost(int tipo_post, int icono) {
        this.id = "";
        this.tipo_post = tipo_post;
        this.icono = icono;
    }

    public ItemPost(int tipo_post) {
        this.id = "";
        this.tipo_post = tipo_post;
    }

    public boolean isMostrarMas() {
        return mostrarMas;
    }

    public void setMostrarMas(boolean mostrarMas) {
        this.mostrarMas = mostrarMas;
    }

    public boolean isInputShow() {
        return inputShow;
    }

    public void setInputShow(boolean inputShow) {
        this.inputShow = inputShow;
    }

    public boolean isEs_nuevo() {
        return es_nuevo;
    }

    public void setEs_nuevo(boolean es_nuevo) {
        this.es_nuevo = es_nuevo;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public int getTipo_post() {
        return tipo_post;
    }

    public void setTipo_post(int tipo_post) {
        this.tipo_post = tipo_post;
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

    public String getFicha() {
        String cad="";
        cad+="Nombre: "+nombre;
        cad+="\n";
        cad+="Correo: "+correo;
        cad+="\n";
        cad+="Contenido:\n"+texto;
        cad+="\n\n";
        cad+="Fecha: "+ Convertidor.convertirFechaAFechaLinda(fecha)+", "+hora;
        cad+="\n\n";
        return cad;
    }

    public boolean esDeEstaVersion(){
        if(tipo_post>0 && tipo_post<=4) return true;
        else return false;
    }

    public static boolean esDeEstaVersion(int tipoPost) {
        if(tipoPost>0 && tipoPost<=4) return true;
        else return false;
    }

    public boolean esTipoTexto(){
        if(tipo_post==1) return true;
        else return false;
    }

    public boolean esTipoImagen(){
        if(tipo_post==2) return true;
        else return false;
    }
}
