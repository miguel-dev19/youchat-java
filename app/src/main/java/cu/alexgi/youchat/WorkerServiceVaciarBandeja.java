package cu.alexgi.youchat;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class WorkerServiceVaciarBandeja extends Worker {

    private Context context;

    public WorkerServiceVaciarBandeja(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        if(context!=null){
            SharedPreferences preferencias = context.getSharedPreferences("Memoria", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferencias.edit();
            editor.putBoolean("puedeVaciarBandeja", true);
            editor.apply();

            return Result.success();
        }
        return Result.failure();
    }
}
