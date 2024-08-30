package com.systex.jbranch.platform.common.errHandle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.multiLang.IdGroupType;
import com.systex.jbranch.platform.common.multiLang.MultiLangBaseProperty;
import com.systex.jbranch.platform.common.multiLang.MultiLangIdType;
import com.systex.jbranch.platform.common.multiLang.MultiLangUtil;
import com.systex.jbranch.platform.common.platformdao.table.TbsysmultilangVO;

/**
 *此類別的功能描述：多國語系模組的Exception物件
 * 此程式的注意事項：多國語系實作使用property的方式，此處須使用ResourceBundle方法取得多國語系資料
 * 
 *@author Benson Chen
 *@since 此Class 從哪個版本開始加入 (外部的version)
 **/
public class MultiLangException extends PlatFormException {

	private String _ml_idGroup;
	private ErrLevelVO _errLevelVO = new ErrLevelVO();
	private List<String> _interMsgList;
	private UUID _uuid;

	public MultiLangException(EnumErrInputType errInputType, String value) {
		super(value);
		this._ml_idGroup = value;
		this._interMsgList = new ArrayList<String>();
		this._uuid = null;

		if (errInputType.equals(EnumErrInputType.MUTILANG_ID)) {
			this.setErrLevelVO(value);

		} else if (errInputType.equals(EnumErrInputType.MSG)) {
			this.setErrLevelVOByMsg(value);
		}
	}

	public MultiLangException(String ml_idGroup_s) {
		super(ml_idGroup_s);
		this._ml_idGroup = ml_idGroup_s;
		this.setErrLevelVO(ml_idGroup_s);
	}

	/**
	 *此類別的功能描述：建立多國語系模組的Exception物件 此程式的注意事項：繼承superclass並傳入多國語系群組編號存入class
	 * variable並call super constructor及setExceptionLevelVOList()函式
	 * 
	 *@param 多國語系群組編號
	 *@author Benson Chen
	 *@since 此Class 從哪個版本開始加入 (外部的version)
	 **/
	public MultiLangException(String ml_idGroup_s, List<String> interMsgList) {
		super(ml_idGroup_s, interMsgList);
		this._ml_idGroup = ml_idGroup_s;
		this.setErrLevelVO(ml_idGroup_s);
	}

	// /**
	// *此類別的功能描述：建立多國語系模組的Exception物件
	// *此程式的注意事項：繼承superclass並傳入多國語系群組編號存入class
	// variable及使用者權限物件及呼叫setExceptonLevelVOByUserPrivilege()函式。
	// *
	// *@param 傳入使用者權限物件(包含該使用者的ExceptionLevel等級)
	// *@param 傳入多國語系群組編號
	// *@author Benson Chen
	// *@since 此Class 從哪個版本開始加入 (外部的version)
	// *@see
	// **/
	// public MultiLangException(UUID userPrivilege_s, String ml_idGroup_s,
	// List<String> interMsgList) {
	// this._ml_idGroup = ml_idGroup_s;
	// this.setErrLevelVOByUUID(userPrivilege_s, ml_idGroup_s);
	// }
	/**
	 *此Method的功能描述：利用多國語系群組編號去取得該多國語系群組資料，使用MultiLangDaoProperties類別，
	 * 使用ResourceBundle方法取得多國語系資料
	 * ExceptionLevel等級為Operator,Admin,SysProvider,Coder，四個等級(不固定)
	 * 其中Coder填入原始exception資訊(此由應用程式面填入)
	 * 
	 *@param 傳入多國語系群組編號
	 * 
	 *@throws 若有丟出Exception
	 *             的說明 (Required)
	 *@since 此Method 從哪個版本開始加入(外部的version)
	 *@see MultiLangBaseProperty
	 **/
	private void setErrLevelVO(String ml_idGroup_s) {
		if (ml_idGroup_s.trim().equals("")) {
			this._errLevelVO = new ErrLevelVO();
			this._errLevelVO.setMessage(EnumErrLevel.EndUser, "");
		} else {
			String[] languageArray = DataManager.getSystem().getDefaultValue().get("language").toString().split("_");
			Locale locale = new Locale(languageArray[0], languageArray[1]);
			// MultiLangDaoProperties multiDao = new
			// MultiLangDaoProperties(locale);
			// MultiLangDaoFacadeIF multiDao = new
			// MultiLangDaoProperties(locale);
			List<IdGroupType> groupList = this.processFindGroup(ml_idGroup_s, locale);
			String content_locale = "";
			this._errLevelVO = new ErrLevelVO();
			// set content by multilang id if size > 0
			if (groupList.size() > 0) {
				for (int i = 0; i < groupList.size(); i++) {
					List<TbsysmultilangVO> Tbsysmultilang_list = groupList.get(i).getIdAttributes();
					for (int j = 0; j < Tbsysmultilang_list.size(); j++) {
						TbsysmultilangVO Tbsysmultilang = Tbsysmultilang_list.get(j);
						content_locale = MultiLangUtil.passMultiLangContent(this._interMsgList, Tbsysmultilang.getContent());
						EnumErrLevel[] enumErrLevel = EnumErrLevel.values();
						for (EnumErrLevel enumErrLevel_inner : enumErrLevel) {
							if (enumErrLevel_inner.getErrLevelCode().equals(Tbsysmultilang.getComp_id().getAttribute())) {
								this._errLevelVO.setMessage(enumErrLevel_inner, content_locale);
							}
						}
					}
				}// end of for
				this.checkMultiLangValue(ml_idGroup_s, "");				
			} else {
				// set ErrLevel = multilang id
				this.setErrLevelVOByMsg(ml_idGroup_s);
			}
		}
	}

