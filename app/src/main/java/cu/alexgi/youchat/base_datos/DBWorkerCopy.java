package cu.alexgi.youchat.base_datos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import cu.alexgi.youchat.Convertidor;
import cu.alexgi.youchat.YouChatApplication;
import cu.alexgi.youchat.items.ItemAdjuntoCorreo;
import cu.alexgi.youchat.items.ItemChat;
import cu.alexgi.youchat.items.ItemContacto;
import cu.alexgi.youchat.items.ItemEstadisticaPersonal;
import cu.alexgi.youchat.items.ItemEstado;
import cu.alexgi.youchat.items.ItemMensajeCorreo;
import cu.alexgi.youchat.items.ItemPost;
import cu.alexgi.youchat.items.ItemReaccionEstado;
import cu.alexgi.youchat.items.ItemTemas;
import cu.alexgi.youchat.items.ItemUsuario;
import cu.alexgi.youchat.items.ItemUsuarioCorreo;
import cu.alexgi.youchat.items.ItemVistaEstado;

public class DBWorkerCopy {

    private ConexionBD conexionBD;
    private final int nuevaVersion=5;

    public DBWorkerCopy(Context context){
        if(YouChatApplication.version_bd==nuevaVersion)
            conexionBD=new ConexionBD(context,BDConstantes.NOMBRE_BASE_DATOS,null, nuevaVersion);
        else {
            conexionBD=new ConexionBD(context,BDConstantes.NOMBRE_BASE_DATOS,null, nuevaVersion);
            conexionBD.onUpgrade(conexionBD.getWritableDatabase(),YouChatApplication.version_bd,nuevaVersion);
        }
    }

    //////////////////////////////////////////CONTACTO////////////////////////////////////
    public synchronized void insertarNuevoContacto(ItemContacto contacto){
        if(!existeContacto(contacto.getCorreo(), true)){
            SQLiteDatabase db = conexionBD.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(BDConstantes.CONTACTO_CAMPO_ALIAS, contacto.getAlias());
            values.put(BDConstantes.CONTACTO_CAMPO_NOMBRE_PERSONAL, contacto.getNombre_personal());
            values.put(BDConstantes.CONTACTO_CAMPO_CORREO, contacto.getCorreo());
            values.put(BDConstantes.CONTACTO_CAMPO_TIPO, contacto.getTipo_contacto());
            values.put(BDConstantes.CONTACTO_CAMPO_VERSION, contacto.getVersion());
            values.put(BDConstantes.CONTACTO_CAMPO_RUTA_IMG, contacto.getRuta_img());
            values.put(BDConstantes.CONTACTO_CAMPO_INFO, contacto.getInfo());
            values.put(BDConstantes.CONTACTO_CAMPO_TELEFONO, contacto.getTelefono());
            values.put(BDConstantes.CONTACTO_CAMPO_GENERO, contacto.getGenero());
            values.put(BDConstantes.CONTACTO_CAMPO_PROVINCIA, contacto.getProvincia());
            values.put(BDConstantes.CONTACTO_CAMPO_FECHA_NACIMIENTO, contacto.getFecha_nac());

            values.put(BDConstantes.CONTACTO_CAMPO_HORA_ULT_CONEXION, contacto.getUlt_hora_conex());
            values.put(BDConstantes.CONTACTO_CAMPO_FECHA_ULT_CONEXION, contacto.getUlt_fecha_conex());

            values.put(BDConstantes.CONTACTO_CAMPO_USA_YOUCHAT, contacto.getUsaYouchat());

            values.put(BDConstantes.CONTACTO_CAMPO_SILENCIADO, contacto.getSilenciado());
            values.put(BDConstantes.CONTACTO_CAMPO_BLOQUEADO, contacto.getBloqueado());

            String nombreOrden = contacto.getNombre_personal().toLowerCase();
            if(nombreOrden.trim().equals("")) nombreOrden = contacto.getAlias().toLowerCase();
            values.put(BDConstantes.CONTACTO_CAMPO_NOMBRE_ORDENAR, nombreOrden);

            //bd v3
            values.put(BDConstantes.CONTACTO_CAMPO_CANT_SEGUIDORES, contacto.getCant_seguidores());

            db.insert(BDConstantes.TABLA_CONTACTO, BDConstantes.CONTACTO_CAMPO_CORREO, values);
            db.close();
        }
        else modificarContacto(contacto);
    }

    public synchronized void insertarNuevoContactoNoVisible(ItemContacto contacto, boolean comoDeYC){
        if(!existeContacto(contacto.getCorreo(), true)){
            SQLiteDatabase db = conexionBD.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(BDConstantes.CONTACTO_CAMPO_ALIAS, contacto.getAlias());
            values.put(BDConstantes.CONTACTO_CAMPO_NOMBRE_PERSONAL, contacto.getNombre_personal());
            values.put(BDConstantes.CONTACTO_CAMPO_CORREO, contacto.getCorreo());
            values.put(BDConstantes.CONTACTO_CAMPO_TIPO, ItemContacto.TIPO_CONTACTO_INVISIBLE);
            values.put(BDConstantes.CONTACTO_CAMPO_VERSION, contacto.getVersion());
            values.put(BDConstantes.CONTACTO_CAMPO_RUTA_IMG, contacto.getRuta_img());
            values.put(BDConstantes.CONTACTO_CAMPO_INFO, contacto.getInfo());
            values.put(BDConstantes.CONTACTO_CAMPO_TELEFONO, contacto.getTelefono());
            values.put(BDConstantes.CONTACTO_CAMPO_GENERO, contacto.getGenero());
            values.put(BDConstantes.CONTACTO_CAMPO_PROVINCIA, contacto.getProvincia());
            values.put(BDConstantes.CONTACTO_CAMPO_FECHA_NACIMIENTO, contacto.getFecha_nac());
            values.put(BDConstantes.CONTACTO_CAMPO_HORA_ULT_CONEXION, contacto.getUlt_hora_conex());
            values.put(BDConstantes.CONTACTO_CAMPO_FECHA_ULT_CONEXION, contacto.getUlt_fecha_conex());

            values.put(BDConstantes.CONTACTO_CAMPO_USA_YOUCHAT, contacto.getUsaYouchat());

            values.put(BDConstantes.CONTACTO_CAMPO_SILENCIADO, contacto.getSilenciado());
            values.put(BDConstantes.CONTACTO_CAMPO_BLOQUEADO, contacto.getBloqueado());

            String nombreOrden = contacto.getNombre_personal().toLowerCase();
            if(nombreOrden.trim().equals("")) nombreOrden = contacto.getAlias().toLowerCase();
            values.put(BDConstantes.CONTACTO_CAMPO_NOMBRE_ORDENAR, nombreOrden);

            //bd v3
            values.put(BDConstantes.CONTACTO_CAMPO_CANT_SEGUIDORES, contacto.getCant_seguidores());

            db.insert(BDConstantes.TABLA_CONTACTO, BDConstantes.CONTACTO_CAMPO_CORREO, values);
            db.close();
        } else if(comoDeYC)
            modificarUsoYCContacto(contacto.getCorreo(), 1);
    }

    public synchronized void modificarTipoContacto(String correo, int tipo){
        SQLiteDatabase db=conexionBD.getWritableDatabase();
        String[] parametros={correo};
        ContentValues values=new ContentValues();
        values.put(BDConstantes.CONTACTO_CAMPO_TIPO,tipo);

        db.update(BDConstantes.TABLA_CONTACTO,values,BDConstantes.CONTACTO_CAMPO_CORREO+"=?",parametros);
        db.close();
    }

    public synchronized void modificarUsoYCContacto(String correo, int uso){
        SQLiteDatabase db=conexionBD.getWritableDatabase();
        String[] parametros={correo};
        ContentValues values=new ContentValues();
        values.put(BDConstantes.CONTACTO_CAMPO_USA_YOUCHAT,uso);

        db.update(BDConstantes.TABLA_CONTACTO,values,BDConstantes.CONTACTO_CAMPO_CORREO+"=?",parametros);
        db.close();
    }

    public synchronized void modificarContacto(ItemContacto contacto){
        SQLiteDatabase db=conexionBD.getWritableDatabase();
        String[] parametros={contacto.getCorreo()};
        ContentValues values=new ContentValues();

        values.put(BDConstantes.CONTACTO_CAMPO_TIPO,ItemContacto.TIPO_CONTACTO);

        String nombreOrden = "";

        if(!contacto.getAlias().trim().equals("") && !contacto.getAlias().equals(contacto.getCorreo())){
            values.put(BDConstantes.CONTACTO_CAMPO_ALIAS,contacto.getAlias());
            nombreOrden = contacto.getAlias().toLowerCase();
        }

        if(!contacto.getNombre_personal().trim().equals("") && !contacto.getNombre_personal().equals(contacto.getCorreo())){
            values.put(BDConstantes.CONTACTO_CAMPO_NOMBRE_PERSONAL,contacto.getNombre_personal());
            nombreOrden = contacto.getNombre_personal().toLowerCase();
        }

        //values.put(BDConstantes.CONTACTO_CAMPO_USA_YOUCHAT,contacto.getUsaYouchat());

        if(!nombreOrden.trim().equals(""))
            values.put(BDConstantes.CONTACTO_CAMPO_NOMBRE_ORDENAR, nombreOrden);

        db.update(BDConstantes.TABLA_CONTACTO,values,BDConstantes.CONTACTO_CAMPO_CORREO+"=?",parametros);
        db.close();
    }

    public synchronized boolean actualizarContacto(ItemContacto contacto){
        if(existeContacto(contacto.getCorreo(), true)){
            SQLiteDatabase db = conexionBD.getWritableDatabase();
            String [] parametros={contacto.getCorreo()};
            ContentValues values = new ContentValues();
            values.put(BDConstantes.CONTACTO_CAMPO_ALIAS, contacto.getAlias());
            values.put(BDConstantes.CONTACTO_CAMPO_NOMBRE_PERSONAL, contacto.getNombre_personal());
            values.put(BDConstantes.CONTACTO_CAMPO_CORREO, contacto.getCorreo());
            values.put(BDConstantes.CONTACTO_CAMPO_TIPO, contacto.getTipo_contacto());
            values.put(BDConstantes.CONTACTO_CAMPO_VERSION, contacto.getVersion());
            values.put(BDConstantes.CONTACTO_CAMPO_RUTA_IMG, contacto.getRuta_img());
            values.put(BDConstantes.CONTACTO_CAMPO_INFO, contacto.getInfo());
            values.put(BDConstantes.CONTACTO_CAMPO_TELEFONO, contacto.getTelefono());
            values.put(BDConstantes.CONTACTO_CAMPO_GENERO, contacto.getGenero());
            values.put(BDConstantes.CONTACTO_CAMPO_PROVINCIA, contacto.getProvincia());
            values.put(BDConstantes.CONTACTO_CAMPO_FECHA_NACIMIENTO, contacto.getFecha_nac());
            values.put(BDConstantes.CONTACTO_CAMPO_HORA_ULT_CONEXION, contacto.getUlt_hora_conex());
            values.put(BDConstantes.CONTACTO_CAMPO_FECHA_ULT_CONEXION, contacto.getUlt_fecha_conex());

            values.put(BDConstantes.CONTACTO_CAMPO_USA_YOUCHAT, contacto.getUsaYouchat());

            values.put(BDConstantes.CONTACTO_CAMPO_SILENCIADO, contacto.getSilenciado());
            values.put(BDConstantes.CONTACTO_CAMPO_BLOQUEADO, contacto.getBloqueado());

            String nombreOrden = contacto.getNombre_personal().toLowerCase();
            if(nombreOrden.trim().equals("")) nombreOrden = contacto.getAlias().toLowerCase();
            values.put(BDConstantes.CONTACTO_CAMPO_NOMBRE_ORDENAR, nombreOrden);

            //bd v3
            values.put(BDConstantes.CONTACTO_CAMPO_CANT_SEGUIDORES, contacto.getCant_seguidores());

            db.update(BDConstantes.TABLA_CONTACTO,values,BDConstantes.CONTACTO_CAMPO_CORREO+"=?",parametros);
            db.close();
            return true;
        }
        else insertarNuevoContacto(contacto);
        return false;
    }

    public synchronized void actualizarUltHoraFechaDe(String contacto_correo, String ultHora, String ultFecha) {
        if(existeContacto(contacto_correo, true)){
            SQLiteDatabase db=conexionBD.getWritableDatabase();

            String [] parametros={contacto_correo};
            ContentValues values=new ContentValues();
            if(!ultHora.equals(""))
                values.put(BDConstantes.CONTACTO_CAMPO_HORA_ULT_CONEXION,ultHora);
            if(!ultFecha.equals(""))
                values.put(BDConstantes.CONTACTO_CAMPO_FECHA_ULT_CONEXION,ultFecha);

            if(values.size()>0)
                db.update(BDConstantes.TABLA_CONTACTO,values,BDConstantes.CONTACTO_CAMPO_CORREO+"=?",parametros);
            db.close();
        }
        else {
            ItemContacto contacto=new ItemContacto(contacto_correo,contacto_correo);
            contacto.setUlt_hora_conex(ultHora);
            contacto.setUlt_fecha_conex(ultFecha);
            contacto.setUsaYouchat(true);
            insertarNuevoContactoNoVisible(contacto, false);
        }
    }

    public synchronized void actualizarUltHoraFechaSinInsertarContactoDe(String contacto_correo, String ultHora, String ultFecha) {
        if(existeContacto(contacto_correo, false)){
            SQLiteDatabase db=conexionBD.getWritableDatabase();

            String [] parametros={contacto_correo};
            ContentValues values=new ContentValues();
            if(!ultHora.equals(""))
                values.put(BDConstantes.CONTACTO_CAMPO_HORA_ULT_CONEXION,ultHora);
            if(!ultFecha.equals(""))
                values.put(BDConstantes.CONTACTO_CAMPO_FECHA_ULT_CONEXION,ultFecha);

            if(values.size()>0)
                db.update(BDConstantes.TABLA_CONTACTO,values,BDConstantes.CONTACTO_CAMPO_CORREO+"=?",parametros);
            db.close();
        }
    }

    public synchronized void actualizarCantSeguidoresDe(String contacto_correo, int cant_seguidores) {
        if(existeContacto(contacto_correo, true)){
            SQLiteDatabase db=conexionBD.getWritableDatabase();
            String [] parametros={contacto_correo};
            ContentValues values=new ContentValues();
            values.put(BDConstantes.CONTACTO_CAMPO_CANT_SEGUIDORES,cant_seguidores);

            db.update(BDConstantes.TABLA_CONTACTO,values,BDConstantes.CONTACTO_CAMPO_CORREO+"=?",parametros);
            db.close();
        }
    }

