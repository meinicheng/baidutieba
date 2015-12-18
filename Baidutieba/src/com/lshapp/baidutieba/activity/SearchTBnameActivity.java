package com.lshapp.baidutieba.activity;

import com.lshapp.baidutieba.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SearchTBnameActivity extends Activity {
	private EditText editText;
	private Button sbtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searchtbname);
		editText = (EditText)findViewById(R.id.sedit);
		sbtn = (Button)findViewById(R.id.sbtn);
		
		sbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String edstring = editText.getText().toString();
				if(edstring==null&&edstring.equals("")){
					Toast.makeText(SearchTBnameActivity.this, "请输入要搜索的贴吧名", Toast.LENGTH_SHORT).show();
					
				}else{
					Intent intent = new Intent(SearchTBnameActivity.this,TiebaListActivity.class);
					intent.putExtra("tiebaname",edstring );
					startActivity(intent);
					overridePendingTransition(R.anim.in_anim, R.anim.out_anim);
				}
			}
		});
		
	}
}
