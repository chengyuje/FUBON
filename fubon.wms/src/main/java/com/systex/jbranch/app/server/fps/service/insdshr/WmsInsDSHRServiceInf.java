package com.systex.jbranch.app.server.fps.service.insdshr;

import com.systex.jbranch.comutil.collection.GenericMap;

public interface WmsInsDSHRServiceInf {
	public GenericMap setCaseSaveData(GenericMap paramGmap) throws Exception;
	public GenericMap validateInsData(GenericMap paramGmap) throws Exception;
}
