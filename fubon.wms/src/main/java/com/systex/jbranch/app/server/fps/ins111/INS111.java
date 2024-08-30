package com.systex.jbranch.app.server.fps.ins111;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.systex.jbranch.app.common.fps.table.TBINS_OUTBUY_MASTVO;
import com.systex.jbranch.app.server.fps.ins.parse.WSMappingParserUtils;
import com.systex.jbranch.app.server.fps.ins810.INS810;
import com.systex.jbranch.app.server.fps.ins810.INS810InputVO;
import com.systex.jbranch.app.server.fps.ins810.INS810OutputVO;
import com.systex.jbranch.app.server.fps.insjlb.FubonInsjlb;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsPremInputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsPremOutputVO;
import com.systex.jbranch.comutil.collection.CollectionSearchUtils;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.errHandle.NotFoundException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;


/**
 * INS111
 * 
 * @author Jimmy
 * @date 2017/08/15
 * @spec null
 */

@Component("ins111")
@Scope("request")
public class INS111 extends FubonWmsBizLogic{
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(INS111.class);
	
	@Autowired @Qualifier("insjlb")
	private FubonInsjlb insjlb;
	
	@Autowired @Qualifier("ins810")
	private INS810 ins810;
	
	public FubonInsjlb getInsjlb() {
		return insjlb;
	}

	public void setInsjlb(FubonInsjlb insjlb) {
		this.insjlb = insjlb;
	}
	
	public INS810 getIns810() {
		return ins810;
	}

	public void setIns810(INS810 ins810) {
		this.ins810 = ins810;
	}
	
