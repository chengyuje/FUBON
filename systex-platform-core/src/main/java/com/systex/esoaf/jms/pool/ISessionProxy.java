package com.systex.esoaf.jms.pool;

import javax.jms.Session;

public interface ISessionProxy extends Session
{
	Session getInternalSession();
}
