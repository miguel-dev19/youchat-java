package cu.alexgi.youchat.base_datos;

public class BDConstantes {

    public static final String NOMBRE_BASE_DATOS = "bd_youchat";

    /////////////////////////////////////////////////VersionBD///////////////////////////////////////////////////
    public static final String TABLA_VERSION_BD="version_bd";

    public static final String VERSION_BD_CAMPO_VERSION="version";

    public static final String CREAR_TABLA_VERSION_BD="CREATE TABLE IF NOT EXISTS "+TABLA_VERSION_BD+"("
            +VERSION_BD_CAMPO_VERSION+" INTEGER)";//0

    /////////////////////////////////////////////////ItemContactos///////////////////////////////////////////////////
    public static final String TABLA_CONTACTO="contactos";

    public static final String CONTACTO_CAMPO_ALIAS="alias";
    public static final String CONTACTO_CAMPO_NOMBRE_PERSONAL="nombre";
    public static final String CONTACTO_CAMPO_CORREO="correo";

    public static final String CONTACTO_CAMPO_TIPO="tipo_contacto";
    public static final String CONTACTO_CAMPO_VERSION="version_contacto";

    //datos contacto tipo usuario
    public static final String CONTACTO_CAMPO_RUTA_IMG="ruta_img";
    public static final String CONTACTO_CAMPO_INFO="info";
    public static final String CONTACTO_CAMPO_TELEFONO="telefono";
    public static final String CONTACTO_CAMPO_GENERO="genero";
    public static final String CONTACTO_CAMPO_PROVINCIA="provincia";
    public static final String CONTACTO_CAMPO_FECHA_NACIMIENTO="fecha_nacimiento";

    public static final String CONTACTO_CAMPO_HORA_ULT_CONEXION="hora_ult_conexion";
    public static final String CONTACTO_CAMPO_FECHA_ULT_CONEXION="fecha_ult_conexion";

    public static final String CONTACTO_CAMPO_USA_YOUCHAT="usa_youchat";

    public static final String CONTACTO_CAMPO_SILENCIADO="silenciado";
    public static final String CONTACTO_CAMPO_BLOQUEADO="bloqueado";

    public static final String CONTACTO_CAMPO_NOMBRE_ORDENAR="nombreOrdenar";

    //bd v3
    public static final String CONTACTO_CAMPO_CANT_SEGUIDORES="cant_seguidores";


    public static final String CREAR_TABLA_CONTACTO="CREATE TABLE IF NOT EXISTS "+TABLA_CONTACTO+"("
            +CONTACTO_CAMPO_ALIAS+" TEXT,"//0
            +CONTACTO_CAMPO_NOMBRE_PERSONAL+" TEXT,"//1
            +CONTACTO_CAMPO_CORREO+" TEXT,"//2

            +CONTACTO_CAMPO_TIPO+" INTEGER,"//3
            +CONTACTO_CAMPO_VERSION+" INTEGER,"//4

            +CONTACTO_CAMPO_RUTA_IMG+" TEXT,"//5
            +CONTACTO_CAMPO_INFO+" TEXT,"//6
            +CONTACTO_CAMPO_TELEFONO+" TEXT,"//7
            +CONTACTO_CAMPO_GENERO+" TEXT,"//8
            +CONTACTO_CAMPO_PROVINCIA+" TEXT,"//9
            +CONTACTO_CAMPO_FECHA_NACIMIENTO+" TEXT,"//10

            +CONTACTO_CAMPO_HORA_ULT_CONEXION+" TEXT,"//11
            +CONTACTO_CAMPO_FECHA_ULT_CONEXION+" TEXT,"//12

            +CONTACTO_CAMPO_USA_YOUCHAT+" INTEGER,"//13

            +CONTACTO_CAMPO_SILENCIADO+" INTEGER,"//14
            +CONTACTO_CAMPO_BLOQUEADO+" INTEGER,"//15

            +CONTACTO_CAMPO_NOMBRE_ORDENAR+" TEXT,"//16
            //bd v3
            +CONTACTO_CAMPO_CANT_SEGUIDORES+" INTEGER)";//17

    public static final String SELECT_ALL_DATA_TABLE_CONTACTO_V2 = CONTACTO_CAMPO_ALIAS+","+
            CONTACTO_CAMPO_NOMBRE_PERSONAL+","+CONTACTO_CAMPO_CORREO+","+
            CONTACTO_CAMPO_TIPO+","+CONTACTO_CAMPO_VERSION+","+
            CONTACTO_CAMPO_RUTA_IMG+","+CONTACTO_CAMPO_INFO+","+
            CONTACTO_CAMPO_TELEFONO+","+CONTACTO_CAMPO_GENERO+","+
            CONTACTO_CAMPO_PROVINCIA+","+CONTACTO_CAMPO_FECHA_NACIMIENTO+","+
            CONTACTO_CAMPO_HORA_ULT_CONEXION+","+CONTACTO_CAMPO_FECHA_ULT_CONEXION+","+
            CONTACTO_CAMPO_USA_YOUCHAT+","+CONTACTO_CAMPO_SILENCIADO+","+
            CONTACTO_CAMPO_BLOQUEADO+","+CONTACTO_CAMPO_NOMBRE_ORDENAR;

    //////////////////////////////////////////////////ItemUsuario///////////////////////////////////////////////////
    public static final String TABLA_USUARIO="usuarios";

    public static final String USUARIO_CAMPO_CORREO="correo";

    public static final String USUARIO_CAMPO_ANCLADO="anclado";
    public static final String USUARIO_CAMPO_CANT_MSG="cant_msg";

    public static final String USUARIO_CAMPO_TIPO_ULT_MSG="ult_msg_tipo";
    public static final String USUARIO_CAMPO_TEXTO_ULT_MSG="ult_msg_texto";
    public static final String USUARIO_CAMPO_ESTADO_ULT_MSG="ult_msg_estado";
    public static final String USUARIO_CAMPO_ORDEN_ULT_MSG="ult_msg_orden";