	//======================== WEB 調用 ========================
	/**
	 * 取得輸入的客戶資訊 來源(本行客戶與非本行客戶)
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getCustInfo(Object body, IPrimitiveMap header) throws JBranchException {
		INS111InputVO inputVO = (INS111InputVO) body;
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT CUST_ID AS INSURED_ID, CUST_NAME AS CUST_NAME, BIRTH_DATE AS BIRTH_DATE, GENDER GENDER");
		sql.append(" FROM TBCRM_CUST_MAST CCM WHERE CUST_ID = :custId and AO_CODE in (:aoCode)");
		sql.append(" UNION ALL ");
		sql.append(" SELECT CUST_ID, CUST_NAME, BIRTH_DATE, GENDER FROM TBINS_CUST_MAST ICM ");
		sql.append(" WHERE CUST_ID = :custId AND NOT EXISTS  ");
		sql.append(" (SELECT 1 FROM TBCRM_CUST_MAST CCM WHERE CCM.CUST_ID = :custId and AO_CODE in (:aoCode)) ");
		
		ArrayList<String> loginAOs = (ArrayList<String>) getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST);
//		String loginAO = loginAOs.get(0);
		
		dam = getDataAccessManager();
		QueryConditionIF qc = null;
		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		qc.setObject("custId", inputVO.getINSURED_ID());
		qc.setObject("aoCode", loginAOs);
		qc.setQueryString(sql.toString());
		List<Map<String, Object>> custList = dam.exeQuery(qc);
		if(CollectionUtils.isNotEmpty(custList)) {
			custList.get(0).put("INSURED_AGE", getAge((Date)custList.get(0).get("BIRTH_DATE")));
		}
		sendRtnObject(custList);
	}
	
	//修改進入行外保單時查詢資料
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void queryData(Object body, IPrimitiveMap header) throws DAOException, JBranchException{
		INS111InputVO inputVO = (INS111InputVO) body;
		INS111OutputVO outputVO = new INS111OutputVO();
		
		Object[] datas = getQueryData(inputVO.getINSSEQ());
		outputVO.setIS_MAIN_YList((List<Map<String, Object>>) datas[0]);
		outputVO.setIS_MAIN_NList((List<Map<String, Object>>) datas[1]);
		sendRtnObject(outputVO);
	}
	
	//產生各下拉選單項目
	public void getDrop_down(Object body, IPrimitiveMap header) throws DAOException, JBranchException{
		INS111InputVO inputVO = (INS111InputVO) body;
		INS111OutputVO outputVO = new INS111OutputVO();
		
			 for(Map<String, Object> map: getProdInfo(inputVO.getPRD_KEY_NO())){
				 if(map.get("LIST_E") != null){
					 String[] LIST_E = map.get("LIST_E").toString().split(",");//幣別
					 outputVO.setCURR_CDList(gern_drop(LIST_E, LIST_E));
				 }
				 
				 if(map.get("ITEM_Y") != null && map.get("LIST_Y") !=null){
					 String[] ITEM_Y = map.get("ITEM_Y").toString().split(",");//繳費年期
					 String[] LIST_Y = map.get("LIST_Y").toString().split(",");
					 outputVO.setPAYMENTYEAR_SELList(gern_drop(ITEM_Y, LIST_Y));
				 }
				 
				 if(map.get("ITEM_A") != null && map.get("LIST_A") !=null){
					 String[] ITEM_A = map.get("ITEM_A").toString().split(",");//保障年期
					 String[] LIST_A = map.get("LIST_A").toString().split(",");
					 outputVO.setCOVERYEAR_SELList(gern_drop(ITEM_A, LIST_A));
				 }

				 if(map.get("LIST_O") !=null){
			 		String[] LIST_O = map.get("LIST_O").toString().split(",");//投保對象
			 		outputVO.setINSURED_OBJECTList(gern_drop(LIST_O,LIST_O )); 
			 	 }
				 
				 if(map.get("ITEM_K") != null && map.get("LIST_K") !=null){
					 String[] ITEM_K = map.get("ITEM_K").toString().split(",");//種類
					 String[] LIST_K = map.get("LIST_K").toString().split(",");
					 outputVO.setKIND_SELList(gern_drop(ITEM_K, LIST_K));
				 }
				 
				 if(map.get("ITEM_P") != null && map.get("LIST_P") !=null){
					 String[] ITEM_P = map.get("ITEM_P").toString().split(",");//保額
					 String[] LIST_P = map.get("LIST_P").toString().split(",");
					 outputVO.setQUANTITY_STYLEList(gern_drop(ITEM_P, LIST_P));
				 }
				 
				 outputVO.setQUANTITY_STYLE(ObjectUtils.toString(map.get("ITEM_U"), null));//保額
				 outputVO.setCOVERCACULUNITDESC(ObjectUtils.toString(map.get("COVERCACULUNITDESC"), null));//單位
				 outputVO.setIS_WL(ObjectUtils.toString(map.get("IS_WL"), null));//是否終身
				 
				 if (map.get("WL_TERM") != null) {
					 outputVO.setWL_TERM(map.get("WL_TERM").toString().replace("@", "歲"));//終身年齡
				 }
			 }
		 this.sendRtnObject(outputVO);
	}
	
	//新增附約
	public void addIS_MAIN_N(Object body, IPrimitiveMap header) throws APException{
		INS111_IS_MAIN_NInputVO inputVO = (INS111_IS_MAIN_NInputVO) body;
		INS111OutputVO outputVO = new INS111OutputVO();
		
		//特定被保人與特定保單商品是否存在與保單序號是否存在的集合
		Object[] isExistObj = isExistWhenNew(inputVO.getIS_MAIN_NList(), inputVO.getINSURED_ID().toUpperCase(), inputVO.getPRD_KEY_NO());
		
		if(inputVO.getIS_MAIN_NList().size()<= 0){
			List<Map<String, Object>> IS_MAIN_NList = new ArrayList<Map<String,Object>>();
			Map<String, Object> IS_MAIN_N = new HashMap<String, Object>();
			IS_MAIN_NList.add(setIS_MAIN_N(inputVO, IS_MAIN_N));
			outputVO.setIS_MAIN_NList(IS_MAIN_NList);
		}else{
			Map<String, Object> IS_MAIN_N = new HashMap<String, Object>();
			if("new".equals(inputVO.getTYPE())){				
				if((boolean)isExistObj[0]){
					throw new APException("同一人同一商品請確認是否重複");
				}else{
					setIS_MAIN_N(inputVO, IS_MAIN_N);
				}
			}else if("update".equals(inputVO.getTYPE())){
				if((boolean)isExistObj[0]) {
					if(isExistObj[1] == null && inputVO.getKEY_NO() == null) {
						throw new APException("同一人同一商品請確認是否重複");
					} else {
						if((isExistObj[1] == null && inputVO.getKEY_NO() != null) || (isExistObj[1] != null && inputVO.getKEY_NO() == null) ) {
							throw new APException("同一人同一商品請確認是否重複");
						} else {
							if(((Double)isExistObj[1]).equals(Double.valueOf(inputVO.getKEY_NO()))) { // PK 一樣
								Map<String, Object> currentMap = CollectionSearchUtils.findMapInColleciton(inputVO.getIS_MAIN_NList(), "KEYNO", Double.valueOf(inputVO.getKEY_NO()));
								setIS_MAIN_N(inputVO, currentMap);
							} else { // PK 不一樣
								throw new APException("同一人同一商品請確認是否重複");
							}
						}
					}
				} else {
					Map<String, Object> currentMap = CollectionSearchUtils.findMapInColleciton(inputVO.getIS_MAIN_NList(), "KEYNO", Double.valueOf(inputVO.getKEY_NO()));
					setIS_MAIN_N(inputVO, currentMap);
				}
			}

			if(IS_MAIN_N.size()>0){
				inputVO.getIS_MAIN_NList().add(IS_MAIN_N);
			}
//				Map<String, Object> isMainNList = inputVO.getIS_MAIN_NList().get(0);
			
			outputVO.setIS_MAIN_NList(inputVO.getIS_MAIN_NList());
		}
		this.sendRtnObject(outputVO);
	}
	
	//判斷被保人+保單商品是否已存在，並判斷是否有保單序號
	private Object[] isExistWhenNew(List<Map<String, Object>> totalNList, String insuredId, String prdKeyno) {
		boolean isExist = false;
		Object keyNoObject = null;
		for(Map<String, Object> totalNMap : totalNList) {
			if(insuredId.equals(totalNMap.get("INSURED_ID")) && prdKeyno.equals(totalNMap.get("PRD_KEYNO"))) {
				isExist = true;
				keyNoObject = totalNMap.get("KEYNO");
				break;
			}
		}
		return new Object[]{isExist, keyNoObject};
	}
	
	//新增行外保單
	public void Save_Data(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		INS111InputVO inputVO = (INS111InputVO) body;
		INS111OutputVO outputVO = new INS111OutputVO();
		
		dam = getDataAccessManager();
		TBINS_OUTBUY_MASTVO voMast = new TBINS_OUTBUY_MASTVO();
		String loginAO = ((ArrayList<String>) getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST)).get(0);
		String insseq = get_INSSEQ(inputVO.getINSURED_ID(), inputVO.getPOLICY_NBR(), inputVO.getCOM_ID());
		
		voMast.setKEYNO(new BigDecimal(get_KEYNO()));
		
		write_tod_Y(voMast, inputVO, loginAO, insseq);
		
		dam.create(voMast);
		
		if(inputVO.getIS_MAIN_NList().size()>0){
			for(Map<String, Object> map:inputVO.getIS_MAIN_NList()){
				TBINS_OUTBUY_MASTVO voDetail = new TBINS_OUTBUY_MASTVO();
				voDetail.setKEYNO(new BigDecimal(get_KEYNO()));
				write_tod_N(voDetail, inputVO, map, loginAO, insseq);
				dam.create(voDetail);
			}

		}
		sendRtnObject(null);
	}
	
	//新增行外保單
	public void Update_Data(Object body, IPrimitiveMap header) throws ParseException, JBranchException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IntrospectionException{
		INS111InputVO inputVO = (INS111InputVO) body;
		INS111OutputVO outputVO = new INS111OutputVO();
		String loginAO = ((ArrayList<String>) getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST)).get(0);
		String insseq = get_INSSEQ(inputVO.getINSURED_ID(), inputVO.getPOLICY_NBR(), inputVO.getCOM_ID());
		
		dam = getDataAccessManager();
		TBINS_OUTBUY_MASTVO voMast = new TBINS_OUTBUY_MASTVO();
		voMast = (TBINS_OUTBUY_MASTVO) dam.findByPKey(TBINS_OUTBUY_MASTVO.TABLE_UID, new BigDecimal(inputVO.getKEYNO()));
		
		Map<String, Object> objMap = WSMappingParserUtils.objectToMap(inputVO);
		if(null != voMast){
			write_tod_Y(voMast, inputVO, loginAO, insseq);
			dam.update(voMast);
		}
		
		if(inputVO.getIS_MAIN_NList().size()>0){
			for(Map<String, Object> map:inputVO.getIS_MAIN_NList()){
				TBINS_OUTBUY_MASTVO voDetail = null;
				if(map.get("KEYNO") != null){
					voDetail = (TBINS_OUTBUY_MASTVO) dam.findByPKey(TBINS_OUTBUY_MASTVO.TABLE_UID, new BigDecimal(ObjectUtils.toString(map.get("KEYNO"))));
					write_tod_N(voDetail, inputVO, map, loginAO, insseq);
					dam.update(voDetail);
				}else{
					voDetail = new TBINS_OUTBUY_MASTVO();
					voDetail.setKEYNO(new BigDecimal(get_KEYNO()));
					write_tod_N(voDetail, inputVO, map, loginAO, insseq);
					dam.create(voDetail);
				}
				
			}

		}
		sendRtnObject(null);
	}
	
	//======================== WEB 處理========================
	//處理為下拉選單
	private  List<Map<String , Object>> gern_drop(String[] item,String[] list){
		 List<Map<String , Object>> drop_downList = new ArrayList<Map<String,Object>>();//繳費年期
		 if(item.length>0 && list.length>0){
			 for(int y=0;y<item.length;y++){
				 Map<String, Object> drop_down = new HashMap<String, Object>();
				 drop_down.put("DATA", item[y]);
				 drop_down.put("LABEL", list[y]);
				 drop_downList.add(drop_down);
			 } 
		 }
		
		return drop_downList;
	}
	
	//把資料put到IS_MAIN_N
	private Map<String, Object> setIS_MAIN_N(INS111_IS_MAIN_NInputVO inputVO,Map<String, Object> IS_MAIN_N){
		IS_MAIN_N.put("PRD_KEYNO", inputVO.getPRD_KEY_NO());
		IS_MAIN_N.put("INSURED_ID", inputVO.getINSURED_ID().toUpperCase());
		IS_MAIN_N.put("INSURED_BIRTHDAY", inputVO.getINSURED_BIRTHDAY());
		IS_MAIN_N.put("INSURED_NAME", inputVO.getINSURED_NAME());
		IS_MAIN_N.put("PRD_NAME", inputVO.getPRD_NAME());
		IS_MAIN_N.put("PRD_ID", inputVO.getPRD_ID());
		IS_MAIN_N.put("IS_MAIN", inputVO.getIS_MAIN());
		IS_MAIN_N.put("PAYMENTYEAR_SEL", inputVO.getPAYMENTYEAR_SEL());
		IS_MAIN_N.put("EFFECTED_DATE", inputVO.getEFFECTED_DATE());
		IS_MAIN_N.put("PAY_TYPE", inputVO.getPAY_TYPE());
		IS_MAIN_N.put("INSURED_OBJECT", inputVO.getINSURED_OBJECT());
		IS_MAIN_N.put("KIND_SEL", inputVO.getKIND_SEL());
		IS_MAIN_N.put("INSYEARFEE", inputVO.getINSYEARFEE());
		IS_MAIN_N.put("GENDER", inputVO.getGENDER());
		
		IS_MAIN_N.put("COVERYEAR_SEL", inputVO.getCOVERYEAR_SEL()); // 保障年期
		IS_MAIN_N.put("PAYMENTYEAR_SEL", inputVO.getPAYMENTYEAR_SEL()); // 繳費年期
		IS_MAIN_N.put("KIND_SEL", inputVO.getKIND_SEL()); // 種類
		IS_MAIN_N.put("INSURED_OBJECT", inputVO.getINSURED_OBJECT()); // 對象
		
		IS_MAIN_N.put("UPQTY_SEL", inputVO.getUPQTY_SEL()); // 
		IS_MAIN_N.put("UPTYPE", inputVO.getUPTYPE()); // 
		IS_MAIN_N.put("INSUREDAMT", inputVO.getINSUREDAMT()); // 
		IS_MAIN_N.put("BENEFICIARY_YN", inputVO.getBENEFICIARY_YN()); // 
		
		IS_MAIN_N.put("TITLE_Y", inputVO.getLabelTITLE_Y()); // 
		IS_MAIN_N.put("TITLE_A", inputVO.getLabelTITLE_A()); // 
		IS_MAIN_N.put("TITLE_O", inputVO.getLabelTITLE_O()); // 
		IS_MAIN_N.put("TITLE_K", inputVO.getLabelTITLE_K()); //

		return IS_MAIN_N;
	}
	
	//寫入TBINS_OUTBUY_MASTVO欄位by主約
	private void write_tod_Y(TBINS_OUTBUY_MASTVO tod,INS111InputVO inputVO, String ao, String insseq) throws JBranchException{
		tod.setAO_CODE(ao);
		tod.setINSSEQ(insseq);
		tod.setINSURED_AGE(new BigDecimal(getAge(inputVO.getINSURED_BIRTHDAY())));
		tod.setSTATUS("01");
		tod.setCUST_ID(inputVO.getINSURED_ID());
		tod.setPOLICY_NBR(inputVO.getPOLICY_NBR());
		tod.setPRD_KEYNO(inputVO.getPRD_KEY_NO());
		tod.setIS_MAIN(inputVO.getIS_MAIN());
		tod.setINSURED_NAME(inputVO.getINSURED_NAME());
		tod.setINSURED_ID(inputVO.getINSURED_ID());
		tod.setINSURED_BIRTHDAY(new Timestamp(inputVO.getINSURED_BIRTHDAY().getTime()));
		tod.setEFFECTED_DATE(new Timestamp(inputVO.getEFFECTED_DATE().getTime()));
		
		tod.setCURR_CD(inputVO.getCURR_CD());
		tod.setPAYTYPE(inputVO.getPAY_TYPE());
		tod.setPAYMENTYEAR_SEL(inputVO.getPAYMENTYEAR_SEL());
		tod.setCOVERYEAR_SEL(inputVO.getCOVERYEAR_SEL());
		tod.setKIND_SEL(inputVO.getKIND_SEL());
		tod.setBENEFICIARY_YN(inputVO.getBENEFICIARY_YN());
		
		if (StringUtils.isNotBlank(inputVO.getINSYEARFEE())) {
			tod.setINSYEARFEE(new BigDecimal(inputVO.getINSYEARFEE().replaceAll(",", "")));
		}		
		tod.setINSURED_OBJECT(inputVO.getINSURED_OBJECT());
		if (StringUtils.isNotBlank(inputVO.getUPTYPE())) {
			tod.setUPTYPE(inputVO.getUPTYPE());
		}		
		if (StringUtils.isNotBlank(inputVO.getUPQTY_SEL())) {
			tod.setUPQTY_SEL(inputVO.getUPQTY_SEL());
		}
		if (StringUtils.isNotBlank(inputVO.getINSUREDAMT())) {
			tod.setINSUREDAMT(new BigDecimal(inputVO.getINSUREDAMT()));
		}
		if(StringUtils.isNotBlank(inputVO.getGENDER())){
			tod.setINSURED_GENDER(inputVO.getGENDER());
		}
	}

	//寫入TBINS_OUTBUY_MASTVO欄位by附約
	private void write_tod_N(TBINS_OUTBUY_MASTVO tod, INS111InputVO mastInputVO, Map<String, Object> map, String loginAO, String insseq) throws ParseException, JBranchException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date birthday = sdf.parse(String.valueOf(map.get("INSURED_BIRTHDAY")));
		Date effecedDate = sdf.parse(String.valueOf(map.get("EFFECTED_DATE")));
		
		tod.setPOLICY_NBR(mastInputVO.getPOLICY_NBR());
		tod.setCUST_ID(mastInputVO.getINSURED_ID());
		tod.setPAYTYPE(mastInputVO.getPAY_TYPE());
		tod.setCURR_CD(mastInputVO.getCURR_CD());
		tod.setAO_CODE(loginAO);
		tod.setINSSEQ(insseq);
		tod.setSTATUS("01");
		tod.setPRD_KEYNO(ObjectUtils.toString(map.get("PRD_KEYNO"), null));
		tod.setINSURED_BIRTHDAY(new Timestamp(birthday.getTime()));
		tod.setINSURED_AGE(new BigDecimal(getAge(birthday)));
		tod.setIS_MAIN(ObjectUtils.toString(map.get("IS_MAIN"), null));
		tod.setINSURED_ID(ObjectUtils.toString(map.get("INSURED_ID"), null));
		tod.setINSURED_NAME(ObjectUtils.toString(map.get("INSURED_NAME"), null));
		tod.setEFFECTED_DATE(new Timestamp(effecedDate.getTime()));
		tod.setPAYMENTYEAR_SEL(ObjectUtils.toString(map.get("PAYMENTYEAR_SEL"), null));			
		tod.setINSURED_OBJECT(ObjectUtils.toString(map.get("INSURED_OBJECT"), null));
		tod.setKIND_SEL(ObjectUtils.toString(map.get("KIND_SEL"), null));   
		tod.setINSURED_GENDER(ObjectUtils.toString(map.get("GENDER"), null));
		tod.setUPTYPE(ObjectUtils.toString(map.get("UPTYPE"), null));
		tod.setUPQTY_SEL(ObjectUtils.toString(map.get("UPQTY_SEL"), null));
		tod.setBENEFICIARY_YN(ObjectUtils.toString(map.get("BENEFICIARY_YN"), null));
		tod.setINSYEARFEE(!org.springframework.util.StringUtils.isEmpty(map.get("INSYEARFEE")) ? 
				new BigDecimal(String.valueOf(map.get("INSYEARFEE")).replaceAll(",", "")):null);
		tod.setINSUREDAMT(!org.springframework.util.StringUtils.isEmpty(map.get("INSUREDAMT")) ? 
				new BigDecimal(String.valueOf(map.get("INSUREDAMT"))) : null);
		tod.setCOVERYEAR_SEL(ObjectUtils.toString(map.get("COVERYEAR_SEL"),null));
	}
	
	//======================== WS 調用 ========================
	/**
     * ws 調用後取得 保單詳細內容
     * 提供給 APP 使用的 API <br>
     * <code> <b> 2.5.3 </b> 取得保單內容 (主約連同其附約)</code>
     * @param inputVO : custId : 客戶ID, insSeq : 保單號碼
     * @return Object : insSeq : 保單號碼, mainContract : 主約保單資料, attachment : 附約保單資料
	 * @throws DAOException 
     * @throws JBranchException : <br>
     * <p>
     * 		<b>客製訊息如下 :</b>
     * 		<li>查無資料: </li>
     * </p>
     */
	@SuppressWarnings("rawtypes")
	public void getPolicyContent(Object body, IPrimitiveMap header) throws DAOException, JBranchException{
		GenericMap inputGMap = new GenericMap((Map) body);
		System.out.println(new Gson().toJson(inputGMap.getParamMap()));
		
		Object[] datas = getQueryData(inputGMap.getNotNullStr("insSeq")); // 取得保單資料 主 + 附
		Map<String, Object> mainYMap = ((List<Map<String, Object>>) datas[0]).get(0); // 主約資料	
		List<Map<String, Object>> mainNList = (List<Map<String, Object>>) datas[1]; // 附約資料
		Map<String, Object> finalOutputMap = WSMappingParserUtils.wsGetPolicyContentMapping(mainYMap, mainNList);
		System.out.println(new Gson().toJson(finalOutputMap));
		
		this.sendRtnObject(finalOutputMap);		
	}
	
