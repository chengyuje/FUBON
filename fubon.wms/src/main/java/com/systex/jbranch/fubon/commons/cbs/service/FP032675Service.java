package com.systex.jbranch.fubon.commons.cbs.service;

import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.cbs.dao.*;
import com.systex.jbranch.fubon.commons.cbs.vo._060425_060433.CBS060433OutputDetailsVO;
import com.systex.jbranch.fubon.commons.cbs.vo._060425_060433.CBS060433OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._067050_067115.CBS067000OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._067050_067115.CBS067101OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._067050_067115.CBS067115OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._067164_067165.CBS067165OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fp032675.FP032675OutputVO;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static java.lang.Integer.parseInt;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.*;

/**
 * 此服務將代替原本的 EB202674 ESB 電文
 * （Note: 原本的 EB202674 ESB 電文改走 CBS 路線）
 */
@Service
public class FP032675Service {
    @Autowired
    private _067050_067000DAO _067050_067000dao;
    @Autowired
    private _067164_067165DAO _067164_067165dao;
    @Autowired
    private _067050_067115DAO _067050_067115dao;
    @Autowired
    private _067050_067101DAO _067050_067101dao;
    @Autowired
    private _067050_067102DAO _067050_067102dao;
    @Autowired
    private _060425_060433DAO _060425_060433dao;
    @Autowired
    private CBSService cbsService;

    /**
     * 查詢客戶基本資訊（地址、Mail...etc）
     */
    public FP032675OutputVO searchBasicInfo(String custId) throws Exception {
        FP032675OutputVO esbVO = new FP032675OutputVO();

        String type = cbsService.getCBSIDCode(custId);
        _067050_067000BasicInfoTransfer(esbVO, _067050_067000dao.search(custId, type));
        if(cbsService.isNaturalPerson(type))
        _067050_067101BasicInfoTransfer(esbVO, _067050_067101dao.search(custId, type));
        return esbVO;
    }

    /**
     * 轉換客戶基本資訊（地址、Mail...etc）
     */
    public FP032675OutputVO transferBasicInfo(String custId, List<CBSUtilOutputVO> data067050_067101_2, List<CBSUtilOutputVO> data067050_067000) throws Exception {
        FP032675OutputVO esbVO = new FP032675OutputVO();

        String type = cbsService.getCBSIDCode(custId);
        _067050_067000BasicInfoTransfer(esbVO, data067050_067000);
        if(cbsService.isNaturalPerson(type))
            _067050_067101BasicInfoTransfer(esbVO, data067050_067101_2);
        return esbVO;
    }

    /**
     * 規格書轉換-客戶基本資訊 _067050_067000 to FP032675
     */
    private void _067050_067000BasicInfoTransfer(FP032675OutputVO esbVO, List<CBSUtilOutputVO> data) {
        if (isEmpty(data)) return;

        CBS067000OutputVO cbsVO = data.get(0).getCbs067000OutputVO();
        // 戶籍地址
        esbVO.setZIP_COD1(trim(cbsVO.getReg_pcode1()));
        esbVO.setDATA1(trim(cbsVO.getReg_addr1()) + trim(cbsVO.getReg_addr2()));

        // 現居地址
        esbVO.setZIP_COD2(trim(cbsVO.getDefaultString9()));
        esbVO.setDATA2(trim(cbsVO.getDefaultString7()) + trim(cbsVO.getDefaultString8()));

        // 通訊地址
        esbVO.setZIP_COD3(trim(cbsVO.getPostCodePostal1()));
        esbVO.setDATA3(trim(cbsVO.getDefaultString5()) + trim(cbsVO.getDefaultString6()));
        // 信箱
        esbVO.setE_MAIL(trim(cbsVO.getEmail_addr1()));
    }

    /**
     * 規格書轉換 BasicInfo _067050_067101 to FP032675
     */
    private void _067050_067101BasicInfoTransfer(FP032675OutputVO esbVO, List<CBSUtilOutputVO> data) {
        if (isEmpty(data)) return;

        CBS067101OutputVO cbsVO = data.get(0).getCbs067101OutputVO();
        // 現職公司地址
        esbVO.setZIP_COD4(trim(cbsVO.getDefaultString10()));
        esbVO.setDATA4(trim(cbsVO.getDefaultString6()) + trim(cbsVO.getDefaultString9()) );
    }

