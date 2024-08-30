package com.systex.jbranch.platform.common.multiLang;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.engine.transaction.spi.TransactionFactory;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.errHandle.MultiLangException;
import com.systex.jbranch.platform.common.platformdao.table.TbsysmultilangDaoIF;
import com.systex.jbranch.platform.common.platformdao.table.TbsysmultilangPK;
import com.systex.jbranch.platform.common.platformdao.table.TbsysmultilangVO;

/**
 *此類別的功能描述：多國語系的存取，使用DB存取的方式 Notices: 此程式的注意事項：無
 * 
 *@author Benson Chen
 *@since 此Class 從哪個版本開始加入 (外部的version)
 *@see
 **/
public class MultiLangDaoDB implements MultiLangDaoFacadeIF {
	DataAccessManager dam = null;
	TbsysmultilangUtil tbsysmultilangUtil = null;
	public MultiLangDaoDB() throws DAOException, JBranchException{
	  this._locale = Locale.getDefault();
	  dam = new DataAccessManager();
	  tbsysmultilangUtil = new TbsysmultilangUtil();
	}
	
	private MultiLangIdType _multiLangIdType;
	private Query _query;
	private Locale _locale;

	public MultiLangDaoDB(Locale locale) throws DAOException, JBranchException {
		this._locale = locale;
		dam = new DataAccessManager();
	}


	/**
	 * 此Method的功能描述：建立一個多國語系的資料
	 * 
	 * @param 傳入多國語系編號
	 *            、欲儲存的內容
	 * @return 是否執行成功
	 * @throws 若有丟出Exception
	 *             的說明 (Required)
	 * @see
	 * @since 此Method 從哪個版本開始加入(外部的version)
	 */
	public void create(String id_s, String content_s) throws MultiLangException {
		// error Msg combination
		List<String> errMsgList = new ArrayList<String>();
		errMsgList.add(id_s);

		_multiLangIdType = MultiLangUtil.parsingId(id_s);
		TbsysmultilangVO Tbsysmultilang = new TbsysmultilangVO();
		Tbsysmultilang = MultiLangUtil.parsingMultiLangIdType(_multiLangIdType);
		Tbsysmultilang.getComp_id().setLocale(this._locale.toString());
		Tbsysmultilang.setContent(content_s);
		Tbsysmultilang.setCreatetime(new Timestamp(System.currentTimeMillis()));
		Tbsysmultilang.setMemo("memo");
		Tbsysmultilang.setCreator("creator");
		Tbsysmultilang.setLastupdate(new Timestamp(System.currentTimeMillis()));
		Tbsysmultilang.setModifier("creator");
		Tbsysmultilang.setVersion((long)1);
		
		try {
			// create
			dam.create(Tbsysmultilang);
		} catch (Exception ex) {
			throw new MultiLangException("", errMsgList);
		}

	}

	/**
	 * 此Method的功能描述：建立一組多國語系的資料
	 * 
	 * @param 傳入多國語系編號
	 *            、欲儲存的語系與內容 Map<語系,內容>
	 * @return 是否執行成功
	 * @throws 若有丟出Exception
	 *             的說明 (Required)
	 * @see
	 * @since 此Method 從哪個版本開始加入(外部的version)
	 */
	public void create(String id_s, Map<String, String> locale_contents)
			throws MultiLangException {
		// error Msg combination
		boolean errFlag = false;
		List<String> errMsgList = new ArrayList<String>();
		StringBuffer errMsg_sb = new StringBuffer();
		errMsgList.add(id_s);

		_multiLangIdType = MultiLangUtil.parsingId(id_s);
		TbsysmultilangVO Tbsysmultilang = new TbsysmultilangVO();
		Tbsysmultilang = MultiLangUtil.parsingMultiLangIdType(_multiLangIdType);
		Set<String> set = locale_contents.keySet();
		String content = "";
		String[] locs = set.toArray(new String[0]);

	
		for (String locale_s : locs) {
			content = locale_contents.get(locale_s);
			Tbsysmultilang.getComp_id().setLocale(locale_s);
			Tbsysmultilang.setContent(content);
			Tbsysmultilang.setCreatetime(new Timestamp(System
					.currentTimeMillis()));
			Tbsysmultilang.setMemo("memo");
			Tbsysmultilang.setCreator("creator");
			Tbsysmultilang.setLastupdate(new Timestamp(System
					.currentTimeMillis()));
			Tbsysmultilang.setModifier("creator");
			Tbsysmultilang.setVersion((long)1);
			try {
				// create
				dam.create(Tbsysmultilang);
			} catch (Exception ex) {
				errFlag = true;
				errMsg_sb.append(locale_s).append(",");
			}
		}// end of for loop
		if (errFlag) {
			errMsgList.add(errMsg_sb.deleteCharAt(errMsg_sb.lastIndexOf(","))
					.toString());
			throw new MultiLangException("", errMsgList);
		}

	}

