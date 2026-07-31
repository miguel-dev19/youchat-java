package cu.alexgi.youchat.items;

public class ItemDetallesTarjeta {
    private int posLottie;
    private int tipoLottie;
    private int colorFondo;

    public ItemDetallesTarjeta(int posLottie, int tipoLottie, int colorFondo) {
        this.posLottie = posLottie;
        this.tipoLottie = tipoLottie;
        this.colorFondo = colorFondo;
    }

    public int getPosLottie() {
        return posLottie;
    }

    public void setPosLottie(int posLottie) {
        this.posLottie = posLottie;
    }

    public int getTipoLottie() {
        return tipoLottie;
    }

    public void setTipoLottie(int tipoLottie) {
        this.tipoLottie = tipoLottie;
    }

    public int getColorFondo() {
        return colorFondo;
    }

    public void setColorFondo(int colorFondo) {
        this.colorFondo = colorFondo;
    }

    public String detallesTarjetaToMensaje() {
        String cad = "";
        cad+=posLottie;
        cad+="<s,p>";
        cad+=tipoLottie;
        cad+="<s,p>";
        cad+=colorFondo;
        return cad;
    }
}
