package com.systex.jbranch.app.server.fps.pms324;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;
/**
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 理專晉級報表InputVO<br>
 * Comments Name : PMS324InputVO.java<br>
 * Author :zhouyiqiong<br>
 * Date :2016年11月12日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2016年11月12日<br>
 */
public class PMS324InputVO extends PagingInputVO{
	private String startTime;
	private String endTime;
	private String execTime;
	private String fileName;// 上傳檔案名
	private int eiss_cnt;
	private int loan_cont;
	private String role;       //登陸者角色
	private String ao_code;    //理專
	private String branch_nbr;            //分行
	private String region_center_id;     //區域中心
	private String branch_area_id;		   //營運區
	private String emp_id;
	
	public int getEiss_cnt()
	{
		return eiss_cnt;
	}
	public void setEiss_cnt(int eiss_cnt)
	{
		this.eiss_cnt = eiss_cnt;
	}
	public int getLoan_cont()
	{
		return loan_cont;
	}
	public void setLoan_cont(int loan_cont)
	{
		this.loan_cont = loan_cont;
	}
	public String getFileName()
	{
		return fileName;
	}
	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}
	public String getRole()
	{
		return role;
	}
	public void setRole(String role)
	{
		this.role = role;
	}
	public String getAo_code()
	{
		return ao_code;
	}
	public void setAo_code(String ao_code)
	{
		this.ao_code = ao_code;
	}
	public String getBranch_nbr()
	{
		return branch_nbr;
	}
	public void setBranch_nbr(String branch_nbr)
	{
		this.branch_nbr = branch_nbr;
	}
	public String getRegion_center_id()
	{
		return region_center_id;
	}
	public void setRegion_center_id(String region_center_id)
	{
		this.region_center_id = region_center_id;
	}
	public String getBranch_area_id()
	{
		return branch_area_id;
	}
	public void setBranch_area_id(String branch_area_id)
	{
		this.branch_area_id = branch_area_id;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getExecTime() {
		return execTime;
	}
	public void setExecTime(String execTime) {
		this.execTime = execTime;
	}
	public String getEmp_id() {
		return emp_id;
	}
	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}
	
}
