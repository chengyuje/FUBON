package com.systex.jbranch.app.server.fps.pms314;

import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;
/**
 * 
 * Copy Right Information :  <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :Controller <br>
 * Comments Name : PMS314.java<br>
 * Author :Frank<br>
 * Date :2016年07月12日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月31日<br>
 */

@Component("pms314")
@Scope("request")
public class PMS314 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS314.class);
	
    /*** 查詢資料 ***/
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {

		PMS314OutputVO outputVO = new PMS314OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF condition = dam.getQueryCondition();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd"); 
		StringBuffer sql = new StringBuffer();    
		sql.append("SELECT * FROM "
				+ "TBPMS_SUMMIT_PAR "
				+ "WHERE SEQ = 1 ");
		condition.setQueryString(sql.toString());			
		outputVO.setResultList(dam.exeQuery(condition));
		sendRtnObject(outputVO);
				
	}
	
	/* ==== 【儲存】更新資料 ========*/
	public void save(Object body, IPrimitiveMap header) throws JBranchException {
//		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		PMS314InputVO inputVO = (PMS314InputVO) body;
		DataAccessManager dam = this.getDataAccessManager();
	
		QueryConditionIF condition = dam.getQueryCondition();
		StringBuffer sql = new StringBuffer();   
		if(inputVO.getSeq() == 1){
			sql.append("UPDATE TBPMS_SUMMIT_PAR ");
			sql.append("SET FC_SUMMIT_INC = "+inputVO.getSummitINC()+", " );
			sql.append("FC_PEAK_INC = "+inputVO.getPeakINC()+", ");
			sql.append("SUMMIT_FC1 = '"+inputVO.getSummitFC1()+"', ");
			sql.append("SUMMIT_FC2 = '"+inputVO.getSummitFC2()+"', ");
			sql.append("SUMMIT_FC3 = '"+inputVO.getSummitFC3()+"', ");
			sql.append("SUMMIT_FC4 = '"+inputVO.getSummitFC4()+"', ");
			sql.append("SUMMIT_FC5 = '"+inputVO.getSummitFC5()+"', ");
			sql.append("PEAK_FC1 = '"+inputVO.getPeakFC1()+"', ");
			sql.append("PEAK_FC2 = '"+inputVO.getPeakFC2()+"', ");
			sql.append("PEAK_FC3 = '"+inputVO.getPeakFC3()+"', ");
			sql.append("PEAK_FC4 = '"+inputVO.getPeakFC4()+"', ");
			sql.append("PEAK_FC5 = '"+inputVO.getPeakFC5()+"', ");
			sql.append("FCH_1_INC = "+inputVO.getFch1INC()+", ");
			sql.append("FCH_2_INC = "+inputVO.getFch2INC()+", ");
			sql.append("FCH_AUM = "+inputVO.getFchAum()+", ");
			sql.append("FCH_NEW = "+inputVO.getFchNew()+", ");
			sql.append("MODIFIER = 'test', ");
			sql.append("LASTUPDATE = SYSDATE ");
			sql.append("WHERE SEQ = 1 ");			
		}else{
			sql.append("INSERT INTO TBPMS_SUMMIT_PAR ");
			sql.append("(FC_SUMMIT_INC, FC_PEAK_INC,  ");
			sql.append("SUMMIT_FC1, SUMMIT_FC2, SUMMIT_FC3, ");
			sql.append("SUMMIT_FC4, SUMMIT_FC5, ");
			sql.append("PEAK_FC1, PEAK_FC2, PEAK_FC3, ");
			sql.append("PEAK_FC4, PEAK_FC5, ");
			sql.append("FCH_1_INC, FCH_2_INC, ");
			sql.append("FCH_AUM, FCH_NEW, ");
			sql.append("CREATETIME, CREATOR, ");
			sql.append("MODIFIER, LASTUPDATE) ");
			sql.append("VALUES ( ");
			sql.append(inputVO.getSummitINC()+", ");
			sql.append(inputVO.getPeakINC()+", ");
			sql.append("'"+inputVO.getSummitFC1()+"', ");
			sql.append("'"+inputVO.getSummitFC2()+"', ");
			sql.append("'"+inputVO.getSummitFC3()+"', ");
			sql.append("'"+inputVO.getSummitFC4()+"', ");
			sql.append("'"+inputVO.getSummitFC5()+"', ");
			sql.append("'"+inputVO.getPeakFC1()+"', ");
			sql.append("'"+inputVO.getPeakFC2()+"', ");
			sql.append("'"+inputVO.getPeakFC3()+"', ");
			sql.append("'"+inputVO.getPeakFC4()+"', ");
			sql.append("'"+inputVO.getPeakFC5()+"', ");
			sql.append(inputVO.getFch1INC()+", ");
			sql.append(inputVO.getFch2INC()+", ");
			sql.append(inputVO.getFchAum()+", ");
			sql.append(inputVO.getFchNew()+", ");
			sql.append("SYSDATE, ");
			sql.append("'"
					+ (String)getUserVariable(FubonSystemVariableConsts.LOGINID)
					+ "', ");
			sql.append("'"
					+ (String)getUserVariable(FubonSystemVariableConsts.LOGINID)
					+ "', ");
			sql.append("SYSDATE) ");
		}
				
		condition.setQueryString(sql.toString());
		dam.exeUpdate(condition);
		sendRtnObject(null);
	}
	
	
	
}