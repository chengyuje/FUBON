package com.systex.jbranch.fubon.commons.cbs.service;

import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.cbs.dao._067050_067000DAO;
import com.systex.jbranch.fubon.commons.cbs.dao._067050_067101DAO;
import com.systex.jbranch.fubon.commons.cbs.dao._067050_067102DAO;
import com.systex.jbranch.fubon.commons.cbs.dao._067050_067112DAO;
import com.systex.jbranch.fubon.commons.cbs.vo._067050_067115.CBS067000OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._067050_067115.CBS067101OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._067050_067115.CBS067102OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._067050_067115.CBS067112OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fp032151.FP032151OutputVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.trim;

/**
 * 此服務將代替原本的 FP032151 ESB 電文
 * （Note: 原本的 FP032151 ESB 電文改走 CBS 路線）
 */
@Service
public class FP032151Service {
    @Autowired
    private _067050_067000DAO _067050_067000dao;
    @Autowired
    private _067050_067101DAO _067050_067101dao;
    @Autowired
    private _067050_067102DAO _067050_067102dao;
    @Autowired
    private _067050_067112DAO _067050_067112dao;
    @Autowired
    private CBSService cbsService;

    /**
     * 查詢客戶 KYC 資料
     */
    public FP032151OutputVO search(String custId) throws Exception {
        String idType = cbsService.getCBSIDCode(custId);

        FP032151OutputVO esbVO = new FP032151OutputVO();
        if (!cbsService.checkJuristicPerson(custId)) // 自然人
            _067050_067101Transfer(esbVO, _067050_067101dao.search(custId, idType));
        else
            _067050_067102Transfer(esbVO, _067050_067102dao.search(custId, idType));

        _067050_067000Transfer(esbVO, _067050_067000dao.search(custId, idType));
        _067050_067112Transfer(esbVO, _067050_067112dao.search(custId, idType));

        return esbVO;
    }
    
    /**
     * 取得客戶年齡
     */
    public int getCustAge(String custId) throws Exception {
        String idType = cbsService.getCBSIDCode(custId);

        FP032151OutputVO esbVO = new FP032151OutputVO();
        if (!cbsService.checkJuristicPerson(custId)) // 自然人
            _067050_067101Transfer(esbVO, _067050_067101dao.search(custId, idType));
        else
            _067050_067102Transfer(esbVO, _067050_067102dao.search(custId, idType));
        
        esbVO.setAGE(cbsService.changeToAge(esbVO.getBDAY()));

        return Integer.valueOf(esbVO.getAGE());
    }

    public FP032151OutputVO transfer(String custId, List<CBSUtilOutputVO> data067050_067101_2, List<CBSUtilOutputVO> data067050_067000, List<CBSUtilOutputVO> data067050_067112) throws Exception {
        FP032151OutputVO esbVO = new FP032151OutputVO();
        if (!cbsService.checkJuristicPerson(custId)) // 自然人
            _067050_067101Transfer(esbVO, data067050_067101_2);
        else
            _067050_067102Transfer(esbVO, data067050_067101_2);

        _067050_067000Transfer(esbVO, data067050_067000);
        _067050_067112Transfer(esbVO, data067050_067112);

        return esbVO;
    }

    /**
     * 規格書轉換 _067050_067112 to FP032151
     */
    private void _067050_067112Transfer(FP032151OutputVO esbVO, List<CBSUtilOutputVO> data) {
        if (isEmpty(data)) return;

        CBS067112OutputVO cbsVO = data.get(0).getCbs067112OutputVO();
        esbVO.setRISK_DATE_01(cbsVO.getCRED_RANK_UPDT());
    }

    /**
     * 規格書轉換 _067050_067000 to FP032151
     */
    private void _067050_067000Transfer(FP032151OutputVO esbVO, List<CBSUtilOutputVO> data) {
        if (isEmpty(data)) return;

        CBS067000OutputVO cbsVO = data.get(0).getCbs067000OutputVO();
        esbVO.setBILLS_UPD_DATE(cbsVO.getLstmntdate());
        esbVO.setLST_TX_DATE1(cbsVO.getLstmntdate());
        esbVO.setLST_TX_DATE2(cbsVO.getLstmntdate());
        esbVO.setLST_TX_DATE3(cbsVO.getLstmntdate());
        esbVO.setLST_TX_DATE4(cbsVO.getLstmntdate());
        esbVO.setLST_TX_DATE5(cbsVO.getLstmntdate());
        esbVO.setLST_TX_DATE6(cbsVO.getLstmntdate());
        esbVO.setMTN_DATE_RAT(cbsVO.getLstmntdate());
        esbVO.setENG_NAME(trim(cbsVO.getEngname1()));
        esbVO.setORG_TYPE(trim(cbsVO.getBussType1()));
    }

