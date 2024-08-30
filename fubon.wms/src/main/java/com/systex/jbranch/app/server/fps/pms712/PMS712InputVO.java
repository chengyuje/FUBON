package com.systex.jbranch.app.server.fps.pms712;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

/**
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 專員信用卡業績上傳Controller<br>
 * Comments Name : PMS712.java<br>
 * Author : cty<br>
 * Date :2016年11月17日 <br>
 * Version : 1.0 <br>
 * Editor : cty<br>
 * Editor Date : 2016年11月17日<br>
 */
public class PMS712InputVO extends PagingInputVO
{
	
	private String modelName;  // 上傳文件模板選擇

	public String getModelName()
	{
		return modelName;
	}
	public void setModelName(String modelName)
	{
		this.modelName = modelName;
	}
	
}
