package com.systex.jbranch.fubon.commons;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.reportdata.ConfigAdapterIF;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.common.util.ThreadDataPool;

/**
 * @author 1500617
 * @date 22/6/2016
 */
public class ExcelUtil {

	private UUID uuid;
	private String reportTemp;
    private String serverPath;
    private String fileName;
    private String path;
    private boolean pathflag = true;

	private XSSFWorkbook xssfWB;

	public void setReportTemp(String reportTemp) {
        this.reportTemp = reportTemp;
    }

    public void setServerPath(String serverPath) {
        this.serverPath = serverPath;
    }

	public void setXssfWB(XSSFWorkbook xssfWB) {
		this.xssfWB = xssfWB;
	}

	public ExcelUtil() throws JBranchException {
		uuid = (UUID) ThreadDataPool.getData(ThreadDataPool.KEY_UUID);
        ConfigAdapterIF config = (ConfigAdapterIF) PlatformContext.getBean("configAdapter");
        this.setReportTemp(config.getReportTemp());
        this.setServerPath(config.getServerHome());

        xssfWB = new XSSFWorkbook();
	}

	public List<List<Object>> readSheet(int i) {
		XSSFSheet sheet = xssfWB.getSheetAt(i);
		Iterator<Row> itr = sheet.iterator();
		List<List<Object>> dataList = new ArrayList<List<Object>>();

		// Iterating over Excel file in Java
		while (itr.hasNext()) {
			Row row = itr.next();
			List<Object> rowList = new ArrayList<Object>();

			// Iterating over each column of Excel file
			Iterator<Cell> cellIterator = row.cellIterator();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				switch (cell.getCellType()) {
				case Cell.CELL_TYPE_NUMERIC: // 数字
					rowList.add(Math.round(cell.getNumericCellValue()));
                    break;
                case Cell.CELL_TYPE_STRING: // 字符串
                	rowList.add(cell.getStringCellValue());
                    break;
                case Cell.CELL_TYPE_BOOLEAN: // Boolean
                	rowList.add(cell.getBooleanCellValue());
                    break;
                case Cell.CELL_TYPE_FORMULA: // 公式
                	rowList.add(cell.getCellFormula());
                    break;
                case Cell.CELL_TYPE_BLANK: // 空值
                    rowList.add("");
                    break;
                case Cell.CELL_TYPE_ERROR: // 故障
                	rowList.add("ERROR");
                    break;
                default:
                	rowList.add("UNKNOWN");
                    break;
                }
			}

			dataList.add(rowList);
		}

		return dataList;
	}

	public void setSheetData(String name, List<Map<String, Object>> data, List<String> order) {
		XSSFSheet sheet = xssfWB.createSheet(name);

		Row row_head = sheet.createRow(0);
		int cell_head_num = 0;
		for (String str : order) {
			Cell cell_head = row_head.createCell(cell_head_num++);
			cell_head.setCellValue(str);
		}

		int rownum = sheet.getLastRowNum();
		for (Map<String, Object> objArr : data) {
			Row row = sheet.createRow(++rownum);

			for (Entry<String, Object> en : objArr.entrySet()) {
				Object obj  = en.getValue();
				int idx = order.indexOf(en.getKey());
				Cell cell = row.createCell(idx);

				if (obj instanceof String) {
					cell.setCellValue((String) obj);
				} else if (obj instanceof Boolean) {
					cell.setCellValue((Boolean) obj);
				} else if (obj instanceof Date) {
					cell.setCellValue((Date) obj);
				} else if (obj instanceof Double) {
					cell.setCellValue((Double) obj);
				} else if(obj instanceof Number) {
					cell.setCellValue(((BigDecimal)obj).doubleValue());
				}
			}
		}
	}

	public String genExcel() throws JBranchException {
		String returnUrl = null;
		FileOutputStream os = null;

		try {
			// 設定存檔路徑
			if (fileName == null) {
				setFileName(getSaveName(), "xlsx");
			}
			if (path == null) {
				this.path = serverPath + reportTemp;
			}
			// 設定回傳URL
			if (pathflag) {
				returnUrl = reportTemp.substring(1) + fileName;
			} else {
				returnUrl = path + fileName;
			}

			File f = new File(path + fileName);
			File parentDir = f.getParentFile();
			if (!parentDir.exists()) {
				parentDir.mkdirs();
			}

			os = new FileOutputStream(f);
			xssfWB.write(os);
		} catch (Exception e) {
			String msg = StringUtil.getStackTraceAsString(e);
			throw new JBranchException("Generate Excel error:" + msg);
		} finally {
			try {
				if (os != null)
					os.close();
			} catch (Exception e) {
				// ignore
			}
			try {
				if (xssfWB != null)
					xssfWB.close();
			} catch (Exception e) {
				// ignore
			}
		}

		return returnUrl;
	}

	public void setFileName(String fileName, String extension) {
        this.fileName = fileName + "." + extension;
    }

	private String getSaveName() {
        //naming rule:交易代號_報表代號_uuid_時間(到微秒).xxx
        String time = getCurrentTime("yyyyMMddhhmmssSSS");
        String savePDFName = uuid + "_" + time;

        return savePDFName;
    }

	private String getCurrentTime(String format) {
        SimpleDateFormat sdFormat = new SimpleDateFormat(format);
        Date currentTime = new Date();
        String time = sdFormat.format(currentTime);
        return time;
    }

	/** 取得 Excel 的 Column 欄位代稱陣列 **/
	public static String[] getColumnsCode(int size) {
		String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String[] columns = new String[size];
		for (int i = 0; i < size; i++) {
			int divide = i / letters.length();
			int mod = i % letters.length();
			columns[i] = "";
			if (divide > 0)
				columns[i] += letters.charAt(divide - 1);
			columns[i] += letters.charAt(mod);
		}
		return columns;
	}

	public static String columnLocation(int rowIndex, int columnIndex, String[] columnCodes) {
		return rowIndex + "-" + columnCodes[columnIndex];
	}

	public static void main(String[] args) {
		System.out.println(Arrays.toString(getColumnsCode(36)));
	}
}