    /**
     * 查詢客戶相關註記
     */
    public FP032675OutputVO searchMarkInfo(String custId,
                                           List<CBSUtilOutputVO> data067050_067101_2,
                                           List<CBSUtilOutputVO> data067050_067000,
                                           List<CBSUtilOutputVO> data067164_067165,
                                           List<CBSUtilOutputVO> data067050_067115,
                                           List<CBSUtilOutputVO> data060425_060433) throws Exception {
        FP032675OutputVO esbVO = new FP032675OutputVO();
        String idType = cbsService.getCBSIDCode(custId);
        SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
        try{
            if (data067164_067165 == null)
        	_067164_067165Transfer(esbVO, _067164_067165dao.search(custId, idType));
            else
                _067164_067165Transfer(esbVO, data067164_067165);
        } catch(Exception e){
        	if(!e.getMessage().contains("ErrorCode: 2214")){
        		throw new Exception(e.getMessage());
        	}

        }


        try{
            if (data067050_067000 == null)
                _067050_067000MarkInfoTransfer(esbVO, _067050_067000dao.search(custId, idType));
            else
                _067050_067000MarkInfoTransfer(esbVO, data067050_067000);
        } catch(Exception e){
        	if(!e.getMessage().contains("ErrorCode: 2214")){
        		throw new Exception(e.getMessage());
        	}

        }

        try{
            if (data067050_067115 == null)
        	_067050_067115Transfer(esbVO, _067050_067115dao.search(custId, idType));
            else
                _067050_067115Transfer(esbVO, data067050_067115);
        } catch(Exception e){
        	if(!e.getMessage().contains("ErrorCode: 2214")){
        		throw new Exception(e.getMessage());
        	}

        }

        try{
        	if (cbsService.isNaturalPerson(idType)) // 自然人
        	    if (data067050_067101_2 == null)
                    _067050_067101MarkInfoTransfer(esbVO, _067050_067101dao.search(custId, idType));
        	    else
                    _067050_067101MarkInfoTransfer(esbVO, data067050_067101_2);
            else // 法人
                if (data067050_067101_2 == null)
                    _067050_067102Transfer(esbVO, _067050_067102dao.search(custId, idType));
                else
                    _067050_067102Transfer(esbVO, data067050_067101_2);
        } catch(Exception e){
        	if(!e.getMessage().contains("ErrorCode: 2214")){
        		throw new Exception(e.getMessage());
        	}
        }

        esbVO.setINVEST_TYP(isInvestType(esbVO,custId) ? "Y" : "N");

        try{
            if (data060425_060433 == null)
        	_060425_060433Transfer(esbVO, _060425_060433dao.search(custId, idType));
            else
                _060425_060433Transfer(esbVO, data060425_060433);

        } catch(Exception e){
        	if(!e.getMessage().contains("ErrorCode: 2214")){
        		throw new Exception(e.getMessage());
        	}
        }
        return esbVO;
    }
    
    
    /**
     * #1865_商品主檔篩選錄音提醒
     * 
     * 查詢客戶相關註記: 年齡 重大傷病證明 教育程度
     * 
     */
    public FP032675OutputVO searchMarkInfo(String custId,
                                           List<CBSUtilOutputVO> data067050_067101_2,
                                           List<CBSUtilOutputVO> data067050_067000,
                                           List<CBSUtilOutputVO> data067050_067115,
                                           List<CBSUtilOutputVO> data060425_060433) throws Exception {
		FP032675OutputVO esbVO = new FP032675OutputVO();
		String idType = cbsService.getCBSIDCode(custId);

		try {
			if (data067050_067000 == null)
				_067050_067000MarkInfoTransfer(esbVO, _067050_067000dao.search(custId, idType));
			else
				_067050_067000MarkInfoTransfer(esbVO, data067050_067000);
		} catch (Exception e) {
			if (!e.getMessage().contains("ErrorCode: 2214")) {
				throw new Exception(e.getMessage());
			}

		}

		try {
			if (data067050_067115 == null)
				_067050_067115Transfer(esbVO, _067050_067115dao.search(custId, idType));
			else
				_067050_067115Transfer(esbVO, data067050_067115);
		} catch (Exception e) {
			if (!e.getMessage().contains("ErrorCode: 2214")) {
				throw new Exception(e.getMessage());
			}

		}

		try {
			// 自然人
			if (cbsService.isNaturalPerson(idType)) {
				if (data067050_067101_2 == null)
					_067050_067101MarkInfoTransfer(esbVO, _067050_067101dao.search(custId, idType));
				else
					_067050_067101MarkInfoTransfer(esbVO, data067050_067101_2);
			} else {// 法人
				if (data067050_067101_2 == null)
					_067050_067102Transfer(esbVO, _067050_067102dao.search(custId, idType));
				else
					_067050_067102Transfer(esbVO, data067050_067101_2);
			}
		} catch (Exception e) {
			if (!e.getMessage().contains("ErrorCode: 2214")) {
				throw new Exception(e.getMessage());
			}
		}

		esbVO.setINVEST_TYP(isInvestType(esbVO, custId) ? "Y" : "N");
		
		try{
            if (data060425_060433 == null)
        	_060425_060433Transfer(esbVO, _060425_060433dao.search(custId, idType));
            else
                _060425_060433Transfer(esbVO, data060425_060433);

        } catch(Exception e){
        	if(!e.getMessage().contains("ErrorCode: 2214")){
        		throw new Exception(e.getMessage());
        	}
        }

        return esbVO;
    }

