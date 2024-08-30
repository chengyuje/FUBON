package com.systex.jbranch.app.server.fps.prd177;

import com.systex.jbranch.app.common.fps.table.TBJSB_INS_PROD_BOUNDPK;
import com.systex.jbranch.app.common.fps.table.TBJSB_INS_PROD_BOUNDVO;
import com.systex.jbranch.app.common.fps.table.TBJSB_INS_PROD_MAINVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.Manager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.systex.jbranch.fubon.commons.ExcelUtil.*;
import static org.apache.commons.lang.StringUtils.*;

/**
 * PRD177 日盛產品管理
 *
 * @author Eli
 * @date 2022/06/17
 */
@Component("prd177")
@Scope("request")
@SuppressWarnings({"unchecked"})
public class PRD177 extends FubonWmsBizLogic {
    private Logger logger = LoggerFactory.getLogger(PRD177.class);

    public void inquireBound(Object body, IPrimitiveMap header) throws JBranchException {
        PRD177InputVO inputVO = (PRD177InputVO) body;
        PRD177OutputVO outputVO = new PRD177OutputVO();
        outputVO.setCodeList(inquireBoundCode());
        outputVO.setBoundList(inquireBound(inputVO.getProductSerialNum()));
        this.sendRtnObject(outputVO);
    }

    private List<Map<String, Object>> inquireBound(BigDecimal productSerialNum) throws JBranchException {
        return Manager.manage(this.getDataAccessManager())
                .append("select PRODUCTSERIALNUM, BOUNDITEM, BOUNDNAME, CHANNEL, ")
                .append("       ADD_1_START_MONTH, ADD_1_END_MONTH, ADD_1_COMMISSIONRATE, ADD_1_COMMRATEA ")
                .append("from TBJSB_INS_PROD_BOUND BOUND ")
                .append("where PRODUCTSERIALNUM = :productSerialNum ")
                .put("productSerialNum", productSerialNum)
                .query();

    }

    private List<Map<String, Object>> inquireBoundCode() throws JBranchException {
        return Manager.manage(this.getDataAccessManager())
                .append("select CD_ITEM, CD_DESC, CD_NM ")
                .append("from TBJSB_INS_PROD_CODE_TABLE ")
                .append("where CD_INDEX = 'A11' ")
                .append("order by CD_ITEM ")
                .query();
    }

    public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
        PRD177InputVO inputVO = (PRD177InputVO) body;

