package com.systex.jbranch.common.io.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;

@Component
public class InputOutputUtils{
	//用uuid當作檔名產生檔案
	public String doWriteFile(byte[] stream , String suffix) throws JBranchException{
		return doWriteFile(stream , UUID.randomUUID().toString() , suffix);
	}
	
	public String doWriteFile(byte[] stream , String fileName , String suffix) throws JBranchException{
		if(stream == null || stream.length == 0){
			return null;
		}
		
		fileName = fileName + "." + suffix;
		String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
	    String tempPathRelative = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH_RELATIVE);
	    String url = tempPathRelative + fileName;
		FileOutputStream fileOut = null;
		
		try {
			File targetFile = new File(filePath, fileName);
			fileOut = new FileOutputStream(targetFile);
			fileOut.write(stream);
			fileOut.flush();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			close(fileOut);
		}
		
		return url;
	}
	
	public void delete(List<File> files){
        for(File file : files){
        	file.delete();
        }
	}

	public void close(Object...closes){
		for(Object close : closes){
			try {
				if(close == null){
					continue;
				}
				
				if(close instanceof Collection){
					for(Object obj : (Collection) close){
						close(obj);
					}
				}
				else{
					close.getClass().getMethod("close").invoke(close);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
