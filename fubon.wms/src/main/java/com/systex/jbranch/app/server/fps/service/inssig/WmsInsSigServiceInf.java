package com.systex.jbranch.app.server.fps.service.inssig;

import java.util.List;

import com.systex.jbranch.comutil.collection.GenericMap;

public interface WmsInsSigServiceInf {
	public GenericMap getWmsJWT(GenericMap paramGmap) throws Exception;
	public GenericMap getSignList(GenericMap paramGmap) throws Exception;
	public GenericMap setCaseStatus(GenericMap paramGmap) throws Exception;
	public GenericMap setCasePDF(GenericMap paramGmap) throws Exception;
}
