package com.systex.jbranch.common.io.util;

import java.util.List;

import org.springframework.stereotype.Component;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfWriter;
import com.systex.jbranch.fubon.commons.PdfUtil;
import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.ThreadDataPool;

@Component
public class PdfInputOutputUtils extends InputOutputUtils{
	public String doWritePdfFile(byte[] stream) throws JBranchException{
		return super.doWriteFile(stream , "pdf");
	}
	
	public String doWritePdfFile(byte[] stream , String fileName) throws JBranchException{
		return super.doWriteFile(stream , fileName , "pdf");
	}
	
	public String doWriteMergePdfFile(List<String> tmpPaths , boolean isAddBlank) throws JBranchException {
		return PdfUtil.mergePDF(tmpPaths , isAddBlank);
    }
	
	public void close(Object...closes){
		for(Object close : closes){
			if(close instanceof PdfWriter){
				PdfWriter pw = ((PdfWriter)close);
				
				if(!pw.isCloseStream()){
					pw.close();
				}
			}
			else if(close instanceof Document){
				Document docClose = (Document)close;
				
				if(docClose.isOpen()){
					docClose.close();
				}
			}else{
				super.close(closes);
			}
		}
	}
	
	public String getUuid(){
		UUID uuid = (UUID) ThreadDataPool.getData(ThreadDataPool.KEY_UUID);
		uuid = uuid == null ? new UUID() : uuid;
		return uuid.toString().replaceAll(":", "_");
	}
}
