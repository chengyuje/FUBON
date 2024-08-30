package com.systex.jbranch.app.server.fps.sot818;

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
 * 特定金錢信託投資國內指數型股票基金產品特別約定事項
 **/
@Component("sot818")
@Scope("request")
public class SOT818 extends SotPdf {
    private final String TXN_CODE = "SOT818";
    private final String REPORT_ID = "R1";

    @Override
    public List<String> printReport() throws JBranchException {
        ReportGeneratorIF generator = new ReportFactory().getGenerator();
        ReportDataIF data = new ReportData();
        data.addParameter("CUST_ID", getInputVO().getCustId());

        List<String> url_list = new ArrayList();
//        data.addParameter("PageFoot1", "第一聯：受理單位留存聯（第一頁/共二頁）           (接次頁)");
//        data.addParameter("PageFoot2", "第一聯：受理單位留存聯（第二頁/共二頁）");
//        url_list.add(generator.generateReport(TXN_CODE, REPORT_ID, data).getLocation());

//        data.addParameter("PageFoot1", "第二聯：客戶收執聯（第一頁/共二頁）                   (接次頁)");
//        data.addParameter("PageFoot2", "第二聯：客戶收執聯（第二頁/共二頁）");
//        url_list.add(generator.generateReport(TXN_CODE, REPORT_ID, data).getLocation());
        
        data.addParameter("PageFoot1", "第一聯：受理單位留存聯 ");
        url_list.add(generator.generateReport(TXN_CODE, REPORT_ID, data).getLocation());
        data.addParameter("PageFoot1", "第二聯：客戶收執聯");
        url_list.add(generator.generateReport(TXN_CODE, REPORT_ID, data).getLocation());
        return url_list;
    }
}
