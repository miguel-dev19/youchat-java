package cu.alexgi.youchat.items;

public class ItemFondo {

    public boolean estaSeleccionado;
    public boolean esInterno;
    private int drawable;
    private String ruta;

    public ItemFondo(boolean estaSeleccionado, boolean esInterno, int drawable, String ruta) {
        this.estaSeleccionado = estaSeleccionado;
        this.esInterno = esInterno;
        this.drawable = drawable;
        this.ruta = ruta;
    }

    public boolean isEstaSeleccionado() {
        return estaSeleccionado;
    }

    public void setEstaSeleccionado(boolean estaSeleccionado) {
        this.estaSeleccionado = estaSeleccionado;
    }

    public boolean isEsInterno() {
        return esInterno;
    }

    public void setEsInterno(boolean esInterno) {
        this.esInterno = esInterno;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }
}
