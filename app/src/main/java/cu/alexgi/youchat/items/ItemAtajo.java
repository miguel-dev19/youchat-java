package cu.alexgi.youchat.items;

public class ItemAtajo {

    private String comando;
    private String descripcion;

    public ItemAtajo(String comando, String descripcion) {
        this.comando = comando;
        this.descripcion = descripcion;
    }

    public String getComando() {
        return comando;
    }

    public void setComando(String comando) {
        this.comando = comando;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
