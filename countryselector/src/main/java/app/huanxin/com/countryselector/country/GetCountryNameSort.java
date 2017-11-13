/*      						
 * Copyright 2010 Beijing Xinwei, Inc. All rights reserved.
 * 
 * History:
 * ------------------------------------------------------------------------------
 * Date    	|  Who  		|  What  
 * 2015��3��20��	| duanbokan 	| 	create the file                       
 */

package app.huanxin.com.countryselector.country;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 
 * ȡ��������ĸ��ģ��ƥ���ѯ
 * 
 * <p>
 * ����ϸ����
 * </p>
 * 
 * @author duanbokan
 * 
 */

public class GetCountryNameSort
{
	
	CharacterParserUtil characterParser = CharacterParserUtil.getInstance();
	
	String chReg = "[\\u4E00-\\u9FA5]+";// �����ַ���ƥ��
	
	/***
	 * ������ת��Ϊƴ�����������ĸ
	 * 
	 * @param name
	 * @return
	 */
	public String getSortLetter(String name)
	{
		String letter = "#";
		if (name == null)
		{
			return letter;
		}
		// ����ת����ƴ��
		String pinyin = characterParser.getSelling(name);
		String sortString = pinyin.substring(0, 1).toUpperCase(Locale.CHINESE);
		
		// ������ʽ���ж�����ĸ�Ƿ���Ӣ����ĸ
		if (sortString.matches("[A-Z]"))
		{
			letter = sortString.toUpperCase(Locale.CHINESE);
		}
		return letter;
	}
	
	/***
	 * ȡ����ĸ
	 * 
	 * @param sortKey
	 * @return
	 */
	public String getSortLetterBySortKey(String sortKey)
	{
		if (sortKey == null || "".equals(sortKey.trim()))
		{
			return null;
		}
		String letter = "#";
		// ����ת����ƴ��
		String sortString = sortKey.trim().substring(0, 1).toUpperCase(Locale.CHINESE);
		// ������ʽ���ж�����ĸ�Ƿ���Ӣ����ĸ
		if (sortString.matches("[A-Z]"))
		{
			letter = sortString.toUpperCase(Locale.CHINESE);
		}
		return letter;
	}
	
	/***
	 * �����������ݽ��в�ѯ
	 * 
	 * @param str
	 *            ��������
	 * @param list
	 *            ��Ҫ��ѯ��List
	 * @return ��ѯ���
	 */
	public List<CountrySortModel> search(String str, List<CountrySortModel> list)
	{
		List<CountrySortModel> filterList = new ArrayList<CountrySortModel>();// ���˺��list
		// if (str.matches("^([0-9]|[/+])*$")) {// ������ʽ ƥ�����
		if (str.matches("^([0-9]|[/+]).*"))
		{// ������ʽ ƥ�������ֻ��߼Ӻſ�ͷ���ַ���(�����˴��ո�-�ָ�ĺ���)
			String simpleStr = str.replaceAll("\\-|\\s", "");
			for (CountrySortModel contact : list)
			{
				if (contact.countryName != null && contact.countryName != null)
				{
					if (contact.simpleCountryNumber.contains(simpleStr)
							|| contact.countryName.contains(str))
					{
						if (!filterList.contains(contact))
						{
							filterList.add(contact);
						}
					}
				}
			}
		}
		else
		{
			for (CountrySortModel contact : list)
			{
				if (contact.countryNumber != null && contact.countryName != null)
				{
					// ����ȫƥ��,��������ĸ��ƴƥ��,����ȫ��ĸƥ��
					if (contact.countryName.toLowerCase(Locale.CHINESE).contains(
							str.toLowerCase(Locale.CHINESE))
							|| contact.countrySortKey.toLowerCase(Locale.CHINESE).replace(" ", "")
									.contains(str.toLowerCase(Locale.CHINESE))
							|| contact.sortToken.simpleSpell.toLowerCase(Locale.CHINESE).contains(
									str.toLowerCase(Locale.CHINESE))
							|| contact.sortToken.wholeSpell.toLowerCase(Locale.CHINESE).contains(
									str.toLowerCase(Locale.CHINESE)))
					{
						if (!filterList.contains(contact))
						{
							filterList.add(contact);
						}
					}
				}
			}
		}
		return filterList;
	}
	
}
