package cu.alexgi.youchat.base_datos2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ConexionBD2 extends SQLiteOpenHelper {



    public ConexionBD2(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
//        Log.e("*****ConexionBD*****","ConexionBD");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BDConstantes2.CREAR_TABLA_CONTACTO_PUBLICO);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
