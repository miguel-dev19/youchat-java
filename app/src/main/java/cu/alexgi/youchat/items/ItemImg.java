package cu.alexgi.youchat.items;

public class ItemImg {
    private String ruta;
    private String texto;
    private String rutaOriginal;
    private int calidad;

    public ItemImg(String r){
        ruta=r;
        texto="";
        rutaOriginal = r;
        calidad = 10;
    }

    public ItemImg(String ruta, String rutaOriginal, int calidad) {
        this.ruta = ruta;
        texto="";
        this.rutaOriginal = rutaOriginal;
        this.calidad = calidad;
    }

    public int getCalidad() {
        return calidad;
    }

    public void setCalidad(int calidad) {
        this.calidad = calidad;
    }

    public String getRutaOriginal() {
        return rutaOriginal;
    }

    public void setRutaOriginal(String rutaOriginal) {
        this.rutaOriginal = rutaOriginal;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}
