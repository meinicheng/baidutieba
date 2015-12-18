package com.lshapp.baidutieba.base;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * @author kingofglory email: kingofglory@yeah.net blog: http:www.google.com
 * @date 2014-4-2 TODO
 */

public class Comment extends BmobObject {

	public static final String TAG = "Comment";

	private User user;
	private String commentContent;
	private String zhuhfname;
	private String ytcontent;
	private String ctbname;
	private BmobRelation xiacengcomment;
	private String cihfname;
	private String cicihfname;
	private String diyitext;
	private String diertext;

	private QiangYu gozhuti;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getCommentContent() {
		return commentContent;
	}

	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}

	public String getZhuhfname() {
		return zhuhfname;
	}

	public void setZhuhfname(String zhuhfname) {
		this.zhuhfname = zhuhfname;
	}

	public String getYtcontent() {
		return ytcontent;
	}

	public void setYtcontent(String ytcontent) {
		this.ytcontent = ytcontent;
	}

	public String getCtbname() {
		return ctbname;
	}

	public void setCtbname(String ctbname) {
		this.ctbname = ctbname;
	}

	public BmobRelation getXiacengcomment() {
		return xiacengcomment;
	}

	public void setXiacengcomment(BmobRelation xiacengcomment) {
		this.xiacengcomment = xiacengcomment;
	}

	public String getCihfname() {
		return cihfname;
	}

	public void setCihfname(String cihfname) {
		this.cihfname = cihfname;
	}

	public String getCicihfname() {
		return cicihfname;
	}

	public void setCicihfname(String cicihfname) {
		this.cicihfname = cicihfname;
	}

	public String getDiyitext() {
		return diyitext;
	}

	public void setDiyitext(String diyitext) {
		this.diyitext = diyitext;
	}

	public String getDiertext() {
		return diertext;
	}

	public void setDiertext(String diertext) {
		this.diertext = diertext;
	}

	public QiangYu getGozhuti() {
		return gozhuti;
	}

	public void setGozhuti(QiangYu gozhuti) {
		this.gozhuti = gozhuti;
	}


}
