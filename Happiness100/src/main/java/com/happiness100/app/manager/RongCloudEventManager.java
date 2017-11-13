package com.happiness100.app.manager;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;
import android.util.Log;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.happiness100.app.App;
import com.happiness100.app.R;
import com.happiness100.app.message.DynamicMessage;
import com.happiness100.app.model.FamilyBaseInfo;
import com.happiness100.app.model.Friend;
import com.happiness100.app.model.NotifyMessage;
import com.happiness100.app.model.User;
import com.happiness100.app.utils.Constants;
import com.justin.utils.GsonUtils;
import com.justin.utils.SharedPreferencesContext;

import java.util.Calendar;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.AnnotationNotFoundException;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.CommandNotificationMessage;
import io.rong.message.ContactNotificationMessage;
import io.rong.message.ImageMessage;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.ProfileNotificationMessage;
import io.rong.message.RichContentMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

/**
 * 融云SDK事件监听处理。
 * 把事件统一处理，开发者可直接复制到自己的项目中去使用。
 * <p/>
 * 该类包含的监听事件有：
 * 1、消息接收器：OnReceiveMessageListener。
 * 2、发出消息接收器：OnSendMessageListener。
 * 3、用户信息提供者：GetUserInfoProvider。
 * 4、好友信息提供者：GetFriendsProvider。
 * 5、群组信息提供者：GetGroupInfoProvider。
 * 6、会话界面操作的监听器：ConversationBehaviorListener。
 * 7、连接状态监听器，以获取连接相关状态：ConnectionStatusListener。
 * 8、地理位置提供者：LocationProvider。
 */
public class RongCloudEventManager {
    private static RongCloudEventManager instance;
    App mApp;
    SoundPool soundPool;
    Vibrator vib;

    public PendingIntent getDefalutIntent(int flags) {
        PendingIntent pendingIntent = PendingIntent.getActivity(mApp, 1, new Intent(), flags);
        return pendingIntent;
    }

    public static RongCloudEventManager getInstance() {
        if (instance == null) {
            instance = new RongCloudEventManager();
        }
        return instance;
    }

