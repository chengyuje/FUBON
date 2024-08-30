package com.systex.jbranch.app.server.fps.cmmgr006;

import com.systex.jbranch.fubon.bth.job.context.AccessContext;
import com.systex.jbranch.fubon.commons.ExcelStyleProvider;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 參數種類與編碼設定
 *
 * @author Eli
 * @date 2019/04/16 將 TBSYSPARAMTYPE（參數種類）、TBSYSPARAMETER（參數編碼）給合併起來。
 */
@Component("cmmgr006")
@Scope("request")
public class CMMGR006 extends FubonWmsBizLogic {

    private CMMGR006InputVO inputVO;

    @Autowired
    private CMMGR006OutputVO outputVO;

    /**
     * 設置 Input
     **/
    private void setInputVO(Object body) {
        inputVO = (CMMGR006InputVO) body;
    }

    /**
     * 根據查詢條件，查詢參數資料並回傳到客戶端
     *
     * @param body
     * @param header
     * @throws JBranchException
     */
    public void inquireType(Object body, IPrimitiveMap<?> header) throws JBranchException {
        setInputVO(body);

        Map params = new HashMap();
        outputVO.setParamData(exeQueryForMap(configureQryParamTypeSql(params), params));
        sendRtnObject(outputVO);
    }

    /**
     * 檢查某參數種類是否存在資料庫中
     **/
    public void existType(Object body, IPrimitiveMap<?> header) throws JBranchException {
        setInputVO(body);
        this.sendRtnObject(CollectionUtils.isNotEmpty(
                exeQueryForQcf(genDefaultQueryConditionIF()
                        .setQueryString("select 1 from TBSYSPARAMTYPE A where A.PARAM_TYPE = :type ")
                        .setObject("type", inputVO.getParamType()))
        ));
    }

