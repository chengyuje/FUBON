package com.systex.jbranch.app.server.fps.crmesbtest;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.crm451.CRM451;
import com.systex.jbranch.app.server.fps.exportfile.EXPORTFILE;
import com.systex.jbranch.app.server.fps.sot709.SOT709;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.jlb.FmpJRunTx;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author walalala
 * @date 2016/10/19
 * 
 */
@Component("crmesbtest")
@Scope("request")
public class CRMESBTEST extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRMESBTEST.class);
	
	public void esbtest(Object body, IPrimitiveMap header) throws Exception {
		CRMESBTESTOutputVO return_VO = new CRMESBTESTOutputVO();
		dam = this.getDataAccessManager();
		
		// get url
		String ESB_URL = "";
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer("select PARAM_NAME from TBSYSPARAMETER where PARAM_TYPE = 'ESB.URL' ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0) {
			ESB_URL = list.get(0).get("PARAM_NAME").toString();
		}
		// get id
		String ESB_ID = "";
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer("select PARAM_NAME from TBSYSPARAMETER where PARAM_TYPE = 'ESB.ID' ");
		queryCondition.setQueryString(sql.toString());
		list = dam.exeQuery(queryCondition);
		if (list.size() > 0) {
			ESB_ID = list.get(0).get("PARAM_NAME").toString();
		}
//		List<Object> total = new ArrayList<Object>();
		FmpJRunTx inst = null;
		String outbound_hretrn = ""; //是否還有下頁資料 C:還有資料  E:已無資料
		Document xmlDoc = new Document();
		boolean doSend = true; // while
		List<Object> total = new ArrayList<Object>();
		String esbSeq = getEsbSeq(); // 不重複流水號
		while (doSend) {
			// 上送電文
			inst = new FmpJRunTx(ESB_URL, "FC032671");
			inst.setInboundField("TxHead.HWSID", ESB_ID);
			inst.setInboundField("TxHead.HSTANO", esbSeq);
			inst.setInboundField("TxHead.HTLID", "");
			inst.setInboundField("TxHead.HTXTID", "FC032671");
			inst.setInboundField("TxHead.HRETRN", outbound_hretrn);
			inst.setInboundField("TxHead.PAGEFLG", "3");

			inst.setInboundField("TxBody.DATA_IND", "1");
			inst.setInboundField("TxBody.INP_DATA", "C200481206");
			inst.setInboundField("TxBody.ENQ_IND", "1");
			inst.setInboundField("TxBody.FUNC", "0");

            inst.run(ESB_ID,ESB_ID);
            // 下行電文
            String fcOutboundXML = inst.getOutboundXMLString();   //ESB回傳電文內容
            SAXBuilder sb = new SAXBuilder();
            xmlDoc = sb.build(new StringReader(fcOutboundXML));
            
            // 解析xml
            Element rootElement = xmlDoc.getRootElement();
            Element txHeadElement = rootElement.getChild("TxHead");
            Element txBodyElement = rootElement.getChild("TxBody");
        	
            outbound_hretrn = txHeadElement.getChild("HRETRN").getText(); //是否還有下頁資料 C:還有資料  E:已無資料
            String outbound_herrid = txHeadElement.getChild("HERRID").getText().trim();
            if ("0000".equals(outbound_herrid)) { //0000代表交易成功
            	total.addAll(txBodyElement.getChildren());
            } else {
            	String errorEmsgid = txBodyElement.getChild("EMSGID").getText().trim();
            	logger.error("TP032675 ESB ERROR");
                logger.error("HERRID:"+outbound_herrid);
                logger.error("EMSGID:"+errorEmsgid); // ERROR時xml！
            }
            // 判斷是否需要再上送
            doSend = "C".equals(outbound_hretrn);
		}
		
		// to List<Map<String, String>> 
		List<Map<String, String>> XDDD = new ArrayList<Map<String, String>>();
		// to List<Map<String, String>>
        List<Map> data = new ArrayList<>();
        Map<String, String> pa = new HashMap<>();   //Parent el
        List txRepeats = new ArrayList();
		
		for(Object repO : total){
			Map<String, String> ch = new HashMap<>();   //Children el
			Element rep = (Element) repO;
			//取TxRepeat的明細資料
            for(Object childO : rep.getChildren()) {
                Element child = (Element) childO;
                ch.put(child.getName(), child.getText());
            }

            //put ch el into pa el
            if(MapUtils.isNotEmpty(ch)){
                txRepeats.add(ch);
            }

            //若為TxBody欄位直接加入ch內
            if(rep.getChildren().size() == 0){
                pa.put(rep.getName(), rep.getValue());
            }
		}
		if(CollectionUtils.isNotEmpty(txRepeats)) XDDD.addAll(txRepeats);
		XDDD.add(pa);

		exportxlsx(XDDD);

	}

	public void exportxlsx(List<Map<String, String>> list) throws JBranchException, IOException{
		
		List<Collection<String>> data = new ArrayList<>();
		for(int i = 0; i < list.size(); i++){
			data.add(list.get(i).values());
		}
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Sample sheet");
		CellStyle cellStyle = workbook.createCellStyle();
		CreationHelper createHelper = workbook.getCreationHelper();
		cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy/MM/dd h:mm:ss"));
		
		Row row_head = sheet.createRow(0);
		int cell_head_num = 0;
		for (String str : list.get(0).keySet()) {
			Cell cell_head = row_head.createCell(cell_head_num++);
			cell_head.setCellValue(str);
		}
		
		int rownum = 1;
		for (Collection<String> strs : data) {
			Row row = sheet.createRow(rownum++);
			int cellnum = 0;
			for (Object obj : strs) {
				Cell cell = row.createCell(cellnum++);
				cell.setCellValue((String)obj);
			}
		}

		//autoSizeColumn
		for (int i = 0; i < sheet.getRow(0).getPhysicalNumberOfCells(); i++) {
			sheet.autoSizeColumn(i);
		}
		
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String fileName = "匯出檔案資料.xlsx";
		String filePath = Path + fileName;
		File targetFile = new File(Path, fileName);
		FileOutputStream fos = new FileOutputStream(targetFile);

		workbook.write(new FileOutputStream(filePath));
		notifyClientToDownloadFile("temp//"+fileName, fileName);
  	 	fos.close();
		workbook.close();
	}
	
	//轉Decimal
	public BigDecimal getBigDecimal( Object value ) throws JBranchException {
        BigDecimal ret = null;
        if( value != null ) {
            if( value instanceof BigDecimal ) {
                ret = (BigDecimal) value;
            } else if( value instanceof String ) {
                ret = new BigDecimal( (String) value );
            } else if( value instanceof BigInteger ) {
                ret = new BigDecimal( (BigInteger) value );
            } else if( value instanceof Number ) {
                ret = new BigDecimal( ((Number)value).doubleValue() );
            } else {
                throw new ClassCastException("Not possible to coerce ["+value+"] from class "+value.getClass()+" into a BigDecimal.");
            }
        }
        return ret;
    }
	
	public void crmtest(Object body, IPrimitiveMap header) throws Exception {
		
		CRM451 crm451 = (CRM451) PlatformContext.getBean("crm451");
		
//		crm451.pushAuthMessage();
		
		this.sendRtnObject(null);
	}
	
}