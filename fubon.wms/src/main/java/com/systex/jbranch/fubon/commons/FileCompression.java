package com.systex.jbranch.fubon.commons;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Component
public class FileCompression 
{
	public enum COMPRESSION_TYPE {ZIP, GZIP}
	
	private final String[] FILE_EXTENSION_NAME = {"zip", "gz", "7z", "rar"};
	private final int FILE_BUFFER = 1024;
	private String inputFilePath;
	private String outputFileFolder;
	private COMPRESSION_TYPE compressionType = COMPRESSION_TYPE.ZIP;
	
	/**
	 * 取得壓縮檔案類型，預設為ZIP。
	 * @return
	 */
	public COMPRESSION_TYPE getCompressionType() {
		return compressionType;
	}
	/**
	 * 設定壓縮檔案類型
	 * @param compressionType
	 */
	public void setCompressionType(COMPRESSION_TYPE compressionType) {
		this.compressionType = compressionType;
	}
	/**
	 * 取得原始輸入檔案名稱路徑
	 * @return
	 */
	public String getInputFilePath() {
		return inputFilePath;
	}
	/**
	 * 設定原始輸入檔案名稱路徑
	 * @param inputFilePath
	 */
	public void setInputFilePath(String inputFilePath) {
		this.inputFilePath = inputFilePath;
	}
	/**
	 * 取得輸出檔案目錄
	 * @return
	 */
	public String getOutputFileFolder() {
		return outputFileFolder;
	}
	/**
	 * 設定輸出檔案目錄
	 * @param outputFileFolder
	 */
	public void setOutputFileFolder(String outputFileFolder) {
		this.outputFileFolder = outputFileFolder;
	}

	public FileCompression()
	{
		this.inputFilePath = "";
		this.outputFileFolder = "";
	}
	
	public FileCompression(String inputFilePath, String outputFileFolder)
	{
		this.inputFilePath = inputFilePath;
		this.outputFileFolder = outputFileFolder;
	}
	
	/**
	 * 執行檔案壓縮作業
	 * 
	 * @return 壓縮檔案名稱路徑
	 */
	public String compress() throws Exception
	{
		String outputFilePath = "";
		
		switch (this.compressionType) {
			case ZIP:
				outputFilePath = zipFile();
				break;
			case GZIP:
				outputFilePath = gzipFile();
				break;
		}
		
		return outputFilePath;
	}
	
	/**
	 * 執行檔案解壓縮作業
	 * 
	 * @return 解壓縮檔案名稱路徑清單
	 */
	public List<String> decompress() throws Exception
	{
		List<String> outputFilePath = new ArrayList();
		
		switch (this.compressionType) {
			case ZIP:
				outputFilePath = unzipFile();
				break;
			case GZIP:
				outputFilePath = ungzipFile();
				break;
		}
		
		return outputFilePath;
	}
	
	/**
	 * 使用ZIP格式進行檔案壓縮
	 * 
	 * @return 壓縮檔案名稱路徑
	 */
	private String zipFile() throws Exception
	{
		String targetFilePath = "";
		
        try {
        	File sourceFile = new File(this.inputFilePath);
        	FileInputStream sourceFileStream = new FileInputStream(sourceFile);
        	targetFilePath = this.outputFileFolder + File.separator + generateCompressFileName(sourceFile.getName());
            ZipOutputStream zipTargetFileStream = new ZipOutputStream(new FileOutputStream(targetFilePath));
            ZipEntry zipEntry = new ZipEntry(sourceFile.getName());
            zipTargetFileStream.putNextEntry(zipEntry);         
            byte[] buffer = new byte[FILE_BUFFER];
            int bytesRead;

            while ((bytesRead = sourceFileStream.read(buffer)) > 0) {
            	zipTargetFileStream.write(buffer, 0, bytesRead);
            }
 
            zipTargetFileStream.closeEntry();
            zipTargetFileStream.close();
            sourceFileStream.close();
            
            //System.out.println("Regular file :" + this.inputFilePath + " is zipped to archive :" + targetFilePath);            
        } 
        catch (IOException e) {
            throw new Exception(e.getMessage());
        }
        
        return targetFilePath;
    }
	
	/**
	 * 依據壓縮格式，產生對應的壓縮檔案名稱。(副檔名)
	 * 
	 * @param sourceFileName 來源檔案名稱
	 * @return 壓縮檔案名稱
	 */
	private String generateCompressFileName(String sourceFileName)
	{
		int lastDotPosition = sourceFileName.lastIndexOf(".");
		String fileName = sourceFileName.substring(0, lastDotPosition+1);
		String compressFileName = "";
		
		switch (this.compressionType) {
		case ZIP:
			compressFileName = fileName + this.FILE_EXTENSION_NAME[this.compressionType.ordinal()];
			break;
		case GZIP:
			compressFileName = sourceFileName + "." + this.FILE_EXTENSION_NAME[this.compressionType.ordinal()];
			break;
		}
		
		return compressFileName;
	}
	
