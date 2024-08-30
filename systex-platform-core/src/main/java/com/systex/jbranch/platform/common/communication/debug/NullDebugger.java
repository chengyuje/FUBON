package com.systex.jbranch.platform.common.communication.debug;


public class NullDebugger implements DebuggerIF {

	public void writeLine(String format, Object... args) {}
	public void writeLine(String arg0) {}

}
