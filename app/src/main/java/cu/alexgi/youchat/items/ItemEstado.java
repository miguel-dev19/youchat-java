package cu.alexgi.youchat.items;

public class ItemEstado {

    private String id;
    private String correo;
    private int tipo_estado;
    private boolean esta_visto;

    private String ruta_imagen;
    private String texto;

    private int cant_me_gusta;
    private int cant_me_encanta;
    private int cant_me_sonroja;
    private int cant_me_divierte;
    private int cant_me_asombra;
    private int cant_me_entristese;
    private int cant_me_enoja;

    private String hora;
    private String fecha;
    private String orden;

    //bd v5
    private int estilo_texto;

    //bd v6
    private boolean descargado;
    private long uid;
    private String id_mensaje;
    private int peso_img;

    public ItemEstado(int tipo_estado){
        this.id = "";
        this.correo = "";
        this.tipo_estado = tipo_estado;
        this.esta_visto = true;
        this.ruta_imagen = "";
        this.texto = "";
        cant_me_gusta = 0;
        cant_me_encanta = 0;
        cant_me_sonroja = 0;
        cant_me_divierte = 0;
        cant_me_asombra = 0;
        cant_me_entristese = 0;
        cant_me_enoja = 0;
        this.hora = "";
        this.fecha = "";
        this.orden = "";
        estilo_texto = 0;
        descargado = true;
        uid = 0;
        id_mensaje="";
        peso_img=0;
    }

    public ItemEstado(String id, String correo, int tipo_estado, boolean esta_visto,
                      String ruta_imagen, String texto, int cant_me_gusta,
                      int cant_me_encanta, int cant_me_sonroja, int cant_me_divierte,
                      int cant_me_asombra, int cant_me_entristese, int cant_me_enoja,
                      String hora, String fecha, String orden, int estilo_texto, boolean descargado,
                      long uid, String id_mensaje, int peso_img) {
        this.id = id;
        this.correo = correo;
        this.tipo_estado = tipo_estado;
        this.esta_visto = esta_visto;
        this.ruta_imagen = ruta_imagen;
        this.texto = texto;
        this.cant_me_gusta = cant_me_gusta;
        this.cant_me_encanta = cant_me_encanta;
        this.cant_me_sonroja = cant_me_sonroja;
        this.cant_me_divierte = cant_me_divierte;
        this.cant_me_asombra = cant_me_asombra;
        this.cant_me_entristese = cant_me_entristese;
        this.cant_me_enoja = cant_me_enoja;
        this.hora = hora;
        this.fecha = fecha;
        this.orden = orden;
        this.estilo_texto = estilo_texto;
        this.descargado = descargado;
        this.uid = uid;
        this.id_mensaje = id_mensaje;
        this.peso_img = peso_img;
    }

    public int getPeso_img() {
        return peso_img;
    }

    public void setPeso_img(int peso_img) {
        this.peso_img = peso_img;
    }

    public String getId_mensaje() {
        return id_mensaje;
    }

    public void setId_mensaje(String id_mensaje) {
        this.id_mensaje = id_mensaje;
    }

    public boolean isDescargado() {
        return descargado;
    }