    public static final String USUARIO_CAMPO_BORRADOR="borrador_usuario";

    public static final String CREAR_TABLA_USUARIO="CREATE TABLE IF NOT EXISTS "+TABLA_USUARIO+"("
            +USUARIO_CAMPO_CORREO+" TEXT,"

            +USUARIO_CAMPO_ANCLADO+" INTEGER,"
            +USUARIO_CAMPO_CANT_MSG+" INTEGER,"

            +USUARIO_CAMPO_TIPO_ULT_MSG+" INTEGER,"
            +USUARIO_CAMPO_TEXTO_ULT_MSG+" TEXT,"
            +USUARIO_CAMPO_ESTADO_ULT_MSG+" INTEGER,"
            +USUARIO_CAMPO_ORDEN_ULT_MSG+" TEXT,"
            +USUARIO_CAMPO_BORRADOR+" TEXT)";


    //////////////////////////////////////////////////ItemChat///////////////////////////////////////////////////
    public static final String TABLA_CHAT="chats";

    public static final String CHAT_CAMPO_ID="id";
    public static final String CHAT_CAMPO_TIPO_MENSAJE="tipo_mensaje";
    public static final String CHAT_CAMPO_ESTADO="estado";
    public static final String CHAT_CAMPO_CORREO="correo";
    public static final String CHAT_CAMPO_MENSAJE="mensaje";
    public static final String CHAT_CAMPO_RUTA_DATO="ruta_dato";
    public static final String CHAT_CAMPO_HORA="hora";
    public static final String CHAT_CAMPO_FECHA="fecha";
    public static final String CHAT_CAMPO_ID_MSG_RESP="id_msg_resp";
    public static final String CHAT_CAMPO_EMISOR="correo_emisor";
    public static final String CHAT_CAMPO_REENVIADO="reenviado";
    public static final String CHAT_CAMPO_ORDEN="orden";

    //bd v5
    public static final String CHAT_CAMPO_EDITADO="editado";

    //bdv7
    public static final String CHAT_CAMPO_ID_MENSAJE="id_mensaje";
    public static final String CHAT_CAMPO_PESO="peso";
    public static final String CHAT_CAMPO_ESTA_DESCARGADO="esta_descargado";

    public static final String CREAR_TABLA_CHAT="CREATE TABLE IF NOT EXISTS "+TABLA_CHAT+"("
            +CHAT_CAMPO_ID+" TEXT,"
            +CHAT_CAMPO_TIPO_MENSAJE+" INTEGER,"
            +CHAT_CAMPO_ESTADO+" INTEGER,"
            +CHAT_CAMPO_CORREO+" TEXT,"
            +CHAT_CAMPO_MENSAJE+" TEXT,"
            +CHAT_CAMPO_RUTA_DATO+" TEXT,"
            +CHAT_CAMPO_HORA+" TEXT,"
            +CHAT_CAMPO_FECHA+" TEXT,"
            +CHAT_CAMPO_ID_MSG_RESP+" TEXT,"
            +CHAT_CAMPO_EMISOR+" TEXT,"
            +CHAT_CAMPO_REENVIADO+" INTEGER,"
            +CHAT_CAMPO_ORDEN+" TEXT,"

            //bd v5
            +CHAT_CAMPO_EDITADO+" INTEGER,"

            //bd v7
            +CHAT_CAMPO_ID_MENSAJE+" TEXT,"
            +CHAT_CAMPO_PESO+" INTEGER,"
            +CHAT_CAMPO_ESTA_DESCARGADO+" INTEGER)";

    public static final String SELECT_ALL_DATA_TABLA_CHAT_V4 = CHAT_CAMPO_ID+","+
            CHAT_CAMPO_ID+","+CHAT_CAMPO_TIPO_MENSAJE+","+
            CHAT_CAMPO_ESTADO+","+CHAT_CAMPO_CORREO+","+
            CHAT_CAMPO_MENSAJE+","+CHAT_CAMPO_RUTA_DATO+","+
            CHAT_CAMPO_HORA+","+CHAT_CAMPO_FECHA+","+
            CHAT_CAMPO_ID_MSG_RESP+","+CHAT_CAMPO_EMISOR+","+
            CHAT_CAMPO_REENVIADO+","+CHAT_CAMPO_ORDEN;

    /////////////////////////////////////////////////ItemEstados///////////////////////////////////////////////////
    public static final String TABLA_ESTADO="estados";

    public static final String ESTADO_CAMPO_ID="id";
    public static final String ESTADO_CAMPO_CORREO="correo";
    public static final String ESTADO_CAMPO_TIPO="tipo_estado";
    public static final String ESTADO_CAMPO_ESTA_VISTO="esta_visto";
    /**
     * tipos de estados
     * 99 es con imagen, solo este
     * 0-16 colores
     */
    public static final String ESTADO_CAMPO_RUTA_IMAGEN="ruta_imagen";
    public static final String ESTADO_CAMPO_TEXTO="texto";

    public static final String ESTADO_CAMPO_CANTIDAD_ME_GUSTA="cant_me_gusta";
    public static final String ESTADO_CAMPO_CANTIDAD_ME_ENCANTA="cant_me_encanta";
    public static final String ESTADO_CAMPO_CANTIDAD_ME_SONROJA="cant_me_sonroja";
    public static final String ESTADO_CAMPO_CANTIDAD_ME_DIVIERTE="cant_me_divierte";
    public static final String ESTADO_CAMPO_CANTIDAD_ME_ASOMBRA="cant_me_asombra";
    public static final String ESTADO_CAMPO_CANTIDAD_ME_ENTRISTESE="cant_me_entristese";
    public static final String ESTADO_CAMPO_CANTIDAD_ME_ENOJA="cant_me_enoja";