    /**
     * 規格書轉換 _060425_060433 to FP032675
     * @throws JBranchException
     * @throws ParseException
     * @throws DAOException
     */
    private void _060425_060433Transfer(FP032675OutputVO esbVO, List<CBSUtilOutputVO> data) throws DAOException, ParseException, JBranchException {
        if (isEmpty(data)) return;

        CBS060433OutputVO cbsVO = data.get(0).getCbs060433OutputVO();
        String DUE_END_DATE = substring(cbsVO.getMemo1(), 24, 32); // 專投到期日
        String DELETE_END_DATE = substring(cbsVO.getMemo1(), 16, 24); // 專投註銷日期


        if (!cbsVO.getIsuse()) { // 查無資料
            esbVO.setTX_FLG("N");
            esbVO.setINVEST_EXP("N");
        } else { // 有資料
            // 先處理專投
            for (CBS060433OutputDetailsVO detail : cbsVO.getDetails()) {
                if (isTX(detail,DUE_END_DATE,DELETE_END_DATE)) {
                    esbVO.setTX_FLG("Y"); // 專投註記
                    break;
                } else {
                    esbVO.setTX_FLG("N");
                }
            }
            esbVO.setDUE_END_DATE(DUE_END_DATE); // 專投到期日

            SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
            String signDate = cbsService.changeDate(cbsVO.getDetails().get(0).getSetDate(),2);
            String sysDate = cbsService.getCBSTestDate().substring(0, 8);
            /*
             * 申請日距今是否滿兩週
             * #1399 新增專業投資人判斷「經業務所屬總行督導主管核准免除兩週限制」
             */
            if("Y".equals(cbsVO.getMemo1().substring(186, 187))) {
            	 esbVO.setINVEST_DUE("Y");
            } else if (cbsService.calDate(ft.parse(sysDate), ft.parse(signDate))  >= 14) {
            	 esbVO.setINVEST_DUE("Y");
            } else {
            	esbVO.setINVEST_DUE("N");
            }

            if (isNotBlank(cbsVO.getMemo1())) {
                //是否具備結構複雜商品之投資經驗
                if ("Y".equals(esbVO.getINVEST_TYP()) &&
                        isNotBlank(substring(cbsVO.getMemo1(), 49, 58))) {
                    esbVO.setINVEST_EXP("Y");
                } else if (isNotBlank(substring(cbsVO.getMemo1(), 24, 33))) {
                    esbVO.setINVEST_EXP("Y");
                } else {
                    esbVO.setINVEST_EXP("N");
                }
                esbVO.setLegalTX(cbsVO.getMemo1().substring(32,34));
                esbVO.setNatureTX(cbsVO.getMemo1().substring(34,36));
                esbVO.setFinancialStatment(cbsVO.getMemo1().substring(37,39));
                
            }
        }
    }

