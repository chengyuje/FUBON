package com.systex.jbranch.app.server.fps.sot815;

import com.systex.jbranch.app.server.fps.sot712.SotPdf;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 後收型基金約定書
 **/
@Component("sot815")
@Scope("request")
public class SOT815 extends SotPdf {
    private final String TXN_CODE = "SOT815";
    private final String REPORT_ID = "R1";

    @Override
    public List<String> printReport() throws JBranchException {
        ReportGeneratorIF generator = new ReportFactory().getGenerator();
        ReportDataIF data = new ReportData();
        data.addParameter("CUST_ID", getInputVO().getCustId());

        List<String> url_list = new ArrayList();
        data.addParameter("PageFoot", "第一聯：受理單位留存聯");
        /** Part：條碼判斷使用。
         *  第一聯 受理單位留存聯僅第一頁有條碼(身分證字號及表單條碼),
         *  第二聯客戶收執聯都不要有任何條碼
         */
        data.addParameter("Part", "1");
        url_list.add(generator.generateReport(TXN_CODE, REPORT_ID, data).getLocation());

        data.addParameter("PageFoot", "第二聯：客戶收執聯");
        data.addParameter("Part", "2");
        url_list.add(generator.generateReport(TXN_CODE, REPORT_ID, data).getLocation());
        return url_list;
    }
}
