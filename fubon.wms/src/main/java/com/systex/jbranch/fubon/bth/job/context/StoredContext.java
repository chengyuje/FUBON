package com.systex.jbranch.fubon.bth.job.context;

import org.springframework.context.annotation.Scope;

import com.systex.jbranch.fubon.bth.job.inf.IGenerateCsvInfo;
import com.systex.jbranch.fubon.bth.job.inf.IGenerateFileInfo;
import com.systex.jbranch.fubon.bth.job.inf.IPrepareStatementInfo;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;

/**
 * 
 * 資訊存放服務器
 * @author Eli
 * @since V1.1版
 * @date 2017/10/12
 * @spec 
 * 		<h1>該服務器必須使用constructor injection 注入欲使用實作<b>InfoStorage介面</b>的物件<br>
 * 			否則將會跳出 <code> 初始化物件錯誤！ </code>的訊息</h1>
 * 		<h2>備註 : 管理介面方法的服務器</h2>
 * @see IPrepareStatementInfo
 */
@Scope("prototype")
public class StoredContext implements IPrepareStatementInfo, IGenerateCsvInfo, IGenerateFileInfo{
	private IPrepareStatementInfo psInfo;
	private IGenerateCsvInfo csvInfo;
	private IGenerateFileInfo fileInfo;

	//其constructor 初始必須注入該存放器
	public StoredContext (String str) throws Exception {
		try {
			Object obj = PlatformContext.getBean(str);
			if (obj instanceof IPrepareStatementInfo) {
				this.psInfo = (IPrepareStatementInfo)obj;
			}
			if (obj instanceof IGenerateCsvInfo) {
				this.csvInfo = (IGenerateCsvInfo)obj;
			}
			if (obj instanceof IGenerateFileInfo) {
				this.fileInfo = (IGenerateFileInfo)obj;
			}
			
		} catch (JBranchException e) {
			throw new APException("物件初始化錯誤!");
		}
	}
	
	@Override
	public String getSQL() {
		return psInfo.getSQL();
	}
	@Override
	public Object[] getParams() {
		return psInfo.getParams();
	}
	@Override
	public String getFileName() {
		return fileInfo.getFileName();
	}
	@Override
	public String[] getFileColumns() {
		return fileInfo.getFileColumns();
	}
	@Override
	public int[] getFileColWidth() {
		return fileInfo.getFileColWidth();
	}
	@Override
	public String getCsvFileName() {
		return csvInfo.getCsvFileName();
	}
	@Override
	public String[] getCsvHeader() {
		return csvInfo.getCsvHeader();
	}
	@Override
	public String[] getCsvColumn() {
		return csvInfo.getCsvColumn();
	}
}
