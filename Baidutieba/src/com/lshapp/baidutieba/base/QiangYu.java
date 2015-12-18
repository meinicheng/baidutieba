package com.lshapp.baidutieba.base;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

public class QiangYu extends BmobObject implements Serializable {

	/**
	 * qiang yu entity,ÿ���б�item���� 2014/4/27
	 */
	private static final long serialVersionUID = -6280656428527540320L;

	private User author;
	private String content;
	// getContentfigureurl����������ݵ�ͼƬ
	private BmobFile Contentfigureurl;
	private int love;
	private int hate;
	private int share;
	private int comment;
	private boolean isPass;
	private boolean myFav;// �ղ�
	private boolean myLove;// ��
	private BmobRelation crelation;
	private String gltbname;
	private String tblisttitle;
	private Integer score;
	

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	// getContentfigureurl����������ݵ�ͼƬ
	public BmobFile getContentfigureurl() {
		return Contentfigureurl;
	}

	public void setContentfigureurl(BmobFile contentfigureurl) {
		Contentfigureurl = contentfigureurl;
	}

	public int getLove() {
		return love;
	}

	public void setLove(int love) {
		this.love = love;
	}

	public int getHate() {
		return hate;
	}

	public void setHate(int hate) {
		this.hate = hate;
	}

	public int getShare() {
		return share;
	}

	public void setShare(int share) {
		this.share = share;
	}

	public int getComment() {
		return comment;
	}

	public void setComment(int comment) {
		this.comment = comment;
	}

	public boolean isPass() {
		return isPass;
	}

	public void setPass(boolean isPass) {
		this.isPass = isPass;
	}

	public boolean getMyFav() {
		return myFav;
	}

	public void setMyFav(boolean myFav) {
		this.myFav = myFav;
	}

	public boolean getMyLove() {
		return myLove;
	}

	public void setMyLove(boolean myLove) {
		this.myLove = myLove;
	}

	@Override
	public String toString() {
		return "QiangYu [author=" + author + ", content=" + content
				+ ", Contentfigureurl=" + Contentfigureurl + ", love=" + love
				+ ", hate=" + hate + ", share=" + share + ", comment="
				+ comment + ", isPass=" + isPass + ", myFav=" + myFav
				+ ", myLove=" + myLove + ", relation=" + crelation + "]";
	}

	public String getGltbname() {
		return gltbname;
	}

	public void setGltbname(String gltbname) {
		this.gltbname = gltbname;
	}

	public String getTblisttitle() {
		return tblisttitle;
	}

	public void setTblisttitle(String tblisttitle) {
		this.tblisttitle = tblisttitle;
	}

	public BmobRelation getCrelation() {
		return crelation;
	}

	public void setCrelation(BmobRelation crelation) {
		this.crelation = crelation;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

}
