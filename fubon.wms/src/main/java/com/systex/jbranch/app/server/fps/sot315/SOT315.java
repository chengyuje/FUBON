package com.systex.jbranch.app.server.fps.sot315;

import com.ibm.icu.math.BigDecimal;
import com.systex.jbranch.app.server.fps.sot701.ContractVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WMS-CR-20191009-01_金錢信託套表需求申請單
 * 
 * @author ocean
 * @date 2020/01/13
 * @spec 海外債申購資料輸入及適配(金錢信託)
 */
@Component("sot315")
@Scope("request")
public class SOT315 extends FubonWmsBizLogic {

	/*
	 * #0864: 金錢信託不受理55歲以上受益人申購SN  2022.02.22 SamTu
	 */
	public void getCustANDContractList (Object body, IPrimitiveMap header) throws JBranchException, Exception {
		
		SOT315InputVO inputVO = (SOT315InputVO) body;
		SOT315OutputVO outputVO = new SOT315OutputVO();
		
		SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
		
		List<ContractVO> contract = sot701.getContractList(inputVO.getCustID().toUpperCase());
		List<Map<String, Object>> contractList = new ArrayList<Map<String, Object>>();
		for (ContractVO vo : contract) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("LABEL", StringUtils.trim(vo.getCONTRACT_ID()));												// 契約編號
			map.put("DATA", StringUtils.trim(vo.getCONTRACT_ID()));													// 契約編號
			map.put("ACC", StringUtils.trim(vo.getACC()));															// 扣款帳號/入帳帳號
			map.put("CUR", StringUtils.trim(StringUtils.isNotEmpty(vo.getCUR1()) ? vo.getCUR1() : "") + 
						   StringUtils.trim(StringUtils.isNotEmpty(vo.getCUR2()) ? "," + vo.getCUR2() : "") +
						   StringUtils.trim(StringUtils.isNotEmpty(vo.getCUR3()) ? "," + vo.getCUR3() : "") +
						   StringUtils.trim(StringUtils.isNotEmpty(vo.getCUR4()) ? "," + vo.getCUR4() : "") +
						   StringUtils.trim(StringUtils.isNotEmpty(vo.getCUR5()) ? "," + vo.getCUR5() : ""));
			map.put("FLAG", new BigDecimal(vo.getTRUST_PEOP_NUM()).compareTo(BigDecimal.ONE) <= 0 ? "N" : "Y");		// 是否為多委託人契約
            map.put("GUARDIANSHIP_FLAG", vo.getGUARDIANSHIP_FLAG());
            map.put("CONTRACT_SPE_FLAG", vo.getCONTRACT_SPE_FLAG());
            map.put("CONTRACT_P_TYPE", vo.getCONTRACT_P_TYPE());
            map.put("CREDIT_FLAG", vo.getCREDIT_FLAG()); //受益人滿55歲(Y/N)
			contractList.add(map);
		}
		outputVO.setContractList(contractList);
		
		this.sendRtnObject(outputVO);
	}

}