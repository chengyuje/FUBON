package com.systex.jbranch.fubon.bth.job.sevEntity;

import com.systex.jbranch.fubon.bth.job.inf.AccessSQLInf;
import com.systex.jbranch.platform.common.errHandle.APException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;

/**
 * 
 * 仲介處理器 : SQL具體物件，實作AccessSQL介面
 * @author Eli
 * @since V1.1版
 * @date 2017/10/12
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Component("JobDam")
@Scope("prototype")
public class JobDam implements AccessSQLInf {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Vector sqls;
	private List datalist;
	
	//用來記錄並使用於commit邏輯判斷
	private int updateIndex = 0;
	private void setUpdateIndex(int updateIndex) {
		this.updateIndex = updateIndex;
	}	
	private static final int COMMIT_LIMIT_COUNT = 5000;
	
	
	public Vector getSqls() {
		return sqls;
	}
	public void setSpcVectorSqls(Vector sqls) throws APException {
		
		if (!(sqls.size() == 5)) throw new APException("SpcVectorSqls規範必須設定五組!");
		
		Object obj1 = sqls.elementAt(0);
		Object obj2 = sqls.elementAt(1);
		Object obj3 = sqls.elementAt(2);
		Object obj4 = sqls.elementAt(3);
		Object obj5 = sqls.elementAt(4);
		
		if (!(obj1 instanceof String || obj1 == null) || 
				!(obj2 instanceof String || obj2 == null) ||
				!(obj3 instanceof String || obj3 == null)) 
			throw new APException("陣列第一元素到第三元素型別只能為String類別，或者為null值!");
		if (obj1 instanceof String)
			if (obj1.toString().toLowerCase().indexOf("truncate") < 0)
				throw new APException("陣列第一元素只能為truncate語法!");
		if (obj2 instanceof String)
			if (!(obj2.toString().toLowerCase().indexOf("insert") > -1 || obj2.toString().toLowerCase().indexOf("update") > -1))
				throw new APException("陣列第二元素只能為insert or update語法!");
		if (obj3 instanceof String) 
			if (obj3.toString().toLowerCase().indexOf("update") < 0) 
				throw new APException("陣列第三元素只能為update語法!");
		
		if (!(obj4 instanceof String[] || obj4 == null) ||
				!(obj5 instanceof String[] || obj5 == null)) {
			throw new APException("陣列第四元素、第五元素型別只能為String[]陣列，或者為null值!");
		}
		
		this.sqls = sqls;
	}
	public List getDatalist() {
		return datalist;
	}
	public void setDatalist(List datalist) {
		this.datalist = datalist;
	}
	private String sqlString;
	public String getSqlString() {
		if (sqlString != null) {
			return sqlString;
		} else {
			return "";
		}
	}
	public void setSqlString(String sqlString) {
		this.sqlString = sqlString;
	}
	
	/**
	 * SQL execute service
	 * @param str String[]
	 * @param arr Object[]
	 * @return void
	 * @throws Exception 
	 */
	@Override
	public void setExeInfo(String str, Object[] arr) throws Exception{
		
		try {
			for (int j = 0; j < arr.length; j++) {
				if ("null".equals(arr[j])) {
					str = str.replaceFirst("\\?", "null");
				} else {
					//特定符號轉為全型,避免顯示錯誤 (設置參數)
					str = str.replaceFirst("\\?", "'" + strFilter(arr[j]) + "'");
				}
			}

			if(!("".equals(str))){
				this.setSqlString(str);
			}
		} catch (Exception e) {
			throw new APException("參數設置錯誤");
		}
	}
	
	private String strFilter(Object str) {
		String tmpStr = "";
		try {
			tmpStr = str.toString().trim();
			tmpStr = tmpStr.replaceAll("'", "’").replaceAll("\\$", "＄").replaceAll("\\?", "？");
		} catch (Exception e) {
		}
		return tmpStr;
	}
	
	/**
	 * querySQL
	 * @return List : 返回的查詢結果集
	 * @throws Exception 
	 */
	@Override
	public List exeQuery(Connection c) throws Exception {
		List<Map<String, Object>> list = null;
		String countSql = "select count(*) Cnt from ( " + this.getSqlString() + ") A ";
		try( 
			 Connection connection = c;
			 PreparedStatement countPS = connection.prepareStatement(countSql);
			 ResultSet cntRS = countPS.executeQuery()
		 ) {
			// 取得該資料筆數
			cntRS.next();
			int count = cntRS.getInt(1);
			logger.info("總筆數 : " + count);
			
			// 依筆數建立ArrayList
			list = new ArrayList(count);
			// 取回結果
		    try (PreparedStatement dataPS = connection.prepareStatement(this.getSqlString())) {
		    	dataPS.setFetchSize(3000);
		    	try (ResultSet dataRS = dataPS.executeQuery()) {
		    		ResultSetMetaData rsmd = dataRS.getMetaData();
		    		while (dataRS.next()) {
			            Map m = new HashMap<String, Object>();
			            for(int i = 1 ; i <= rsmd.getColumnCount() ; i++) 
			            	m.put(rsmd.getColumnName(i), dataRS.getObject(i));
			            list.add(m);
			        }
			    }
		    }
		} catch (SQLException e) {
			throw new APException(e.getMessage());
		} catch (Exception e) {
			throw new APException("系統發生錯誤請洽系統管理員");
		}
		return list;
	}

	/**
	 * 更新SQL
	 * @param conn
	 * @throws Exception
	 */
	@Override
	public void exeUpdate(Connection conn) throws Exception {
		// 檢查是否有提供語法組，沒有則返回
		Vector sqlV = getSqls();
		if (sqlV == null) return;

		try (Connection c = conn) {
			//前置語法 ，ex : truncate
			String trunSql = (String)sqlV.elementAt(0);
			if (trunSql != null) {
				try (PreparedStatement trunPS = c.prepareStatement(trunSql)) {
					trunPS.executeUpdate();
				}
			}

			//主要語法 ，ex : insert or update
			String mainSql = (String)sqlV.elementAt(1);
			String exSql = (String)sqlV.elementAt(2);

            if (mainSql != null) {
				try (PreparedStatement mainPS = c.prepareStatement(mainSql);
                     PreparedStatement exPS = getExPS(c, exSql)) {
					for (Object strline : getDatalist()) {
						try {
                            processUpdate(mainPS, strline.toString().split(","));
                        } catch (SQLException mainErr) {
							//處置語法sqls[2]存在時的例外處理，Ex: 重複鍵時，將改用update
							if (exSql != null) {
							    try {
                                    processUpdate(exPS, getExParams(sqlV, strline));
                                } catch (SQLException exErr) {
                                    logger.info("error:" + exErr.getMessage());
                                }
                            } else {
                                logger.info("error:" + mainErr.getMessage());
							}
						} finally {
						    // 不管成功失敗，最後都將清除ps的參數
						    mainPS.clearParameters();
						    if (exSql != null) exPS.clearParameters();
                        }
						if(isLimitOfCommit()){
							mainPS.executeBatch();
							if (exSql != null) exPS.executeBatch();
						}

					}
					//最後未滿 #COMMIT_LIMIT_COUNT 的筆數也要commit
					mainPS.executeBatch();
					if (exSql != null) exPS.executeBatch();
				}
			}
		}
	}

	/** 執行更新邏輯，成功後index + 1 **/
    private void processUpdate(PreparedStatement ps, String[] params) throws SQLException {
        setPSParams(ps, params);
		ps.addBatch();
        updateIndex++;
    }

    /** 取得Ex的參數集 **/
    private String[] getExParams(Vector sqlV, Object strline) {
        List<String> dataline = Arrays.asList(strline.toString().split(","));
        List insertColumns = Arrays.asList((String[])sqlV.elementAt(3));

        List<String> newLine = new ArrayList();
        newLine.addAll(dataline);
        for (String str : (String[])sqlV.elementAt(4)) {
            newLine.add(dataline.get(insertColumns.indexOf(str)));
        }
        return newLine.toArray(new String[newLine.size()]);
    }

    /** 依照給定的exSql返回PreparedStatement，如果exSql為null，則返回null **/
    private PreparedStatement getExPS(Connection c, String exSql) throws SQLException {
        if (exSql != null) return c.prepareStatement(exSql);
        else return null;
    }

    /** 設置 PreparedStatement的SQL參數 **/
	private void setPSParams(PreparedStatement ps, String[] params) throws SQLException {
		int index = 1;
		for (String param: params) {
			ps.setString(index++, param);
		}
	}
	
	/**
	 * 固定筆數commit;
	 */
	private boolean isLimitOfCommit() {
		if (updateIndex >= COMMIT_LIMIT_COUNT) {
			this.setUpdateIndex(0); //初始計數
			return true;
		}
		return false;
	}
	
	/**
	 * 返回Meta
	 */
	@Override
	public List exeQueryMeta(Connection c) throws Exception {
		List list = null;
		try( 
			 Connection connection = c;
			 PreparedStatement ps = connection.prepareStatement(this.getSqlString());
			 ResultSet rs = ps.executeQuery()
		 ) {
			ResultSetMetaData rsmd = rs.getMetaData();
			int colCounts = rsmd.getColumnCount();
			list = new ArrayList(colCounts);
			for(int i = 1 ; i <= colCounts ; i++) 
				list.add(rsmd.getColumnName(i));
		} catch (SQLException e) {
			throw new APException(e.getMessage());
		} catch (Exception e) {
			throw new APException("系統發生錯誤請洽系統管理員");
		}
		return list;
	}

}
