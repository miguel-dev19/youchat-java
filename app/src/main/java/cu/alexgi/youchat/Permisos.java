package cu.alexgi.youchat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Permisos {

    public static int REQUEST_CODE_ALL_PERMISSION=333;
    public static int REQUEST_CODE_ONE_PERMISSION=111;

    private Activity activity;
    private Context context;

    public Permisos(Activity activity, Context context){
        this.activity = activity;
        this.context = context;
    }

    public void requestAllPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return;

        String[] permission=new String[7];
        int cant=0;
        boolean estaDado = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
        if (!estaDado) permission[cant++]=Manifest.permission.WRITE_EXTERNAL_STORAGE;

        estaDado = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
        if (!estaDado) permission[cant++]=Manifest.permission.READ_EXTERNAL_STORAGE;

        estaDado = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
        if (!estaDado) permission[cant++]=Manifest.permission.CAMERA;

        estaDado = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS)
                == PackageManager.PERMISSION_GRANTED;
        if (!estaDado) permission[cant++]=Manifest.permission.WRITE_CONTACTS;

        estaDado = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED;
        if (!estaDado) permission[cant++]=Manifest.permission.READ_CONTACTS;

        estaDado = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
        if (!estaDado) permission[cant++]=Manifest.permission.RECORD_AUDIO;

        estaDado = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED;
        if (!estaDado) permission[cant++]=Manifest.permission.CALL_PHONE;

        if(cant>0){
            String[] permisosOK = new String[cant];
            for(int i=0; i<cant; i++) {
                permisosOK[i]=permission[i];
                ActivityCompat.shouldShowRequestPermissionRationale(activity, permission[i]);
            }
                ActivityCompat.requestPermissions( activity, permisosOK, REQUEST_CODE_ALL_PERMISSION);
        }
    }

    public boolean requestPermissionAlmacenamiento(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;

        String[] permission=new String[2];
        int cant=0;
        boolean estaDado = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
        if (!estaDado) permission[cant++]=Manifest.permission.WRITE_EXTERNAL_STORAGE;

        estaDado = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
        if (!estaDado) permission[cant++]=Manifest.permission.READ_EXTERNAL_STORAGE;
        if(cant>0){
            String[] permisosOK = new String[cant];
            for(int i=0; i<cant; i++) {
                permisosOK[i]=permission[i];
                ActivityCompat.shouldShowRequestPermissionRationale(activity, permission[i]);
            }
            ActivityCompat.requestPermissions( activity, permisosOK, REQUEST_CODE_ONE_PERMISSION);
            return false;
        }
        else return true;
    }

    public boolean requestPermissionCamera(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;

        String[] permission=new String[3];
        int cant=0;
        boolean estaDado = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
        if (!estaDado) permission[cant++]=Manifest.permission.CAMERA;

        estaDado = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
        if (!estaDado) permission[cant++]=Manifest.permission.WRITE_EXTERNAL_STORAGE;

        estaDado = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
        if (!estaDado) permission[cant++]=Manifest.permission.READ_EXTERNAL_STORAGE;

        if(cant>0){
            String[] permisosOK = new String[cant];
            for(int i=0; i<cant; i++) {
                permisosOK[i]=permission[i];
                ActivityCompat.shouldShowRequestPermissionRationale(activity, permission[i]);
            }
            ActivityCompat.requestPermissions( activity, permisosOK, REQUEST_CODE_ONE_PERMISSION);
            return false;
        }
        else return true;
    }

    public boolean requestPermissionContactos(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;

        String[] permission=new String[2];
        int cant=0;
        boolean estaDado = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS)
                == PackageManager.PERMISSION_GRANTED;
        if (!estaDado) permission[cant++]=Manifest.permission.WRITE_CONTACTS;

        estaDado = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED;
        if (!estaDado) permission[cant++]=Manifest.permission.READ_CONTACTS;
        if(cant>0){
            String[] permisosOK = new String[cant];
            for(int i=0; i<cant; i++) {
                permisosOK[i]=permission[i];
                ActivityCompat.shouldShowRequestPermissionRationale(activity, permission[i]);
            }
            ActivityCompat.requestPermissions( activity, permisosOK, REQUEST_CODE_ONE_PERMISSION);
            return false;
        }
        else return true;
    }

    public boolean requestPermissionAudio(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;

        String[] permission=new String[3];
        int cant=0;
        boolean estaDado = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
        if (!estaDado) permission[cant++]=Manifest.permission.RECORD_AUDIO;

        estaDado = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
        if (!estaDado) permission[cant++]=Manifest.permission.WRITE_EXTERNAL_STORAGE;

        estaDado = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
        if (!estaDado) permission[cant++]=Manifest.permission.READ_EXTERNAL_STORAGE;

        if(cant>0){
            String[] permisosOK = new String[cant];
            for(int i=0; i<cant; i++) {
                permisosOK[i]=permission[i];
                ActivityCompat.shouldShowRequestPermissionRationale(activity, permission[i]);
            }
            ActivityCompat.requestPermissions( activity, permisosOK, REQUEST_CODE_ONE_PERMISSION);
            return false;
        }
        else return true;
    }

    public boolean requestPermissionTelefono(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;

        boolean estaDado = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED;
        if (!estaDado) {
            ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CALL_PHONE);
            ActivityCompat.requestPermissions( activity,
                    new String[]{Manifest.permission.CALL_PHONE},
                    REQUEST_CODE_ONE_PERMISSION);
        }
        return estaDado;
    }
}
