/*      						
 * Copyright 2010 Beijing Xinwei, Inc. All rights reserved.
 * 
 * History:
 * ------------------------------------------------------------------------------
 * Date    	|  Who  		|  What  
 * 2015��3��21��	| duanbokan 	| 	create the file                       
 */

package app.huanxin.com.countryselector.country;

/**
 * 
 * ���Ҫ����
 * 
 * <p>
 * ����ϸ����
 * </p>
 * 
 * @author duanbokan
 * 
 */

public class CountrySortModel extends CountryModel

{
	// ��ʾ����ƴ��������ĸ
	public String sortLetters;
	
	public CountrySortToken sortToken = new CountrySortToken();
	
	public CountrySortModel(String name, String number, String countrySortKey)
	{
		super(name, number, countrySortKey);
	}
	
}