        PRD177OutputVO outputVO = new PRD177OutputVO();
        outputVO.setResultList(getResult(inputVO));
        this.sendRtnObject(outputVO);
    }

    private List<Map<String, Object>> getResult(PRD177InputVO inputVO) throws JBranchException {
        return Manager.manage(this.getDataAccessManager())
                .append("select COM.CNAME, ")
                .append("       PRD_M.*, ")
                .append("       ITEM.ITEMSERIALNUM, ITEM.PRODUCTPUDTYPE, ITEM.PRODUCTPUD, ITEM.PRODUCTEDTYPE, ITEM.PRODUCTED, ")
                .append("       BOUND.BOUNDRATE ")
                .append("from TBJSB_INS_PROD_MAIN PRD_M ")
                .append("join TBJSB_INS_PROD_COMPANY COM ")
                .append("on (PRD_M.INSURANCECOSERIALNUM = COM.SERIALNUM) ")
                .append("left join TBJSB_INS_PROD_LIFEITEM ITEM ")
                .append("on (PRD_M.PRODUCTSERIALNUM = ITEM.PRODUCTSERIALNUM) ")
                .append("left join ( ")
                .append("   select LISTAGG(BOUNDRATE, ',') within group ( order by BOUNDITEM) BOUNDRATE, PRODUCTSERIALNUM ")
                .append("   from TBJSB_INS_PROD_BOUND ")
                .append("   group by PRODUCTSERIALNUM ")
                .append(") BOUND on (PRD_M.PRODUCTSERIALNUM = BOUND.PRODUCTSERIALNUM) ")
                .append("where 1=1 ")
                .condition(inputVO.getInsuranceCoSerialNum() != null,
                        "and PRD_M.INSURANCECOSERIALNUM = :insuranceCoSerialNum ", "insuranceCoSerialNum", inputVO.getInsuranceCoSerialNum()) // 保險公司序號
                .condition(isNotBlank(inputVO.getShortName()), // 保險產品簡稱
                        "and PRD_M.PRODUCTSHORTNAME like :shortName ", "shortName", "%" + inputVO.getShortName() + "%")
                .condition(isNotBlank(inputVO.getPrdId()),     // 英文代碼
                        "and PRD_M.PRODUCTID like :productId ", "productId", "%" + inputVO.getPrdId() + "%")
                .condition(isNotBlank(inputVO.getCurrency()),  // 幣別
                        "and PRD_M.CURRENCY1 like :currency ", "currency", "%" + inputVO.getCurrency() + "%")
                .condition(null != inputVO.getProductValidFromSDate(), // 開始銷售日（起）
                        "and PRD_M.PRODUCTVALIDFROM >= :startDate ", "startDate", inputVO.getProductValidFromSDate())
                .condition(null != inputVO.getProductValidFromEDate(), // 開始銷售日（迄）
                        "and PRD_M.PRODUCTVALIDFROM <= :endDate ", "endDate", inputVO.getProductValidFromEDate())
                .query();
    }

    public void exportCSV(Object body, IPrimitiveMap header) throws JBranchException {
        PRD177InputVO inputVO = (PRD177InputVO) body;

        CSVUtil csv = new CSVUtil();
        csv.setHeader(getCsvHeader().toArray(new String[0]));

        if (!inputVO.isSampleExport()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

            List<Object[]> csvRecordList = new LinkedList<>();
            for (Map<String, Object> map : getResult(inputVO)) {
                List<String> rowData = new LinkedList<>();
                rowData.add(ObjectUtils.toString(map.get("PRODUCTSERIALNUM")));   // 日盛產品序號
                rowData.add(ObjectUtils.toString(map.get("CNAME")));              // 保險公司
                rowData.add(ObjectUtils.toString(map.get("PRODUCTID")));          // 英文代碼
                rowData.add(ObjectUtils.toString(map.get("MOF")));                // 核准文號
                rowData.add(ObjectUtils.toString(map.get("PRODUCTNAME")));        // 保險產品
                rowData.add(ObjectUtils.toString(map.get("MAINSTAY_PRODUCT")));   // 主推商品
                rowData.add(ObjectUtils.toString(map.get("PRODUCTSHORTNAME")));   // 保險產品簡稱
                rowData.add(ObjectUtils.toString(map.get("CONTRACTID")));         // 主附約別
                rowData.add(ObjectUtils.toString(map.get("ADDSUB_FLAG")));        // 可附加附約

                Date productOnDt = (Date) map.get("PRODUCTON_DT");
                rowData.add(productOnDt != null ? sdf.format(productOnDt) : "");    // 產品上架日
                Date productValidFrom = (Date) map.get("PRODUCTVALIDFROM");
                rowData.add(productValidFrom != null ? sdf.format(productValidFrom) : ""); // 開始銷售日
                Date productValidThru = (Date) map.get("PRODUCTVALIDTHRU");
                rowData.add(productValidThru != null ? sdf.format(productValidThru) : ""); // 停止銷售日

                rowData.add(ObjectUtils.toString(map.get("COMM1_FLAG")));         // 保費必輸檢核-目標保費
                rowData.add(ObjectUtils.toString(map.get("COMM2_FLAG")));         // 保費必輸檢核-超額保費
                rowData.add(ObjectUtils.toString(map.get("PRODUCTTYPE1")));       // 產品大分類
                rowData.add(ObjectUtils.toString(map.get("PRODUCTCATEGORY")));    // 產品中分類
                rowData.add(ObjectUtils.toString(map.get("SUMASSUMEDTYPE")));     // 保額型態
                rowData.add(ObjectUtils.toString(map.get("PREMIUMTABLE")));       // 平台
                rowData.add(ObjectUtils.toString(map.get("CONTRACTBONUS")));      // 年化佣金註記
                rowData.add(ObjectUtils.toString(map.get("CURRENCY1")));          // 商品幣別
                rowData.add(ObjectUtils.toString(map.get("CHANNEL")));            // 案件來源
                rowData.add(ObjectUtils.toString(map.get("PRODUCTRISK")));        // 產品風險
                rowData.add(ObjectUtils.toString(map.get("RISKRATE")));           // 風險等級
                rowData.add(ObjectUtils.toString(map.get("COMMRATE")));           // 目標佣金率
                rowData.add(ObjectUtils.toString(map.get("COMMRATEA")));          // 超額佣金率
                rowData.add(ObjectUtils.toString(map.get("COMMRATEC")));          // 浮動佣金率
                rowData.add(ObjectUtils.toString(map.get("BOUNDRATE")));          // 續期佣金率
                rowData.add(ObjectUtils.toString(map.get("PRODUCTPUDTYPE")));     // 繳費年期狀態
                rowData.add(ObjectUtils.toString(map.get("PRODUCTPUD")));         // 繳費年期
                rowData.add(ObjectUtils.toString(map.get("PRODUCTEDTYPE")));      // 保障期狀態
                rowData.add(ObjectUtils.toString(map.get("PRODUCTED")));          // 保障年期
                rowData.add(ObjectUtils.toString(map.get("PRODUCTEXPDATE")));     // 產品年期
                rowData.add(ObjectUtils.toString(map.get("MEMO")));               // 備註
                rowData.add(ObjectUtils.toString(map.get("CH_FLAG")));            // 轉換保單
                rowData.add(ObjectUtils.toString(map.get("A_FLAG")));             // 證照檢核-壽險證照
                rowData.add(ObjectUtils.toString(map.get("B_FLAG")));             // 證照檢核-投資證照
                rowData.add(ObjectUtils.toString(map.get("C_FLAG")));             // 證照檢核-外幣商品證照

                csvRecordList.add(rowData.toArray(new String[0]));
            }
            csv.addRecordList(csvRecordList);
        }
        notifyClientToDownloadFile(csv.generateCSV(), "日盛產品管理.csv");
    }

    private List<String> getCsvHeader() {
        List<String> csvHeader = new LinkedList<>();
        csvHeader.add("日盛產品序號");
        csvHeader.add("保險公司");
        csvHeader.add("英文代碼");
        csvHeader.add("核准文號");
        csvHeader.add("保險產品");
        csvHeader.add("主推商品");
        csvHeader.add("保險產品簡稱");
        csvHeader.add("主附約別");
        csvHeader.add("可附加附約");
        csvHeader.add("產品上架日");
        csvHeader.add("開始銷售日");
        csvHeader.add("停止銷售日");
        csvHeader.add("保費必輸檢核-目標保費");
        csvHeader.add("保費必輸檢核-超額保費");
        csvHeader.add("產品大分類");
        csvHeader.add("產品中分類");
        csvHeader.add("保額型態");
        csvHeader.add("平台");
        csvHeader.add("年化佣金註記");
        csvHeader.add("商品幣別");
        csvHeader.add("案件來源");
        csvHeader.add("產品風險");
        csvHeader.add("風險等級");
        csvHeader.add("目標佣金率");
        csvHeader.add("超額佣金率");
        csvHeader.add("浮動佣金率");
        csvHeader.add("續期佣金率(區間年)");
        csvHeader.add("繳費年期狀態");
        csvHeader.add("繳費年期");
        csvHeader.add("保障期狀態");
        csvHeader.add("保障年期");
        csvHeader.add("產品年期");
        csvHeader.add("備註");
        csvHeader.add("轉換保單");
        csvHeader.add("證照檢核-壽險證照");
        csvHeader.add("證照檢核-投資證照");
        csvHeader.add("證照檢核-外幣商品證照");
        return csvHeader;
    }

    public void upload(Object body, IPrimitiveMap header) throws JBranchException {
        PRD177InputVO inputVO = (PRD177InputVO) body;

        String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
        List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());
        List<String> csvHeader = getCsvHeader();
        if (dataCsv.isEmpty() ||
                // Header 必須一致
                !Arrays.equals(dataCsv.get(0), csvHeader.toArray(new String[0]))) {
            throw new APException("上傳格式錯誤，請下載範例檔案");
        }

        if (dataCsv.size() <= 1) {
            throw new APException("上傳檔案僅有 header，無包含資料");
        }

        List<String> typeError = new LinkedList<>(); // 數值不符合資料庫欄位型態
        List<String> requiredError = new LinkedList<>(); // 若為新增，缺少必要欄位
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        DataAccessManager dam = this.getDataAccessManager();
        int columnSize = csvHeader.size();
        String[] columnCodes = getColumnsCode(columnSize);
        for (int index = 1; index < dataCsv.size(); index++) {
            // 以 header size 建立新的 string array，避免上傳欄位空值導致 OutOfIndexBound
            String[] columns = Arrays.copyOf(dataCsv.get(index), columnSize);

            // 保險產品序號
            String serialNumCol = trim(columns[0]);
            TBJSB_INS_PROD_MAINVO vo = null;
            boolean notExists = isBlank(serialNumCol)
                    || (vo = (TBJSB_INS_PROD_MAINVO) dam.findByPKey(
                    TBJSB_INS_PROD_MAINVO.TABLE_UID, new BigDecimal(serialNumCol))) == null;
            vo = (TBJSB_INS_PROD_MAINVO) ObjectUtils.defaultIfNull(vo, new TBJSB_INS_PROD_MAINVO());

            // 保險公司序號 columns[1] 實際上僅記錄用途，整批上傳必須要選擇保險公司後，才能上傳該公司產品
            vo.setINSURANCECOSERIALNUM(inputVO.getInsuranceCoSerialNum());

            // 英文代碼
            String productId = trim(columns[2]);
            if (isNotBlank(productId)) {
                if (productId.length() > 20) {
                    typeError.add(columnLocation(index + 1, 2, columnCodes));
                    continue;
                } else {
                    vo.setPRODUCTID(productId);
                }
            } else {
                requiredError.add(columnLocation(index + 1, 2, columnCodes));
                continue;
            }
            // 核准文號
            String mof = trim(columns[3]);
            if (isNotBlank(mof)) {
                if (mof.length() > 100) {
                    typeError.add(columnLocation(index + 1, 3, columnCodes));
                    continue;
                } else {
                    vo.setMOF(mof);
                }
            } else {
                requiredError.add(columnLocation(index + 1, 3, columnCodes));
                continue;
            }
            // 保險產品名稱
            String productName = trim(columns[4]);
            if (isNotBlank(productName)) {
                if (productName.length() > 50) {
                    typeError.add(columnLocation(index + 1, 4, columnCodes));
                    continue;
                } else {
                    vo.setPRODUCTNAME(productName);
                }
            } else {
                requiredError.add(columnLocation(index + 1, 4, columnCodes));
                continue;
            }
            // 主推商品
            String mainstayProduct = trim(columns[5]);
            if (isNotBlank(mainstayProduct)) {
                if (mainstayProduct.length() > 1) {
                    typeError.add(columnLocation(index + 1, 5, columnCodes));
                    continue;
                } else {
                    vo.setMAINSTAY_PRODUCT(mainstayProduct);
                }
            } else {
                requiredError.add(columnLocation(index + 1, 5, columnCodes));
                continue;
            }
            // 保險產品簡稱
            String productShortName = trim(columns[6]);
            if (isNotBlank(productShortName)) {
                if (productShortName.length() > 50) {
                    typeError.add(columnLocation(index + 1, 6, columnCodes));
                    continue;
                } else {
                    vo.setPRODUCTSHORTNAME(productShortName);
                }
            } else {
                requiredError.add(columnLocation(index + 1, 6, columnCodes));
                continue;
            }
            // 主附約別
            String contractId = trim(columns[7]);
            if (isNotBlank(contractId)) {
                if (contractId.length() > 4) {
                    typeError.add(columnLocation(index + 1, 7, columnCodes));
                    continue;
                } else {
                    vo.setCONTRACTID(contractId);
                }
            } else {
                requiredError.add(columnLocation(index + 1, 7, columnCodes));
                continue;
            }
            // 可附加附約
            String addSubFlag = trim(columns[8]);
            if (isNotBlank(addSubFlag)) {
                if (addSubFlag.length() > 1) {
                    typeError.add(columnLocation(index + 1, 8, columnCodes));
                    continue;
                } else {
                    vo.setADDSUB_FLAG(addSubFlag);
                }
            }
            // 產品上架日
            String productOnDT = trim(columns[9]);
            if (isNotBlank(productOnDT)) {
                try {
                    vo.setPRODUCTON_DT(new Timestamp(sdf.parse(productOnDT).getTime()));
                } catch (ParseException e) {
                    typeError.add(columnLocation(index + 1, 9, columnCodes));
                    continue;
                }
            } else {
                requiredError.add(columnLocation(index + 1, 9, columnCodes));
                continue;
            }
            // 開始銷售日
            String productValidFrom = trim(columns[10]);
            if (isNotBlank(productValidFrom)) {
                try {
                    vo.setPRODUCTVALIDFROM(new Timestamp(sdf.parse(productValidFrom).getTime()));
                } catch (ParseException e) {
                    typeError.add(columnLocation(index + 1, 10, columnCodes));
                    continue;
                }
            } else {
                requiredError.add(columnLocation(index + 1, 10, columnCodes));
                continue;
            }
            // 停止銷售日
            String productValidThru = trim(columns[11]);
            if (isNotBlank(productValidThru)) {
                try {
                    vo.setPRODUCTVALIDTHRU(new Timestamp(sdf.parse(productValidThru).getTime()));
                } catch (ParseException e) {
                    typeError.add(columnLocation(index + 1, 11, columnCodes));
                    continue;
                }
            }
            // 保費必輸檢核-目標保費
            String comm1Flag = trim(columns[12]);
            if (isNotBlank(comm1Flag)) {
                if (comm1Flag.length() > 1) {
                    typeError.add(columnLocation(index + 1, 12, columnCodes));
                    continue;
                } else {
                    vo.setCOMM1_FLAG(comm1Flag);
                }
            } else {
                requiredError.add(columnLocation(index + 1, 12, columnCodes));
                continue;
            }
            // 保費必輸檢核-超額保費
            String comm2Flag = trim(columns[13]);
            if (isNotBlank(comm2Flag)) {
                if (comm2Flag.length() > 1) {
                    typeError.add(columnLocation(index + 1, 13, columnCodes));
                    continue;
                } else {
                    vo.setCOMM2_FLAG(comm2Flag);
                }
            } else {
                requiredError.add(columnLocation(index + 1, 13, columnCodes));
                continue;
            }
            // 產品大分類
            String productType1 = trim(columns[14]);
            if (isNotBlank(productType1)) {
                if (productType1.length() > 10) {
                    typeError.add(columnLocation(index + 1, 14, columnCodes));
                    continue;
                } else {
                    vo.setPRODUCTTYPE1(productType1);
                }
            } else {
                requiredError.add(columnLocation(index + 1, 14, columnCodes));
                continue;
            }
            // 產品中分類
            String productCategory = trim(columns[15]);
            if (isNotBlank(productCategory)) {
                if (productCategory.length() > 20) {
                    typeError.add(columnLocation(index + 1, 15, columnCodes));
                    continue;
                } else {
                    vo.setPRODUCTCATEGORY(productCategory);
                }
            } else {
                requiredError.add(columnLocation(index + 1, 15, columnCodes));
                continue;
            }
            // 保額型態
            String sumAssumedType = trim(columns[16]);
            if (isNotBlank(sumAssumedType)) {
                if (sumAssumedType.length() > 6) {
                    typeError.add(columnLocation(index + 1, 16, columnCodes));
                    continue;
                } else {
                    vo.setSUMASSUMEDTYPE(sumAssumedType);
                }
            } else {
                requiredError.add(columnLocation(index + 1, 16, columnCodes));
                continue;
            }
            // 平台
            String premiumTable = trim(columns[17]);
            if (isNotBlank(premiumTable)) {
                if (premiumTable.length() > 20) {
                    typeError.add(columnLocation(index + 1, 17, columnCodes));
                    continue;
                } else {
                    vo.setPREMIUMTABLE(premiumTable);
                }
            } else {
                requiredError.add(columnLocation(index + 1, 17, columnCodes));
                continue;
            }
            // 年化佣金註記
            String contractBonus = trim(columns[18]);
            if (isNotBlank(contractBonus)) {
                if (contractBonus.length() > 1) {
                    typeError.add(columnLocation(index + 1, 18, columnCodes));
                    continue;
                } else {
                    vo.setCONTRACTBONUS(contractBonus);
                }
            } else {
                requiredError.add(columnLocation(index + 1, 18, columnCodes));
                continue;
            }
            // 商品幣別
            String currency1 = trim(columns[19]);
            if (isNotBlank(currency1)) {
                if (currency1.length() > 200) {
                    typeError.add(columnLocation(index + 1, 19, columnCodes));
                    continue;
                } else {
                    vo.setCURRENCY1(currency1);
                }
            } else {
                requiredError.add(columnLocation(index + 1, 19, columnCodes));
                continue;
            }
            // 案件來源
            String channel = trim(columns[20]);
            if (isNotBlank(channel)) {
                if (channel.length() > 10) {
                    typeError.add(columnLocation(index + 1, 20, columnCodes));
                    continue;
                } else {
                    vo.setCHANNEL(channel);
                }
            } else {
                requiredError.add(columnLocation(index + 1, 20, columnCodes));
                continue;
            }
            // 產品風險
            String productRisk = trim(columns[21]);
            if (isNotBlank(productRisk)) {
                if (productRisk.length() > 2) {
                    typeError.add(columnLocation(index + 1, 21, columnCodes));
                    continue;
                } else {
                    vo.setPRODUCTRISK(productRisk);
                }
            } else {
                requiredError.add(columnLocation(index + 1, 21, columnCodes));
                continue;
            }
            // 風險等級
            String riskRate = trim(columns[22]);
            if (isNotBlank(riskRate)) {
                if (riskRate.length() > 3) {
                    typeError.add(columnLocation(index + 1, 22, columnCodes));
                    continue;
                } else {
                    vo.setRISKRATE(riskRate);
                }
            } else {
                requiredError.add(columnLocation(index + 1, 22, columnCodes));
                continue;
            }
            // 目標佣金率
            String commrate = trim(columns[23]);
            if (isNotBlank(commrate)) {
                try {
                    vo.setCOMMRATE(new BigDecimal(commrate).floatValue());
                } catch (NumberFormatException e) {
                    typeError.add(columnLocation(index + 1, 23, columnCodes));
                    continue;
                }
            }
            // 超額佣金率
            String commRateA = trim(columns[24]);
            if (isNotBlank(commRateA)) {
                try {
                    vo.setCOMMRATEA(new BigDecimal(commRateA).floatValue());
                } catch (NumberFormatException e) {
                    typeError.add(columnLocation(index + 1, 24, columnCodes));
                    continue;
                }
            }
            // 浮動佣金率
            String commRateC = trim(columns[25]);
            if (isNotBlank(commRateC)) {
                try {
                    vo.setCOMMRATEC(new BigDecimal(commRateC).floatValue());
                } catch (NumberFormatException e) {
                    typeError.add(columnLocation(index + 1, 25, columnCodes));
                    continue;
                }
            }
            // 其他表格的欄位值不維護
            // 26：續期佣金率：TBJSB_INS_PROD_BOUND.BOUNDRATE
            // 27：繳費年期狀態：TBJSB_INS_PROD_LIFEITEM.PRODUCTPUDTYPE
            // 28：繳費年期：TBJSB_INS_PROD_LIFEITEM.PRODUCTPUD
            // 29：保障期狀態：TBJSB_INS_PROD_LIFEITEM.PRODUCTEDTYPE
            // 30：保障年期：TBJSB_INS_PROD_LIFEITEM.PRODUCTED
            // 31：產品年期：TBJSB_INS_PROD_LIFEITEM.PRODUCTEXPDATE
            // 備註
            String memo = trim(columns[32]);
            if (isNotBlank(memo)) {
                if (memo.length() > 4000) {
                    typeError.add(columnLocation(index + 1, 32, columnCodes));
                    continue;
                } else {
                    vo.setMEMO(memo);
                }
            }
            // 轉換保單
            String chFlag = trim(columns[33]);
            if (isNotBlank(chFlag)) {
                if (chFlag.length() > 10) {
                    typeError.add(columnLocation(index + 1, 33, columnCodes));
                    continue;
                } else {
                    vo.setCH_FLAG(chFlag);
                }
            }
            // 證照檢核-壽險證照
            String aFlag = trim(columns[34]);
            if (isNotBlank(aFlag)) {
                if (aFlag.length() > 10) {
                    typeError.add(columnLocation(index + 1, 34, columnCodes));
                    continue;
                } else {
                    vo.setA_FLAG(aFlag);
                }
            }
            // 證照檢核-投資證照
            String bFlag = trim(columns[35]);
            if (isNotBlank(bFlag)) {
                if (bFlag.length() > 10) {
                    typeError.add(columnLocation(index + 1, 35, columnCodes));
                    continue;
                } else {
                    vo.setB_FLAG(bFlag);
                }
            }
            // 證照檢核-外幣商品證照
            String cFlag = trim(columns[36]);
            if (isNotBlank(cFlag)) {
                if (cFlag.length() > 10) {
                    typeError.add(columnLocation(index + 1, 36, columnCodes));
                    continue;
                } else {
                    vo.setB_FLAG(cFlag);
                }
            }

            // 如果為新增動作，為避免前面失敗，保險產品序號最後再取號
            if (notExists) {
                vo.setPRODUCTSERIALNUM(isBlank(serialNumCol)
                        ? getSerialNum() : new BigDecimal(serialNumCol));
                dam.create(vo);
            } else {
                dam.update(vo);
            }
        }

        PRD177OutputVO outputVO = new PRD177OutputVO();
        outputVO.setTypeError(typeError);
        outputVO.setRequiredError(requiredError);
        this.sendRtnObject(outputVO);
    }

    private BigDecimal getSerialNum() throws JBranchException {
        List<Map<String, BigDecimal>> result = Manager.manage(this.getDataAccessManager())
                .append("select MAX(PRODUCTSERIALNUM) + 1 SEQ from TBJSB_INS_PROD_MAIN ")
                .query();
        return result.get(0).get("SEQ");
    }

    public void delete(Object body, IPrimitiveMap header) throws JBranchException {
        PRD177InputVO inputVO = (PRD177InputVO) body;

        Manager.manage(this.getDataAccessManager())
                .append("delete TBJSB_INS_PROD_MAIN where PRODUCTSERIALNUM = :productSerialNum ")
                .put("productSerialNum", inputVO.getProductSerialNum())
                .update();
        Manager.manage(this.getDataAccessManager())
                .append("delete TBJSB_INS_PROD_LIFEITEM where PRODUCTSERIALNUM = :productSerialNum ")
                .put("productSerialNum", inputVO.getProductSerialNum())
                .update();
        Manager.manage(this.getDataAccessManager())
                .append("delete TBJSB_INS_PROD_BOUND where PRODUCTSERIALNUM = :productSerialNum ")
                .put("productSerialNum", inputVO.getProductSerialNum())
                .update();

        this.sendRtnObject(new PRD177OutputVO());
    }

    public void save(Object body, IPrimitiveMap header) throws JBranchException {
        PRD177DataVO inputVO = (PRD177DataVO) body;

        TBJSB_INS_PROD_MAINVO mainvo = (TBJSB_INS_PROD_MAINVO) this.getDataAccessManager()
                .findByPKey(TBJSB_INS_PROD_MAINVO.TABLE_UID, inputVO.getProductSerialNum());

        if (mainvo == null)
            throw new APException("ehl_01_common_017"); // 資料不存在

        mainvo.setPRODUCTID(inputVO.getProductId());
        mainvo.setPRODUCTNAME(inputVO.getProductName());
        mainvo.setINSURANCECOSERIALNUM(inputVO.getInsuranceCoSerialNum());
        mainvo.setCOMMRATE(floatVal(inputVO.getCommRate()));
        mainvo.setCOMMRATE2(floatVal(inputVO.getCommRate2()));
        mainvo.setPRODUCTCATEGORY(inputVO.getProductCategory());
        mainvo.setCONTRACTID(inputVO.getContractId());
        mainvo.setMOF(inputVO.getMof());
        if (inputVO.getProductValidFrom() != null)
            mainvo.setPRODUCTVALIDFROM(new Timestamp(inputVO.getProductValidFrom().getTime()));
        if (inputVO.getProductValidThru() != null)
            mainvo.setPRODUCTVALIDTHRU(new Timestamp(inputVO.getProductValidThru().getTime()));
        mainvo.setSUMASSUMEDTYPE(inputVO.getSumAssumedType());
        mainvo.setPREMIUMTABLE(inputVO.getPremiumTable());
        mainvo.setCONTRACTBONUS(inputVO.getContractBonus());
        mainvo.setCOMMRATEA(floatVal(inputVO.getCommRateA()));
        mainvo.setMEMO(inputVO.getMemo());
        mainvo.setCOMMRATE_2(floatVal(inputVO.getCommRate2()));
        mainvo.setCOMMRATEA_2(floatVal(inputVO.getCommRateA2()));
        mainvo.setCURRENCY1(inputVO.getCurrency1());
        mainvo.setA_FLAG(inputVO.getaFlag());
        mainvo.setB_FLAG(inputVO.getbFlag());
        mainvo.setC_FLAG(inputVO.getcFlag());
        mainvo.setPRODUCTTYPE1(inputVO.getProductType1());
        mainvo.setCOMMRATEC(floatVal(inputVO.getCommRateC()));
        mainvo.setCH_FLAG(inputVO.getChFlag());
        mainvo.setCOMM1_FLAG(inputVO.getComm1Flag());
        mainvo.setCOMM2_FLAG(inputVO.getComm2Flag());
        if (inputVO.getProductOnDt() != null)
            mainvo.setPRODUCTON_DT(new Timestamp(inputVO.getProductOnDt().getTime()));
        mainvo.setPRODUCTSHORTNAME(inputVO.getProductShortName());
        mainvo.setMAINSTAY_PRODUCT(inputVO.getMainstayProduct());
        mainvo.setADDSUB_FLAG(inputVO.getAddSubFlag());
        mainvo.setCHANNEL(inputVO.getChannel());
        mainvo.setCOMMRATEC_2(floatVal(inputVO.getCommRateC2()));
        mainvo.setPRODUCTRISK(inputVO.getProductRisk());
        mainvo.setRISKRATE(inputVO.getRiskRate());
        mainvo.setPOLICY_TYPE(inputVO.getPolicyType());

        for (ProdBoundDataVO vo : inputVO.getProdBoundGroup()) {
            TBJSB_INS_PROD_BOUNDPK boundpk = new TBJSB_INS_PROD_BOUNDPK(vo.getBoundItem(), vo.getBoundName(), vo.getChannel(), vo.getProductSerialNum());
            TBJSB_INS_PROD_BOUNDVO boundvo = (TBJSB_INS_PROD_BOUNDVO) ObjectUtils.defaultIfNull(this.getDataAccessManager()
                    .findByPKey(TBJSB_INS_PROD_BOUNDVO.TABLE_UID, boundpk), new TBJSB_INS_PROD_BOUNDVO());

            boundvo.setADD_1_START_MONTH(vo.getAdd1StartMonth());
            boundvo.setADD_1_END_MONTH(vo.getAdd1EndMonth());
            boundvo.setADD_1_COMMISSIONRATE(floatVal(vo.getAdd1CommissionRate()));
            boundvo.setADD_1_COMMRATEA(floatVal(vo.getAdd1CommRateA()));

            if (boundvo.getcomp_id() != null) { // 更新
                this.getDataAccessManager().update(boundvo);
            } else { // 新增
                boundvo.setcomp_id(boundpk);
                boundvo.setBOUNDRATE(new BigDecimal(0)); // 頁面無維護該值，所以塞預設值 0 進去
                this.getDataAccessManager().create(boundvo);
            }
        }
        this.sendRtnObject(this.getDataAccessManager().update(mainvo));
    }

    private Float floatVal(BigDecimal bigDecimal) {
        return bigDecimal == null ? 0 : bigDecimal.floatValue();
    }

    public void getProdCode(Object body, IPrimitiveMap header) throws JBranchException {
        ProdCodeInputVO inputVO = (ProdCodeInputVO) body;

        List<Map<String, Object>> result = Manager.manage(this.getDataAccessManager())
                .append("select * from TBJSB_INS_PROD_CODE_TABLE where CD_INDEX in (:cdIndexList) ")
                .put("cdIndexList", inputVO.getCdIndexList())
                .query();

        ProdCodeOutputVO outputVO = new ProdCodeOutputVO();
        outputVO.setList(result);
        this.sendRtnObject(outputVO);
    }
}
