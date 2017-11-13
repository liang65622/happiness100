package com.justin.utils;/**
 * Created by Administrator on 2016/8/31.
 */

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.justin.bean.Contacts;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：justin on 2016/8/31 16:06
 */
public class PhoneUtils {

      public static ArrayList<Contacts> getAllContacts(Context context){

        Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        int contactIdIndex = 0;
        int nameIndex = 0;

        if (cursor.getCount() > 0) {
            contactIdIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        }


          ArrayList<Contacts> contactsList =  new ArrayList<>();

        while (cursor.moveToNext()) {
            Contacts contacts =  new Contacts();

            String contactId = cursor.getString(contactIdIndex);
            String name = cursor.getString(nameIndex);
            contacts.setName(name);
            /*
             * 查找该联系人的phone信息
             */
            Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId,
                    null, null);
            int phoneIndex = 0;
            if (phones.getCount() > 0) {
                phoneIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            }

            List<String> phoneList = new ArrayList<>();
            while (phones.moveToNext()) {
                String phoneNumber = phones.getString(phoneIndex);
                phoneNumber = phoneNumber.replaceAll("-", "").replaceAll(" ", "");
                phoneList.add(phoneNumber);
            }
            contacts.setPhones(phoneList);
            contactsList.add(contacts);
        }
        return contactsList;
    }

}