    /**
     * 規格書轉換 _067050_067101 to FP032151
     * 法人 TODO
     */
    private void _067050_067102Transfer(FP032151OutputVO esbVO, List<CBSUtilOutputVO> data) {
    	CBS067102OutputVO cbsVO = data.get(0).getCbs067102OutputVO();
    	esbVO.setCHILD_NO("");
    	esbVO.setEDUCATION("");
    	esbVO.setCAREER("");
    	esbVO.setMARRAGE("");
    	esbVO.setRESP_ID(cbsVO.getDefaultString10());
    	esbVO.setRESP_NAME(cbsVO.getDefaultString11());
    	esbVO.setBDAY(cbsVO.getDate1());  
    	esbVO.setBDAY_D(cbsVO.getDate1());
    	esbVO.setRESP_BDAY(StringUtils.isNotEmpty(cbsVO.getDate2()) ? cbsService.changeDateView(cbsVO.getDate2(),"2")  : "");
    }

    /**
     * 規格書轉換 _067050_067101 to FP032151
     * 20200406_CBS_偲偲_RE CBS測試_平板功能會打到FC032151電文  >> BDAY_D也存個生日
     */
    public void _067050_067101Transfer(FP032151OutputVO esbVO, List<CBSUtilOutputVO> data) {
        if (isEmpty(data)) return;

        CBS067101OutputVO cbsVO = data.get(0).getCbs067101OutputVO();
        esbVO.setCUST_NAME(cbsVO.getDefaultString1());
        esbVO.setBDAY(cbsVO.getDate1());  
        esbVO.setBDAY_D(cbsVO.getDate1());

        String gender = cbsVO.getDefaultString2();
        esbVO.setSEX("M".equals(gender) ? "男" :
                "F".equals(gender) ? "女" : "");
        esbVO.setTITLE(trim(cbsVO.getDefaultString7()));
        esbVO.setCOMPANY(trim(cbsVO.getDefaultString5()));
        esbVO.setCHILD_NO(dealChildNo(trim(cbsVO.getChildren())));
        esbVO.setEDUCATION(dealEducation(trim(cbsVO.getDefaultString13())));
        esbVO.setCAREER(dealCareer(trim(cbsVO.getOccupationCode())));
        esbVO.setMARRAGE(dealMarrage(trim(cbsVO.getDefaultString3())));
    }
    
    private String dealEducation(String education) {
    	//主機學歷若收電文【7不識字】或【8其他】，問卷帶出時是不是顯示未填(空值)，而非【年滿20歲不識字】
    	//20200519_CBS_麗文_KYC_教育程度問題 >> 改成給"7"(其他)
    	
    	//處理字串數
    	if(isNotBlank(education)){
    		education =  String.valueOf(Integer.parseInt(education));   		
    		if(education.matches("7|8")){
    			return "7";
    		} else {
    			return education;
    		}
    		
    	} else {
    		return "";
    	}

		
	}

	private String dealChildNo(String childNo){
    	//根據電文給的值跟KYC.CHILD_NO做轉換
		//#0425 彥德 資轉問題所以子女人數欄位可能給9 要回""
    	if(isNotBlank(childNo) && !"9".equals(childNo)){
    		return  String.valueOf(Integer.parseInt(childNo)).equals("5") ? "0" : String.valueOf(Integer.parseInt(childNo));
    	} else {
    		return "";
    	}
    }
	
	private String dealCareer(String career){
		//#0425 彥德 資轉問題所以職業別欄位可能給99 要回""
		if(isNotBlank(career) && !"0099".equals(career)){
			return String.valueOf(Integer.parseInt(career));
		} else {
			return "";
		}
	}
	private String dealMarrage(String marrage){
		if("S".equals(marrage)) return "1";
		if("M".equals(marrage)) return "2";
		return marrage;
	}
}
