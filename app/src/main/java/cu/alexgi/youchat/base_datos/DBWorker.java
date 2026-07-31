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
import cu.alexgi.youchat.items.ItemAtajo;
import cu.alexgi.youchat.items.ItemChat;
import cu.alexgi.youchat.items.ItemComentarioPost;
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

public class DBWorker {

    private Context context;
    private ConexionBD conexionBD;
    private final int nuevaVersion=8;
    private SQLiteDatabase db;

    public DBWorker(Context c){
        context = c;
        if(YouChatApplication.version_bd==nuevaVersion)
            conexionBD=new ConexionBD(context,BDConstantes.NOMBRE_BASE_DATOS,null, nuevaVersion);
        else {
            conexionBD=new ConexionBD(context,BDConstantes.NOMBRE_BASE_DATOS,null, nuevaVersion);
            conexionBD.onUpgrade(conexionBD.getWritableDatabase(),YouChatApplication.version_bd,nuevaVersion);
        }
        db = conexionBD.getWritableDatabase();

//        db.execSQL(BDConstantes.CREAR_TABLA_COMENTARIO_POST);
    }

    //////////////////////////////////////////VERSIONBD////////////////////////////////////
    public synchronized void insertarVersionBD(int version){
        db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_VERSION_BD);
        db.execSQL(BDConstantes.CREAR_TABLA_VERSION_BD);
        ContentValues values = new ContentValues();
        values.put(BDConstantes.VERSION_BD_CAMPO_VERSION, version);
        db.insert(BDConstantes.TABLA_VERSION_BD, BDConstantes.VERSION_BD_CAMPO_VERSION, values);
    }

    public int obtenerVersionBD(){
        db.execSQL(BDConstantes.CREAR_TABLA_VERSION_BD);
        int version = 5;
        Cursor cursor;
        try{
            cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_VERSION_BD, null);
            if(cursor==null || cursor.getCount()==0){
                cursor.close();
                return version;
            }
            cursor.moveToFirst();
            version=cursor.getInt(0);
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return version;
    }
    //////////////////////////////////////////CONTACTO////////////////////////////////////
    public synchronized void insertarNuevoContacto(ItemContacto contacto){
        if(!existeContacto(contacto.getCorreo(), true)){
            
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
            
        }
        else modificarContacto(contacto);
    }

    public synchronized void insertarNuevoContactoNoVisible(ItemContacto contacto, boolean comoDeYC){
        if(!existeContacto(contacto.getCorreo(), true)){
            
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
            
        } else if(comoDeYC)
            modificarUsoYCContacto(contacto.getCorreo(), 1);
    }

    public synchronized void modificarTipoContacto(String correo, int tipo){
        
        String[] parametros={correo};
        ContentValues values=new ContentValues();
        values.put(BDConstantes.CONTACTO_CAMPO_TIPO,tipo);

        db.update(BDConstantes.TABLA_CONTACTO,values,BDConstantes.CONTACTO_CAMPO_CORREO+"=?",parametros);
        
    }

    public synchronized void modificarUsoYCContacto(String correo, int uso){
        
        String[] parametros={correo};
        ContentValues values=new ContentValues();
        values.put(BDConstantes.CONTACTO_CAMPO_USA_YOUCHAT,uso);

        db.update(BDConstantes.TABLA_CONTACTO,values,BDConstantes.CONTACTO_CAMPO_CORREO+"=?",parametros);
        
    }

    public synchronized void modificarContacto(ItemContacto contacto){
        
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
        
    }

    public synchronized boolean actualizarContacto(ItemContacto contacto){
        if(existeContacto(contacto.getCorreo(), true)){
            
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
            
            return true;
        }
        else insertarNuevoContacto(contacto);
        return false;
    }

    public synchronized void actualizarUltHoraFechaDe(String contacto_correo, String ultHora, String ultFecha) {
        if(existeContacto(contacto_correo, true)){
            

            String [] parametros={contacto_correo};
            ContentValues values=new ContentValues();
            if(!ultHora.equals(""))
                values.put(BDConstantes.CONTACTO_CAMPO_HORA_ULT_CONEXION,ultHora);
            if(!ultFecha.equals(""))
                values.put(BDConstantes.CONTACTO_CAMPO_FECHA_ULT_CONEXION,ultFecha);

            if(values.size()>0)
                db.update(BDConstantes.TABLA_CONTACTO,values,BDConstantes.CONTACTO_CAMPO_CORREO+"=?",parametros);
            
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
            

            String [] parametros={contacto_correo};
            ContentValues values=new ContentValues();
            if(!ultHora.equals(""))
                values.put(BDConstantes.CONTACTO_CAMPO_HORA_ULT_CONEXION,ultHora);
            if(!ultFecha.equals(""))
                values.put(BDConstantes.CONTACTO_CAMPO_FECHA_ULT_CONEXION,ultFecha);

            if(values.size()>0)
                db.update(BDConstantes.TABLA_CONTACTO,values,BDConstantes.CONTACTO_CAMPO_CORREO+"=?",parametros);
            
        }
    }

    public synchronized void actualizarCantSeguidoresDe(String contacto_correo, int cant_seguidores) {
        if(existeContacto(contacto_correo, true)){
            
            String [] parametros={contacto_correo};
            ContentValues values=new ContentValues();
            values.put(BDConstantes.CONTACTO_CAMPO_CANT_SEGUIDORES,cant_seguidores);

            db.update(BDConstantes.TABLA_CONTACTO,values,BDConstantes.CONTACTO_CAMPO_CORREO+"=?",parametros);
            
        }
    }

    public synchronized void actualizarNombrePersonalDe(String contacto_correo, String nombre)
    {
        if(nombre.equals(""))
            nombre=contacto_correo;
        if(existeContacto(contacto_correo, true)){
            

            String [] parametros={contacto_correo};
            ContentValues values=new ContentValues();
            values.put(BDConstantes.CONTACTO_CAMPO_NOMBRE_PERSONAL,nombre);
            values.put(BDConstantes.CONTACTO_CAMPO_NOMBRE_ORDENAR,nombre.toLowerCase());

            db.update(BDConstantes.TABLA_CONTACTO,values,BDConstantes.CONTACTO_CAMPO_CORREO+"=?",parametros);
            
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
            

            String [] parametros={contacto_correo};
            ContentValues values=new ContentValues();
            values.put(BDConstantes.CONTACTO_CAMPO_SILENCIADO,silenciado?1:0);

            db.update(BDConstantes.TABLA_CONTACTO,values,BDConstantes.CONTACTO_CAMPO_CORREO+"=?",parametros);
            
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
            

            String [] parametros={contacto_correo};
            ContentValues values=new ContentValues();
            values.put(BDConstantes.CONTACTO_CAMPO_BLOQUEADO,bloqueado?1:0);

            db.update(BDConstantes.TABLA_CONTACTO,values,BDConstantes.CONTACTO_CAMPO_CORREO+"=?",parametros);
            
        }
        else {
            ItemContacto contacto=new ItemContacto(contacto_correo,contacto_correo);
            contacto.setBloqueado(bloqueado);
            insertarNuevoContacto(contacto);
        }
    }

    public boolean existeContacto(String contacto_correo, boolean defecto){
        
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
        
        return exist;
    }

    public ArrayList<ItemContacto> obtenerContactosOrdenadosXNombre(boolean ordenarXNombre){
        
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
        
        return datos_Contacto;
    }

    public ArrayList<ItemContacto> obtenerContactosBloqueados(){

        ArrayList<ItemContacto> datos_Contacto = new ArrayList<>();
        Cursor cursor;
        try{
            cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_CONTACTO+
                    " WHERE "+BDConstantes.CONTACTO_CAMPO_BLOQUEADO+" ='1' ORDER BY "+BDConstantes.CONTACTO_CAMPO_CORREO+" ASC", null);
            if(cursor.getCount()==0){
                cursor.close();

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

        return datos_Contacto;
    }

    public String obtenerContactosBloqueadosString(){
        String datos = "";
        String [] parametros={"1"};
        String [] campos={BDConstantes.CONTACTO_CAMPO_CORREO};
        Cursor cursor;
        try {
            cursor=db.query(BDConstantes.TABLA_CONTACTO,campos,
                    BDConstantes.CONTACTO_CAMPO_BLOQUEADO+"=?",parametros,null,null,null);
            if(cursor.getCount()==0){
                cursor.close();
                return "";
            }
            while (cursor.moveToNext()){
                if(!datos.isEmpty()) datos+=",";
                datos = cursor.getString(0);
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return datos;
    }

    public ItemContacto obtenerContacto(String correo){
        ItemContacto contacto = null;
        
        String [] parametros={correo};
        String [] campos={"*"};

        Cursor cursor;
        try {
//            cursor=db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_CONTACTO+
//                    " WHERE "+BDConstantes.CONTACTO_CAMPO_NOMBRE_ORDENAR+" ASC", null);
            cursor=db.query(BDConstantes.TABLA_CONTACTO,campos,
                    BDConstantes.CONTACTO_CAMPO_CORREO+"=? LIMIT 1",parametros,null,null,null);
            if(cursor.getCount()>0){
                cursor.moveToFirst();
                contacto=Convertidor.createItemContactoOfCursor(cursor);
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        
        return contacto;
    }

    public String[] obtenerUltHorayFecha(String contacto_correo){
        String[] ultHoraFecha = new String[3];
        ultHoraFecha[0]=ultHoraFecha[1]="";
        
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
        
        return ultHoraFecha;
    }

    public String obtenerNombre(String contacto_correo){
        if(contacto_correo.equals(YouChatApplication.idOficial))
            return "YouChat Oficial";
        String nombre="";
        
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
        
        if(nombre.equals(""))
            nombre=contacto_correo;
        return nombre;
    }

    public int obtenerTipoContacto(String contacto_correo){
        int tipo=ItemContacto.TIPO_CONTACTO_INVISIBLE;
        
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
        
        return tipo;
    }

    public int obtenerVersionContacto(String contacto_correo){
        int version=0;
        
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
        
        return version;
    }

    public boolean estaSilenciado(String contacto_correo){
        boolean silenciado=false;
        
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
        
        return silenciado;
    }

    public boolean estaBloqueado(String contacto_correo){
        boolean bloqueado=false;
        
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
        
        return bloqueado;
    }

    public String obtenerRutaImg(String contacto_correo){
        String ruta="";
        
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
        
        return ruta;
    }

    public synchronized void eliminarContacto(String contacto_correo){
        
        String [] parametros={contacto_correo};
        db.delete(BDConstantes.TABLA_CONTACTO,BDConstantes.CONTACTO_CAMPO_CORREO+"=?",parametros);
        db.delete(BDConstantes.TABLA_USUARIO,BDConstantes.USUARIO_CAMPO_CORREO+"=?",parametros);
        db.delete(BDConstantes.TABLA_CHAT,BDConstantes.CHAT_CAMPO_CORREO+"=?",parametros);
        db.delete(BDConstantes.TABLA_SEGUIDOR,BDConstantes.SEGUIDOR_CAMPO_ID+"=?",parametros);
        db.delete(BDConstantes.TABLA_ESTADO,BDConstantes.ESTADO_CAMPO_CORREO+"=?",parametros);
        db.delete(BDConstantes.TABLA_REACCION_ESTADO,BDConstantes.REACCION_CAMPO_CORREO+"=?",parametros);
        
    }

    //////////////////////////////////////////USUARIO////////////////////////////////////
    public ArrayList<ItemUsuario> obtenerUsuarios(){
        
        ArrayList<ItemUsuario> datos_Usuario= new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_USUARIO+" ORDER BY "+
                    BDConstantes.USUARIO_CAMPO_ORDEN_ULT_MSG+" DESC", null);
            if(cursor.getCount()==0){
                cursor.close();
                
                return datos_Usuario;
            }
            while (cursor.moveToNext()) datos_Usuario.add(Convertidor.createItemUsuarioOfCursor(cursor));
            cursor.close();
            
        }catch (Exception e){
            e.printStackTrace();
        }
        return datos_Usuario;
    }

    public ItemUsuario obtenerUsuario(String correo){
        
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
        
        return usuario;
    }

    public ArrayList<ItemUsuario> obtenerUsuariosOrdenadosPorAnclados(){
        Log.e("*****DBWorker*****","obtenerUsuarios");
        
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
                
                return datos_Usuario;
            }
            while (cursor.moveToNext()) datos_Usuario.add(Convertidor.createItemUsuarioOfCursor(cursor));
            cursor.close();
            
        }catch (Exception e){
            e.printStackTrace();
            
        }
        return datos_Usuario;
    }

    public synchronized void insertarNuevoUsuario(ItemUsuario usuario){
        if (!existeUsuario(usuario.getCorreo(), true)){
            
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
            
        }
    }

    public boolean existeUsuario(String usuario_correo, boolean defecto){
        
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
        
        return exist;
    }

    public synchronized int cantMsgNoVistos(String usuario_correo){
        int cant=0;
        
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
        
        return cant;
    }

    public synchronized void actualizarCantMensajesNoVistosX(String usuario_correo, int cant) {
        int cantExistentes=cantMsgNoVistos(usuario_correo);
        

        String [] parametros={usuario_correo};
        ContentValues values=new ContentValues();
        values.put(BDConstantes.USUARIO_CAMPO_CANT_MSG,cantExistentes+cant);

        db.update(BDConstantes.TABLA_USUARIO,values,BDConstantes.USUARIO_CAMPO_CORREO+"=?",parametros);
        
    }

    public String obtenerBorradorDe(String usuario_correo){
        String borrador="";
        
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
        
        return borrador;
    }

    public synchronized void actualizarBorrador(String usuario_correo, String borrador) {
        

        String [] parametros={usuario_correo};
        ContentValues values=new ContentValues();
        values.put(BDConstantes.USUARIO_CAMPO_BORRADOR,borrador);

        db.update(BDConstantes.TABLA_USUARIO,values,BDConstantes.USUARIO_CAMPO_CORREO+"=?",parametros);
        
    }

    public synchronized void marcarComoVistoMensajesNoVistos(String usuario_correo){
        

        String [] parametros={usuario_correo};
        ContentValues values=new ContentValues();
        values.put(BDConstantes.USUARIO_CAMPO_CANT_MSG,0);

        db.update(BDConstantes.TABLA_USUARIO,values,BDConstantes.USUARIO_CAMPO_CORREO+"=?",parametros);
        
    }

    public synchronized void eliminarUsuario(String usuario_correo, boolean eliminarUsuario, boolean eliminarChat){
        
        String [] parametros={usuario_correo};
        if(eliminarUsuario) db.delete(BDConstantes.TABLA_USUARIO,BDConstantes.USUARIO_CAMPO_CORREO+"=?",parametros);
        if(eliminarChat) db.delete(BDConstantes.TABLA_CHAT,BDConstantes.CHAT_CAMPO_CORREO+"=?",parametros);
        
    }

    public synchronized void modificarUsuarioAnclado(String usuario_correo, int estAnclado){
        
        String[] parametrosU={usuario_correo};
        ContentValues valuesU=new ContentValues();
        valuesU.put(BDConstantes.USUARIO_CAMPO_ANCLADO,estAnclado); //0 no anclado 1 anclado
        db.update(BDConstantes.TABLA_USUARIO,valuesU,BDConstantes.USUARIO_CAMPO_CORREO+"=?",parametrosU);
        
    }

//    public synchronized void actualizarUltMsgUsuario(String correo, int tipo, int estado, String texto, String fech) {
//        
//        String [] parametros={correo};
//        ContentValues values=new ContentValues();
//        values.put(BDConstantes.USUARIO_CAMPO_TIPO_ULT_MSG,tipo);
//        values.put(BDConstantes.USUARIO_CAMPO_ESTADO_ULT_MSG,estado);
//        values.put(BDConstantes.USUARIO_CAMPO_TEXTO_ULT_MSG,texto);
//        values.put(BDConstantes.USUARIO_CAMPO_ORDEN_ULT_MSG,fech);
//        db.update(BDConstantes.TABLA_USUARIO,values,BDConstantes.USUARIO_CAMPO_CORREO+"=?",parametros);
//        
//    }

    public synchronized void actualizarUltMsgUsuario(ItemChat chat) {
        if(chat!=null){
            actualizarOrdenUsuario(chat.getCorreo(),chat.getOrden());
//            
//            String [] parametros={chat.getCorreo()};
//            ContentValues values=new ContentValues();
//            values.put(BDConstantes.USUARIO_CAMPO_TIPO_ULT_MSG,chat.getTipo_mensaje());
//            values.put(BDConstantes.USUARIO_CAMPO_ESTADO_ULT_MSG,chat.getEstado());
//            values.put(BDConstantes.USUARIO_CAMPO_TEXTO_ULT_MSG,chat.getMensaje());
//            values.put(BDConstantes.USUARIO_CAMPO_ORDEN_ULT_MSG,chat.getOrden());
//            db.update(BDConstantes.TABLA_USUARIO,values,BDConstantes.USUARIO_CAMPO_CORREO+"=?",parametros);
//            
        }
    }

    public synchronized void actualizarOrdenUsuario(String correo, String orden) {
        if(!correo.isEmpty() && !orden.isEmpty()){
            
            String [] parametros={correo};
            ContentValues values=new ContentValues();
            values.put(BDConstantes.USUARIO_CAMPO_ORDEN_ULT_MSG,orden);
            db.update(BDConstantes.TABLA_USUARIO,values,BDConstantes.USUARIO_CAMPO_CORREO+"=?",parametros);
            
        }
    }

    //////////////////////////////////////////CHAT////////////////////////////////////

    public ArrayList<ItemChat> obtenerMsgChat(String correo, int cantAct, int limite){
        
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
        
        return datos_chat;
    }

    public ArrayList<ItemChat> obtenerTodosMsgChat(String correo){
        
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
                
                return datos_chat;
            }

            while (cursor.moveToNext())
                datos_chat.add(Convertidor.createItemChatOfCursor(cursor));
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        
        return datos_chat;
    }

    public ItemChat obtenerUltMsgChatDe(String correo){
        
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
                
                return ultMsg;
            }

            cursor.moveToFirst();
            ultMsg = Convertidor.createItemChatOfCursor(cursor);

            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        
