package com.systex.jbranch.app.server.fps.prd311;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.bth.job.context.AccessContext;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.Manager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * PRD311 保險商品訓練維護查詢
 *
 * @author Eli
 * @date 20190903
 */
@Component("prd311")
@Scope("request")
public class PRD311 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
    private PRD311InputVO inputVO;

    @Autowired
    private PRD311OutputVO outputVO;

    /**
     * inputVO setter
     **/
    private void setInputVO(Object body) {
        inputVO = (PRD311InputVO) body;
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
        sql.append("select A.CLASS_ID, A.PRD_ID, A.EMP_ID, A.EMP_NAME, A.STATUS, A.COMPLETE_DATE, I.INSPRD_NAME ")
                .append("from TBPRD_AO_TRAN A ")
                .append("left join ( ")
                .append("    select INSPRD_ID, INSPRD_NAME from TBPRD_INS_MAIN ")
                .append("    group by INSPRD_ID, INSPRD_NAME ")
                .append(") I on ( A.PRD_ID = I.INSPRD_ID ) ")
                .append("where 1=1 ");
        if (StringUtils.isNotBlank(inputVO.getClassId())) {
            sql.append("and A.CLASS_ID = :classId ");
            params.put("classId", inputVO.getClassId());
        }

        if (StringUtils.isNotBlank(inputVO.getProdId())) {
            sql.append("and A.PRD_ID = :prodId ");
            params.put("prodId", inputVO.getProdId());
        }

        if (StringUtils.isNotBlank(inputVO.getEmpId())) {
            sql.append("and A.EMP_ID = :empId");
            params.put("empId", inputVO.getEmpId());
        }
        return sql.toString();
    }

    /**
     * 新增課程
     **/
    public void add(Object body, IPrimitiveMap header) {
        setInputVO(body);
        String errMsg = "";
        try {
            if (getAllPrdMap().containsKey(inputVO.getProdId())) {
                mergeData(inputVO.getClassId(), inputVO.getProdId(), inputVO.getEmpId());
            } else {
                errMsg = getPrdTrainingNotExistsMsg(inputVO.getProdId());
            }
        } catch (JBranchException e) {
            errMsg = e.getCause().toString();
        }
        sendRtnObject(errMsg);
    }

    /**
     * 下載【上傳課程資訊】範例檔
     **/
    public void uploadExample(Object body, IPrimitiveMap header) throws JBranchException {
        CSVUtil csv = new CSVUtil();
        csv.setFileName(UUID.randomUUID().toString());
        csv.setHeader(new String[]{"課程代碼", "商品代碼", "員工編碼"});
        csv.addRecord(new String[]{"CLASS1", "PROD1", "888888"});
        notifyClientToDownloadFile(csv.generateCSV(), "上傳指定課程與專員檔案範例.csv");
    }

    /**
     * 上傳課程
     **/
    public void upload(Object body, IPrimitiveMap header) throws JBranchException {
        setInputVO(body);
        List<String[]> dataCsv = CSVUtil.getBig5CSVFile(AccessContext.tempPath, inputVO.getFileName());
        List errMsg = new ArrayList();
        /** 忽略 Header，若有資料則執行 **/
        if (dataCsv.size() > 1) {
            if (produstsAllExist(dataCsv, errMsg)) {
                for (int i = 1; i < dataCsv.size(); i++) {
                    String[] row = dataCsv.get(i);
                    if (dataCheck(row))
                        try {
                            mergeData(row[0], row[1], row[2]);
                        } catch (JBranchException e) {
                            errMsg.add(String.format("第 %s 行發生錯誤：%s！", i + 1,e.getCause().toString()));
                        }
                    else errMsg.add(String.format("第 %s 行資料錯誤，無法上傳！", i + 1));
                }
            }
        } else errMsg.add("上傳文件沒有資料項！");
        sendRtnObject(errMsg);
    }

    /**
     *	檢查每個上傳的 PRD_ID 是否有在 “保險商品訓練設定檔” 內做過維護(在 TBPRD_AO_TRAN_LIST 內有資料)，
     * 	若無，則跳出錯誤提示訊息(商品XXX、YYY尚未有訓練設定)
     */
    private boolean produstsAllExist(List<String[]> dataCsv, List errMsg) throws JBranchException {
        Set<String> uploadedPrdIDs = getUploadedPrdIDs(dataCsv);
        Map<String, String> allPrdMap = getAllPrdMap();
        List<String> notExistPrdIDs = new ArrayList();

        for (String prdID: uploadedPrdIDs) {
            if (allPrdMap.containsKey(prdID)) continue;

            notExistPrdIDs.add(prdID);
        }

        if (notExistPrdIDs.isEmpty()) return true;

        errMsg.add(getPrdTrainingNotExistsMsg(StringUtils.join(notExistPrdIDs, "、")));
        return false;
    }

    private String getPrdTrainingNotExistsMsg(String prdIDs) {
        return "商品" + prdIDs + "尚未有訓練設定";
    }

    /** 取得保險商品訓練設定檔（TBPRD_AO_TRAN_LIST）所有的 RPD_ID **/
    private Map<String, String> getAllPrdMap() throws JBranchException {
        List<Map<String, String>> data = Manager.manage(this.getDataAccessManager())
                .append("select PRD_ID from TBPRD_AO_TRAN_LIST ")
                .append("group by PRD_ID ")
                .query();

        Map<String, String> allPrdMap = new HashMap();
        for (Map<String, String> map: data) {
            String prdID = map.get("PRD_ID");
            if (allPrdMap.containsKey(prdID)) continue;

            allPrdMap.put(prdID, prdID);
        }
        return allPrdMap;
    }

    /** 取得上傳檔案的所有商品代碼 **/
    private Set<String> getUploadedPrdIDs(List<String[]> dataCsv) {
        Set<String> prdIDs = new HashSet();
        // skip header
        for (int i = 1; i < dataCsv.size(); i++) {
            String prd = dataCsv.get(i)[1];
            if (StringUtils.isNotBlank(prd))
                prdIDs.add(prd);
        }
        return prdIDs;
    }

    /**
     * 將資料 merge 到資料庫
     **/
    private void mergeData(String classId, String prodId, String empId) throws JBranchException {
        exeUpdateForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
                .append("merge into TBPRD_AO_TRAN MAIN ")
                .append("using ( ")
                .append("  select M.EMP_NAME from TBORG_MEMBER M where M.EMP_ID = :empId ")
                .append(") REF ")
                .append("on ( ")
                .append("  MAIN.EMP_ID = :empId and ")
                .append("  MAIN.CLASS_ID = :classId and ")
                .append("  MAIN.PRD_ID = :prodId) ")
                .append("when matched then ")
                .append("  update set MAIN.MANUAL = 1, ")
                .append("             MAIN.STATUS = 1, ")
                .append("             MAIN.UPDATE_DATE = sysdate, ")
                .append("             MAIN.COMPLETE_DATE = sysdate, ")
                .append("             MAIN.MODIFIER = :modifier ")
                .append("when not matched then ")
                .append("  insert (CLASS_ID, PRD_ID, EMP_ID, EMP_NAME, MANUAL, STATUS, COMPLETE_DATE, CREATE_DATE, CREATOR, UPDATE_DATE, MODIFIER) ")
                .append("  values (:classId, :prodId, :empId, REF.EMP_NAME, 1, 1, sysdate, sysdate, :modifier, sysdate, :modifier) ")
                .toString())
                .setObject("empId", empId)
                .setObject("classId", classId)
                .setObject("prodId", prodId)
                .setObject("modifier", getCommonVariable(SystemVariableConsts.LOGINID)));
    }

    /**
     * 檢查上傳文件是否格式正確
     **/
    private boolean dataCheck(String[] data) {
        final int length = 3;
        for (int i = 0; i < length; i++)
            if (StringUtils.isBlank(data[i]))
                return false;
        return true;
    }

    /**
     * 刪除課程
     **/
    public void delete(Object body, IPrimitiveMap header) throws JBranchException {
        setInputVO(body);
        String errMsg = "";
        try {
            exeUpdateForQcf(genDefaultQueryConditionIF()
                    .setQueryString("delete from TBPRD_AO_TRAN where CLASS_ID = :classId and PRD_ID = :prodId and EMP_ID = :empId ")
                    .setObject("classId", inputVO.getClassId())
                    .setObject("prodId", inputVO.getProdId())
                    .setObject("empId", inputVO.getEmpId()));
        } catch (JBranchException e) {
            errMsg = e.getCause().toString();
        }
        sendRtnObject(errMsg);
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

        Map<String, String> statusMap = new HashMap();
        statusMap.put("0", "未通過");
        statusMap.put("1", "通過");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        List listCSV = new ArrayList();
        for (Map<String, Object> map : list) {
            String[] records = new String[7];
            int i = 0;
            records[i] = formatStr(map.get("CLASS_ID"));                // 課程代碼
            records[++i] = formatStr(map.get("PRD_ID"));                // 商品代碼
            records[++i] = formatStr(map.get("INSPRD_NAME"));           // 商品名稱
            records[++i] = formatStr(map.get("EMP_ID"));                // 員工編碼
            records[++i] = formatStr(map.get("EMP_NAME"));              // 員工姓名
            records[++i] = formatStr(statusMap.get(map.get("STATUS"))); // 通過狀態
            records[++i] = formatStr(dateFormat.format(map.get("COMPLETE_DATE"))); // 通過時間

            listCSV.add(records);
        }

        CSVUtil csv = new CSVUtil();
        csv.setHeader(new String[]{"課程代碼", "商品代碼", "商品名稱", "員工編碼", "員工姓名", "通過狀態", "通過時間"});
        csv.addRecordList(listCSV);
        String url = csv.generateCSV();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String fileName = "保險商品訓練檔_" + sdf.format(new Date())+ ".csv";
        notifyClientToDownloadFile(url, fileName);
        this.sendRtnObject(null);
    }
    
    /**
     * 修改：通過時間
     */
    public void save(Object body, IPrimitiveMap header) throws JBranchException {
    	PRD311InputVO inputVO = (PRD311InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append(" UPDATE TBPRD_AO_TRAN SET COMPLETE_DATE = :complete_date ");
		sb.append(" WHERE CLASS_ID = :class_id AND PRD_ID = :prd_id AND EMP_ID = :emp_id ");
		
		qc.setObject("complete_date", new Timestamp(inputVO.getCompleteDate().getTime()));
		qc.setObject("class_id", inputVO.getClassId());
		qc.setObject("prd_id", inputVO.getProdId());
		qc.setObject("emp_id", inputVO.getEmpId());
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		
		this.sendRtnObject(null);
    }
}