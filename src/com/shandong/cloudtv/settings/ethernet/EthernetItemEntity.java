package com.shandong.cloudtv.settings.ethernet;


public class EthernetItemEntity {
	public static final int ETHERNET_CONNECT_ITEM_CATEGORY_NULL = 0x1000;
	public static final int ETHERNET_CONNECT_ITEM_CATEGORY_SELECT = 0x1001;
	public static final int ETHERNET_CONNECT_ITEM_CATEGORY_ENTER = 0x1002;
	public static final int ETHERNET_CONNECT_ITEM_CATEGORY_EDIT = 0x1003;
	public static final int SELECT_ON = 1;
	public static final int SELECT_OFF = 0;
	private String mItemTitleStr = null;
	private String[] mItemContentStrs = null;
	private String mItemContentStr = null;
	private int mItemCategory = ETHERNET_CONNECT_ITEM_CATEGORY_NULL;
	private int mItemSelectFlag = SELECT_OFF;
	
	public void setItemTitle(String title){
		mItemTitleStr = title;
	}
	
	public String getItemTitle(){
		return mItemTitleStr;
	}
	
	public void setItemContents(String[] content){
		if(content == null){
			mItemContentStrs = null;
		}else{
			int length =  content.length;
			mItemContentStrs = new String[length];
			for(int i=0;i<length;i++){
				mItemContentStrs[i] = content[i];
			}
		}
	
	}
	
	public String[] getItemContents(){
		return mItemContentStrs;
	}
	
	public void setItemContent(String content){
		mItemContentStr = content;
	}
	
	public String getItemContent(){
		return mItemContentStr;
	}
	
	public void setItemCategory(int category){
		mItemCategory = category;
	}
	
	public int getItemCategory(){
		return mItemCategory;
	}
	
	public void setItemSelectFlag(int flag){
		mItemSelectFlag = flag;
	}
	
	public int getItemSelectFlag(){
		return mItemSelectFlag;
	}
}