//        if(ultMsg!=null) actualizarUltMsgUsuario(ultMsg);
        return ultMsg;
    }

    public ArrayList<ItemChat> obtenerMsgNoEnviados(){
        
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
            cursor.close();
            Cursor cursor2=db.query(BDConstantes.TABLA_CHAT,campos,
                    BDConstantes.CHAT_CAMPO_ESTADO+"=? ORDER BY "+BDConstantes.CHAT_CAMPO_ORDEN+
                            " ASC",
                    parametros,null,null, null);

            if(cursor2.getCount()>0){
                while (cursor2.moveToNext())
                    datos_chat.add(Convertidor.createItemChatOfCursor(cursor2));
            }
            cursor2.close();
        }catch (Exception e){
            e.printStackTrace();
            return obtenerMsgNoEnviados();
        }
        
        return datos_chat;
    }

    public synchronized void insertarChat(ItemChat chat){
        
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

        values.put(BDConstantes.CHAT_CAMPO_ID_MENSAJE, chat.getId_mensaje());
        values.put(BDConstantes.CHAT_CAMPO_PESO, chat.getPeso());
        values.put(BDConstantes.CHAT_CAMPO_ESTA_DESCARGADO, chat.isDescargado()?"1":"0");

        db.insert(BDConstantes.TABLA_CHAT,BDConstantes.CHAT_CAMPO_ID,values);
        
    }

    public synchronized boolean actualizarChatDescargado(ItemChat chat){
        if(existeChat(chat.getId())){

            String [] parametros={chat.getId()};
            ContentValues values = new ContentValues();

            values.put(BDConstantes.CHAT_CAMPO_TIPO_MENSAJE,chat.getTipo_mensaje());
            values.put(BDConstantes.CHAT_CAMPO_MENSAJE,chat.getMensaje());
            values.put(BDConstantes.CHAT_CAMPO_RUTA_DATO,chat.getRuta_Dato());
            values.put(BDConstantes.CHAT_CAMPO_ID_MSG_RESP, chat.getId_msg_resp());
            values.put(BDConstantes.CHAT_CAMPO_EMISOR,chat.getEmisor());
            values.put(BDConstantes.CHAT_CAMPO_REENVIADO,chat.getReenviado());

            db.update(BDConstantes.TABLA_CHAT,values,BDConstantes.CHAT_CAMPO_ID+"=?",parametros);

            return true;
        }
        else insertarChat(chat);
        return false;
    }

    public synchronized boolean actualizarChatDescargadoNuevo(ItemChat chat){
        if(existeChat(chat.getId())){

            String [] parametros={chat.getId()};
            ContentValues values = new ContentValues();

            values.put(BDConstantes.CHAT_CAMPO_RUTA_DATO,chat.getRuta_Dato());
            values.put(BDConstantes.CHAT_CAMPO_ESTA_DESCARGADO, "1");

            db.update(BDConstantes.TABLA_CHAT,values,BDConstantes.CHAT_CAMPO_ID+"=?",parametros);

            return true;
        }
        else insertarChat(chat);
        return false;
    }

    public synchronized void editarMensajeChat(String idChat, String text){
        
        String [] parametros={idChat};
        ContentValues values = new ContentValues();
        values.put(BDConstantes.CHAT_CAMPO_MENSAJE,text);
        values.put(BDConstantes.CHAT_CAMPO_EDITADO,1);
        db.update(BDConstantes.TABLA_CHAT,values,BDConstantes.CHAT_CAMPO_ID+"=?",parametros);
        
    }

    public boolean existeChat(String idChat){
        
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
        
        return exist;
    }

    public boolean estaDescargadoChat(String idChat){

        String [] parametros={idChat};
        String [] campos={BDConstantes.CHAT_CAMPO_ESTA_DESCARGADO};

        Cursor cursor;
        boolean exist=false;
        try {
            cursor=db.query(BDConstantes.TABLA_CHAT,campos,
                    BDConstantes.CHAT_CAMPO_ID+"=? LIMIT 1",parametros,null,null,null);
            if(cursor.getCount()>0){
                cursor.moveToFirst();
                exist=cursor.getInt(0)==1;
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
            exist=false;
        }
        return exist;
    }

    public String existeMsgAntiguoDameCorreo(String idMsg){
        
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
        
        return existMsg;
    }

    public boolean existeMsgAntiguo(String idMsg){
        
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
        
        return existMsg;
    }

    public boolean esNecesarioEnviar(String idMsg){
        
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
        
        return esNec;
    }

    public synchronized void eliminarMsg(String idMsg){
        if(!idMsg.isEmpty()){
            
            String [] parametros={idMsg};
            db.delete(BDConstantes.TABLA_CHAT,BDConstantes.CHAT_CAMPO_ID+"=?",parametros);
        }
    }

    public synchronized void eliminarMsgFecha(){
        
        String [] parametros={"0"};
        db.delete(BDConstantes.TABLA_CHAT,BDConstantes.CHAT_CAMPO_TIPO_MENSAJE+"=?",parametros);
        
    }

    public synchronized void modificarEstadoUltMensaje(String correo, String idMsg, int estado){
        
        String[] parametros={idMsg};
        ContentValues values=new ContentValues();
        values.put(BDConstantes.CHAT_CAMPO_ESTADO,estado);

        String[] parametrosU={correo};
        ContentValues valuesU=new ContentValues();
        valuesU.put(BDConstantes.USUARIO_CAMPO_ESTADO_ULT_MSG,estado);

        db.update(BDConstantes.TABLA_CHAT,values,BDConstantes.CHAT_CAMPO_ID+"=?",parametros);
        db.update(BDConstantes.TABLA_USUARIO,valuesU,BDConstantes.USUARIO_CAMPO_CORREO+"=?",parametrosU);
        
    }

    public synchronized void modificarEstadoMensaje(String idMsg, int estado){
        if(existeMsgAntiguo(idMsg)){
            
            String[] parametros={idMsg};
            ContentValues values=new ContentValues();
            values.put(BDConstantes.CHAT_CAMPO_ESTADO,estado);

            db.update(BDConstantes.TABLA_CHAT,values,BDConstantes.CHAT_CAMPO_ID+"=?",parametros);
            
        }
    }

    //////////////////////////////////////////ESTADOS////////////////////////////////////

    public synchronized void insertarNuevoEstado(ItemEstado estado){
        if(!existeEstado(estado.getId())){
            
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
            values.put(BDConstantes.ESTADO_CAMPO_ESTA_DESCARGADO, estado.isDescargado()?1:0);
            values.put(BDConstantes.ESTADO_CAMPO_UID, ""+estado.getUid());
            values.put(BDConstantes.ESTADO_CAMPO_ID_MENSAJE, ""+estado.getId_mensaje());
            values.put(BDConstantes.ESTADO_CAMPO_PESO_IMG, estado.getPeso_img());

            db.insert(BDConstantes.TABLA_ESTADO, BDConstantes.ESTADO_CAMPO_ID, values);
            
        }
    }

    public synchronized void actualizarEstadoDescargado(String idNow, String ruta) {
        String [] parametros={idNow};
        ContentValues values=new ContentValues();
        values.put(BDConstantes.ESTADO_CAMPO_RUTA_IMAGEN, ruta);
        values.put(BDConstantes.ESTADO_CAMPO_ESTA_DESCARGADO, 1);
        db.update(BDConstantes.TABLA_ESTADO,values,BDConstantes.ESTADO_CAMPO_ID+"=?",parametros);

    }

    public boolean estaDescargadoEstado(String idEstado){

        String [] parametros={idEstado};
        String [] campos={BDConstantes.ESTADO_CAMPO_ESTA_DESCARGADO};

        Cursor cursor;
        boolean exist=false;
        try {
            cursor=db.query(BDConstantes.TABLA_ESTADO,campos,
                    BDConstantes.ESTADO_CAMPO_ID+"=? LIMIT 1",parametros,null,null,null);
            if(cursor.getCount()>0){
                cursor.moveToFirst();
                exist=cursor.getInt(0)==1;
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
            exist=false;
        }

        return exist;
    }

    public boolean existeEstado(String idEstado){
        
        String [] parametros={idEstado};
        String [] campos={BDConstantes.ESTADO_CAMPO_ID};

        Cursor cursor;
        boolean exist=false;
        try {
            cursor=db.query(BDConstantes.TABLA_ESTADO,campos,
                    BDConstantes.ESTADO_CAMPO_ID+"=? LIMIT 1",parametros,null,null,null);
            if(cursor.getCount()>0) exist=cursor.moveToFirst();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
            exist=false;
        }
        
        return exist;
    }

    public synchronized void eliminarTodosLosEstadosDe(String correo){
        
        String [] parametros={correo};
        db.delete(BDConstantes.TABLA_ESTADO,BDConstantes.ESTADO_CAMPO_CORREO+"=?",parametros);
        
    }
    public synchronized void eliminarElEstadosDe(String idEstado){
        if(!idEstado.isEmpty()){
            
            String [] parametros={idEstado};
            db.delete(BDConstantes.TABLA_ESTADO,BDConstantes.ESTADO_CAMPO_ID+"=?",parametros);

            eliminarTodasLasReaccionesEstadosDeEstado(idEstado);
            eliminarVistaEstadosDe(idEstado);
        }
    }

    public ArrayList<String> obtenerCorreosEstadosNuevos(){
        
        ArrayList<String> seguidores = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_ESTADO+
                    " WHERE "+BDConstantes.ESTADO_CAMPO_ESTA_VISTO+" ='0' ORDER BY "
                    +BDConstantes.ESTADO_CAMPO_ORDEN+" DESC", null);
            if(cursor.getCount()==0){
                cursor.close();
                
                return seguidores;
            }
            while (cursor.moveToNext())
                seguidores.add(cursor.getString(0));
            cursor.close();
            
        }catch (Exception e){
            e.printStackTrace();
        }
        return seguidores;
    }

    public ArrayList<ItemEstado> obtenerEstadosNuevosDe(String correo){
        
        ArrayList<ItemEstado> estados = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_ESTADO+
                    " WHERE "+BDConstantes.ESTADO_CAMPO_ESTA_VISTO+" ='0' AND "+BDConstantes.ESTADO_CAMPO_CORREO
                    +" = \'"+correo+"\' ORDER BY "
                    +BDConstantes.ESTADO_CAMPO_ORDEN+" ASC", null);
            if(cursor.getCount()==0){
                cursor.close();
                
                return estados;
            }
            while (cursor.moveToNext())
                estados.add(Convertidor.createItemEstadoOfCursor(cursor));
            cursor.close();
            
        }catch (Exception e){
            e.printStackTrace();
        }
        return estados;
    }

    public ArrayList<ItemEstado> obtenerEstadosDe(String correo){
        
        ArrayList<ItemEstado> estados = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_ESTADO+
                    " WHERE "+BDConstantes.ESTADO_CAMPO_CORREO + " = \'"+correo+"\' ORDER BY "
                    +BDConstantes.ESTADO_CAMPO_ORDEN+" ASC", null);
            if(cursor.getCount()==0){
                cursor.close();
                
                return estados;
            }
            while (cursor.moveToNext())
                estados.add(Convertidor.createItemEstadoOfCursor(cursor));
            cursor.close();
            
        }catch (Exception e){
            e.printStackTrace();
        }
        return estados;
    }

    public int obtenerCantEstadosDe(String correo){
        int cant = 0;
        String [] parametros={correo};
        String [] campos={BDConstantes.ESTADO_CAMPO_CORREO};
        try {
            Cursor cursor=db.query(BDConstantes.TABLA_ESTADO,campos,
                    BDConstantes.ESTADO_CAMPO_CORREO+"=?",parametros,
                    null,null,null);
            if(cursor!=null) cant = cursor.getCount();
            cursor.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        return cant;
    }

    public ItemEstado obtenerEstado(String idEstado){
        
        ItemEstado estado = null;
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_ESTADO+
                    " WHERE "+BDConstantes.ESTADO_CAMPO_ID + " = \'"+idEstado+"\' ORDER BY "
                    +BDConstantes.ESTADO_CAMPO_ORDEN+" DESC LIMIT 1", null);
            if(cursor.getCount()==0){
                cursor.close();
                
                return null;
            }
            cursor.moveToFirst();
            estado = Convertidor.createItemEstadoOfCursor(cursor);
            cursor.close();
            
        }catch (Exception e){
            e.printStackTrace();
        }
        return estado;
    }

    public ArrayList<ItemEstado> obtenerEstadosNuevos(){
        
        ArrayList<ItemEstado> estadosNuevos = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_ESTADO+
                    " WHERE "+BDConstantes.ESTADO_CAMPO_ESTA_VISTO+" ='0' ORDER BY "
                    +BDConstantes.ESTADO_CAMPO_ORDEN+" DESC", null);
            if(cursor.getCount()==0){
                cursor.close();
                
                return estadosNuevos;
            }
            while (cursor.moveToNext())
                estadosNuevos.add(Convertidor.createItemEstadoOfCursor(cursor));
            cursor.close();
            
        }catch (Exception e){
            e.printStackTrace();
        }
        return estadosNuevos;
    }

    public ArrayList<ItemEstado> obtenerTodosLosEstados(){
        
        ArrayList<ItemEstado> datos_Estados= new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_ESTADO+" ORDER BY "+
                    BDConstantes.ESTADO_CAMPO_ORDEN+" DESC", null);
            if(cursor.getCount()==0){
                cursor.close();
                
                return datos_Estados;
            }
            while (cursor.moveToNext()) datos_Estados.add(Convertidor.createItemEstadoOfCursor(cursor));
            cursor.close();
            
        }catch (Exception e){
            e.printStackTrace();
        }
        return datos_Estados;
    }

    public ArrayList<ItemEstado> obtenerTodosLosEstadosLimite30(){
        
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
                    
                    return datos_Estados;
                }

                while (cursor.moveToNext())
                    datos_Estados.add(Convertidor.createItemEstadoOfCursor(cursor));
            }
            cursor.close();
            
        }catch (Exception e){
            e.printStackTrace();
        }
        return datos_Estados;
    }

    public ArrayList<ItemEstado> obtenerTodosLosEstadosOrdenadosXCorreos(){
        ArrayList<ItemEstado> datos_Estados= new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_ESTADO + " ORDER BY "
                    +BDConstantes.ESTADO_CAMPO_ID+" DESC", null);
            if(cursor.getCount()>0){
                while (cursor.moveToNext())
                    datos_Estados.add(Convertidor.createItemEstadoOfCursor(cursor));
            }
            cursor.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        return datos_Estados;
    }

    public ArrayList<ItemEstado> obtenerTodosLosEstadosOrdenadosXnoVistos(){
        
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
                
                return datos_Estados;
            }

            while (cursor.moveToNext())
                datos_Estados.add(Convertidor.createItemEstadoOfCursor(cursor));
            cursor.close();
            
        }catch (Exception e){
            e.printStackTrace();
        }
        return datos_Estados;
    }

    public synchronized void marcarEstadoComoVisto(String idEstado){
        if(existeEstado(idEstado)){
            
            String[] parametros={idEstado};
            ContentValues values=new ContentValues();
            values.put(BDConstantes.ESTADO_CAMPO_ESTA_VISTO,1);

            db.update(BDConstantes.TABLA_ESTADO,values,BDConstantes.ESTADO_CAMPO_ID+"=?",parametros);
            
        }
    }

    public synchronized void sumarUnaReaccion(String idEstado, String tipo, int cant){
        if(existeEstado(idEstado)){
            
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
            
        }
    }

    public int obtenerCantReacciones(String idEstado, int tipo){
        
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
                
                return -1;
            }

            cursor.moveToFirst();
            cant = cursor.getInt(0);
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        
        return cant;
    }

    //////////////////////////////////////////SEGUIDORES////////////////////////////////////
    public synchronized void insertarSeguidor(String correo){
        if(!existeSeguidor(correo)){
            
            ContentValues values=new ContentValues();
            values.put(BDConstantes.SEGUIDOR_CAMPO_ID,correo);
            db.insert(BDConstantes.TABLA_SEGUIDOR,BDConstantes.SEGUIDOR_CAMPO_ID,values);
            
        }
    }

    public boolean existeSeguidor(String correo) {
        
        String [] parametros={correo};
        String [] campos={BDConstantes.SEGUIDOR_CAMPO_ID};

        Cursor cursor;
        boolean exist=false;
        try {
            cursor=db.query(BDConstantes.TABLA_SEGUIDOR,campos,
                    BDConstantes.SEGUIDOR_CAMPO_ID+"=? LIMIT 1",parametros,null,null,null);
            if(cursor.getCount()>0) exist=cursor.moveToFirst();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
            exist=false;
        }
        
        return exist;
    }

    public synchronized void eliminarSeguidor(String correo){
        
        String [] parametros={correo};
        db.delete(BDConstantes.TABLA_SEGUIDOR,BDConstantes.SEGUIDOR_CAMPO_ID+"=?",parametros);
        
    }

    public int obtenerCantSeguidores(){
        
        int cant_seguidores = 0;
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_SEGUIDOR, null);
            cant_seguidores = cursor.getCount();
            cursor.close();
            
        }catch (Exception e){
            e.printStackTrace();
        }
        return cant_seguidores;
    }

    public ArrayList<String> obtenerTodosSeguidoresOrdenadosCorreo(){
        
        ArrayList<String> seguidores = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_SEGUIDOR +
                    " ORDER BY "+BDConstantes.SEGUIDOR_CAMPO_ID+" ASC", null);
            if(cursor.getCount()==0){
                cursor.close();
                
                return seguidores;
            }
            while (cursor.moveToNext())
                seguidores.add(cursor.getString(0));
            cursor.close();
            
        }catch (Exception e){
            e.printStackTrace();
        }
        return seguidores;
    }

    public ArrayList<String> obtenerTodosSeguidores(){
        
        ArrayList<String> seguidores = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_SEGUIDOR, null);
            if(cursor.getCount()==0){
                cursor.close();
                
                return seguidores;
            }
            while (cursor.moveToNext())
                seguidores.add(cursor.getString(0));
            cursor.close();
            
        }catch (Exception e){
            e.printStackTrace();
        }
        return seguidores;
    }

    //////////////////////////////////////////SIGUIENDO_A////////////////////////////////////
    public synchronized void insertarSiguiendoA(String correo){
        if(!existeSiguiendoA(correo)){
            
            ContentValues values=new ContentValues();
            values.put(BDConstantes.SIGUIENDO_A_CAMPO_ID,correo);
            db.insert(BDConstantes.TABLA_SIGUIENDO_A,BDConstantes.SIGUIENDO_A_CAMPO_ID,values);
            
        }
    }

    public boolean existeSiguiendoA(String correo) {
        
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
        
        return exist;
    }

    public synchronized void eliminarSiguiendoA(String correo){
        
        String [] parametros={correo};
        db.delete(BDConstantes.TABLA_SIGUIENDO_A,BDConstantes.SIGUIENDO_A_CAMPO_ID+"=?",parametros);
        
    }

    public int obtenerCantSiguiendoA(){
        
        int cant_seguidores = 0;
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_SIGUIENDO_A, null);
            cant_seguidores = cursor.getCount();
            cursor.close();
            
        }catch (Exception e){
            e.printStackTrace();
        }
        return cant_seguidores;
    }

    public ArrayList<String> obtenerTodosSiguiendoA(){
        
        ArrayList<String> seguidores = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_SIGUIENDO_A+
                    " ORDER BY "+BDConstantes.SIGUIENDO_A_CAMPO_ID+" ASC", null);
            if(cursor.getCount()==0){
                cursor.close();
                
                return seguidores;
            }
            while (cursor.moveToNext())
                seguidores.add(cursor.getString(0));
            cursor.close();
            
        }catch (Exception e){
            e.printStackTrace();
        }
        return seguidores;
    }

    //////////////////////////////////////////RACCIONES ESTADOS////////////////////////////////////
    public synchronized void insertarNuevaReaccionEstado(ItemReaccionEstado reaccionEstado) {
        if (existeEstado(reaccionEstado.getIdEstado())) {
            
            ContentValues values = new ContentValues();
            values.put(BDConstantes.REACCION_CAMPO_ID_ESTADO, reaccionEstado.getIdEstado());
            values.put(BDConstantes.REACCION_CAMPO_CORREO, reaccionEstado.getCorreo());
            values.put(BDConstantes.REACCION_CAMPO_TIPO_REACCION, reaccionEstado.getTipoReaccion());
            values.put(BDConstantes.REACCION_CAMPO_HORA, reaccionEstado.getHora());
            values.put(BDConstantes.REACCION_CAMPO_FECHA, reaccionEstado.getFecha());

            db.insert(BDConstantes.TABLA_REACCION_ESTADO, BDConstantes.REACCION_CAMPO_ID_ESTADO, values);
            
        }
    }

    public ArrayList<ItemReaccionEstado> obtenerReaccionesEstadosDelEstado(String idEstado){
        
        ArrayList<ItemReaccionEstado> reaccionEstados = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_REACCION_ESTADO+
                    " WHERE "+BDConstantes.REACCION_CAMPO_ID_ESTADO + " = \'"+idEstado+"\'", null);
            if(cursor.getCount()==0){
                cursor.close();
                
                return reaccionEstados;
            }
            while (cursor.moveToNext())
                reaccionEstados.add(Convertidor.createItemReaccionEstadoOfCursor(cursor));
            cursor.close();
            
        }catch (Exception e){
            e.printStackTrace();
        }
        return reaccionEstados;
    }

    public ArrayList<ItemReaccionEstado> obtenerReaccionesEstadosDelCorreo(String correo){
        
        ArrayList<ItemReaccionEstado> reaccionEstados = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_REACCION_ESTADO+
                    " WHERE "+BDConstantes.REACCION_CAMPO_CORREO + " = \'"+correo+"\'", null);
            if(cursor.getCount()==0){
                cursor.close();
                
                return reaccionEstados;
            }
            while (cursor.moveToNext())
                reaccionEstados.add(Convertidor.createItemReaccionEstadoOfCursor(cursor));
            cursor.close();
            
        }catch (Exception e){
            e.printStackTrace();
        }
        return reaccionEstados;
    }

    public synchronized void eliminarTodasLasReaccionesEstadosDeCorreo(String correo){
        
        String [] parametros={correo};
        db.delete(BDConstantes.TABLA_REACCION_ESTADO,
                BDConstantes.REACCION_CAMPO_CORREO+"=?",parametros);
        
    }
    public synchronized void eliminarTodasLasReaccionesEstadosDeEstado(String idEstado){
        
        String [] parametros={idEstado};
        db.delete(BDConstantes.TABLA_REACCION_ESTADO,
                BDConstantes.REACCION_CAMPO_ID_ESTADO+"=?",parametros);
        
    }

    //////////////////////////////////////////VISTAS_ESTADOS////////////////////////////////////
    public synchronized void insertarNuevaVistaEstado(ItemVistaEstado vistaEstado){
        
        ContentValues values=new ContentValues();
        values.put(BDConstantes.VISTA_ESTADO_CAMPO_ID_ESTADO,vistaEstado.getIdEstado());
        values.put(BDConstantes.VISTA_ESTADO_CAMPO_CORREO,vistaEstado.getCorreo());
        values.put(BDConstantes.VISTA_ESTADO_CAMPO_HORA,vistaEstado.getHora());
        values.put(BDConstantes.VISTA_ESTADO_CAMPO_FECHA,vistaEstado.getFecha());
        db.insert(BDConstantes.TABLA_VISTA_ESTADO,BDConstantes.VISTA_ESTADO_CAMPO_ID_ESTADO,values);
        
    }

    public synchronized void eliminarVistaEstadosDe(String idEstado){
        
        String [] parametros={idEstado};
        db.delete(BDConstantes.TABLA_VISTA_ESTADO,BDConstantes.VISTA_ESTADO_CAMPO_ID_ESTADO+"=?",parametros);
        
    }

    public int obtenerCantVistasEstadosDe(String idEstado){
        
        int cant = 0;
        String [] parametros={idEstado};
        String [] campos={BDConstantes.VISTA_ESTADO_CAMPO_ID_ESTADO};
        try {
            Cursor cursor=db.query(BDConstantes.TABLA_VISTA_ESTADO,campos,
                    BDConstantes.VISTA_ESTADO_CAMPO_ID_ESTADO+"=?",parametros,
                    null,null,null);
            if(cursor!=null) cant = cursor.getCount();
            cursor.close();
            
        }catch (Exception e){
            e.printStackTrace();
        }
        return cant;
    }

    public ArrayList<ItemVistaEstado> obtenerVistasEstadosDe(String idEstado){
        
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
            
        }catch (Exception e){
            e.printStackTrace();
        }
        return vistas;
    }

    //////////////////////////////////////////ESTADISTICA_PERSONAL////////////////////////////////////
    public synchronized boolean insertarNuevaEstadisticaPersonal(ItemEstadisticaPersonal estadisticaPersonal){
        if(!existeEstadisticaPersonal(estadisticaPersonal.getId())){
            
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
            
            return true;
        }
        return false;
    }

    public ItemEstadisticaPersonal obtenerEstadisticaPersonal(String correo){
        
        ItemEstadisticaPersonal dato = null;
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_ESTADISTICA_PERSONAL+
                    " WHERE "+BDConstantes.ESTADISTICA_PERSONAL_CAMPO_ID + " = \'"+correo+"\' LIMIT 1", null);
            if(cursor.getCount()==0){
                cursor.close();
                
                return null;
            }
            cursor.moveToFirst();
            dato = Convertidor.createItemEstadisticaPersonalOfCursor(cursor);
            cursor.close();
            
        }catch (Exception e){
            e.printStackTrace();
        }
        return dato;
    }

    public boolean existeEstadisticaPersonal(String correo) {
        
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
        
        return exist;
    }

    public ArrayList<ItemEstadisticaPersonal> obtenerTodasEstadisticasPersonales(){
        
        ArrayList<ItemEstadisticaPersonal> datos= new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_ESTADISTICA_PERSONAL, null);
            if(cursor.getCount()==0){
                cursor.close();
                
                return datos;
            }
            while (cursor.moveToNext()) datos.add(Convertidor.createItemEstadisticaPersonalOfCursor(cursor));
            cursor.close();
            
        }catch (Exception e){
            e.printStackTrace();
        }
        return datos;
    }

    public synchronized void modificarEstadisticaPersonal(ItemEstadisticaPersonal estadisticaPersonal){
        
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
        
    }

    public synchronized void resetearEstadisticasPersonales(){
        
        db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_ESTADISTICA_PERSONAL);
        db.execSQL(BDConstantes.CREAR_TABLA_ESTADISTICA_PERSONAL);
        
    }

    //////////////////////////////////////////TEMAS////////////////////////////////////
    public synchronized void insertarNuevoTema(ItemTemas temas){
//        if(existeTema(temas.getNombre())) eliminarTema(temas.getNombre());
        if(!existeTemaId(temas.getId())){
            
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
            values.put(BDConstantes.TEMAS_CAMPO_COLOR_STATUS_BAR, temas.getStatus_bar());

            db.insert(BDConstantes.TABLA_TEMAS, BDConstantes.TEMAS_CAMPO_ID, values);
            
        }
    }

    public synchronized void modificarTema(ItemTemas temas){
        if(existeTemaId(temas.getId())){
            
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
            values.put(BDConstantes.TEMAS_CAMPO_COLOR_STATUS_BAR, temas.getStatus_bar());

            db.update(BDConstantes.TABLA_TEMAS,values,BDConstantes.TEMAS_CAMPO_ID+"=?",parametros);
            
        } else insertarNuevoTema(temas);
    }

    public boolean existeTemaNombre(String nombre){
        
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
        
        return exist;
    }

    public boolean existeTemaId(String id){
        
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
        
        return exist;
    }

    public ArrayList<ItemTemas> obtenerTemas(){
        
        ArrayList<ItemTemas> datos = new ArrayList<>();
        Cursor cursor;
        try{
            cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_TEMAS, null);

            if(cursor.getCount()==0){
                cursor.close();
                
                return datos;
            }
            while (cursor.moveToNext()){
                datos.add(Convertidor.createItemTemasOfCursor(cursor));
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        
        return datos;
    }

    public ArrayList<ItemTemas> obtenerTemas(boolean oscuro){
        
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
                
                return datos;
            }
            while (cursor.moveToNext()){
                datos.add(Convertidor.createItemTemasOfCursor(cursor));
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        
        return datos;
    }

    public ItemTemas obtenerTema(String id, boolean esOscuro){
        ItemTemas temas = null;
        
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
        
        String [] parametros={id};
        db.delete(BDConstantes.TABLA_TEMAS,BDConstantes.TEMAS_CAMPO_ID+"=?",parametros);
        
    }

    public synchronized void eliminarTodosTema(){
        
        db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_TEMAS);
        db.execSQL(BDConstantes.CREAR_TABLA_TEMAS);
        
    }

    //////////////////////////////////////////UsuarioCorreo////////////////////////////////////
    public synchronized void insertarUsuarioCorreo(ItemUsuarioCorreo usuarioCorreo, boolean esMio){
        if(!existeUsuarioCorreo(usuarioCorreo.getCorreo())){
            
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
            
        }
        else modificarUsuarioCorreo(usuarioCorreo, esMio);
    }

    public synchronized void modificarUsuarioCorreo(ItemUsuarioCorreo usuarioCorreo, boolean esMio){
        
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
        
    }

    public boolean existeUsuarioCorreo(String correo){
        
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
        
        return exist;
    }

    public String obtenerIdGroupUsuarioCorreo(String correo){
        
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
        
        return groupId;
    }

    public ArrayList<ItemUsuarioCorreo> obtenerUsuarioCorreos(){
        
        ArrayList<ItemUsuarioCorreo> datos = new ArrayList<>();
        Cursor cursor;
        try{
            cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_USUARIO_CORREO
                    + " ORDER BY "+BDConstantes.USUARIO_CORREO_CAMPO_ORDEN + " DESC", null);

            if(cursor.getCount()==0){
                cursor.close();
                
                return datos;
            }
            while (cursor.moveToNext()){
                datos.add(Convertidor.createItemUsuarioCorreoOfCursor(cursor));
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        
        return datos;
    }

    public synchronized void eliminarUsuarioCorreo(String correo){
        
        String [] parametros={correo};
        db.delete(BDConstantes.TABLA_USUARIO_CORREO,BDConstantes.USUARIO_CAMPO_CORREO+"=?",parametros);
        
        eliminarTodosMensajesCorreosDe(correo);
    }

    public void eliminarTodoBuzon() {
        
        db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_USUARIO_CORREO);
        db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_MENSAJE_CORREO);
        db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_ADJUNTO_CORREO);
        db.execSQL(BDConstantes.CREAR_TABLA_USUARIO_CORREO);
        db.execSQL(BDConstantes.CREAR_TABLA_MENSAJE_CORREO);
        db.execSQL(BDConstantes.CREAR_TABLA_ADJUNTO_CORREO);
        
    }

    //////////////////////////////////////////ItemMensajeCorreo////////////////////////////////////
    public synchronized void insertarNuevoMensajeCorreo(ItemMensajeCorreo mensajeCorreo){
        if(!existeMensajeCorreo(mensajeCorreo.getId())){
            
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
            
        }
    }

    public synchronized void modificarMensajeCorreo(ItemMensajeCorreo mensajeCorreo){
        if(existeMensajeCorreo(mensajeCorreo.getId())){
            
            String[] parametros={mensajeCorreo.getId()};
            ContentValues values=new ContentValues();

            values.put(BDConstantes.MENSAJE_CORREO_CAMPO_ID, mensajeCorreo.getId());

            db.update(BDConstantes.TABLA_MENSAJE_CORREO,values,BDConstantes.MENSAJE_CORREO_CAMPO_ID+"=?",parametros);
            
        }
    }

    public boolean existeMensajeCorreo(String id){
        
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
        
        return exist;
    }

    public ArrayList<ItemMensajeCorreo> obtenerMensajeFavoritos(){
        
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
                
                return datos;
            }
            while (cursor.moveToNext()){
                datos.add(Convertidor.createItemMensajeCorreoOfCursor(cursor));
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        
        return datos;
    }

    public ArrayList<ItemMensajeCorreo> obtenerMensajeCorreo(boolean enviado){
        
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
                
                return datos;
            }
            while (cursor.moveToNext()){
                datos.add(Convertidor.createItemMensajeCorreoOfCursor(cursor));
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        
        return datos;
    }

    public ArrayList<ItemMensajeCorreo> obtenerMensajeCorreoDe(String correo){
        
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
                
                return datos;
            }
            while (cursor.moveToNext()){
                datos.add(Convertidor.createItemMensajeCorreoOfCursor(cursor));
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        
        return datos;
    }

    public int obtenerCantMensajeCorreoNoVistoDe(String correo){
        
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
        
        return cant;
    }

    public synchronized void marcarComoVistoCorreosNoVistosDe(String correo){
        ArrayList<ItemMensajeCorreo> listaDatos = obtenerMensajeCorreoNoVistoDe(correo);
        int l = listaDatos.size();
        if(l>0){
            
            for(int i=0; i<l; i++){
                String[] parametros={listaDatos.get(i).getId()};
                ContentValues values=new ContentValues();
                values.put(BDConstantes.MENSAJE_CORREO_CAMPO_ES_NUEVO, "0");
                db.update(BDConstantes.TABLA_MENSAJE_CORREO,values,BDConstantes.MENSAJE_CORREO_CAMPO_ID+"=?",parametros);
            }
            
        }
    }

    public synchronized void marcarComoVistoCorreoNoVistosPor(String idMensajeCorreo){
        
        String[] parametros={idMensajeCorreo};
        ContentValues values=new ContentValues();
        values.put(BDConstantes.MENSAJE_CORREO_CAMPO_ES_NUEVO, "0");
        db.update(BDConstantes.TABLA_MENSAJE_CORREO,values,
                BDConstantes.MENSAJE_CORREO_CAMPO_ID+"=?",parametros);

    }

    public synchronized void marcarComoNoVistoCorreoPor(String idMensajeCorreo){
        
        String[] parametros={idMensajeCorreo};
        ContentValues values=new ContentValues();
        values.put(BDConstantes.MENSAJE_CORREO_CAMPO_ES_NUEVO, "1");
        db.update(BDConstantes.TABLA_MENSAJE_CORREO,values,
                BDConstantes.MENSAJE_CORREO_CAMPO_ID+"=?",parametros);

    }

    private ArrayList<ItemMensajeCorreo> obtenerMensajeCorreoNoVistoDe(String correo){
        
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
                
                return datos;
            }
            while (cursor.moveToNext()){
                datos.add(Convertidor.createItemMensajeCorreoOfCursor(cursor));
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        
        return datos;
    }

    public String existeMensajeCorreoAntiguoDameCorreo(String idMsg){
        
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
        
        return existMsg;
    }

    public int obtenerCantMensajeCorreoNoVistoTotal(){
        
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
        
        return cant;
    }

    public synchronized void modificarEstadoMensajeCorreo(String idMsg, int estado, int peso){
        if(existeMensajeCorreo(idMsg)){
            
            String[] parametros={idMsg};
            ContentValues values=new ContentValues();
            values.put(BDConstantes.MENSAJE_CORREO_CAMPO_ESTADO,estado);
            values.put(BDConstantes.MENSAJE_CORREO_CAMPO_PESO,peso);

            db.update(BDConstantes.TABLA_MENSAJE_CORREO,values,
                    BDConstantes.MENSAJE_CORREO_CAMPO_ID+"=?",parametros);

        }
    }

