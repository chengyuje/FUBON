package com.systex.jbranch.fubon.commons;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.stereotype.Component;
/**
 * <h4>FileCompressionExp 檔案壓縮<b>加強版</b></h4>
 * <p>
 * 		該類別繼承了 {@link FileCompression}。<br>
 * 		主要增加 {@link FileCompressionExp#compressMany()}方法，用以壓縮多個檔案。
 * 		並且設置相關setter & getter，讓使用者在執行該方法<code>compressMany()</code>
 * 		前，進行相關的參數設置。
 * </p>
 * @author Eli
 * @date 2017/12/08
 *
 */
@Component
@SuppressWarnings({ "rawtypes", "unchecked"})
public class FileCompressionExp extends FileCompression
{
	/**該list存放所有壓縮前的應刪除檔案*/
	private List<Path> delList = new ArrayList<Path>();
	private Path sourcePath;
	private Path targetPath;
	private boolean delAfterCompress;
	public boolean isDelAfterCompress() {
		return delAfterCompress;
	}
	public void setDelAfterCompress(boolean delAfterCompress) {
		this.delAfterCompress = delAfterCompress;
	}

	private final int FILE_BUFFER = 1024;
	
	public Path getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = Paths.get(sourcePath);
	}
	
	public void setSourcePath(Path sourcePath) {
		this.sourcePath = sourcePath;
	}

	public Path getTargetPath() {
		return targetPath;
	}

	public void setTargetPath(String targetPath) {
		this.targetPath = Paths.get(targetPath);
	}
	
	public void setTargetPath(Path targetPath) {
		this.targetPath = targetPath;
	}

	/**
	 * 執行多個檔案壓縮作業
	 * 
	 * @return List: 成功壓縮的檔案list
	 */
	public List compressMany() throws Exception {
		return zipFiles();
	}
	
	/**
	 * 執行多個檔案壓縮作業
	 * @param pathList : 壓縮路徑集合
	 * @return List : 成功壓縮的檔案集合
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public List compressMany(List<Path> pathList) throws FileNotFoundException, IOException {
		 List zippedFiles = new ArrayList();
		 try (
			 ZipOutputStream zipTargetFileStream
			 = new ZipOutputStream(
					new FileOutputStream(targetPath.toFile()));
		 ){
			 zippedFiles.addAll(processZip(zipTargetFileStream, pathList));
		 }		 
		 return zippedFiles;
	}

	/**
	 * 使用ZIP格式進行檔案壓縮
	 * 
	 * @return 壓縮檔案名稱路徑
	 */
	private List zipFiles() throws Exception {
        List zippedFiles = null;
		try (
        	ZipOutputStream zipTargetFileStream
        	= new ZipOutputStream(
        			new FileOutputStream(targetPath.toFile()));
        	DirectoryStream<Path> srcStream
        	= Files.newDirectoryStream(sourcePath);
        ){
			zippedFiles = processZip(zipTargetFileStream, srcStream);
        } 
        catch (Exception e) {
            throw new Exception(e.getMessage());
        }
		return zippedFiles;
    }

	/**該方法被{@link FileCompressionExp#zipFiles()}所呼叫，負責壓縮多個檔案邏輯*/
	private List processZip(ZipOutputStream zipTargetFileStream,
			Iterable<Path> srcStream) throws FileNotFoundException, IOException {
		List zippedFiles = new ArrayList<>();
		for (Path eachFile : srcStream) {
        	if (pickout(eachFile)) continue;
        	zippedFiles.add(writeToZip(zipTargetFileStream, eachFile));
        }
		if (isDelAfterCompress()) delLogic();
		return zippedFiles;
	}

	/**將檔案寫進zip*/
	private String writeToZip(ZipOutputStream zipTargetFileStream, Path eachFile) throws IOException {
		try(FileInputStream sourceFileStream = new FileInputStream(eachFile.toFile())){
    		ZipEntry zipEntry = new ZipEntry(eachFile.getFileName().toString());
            zipTargetFileStream.putNextEntry(zipEntry);         
            byte[] buffer = new byte[FILE_BUFFER];
            int bytesRead;
            while ((bytesRead = sourceFileStream.read(buffer)) > 0) {
            	zipTargetFileStream.write(buffer, 0, bytesRead);
            }
            zipTargetFileStream.closeEntry();
            return eachFile.getFileName().toString();
    	}
	}
	/**刪除檔案邏輯*/
	private void delLogic() throws IOException {
		for (Path file: delList) {
			Files.deleteIfExists(file);
		}
	}
	
	/**挑選適合解壓縮的檔案以及刪除檔案*/
	private boolean pickout(Path eachFile) throws IOException {
		if (eachFile.getFileName().toString().indexOf("zip") > -1) {
			return true;
		} else if (!Files.isRegularFile(eachFile) || Files.size(eachFile) <= 0) {
			delList.add(eachFile);
			return true;
		} else {
			delList.add(eachFile);
			return false;
		}
	}
}

