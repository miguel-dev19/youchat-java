package cu.alexgi.youchat.items;

import java.util.ArrayList;

public class ItemGrupo {
    
    public static final int TIPO_GRUPO_NORMAL = 1;
    public static final int TIPO_SUPERGRUPO = 2;
    public static final int TIPO_CANAL = 3;
    
    private String idGrupo;
    private String nombre;
    private String descripcion;
    private String creador;
    private String fotoGrupo;
    private String fechaCreacion;
    private int tipoGrupo;
    private ArrayList<String> miembros;
    private ArrayList<String> admins;
    private int totalMiembros;
    
    public ItemGrupo() {
        this.idGrupo = "";
        this.nombre = "";
        this.descripcion = "";
        this.creador = "";
        this.fotoGrupo = "";
        this.fechaCreacion = "";
        this.tipoGrupo = TIPO_GRUPO_NORMAL;
        this.miembros = new ArrayList<>();
        this.admins = new ArrayList<>();
        this.totalMiembros = 0;
    }
    
    public ItemGrupo(String idGrupo, String nombre, String creador) {
        this.idGrupo = idGrupo;
        this.nombre = nombre;
        this.descripcion = "";
        this.creador = creador;
        this.fotoGrupo = "";
        this.fechaCreacion = "";
        this.tipoGrupo = TIPO_GRUPO_NORMAL;
        this.miembros = new ArrayList<>();
        this.admins = new ArrayList<>();
        this.totalMiembros = 0;
    }
    
    // Getters y Setters
    public String getIdGrupo() { return idGrupo; }
    public void setIdGrupo(String idGrupo) { this.idGrupo = idGrupo; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getCreador() { return creador; }
    public void setCreador(String creador) { this.creador = creador; }
    
    public String getFotoGrupo() { return fotoGrupo; }
    public void setFotoGrupo(String fotoGrupo) { this.fotoGrupo = fotoGrupo; }
    
    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public int getTipoGrupo() { return tipoGrupo; }
    public void setTipoGrupo(int tipoGrupo) { this.tipoGrupo = tipoGrupo; }
    
    public ArrayList<String> getMiembros() { return miembros; }
    public void setMiembros(ArrayList<String> miembros) { 
        this.miembros = miembros;
        this.totalMiembros = miembros.size();
    }
    
    public ArrayList<String> getAdmins() { return admins; }
    public void setAdmins(ArrayList<String> admins) { this.admins = admins; }
    
    public int getTotalMiembros() { return totalMiembros; }
    
    public boolean esAdmin(String correo) {
        return admins.contains(correo);
    }
    
    public boolean esMiembro(String correo) {
        return miembros.contains(correo);
    }
    
    public boolean esCreador(String correo) {
        return creador.equals(correo);
    }
    
    public void agregarMiembro(String correo) {
        if (!miembros.contains(correo)) {
            miembros.add(correo);
            totalMiembros = miembros.size();
        }
    }
    
    public void eliminarMiembro(String correo) {
        miembros.remove(correo);
        admins.remove(correo);
        totalMiembros = miembros.size();
    }
}