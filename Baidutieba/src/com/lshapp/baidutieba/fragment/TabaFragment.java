package com.lshapp.baidutieba.fragment;

import java.util.List;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.lshapp.baidutieba.R;
import com.lshapp.baidutieba.activity.SearchTBnameActivity;
import com.lshapp.baidutieba.activity.TiebaListActivity;
import com.lshapp.baidutieba.adapter.MyGridAdapter;
import com.lshapp.baidutieba.base.TieBaName;
import com.lshapp.baidutieba.base.User;

public class TabaFragment extends FragmentBase {
	private View view;
	private PullToRefreshGridView mPullRefreshListView;
	private LinearLayout asearch;
	private GridView gridview;
	private int datasize = 10000;
	private AlertDialog albumDialog;
	private User user;
	private List<TieBaName> tbdata;
	private MyGridAdapter gridAdapter;

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.taba_fragment, container, false);
		user = BmobUser.getCurrentUser(getActivity(), User.class);
		mPullRefreshListView = (PullToRefreshGridView) view
				.findViewById(R.id.pull_refresh_grid);
		asearch = (LinearLayout) view.findViewById(R.id.asearch);

		mPullRefreshListView.setMode(Mode.PULL_FROM_START);
		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener<GridView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<GridView> refreshView) {
						String label = DateUtils.formatDateTime(getActivity(),
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);
						datasize = 0;
						data();
						mPullRefreshListView.onRefreshComplete();
					}
				});

		gridview = mPullRefreshListView.getRefreshableView();
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				TextView text = (TextView) view.findViewById(R.id.gv_tv);

				String name = text.getText().toString();
				Intent intent = new Intent(getActivity(),
						TiebaListActivity.class);
				intent.putExtra("tiebaname", name);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.in_anim,
						R.anim.out_anim);
			}
		});

		gridview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {

				showAlbumDialog(position);

				return false;
			}
		});

		asearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						SearchTBnameActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.in_anim,
						R.anim.out_anim);
			}
		});

		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		data();
	}

	private void data() {
		if (user != null) {
			BmobQuery<TieBaName> query = new BmobQuery<TieBaName>();
			query.addWhereRelatedTo("usertbname", new BmobPointer(user));

			query.findObjects(getActivity(), new FindListener<TieBaName>() {


				@Override
				public void onSuccess(List<TieBaName> arg0) {

					if (datasize != arg0.size()) {
						tbdata = arg0;
						datasize = arg0.size();
						gridAdapter = new MyGridAdapter(
								getActivity(), tbdata);
						gridview.setAdapter(gridAdapter);

						Toast.makeText(getActivity(), "更新贴吧列表成功",
								Toast.LENGTH_SHORT).show();
					}
				}

				@Override
				public void onError(int arg0, String arg1) {

				}
			});

		}
	}

	public void showAlbumDialog(final int position) {
		albumDialog = new AlertDialog.Builder(getActivity()).create();
		albumDialog.setCanceledOnTouchOutside(true);
		View v = LayoutInflater.from(getActivity()).inflate(
				R.layout.digbargridview, null);
		albumDialog.show();
		albumDialog.setContentView(v);
		albumDialog.getWindow().setGravity(Gravity.CENTER);

		TextView albumPic = (TextView) v.findViewById(R.id.digdelete);
		TextView digback = (TextView) v.findViewById(R.id.digback);
		albumPic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				albumDialog.dismiss();
				
				
				 BmobRelation relation = new BmobRelation();
				 relation.remove(tbdata.get(position));
				 
				 user.setUsertbname(relation);
				 user.update(getActivity(), new UpdateListener() {
					
					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						Toast.makeText(getActivity(), position + "", Toast.LENGTH_SHORT)
						.show();
						datasize = datasize - 1;
						tbdata.remove(position);
						gridAdapter.notifyDataSetChanged();
						
					}
					
					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						
					}
				});
			}
		});
		digback.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				albumDialog.dismiss();
			}
		});
		
		
	}

}