	/**
	 * 此Method的功能描述：刪除一個多國語系的資料
	 * 
	 * @param 傳入多國語系編號
	 * @return 是否執行成功
	 * @throws 若有丟出Exception
	 *             的說明 (Required)
	 * @see
	 * @since 此Method 從哪個版本開始加入(外部的version)
	 */
	public void destroy(String id_s) throws MultiLangException {
		// error Msg combination
		// boolean errFlag = false;
		List<String> errMsgList = new ArrayList<String>();

		errMsgList.add(id_s);

		_multiLangIdType = MultiLangUtil.parsingId(id_s);
		TbsysmultilangVO Tbsysmultilang = new TbsysmultilangVO();
		Tbsysmultilang = MultiLangUtil.parsingMultiLangIdType(_multiLangIdType);
		Tbsysmultilang.getComp_id().setLocale(this._locale.toString());
		try {
			// create
			dam.delete(Tbsysmultilang);
		} catch (Exception ex) {
			throw new MultiLangException("", errMsgList);
		}

	}

	/**
	 * 此Method的功能描述：刪除一組多國語系的資料(所有語系)
	 * 
	 * @param 傳入多國語系編號
	 * @return 是否執行成功
	 * @throws 若有丟出Exception
	 *             的說明 (Required)
	 * @see
	 * @since 此Method 從哪個版本開始加入(外部的version)
	 */
	public void destroyAll(String id_s) throws MultiLangException {
		// error Msg combination
		// boolean errFlag = false;
		List<String> errMsgList = new ArrayList<String>();
		// StringBuffer errMsg_sb = new StringBuffer();
		errMsgList.add(id_s);

		boolean del_flag = false;
		_multiLangIdType = MultiLangUtil.parsingId(id_s);
		TbsysmultilangVO Tbsysmultilang = new TbsysmultilangVO();
		Tbsysmultilang = MultiLangUtil.parsingMultiLangIdType(_multiLangIdType);

		try {
			// delete
			del_flag = tbsysmultilangUtil.deleteMultiFields(Tbsysmultilang);

			if (!del_flag) {
				throw new MultiLangException("", errMsgList);
			}
		} catch (Exception ex) {
			throw new MultiLangException("", errMsgList);
		}
	}

	/**
	 * 此Method的功能描述：修改一個多國語系的資料
	 * 
	 * @param 傳入多國語系編號
	 *            、欲儲存的檔案
	 * @return 是否執行成功
	 * @throws 若有丟出Exception
	 *             的說明 (Required)
	 * @see
	 * @since 此Method 從哪個版本開始加入(外部的version)
	 */
	public void edit(String id_s, String content_s) throws MultiLangException {
		// error Msg combination
		// boolean errFlag = false;
		List<String> errMsgList = new ArrayList<String>();
		StringBuffer errMsg_sb = new StringBuffer();
		errMsgList.add(id_s);

		_multiLangIdType = MultiLangUtil.parsingId(id_s);
		TbsysmultilangVO Tbsysmultilang = new TbsysmultilangVO();
		Tbsysmultilang = MultiLangUtil.parsingMultiLangIdType(_multiLangIdType);
		Tbsysmultilang.getComp_id().setLocale(this._locale.toString());

		try {
			List<TbsysmultilangVO> list = tbsysmultilangUtil
					.findByMultiFields(Tbsysmultilang);

			// got record from database
			if (list.size() == 1) {
				Tbsysmultilang = list.get(0);
				Tbsysmultilang.setContent(content_s);
				Tbsysmultilang.setLastupdate(new Timestamp(System
						.currentTimeMillis()));
				Tbsysmultilang.setModifier("Modifier");
				Tbsysmultilang.setVersion((long) Tbsysmultilang.getVersion()+ 1);
				// update
				dam.update(Tbsysmultilang);

			} else {
				errMsg_sb.append("Tbsysmultilang");
				errMsgList.add(errMsg_sb.toString());
				throw new MultiLangException("", errMsgList);
			}
		} catch (Exception ex) {
			throw new MultiLangException("", errMsgList);
		}

	}

