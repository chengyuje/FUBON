package com.systex.jbranch.platform.common.dataManager;

import com.systex.jbranch.platform.server.conversation.TiaIF;
import com.systex.jbranch.platform.server.conversation.ToaIF;

import java.io.Serializable;

/**
 * 作為與Client資料流通的暫存區。<br>
 * @author Eric.Lin
 *
 */

public class ConversationVO implements Serializable {

	private  TiaIF   tia;
	private  ToaIF   toa;

	public TiaIF getTia() {
		return tia;
	}
	public void setTia(TiaIF tia) {
		this.tia = tia;
	}
	public ToaIF getToa() {
		return toa;
	}
	public void setToa(ToaIF toa) {
		this.toa = toa;
	}


}
