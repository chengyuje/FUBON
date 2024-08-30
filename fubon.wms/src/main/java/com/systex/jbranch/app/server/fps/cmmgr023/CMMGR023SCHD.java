package com.systex.jbranch.app.server.fps.cmmgr023;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.ibm.icu.util.Calendar;
import com.systex.jbranch.fubon.bth.ftp.BthFtpJobUtil;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 排程監控_確認未來排程預計要Get的檔案是否存在 for排程
 * 
 * @author SamTu
 * @date 2019/09/04
 */

@Repository("cmmgr023schd")
@Scope("prototype")
public class CMMGR023SCHD extends BizLogic {

	private BthFtpJobUtil ftpJobUtil = new BthFtpJobUtil();
	private DataAccessManager dam;
	private QueryConditionIF condition;
	private String[] voidList = { "*", "?" };
	private Date originDate; // 執行測試時的時間
	private String startCheckDate; // 第一個判斷時間 原始時間10分鐘後
	private String endCheckDate; // 第二個判斷時間 原始時間30分鐘後
	private Logger logger = LoggerFactory.getLogger(CMMGR023SCHD.class);
	private String SCHDDealTime = "無";
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssEEE");
	private SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyyMMddHHmmss");
	private Calendar calendar = Calendar.getInstance();

	public void deletePartitionTable(Object body, IPrimitiveMap<?> header) throws Exception {
		Date deleteDate = new Date();
		String deleteMonth = dateFormat.format(deleteDate).toString().substring(4, 6);
		String[] deleteMonthList = getDeleteMonth(deleteMonth);
		getDam();
		getCondition();
		
		for(int i=0 ; i<deleteMonthList.length; i++){
			String deleteSql = getDeletePartitionMonthSql(deleteMonthList[i]);
			condition.setQueryString(deleteSql);
			dam.exeUpdate(condition);
		}
		
		
	}

	private String getDeletePartitionMonthSql(String string) {
		
		return "ALTER TABLE TBSYSSCHDMONITOR TRUNCATE PARTITION PARTITION"+string+" UPDATE GLOBAL INDEXES";
	}

