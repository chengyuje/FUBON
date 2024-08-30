package com.systex.jbranch.fubon.commons;

import static com.systex.jbranch.app.server.fps.kyccons.KYCCons.LEGAL_PERSON_FINANCE_BOSS;
import static com.systex.jbranch.app.server.fps.kyccons.KYCCons.LEGAL_PERSON_FINANCE_OP;
import static com.systex.jbranch.app.server.fps.kyccons.KYCCons.PERSONAL_FINANCE_BOSS;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.systex.jbranch.app.server.fps.kyc.chk.KYCCheckIdentityWeights;
import com.systex.jbranch.app.server.fps.kyc311.KYC311;
import com.systex.jbranch.app.server.fps.kyc311.KYC311InputVO;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;

@Service("checkBossAdmin")
@Scope("request")
public class CheckBossAdmin extends BizLogic {

	@Autowired
	@Qualifier("KYCCheckIdentityWeights")
	private KYCCheckIdentityWeights KYCCheck;

	public Boolean checkBossAdmin(String createrEmpID, String bossEmpID, String bossEmpPWD) throws Exception {

		// (1)發送LDAP驗證主管帳密
		KYC311InputVO inputVO_KYC311 = new KYC311InputVO();// 頁面帶進來的物件
		inputVO_KYC311.setEMP_ID(bossEmpID);
		inputVO_KYC311.setEMP_PASSWORD(bossEmpPWD);
		
		KYC311 kyc311 = (KYC311) PlatformContext.getBean("kyc311");	
		kyc311.checkBossAdmin(inputVO_KYC311);				

		// (2)檢核轄下關係
		List<String> supervisoList = new ArrayList<String>();
		
		if (KYCCheck.chkRole(createrEmpID, LEGAL_PERSON_FINANCE_OP) && !KYCCheck.chkRole(bossEmpID, LEGAL_PERSON_FINANCE_BOSS)) {
			supervisoList.add("企金");
			supervisoList.add("企金");
		} else {
			Integer cnt = KYCCheck.queryOtherRoleCnt(bossEmpID, PERSONAL_FINANCE_BOSS);
			if (cnt == null || cnt < 1) {
				supervisoList.add("個金");
				supervisoList.add("個金");
			}
		}

		if (CollectionUtils.isNotEmpty(supervisoList)) {
			throw new JBranchException("主管身份不符");
		}

		return true;
	}

}