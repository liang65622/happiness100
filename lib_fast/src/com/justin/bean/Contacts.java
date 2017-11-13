package com.justin.bean;/**
 * Created by Administrator on 2016/8/31.
 */

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 作者：justin on 2016/8/31 16:10
 */

public class Contacts implements Parcelable {

    private String name;
    private List<String> phones;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPhones() {
        return phones;
    }

    public void setPhones(List<String> phones) {
        this.phones = phones;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeStringList(this.phones);
    }

    public Contacts() {
    }

    protected Contacts(Parcel in) {
        this.name = in.readString();
        this.phones = in.createStringArrayList();
    }

    public static final Parcelable.Creator<Contacts> CREATOR = new Parcelable.Creator<Contacts>() {
        @Override
        public Contacts createFromParcel(Parcel source) {
            return new Contacts(source);
        }

        @Override
        public Contacts[] newArray(int size) {
            return new Contacts[size];
        }
    };
}
