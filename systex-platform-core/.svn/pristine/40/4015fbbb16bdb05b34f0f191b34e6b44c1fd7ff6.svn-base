package com.systex.jbranch.platform.common.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;

public class FileUtil {

	/**
	 * 讀入server上的temp檔案，以List回傳每一列內容，並指定編碼與路徑
	 * @param path server上的路徑
	 * @param fileName 檔名
	 * @param encoding 編碼
	 * @return 檔案內容
	 * @throws JBranchException
	 * @throws IOException
	 */
	public static String readToString(String path, String fileName, String encoding) throws JBranchException, IOException{
		
		return FileUtils.readFileToString(new File(path, fileName), encoding);
	}
	
	/**
	 * 讀入server上的temp檔案，以List回傳每一列內容，並指定編碼
	 * @param fileName 檔名
	 * @param encoding 編碼
	 * @return 檔案內容
	 * @throws JBranchException
	 * @throws IOException
	 */
	public static String readToString(String fileName, String encoding) throws JBranchException, IOException{
		String tempPath = getTempPath();
		return readToString(tempPath, fileName, encoding);
	}
	
	/**
	 * 讀入server上的temp檔案(預設編碼為MS950)，以List回傳每一列內容，
	 * @param fileName 檔名
	 * @return 檔案內容
	 * @throws JBranchException
	 * @throws IOException
	 */
	public static String readToString(String fileName) throws JBranchException, IOException{
		String tempPath = getTempPath();
		return readToString(tempPath, fileName, "MS950");
	}
	
	/**
	 * 讀入server上的temp檔案，並指定編碼與路徑
	 * @param path server上的路徑
	 * @param fileName 檔名
	 * @param encoding 編碼
	 * @return 檔案內容
	 * @throws JBranchException
	 * @throws IOException
	 */
	public static List readLines(String path, String fileName, String encoding) throws JBranchException, IOException{
		
		return FileUtils.readLines(new File(path, fileName), encoding);
	}
	
	/**
	 * 讀入server上的temp檔案，並指定編碼
	 * @param fileName 檔名
	 * @param encoding 編碼
	 * @return 檔案內容
	 * @throws JBranchException
	 * @throws IOException
	 */
	public static List readLines(String fileName, String encoding) throws JBranchException, IOException{
		String tempPath = getTempPath();
		return readLines(tempPath, fileName, encoding);
	}
	
	/**
	 * 讀入server上的temp檔案(預設編碼為MS950)
	 * @param fileName 檔名
	 * @return 檔案內容
	 * @throws JBranchException
	 * @throws IOException
	 */
	public static List readLines(String fileName) throws JBranchException, IOException{
		String tempPath = getTempPath();
		return readLines(tempPath, fileName, "MS950");
	}

	/**
	 * 寫入server上的temp檔案，並指定編碼與路徑
	 * @param path server上的路徑
	 * @param fileName 檔名
	 * @param encoding 編碼
	 * @return 檔案內容
	 * @throws JBranchException
	 * @throws IOException
	 */
	public static void writeToString(String path, String fileName, String encoding) throws JBranchException, IOException{
		
		FileUtils.writeStringToFile(new File(path, fileName), encoding);
	}
	
	/**
	 * 寫入server上的temp檔案，並指定編碼
	 * @param fileName 檔名
	 * @param encoding 編碼
	 * @return 檔案內容
	 * @throws JBranchException
	 * @throws IOException
	 */
	public static void writeToString(String fileName, String encoding) throws JBranchException, IOException{
		String tempPath = getTempPath();
		writeToString(tempPath, fileName, encoding);
	}
	
	/**
	 * 寫入server上的temp檔案(預設編碼為MS950)
	 * @param fileName 檔名
	 * @return 檔案內容
	 * @throws JBranchException
	 * @throws IOException
	 */
	public static void writeToString(String fileName) throws JBranchException, IOException{
		String tempPath = getTempPath();
		writeToString(tempPath, fileName, "MS950");
	}
	
	/**
	 * @param fileName 來源檔案
	 * @param skip 略過長度
	 * @param length 讀取長度
	 * @return
	 * @throws IOException
	 * @throws JBranchException
	 */
	public static String randomAccess(String fileName, int skip, int length) throws IOException, JBranchException{
		return randomAccess(fileName, skip, length, "MS950");
	}
	
	/**
	 * @param fileName 來源檔案
	 * @param skip 略過長度
	 * @param length 讀取長度
	 * @param encoding 編碼
	 * @return
	 * @throws IOException
	 * @throws JBranchException
	 */
	public static String randomAccess(String fileName, int skip, int length, String encoding) throws IOException, JBranchException{
		String tempPath = getTempPath();
		return randomAccess(tempPath, fileName, skip, length, encoding);
	}
	
	/**
	 * @param path 來源路徑
	 * @param fileName 來源檔案
	 * @param skip 略過長度
	 * @param length 讀取長度
	 * @param encoding 編碼
	 * @return
	 * @throws IOException
	 */
	public static String randomAccess(String path, String fileName, int skip, int length, String encoding) throws IOException{
		return randomAccess(new File(path, fileName), skip, length, encoding);
	}
	
	/**
	 * @param file 來源檔案
	 * @param skip 略過長度
	 * @param length 讀取長度
	 * @param encoding 編碼
	 * @return
	 * @throws IOException
	 */
	public static String randomAccess(File file, int skip, int length, String encoding) throws IOException{

		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(file, "r");
			raf.skipBytes(skip);
			byte[] buf = new byte[length];
			raf.read(buf, 0, buf.length);
			return new String(buf, encoding);
		} finally{
			if(raf != null){
				try{
					raf.close();
				}catch(Exception e){
					
				}
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		String filename = "C:\\Users\\Angus\\Desktop\\test.txt";
		String str = randomAccess(new File(filename), 26, 24, "MS950");
		System.out.println(str);
	}
	
	private static String getTempPath() throws JBranchException {
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		return tempPath;
	}
}
