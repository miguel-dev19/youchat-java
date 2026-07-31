package cu.alexgi.youchat;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class WorkerService extends Worker {

    private Context context;

    public WorkerService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {

        if(context!=null){
            SharedPreferences preferencias = context.getSharedPreferences("Memoria", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferencias.edit();
            editor.putInt("cantPostSubidosHoy", 0);
            editor.putInt("cantComentarioPostSubidosHoy", 0);
            editor.apply();

            return Result.success();
        }


//        Log.e("WORKER","SERVICE");
//        context.startService(new Intent(context, ChatService.class));
        return Result.failure();
    }
}