    public static final String ESTADO_CAMPO_HORA="hora";
    public static final String ESTADO_CAMPO_FECHA="fecha";
    public static final String ESTADO_CAMPO_ORDEN="orden";

    //bd v5
    public static final String ESTADO_CAMPO_ESTILO_TEXTO="estilo_texto";

    //bd v6
    public static final String ESTADO_CAMPO_ESTA_DESCARGADO="esta_descargado";
    public static final String ESTADO_CAMPO_UID="uid";
    public static final String ESTADO_CAMPO_ID_MENSAJE="id_mensaje";
    public static final String ESTADO_CAMPO_PESO_IMG="peso_img";

    public static final String CREAR_TABLA_ESTADO="CREATE TABLE IF NOT EXISTS "+TABLA_ESTADO+"("
            +ESTADO_CAMPO_ID+" TEXT,"
            +ESTADO_CAMPO_CORREO+" TEXT,"
            +ESTADO_CAMPO_TIPO+" INTEGER,"
            +ESTADO_CAMPO_ESTA_VISTO+" INTEGER,"

            +ESTADO_CAMPO_RUTA_IMAGEN+" TEXT,"
            +ESTADO_CAMPO_TEXTO+" TEXT,"

            +ESTADO_CAMPO_CANTIDAD_ME_GUSTA+" INTEGER,"
            +ESTADO_CAMPO_CANTIDAD_ME_ENCANTA+" INTEGER,"
            +ESTADO_CAMPO_CANTIDAD_ME_SONROJA+" INTEGER,"
            +ESTADO_CAMPO_CANTIDAD_ME_DIVIERTE+" INTEGER,"
            +ESTADO_CAMPO_CANTIDAD_ME_ASOMBRA+" INTEGER,"
            +ESTADO_CAMPO_CANTIDAD_ME_ENTRISTESE+" INTEGER,"
            +ESTADO_CAMPO_CANTIDAD_ME_ENOJA+" INTEGER,"

            +ESTADO_CAMPO_HORA+" TEXT,"
            +ESTADO_CAMPO_FECHA+" TEXT,"
            +ESTADO_CAMPO_ORDEN+" TEXT,"
            //bd v5
            +ESTADO_CAMPO_ESTILO_TEXTO+" INTEGER,"
            //bd v6
            +ESTADO_CAMPO_ESTA_DESCARGADO+" INTEGER,"
            +ESTADO_CAMPO_UID+" TEXT,"
            +ESTADO_CAMPO_ID_MENSAJE+" TEXT,"
            +ESTADO_CAMPO_PESO_IMG+" INTEGER)";

    public static final String SELECT_ALL_DATA_TABLA_ESTADO_V4 = ESTADO_CAMPO_ID+","+
            ESTADO_CAMPO_CORREO+","+ESTADO_CAMPO_CORREO+","+
            ESTADO_CAMPO_TIPO+","+ESTADO_CAMPO_ESTA_VISTO+","+
            ESTADO_CAMPO_RUTA_IMAGEN+","+ESTADO_CAMPO_TEXTO+","+
            ESTADO_CAMPO_CANTIDAD_ME_GUSTA+","+ESTADO_CAMPO_CANTIDAD_ME_ENCANTA+","+
            ESTADO_CAMPO_CANTIDAD_ME_SONROJA+","+ESTADO_CAMPO_CANTIDAD_ME_DIVIERTE+","+
            ESTADO_CAMPO_CANTIDAD_ME_ASOMBRA+","+ESTADO_CAMPO_CANTIDAD_ME_ENTRISTESE+","+
            ESTADO_CAMPO_CANTIDAD_ME_ENOJA+","+ESTADO_CAMPO_HORA+","+
            ESTADO_CAMPO_FECHA+","+ESTADO_CAMPO_ORDEN;

    //////////////////////////////////////////////////ItemSeguidor///////////////////////////////////////////////////
    public static final String TABLA_SEGUIDOR="seguidores";

    public static final String SEGUIDOR_CAMPO_ID="correo";

    public static final String CREAR_TABLA_SEGUIDOR="CREATE TABLE IF NOT EXISTS "+TABLA_SEGUIDOR+"("
            +SEGUIDOR_CAMPO_ID+" TEXT)";

    //////////////////////////////////////////////////ItemSiguiendoA///////////////////////////////////////////////////
    public static final String TABLA_SIGUIENDO_A="siguiendo_a";

    public static final String SIGUIENDO_A_CAMPO_ID="correo";

    public static final String CREAR_TABLA_SIGUIENDO_A="CREATE TABLE IF NOT EXISTS "+TABLA_SIGUIENDO_A+"("
            +SIGUIENDO_A_CAMPO_ID+" TEXT)";

    /////////////////////////////////////////////////ItemReaccionEstados///////////////////////////////////////////////////
    public static final String TABLA_REACCION_ESTADO="reaccion_estados";

    public static final String REACCION_CAMPO_ID_ESTADO="id_estado";
    public static final String REACCION_CAMPO_CORREO="correo";
    public static final String REACCION_CAMPO_TIPO_REACCION="tipo_reaccion";
    /**
     * tipos de reacciones
     * 1 me gusta
     * 2 me encanta
     * 3 me sonroja
     * 4 me divierte
     * 5 me asombra
     * 6 me entristese
     * 7 me enoja
     */

    public static final String REACCION_CAMPO_HORA="hora";
    public static final String REACCION_CAMPO_FECHA="fecha";
//    public static final String ESTADO_CAMPO_ORDEN="orden";


    public static final String CREAR_TABLA_REACCION_ESTADO="CREATE TABLE IF NOT EXISTS "+TABLA_REACCION_ESTADO+"("
            +REACCION_CAMPO_ID_ESTADO+" TEXT,"
            +REACCION_CAMPO_CORREO+" TEXT,"
            +REACCION_CAMPO_TIPO_REACCION+" INTEGER,"
            +REACCION_CAMPO_HORA+" TEXT,"
            +REACCION_CAMPO_FECHA+" TEXT)";

