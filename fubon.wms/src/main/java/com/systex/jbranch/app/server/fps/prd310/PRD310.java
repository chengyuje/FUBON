package com.systex.jbranch.app.server.fps.prd310;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.Manager;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * PRD310 保險商品訓練設定檔
 *
 * @author Eli
 * @date 20190903
 */
@Component("prd310")
@Scope("request")
public class PRD310 extends FubonWmsBizLogic {
    private PRD310InputVO inputVO;

    @Autowired
    private PRD310OutputVO outputVO;

    /**
     * inputVO setter
     **/
    private void setInputVO(Object body) {
        inputVO = (PRD310InputVO) body;
    }

    /**
     * 查詢課程資訊
     **/
    public void query(Object body, IPrimitiveMap header) throws JBranchException {
        setInputVO(body);
        Map params = new HashMap();
        outputVO.setResult(exeQueryForMap(configureQrySql(params), params));
        sendRtnObject(outputVO);
    }

    /**
     * 配置查詢 SQL 與 PARAMS
     **/
    private String configureQrySql(Map params) {
        StringBuilder sql = new StringBuilder();
        sql.append("select L.CLASS_ID, L.PRD_ID, I.INSPRD_NAME ")
                .append("from TBPRD_AO_TRAN_LIST L ")
                .append("left join ( ")
                .append("    select INSPRD_ID, INSPRD_NAME from TBPRD_INS_MAIN ")
                .append("    group by INSPRD_ID, INSPRD_NAME ")
                .append(") I on ( L.PRD_ID = I.INSPRD_ID ) ")
                .append("where 1=1 ");

        if (StringUtils.isNotBlank(inputVO.getClassId())) {
            sql.append("and L.CLASS_ID = :classId ");
            params.put("classId", inputVO.getClassId());
        }

        if (StringUtils.isNotBlank(inputVO.getProdId())) {
            sql.append("and L.PRD_ID = :prodId ");
            params.put("prodId", inputVO.getProdId());
        }
        return sql.toString();
    }

    /**
     * 新增課程
     **/
    public void add(Object body, IPrimitiveMap header) throws JBranchException {
        setInputVO(body);
        String errMsg = "";

        try {
            if (prdIsExists(inputVO.getProdId())) {
                exeUpdateForQcf(genDefaultQueryConditionIF()
                        .setQueryString("insert into TBPRD_AO_TRAN_LIST(CLASS_ID, PRD_ID, CREATOR, CREATE_DATE) values(:classId, :prodId, :creator, sysdate) ")
                        .setObject("classId", inputVO.getClassId())
                        .setObject("prodId", inputVO.getProdId())
                        .setObject("creator", SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID)));
            } else {
                errMsg = "商品" + inputVO.getProdId() + "尚未建檔";
            }
        } catch (JBranchException e) {
            errMsg = "資料已存在，無法新增該筆資料！";
        }
        sendRtnObject(errMsg);
    }

    /** 商品代號是否存在 TBPRD_INS_MAIN 中（對應欄位: INSPRD_ID）  **/
    private boolean prdIsExists(String prdId) throws JBranchException {
        List data = Manager.manage(this.getDataAccessManager())
                .append("select 1 from TBPRD_INS_MAIN ")
                .append("where INSPRD_ID = :prdId ")
                .put("prdId", prdId)
                .query();
        return !data.isEmpty();
    }

    /**
     * 刪除課程
     **/
    public void delete(Object body, IPrimitiveMap header) throws JBranchException {
        setInputVO(body);
        exeUpdateForQcf(genDefaultQueryConditionIF()
                .setQueryString("delete from TBPRD_AO_TRAN_LIST where CLASS_ID = :classId ")
                .setObject("classId", inputVO.getClassId()));
        sendRtnObject(null);
    }

    private String formatStr(Object column) {
        return "=\"" + ObjectUtils.defaultIfNull(column, "") + "\"";
    }

    /**
     * 匯出
     */
    public void export(Object body, IPrimitiveMap header) throws JBranchException {
        setInputVO(body);
        Map params = new HashMap();
        List<Map<String, Object>> list = exeQueryForMap(configureQrySql(params), params);

        List listCSV = new ArrayList();
        for (Map<String, Object> map : list) {
            String[] records = new String[3];
            int i = 0;

            records[i] = formatStr(map.get("CLASS_ID"));     // 課程代碼
            records[++i] = formatStr(map.get("PRD_ID"));     // 商品代碼
            records[++i] = formatStr(map.get("INSPRD_NAME"));// 商品名稱
            listCSV.add(records);
        }

        // header
        CSVUtil csv = new CSVUtil();
        csv.setHeader(new String[]{"課程代碼", "商品代碼", "商品名稱"});
        csv.addRecordList(listCSV);
        String url = csv.generateCSV();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String fileName = "保險商品訓練設定檔_" + sdf.format(new Date())+ ".csv";
        notifyClientToDownloadFile(url, fileName);
        this.sendRtnObject(null);
    }
}