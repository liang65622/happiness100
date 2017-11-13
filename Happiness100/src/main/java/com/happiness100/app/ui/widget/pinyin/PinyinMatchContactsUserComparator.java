package com.happiness100.app.ui.widget.pinyin;

import com.happiness100.app.model.MatchContactsUser;

import java.util.Comparator;

/**
 * 
 * @author 
 *
 */
public class PinyinMatchContactsUserComparator implements Comparator<MatchContactsUser> {


	public static PinyinMatchContactsUserComparator instance = null;

	public static PinyinMatchContactsUserComparator getInstance() {
		if (instance == null) {
			instance = new PinyinMatchContactsUserComparator();
		}
		return instance;
	}

	public int compare(MatchContactsUser o1, MatchContactsUser o2) {
		if (o1.getLetters().equals("@")
				|| o2.getLetters().equals("#")) {
			return -1;
		} else if (o1.getLetters().equals("#")
				|| o2.getLetters().equals("@")) {
			return 1;
		} else {
			return o1.getLetters().compareTo(o2.getLetters());
		}
	}

}