    /////////////////////////////////////////////////ItemVistaEstados///////////////////////////////////////////////////
    public static final String TABLA_VISTA_ESTADO="vista_estados";

    public static final String VISTA_ESTADO_CAMPO_ID_ESTADO="id_estado";
    public static final String VISTA_ESTADO_CAMPO_CORREO="correo";
    public static final String VISTA_ESTADO_CAMPO_HORA="hora";
    public static final String VISTA_ESTADO_CAMPO_FECHA="fecha";


    public static final String CREAR_TABLA_VISTA_ESTADO="CREATE TABLE IF NOT EXISTS "+TABLA_VISTA_ESTADO+"("
            +VISTA_ESTADO_CAMPO_ID_ESTADO+" TEXT,"
            +VISTA_ESTADO_CAMPO_CORREO+" TEXT,"
            +VISTA_ESTADO_CAMPO_HORA+" TEXT,"
            +VISTA_ESTADO_CAMPO_FECHA+" TEXT)";

    //v5///////////////////////////////////////////////MiPerfil///////////////////////////////////////////////////
    public static final String TABLA_MI_PERFIL="mi_perfil";

    public static final String MI_PERFIL_CAMPO_CORREO="correo";
    public static final String MI_PERFIL_CAMPO_VERSION_BD="version_bd";
    public static final String MI_PERFIL_CAMPO_NOMBRE="nombre";
    public static final String MI_PERFIL_CAMPO_INFO="info";
    public static final String MI_PERFIL_CAMPO_TELEFONO="telefono";
    public static final String MI_PERFIL_CAMPO_GENERO="genero";
    public static final String MI_PERFIL_CAMPO_PROVINCIA="provincia";
    public static final String MI_PERFIL_CAMPO_FECHA_NACIMIENTO="fecha_nacimiento";
    public static final String MI_PERFIL_CAMPO_RUTA_IMG_PERFIL="ruta_img_perfil";
    public static final String MI_PERFIL_CAMPO_FECHA_CUMPLE="fecha_cumple";
    public static final String MI_PERFIL_CAMPO_VERSION_INFO="version_info";

    public static final String MI_PERFIL_CAMPO_TEMA_APP="tema_app";
    public static final String MI_PERFIL_CAMPO_COLOR_APP="color_app";
    public static final String MI_PERFIL_CAMPO_TAM_FUENTE="tam_fuente";


    public static final String CREAR_TABLA_MI_PERFIL="CREATE TABLE IF NOT EXISTS "+TABLA_MI_PERFIL+"("
            +MI_PERFIL_CAMPO_CORREO+" TEXT,"
            +MI_PERFIL_CAMPO_VERSION_BD+" INTEGER,"
            +MI_PERFIL_CAMPO_NOMBRE+" TEXT,"
            +MI_PERFIL_CAMPO_INFO+" TEXT,"
            +MI_PERFIL_CAMPO_TELEFONO+" TEXT,"
            +MI_PERFIL_CAMPO_GENERO+" TEXT,"
            +MI_PERFIL_CAMPO_PROVINCIA+" TEXT,"
            +MI_PERFIL_CAMPO_FECHA_NACIMIENTO+" TEXT,"
            +MI_PERFIL_CAMPO_RUTA_IMG_PERFIL+" TEXT,"
            +MI_PERFIL_CAMPO_FECHA_CUMPLE+" TEXT,"
            +MI_PERFIL_CAMPO_VERSION_INFO+" INTEGER,"
            +MI_PERFIL_CAMPO_TEMA_APP+" INTEGER,"
            +MI_PERFIL_CAMPO_COLOR_APP+" INTEGER,"
            +MI_PERFIL_CAMPO_TAM_FUENTE+" INTEGER)";

    //v5///////////////////////////////////////////////ItemPost///////////////////////////////////////////////////
    public static final String TABLA_POST="post";

    public static final String POST_CAMPO_ID="id";
    public static final String POST_CAMPO_UID="uid";
    public static final String POST_CAMPO_TIPO_POST="tipo_post";
    public static final String POST_CAMPO_NOMBRE="nombre";
    public static final String POST_CAMPO_CORREO="correo";
    public static final String POST_CAMPO_TIPO_USUARIO="tipo_usuario";
    public static final String POST_CAMPO_ICONO="icono";
    public static final String POST_CAMPO_TEXTO="texto";

    public static final String POST_CAMPO_ES_NUEVO="es_nuevo";

    public static final String POST_CAMPO_RUTA_DATO="ruta_dato";
    public static final String POST_CAMPO_PESO_DATO="peso_dato";

    public static final String POST_CAMPO_HORA="hora";
    public static final String POST_CAMPO_FECHA="fecha";
    public static final String POST_CAMPO_ORDEN="orden";

    public static final String CREAR_TABLA_POST="CREATE TABLE IF NOT EXISTS "+TABLA_POST+"("
            +POST_CAMPO_ID+" TEXT,"
            +POST_CAMPO_UID+" TEXT,"
            +POST_CAMPO_TIPO_POST+" INTEGER,"
            +POST_CAMPO_NOMBRE+" TEXT,"
            +POST_CAMPO_CORREO+" TEXT,"
            +POST_CAMPO_TIPO_USUARIO+" INTEGER,"
            +POST_CAMPO_ICONO+" INTEGER,"
            +POST_CAMPO_TEXTO+" TEXT,"
            +POST_CAMPO_ES_NUEVO+" INTEGER,"
            +POST_CAMPO_RUTA_DATO+" TEXT,"
            +POST_CAMPO_PESO_DATO+" INTEGER,"
            +POST_CAMPO_HORA+" TEXT,"
            +POST_CAMPO_FECHA+" TEXT,"
            +POST_CAMPO_ORDEN+" TEXT)";