	private void setErrLevelVOByMsg(String msg) {

		this._errLevelVO = new ErrLevelVO();
		EnumErrLevel[] enumErrLevel = EnumErrLevel.values();
		for (EnumErrLevel enumErrLevel_inner : enumErrLevel) {
			this._errLevelVO.setMessage(enumErrLevel_inner, msg);
		}
	}

	/**
	 *此Method的功能描述：回傳ExceptionLevel的list物件
	 * ExceptionLevel等級為Operator,Admin,SysProvider,Coder，四個等級(不固定)
	 * 其中Coder填入原始exception資訊(此由應用程式面填入)
	 * 
	 *@return ExceptionLevelVO的list
	 * 
	 *@throws 若有丟出Exception
	 *             的說明 (Required)
	 *@since 此Method 從哪個版本開始加入(外部的version)
	 *@see MultiLangBaseProperty
	 **/
	@Override
	public ErrLevelVO getErrLevelVO() {
		return _errLevelVO;
	}

	/**
	 *此Method的功能描述：利用多國語系群組編號及使用者權限物件去取得該多國語系群組資料，使用ResourceBundle方法取得多國語系資料(
	 * 一個使用者只有一種ExceptionLevel等級)
	 * ExceptionLevel等級為Operator,Admin,SysProvider,Coder，四個等級(不固定)
	 * 其中Coder填入原始exception資訊(此由應用程式面填入)
	 * 
	 *@param 傳入使用者權限物件
	 *@param 傳入多國語系群組編號
	 *@throws 若有丟出Exception
	 *             的說明 (Required)
	 *@since 此Method 從哪個版本開始加入(外部的version)
	 *@see MultiLangBaseProperty
	 **/
	private String setErrLevelVOByUUID(UUID uuid, String ml_idGroup_s) {
		// get errorLevel from UUID
		String errorLevel = "00";
		if (DataManager.existUser(uuid)) {
			if (DataManager.getUser(uuid) != null) {
				if (DataManager.getUser(uuid).getErrLevel() != null)
					errorLevel = DataManager.getUser(uuid).getErrLevel();
			}
		}
		String content_locale = "";
		String content_locale_tmp = "";
		if (this._errLevelVO == null) {
			this._errLevelVO = new ErrLevelVO();
		}
		
//		// get errorLevel from UUID
//		String errorLevel = "";
//		if (DataManager.getUser(uuid) != null) {
//			if (DataManager.getUser(uuid).getErrLevel() != null) {
//				errorLevel = DataManager.getUser(uuid).getErrLevel();
//				this._errLevelVO = new ErrLevelVO(errorLevel);
//			}
//		} else {
//			this._errLevelVO = new ErrLevelVO();
//			this._errLevelVO.setMessage(EnumErrLevel.EndUser, "");
//			return "";
//		}

		String[] languageArray = DataManager.getSystem().getDefaultValue().get("language").toString().split("_");
		Locale locale = new Locale(languageArray[0], languageArray[1]);
		// MultiLangDaoProperties multiDao = new MultiLangDaoProperties(locale);
		// MultiLangDaoFacadeIF multiDao = new MultiLangDaoProperties(locale);
		// List<IdGroupType> groupList = multiDao.findGroup(ml_idGroup_s);
		List<IdGroupType> groupList = this.processFindGroup(ml_idGroup_s, locale);
//		String content_locale = "";

		if (groupList.size() > 0) {
//			for (int i = 0; i < groupList.size(); i++) {
//				List<TbsysmultilangVO> Tbsysmultilang_list = groupList.get(i).getIdAttributes();
//				for (int j = 0; j < Tbsysmultilang_list.size(); j++) {
//					TbsysmultilangVO Tbsysmultilang = Tbsysmultilang_list.get(j);
//					content_locale = MultiLangUtil.passMultiLangContent(this._interMsgList, Tbsysmultilang.getContent());
//
//					if (Tbsysmultilang.getComp_id().getAttribute().equals(errorLevel)) {
//						EnumErrLevel[] enumErrLevel = EnumErrLevel.values();
//						for (EnumErrLevel enumErrLevel_inner : enumErrLevel) {
//							if (enumErrLevel_inner.getErrLevelCode().equals(errorLevel)) {
//								this._errLevelVO.setMessage(enumErrLevel_inner, content_locale);
//							}
//						}
//					}
//				}
//			}// end of for
			
			for (int i = 0; i < groupList.size(); i++) {
				List<TbsysmultilangVO> Tbsysmultilang_list = groupList.get(i).getIdAttributes();
				for (int j = 0; j < Tbsysmultilang_list.size(); j++) {
					TbsysmultilangVO Tbsysmultilang = Tbsysmultilang_list.get(j);
					content_locale_tmp = MultiLangUtil.passMultiLangContent(this._interMsgList, Tbsysmultilang.getContent());
					EnumErrLevel[] enumErrLevel = EnumErrLevel.values();
					for (EnumErrLevel enumErrLevel_inner : enumErrLevel) {
						if (enumErrLevel_inner.getErrLevelCode().equals(Tbsysmultilang.getComp_id().getAttribute())) {
							this._errLevelVO.setMessage(enumErrLevel_inner, content_locale_tmp);
						}
					}
					if (Tbsysmultilang.getComp_id().getAttribute().equals(errorLevel)) {
						content_locale = content_locale_tmp;
					}
				}
			}// end of for

			content_locale = this.checkMultiLangValue(ml_idGroup_s, errorLevel);
			
			
		} else {
			this.setErrLevelVOByMsg(ml_idGroup_s);
			content_locale = ml_idGroup_s;
			// this._errLevelVO.setMessage(EnumErrLevel.EndUser,
			// content_locale);
		}

		return content_locale;
	}

