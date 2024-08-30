package com.systex.jbranch.platform.common.errHandle;

import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.multiLang.IdGroupType;
import com.systex.jbranch.platform.common.multiLang.MultiLangBaseProperty;
import com.systex.jbranch.platform.common.multiLang.MultiLangFacadeIF;
import com.systex.jbranch.platform.common.multiLang.MultiLangIdType;
import com.systex.jbranch.platform.common.multiLang.MultiLangUtil;
import com.systex.jbranch.platform.common.platformdao.table.TbsysmultilangVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *此類別的功能描述：JavaBranch的Error物件，繼承Error 此程式的注意事項：
 *
 *@author Benson Chen
 *@since 此Class 從哪個版本開始加入 (外部的version)
 *@see
 **/
public class JBranchError extends Error {

	private String _ml_idGroup;
	private ErrLevelVO _errLevelVO = new ErrLevelVO();
	private Error _error;
	private List<String> _interMsgList;
	private UUID _uuid;

	public JBranchError(EnumErrInputType errInputType, String value) {
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

	// public JBranchError() {
	// }

	public JBranchError(String ml_idGroup) {
		super(ml_idGroup);
		this._ml_idGroup = ml_idGroup;
		this._interMsgList = new ArrayList<String>();
		this._uuid = null;
		// try {
		this.setErrLevelVO(ml_idGroup);
		// } catch (MultiLangException ex) {
		// if (ex != null) {
		// this._errLevelVO = ex.getErrLevelVO();
		// } else {
		// this._errLevelVO = new ErrLevelVO();
		// this._errLevelVO.setMessage(EnumErrLevel.EndUser,
		// "MultiLangException Error");
		// }
		// }
	}

	/**
	 *此類別的功能描述：建立JBranchException物件 此程式的注意事項：傳入多國語系群組編號存入class variable並call
	 * super constructor及setExceptionLevelVOList()函式
	 *
	 *@param 傳入多國語系群組編號
	 *@author Benson Chen
	 *@since 此Class 從哪個版本開始加入 (外部的version)
	 *@see
	 **/
	public JBranchError(String ml_idGroup, List<String> interMsgList) {
		super(ml_idGroup);
		this._ml_idGroup = ml_idGroup;
		this._interMsgList = interMsgList;
		this._uuid = null;
		// try {
		this.setErrLevelVO(ml_idGroup);
		// } catch (MultiLangException ex) {
		// if (ex != null) {
		// this._errLevelVO = ex.getErrLevelVO();
		// } else {
		// this._errLevelVO = new ErrLevelVO();
		// this._errLevelVO.setMessage(EnumErrLevel.EndUser,
		// "MultiLangException Error");
		// }
		// }
	}

	/**
	 *此類別的功能描述：建立JBranchException物件 此程式的注意事項：傳入多國語系群組編號存入class variable並call
	 * super constructor及setExceptionLevelVOList()函式
	 *
	 *@param 傳入多國語系群組編號
	 *@author Benson Chen
	 *@since 此Class 從哪個版本開始加入 (外部的version)
	 *@see
	 **/
	public JBranchError(String ml_idGroup, List<String> interMsgList, Error error) {
		super(ml_idGroup);
		this._ml_idGroup = ml_idGroup;
		this._interMsgList = interMsgList;
		this._error = error;
		this._uuid = null;
		// try {
		this.setErrLevelVO(ml_idGroup);
		// } catch (MultiLangException ex) {
		// if (ex != null) {
		// this._errLevelVO = ex.getErrLevelVO();
		// } else {
		// this._errLevelVO = new ErrLevelVO();
		// this._errLevelVO.setMessage(EnumErrLevel.EndUser,
		// "MultiLangException Error");
		// }
		// }
	}

	// /**
	// *此類別的功能描述：建立JBranchException物件
	// *此程式的注意事項：傳入多國語系群組編號存入class
	// variable及使用者權限物件及呼叫setExceptonLevelVOByUserPrivilege()函式。
	// *
	// *@param 傳入使用者權限物件(包含該使用者的ExceptionLevel等級)
	// *@param 傳入多國語系群組編號
	// *@author Benson Chen
	// *@since 此Class 從哪個版本開始加入 (外部的version)
	// *@see
	// **/
	// public JBranchError(UUID userPrivilege_s, String ml_idGroup_s,
	// List<String> interMsgList) {
	// this._ml_idGroup = ml_idGroup_s;
	// this._uuid = userPrivilege_s;
	// this._interMsgList = interMsgList;
	// try {
	// this.setErrLevelVOByUUID(userPrivilege_s, ml_idGroup_s);
	// } catch (MultiLangException ex) {
	// if (ex != null) {
	// this._errLevelVO = ex.getErrLevelVO();
	// } else {
	// this._errLevelVO = new ErrLevelVO();
	// this._errLevelVO.setMessage(EnumErrLevel.EndUser,
	// "MultiLangException Error");
	// }
	// }
	// }

	// /**
	// *此類別的功能描述：建立JBranchException物件
	// *此程式的注意事項：傳入多國語系群組編號存入class
	// variable及使用者權限物件及呼叫setExceptonLevelVOByUserPrivilege()函式。
	// *
	// *@param 傳入使用者權限物件(包含該使用者的ExceptionLevel等級)
	// *@param 傳入多國語系群組編號
	// *@author Benson Chen
	// *@since 此Class 從哪個版本開始加入 (外部的version)
	// *@see
	// **/
	// public JBranchError(UUID userPrivilege_s, String ml_idGroup_s,
	// List<String> interMsgList, Error error) {
	// this._ml_idGroup = ml_idGroup_s;
	// this._uuid = userPrivilege_s;
	// this._interMsgList = interMsgList;
	// this._error = error;
	// try {
	// this.setErrLevelVOByUUID(userPrivilege_s, ml_idGroup_s);
	// } catch (MultiLangException ex) {
	// if (ex != null) {
	// this._errLevelVO = ex.getErrLevelVO();
	// } else {
	// this._errLevelVO = new ErrLevelVO();
	// this._errLevelVO.setMessage(EnumErrLevel.EndUser,
	// "MultiLangException Error");
	// }
	// }
	// }

	/**
	 *此Method的功能描述：利用多國語系群組編號去取得該多國語系群組資料，使用MultiLangBaseProperty類別
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
		String[] languageArray = DataManager.getSystem().getDefaultValue().get("language").toString().split("_");
		Locale locale = new Locale(languageArray[0], languageArray[1]);
		// Locale locale = new Locale("en", "US");
		MultiLangFacadeIF baseProperty = new MultiLangBaseProperty(locale);
		List<IdGroupType> group = baseProperty.getByGroup(ml_idGroup_s);
		String content_locale = "";
		this._errLevelVO = new ErrLevelVO();
		// set content by multilang id if size > 0
		if (group.size() > 0) {
			for (int i = 0; i < group.size(); i++) {
				List<TbsysmultilangVO> Tbsysmultilang_list = group.get(i).getIdAttributes();
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
	public ErrLevelVO getErrLevelVO() {

		return _errLevelVO;
	}

	/**
	 *此Method的功能描述：利用多國語系群組編號及使用者權限物件去取得該多國語系群組資料，使用MultiLangBaseProperty類別(
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
	private String setErrLevelVOByUUID(UUID userPrivilege, String ml_idGroup_s) throws MultiLangException {
		// get errorLevel from UUID
		String errorLevel = "00";
		if (DataManager.existUser(userPrivilege)) {
			if (DataManager.getUser(userPrivilege) != null) {
				if (DataManager.getUser(userPrivilege).getErrLevel() != null)
					errorLevel = DataManager.getUser(userPrivilege).getErrLevel();
			}
		}
		// if (DataManager.getUser(userPrivilege) != null){
		// if (DataManager.getUser(userPrivilege).getErrLevel() != null)
		// errorLevel = DataManager.getUser(userPrivilege).getErrLevel();
		// }

		String[] languageArray = DataManager.getSystem().getDefaultValue().get("language").toString().split("_");
		Locale locale = new Locale(languageArray[0], languageArray[1]);

		// String errorLevel = "01";
		// Locale locale = new Locale("en", "US");

		MultiLangFacadeIF baseProperty = new MultiLangBaseProperty(locale);
		List<IdGroupType> group = baseProperty.getByGroup(ml_idGroup_s);
		String content_locale = "";
		String content_locale_tmp = "";
		if (this._errLevelVO == null) {
			this._errLevelVO = new ErrLevelVO();
		}

		// set content by multilang id if size > 0
		if (group.size() > 0) {
			for (int i = 0; i < group.size(); i++) {
				List<TbsysmultilangVO> Tbsysmultilang_list = group.get(i).getIdAttributes();
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
			// set ErrLevel = multilang id
			this.setErrLevelVOByMsg(ml_idGroup_s);
			content_locale = ml_idGroup_s;
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
	// public ErrLevelVO getErrLevelVOByUserPrivilege(UUID userPrivilege) {
	// this._uuid = userPrivilege;
	// try {
	// this.setErrLevelVOByUUID(userPrivilege, this._ml_idGroup);
	// } catch (MultiLangException ex) {
	// if (ex != null) {
	// this._errLevelVO = ex.getErrLevelVO();
	// } else {
	// this._errLevelVO = new ErrLevelVO();
	// this._errLevelVO.setMessage(EnumErrLevel.EndUser,
	// "MultiLangException Error");
	// }
	// }
	// return _errLevelVO;
	//
	// }

	/**
	 *此Method的功能描述：取得使用者權限後的錯誤訊息
	 *
	 *@return String
	 *@throws 若有丟出Exception
	 *             的說明 (Required)
	 *@since 此Method 從哪個版本開始加入(外部的version)
	 **/
	public String getMessage(UUID uuid) {
		this._uuid = uuid;
		String msg = "";
		try {
			msg = this.setErrLevelVOByUUID(uuid, this._ml_idGroup);
		} catch (MultiLangException ex) {
			if (ex != null) {
				this._errLevelVO = ex.getErrLevelVO();
			} else {
				this._errLevelVO = new ErrLevelVO();
				msg = "MultiLangException Error";
				this._errLevelVO.setMessage(EnumErrLevel.EndUser, msg);
			}
		}
		return msg;
	}

	public Error getError() {
		return _error;
	}

	public void setError(Error error) {
		this._error = error;
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
