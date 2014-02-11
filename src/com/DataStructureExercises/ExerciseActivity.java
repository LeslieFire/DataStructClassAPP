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
//题目数量函数
	public static final int problemLimit = 29;
	public static final String label = "label";
	int curIndex;//当前题目题号
	String myAnswer;//选择的答案
	//定义错题
	int[] myWAset = new int[problemLimit];// 错题库数组的名字
	int[] problemTurn = new int[problemLimit];
	int Option;// 表示是随机 or 顺序
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
		
		//以下界面xml都被调用在这个excercise activity中
		if (Option == DataStructureExercises.OPTION_RDM){
			setContentView(R.layout.randomexerciselayout);
		}
		else if(Option == DataStructureExercises.OPTION_ORDER){		
			setContentView(R.layout.exerciselayout);		
		}
		else if(Option == DataStructureExercises.OPTION_WRONGEXERCISE){
			setContentView(R.layout.mywrongsetlayout2);
		}
		

		Init();// 图像、数据初始化
		settingInit();// 配置设定
		OnPaint();// 重绘
		//上一题按钮监听函数
		forword_btn.setOnClickListener(new OnClickListener() {
			// 上一题
			@Override
			//如果无上一题，出现toast
			public void onClick(View v) {
		
				if (curIndex == 0) {
					ShowToast("当前为第一题");
				} else {//在错题库练习模式下相同
					if (Option == DataStructureExercises.OPTION_WRONGEXERCISE) {
						int tindex = curIndex;
						while (--tindex >= 0) {
							if (myWAset[tindex] == 1) {
								curIndex = tindex;
								OnPaint();
								return;
							}
						}
						ShowToast("当前为第一题");
						return;
					} else {
						curIndex--;
						OnPaint();
					}
				}
			}
		});
		
		/*
		 * 返回
		 */
		return_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
	
				finish();
			}
		});
		
		
		/*
		 * 下一题
		 */
		next_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
		
				if (curIndex == problemLimit - 1) {
					ShowToast("当前为最后一题");
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
						ShowToast("当前为最后一题");
						return;
					} else {
						curIndex++;
						OnPaint();
					}
				}
			}
		});

		/*
		 * 确认按钮的监听函数  对应ABCD radio圆圈选项 把我的答案输到MYanswer
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
				//判断题
				if (TESTTPYE == 2) {
					if (myAnswer == "A") {
						myAnswer = "对";
					} else if(myAnswer == "C"){
						myAnswer = "错";
					}
				} 
				// ShowToast(myAnswer + " " + TESTANSWER);//判断对错，对的绿色，错的红色，和数据库里的答案做对比，compareto 做减法 
				if (myAnswer.compareTo(TESTANSWER) == 0) {
					//得出答案，判改，显示promptText
					promptText.setText(R.string.prompt_right);
					promptText.setVisibility(View.VISIBLE);
					promptText.setTextColor(Color.GREEN);
					if (auto2next) {
						next_btn.performClick();//自动按下一题
					}
				} else {
					promptText.setText(R.string.prompt_wrong);
					promptText.setText(promptText.getText().toString()
							+ TESTANSWER);
					promptText.setVisibility(View.VISIBLE);
					promptText.setTextColor(Color.RED);
					if (Option != DataStructureExercises.OPTION_WRONGEXERCISE
							&& auto2addWAset) {
						addWAset_btn.performClick();//自动按加入错题库
					}
				}
			}
		});

		/*
		 * 加入错题库的监听函数,最初myWAset为0，如果加入错题库，则给这题的myWAset参数置为1，
		 */
		addWAset_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
					myWAset[problemTurn[curIndex]] = 1;
					ShowToast("加入成功");
			}
		});
		/*
		 * 移除错题库按钮监听函数
		 */
		unaddWAset_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
					myWAset[problemTurn[curIndex]] = 0;
					saveWaset();
					ShowToast("移除成功");
			}
		});

		/*
		 * 定义选择
		 */
		radioGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
				
						if (autoCheck
								&& (radioA.isChecked() || radioB.isChecked()
										|| radioC.isChecked() || radioD
										.isChecked())) {
							check_btn.performClick();//有选择了，自动调用确认函数
						}
					}

				});
	}

	//重载菜单按钮，安卓自带函数onCreateOptionsMenu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		if (Option == DataStructureExercises.OPTION_ORDER) {
			menu.add(0, 1, 1, "跳转到指定题号");
			menu.add(0, 2, 2, "跳转到标签");
			menu.add(0, 3, 3, "存为标签");
		}
		menu.add(0, 4, 4, "设置");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if (item.getItemId() == 1) {
			ShowDialog2JumpByIndex();
			//条转到指定页
		} else if (item.getItemId() == 2) {
			//jumpAction是跳转
			jumpAction(labelProblemID);
		} else if (item.getItemId() == 3) {
			labelProblemID = curIndex + 1;
			//跳转到该题号
		} else if (item.getItemId() == 4) {
			startActivity(new Intent().setClass(ExerciseActivity.this,
					OptionActivity.class));
			
		}
		return super.onOptionsItemSelected(item);
	}
 //跳转题号的函数
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
		editText.setHint("请输入要跳转的题号");
		new AlertDialog.Builder(this)
				.setTitle("请输入")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setView(editText)
				.setPositiveButton("确定",
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								
								if (editText.getText().toString().equals(""))
									ShowToast("没有输入");
								else {						
									if (!jumpAction(Integer.parseInt(editText
										.getText().toString()))) {
										ShowToast("指定题号不存在");
									}
								}
							}
						}).setNegativeButton("取消", null).show();
	}

	public void ShowToast(String str) {
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

//初始化
	public void Init() {
		Bundle bundle = getIntent().getExtras();
		Option = bundle.getInt("option");
		try {
			dbAdapter = new DBAdapter(this);
			dbAdapter.open();
			sharedPreferences = getSharedPreferences(
					DataStructureExercises.PREFERENCE_NAME, DataStructureExercises.MODE);// SharedPreferences存储方式
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
		 * 错误题目读取，为错题库调用
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
//设置保存
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
			 * 初始化View
			 */
			cursor.moveToPosition(problemTurn[curIndex]);
			radioGroup.clearCheck();
			TESTSUBJECT = cursor.getString(cursor
					.getColumnIndex(DBAdapter.TESTSUBJECT));
			TESTSUBJECT = TESTSUBJECT.replace("“|”", "下图");
			TESTANSWER = cursor.getString(cursor
					.getColumnIndex(DBAdapter.TESTANSWER));
			IMAGENAME = cursor.getString(cursor
					.getColumnIndex(DBAdapter.IMAGENAME));
			TESTTPYE = cursor.getInt(cursor.getColumnIndex(DBAdapter.TESTTPYE));
			proTextView
					.setText((problemTurn[curIndex] + 1) + "." + TESTSUBJECT);
		

			promptText.setVisibility(View.GONE);
			promptText.setText("");
			

			// 图片处理
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
				// 判断题
				radioA.setText("对");
				radioC.setText("错");
				radioB.setVisibility(View.INVISIBLE);
				radioD.setVisibility(View.INVISIBLE);
			} else {
				// 选择题
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
		// 保存错题库
		saveWaset();
		dbAdapter.close();
		super.onDestroy();
	}
}
