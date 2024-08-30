package com.systex.jbranch.ws.external.service.domain.insurance_status;

import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.systex.jbranch.ws.external.service.domain.insurance_status.InsuranceStatusConfig.*;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.stripStart;

@RestController
@RequestMapping("/insuranceStatus")
public class InsuranceStatusController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/query"
    )
    public ReturnVO query(@RequestBody InsuranceStatusInputVO inputVO) {
        logger.info("./insuranceStatus/query - " + inputVO.toString());
        ReturnVO returnVO = new ReturnVO();
        ProcessResult result = new ProcessResult();
        try {
            check(inputVO);
            InsuranceStatusService insuranceStatusService = PlatformContext.getBean(InsuranceStatusService.class);
            List data = insuranceStatusService.query(inputVO);

            if (data.isEmpty()) {
                result.setStatus(WARNING);
                result.setReturnCode(APP_000_003);
                result.setReturnMessage("查無資料");
            } else {
            	returnVO.setQueryResult(data);
                result.setStatus(SUCCESS);
                result.setReturnCode(APP_000_001);
                result.setReturnMessage("成功");
            }
        } catch (JBranchException e) {
            result.setStatus(FAIL);
            result.setReturnCode(APP_000_002);
            result.setReturnMessage(e.getMessage());
        }
        returnVO.setProcessResult(result);
        return returnVO;
    }

    private void check(InsuranceStatusInputVO inputVO) throws APException {
        required(inputVO);
        validDate(inputVO);
    }

    /**
     * 最小值：今日往前推三個月。
     * 最大值：今日。
     */
    private void validDate(InsuranceStatusInputVO inputVO) throws APException {
        Date base = getBaseDate();
        Date today = getToday();
        try {
            betweenBaseAndToday(base, today, inputVO.getQueryDateStart());
            betweenBaseAndToday(base, today, inputVO.getQueryDateEnd());

        } catch (ParseException e) {
            throw new APException("查詢日期不符合 yyy/MM/dd 格式，其中 yyy 為民國年！");
        }
    }

    private Date getToday() {
        Calendar now = Calendar.getInstance();
        changeRocYear(now);
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        return now.getTime();
    }

    private void betweenBaseAndToday(Date base, Date today, String inputDate) throws APException, ParseException {
        if (isBlank(inputDate)) return;
        // 民國年的日期比較邏輯
        Date date = sdf.parse(StringUtils.leftPad(inputDate, 8, "0"));
        if (date.before(base) || date.after(today)) {
            Calendar baseBound = Calendar.getInstance();
            // 查詢日期的最小值就是 base + 1 天
            baseBound.setTime(base);
            baseBound.set(Calendar.DAY_OF_MONTH, baseBound.get(Calendar.DAY_OF_MONTH) + 1);

            throw new APException("查詢日期必須介於 " + rocStr(baseBound.getTime()) + " ~ " + rocStr(today) + " 之間！");
        }
    }

    // 轉換為民國年 yyy/mm/dd 格式，如 110/10/15
    private String rocStr(Date date) {
        return stripStart(sdf.format(date), "0");
    }

    private void required(InsuranceStatusInputVO inputVO) throws APException {
        if (isBlank(inputVO.getDocNum()) &&
                isBlank(inputVO.getPolicyNum()) &&
                isBlank(inputVO.getSalesEmployeeId()) &&
                isBlank(inputVO.getHolderName()))
            throw new APException("docNum（文件編號）、" +
                    "policyNum（保單號碼）、" +
                    "salesEmployeeId（業務員員編）、" +
                    "holderName（被保險人姓名），" +
                    "擇一必填！");
    }

    private Date getBaseDate() {
        Calendar calendar = Calendar.getInstance();
        changeRocYear(calendar);
        int base = 3; // 三個月前
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 3);
        // 以 8/24 為例，三個月前為 5/24，將日期往前一天使得 5/24 符合條件
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * 變為民國年
     **/
    private void changeRocYear(Calendar calendar) {
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1911);
    }
}
