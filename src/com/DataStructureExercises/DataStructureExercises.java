package com.DataStructureExercises;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.PublicKey;

import com.DataStructureExercises.R;

import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
//����
public class DataStructureExercises extends Activity {

	public static final int OPTION_ORDER = 1;
	public static final int OPTION_RDM = 2;
	public static final int OPTION_TEST = 3;
	public static final int OPTION_WRONGEXERCISE = 4;
	public static final int MODE = MODE_PRIVATE;
	public static final String PREFERENCE_NAME = "SaveSetting";
	public static final String CONFIG_AUTOCHECK = "config_autocheck";
	public static final String CONFIG_AUTO2NEXT = "config_auto2next";
	public static final String CONFIG_AUTO2ADDWRONGSET = "config_auto2addwrongset";
	
	


	private TextView tv = null;
	private ImageButton ibtn_order = null;
	private ImageButton ibtn_rdm = null;
	private ImageButton ibtn_test = null;
	private ImageButton ibtn_myWAset = null;
	private ImageButton ibtn_option = null;
	private ImageButton ibtn_about = null;

	Dialog dialog;

	//����������ܹ�
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//����������xml
		setContentView(R.layout.menu);
		
		//�����ͼƬ��ť
		ibtn_order = (ImageButton) findViewById(R.id.ibtn_order);
		ibtn_rdm = (ImageButton) findViewById(R.id.ibtn_rdm);
		ibtn_test = (ImageButton) findViewById(R.id.ibtn_test);
		ibtn_myWAset = (ImageButton) findViewById(R.id.ibtn_myWAset);
		ibtn_option = (ImageButton) findViewById(R.id.ibtn_option);
		ibtn_about = (ImageButton) findViewById(R.id.ibtn_about);

		judgeTheFirstTime2Run();
		
		//����ť��������
		ibtn_order.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				                                                                                                                                                                                                      
				Intent intent = new Intent();
				//�����˳����ϰ
				intent.putExtra("option", OPTION_ORDER);
				//ת��ExcerciseActivity
				intent.setClass(DataStructureExercises.this, ExerciseActivity.class);
				//����-��ʼ��
				startActivity(intent);
			}
		});

		ibtn_rdm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			
				Intent intent = new Intent();
				intent.putExtra("option", OPTION_RDM);
				intent.setClass(DataStructureExercises.this, ExerciseActivity.class);
				startActivity(intent);
			}
		});
		
		/*
		 * �ҵĴ����
		 */
		ibtn_myWAset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				startActivity(new Intent().setClass(DataStructureExercises.this,WrongSetShowList.class));
			}
		});

		ibtn_test.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent().setClass(DataStructureExercises.this,ExamActivity.class));
			}
		});

		/*
		 * ���� 
		 */
		ibtn_option.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				startActivity(new Intent().setClass(DataStructureExercises.this,
						OptionActivity.class));
			}
		});
		
		ibtn_about.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				aboutdialog();
			}
		});

	}

	
//�������õĺ���
	private void judgeTheFirstTime2Run() {
		
		DBAdapter dbAdapter;
		Cursor cursor;
		try {
			dbAdapter = new DBAdapter(this);
			dbAdapter.open();
			cursor = dbAdapter.getAllData();
			if(cursor.getCount()==0){
				new AlertDialog.Builder(this)
					.setTitle("ע�⣡")
					.setMessage("�������ã���ȷ�������Ե�...")
					.setPositiveButton("ȷ��",new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							DataTrans();
						}
					}).create().show();
			}
		} catch (Exception e) {
		
			Toast.makeText(this, "test1", Toast.LENGTH_LONG).show();
		}
	}


	
//keydown�ǰ�׿�Դ���activity�����ذ�ť�����أ�����exitdialog
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			exitdialog();
		}
		return false;
	}
//����exitdialog 
	protected void exitdialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage("ȷ���˳���");

		builder.setTitle("��ʾ");

		builder.setPositiveButton("ȷ��",
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface v, int which) {
					
						finish();
					}
				});
		builder.setNegativeButton("ȡ��",
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	//����aboutdialog
	protected void aboutdialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage("Data Structure Exercises By Huangweidi");

		builder.setTitle("����");

		builder.setPositiveButton("ȷ��",
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
				
					}
				});
		builder.create().show();
	}

	
	//����ת��-��������
	public void DataTrans() {
		InputStream in = getResources().openRawResource(R.raw.testsubject);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(in, "gb2312"));// ע�����
		} catch (UnsupportedEncodingException e1) {
	
			Log.e("debug", e1.toString());

		}
		String tmp, body;
		String TESTSUBJECT;
		String TESTANSWER;
		String ANSWERA;
		String ANSWERB;
		String ANSWERC;
		String ANSWERD;
		String IMAGENAME;

		int TESTTPYE;
		int TESTBELONG;
		int EXPR1;

		String[] strings = new String[13];
		ContentValues values = new ContentValues();
		try {
			DBAdapter dbAdapter = new DBAdapter(this);
			dbAdapter.open();
			while ((tmp = br.readLine()) != null) {
				strings = tmp.split("#");

				TESTSUBJECT = strings[1];
				TESTANSWER = strings[2];
				TESTTPYE = Integer.parseInt(strings[3]);// int
				TESTBELONG = Integer.parseInt(strings[4]);// int
				ANSWERA = strings[5];
				ANSWERB = strings[6];
				ANSWERC = strings[7];
				ANSWERD = strings[8];
				IMAGENAME = "image" + strings[9];
				IMAGENAME.replace("-", "_");
				EXPR1 = Integer.parseInt(strings[10]);// int

				values.clear();
				values.put(DBAdapter.TESTSUBJECT, TESTSUBJECT);
				values.put(DBAdapter.TESTANSWER, TESTANSWER);
				values.put(DBAdapter.TESTTPYE, TESTTPYE);
				values.put(DBAdapter.TESTBELONG, TESTBELONG);
				values.put(DBAdapter.ANSWERA, ANSWERA);
				values.put(DBAdapter.ANSWERB, ANSWERB);
				values.put(DBAdapter.ANSWERC, ANSWERC);
				values.put(DBAdapter.ANSWERD, ANSWERD);
				values.put(DBAdapter.IMAGENAME, IMAGENAME);
				values.put(DBAdapter.EXPR1, EXPR1);

				dbAdapter.DBInsert(values);

				Log.i(tmp, tmp);
			}
		
			br.close();
			in.close();
			body = "";
			Cursor cursor = dbAdapter.getAllData();
			body += cursor.getCount();
	

		} catch (IOException e) {
		
			Log.e("debug", e.toString());
		}
	}

}