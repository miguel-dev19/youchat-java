package cu.alexgi.youchat;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class WorkerServiceCopiaSeguridad extends Worker {

    private Context context;

    public WorkerServiceCopiaSeguridad(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        if(context!=null){
            SharedPreferences preferencias = context.getSharedPreferences("Memoria", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferencias.edit();
            editor.putBoolean("puedeHacerCopiaSeguridad", true);
            editor.apply();

            return Result.success();
        }
        return Result.failure();
    }
}
