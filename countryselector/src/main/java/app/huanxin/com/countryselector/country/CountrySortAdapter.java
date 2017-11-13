/*      						
 * Copyright 2010 Beijing Xinwei, Inc. All rights reserved.
 * 
 * History:
 * ------------------------------------------------------------------------------
 * Date    	|  Who  		|  What  
 * 2015��3��21��	| duanbokan 	| 	create the file                       
 */

package app.huanxin.com.countryselector.country;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.huanxin.com.countryselector.R;


/**
 * 
 * ������ѡ��
 * 
 * <p>
 * ����ϸ����
 * </p>
 * 
 * @author duanbokan
 * 
 */

public class CountrySortAdapter extends BaseAdapter implements SectionIndexer
{
	
	private List<CountrySortModel> mList;
	
	private Context mContext;
	
	LayoutInflater mInflater;
	
	/***
	 * ��ʼ��
	 * 
	 * @param mContext
	 * @param list
	 */
	public CountrySortAdapter(Context mContext, List<CountrySortModel> list)
	{
		this.mContext = mContext;
		if (list == null)
		{
			this.mList = new ArrayList<CountrySortModel>();
		}
		else
		{
			this.mList = list;
		}
	}
	
	@Override
	public int getCount()
	{
		return this.mList.size();
	}
	
	@Override
	public Object getItem(int position)
	{
		return mList.get(position);
	}
	
	@Override
	public long getItemId(int position)
	{
		return position;
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent)
	{
		ViewHolder viewHolder = null;
		final CountrySortModel mContent = mList.get(position);
		
		if (view == null)
		{
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.coogame_country_item, null);
			viewHolder.country_sortName = (TextView) view.findViewById(R.id.country_catalog);
			viewHolder.country_name = (TextView) view.findViewById(R.id.country_name);
			viewHolder.country_number = (TextView) view.findViewById(R.id.country_number);
			view.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ViewHolder) view.getTag();
		}
		
		// ����position��ȡ���������ĸ��Char asciiֵ
		int section = getSectionForPosition(position);
		
		// �����ǰλ�õ��ڸ÷�������ĸ��Char��λ�� ������Ϊ�ǵ�һ�γ���
		if (position == getPositionForSection(section))
		{
			viewHolder.country_sortName.setVisibility(View.VISIBLE);
			viewHolder.country_sortName.setText(mContent.sortLetters);
		}
		else
		{
			viewHolder.country_sortName.setVisibility(View.GONE);
		}
		
		viewHolder.country_name.setText(this.mList.get(position).countryName);
		viewHolder.country_number.setText(this.mList.get(position).countryNumber);
		
		return view;
	}
	
	@Override
	public int getPositionForSection(int section)
	{
		if (section != 42)
		{
			for (int i = 0; i < getCount(); i++)
			{
				String sortStr = mList.get(i).sortLetters;
				char firstChar = sortStr.toUpperCase(Locale.CHINESE).charAt(0);
				if (firstChar == section)
				{
					return i;
				}
			}
		}
		else
		{
			return 0;
		}
		
		return -1;
	}
	
	@Override
	public int getSectionForPosition(int position)
	{
		return mList.get(position).sortLetters.charAt(0);
	}
	
	@Override
	public Object[] getSections()
	{
		return null;
	}
	
	/**
	 * ��ListView���ݷ����仯ʱ,���ô˷���������ListView
	 * 
	 * @param list
	 */
	public void updateListView(List<CountrySortModel> list)
	{
		if (list == null)
		{
			this.mList = new ArrayList<CountrySortModel>();
		}
		else
		{
			this.mList = list;
		}
		notifyDataSetChanged();
	}
	
	public static class ViewHolder
	{
		// �������ƴ��������ĸ��Χ
		public TextView country_sortName;
		
		// ������
		public TextView country_name;
		
		// ����
		public TextView country_number;
		
	}
	
}
