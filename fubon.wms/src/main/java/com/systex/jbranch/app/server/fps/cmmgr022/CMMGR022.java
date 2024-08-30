package com.systex.jbranch.app.server.fps.cmmgr022;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.Manager;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * 電文查詢功能
 */
@Component("cmmgr022")
@Scope("request")
public class CMMGR022 extends FubonWmsBizLogic {
    private CMMGR022InputVO inputVO;
    @Autowired
    private CMMGR022OutputVO outputVO;

    /**
     * 配置 Input
     **/
    private void setInputVO(Object body) {
        inputVO = (CMMGR022InputVO) body;
    }

    /**
     * 查詢電文資訊（不包含上下電文內容）
     *
     * @param body
     * @param header
     */
    public void query(Object body, IPrimitiveMap header) throws JBranchException {
        setInputVO(body);
        outputVO.setTxResult(configureQryTxInfo().query());
        sendRtnObject(outputVO);
    }

    /**
     * 配置查詢電文基本資訊 Sql
     **/
    private Manager configureQryTxInfo() throws JBranchException {
        Manager manager = Manager.manage(this.getDataAccessManager())
                .append("select * from ( ");

        for (int day = 0; day < 8; day++) {
            manager.append("select HSTANO, HTXTID, CREATOR, CREATETIME, ENDTIME ")
                    .append("from TBSYS_ESB_RECORD" + (day != 0 ? "_W" + day : "") + " ")
                    .append("where 1=1 ")
                    .condition(isNotBlank(inputVO.getId()), "and HTXTID = :id ", "id", inputVO.getId())
                    .condition(isNotBlank(inputVO.getEmpId()), "and CREATOR = :empId ", "empId", inputVO.getEmpId())
                    .condition(null != inputVO.getStart(), "and CREATETIME >= :start ", "start", inputVO.getStart())
                    .condition(null != inputVO.getEnd(), "and ENDTIME <= :end ", "end", inputVO.getEnd())
                    .condition(isNotBlank(inputVO.getNumber()), "and HSTANO = :number ", "number", inputVO.getNumber())
                    // union 周一到周日的電文資料
                    .append(day != 7 ? "union " : "");
        }
        manager.append(") order by HSTANO desc ");
        return manager;
    }

    /**
     * 檢索該編號的上下文電文內容
     *
     * @param body
     * @param header
     */
    public void qryContent(Object body, IPrimitiveMap header) throws Exception {
        setInputVO(body);
        String condition = " where HSTANO = :number and HTXTID = :id and CREATOR = :empId ";
        String tempSql = "select %s TABLE_NAME from %s " + condition;

        /** 因為 CLOB 無法使用 union，故先找到該筆電文位於哪個表格，再去該表格撈取上下文資料 **/
        Map<String, String> finder = (Map) exeQueryForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
                .append(String.format(tempSql, "'TBSYS_ESB_RECORD'", "TBSYS_ESB_RECORD")).append(" union ")
                .append(String.format(tempSql, "'TBSYS_ESB_RECORD_W1'", "TBSYS_ESB_RECORD_W1")).append(" union ")
                .append(String.format(tempSql, "'TBSYS_ESB_RECORD_W2'", "TBSYS_ESB_RECORD_W2")).append(" union ")
                .append(String.format(tempSql, "'TBSYS_ESB_RECORD_W3'", "TBSYS_ESB_RECORD_W3")).append(" union ")
                .append(String.format(tempSql, "'TBSYS_ESB_RECORD_W4'", "TBSYS_ESB_RECORD_W4")).append(" union ")
                .append(String.format(tempSql, "'TBSYS_ESB_RECORD_W5'", "TBSYS_ESB_RECORD_W5")).append(" union ")
                .append(String.format(tempSql, "'TBSYS_ESB_RECORD_W6'", "TBSYS_ESB_RECORD_W6")).append(" union ")
                .append(String.format(tempSql, "'TBSYS_ESB_RECORD_W7'", "TBSYS_ESB_RECORD_W7"))
                .toString())
                .setObject("number", inputVO.getNumber())
                .setObject("id", inputVO.getId())
                .setObject("empId", inputVO.getEmpId())).get(0);

        Map result = (Map) exeQueryForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
                .append("select ONMSG, OFFMSG from " + finder.get("TABLE_NAME") + condition)
                .toString())
                .setObject("number", inputVO.getNumber())
                .setObject("id", inputVO.getId())
                .setObject("empId", inputVO.getEmpId())).get(0);

        outputVO.setOnMsg(clobToString((Clob) result.get("ONMSG")));
        outputVO.setOffMsg(clobToString((Clob) result.get("OFFMSG")));
        this.sendRtnObject(outputVO);
    }

    /**
     * 將 Clob 變為 String
     **/
    private String clobToString(Clob msg) throws SQLException, IOException {
        StringBuffer sb = new StringBuffer();
        try (BufferedReader sqlReader = new BufferedReader(msg.getCharacterStream())) {
            String line;
            while ((line = sqlReader.readLine()) != null)
                sb.append(line + "\n");
        }
        return sb.toString();
    }
}
