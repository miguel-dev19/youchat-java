package cu.alexgi.youchat.base_datos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import cu.alexgi.youchat.YouChatApplication;

public class ConexionBD extends SQLiteOpenHelper {



    public ConexionBD(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
//        Log.e("*****ConexionBD*****","ConexionBD");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BDConstantes.CREAR_TABLA_VERSION_BD);
        db.execSQL(BDConstantes.CREAR_TABLA_USUARIO);
        db.execSQL(BDConstantes.CREAR_TABLA_CHAT);
        db.execSQL(BDConstantes.CREAR_TABLA_CONTACTO);
        db.execSQL(BDConstantes.CREAR_TABLA_ESTADO);
        db.execSQL(BDConstantes.CREAR_TABLA_SEGUIDOR);
        db.execSQL(BDConstantes.CREAR_TABLA_REACCION_ESTADO);

        db.execSQL(BDConstantes.CREAR_TABLA_VISTA_ESTADO);
        db.execSQL(BDConstantes.CREAR_TABLA_MI_PERFIL);

        db.execSQL(BDConstantes.CREAR_TABLA_SIGUIENDO_A);
        db.execSQL(BDConstantes.CREAR_TABLA_ESTADISTICA_PERSONAL);

        db.execSQL(BDConstantes.CREAR_TABLA_TEMAS);

        db.execSQL(BDConstantes.CREAR_TABLA_POST);
        db.execSQL(BDConstantes.CREAR_TABLA_LISTA_NEGRA_POST);

        db.execSQL(BDConstantes.CREAR_TABLA_DESCRIPCION_ERROR);

        db.execSQL(BDConstantes.CREAR_TABLA_USUARIO_CORREO);
        db.execSQL(BDConstantes.CREAR_TABLA_MENSAJE_CORREO);
        db.execSQL(BDConstantes.CREAR_TABLA_ADJUNTO_CORREO);

        db.execSQL(BDConstantes.CREAR_TABLA_ATAJOS);
        db.execSQL(BDConstantes.CREAR_TABLA_COMENTARIO_POST);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

//        db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_USUARIO_CORREO);
//        db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_MENSAJE_CORREO);
//        db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_ADJUNTO_CORREO);
//        db.execSQL(BDConstantes.CREAR_TABLA_USUARIO_CORREO);
//        db.execSQL(BDConstantes.CREAR_TABLA_MENSAJE_CORREO);
//        db.execSQL(BDConstantes.CREAR_TABLA_ADJUNTO_CORREO);

//        db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_POST);
//        db.execSQL(BDConstantes.CREAR_TABLA_POST);
//        db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_ESTADO);
//        db.execSQL(BDConstantes.CREAR_TABLA_ESTADO);

//        db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_ESTADO);
//        db.execSQL(BDConstantes.CREAR_TABLA_ESTADO);

        if(oldVersion==2){ //para la 4
            UpgradeTablaContactoV1ToV3(db);

            db.execSQL(BDConstantes.CREAR_TABLA_ESTADO);
            db.execSQL(BDConstantes.CREAR_TABLA_SEGUIDOR);
            db.execSQL(BDConstantes.CREAR_TABLA_REACCION_ESTADO);
            oldVersion=4;
            YouChatApplication.setVersion_bd(4);
        }
        if(oldVersion==3){ //para la 4
            db.execSQL(BDConstantes.CREAR_TABLA_REACCION_ESTADO);
            oldVersion=4;
            YouChatApplication.setVersion_bd(4);
        }
        if(oldVersion == 4){ //para la 5
            UpgradeTablaChatV1ToV5(db);
            UpgradeTablaEstadoV4ToV5(db);

            db.execSQL(BDConstantes.CREAR_TABLA_VISTA_ESTADO);
            db.execSQL(BDConstantes.CREAR_TABLA_MI_PERFIL);
            db.execSQL(BDConstantes.CREAR_TABLA_POST);
            db.execSQL(BDConstantes.CREAR_TABLA_LISTA_NEGRA_POST);
            db.execSQL(BDConstantes.CREAR_TABLA_SIGUIENDO_A);
            db.execSQL(BDConstantes.CREAR_TABLA_ESTADISTICA_PERSONAL);
            db.execSQL(BDConstantes.CREAR_TABLA_TEMAS);

            oldVersion=5;
            YouChatApplication.setVersion_bd(5);
        }
        if(oldVersion == 5){ //para la 6
            db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_POST);
            db.execSQL(BDConstantes.CREAR_TABLA_POST);
            db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_ESTADO);
            db.execSQL(BDConstantes.CREAR_TABLA_ESTADO);

