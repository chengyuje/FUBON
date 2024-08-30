package com.systex.jbranch.platform.common.dataaccess.serialnumber;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.helper.SerialNumErrMsgHelper;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.EnumErrInputType;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TbsysserialnumberVO;

public class SerialNumberImpl implements SerialNumberIF{
	private DataAccessManager dam = null;
	
	public void setDataAccessManager(DataAccessManager dam){
		this.dam = dam;
	}
	/**
	 * 取得指定ID的下一個序號
	 * @param snid
	 * @return
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String getNextSerialNumber(String snid) throws DAOException, JBranchException{
		try {

			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
			condition.setQueryString("update TBSYSSERIALNUMBER set SNID=? where SNID=?");
			condition.setString(0, snid);
			condition.setString(1, snid);
			dam.executeUpdate(condition);
			TbsysserialnumberVO vo = (TbsysserialnumberVO) dam.findByPKey(TbsysserialnumberVO.TABLE_UID, snid, true);			
			if(vo == null){
				DAOException daoE =  new DAOException(SerialNumErrMsgHelper.SNID_NOT_FOUND);
				throw daoE;
			}
			if(checkPeriod(vo)){
				initValue(vo);
			}else{
				calculateValue(vo);
			}
			dam.update(vo);
			return new DecimalFormat(vo.getPattern()).format(vo.getValue().longValue());
		} catch (Exception e) {
			JBranchException je = new JBranchException(EnumErrInputType.MSG, e.getMessage());			
			je.setException(e);
			throw je;
		}
	}
	
	/**
	 * snid SerialNumber識別碼
	 * pattern 數字格式ex.#,##0，其它格式請參考http://download.oracle.com/javase/1.5.0/docs/api/java/text/DecimalFormat.html
	 * period 增量值
	 * periodunit 增量單位，例如y、m、d，分別代表年月日
	 * periodupdatetime 重置日
	 * min 最小值
	 * max 最大值
	 * increase 是遞增(y,n)
	 * value 初始值
	 * user 建立人員
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void createNewSerial(String snid, String pattern, Integer period, String periodunit, 
			     Timestamp periodupdatetime, Integer min, Long max, String increase, Long value, 
			     String user) throws JBranchException{
		try {

			dam.setAutoCommit(true);
			if(increase == null) increase = "y";
			if(!increase.equals("y") && (max==null || max<=0)) throw new Exception("increase number need a max setting"); 
			
			Timestamp now = new Timestamp(System.currentTimeMillis());
			TbsysserialnumberVO vo = new TbsysserialnumberVO();
			vo.setSnid(snid);
			vo.setCreatetime(now);
			vo.setLastupdate(now);
			vo.setCreator(user);
			vo.setModifier(user);
			vo.setPattern(pattern);
			vo.setPeriod((period == null) ? new Integer(-1) : period);
			vo.setPeriodunit(periodunit == null ? "" : periodunit);
			if(periodupdatetime != null) vo.setPeriodupdatetime(periodupdatetime);
			vo.setMin(min == null ? new Integer(1) : min);
			vo.setMax(max == null ? new Long(-1) : max);
			vo.setIncrease(increase);
			vo.setValue(value);
			dam.create( vo);			
		} catch (Exception e) {
			JBranchException je = new JBranchException(EnumErrInputType.MSG, e.getMessage());			
			je.setException(e);
			throw je;
		}
	}
	
	/**
	 * 檢查序號是否需要歸零，檢查條件為週期相關參數
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	private boolean checkPeriod(TbsysserialnumberVO vo) throws Exception{
		try{
			if(vo == null) return false;
			
			//檢查週期相關設定是否存在，若沒有，則不需處理
			if(vo.getPeriodunit() == null || vo.getPeriodunit().trim().length()==0) return false;
			if(vo.getPeriod() == null || vo.getPeriod().intValue()<=0) return false;
			if(vo.getPeriodupdatetime() == null) return false;
						
			Calendar rightnow   = Calendar.getInstance();
			Calendar expire = Calendar.getInstance();
			expire.setTimeInMillis(vo.getPeriodupdatetime().getTime());
			boolean update = false;
			if(vo.getPeriodunit().equalsIgnoreCase("y")){//單位為年				 
			  //設定到期時間
			  expire.set(Calendar.YEAR, (expire.get(Calendar.YEAR) + vo.getPeriod()));
			  
			  //比較是否為到期時間
			  if(expire.get(Calendar.YEAR) <= rightnow.get(Calendar.YEAR)){
				  vo.setPeriodupdatetime(new Timestamp(System.currentTimeMillis()));
				  update = true;
			  }else{
				  update = false;
			  }
			}else if(vo.getPeriodunit().equalsIgnoreCase("m")){//單位為月
			  //設定到期時間
			  expire.set(Calendar.MONTH, (expire.get(Calendar.MONTH) + vo.getPeriod()));
			  
			   //比較是否為到期時間
			  if(expire.get(Calendar.YEAR) < rightnow.get(Calendar.YEAR)){
				  vo.setPeriodupdatetime(new Timestamp(System.currentTimeMillis()));
				  update = true;
			  }else if(expire.get(Calendar.YEAR) == rightnow.get(Calendar.YEAR) 
					  && expire.get(Calendar.MONTH) <= rightnow.get(Calendar.MONTH)){
				  vo.setPeriodupdatetime(new Timestamp(System.currentTimeMillis()));
				  update = true;
			  }else{
				  update = false;
			  }
			}else if(vo.getPeriodunit().equalsIgnoreCase("d")){//單位為天
				//設定到期時間
			    expire.set(Calendar.DAY_OF_YEAR, (expire.get(Calendar.DAY_OF_YEAR) + vo.getPeriod()));
			  
			    //比較是否為到期時間
			    if(expire.get(Calendar.YEAR) < rightnow.get(Calendar.YEAR)){
			      vo.setPeriodupdatetime(new Timestamp(System.currentTimeMillis()));
				  update = true;
			    }else if(expire.get(Calendar.YEAR) == rightnow.get(Calendar.YEAR) 
					  && expire.get(Calendar.DAY_OF_YEAR) <= rightnow.get(Calendar.DAY_OF_YEAR)){
			      vo.setPeriodupdatetime(new Timestamp(System.currentTimeMillis()));
				  update = true;
			    }else{
				  update = false;
			    }
			}
			
			return update;
		}catch(Exception e){
			JBranchException je = new JBranchException(EnumErrInputType.MSG, e.getMessage());			
			je.setException(e);
			throw je;
		}
	}
	
	/**
     * 若週期到，則呼叫此method作歸零動作
     * @param vo
     */
	private void initValue(TbsysserialnumberVO vo){		
		//遞增
		if(vo.getIncrease().equalsIgnoreCase("y")){
			vo.setValue(vo.getMin().longValue());
		
		//遞減
		}else{
			vo.setValue(vo.getMax());
		}
	}
	
