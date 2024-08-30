package com.systex.jbranch.app.server.fps.pms338;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;
/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 房貸壽險佣金報表InputVO<br>
 * Comments Name : PMS338InputVO.java<br>
 * Author :zhouyiqiong<br>
 * Date :2016年11月15日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2016年11月15日<br>
 */
public class PMS338InputVO extends PagingInputVO{	
	private String sTime;
	private String data_date;
	private String mon;
	private String loanType;
	private String yearMon;
	
	public String getYearMon()
	{
		return yearMon;
	}
	public void setYearMon(String yearMon)
	{
		this.yearMon = yearMon;
	}
	public String getLoanType()
	{
		return loanType;
	}
	public void setLoanType(String loanType)
	{
		this.loanType = loanType;
	}
	public String getMon()
	{
		return mon;
	}
	public void setMon(String mon)
	{
		this.mon = mon;
	}
	public String getLoan_type()
	{
		return loan_type;
	}
	public void setLoan_type(String loan_type)
	{
		this.loan_type = loan_type;
	}
	private String loan_type;
	
	public String getsTime()
	{
		return sTime;
	}
	public void setsTime(String sTime)
	{
		this.sTime = sTime;
	}
	public String getData_date() {
		return data_date;
	}
	public void setData_date(String data_date) {
		this.data_date = data_date;
	}
}