            db.execSQL(BDConstantes.CREAR_TABLA_DESCRIPCION_ERROR);
            db.execSQL(BDConstantes.CREAR_TABLA_USUARIO_CORREO);
            db.execSQL(BDConstantes.CREAR_TABLA_MENSAJE_CORREO);
            db.execSQL(BDConstantes.CREAR_TABLA_ADJUNTO_CORREO);
            oldVersion=6;
            YouChatApplication.setVersion_bd(6);
        }
        if(oldVersion == 6){ //para la 7
            db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_POST);
            db.execSQL(BDConstantes.CREAR_TABLA_POST);
            db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_ESTADO);
            db.execSQL(BDConstantes.CREAR_TABLA_ESTADO);

            db.execSQL(BDConstantes.CREAR_TABLA_ATAJOS);
            db.execSQL(BDConstantes.CREAR_TABLA_VERSION_BD);

            oldVersion=7;
            YouChatApplication.setVersion_bd(7);

            db.execSQL("ALTER TABLE "+BDConstantes.TABLA_TEMAS+" ADD COLUMN "
                    +BDConstantes.TEMAS_CAMPO_COLOR_STATUS_BAR+" TEXT");

            db.execSQL("ALTER TABLE "+BDConstantes.TABLA_CHAT+" ADD COLUMN "
                    +BDConstantes.CHAT_CAMPO_ID_MENSAJE+" TEXT");
            db.execSQL("ALTER TABLE "+BDConstantes.TABLA_CHAT+" ADD COLUMN "
                    +BDConstantes.CHAT_CAMPO_PESO+" INTEGER");
            db.execSQL("ALTER TABLE "+BDConstantes.TABLA_CHAT+" ADD COLUMN "
                    +BDConstantes.CHAT_CAMPO_ESTA_DESCARGADO+" INTEGER");
        }
        if(oldVersion==7){//para la 8
            db.execSQL(BDConstantes.CREAR_TABLA_COMENTARIO_POST);
            oldVersion=8;
            YouChatApplication.setVersion_bd(8);
        }
        else if(oldVersion!=newVersion){
            YouChatApplication.setVersion_bd(newVersion);
            db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_VERSION_BD);
            db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_CONTACTO);
            db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_USUARIO);
            db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_CHAT);
            db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_ESTADO);
            db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_SEGUIDOR);
            db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_REACCION_ESTADO);
            db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_VISTA_ESTADO);
            db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_MI_PERFIL);
            db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_POST);
            db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_LISTA_NEGRA_POST);
            db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_SIGUIENDO_A);
            db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_ESTADISTICA_PERSONAL);

            db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_TEMAS);
            db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_DESCRIPCION_ERROR);

            db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_USUARIO_CORREO);
            db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_MENSAJE_CORREO);
            db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_ADJUNTO_CORREO);
            db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_ATAJOS);
            db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_COMENTARIO_POST);
            onCreate(db);
        }
    }

    private void UpgradeTablaChatV1ToV5(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE new_chats ("
                +BDConstantes.CHAT_CAMPO_ID+" TEXT,"
                +BDConstantes.CHAT_CAMPO_TIPO_MENSAJE+" INTEGER,"
                +BDConstantes.CHAT_CAMPO_ESTADO+" INTEGER,"
                +BDConstantes.CHAT_CAMPO_CORREO+" TEXT,"
                +BDConstantes.CHAT_CAMPO_MENSAJE+" TEXT,"
                +BDConstantes.CHAT_CAMPO_RUTA_DATO+" TEXT,"
                +BDConstantes.CHAT_CAMPO_HORA+" TEXT,"
                +BDConstantes.CHAT_CAMPO_FECHA+" TEXT,"
                +BDConstantes.CHAT_CAMPO_ID_MSG_RESP+" TEXT,"
                +BDConstantes.CHAT_CAMPO_EMISOR+" TEXT,"
                +BDConstantes.CHAT_CAMPO_REENVIADO+" INTEGER,"
                +BDConstantes.CHAT_CAMPO_ORDEN+" TEXT,"
                //bd v5
                +BDConstantes.CHAT_CAMPO_EDITADO+" INTEGER)");
        db.execSQL("INSERT INTO new_chats" +
                " ("+BDConstantes.SELECT_ALL_DATA_TABLA_CHAT_V4 +") "+
                "SELECT " +
                BDConstantes.SELECT_ALL_DATA_TABLA_CHAT_V4 +
                " FROM "+BDConstantes.TABLA_CHAT);
        db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_CHAT);
        db.execSQL("ALTER TABLE new_chats RENAME TO "+BDConstantes.TABLA_CHAT);

