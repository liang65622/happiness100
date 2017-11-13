package com.happiness100.app.ui.widget;/**
 * Created by Administrator on 2016/8/18.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.happiness100.app.R;
import com.happiness100.app.model.CityModel;
import com.happiness100.app.model.ProvinceModel;
import com.happiness100.app.service.XmlParserHandler;
import com.happiness100.app.ui.widget.wheel.OnWheelChangedListener;
import com.happiness100.app.ui.widget.wheel.WheelView;
import com.happiness100.app.ui.widget.wheel.adapters.ArrayWheelAdapter;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：jiangsheng on 2016/8/18 09:16
 */
public class DialogZoneSelector extends Dialog implements OnWheelChangedListener {
    @Bind(R.id.id_province)
    WheelView mIdProvince;
    @Bind(R.id.id_city)
    WheelView mIdCity;
    @Bind(R.id.btn_confirm)
    Button mBtnConfirm;
    private Activity mContext;
    private PriorityListener mlistener;//activity监听回调

    /**
     * 所有省
     */
    protected String[] mProvinceDatas;
    /**
     * key - 省 value - 市
     */
    protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
    /**
     * 当前省的名称
     */
    protected String mCurrentProviceName;
    /**
     * 当前市的名称
     */
    protected String mCurrentCityName;

    /**
     * 解析省市区的XML数据
     */
    protected void initProvinceDatas() {
        List<ProvinceModel> provinceList = null;
        AssetManager asset = mContext.getAssets();
        try {
            InputStream input = asset.open("province_data.xml");
            // 创建一个解析xml的工厂对象
            SAXParserFactory spf = SAXParserFactory.newInstance();
            // 解析xml
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(input, handler);
            input.close();
            // 获取解析出来的数据
            provinceList = handler.getDataList();
            //*/ 初始化默认选中的省、市
            if (provinceList != null && !provinceList.isEmpty()) {
                mCurrentProviceName = provinceList.get(0).getName();
                List<CityModel> cityList = provinceList.get(0).getCityList();
                if (cityList != null && !cityList.isEmpty()) {
                    mCurrentCityName = cityList.get(0).getName();
                }
            }
            //*/
            mProvinceDatas = new String[provinceList.size()];
            for (int i = 0; i < provinceList.size(); i++) {
                // 遍历所有省的数据
                mProvinceDatas[i] = provinceList.get(i).getName();
                List<CityModel> cityList = provinceList.get(i).getCityList();
                String[] cityNames = new String[cityList.size()];
                for (int j = 0; j < cityList.size(); j++) {
                    // 遍历省下面的所有市的数据
                    cityNames[j] = cityList.get(j).getName();
                }
                // 省-市的数据，保存到mCitisDatasMap
                mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {

        }
    }

    /**
     * @param context
     * @param theme
     */
    public DialogZoneSelector(Activity context, int theme, PriorityListener listener) {
        super(context, theme);
        mContext = context;
        mlistener = listener;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_zone);
        ButterKnife.bind(this);
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.5f;
        window.setGravity(Gravity.BOTTOM);
        window.setAttributes(layoutParams);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        setUpListener();
        setUpData();
    }

    private void setUpListener() {
        // 添加change事件
        mIdProvince.addChangingListener(this);
        // 添加change事件
        mIdCity.addChangingListener(this);
    }

    private void setUpData() {
        initProvinceDatas();
        ArrayWheelAdapter adapter = new ArrayWheelAdapter<String>(getContext(), mProvinceDatas);
        mIdProvince.setViewAdapter(adapter);
        // 设置可见条目数量
        mIdProvince.setVisibleItems(5);
        mIdCity.setVisibleItems(5);
        updateCities();
        updateAreas();
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == mIdProvince) {
            updateCities();
        } else if (wheel == mIdCity) {
            updateAreas();
        }

    }


    /**
     * 根据当前的市，更新区WheelView的信息
     */
    private void updateAreas() {
        int pCurrent = mIdCity.getCurrentItem();
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void updateCities() {
        int pCurrent = mIdProvince.getCurrentItem();
        mCurrentProviceName = mProvinceDatas[pCurrent];
        String[] cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities == null) {
            cities = new String[]{""};
        }

        ArrayWheelAdapter adapter = new ArrayWheelAdapter<String>(getContext(), cities);
        mIdCity.setViewAdapter(adapter);
        mIdCity.setCurrentItem(0);
        updateAreas();
    }

    private void showSelectedResult() {
        mlistener.refreshPriorityUI(mCurrentProviceName, mCurrentCityName);
        dismiss();
    }

    @OnClick(R.id.btn_confirm)
    public void onClick() {
        showSelectedResult();
    }

    public interface PriorityListener {
        public void refreshPriorityUI(String Provice, String City);
    }
}
