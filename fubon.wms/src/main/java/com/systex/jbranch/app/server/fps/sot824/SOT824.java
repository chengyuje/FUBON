package com.systex.jbranch.app.server.fps.sot824;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO;
import com.systex.jbranch.app.server.fps.sot712.SotPdf;
import com.systex.jbranch.app.server.fps.sot714.WMSHACRDataVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;

/**
 * 高資產客戶投資產品集中度聲明書
 */
@Component("sot824")
@Scope("request")
public class SOT824 extends SotPdf {
	/**
	 * 高資產客戶投資產品集中度聲明書
	 */
	@Override
	public List<String> printReport() throws Exception {
		String url = null;
        String txnCode = "SOT824";
        String reportID = "R1";
        ReportIF report = null;
        List<String> url_list = new ArrayList<String>();

        ReportFactory factory = new ReportFactory();
        ReportDataIF data = new ReportData();
        ReportGeneratorIF gen = factory.getGenerator();  //產出pdf
        
        PRDFitInputVO inputVO = getInputVO();
        WMSHACRDataVO cRateVO = inputVO.getHmshacrDataVO();
        if (inputVO.getHmshacrDataVO() != null) {
        	if(inputVO.getPrdType().matches("3")) { //特金海外債/金市海外債
        		data.addParameter("PROD_TYPE1", "□	不保本境內外結構型商品");
        		data.addParameter("PROD_TYPE2", "■	(1)BB-(含)以下信用評等債券(含無信用評等債券)");
        		data.addParameter("PROD_TYPE3", "□	(2)未具證券投資信託基金性質之境外基金");
        		
        		data.addParameter("PROD_NAME1", "");
                data.addParameter("PROD_ID1", "");
        		data.addParameter("PROD_NAME2", inputVO.getPrdName());
                data.addParameter("PROD_ID2", inputVO.getPrdId());		
                data.addParameter("PROD_NAME3", "");
                data.addParameter("PROD_ID3", "");
        	} else if (inputVO.getPrdType().matches("4|5")) { //SI/SN/DCI(SOT620過來)
        		data.addParameter("PROD_TYPE1", "■	不保本境內外結構型商品");
        		data.addParameter("PROD_TYPE2", "□	(1)BB-(含)以下信用評等債券(含無信用評等債券)");
        		data.addParameter("PROD_TYPE3", "□	(2)未具證券投資信託基金性質之境外基金");
        		
        		data.addParameter("PROD_NAME1", inputVO.getPrdName());
                data.addParameter("PROD_ID1", inputVO.getPrdId());
        		data.addParameter("PROD_NAME2", "");
                data.addParameter("PROD_ID2", "");		
                data.addParameter("PROD_NAME3", "");
                data.addParameter("PROD_ID3", "");
        	} else if (inputVO.getPrdType().matches("1")) { //境外私募基金
        		data.addParameter("PROD_TYPE1", "□	不保本境內外結構型商品");
        		data.addParameter("PROD_TYPE2", "□	(1)BB-(含)以下信用評等債券(含無信用評等債券)");
        		data.addParameter("PROD_TYPE3", "■	(2)未具證券投資信託基金性質之境外基金");
        		
        		data.addParameter("PROD_NAME1", "");
                data.addParameter("PROD_ID1", "");
        		data.addParameter("PROD_NAME2", "");
                data.addParameter("PROD_ID2", "");		
                data.addParameter("PROD_NAME3", inputVO.getPrdName());
                data.addParameter("PROD_ID3", inputVO.getPrdId());
        	}
        	
            data.addParameter("PERCENT1", cRateVO.getPERCENTAGE_1() == null ? "0%" : cRateVO.getPERCENTAGE_1().toString()+"%");
            data.addParameter("S_PERC1", cRateVO.getBASE_PERC_1() == null ? "0%" : cRateVO.getBASE_PERC_1().toString()+"%");
            data.addParameter("PERCENT2", cRateVO.getPERCENTAGE_2() == null ? "0%" : cRateVO.getPERCENTAGE_2().toString()+"%");
            data.addParameter("S_PERC2", cRateVO.getBASE_PERC_2() == null ? "0%" : cRateVO.getBASE_PERC_2().toString()+"%");
            data.addParameter("PERCENT3", cRateVO.getPERCENTAGE_3() == null ? "0%" : cRateVO.getPERCENTAGE_3().toString()+"%");
            data.addParameter("S_PERC3", cRateVO.getBASE_PERC_3() == null ? "0%" : cRateVO.getBASE_PERC_3().toString()+"%");
            data.addParameter("PERCENT4", cRateVO.getPERCENTAGE_4() == null ? "0%" : cRateVO.getPERCENTAGE_4().toString()+"%");
            data.addParameter("S_PERC4", cRateVO.getBASE_PERC_4() == null ? "0%" : cRateVO.getBASE_PERC_4().toString()+"%");
            
            report = gen.generateReport(txnCode, reportID, data);
            url = report.getLocation();
            url_list.add(url);
        }
        
        return url_list;
	}
}