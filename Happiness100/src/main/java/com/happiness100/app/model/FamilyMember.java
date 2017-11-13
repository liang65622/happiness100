package com.happiness100.app.model;/**
 * Created by Administrator on 2016/8/25.
 */

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 作者：justin on 2016/8/25 11:43
 */
public class FamilyMember implements Parcelable {

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public FamilyMember() {
    }

    protected FamilyMember(Parcel in) {
    }

    public static final Parcelable.Creator<FamilyMember> CREATOR = new Parcelable.Creator<FamilyMember>() {
        @Override
        public FamilyMember createFromParcel(Parcel source) {
            return new FamilyMember(source);
        }

        @Override
        public FamilyMember[] newArray(int size) {
            return new FamilyMember[size];
        }
    };
}
