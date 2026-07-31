package cu.alexgi.youchat.items;

public class ItemSticker {

    private boolean isTGS;
    private String rutaOriginal;
    private String rutaCache;

    public ItemSticker(String rutaOriginal, String rutaCache) {
        isTGS = false;
        this.rutaOriginal = rutaOriginal;
        this.rutaCache = rutaCache;
    }

    public ItemSticker(boolean isTGS, String rutaOriginal, String rutaCache) {
        this.isTGS = isTGS;
        this.rutaOriginal = rutaOriginal;
        this.rutaCache = rutaCache;
    }

    public String getRutaOriginal() {
        return rutaOriginal;
    }

    public void setRutaOriginal(String rutaOriginal) {
        this.rutaOriginal = rutaOriginal;
    }

    public String getRutaCache() {
        return rutaCache;
    }

    public void setRutaCache(String rutaCache) {
        this.rutaCache = rutaCache;
    }

    public boolean isTGS() {
        return isTGS;
    }

    public void setTGS(boolean TGS) {
        isTGS = TGS;
    }
}
