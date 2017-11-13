package com.happiness100.app.manager;/**
 * Created by Administrator on 2016/9/13.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.happiness100.app.App;
import com.happiness100.app.R;
import com.happiness100.app.model.User;
import com.happiness100.app.ui.activity.MainActivity;
import com.happiness100.app.utils.Constants;
import com.justin.utils.SharedPreferencesContext;

import java.util.Calendar;

import io.rong.push.RongPushClient;
import io.rong.push.notification.PushMessageReceiver;
import io.rong.push.notification.PushNotificationMessage;

/**
 * 作者：jiangsheng on 2016/9/13 21:48
 */
public class RongCloudPushReceiverManager extends PushMessageReceiver {
    //onNotificationMessageArrived 用来接收服务器发来的通知栏消息(消息到达客户端时触发)，默认return false，
    // 通知消息会以融云 SDK 的默认形式展现。如果需要自定义通知栏的展示，在这里实现⾃己的通知栏展现代码，
    // 同时 return true 即可。
    static NotificationCompat.Builder s_Builder;
    static int notify_id = 100;
    @Override
    public boolean onNotificationMessageArrived(Context context, PushNotificationMessage pushNotificationMessage) {
        String id = pushNotificationMessage.getTargetId();
        String content = pushNotificationMessage.getPushContent();

        String pushTitle = pushNotificationMessage.getPushTitle();
        String pushId =  pushNotificationMessage.getPushId();
        String pushData =  pushNotificationMessage.getPushData();
        String pushFlag =  pushNotificationMessage.getPushFlag();

        RongPushClient.ConversationType type = pushNotificationMessage.getConversationType();

        String senderid = pushNotificationMessage.getSenderId();
        String senderName = pushNotificationMessage.getSenderName();
        Uri senderPortrait = pushNotificationMessage.getSenderPortrait();

        NotificationManager NotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        if (s_Builder == null)
        {
            s_Builder = new NotificationCompat.Builder(context);
            s_Builder.setContentTitle(pushTitle)
                    .setContentIntent(getDefalutIntent(context))
                    .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
                    .setPriority(Notification.PRIORITY_HIGH)//设置该通知优先级
                    .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                    .setSmallIcon(R.mipmap.ic_launcher);
            s_Builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher));
        }
        App app =  (App)context.getApplicationContext();
        User user = app.getUser();
        if (user != null)
        {
            //设置提醒方式
            int notifyType = 0;
            SharedPreferences sharedPreferences = SharedPreferencesContext.getInstance().getSharedPreferences();
            if(sharedPreferences.getBoolean(Constants.Function.AUDIO+user.getXf(),true))
                notifyType = notifyType|Notification.DEFAULT_SOUND;
            if(sharedPreferences.getBoolean(Constants.Function.SHAKE+user.getXf(),true))
                notifyType = notifyType|Notification.DEFAULT_VIBRATE;
            s_Builder.setDefaults(notifyType);

            //是否显示消息类容
            if(sharedPreferences.getBoolean(Constants.Function.SHOWNOTIFYCONTENT+user.getXf(),true))
            {
                s_Builder .setContentText(content);
                s_Builder.setTicker(content);
            }
            else
            {
                s_Builder .setContentText("你收到了一条消息");
                s_Builder.setTicker(content);
            }

            //消息免打扰
            int status = sharedPreferences.getInt(Constants.Function.NODISTURBING+user.getXf(),Constants.NoDisturbStatus.CLOSE);
            if (status == Constants.NoDisturbStatus.CLOSE)
            {
                Notification notification = s_Builder.build();
                notification.flags = Notification.FLAG_AUTO_CANCEL;
                NotificationManager.notify(notify_id, notification);
                notify_id++;
            }
            else if (status == Constants.NoDisturbStatus.OPENLIMITE)
            {
                Calendar c = Calendar.getInstance();
               int hour = c.get(Calendar.HOUR);
                if (hour >= 22 ||hour <= 8)
                {
                    s_Builder.setDefaults(0);
                }
                Notification notification = s_Builder.build();
                notification.flags = Notification.FLAG_AUTO_CANCEL;
                NotificationManager.notify(notify_id, notification);
                notify_id++;
            }
            else
            {
                s_Builder.setDefaults(0);
                Notification notification = s_Builder.build();
                notification.flags = Notification.FLAG_AUTO_CANCEL;
                NotificationManager.notify(notify_id, notification);
                notify_id++;
            }
        }
        Log.e("PUSH","~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        return true;
    }
    //onNotificationMessageClicked 是在⽤户点击通知栏消息时触发 (注意:如果⾃定义了通知栏的展现，则不会触发)，
    // 默认 return false 。如果需要自定义点击通知时的跳转，
    // return true 即可
    @Override
    public boolean onNotificationMessageClicked(Context context, PushNotificationMessage pushNotificationMessage) {
        return true;
    }

    public PendingIntent getDefalutIntent(Context context){
        Intent intent = new Intent(context,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        return pendingIntent;
    }
}
