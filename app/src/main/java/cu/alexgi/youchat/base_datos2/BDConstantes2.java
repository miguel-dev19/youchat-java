package cu.alexgi.youchat.base_datos2;

public class BDConstantes2 {

    public static final String NOMBRE_BASE_DATOS = "bd_youchat_user_pub";

    /////////////////////////////////////////////////ItemContactos///////////////////////////////////////////////////
    public static final String TABLA_CONTACTO_PUBLICO="contactos";

    public static final String CONTACTO_PUBLICO_CAMPO_ALIAS="alias";
    public static final String CONTACTO_PUBLICO_CAMPO_CORREO="correo";
    public static final String CONTACTO_PUBLICO_CAMPO_INFO="info";
    public static final String CONTACTO_PUBLICO_CAMPO_TELEFONO="telefono";
    public static final String CONTACTO_PUBLICO_CAMPO_GENERO="genero";
    public static final String CONTACTO_PUBLICO_CAMPO_PROVINCIA="provincia";
    public static final String CONTACTO_PUBLICO_CAMPO_FECHA_NACIMIENTO="fecha_nacimiento";
    public static final String CONTACTO_PUBLICO_CAMPO_NOMBRE_ORDENAR="nombreOrdenar";

    public static final String CREAR_TABLA_CONTACTO_PUBLICO="CREATE TABLE IF NOT EXISTS "+TABLA_CONTACTO_PUBLICO+"("
            +CONTACTO_PUBLICO_CAMPO_ALIAS+" TEXT,"//0
            +CONTACTO_PUBLICO_CAMPO_CORREO+" TEXT,"//1
            +CONTACTO_PUBLICO_CAMPO_INFO+" TEXT,"//2
            +CONTACTO_PUBLICO_CAMPO_TELEFONO+" TEXT,"//3
            +CONTACTO_PUBLICO_CAMPO_GENERO+" TEXT,"//4
            +CONTACTO_PUBLICO_CAMPO_PROVINCIA+" TEXT,"//5
            +CONTACTO_PUBLICO_CAMPO_FECHA_NACIMIENTO+" TEXT,"//6
            +CONTACTO_PUBLICO_CAMPO_NOMBRE_ORDENAR+" INTEGER)";//7
}
