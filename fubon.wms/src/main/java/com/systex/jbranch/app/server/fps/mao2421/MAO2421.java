package com.systex.jbranch.app.server.fps.mao2421;

import java.text.SimpleDateFormat;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("mao2421")
@Scope("prototype")
public class MAO2421 extends BizLogic {
	
	private DataAccessManager dam = null;
	
	SimpleDateFormat sdfYYYYMMDD  = new SimpleDateFormat("yyyyMMdd");

	//每天 09:00、13:00、17:00
	//執行一次處理逾時未核可，將狀態更新為已退回A01(代表釋放設備)
	public void ReleaseDev(Object body, IPrimitiveMap<?> header) throws JBranchException, Exception {
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
						
		//執行一次處理逾時未核可，將狀態更新為已退回A01(代表釋放設備)
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		
		sql.append("UPDATE TBMAO_DEV_APL_PLIST P ");
		sql.append("SET P.DEV_STATUS = 'A01', ");
		sql.append("    P.MODIFIER = 'SYSTEM', ");
		sql.append("    P.LASTUPDATE = SYSDATE ");
		sql.append("WHERE P.DEV_STATUS = 'B04' ");
		sql.append("AND ( ");
		sql.append("  (USE_PERIOD_E_TIME > USE_PERIOD_S_TIME AND TO_CHAR(CURRENT_TIMESTAMP,'yyyyMMddHH24MISS') >= RPAD(TO_CHAR(USE_DATE, 'yyyyMMdd')  || USE_PERIOD_E_TIME || '00', 14, '0')) OR ");
		sql.append("  (USE_PERIOD_E_TIME < USE_PERIOD_S_TIME AND TO_CHAR(CURRENT_TIMESTAMP,'yyyyMMddHH24MISS') >= RPAD(TO_CHAR(USE_DATE + 1, 'yyyyMMdd')  || USE_PERIOD_E_TIME || '00', 14, '0'))  ");
		sql.append(") ");
		sql.append("AND EXISTS (SELECT DISTINCT UEMP_ID FROM TBORG_CUST_UHRM_PLIST UP WHERE P.APL_EMP_ID = UP.UEMP_ID) ");
		
		queryCondition.setQueryString(sql.toString());
		
		dam.exeUpdate(queryCondition);
	}
	
	//找出逾時未歸還設備，更新設備狀態為A02(已歸還)
	//每天 09:00、13:00、17:00執行一次
	public void SetDev(Object body, IPrimitiveMap<?> header) throws JBranchException, Exception {
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
						
		sql.append("UPDATE TBMAO_DEV_APL_PLIST P ");
		sql.append("SET P.DEV_STATUS = 'A02',  ");
		sql.append("    P.DEV_RETURN_DATETIME = SYSDATE, ");
		sql.append("    P.DEV_RETURN_EMP_ID = 'SYSTEM', ");
		sql.append("    P.MODIFIER = 'SYSTEM', ");
		sql.append("    P.LASTUPDATE = SYSDATE ");
		sql.append("WHERE P.DEV_STATUS = 'D06' ");
		sql.append("AND( ");
		sql.append("  (USE_PERIOD_E_TIME > USE_PERIOD_S_TIME AND TO_CHAR(CURRENT_TIMESTAMP,'yyyyMMddHH24MISS') >= RPAD(TO_CHAR(USE_DATE, 'yyyyMMdd')  || USE_PERIOD_E_TIME || '00', 14, '0')) OR ");
		sql.append("  (USE_PERIOD_E_TIME < USE_PERIOD_S_TIME AND TO_CHAR(CURRENT_TIMESTAMP,'yyyyMMddHH24MISS') >= RPAD(TO_CHAR(USE_DATE + 1, 'yyyyMMdd')  || USE_PERIOD_E_TIME || '00', 14, '0'))  ");
		sql.append(")  ");
		sql.append("AND EXISTS (SELECT DISTINCT UEMP_ID FROM TBORG_CUST_UHRM_PLIST UP WHERE P.APL_EMP_ID = UP.UEMP_ID) ");
		
		queryCondition.setQueryString(sql.toString());
		
		dam.exeUpdate(queryCondition);
	}

}