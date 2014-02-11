package com.DataStructureExercises;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.CheckBox;
import android.widget.Toast;

public class OptionActivity extends Activity {

	//方框
	CheckBox chk_autocheck;
	CheckBox chk_auto2next;
	CheckBox chk_auto2addWAset;

	//返回和储存按钮
	ImageButton btn_saveSetting;
	ImageButton btn_return;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.optionlayout);
        //定义id，用来在xml中控制
		chk_autocheck = (CheckBox) findViewById(R.id.chk_autocheck);
		chk_auto2next = (CheckBox) findViewById(R.id.chk_auto2next);
		chk_auto2addWAset = (CheckBox) findViewById(R.id.chk_auto2addWAset);

		btn_saveSetting = (ImageButton) findViewById(R.id.btn_savesetting);
		btn_return = (ImageButton) findViewById(R.id.btn_return);
		
		//储存和返回按钮的监听函数
		configInit();
		btn_saveSetting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveSettingAction();
				finish();
			}
		});
		
		btn_return.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				finish();
			}
		});
	}

	//保存设置,到sharedpreferences的函数saveSettingAction(除了SQLite数据库外，SharedPreferences也是一种轻型的数据存储方式)
	public void saveSettingAction(){
		try {
			SharedPreferences sharedPreferences = getSharedPreferences(
					DataStructureExercises.PREFERENCE_NAME, DataStructureExercises.MODE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putBoolean(DataStructureExercises.CONFIG_AUTOCHECK, chk_autocheck.isChecked());
			editor.putBoolean(DataStructureExercises.CONFIG_AUTO2NEXT, chk_auto2next.isChecked());
			editor.putBoolean(DataStructureExercises.CONFIG_AUTO2ADDWRONGSET, chk_auto2addWAset.isChecked());
			editor.commit();
			ShowToast("保存配置成功");
		} catch (Exception e) {

			ShowToast("保存配置错误");
			e.printStackTrace();
		}
	}
	//定义toast length_short-2秒
	public void ShowToast(String str) {
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	//设置初值设定
	private void configInit() {

		SharedPreferences sharedPreferences = getSharedPreferences(
				DataStructureExercises.PREFERENCE_NAME, DataStructureExercises.MODE);
		chk_autocheck.setChecked(sharedPreferences.getBoolean(DataStructureExercises.CONFIG_AUTOCHECK, false));
		chk_auto2next.setChecked(sharedPreferences.getBoolean(DataStructureExercises.CONFIG_AUTO2NEXT, false));
		chk_auto2addWAset.setChecked(sharedPreferences.getBoolean(DataStructureExercises.CONFIG_AUTO2ADDWRONGSET, false));
	}
}