//    public synchronized void modificarEstadoMensajeCorreo(String idMsg, int estado){
//        if(existeMensajeCorreo(idMsg)){
//            
//            String[] parametros={idMsg};
//            ContentValues values=new ContentValues();
//            values.put(BDConstantes.MENSAJE_CORREO_CAMPO_ESTADO,estado);
//            values.put(BDConstantes.MENSAJE_CORREO_CAMPO_PESO,estado);
//
//            db.update(BDConstantes.TABLA_MENSAJE_CORREO,values,
//                    BDConstantes.MENSAJE_CORREO_CAMPO_ID+"=?",parametros);
//
//        }
//    }

    public synchronized void modificarFavoritoMensajeCorreo(String idMsg, boolean esFav){
        if(existeMensajeCorreo(idMsg)){
            
            String[] parametros={idMsg};
            ContentValues values=new ContentValues();
            values.put(BDConstantes.MENSAJE_CORREO_CAMPO_ES_FAVORITO, esFav?1:0);

            db.update(BDConstantes.TABLA_MENSAJE_CORREO,values,
                    BDConstantes.MENSAJE_CORREO_CAMPO_ID+"=?",parametros);
            
        }
    }

    public ItemMensajeCorreo obtenerMensajeCorreo(String id){
        ItemMensajeCorreo mensajeCorreo= null;
        
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
        
        return mensajeCorreo;
    }

    public String obtenerNombreMensajeCorreo(String id, String correo){
        String nombre = correo;
        
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
        
        return nombre;
    }

    public synchronized void eliminarMensajeCorreo(String id){
        
        String [] parametros={id};
        db.delete(BDConstantes.TABLA_MENSAJE_CORREO,BDConstantes.MENSAJE_CORREO_CAMPO_ID+"=?",parametros);
        
        eliminarAdjuntosCorreosPorIdDe(id);
    }

    public synchronized void eliminarTodosMensajesCorreosDe(String correo){
        
        String [] parametros={correo};
        db.delete(BDConstantes.TABLA_MENSAJE_CORREO,BDConstantes.MENSAJE_CORREO_CAMPO_CORREO+"=?",parametros);
        
        eliminarAdjuntosCorreosPorCorreoDe(correo);
    }

    //////////////////////////////////////////ItemAdjuntoCorreo////////////////////////////////////
    public synchronized void insertarNuevoAdjuntoCorreo(ItemAdjuntoCorreo adjuntoCorreo){
        if(!existeAdjuntoCorreo(adjuntoCorreo.getId())){
            
            ContentValues values = new ContentValues();

            values.put(BDConstantes.ADJUNTO_CORREO_CAMPO_ID, adjuntoCorreo.getId());
            values.put(BDConstantes.ADJUNTO_CORREO_CAMPO_ID_MENSAJE, adjuntoCorreo.getId_mensaje());
            values.put(BDConstantes.ADJUNTO_CORREO_CAMPO_CORREO, adjuntoCorreo.getCorreo());
            values.put(BDConstantes.ADJUNTO_CORREO_CAMPO_POSICION, adjuntoCorreo.getPosicion());
            values.put(BDConstantes.ADJUNTO_CORREO_CAMPO_NOMBRE, adjuntoCorreo.getNombre());
            values.put(BDConstantes.ADJUNTO_CORREO_CAMPO_TIPO, adjuntoCorreo.getTipo());
            values.put(BDConstantes.ADJUNTO_CORREO_CAMPO_PESO, adjuntoCorreo.getPeso());

            db.insert(BDConstantes.TABLA_ADJUNTO_CORREO, BDConstantes.ADJUNTO_CORREO_CAMPO_ID, values);
            
        }
    }

    public boolean existeAdjuntoCorreo(String id){
        
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
        
        return exist;
    }

    public ItemAdjuntoCorreo obtenerAdjuntosCorreo(String id){
        
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
        
        return dato;
    }

    public ArrayList<ItemAdjuntoCorreo> obtenerAdjuntosCorreoDe(String idMensajeCorreo){
        
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
                
                return datos;
            }
            while (cursor.moveToNext()){
                datos.add(Convertidor.createItemAdjuntoCorreoOfCursor(cursor));
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        
        return datos;
    }

    public synchronized void eliminarAdjuntosCorreosPorIdDe(String idMensajeCorreo){
        
        String [] parametros={idMensajeCorreo};
        db.delete(BDConstantes.TABLA_ADJUNTO_CORREO,BDConstantes.ADJUNTO_CORREO_CAMPO_ID_MENSAJE+"=?",parametros);
        
    }

    public synchronized void eliminarAdjuntosCorreosPorCorreoDe(String correo){
        
        String [] parametros={correo};
        db.delete(BDConstantes.TABLA_ADJUNTO_CORREO,BDConstantes.ADJUNTO_CORREO_CAMPO_CORREO+"=?",parametros);
        
    }

    //////////////////////////////////////////ItemPost////////////////////////////////////
    public synchronized void insertarNuevoPost(ItemPost post){
        if(!existePost(post.getId())){
            
            ContentValues values = new ContentValues();

            values.put(BDConstantes.POST_CAMPO_ID, post.getId());
            values.put(BDConstantes.POST_CAMPO_UID, ""+post.getUid());
            values.put(BDConstantes.POST_CAMPO_TIPO_POST, post.getTipo_post());
            values.put(BDConstantes.POST_CAMPO_NOMBRE, post.getNombre());
            values.put(BDConstantes.POST_CAMPO_CORREO, post.getCorreo());
            values.put(BDConstantes.POST_CAMPO_TIPO_USUARIO, post.getTipo_usuario());
            values.put(BDConstantes.POST_CAMPO_ICONO, post.getIcono());

            values.put(BDConstantes.POST_CAMPO_TEXTO, post.getTexto());
            values.put(BDConstantes.POST_CAMPO_ES_NUEVO, post.isEs_nuevo()?1:0);
            values.put(BDConstantes.POST_CAMPO_RUTA_DATO, post.getRuta_dato());
            values.put(BDConstantes.POST_CAMPO_PESO_DATO, post.getPeso_dato());
            values.put(BDConstantes.POST_CAMPO_HORA, post.getHora());
            values.put(BDConstantes.POST_CAMPO_FECHA, post.getFecha());
            values.put(BDConstantes.POST_CAMPO_ORDEN, post.getOrden());

            db.insert(BDConstantes.TABLA_POST, BDConstantes.POST_CAMPO_ID, values);
            
        }
    }

    public synchronized void actualizarPost(String idPost, String ruta) {
        String [] parametros={idPost};
        ContentValues values=new ContentValues();
        values.put(BDConstantes.POST_CAMPO_RUTA_DATO,ruta);

        db.update(BDConstantes.TABLA_POST,values,BDConstantes.POST_CAMPO_ID+"=?",parametros);

    }

    public synchronized void renovarPost(String idPost, String orden) {
        String [] parametros={idPost};
        ContentValues values=new ContentValues();
        values.put(BDConstantes.POST_CAMPO_ES_NUEVO, 1);
        values.put(BDConstantes.POST_CAMPO_ORDEN,orden);
        db.update(BDConstantes.TABLA_POST,values,BDConstantes.POST_CAMPO_ID+"=?",parametros);

    }

    public boolean existePost(String id){
        
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
        
        return exist;
    }

    public ItemPost obtenerPost(String id){
        
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
                post.setComentarioPosts(obtenerComentarioPostDePost(post.getId()));
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        
        return post;
    }

    public ArrayList<ItemPost> obtenerPostsNuevos(){

        String [] parametros={"1"};
        String [] campos={"*"};

        ArrayList<ItemPost> datos = new ArrayList<>();
        Cursor cursor;
        try {
            cursor=db.query(BDConstantes.TABLA_POST,campos,
                    BDConstantes.POST_CAMPO_ES_NUEVO+"=?"
                            + " ORDER BY "+BDConstantes.POST_CAMPO_ORDEN + " DESC",
                    parametros,null,null,null);
            if(cursor.getCount()==0){
                cursor.close();
                return datos;
            }
            while (cursor.moveToNext()){
                ItemPost post = Convertidor.createItemPostOfCursor(cursor);
                post.setComentarioPosts(obtenerComentarioPostDePost(post.getId()));
                datos.add(post);
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return datos;
    }

    public ArrayList<ItemPost> obtenerTodosPosts(){

        ArrayList<ItemPost> datos = new ArrayList<>();
        Cursor cursor;
        try{
            cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_POST
                    + " ORDER BY "+BDConstantes.POST_CAMPO_ORDEN + " DESC", null);

            if(cursor.getCount()==0){
                cursor.close();

                return datos;
            }
            while (cursor.moveToNext()){
                ItemPost post = Convertidor.createItemPostOfCursor(cursor);
                post.setComentarioPosts(obtenerComentarioPostDePost(post.getId()));
                datos.add(post);
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return datos;
    }

    public int obtenerCantTotalPosts(){
        Cursor cursor;
        int cant = 0;
        String [] parametros={"1"};
        String [] campos={BDConstantes.POST_CAMPO_ES_NUEVO};
        try {
            cursor=db.query(BDConstantes.TABLA_POST,campos,
                    BDConstantes.POST_CAMPO_ES_NUEVO+"=?",
                    parametros,null,null,null);
            cant = cursor.getCount();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return cant;
    }

    public synchronized void ocultarPost(String id){
        
        String[] parametros={id};
        ContentValues values=new ContentValues();
        values.put(BDConstantes.POST_CAMPO_ES_NUEVO, 0);

        db.update(BDConstantes.TABLA_POST,values,
                BDConstantes.POST_CAMPO_ID+"=?",parametros);
    }

    public synchronized void eliminarPost(String id){
        String [] parametros={id};
        db.delete(BDConstantes.TABLA_POST,BDConstantes.POST_CAMPO_ID+"=?",parametros);
        eliminarComentariosDePost(id);
    }

    //////////////////////////////////////////BloquedosPost////////////////////////////////////
    public synchronized void insertarNuevoBloqueadoPost(String correo){
        if(!existeBloqueadoPost(correo)){
            
            ContentValues values=new ContentValues();
            values.put(BDConstantes.LISTA_NEGRA_POST_CAMPO_ID,correo);
            db.insert(BDConstantes.TABLA_LISTA_NEGRA_POST,BDConstantes.LISTA_NEGRA_POST_CAMPO_ID,values);
            
        }
    }

    public boolean existeBloqueadoPost(String correo, boolean defecto){
        
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
//            existeBloqueadoPost(correo, defecto);
            exist=defecto;
        }
        
        return exist;
    }

    public boolean existeBloqueadoPost(String correo){
        
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
        
        return exist;
    }

    public synchronized void eliminarBloqueadoPost(String correo){
        
        String [] parametros={correo};
        db.delete(BDConstantes.TABLA_LISTA_NEGRA_POST,
                BDConstantes.LISTA_NEGRA_POST_CAMPO_ID+"=?",parametros);
        
    }

    public ArrayList<String> obtenerBloqueadosPost(){
        
        ArrayList<String> datos = new ArrayList<>();
        Cursor cursor;
        try{
            cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_LISTA_NEGRA_POST, null);

            if(cursor.getCount()==0){
                cursor.close();
                
                return datos;
            }
            while (cursor.moveToNext()){
                datos.add(cursor.getString(0));
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        
        return datos;
    }

    //////////////////////////////////////////DescripcionError////////////////////////////////////
    public synchronized void insertarDescripcionError(String msg_pub, String msg_piv){
        
        ContentValues values=new ContentValues();
        values.put(BDConstantes.DESCRIPCION_ERROR_CAMPO_MENSAJE_PUBLICO,msg_pub);
        values.put(BDConstantes.DESCRIPCION_ERROR_CAMPO_MENSAJE_PRIVADO,msg_piv);
        db.insert(BDConstantes.TABLA_DESCRIPCION_ERROR,BDConstantes.DESCRIPCION_ERROR_CAMPO_MENSAJE_PUBLICO,values);
        
    }

    public synchronized void eliminarDescripcionesError(){
        
        db.execSQL("DROP TABLE IF EXISTS "+BDConstantes.TABLA_DESCRIPCION_ERROR);
        db.execSQL(BDConstantes.CREAR_TABLA_DESCRIPCION_ERROR);
        
    }

    public ArrayList<String> obtenerPrimeraDescripcionError(){

        ArrayList<String> datos = new ArrayList<>();
        Cursor cursor;
        try{
            cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_DESCRIPCION_ERROR
                    +" LIMIT 1", null);

            if(cursor.getCount()==0){
                cursor.close();

                return datos;
            }
            cursor.moveToFirst();
            datos.add(cursor.getString(0));
            datos.add(cursor.getString(1));
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return datos;
    }

    public int obtenerCantidadDescripcionError(){

        int cant = 0;
        Cursor cursor;
        try{
            cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_DESCRIPCION_ERROR, null);
            cant = cursor.getCount();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return cant;
    }

    //////////////////////////////////////////itemAtajos////////////////////////////////////
    public synchronized void insertarNuevoAtajo(ItemAtajo atajo){
        if(!existeAtajo(atajo.getComando())){
            ContentValues values=new ContentValues();
            values.put(BDConstantes.ATAJOS_CAMPO_COMANDO,atajo.getComando());
            values.put(BDConstantes.ATAJOS_CAMPO_DESCRIPCION,atajo.getDescripcion());
            db.insert(BDConstantes.TABLA_ATAJOS,BDConstantes.ATAJOS_CAMPO_COMANDO,values);
        } else modificarAtajo(atajo);
    }

    public boolean existeAtajo(String comando){

        String [] parametros={comando};
        String [] campos={BDConstantes.ATAJOS_CAMPO_COMANDO};

        Cursor cursor;
        boolean exist=false;
        try {
            cursor=db.query(BDConstantes.TABLA_ATAJOS,campos,
                    BDConstantes.ATAJOS_CAMPO_COMANDO+"=? LIMIT 1"
                    ,parametros,null,null,null);
            if(cursor.getCount()>0) exist=cursor.moveToFirst();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
            exist=true;
        }

        return exist;
    }

    public synchronized void modificarAtajo(ItemAtajo atajo){
        String [] parametros={atajo.getComando()};
        ContentValues values = new ContentValues();
        values.put(BDConstantes.ATAJOS_CAMPO_DESCRIPCION, atajo.getDescripcion());
        db.update(BDConstantes.TABLA_ATAJOS,values,BDConstantes.ATAJOS_CAMPO_COMANDO+"=?",parametros);

    }

    public synchronized void eliminarAtajo(String comandoAtajo){
        String [] parametros={comandoAtajo};
        db.delete(BDConstantes.TABLA_ATAJOS,
                BDConstantes.ATAJOS_CAMPO_COMANDO+"=?",parametros);
    }

    public ArrayList<ItemAtajo> obtenerAtajos(){

        ArrayList<ItemAtajo> datos = new ArrayList<>();
        Cursor cursor;
        try{
            cursor = db.rawQuery("SELECT * FROM "+BDConstantes.TABLA_ATAJOS, null);

            if(cursor.getCount()==0){
                cursor.close();

                return datos;
            }
            while (cursor.moveToNext()){
                String com = cursor.getString(0);
                String des = cursor.getString(1);
                datos.add(new ItemAtajo(com,des));
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return datos;
    }

    //////////////////////////////////////////ItemComentarioPost////////////////////////////////////
    public synchronized void insertarNuevoComentarioPost(ItemComentarioPost post){
        if(!existeComentarioPost(post.getId())){
            ContentValues values = new ContentValues();
            values.put(BDConstantes.COMENTARIO_POST_CAMPO_ID, post.getId());
            values.put(BDConstantes.COMENTARIO_POST_CAMPO_ID_POST, ""+post.getId_post());
            values.put(BDConstantes.COMENTARIO_POST_CAMPO_TIPO_COMENTARIO_POST, post.getTipo_comentario_post());
            values.put(BDConstantes.COMENTARIO_POST_CAMPO_NOMBRE, post.getNombre());
            values.put(BDConstantes.COMENTARIO_POST_CAMPO_CORREO, post.getCorreo());
            values.put(BDConstantes.COMENTARIO_POST_CAMPO_TIPO_USUARIO, post.getTipo_usuario());
            values.put(BDConstantes.COMENTARIO_POST_CAMPO_ICONO, post.getIcono());

            values.put(BDConstantes.COMENTARIO_POST_CAMPO_TEXTO, post.getTexto());
            values.put(BDConstantes.COMENTARIO_POST_CAMPO_RUTA_DATO, post.getRuta_dato());
            values.put(BDConstantes.COMENTARIO_POST_CAMPO_PESO_DATO, post.getPeso_dato());
            values.put(BDConstantes.COMENTARIO_POST_CAMPO_HORA, post.getHora());
            values.put(BDConstantes.COMENTARIO_POST_CAMPO_FECHA, post.getFecha());
            values.put(BDConstantes.COMENTARIO_POST_CAMPO_ORDEN, post.getOrden());

            db.insert(BDConstantes.TABLA_COMENTARIO_POST, BDConstantes.COMENTARIO_POST_CAMPO_ID, values);
            renovarPost(post.getId_post(),post.getOrden());
        }
    }

    public boolean existeComentarioPost(String id){
        String [] parametros={id};
        String [] campos={BDConstantes.COMENTARIO_POST_CAMPO_ID_POST};

        Cursor cursor;
        boolean exist=false;
        try {
            cursor=db.query(BDConstantes.TABLA_COMENTARIO_POST,campos,
                    BDConstantes.COMENTARIO_POST_CAMPO_ID_POST+"=? LIMIT 1",parametros,null,null,null);
            if(cursor.getCount()>0) exist=cursor.moveToFirst();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
            exist=true;
        }

        return exist;
    }

    public ItemComentarioPost obtenerComentarioPost(String id){

        String [] parametros={id};
        String [] campos={"*"};

        Cursor cursor;
        ItemComentarioPost post=null;
        try {
            cursor=db.query(BDConstantes.TABLA_COMENTARIO_POST,campos,
                    BDConstantes.COMENTARIO_POST_CAMPO_ID+"=? LIMIT 1",parametros,null,null,null);
            if(cursor.getCount()>0){
                cursor.moveToFirst();
                post = Convertidor.createItemComentarioPostOfCursor(cursor);
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return post;
    }

    public ArrayList<ItemComentarioPost> obtenerComentarioPostDePost(String idPost){

        String [] parametros={idPost};
        String [] campos={"*"};

        Cursor cursor;
        ArrayList<ItemComentarioPost> datos = new ArrayList<>();
        try {
            cursor=db.query(BDConstantes.TABLA_COMENTARIO_POST,campos,
                    BDConstantes.COMENTARIO_POST_CAMPO_ID_POST+"=?"
                            + " ORDER BY "+BDConstantes.POST_CAMPO_ORDEN + " ASC",parametros,null,null,null);
            if(cursor.getCount()==0){
                cursor.close();
                return datos;
            }
            while (cursor.moveToNext()){
                datos.add(Convertidor.createItemComentarioPostOfCursor(cursor));
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return datos;
    }

    public ArrayList<ItemComentarioPost> obtenerComentarioPostDeCorreo(String correo){

        String [] parametros={correo};
        String [] campos={"*"};

        Cursor cursor;
        ArrayList<ItemComentarioPost> datos = new ArrayList<>();
        try {
            cursor=db.query(BDConstantes.TABLA_COMENTARIO_POST,campos,
                    BDConstantes.COMENTARIO_POST_CAMPO_CORREO+"=?"
                            + " ORDER BY "+BDConstantes.POST_CAMPO_ORDEN + " ASC",parametros,null,null,null);
            if(cursor.getCount()==0){
                cursor.close();
                return datos;
            }
            while (cursor.moveToNext()){
                datos.add(Convertidor.createItemComentarioPostOfCursor(cursor));
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return datos;
    }

    public synchronized void eliminarComentarioPost(String id){
        String [] parametros={id};
        db.delete(BDConstantes.TABLA_COMENTARIO_POST,BDConstantes.COMENTARIO_POST_CAMPO_ID+"=?",parametros);
    }

    public synchronized void eliminarComentariosDePost(String idPost){
        String [] parametros={idPost};
        db.delete(BDConstantes.TABLA_COMENTARIO_POST,BDConstantes.COMENTARIO_POST_CAMPO_ID_POST+"=?",parametros);
    }
}
