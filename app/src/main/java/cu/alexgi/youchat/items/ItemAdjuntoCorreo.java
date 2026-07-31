package cu.alexgi.youchat.items;

public class ItemAdjuntoCorreo {

    private String id;
    private String id_mensaje;
    private String correo;
    private int posicion;
    private String nombre;
    /*
    0 - file
    1 - audio
    2 - apk
    3 - video
    4 - imagen
    5 - txt
    6 - gif
    7 - comprimido
    8 - xml
    9 - pdf
     */
    private int tipo;
    private int peso;

    public ItemAdjuntoCorreo(String id, String id_mensaje, String correo, int posicion, String nombre, int tipo, int peso) {
        this.id = id;
        this.id_mensaje = id_mensaje;
        this.correo = correo;
        this.posicion = posicion;
        this.nombre = nombre;
        this.tipo = tipo;
        this.peso = peso;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_mensaje() {
        return id_mensaje;
    }

    public void setId_mensaje(String id_mensaje) {
        this.id_mensaje = id_mensaje;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public int getPosicion() {
        return posicion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPeso() {
        return peso;
    }

    public void setPeso(int peso) {
        this.peso = peso;
    }

    public boolean esArchivo(){
        return tipo == 0;
    }
    public boolean esAudio(){
        return tipo == 1;
    }
    public boolean esApk(){
        return tipo == 2;
    }
    public boolean esVideo(){
        return tipo == 3;
    }
    public boolean esImagen(){
        return tipo == 4;
    }
    public boolean esTXT(){
        return tipo == 5;
    }
    public boolean esGIF(){
        return tipo == 6;
    }
    public boolean esComprimido(){
        return tipo == 7;
    }
    public boolean esXML(){
        return tipo == 8;
    }
    public boolean esPDF(){
        return tipo == 9;
    }
    public boolean esSticker(){
        return tipo == 10;
    }
}