    public synchronized void actualizarNombrePersonalDe(String contacto_correo, String nombre)
    {
        if(nombre.equals(""))
            nombre=contacto_correo;
        if(existeContacto(contacto_correo, true)){
            SQLiteDatabase db=conexionBD.getWritableDatabase();

            String [] parametros={contacto_correo};
            ContentValues values=new ContentValues();
            values.put(BDConstantes.CONTACTO_CAMPO_NOMBRE_PERSONAL,nombre);
            values.put(BDConstantes.CONTACTO_CAMPO_NOMBRE_ORDENAR,nombre.toLowerCase());

            db.update(BDConstantes.TABLA_CONTACTO,values,BDConstantes.CONTACTO_CAMPO_CORREO+"=?",parametros);
            db.close();
        }
        else {
            ItemContacto contacto=new ItemContacto("",contacto_correo);
            contacto.setNombre_personal(nombre);
            insertarNuevoContacto(contacto);
        }
    }

    public synchronized void actualizarSilenciadoDe(String contacto_correo, boolean silenciado)
    {
        if(existeContacto(contacto_correo, true)){
            SQLiteDatabase db=conexionBD.getWritableDatabase();

            String [] parametros={contacto_correo};
            ContentValues values=new ContentValues();
            values.put(BDConstantes.CONTACTO_CAMPO_SILENCIADO,silenciado?1:0);

            db.update(BDConstantes.TABLA_CONTACTO,values,BDConstantes.CONTACTO_CAMPO_CORREO+"=?",parametros);
            db.close();
        }
        else {
            ItemContacto contacto=new ItemContacto(contacto_correo,contacto_correo);
            contacto.setSilenciado(silenciado);
            insertarNuevoContacto(contacto);
        }
    }

    public synchronized void actualizarBloqueadoDe(String contacto_correo, boolean bloqueado)
    {
        if(existeContacto(contacto_correo, true)){
            SQLiteDatabase db=conexionBD.getWritableDatabase();

            String [] parametros={contacto_correo};
            ContentValues values=new ContentValues();
            values.put(BDConstantes.CONTACTO_CAMPO_BLOQUEADO,bloqueado?1:0);

            db.update(BDConstantes.TABLA_CONTACTO,values,BDConstantes.CONTACTO_CAMPO_CORREO+"=?",parametros);
            db.close();
        }
        else {
            ItemContacto contacto=new ItemContacto(contacto_correo,contacto_correo);
            contacto.setBloqueado(bloqueado);
            insertarNuevoContacto(contacto);
        }
    }

    public boolean existeContacto(String contacto_correo, boolean defecto){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={contacto_correo};
        String [] campos={BDConstantes.CONTACTO_CAMPO_NOMBRE_PERSONAL};

        Cursor cursor;
        boolean exist=false;
        try {
            cursor=db.query(BDConstantes.TABLA_CONTACTO,campos,
                    BDConstantes.CONTACTO_CAMPO_CORREO+"=?",parametros,null,null,null);
            if(cursor.getCount()>0) exist=cursor.moveToFirst();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
            exist=defecto;
        }
        db.close();
        return exist;
    }

    public ArrayList<ItemContacto> obtenerContactosOrdenadosXNombre(boolean ordenarXNombre){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        ArrayList<ItemContacto> datos_Contacto = new ArrayList<>();
        Cursor cursor;
        try{
            if(ordenarXNombre){
                cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_CONTACTO+
                        " WHERE "+BDConstantes.CONTACTO_CAMPO_TIPO+" ='1' ORDER BY "+BDConstantes.CONTACTO_CAMPO_NOMBRE_ORDENAR+" ASC", null);
            }
            else {
                cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_CONTACTO+
                        " WHERE "+BDConstantes.CONTACTO_CAMPO_TIPO+" ='1' ORDER BY "+BDConstantes.CONTACTO_CAMPO_CORREO+" ASC", null);
            }
            if(cursor.getCount()==0){
                cursor.close();
                db.close();
                return datos_Contacto;
            }
            while (cursor.moveToNext()){
                ItemContacto temp = Convertidor.createItemContactoOfCursor(cursor);
                datos_Contacto.add(temp);
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return datos_Contacto;
    }