    //v5////////////////////////////////////////////////ItemListaNegraPost///////////////////////////////////////////////////
    public static final String TABLA_LISTA_NEGRA_POST="lista_negra_post";

    public static final String LISTA_NEGRA_POST_CAMPO_ID="correo";

    public static final String CREAR_TABLA_LISTA_NEGRA_POST="CREATE TABLE IF NOT EXISTS "+TABLA_LISTA_NEGRA_POST+"("
            +LISTA_NEGRA_POST_CAMPO_ID+" TEXT)";

    //v5////////////////////////////////////////////////ItemEstadisticaPersonal///////////////////////////////////////////////////
    public static final String TABLA_ESTADISTICA_PERSONAL="estadistica_personal";

    public static final String ESTADISTICA_PERSONAL_CAMPO_ID="correo";
    public static final String ESTADISTICA_PERSONAL_CAMPO_CANT_MSG_ENVIADOS="cant_msg_env";
    public static final String ESTADISTICA_PERSONAL_CAMPO_CANT_MSG_ENVIADOS_MG="cant_msg_env_mg";
    public static final String ESTADISTICA_PERSONAL_CAMPO_CANT_MSG_RECIBIDOS="cant_msg_rec";
    public static final String ESTADISTICA_PERSONAL_CAMPO_CANT_MSG_RECIBIDOS_MG="cant_msg_rec_mg";
    public static final String ESTADISTICA_PERSONAL_CAMPO_CANT_IMG_ENVIADAS="cant_img_env";
    public static final String ESTADISTICA_PERSONAL_CAMPO_CANT_IMG_ENVIADAS_MG="cant_img_env_mg";
    public static final String ESTADISTICA_PERSONAL_CAMPO_CANT_IMG_RECIBIDAS="cant_img_rec";
    public static final String ESTADISTICA_PERSONAL_CAMPO_CANT_IMG_RECIBIDAS_MG="cant_img_rec_mg";
    public static final String ESTADISTICA_PERSONAL_CAMPO_CANT_AUD_ENVIADOS="cant_aud_env";
    public static final String ESTADISTICA_PERSONAL_CAMPO_CANT_AUD_ENVIADOS_MG="cant_aud_env_mg";
    public static final String ESTADISTICA_PERSONAL_CAMPO_CANT_AUD_RECIBIDOS="cant_aud_rec";
    public static final String ESTADISTICA_PERSONAL_CAMPO_CANT_AUD_RECIBIDOS_MG="cant_aud_rec_mg";
    public static final String ESTADISTICA_PERSONAL_CAMPO_CANT_ARC_ENVIADOS="cant_arc_env";
    public static final String ESTADISTICA_PERSONAL_CAMPO_CANT_ARC_ENVIADOS_MG="cant_arc_env_mg";
    public static final String ESTADISTICA_PERSONAL_CAMPO_CANT_ARC_RECIBIDOS="cant_arc_rec";
    public static final String ESTADISTICA_PERSONAL_CAMPO_CANT_ARC_RECIBIDOS_MG="cant_arc_rec_mg";

    public static final String ESTADISTICA_PERSONAL_CAMPO_CANT_STI_ENVIADOS="cant_sti_env";
    public static final String ESTADISTICA_PERSONAL_CAMPO_CANT_STI_ENVIADOS_MG="cant_sti_env_mg";
    public static final String ESTADISTICA_PERSONAL_CAMPO_CANT_STI_RECIBIDOS="cant_sti_rec";
    public static final String ESTADISTICA_PERSONAL_CAMPO_CANT_STI_RECIBIDOS_MG="cant_sti_rec_mg";

    public static final String ESTADISTICA_PERSONAL_CAMPO_CANT_ESTADOS_RECIBIDOS="cant_est_rec";
    public static final String ESTADISTICA_PERSONAL_CAMPO_CANT_ESTADOS_RECIBIDOS_MG="cant_est_rec_mg";
    public static final String ESTADISTICA_PERSONAL_CAMPO_CANT_ACT_PERFIL_RECIBIDAS="cant_act_per_rec";
    public static final String ESTADISTICA_PERSONAL_CAMPO_CANT_ACT_PERFIL_RECIBIDAS_MG="cant_act_per_rec_mg";

    public static final String CREAR_TABLA_ESTADISTICA_PERSONAL="CREATE TABLE IF NOT EXISTS "+TABLA_ESTADISTICA_PERSONAL+"("
            +ESTADISTICA_PERSONAL_CAMPO_ID+" TEXT,"//0
            +ESTADISTICA_PERSONAL_CAMPO_CANT_MSG_ENVIADOS+" INTEGER,"//1
            +ESTADISTICA_PERSONAL_CAMPO_CANT_MSG_ENVIADOS_MG+" INTEGER,"//2
            +ESTADISTICA_PERSONAL_CAMPO_CANT_MSG_RECIBIDOS+" INTEGER,"//3
            +ESTADISTICA_PERSONAL_CAMPO_CANT_MSG_RECIBIDOS_MG+" INTEGER,"//4
            +ESTADISTICA_PERSONAL_CAMPO_CANT_IMG_ENVIADAS+" INTEGER,"//5
            +ESTADISTICA_PERSONAL_CAMPO_CANT_IMG_ENVIADAS_MG+" INTEGER,"//6
            +ESTADISTICA_PERSONAL_CAMPO_CANT_IMG_RECIBIDAS+" INTEGER,"//7
            +ESTADISTICA_PERSONAL_CAMPO_CANT_IMG_RECIBIDAS_MG+" INTEGER,"//8
            +ESTADISTICA_PERSONAL_CAMPO_CANT_AUD_ENVIADOS+" INTEGER,"//9
            +ESTADISTICA_PERSONAL_CAMPO_CANT_AUD_ENVIADOS_MG+" INTEGER,"//10
            +ESTADISTICA_PERSONAL_CAMPO_CANT_AUD_RECIBIDOS+" INTEGER,"//11
            +ESTADISTICA_PERSONAL_CAMPO_CANT_AUD_RECIBIDOS_MG+" INTEGER,"//12
            +ESTADISTICA_PERSONAL_CAMPO_CANT_ARC_ENVIADOS+" INTEGER,"//13
            +ESTADISTICA_PERSONAL_CAMPO_CANT_ARC_ENVIADOS_MG+" INTEGER,"//14
            +ESTADISTICA_PERSONAL_CAMPO_CANT_ARC_RECIBIDOS+" INTEGER,"//15
            +ESTADISTICA_PERSONAL_CAMPO_CANT_ARC_RECIBIDOS_MG+" INTEGER,"//16

