package com.systex.jbranch.fubon.bth.job.context;

import java.util.List;

import org.springframework.context.annotation.Scope;

import com.systex.jbranch.fubon.bth.job.inf.FileToTableInf;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;

/**
 * 
 * FileLogic服務器
 * @author Eli
 * @since V1.1版
 * @date 2017/11/13
 * @spec 
 * 		<h1>該服務器必須使用constructor injection 注入欲使用實作<b>FileToTable介面</b>的物件<br>
 * 			否則將會跳出 <code> 初始化物件錯誤！ </code>的訊息</h1>
 * 		<h2>備註 : 管理介面方法的服務器</h2>
 * @see FileToTableInf
 */
@Scope("prototype")
@SuppressWarnings({"rawtypes"})
public class FileContext implements FileToTableInf{
	private FileToTableInf fileLogic;

	//其constructor 初始必須注入該存放器
	public FileContext (String str) throws Exception {
		try {
			fileLogic = (FileToTableInf)PlatformContext.getBean(str);
		} catch (JBranchException e) {
			throw new APException("物件初始化錯誤!");
		}
	}

	@Override
	public String getInsertSQL() {
		return fileLogic.getInsertSQL();
	}

	@Override
	public List getData() throws JBranchException {
		return fileLogic.getData();
	}
}