	/**
	 * ws 調用後取得某產品下拉相關所有東西
	 * [繳費年期、保障年期、類型之類的東西]
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	@SuppressWarnings("rawtypes")
	public void getProductOption(Object body, IPrimitiveMap header) throws JBranchException {
		GenericMap inputMap = new GenericMap((Map) body);
		System.out.println(new Gson().toJson(inputMap.getParamMap()));
		List<Map<String, Object>> prodInfoList = getProdInfo(inputMap.getStr("prdKeyno"));
		Map<String, Object> toMap = new HashMap<String, Object>();
		WSMappingParserUtils.dropProdInfoMapping(prodInfoList.get(0), toMap);
		System.out.println(new Gson().toJson(toMap));
		this.sendRtnObject(toMap);
	}
	
	/**
	 * ws 調用 進行新增(更新)行外保單
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws ParseException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void updatePolicy(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		dam = this.getDataAccessManager();
		GenericMap mapAll = new GenericMap((Map) body);
		System.out.println(new Gson().toJson(mapAll.getParamMap()));
		Map<String, Object> mapMast = (Map<String, Object>) mapAll.get("policy");
		List<Map<String, Object>> mapDetails = (ArrayList<Map<String, Object>>) mapAll.get("attachment");
		
		List<TBINS_OUTBUY_MASTVO> insertList = new ArrayList<TBINS_OUTBUY_MASTVO>();
		List<TBINS_OUTBUY_MASTVO> updateList = new ArrayList<TBINS_OUTBUY_MASTVO>();
		
		// 主約
		prepareInsertOrUpdateData(mapMast, null, true, insertList, updateList);
		
		// 附約
		for(Map<String, Object> mapDetail : mapDetails) {
			prepareInsertOrUpdateData(mapMast, mapDetail, false, insertList, updateList);
		}
		
		doInsertOrUpdateData(insertList, dam, 0);
		doInsertOrUpdateData(updateList, dam, 1);
		this.sendRtnObject(null);
	}
	
	//======================== WS 處理========================
	/**
	 * 統一處理要 insert or update
	 * @param insertList
	 * @param dam
	 * @param isInsert 0-新增, 1-更新
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private void doInsertOrUpdateData(List<TBINS_OUTBUY_MASTVO> dataList, DataAccessManager dam, int isInsert) throws DAOException, JBranchException {
		if(dataList.size() >0 ){
			for(TBINS_OUTBUY_MASTVO vo : dataList) {
				if(isInsert == 0) {
					dam.create(vo);
				} else {
					dam.update(vo);
				}
			}
		}
	}
	
	/**
	 * 判斷當前是新增還是更新，並取得 VO (0-新增, 1-更新)
	 * @param map
	 * @param dam
	 * @return
	 * @throws NotFoundException
	 * @throws DAOException
	 */
	private Object[] getStatusAndVO(Map<String, Object> map, DataAccessManager dam) throws NotFoundException, DAOException {
		Object[] objArr = new Object[2];
		String pkNo = ObjectUtils.toString(map.get("policySerial"));
		int isInsert = 0;
		TBINS_OUTBUY_MASTVO vo = null;
		if(StringUtils.isEmpty(pkNo)) {
			vo = new TBINS_OUTBUY_MASTVO();
		} else {
			vo = (TBINS_OUTBUY_MASTVO) dam.findByPKey(TBINS_OUTBUY_MASTVO.TABLE_UID, new BigDecimal(pkNo));
			isInsert = 1;
		}
		objArr[0] = isInsert;
		objArr[1] = vo;
		return objArr;
	}
	