	private String[] getDeleteMonth(String deleteMonth) {
		String[] result = new String[3];
		if (deleteMonth.equals("01")) {
			result[0] = "10";
			result[1] = "11";
			result[2] = "12";
		}
		if (deleteMonth.equals("04")) {
			result[0] = "01";
			result[1] = "02";
			result[2] = "03";
		}
		if (deleteMonth.equals("07")) {
			result[0] = "04";
			result[1] = "05";
			result[2] = "06";
		}
		if (deleteMonth.equals("10")) {
			result[0] = "07";
			result[1] = "08";
			result[2] = "09";
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public void monitorGetFile(Object body, IPrimitiveMap<?> header) throws Exception {
		dealMonitorTime();
		// 取得相關資料
		getDam();
		getCondition();
		String searchSql = getSearchSql();
		condition.setQueryString(searchSql);
		List qryLst = dam.exeQuery(condition);
		for (int i = 0; i < qryLst.size(); i++) {
			HashMap map = (HashMap) qryLst.get(i);
			try {
				map.put("MONITORTIME", originDate);
				// 從PARAMETER提煉FTPGETCODE;
				map.put("FTPGETCODE", getFtpGetCode(map.get("PARAMETERS").toString()));
				// 抓FTPSETTINGID的部分資料
				try{
					getFTPDetails(map);
				} catch (Exception e){
					map.put("HOSTID", "查無資料");
					map.put("SRCDIRECTORY", "查無資料");
					map.put("SRCFILENAME", "查無資料");	
				}
				
				// CRON判斷順便紀錄結果
				map.put("CRONRESULT", dealCRON(map.get("CRONEXPRESSION").toString()));
				map.put("SCHDDEALTIME", SCHDDealTime);
				SCHDDealTime = "無";

			} catch (Exception e) {
				SCHDDealTime = "無";
				map.put("SCHDDEALTIME", SCHDDealTime);
				logger.debug("排程:" + map.get("SCHEDULEID").toString() + "  JOB:" + map.get("JOBID").toString() + "  CRON:" + map.get("CRONEXPRESSION").toString() + "  CRON處理結果:" + map.get("CRONRESULT").toString());
			}
			if (map.get("CRONRESULT").toString().equals("Y")) {
				try {
					if (ftpJobUtil.ftpCheckGetFile(map.get("FTPGETCODE").toString())) {
						map.put("FILECHECK", "Y");
						map.put("FTPCONNECTRESULT", "有檔案存在");
					} else {
						map.put("FILECHECK", "N");
						map.put("FTPCONNECTRESULT", "檔案不存在");
					}
				} catch (Exception e) {
					map.put("FILECHECK", "E");
					map.put("FTPCONNECTRESULT", e.getMessage());
					logger.debug("排程:" + map.get("SCHEDULEID").toString() + "  JOB:" + map.get("JOBID").toString() + "  CRON:" + map.get("CRONEXPRESSION").toString() + "  FTP連線結果" + map.get("FTPCONNECTRESULT").toString());
				}
			} else {
				map.put("FILECHECK", "E");
				map.put("FTPCONNECTRESULT", "CRON判斷無符合故未進行檔案判斷");
			}
			// System.out.println("排程:" + map.get("SCHEDULEID").toString() +
			// "  JOB:" + map.get("JOBID").toString() + "  CRON:" +
			// map.get("CRONEXPRESSION").toString() + "  CRON處理結果:" +
			// map.get("CRONRESULT").toString() + "  FTP連線結果" +
			// map.get("FTPCONNECTRESULT").toString());
			// 存partition用的資料
			map.put("PARTITIONMONTH", dateFormat.format(originDate).toString().substring(4, 6));
			exeUpdateForMap(getMergeSql(), map);
		}
	}

	/** 取得 {@link DataAccessManager} 物件 */
	private void getDam() {
		dam = this.getDataAccessManager();
	}

	/** 取得 {@link QueryConditionIF} 物件 */
	private void getCondition() throws DAOException, JBranchException {
		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	}

	private String getFtpGetCode(String first) {

		String split[] = first.split(";");
		for (String str : split) {
			String split2[] = str.split("=");
			if (split2[0].equals("ftpGetCode")) {
				return (split2[1]);
			}
		}
		return " ";
	}

	public void dealMonitorTime() throws DAOException, JBranchException {

		// 處理當前日期
		// 判斷未來10-30分鐘需要執行的批次
		int[] monitorRange = getMonitorRange();
		originDate = new Date();
		calendar.setTime(originDate);
		calendar.add(calendar.MINUTE, monitorRange[0]);
		startCheckDate = dateFormat.format(calendar.getTime()); // 十分鐘後的時間
		calendar.add(calendar.MINUTE, monitorRange[1] - monitorRange[0]);
		endCheckDate = dateFormat.format(calendar.getTime()); // 三十分鐘後的時間
		// String dateALL = dateFormat.format(date);
		// System.out.println(dateALL);
		// System.out.println("轉化後: " +
		// dateFormat.parse(dateALL).getTime());
		// String year = dateALL.substring(0, 4);
		// String month = dateALL.substring(4, 6);
		// String day = dateALL.substring(6, 8);
		// String hour = dateALL.substring(8, 10);
		// String min = dateALL.substring(10, 12);
		// String sec = dateALL.substring(12, 14);
		// String week = dateALL.substring(14, 17);
		// System.out.println(year + month + day + hour + min + sec + week);
		// System.out.println("解開合起來之後: " + dateFormat.parse(year + month +
		// day
		// + hour + min + sec + week).getTime());
		// System.out.println("處理週幾:" + dealWeek(week));

	}

	public String dealCRON(String CRON) throws ParseException {

		String CRONYear[];
		String CRONWeek[];
		String CRONMonth[];
		String CRONDay[];
		String CRONHour[];
		String CRONMin[];
		String CRONSec[];
		try {
			String split[] = CRON.split(" ");
			CRONYear = dealYear(split, startCheckDate, endCheckDate); // 處理年
			CRONWeek = dealWeek(split[5], startCheckDate, endCheckDate); // 處理週幾
			CRONMonth = dealMonth(split[4], startCheckDate, endCheckDate); // 處理月
			CRONDay = dealDay(split[3], startCheckDate, endCheckDate); // 處理日
			CRONHour = dealHour(split[2], startCheckDate, endCheckDate); // 處理時
			CRONMin = dealMin(split[1], startCheckDate, endCheckDate); // 處理分
			CRONSec = dealSec(split[0], endCheckDate); // 處理秒

			// 最終判斷
			return finalCheck(CRONYear, CRONMonth, CRONDay, CRONHour, CRONMin, CRONSec, startCheckDate, endCheckDate);
		} catch (Exception e) {
			return "CRON處理過程錯誤: " + e.getMessage();
		}
	}

	private String finalCheck(String[] year, String[] month, String[] day, String[] hour, String[] min, String[] sec, String startCheckDate, String endCheckDate) throws ParseException {
		String finalCheckDate;
		for (int i = 0; i < year.length; i++) {
			for (int j = 0; j < month.length; j++) {
				for (int k = 0; k < day.length; k++) {
					for (int l = 0; l < hour.length; l++) {
						for (int m = 0; m < min.length; m++) {
							for (int n = 0; n < sec.length; n++) {
								finalCheckDate = year[i] + month[j] + day[k] + hour[l] + min[m] + sec[n];
								if (dateFormat2.parse(finalCheckDate).getTime() >= dateFormat2.parse(startCheckDate).getTime() && dateFormat2.parse(finalCheckDate).getTime() <= dateFormat2.parse(endCheckDate).getTime()) {
									SCHDDealTime = finalCheckDate;
									return "Y";
								}
							}
						}
					}
				}
			}
		}
		// TODO Auto-generated method stub
		return "無合適時間";
	}

	public String[] dealYear(String[] split, String startCheckDate, String endCheckDate) throws Exception {
		String year[] = null;
		if (split.length == 7) {
			if (Arrays.asList(voidList).contains(split[6])) {
				year = new String[1];
				year[0] = endCheckDate.substring(0, 4);
			} else if (split[6].contains(",")) {
				year = split[6].split(",");
			} else if (split[6].contains("-")) {
				String[] split2 = split[6].split("-");
				int startYear = Integer.parseInt(split2[0]);
				int endYear = Integer.parseInt(split2[1]);
				year = new String[endYear - startYear + 1];
				for (int i = 0; i <= endYear - startYear; i++) {
					year[i] = String.valueOf(startYear + i);
				}
			} else if (split[6].contains("/")) {
				String[] split2 = split[6].split("/");
				int startYear = Integer.parseInt(split2[0]);
				int addYear = Integer.parseInt(split2[1]);
				int endYear = 2099; // Cron定義
				int count = 0;
				for (int i = startYear; i < endYear; i = i + addYear) {
					count = count + 1;
				}
				year = new String[count];
				int count2 = -1;
				for (int i = startYear; i < endYear; i = i + addYear) {
					count2 = count2 + 1;
					year[count2] = String.valueOf(i);
				}
			}
		} else {
			year = new String[1];
			year[0] = endCheckDate.substring(0, 4);
		}

		// 判斷年若都在範圍之外就直接return false
		int countbyYear = 0;
		for (int i = 0; i < year.length; i++) {
			if (Integer.parseInt(year[i]) < Integer.parseInt(startCheckDate.substring(0, 4)) || Integer.parseInt(year[i]) > Integer.parseInt(endCheckDate.substring(0, 4))) {
				countbyYear = countbyYear + 1;
			}
		}
		if (countbyYear == year.length) {
			throw new Exception("年份判斷不符合");
		}
		return year;
	}

	public String[] dealWeek(String inputWeek, String startCheckDate, String endCheckDate) throws Exception {
		String[] week = null;

		if (Arrays.asList(voidList).contains(inputWeek)) {
			week = new String[1];
			week[0] = endCheckDate.substring(14, 17);
		} else if (inputWeek.contains(",")) {
			week = inputWeek.split(",");
			for (int i = 0; i < week.length; i++) {
				week[i] = weekChange1(week[i]);
			}
		} else if (inputWeek.contains("-")) {
			String[] split2 = inputWeek.split("-");
			int startWeek = weekChange2(split2[0]);
			int endWeek = weekChange2(split2[1]);
			week = new String[endWeek - startWeek + 1];
			for (int i = 0; i <= endWeek - startWeek; i++) {
				week[i] = weekChange1(String.valueOf(startWeek + i));
			}
		} else if (inputWeek.contains("/")) {
			String[] split2 = inputWeek.split("/");
			int startWeek = weekChange2(split2[0]);
			int addWeek = weekChange2(split2[1]);
			int endWeeK = 7; // Cron定義
			int count = 0;
			for (int i = startWeek; i < endWeeK; i = i + addWeek) {
				count = count + 1;
			}
			week = new String[count];
			int count2 = -1;
			for (int i = startWeek; i < endWeeK; i = i + addWeek) {
				count2 = count2 + 1;
				week[count2] = weekChange1(String.valueOf(i));
			}
		} else {
			week = new String[1];
			week[0] = weekChange1(inputWeek);
		}

		// 判斷週幾若都在範圍之外就直接return false
		int countbyWeek = 0;

		for (int i = 0; i < week.length; i++) {
			// 處理跨週
			// EX 星期日~星期一 (日)
			if (weekChange2(startCheckDate.substring(14, 17)) > weekChange2(endCheckDate.substring(14, 17))) {
				if (weekChange2(week[i]) >= weekChange2(startCheckDate.substring(14, 17)) && weekChange2(week[i]) <= weekChange2(endCheckDate.substring(14, 17)) + 7) {

				} else {
					countbyWeek = countbyWeek + 1;
				}
			} else {
				if (weekChange2(week[i]) < weekChange2(startCheckDate.substring(14, 17)) || weekChange2(week[i]) > weekChange2(endCheckDate.substring(14, 17))) {
					countbyWeek = countbyWeek + 1;
				}
			}

		}
		if (countbyWeek == week.length) {
			throw new Exception("週幾判斷不符合");
		}
		return week;

	}

	public String[] dealMonth(String inputMonth, String startCheckDate, String endCheckDate) throws Exception {
		String[] month = null;

		if (Arrays.asList(voidList).contains(inputMonth)) {
			month = new String[1];
			month[0] = endCheckDate.substring(4, 6);
		} else if (inputMonth.contains(",")) {
			month = inputMonth.split(",");
			for (int i = 0; i < month.length; i++) {
				month[i] = monthChange(month[i]);
			}
		} else if (inputMonth.contains("-")) {
			String[] split2 = inputMonth.split("-");
			int startMonth = Integer.parseInt(monthChange(split2[0]));
			int endMonth = Integer.parseInt(monthChange(split2[1]));
			month = new String[endMonth - startMonth + 1];
			for (int i = 0; i <= endMonth - startMonth; i++) {
				month[i] = monthChange(String.valueOf(startMonth + i));
			}
		} else if (inputMonth.contains("/")) {
			String[] split2 = inputMonth.split("/");
			int startMonth = Integer.parseInt(monthChange(split2[0]));
			int addMonth = Integer.parseInt(monthChange(split2[1]));
			int endMonth = 12; // Cron定義
			int count = 0;
			for (int i = startMonth; i < endMonth; i = i + addMonth) {
				count = count + 1;
			}
			month = new String[count];
			int count2 = -1;
			for (int i = startMonth; i < endMonth; i = i + addMonth) {
				count2 = count2 + 1;
				month[count2] = monthChange(String.valueOf(i));
			}
		} else {
			month = new String[1];
			month[0] = monthChange(inputMonth);
		}

		// 判斷月若都在範圍之外就直接return false
		int countbyMonth = 0;
		for (int i = 0; i < month.length; i++) {
			// 處理跨年
			// EX 12月~1月
			if (Integer.parseInt(startCheckDate.substring(4, 6)) > Integer.parseInt(endCheckDate.substring(4, 6))) {
				if (Integer.parseInt(month[i]) >= Integer.parseInt(startCheckDate.substring(4, 6)) && Integer.parseInt(month[i]) <= Integer.parseInt(endCheckDate.substring(4, 6)) + 12) {

				} else {
					countbyMonth = countbyMonth + 1;
				}
			} else {
				if (Integer.parseInt(month[i]) < Integer.parseInt(startCheckDate.substring(4, 6)) || Integer.parseInt(month[i]) > Integer.parseInt(endCheckDate.substring(4, 6))) {
					countbyMonth = countbyMonth + 1;
				}
			}

		}
		if (countbyMonth == month.length) {
			throw new Exception("月判斷不符合");
		}

		return month;

	}

	public String[] dealDay(String inputDay, String startCheckDate, String endCheckDate) throws Exception {
		String[] day = null;

		if (Arrays.asList(voidList).contains(inputDay)) {
			day = new String[1];
			day[0] = endCheckDate.substring(6, 8);
		} else if (inputDay.contains(",")) {
			day = inputDay.split(",");
			for (int i = 0; i < day.length; i++) {
				day[i] = dayChange(day[i]);
			}
		} else if (inputDay.contains("-")) {
			String[] split2 = inputDay.split("-");
			int startDay = Integer.parseInt(split2[0]);
			int endDay = Integer.parseInt(split2[1]);
			day = new String[endDay - startDay + 1];
			for (int i = 0; i <= endDay - startDay; i++) {
				day[i] = dayChange(String.valueOf(startDay + i));
			}
		} else if (inputDay.contains("/")) {
			String[] split2 = inputDay.split("/");
			int startDay = Integer.parseInt(split2[0]);
			int addDay = Integer.parseInt(split2[1]);
			int endDay = dealDayTimes(endCheckDate); // 看月份
			int count = 0;
			for (int i = startDay; i < endDay; i = i + addDay) {
				count = count + 1;
			}
			day = new String[count];
			int count2 = -1;
			for (int i = startDay; i < endDay; i = i + addDay) {
				count2 = count2 + 1;
				day[count2] = dayChange(String.valueOf(i));
			}
		} else {
			day = new String[1];
			day[0] = dayChange(inputDay);
		}

		// 判斷日若都在範圍之外就直接return false
		int countbyDay = 0;
		for (int i = 0; i < day.length; i++) {

			// 處理跨月
			// EX 30日~1日 (30)
			if (Integer.parseInt(startCheckDate.substring(6, 8)) > Integer.parseInt(endCheckDate.substring(6, 8))) {
				if (Integer.parseInt(day[i]) >= Integer.parseInt(startCheckDate.substring(6, 8)) && Integer.parseInt(day[i]) <= Integer.parseInt(endCheckDate.substring(6, 8)) + dealDayTimes(endCheckDate)) {

				} else {
					countbyDay = countbyDay + 1;
				}
			} else {
				if (Integer.parseInt(day[i]) < Integer.parseInt(startCheckDate.substring(6, 8)) || Integer.parseInt(day[i]) > Integer.parseInt(endCheckDate.substring(6, 8))) {
					countbyDay = countbyDay + 1;
				}
			}

		}
		if (countbyDay == day.length) {
			throw new Exception("日判斷不符合");
		}

		return day;

	}

	public String[] dealHour(String inputHour, String startCheckDate, String endCheckDate) throws Exception {
		String[] hour = null;

		if (Arrays.asList(voidList).contains(inputHour)) {
			hour = new String[1];
			hour[0] = endCheckDate.substring(8, 10);
		} else if (inputHour.contains(",")) {
			hour = inputHour.split(",");
			for (int i = 0; i < hour.length; i++) {
				hour[i] = hourChange(hour[i]);
			}
		} else if (inputHour.contains("-")) {
			String[] split2 = inputHour.split("-");
			int startHour = Integer.parseInt(split2[0]);
			int endHour = Integer.parseInt(split2[1]);
			hour = new String[endHour - startHour + 1];
			for (int i = 0; i <= endHour - startHour; i++) {
				hour[i] = hourChange(String.valueOf(startHour + i));
			}
		} else if (inputHour.contains("/")) {
			String[] split2 = inputHour.split("/");
			int startHour = Integer.parseInt(split2[0]);
			int addHour = Integer.parseInt(split2[1]);
			int endHour = 23; // CRON定義
			int count = 0;
			for (int i = startHour; i < endHour; i = i + addHour) {
				count = count + 1;
			}
			hour = new String[count];
			int count2 = -1;
			for (int i = startHour; i < endHour; i = i + addHour) {
				count2 = count2 + 1;
				hour[count2] = hourChange(String.valueOf(i));
			}
		} else {
			hour = new String[1];
			hour[0] = hourChange(inputHour);
		}

		// 判斷時若都在範圍之外就直接return false
		int countbyHour = 0;
		for (int i = 0; i < hour.length; i++) {
			// 處理跨日
			// EX 23~1 (23)
			if (Integer.parseInt(startCheckDate.substring(8, 10)) > Integer.parseInt(endCheckDate.substring(8, 10))) {
				if (Integer.parseInt(hour[i]) >= Integer.parseInt(startCheckDate.substring(8, 10)) && Integer.parseInt(hour[i]) <= Integer.parseInt(startCheckDate.substring(8, 10)) + 24) {

				} else {
					countbyHour = countbyHour + 1;
				}
			} else {
				if (Integer.parseInt(hour[i]) < Integer.parseInt(startCheckDate.substring(8, 10)) || Integer.parseInt(hour[i]) > Integer.parseInt(endCheckDate.substring(8, 10))) {
					countbyHour = countbyHour + 1;
				}
			}

		}
		if (countbyHour == hour.length) {
			throw new Exception("時判斷不符合");
		}

		return hour;

	}

	public String[] dealMin(String inputMin, String startCheckDate, String endCheckDate) throws Exception {
		String[] min = null;

		if (Arrays.asList(voidList).contains(inputMin)) {
			min = new String[1];
			min[0] = endCheckDate.substring(10, 12);
		} else if (inputMin.contains(",")) {
			min = inputMin.split(",");
			for (int i = 0; i < min.length; i++) {
				min[i] = minChange(min[i]);
			}
		} else if (inputMin.contains("-")) {
			String[] split2 = inputMin.split("-");
			int startMin = Integer.parseInt(split2[0]);
			int endMin = Integer.parseInt(split2[1]);
			min = new String[endMin - startMin + 1];
			for (int i = 0; i <= endMin - startMin; i++) {
				min[i] = minChange(String.valueOf(startMin + i));
			}
		} else if (inputMin.contains("/")) {
			String[] split2 = inputMin.split("/");
			int startMin = Integer.parseInt(split2[0]);
			int addMin = Integer.parseInt(split2[1]);
			int endMin = 59; // CRON定義
			int count = 0;
			for (int i = startMin; i < endMin; i = i + addMin) {
				count = count + 1;
			}
			min = new String[count];
			int count2 = -1;
			for (int i = startMin; i < endMin; i = i + addMin) {
				count2 = count2 + 1;
				min[count2] = minChange(String.valueOf(i));
			}
		} else {
			min = new String[1];
			min[0] = minChange(inputMin);
		}

		// 判斷分若都在範圍之外就直接return false
		int countbyMin = 0;
		for (int i = 0; i < min.length; i++) {
			// 處理跨時60分鐘

			if (Integer.parseInt(startCheckDate.substring(10, 12)) > Integer.parseInt(endCheckDate.substring(10, 12))) {
				// EX 42~12 (50)
				if (Integer.parseInt(min[i]) >= Integer.parseInt(startCheckDate.substring(10, 12))) {
					if (Integer.parseInt(min[i]) <= Integer.parseInt(endCheckDate.substring(10, 12)) + 60) {
					} else {
						countbyMin = countbyMin + 1;
					}
				}
				// EX 52~12 (10)
				if (Integer.parseInt(min[i]) < Integer.parseInt(startCheckDate.substring(10, 12))) {
					if (Integer.parseInt(min[i]) + 60 <= Integer.parseInt(endCheckDate.substring(10, 12)) + 60) {
					} else {
						countbyMin = countbyMin + 1;
					}
				}
			} else {
				if (Integer.parseInt(min[i]) < Integer.parseInt(startCheckDate.substring(10, 12)) || Integer.parseInt(min[i]) > Integer.parseInt(endCheckDate.substring(10, 12))) {
					countbyMin = countbyMin + 1;
				}
			}
		}
		if (countbyMin == min.length) {
			throw new Exception("分判斷不符合");
		}

		return min;

	}

	public String[] dealSec(String inputSec, String endCheckDate) {
		String[] sec = null;

		if (Arrays.asList(voidList).contains(inputSec)) {
			sec = new String[1];
			sec[0] = endCheckDate.substring(12, 14);
		} else if (inputSec.contains(",")) {
			sec = inputSec.split(",");
			for (int i = 0; i < sec.length; i++) {
				sec[i] = secChange(sec[i]);
			}
		} else if (inputSec.contains("-")) {
			String[] split2 = inputSec.split("-");
			int startSec = Integer.parseInt(split2[0]);
			int endSec = Integer.parseInt(split2[1]);
			sec = new String[endSec - startSec + 1];
			for (int i = 0; i <= endSec - startSec; i++) {
				sec[i] = secChange(String.valueOf(startSec + i));
			}
		} else if (inputSec.contains("/")) {
			String[] split2 = inputSec.split("/");
			int startSec = Integer.parseInt(split2[0]);
			int addSec = Integer.parseInt(split2[1]);
			int endSec = 59; // CRON定義
			int count = 0;
			for (int i = startSec; i < endSec; i = i + addSec) {
				count = count + 1;
			}
			sec = new String[count];
			int count2 = -1;
			for (int i = startSec; i < endSec; i = i + addSec) {
				count2 = count2 + 1;
				sec[count2] = secChange(String.valueOf(i));
			}
		} else {
			sec = new String[1];
			sec[0] = secChange(inputSec);
		}

		return sec;

	}

	public String weekChange1(String Week) {
		if (Week.equals("MON") || Week.equals("1")) {
			return "星期一";
		}
		if (Week.equals("TUE") || Week.equals("2")) {
			return "星期二";
		}
		if (Week.equals("WED") || Week.equals("3")) {
			return "星期三";
		}
		if (Week.equals("THU") || Week.equals("4")) {
			return "星期四";
		}
		if (Week.equals("FRI") || Week.equals("5")) {
			return "星期五";
		}
		if (Week.equals("SAT") || Week.equals("6")) {
			return "星期六";
		}
		if (Week.equals("SUN") || Week.equals("7")) {
			return "星期日";
		}
		return Week;

	}

	public int weekChange2(String Week) throws Exception {
		if (NumberUtils.isNumber(Week)) {
			return Integer.parseInt(Week);

		}
		if (Week.equals("MON") || Week.equals("星期一")) {
			return 1;
		} else if (Week.equals("TUE") || Week.equals("星期二")) {
			return 2;
		} else if (Week.equals("WED") || Week.equals("星期三")) {
			return 3;
		} else if (Week.equals("THU") || Week.equals("星期四")) {
			return 4;
		} else if (Week.equals("FRI") || Week.equals("星期五")) {
			return 5;
		} else if (Week.equals("SAT") || Week.equals("星期六")) {
			return 6;
		} else if (Week.equals("SUN") || Week.equals("星期日")) {
			return 7;
		} else {
			throw new Exception("輸入的星期有問題");
		}

	}

	public String monthChange(String Month) {
		if (NumberUtils.isNumber(Month)) {
			if (Integer.parseInt(Month) >= 10 && Integer.parseInt(Month) <= 12) {
				return Month;
			}
		}
		if (Month.equals("JAN") || Month.equals("1")) {
			return "01";
		}
		if (Month.equals("FEB") || Month.equals("2")) {
			return "02";
		}
		if (Month.equals("MAR") || Month.equals("3")) {
			return "03";
		}
		if (Month.equals("APR") || Month.equals("4")) {
			return "04";
		}
		if (Month.equals("MAY") || Month.equals("5")) {
			return "05";
		}
		if (Month.equals("JUN") || Month.equals("6")) {
			return "06";
		}
		if (Month.equals("JUL") || Month.equals("7")) {
			return "07";
		}
		if (Month.equals("AUG") || Month.equals("8")) {
			return "08";
		}
		if (Month.equals("SEP") || Month.equals("9")) {
			return "09";
		}
		if (Month.equals("OCT")) {
			return "10";
		}
		if (Month.equals("NOV")) {
			return "11";
		}
		if (Month.equals("DEC")) {
			return "12";
		}
		return Month;
	}

	public String dayChange(String Day) {
		if (Integer.parseInt(Day) < 10 && Integer.parseInt(Day) >= 0) {
			return "0" + Day;
		} else {
			return Day;
		}
	}

	public int dealDayTimes(String endCheckDate) {
		int month = Integer.parseInt((String) endCheckDate.subSequence(4, 6));
		int year = Integer.parseInt((String) endCheckDate.subSequence(0, 4));
		if (month == 1) {
			return 31;
		}
		if (month == 2) {
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
				return 29;
			} else {
				return 28;
			}
		}
		if (month == 3) {
			return 31;
		}
		if (month == 4) {
			return 30;
		}
		if (month == 5) {
			return 31;
		}
		if (month == 6) {
			return 30;
		}
		if (month == 7) {
			return 31;
		}
		if (month == 8) {
			return 31;
		}
		if (month == 9) {
			return 30;
		}
		if (month == 10) {
			return 31;
		}
		if (month == 11) {
			return 30;
		}
		if (month == 12) {
			return 31;
		}
		return month;
	}

	public String hourChange(String hour) {
		if (Integer.parseInt(hour) < 10 && Integer.parseInt(hour) >= 0) {
			return "0" + hour;
		} else {
			return hour;
		}
	}

	public String minChange(String min) {
		if (Integer.parseInt(min) < 10 && Integer.parseInt(min) >= 0) {
			return "0" + min;
		} else {
			return min;
		}
	}

	public String secChange(String sec) {
		if (Integer.parseInt(sec) < 10 && Integer.parseInt(sec) >= 0) {
			return "0" + sec;
		} else {
			return sec;
		}
	}

	public String getMergeSql() {

		return new StringBuffer().append("MERGE INTO TBSYSSCHDMONITOR a ") 
				.append("USING (SELECT COUNT(SCHEDULEID) C from TBSYSSCHDMONITOR where SCHEDULEID=:SCHEDULEID AND JOBID=:JOBID AND SCHDDEALTIME=:SCHDDEALTIME) ")
				.append("ON (C > 0) ")
				.append("WHEN MATCHED THEN UPDATE SET ")
				.append("a.SCHEDULEDESP = :SCHEDULEDESP,a.JOBDESP = :JOBDESP, a.PARAMETERS = :PARAMETERS ")
				.append(", a.CRONEXPRESSION = :CRONEXPRESSION, a.MONITORTIME = :MONITORTIME, a.FTPGETCODE = :FTPGETCODE, a.CRONRESULT = :CRONRESULT ")
				.append(", a.FILECHECK = :FILECHECK, a.FTPCONNECTRESULT = :FTPCONNECTRESULT, a.SCHDDEALTIME = :SCHDDEALTIME , a.PARTITIONMONTH = :PARTITIONMONTH ")
				.append(", a.HOSTID = :HOSTID, a.SRCDIRECTORY = :SRCDIRECTORY, a.SRCFILENAME = :SRCFILENAME ")
				.append("where a.SCHEDULEID = :SCHEDULEID and a.JOBID =:JOBID AND SCHDDEALTIME=:SCHDDEALTIME  ")
				.append("WHEN NOT MATCHED THEN INSERT VALUES ")
				.append("(:SCHEDULEID, :SCHEDULEDESP, :JOBID, :JOBDESP, :PARAMETERS ")
				.append(", :CRONEXPRESSION, :MONITORTIME, :FTPGETCODE,:CRONRESULT")
				.append(", :FILECHECK, :FTPCONNECTRESULT, :SCHDDEALTIME ,:PARTITIONMONTH")
				.append(", :HOSTID, :SRCDIRECTORY, :SRCFILENAME)")
				.toString();

	}

	public String getSearchSql() throws DAOException, JBranchException {
		String[] processor = getMonitorProcessor();
		StringBuffer searchSql = new StringBuffer().append("select A.SCHEDULEID, A.JOBID, NVL(C.DESCRIPTION,' ') SCHEDULEDESP,  NVL(B.DESCRIPTION,' ') JOBDESP, B.PARAMETERS, C.CRONEXPRESSION ").append("from TBSYSSCHDJOBASS A ").append("left join TBSYSSCHDJOB B on A.JOBID = B.JOBID ").append("left join TBSYSSCHD C on A.SCHEDULEID = C.SCHEDULEID ").append("where B.PARAMETERS like '%ftpGet%' AND C.ISUSE = 'Y' AND C.ISSCHEDULED = 'Y' ");
		for (int i = 0; i < processor.length; i++) {
			if (i == 0) {
				searchSql.append("AND C.PROCESSOR = '" + processor[i].trim() + "' ");
			} else {
				searchSql.append("OR C.PROCESSOR = '" + processor[i].trim() + "' ");
			}

		}
		return searchSql.toString();
	}

	public int[] getMonitorRange() throws DAOException, JBranchException {
		int[] Range = new int[2];
		getDam();
		getCondition();
		condition.setQueryString("select PARAM_NAME from TBSYSPARAMETER where PARAM_TYPE = 'CMMGR023' AND PARAM_CODE = 'MONITOR.RANGE' ");
		List qryLst = dam.exeQuery(condition);
		HashMap map = (HashMap) qryLst.get(0);
		String[] split = map.get("PARAM_NAME").toString().split(";");
		Range[0] = Integer.parseInt(split[0]);
		Range[1] = Integer.parseInt(split[1]);

		return Range;
	}

	public String[] getMonitorProcessor() throws DAOException, JBranchException {
		getDam();
		getCondition();
		condition.setQueryString("select PARAM_NAME from TBSYSPARAMETER where PARAM_TYPE = 'CMMGR023' AND PARAM_CODE = 'MONITOR.PROCESSOR' ");
		List qryLst = dam.exeQuery(condition);
		HashMap map = (HashMap) qryLst.get(0);
		String[] result = map.get("PARAM_NAME").toString().split(";");
		return result;
	}
	
	private void getFTPDetails(HashMap map) throws DAOException, JBranchException {
		getDam();
		getCondition();
		condition.setQueryString("select HOSTID, SRCDIRECTORY, SRCFILENAME from TBSYSFTP where FTPSETTINGID = :FTPGETCODE");
		condition.setObject("FTPGETCODE", map.get("FTPGETCODE"));
		List qryLst = dam.exeQuery(condition);

	    HashMap FTPMap = (HashMap) qryLst.get(0);
		map.put("HOSTID", FTPMap.get("HOSTID"));
		map.put("SRCDIRECTORY", FTPMap.get("SRCDIRECTORY"));
		map.put("SRCFILENAME", FTPMap.get("SRCFILENAME"));			
	}

	public static void main(String args[]) throws ParseException {
		CMMGR023SCHD test = new CMMGR023SCHD();
		String CRON = "0 10 8-18 * * ?";
		System.out.println("CRON處理結果:  " + test.dealCRON(CRON));
		// String test = "10,20,30";
		// if(test.contains(",")){
		// System.out.println(test + "有逗點");
		// }else{
		// System.out.println(test + "沒偵測到逗點");
		// }

		// int a = 1970;
		// int b = 11;
		// int c = 2099;
		// int count = 0;
		// for (int i = a; i < c; i = i + b) {
		// count = count + 1;
		// System.out.println(i + ":" + count);
		// }

	}

}
