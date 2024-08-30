package com.systex.jbranch.platform.common.communication.debug;

public interface DebuggerIF {
	void writeLine(String format,Object... args);
	void writeLine(String arg0);
}
