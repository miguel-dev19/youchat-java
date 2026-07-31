package cu.alexgi.youchat.items;

import cu.alexgi.youchat.YouChatApplication;

public class ItemContacto {

    public static int TIPO_CONTACTO=1;
    public static int TIPO_CONTACTO_INVISIBLE=2;
    public static int TIPO_GRUPO=3;
    public static int TIPO_CANAL=4;

    public static int TIPO_CONTACTO_MASCOTA=5;

    public static int TIPO_DIVIDER=66;

    //usuario correo ruta_img info telefono genero provincia fecha_nacimiento

    private String alias;
    private String nombre_personal;
    private String correo;

    private int tipo_contacto;
    private int version;

    private String ruta_img;
    private String info;
    private String telefono;
    private String genero;
    private String provincia;
    private String fecha_nac;

    private String ult_hora_conex;
    private String ult_fecha_conex;

    private boolean usaYouchat;

    private boolean silenciado;
    private boolean bloqueado;

    //bd v3
    private int cant_seguidores;

    public ItemContacto(String textDivider){
        this.alias = "";
        this.nombre_personal = "";
        this.correo = "";
        this.tipo_contacto = 66;
        this.version = 0;
        this.ruta_img = "";
        this.info = textDivider;
        this.telefono = "";
        this.genero = "";
        this.provincia = "";
        this.fecha_nac = "";
        this.ult_hora_conex = "";
        this.ult_fecha_conex = "";
        this.usaYouchat=false;
        this.silenciado = false;
        this.bloqueado = false;
        this.cant_seguidores = 0;
    }

    public ItemContacto(String alias, String correo){
        this.alias = alias;
        this.nombre_personal = "";
        this.correo = correo;
        this.tipo_contacto = 1;
        this.version = 0;
        this.ruta_img = "";
        this.info = "";
        this.telefono = "";
        this.genero = "";
        this.provincia = "";
        this.fecha_nac = "";
        this.ult_hora_conex = "";
        this.ult_fecha_conex = "";

        this.usaYouchat=true;

        this.silenciado = false;
        this.bloqueado = false;

        this.cant_seguidores = 0;
    }

    public ItemContacto(String alias, String nombre, String correo,
                        int tipo_contacto, int version,
                        String ruta_img, String info, String telefono, String genero,
                        String provincia, String fecha_nac, String uhc, String ufc,
                        boolean usaYouchat, boolean silenciado, boolean bloqueado,
                        int cantSeg) {
        this.alias = alias;
        this.nombre_personal = nombre;
        this.correo = correo;
        this.tipo_contacto = tipo_contacto;
        this.version = version;
        this.ruta_img = ruta_img;
        this.info = info;
        this.telefono = telefono;
        this.genero = genero;
        this.provincia = provincia;
        this.fecha_nac = fecha_nac;
        this.ult_hora_conex = uhc;
        this.ult_fecha_conex = ufc;

        this.usaYouchat=usaYouchat;

        this.silenciado = silenciado;
        this.bloqueado = bloqueado;

        this.cant_seguidores = cantSeg;
    }

    public ItemContacto(String alias, String nombre, String correo,
                        int tipo_contacto, int version,
                        String ruta_img, String info, String telefono, String genero,
                        String provincia, String fecha_nac, String uhc, String ufc,
                        int usaYC, int sil, int bloq, int cantSeg) {
        this.alias = alias;
        this.nombre_personal = nombre;
        this.correo = correo;
        this.tipo_contacto = tipo_contacto;
        this.version = version;
        this.ruta_img = ruta_img;
        this.info = info;
        this.telefono = telefono;
        this.genero = genero;
        this.provincia = provincia;
        this.fecha_nac = fecha_nac;

        this.ult_hora_conex = uhc;
        this.ult_fecha_conex = ufc;

        if(usaYC==1) this.usaYouchat = true;
        else this.usaYouchat = false;

        if(sil==1) this.silenciado = true;
        else this.silenciado = false;
        if(bloq==1) this.bloqueado = true;
        else this.bloqueado = false;

        this.cant_seguidores = cantSeg;
    }

    public int getCant_seguidores() {
        return cant_seguidores;
    }

    public void setCant_seguidores(int cant_seguidores) {
        this.cant_seguidores = cant_seguidores;
    }

    public String getAlias() {
        if(alias.equals(correo))
            return "";
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getNombre_personal() {
        if(!nombre_personal.equals(""))
            return nombre_personal;
        return correo;
    }

    public void setNombre_personal(String nombre_personal) {
        this.nombre_personal = nombre_personal;
    }

    public String getNombreMostrar(){
        if(correo.equals(YouChatApplication.idOficial))
            return "YouChat Oficial";
        else if(!nombre_personal.trim().equals("") && !nombre_personal.equals(correo))
            return nombre_personal;
        else if(!alias.trim().equals(""))
            return alias;
        return correo;
    }

    public boolean isUsaYouchat() {
        return usaYouchat;
    }

    public void setUsaYouchat(boolean usaYouchat) {
        this.usaYouchat = usaYouchat;
    }

    public int getUsaYouchat(){
        if(usaYouchat) return 1;
        return 0;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getRuta_img() {
        return ruta_img;
    }

    public void setRuta_img(String ruta_img) {
        this.ruta_img = ruta_img;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getFecha_nac() {
        return fecha_nac;
    }

    public void setFecha_nac(String fecha_nac) {
        this.fecha_nac = fecha_nac;
    }

    public int getTipo_contacto() {
        return tipo_contacto;
    }

    public void setTipo_contacto(int tipo_contacto) {
        this.tipo_contacto = tipo_contacto;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isSilenciado() {
        return silenciado;
    }

    public void setSilenciado(boolean silenciado) {
        this.silenciado = silenciado;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public String getUlt_hora_conex() {
        return ult_hora_conex;
    }

    public void setUlt_hora_conex(String ult_hora_conex) {
        this.ult_hora_conex = ult_hora_conex;
    }

    public String getUlt_fecha_conex() {
        return ult_fecha_conex;
    }

    public void setUlt_fecha_conex(String ult_fecha_conex) {
        this.ult_fecha_conex = ult_fecha_conex;
    }

    public int getSilenciado(){
        if(silenciado)
            return 1;
        return 0;
    }

    public int getBloqueado(){
        if(bloqueado)
            return 1;
        return 0;
    }

    public boolean esUsuario(){
        if(tipo_contacto==TIPO_CONTACTO || tipo_contacto==TIPO_CONTACTO_INVISIBLE)
            return true;
        return false;
    }
    public boolean esMascota(){
        if(tipo_contacto==TIPO_CONTACTO_MASCOTA)
            return true;
        return false;
    }
    public boolean esGrupo(){
        if(tipo_contacto==TIPO_GRUPO)
            return true;
        return false;
    }
    public boolean esCanal(){
        if(tipo_contacto==TIPO_CANAL)
            return true;
        return false;
    }
}
