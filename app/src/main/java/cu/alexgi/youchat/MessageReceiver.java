package cu.alexgi.youchat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class MessageReceiver extends BroadcastReceiver {

    public MessageReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent!=null){
            if(intent.getAction().equals("cu.youchat.reboot")
                    || intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
                if(context!=null && YouChatApplication.chatService == null){
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                        context.startService(new Intent(context, ChatService.class));
                    }
                }
            }
        }
    }
}