//        db.execSQL("ALTER TABLE "+BDConstantes.TABLA_CHAT+" ADD COLUMN "
//                +BDConstantes.CHAT_CAMPO_EDITADO+" INTEGER");
    }

    private void UpgradeTablaEstadoV4ToV5(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE new_estados ("
                +BDConstantes.ESTADO_CAMPO_ID+" TEXT,"
                +BDConstantes.ESTADO_CAMPO_CORREO+" TEXT,"
                +BDConstantes.ESTADO_CAMPO_TIPO+" INTEGER,"
                +BDConstantes.ESTADO_CAMPO_ESTA_VISTO+" INTEGER,"
                +BDConstantes.ESTADO_CAMPO_RUTA_IMAGEN+" TEXT,"
                +BDConstantes.ESTADO_CAMPO_TEXTO+" TEXT,"
                +BDConstantes.ESTADO_CAMPO_CANTIDAD_ME_GUSTA+" INTEGER,"
                +BDConstantes.ESTADO_CAMPO_CANTIDAD_ME_ENCANTA+" INTEGER,"
                +BDConstantes.ESTADO_CAMPO_CANTIDAD_ME_SONROJA+" INTEGER,"
                +BDConstantes.ESTADO_CAMPO_CANTIDAD_ME_DIVIERTE+" INTEGER,"
                +BDConstantes.ESTADO_CAMPO_CANTIDAD_ME_ASOMBRA+" INTEGER,"
                +BDConstantes.ESTADO_CAMPO_CANTIDAD_ME_ENTRISTESE+" INTEGER,"
                +BDConstantes.ESTADO_CAMPO_CANTIDAD_ME_ENOJA+" INTEGER,"
                +BDConstantes.ESTADO_CAMPO_HORA+" TEXT,"
                +BDConstantes.ESTADO_CAMPO_FECHA+" TEXT,"
                +BDConstantes.ESTADO_CAMPO_ORDEN+" TEXT,"
                //bd v5
                +BDConstantes.ESTADO_CAMPO_ESTILO_TEXTO+" INTEGER)");
        db.execSQL("INSERT INTO new_estados"+
                " ("+BDConstantes.SELECT_ALL_DATA_TABLA_ESTADO_V4 +") "+
                "SELECT " +
                BDConstantes.SELECT_ALL_DATA_TABLA_ESTADO_V4 +
                " FROM "+BDConstantes.TABLA_ESTADO);
        db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_ESTADO);
        db.execSQL("ALTER TABLE new_estados RENAME TO "+BDConstantes.TABLA_ESTADO);
    }

    private void UpgradeTablaContactoV1ToV3(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE new_contactos ("
                +BDConstantes.CONTACTO_CAMPO_ALIAS+" TEXT,"
                +BDConstantes.CONTACTO_CAMPO_NOMBRE_PERSONAL+" TEXT,"
                +BDConstantes.CONTACTO_CAMPO_CORREO+" TEXT,"
                +BDConstantes.CONTACTO_CAMPO_TIPO+" INTEGER,"
                +BDConstantes.CONTACTO_CAMPO_VERSION+" INTEGER,"
                +BDConstantes.CONTACTO_CAMPO_RUTA_IMG+" TEXT,"
                +BDConstantes.CONTACTO_CAMPO_INFO+" TEXT,"
                +BDConstantes.CONTACTO_CAMPO_TELEFONO+" TEXT,"
                +BDConstantes.CONTACTO_CAMPO_GENERO+" TEXT,"
                +BDConstantes.CONTACTO_CAMPO_PROVINCIA+" TEXT,"
                +BDConstantes.CONTACTO_CAMPO_FECHA_NACIMIENTO+" TEXT,"
                +BDConstantes.CONTACTO_CAMPO_HORA_ULT_CONEXION+" TEXT,"
                +BDConstantes.CONTACTO_CAMPO_FECHA_ULT_CONEXION+" TEXT,"
                +BDConstantes.CONTACTO_CAMPO_USA_YOUCHAT+" INTEGER,"
                +BDConstantes.CONTACTO_CAMPO_SILENCIADO+" INTEGER,"
                +BDConstantes.CONTACTO_CAMPO_BLOQUEADO+" INTEGER,"
                +BDConstantes.CONTACTO_CAMPO_NOMBRE_ORDENAR+" TEXT,"
                //bd v3
                +BDConstantes.CONTACTO_CAMPO_CANT_SEGUIDORES+" INTEGER)");
        db.execSQL("INSERT INTO new_contactos"+
                " ("+BDConstantes.SELECT_ALL_DATA_TABLE_CONTACTO_V2 +") "+
                "SELECT " +
                BDConstantes.SELECT_ALL_DATA_TABLE_CONTACTO_V2 +
                " FROM "+BDConstantes.TABLA_CONTACTO);
        db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_CONTACTO);
        db.execSQL("ALTER TABLE new_contactos RENAME TO "+BDConstantes.TABLA_CONTACTO);
    }
}
