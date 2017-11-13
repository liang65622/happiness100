package com.happiness100.app.utils;/**
 * Created by Administrator on 2016/9/13.
 */

import com.activeandroid.query.Select;
import com.happiness100.app.model.FamilyIndex;

/**
 * 作者：justin on 2016/9/13 17:57
 */
public final class SQLUtils {
    public static FamilyIndex.Member getMemberImage(long userId) {
        FamilyIndex.Member member = new Select()
                .from(FamilyIndex.Member.class)
                .where("userid = ?", userId)
                .executeSingle();
        if (member == null) {
            return new FamilyIndex.Member();
        }
        return member;
    }

    public static void saveOrUpdateMember(FamilyIndex.Member member) {
//        if (new Select()
//                .from(FamilyIndex.Member.class)
//                .where("userid = ?", member.getUserid())
//                .executeSingle() != null) {
//
//            new Update(FamilyIndex.Member.class).set("age=?," + "name=?", age, name).execute();
//        }
//        new Update(FamilyIndex.Member.class).
    }

    public static FamilyIndex.Member getFamilyMember(String clanId, long userId) {
        return new Select()
                .from(FamilyIndex.Member.class)
                .where("userid = ?, clanId=?", clanId, userId)
                .executeSingle();
    }
}
