package cu.alexgi.youchat.items;

public class ItemListaSticker {

    private String nombre;
    private boolean descargado;
    private boolean descargando;
    private float progreso;

    public ItemListaSticker(String nombre, boolean descargado, boolean descargando, float progreso) {
        this.nombre = nombre;
        this.descargado = descargado;
        this.descargando = descargando;
        this.progreso = progreso;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isDescargado() {
        return descargado;
    }

    public void setDescargado(boolean descargado) {
        this.descargado = descargado;
    }

    public boolean isDescargando() {
        return descargando;
    }

    public void setDescargando(boolean descargando) {
        this.descargando = descargando;
    }

    public float getProgreso() {
        return progreso;
    }

    public void setProgreso(float progreso) {
        this.progreso = progreso;
    }

    public void cancelar() {
        this.progreso = 0;
        descargando = false;
    }

    public void DescargaFinalizada() {
        this.progreso = 0;
        descargando = false;
        descargado = true;
    }
}