    /**
     * 是否為專投
     * 20200211 加上到期日以及註銷日期判斷
     * 20201118
     * 到期日 > =SYSDATE--->專投身分為Y
     * 註銷日 > SYSDATE--->專投身分為Y
     * 到期日=註銷日=SYSDATE----------------------->專投身分為N
     * @throws JBranchException 
     * @throws ParseException 
     * @throws DAOException 
     **/
	private boolean isTX(CBS060433OutputDetailsVO detail, String DUE_END_DATE, String DELETE_END_DATE)
			throws DAOException, ParseException, JBranchException {
		return  "98".equals(detail.getReasonCode()) 
				&& "J".equals(detail.getST())
				&& "0002".equals(detail.getIncidentType())
				&& Integer.parseInt(DUE_END_DATE) >= Integer.parseInt(cbsService.getCBSTestDate().substring(0, 8))
				&& (StringUtils.isBlank(DELETE_END_DATE) || Integer.parseInt(DELETE_END_DATE) > Integer
						.parseInt(cbsService.getCBSTestDate().substring(0, 8)));
	}

    /**
     * 特定客戶
     **/
    private boolean isInvestType(FP032675OutputVO esbVO, String custID) {
    	
    	//法人固定給flase
    	if(cbsService.isNaturalPerson(cbsService.getCBSIDCode(custID))){
    		 return "N".equals(esbVO.getAGE_UN70_FLAG()) ||
    	                "N".equals(esbVO.getHEALTH_FLAG()) ||
    	                "N".equals(esbVO.getEDU_OV_JR_FLAG());
    	} else {
    		return false;
    	}
    }
    

    /**
     * 規格書轉換 _067050_067102 to FP032675 TODO
     */
    private void _067050_067102Transfer(FP032675OutputVO esbVO, List<CBSUtilOutputVO> data) {
    	esbVO.setEDU_OV_JR_FLAG("Y");
    }

    /**
     * 規格書轉換 MarkInfo _067050_067101 to FP032675
     */
    private void _067050_067101MarkInfoTransfer(FP032675OutputVO esbVO, List<CBSUtilOutputVO> data) {
        if (isEmpty(data)) return;

        CBS067101OutputVO cbsVO = data.get(0).getCbs067101OutputVO();
        String str13 = trim(cbsVO.getDefaultString13());
       
        //20200303_CBS_彥德_修改教育程度註記邏輯
        // 6/7/8/空白 都為N
        if (isBlank(str13)) {
        	esbVO.setEDU_OV_JR_FLAG("N");
        	return;
        }
        esbVO.setEDU_OV_JR_FLAG(parseInt(str13) != 6 && parseInt(str13) != 7 && parseInt(str13) != 8   ? "Y" : "N");
    }

    /**
     * 規格書轉換 _067050_067115 to FP032675
     */
    private void _067050_067115Transfer(FP032675OutputVO esbVO, List<CBSUtilOutputVO> data) {
        if (isEmpty(data)) return;

        CBS067115OutputVO cbsVO = data.get(0).getCbs067115OutputVO();
        esbVO.setPROD_TYP(cbsVO.getAnntationFlag05());

        String flag12 = cbsVO.getAnntationFlag12();
        esbVO.setSICK_TYPE("N".equals(flag12) ? "1" : "Y".equals(flag12) ? "2" : "");
        esbVO.setHEALTH_FLAG("Y".equals(flag12) ? "N" : "Y");
        esbVO.setREJ_TYP(cbsVO.getAnntationFlag06());
        
        String degrade_date = substring(cbsVO.getReligiousRef1(), 80, 88);
        esbVO.setDEGRADE_DATE(degrade_date);
        
        try{
        	esbVO.setDegrade(cbsVO.getFinManInstrmt1().substring(10, 11));
        } catch (Exception e){
        	
        }
    }

