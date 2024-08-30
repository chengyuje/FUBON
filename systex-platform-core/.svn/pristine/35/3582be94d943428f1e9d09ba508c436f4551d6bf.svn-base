package com.systex.jbranch.platform.server.pipeline.flex;

import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.server.conversation.TiaIF;
import com.systex.jbranch.platform.server.conversation.message.EnumTiaHeader;

public class DefaultUUIDGenerator implements UUIDGeneratorIF {

	@Override
	public UUID generateUUID(TiaIF tia)
	{
		UUID uuid = new UUID();
		String ip = PipelineUtil.getRemoteAddr();
		initTlrID(tia, uuid);
		//2010-06-08 
		//  ip:方便識別
		//  applicationID:for相同id可於相同ws登入
		//uuid.setWsId(ip + tlrid);
		initWsID(tia, uuid, ip);
		
		initBranchID(tia, uuid);
		
		//2010-03-23 不提供ClientTransaction功能,故每個request皆編唯一sectionid
		//uuid.setSectionID(tia.Headers().getStr(EnumTiaHeader.SectionID));
		initSectionID(uuid);

		return uuid;
	}

	protected void initSectionID(UUID uuid) {
		uuid.setSectionID(java.util.UUID.randomUUID().toString());
	}

	protected void initBranchID(TiaIF tia, UUID uuid) {
		uuid.setBranchID(tia.Headers().getStr(EnumTiaHeader.BranchID));
	}

	protected void initWsID(TiaIF tia, UUID uuid, String ip) {
		uuid.setWsId(ip + "_" + tia.Headers().getStr(EnumTiaHeader.ApplicationID));
	}

	protected void initTlrID(TiaIF tia, UUID uuid) {
		String tlrid;
		try	{
			tlrid=tia.Headers().getStr(EnumTiaHeader.TlrID);
		}
		catch(Exception e){
			tlrid="";
		}
		uuid.setTellerID(tlrid);
	}

}