    private void InitMessageRecevier() {
        //消息注册
        try {
            // 注册一个自定义消息类型。
            RongIMClient.registerMessageType(DynamicMessage.class);
        } catch (AnnotationNotFoundException e) {
            e.printStackTrace();
        }
        vib = (Vibrator) mApp.getSystemService(Service.VIBRATOR_SERVICE);
        soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        soundPool.load(mApp, R.raw.newmsgaudio, 1);

        //消息监听
        RongIM.getInstance().setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener() {
            @Override
            public boolean onReceived(Message message, int i) {
                MessageContent messageContent = message.getContent();
                String TAG = "RongCloudEventManager";
                if (messageContent instanceof TextMessage) {//文本消息
                    TextMessage textMessage = (TextMessage) messageContent;
                    Log.d(TAG, "onReceived-TextMessage:" + textMessage.getContent());
                } else if (messageContent instanceof ImageMessage) {//图片消息
                    ImageMessage imageMessage = (ImageMessage) messageContent;
                    Log.d(TAG, "onReceived-ImageMessage:" + imageMessage.getRemoteUri());
                } else if (messageContent instanceof VoiceMessage) {//语音消息
                    VoiceMessage voiceMessage = (VoiceMessage) messageContent;
                    Log.d(TAG, "onReceived-voiceMessage:" + voiceMessage.getUri().toString());
                } else if (messageContent instanceof RichContentMessage) {//图文消息
                    RichContentMessage richContentMessage = (RichContentMessage) messageContent;
                    Log.d(TAG, "onReceived-RichContentMessage:" + richContentMessage.getContent());
                } else if (messageContent instanceof ContactNotificationMessage) {//联系人（好友）操作通知消息
                    ContactNotificationMessage contactMessage = (ContactNotificationMessage) messageContent;
                    Log.d(TAG, "onReceived-ContactNotificationMessage:" + contactMessage.getMessage());
                } else if (messageContent instanceof ProfileNotificationMessage) {//资料变更通知消息
                    ProfileNotificationMessage profileMessage = (ProfileNotificationMessage) messageContent;
                    Log.d(TAG, "onReceived-ProfileNotificationMessage:" + profileMessage.getExtra());
                } else if (messageContent instanceof CommandNotificationMessage) {//命令通知消息
                    CommandNotificationMessage commantMessage = (CommandNotificationMessage) messageContent;
                    Log.d(TAG, "onReceived-CommandNotificationMessage:" + commantMessage.getName());
                } else if (messageContent instanceof InformationNotificationMessage) {//小灰条消息
                    InformationNotificationMessage infoMessage = (InformationNotificationMessage) messageContent;
                    Log.d(TAG, "onReceived-GroupInvitationNotification:" + infoMessage.getMessage());
                } else if (messageContent instanceof DynamicMessage) {//自定义消息
                    DynamicMessage dynamicMessage = (DynamicMessage) messageContent;
                    Log.d(TAG, "onReceived-DynamicMessage:" + dynamicMessage.getContent());

                    Gson gson = new Gson();
                    String[] array = gson.fromJson(dynamicMessage.getContent(), String[].class);

                    switch (array[0]) {
                        case "100":
                            if (FriendsManager.getInstance().findFriend(array[1]) == null) {
                                ParseNotifyMessage(array);
                            } else {
                                return true;
                            }
                        case "102":
                            List<FamilyBaseInfo> list = mApp.getUser().getFamilyBaseInfos();
                            if (list != null && list.size() > 0) {
                                for (int index = 0; index < list.size(); ++index) {
                                    FamilyBaseInfo info = list.get(index);
                                    if (array[1].compareTo(info.getClanId() + "") == 0) {
                                        return true;
                                    }
                                }
                            }
                            ParseNotifyMessage(array);
                        case "103":
                            ParseNotifyMessage(array);
                            break;
                        case "101":
                            ParseAddFriendSucceedMessage(array[1]);
                            break;
                        case "104":
                            Intent intent = new Intent("com.happniess100.app.applymsg");
                            SharedPreferencesContext.getInstance().getSharedPreferences().edit().putBoolean(Constants.SpKey.HAS_APPLY_TIPS, true).commit();
                            mApp.sendBroadcast(intent);
                            break;
                        case "105":
                            FriendsManager.getInstance().deleteFriend(array[1]);
                            RemoveTargetConversationList(array[1]);
                            break;
                    }
                    return PlayEffect(true);
                } else {
                    Log.d(TAG, "onReceived-其他消息，自己来判断处理");
                }
                return PlayEffect(false);
            }

        });
    }


    void RemoveTargetConversationList(String target)
    {
        RongIM.getInstance().clearMessages(Conversation.ConversationType.SYSTEM, target, new RongIMClient.ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {

            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
        RongIM.getInstance().removeConversation(Conversation.ConversationType.SYSTEM, target, new RongIMClient.ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {

            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
        RongIM.getInstance().clearMessages(Conversation.ConversationType.PRIVATE, target, new RongIMClient.ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {

            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
        RongIM.getInstance().removeConversation(Conversation.ConversationType.PRIVATE, target, new RongIMClient.ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {

            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }

    boolean PlayEffect(boolean UseCustomAudio)
    {
        int playMode = 0;
        User user = mApp.getUser();
        SharedPreferences sharedPreferences = SharedPreferencesContext.getInstance().getSharedPreferences();
        if (UseCustomAudio== true &&sharedPreferences.getBoolean(Constants.Function.AUDIO + user.getXf(), true))
            playMode = playMode | 1;
        if (sharedPreferences.getBoolean(Constants.Function.SHAKE + user.getXf(), true))
            playMode = playMode | 1 << 1;
        int status = sharedPreferences.getInt(Constants.Function.NODISTURBING + user.getXf(), Constants.NoDisturbStatus.CLOSE);
        if (status == Constants.NoDisturbStatus.CLOSE) {

        } else if (status == Constants.NoDisturbStatus.OPENLIMITE) {
            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR);
            if (hour >= 22 || hour <= 8) {
                playMode = 0;
                return true;
            }
        } else {
            playMode = 0;
            return true;
        }
        switch (playMode) {
            case 1:
                soundPool.play(1, 1, 1, 0, 0, 1);
                break;
            case 2:
                vib.vibrate(1000);
                break;
            case 3:
                soundPool.play(1, 1, 1, 0, 0, 1);
                vib.vibrate(1000);
                break;
        }

        if (sharedPreferences.getBoolean(Constants.Function.AUDIO + user.getXf(), true))
        {
            return false;
        }
        return true;
    }

    public void Init(App APP) {
        if (mApp != null)
            return;
        mApp = APP;
        InitMessageRecevier();
    }

    void ParseAddFriendSucceedMessage(String json) {
        Friend friend = GsonUtils.parseJSON(json, Friend.class);
        if (friend != null) {
            FriendsManager.getInstance().addFriend(friend);
        }
    }


    void ParseNotifyMessage(String[] array) {
        NotifyMessage checkItem = new Select().from(NotifyMessage.class).where("UserId = ? and senderId = ? and type = ?", mApp.getUser().getXf() + "", array[1].toString(), array[0].toString().toString()).executeSingle();
        if (checkItem != null) {
            Log.d("ParseNotifyMessage", "ParseNotifyMessage-数据库已经有这条消息,要更新   senderId = " + array[1].toString());
            new Delete().from(NotifyMessage.class).where("UserId = ? and senderId = ? and type = ?", mApp.getUser().getXf() + "", array[1].toString(), array[0].toString().toString()).execute();
        }

        NotifyMessage notify = new NotifyMessage();
        if (array[0] != null) {
            notify.setNotifyType(array[0].toString());
        } else {
            notify.setNotifyType("");
        }
        if (array[1] != null) {
            notify.setSenderId(array[1].toString());
        } else {
            notify.setSenderId("");
        }
        if (array[2] != null) {
            notify.setSenderName(array[2].toString());
        } else {
            notify.setSenderName("");
        }
        if (array[3] != null) {
            notify.setImage(array[3].toString());
        } else {
            notify.setImage("");
        }
        if (array[4] != null) {
            notify.setRemark(array[4].toString());
        } else {
            notify.setRemark("");
        }
        notify.setUserId(mApp.getUser().getXf() + "");
        notify.save();
        Intent mIntent = new Intent("新的消息");
        mIntent.putExtra("data", notify);
        //发送广播
        mApp.sendBroadcast(mIntent);
    }
}