    /**
     * 規格書轉換 _067164_067165 to FP032675
     */
    private void _067164_067165Transfer(FP032675OutputVO esbVO, List<CBSUtilOutputVO> data) {
        if (isEmpty(data)) return;

        CBS067165OutputVO cbsVO = data.get(0).getCbs067165OutputVO();
        esbVO.setINFO_FLG(cbsVO.getAgreementLevel().trim()); //20200218_CBS_俊達_客戶首頁信託推介同意註記跟共同行銷註記修正
        esbVO.setDM_FLG(cbsVO.getDM());
        esbVO.setEDM_FLG(cbsVO.getEDM());
        esbVO.setSMS_FLG(cbsVO.getSMS());
        esbVO.setTM_FLG(cbsVO.getTM());

        esbVO.setACC1_FLG(substring(cbsVO.getAuthHLDGInfo(), 0, 1));
        esbVO.setACC2_FLG(substring(cbsVO.getAuthHLDGInfo(), 1, 1));
        esbVO.setACC3_FLG(substring(cbsVO.getAuthHLDGInfo(), 2, 3));
        esbVO.setACC4_FLG(substring(cbsVO.getAuthHLDGInfo(), 3, 4));
        esbVO.setACC6_FLG(substring(cbsVO.getAuthHLDGInfo(), 4, 5));
        esbVO.setACC7_FLG(substring(cbsVO.getAuthHLDGInfo(), 5, 6));
        esbVO.setACC8_FLG(substring(cbsVO.getAuthHLDGInfo(), 6, 7));

        esbVO.setACC1_OTHER(substring(cbsVO.getBasicHLDGInfo(), 0, 1));
        esbVO.setACC2_OTHER(substring(cbsVO.getBasicHLDGInfo(), 1, 2));
        esbVO.setACC3_OTHER(substring(cbsVO.getBasicHLDGInfo(), 2, 3));
        esbVO.setACC4_OTHER(substring(cbsVO.getBasicHLDGInfo(), 3, 4));
        esbVO.setACC6_OTHER(substring(cbsVO.getBasicHLDGInfo(), 4, 5));
        esbVO.setACC7_OTHER(substring(cbsVO.getBasicHLDGInfo(), 5, 6));
        esbVO.setACC8_OTHER(substring(cbsVO.getBasicHLDGInfo(), 6, 7));
    }

    /**
     * 規格書轉換-客戶相關註記 _067050_067000 to FP032675
     */
    private void _067050_067000MarkInfoTransfer(FP032675OutputVO esbVO, List<CBSUtilOutputVO> data) throws ParseException {
        if (isEmpty(data)) return;

        CBS067000OutputVO cbsVO = data.get(0).getCbs067000OutputVO();
        configureByAge(esbVO, cbsVO);
        esbVO.setDEAD_TYP("002".equals(cbsVO.getDefaultString28()) ? "Y" : "N"); // 沒有Status1
    }
    
    /**
     * 根據年齡設置結果
     */
    private void configureByAge(FP032675OutputVO esbVO, CBS067000OutputVO cbsVO) throws ParseException {
        Calendar now = Calendar.getInstance(); // 系統日
        Calendar birth = Calendar.getInstance(); // 出生日
        birth.setTime(new SimpleDateFormat("ddMMyyyy").parse(cbsVO.getDate2()));

        int yearDiff = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
        int dayDiff = now.get(Calendar.DAY_OF_YEAR) - birth.get(Calendar.DAY_OF_YEAR);

        // 取得「是否年齡小於 65 歲」Flag //原70歲改為65歲
        esbVO.setAGE_UN70_FLAG((yearDiff > 65 || (yearDiff == 65 && dayDiff >= 0)) ? "N" : "Y");
        
    }
    
}
