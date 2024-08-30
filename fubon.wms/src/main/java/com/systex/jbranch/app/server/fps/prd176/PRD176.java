package com.systex.jbranch.app.server.fps.prd176;

import com.systex.jbranch.app.common.fps.table.TBJSB_INS_PROD_COMPANYVO;
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
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.*;

/**
 * PRD176 保險公司設定
 *
 * @author Eli
 * @date 2022/06/17
 */
@Component("prd176")
@Scope("request")
@SuppressWarnings({"unchecked"})
public class PRD176 extends FubonWmsBizLogic {
    private final String columnCodes = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
        PRD176InputVO inputVO = (PRD176InputVO) body;

        PRD176OutputVO outputVO = new PRD176OutputVO();
        outputVO.setResultList(getResult(inputVO));
        this.sendRtnObject(outputVO);
    }

    private List<TBJSB_INS_PROD_COMPANYVO> getResult(PRD176InputVO inputVO) throws JBranchException {
        List<Criterion> criteria = new LinkedList<>();
        if (StringUtils.isNotBlank(inputVO.getName()))
            criteria.add(Restrictions.like("CNAME", inputVO.getName(), MatchMode.ANYWHERE));
        if (null != inputVO.getContractSDate())
            criteria.add(Restrictions.ge("CONTRACTDATE", inputVO.getContractSDate()));
        if (null != inputVO.getContractEDate())
            criteria.add(Restrictions.le("CONTRACTDATE", inputVO.getContractEDate()));
        if (null != inputVO.getRenewSDate())
            criteria.add(Restrictions.ge("RENEWDATE", inputVO.getRenewSDate()));
        if (null != inputVO.getRenewEDate())
            criteria.add(Restrictions.le("RENEWDATE", inputVO.getRenewSDate()));

        return (List<TBJSB_INS_PROD_COMPANYVO>) this.getDataAccessManager()
                .findByCriteria(TBJSB_INS_PROD_COMPANYVO.TABLE_UID, criteria);
    }

    public void exportCSV(Object body, IPrimitiveMap header) throws JBranchException {
        PRD176InputVO inputVO = (PRD176InputVO) body;

        CSVUtil csv = new CSVUtil();
        csv.setHeader(getCsvHeader().toArray(new String[0]));

        if (!inputVO.isSampleExport()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

            List<Object[]> csvRecordList = new LinkedList<>();
            for (TBJSB_INS_PROD_COMPANYVO vo : getResult(inputVO)) {
                List<String> rowData = new LinkedList<>();
                rowData.add(vo.getSERIALNUM().toString());      // 保險公司序號
                rowData.add(vo.getCNAME());                     // 保險公司中文名稱
                rowData.add(vo.getSHORTNAME());                 // 保險公司簡稱
                rowData.add(vo.getENAME());                     // 保險公司英文名稱
                rowData.add(vo.getCOM_CODE());                  // 保險公司英文簡碼
                rowData.add(vo.getZIPCODE());                   // 保險總公司地址郵遞區碼
                rowData.add(vo.getADDRESS());                   // 地址
                rowData.add(vo.getCONTACTPERSONNAME());         // 業務聯絡人姓名
                rowData.add(vo.getCONTACTPERSONPHONE());        // 聯絡人電話
                rowData.add(vo.getIDNUM());                     // 統一編號
                rowData.add(vo.getCONTACTPERSONEMAIL());        // 聯絡人e-mail
                rowData.add(vo.getADMCONTACTPERSONNAME());      // 行政聯絡人姓名
                rowData.add(vo.getADMCONTACTPERSONPHONE());     // 行政聯絡人電話
                rowData.add(vo.getADMCONTACTPERSONEMAIL());     // 行政聯絡人e-mail
                rowData.add(vo.getINDCONTACTPERSONNAME());      // 理賠聯絡人姓名
                rowData.add(vo.getINDCONTACTPERSONPHONE());     // 理賠聯絡人電話
                rowData.add(vo.getINDCONTACTPERSONEMAIL());     // 理賠聯絡人e-mail
                if (null != vo.getCONTRACTDATE())
                    rowData.add(sdf.format(vo.getCONTRACTDATE()));  // 簽約日期
                if (null != vo.getRENEWDATE())
                    rowData.add(sdf.format(vo.getRENEWDATE()));  // 續約日期
                if (null != vo.getTERMDATE())
                    rowData.add(sdf.format(vo.getTERMDATE()));  // 終止合約日期
                csvRecordList.add(rowData.toArray(new String[0]));
            }
            csv.addRecordList(csvRecordList);
        }
        notifyClientToDownloadFile(csv.generateCSV(), "保險公司設定.csv");
    }

    private List<String> getCsvHeader() {
        List<String> csvHeader = new LinkedList<>();
        csvHeader.add("保險公司序號");
        csvHeader.add("保險公司中文名稱");
        csvHeader.add("保險公司簡稱");
        csvHeader.add("保險公司英文名稱");
        csvHeader.add("保險公司英文簡碼");
        csvHeader.add("保險總公司地址郵遞區碼");
        csvHeader.add("地址");
        csvHeader.add("業務聯絡人姓名");
        csvHeader.add("聯絡人電話");
        csvHeader.add("統一編號");
        csvHeader.add("聯絡人e-mail");
        csvHeader.add("行政聯絡人姓名");
        csvHeader.add("行政聯絡人電話");
        csvHeader.add("行政聯絡人e-mail");
        csvHeader.add("理賠聯絡人姓名");
        csvHeader.add("理賠聯絡人電話");
        csvHeader.add("理賠聯絡人e-mail");
        csvHeader.add("簽約日期");
        csvHeader.add("續約日期");
        csvHeader.add("終止合約日期");
        return csvHeader;
    }

    private String location(int rowIndex, int columnIndex) {
        return rowIndex + "-" + columnCodes.charAt(columnIndex);
    }

    public void upload(Object body, IPrimitiveMap header) throws JBranchException {
        PRD176InputVO inputVO = (PRD176InputVO) body;

        String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
        List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());
        List<String> csvHeader = getCsvHeader();
        if (dataCsv.isEmpty() ||
                // Header 必須一致
                !Arrays.equals(dataCsv.get(0), csvHeader.toArray(new String[0]))) {
            throw new APException("上傳格式錯誤，請下載範例檔案");
        }

        if (dataCsv.size() == 1) {
            throw new APException("上傳檔案僅有 header，無包含資料");
        }

        List<String> typeError = new LinkedList<>(); // 數值不符合資料庫欄位型態
        List<String> requiredError = new LinkedList<>(); // 若為新增，缺少必要欄位
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        DataAccessManager dam = this.getDataAccessManager();
        for (int index = 1; index < dataCsv.size(); index++) {
            // 以 header size 建立新的 string array，避免上傳欄位空值導致 OutOfIndexBound
            String[] columns = Arrays.copyOf(dataCsv.get(index), csvHeader.size());
            int columnIndex = 0;
            // 保險公司序號
            String serialNumCol = trim(columns[columnIndex]);
            TBJSB_INS_PROD_COMPANYVO vo = null;
            boolean notExists = isBlank(serialNumCol)
                    || (vo = (TBJSB_INS_PROD_COMPANYVO) dam.findByPKey(
                    TBJSB_INS_PROD_COMPANYVO.TABLE_UID, new BigDecimal(serialNumCol))) == null;
            vo = (TBJSB_INS_PROD_COMPANYVO) ObjectUtils.defaultIfNull(vo, new TBJSB_INS_PROD_COMPANYVO());

            // 保險公司中文名稱
            String cName = trim(columns[++columnIndex]);
            if (isNotBlank(cName)) {
                if (cName.length() > 50) {
                    typeError.add(location(index + 1, columnIndex));
                    continue;
                } else {
                    vo.setCNAME(cName);
                }
            } else {
                requiredError.add(location(index + 1, columnIndex));
                continue;
            }

            // 保險公司簡稱
            String shortName = trim(columns[++columnIndex]);
            if (isNotBlank(shortName)) {
                if (shortName.length() > 50) {
                    typeError.add(location(index + 1, columnIndex));
                    continue;
                } else {
                    vo.setSHORTNAME(shortName);
                }
            } else {
                requiredError.add(location(index + 1, columnIndex));
                continue;
            }
            // 保險公司英文名稱
            String eName = trim(columns[++columnIndex]);
            if (isNotBlank(eName)) {
                if (eName.length() > 50) {
                    typeError.add(location(index + 1, columnIndex));
                    continue;
                } else {
                    vo.setENAME(eName);
                }
            }
            // 保險公司英文簡碼
            String comCod = trim(columns[++columnIndex]);
            if (isNotBlank(comCod)) {
                if (utf_8_length(comCod) > 5) {
                    typeError.add(location(index + 1, columnIndex));
                    continue;
                } else {
                    vo.setCOM_CODE(comCod);
                }
            }
            // 保險總公司地址郵遞區碼
            String zipCode = trim(columns[++columnIndex]);
            if (isNotBlank(zipCode)) {
                if (zipCode.length() > 3) {
                    typeError.add(location(index + 1, columnIndex));
                    continue;
                } else {
                    vo.setZIPCODE(zipCode);
                }
            } else {
                requiredError.add(location(index + 1, columnIndex));
                continue;
            }
            // 地址
            String address = trim(columns[++columnIndex]);
            if (isNotBlank(address)) {
                if (address.length() > 100) {
                    typeError.add(location(index + 1, columnIndex));
                    continue;
                } else {
                    vo.setADDRESS(address);
                }
            } else {
                requiredError.add(location(index + 1, columnIndex));
                continue;
            }
            // 業務聯絡人姓名
            String contactPersonName = trim(columns[++columnIndex]);
            if (isNotBlank(contactPersonName)) {
                if (contactPersonName.length() > 50) {
                    typeError.add(location(index + 1, columnIndex));
                    continue;
                } else {
                    vo.setCONTACTPERSONNAME(contactPersonName);
                }
            } else {
                requiredError.add(location(index + 1, columnIndex));
                continue;
            }

            // 聯絡人電話
            String contactPersonPhone = trim(columns[++columnIndex]);
            if (isNotBlank(contactPersonPhone)) {
                if (contactPersonPhone.length() > 20) {
                    typeError.add(location(index + 1, columnIndex));
                    continue;
                } else {
                    vo.setCONTACTPERSONPHONE(contactPersonPhone);
                }
            } else {
                requiredError.add(location(index + 1, columnIndex));
                continue;
            }
            // 統一編號
            String idNum = trim(columns[++columnIndex]);
            if (isNotBlank(idNum)) {
                if (idNum.length() > 20) {
                    typeError.add(location(index + 1, columnIndex));
                    continue;
                } else {
                    vo.setIDNUM(idNum);
                }
            } else {
                requiredError.add(location(index + 1, columnIndex));
                continue;
            }
            // 聯絡人e-mail
            String contactPersonEmail = trim(columns[++columnIndex]);
            if (isNotBlank(contactPersonEmail)) {
                if (contactPersonEmail.length() > 50) {
                    typeError.add(location(index + 1, columnIndex));
                    continue;
                } else {
                    vo.setCONTACTPERSONEMAIL(contactPersonEmail);
                }
            } else {
                requiredError.add(location(index + 1, columnIndex));
                continue;
            }
            // 行政聯絡人姓名
            String admContactPersonName = trim(columns[++columnIndex]);
            if (isNotBlank(admContactPersonName)) {
                if (admContactPersonName.length() > 20) {
                    typeError.add(location(index + 1, columnIndex));
                    continue;
                } else {
                    vo.setADMCONTACTPERSONNAME(admContactPersonName);
                }
            }
            // 行政聯絡人電話
            String admContactPersonPhone = trim(columns[++columnIndex]);
            if (isNotBlank(admContactPersonPhone)) {
                if (admContactPersonPhone.length() > 20) {
                    typeError.add(location(index + 1, columnIndex));
                    continue;
                } else {
                    vo.setADMCONTACTPERSONPHONE(admContactPersonPhone);
                }
            }
            // 行政聯絡人e-mail
            String admContactPersonEmail = trim(columns[++columnIndex]);
            if (isNotBlank(admContactPersonEmail)) {
                if (admContactPersonEmail.length() > 50) {
                    typeError.add(location(index + 1, columnIndex));
                    continue;
                } else {
                    vo.setADMCONTACTPERSONEMAIL(admContactPersonEmail);
                }
            }
            // 理賠聯絡人姓名
            String indContactPersonName = trim(columns[++columnIndex]);
            if (isNotBlank(indContactPersonName)) {
                if (indContactPersonName.length() > 20) {
                    typeError.add(location(index + 1, columnIndex));
                    continue;
                } else {
                    vo.setINDCONTACTPERSONNAME(indContactPersonName);
                }
            }
            // 理賠聯絡人電話
            String indContactPersonPhone = trim(columns[++columnIndex]);
            if (isNotBlank(indContactPersonPhone)) {
                if (indContactPersonPhone.length() > 20) {
                    typeError.add(location(index + 1, columnIndex));
                    continue;
                } else {
                    vo.setINDCONTACTPERSONPHONE(indContactPersonPhone);
                }
            }
            // 理賠聯絡人e-mail
            String indContactPersonEmail = trim(columns[++columnIndex]);
            if (isNotBlank(indContactPersonEmail)) {
                if (indContactPersonEmail.length() > 50) {
                    typeError.add(location(index + 1, columnIndex));
                    continue;
                } else {
                    vo.setINDCONTACTPERSONEMAIL(indContactPersonEmail);
                }
            }
            // 簽約日期
            String contractDate = trim(columns[++columnIndex]);
            if (isNotBlank(contractDate)) {
                try {
                    vo.setCONTRACTDATE(new Timestamp(sdf.parse(contractDate).getTime()));
                } catch (ParseException e) {
                    typeError.add(location(index + 1, columnIndex));
                    continue;
                }
            } else {
                requiredError.add(location(index + 1, columnIndex));
                continue;
            }
            // 續約日期
            String renewDate = trim(columns[++columnIndex]);
            if (isNotBlank(renewDate)) {
                try {
                    vo.setRENEWDATE(new Timestamp(sdf.parse(renewDate).getTime()));
                } catch (ParseException e) {
                    typeError.add(location(index + 1, columnIndex));
                    continue;
                }
            }
            // 終止合約日期
            String termDate = trim(columns[++columnIndex]);
            if (isNotBlank(termDate)) {
                try {
                    vo.setTERMDATE(new Timestamp(sdf.parse(termDate).getTime()));
                } catch (ParseException e) {
                    typeError.add(location(index + 1, columnIndex));
                    continue;
                }
            }

            // 如果為新增動作，為避免前面失敗，保險公司序號最後再取號
            if (notExists) {
                vo.setSERIALNUM(isBlank(serialNumCol)
                        ? getSerialNum() : new BigDecimal(serialNumCol));
                dam.create(vo);
            } else {
                dam.update(vo);
            }
        }

        PRD176OutputVO outputVO = new PRD176OutputVO();
        outputVO.setTypeError(typeError);
        outputVO.setRequiredError(requiredError);
        this.sendRtnObject(outputVO);
    }

    private BigDecimal getSerialNum() throws JBranchException {
        List<Map<String, BigDecimal>> result = Manager.manage(this.getDataAccessManager())
                .append("select MAX(SERIALNUM) + 1 SEQ from TBJSB_INS_PROD_COMPANY ")
                .query();
        return result.get(0).get("SEQ");
    }

    public void delete(Object body, IPrimitiveMap header) throws JBranchException {
        PRD176InputVO inputVO = (PRD176InputVO) body;

        TBJSB_INS_PROD_COMPANYVO vo = (TBJSB_INS_PROD_COMPANYVO) this.getDataAccessManager().findByPKey(
                TBJSB_INS_PROD_COMPANYVO.TABLE_UID, inputVO.getSerialNum());
        if (vo != null)
            this.getDataAccessManager().delete(vo);
        else
            throw new APException("ehl_01_common_017"); // 資料不存在
        this.sendRtnObject(null);
    }

    public void save(Object body, IPrimitiveMap header) throws JBranchException {
        PRD176DataVO inputVO = (PRD176DataVO) body;

        TBJSB_INS_PROD_COMPANYVO vo = (TBJSB_INS_PROD_COMPANYVO) this.getDataAccessManager()
                .findByPKey(TBJSB_INS_PROD_COMPANYVO.TABLE_UID, inputVO.getSerialNum());

        if (vo == null)
            throw new APException("ehl_01_common_017"); // 資料不存在

        vo.setCNAME(inputVO.getCname());
        vo.setSHORTNAME(inputVO.getShortname());
        vo.setENAME(inputVO.getEname());
        vo.setZIPCODE(inputVO.getZipcode());
        vo.setADDRESS(inputVO.getAddress());
        vo.setCONTACTPERSONNAME(inputVO.getContactPersonName());
        vo.setCONTACTPERSONPHONE(inputVO.getContactPersonPhone());
        vo.setIDNUM(inputVO.getIdNum());
        vo.setCONTACTPERSONEMAIL(inputVO.getContactPersonEmail());
        vo.setADMCONTACTPERSONNAME(inputVO.getAdmContactPersonName());
        vo.setADMCONTACTPERSONPHONE(inputVO.getAdmContactPersonPhone());
        vo.setADMCONTACTPERSONEMAIL(inputVO.getAdmContactPersonEmail());
        vo.setINDCONTACTPERSONNAME(inputVO.getIndContactPersonName());
        vo.setINDCONTACTPERSONPHONE(inputVO.getIndContactPersonPhone());
        vo.setINDCONTACTPERSONEMAIL(inputVO.getIndContactPersonEmail());
        if (inputVO.getContractDate() != null)
            vo.setCONTRACTDATE(new Timestamp(inputVO.getContractDate().getTime()));
        if (inputVO.getRenewDate() != null)
            vo.setRENEWDATE(new Timestamp(inputVO.getRenewDate().getTime()));
        if (inputVO.getTermDate() != null)
            vo.setTERMDATE(new Timestamp(inputVO.getTermDate().getTime()));

        this.sendRtnObject(this.getDataAccessManager().update(vo));
    }
}
