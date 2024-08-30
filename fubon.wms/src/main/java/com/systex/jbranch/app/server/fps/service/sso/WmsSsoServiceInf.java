package com.systex.jbranch.app.server.fps.service.sso;

import com.systex.jbranch.comutil.collection.GenericMap;

public interface WmsSsoServiceInf {
	public GenericMap tokenVerification(GenericMap paramGmap) throws Exception;
}
