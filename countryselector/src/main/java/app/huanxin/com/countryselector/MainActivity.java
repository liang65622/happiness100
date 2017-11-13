package app.huanxin.com.countryselector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.huanxin.com.countryselector.country.CharacterParserUtil;
import app.huanxin.com.countryselector.country.CountryActivity;
import app.huanxin.com.countryselector.country.CountrySortModel;
import app.huanxin.com.countryselector.country.GetCountryNameSort;


public class MainActivity extends Activity
{
	
	private String TAG = "XINWEI";
	
	private RelativeLayout relative_choseCountry;
	
	private EditText editText_countryNum;
	
	private TextView tv_countryName;
	
	private GetCountryNameSort countryChangeUtil;
	
	private CharacterParserUtil characterParserUtil;
	
	private List<CountrySortModel> mAllCountryList;
	
	String beforText = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		
		initView();
		initCountryList();
		setListener();
		
	}
	
	private void initView()
	{
		relative_choseCountry = (RelativeLayout) findViewById(R.id.rala_chose_country);
		editText_countryNum = (EditText) findViewById(R.id.edt_chosed_country_num);
		tv_countryName = (TextView) findViewById(R.id.tv_chosed_country);
		
		mAllCountryList = new ArrayList<CountrySortModel>();
		countryChangeUtil = new GetCountryNameSort();
		characterParserUtil = new CharacterParserUtil();
		
	}
	
	private void initCountryList()
	{
		String[] countryList = getResources().getStringArray(R.array.country_code_list_ch);
		
		for (int i = 0, length = countryList.length; i < length; i++)
		{
			String[] country = countryList[i].split("\\*");
			
			String countryName = country[0];
			String countryNumber = country[1];
			String countrySortKey = characterParserUtil.getSelling(countryName);
			CountrySortModel countrySortModel = new CountrySortModel(countryName, countryNumber,
					countrySortKey);
			String sortLetter = countryChangeUtil.getSortLetterBySortKey(countrySortKey);
			if (sortLetter == null)
			{
				sortLetter = countryChangeUtil.getSortLetterBySortKey(countryName);
			}
			
			countrySortModel.sortLetters = sortLetter;
			mAllCountryList.add(countrySortModel);
		}
		
	}
	
	private void setListener()
	{
		relative_choseCountry.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, CountryActivity.class);
				startActivityForResult(intent, 12);
			}
		});
		
		editText_countryNum.addTextChangedListener(new TextWatcher()
		{
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
				beforText = s.toString();
			}
			
			@Override
			public void afterTextChanged(Editable s)
			{
				String contentString = editText_countryNum.getText().toString();
				
				CharSequence contentSeq = editText_countryNum.getText();
				
//				Log.i(TAG, "contentString :" + contentString.length());
				
				if (contentString.length() > 1)
				{
					// �����������ݽ���ƥ��
					List<CountrySortModel> fileterList = (ArrayList<CountrySortModel>) countryChangeUtil
							.search(contentString, mAllCountryList);
					
					if (fileterList.size() == 1)
					{
						tv_countryName.setText(fileterList.get(0).countryName);
					}
					else
					{
						tv_countryName.setText("���Ҵ�����Ч");
					}
					
				}
				else
				{
					if (contentString.length() == 0)
					{
						editText_countryNum.setText(beforText);
						tv_countryName.setText("���б�ѡ��");
					}
					else if (contentString.length() == 1 && contentString.equals("+"))
					{
						tv_countryName.setText("���б�ѡ��");
					}
					
				}
				
				if (contentSeq instanceof Spannable)
				{
					Spannable spannable = (Spannable) contentSeq;
					Selection.setSelection(spannable, contentSeq.length());
				}
			}
		});
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		switch (requestCode)
		{
			case 12:
				if (resultCode == RESULT_OK)
				{
					Bundle bundle = data.getExtras();
					String countryName = bundle.getString("countryName");
					String countryNumber = bundle.getString("countryNumber");
					
					editText_countryNum.setText(countryNumber);
					tv_countryName.setText(countryName);
					
				}
				break;
			
			default:
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}
