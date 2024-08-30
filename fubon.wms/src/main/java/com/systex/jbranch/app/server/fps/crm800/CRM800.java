package com.systex.jbranch.app.server.fps.crm800;

import com.systex.jbranch.app.server.fps.crm681.CRM681InputVO;
import com.systex.jbranch.app.server.fps.crm681.CRM681OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.cbs.service.SC120100Service;
import com.systex.jbranch.fubon.commons.esb.vo.sc120100.SC120100DetailOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sc120100.SC120100OutputVO;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Walalala
 * @date 2016/11/24
 * 
 */
@Component("crm800")
@Scope("request")
public class CRM800 extends FubonWmsBizLogic {
	@Autowired
	private SC120100Service sc120100Service;

    public void getFu(Object body, IPrimitiveMap header) throws Exception {
    	CRM681OutputVO return_VO = new CRM681OutputVO();
		CRM681InputVO inputVO = (CRM681InputVO) body;

        //發送電文
		List<SC120100OutputVO> vos = sc120100Service.searchInstallment(inputVO.getCust_id().trim());

		System.out.println("測試成功!");
        List<SC120100DetailOutputVO> results = new ArrayList<>();
        List<SC120100DetailOutputVO> results2 = new ArrayList<>();
        List<SC120100DetailOutputVO> results3 = new ArrayList<>();
        List<SC120100DetailOutputVO> results4 = new ArrayList<>();
        List<SC120100DetailOutputVO> results5 = new ArrayList<>();
        List<SC120100DetailOutputVO> results6 = new ArrayList<>();
        List<SC120100DetailOutputVO> results7 = new ArrayList<>();
        List<SC120100DetailOutputVO> results8 = new ArrayList<>();

        for(SC120100OutputVO sc120100OutputVO : vos) {
        	System.out.println(sc120100OutputVO);
            List<SC120100DetailOutputVO> details = sc120100OutputVO.getDetails();
            details = (CollectionUtils.isEmpty(details)) ? new ArrayList<SC120100DetailOutputVO>() : details;

            for (SC120100DetailOutputVO data : details) {
            	
            	//分期型房貸
            	if("61".equals(data.getTYPE())) {
            		results.add(data);
            	}
            	//循環性貸款
            	else if ( "69".equals(data.getTYPE())) {

//            	else if ("68".equals(data.getTYPE()) || "69".equals(data.getTYPE())) {
            		results2.add(data);
            	}
            	//信貸
            	else if ("62".equals(data.getTYPE())) {
            		results3.add(data);
            	}
            	//就學貸款
            	else if ("63".equals(data.getTYPE()) && "01".equals(data.getLOAN_TYP())) {
            		results4.add(data);
            	}
            	//留學貸款
            	else if ("63".equals(data.getTYPE()) && "02".equals(data.getLOAN_TYP())) {
            		results5.add(data);
            	}
            	//存單質借
            	else if ("70".equals(data.getTYPE())) {
            		results6.add(data);
            	}
            	//個人週轉金
            	else if("64".equals(data.getTYPE())){
        			results7.add(data);
        		}
        		//企業貸款
        		else if("65".equals(data.getTYPE())) {
        			results8.add(data);
        		}
            }
        }

      //分期型房貸
        return_VO.setResultList(results);
      //循環性貸款
        return_VO.setResultList2(results2);
      //信貸
        return_VO.setResultList3(results3);
      //就學貸款
        return_VO.setResultList4(results4);
      //留學貸款
        return_VO.setResultList5(results5);
      //存單質借
        return_VO.setResultList6(results6);
      //個人週轉金
        return_VO.setResultList7(results7);
      //企業貸款
        return_VO.setResultList8(results8);

		this.sendRtnObject(return_VO);
    }
}
