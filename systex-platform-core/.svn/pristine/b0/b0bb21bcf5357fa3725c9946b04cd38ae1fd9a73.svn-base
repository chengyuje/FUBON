package com.systex.jbranch.platform.host.transform;

import java.util.List;

import com.systex.jbranch.platform.common.errHandle.JBranchException;


public interface GatewayTransformIF {

	/**
	 *送收電文
	 * @param <T> titaVO
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	List<byte[]> sendAndReceive(Object titaVO) throws JBranchException;
	
	 /**
	  * 收電文 (for 折返使用)
	 * @param <T> titaBytes
	 * @return
	 * @throws Exception
	 */
	List<byte[]> sendAndReceive(byte[] titaBytes ) throws JBranchException;
	

	 /**
	  * 送收電文(for Test)
	 * @param titaString
	 * @return
	 * @throws Exception
	 */
	List<byte[]> sendAndReceive(String titaString) throws JBranchException;
}