	/**
	 * 此Method的功能描述：修改一組多國語系的資料
	 * 
	 * @param 傳入多國語系編號
	 *            、欲修改的語系與內容 Map<語系,內容>
	 * @return 是否執行成功
	 * @throws 若有丟出Exception
	 *             的說明 (Required)
	 * @see
	 * @since 此Method 從哪個版本開始加入(外部的version)
	 */
	public void edit(String id_s, Map<String, String> locale_contents)
			throws MultiLangException {
		// error Msg combination
		boolean errFlag = false;
		List<String> errMsgList = new ArrayList<String>();
		StringBuffer errMsg_sb = new StringBuffer();
		errMsgList.add(id_s);

		_multiLangIdType = MultiLangUtil.parsingId(id_s);
		TbsysmultilangVO Tbsysmultilang = new TbsysmultilangVO();
		Tbsysmultilang = MultiLangUtil.parsingMultiLangIdType(_multiLangIdType);

		Set<String> set = locale_contents.keySet();
		String content = "";
		String[] locs = set.toArray(new String[0]);
		try {
			TbsysmultilangPK TbsysmultilangPK;
			for (String locale_s : locs) {
				content = locale_contents.get(locale_s);
				Tbsysmultilang.getComp_id().setLocale(locale_s);

				List<TbsysmultilangVO> list = tbsysmultilangUtil
						.findByMultiFields(Tbsysmultilang);

				// got record from database
				if (list.size() == 1) {
					Tbsysmultilang = list.get(0);
					Tbsysmultilang.setContent(content);
					Tbsysmultilang.setLastupdate(new Timestamp(System
							.currentTimeMillis()));
					Tbsysmultilang.setModifier("Modifier");
					Tbsysmultilang.setVersion((long) Tbsysmultilang.getVersion()+ 1);

					// update
					dam.update(Tbsysmultilang);

				} else {
					errFlag = true;
					errMsg_sb.append(locale_s).append(",");
				}
			}// end of for loop
			if (errFlag) {
				errMsgList.add(errMsg_sb.deleteCharAt(
						errMsg_sb.lastIndexOf(",")).toString());
				throw new MultiLangException("", errMsgList);
			}
		} catch (Exception ex) {
			errMsgList.add(errMsg_sb.toString());
			throw new MultiLangException("", errMsgList);
		}

	}

	/**
	 * 此Method的功能描述：找出多國語系的所有檔案型別
	 * 
	 * @param 傳入BaseName的類別
	 * @return 一系列多國語系型別名稱
	 * @throws 若有丟出Exception
	 *             的說明 (Required)
	 * @see
	 * @since 此Method 從哪個版本開始加入(外部的version)
	 */
	public List<String> findAllType() {
		List<String> list = null;
		try {
			//TbsysmultilangPK
			list = tbsysmultilangUtil.distinctFields("comp_id.type","");

		} catch (Exception ex) {
			//ignore
		}
		return list;
	}

	/**
	 * 此Method的功能描述：找出該多國語系檔的群組資料(substring搜尋)
	 * 
	 * @param 傳入多國語系的群組編號子字串
	 *            、BaseName的類別、欲搜尋的檔案
	 * @return 一系列群組名稱
	 * @throws 若有丟出Exception
	 *             的說明 (Required)
	 * @see
	 * @since 此Method 從哪個版本開始加入(外部的version)
	 */
	public List<IdGroupType> findGroup(String subName_s) {
		_multiLangIdType = MultiLangUtil.parsingId(subName_s);
		TbsysmultilangVO Tbsysmultilang = new TbsysmultilangVO();
		Tbsysmultilang = MultiLangUtil.parsingMultiLangIdType(_multiLangIdType);
		Tbsysmultilang.getComp_id().setLocale(this._locale.toString());

		List<IdGroupType> idGrouplist = new ArrayList<IdGroupType>();

		try {

			List<TbsysmultilangVO> list = tbsysmultilangUtil
					.findByGroup(Tbsysmultilang);

			// set value to IdGroupType
			IdGroupType idGroup = new IdGroupType();
			List<TbsysmultilangVO> inList = new ArrayList<TbsysmultilangVO>();
			String cmp = "";
			for (int i = 0; i < list.size(); i++) {
				Tbsysmultilang = (TbsysmultilangVO) list.get(i);
				if (i == 0) {
					cmp = Tbsysmultilang.getComp_id().getAttribute();
				}
				if (Tbsysmultilang.getComp_id().getAttribute().equals(cmp)) {
					inList.add(list.get(i));
				} else {
					idGroup = new IdGroupType();
					idGroup.setAttributeName(cmp);
					idGroup.setIdAttributes(inList);
					idGrouplist.add(idGroup);
					cmp = Tbsysmultilang.getComp_id().getAttribute();
					inList = new ArrayList<TbsysmultilangVO>();
					inList.add(list.get(i));
				}
				if (i == list.size() - 1) {
					idGroup = new IdGroupType();
					idGroup.setAttributeName(Tbsysmultilang.getComp_id()
							.getAttribute());
					idGroup.setIdAttributes(inList);
					idGrouplist.add(idGroup);
				}
			}
		} catch (Exception ex) {
			//ignore
		}

		return idGrouplist;
	}

