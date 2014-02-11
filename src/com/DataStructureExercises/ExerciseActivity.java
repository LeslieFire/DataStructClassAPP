package com.DataStructureExercises;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ExerciseActivity extends Activity {
//��Ŀ��������
	public static final int problemLimit = 29;
	public static final String label = "label";
	int curIndex;//��ǰ��Ŀ���
	String myAnswer;//ѡ��Ĵ�
	//�������
	int[] myWAset = new int[problemLimit];// ��������������
	int[] problemTurn = new int[problemLimit];
	int Option;// ��ʾ����� or ˳��
	int labelProblemID;

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

	boolean autoCheck;
	boolean auto2next;
	boolean auto2addWAset;
	EditText editText;
	TextView proTextView;
	ImageView imageview;
	RadioButton radioA;
	RadioButton radioB;
	RadioButton radioC;
	RadioButton radioD;
	RadioGroup radioGroup;
	ImageButton forword_btn;
	ImageButton next_btn;
	ImageButton check_btn;
	ImageButton addWAset_btn;
	ImageButton unaddWAset_btn;
	ImageButton randomorder;
	ImageButton return_btn = null;
	TextView promptText;

	Cursor cursor;
	DBAdapter dbAdapter;
	SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;

	FileInputStream fis;
	FileOutputStream fos;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getIntent().getExtras();
		Option = bundle.getInt("option");
		
		//���½���xml�������������excercise activity��
		if (Option == DataStructureExercises.OPTION_RDM){
			setContentView(R.layout.randomexerciselayout);
		}
		else if(Option == DataStructureExercises.OPTION_ORDER){		
			setContentView(R.layout.exerciselayout);		
		}
		else if(Option == DataStructureExercises.OPTION_WRONGEXERCISE){
			setContentView(R.layout.mywrongsetlayout2);
		}
		

		Init();// ͼ�����ݳ�ʼ��
		settingInit();// �����趨
		OnPaint();// �ػ�
		//��һ�ⰴť��������
		forword_btn.setOnClickListener(new OnClickListener() {
			// ��һ��
			@Override
			//�������һ�⣬����toast
			public void onClick(View v) {
		
				if (curIndex == 0) {
					ShowToast("��ǰΪ��һ��");
				} else {//�ڴ������ϰģʽ����ͬ
					if (Option == DataStructureExercises.OPTION_WRONGEXERCISE) {
						int tindex = curIndex;
						while (--tindex >= 0) {
							if (myWAset[tindex] == 1) {
								curIndex = tindex;
								OnPaint();
								return;
							}
						}
						ShowToast("��ǰΪ��һ��");
						return;
					} else {
						curIndex--;
						OnPaint();
					}
				}
			}
		});
		
		/*
		 * ����
		 */
		return_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
	
				finish();
			}
		});
		
		
		/*
		 * ��һ��
		 */
		next_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
		
				if (curIndex == problemLimit - 1) {
					ShowToast("��ǰΪ���һ��");
				} else {
					if (Option == DataStructureExercises.OPTION_WRONGEXERCISE) {
						int tindex = curIndex;
						while (++tindex < problemLimit) {
							if (myWAset[tindex] == 1) {
								curIndex = tindex;
								OnPaint();
								return;
							}
						}
						ShowToast("��ǰΪ���һ��");
						return;
					} else {
						curIndex++;
						OnPaint();
					}
				}
			}
		});

		/*
		 * ȷ�ϰ�ť�ļ�������  ��ӦABCD radioԲȦѡ�� ���ҵĴ��䵽MYanswer
		 */
		check_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			
				int answerNum = radioGroup.getCheckedRadioButtonId();
				switch (answerNum) {
				case R.id.radioA:
					myAnswer = "A";
					break;
				case R.id.radioB:
					myAnswer = "B";
					break;
				case R.id.radioC:
					myAnswer = "C";
					break;
				case R.id.radioD:
					myAnswer = "D";
					break;
				case -1:
					myAnswer = "";
				default:
					myAnswer = "";
					break;
				}
				//�ж���
				if (TESTTPYE == 2) {
					if (myAnswer == "A") {
						myAnswer = "��";
					} else if(myAnswer == "C"){
						myAnswer = "��";
					}
				} 
				// ShowToast(myAnswer + " " + TESTANSWER);//�ж϶Դ��Ե���ɫ����ĺ�ɫ�������ݿ���Ĵ����Աȣ�compareto ������ 
				if (myAnswer.compareTo(TESTANSWER) == 0) {
					//�ó��𰸣��иģ���ʾpromptText
					promptText.setText(R.string.prompt_right);
					promptText.setVisibility(View.VISIBLE);
					promptText.setTextColor(Color.GREEN);
					if (auto2next) {
						next_btn.performClick();//�Զ�����һ��
					}
				} else {
					promptText.setText(R.string.prompt_wrong);
					promptText.setText(promptText.getText().toString()
							+ TESTANSWER);
					promptText.setVisibility(View.VISIBLE);
					promptText.setTextColor(Color.RED);
					if (Option != DataStructureExercises.OPTION_WRONGEXERCISE
							&& auto2addWAset) {
						addWAset_btn.performClick();//�Զ�����������
					}
				}
			}
		});

		/*
		 * ��������ļ�������,���myWAsetΪ0������������⣬��������myWAset������Ϊ1��
		 */
		addWAset_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
					myWAset[problemTurn[curIndex]] = 1;
					ShowToast("����ɹ�");
			}
		});
		/*
		 * �Ƴ�����ⰴť��������
		 */
		unaddWAset_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
					myWAset[problemTurn[curIndex]] = 0;
					saveWaset();
					ShowToast("�Ƴ��ɹ�");
			}
		});

		/*
		 * ����ѡ��
		 */
		radioGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
				
						if (autoCheck
								&& (radioA.isChecked() || radioB.isChecked()
										|| radioC.isChecked() || radioD
										.isChecked())) {
							check_btn.performClick();//��ѡ���ˣ��Զ�����ȷ�Ϻ���
						}
					}

				});
	}

	//���ز˵���ť����׿�Դ�����onCreateOptionsMenu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		if (Option == DataStructureExercises.OPTION_ORDER) {
			menu.add(0, 1, 1, "��ת��ָ�����");
			menu.add(0, 2, 2, "��ת����ǩ");
			menu.add(0, 3, 3, "��Ϊ��ǩ");
		}
		menu.add(0, 4, 4, "����");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if (item.getItemId() == 1) {
			ShowDialog2JumpByIndex();
			//��ת��ָ��ҳ
		} else if (item.getItemId() == 2) {
			//jumpAction����ת
			jumpAction(labelProblemID);
		} else if (item.getItemId() == 3) {
			labelProblemID = curIndex + 1;
			//��ת�������
		} else if (item.getItemId() == 4) {
			startActivity(new Intent().setClass(ExerciseActivity.this,
					OptionActivity.class));
			
		}
		return super.onOptionsItemSelected(item);
	}
 //��ת��ŵĺ���
	public boolean jumpAction(int jump2ID) {
		if (jump2ID > 0 && jump2ID <= problemLimit) {
			curIndex = jump2ID - 1;
			OnPaint();
			return true;
		} else {
			return false;
		}

	}

	public void ShowDialog2JumpByIndex() {
		editText = new EditText(this);
		editText.setKeyListener(new DigitsKeyListener(false, true));
		editText.setHint("������Ҫ��ת�����");
		new AlertDialog.Builder(this)
				.setTitle("������")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setView(editText)
				.setPositiveButton("ȷ��",
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								
								if (editText.getText().toString().equals(""))
									ShowToast("û������");
								else {						
									if (!jumpAction(Integer.parseInt(editText
										.getText().toString()))) {
										ShowToast("ָ����Ų�����");
									}
								}
							}
						}).setNegativeButton("ȡ��", null).show();
	}

	public void ShowToast(String str) {
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

//��ʼ��
	public void Init() {
		Bundle bundle = getIntent().getExtras();
		Option = bundle.getInt("option");
		try {
			dbAdapter = new DBAdapter(this);
			dbAdapter.open();
			sharedPreferences = getSharedPreferences(
					DataStructureExercises.PREFERENCE_NAME, DataStructureExercises.MODE);// SharedPreferences�洢��ʽ
			editor = sharedPreferences.edit();


		} catch (Exception e) {

			Log.i("Init", "WA");
		}
		proTextView = (TextView) findViewById(R.id.pro_text);
		imageview = (ImageView) findViewById(R.id.imageview);
		radioA = (RadioButton) findViewById(R.id.radioA);
		radioB = (RadioButton) findViewById(R.id.radioB);
		radioC = (RadioButton) findViewById(R.id.radioC);
		radioD = (RadioButton) findViewById(R.id.radioD);
		forword_btn = (ImageButton) findViewById(R.id.forwordBtn);
		next_btn = (ImageButton) findViewById(R.id.nextBtn);
		check_btn = (ImageButton) findViewById(R.id.checkBtn);
		addWAset_btn = (ImageButton) findViewById(R.id.addWAsetBtn);
		unaddWAset_btn = (ImageButton) findViewById(R.id.unaddWAsetBtn);
		return_btn = (ImageButton) findViewById(R.id.return_btn);
		radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		promptText = (TextView) findViewById(R.id.promptText);

		for (int i = 0; i < problemLimit; i++) {
	
			problemTurn[i] = i;
		}

		/*
		 * ������Ŀ��ȡ��Ϊ��������
		 */
		try {
			String Text = "";
			fis = openFileInput(WrongSetShowList.WAsetFilename);
			byte[] readBytes = new byte[fis.available()];
			while (fis.read(readBytes) != -1) {
				Text = new String(readBytes);
			}
			String[] tmp_waset = Text.split("#");
			String tmpString;
			if (tmp_waset[0].compareTo("") != 0) {
				for (int i = 0; i < tmp_waset.length; i++) {
					tmpString = tmp_waset[i].substring(0,
							tmp_waset[i].indexOf('.'));

					myWAset[Integer.parseInt(tmpString) - 1] = 1;

				}
			}
		} catch (Exception e) {

			e.printStackTrace();
	
		}

		if (Option == DataStructureExercises.OPTION_RDM) {
			Random r = new Random();
			int t, rt1, rt2;
			for (int i = 0; i < problemLimit; i++) {
				rt1 = r.nextInt(problemLimit);
				rt2 = r.nextInt(problemLimit);
				t = problemTurn[rt1];
				problemTurn[rt1] = problemTurn[rt2];
				problemTurn[rt2] = t;
			}
		}
		curIndex = 0;
		if (Option == DataStructureExercises.OPTION_WRONGEXERCISE) {
			curIndex = bundle.getInt("startfrom");
			addWAset_btn.setVisibility(View.GONE);
			unaddWAset_btn.setVisibility(View.VISIBLE);
		}
		cursor = dbAdapter.getAllData();
		Log.i("Count", cursor.getCount() + "");
	}
//���ñ���
	public void settingInit() {
		autoCheck = sharedPreferences.getBoolean(DataStructureExercises.CONFIG_AUTOCHECK,
				false);
		labelProblemID = sharedPreferences.getInt(label, 0);
		auto2next = sharedPreferences.getBoolean(DataStructureExercises.CONFIG_AUTO2NEXT,
				false);
		auto2addWAset = sharedPreferences.getBoolean(
				DataStructureExercises.CONFIG_AUTO2ADDWRONGSET, false);
	}

	public void OnPaint() {
		if (cursor.getCount() == 0) {
			Toast.makeText(this, "Please reboot software", Toast.LENGTH_LONG).show();
		} else {
			/*
			 * ��ʼ��View
			 */
			cursor.moveToPosition(problemTurn[curIndex]);
			radioGroup.clearCheck();
			TESTSUBJECT = cursor.getString(cursor
					.getColumnIndex(DBAdapter.TESTSUBJECT));
			TESTSUBJECT = TESTSUBJECT.replace("��|��", "��ͼ");
			TESTANSWER = cursor.getString(cursor
					.getColumnIndex(DBAdapter.TESTANSWER));
			IMAGENAME = cursor.getString(cursor
					.getColumnIndex(DBAdapter.IMAGENAME));
			TESTTPYE = cursor.getInt(cursor.getColumnIndex(DBAdapter.TESTTPYE));
			proTextView
					.setText((problemTurn[curIndex] + 1) + "." + TESTSUBJECT);
		

			promptText.setVisibility(View.GONE);
			promptText.setText("");
			

			// ͼƬ����
			if (IMAGENAME.compareTo("image") != 0) {
				InputStream inputStream;
				try {
					IMAGENAME = IMAGENAME.replace('-', '_');
			
					inputStream = super.getAssets().open(IMAGENAME);
					imageview.setImageDrawable(Drawable.createFromStream(
							inputStream, "assets"));
					imageview.setVisibility(View.VISIBLE);
				
				} catch (Exception e) {
				
					Toast.makeText(this, e.toString(), Toast.LENGTH_LONG)
							.show();
					e.printStackTrace();
				}
			} else {
				imageview.setVisibility(View.GONE);
			}
			ANSWERA = cursor
					.getString(cursor.getColumnIndex(DBAdapter.ANSWERA));
			ANSWERB = cursor
					.getString(cursor.getColumnIndex(DBAdapter.ANSWERB));
			ANSWERC = cursor
					.getString(cursor.getColumnIndex(DBAdapter.ANSWERC));
			ANSWERD = cursor
					.getString(cursor.getColumnIndex(DBAdapter.ANSWERD));
			if (ANSWERA.compareTo("") == 0) {
				// �ж���
				radioA.setText("��");
				radioC.setText("��");
				radioB.setVisibility(View.INVISIBLE);
				radioD.setVisibility(View.INVISIBLE);
			} else {
				// ѡ����
				radioA.setText("A." + ANSWERA);
				radioB.setText("B." + ANSWERB);
				radioC.setText("C." + ANSWERC);
				radioD.setText("D." + ANSWERD);
				radioA.setVisibility(View.VISIBLE);
				radioB.setVisibility(View.VISIBLE);
				radioC.setVisibility(View.VISIBLE);
				radioD.setVisibility(View.VISIBLE);
			}

		}
	}

	@Override
	protected void onRestart() {
	
		settingInit();
		super.onRestart();
	}

	public void saveWaset() {
		try {
			String text = "";
			fos = openFileOutput(WrongSetShowList.WAsetFilename, MODE_PRIVATE);
			for (int i = 0; i < problemLimit; i++) {
				if (myWAset[i] == 1) {
		
					cursor.moveToPosition(i);
					text += (i + 1)
							+ "."
							+ cursor.getString(cursor
									.getColumnIndex(DBAdapter.TESTSUBJECT))
							+ "#";
				}
			}
			if (text.compareTo("") == 0)
				text = "#";
			fos.write(text.getBytes());
		} catch (Exception e) {

		} finally {
			if (fos != null) {
				try {
					fos.flush();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


	@Override
	protected void onDestroy() {
		// ��������
		saveWaset();
		dbAdapter.close();
		super.onDestroy();
	}
}
