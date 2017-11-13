package com.happiness100.app.manager;/**
 * Created by Administrator on 2016/9/24.
 */

import android.util.Log;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.happiness100.app.App;
import com.happiness100.app.model.ConversationBackGroudImage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：jiangsheng on 2016/9/24 10:45
 */
public class ConversationBackGroudImageManager {
    private App mApp;
    private static ConversationBackGroudImageManager instance;
    private Map<String, ConversationBackGroudImage> manager;

    private ConversationBackGroudImageManager() {
        manager = new HashMap<String, ConversationBackGroudImage>();
    }

    public static ConversationBackGroudImageManager getInstance() {
        if (instance == null) {
            instance = new ConversationBackGroudImageManager();
        }
        return instance;
    }

    private boolean Add(String key, String val) {
        if (key == null || key.isEmpty()) {
            Log.e("ConversationBackGroud", "Add  key is null");
            return false;
        }

        if (manager.containsKey(key)) {
            Log.w("ConversationBackGroud", "key is exist, id = " + key);
            return false;
        }
        ConversationBackGroudImage iv = new ConversationBackGroudImage();
        iv.setUserId(mApp.getUser().getXf() + "");
        iv.setIndex(key);
        iv.setImageData(val);
        manager.put(key, iv);
        iv.save();
        return true;
    }

    public ConversationBackGroudImage find(String id) {
        if (id == null || id.isEmpty()) {
            Log.e("ConversationBackGroud", "find Id is null");
            return null;
        }

        if (!manager.containsKey(id)) {
            Log.w("ConversationBackGroud", "find not exist, id = " + id);
            return null;
        }
        return manager.get(id);
    }

    public boolean update(String key, String val) {
        ;
        if (key == null || key.isEmpty()) {
            Log.e("ConversationBackGroud", "update is null");
            return false;
        }

        if (!manager.containsKey(key)) {
            Log.w("ConversationBackGroud", "update not exist, id = " + key);
            Add(key, val);
            return true;
        }
        delete(key);
        Add(key, val);
        return true;
    }

    public boolean delete(String id) {
        if (id == null || id.isEmpty()) {
            Log.e("ConversationBackGroud", "id is null");
            return false;
        }

        if (!manager.containsKey(id)) {
            Log.w("ConversationBackGroud", "delete not exist, id = " + id);
            return false;
        }
        new Delete().from(ConversationBackGroudImage.class).where("Tag = ? and userId = ?", id, mApp.getUser().getXf() + "").execute();
        manager.remove(id);
        return true;
    }

    public void Init(App app) {
        mApp = app;
        manager.clear();
        List<ConversationBackGroudImage> list = new Select().from(ConversationBackGroudImage.class).where("userId = ?", mApp.getUser().getXf() + "").execute();

        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); ++i) {
                ConversationBackGroudImage content = list.get(i);
                manager.put(content.getTag(), content);
            }
        }
    }
}

