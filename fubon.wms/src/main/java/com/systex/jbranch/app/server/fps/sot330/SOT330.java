package com.systex.jbranch.app.server.fps.sot330;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.esb.service.NJBRVC3Service;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvc3.NJBRVC3OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvc3.NJBRVC3OutputVO;
import com.systex.jbranch.fubon.jlb.DataFormat;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * SOT330 海外債成交結果查詢
 * 
 * @author SamTu
 * @date 2020.11.05
 */
@Component("sot330")
@Scope("request")
public class SOT330 extends FubonWmsBizLogic {
	@Autowired
	NJBRVC3Service njbrvc3Service;
	@Autowired
	CBSService cbsService;
	@Autowired 
	SOT701 sot701;

	public void query(Object body, IPrimitiveMap header) throws Exception {

		SOT330InputVO inputVO = (SOT330InputVO) body;
		SOT330OutputVO outputVO = new SOT330OutputVO();
        List<NJBRVC3OutputDetailsVO> list = new ArrayList<>();
        inputVO.setIsOBU(sot701.isObu(inputVO.getCustID()));
		List<ESBUtilOutputVO> vos = njbrvc3Service.search(inputVO);
		for(ESBUtilOutputVO esbVO : vos){
			NJBRVC3OutputVO njbrvc3outputVO = inputVO.getIsOBU() ? esbVO.getAjbrvc3OutputVO() : esbVO.getNjbrvc3OutputVO();
			for(NJBRVC3OutputDetailsVO detail : njbrvc3outputVO.getDetails()){
				if(StringUtils.isNotBlank(detail.getBondNo())){
					detail.setTxAmt(cbsService.amountFormat(detail.getTxAmt(),9));
					detail.setChanCharge(cbsService.amountFormat(detail.getChanCharge(),2));
					detail.setTxFee(cbsService.amountFormat(detail.getTxFee(),2));
					detail.setTxVal(String.valueOf(Integer.valueOf(detail.getTxVal())));
					detail.setAcAMT(cbsService.amountFormat(detail.getAcAMT(),2));
					detail.setTxDate(changeROCtoAC(detail.getTxDate()));
					if(StringUtils.equals("00000000", detail.getSellAmtDate()) ||StringUtils.isBlank(detail.getSellAmtDate())){
						detail.setSellAmtDate("");
					}else{
						detail.setSellAmtDate(changeROCtoAC(detail.getSellAmtDate()));
					}
					detail.setCustId(DataFormat.getCustIdMaskForHighRisk(detail.getCustId()));
					
					list.add(detail);	
					
				}
				
			}
		}
//        NJBRVC3OutputDetailsVO test = new NJBRVC3OutputDetailsVO();
//        test.setTxnType2("B");
//        test.setEntrustStatus("01");
//        list.add(test);
        outputVO.setResultList(list);

		this.sendRtnObject(outputVO);
	}
	
	/**
	 * 西元轉民國
	 */
	private String changeROCtoAC(String ROC){
		String dd = ROC.substring(6);
		String mm = ROC.substring(4,6);
		String yyyy = String.valueOf(Integer.parseInt(ROC.substring(0, 4)) + 1911);
        		
		return yyyy + "/" + mm + "/" +dd;
		
	}

}
