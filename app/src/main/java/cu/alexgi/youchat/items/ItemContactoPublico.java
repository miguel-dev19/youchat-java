package cu.alexgi.youchat.items;

import cu.alexgi.youchat.Convertidor;
import cu.alexgi.youchat.YouChatApplication;

public class ItemContactoPublico {

    private String alias;
    private String correo;

    private String info;
    private String telefono;
    private String genero;
    private String provincia;
    private String fecha_nac;

    //no bd
    private boolean cumpleHoy;
    private int edad;

    private ItemContacto contacto;

    public ItemContactoPublico(String alias, String correo,
                               String info, String telefono,
                               String genero, String provincia,
                               String fecha_nac) {
        this.alias = alias;
        this.correo = correo;
        this.info = info;
        this.telefono = telefono;
        this.genero = genero;
        this.provincia = provincia;
        this.fecha_nac = fecha_nac;

        //no bd
        cumpleHoy = false;
        edad=-1;
        if(!fecha_nac.isEmpty()){
            int ano = Convertidor.createIntOfString(fecha_nac.substring(fecha_nac.length()-4));
            int dif = YouChatApplication.anoActual-ano;
            if(dif>0) edad = dif;
            if(fecha_nac.contains(YouChatApplication.fechaActual))
                cumpleHoy = true;
        }
        contacto = null;
    }

    public ItemContacto getContacto() {
        return contacto;
    }

    public void setContacto(ItemContacto contacto) {
        this.contacto = contacto;
    }

    public boolean isCumpleHoy() {
        return cumpleHoy;
    }

    public int getEdad() {
        return edad;
    }

    public String getAlias() {
        if(alias.isEmpty())
            return correo;
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
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

    public String obtenerCadBuscar(){
        String cad = "";
        cad+=alias+", ";
        cad+=correo+", ";
        cad+=info+", ";
        cad+=telefono+", ";
        cad+=genero+", ";
        cad+=provincia+", ";
        cad+=fecha_nac;
        return cad.toLowerCase();
    }

    public String obtenerCadOrdenada(){
        String cad = "";
        cad+="alias: "+alias+"\n";
        cad+="correo: "+correo+"\n";
        cad+="info: "+info+"\n";
        cad+="telefono: "+telefono+"\n";
        cad+="genero: "+genero+"\n";
        cad+="provincia: "+provincia+"\n";
        cad+="fecha_nac: "+fecha_nac;
        return cad.toLowerCase();
    }
}
