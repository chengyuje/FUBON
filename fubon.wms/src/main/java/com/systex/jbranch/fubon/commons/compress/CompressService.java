package com.systex.jbranch.fubon.commons.compress;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

// import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
// import net.lingala.zip4j.util.Zip4jConstants;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import org.springframework.context.annotation.Scope;

import com.systex.jbranch.platform.common.errHandle.APException;

/**
 * 壓縮服務器
 * @author Eli
 * @date 2017/12/15
 *
 */
@Scope("prototype")
@SuppressWarnings({ "rawtypes", "unchecked"})
public class CompressService {
	/**壓縮來源*/
	private Path sourcePath;
	
	/**壓縮目的地*/
	private Path targetPath;
	
	/**壓縮準則*/
	private String basisSrc;
	
	/**刪除準則*/
	private String basisTar;
	
	/**預設壓縮檔名，格式為當日(yyyyMMdd).zip*/
	private static final Path defaultCompressName = Paths.get(new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".zip");
	
	/**日期格式 yyyyMMdd */
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	/**封閉建構子*/
	private CompressService(){}

	/**取得實例*/
	public static CompressService getService() {
		return new CompressService();
	}

	/**
	 * 執行壓縮動作
	 * @param compressInfoMap
	 * 		<b>應包含四個key值<br>
	 * <ui>
	 * 		<li>SOURCE_PATH: 壓縮目錄</li>
	 * 		<li>TARGET_PATH: 壓縮目的地</li>
	 * 		<li>BASIS_SRC: 壓縮檔案的判斷條件(by date)</li>
	 * 		<li>BASIS_TAR: 刪除指定路徑的檔案的判斷條件(by date)</li>
	 * </ui>
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * @throws APException
	 * @throws ZipException
	 */
	public void compress(Map compressInfoMap) throws FileNotFoundException, IOException, ParseException, APException, ZipException{
		set(compressInfoMap);
		checkSource();
		buildTarget();
		doCompress();
		del(targetPath, basisTar);
	}
	
	/**將map值取出設值*/
	private void set(Map compressInfoMap) {
	    sourcePath = Paths.get((String)compressInfoMap.get("SOURCE_PATH"));
		targetPath = Paths.get((String)compressInfoMap.get("TARGET_PATH"));
		basisSrc = (String)compressInfoMap.get("BASIS_SRC");
		basisTar = (String)compressInfoMap.get("BASIS_TAR");
	}

	/**將目的地目錄確實建立*/
	private void buildTarget() throws IOException {
		if(Files.notExists(targetPath)) Files.createDirectories(targetPath);
	}

	/**迭代刪除符合basis的檔案*/
	private void iteratorDel(Iterable<Path> paths, String basis) throws IOException, ParseException {
		for (Path each: paths) {
			if (Files.isDirectory(each)) del(each, basis);
			if (basis(each, basis)) Files.deleteIfExists(each);
		}
	}
	
	/**刪除目標符合basis的檔案*/
	private void del(Path path, String basis) throws IOException, ParseException {
		try (
			DirectoryStream<Path> stream = Files.newDirectoryStream(path);	
		) {
			iteratorDel(stream, basis);
		}
	}

	/**執行壓縮任務 */
	private void doCompress() throws  ZipException, IOException, ParseException {
		final List<Path> srcPaths = getSrcPaths();
		zipSrc(srcPaths);
		iteratorDel(srcPaths, basisSrc);
	}
	
	/**壓縮動作*/
	private void zipSrc(List<Path> srcPaths) throws ZipException {
		ZipFile zipfile = new ZipFile(targetPath.resolve(defaultCompressName).toString());
		ZipParameters parameters = new ZipParameters();
		setZipParameters(parameters);
		
		ArrayList<File> fileList = new ArrayList();
		for (Path each: srcPaths) {
			if (Files.isDirectory(each)) zipfile.addFolder(each.toFile(), parameters);
			else fileList.add(each.toFile());
		}
		if(fileList.size() > 0) zipfile.addFiles(fileList, parameters);
	}

	/**設定parameters*/
	private void setZipParameters(ZipParameters parameters) {
		//定義壓縮方式
		parameters.setCompressionMethod(CompressionMethod.DEFLATE); // Zip4jConstants.COMP_DEFLATE);
		//壓縮等級
		parameters.setCompressionLevel(CompressionLevel.ULTRA); // Zip4jConstants.DEFLATE_LEVEL_NORMAL);
	}

	/**得到來源檔案paths */
	private List<Path> getSrcPaths() throws IOException, ParseException {
		List<Path> srcPaths = new ArrayList();
		try (
			DirectoryStream<Path> srcStream  = Files.newDirectoryStream(sourcePath);
		) {
			for (Path file: srcStream) 
				if (!Files.isSameFile(file, targetPath) //避免壓縮目的在sourcePath裡面，一起加入壓縮行列 
						&&	basis(file, basisSrc)) srcPaths.add(file);
		}
		return srcPaths;
	}

	/**依據日期邏輯*/
	private boolean basis(Path file, String basis) throws IOException, ParseException{
		return basisByDate(file, getBasisTime(basis));
	}

	/**取得依據時間*/
	private Date getBasisTime(String basis) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, oppositeSign(Integer.valueOf(basis))); 
		return c.getTime();
	}

	//取相反符號 =>  oppositeSign(3) => -3
	private int oppositeSign(Integer num) {
		return ~num+1;
	}

	/**使用依據日期判斷檔案是否符合*/
	private boolean basisByDate(Path file, Date date) throws IOException, ParseException {
		BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
		return parse(attrs.creationTime()).before(date) 
				|| parse(attrs.lastModifiedTime()).before(date);
	}

	//解析FileTime
	private Date parse(FileTime time) throws ParseException {
		return sdf.parse(time.toString().substring(0,10));
	}

	/**確認來源是否存在*/
	private void checkSource() throws APException {
		if (Files.notExists(sourcePath)) throw new APException(String.format("%s 來源不存在，無法執行", sourcePath));
	}
}
