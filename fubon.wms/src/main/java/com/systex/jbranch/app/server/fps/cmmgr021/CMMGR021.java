package com.systex.jbranch.app.server.fps.cmmgr021;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("cmmgr021")
@Scope("request")
public class CMMGR021 extends FubonWmsBizLogic {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());

	private static String MANUAL_FILE = "VFPS系統-操作手冊";  	//指定檔名開頭
	private static String EXT_FILE = ".zip";	//指定副檔名
	
	/**
	 * 取得下載檔案清單
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getFileList(Object body, IPrimitiveMap<?> header) throws JBranchException{
		XmlInfo xmlInfo = new XmlInfo();
		//String path = "D:\\01_Projects\\HNCB\\jboss-eap-6.3\\standalone\\deployments\\fps.war\\temp\\sample\\操作手冊";	//TODO TESTING
		String path = xmlInfo.getVariable("CMMGR021.PARAM", "PATH", FormatHelper.FORMAT_3); 
		File dir = new File(path); 	// 操作手冊路徑
		File fileList[] = dir.listFiles(); // 得出路徑下檔案清單
		CMMGR021OutputVO outputVO = new CMMGR021OutputVO();
		
		try{
		
			if(fileList == null || fileList.length == 0){
//				throw new JBranchException("");
				outputVO.setFileList(null);
				sendRtnObject(outputVO);
				throw new APException("ehl_01_common_001");	
				
			}else{				
				List<Map<String, String>> result = new ArrayList<Map<String, String>>();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
				
				for(int i = 0; i < fileList.length; i++){
					if (fileList[i].isFile()) { // 過濾檔案
						File file = fileList[i];
						
						String fileName = file.getName();
						if (fileName.startsWith(MANUAL_FILE) && fileName.endsWith(EXT_FILE)) { //檔名"VFPS系統-操作手冊"開頭的ZIP檔
							Map<String, String> data = new HashMap<String, String>();
				
							data.put("fileName", fileName);
							data.put("filePath", file.getAbsolutePath());
							data.put("fileSize", (file.length()/1024) + "kb");
							data.put("lastUpdate", sdf.format(file.lastModified()));
							result.add(data);
						}
					}
				}			
				outputVO.setFileList(result);
				sendRtnObject(outputVO);
				
			}
		
		}catch(Exception e){
			logger.debug(e.getMessage(),e);
		}
		
	}
	
	/**
	 * 檔案下載
	 * @param body
	 * @param header
	 * @throws Exception 
	 */
	public void downloadFile(Object body, IPrimitiveMap<?> header) throws Exception{
		CMMGR021InputVO inputVO = (CMMGR021InputVO) body;

		// 將檔名Hex編碼處理並以此在下載資料夾中建立檔案
		final String hexFileName = getTempPath() + System.getProperties().getProperty("file.separator") + Hex.encodeHexString(inputVO.getFileName().getBytes("UTF-8"));
		File fileToDownload = new File(hexFileName);
		
		// 如下載資料夾中尚無此檔案，則自操作手冊路徑中複製出來
		if(!fileToDownload.exists()){
			
			int byteread = 0;
			String filePathOriginal = inputVO.getFilePath();
			
			File fileOriginal = new File(filePathOriginal);	// 原檔
			
			if (fileOriginal.exists()) { // 原檔存在時
				InputStream inStream = new FileInputStream(filePathOriginal);	// 讀入原檔
				FileOutputStream outStream = new FileOutputStream(hexFileName);	// 新檔
				byte[] buffer = new byte[1024];
				
				// 寫入新檔
				while ( (byteread = inStream.read(buffer)) != -1) {
					outStream.write(buffer, 0, byteread);
				}
				
				inStream.close();
				outStream.close();
			}
		}

		// 通知前端下載
		notifyClientToDownloadFile(getRelativePath(fileToDownload.getPath()), inputVO.getFileName());
	}
	
	/**
	 * 取得temp資料夾絕對路徑
	 * @return
	 * @throws JBranchException
	 */
	private String getTempPath() throws JBranchException{
		String serverPath = (String)getCommonVariable(SystemVariableConsts.SERVER_PATH);
		String seperator = System.getProperties().getProperty("file.separator");
		if(!serverPath.endsWith(seperator)){
			serverPath+=seperator;
		}
		return serverPath+"temp";
	}
	
	/**
	 * 取得下載檔案相對路徑 
	 * @param path
	 * @return
	 * @throws JBranchException
	 */
	private String getRelativePath(String path) throws JBranchException{
		String seperator = System.getProperties().getProperty("file.separator");
		String relativePath = StringUtils.remove(path, (String) getCommonVariable(SystemVariableConsts.SERVER_PATH));
		return StringUtils.replace(relativePath, seperator, "/");
	}

}
