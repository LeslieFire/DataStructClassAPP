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

	//����
	CheckBox chk_autocheck;
	CheckBox chk_auto2next;
	CheckBox chk_auto2addWAset;

	//���غʹ��水ť
	ImageButton btn_saveSetting;
	ImageButton btn_return;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.optionlayout);
        //����id��������xml�п���
		chk_autocheck = (CheckBox) findViewById(R.id.chk_autocheck);
		chk_auto2next = (CheckBox) findViewById(R.id.chk_auto2next);
		chk_auto2addWAset = (CheckBox) findViewById(R.id.chk_auto2addWAset);

		btn_saveSetting = (ImageButton) findViewById(R.id.btn_savesetting);
		btn_return = (ImageButton) findViewById(R.id.btn_return);
		
		//����ͷ��ذ�ť�ļ�������
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

	//��������,��sharedpreferences�ĺ���saveSettingAction(����SQLite���ݿ��⣬SharedPreferencesҲ��һ�����͵����ݴ洢��ʽ)
	public void saveSettingAction(){
		try {
			SharedPreferences sharedPreferences = getSharedPreferences(
					DataStructureExercises.PREFERENCE_NAME, DataStructureExercises.MODE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putBoolean(DataStructureExercises.CONFIG_AUTOCHECK, chk_autocheck.isChecked());
			editor.putBoolean(DataStructureExercises.CONFIG_AUTO2NEXT, chk_auto2next.isChecked());
			editor.putBoolean(DataStructureExercises.CONFIG_AUTO2ADDWRONGSET, chk_auto2addWAset.isChecked());
			editor.commit();
			ShowToast("�������óɹ�");
		} catch (Exception e) {

			ShowToast("�������ô���");
			e.printStackTrace();
		}
	}
	//����toast length_short-2��
	public void ShowToast(String str) {
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	//���ó�ֵ�趨
	private void configInit() {

		SharedPreferences sharedPreferences = getSharedPreferences(
				DataStructureExercises.PREFERENCE_NAME, DataStructureExercises.MODE);
		chk_autocheck.setChecked(sharedPreferences.getBoolean(DataStructureExercises.CONFIG_AUTOCHECK, false));
		chk_auto2next.setChecked(sharedPreferences.getBoolean(DataStructureExercises.CONFIG_AUTO2NEXT, false));
		chk_auto2addWAset.setChecked(sharedPreferences.getBoolean(DataStructureExercises.CONFIG_AUTO2ADDWRONGSET, false));
	}
}