    public ArrayList<ItemContacto> obtenerContactosBloqueados(){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        ArrayList<ItemContacto> datos_Contacto = new ArrayList<>();
        Cursor cursor;
        try{
                cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_CONTACTO+
                        " WHERE "+BDConstantes.CONTACTO_CAMPO_BLOQUEADO+" ='1' ORDER BY "+BDConstantes.CONTACTO_CAMPO_CORREO+" ASC", null);
            if(cursor.getCount()==0){
                cursor.close();
                db.close();
                return datos_Contacto;
            }
            while (cursor.moveToNext()){
                ItemContacto temp = Convertidor.createItemContactoOfCursor(cursor);
                datos_Contacto.add(temp);
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return datos_Contacto;
    }

    public ItemContacto obtenerContacto(String correo){
        ItemContacto contacto = null;
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={correo};
        String [] campos={"*"};

        Cursor cursor;
        try {
//            cursor=db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_CONTACTO+
//                    " WHERE "+BDConstantes.CONTACTO_CAMPO_NOMBRE_ORDENAR+" ASC", null);
            cursor=db.query(BDConstantes.TABLA_CONTACTO,campos,
                    BDConstantes.CONTACTO_CAMPO_CORREO+"=?",parametros,null,null,null);
            if(cursor.getCount()>0){
                cursor.moveToFirst();
                contacto=Convertidor.createItemContactoOfCursor(cursor);
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return contacto;
    }

    public String[] obtenerUltHorayFecha(String contacto_correo){
        String[] ultHoraFecha = new String[3];
        ultHoraFecha[0]=ultHoraFecha[1]="";
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={contacto_correo};
        String [] campos={BDConstantes.CONTACTO_CAMPO_HORA_ULT_CONEXION,
                BDConstantes.CONTACTO_CAMPO_FECHA_ULT_CONEXION};

        Cursor cursor;
        try {
            cursor=db.query(BDConstantes.TABLA_CONTACTO,campos,
                    BDConstantes.CONTACTO_CAMPO_CORREO+"=?",parametros,null,null,null);
            if(cursor.getCount()>0){
                cursor.moveToFirst();
                ultHoraFecha[0]=cursor.getString(0);
                ultHoraFecha[1]=cursor.getString(1);
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return ultHoraFecha;
    }

    public String obtenerNombre(String contacto_correo){
        if(contacto_correo.equals(YouChatApplication.idOficial))
            return "YouChat Oficial";
        String nombre="";
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={contacto_correo};
        String [] campos={BDConstantes.CONTACTO_CAMPO_NOMBRE_PERSONAL, BDConstantes.CONTACTO_CAMPO_ALIAS};

        Cursor cursor;
        try {
            cursor=db.query(BDConstantes.TABLA_CONTACTO,campos,
                    BDConstantes.CONTACTO_CAMPO_CORREO+"=? LIMIT 1",parametros,null,null,null);
            if(cursor.getCount()>0){
                cursor.moveToFirst();
                nombre=cursor.getString(0);
                String alias = cursor.getString(1);
                if(nombre.equals("") || nombre.equals(contacto_correo))
                    if(!alias.equals(""))
                        nombre = alias;
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        if(nombre.equals(""))
            nombre=contacto_correo;
        return nombre;
    }

    public int obtenerTipoContacto(String contacto_correo){
        int tipo=ItemContacto.TIPO_CONTACTO_INVISIBLE;
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={contacto_correo};
        String [] campos={BDConstantes.CONTACTO_CAMPO_TIPO};

        Cursor cursor;
        try {
            cursor=db.query(BDConstantes.TABLA_CONTACTO,campos,
                    BDConstantes.CONTACTO_CAMPO_CORREO+"=?",parametros,null,null,null);
            if(cursor.getCount()>0){
                cursor.moveToFirst();
                tipo=cursor.getInt(0);
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return tipo;
    }

    public int obtenerVersionContacto(String contacto_correo){
        int version=0;
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={contacto_correo};
        String [] campos={BDConstantes.CONTACTO_CAMPO_VERSION};

        Cursor cursor;
        try {
            cursor=db.query(BDConstantes.TABLA_CONTACTO,campos,
                    BDConstantes.CONTACTO_CAMPO_CORREO+"=?",parametros,null,null,null);
            if(cursor.getCount()>0){
                cursor.moveToFirst();
                version = cursor.getInt(0);
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return version;
    }

    public boolean estaSilenciado(String contacto_correo){
        boolean silenciado=false;
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={contacto_correo};
        String [] campos={BDConstantes.CONTACTO_CAMPO_SILENCIADO};

        Cursor cursor;
        try {
            cursor=db.query(BDConstantes.TABLA_CONTACTO,campos,
                    BDConstantes.CONTACTO_CAMPO_CORREO+"=?",parametros,null,null,null);
            if(cursor.getCount()>0){
                cursor.moveToFirst();
                silenciado = cursor.getInt(0)==1;
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return silenciado;
    }

    public boolean estaBloqueado(String contacto_correo){
        boolean bloqueado=false;
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={contacto_correo};
        String [] campos={BDConstantes.CONTACTO_CAMPO_BLOQUEADO};

        Cursor cursor;
        try {
            cursor=db.query(BDConstantes.TABLA_CONTACTO,campos,
                    BDConstantes.CONTACTO_CAMPO_CORREO+"=?",parametros,null,null,null);
            if(cursor.getCount()>0){
                cursor.moveToFirst();
                bloqueado = cursor.getInt(0)==1;
            }
            cursor.close();
        }catch (Exception e){

            e.printStackTrace();
        }
        db.close();
        return bloqueado;
    }

    public String obtenerRutaImg(String contacto_correo){
        String ruta="";
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={contacto_correo};
        String [] campos={BDConstantes.CONTACTO_CAMPO_RUTA_IMG};

        Cursor cursor;
        try {
            cursor=db.query(BDConstantes.TABLA_CONTACTO,campos,
                    BDConstantes.CONTACTO_CAMPO_CORREO+"=?",parametros,null,null,null);
            if(cursor.getCount()>0){
                cursor.moveToFirst();
                ruta=cursor.getString(0);
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return ruta;
    }

    public synchronized void eliminarContacto(String contacto_correo){
        SQLiteDatabase db=conexionBD.getWritableDatabase();
        String [] parametros={contacto_correo};
        db.delete(BDConstantes.TABLA_CONTACTO,BDConstantes.CONTACTO_CAMPO_CORREO+"=?",parametros);
        db.delete(BDConstantes.TABLA_USUARIO,BDConstantes.USUARIO_CAMPO_CORREO+"=?",parametros);
        db.delete(BDConstantes.TABLA_CHAT,BDConstantes.CHAT_CAMPO_CORREO+"=?",parametros);
        db.delete(BDConstantes.TABLA_SEGUIDOR,BDConstantes.SEGUIDOR_CAMPO_ID+"=?",parametros);
        db.delete(BDConstantes.TABLA_ESTADO,BDConstantes.ESTADO_CAMPO_CORREO+"=?",parametros);
        db.delete(BDConstantes.TABLA_REACCION_ESTADO,BDConstantes.REACCION_CAMPO_CORREO+"=?",parametros);
        db.close();
    }

    //////////////////////////////////////////USUARIO////////////////////////////////////
    public ArrayList<ItemUsuario> obtenerUsuarios(){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        ArrayList<ItemUsuario> datos_Usuario= new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_USUARIO+" ORDER BY "+
                    BDConstantes.USUARIO_CAMPO_ORDEN_ULT_MSG+" DESC", null);
            if(cursor.getCount()==0){
                cursor.close();
                db.close();
                return datos_Usuario;
            }
            while (cursor.moveToNext()) datos_Usuario.add(Convertidor.createItemUsuarioOfCursor(cursor));
            cursor.close();
            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return datos_Usuario;
    }

    public ItemUsuario obtenerUsuario(String correo){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={correo};
        String [] campos={"*"};
        Cursor cursor;
        ItemUsuario usuario=new ItemUsuario(correo);
        try {
            cursor=db.query(BDConstantes.TABLA_USUARIO,campos,
                    BDConstantes.USUARIO_CAMPO_CORREO+"=? LIMIT 1",parametros,null,null,null);
            if(cursor.getCount()>0){
                cursor.moveToFirst();
                usuario = Convertidor.createItemUsuarioOfCursor(cursor);
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return usuario;
    }

    public ArrayList<ItemUsuario> obtenerUsuariosOrdenadosPorAnclados(){
        Log.e("*****DBWorker*****","obtenerUsuarios");
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        ArrayList<ItemUsuario> datos_Usuario= new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_USUARIO
                    +" WHERE "+BDConstantes.USUARIO_CAMPO_ANCLADO+" ='1' ORDER BY "+
                    BDConstantes.USUARIO_CAMPO_ORDEN_ULT_MSG+" DESC", null);
            if(cursor.getCount()>0){
                while (cursor.moveToNext())
                    datos_Usuario.add(Convertidor.createItemUsuarioOfCursor(cursor));
            }
            cursor.close();
            cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_USUARIO
                    +" WHERE "+BDConstantes.USUARIO_CAMPO_ANCLADO+" ='0' ORDER BY "+
                    BDConstantes.USUARIO_CAMPO_ORDEN_ULT_MSG+" DESC", null);
            if(cursor.getCount()==0){
                cursor.close();
                db.close();
                return datos_Usuario;
            }
            while (cursor.moveToNext()) datos_Usuario.add(Convertidor.createItemUsuarioOfCursor(cursor));
            cursor.close();
            db.close();
        }catch (Exception e){
            e.printStackTrace();
            db.close();
        }
        return datos_Usuario;
    }

    public synchronized void insertarNuevoUsuario(ItemUsuario usuario){
        if (!existeUsuario(usuario.getCorreo(), true)){
            SQLiteDatabase db=conexionBD.getWritableDatabase();
            ContentValues values=new ContentValues();
            values.put(BDConstantes.USUARIO_CAMPO_CORREO,usuario.getCorreo());

            values.put(BDConstantes.USUARIO_CAMPO_ANCLADO,0);
            values.put(BDConstantes.USUARIO_CAMPO_CANT_MSG,0);

            values.put(BDConstantes.USUARIO_CAMPO_TIPO_ULT_MSG,0);
            values.put(BDConstantes.USUARIO_CAMPO_TEXTO_ULT_MSG,"");
            values.put(BDConstantes.USUARIO_CAMPO_ESTADO_ULT_MSG,1);
            values.put(BDConstantes.USUARIO_CAMPO_ORDEN_ULT_MSG,"");

            values.put(BDConstantes.USUARIO_CAMPO_BORRADOR,"");

            db.insert(BDConstantes.TABLA_USUARIO,BDConstantes.USUARIO_CAMPO_CORREO,values);
            db.close();
        }
    }

    public boolean existeUsuario(String usuario_correo, boolean defecto){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={usuario_correo};
        String [] campos={BDConstantes.USUARIO_CAMPO_CORREO};
        Cursor cursor;
        boolean exist=false;
        try {
            cursor=db.query(BDConstantes.TABLA_USUARIO,campos,
                    BDConstantes.USUARIO_CAMPO_CORREO+"=? LIMIT 1",parametros,null,null,null);
            if(cursor.getCount()>0) exist=cursor.moveToFirst();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
            exist=defecto;
        }
        db.close();
        return exist;
    }

    public synchronized int cantMsgNoVistos(String usuario_correo){
        int cant=0;
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={usuario_correo};
        String [] camposU={BDConstantes.USUARIO_CAMPO_CANT_MSG};
        Cursor cursor;
        try {
            cursor=db.query(BDConstantes.TABLA_USUARIO,camposU,
                    BDConstantes.USUARIO_CAMPO_CORREO+"=?",parametros,null,null,null);
            if(cursor.getCount()>0){
                cursor.moveToFirst();
                cant=cursor.getInt(0);
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return cant;
    }

    public synchronized void actualizarCantMensajesNoVistosX(String usuario_correo, int cant) {
        int cantExistentes=cantMsgNoVistos(usuario_correo);
        SQLiteDatabase db=conexionBD.getWritableDatabase();

        String [] parametros={usuario_correo};
        ContentValues values=new ContentValues();
        values.put(BDConstantes.USUARIO_CAMPO_CANT_MSG,cantExistentes+cant);

        db.update(BDConstantes.TABLA_USUARIO,values,BDConstantes.USUARIO_CAMPO_CORREO+"=?",parametros);
        db.close();
    }

    public String obtenerBorradorDe(String usuario_correo){
        String borrador="";
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={usuario_correo};
        String [] camposU={BDConstantes.USUARIO_CAMPO_BORRADOR};
        Cursor cursor;
        try {
            cursor=db.query(BDConstantes.TABLA_USUARIO,camposU,
                    BDConstantes.USUARIO_CAMPO_CORREO+"=?",parametros,null,null,null);
            if(cursor.getCount()>0){
                cursor.moveToFirst();
                if(cursor.getString(0)!=null)
                    borrador=cursor.getString(0);
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return borrador;
    }

    public synchronized void actualizarBorrador(String usuario_correo, String borrador) {
        SQLiteDatabase db=conexionBD.getWritableDatabase();

        String [] parametros={usuario_correo};
        ContentValues values=new ContentValues();
        values.put(BDConstantes.USUARIO_CAMPO_BORRADOR,borrador);

        db.update(BDConstantes.TABLA_USUARIO,values,BDConstantes.USUARIO_CAMPO_CORREO+"=?",parametros);
        db.close();
    }

    public synchronized void marcarComoVistoMensajesNoVistos(String usuario_correo){
        SQLiteDatabase db=conexionBD.getWritableDatabase();

        String [] parametros={usuario_correo};
        ContentValues values=new ContentValues();
        values.put(BDConstantes.USUARIO_CAMPO_CANT_MSG,0);

        db.update(BDConstantes.TABLA_USUARIO,values,BDConstantes.USUARIO_CAMPO_CORREO+"=?",parametros);
        db.close();
    }

    public synchronized void eliminarUsuario(String usuario_correo, boolean eliminarUsuario, boolean eliminarChat){
        SQLiteDatabase db=conexionBD.getWritableDatabase();
        String [] parametros={usuario_correo};
        if(eliminarUsuario) db.delete(BDConstantes.TABLA_USUARIO,BDConstantes.USUARIO_CAMPO_CORREO+"=?",parametros);
        if(eliminarChat) db.delete(BDConstantes.TABLA_CHAT,BDConstantes.CHAT_CAMPO_CORREO+"=?",parametros);
        db.close();
    }

    public synchronized void modificarUsuarioAnclado(String usuario_correo, int estAnclado){
        SQLiteDatabase db=conexionBD.getWritableDatabase();
        String[] parametrosU={usuario_correo};
        ContentValues valuesU=new ContentValues();
        valuesU.put(BDConstantes.USUARIO_CAMPO_ANCLADO,estAnclado); //0 no anclado 1 anclado
        db.update(BDConstantes.TABLA_USUARIO,valuesU,BDConstantes.USUARIO_CAMPO_CORREO+"=?",parametrosU);
        db.close();
    }

//    public synchronized void actualizarUltMsgUsuario(String correo, int tipo, int estado, String texto, String fech) {
//        SQLiteDatabase db=conexionBD.getWritableDatabase();
//        String [] parametros={correo};
//        ContentValues values=new ContentValues();
//        values.put(BDConstantes.USUARIO_CAMPO_TIPO_ULT_MSG,tipo);
//        values.put(BDConstantes.USUARIO_CAMPO_ESTADO_ULT_MSG,estado);
//        values.put(BDConstantes.USUARIO_CAMPO_TEXTO_ULT_MSG,texto);
//        values.put(BDConstantes.USUARIO_CAMPO_ORDEN_ULT_MSG,fech);
//        db.update(BDConstantes.TABLA_USUARIO,values,BDConstantes.USUARIO_CAMPO_CORREO+"=?",parametros);
//        db.close();
//    }

    public synchronized void actualizarUltMsgUsuario(ItemChat chat) {
        if(chat!=null){
            actualizarOrdenUsuario(chat.getCorreo(),chat.getOrden());
//            SQLiteDatabase db=conexionBD.getWritableDatabase();
//            String [] parametros={chat.getCorreo()};
//            ContentValues values=new ContentValues();
//            values.put(BDConstantes.USUARIO_CAMPO_TIPO_ULT_MSG,chat.getTipo_mensaje());
//            values.put(BDConstantes.USUARIO_CAMPO_ESTADO_ULT_MSG,chat.getEstado());
//            values.put(BDConstantes.USUARIO_CAMPO_TEXTO_ULT_MSG,chat.getMensaje());
//            values.put(BDConstantes.USUARIO_CAMPO_ORDEN_ULT_MSG,chat.getOrden());
//            db.update(BDConstantes.TABLA_USUARIO,values,BDConstantes.USUARIO_CAMPO_CORREO+"=?",parametros);
//            db.close();
        }
    }

    public synchronized void actualizarOrdenUsuario(String correo, String orden) {
        if(!correo.isEmpty() && !orden.isEmpty()){
            SQLiteDatabase db=conexionBD.getWritableDatabase();
            String [] parametros={correo};
            ContentValues values=new ContentValues();
            values.put(BDConstantes.USUARIO_CAMPO_ORDEN_ULT_MSG,orden);
            db.update(BDConstantes.TABLA_USUARIO,values,BDConstantes.USUARIO_CAMPO_CORREO+"=?",parametros);
            db.close();
        }
    }

    //////////////////////////////////////////CHAT////////////////////////////////////

    public ArrayList<ItemChat> obtenerMsgChat(String correo, int cantAct, int limite){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={correo};
        String [] campos={"*"};

        Cursor cursor;
        ArrayList<ItemChat> datos_chat = new ArrayList<>();
        try {
            cursor=db.query(BDConstantes.TABLA_CHAT,campos,
                    BDConstantes.CHAT_CAMPO_CORREO+"=? ORDER BY "+BDConstantes.CHAT_CAMPO_ORDEN+
                            " DESC LIMIT "+(cantAct+limite),
                    parametros,null,null, null);

            if(cursor.getCount()==0){
                cursor.close();
                db.close();
                return datos_chat;
            }

            cursor.move(cantAct+1);
            ItemChat temp = Convertidor.createItemChatOfCursor(cursor);
            datos_chat.add(temp);

            while (cursor.moveToNext()){
                temp = Convertidor.createItemChatOfCursor(cursor);
                datos_chat.add(temp);
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return datos_chat;
    }

    public ArrayList<ItemChat> obtenerTodosMsgChat(String correo){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={correo};
        String [] campos={"*"};

        Cursor cursor;
        ArrayList<ItemChat> datos_chat = new ArrayList<>();
        try {
            cursor=db.query(BDConstantes.TABLA_CHAT,campos,
                    BDConstantes.CHAT_CAMPO_CORREO+"=?",
                    parametros,null,null, null);

            if(cursor.getCount()==0){
                cursor.close();
                db.close();
                return datos_chat;
            }

            while (cursor.moveToNext())
                datos_chat.add(Convertidor.createItemChatOfCursor(cursor));
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return datos_chat;
    }

    public ItemChat obtenerUltMsgChatDe(String correo){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={correo};
        String [] campos={"*"};

        Cursor cursor;
        ItemChat ultMsg = null;
        try {
            cursor=db.query(BDConstantes.TABLA_CHAT,campos,
                    BDConstantes.CHAT_CAMPO_CORREO+"=? ORDER BY "+BDConstantes.CHAT_CAMPO_ORDEN+
                            " DESC LIMIT 1",
                    parametros,null,null, null);

            if(cursor.getCount()==0){
                cursor.close();
                db.close();
                return ultMsg;
            }

            cursor.moveToFirst();
            ultMsg = Convertidor.createItemChatOfCursor(cursor);

            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
//        if(ultMsg!=null) actualizarUltMsgUsuario(ultMsg);
        return ultMsg;
    }

    public ArrayList<ItemChat> obtenerMsgNoEnviados(){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={"1"};
        String [] campos={"*"};

        Cursor cursor;
        ArrayList<ItemChat> datos_chat = new ArrayList<>();
        try {
            cursor=db.query(BDConstantes.TABLA_CHAT,campos,
                    BDConstantes.CHAT_CAMPO_ESTADO+"=? ORDER BY "+BDConstantes.CHAT_CAMPO_ORDEN+
                            " ASC",
                    parametros,null,null, null);

            if(cursor.getCount()>0){
                while (cursor.moveToNext())
                    datos_chat.add(Convertidor.createItemChatOfCursor(cursor));
            }
            parametros[0] = "2";
            cursor=db.query(BDConstantes.TABLA_CHAT,campos,
                    BDConstantes.CHAT_CAMPO_ESTADO+"=? ORDER BY "+BDConstantes.CHAT_CAMPO_ORDEN+
                            " ASC",
                    parametros,null,null, null);

            if(cursor.getCount()>0){
                while (cursor.moveToNext())
                    datos_chat.add(Convertidor.createItemChatOfCursor(cursor));
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
            return obtenerMsgNoEnviados();
        }
        db.close();
        return datos_chat;
    }

    public synchronized void insertarChat(ItemChat chat){
        SQLiteDatabase db=conexionBD.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(BDConstantes.CHAT_CAMPO_ID,chat.getId());
        values.put(BDConstantes.CHAT_CAMPO_TIPO_MENSAJE,chat.getTipo_mensaje());
        values.put(BDConstantes.CHAT_CAMPO_ESTADO,chat.getEstado());
        values.put(BDConstantes.CHAT_CAMPO_CORREO,chat.getCorreo());
        values.put(BDConstantes.CHAT_CAMPO_MENSAJE,chat.getMensaje());
        values.put(BDConstantes.CHAT_CAMPO_RUTA_DATO,chat.getRuta_Dato());
        values.put(BDConstantes.CHAT_CAMPO_HORA,chat.getHora());
        values.put(BDConstantes.CHAT_CAMPO_FECHA,chat.getFecha());
        values.put(BDConstantes.CHAT_CAMPO_ID_MSG_RESP, chat.getId_msg_resp());
        values.put(BDConstantes.CHAT_CAMPO_EMISOR,chat.getEmisor());
        values.put(BDConstantes.CHAT_CAMPO_REENVIADO,chat.getReenviado());
        values.put(BDConstantes.CHAT_CAMPO_ORDEN,chat.getOrden());
        values.put(BDConstantes.CHAT_CAMPO_EDITADO,chat.getEditado());

        db.insert(BDConstantes.TABLA_CHAT,BDConstantes.CHAT_CAMPO_ID,values);
        db.close();
    }

    public synchronized boolean actualizarChatDescargado(ItemChat chat){
        if(existeChat(chat.getId())){
            SQLiteDatabase db = conexionBD.getWritableDatabase();
            String [] parametros={chat.getId()};
            ContentValues values = new ContentValues();

            values.put(BDConstantes.CHAT_CAMPO_TIPO_MENSAJE,chat.getTipo_mensaje());
            values.put(BDConstantes.CHAT_CAMPO_MENSAJE,chat.getMensaje());
            values.put(BDConstantes.CHAT_CAMPO_RUTA_DATO,chat.getRuta_Dato());
            values.put(BDConstantes.CHAT_CAMPO_ID_MSG_RESP, chat.getId_msg_resp());
            values.put(BDConstantes.CHAT_CAMPO_EMISOR,chat.getEmisor());
            values.put(BDConstantes.CHAT_CAMPO_REENVIADO,chat.getReenviado());

            db.update(BDConstantes.TABLA_CHAT,values,BDConstantes.CHAT_CAMPO_ID+"=?",parametros);
            db.close();
            return true;
        }
        else insertarChat(chat);
        return false;
    }

    public synchronized void editarMensajeChat(String idChat, String text){
        SQLiteDatabase db = conexionBD.getWritableDatabase();
        String [] parametros={idChat};
        ContentValues values = new ContentValues();
        values.put(BDConstantes.CHAT_CAMPO_MENSAJE,text);
        values.put(BDConstantes.CHAT_CAMPO_EDITADO,1);
        db.update(BDConstantes.TABLA_CHAT,values,BDConstantes.CHAT_CAMPO_ID+"=?",parametros);
        db.close();
    }

    public boolean existeChat(String idChat){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={idChat};
        String [] campos={BDConstantes.CHAT_CAMPO_ID};

        Cursor cursor;
        boolean exist=false;
        try {
            cursor=db.query(BDConstantes.TABLA_CHAT,campos,
                    BDConstantes.CHAT_CAMPO_ID+"=?",parametros,null,null,null);
            if(cursor.getCount()>0) exist=cursor.moveToFirst();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
            exist=false;
        }
        db.close();
        return exist;
    }

    public String existeMsgAntiguoDameCorreo(String idMsg){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={idMsg};
        String [] campos={BDConstantes.CHAT_CAMPO_CORREO};

        Cursor cursor;
        String existMsg="";
        try {
            cursor=db.query(BDConstantes.TABLA_CHAT,campos,
                    BDConstantes.CHAT_CAMPO_ID+"=? LIMIT 1",parametros,null,null,null);
            if(cursor.getCount()>0){
                cursor.moveToFirst();
                existMsg=cursor.getString(0);
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return existMsg;
    }

    public boolean existeMsgAntiguo(String idMsg){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={idMsg};
        String [] campos={BDConstantes.CHAT_CAMPO_CORREO};

        Cursor cursor;
        boolean existMsg=false;
        try {
            cursor=db.query(BDConstantes.TABLA_CHAT,campos,
                    BDConstantes.CHAT_CAMPO_ID+"=?",parametros,null,null,null);
            if(cursor.getCount()>0)
                existMsg=cursor.moveToFirst();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return existMsg;
    }

    public boolean esNecesarioEnviar(String idMsg){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={idMsg};
        String [] campos={BDConstantes.CHAT_CAMPO_ESTADO};

        Cursor cursor;
        boolean esNec=true;
        try {
            cursor=db.query(BDConstantes.TABLA_CHAT,campos,
                    BDConstantes.CHAT_CAMPO_ID+"=?",parametros,null,null,null);
            if(cursor!=null && cursor.getCount()>0){
                int estado = cursor.getInt(0);
                if(estado!=ItemChat.ESTADO_ERROR && estado!=ItemChat.ESTADO_ESPERANDO)
                    esNec=false;
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return esNec;
    }

    public synchronized void eliminarMsg(String idMsg){
        SQLiteDatabase db=conexionBD.getWritableDatabase();
        String [] parametros={idMsg};
        db.delete(BDConstantes.TABLA_CHAT,BDConstantes.CHAT_CAMPO_ID+"=?",parametros);
        db.close();
    }

    public synchronized void eliminarMsgFecha(){
        SQLiteDatabase db=conexionBD.getWritableDatabase();
        String [] parametros={"0"};
        db.delete(BDConstantes.TABLA_CHAT,BDConstantes.CHAT_CAMPO_TIPO_MENSAJE+"=?",parametros);
        db.close();
    }

    public synchronized void modificarEstadoUltMensaje(String correo, String idMsg, int estado){
        SQLiteDatabase db=conexionBD.getWritableDatabase();
        String[] parametros={idMsg};
        ContentValues values=new ContentValues();
        values.put(BDConstantes.CHAT_CAMPO_ESTADO,estado);

        String[] parametrosU={correo};
        ContentValues valuesU=new ContentValues();
        valuesU.put(BDConstantes.USUARIO_CAMPO_ESTADO_ULT_MSG,estado);

        db.update(BDConstantes.TABLA_CHAT,values,BDConstantes.CHAT_CAMPO_ID+"=?",parametros);
        db.update(BDConstantes.TABLA_USUARIO,valuesU,BDConstantes.USUARIO_CAMPO_CORREO+"=?",parametrosU);
        db.close();
    }

    public synchronized void modificarEstadoMensaje(String idMsg, int estado){
        if(existeMsgAntiguo(idMsg)){
            SQLiteDatabase db=conexionBD.getWritableDatabase();
            String[] parametros={idMsg};
            ContentValues values=new ContentValues();
            values.put(BDConstantes.CHAT_CAMPO_ESTADO,estado);

            db.update(BDConstantes.TABLA_CHAT,values,BDConstantes.CHAT_CAMPO_ID+"=?",parametros);
            db.close();
        }
    }

    //////////////////////////////////////////ESTADOS////////////////////////////////////

    public synchronized void insertarNuevoEstado(ItemEstado estado){
        if(!existeEstado(estado.getId())){
            SQLiteDatabase db = conexionBD.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(BDConstantes.ESTADO_CAMPO_ID, estado.getId());
            values.put(BDConstantes.ESTADO_CAMPO_CORREO, estado.getCorreo());
            values.put(BDConstantes.ESTADO_CAMPO_TIPO, estado.getTipo_estado());
            values.put(BDConstantes.ESTADO_CAMPO_ESTA_VISTO, estado.Visibilidad());
            values.put(BDConstantes.ESTADO_CAMPO_RUTA_IMAGEN, estado.getRuta_imagen());
            values.put(BDConstantes.ESTADO_CAMPO_TEXTO, estado.getTexto());
            values.put(BDConstantes.ESTADO_CAMPO_CANTIDAD_ME_GUSTA, estado.getCant_me_gusta());
            values.put(BDConstantes.ESTADO_CAMPO_CANTIDAD_ME_ENCANTA, estado.getCant_me_encanta());
            values.put(BDConstantes.ESTADO_CAMPO_CANTIDAD_ME_SONROJA, estado.getCant_me_sonroja());
            values.put(BDConstantes.ESTADO_CAMPO_CANTIDAD_ME_DIVIERTE, estado.getCant_me_divierte());
            values.put(BDConstantes.ESTADO_CAMPO_CANTIDAD_ME_ASOMBRA, estado.getCant_me_asombra());
            values.put(BDConstantes.ESTADO_CAMPO_CANTIDAD_ME_ENTRISTESE, estado.getCant_me_entristese());
            values.put(BDConstantes.ESTADO_CAMPO_CANTIDAD_ME_ENOJA, estado.getCant_me_enoja());
            values.put(BDConstantes.ESTADO_CAMPO_HORA, estado.getHora());
            values.put(BDConstantes.ESTADO_CAMPO_FECHA, estado.getFecha());
            values.put(BDConstantes.ESTADO_CAMPO_ORDEN, estado.getOrden());
            values.put(BDConstantes.ESTADO_CAMPO_ESTILO_TEXTO, estado.getEstilo_texto());

            db.insert(BDConstantes.TABLA_ESTADO, BDConstantes.ESTADO_CAMPO_ID, values);
            db.close();
        }
    }

    public boolean existeEstado(String idEstado){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={idEstado};
        String [] campos={BDConstantes.ESTADO_CAMPO_ID};

        Cursor cursor;
        boolean exist=false;
        try {
            cursor=db.query(BDConstantes.TABLA_ESTADO,campos,
                    BDConstantes.ESTADO_CAMPO_ID+"=?",parametros,null,null,null);
            if(cursor.getCount()>0) exist=cursor.moveToFirst();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
            exist=false;
        }
        db.close();
        return exist;
    }

    public synchronized void eliminarTodosLosEstadosDe(String correo){
        SQLiteDatabase db=conexionBD.getWritableDatabase();
        String [] parametros={correo};
        db.delete(BDConstantes.TABLA_ESTADO,BDConstantes.ESTADO_CAMPO_CORREO+"=?",parametros);
        db.close();
    }
    public synchronized void eliminarElEstadosDe(String idEstado){
        SQLiteDatabase db=conexionBD.getWritableDatabase();
        String [] parametros={idEstado};
        db.delete(BDConstantes.TABLA_ESTADO,BDConstantes.ESTADO_CAMPO_ID+"=?",parametros);
        db.close();
        eliminarTodasLasReaccionesEstadosDeEstado(idEstado);
        eliminarVistaEstadosDe(idEstado);
    }

    public ArrayList<String> obtenerCorreosEstadosNuevos(){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        ArrayList<String> seguidores = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_ESTADO+
                    " WHERE "+BDConstantes.ESTADO_CAMPO_ESTA_VISTO+" ='0' ORDER BY "
                    +BDConstantes.ESTADO_CAMPO_ORDEN+" DESC", null);
            if(cursor.getCount()==0){
                cursor.close();
                db.close();
                return seguidores;
            }
            while (cursor.moveToNext())
                seguidores.add(cursor.getString(0));
            cursor.close();
            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return seguidores;
    }

    public ArrayList<ItemEstado> obtenerEstadosNuevosDe(String correo){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        ArrayList<ItemEstado> estados = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_ESTADO+
                    " WHERE "+BDConstantes.ESTADO_CAMPO_ESTA_VISTO+" ='0' AND "+BDConstantes.ESTADO_CAMPO_CORREO
                    +" = \'"+correo+"\' ORDER BY "
                    +BDConstantes.ESTADO_CAMPO_ORDEN+" ASC", null);
            if(cursor.getCount()==0){
                cursor.close();
                db.close();
                return estados;
            }
            while (cursor.moveToNext())
                estados.add(Convertidor.createItemEstadoOfCursor(cursor));
            cursor.close();
            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return estados;
    }

    public ArrayList<ItemEstado> obtenerEstadosDe(String correo){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        ArrayList<ItemEstado> estados = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_ESTADO+
                    " WHERE "+BDConstantes.ESTADO_CAMPO_CORREO + " = \'"+correo+"\' ORDER BY "
                    +BDConstantes.ESTADO_CAMPO_ORDEN+" ASC", null);
            if(cursor.getCount()==0){
                cursor.close();
                db.close();
                return estados;
            }
            while (cursor.moveToNext())
                estados.add(Convertidor.createItemEstadoOfCursor(cursor));
            cursor.close();
            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return estados;
    }

    public ItemEstado obtenerEstado(String idEstado){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        ItemEstado estado = null;
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_ESTADO+
                    " WHERE "+BDConstantes.ESTADO_CAMPO_ID + " = \'"+idEstado+"\' ORDER BY "
                    +BDConstantes.ESTADO_CAMPO_ORDEN+" DESC LIMIT 1", null);
            if(cursor.getCount()==0){
                cursor.close();
                db.close();
                return null;
            }
            cursor.moveToFirst();
            estado = Convertidor.createItemEstadoOfCursor(cursor);
            cursor.close();
            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return estado;
    }

    public ArrayList<ItemEstado> obtenerEstadosNuevos(){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        ArrayList<ItemEstado> estadosNuevos = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_ESTADO+
                    " WHERE "+BDConstantes.ESTADO_CAMPO_ESTA_VISTO+" ='0' ORDER BY "
                    +BDConstantes.ESTADO_CAMPO_ORDEN+" DESC", null);
            if(cursor.getCount()==0){
                cursor.close();
                db.close();
                return estadosNuevos;
            }
            while (cursor.moveToNext())
                estadosNuevos.add(Convertidor.createItemEstadoOfCursor(cursor));
            cursor.close();
            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return estadosNuevos;
    }

    public ArrayList<ItemEstado> obtenerTodosLosEstados(){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        ArrayList<ItemEstado> datos_Estados= new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_ESTADO+" ORDER BY "+
                    BDConstantes.ESTADO_CAMPO_ORDEN+" DESC", null);
            if(cursor.getCount()==0){
                cursor.close();
                db.close();
                return datos_Estados;
            }
            while (cursor.moveToNext()) datos_Estados.add(Convertidor.createItemEstadoOfCursor(cursor));
            cursor.close();
            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return datos_Estados;
    }

    public ArrayList<ItemEstado> obtenerTodosLosEstadosLimite30(){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        ArrayList<ItemEstado> datos_Estados= new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_ESTADO+
                    " WHERE "+BDConstantes.ESTADO_CAMPO_ESTA_VISTO+" ='0' ORDER BY "
                    +BDConstantes.ESTADO_CAMPO_ORDEN+" DESC", null);
            if(cursor.getCount()>0){
                while (cursor.moveToNext())
                    datos_Estados.add(Convertidor.createItemEstadoOfCursor(cursor));
            }
            if(datos_Estados.size()<30){
                int cant = 30-datos_Estados.size();
                cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_ESTADO+
                        " WHERE "+BDConstantes.ESTADO_CAMPO_ESTA_VISTO+" ='1' ORDER BY "
                        +BDConstantes.ESTADO_CAMPO_ORDEN+" DESC LIMIT "+cant, null);
                if(cursor.getCount()==0){
                    cursor.close();
                    db.close();
                    return datos_Estados;
                }

                while (cursor.moveToNext())
                    datos_Estados.add(Convertidor.createItemEstadoOfCursor(cursor));
            }
            cursor.close();
            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return datos_Estados;
    }

    public ArrayList<ItemEstado> obtenerTodosLosEstadosOrdenadosXnoVistos(){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        ArrayList<ItemEstado> datos_Estados= new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_ESTADO+
                    " WHERE "+BDConstantes.ESTADO_CAMPO_ESTA_VISTO+" ='0' ORDER BY "
                    +BDConstantes.ESTADO_CAMPO_ORDEN+" DESC", null);
            if(cursor.getCount()>0){
                while (cursor.moveToNext())
                    datos_Estados.add(Convertidor.createItemEstadoOfCursor(cursor));
            }
            cursor.close();
            cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_ESTADO+
                    " WHERE "+BDConstantes.ESTADO_CAMPO_ESTA_VISTO+" ='1' ORDER BY "
                    +BDConstantes.ESTADO_CAMPO_ORDEN+" DESC", null);
            if(cursor.getCount()==0){
                cursor.close();
                db.close();
                return datos_Estados;
            }

            while (cursor.moveToNext())
                datos_Estados.add(Convertidor.createItemEstadoOfCursor(cursor));
            cursor.close();
            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return datos_Estados;
    }

    public synchronized void marcarEstadoComoVisto(String idEstado){
        if(existeEstado(idEstado)){
            SQLiteDatabase db=conexionBD.getWritableDatabase();
            String[] parametros={idEstado};
            ContentValues values=new ContentValues();
            values.put(BDConstantes.ESTADO_CAMPO_ESTA_VISTO,1);

            db.update(BDConstantes.TABLA_ESTADO,values,BDConstantes.ESTADO_CAMPO_ID+"=?",parametros);
            db.close();
        }
    }

    public synchronized void sumarUnaReaccion(String idEstado, String tipo, int cant){
        if(existeEstado(idEstado)){
            SQLiteDatabase db=conexionBD.getWritableDatabase();
            String[] parametros={idEstado};
            ContentValues values=new ContentValues();
            if(tipo.equals("1")) values.put(BDConstantes.ESTADO_CAMPO_CANTIDAD_ME_GUSTA,cant);
            else if(tipo.equals("2")) values.put(BDConstantes.ESTADO_CAMPO_CANTIDAD_ME_ENCANTA,cant);
            else if(tipo.equals("3")) values.put(BDConstantes.ESTADO_CAMPO_CANTIDAD_ME_SONROJA,cant);
            else if(tipo.equals("4")) values.put(BDConstantes.ESTADO_CAMPO_CANTIDAD_ME_DIVIERTE,cant);
            else if(tipo.equals("5")) values.put(BDConstantes.ESTADO_CAMPO_CANTIDAD_ME_ASOMBRA,cant);
            else if(tipo.equals("6")) values.put(BDConstantes.ESTADO_CAMPO_CANTIDAD_ME_ENTRISTESE,cant);
            else if(tipo.equals("7")) values.put(BDConstantes.ESTADO_CAMPO_CANTIDAD_ME_ENOJA,cant);

            if(values.size()>0)
                db.update(BDConstantes.TABLA_ESTADO,values,BDConstantes.ESTADO_CAMPO_ID+"=?",parametros);
            db.close();
        }
    }

    public int obtenerCantReacciones(String idEstado, int tipo){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={idEstado};
        String [] campos = new String[1];
        if(tipo==1) campos[0] = BDConstantes.ESTADO_CAMPO_CANTIDAD_ME_GUSTA;
        else if(tipo==2) campos[0] = BDConstantes.ESTADO_CAMPO_CANTIDAD_ME_ENCANTA;
        else if(tipo==3) campos[0] = BDConstantes.ESTADO_CAMPO_CANTIDAD_ME_SONROJA;
        else if(tipo==4) campos[0] = BDConstantes.ESTADO_CAMPO_CANTIDAD_ME_DIVIERTE;
        else if(tipo==5) campos[0] = BDConstantes.ESTADO_CAMPO_CANTIDAD_ME_ASOMBRA;
        else if(tipo==6) campos[0] = BDConstantes.ESTADO_CAMPO_CANTIDAD_ME_ENTRISTESE;
        else if(tipo==7) campos[0] = BDConstantes.ESTADO_CAMPO_CANTIDAD_ME_ENOJA;
        else return -1;

        Cursor cursor;
        int cant = -1;
        try {
            cursor=db.query(BDConstantes.TABLA_ESTADO,campos,
                    BDConstantes.ESTADO_CAMPO_ID+"=? LIMIT 1",
                    parametros,null,null, null);

            if(cursor.getCount()==0){
                cursor.close();
                db.close();
                return -1;
            }

            cursor.moveToFirst();
            cant = cursor.getInt(0);
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return cant;
    }

    //////////////////////////////////////////SEGUIDORES////////////////////////////////////
    public synchronized void insertarSeguidor(String correo){
        if(!existeSeguidor(correo)){
            SQLiteDatabase db=conexionBD.getWritableDatabase();
            ContentValues values=new ContentValues();
            values.put(BDConstantes.SEGUIDOR_CAMPO_ID,correo);
            db.insert(BDConstantes.TABLA_SEGUIDOR,BDConstantes.SEGUIDOR_CAMPO_ID,values);
            db.close();
        }
    }

    public boolean existeSeguidor(String correo) {
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={correo};
        String [] campos={BDConstantes.SEGUIDOR_CAMPO_ID};

        Cursor cursor;
        boolean exist=false;
        try {
            cursor=db.query(BDConstantes.TABLA_SEGUIDOR,campos,
                    BDConstantes.SEGUIDOR_CAMPO_ID+"=?",parametros,null,null,null);
            if(cursor.getCount()>0) exist=cursor.moveToFirst();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
            exist=false;
        }
        db.close();
        return exist;
    }

    public synchronized void eliminarSeguidor(String correo){
        SQLiteDatabase db=conexionBD.getWritableDatabase();
        String [] parametros={correo};
        db.delete(BDConstantes.TABLA_SEGUIDOR,BDConstantes.SEGUIDOR_CAMPO_ID+"=?",parametros);
        db.close();
    }

    public int obtenerCantSeguidores(){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        int cant_seguidores = 0;
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_SEGUIDOR, null);
            cant_seguidores = cursor.getCount();
            cursor.close();
            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return cant_seguidores;
    }

    public ArrayList<String> obtenerTodosSeguidoresOrdenadosCorreo(){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        ArrayList<String> seguidores = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_SEGUIDOR +
                    " ORDER BY "+BDConstantes.SEGUIDOR_CAMPO_ID+" ASC", null);
            if(cursor.getCount()==0){
                cursor.close();
                db.close();
                return seguidores;
            }
            while (cursor.moveToNext())
                seguidores.add(cursor.getString(0));
            cursor.close();
            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return seguidores;
    }

    public ArrayList<String> obtenerTodosSeguidores(){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        ArrayList<String> seguidores = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_SEGUIDOR, null);
            if(cursor.getCount()==0){
                cursor.close();
                db.close();
                return seguidores;
            }
            while (cursor.moveToNext())
                seguidores.add(cursor.getString(0));
            cursor.close();
            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return seguidores;
    }

    //////////////////////////////////////////SIGUIENDO_A////////////////////////////////////
    public synchronized void insertarSiguiendoA(String correo){
        if(!existeSiguiendoA(correo)){
            SQLiteDatabase db=conexionBD.getWritableDatabase();
            ContentValues values=new ContentValues();
            values.put(BDConstantes.SIGUIENDO_A_CAMPO_ID,correo);
            db.insert(BDConstantes.TABLA_SIGUIENDO_A,BDConstantes.SIGUIENDO_A_CAMPO_ID,values);
            db.close();
        }
    }

    public boolean existeSiguiendoA(String correo) {
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={correo};
        String [] campos={BDConstantes.SIGUIENDO_A_CAMPO_ID};

        Cursor cursor;
        boolean exist=false;
        try {
            cursor=db.query(BDConstantes.TABLA_SIGUIENDO_A,campos,
                    BDConstantes.SIGUIENDO_A_CAMPO_ID+"=? LIMIT 1",parametros,null,null,null);
            if(cursor.getCount()>0) exist=cursor.moveToFirst();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
            exist=false;
        }
        db.close();
        return exist;
    }

    public synchronized void eliminarSiguiendoA(String correo){
        SQLiteDatabase db=conexionBD.getWritableDatabase();
        String [] parametros={correo};
        db.delete(BDConstantes.TABLA_SIGUIENDO_A,BDConstantes.SIGUIENDO_A_CAMPO_ID+"=?",parametros);
        db.close();
    }

    public int obtenerCantSiguiendoA(){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        int cant_seguidores = 0;
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_SIGUIENDO_A, null);
            cant_seguidores = cursor.getCount();
            cursor.close();
            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return cant_seguidores;
    }

    public ArrayList<String> obtenerTodosSiguiendoA(){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        ArrayList<String> seguidores = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_SIGUIENDO_A, null);
            if(cursor.getCount()==0){
                cursor.close();
                db.close();
                return seguidores;
            }
            while (cursor.moveToNext())
                seguidores.add(cursor.getString(0));
            cursor.close();
            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return seguidores;
    }

    //////////////////////////////////////////RACCIONES ESTADOS////////////////////////////////////
    public synchronized void insertarNuevaReaccionEstado(ItemReaccionEstado reaccionEstado) {
        if (existeEstado(reaccionEstado.getIdEstado())) {
            SQLiteDatabase db = conexionBD.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(BDConstantes.REACCION_CAMPO_ID_ESTADO, reaccionEstado.getIdEstado());
            values.put(BDConstantes.REACCION_CAMPO_CORREO, reaccionEstado.getCorreo());
            values.put(BDConstantes.REACCION_CAMPO_TIPO_REACCION, reaccionEstado.getTipoReaccion());
            values.put(BDConstantes.REACCION_CAMPO_HORA, reaccionEstado.getHora());
            values.put(BDConstantes.REACCION_CAMPO_FECHA, reaccionEstado.getFecha());

            db.insert(BDConstantes.TABLA_REACCION_ESTADO, BDConstantes.REACCION_CAMPO_ID_ESTADO, values);
            db.close();
        }
    }

    public ArrayList<ItemReaccionEstado> obtenerReaccionesEstadosDelEstado(String idEstado){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        ArrayList<ItemReaccionEstado> reaccionEstados = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_REACCION_ESTADO+
                    " WHERE "+BDConstantes.REACCION_CAMPO_ID_ESTADO + " = \'"+idEstado+"\'", null);
            if(cursor.getCount()==0){
                cursor.close();
                db.close();
                return reaccionEstados;
            }
            while (cursor.moveToNext())
                reaccionEstados.add(Convertidor.createItemReaccionEstadoOfCursor(cursor));
            cursor.close();
            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return reaccionEstados;
    }

    public ArrayList<ItemReaccionEstado> obtenerReaccionesEstadosDelCorreo(String correo){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        ArrayList<ItemReaccionEstado> reaccionEstados = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_REACCION_ESTADO+
                    " WHERE "+BDConstantes.REACCION_CAMPO_CORREO + " = \'"+correo+"\'", null);
            if(cursor.getCount()==0){
                cursor.close();
                db.close();
                return reaccionEstados;
            }
            while (cursor.moveToNext())
                reaccionEstados.add(Convertidor.createItemReaccionEstadoOfCursor(cursor));
            cursor.close();
            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return reaccionEstados;
    }

    public synchronized void eliminarTodasLasReaccionesEstadosDeCorreo(String correo){
        SQLiteDatabase db=conexionBD.getWritableDatabase();
        String [] parametros={correo};
        db.delete(BDConstantes.TABLA_REACCION_ESTADO,
                BDConstantes.REACCION_CAMPO_CORREO+"=?",parametros);
        db.close();
    }
    public synchronized void eliminarTodasLasReaccionesEstadosDeEstado(String idEstado){
        SQLiteDatabase db=conexionBD.getWritableDatabase();
        String [] parametros={idEstado};
        db.delete(BDConstantes.TABLA_REACCION_ESTADO,
                BDConstantes.REACCION_CAMPO_ID_ESTADO+"=?",parametros);
        db.close();
    }

    //////////////////////////////////////////VISTAS_ESTADOS////////////////////////////////////
    public synchronized void insertarNuevaVistaEstado(ItemVistaEstado vistaEstado){
        SQLiteDatabase db=conexionBD.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(BDConstantes.VISTA_ESTADO_CAMPO_ID_ESTADO,vistaEstado.getIdEstado());
        values.put(BDConstantes.VISTA_ESTADO_CAMPO_CORREO,vistaEstado.getCorreo());
        values.put(BDConstantes.VISTA_ESTADO_CAMPO_HORA,vistaEstado.getHora());
        values.put(BDConstantes.VISTA_ESTADO_CAMPO_FECHA,vistaEstado.getFecha());
        db.insert(BDConstantes.TABLA_VISTA_ESTADO,BDConstantes.VISTA_ESTADO_CAMPO_ID_ESTADO,values);
        db.close();
    }

    public synchronized void eliminarVistaEstadosDe(String idEstado){
        SQLiteDatabase db=conexionBD.getWritableDatabase();
        String [] parametros={idEstado};
        db.delete(BDConstantes.TABLA_VISTA_ESTADO,BDConstantes.VISTA_ESTADO_CAMPO_ID_ESTADO+"=?",parametros);
        db.close();
    }

    public int obtenerCantVistasEstadosDe(String idEstado){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        int cant = 0;
        String [] parametros={idEstado};
        String [] campos={BDConstantes.VISTA_ESTADO_CAMPO_ID_ESTADO};
        try {
            Cursor cursor=db.query(BDConstantes.TABLA_VISTA_ESTADO,campos,
                    BDConstantes.VISTA_ESTADO_CAMPO_ID_ESTADO+"=?",parametros,
                    null,null,null);
            if(cursor!=null) cant = cursor.getCount();
            cursor.close();
            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return cant;
    }

    public ArrayList<ItemVistaEstado> obtenerVistasEstadosDe(String idEstado){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        ArrayList<ItemVistaEstado> vistas = new ArrayList<>();
        String [] parametros={idEstado};
        String [] campos={"*"};
        try {
            Cursor cursor=db.query(BDConstantes.TABLA_VISTA_ESTADO,campos,
                    BDConstantes.VISTA_ESTADO_CAMPO_ID_ESTADO+"=?",parametros,
                    null,null,null);
            if(cursor.getCount()>0){
                while (cursor.moveToNext()){
                    ItemVistaEstado temp = Convertidor.createItemVistaEstadoOfCursor(cursor);
                    vistas.add(temp);
                }
            }
            cursor.close();
            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return vistas;
    }

    //////////////////////////////////////////ESTADISTICA_PERSONAL////////////////////////////////////
    public synchronized boolean insertarNuevaEstadisticaPersonal(ItemEstadisticaPersonal estadisticaPersonal){
        if(!existeEstadisticaPersonal(estadisticaPersonal.getId())){
            SQLiteDatabase db=conexionBD.getWritableDatabase();
            ContentValues values=new ContentValues();
            values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_ID,estadisticaPersonal.getId());

            values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_MSG_ENVIADOS,
                    estadisticaPersonal.getCant_msg_env());
            values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_MSG_ENVIADOS_MG,
                    estadisticaPersonal.getCant_msg_env_mg());
            values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_MSG_RECIBIDOS,
                    estadisticaPersonal.getCant_msg_rec());
            values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_MSG_RECIBIDOS_MG,
                    estadisticaPersonal.getCant_msg_rec_mg());
            values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_IMG_ENVIADAS,
                    estadisticaPersonal.getCant_img_env());
            values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_IMG_ENVIADAS_MG,
                    estadisticaPersonal.getCant_img_env_mg());
            values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_IMG_RECIBIDAS,
                    estadisticaPersonal.getCant_img_rec());
            values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_IMG_RECIBIDAS_MG,
                    estadisticaPersonal.getCant_img_rec_mg());
            values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_AUD_ENVIADOS,
                    estadisticaPersonal.getCant_aud_env());
            values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_AUD_ENVIADOS_MG,
                    estadisticaPersonal.getCant_aud_env_mg());
            values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_AUD_RECIBIDOS,
                    estadisticaPersonal.getCant_aud_rec());
            values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_AUD_RECIBIDOS_MG,
                    estadisticaPersonal.getCant_aud_rec_mg());
            values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_ARC_ENVIADOS,
                    estadisticaPersonal.getCant_arc_env());
            values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_ARC_ENVIADOS_MG,
                    estadisticaPersonal.getCant_arc_env_mg());
            values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_ARC_RECIBIDOS,
                    estadisticaPersonal.getCant_arc_rec());
            values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_ARC_RECIBIDOS_MG,
                    estadisticaPersonal.getCant_arc_rec_mg());

            values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_STI_ENVIADOS,
                    estadisticaPersonal.getCant_sti_env());
            values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_STI_ENVIADOS_MG,
                    estadisticaPersonal.getCant_sti_env_mg());
            values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_STI_RECIBIDOS,
                    estadisticaPersonal.getCant_sti_rec());
            values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_STI_RECIBIDOS_MG,
                    estadisticaPersonal.getCant_sti_rec_mg());

            values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_ESTADOS_RECIBIDOS,
                    estadisticaPersonal.getCant_est_rec());
            values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_ESTADOS_RECIBIDOS_MG,
                    estadisticaPersonal.getCant_est_rec_mg());
            values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_ACT_PERFIL_RECIBIDAS,
                    estadisticaPersonal.getCant_act_per_rec());
            values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_ACT_PERFIL_RECIBIDAS_MG,
                    estadisticaPersonal.getCant_act_per_rec_mg());

            db.insert(BDConstantes.TABLA_ESTADISTICA_PERSONAL,BDConstantes.ESTADISTICA_PERSONAL_CAMPO_ID,values);
            db.close();
            return true;
        }
        return false;
    }

    public ItemEstadisticaPersonal obtenerEstadisticaPersonal(String correo){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        ItemEstadisticaPersonal dato = null;
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_ESTADISTICA_PERSONAL+
                    " WHERE "+BDConstantes.ESTADISTICA_PERSONAL_CAMPO_ID + " = \'"+correo+"\' LIMIT 1", null);
            if(cursor.getCount()==0){
                cursor.close();
                db.close();
                return null;
            }
            cursor.moveToFirst();
            dato = Convertidor.createItemEstadisticaPersonalOfCursor(cursor);
            cursor.close();
            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return dato;
    }

    public boolean existeEstadisticaPersonal(String correo) {
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={correo};
        String [] campos={BDConstantes.ESTADISTICA_PERSONAL_CAMPO_ID};

        Cursor cursor;
        boolean exist=false;
        try {
            cursor=db.query(BDConstantes.TABLA_ESTADISTICA_PERSONAL,campos,
                    BDConstantes.ESTADISTICA_PERSONAL_CAMPO_ID+"=? LIMIT 1",parametros,null,null,null);
            if(cursor.getCount()>0) exist=cursor.moveToFirst();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
            exist=false;
        }
        db.close();
        return exist;
    }

    public ArrayList<ItemEstadisticaPersonal> obtenerTodasEstadisticasPersonales(){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        ArrayList<ItemEstadisticaPersonal> datos= new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_ESTADISTICA_PERSONAL, null);
            if(cursor.getCount()==0){
                cursor.close();
                db.close();
                return datos;
            }
            while (cursor.moveToNext()) datos.add(Convertidor.createItemEstadisticaPersonalOfCursor(cursor));
            cursor.close();
            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return datos;
    }

    public synchronized void modificarEstadisticaPersonal(ItemEstadisticaPersonal estadisticaPersonal){
        SQLiteDatabase db=conexionBD.getWritableDatabase();
        String[] parametros={estadisticaPersonal.getId()};
        ContentValues values=new ContentValues();
        values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_MSG_ENVIADOS,
                estadisticaPersonal.getCant_msg_env());
        values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_MSG_ENVIADOS_MG,
                estadisticaPersonal.getCant_msg_env_mg());
        values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_MSG_RECIBIDOS,
                estadisticaPersonal.getCant_msg_rec());
        values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_MSG_RECIBIDOS_MG,
                estadisticaPersonal.getCant_msg_rec_mg());
        values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_IMG_ENVIADAS,
                estadisticaPersonal.getCant_img_env());
        values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_IMG_ENVIADAS_MG,
                estadisticaPersonal.getCant_img_env_mg());
        values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_IMG_RECIBIDAS,
                estadisticaPersonal.getCant_img_rec());
        values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_IMG_RECIBIDAS_MG,
                estadisticaPersonal.getCant_img_rec_mg());
        values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_AUD_ENVIADOS,
                estadisticaPersonal.getCant_aud_env());
        values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_AUD_ENVIADOS_MG,
                estadisticaPersonal.getCant_aud_env_mg());
        values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_AUD_RECIBIDOS,
                estadisticaPersonal.getCant_aud_rec());
        values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_AUD_RECIBIDOS_MG,
                estadisticaPersonal.getCant_aud_rec_mg());
        values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_ARC_ENVIADOS,
                estadisticaPersonal.getCant_arc_env());
        values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_ARC_ENVIADOS_MG,
                estadisticaPersonal.getCant_arc_env_mg());
        values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_ARC_RECIBIDOS,
                estadisticaPersonal.getCant_arc_rec());
        values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_ARC_RECIBIDOS_MG,
                estadisticaPersonal.getCant_arc_rec_mg());

        values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_STI_ENVIADOS,
                estadisticaPersonal.getCant_sti_env());
        values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_STI_ENVIADOS_MG,
                estadisticaPersonal.getCant_sti_env_mg());
        values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_STI_RECIBIDOS,
                estadisticaPersonal.getCant_sti_rec());
        values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_STI_RECIBIDOS_MG,
                estadisticaPersonal.getCant_sti_rec_mg());

        values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_ESTADOS_RECIBIDOS,
                estadisticaPersonal.getCant_est_rec());
        values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_ESTADOS_RECIBIDOS_MG,
                estadisticaPersonal.getCant_est_rec_mg());
        values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_ACT_PERFIL_RECIBIDAS,
                estadisticaPersonal.getCant_act_per_rec());
        values.put(BDConstantes.ESTADISTICA_PERSONAL_CAMPO_CANT_ACT_PERFIL_RECIBIDAS_MG,
                estadisticaPersonal.getCant_act_per_rec_mg());

        db.update(BDConstantes.TABLA_ESTADISTICA_PERSONAL,values,BDConstantes.ESTADISTICA_PERSONAL_CAMPO_ID+"=?",parametros);
        db.close();
    }

    public synchronized void resetearEstadisticasPersonales(){
        SQLiteDatabase db=conexionBD.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_ESTADISTICA_PERSONAL);
        db.execSQL(BDConstantes.CREAR_TABLA_ESTADISTICA_PERSONAL);
        db.close();
    }

    //////////////////////////////////////////TEMAS////////////////////////////////////
    public synchronized void insertarNuevoTema(ItemTemas temas){
//        if(existeTema(temas.getNombre())) eliminarTema(temas.getNombre());
        if(!existeTemaId(temas.getId())){
            SQLiteDatabase db = conexionBD.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(BDConstantes.TEMAS_CAMPO_ID, temas.getId());
            values.put(BDConstantes.TEMAS_CAMPO_NOMBRE, temas.getNombre());
            values.put(BDConstantes.TEMAS_CAMPO_TIPO, temas.getTipo());
            values.put(BDConstantes.TEMAS_CAMPO_OSCURO, temas.isOscuro()?1:0);
            values.put(BDConstantes.TEMAS_CAMPO_RUTA_IMG, temas.getRutaImg());
            values.put(BDConstantes.TEMAS_CAMPO_CREADOR, temas.getCreador());

            values.put(BDConstantes.TEMAS_CAMPO_COLOR_BARRA, temas.getColor_barra());
            values.put(BDConstantes.TEMAS_CAMPO_COLOR_BTN, temas.getColor_btn());
            values.put(BDConstantes.TEMAS_CAMPO_COLOR_TEXTO, temas.getColor_texto());
            values.put(BDConstantes.TEMAS_CAMPO_COLOR_FONDO, temas.getColor_fondo());
            values.put(BDConstantes.TEMAS_CAMPO_COLOR_INTERIOR, temas.getColor_interior());
            values.put(BDConstantes.TEMAS_CAMPO_COLOR_MSG_IZQ, temas.getColor_msg_izq());
            values.put(BDConstantes.TEMAS_CAMPO_COLOR_MSG_DER, temas.getColor_msg_der());
            values.put(BDConstantes.TEMAS_CAMPO_COLOR_MSG_FECHA, temas.getColor_msg_fecha());
            values.put(BDConstantes.TEMAS_CAMPO_COLOR_ACCENTO, temas.getColor_accento());
            values.put(BDConstantes.TEMAS_CAMPO_COLOR_ICO_GEN, temas.getColor_ico_gen());

            values.put(BDConstantes.TEMAS_CAMPO_FONT_BARRA, temas.getFont_barra());
            values.put(BDConstantes.TEMAS_CAMPO_FONT_MSG_IZQ, temas.getFont_msg_izq());
            values.put(BDConstantes.TEMAS_CAMPO_FONT_MSG_DER, temas.getFont_msg_der());
            values.put(BDConstantes.TEMAS_CAMPO_FONT_MSG_FECHA, temas.getFont_msg_fecha());
            values.put(BDConstantes.TEMAS_CAMPO_FONT_ICO, temas.getFont_texto_resaltado());

            values.put(BDConstantes.TEMAS_CAMPO_COLOR_DIALOGO, temas.getColor_dialogo());
            values.put(BDConstantes.TEMAS_CAMPO_COLOR_TOAST, temas.getColor_toast());
            values.put(BDConstantes.TEMAS_CAMPO_COLOR_BURBUJA, temas.getColor_burbuja());
            values.put(BDConstantes.TEMAS_CAMPO_FONT_BURBUJA, temas.getFont_burbuja());

            values.put(BDConstantes.TEMAS_CAMPO_COLOR_BARCHAT, temas.getColor_barchat());
            values.put(BDConstantes.TEMAS_CAMPO_FONT_BARCHAT, temas.getFont_barchat());

            values.put(BDConstantes.TEMAS_CAMPO_COLOR_SEL_MSJ, temas.getSel_msj());

            db.insert(BDConstantes.TABLA_TEMAS, BDConstantes.TEMAS_CAMPO_ID, values);
            db.close();
        }
    }

    public synchronized void modificarTema(ItemTemas temas){
        if(existeTemaId(temas.getId())){
            SQLiteDatabase db=conexionBD.getWritableDatabase();
            String[] parametros={temas.getId()};
            ContentValues values=new ContentValues();

            values.put(BDConstantes.TEMAS_CAMPO_RUTA_IMG, temas.getRutaImg());

            values.put(BDConstantes.TEMAS_CAMPO_COLOR_BARRA, temas.getColor_barra());
            values.put(BDConstantes.TEMAS_CAMPO_COLOR_BTN, temas.getColor_btn());
            values.put(BDConstantes.TEMAS_CAMPO_COLOR_TEXTO, temas.getColor_texto());
            values.put(BDConstantes.TEMAS_CAMPO_COLOR_FONDO, temas.getColor_fondo());
            values.put(BDConstantes.TEMAS_CAMPO_COLOR_INTERIOR, temas.getColor_interior());
            values.put(BDConstantes.TEMAS_CAMPO_COLOR_MSG_IZQ, temas.getColor_msg_izq());
            values.put(BDConstantes.TEMAS_CAMPO_COLOR_MSG_DER, temas.getColor_msg_der());
            values.put(BDConstantes.TEMAS_CAMPO_COLOR_MSG_FECHA, temas.getColor_msg_fecha());
            values.put(BDConstantes.TEMAS_CAMPO_COLOR_ACCENTO, temas.getColor_accento());
            values.put(BDConstantes.TEMAS_CAMPO_COLOR_ICO_GEN, temas.getColor_ico_gen());

            values.put(BDConstantes.TEMAS_CAMPO_FONT_BARRA, temas.getFont_barra());
            values.put(BDConstantes.TEMAS_CAMPO_FONT_MSG_IZQ, temas.getFont_msg_izq());
            values.put(BDConstantes.TEMAS_CAMPO_FONT_MSG_DER, temas.getFont_msg_der());
            values.put(BDConstantes.TEMAS_CAMPO_FONT_MSG_FECHA, temas.getFont_msg_fecha());
            values.put(BDConstantes.TEMAS_CAMPO_FONT_ICO, temas.getFont_texto_resaltado());

            values.put(BDConstantes.TEMAS_CAMPO_COLOR_DIALOGO, temas.getColor_dialogo());
            values.put(BDConstantes.TEMAS_CAMPO_COLOR_TOAST, temas.getColor_toast());
            values.put(BDConstantes.TEMAS_CAMPO_COLOR_BURBUJA, temas.getColor_burbuja());
            values.put(BDConstantes.TEMAS_CAMPO_FONT_BURBUJA, temas.getFont_burbuja());

            values.put(BDConstantes.TEMAS_CAMPO_COLOR_BARCHAT, temas.getColor_barchat());
            values.put(BDConstantes.TEMAS_CAMPO_FONT_BARCHAT, temas.getFont_barchat());

            values.put(BDConstantes.TEMAS_CAMPO_COLOR_SEL_MSJ, temas.getSel_msj());

            db.update(BDConstantes.TABLA_TEMAS,values,BDConstantes.TEMAS_CAMPO_ID+"=?",parametros);
            db.close();
        } else insertarNuevoTema(temas);
    }

    public boolean existeTemaNombre(String nombre){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={nombre};
        String [] campos={BDConstantes.TEMAS_CAMPO_NOMBRE};

        Cursor cursor;
        boolean exist=false;
        try {
            cursor=db.query(BDConstantes.TABLA_TEMAS,campos,
                    BDConstantes.TEMAS_CAMPO_NOMBRE+"=?",parametros,null,null,null);
            if(cursor.getCount()>0) exist=cursor.moveToFirst();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
            exist=false;
        }
        db.close();
        return exist;
    }

    public boolean existeTemaId(String id){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={id};
        String [] campos={BDConstantes.TEMAS_CAMPO_ID};

        Cursor cursor;
        boolean exist=false;
        try {
            cursor=db.query(BDConstantes.TABLA_TEMAS,campos,
                    BDConstantes.TEMAS_CAMPO_ID+"=?",parametros,null,null,null);
            if(cursor.getCount()>0) exist=cursor.moveToFirst();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
            exist=false;
        }
        db.close();
        return exist;
    }

    public ArrayList<ItemTemas> obtenerTemas(){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        ArrayList<ItemTemas> datos = new ArrayList<>();
        Cursor cursor;
        try{
            cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_TEMAS, null);

            if(cursor.getCount()==0){
                cursor.close();
                db.close();
                return datos;
            }
            while (cursor.moveToNext()){
                datos.add(Convertidor.createItemTemasOfCursor(cursor));
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return datos;
    }

    public ArrayList<ItemTemas> obtenerTemas(boolean oscuro){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        ArrayList<ItemTemas> datos = new ArrayList<>();
        Cursor cursor;
        try{
            if(oscuro){
                cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_TEMAS+
                        " WHERE "+BDConstantes.TEMAS_CAMPO_OSCURO+" ='1'", null);
            }
            else{
                cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_TEMAS+
                        " WHERE "+BDConstantes.TEMAS_CAMPO_OSCURO+" ='0'", null);
            }


            if(cursor.getCount()==0){
                cursor.close();
                db.close();
                return datos;
            }
            while (cursor.moveToNext()){
                datos.add(Convertidor.createItemTemasOfCursor(cursor));
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return datos;
    }

    public ItemTemas obtenerTema(String id, boolean esOscuro){
        ItemTemas temas = null;
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={id};
        String [] campos={"*"};

        Cursor cursor;
        try {
            cursor=db.query(BDConstantes.TABLA_TEMAS,campos,
                    BDConstantes.TEMAS_CAMPO_ID+"=?",parametros,null,null,null);
            if(cursor.getCount()>0){
                cursor.moveToFirst();
                temas=Convertidor.createItemTemasOfCursor(cursor);
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        if(temas==null){
            ArrayList<ItemTemas> t = obtenerTemas(esOscuro);
            if(t.size()>0){
                temas = t.get(0);
                if(esOscuro) YouChatApplication.setIDTemaOscuro(temas.getId());
                else YouChatApplication.setIDTemaClaro(temas.getId());
            }
        }
        return temas;
    }

    public synchronized void eliminarTema(String id){
        SQLiteDatabase db=conexionBD.getWritableDatabase();
        String [] parametros={id};
        db.delete(BDConstantes.TABLA_TEMAS,BDConstantes.TEMAS_CAMPO_ID+"=?",parametros);
        db.close();
    }

    public synchronized void eliminarTodosTema(){
        SQLiteDatabase db=conexionBD.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_TEMAS);
        db.execSQL(BDConstantes.CREAR_TABLA_TEMAS);
        db.close();
    }

    //////////////////////////////////////////UsuarioCorreo////////////////////////////////////
    public synchronized void insertarUsuarioCorreo(ItemUsuarioCorreo usuarioCorreo, boolean esMio){
        if(!existeUsuarioCorreo(usuarioCorreo.getCorreo())){
            SQLiteDatabase db = conexionBD.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(BDConstantes.USUARIO_CORREO_CAMPO_CORREO, usuarioCorreo.getCorreo());
            if(!esMio){
                values.put(BDConstantes.USUARIO_CORREO_CAMPO_NOMBRE, usuarioCorreo.getNombre());
                values.put(BDConstantes.USUARIO_CORREO_CAMPO_CHAT_GROUP_ID, usuarioCorreo.getGroupId());
            }
            values.put(BDConstantes.USUARIO_CORREO_CAMPO_HORA, usuarioCorreo.getHora());
            values.put(BDConstantes.USUARIO_CORREO_CAMPO_FECHA, usuarioCorreo.getFecha());
            values.put(BDConstantes.USUARIO_CORREO_CAMPO_ORDEN, usuarioCorreo.getOrden());

            db.insert(BDConstantes.TABLA_USUARIO_CORREO, BDConstantes.USUARIO_CORREO_CAMPO_CORREO, values);
            db.close();
        }
        else modificarUsuarioCorreo(usuarioCorreo, esMio);
    }

    public synchronized void modificarUsuarioCorreo(ItemUsuarioCorreo usuarioCorreo, boolean esMio){
        SQLiteDatabase db=conexionBD.getWritableDatabase();
        String[] parametros={usuarioCorreo.getCorreo()};
        ContentValues values=new ContentValues();

        if(!esMio){
            values.put(BDConstantes.USUARIO_CORREO_CAMPO_NOMBRE, usuarioCorreo.getNombre());
            values.put(BDConstantes.USUARIO_CORREO_CAMPO_CHAT_GROUP_ID, usuarioCorreo.getGroupId());
        }
        values.put(BDConstantes.USUARIO_CORREO_CAMPO_HORA, usuarioCorreo.getHora());
        values.put(BDConstantes.USUARIO_CORREO_CAMPO_FECHA, usuarioCorreo.getFecha());
        values.put(BDConstantes.USUARIO_CORREO_CAMPO_ORDEN, usuarioCorreo.getOrden());

        db.update(BDConstantes.TABLA_USUARIO_CORREO,values,BDConstantes.USUARIO_CORREO_CAMPO_CORREO+"=?",parametros);
        db.close();
    }

    public boolean existeUsuarioCorreo(String correo){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={correo};
        String [] campos={BDConstantes.USUARIO_CAMPO_CORREO};

        Cursor cursor;
        boolean exist=false;
        try {
            cursor=db.query(BDConstantes.TABLA_USUARIO_CORREO,campos,
                    BDConstantes.USUARIO_CAMPO_CORREO+"=?",parametros,null,null,null);
            if(cursor.getCount()>0) exist=cursor.moveToFirst();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
            exist=false;
        }
        db.close();
        return exist;
    }

    public String obtenerIdGroupUsuarioCorreo(String correo){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={correo};
        String [] campos={BDConstantes.USUARIO_CORREO_CAMPO_CHAT_GROUP_ID};

        Cursor cursor;
        String groupId="";
        try {
            cursor=db.query(BDConstantes.TABLA_USUARIO_CORREO,campos,
                    BDConstantes.USUARIO_CAMPO_CORREO+"=? LIMIT 1",parametros,null,null,null);
            if(cursor.getCount()>0){
                cursor.moveToFirst();
                groupId = cursor.getString(0);
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return groupId;
    }

    public ArrayList<ItemUsuarioCorreo> obtenerUsuarioCorreos(){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        ArrayList<ItemUsuarioCorreo> datos = new ArrayList<>();
        Cursor cursor;
        try{
            cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_USUARIO_CORREO
                    + " ORDER BY "+BDConstantes.USUARIO_CORREO_CAMPO_ORDEN + " DESC", null);

            if(cursor.getCount()==0){
                cursor.close();
                db.close();
                return datos;
            }
            while (cursor.moveToNext()){
                datos.add(Convertidor.createItemUsuarioCorreoOfCursor(cursor));
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return datos;
    }

    public synchronized void eliminarUsuarioCorreo(String correo){
        SQLiteDatabase db=conexionBD.getWritableDatabase();
        String [] parametros={correo};
        db.delete(BDConstantes.TABLA_USUARIO_CORREO,BDConstantes.USUARIO_CAMPO_CORREO+"=?",parametros);
        db.close();
        eliminarTodosMensajesCorreosDe(correo);
    }

    public void eliminarTodoBuzon() {
        SQLiteDatabase db=conexionBD.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_USUARIO_CORREO);
        db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_MENSAJE_CORREO);
        db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_ADJUNTO_CORREO);
        db.execSQL(BDConstantes.CREAR_TABLA_USUARIO_CORREO);
        db.execSQL(BDConstantes.CREAR_TABLA_MENSAJE_CORREO);
        db.execSQL(BDConstantes.CREAR_TABLA_ADJUNTO_CORREO);
        db.close();
    }

    //////////////////////////////////////////ItemMensajeCorreo////////////////////////////////////
    public synchronized void insertarNuevoMensajeCorreo(ItemMensajeCorreo mensajeCorreo){
        if(!existeMensajeCorreo(mensajeCorreo.getId())){
            SQLiteDatabase db = conexionBD.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(BDConstantes.MENSAJE_CORREO_CAMPO_ID, mensajeCorreo.getId());
            values.put(BDConstantes.MENSAJE_CORREO_CAMPO_UID, ""+mensajeCorreo.getUid());
            values.put(BDConstantes.MENSAJE_CORREO_CAMPO_ES_MIO, mensajeCorreo.isEsMio()?1:0);
            values.put(BDConstantes.MENSAJE_CORREO_CAMPO_ES_NUEVO, mensajeCorreo.isEsNuevo()?1:0);
            values.put(BDConstantes.MENSAJE_CORREO_CAMPO_ES_FAVORITO, mensajeCorreo.isEsFavorito()?1:0);
            values.put(BDConstantes.MENSAJE_CORREO_CAMPO_CORREO, mensajeCorreo.getCorreo());
            values.put(BDConstantes.MENSAJE_CORREO_CAMPO_REMITENTE, mensajeCorreo.getRemitente());
            values.put(BDConstantes.MENSAJE_CORREO_CAMPO_NOMBRE, mensajeCorreo.getNombre());
            values.put(BDConstantes.MENSAJE_CORREO_CAMPO_DESTINATARIO, mensajeCorreo.getDestinatario());
            values.put(BDConstantes.MENSAJE_CORREO_CAMPO_ASUNTO, mensajeCorreo.getAsunto());
            values.put(BDConstantes.MENSAJE_CORREO_CAMPO_TEXTO, mensajeCorreo.getTexto());
            values.put(BDConstantes.MENSAJE_CORREO_CAMPO_ESTADO, mensajeCorreo.getEstado());
            values.put(BDConstantes.MENSAJE_CORREO_CAMPO_ES_RESPONDIDO, mensajeCorreo.isEsRespondido()?1:0);
            values.put(BDConstantes.MENSAJE_CORREO_CAMPO_PESO, mensajeCorreo.getPeso());
            values.put(BDConstantes.MENSAJE_CORREO_CAMPO_HORA, mensajeCorreo.getHora());
            values.put(BDConstantes.MENSAJE_CORREO_CAMPO_FECHA, mensajeCorreo.getFecha());
            values.put(BDConstantes.MENSAJE_CORREO_CAMPO_ORDEN, mensajeCorreo.getOrden());

            db.insert(BDConstantes.TABLA_MENSAJE_CORREO, BDConstantes.MENSAJE_CORREO_CAMPO_ID, values);
            db.close();
        }
    }

    public synchronized void modificarMensajeCorreo(ItemMensajeCorreo mensajeCorreo){
        if(existeMensajeCorreo(mensajeCorreo.getId())){
            SQLiteDatabase db=conexionBD.getWritableDatabase();
            String[] parametros={mensajeCorreo.getId()};
            ContentValues values=new ContentValues();

            values.put(BDConstantes.MENSAJE_CORREO_CAMPO_ID, mensajeCorreo.getId());

            db.update(BDConstantes.TABLA_MENSAJE_CORREO,values,BDConstantes.MENSAJE_CORREO_CAMPO_ID+"=?",parametros);
            db.close();
        }
    }

    public boolean existeMensajeCorreo(String id){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={id};
        String [] campos={BDConstantes.MENSAJE_CORREO_CAMPO_ID};

        Cursor cursor;
        boolean exist=false;
        try {
            cursor=db.query(BDConstantes.TABLA_MENSAJE_CORREO,campos,
                    BDConstantes.MENSAJE_CORREO_CAMPO_ID+"=? LIMIT 1",parametros,null,null,null);
            if(cursor.getCount()>0) exist=cursor.moveToFirst();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
            exist=true;
        }
        db.close();
        return exist;
    }

    public ArrayList<ItemMensajeCorreo> obtenerMensajeFavoritos(){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={"1"};
        String [] campos={"*"};
        ArrayList<ItemMensajeCorreo> datos = new ArrayList<>();

        Cursor cursor;
        try {
            cursor=db.query(BDConstantes.TABLA_MENSAJE_CORREO,campos,
                    BDConstantes.MENSAJE_CORREO_CAMPO_ES_FAVORITO+"=?"
                            + " ORDER BY "+BDConstantes.MENSAJE_CORREO_CAMPO_ORDEN + " DESC"
                    ,parametros,null,null,null);
            if(cursor.getCount()==0){
                cursor.close();
                db.close();
                return datos;
            }
            while (cursor.moveToNext()){
                datos.add(Convertidor.createItemMensajeCorreoOfCursor(cursor));
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return datos;
    }

    public ArrayList<ItemMensajeCorreo> obtenerMensajeCorreo(boolean enviado){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={enviado?"1":"0"};
        String [] campos={"*"};
        ArrayList<ItemMensajeCorreo> datos = new ArrayList<>();

        Cursor cursor;
        try {
            cursor=db.query(BDConstantes.TABLA_MENSAJE_CORREO,campos,
                    BDConstantes.MENSAJE_CORREO_CAMPO_ES_MIO+"=?"
                            + " ORDER BY "+BDConstantes.MENSAJE_CORREO_CAMPO_ORDEN + " DESC"
                    ,parametros,null,null,null);
            if(cursor.getCount()==0){
                cursor.close();
                db.close();
                return datos;
            }
            while (cursor.moveToNext()){
                datos.add(Convertidor.createItemMensajeCorreoOfCursor(cursor));
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return datos;
    }

    public ArrayList<ItemMensajeCorreo> obtenerMensajeCorreoDe(String correo){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={correo};
        String [] campos={"*"};
        ArrayList<ItemMensajeCorreo> datos = new ArrayList<>();

        Cursor cursor;
        try {
            cursor=db.query(BDConstantes.TABLA_MENSAJE_CORREO,campos,
                    BDConstantes.MENSAJE_CORREO_CAMPO_CORREO+"=?"
                            + " ORDER BY "+BDConstantes.MENSAJE_CORREO_CAMPO_ORDEN + " DESC"
                    ,parametros,null,null,null);
            if(cursor.getCount()==0){
                cursor.close();
                db.close();
                return datos;
            }
            while (cursor.moveToNext()){
                datos.add(Convertidor.createItemMensajeCorreoOfCursor(cursor));
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return datos;
    }

    public int obtenerCantMensajeCorreoNoVistoDe(String correo){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={"1",correo};
        String [] campos={BDConstantes.MENSAJE_CORREO_CAMPO_ES_NUEVO};
        int cant = 0;

        Cursor cursor;
        try {
            cursor=db.query(BDConstantes.TABLA_MENSAJE_CORREO,campos,
                    BDConstantes.MENSAJE_CORREO_CAMPO_ES_NUEVO+"=? AND "+
                            BDConstantes.MENSAJE_CORREO_CAMPO_CORREO+"=?"
                    ,parametros,null,null,null);
            cant = cursor.getCount();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return cant;
    }

    public synchronized void marcarComoVistoCorreosNoVistosDe(String correo){
        ArrayList<ItemMensajeCorreo> listaDatos = obtenerMensajeCorreoNoVistoDe(correo);
        int l = listaDatos.size();
        if(l>0){
            SQLiteDatabase db=conexionBD.getWritableDatabase();
            for(int i=0; i<l; i++){
                String[] parametros={listaDatos.get(i).getId()};
                ContentValues values=new ContentValues();
                values.put(BDConstantes.MENSAJE_CORREO_CAMPO_ES_NUEVO, "0");
                db.update(BDConstantes.TABLA_MENSAJE_CORREO,values,BDConstantes.MENSAJE_CORREO_CAMPO_ID+"=?",parametros);
            }
            db.close();
        }
    }

    public synchronized void marcarComoVistoCorreoNoVistosPor(String idMensajeCorreo){
        SQLiteDatabase db=conexionBD.getWritableDatabase();
        String[] parametros={idMensajeCorreo};
        ContentValues values=new ContentValues();
        values.put(BDConstantes.MENSAJE_CORREO_CAMPO_ES_NUEVO, "0");
        db.update(BDConstantes.TABLA_MENSAJE_CORREO,values,
                BDConstantes.MENSAJE_CORREO_CAMPO_ID+"=?",parametros);
        db.close();
    }

    private ArrayList<ItemMensajeCorreo> obtenerMensajeCorreoNoVistoDe(String correo){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={"1",correo};
        String [] campos={"*"};
        ArrayList<ItemMensajeCorreo> datos = new ArrayList<>();

        Cursor cursor;
        try {
            cursor=db.query(BDConstantes.TABLA_MENSAJE_CORREO,campos,
                    BDConstantes.MENSAJE_CORREO_CAMPO_ES_NUEVO+"=? AND "+
                            BDConstantes.MENSAJE_CORREO_CAMPO_CORREO+"=?"
                    ,parametros,null,null,null);
            if(cursor.getCount()==0){
                cursor.close();
                db.close();
                return datos;
            }
            while (cursor.moveToNext()){
                datos.add(Convertidor.createItemMensajeCorreoOfCursor(cursor));
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return datos;
    }

    public String existeMensajeCorreoAntiguoDameCorreo(String idMsg){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={idMsg};
        String [] campos={BDConstantes.MENSAJE_CORREO_CAMPO_CORREO};

        Cursor cursor;
        String existMsg="";
        try {
            cursor=db.query(BDConstantes.TABLA_MENSAJE_CORREO,campos,
                    BDConstantes.MENSAJE_CORREO_CAMPO_ID+"=? LIMIT 1",parametros,null,null,null);
            if(cursor.getCount()>0){
                cursor.moveToFirst();
                existMsg=cursor.getString(0);
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return existMsg;
    }

    public int obtenerCantMensajeCorreoNoVistoTotal(){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={"1"};
        String [] campos={BDConstantes.MENSAJE_CORREO_CAMPO_ES_NUEVO};
        int cant = 0;

        Cursor cursor;
        try {
            cursor=db.query(BDConstantes.TABLA_MENSAJE_CORREO,campos,
                    BDConstantes.MENSAJE_CORREO_CAMPO_ES_NUEVO+"=?"
                    ,parametros,null,null,null);
            cant = cursor.getCount();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return cant;
    }

    public synchronized void modificarEstadoMensajeCorreo(String idMsg, int estado){
        if(existeMensajeCorreo(idMsg)){
            SQLiteDatabase db=conexionBD.getWritableDatabase();
            String[] parametros={idMsg};
            ContentValues values=new ContentValues();
            values.put(BDConstantes.MENSAJE_CORREO_CAMPO_ESTADO,estado);

            db.update(BDConstantes.TABLA_MENSAJE_CORREO,values,
                    BDConstantes.MENSAJE_CORREO_CAMPO_ID+"=?",parametros);
            db.close();
        }
    }

    public synchronized void modificarFavoritoMensajeCorreo(String idMsg, boolean esFav){
        if(existeMensajeCorreo(idMsg)){
            SQLiteDatabase db=conexionBD.getWritableDatabase();
            String[] parametros={idMsg};
            ContentValues values=new ContentValues();
            values.put(BDConstantes.MENSAJE_CORREO_CAMPO_ES_FAVORITO, esFav?1:0);

            db.update(BDConstantes.TABLA_MENSAJE_CORREO,values,
                    BDConstantes.MENSAJE_CORREO_CAMPO_ID+"=?",parametros);
            db.close();
        }
    }

    public ItemMensajeCorreo obtenerMensajeCorreo(String id){
        ItemMensajeCorreo mensajeCorreo= null;
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={id};
        String [] campos={"*"};

        Cursor cursor;
        try {
            cursor=db.query(BDConstantes.TABLA_MENSAJE_CORREO,campos,
                    BDConstantes.MENSAJE_CORREO_CAMPO_ID+"=? LIMIT 1",parametros,null,null,null);
            if(cursor.getCount()>0){
                cursor.moveToFirst();
                mensajeCorreo=Convertidor.createItemMensajeCorreoOfCursor(cursor);
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return mensajeCorreo;
    }

    public String obtenerNombreMensajeCorreo(String id, String correo){
        String nombre = correo;
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={id};
        String [] campos={BDConstantes.MENSAJE_CORREO_CAMPO_NOMBRE};

        Cursor cursor;
        try {
            cursor=db.query(BDConstantes.TABLA_MENSAJE_CORREO,campos,
                    BDConstantes.MENSAJE_CORREO_CAMPO_ID+"=? LIMIT 1",parametros,null,null,null);
            if(cursor.getCount()>0){
                cursor.moveToFirst();
                nombre = cursor.getString(0);
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return nombre;
    }

    public synchronized void eliminarMensajeCorreo(String id){
        SQLiteDatabase db=conexionBD.getWritableDatabase();
        String [] parametros={id};
        db.delete(BDConstantes.TABLA_MENSAJE_CORREO,BDConstantes.MENSAJE_CORREO_CAMPO_ID+"=?",parametros);
        db.close();
        eliminarAdjuntosCorreosPorIdDe(id);
    }

    public synchronized void eliminarTodosMensajesCorreosDe(String correo){
        SQLiteDatabase db=conexionBD.getWritableDatabase();
        String [] parametros={correo};
        db.delete(BDConstantes.TABLA_MENSAJE_CORREO,BDConstantes.MENSAJE_CORREO_CAMPO_CORREO+"=?",parametros);
        db.close();
        eliminarAdjuntosCorreosPorCorreoDe(correo);
    }

    //////////////////////////////////////////ItemAdjuntoCorreo////////////////////////////////////
    public synchronized void insertarNuevoAdjuntoCorreo(ItemAdjuntoCorreo adjuntoCorreo){
        if(!existeAdjuntoCorreo(adjuntoCorreo.getId())){
            SQLiteDatabase db = conexionBD.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(BDConstantes.ADJUNTO_CORREO_CAMPO_ID, adjuntoCorreo.getId());
            values.put(BDConstantes.ADJUNTO_CORREO_CAMPO_ID_MENSAJE, adjuntoCorreo.getId_mensaje());
            values.put(BDConstantes.ADJUNTO_CORREO_CAMPO_CORREO, adjuntoCorreo.getCorreo());
            values.put(BDConstantes.ADJUNTO_CORREO_CAMPO_POSICION, adjuntoCorreo.getPosicion());
            values.put(BDConstantes.ADJUNTO_CORREO_CAMPO_NOMBRE, adjuntoCorreo.getNombre());
            values.put(BDConstantes.ADJUNTO_CORREO_CAMPO_TIPO, adjuntoCorreo.getTipo());
            values.put(BDConstantes.ADJUNTO_CORREO_CAMPO_PESO, adjuntoCorreo.getPeso());

            db.insert(BDConstantes.TABLA_ADJUNTO_CORREO, BDConstantes.ADJUNTO_CORREO_CAMPO_ID, values);
            db.close();
        }
    }

    public boolean existeAdjuntoCorreo(String id){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={id};
        String [] campos={BDConstantes.ADJUNTO_CORREO_CAMPO_ID};

        Cursor cursor;
        boolean exist=false;
        try {
            cursor=db.query(BDConstantes.TABLA_ADJUNTO_CORREO,campos,
                    BDConstantes.ADJUNTO_CORREO_CAMPO_ID+"=? LIMIT 1",parametros,null,null,null);
            if(cursor.getCount()>0) exist=cursor.moveToFirst();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
            exist=false;
        }
        db.close();
        return exist;
    }

    public ItemAdjuntoCorreo obtenerAdjuntosCorreo(String id){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={id};
        String [] campos={"*"};

        Cursor cursor;
        ItemAdjuntoCorreo dato = null;
        try {
            cursor=db.query(BDConstantes.TABLA_ADJUNTO_CORREO,campos,
                    BDConstantes.ADJUNTO_CORREO_CAMPO_ID+"=? LIMIT 1",parametros,null,null,null);
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                dato = Convertidor.createItemAdjuntoCorreoOfCursor(cursor);
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return dato;
    }

    public ArrayList<ItemAdjuntoCorreo> obtenerAdjuntosCorreoDe(String idMensajeCorreo){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={idMensajeCorreo};
        String [] campos={"*"};
        ArrayList<ItemAdjuntoCorreo> datos = new ArrayList<>();

        Cursor cursor;
        try {
            cursor=db.query(BDConstantes.TABLA_ADJUNTO_CORREO,campos,
                    BDConstantes.ADJUNTO_CORREO_CAMPO_ID_MENSAJE+"=?"
                    ,parametros,null,null,null);
            if(cursor.getCount()==0){
                cursor.close();
                db.close();
                return datos;
            }
            while (cursor.moveToNext()){
                datos.add(Convertidor.createItemAdjuntoCorreoOfCursor(cursor));
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return datos;
    }

    public synchronized void eliminarAdjuntosCorreosPorIdDe(String idMensajeCorreo){
        SQLiteDatabase db=conexionBD.getWritableDatabase();
        String [] parametros={idMensajeCorreo};
        db.delete(BDConstantes.TABLA_ADJUNTO_CORREO,BDConstantes.ADJUNTO_CORREO_CAMPO_ID_MENSAJE+"=?",parametros);
        db.close();
    }

    public synchronized void eliminarAdjuntosCorreosPorCorreoDe(String correo){
        SQLiteDatabase db=conexionBD.getWritableDatabase();
        String [] parametros={correo};
        db.delete(BDConstantes.TABLA_ADJUNTO_CORREO,BDConstantes.ADJUNTO_CORREO_CAMPO_CORREO+"=?",parametros);
        db.close();
    }

    //////////////////////////////////////////ItemPost////////////////////////////////////
    public synchronized void insertarNuevoPost(ItemPost post){
        if(!existePost(post.getId())){
            SQLiteDatabase db = conexionBD.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(BDConstantes.POST_CAMPO_ID, post.getId());
            values.put(BDConstantes.POST_CAMPO_NOMBRE, post.getNombre());
            values.put(BDConstantes.POST_CAMPO_CORREO, post.getCorreo());
            values.put(BDConstantes.POST_CAMPO_TIPO_USUARIO, post.getTipo_usuario());
            values.put(BDConstantes.POST_CAMPO_ICONO, post.getIcono());

            values.put(BDConstantes.POST_CAMPO_TEXTO, post.getTexto());
            values.put(BDConstantes.POST_CAMPO_HORA, post.getHora());
            values.put(BDConstantes.POST_CAMPO_FECHA, post.getFecha());
            values.put(BDConstantes.POST_CAMPO_ORDEN, post.getOrden());

            db.insert(BDConstantes.TABLA_POST, BDConstantes.POST_CAMPO_ID, values);
            db.close();
        }
    }

    public boolean existePost(String id){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={id};
        String [] campos={BDConstantes.POST_CAMPO_ID};

        Cursor cursor;
        boolean exist=false;
        try {
            cursor=db.query(BDConstantes.TABLA_POST,campos,
                    BDConstantes.POST_CAMPO_ID+"=? LIMIT 1",parametros,null,null,null);
            if(cursor.getCount()>0) exist=cursor.moveToFirst();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
            exist=false;
        }
        db.close();
        return exist;
    }

    public ItemPost obtenerPost(String id){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={id};
        String [] campos={"*"};

        Cursor cursor;
        ItemPost post=null;
        try {
            cursor=db.query(BDConstantes.TABLA_POST,campos,
                    BDConstantes.POST_CAMPO_ID+"=? LIMIT 1",parametros,null,null,null);
            if(cursor.getCount()>0){
                cursor.moveToFirst();
                post = Convertidor.createItemPostOfCursor(cursor);
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return post;
    }

    public ArrayList<ItemPost> obtenerPosts(){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        ArrayList<ItemPost> datos = new ArrayList<>();
        Cursor cursor;
        try{
            cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_POST
                    + " ORDER BY "+BDConstantes.POST_CAMPO_ORDEN + " DESC", null);

            if(cursor.getCount()==0){
                cursor.close();
                db.close();
                return datos;
            }
            while (cursor.moveToNext()){
                datos.add(Convertidor.createItemPostOfCursor(cursor));
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return datos;
    }

    public int obtenerCantTotalPosts(){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        Cursor cursor;
        int cant = 0;
        try{
            cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_POST, null);
            cant = cursor.getCount();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
            db.close();
            return obtenerCantTotalPosts();
        }
        db.close();
        return cant;
    }

    public synchronized void eliminarPost(String id){
        SQLiteDatabase db=conexionBD.getWritableDatabase();
        String [] parametros={id};
        db.delete(BDConstantes.TABLA_POST,BDConstantes.POST_CAMPO_ID+"=?",parametros);
        db.close();
    }

    //////////////////////////////////////////BloquedosPost////////////////////////////////////
    public synchronized void insertarNuevoBloqueadoPost(String correo){
        if(!existeBloqueadoPost(correo)){
            SQLiteDatabase db=conexionBD.getWritableDatabase();
            ContentValues values=new ContentValues();
            values.put(BDConstantes.LISTA_NEGRA_POST_CAMPO_ID,correo);
            db.insert(BDConstantes.TABLA_LISTA_NEGRA_POST,BDConstantes.LISTA_NEGRA_POST_CAMPO_ID,values);
            db.close();
        }
    }

    public boolean existeBloqueadoPost(String correo, boolean defecto){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={correo};
        String [] campos={BDConstantes.LISTA_NEGRA_POST_CAMPO_ID};

        Cursor cursor;
        boolean exist=false;
        try {
            cursor=db.query(BDConstantes.TABLA_LISTA_NEGRA_POST,campos,
                    BDConstantes.LISTA_NEGRA_POST_CAMPO_ID+"=? LIMIT 1",parametros,null,null,null);
            if(cursor.getCount()>0) exist=cursor.moveToFirst();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
            exist=defecto;
        }
        db.close();
        return exist;
    }

    public boolean existeBloqueadoPost(String correo){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        String [] parametros={correo};
        String [] campos={BDConstantes.LISTA_NEGRA_POST_CAMPO_ID};

        Cursor cursor;
        boolean exist=false;
        try {
            cursor=db.query(BDConstantes.TABLA_LISTA_NEGRA_POST,campos,
                    BDConstantes.LISTA_NEGRA_POST_CAMPO_ID+"=? LIMIT 1",parametros,null,null,null);
            if(cursor.getCount()>0) exist=cursor.moveToFirst();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
            exist=true;
        }
        db.close();
        return exist;
    }

    public synchronized void eliminarBloqueadoPost(String correo){
        SQLiteDatabase db=conexionBD.getWritableDatabase();
        String [] parametros={correo};
        db.delete(BDConstantes.TABLA_LISTA_NEGRA_POST,
                BDConstantes.LISTA_NEGRA_POST_CAMPO_ID+"=?",parametros);
        db.close();
    }

    public ArrayList<String> obtenerBloqueadosPost(){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        ArrayList<String> datos = new ArrayList<>();
        Cursor cursor;
        try{
            cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_LISTA_NEGRA_POST, null);

            if(cursor.getCount()==0){
                cursor.close();
                db.close();
                return datos;
            }
            while (cursor.moveToNext()){
                datos.add(cursor.getString(0));
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return datos;
    }

    //////////////////////////////////////////DescripcionError////////////////////////////////////
    public synchronized void insertarDescripcionError(String msg_pub, String msg_piv){
        SQLiteDatabase db=conexionBD.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(BDConstantes.DESCRIPCION_ERROR_CAMPO_MENSAJE_PUBLICO,msg_pub);
        values.put(BDConstantes.DESCRIPCION_ERROR_CAMPO_MENSAJE_PRIVADO,msg_piv);
        db.insert(BDConstantes.TABLA_DESCRIPCION_ERROR,BDConstantes.DESCRIPCION_ERROR_CAMPO_MENSAJE_PUBLICO,values);
        db.close();
    }

    public synchronized void eliminarDescripcionesError(){
        SQLiteDatabase db=conexionBD.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_DESCRIPCION_ERROR);
        db.execSQL(BDConstantes.CREAR_TABLA_DESCRIPCION_ERROR);
        db.close();
    }

    public ArrayList<String> obtenerPrimeraDescripcionError(){
        SQLiteDatabase db=conexionBD.getReadableDatabase();
        ArrayList<String> datos = new ArrayList<>();
        Cursor cursor;
        try{
            cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_DESCRIPCION_ERROR
                    +" LIMIT 1", null);

            if(cursor.getCount()==0){
                cursor.close();
                db.close();
                return datos;
            }
            cursor.moveToFirst();
            datos.add(cursor.getString(0));
            datos.add(cursor.getString(1));
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
        return datos;
    }
}
