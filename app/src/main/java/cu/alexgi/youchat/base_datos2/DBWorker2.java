package cu.alexgi.youchat.base_datos2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import cu.alexgi.youchat.Convertidor;
import cu.alexgi.youchat.MainActivity;
import cu.alexgi.youchat.items.ItemContactoPublico;

public class DBWorker2 {

    private ConexionBD2 conexionBD;
    private final int nuevaVersion=1;
    private SQLiteDatabase db;

    public DBWorker2(Context context){
//        if(YouChatApplication.version_bd==nuevaVersion)
            conexionBD=new ConexionBD2(context, BDConstantes2.NOMBRE_BASE_DATOS,null, nuevaVersion);
//        else {
//            conexionBD=new ConexionBD2(context, BDConstantes2.NOMBRE_BASE_DATOS,null, nuevaVersion);
//            conexionBD.onUpgrade(conexionBD.getWritableDatabase(),YouChatApplication.version_bd,nuevaVersion);
//        }
        db = conexionBD.getWritableDatabase();
    }

    //////////////////////////////////////////CONTACTO////////////////////////////////////
    public synchronized void insertarNuevoContactoPublico(ItemContactoPublico contacto){
        if(!existeContactoPublico(contacto.getCorreo(), true)){
            ContentValues values = new ContentValues();
            values.put(BDConstantes2.CONTACTO_PUBLICO_CAMPO_ALIAS, contacto.getAlias());
            values.put(BDConstantes2.CONTACTO_PUBLICO_CAMPO_CORREO, contacto.getCorreo());
            values.put(BDConstantes2.CONTACTO_PUBLICO_CAMPO_INFO, contacto.getInfo());
            values.put(BDConstantes2.CONTACTO_PUBLICO_CAMPO_TELEFONO, contacto.getTelefono());
            values.put(BDConstantes2.CONTACTO_PUBLICO_CAMPO_GENERO, contacto.getGenero());
            values.put(BDConstantes2.CONTACTO_PUBLICO_CAMPO_PROVINCIA, contacto.getProvincia());
            values.put(BDConstantes2.CONTACTO_PUBLICO_CAMPO_FECHA_NACIMIENTO, contacto.getFecha_nac());
            String nombreOrden = contacto.getAlias().toLowerCase();
            values.put(BDConstantes2.CONTACTO_PUBLICO_CAMPO_NOMBRE_ORDENAR, nombreOrden);

            db.insert(BDConstantes2.TABLA_CONTACTO_PUBLICO, BDConstantes2.CONTACTO_PUBLICO_CAMPO_CORREO, values);
            
        } else modificarContactoPublico(contacto);
    }

    public boolean existeContactoPublico(String contacto_correo, boolean defecto){

        String [] parametros={contacto_correo};
        String [] campos={BDConstantes2.CONTACTO_PUBLICO_CAMPO_CORREO};

        Cursor cursor;
        boolean exist=false;
        try {
            cursor=db.query(BDConstantes2.TABLA_CONTACTO_PUBLICO,campos,
                    BDConstantes2.CONTACTO_PUBLICO_CAMPO_CORREO+"=? LIMIT 1",parametros,null,null,null);
            if(cursor.getCount()>0) exist=cursor.moveToFirst();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
            exist=defecto;
        }

        return exist;
    }

    public synchronized void modificarContactoPublico(ItemContactoPublico contacto){
        SQLiteDatabase db=conexionBD.getWritableDatabase();
        String[] parametros={contacto.getCorreo()};
        ContentValues values=new ContentValues();
        values.put(BDConstantes2.CONTACTO_PUBLICO_CAMPO_ALIAS, contacto.getAlias());
        values.put(BDConstantes2.CONTACTO_PUBLICO_CAMPO_INFO, contacto.getInfo());
        values.put(BDConstantes2.CONTACTO_PUBLICO_CAMPO_TELEFONO, contacto.getTelefono());
        values.put(BDConstantes2.CONTACTO_PUBLICO_CAMPO_GENERO, contacto.getGenero());
        values.put(BDConstantes2.CONTACTO_PUBLICO_CAMPO_PROVINCIA, contacto.getProvincia());
        values.put(BDConstantes2.CONTACTO_PUBLICO_CAMPO_FECHA_NACIMIENTO, contacto.getFecha_nac());
        String nombreOrden = contacto.getAlias().toLowerCase();
        values.put(BDConstantes2.CONTACTO_PUBLICO_CAMPO_NOMBRE_ORDENAR, nombreOrden);

        db.update(BDConstantes2.TABLA_CONTACTO_PUBLICO,values, BDConstantes2.CONTACTO_PUBLICO_CAMPO_CORREO+"=?",parametros);
        
    }

    public ArrayList<ItemContactoPublico> obtenerContactosOrdenadosXNombre(boolean ordenarXNombre){
        
        ArrayList<ItemContactoPublico> datos_Contacto = new ArrayList<>();
        Cursor cursor;
        try{
            if(ordenarXNombre){
                cursor = db.rawQuery("SELECT * FROM "+ BDConstantes2.TABLA_CONTACTO_PUBLICO+
                        " ORDER BY "+ BDConstantes2.CONTACTO_PUBLICO_CAMPO_NOMBRE_ORDENAR+" ASC", null);
            }
            else {
                cursor = db.rawQuery("SELECT * FROM "+ BDConstantes2.TABLA_CONTACTO_PUBLICO+
                        " ORDER BY "+ BDConstantes2.CONTACTO_PUBLICO_CAMPO_CORREO+" ASC", null);
            }
            if(cursor.getCount()==0){
                cursor.close();
                
                return datos_Contacto;
            }
            while (cursor.moveToNext()){
                ItemContactoPublico contactoPublico = Convertidor.createItemContactoPublicoOfCursor(cursor);
                contactoPublico.setContacto(MainActivity.dbWorker.obtenerContacto(contactoPublico.getCorreo()));
                datos_Contacto.add(contactoPublico);
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        
        return datos_Contacto;
    }

    public ItemContactoPublico obtenerContactoPublico(String correo){
        ItemContactoPublico contacto = null;
        String [] parametros={correo};
        String [] campos={"*"};
        Cursor cursor;
        try {
            cursor=db.query(BDConstantes2.TABLA_CONTACTO_PUBLICO,campos,
                    BDConstantes2.CONTACTO_PUBLICO_CAMPO_CORREO+"=? LIMIT 1",parametros,null,null,null);
            if(cursor.getCount()>0){
                cursor.moveToFirst();
                contacto=Convertidor.createItemContactoPublicoOfCursor(cursor);
                contacto.setContacto(MainActivity.dbWorker.obtenerContacto(contacto.getCorreo()));
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return contacto;
    }

    public synchronized void eliminarContacto(String contacto_correo){
        String [] parametros={contacto_correo};
        db.delete(BDConstantes2.TABLA_CONTACTO_PUBLICO, BDConstantes2.CONTACTO_PUBLICO_CAMPO_CORREO+"=?",parametros);
    }
}
