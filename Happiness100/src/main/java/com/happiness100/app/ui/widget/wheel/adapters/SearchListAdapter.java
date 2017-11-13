package com.happiness100.app.ui.widget.wheel.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.happiness100.app.R;
import com.happiness100.app.model.Friend;
import com.happiness100.app.ui.widget.CircleImageView;
/**
 * Created by AMing on 15/11/13.
 * Company RongCloud
 */
public class SearchListAdapter extends BaseAdapter<Friend>{


    private ViewHolder holder;


    public SearchListAdapter(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.searchlist_item_layout, null);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    class ViewHolder {
        CircleImageView mHead;
        TextView mUserName;
        TextView mUserId;

    }
}
