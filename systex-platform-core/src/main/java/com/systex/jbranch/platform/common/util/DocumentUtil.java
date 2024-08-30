package com.systex.jbranch.platform.common.util;

import java.io.File;
import java.io.FileOutputStream;

import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfReader;
import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

public class DocumentUtil {

	public static String megerPDF(String[] paths) throws JBranchException{
		String serverHome = PathUtil.getServerHome();
		UUID uuid = (UUID) ThreadDataPool.getData(ThreadDataPool.KEY_UUID);
		if(uuid == null){
			uuid = new UUID();
		}
		String outputName = "temp" + File.separator + uuid.toString() + ".pdf";
		File outputPath = new File(serverHome, outputName);
		PdfCopyFields mergePDF = null;
		try {
			mergePDF = new PdfCopyFields(new FileOutputStream(outputPath));

			for (int i = 0; i < paths.length; i++) {
				File f = new File(serverHome, paths[i]);
				mergePDF.addDocument(new PdfReader(f.getAbsolutePath()));
			}
		} catch (Exception e) {
			throw new JBranchException(e.getMessage(), e);
		}finally{
			if(mergePDF != null){
				mergePDF.close();
			}
		}
		
		return outputName;
	}
}