            +ESTADISTICA_PERSONAL_CAMPO_CANT_STI_ENVIADOS+" INTEGER,"//17
            +ESTADISTICA_PERSONAL_CAMPO_CANT_STI_ENVIADOS_MG+" INTEGER,"//18
            +ESTADISTICA_PERSONAL_CAMPO_CANT_STI_RECIBIDOS+" INTEGER,"//19
            +ESTADISTICA_PERSONAL_CAMPO_CANT_STI_RECIBIDOS_MG+" INTEGER,"//20

            +ESTADISTICA_PERSONAL_CAMPO_CANT_ESTADOS_RECIBIDOS+" INTEGER,"//21
            +ESTADISTICA_PERSONAL_CAMPO_CANT_ESTADOS_RECIBIDOS_MG+" INTEGER,"//22
            +ESTADISTICA_PERSONAL_CAMPO_CANT_ACT_PERFIL_RECIBIDAS+" INTEGER,"//23
            +ESTADISTICA_PERSONAL_CAMPO_CANT_ACT_PERFIL_RECIBIDAS_MG+" INTEGER)";//24

    //v5////////////////////////////////////////////////ItemTemas///////////////////////////////////////////////////
    public static final String TABLA_TEMAS="temas";
    /*
        TIPO
     * 1- SISTEMA
     * 2- TEMA RECIBIDO
     * 3- TEMA CREADO
     */

    public static final String TEMAS_CAMPO_ID="id";
    public static final String TEMAS_CAMPO_NOMBRE="nombre";
    public static final String TEMAS_CAMPO_TIPO="tipo";
    public static final String TEMAS_CAMPO_OSCURO="oscuro";
    public static final String TEMAS_CAMPO_RUTA_IMG="rutaImg";
    public static final String TEMAS_CAMPO_CREADOR="creador";

    public static final String TEMAS_CAMPO_COLOR_BARRA="color_barra";
    public static final String TEMAS_CAMPO_COLOR_BTN="color_btn";
    public static final String TEMAS_CAMPO_COLOR_TEXTO="color_texto";
    public static final String TEMAS_CAMPO_COLOR_FONDO="color_fondo";
    public static final String TEMAS_CAMPO_COLOR_INTERIOR="color_interior";
    public static final String TEMAS_CAMPO_COLOR_MSG_IZQ="color_msg_izq";
    public static final String TEMAS_CAMPO_COLOR_MSG_DER="color_msg_der";
    public static final String TEMAS_CAMPO_COLOR_MSG_FECHA="color_msg_fecha";
    public static final String TEMAS_CAMPO_COLOR_ACCENTO="color_accento";
    public static final String TEMAS_CAMPO_COLOR_ICO_GEN="color_ico_gen";

    public static final String TEMAS_CAMPO_FONT_BARRA="font_barra";
    public static final String TEMAS_CAMPO_FONT_MSG_IZQ="font_msg_izq";
    public static final String TEMAS_CAMPO_FONT_MSG_DER="font_msg_der";
    public static final String TEMAS_CAMPO_FONT_MSG_FECHA="font_msg_fecha";
    public static final String TEMAS_CAMPO_FONT_ICO="font_ico";

    public static final String TEMAS_CAMPO_COLOR_DIALOGO="color_dialg";
    public static final String TEMAS_CAMPO_COLOR_TOAST="color_toast";
    public static final String TEMAS_CAMPO_COLOR_BURBUJA="color_burbuja";
    public static final String TEMAS_CAMPO_FONT_BURBUJA="font_burbuja";
    public static final String TEMAS_CAMPO_COLOR_BARCHAT="color_barchat";
    public static final String TEMAS_CAMPO_FONT_BARCHAT="font_barchat";

    public static final String TEMAS_CAMPO_COLOR_SEL_MSJ="sel_msg";
    public static final String TEMAS_CAMPO_COLOR_STATUS_BAR="status_bar";

    public static final String CREAR_TABLA_TEMAS="CREATE TABLE IF NOT EXISTS "+TABLA_TEMAS+"("
            +TEMAS_CAMPO_ID+" TEXT,"
            +TEMAS_CAMPO_NOMBRE+" TEXT,"
            +TEMAS_CAMPO_TIPO+" INTEGER,"
            +TEMAS_CAMPO_OSCURO+" INTEGER,"
            +TEMAS_CAMPO_RUTA_IMG+" TEXT,"
            +TEMAS_CAMPO_CREADOR+" TEXT,"

            +TEMAS_CAMPO_COLOR_BARRA+" TEXT,"
            +TEMAS_CAMPO_COLOR_BTN+" TEXT,"
            +TEMAS_CAMPO_COLOR_TEXTO+" TEXT,"
            +TEMAS_CAMPO_COLOR_FONDO+" TEXT,"
            +TEMAS_CAMPO_COLOR_INTERIOR+" TEXT,"
            +TEMAS_CAMPO_COLOR_MSG_IZQ+" TEXT,"
            +TEMAS_CAMPO_COLOR_MSG_DER+" TEXT,"
            +TEMAS_CAMPO_COLOR_MSG_FECHA+" TEXT,"
            +TEMAS_CAMPO_COLOR_ACCENTO+" TEXT,"
            +TEMAS_CAMPO_COLOR_ICO_GEN+" TEXT,"