	/**
	 * 計算並設定目前序號值
	 * @param vo
	 */
	private void calculateValue(TbsysserialnumberVO vo){	
		//遞增
		if(vo.getIncrease().equalsIgnoreCase("y")){		
			//判斷是否達到最大值
			if((vo.getMax() != null) 
					&& (vo.getMax().longValue() > 0)
					&& (vo.getMax().longValue() == vo.getValue().longValue())){
				vo.setValue(vo.getMin().longValue());
			}else{
				vo.setValue(vo.getValue().longValue()+1);
			}
		
		//遞減
		}else{
			//判斷是否達到最小值
			if((vo.getMin() != null) 
					&& (vo.getMin().intValue() > 0)
					&& (vo.getMin().intValue() == vo.getValue().longValue())){
				if((vo.getMax() == null) || (vo.getMax().longValue() <= 0)){
					vo.setValue(Long.MAX_VALUE);
				}else{
					vo.setValue(vo.getMax().longValue());
				}			
			}else{
				vo.setValue(vo.getValue()-1);
			}
		}
	}

	
	public static void main(String [] args){
		try{
			
			DecimalFormat decimalFormat = new DecimalFormat("PREFIX####SUFFIX");
			System.out.println(decimalFormat.format(112321342.456));
			Calendar rightnow   = Calendar.getInstance();
			System.out.println(rightnow.get(Calendar.DAY_OF_YEAR));//+","+rightnow.get(Calendar.MONTH));
			
			rightnow.set(Calendar.DAY_OF_YEAR, (rightnow.get(Calendar.DAY_OF_YEAR) + 300));
			System.out.println(rightnow.get(Calendar.DAY_OF_YEAR));
			System.out.println(rightnow.get(Calendar.YEAR)+","+rightnow.get(Calendar.MONTH));
			System.out.println(Long.MAX_VALUE);
//			SerialNumberUtil.createNewSerial("tbsysej11");
//			System.out.println(SerialNumberUtil.getNextSerialNumber("tbsyspflog"));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void deleteSerialNumber(String snid) throws JBranchException {

		TbsysserialnumberVO serialNumberVo = (TbsysserialnumberVO) dam.findByPKey(TbsysserialnumberVO.TABLE_UID, snid);
		dam.delete(serialNumberVo);
	}
}