	// /**
	// *此Method的功能描述：取得使用者權限後並call setErrLevelVOByUUID()
	// *
	// *@param 使用者權限物件
	// *@return ErrLevelVO
	// *@throws 若有丟出Exception 的說明 (Required)
	// *@since 此Method 從哪個版本開始加入(外部的version)
	// **/
	// @Override
	// public ErrLevelVO getErrLevelVOByUUID(UUID userPrivilege) {
	// this._uuid = userPrivilege;
	// this.setErrLevelVOByUUID(userPrivilege, this._ml_idGroup);
	// return this._errLevelVO;
	// }
	//
	// /**
	// *此Method的功能描述：取得使用者權限後的錯誤訊息
	// *
	// *@return ExceptionHandleVO
	// *@throws 若有丟出Exception 的說明 (Required)
	// *@since 此Method 從哪個版本開始加入(外部的version)
	// **/
	// @Override
	// public ErrLevelVO getErrLevelVOByUserPrivilege() {
	//
	// if (_errLevelVO != null) {
	// return _errLevelVO;
	// } else {
	// return null;
	// }
	// }
	@Override
	public String getMessage(UUID uuid) {
		this._uuid = uuid;
		String msg = "";
		msg = this.setErrLevelVOByUUID(uuid, this._ml_idGroup);
		if (msg == null) {
			this._errLevelVO = new ErrLevelVO();
			msg = "";
			this._errLevelVO.setMessage(EnumErrLevel.EndUser, msg);
		}
		return msg;
	}

