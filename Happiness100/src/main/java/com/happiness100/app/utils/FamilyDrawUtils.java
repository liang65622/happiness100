package com.happiness100.app.utils;/**
 * Created by Administrator on 2016/9/7.
 */

import com.happiness100.app.model.ClanRela;
import com.happiness100.app.model.ClanRela.FamilyUnit;
import com.happiness100.app.model.ClanRela.PUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 作者：justin on 2016/9/7 23:05
 */
public class FamilyDrawUtils {
    private ClanRela mClanRela;

    public FamilyDrawUtils(ClanRela clanRela) {
        mClanRela = clanRela;
    }

    public void init() {
        initBeifen();
    }

    private void initBeifen() {

        PUnit user = mClanRela.uIndex2Punit.get(mClanRela.creater);
        List<Long> myBeifen = new ArrayList<>();
        myBeifen.add(user.unitId);

        Iterator iter = mClanRela.uIndex2Punit.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Long key = (Long) entry.getKey();
            List<Integer> vals = (List<Integer>) entry.getValue();
            for (int id : vals) {
//                if (userId == id) {
//                    return key.intValue();
//                }
            }
        }
    }


    public PUnit getWife(PUnit user) {
        if (user != null && user.dFaId != 0) {
            FamilyUnit family = mClanRela.familyUnitMap.get(user.dFaId);
            return mClanRela.uIndex2Punit.get(family.mother);
        } else {
            return null;
        }
    }

    public PUnit getFater(PUnit user) {
        if (user != null && (Long) user.uFaId != 0) {
            FamilyUnit family = mClanRela.familyUnitMap.get((Long) user.uFaId);
            return mClanRela.uIndex2Punit.get(family.father);
        } else {
            return null;
        }
    }

    private Map<Integer, List<Integer>> beifenMaps = new HashMap<>();

    public int getBeifen(long userId) {
        Iterator iter = beifenMaps.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Long key = (Long) entry.getKey();
            List<Integer> vals = (List<Integer>) entry.getValue();
            for (int id : vals) {
                if (userId == id) {
                    return key.intValue();
                }
            }
        }
        return -9999;
    }


    public List<Long> getBrothers(PUnit user) {
        if (user != null && user.uFaId != 0) {
            FamilyUnit family = mClanRela.familyUnitMap.get(user.uFaId);
            List<Long> ids = family.children;
            List<Long> tempIds = new ArrayList<>(ids);
            removeMyself(user.unitId, tempIds);
//            ids.remove(user.unitId);
            return tempIds;
        } else {
            return null;
        }

    }

    private void removeMyself(long unitId, List<Long> ids) {
        for (int i = 0; i < ids.size(); i++) {
            if (unitId == ids.get(i)) {
                ids.remove(i);
            }
        }
    }

    public PUnit getMother(PUnit user) {
        if (user != null && (Long) user.uFaId != 0) {
            FamilyUnit family = mClanRela.familyUnitMap.get((Long) user.uFaId);
            return mClanRela.uIndex2Punit.get(family.mother);
        } else {
            return null;
        }
    }

    public List<Long> getChilds(PUnit user) {
        if (user != null && user.dFaId != 0) {
            FamilyUnit family = mClanRela.familyUnitMap.get(user.dFaId);
            List<Long> ids = family.children;
            return ids;
        } else {
            return null;
        }
    }

    //伴侣
    public PUnit getMate(PUnit user) {
        if (user != null && user.dFaId != 0) {
            FamilyUnit family = mClanRela.familyUnitMap.get(user.dFaId);
            if ((user.rank + "").length() == 3) {
                return mClanRela.uIndex2Punit.get(family.father);
            } else {
                return mClanRela.uIndex2Punit.get(family.mother);
            }
        } else {
            return null;
        }
    }

    //获取家长的辈份
    public int getBeifen(FamilyUnit familyUnit) {
        long fatherId = familyUnit.father;
        if (fatherId != 0) {
            return mClanRela.uIndex2Punit.get(fatherId).bfIndex;
        }
        long motherId = familyUnit.mother;
        if (motherId != 0) {
            return mClanRela.uIndex2Punit.get(motherId).bfIndex;
        }
        return -99;
    }

    //判断是不是创建者的直系

    public boolean isCreaterFather(long unitId,long createrId) {
        if(unitId<=0){
            return false;
        }
        PUnit creater = mClanRela.uIndex2Punit.get(createrId);
        long uFid =  creater.uFaId;
        long dFid =  creater.dFaId;

        if(uFid!=0){
            FamilyUnit family =  mClanRela.familyUnitMap.get(uFid);
            if(family.father==unitId||family.mother==unitId){
                return true;
            }else{
                return isCreaterFather(unitId,family.father);
            }
        }else{

        }


        return false;
    }

//    public boolean isCreaterChild(long unitId, long createrId) {
//        if(unitId<=0){
//            return false;
//        }
//        PUnit creater = mClanRela.uIndex2Punit.get(createrId);
//        long dFid =  creater.dFaId;
//
//        if(dFid!=0){
//            FamilyUnit family =  mClanRela.familyUnitMap.get(dFid);
//            if(family.children.get(0)==unitId){
//                return true;
//            }else{
//                return isCreaterChild(unitId,family.father);
//            }
//        }else{
//
//        }
//        return false;
//
//    }
}
