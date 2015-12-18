package com.lshapp.baidutieba.base;


import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobRelation;

/** ����BmobChatUser����������������Ҫ���ӵ����Կ��ڴ����
  * @ClassName: TextUser
  * @Description: TODO
  * @author smile
  * @date 2014-5-29 ����6:15:45
  */
public class User extends BmobChatUser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * �����Ĳ����б�
	 */
	private BmobRelation blogs;
	/**
	 * //��ʾ����ƴ��������ĸ
	 */
	private String sortLetters;
	
	/**
	 * //�Ա�-true-��
	 */
	private Boolean sex;
	
	
	private Blog blog;
	
	/**
	 * ��������
	 */
	private BmobGeoPoint location;//
	
	private Integer hight;
	
	private BmobRelation tbname;
	
	private BmobRelation usertbname;
	
	private String setread;
	
	public Blog getBlog() {
		return blog;
	}
	public void setBlog(Blog blog) {
		this.blog = blog;
	}
	public Integer getHight() {
		return hight;
	}
	public void setHight(Integer hight) {
		this.hight = hight;
	}
	public BmobRelation getBlogs() {
		return blogs;
	}
	public void setBlogs(BmobRelation blogs) {
		this.blogs = blogs;
	}
	public BmobGeoPoint getLocation() {
		return location;
	}
	public void setLocation(BmobGeoPoint location) {
		this.location = location;
	}
	public Boolean getSex() {
		return sex;
	}
	public void setSex(Boolean sex) {
		this.sex = sex;
	}
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
	public BmobRelation getTbname() {
		return tbname;
	}
	public void setTbname(BmobRelation tbname) {
		this.tbname = tbname;
	}
	public BmobRelation getUsertbname() {
		return usertbname;
	}
	public void setUsertbname(BmobRelation usertbname) {
		this.usertbname = usertbname;
	}
	public String getSetread() {
		return setread;
	}
	public void setSetread(String setread) {
		this.setread = setread;
	}
	
}
