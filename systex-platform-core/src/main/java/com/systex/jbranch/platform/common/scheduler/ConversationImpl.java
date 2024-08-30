package com.systex.jbranch.platform.common.scheduler;

import java.util.Map;

import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.server.conversation.ConversationIF;
import com.systex.jbranch.platform.server.conversation.MapToaIF;
import com.systex.jbranch.platform.server.conversation.ObjectToaIF;
import com.systex.jbranch.platform.server.conversation.TiaHelperIF;
import com.systex.jbranch.platform.server.conversation.TiaIF;
import com.systex.jbranch.platform.server.conversation.ToaHelperIF;
import com.systex.jbranch.platform.server.conversation.message.EnumMessageType;
import com.systex.jbranch.platform.server.conversation.message.EnumShowType;
import com.systex.jbranch.platform.server.conversation.message.MapTia;
import com.systex.jbranch.platform.server.conversation.message.MapToa;
import com.systex.jbranch.platform.server.conversation.message.Tia;

public class ConversationImpl implements ConversationIF{

	private UUID uuid;
	final Tia tia=new Tia();
	
	public ConversationImpl(UUID uuid, Map map){
		this.uuid = uuid;
		this.tia.setBody(map);		
		this.tiaHelper=new TiaHelperIF()
		{
			public TiaIF getTia() {		
				return tia;
			}
		};
	}
	
	public TiaHelperIF getTiaHelper() {
		return tiaHelper;
	}

	public ToaHelperIF getToaHelper() {		
		return toaHelper;
	}

	public UUID getUUID() {
		return uuid;
	}
	
	private ToaHelperIF toaHelper = new ToaHelperIF()
	{

		public MapToaIF createMapToa() {

			return new MapToa();
		}

		public ObjectToaIF createObjectToa() {
			// TODO Auto-generated method stub
			return null;
		}

		public void sendTOA(
				com.systex.jbranch.platform.server.conversation.ToaIF arg0) {
			// TODO Auto-generated method stub
			
		}

		public void sendTOA(
				com.systex.jbranch.platform.server.conversation.ToaIF arg0,
				boolean arg1) {
			// TODO Auto-generated method stub
			
		}

		public void sendTOA(EnumShowType arg0, EnumMessageType arg1,
				String arg2, String arg3) {
			// TODO Auto-generated method stub
			
		}

		public void sendTOA(EnumShowType arg0, EnumMessageType arg1,
				String arg2, String arg3, boolean arg4) {
			// TODO Auto-generated method stub
			
		}

	};
	
	private TiaHelperIF tiaHelper = new TiaHelperIF()
	{											
		public TiaIF getTia()
		{
			return new MapTia();
		}
	};
}
