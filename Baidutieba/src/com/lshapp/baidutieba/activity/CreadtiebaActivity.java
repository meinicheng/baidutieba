package com.lshapp.baidutieba.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.listener.SaveListener;

import com.lshapp.baidutieba.R;
import com.lshapp.baidutieba.base.TieBaName;

public class CreadtiebaActivity extends Activity implements OnClickListener {
	private String tbname;
	private EditText cread_edit;
	private TextView cread_name;
	private Button bt_creadtieba;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creadtieba);
		Intent intent = getIntent();
		tbname = intent.getStringExtra("tiebaname");
		initView();
		cread_name.setText(tbname);
		cread_edit.setText(tbname);

	}

	private void initView() {
		cread_edit = (EditText) findViewById(R.id.cread_edit);
		cread_name = (TextView) findViewById(R.id.cread_name);
		bt_creadtieba = (Button) findViewById(R.id.bt_creadtieba);
		bt_creadtieba.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_creadtieba:
			final String name = cread_edit.getText().toString();
			if (TextUtils.isEmpty(name)) {
				Toast.makeText(CreadtiebaActivity.this, "请输入要创建的贴吧名",
						Toast.LENGTH_SHORT).show();
			} else {
				TieBaName tieBaName = new TieBaName();
				tieBaName.setName(name);
				tieBaName.save(this, new SaveListener() {

					@Override
					public void onSuccess() {
						Toast.makeText(CreadtiebaActivity.this, "成功",
								Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(CreadtiebaActivity.this,TiebaListActivity.class);
						intent.putExtra("tiebaname",name );
						startActivity(intent);
						overridePendingTransition(R.anim.in_anim, R.anim.out_anim);
						finish();
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						Toast.makeText(CreadtiebaActivity.this,
								"失败" + arg1 + arg0, Toast.LENGTH_SHORT).show();
					}
				});
				break;
			}
		}
	}

}