            +TEMAS_CAMPO_FONT_BARRA+" TEXT,"
            +TEMAS_CAMPO_FONT_MSG_IZQ+" TEXT,"
            +TEMAS_CAMPO_FONT_MSG_DER+" TEXT,"
            +TEMAS_CAMPO_FONT_MSG_FECHA+" TEXT,"
            +TEMAS_CAMPO_FONT_ICO+" TEXT,"

            +TEMAS_CAMPO_COLOR_DIALOGO+" TEXT,"
            +TEMAS_CAMPO_COLOR_TOAST+" TEXT,"
            +TEMAS_CAMPO_COLOR_BURBUJA+" TEXT,"
            +TEMAS_CAMPO_FONT_BURBUJA+" TEXT,"
            +TEMAS_CAMPO_COLOR_BARCHAT+" TEXT,"
            +TEMAS_CAMPO_FONT_BARCHAT+" TEXT,"
            +TEMAS_CAMPO_COLOR_SEL_MSJ+" TEXT,"
            +TEMAS_CAMPO_COLOR_STATUS_BAR+" TEXT)";

    //v6///////////////////////////////////////////////ItemUsuarioCorreo///////////////////////////////////////////////////
    public static final String TABLA_USUARIO_CORREO="usuario_correo";

    public static final String USUARIO_CORREO_CAMPO_CORREO="correo";
    public static final String USUARIO_CORREO_CAMPO_NOMBRE="nombre";

    public static final String USUARIO_CORREO_CAMPO_CHAT_GROUP_ID="group_id";

    public static final String USUARIO_CORREO_CAMPO_HORA="hora";
    public static final String USUARIO_CORREO_CAMPO_FECHA="fecha";
    public static final String USUARIO_CORREO_CAMPO_ORDEN="orden";

    public static final String CREAR_TABLA_USUARIO_CORREO="CREATE TABLE IF NOT EXISTS "+TABLA_USUARIO_CORREO+"("
            +USUARIO_CORREO_CAMPO_CORREO+" TEXT,"
            +USUARIO_CORREO_CAMPO_NOMBRE+" TEXT,"
            +USUARIO_CORREO_CAMPO_CHAT_GROUP_ID+" TEXT,"
            +USUARIO_CORREO_CAMPO_HORA+" TEXT,"
            +USUARIO_CORREO_CAMPO_FECHA+" TEXT,"
            +USUARIO_CORREO_CAMPO_ORDEN+" TEXT)";

    //v6///////////////////////////////////////////////ItemMensajeCorreo///////////////////////////////////////////////////
    public static final String TABLA_MENSAJE_CORREO="mensaje_correo";

    public static final String MENSAJE_CORREO_CAMPO_ID="id";
    public static final String MENSAJE_CORREO_CAMPO_UID="uid";
    public static final String MENSAJE_CORREO_CAMPO_ES_MIO="es_mio";
    public static final String MENSAJE_CORREO_CAMPO_ES_NUEVO="es_nuevo";
    public static final String MENSAJE_CORREO_CAMPO_ES_FAVORITO="es_fav";
    public static final String MENSAJE_CORREO_CAMPO_CORREO="correo";
    public static final String MENSAJE_CORREO_CAMPO_REMITENTE="remitente";
    public static final String MENSAJE_CORREO_CAMPO_NOMBRE="nombre";
    public static final String MENSAJE_CORREO_CAMPO_DESTINATARIO="destinatario";
    public static final String MENSAJE_CORREO_CAMPO_ASUNTO="asunto";
    public static final String MENSAJE_CORREO_CAMPO_TEXTO="texto";
    public static final String MENSAJE_CORREO_CAMPO_ESTADO="estado";

    public static final String MENSAJE_CORREO_CAMPO_ES_RESPONDIDO="es_respondido";
    public static final String MENSAJE_CORREO_CAMPO_PESO="peso";

    public static final String MENSAJE_CORREO_CAMPO_HORA="hora";
    public static final String MENSAJE_CORREO_CAMPO_FECHA="fecha";
    public static final String MENSAJE_CORREO_CAMPO_ORDEN="orden";

    public static final String CREAR_TABLA_MENSAJE_CORREO="CREATE TABLE IF NOT EXISTS "+TABLA_MENSAJE_CORREO+"("
            +MENSAJE_CORREO_CAMPO_ID+" TEXT,"
            +MENSAJE_CORREO_CAMPO_UID+" TEXT,"
            +MENSAJE_CORREO_CAMPO_ES_MIO+" INTEGER,"
            +MENSAJE_CORREO_CAMPO_ES_NUEVO+" INTEGER,"
            +MENSAJE_CORREO_CAMPO_ES_FAVORITO+" INTEGER,"
            +MENSAJE_CORREO_CAMPO_CORREO+" TEXT,"
            +MENSAJE_CORREO_CAMPO_REMITENTE+" TEXT,"
            +MENSAJE_CORREO_CAMPO_NOMBRE+" TEXT,"
            +MENSAJE_CORREO_CAMPO_DESTINATARIO+" TEXT,"
            +MENSAJE_CORREO_CAMPO_ASUNTO+" TEXT,"
            +MENSAJE_CORREO_CAMPO_TEXTO+" TEXT,"
            +MENSAJE_CORREO_CAMPO_ESTADO+" INTEGER,"
            +MENSAJE_CORREO_CAMPO_ES_RESPONDIDO+" INTEGER,"
            +MENSAJE_CORREO_CAMPO_PESO+" INTEGER,"
            +MENSAJE_CORREO_CAMPO_HORA+" TEXT,"
            +MENSAJE_CORREO_CAMPO_FECHA+" TEXT,"
            +MENSAJE_CORREO_CAMPO_ORDEN+" TEXT)";