    /**
     * 新增客戶所填的參數種類
     *
     * @param body
     * @param header
     * @throws JBranchException
     */
    public void insertType(Object body, IPrimitiveMap<?> header) throws JBranchException {
        setInputVO(body);

        exeUpdateForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
                .append("insert into TBSYSPARAMTYPE(PARAM_TYPE, PTYPE_NAME, PTYPE_BUSS, PTYPE_DESC, ROLE_MAINTAIN, PTYPE_RANGE, ")
                .append("WORKS_TYPE, CSV_TYPE, VALIDATE_TYPE, MAX_INPUT, VALIDATE_RANGE, AUTH_CODE, VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE) ")
                .append("values(:type, :name, :buss, :desc, '00', '0', '0', '9', '0', 0, '', '0', 0, sysdate, 'system', 'system', sysdate) ")
                .toString())
                .setObject("type", inputVO.getParamType())
                .setObject("name", inputVO.getPtypeName())
                .setObject("buss", inputVO.getPtypeBuss())
                .setObject("desc", inputVO.getPtypeDesc()));
        sendRtnObject(null);
    }

    /**
     * 修改客戶所填寫的參數種類資訊
     *
     * @param body
     * @param header
     * @throws JBranchException
     */
    public void updateType(Object body, IPrimitiveMap<?> header) throws JBranchException {
        setInputVO(body);

        exeUpdateForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
                .append("update TBSYSPARAMTYPE set PTYPE_NAME = :name, PTYPE_BUSS = :buss, PTYPE_DESC = :desc, LASTUPDATE = sysdate ")
                .append("where PARAM_TYPE = :type ")
                .toString())
                .setObject("name", inputVO.getPtypeName())
                .setObject("buss", inputVO.getPtypeBuss())
                .setObject("desc", inputVO.getPtypeDesc())
                .setObject("type", inputVO.getParamType()));
        sendRtnObject(null);
    }

    /**
     * 刪除客戶所選定的參數種類
     *
     * @param body
     * @param header
     * @throws JBranchException
     */
    public void deleteType(Object body, IPrimitiveMap<?> header) throws JBranchException {
        setInputVO(body);

        exeUpdateForQcf(genDefaultQueryConditionIF()
                .setQueryString("delete from TBSYSPARAMETER where PARAM_TYPE = :type ")
                .setObject("type", inputVO.getParamType()));


        exeUpdateForQcf(genDefaultQueryConditionIF()
                .setQueryString("delete from TBSYSPARAMTYPE where PARAM_TYPE = :type ")
                .setObject("type", inputVO.getParamType()));
        sendRtnObject(null);
    }

    /**
     * 參數種類 - 配置 SQL & Params
     **/
    private String configureQryParamTypeSql(Map params) {
        StringBuilder sql = new StringBuilder()
                .append("select PARAM_TYPE, PTYPE_NAME, PTYPE_BUSS, CREATETIME, PTYPE_DESC from TBSYSPARAMTYPE A where 1=1 ");

        if (StringUtils.isNotBlank(inputVO.getPtypeBuss())) {
            sql.append("and A.PTYPE_BUSS = :buss ");
            params.put("buss", inputVO.getPtypeBuss());
        }

        if (StringUtils.isNotBlank(inputVO.getParamType())) {
            sql.append("and A.PARAM_TYPE like :type ");
            params.put("type", "%" + inputVO.getParamType() + "%");
        }

        if (StringUtils.isNotBlank(inputVO.getPtypeName())) {
            sql.append("and A.PTYPE_NAME like :name ");
            params.put("name", "%" + inputVO.getPtypeName() + "%");
        }
        sql.append("order by PARAM_TYPE ");

        return sql.toString();
    }

    /**
     * 查詢參數編碼
     *
     * @param body
     * @param header
     */
    public void inquireCode(Object body, IPrimitiveMap<?> header) throws JBranchException {
        setInputVO(body);
        outputVO.setParamData(exeQueryForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
                .append("select PARAM_CODE, PARAM_NAME, PARAM_DESC, PARAM_ORDER from TBSYSPARAMETER ")
                .append("where PARAM_TYPE = :type order by PARAM_ORDER desc ")
                .toString())
                .setObject("type", inputVO.getParamType())));
        sendRtnObject(outputVO);
    }

    /**
     * 新增參數編碼
     *
     * @param body
     * @param header
     */
    public void insertCode(Object body, IPrimitiveMap<?> header) throws JBranchException {
        setInputVO(body);
        exeUpdateForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
                .append("insert into TBSYSPARAMETER(PARAM_TYPE,PARAM_CODE,VERSION,PARAM_ORDER,PARAM_NAME,PARAM_NAME_EDIT, ")
                .append("PARAM_DESC,PARAM_STATUS,CREATETIME,CREATOR,MODIFIER,LASTUPDATE)")
                .append("values (:type, :code, 0, :order, :name, :name, :desc, 0, sysdate, 'system', 'system', sysdate)")
                .toString())
                .setObject("type", inputVO.getParamType())
                .setObject("code", inputVO.getParamCode())
                .setObject("order", inputVO.getParamOrder())
                .setObject("name", inputVO.getParamName())
                .setObject("desc", inputVO.getParamDesc()));
        sendRtnObject(null);
    }

    /**
     * 更新參數編碼
     *
     * @param body
     * @param header
     */
    public void updateCode(Object body, IPrimitiveMap<?> header) throws JBranchException {
        setInputVO(body);
        exeUpdateForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
                .append("update TBSYSPARAMETER set PARAM_CODE = :newCode, PARAM_NAME = :name, PARAM_DESC = :desc, LASTUPDATE = sysdate ")
                .append("where PARAM_TYPE = :type and PARAM_CODE = :prevCode ")
                .toString())
                .setObject("newCode", inputVO.getParamCode())
                .setObject("name", inputVO.getParamName())
                .setObject("desc", inputVO.getParamDesc())
                .setObject("type", inputVO.getParamType())
                .setObject("prevCode", inputVO.getPrevCode()));
        sendRtnObject(null);
    }

    /**
     * 刪除參數編碼
     *
     * @param body
     * @param header
     */
    public void deleteCode(Object body, IPrimitiveMap<?> header) throws JBranchException {
        setInputVO(body);
        exeUpdateForQcf(genDefaultQueryConditionIF()
                .setQueryString("delete from TBSYSPARAMETER where PARAM_TYPE = :type and PARAM_CODE = :code ")
                .setObject("type", inputVO.getParamType())
                .setObject("code", inputVO.getParamCode()));
        sendRtnObject(null);
    }

    /**
     * 更新參數編碼順序
     *
     * @param body
     * @param header
     * @throws JBranchException
     */
    public void updateCodeOrder(Object body, IPrimitiveMap<?> header) throws JBranchException {
        setInputVO(body);

        for (Map.Entry entry : inputVO.getCodeMap().entrySet()) {
            exeUpdateForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
                    .append("update TBSYSPARAMETER set PARAM_ORDER = :order ")
                    .append("where PARAM_TYPE = :type and PARAM_CODE = :code ")
                    .toString())
                    .setObject("order", entry.getValue())
                    .setObject("type", inputVO.getParamType())
                    .setObject("code", entry.getKey()));
        }
    }

    /**
     * 輸出參數種類與參數編碼資料到 Excel
     *
     * @param body
     * @param header
     * @throws JBranchException
     */
    public void exportExcel(Object body, IPrimitiveMap<?> header) throws JBranchException, IOException {
        setInputVO(body);

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("參數資料");
            /** 凍結區域 **/
            sheet.createFreezePane(2, 1);

            /** 設置表格欄寬與列高 **/
            sheet.setDefaultColumnWidth(50);
            sheet.setDefaultRowHeightInPoints(20);

            /** header style **/
            XSSFCellStyle headerStyle = workbook.createCellStyle();
            ExcelStyleProvider.renderDefaultHeader(headerStyle)
                    .setFont(ExcelStyleProvider.renderDefaultHeaderFont(workbook.createFont()));

            XSSFRow headerRow = sheet.createRow(0);
            /** 標題各欄位數值 **/
            String[] headerCols = {"種類＆名稱（PARAM_TYPE）", "類型", "顯示順序（PARAM_ORDER）", "編碼（PARAM_CODE） - F2",
                    "參數值（PARAM_NAME）- F3", "編碼-參數值 - F1（預設）", "參數說明（PARAM_DESC）", "SQL"};

            for (int i = 0; i < headerCols.length; i++) {
                XSSFCell headerCell = headerRow.createCell(i, XSSFCell.CELL_TYPE_STRING);
                headerCell.setCellValue(headerCols[i]);
                headerCell.setCellStyle(headerStyle);
            }

            /** PTYPE_BUSS PARAMETER **/
            Map<String, String> buss = new XmlInfo().doGetVariable("TBSYSPARAMTYPE.PTYPE_BUSS", FormatHelper.FORMAT_3);

            /** 資料區 **/
            List<Map> data = getExcelData();
            String prevTypeTmp = "";
            int tmpIndex = 0;
            for (int i = 1; i < data.size() + 1; i++) {
                XSSFRow dataRow = sheet.createRow(i);
                Map each = data.get(i - 1);

                /** 此 if 達到每組參數種類第一欄與第二欄位合併效果 **/
                if (!prevTypeTmp.equals(each.get("PARAM_TYPE").toString())) {
                    /** 排除一開始的合併欄位動作 **/
                    if (i != 1) {
                        try {
                            sheet.addMergedRegion(new CellRangeAddress(tmpIndex, i - 1, 0, 0));
                            sheet.addMergedRegion(new CellRangeAddress(tmpIndex, i - 1, 1, 1));
                        } catch (Exception e) {
                        } // 排除參數只有一個的情況
                    }

                    /** 參數種類＆名稱 **/
                    XSSFCell typeNameCell = dataRow.createCell(0, XSSFCell.CELL_TYPE_STRING);
                    typeNameCell.setCellValue(String.format("%s（%s）", each.get("PARAM_TYPE").toString(), each.get("PTYPE_NAME")));
                    /** 類型 **/
                    XSSFCell typeCell = dataRow.createCell(1, XSSFCell.CELL_TYPE_STRING);
                    typeCell.setCellValue(buss.get(each.get("PTYPE_BUSS")) != null ? buss.get(each.get("PTYPE_BUSS")) : " ");

                    prevTypeTmp = each.get("PARAM_TYPE").toString();
                    tmpIndex = i;
                }
                /** 顯示順序 **/
                XSSFCell orderCell = dataRow.createCell(2, XSSFCell.CELL_TYPE_NUMERIC);
                orderCell.setCellValue(Integer.parseInt(each.get("PARAM_ORDER").toString()));
                /** 編碼 **/
                XSSFCell codeCell = dataRow.createCell(3, XSSFCell.CELL_TYPE_STRING);
                codeCell.setCellValue(each.get("PARAM_CODE").toString());
                /** 參數值 **/
                XSSFCell codeNameCell = dataRow.createCell(4, XSSFCell.CELL_TYPE_STRING);
                codeNameCell.setCellValue(each.get("PARAM_NAME").toString());
                /** 編碼-參數值 - F1（預設） **/
                XSSFCell mixCell = dataRow.createCell(5, XSSFCell.CELL_TYPE_STRING);
                mixCell.setCellValue(String.format("%s-%s", each.get("PARAM_CODE"), each.get("PARAM_NAME")));
                /** 參數說明 **/
                XSSFCell descCell = dataRow.createCell(6, XSSFCell.CELL_TYPE_STRING);
                descCell.setCellValue(each.get("PARAM_DESC").toString());
                /** SQL **/
                XSSFCell sqlCell = dataRow.createCell(7, XSSFCell.CELL_TYPE_STRING);
                sqlCell.setCellValue(String.format(
                        new StringBuilder()
                                .append("insert into TBSYSPARAMETER(PARAM_TYPE,PARAM_CODE,PARAM_ORDER,PARAM_NAME,PARAM_NAME_EDIT, ")
                                .append("PARAM_DESC,PARAM_STATUS, VERSION, CREATOR, CREATETIME, LASTUPDATE, MODIFIER) ")
                                .append("values('%s','%s',%s,'%s','%s','%s','0', 0, 'system', sysdate, sysdate, 'system'); ").toString(),
                        each.get("PARAM_TYPE"), each.get("PARAM_CODE"), each.get("PARAM_ORDER"), each.get("PARAM_NAME"),
                        each.get("PARAM_NAME"), each.get("PARAM_DESC")
                ));
            }
            /** 最後一組參數做合併 **/
            try {
                sheet.addMergedRegion(new CellRangeAddress(tmpIndex, data.size(), 0, 0));
                sheet.addMergedRegion(new CellRangeAddress(tmpIndex, data.size(), 1, 1));
            } catch (Exception e) {
            } // 排除參數只有一個的情況

            /** 寫檔 **/
            String fileName = UUID.randomUUID().toString();
            try (OutputStream excel = Files.newOutputStream(Paths.get(AccessContext.tempReportPath).resolve(fileName))) {
                workbook.write(excel);
            }

            /** 通知瀏覽器下載該檔 **/
            notifyClientToDownloadFile("temp\\reports\\" + fileName, "Common_XML參數.xlsx");
        }
    }

    /**
     * 取得需要輸出到 Excel 資料
     **/
    private List<Map> getExcelData() throws JBranchException {
        Map params = new HashMap();
        return exeQueryForMap(configureExcelDataSql(params), params);
    }

    /**
     * Excel 資料 - 配置 SQL & Params
     **/
    private String configureExcelDataSql(Map params) {
        StringBuilder sql = new StringBuilder()
                .append("select NVL(M.PARAM_TYPE, ' ') PARAM_TYPE, NVL(T.PTYPE_NAME, ' ') PTYPE_NAME, NVL(T.PTYPE_BUSS, ' ') PTYPE_BUSS, ")
                .append("NVL(M.PARAM_CODE, ' ') PARAM_CODE, NVL(M.PARAM_NAME, ' ') PARAM_NAME, NVL(M.PARAM_ORDER, 0) PARAM_ORDER,  NVL(M.PARAM_DESC, ' ') PARAM_DESC ")
                .append("from TBSYSPARAMETER M ")
                .append("left join TBSYSPARAMTYPE T ")
                .append("on M.PARAM_TYPE = T.PARAM_TYPE where 1=1 ");
        if (StringUtils.isNotBlank(inputVO.getPtypeBuss())) {
            sql.append("and T.PTYPE_BUSS = :buss ");
            params.put("buss", inputVO.getPtypeBuss());
        }

        if (StringUtils.isNotBlank(inputVO.getParamType())) {
            sql.append("and M.PARAM_TYPE like :type ");
            params.put("type", "%" + inputVO.getParamType() + "%");
        }

        if (StringUtils.isNotBlank(inputVO.getPtypeName())) {
            sql.append("and T.PTYPE_NAME like :name ");
            params.put("name", "%" + inputVO.getPtypeName() + "%");
        }
        sql.append("order by M.PARAM_TYPE, M.PARAM_ORDER ");
        return sql.toString();
    }
}
