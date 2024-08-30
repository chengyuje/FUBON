package com.systex.jbranch.platform.common.excel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jxl.Cell;
import jxl.CellFeatures;
import jxl.Range;
import jxl.Workbook;
import jxl.format.CellFormat;
import jxl.write.Blank;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PathUtil;

public class ExcelGenerator {

	private Map<String, List> dataSets = new HashMap<String, List>();
	private String ROW_TEMP = "_ROW_TEMP";
	private String templatePath = null;
	private String savePath = null;
	private String txnCode = null;
	
	/**
	 * 設置DataSet
	 * @param name 資料名稱
	 * @param dataSet 資料內容
	 */
	public void addDataSet(String name, List<Map<String, Object>> dataSet){
		dataSets.put(name, dataSet);
	}
	
	/**
	 * 取得範本路徑
	 * @return templetPath
	 */
	public String getTemplatePath() {
		return templatePath;
	}

	/**
	 * 指定範本路徑
	 * @param templatePath
	 */
	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}
	
	/**
	 * @param txnCode
	 * @param reportID
	 * @throws JBranchException
	 */
	public void setTemplate(String txnCode, String reportID) throws JBranchException{
		this.txnCode = txnCode;
		this.templatePath = PathUtil.getReportPath(txnCode, reportID, "xls");
	}

	/**
	 * 取得儲存路徑
	 * @return savePath
	 */
	public String getSavePath() {
		return savePath;
	}

	/**
	 * 指定儲存路徑
	 * @param savePath
	 */
	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	/**
	 * 產生excel檔至指定的路徑
	 * @param templatePath 範本路徑
	 * @param savePath excel儲存路徑
	 * @throws JBranchException 產生excel失敗時丟出
	 */
	public void generateToExcel() throws JBranchException{
		
		if(savePath == null){
			savePath = PathUtil.getSavePath(txnCode);
		}
		
		Workbook srcWB = null;
		WritableWorkbook desWW = null;
		try{
			srcWB = Workbook.getWorkbook(new FileInputStream(templatePath));
			desWW = Workbook.createWorkbook(new FileOutputStream(savePath), srcWB);
			fillDataSet(desWW);
		}catch(Exception e){
			throw new JBranchException(e.getMessage(), e);
		}finally{
			if(desWW != null){
				try {
					desWW.write();
					desWW.close();
				} catch (Exception e) {
					//ignore
				}
				try {
					desWW.close();
				} catch (Exception e) {
					//ignore
				}
			}
			if(srcWB != null){
				try {
					srcWB.close();
				} catch (Exception e) {
					//ignore
				}
			}
		}	
	}

	private void fillDataSet(WritableWorkbook desWW) throws Exception {
		
		WritableSheet[] sheets = desWW.getSheets();
		for (int i = 0; i < sheets.length; i++) {
			WritableSheet sheet = sheets[i];
			fillSheet(sheet);
		}
	}
	
	private void fillSheet(WritableSheet sheet) throws Exception{
		int rowCount = sheet.getRows();
		int columnCount = sheet.getColumns();
		Map<String, DataSetDetail> dsMap = new LinkedHashMap<String, DataSetDetail>();

		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < columnCount; j++) {
				WritableCell cell = sheet.getWritableCell(j, i);
				
				if(isVarable(cell)){
					NameAndColumn nc = getDataSetDetail(cell);
					String name = nc.getName();
					String column = nc.getColumn();
					DataSetDetail ds = dsMap.get(name);
					
					if(ds == null){
						if(column.equals(ROW_TEMP)){
							continue;
						}
						ds = new DataSetDetail();
						ds.setName(name);
						dsMap.put(name, ds);
					}
					if(column.equals(ROW_TEMP)){
						ds.setTempRowCell(cell);
						continue;
					}
					ds.appendVarableCell(cell);
				}
			}
		}

		Iterator<String> it = dsMap.keySet().iterator();

		while(it.hasNext()){
			String key = it.next();
			DataSetDetail ds = dsMap.get(key);

			List<Map<String, Object>> list = dataSets.get(ds.getName());
			if(list != null){
				RowAndColumn rc = ds.getRowAndColumnCount();
				int varRowCount = rc.getRow();

				Range[] ranges = sheet.getMergedCells();
				
				if(list.size() == 0){
					List<WritableCell> cells = ds.getVarableCells();
					for (int i = 0; i < cells.size(); i++) {
						Cell cell = cells.get(i);
						Blank blank = new Blank(cell.getColumn(), cell.getRow());
						copyStyle(cell, blank);
						sheet.addCell(blank);
					}
				}

				int count = 0;
				for (int i = 0; i < list.size(); i++) {

					if(i < list.size() - 1){
						for (int j = 0; j < varRowCount; j++) {
							sheet.insertRow(ds.getEnd().getY() + 1 + i);
						}
					}

					Map<String, Object> data = list.get(i);
					List<WritableCell> varables = ds.getVarableCells(); 
					for (int j = 0; j < varables.size(); j++) {

						WritableCell cell = varables.get(j);
						Cell endCell = getEndCell(ranges, cell);
						if(endCell != null && i != 0){
							sheet.mergeCells(cell.getColumn(), cell.getRow() + count, endCell.getColumn(), endCell.getRow() + count);
						}
						if(isVarable(cell)){
							String col = getDataSetDetail(cell).getColumn();
							Object o = data.get(col);
							if(o == null){
								continue;
							}
							WritableCell wc = getWritableCell(o, cell.getColumn(), cell.getRow() + count);
							copyStyle(cell, wc);
							try {
								sheet.addCell(wc);
							} catch (Exception e) {
								throw new Exception("第" + (i + 1) + "筆資料" + cell.getContents() + ", 加入失敗, 內容為[" + o + "]");
							}
						}else{
							sheet.addCell(cell.copyTo(cell.getColumn(), cell.getRow() + count));
						}
					}
					count += varRowCount;
				}
				
				if(ds.getTempRowCell() != null){
					sheet.removeRow(ds.getTempRowCell().getRow());
				}
			}
		}
	}
	
	private Cell getEndCell(Range[] ranges, Cell cell){
		Cell bottomRightCell = null;
		for (int i = 0; i < ranges.length; i++) {
			Cell topLeftCell = ranges[i].getTopLeft();
			if(cell.getRow() == topLeftCell.getRow() && cell.getColumn() == topLeftCell.getColumn()){
				bottomRightCell = ranges[i].getBottomRight();
			}
		}
		
		return bottomRightCell;
	}
	
	private WritableCell getWritableCell(Object o, int col, int row){
		if(o == null){
			return null;
		}
		WritableCell cell = null;
		String content = o.toString();
		if(o instanceof Number){
			cell = new jxl.write.Number(col, row, Double.parseDouble(content)); 
		}else if(o instanceof Date){
			cell = new DateTime(col, row, (Date) o);
		}else if(o instanceof Formula){
			cell = new jxl.write.Formula(col, row, content);
		}else{
			cell = new Label(col, row, content);
		}
		return cell;
	}

	private void copyStyle(Cell cell, WritableCell wc){
		CellFeatures cf = cell.getCellFeatures();
		CellFormat cfor = cell.getCellFormat();
		if(cfor != null){
			wc.setCellFormat(new WritableCellFormat(cfor));
		}
		if(cf != null){
			wc.setCellFeatures(new WritableCellFeatures(cf));
		}
	}
	
	private boolean isVarable(Cell cell){
		
		return cell.getContents().matches("\\$\\{.+\\..+\\}");
	}
	
	private NameAndColumn getDataSetDetail(Cell cell){
		
		String content = cell.getContents();
		NameAndColumn nc = new NameAndColumn();
		int index = content.indexOf(".");
		String name = content.substring(2, index);
		String column = content.substring(index + 1, content.length() - 1);
		nc.setColumn(column);
		nc.setName(name);
		return nc;
	}
}