	private List<IdGroupType> processFindGroup(String ml_idGroup_s, Locale locale) {
		MultiLangIdType multiLangIdType = MultiLangUtil.parsingId(ml_idGroup_s);
		String groupName = multiLangIdType.getType_s() + "_" + multiLangIdType.getTypeSeq_s() + "_" + multiLangIdType.getGroupname_s() + "_"
				+ multiLangIdType.getGroupSeq_s();
		List<IdGroupType> idGrouplist = new ArrayList<IdGroupType>();
		String name = "ml_" + multiLangIdType.getType_s() + "_" + multiLangIdType.getTypeSeq_s();
		ResourceBundle resourceBundle;
		try {
			resourceBundle = ResourceBundle.getBundle(MultiLangUtil.getMultiLangConfigPropertiesClassPath() + locale.toString() + "/" + name, locale);
		} catch (IOException ioe) {
			return idGrouplist;
		}

		TbsysmultilangVO Tbsysmultilang_tmp = new TbsysmultilangVO();
		List<TbsysmultilangVO> allList = new ArrayList<TbsysmultilangVO>();

		Enumeration<String> keyEnum = resourceBundle.getKeys();
		String tmpKey = "";
		while (keyEnum.hasMoreElements()) {
			tmpKey = keyEnum.nextElement();
			if (tmpKey.toLowerCase().contains(groupName.toLowerCase())) {
				Tbsysmultilang_tmp = new TbsysmultilangVO();
				Tbsysmultilang_tmp = MultiLangUtil.parsingMultiLangIdType(MultiLangUtil.parsingId(tmpKey));
				Tbsysmultilang_tmp.getComp_id().setLocale(locale.toString());
				Tbsysmultilang_tmp.setContent(resourceBundle.getString(tmpKey));
				allList.add(Tbsysmultilang_tmp);
			}
		}

		// separating the attribute and get contents
		if (!allList.isEmpty()) {
			// msg_01_group_001_attribute_001 ==> preAttribute =
			// msg_01_group_001_attribute
			// set value to IdGroupType

			IdGroupType idGroup = new IdGroupType();
			List<TbsysmultilangVO> inList = new ArrayList<TbsysmultilangVO>();
			String cmp = "";
			for (int i = 0; i < allList.size(); i++) {
				TbsysmultilangVO Tbsysmultilang = (TbsysmultilangVO) allList.get(i);
				if (i == 0) {
					cmp = Tbsysmultilang.getComp_id().getAttribute();
				}
				if (Tbsysmultilang.getComp_id().getAttribute().equals(cmp)) {
					inList.add(allList.get(i));
				} else {
					idGroup = new IdGroupType();
					idGroup.setAttributeName(cmp);
					idGroup.setIdAttributes(inList);
					idGrouplist.add(idGroup);
					cmp = Tbsysmultilang.getComp_id().getAttribute();
					inList = new ArrayList<TbsysmultilangVO>();
					inList.add(allList.get(i));
				}
				if (i == allList.size() - 1) {
					idGroup = new IdGroupType();
					idGroup.setAttributeName(cmp);
					idGroup.setIdAttributes(inList);
					idGrouplist.add(idGroup);
				}
			}
			return idGrouplist;
		} else {
			return idGrouplist;
		}
	}
	
	private String checkMultiLangValue(String ml_idGroup_s,String errorLevel){
		String content_locale ="";
		EnumErrLevel[] enumErrLevel = EnumErrLevel.values();
		for (EnumErrLevel enumErrLevel1 : enumErrLevel) {
			MultiLangIdType idType = MultiLangUtil.parsingId(ml_idGroup_s);
			String ml_id = "";
			
			if (this._errLevelVO.getMessage(enumErrLevel1) != null) {

				if (this._errLevelVO.getMessage(enumErrLevel1).trim().equals("")) {
					if (idType.getSeq_s().trim().equals("")){
						ml_id = idType.getType_s() + "_" + idType.getTypeSeq_s() + "_" + idType.getGroupname_s() + "_" + idType.getGroupSeq_s() + "_"
						+ enumErrLevel1.getErrLevelCode() + "_" + "001";
					}else{
						ml_id = idType.getType_s() + "_" + idType.getTypeSeq_s() + "_" + idType.getGroupname_s() + "_" + idType.getGroupSeq_s() + "_"
						+ enumErrLevel1.getErrLevelCode() + "_" + idType.getSeq_s();
					}
					
					this._errLevelVO.setMessage(enumErrLevel1, ml_id);
				}
			} else {
				if (idType.getSeq_s().trim().equals("")){
					ml_id = idType.getType_s() + "_" + idType.getTypeSeq_s() + "_" + idType.getGroupname_s() + "_" + idType.getGroupSeq_s() + "_"
					+ enumErrLevel1.getErrLevelCode() + "_" + "001";
				}else{
					ml_id = idType.getType_s() + "_" + idType.getTypeSeq_s() + "_" + idType.getGroupname_s() + "_" + idType.getGroupSeq_s() + "_"
					+ enumErrLevel1.getErrLevelCode() + "_" + idType.getSeq_s();
				}					

				this._errLevelVO.setMessage(enumErrLevel1, ml_id);
			}
			if (enumErrLevel1.getErrLevelCode().equals(errorLevel)) {
				content_locale = this._errLevelVO.getMessage(enumErrLevel1);
			}
		}
		return content_locale ;
	}
}