	/**
	 * 資料準備，把所有的資料分群 insert 的一群，update 的一群
	 * @param mapData
	 * @param insertList
	 * @param updateList
	 * @throws ParseException 
	 * @throws JBranchException 
	 */
	private void prepareInsertOrUpdateData(Map<String, Object> mapMast, Map<String, Object> mapDetail, boolean isMast, List<TBINS_OUTBUY_MASTVO> insertList, List<TBINS_OUTBUY_MASTVO> updateList) 
			throws ParseException, JBranchException {
		Map<String, Object> currentMap = isMast ? mapMast : mapDetail;
		Object[] obj = getStatusAndVO(currentMap, dam);
		TBINS_OUTBUY_MASTVO vo = (TBINS_OUTBUY_MASTVO) obj[1];
		reCombonVo(new GenericMap(mapMast), new GenericMap(mapDetail), isMast, vo);
		if(((int) obj[0]) == 0) {
			insertList.add(vo);
		} else {
			updateList.add(vo);
		}
	}
	
	/**
	 * 針對單一一筆進行轉換動作
	 * @param mast 主約資料
	 * @param detail 附約資料
	 * @param isMast 當前是主約資料還是附約資料 (用途：附約有些要來自主約)
	 * @param vo 當前是要 update 的 VO 還是 insert 的 VO
	 * @return 當前轉換成 TBINS_OUTBUY_MASTVO
	 * @throws ParseException 
	 * @throws JBranchException 
	 */
	private void reCombonVo(GenericMap mast, GenericMap detail, boolean isMast, TBINS_OUTBUY_MASTVO vo) throws ParseException, JBranchException {
		// 如果是主約就根據 mast 為主反之
		GenericMap currentMap = isMast ? mast : detail;
		
		ArrayList<String> loginAO = (ArrayList<String>) getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST);
		currentMap.put("ao", loginAO.get(0));
		// 新增
		if(vo.getKEYNO() == null) {
			vo.setKEYNO(new BigDecimal(get_KEYNO()));
		} 
		mappingData(vo, currentMap, isMast);		
		