    //v6///////////////////////////////////////////////ItemAdjuntoCorreo///////////////////////////////////////////////////
    public static final String TABLA_ADJUNTO_CORREO="adjunto_correo";

    public static final String ADJUNTO_CORREO_CAMPO_ID="id";
    public static final String ADJUNTO_CORREO_CAMPO_ID_MENSAJE="id_mensaje";
    public static final String ADJUNTO_CORREO_CAMPO_CORREO="correo";
    public static final String ADJUNTO_CORREO_CAMPO_POSICION="posicion";
    public static final String ADJUNTO_CORREO_CAMPO_NOMBRE="nombre";
    public static final String ADJUNTO_CORREO_CAMPO_TIPO="tipo";
    public static final String ADJUNTO_CORREO_CAMPO_PESO="peso";

    public static final String CREAR_TABLA_ADJUNTO_CORREO="CREATE TABLE IF NOT EXISTS "+TABLA_ADJUNTO_CORREO+"("
            +ADJUNTO_CORREO_CAMPO_ID+" TEXT,"
            +ADJUNTO_CORREO_CAMPO_ID_MENSAJE+" TEXT,"
            +ADJUNTO_CORREO_CAMPO_CORREO+" TEXT,"
            +ADJUNTO_CORREO_CAMPO_POSICION+" INTEGER,"
            +ADJUNTO_CORREO_CAMPO_NOMBRE+" TEXT,"
            +ADJUNTO_CORREO_CAMPO_TIPO+" INTEGER,"
            +ADJUNTO_CORREO_CAMPO_PESO+" INTEGER)";

    //v6///////////////////////////////////////////////DESCRIPCION_ERROR///////////////////////////////////////////////////
    public static final String TABLA_DESCRIPCION_ERROR="descripcion_error";

    public static final String DESCRIPCION_ERROR_CAMPO_MENSAJE_PUBLICO="mensaje_pub";
    public static final String DESCRIPCION_ERROR_CAMPO_MENSAJE_PRIVADO="mensaje_piv";

    public static final String CREAR_TABLA_DESCRIPCION_ERROR="CREATE TABLE IF NOT EXISTS "+TABLA_DESCRIPCION_ERROR+"("
            +DESCRIPCION_ERROR_CAMPO_MENSAJE_PUBLICO+" TEXT,"
            +DESCRIPCION_ERROR_CAMPO_MENSAJE_PRIVADO+" TEXT)";

    //v7///////////////////////////////////////////////ATAJOS///////////////////////////////////////////////////
    public static final String TABLA_ATAJOS="atajos";

    public static final String ATAJOS_CAMPO_COMANDO="comando";
    public static final String ATAJOS_CAMPO_DESCRIPCION="descripcion";

    public static final String CREAR_TABLA_ATAJOS="CREATE TABLE IF NOT EXISTS "+TABLA_ATAJOS+"("
            +ATAJOS_CAMPO_COMANDO+" TEXT,"
            +ATAJOS_CAMPO_DESCRIPCION+" TEXT)";

    //v7///////////////////////////////////////////////ItemComentarioPost///////////////////////////////////////////////////
    public static final String TABLA_COMENTARIO_POST="comentario_post";

    public static final String COMENTARIO_POST_CAMPO_ID="id";
    public static final String COMENTARIO_POST_CAMPO_ID_POST="id_post";
    public static final String COMENTARIO_POST_CAMPO_TIPO_COMENTARIO_POST="tipo_com_post";
    public static final String COMENTARIO_POST_CAMPO_NOMBRE="nombre";
    public static final String COMENTARIO_POST_CAMPO_CORREO="correo";
    public static final String COMENTARIO_POST_CAMPO_TIPO_USUARIO="tipo_usuario";
    public static final String COMENTARIO_POST_CAMPO_ICONO="icono";
    public static final String COMENTARIO_POST_CAMPO_TEXTO="texto";

    public static final String COMENTARIO_POST_CAMPO_RUTA_DATO="ruta_dato";
    public static final String COMENTARIO_POST_CAMPO_PESO_DATO="peso_dato";

    public static final String COMENTARIO_POST_CAMPO_HORA="hora";
    public static final String COMENTARIO_POST_CAMPO_FECHA="fecha";
    public static final String COMENTARIO_POST_CAMPO_ORDEN="orden";

    public static final String CREAR_TABLA_COMENTARIO_POST="CREATE TABLE IF NOT EXISTS "+TABLA_COMENTARIO_POST+"("
            +COMENTARIO_POST_CAMPO_ID+" TEXT,"
            +COMENTARIO_POST_CAMPO_ID_POST+" TEXT,"
            +COMENTARIO_POST_CAMPO_TIPO_COMENTARIO_POST+" INTEGER,"
            +COMENTARIO_POST_CAMPO_NOMBRE+" TEXT,"
            +COMENTARIO_POST_CAMPO_CORREO+" TEXT,"
            +COMENTARIO_POST_CAMPO_TIPO_USUARIO+" INTEGER,"
            +COMENTARIO_POST_CAMPO_ICONO+" INTEGER,"
            +COMENTARIO_POST_CAMPO_TEXTO+" TEXT,"
            +COMENTARIO_POST_CAMPO_RUTA_DATO+" TEXT,"
            +COMENTARIO_POST_CAMPO_PESO_DATO+" INTEGER,"
            +COMENTARIO_POST_CAMPO_HORA+" TEXT,"
            +COMENTARIO_POST_CAMPO_FECHA+" TEXT,"
            +COMENTARIO_POST_CAMPO_ORDEN+" TEXT)";

}