	/**
	 * 使用gZIP格式進行檔案壓縮
	 * 
	 * @return 壓縮檔案名稱路徑
	 */
	private String gzipFile() throws Exception
	{
		String targetFilePath = "";
		
        try {
        	File sourceFile = new File(this.inputFilePath);
        	FileInputStream sourceFileStream = new FileInputStream(sourceFile);
        	targetFilePath = this.outputFileFolder + File.separator + generateCompressFileName(sourceFile.getName());
            GZIPOutputStream gzipTargetFileStream = new GZIPOutputStream(new FileOutputStream(targetFilePath));
            byte[] buffer = new byte[FILE_BUFFER];
            int bytesRead;

            while ((bytesRead = sourceFileStream.read(buffer)) > 0) {
            	gzipTargetFileStream.write(buffer, 0, bytesRead);
            }
 
            gzipTargetFileStream.finish();
            gzipTargetFileStream.close();
            sourceFileStream.close();
            
            //System.out.println("Regular file :" + this.inputFilePath + " is zipped to archive :" + targetFilePath);            
        } 
        catch (IOException e) {
            throw new Exception(e.getMessage());
        }
        
        return targetFilePath;
    }
	
	/**
	 * 使用ZIP格式進行檔案解壓縮
	 * @return
	 */
	private List<String> unzipFile() throws Exception
	{
		List<String> outputFilePathList = new ArrayList();
		
        try {
        	File sourceFile = new File(this.inputFilePath);
        	ZipInputStream zipSourceFileStream = new ZipInputStream(new FileInputStream(sourceFile));
        	ZipEntry zipEntry = zipSourceFileStream.getNextEntry();

            while (zipEntry != null) {
            	String outputFilePath = this.outputFileFolder + File.separator + zipEntry.getName();

				if(outputFilePath.matches(".*\\\\$|.*/$")) {
					new File(outputFilePath).mkdir();
				}else {
					FileOutputStream targetFileStream = new FileOutputStream(new File(outputFilePath));
					byte[] buffer = new byte[FILE_BUFFER];
					int bytesRead;

					while ((bytesRead = zipSourceFileStream.read(buffer)) > 0) {
						targetFileStream.write(buffer, 0, bytesRead);
					}

					targetFileStream.close();
					outputFilePathList.add(outputFilePath);
					//System.out.println("Zip file :" + this.inputFilePath + " is unzipped to archive :" + outputFilePath);
				}

            	zipEntry = zipSourceFileStream.getNextEntry();
            }
 
            zipSourceFileStream.closeEntry();
            zipSourceFileStream.close();
            
        } 
        catch (IOException e) {
            throw new Exception(e.getMessage());
        }
        
        return outputFilePathList;
    }
	
	/**
	 * 使用gZIP格式進行檔案解壓縮
	 * @return
	 */
	private List<String> ungzipFile() throws Exception
	{
		List<String> outputFilePathList = new ArrayList();
		
        try {
        	File sourceFile = new File(this.inputFilePath);
        	GZIPInputStream gzipSourceFileStream = new GZIPInputStream(new FileInputStream(sourceFile));
        	String targetFileName = this.generateDecompressFileName(sourceFile.getName()); 
            String outputFilePath = this.outputFileFolder + File.separator + targetFileName;
            FileOutputStream targetFileStream = new FileOutputStream(new File(outputFilePath));
           	byte[] buffer = new byte[FILE_BUFFER];
           	int bytesRead;
            	
           	while ((bytesRead = gzipSourceFileStream.read(buffer)) > 0) {
           		targetFileStream.write(buffer, 0, bytesRead);
           	}
            	
        	targetFileStream.close();
        	gzipSourceFileStream.close();
        	outputFilePathList.add(outputFilePath);
  
        	//System.out.println("Zip file :" + this.inputFilePath + " is unzipped to archive :" + outputFilePath);                     
        } 
        catch (IOException e) {
            throw new Exception(e.getMessage());
        }
        
        return outputFilePathList;
    }
	
	/**
	 * 依據壓縮格式，產生對應的解壓縮檔案名稱。
	 * 
	 * @param sourceFileName 來源檔案名稱
	 * @return 解壓縮檔案名稱
	 */
	private String generateDecompressFileName(String sourceFileName)
	{
		int lastDotPosition = sourceFileName.lastIndexOf(".");
		String fileName = sourceFileName.substring(0, lastDotPosition);
		
		return fileName;
	}
}