	public String findtById(String id_s) {
		_multiLangIdType = MultiLangUtil.parsingId(id_s);

		TbsysmultilangPK TbsysmultilangPK = new TbsysmultilangPK();
		TbsysmultilangPK.setType(_multiLangIdType.getType_s());
		TbsysmultilangPK.setTypeSeq(_multiLangIdType.getTypeSeq_s());
		TbsysmultilangPK.setGroupname(_multiLangIdType.getGroupname_s());
		TbsysmultilangPK.setGroupSeq(_multiLangIdType.getGroupSeq_s());
		TbsysmultilangPK.setAttribute(_multiLangIdType.getAttribute_s());
		TbsysmultilangPK.setSeq(_multiLangIdType.getSeq_s());
		TbsysmultilangPK.setLocale(_locale.toString());

		try {
			TbsysmultilangVO tbsysmultilangVO = (TbsysmultilangVO) dam
					.findByPKey(TbsysmultilangVO.TABLE_UID,TbsysmultilangPK);

			if (tbsysmultilangVO != null) {
				return tbsysmultilangVO.getContent();
			} else {
				return null;
			}

		} catch (Exception ex) {
			return null;
		}
	}

	public void changeLocale(Locale locale) {
		this._locale = locale;
	}

	public List<String> findTypeSeq(String type) {
		List<String> list = null;
		try {
			String condition = "comp_id.type='" +type + "'";
			list = tbsysmultilangUtil.distinctFields("comp_id.typeSeq", condition);
		} catch (Exception ex) {
			//ignore
		}

		return list;
	}

	public List<String> findLocale(String type) {
		List<String> list = null;
		try {
			if (!type.equals("")) {
				list = tbsysmultilangUtil.distinctFields("comp_id.locale",
						"comp_id.type='" + type+"'");
			} else {
				list = tbsysmultilangUtil.distinctFields("comp_id.locale",
						"");
			}
		} catch (Exception ex) {
			//ignore
		}

		return list;
	}

	public List<TbsysmultilangVO> findByProperties(String type, String typeSeq,
			String locale) {

		// 加入查詢條件
		List<TbsysmultilangVO> list = null;
		// Transaction Object
		try {
			list = tbsysmultilangUtil.findByProperties(type, typeSeq, locale);
		} catch (Exception ex) {
			//ignore
		}

		return list;
	}

	public void createByProperties(Properties properties)
			throws MultiLangException {
		TbsysmultilangVO tbsysmultilangVO;
		Set set = properties.keySet();
		String[] idArray = (String[]) set.toArray(new String[0]);
		try {
			for (int i = 0; i < idArray.length; i++) {

				tbsysmultilangVO = MultiLangUtil
						.parsingMultiLangIdType(MultiLangUtil
								.parsingId(idArray[i]));
				tbsysmultilangVO.getComp_id().setLocale(this._locale.toString());
				tbsysmultilangVO.setContent(properties.getProperty(idArray[i]));

				
				TbsysmultilangPK tbsysmultilangPK = tbsysmultilangVO.getComp_id();								
				
				List<TbsysmultilangVO> list_vo = tbsysmultilangUtil.findByMultiFields(tbsysmultilangVO);
				
				if(list_vo.size() == 1){
					tbsysmultilangVO.setMemo("memo");
					tbsysmultilangVO.setLastupdate(new Timestamp(System
							.currentTimeMillis()));
					tbsysmultilangVO.setModifier("Modifier");
					tbsysmultilangVO.setVersion((long) tbsysmultilangVO.getVersion()+ 1);
					dam.update(tbsysmultilangVO);
				}else if (list_vo.size() == 0){
					tbsysmultilangVO.setCreatetime(new Timestamp(System.currentTimeMillis()));
					tbsysmultilangVO.setMemo("memo");
					tbsysmultilangVO.setCreator("creator");
					tbsysmultilangVO.setLastupdate(new Timestamp(System
							.currentTimeMillis()));
					tbsysmultilangVO.setModifier("creator");
					tbsysmultilangVO.setVersion((long)1);
					dam.create(tbsysmultilangVO);
				}
																										
			}// end of for loop
		} catch (Exception ex) {
			throw new MultiLangException("");
		}

	}
}