		// 把附約要塞的跟主約一樣的寫進來
		if(!isMast) {
			vo.setINSSEQ(get_INSSEQ(mast.getNotNullStr("insuredId"), mast.getNotNullStr("policyNbr"), mast.getNotNullStr("comId")));
			vo.setPOLICY_NBR(mast.getNotNullStr("policyNbr"));
			vo.setCUST_ID(mast.getNotNullStr("insuredId"));
			
			GenericMap mastItemMap = new GenericMap(WSMappingParserUtils.itemListToMap((List<Map<String, Object>>) currentMap.get("item")));
			
			vo.setPAYTYPE(mastItemMap.getNotNullStr("paytype"));
			vo.setCURR_CD(mastItemMap.getNotNullStr("currCd"));
		}
	}
	
	/**
	 * 統一 mapping 主約附約 mapping 要一樣
	 * @param vo
	 * @param currentMap
	 * @param isMast
	 * @throws ParseException
	 * @throws APException
	 */
	private void mappingData(TBINS_OUTBUY_MASTVO vo, GenericMap currentMap, boolean isMast) throws ParseException, APException {
		INS810InputVO ins810InputVO = new INS810InputVO();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date birthday = sdf.parse(currentMap.getNotNullStr("insuredBirthday"));
		Date effectedDate = sdf.parse(currentMap.getNotNullStr("effectedDate"));
		ins810InputVO.setBirthday(birthday);
		int age = getIns810().getAge(ins810InputVO).getAge();
		
		vo.setINSSEQ(get_INSSEQ(currentMap.getNotNullStr("insuredId"), currentMap.getNotNullStr("policyNbr"), currentMap.getNotNullStr("comId")));
		vo.setAO_CODE(currentMap.getNotNullStr("ao"));
		vo.setINSURED_AGE(new BigDecimal(age));
		vo.setSTATUS("01");
		vo.setCUST_ID(currentMap.getNotNullStr("insuredId"));
		vo.setPOLICY_NBR(currentMap.getNotNullStr("policyNbr"));
		vo.setPRD_KEYNO(currentMap.getNotNullStr("prdKeyno"));
		vo.setIS_MAIN(isMast ? "Y" : "N");
		vo.setINSURED_NAME(currentMap.getNotNullStr("insuredName"));
		vo.setINSURED_ID(currentMap.getNotNullStr("insuredId"));
		vo.setINSURED_BIRTHDAY(new Timestamp(birthday.getTime()));
		vo.setEFFECTED_DATE(new Timestamp(effectedDate.getTime()));
		vo.setINSURED_GENDER(currentMap.getNotNullStr("gender"));
		vo.setINSYEARFEE(currentMap.getBigDecimal("insyearfee"));
		String beneficiaryYN = currentMap.getNotNullStr("beneficiaryYN");
		if ("是".equals(beneficiaryYN)) {
			beneficiaryYN = "Y";
		} else if ("否".equals(beneficiaryYN)) {
			beneficiaryYN = "N";
		}
		vo.setBENEFICIARY_YN(beneficiaryYN);
		
		// item
		GenericMap itemMap = new GenericMap(WSMappingParserUtils.itemListToMap((List<Map<String, Object>>) currentMap.get("item")));
		
		vo.setCURR_CD(itemMap.getNotNullStr("currCd"));
		vo.setPAYTYPE(itemMap.getNotNullStr("paytype"));
		vo.setPAYMENTYEAR_SEL(itemMap.getNotNullStr("paymentyearSel"));
		vo.setCOVERYEAR_SEL(itemMap.getNotNullStr("coveryearSel"));
		vo.setKIND_SEL(itemMap.getNotNullStr("kindSel"));
		vo.setINSURED_OBJECT(itemMap.getNotNullStr("insuredObject"));

		vo.setUPTYPE(itemMap.getNotNullStr("uptype"));
		if(vo.getUPTYPE().isEmpty()) {
			vo.setINSUREDAMT(itemMap.getBigDecimal("assureAmt"));
		} else {
			vo.setUPQTY_SEL(itemMap.getNotNullStr("assureAmt"));
		}
	}

	//======================== 共用 調用 & 處理========================
	/**
	 * 共用取得保單詳細資料
	 * @param insSeq 保單自訂序號 
	 * @return object[] 0-主約資料 1-附約資料
	 * @throws DAOException
	 * @throws JBranchException
	 */
	@SuppressWarnings("unchecked")
	public Object[] getQueryData(String insSeq) throws DAOException, JBranchException{
		Object[] datas = new Object[2];
		List<Map<String, Object>> queryList = new ArrayList<Map<String,Object>>();
		
		dam = getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append(" Select a. KEYNO, a. INSSEQ, a.INSURED_ID,  a.INSURED_NAME, a.INSURED_BIRTHDAY, ");
		sql.append(" a.POLICY_NBR, a.IS_MAIN, a.PRD_KEYNO, a.PAYMENTYEAR_SEL, a.EFFECTED_DATE, a.COVERYEAR_SEL, ");
		sql.append(" a.LOCAL_INSUREDAMT, TO_CHAR(a.INSYEARFEE,'FM999,999,999,999,999,999') as INSYEARFEE, a.INSURED_OBJECT, a.PAYTYPE, a.KIND_SEL, ");
		sql.append(" a.CURR_CD, a.UPTYPE, a.UPQTY_SEL, a.INSUREDAMT, a.BENEFICIARY_YN, b.COM_ID, b.PRD_NAME, b.PRD_ID, b.COVERCACULUNITDESC ");
		sql.append(" , b.TITLE_Y, b.TITLE_A, b.TITLE_O, b.TITLE_K");
		sql.append(" from TBINS_OUTBUY_MAST a,TBPRD_INSDATA_PROD_MAIN b ");
		sql.append(" where a.PRD_KEYNO = b.KEY_NO ");
		sql.append(" and a. INSSEQ = :insseq ");
		qc.setObject("insseq", insSeq);
		qc.setQueryString(sql.toString());
		
		queryList = dam.exeQuery(qc);
		List<Map<String, Object>> IS_MAIN_YList = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> IS_MAIN_NList = new ArrayList<Map<String,Object>>();
		for(Map<String, Object> map:queryList){
			if("Y".equals(String.valueOf(map.get("IS_MAIN")))){
				IS_MAIN_YList.add(map);
			}else if("N".equals(String.valueOf(map.get("IS_MAIN")))){
				IS_MAIN_NList.add(map);
			}
		}
		datas[0] = IS_MAIN_YList; // 主約
		datas[1] = IS_MAIN_NList; // 附約
		return datas;
	}

	/**
	 * 共用取得產品相關資訊
	 * @param keyNo
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private List<Map<String, Object>> getProdInfo(String keyNo) throws DAOException, JBranchException {
		List<Map<String , Object>> prodList = new ArrayList<Map<String,Object>>();
		
		dam = getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT PRD_ID, PRD_NAME, "); 
		sql.append(" IS_WL, WL_TERM, ");  
		sql.append(" COVERCACULUNITDESC, "); 
		sql.append(" LIST_E as ITEM_E, LIST_E, '幣別' as TITLE_E, ");
		sql.append(" ITEM_U, LIST_U, '保額' as TITLE_U, "); 
		sql.append(" ITEM_P, LIST_P, '保額' as TITLE_P, ");
		sql.append(" ITEM_Y, LIST_Y, TITLE_Y, ");  
		sql.append(" ITEM_A, LIST_A, TITLE_A, ");  
		sql.append(" LIST_O, TITLE_O, LIST_O as ITEM_O, ");  
		sql.append(" ITEM_K, LIST_K, TITLE_K "); 
		sql.append(" FROM TBPRD_INSDATA_PROD_MAIN WHERE KEY_NO = :keyNo "); 
		qc.setObject("keyNo", keyNo);
		qc.setQueryString(sql.toString());
		prodList = dam.exeQuery(qc);
		return prodList;
	}

	/**
	 * 共用產生 保單自訂序號
	 * @param INSURED_ID
	 * @param POLICY_NBR
	 * @param COM_ID
	 * @return
	 */
	private String get_INSSEQ(String INSURED_ID,String POLICY_NBR,String COM_ID){
		String INSSEQ;
		INSSEQ = INSURED_ID+"_"+POLICY_NBR+"_"+COM_ID;
		return INSSEQ;
	}
	
	/**
	 * 共用取得 行外保單序號
	 * @return
	 * @throws APException
	 */
	private String get_KEYNO() throws APException{
		List<Map<String, Object>> KEYNOList = new ArrayList<Map<String,Object>>();
		try {
			String KEYNO;
			dam = getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();
			sb.append(" select TBINS_OUTBUY_MAST_SEQ.NEXTVAL from dual ");
			qc.setQueryString(sb.toString());
			KEYNOList = dam.exeQuery(qc);
			KEYNO = KEYNOList.get(0).get("NEXTVAL").toString();
			return KEYNO;
		} catch (Exception e) {
			throw new APException("行外保單序號錯誤"+e.getMessage());
		}
	}
	
	/**
	 * 統一透過 INS810 取得年齡
	 * @param birthday 生日
	 * @return
	 * @throws JBranchException
	 */
	private int getAge(Date birthday) throws JBranchException {
		INS810 ins810 = (INS810) PlatformContext.getBean("ins810");
		INS810InputVO ins810inputVO = new INS810InputVO();
		INS810OutputVO ins810outputVO = new INS810OutputVO();
		ins810inputVO.setBirthday(birthday);
		ins810outputVO = ins810.getAge(ins810inputVO);
		return ins810outputVO.getAge();
	}
	
	//======================== 用不到但不能拔掉的功能 ========================
	/**
	 * WEB 調用 行外保單試算 
	 * funcType = 1
	 * @param body
	 * @param header
	 * @throws JBranchException 
	 * @throws ParseException 
	 */
	public void calculationPolicy(Object body, IPrimitiveMap header) throws JBranchException, ParseException{
		INS111InputVO inputVO111 = (INS111InputVO) body;
		sendRtnObject(getInsPrem(inputVO111).getPremium());
	}
	
	/**
	 * ws 調用 取得保費試算後的結果
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws ParseException 
	 */
	@SuppressWarnings("rawtypes")
	public void trialPremium(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		Map<String, Object> premiumMap = new HashMap<String, Object>();
		System.out.println(new Gson().toJson(body));
		premiumMap.put("premium", getInsPrem(body).getPremium());
		System.out.println(new Gson().toJson(premiumMap));
		this.sendRtnObject(premiumMap);
	}
	
	/**
	 * 統一試算入口，管你網頁還是行動都可以來
	 * @param inputObj 傳入的物件
	 * @return 試算後的整個 OutputVO
	 * @throws JBranchException
	 * @throws ParseException 
	 */
	private GetInsPremOutputVO getInsPrem(Object inputObj) throws JBranchException, ParseException {
		GetInsPremInputVO inputVO = new GetInsPremInputVO();
		List<Map<String, Object>> lstInsData = new ArrayList<Map<String, Object>>();
		prepareLstInsData(inputObj, lstInsData);
		
		GenericMap inputMap = null;
		List<Map<String, Object>> itemList = null;
		Map<String, Object>[] itemMapArray = null;
		if(inputObj instanceof INS111InputVO) {
		} else {
			inputMap = new GenericMap((Map) inputObj);
			itemList = inputMap.get("item");
			itemMapArray = (Map<String, Object>[]) new Map[2];
			itemMapArray[0] = CollectionSearchUtils.findMapInColleciton(itemList, "itemCode", "paymentyearSel");
			itemMapArray[1] = CollectionSearchUtils.findMapInColleciton(itemList, "itemCode", "coveryearSel");
		}
		
		// 繳費年期
		String paymentYear = (inputObj instanceof INS111InputVO) ? ((INS111InputVO)inputObj).getPAYMENTYEAR_SEL() : 
			ObjectUtils.toString(((Map)inputObj).get("paymentyearSel"));
		
		// 保障年期
		String coverYearSel = (inputObj instanceof INS111InputVO) ? ((INS111InputVO)inputObj).getCOVERYEAR_SEL() : 
			ObjectUtils.toString(((Map)inputObj).get("coveryearSel"));
		
		inputVO.setFuncType("1");
		inputVO.setMainPayYear(paymentYear); 
		inputVO.setMainCovYear(coverYearSel); 
		inputVO.setLstInsData(lstInsData);
		return getInsjlb().getInsPrem(inputVO);
	}
	
	/**
	 * 處理 JLB getInsPrem 要的來源資料
	 * 根據 傳入型態會自己決定 Map 可以 InputVO 也可
	 * @param inputVO111
	 * @param lstInsData
	 * @throws ParseException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void prepareLstInsData (Object input, List<Map<String, Object>> lstInsData) throws ParseException{
		int isVoObject = input instanceof INS111InputVO ? 0 :1; // 0 >> InputVO, 1 >> Map
		INS111InputVO inputVO111 = null;
		GenericMap inputMap = null;
		List<Map<String, Object>> itemList = null;
		Map<String, Object>[] itemMapArray = null;
		if(isVoObject == 0) {
			inputVO111 = (INS111InputVO) input;
		} else {
			inputMap = new GenericMap((Map) input);
			itemList = inputMap.get("item");
			itemMapArray = (Map<String, Object>[]) new Map[8];
			itemMapArray[0] = CollectionSearchUtils.findMapInColleciton(itemList, "itemCode", "currCd");
			itemMapArray[1] = CollectionSearchUtils.findMapInColleciton(itemList, "itemCode", "paytype");
			itemMapArray[2] = CollectionSearchUtils.findMapInColleciton(itemList, "itemCode", "paymentyearSel");
			itemMapArray[3] = CollectionSearchUtils.findMapInColleciton(itemList, "itemCode", "coveryearSel");
			itemMapArray[4] = CollectionSearchUtils.findMapInColleciton(itemList, "itemCode", "kindSel");
			itemMapArray[5] = CollectionSearchUtils.findMapInColleciton(itemList, "itemCode", "insuredObject");
			itemMapArray[6] = CollectionSearchUtils.findMapInColleciton(itemList, "itemCode", "uptype");
			itemMapArray[7] = CollectionSearchUtils.findMapInColleciton(itemList, "itemCode", "assureAmt");
		}
		
		Map<String, Object> lstInsDataMap = new HashMap<String, Object>();
		lstInsDataMap.put("CURR_CD", isVoObject == 0 ? inputVO111.getCURR_CD() : itemMapArray[0] != null ? itemMapArray[0].get("optionCode") : ""); // 必要
		lstInsDataMap.put("PAYTYPE", isVoObject == 0 ? inputVO111.getPAY_TYPE() : itemMapArray[1] != null ? itemMapArray[1].get("optionCode") : ""); // 必要
		lstInsDataMap.put("INSURED_AGE", isVoObject == 0 ? inputVO111.getINSURED_AGE() : null);
		lstInsDataMap.put("INSURED_GENDER", isVoObject == 0 ? inputVO111.getGENDER() : null);
		lstInsDataMap.put("INSUREDAMT", isVoObject == 0 ? inputVO111.getINSUREDAMT() : itemMapArray[6] == null || ObjectUtils.toString(itemMapArray[6].get("optionCode")).isEmpty() ? itemMapArray[7] != null ? itemMapArray[7].get("optionCode") : "" : null);
		lstInsDataMap.put("PAYMENTYEAR_SEL", isVoObject == 0 ? inputVO111.getPAYMENTYEAR_SEL() : itemMapArray[2] != null ? itemMapArray[2].get("optionCode") : "");
		lstInsDataMap.put("COVERYEAR_SEL", isVoObject == 0 ? inputVO111.getCOVERYEAR_SEL() : itemMapArray[3] != null ? itemMapArray[3].get("optionCode") : "");
		lstInsDataMap.put("KIND_SEL", isVoObject == 0 ? inputVO111.getKIND_SEL() : itemMapArray[4] != null ? itemMapArray[4].get("optionCode") : "");
		lstInsDataMap.put("INSURED_OBJECT", isVoObject == 0 ? inputVO111.getINSURED_OBJECT() : itemMapArray[5] != null ? itemMapArray[5].get("optionCode") : "");
		lstInsDataMap.put("PRD_KEYNO", isVoObject == 0 ? inputVO111.getPRD_KEY_NO() : inputMap.get("prdKeyno")); // 到產品 PRD_KEYNO 就好~ 剩下資訊源會貼心地幫你查 // 必要
		lstInsDataMap.put("COM_ID", isVoObject == 0 ? inputVO111.getCOM_ID() : inputMap.get("comId"));
		
		// 日期格式額外處理
		if(isVoObject == 0) {
			lstInsDataMap.put("EFFECTED_DATE", inputVO111.getEFFECTED_DATE()); // 必要
			lstInsDataMap.put("INSURED_BIRTHDAY", inputVO111.getINSURED_BIRTHDAY()); // 必要
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String effectedDateStr = inputMap.get("effectedDate");
			String birthDaayStr = inputMap.get("insuredBirthday");
			
			lstInsDataMap.put("EFFECTED_DATE", sdf.parse(effectedDateStr)); // 必要
			lstInsDataMap.put("INSURED_BIRTHDAY", sdf.parse(birthDaayStr)); // 必要
		}
		
		// 補塞
		// 單位
		lstInsDataMap.put("UPTYPE", isVoObject == 0 ? inputVO111.getUPTYPE() : itemMapArray[6] != null ? itemMapArray[6].get("optionCode") : "");
		lstInsDataMap.put("UPQTY_SEL", isVoObject == 0 ? inputVO111.getUPQTY_SEL() : itemMapArray[7] != null ? itemMapArray[7].get("optionCode") : "");
		lstInsData.add(lstInsDataMap);
	}
	
}