    public void setDescargado(boolean descargado) {
        this.descargado = descargado;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public int getEstilo_texto() {
        return estilo_texto;
    }

    public void setEstilo_texto(int estilo_texto) {
        this.estilo_texto = estilo_texto;
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

    public int getTipo_estado() {
        return tipo_estado;
    }
    public boolean esEstadoImagen(){
        if(tipo_estado==99)
            return true;
        return false;
    }

    public void setTipo_estado(int tipo_estado) {
        this.tipo_estado = tipo_estado;
    }

    public int Visibilidad(){
        if(esta_visto) return 1;
        return 0;
    }

    public boolean isEsta_visto() {
        return esta_visto;
    }

    public void setEsta_visto(boolean esta_visto) {
        this.esta_visto = esta_visto;
    }

    public String getRuta_imagen() {
        return ruta_imagen;
    }

    public void setRuta_imagen(String ruta_imagen) {
        this.ruta_imagen = ruta_imagen;
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

    public int getCant_me_gusta() {
        return cant_me_gusta;
    }

    public void setCant_me_gusta(int cant_me_gusta) {
        this.cant_me_gusta = cant_me_gusta;
    }

    public int getCant_me_encanta() {
        return cant_me_encanta;
    }

    public void setCant_me_encanta(int cant_me_encanta) {
        this.cant_me_encanta = cant_me_encanta;
    }

    public int getCant_me_sonroja() {
        return cant_me_sonroja;
    }

    public void setCant_me_sonroja(int cant_me_sonroja) {
        this.cant_me_sonroja = cant_me_sonroja;
    }

    public int getCant_me_divierte() {
        return cant_me_divierte;
    }

    public void setCant_me_divierte(int cant_me_divierte) {
        this.cant_me_divierte = cant_me_divierte;
    }

    public int getCant_me_asombra() {
        return cant_me_asombra;
    }

    public void setCant_me_asombra(int cant_me_asombra) {
        this.cant_me_asombra = cant_me_asombra;
    }

    public int getCant_me_entristese() {
        return cant_me_entristese;
    }

    public void setCant_me_entristese(int cant_me_entristese) {
        this.cant_me_entristese = cant_me_entristese;
    }

    public int getCant_me_enoja() {
        return cant_me_enoja;
    }

    public void setCant_me_enoja(int cant_me_enoja) {
        this.cant_me_enoja = cant_me_enoja;
    }

    public int reaccionDelEstado(){
//        if(!descargado) return 0;

        if(cant_me_gusta>0)
            return 1;
        if(cant_me_encanta>0)
            return 2;
        if(cant_me_sonroja>0)
            return 3;
        if(cant_me_divierte>0)
            return 4;
        if(cant_me_asombra>0)
            return 5;
        if(cant_me_entristese>0)
            return 6;
        if(cant_me_enoja>0)
            return 7;
        return 0;
    }

    public int totalReacciones() {
        return cant_me_gusta+cant_me_encanta+cant_me_sonroja+cant_me_divierte+cant_me_asombra
                +cant_me_entristese+cant_me_enoja;
    }

    public int[] obtenerTresReaccionesPopulares(){
        int[] reacPopu = {0,0,0};
        int[] arr = {cant_me_gusta, cant_me_encanta, cant_me_sonroja,
                cant_me_divierte, cant_me_asombra, cant_me_entristese, cant_me_enoja};
        for(int i=0; i<6; i++){
            for(int j=i+1; j<7; j++){
                if(arr[i]<arr[j]){
                    int temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                }
            }
        }

        int pos=0;
        for(int i=0; i<3; i++){
            if(arr[i]==0) break;
            if(arr[i]==cant_me_gusta
                    && (reacPopu[0]!=1 && reacPopu[1]!=1 && reacPopu[2]!=1)){
                reacPopu[pos] = 1;
                pos++;
            }
            else if(arr[i]==cant_me_encanta
                    && (reacPopu[0]!=2 && reacPopu[1]!=2 && reacPopu[2]!=2)){
                reacPopu[pos] = 2;
                pos++;
            }
            else if(arr[i]==cant_me_sonroja
                    && (reacPopu[0]!=3 && reacPopu[1]!=3 && reacPopu[2]!=3)){
                reacPopu[pos] = 3;
                pos++;
            }
            else if(arr[i]==cant_me_divierte
                    && (reacPopu[0]!=4 && reacPopu[1]!=4 && reacPopu[2]!=4)){
                reacPopu[pos] = 4;
                pos++;
            }
            else if(arr[i]==cant_me_asombra
                    && (reacPopu[0]!=5 && reacPopu[1]!=5 && reacPopu[2]!=5)){
                reacPopu[pos] = 5;
                pos++;
            }
            else if(arr[i]==cant_me_entristese
                    && (reacPopu[0]!=6 && reacPopu[1]!=6 && reacPopu[2]!=6)){
                reacPopu[pos] = 6;
                pos++;
            }
            else if(arr[i]==cant_me_enoja
                    && (reacPopu[0]!=7 && reacPopu[1]!=7 && reacPopu[2]!=7)){
                reacPopu[pos] = 7;
                pos++;
            }
        }
        return reacPopu;
    }
}
