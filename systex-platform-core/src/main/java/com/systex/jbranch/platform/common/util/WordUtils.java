package com.systex.jbranch.platform.common.util;

import java.io.File;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;


public class WordUtils {
	public static final int WML = 11;
	public static final int PDF = 17;

	public static final int wdStatisticPages = 2;

	/**
	* 轉換doc文件為word支援格式
	* @param fileFormat 檔換格式
	* @param sourcePath doc來源路徑
	* @param desPath 目地路徑
	* @return 目地文件
	* @throws Exception
	*/
	public static WordContext convertTo(int fileFormat, String sourcePath,String desPath) throws Exception{

	   //Thread init
	   ComThread.InitSTA();
	    // Instantiate app
	        ActiveXComponent app = new ActiveXComponent("Word.Application");  
	        try
	        {  
	        // Set component to hide that is opened
	            app.setProperty("Visible", new Variant(false));
	            // Instantiate the Documents Property
	            Dispatch docs = app.getProperty("Documents").toDispatch();  
	            // Open a word document
	            Dispatch doc = Dispatch.invoke( docs,  
	                    "Open",  
	                     Dispatch.Method,  
	                     new Object[] { sourcePath, new Variant(false),  
	                     new Variant(true) }, new int[1]).toDispatch();  
	            
	            //取得頁數
	            int countOfPage = Dispatch.invoke( doc, "ComputeStatistics",
		                 Dispatch.Method, new Object[] {new Variant(wdStatisticPages)}, new int[1]).getInt();
	            //Save
	            Dispatch.invoke( doc, "SaveAs",
	                 Dispatch.Method, new Object[] {desPath,new Variant(fileFormat)}, new int[1]);  
	            // Close doc
	            Dispatch.call(doc, "Close", new Variant(false)); 
	            WordContext context = new WordContext();
	            context.setCountOfPage(countOfPage);
	            context.setFile(new File(desPath));
	            return context;
	        }  
	        catch (Exception e)  
	        {  
	           throw e;
	        }  
	        finally
	        {  
	        app.invoke("Quit", new Variant[] {});//ActiveXComponent quit
	        ComThread.Release();//Thread release
	        }  
 
	}
 
	static class WordContext{
		
		private int countOfPage;
		private File file;

		public int getCountOfPage() {
			return countOfPage;
		}

		public void setCountOfPage(int countOfPage) {
			this.countOfPage = countOfPage;
		}

		public File getFile() {
			return file;
		}

		public void setFile(File file) {
			this.file = file;
		}
	}
 
	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		WordUtils.convertTo(WML, "d:\\Test\\test.doc", "d:\\Test\\test.xml");
	   long end = System.currentTimeMillis();
	   System.out.println((end - start) / 1000.0 + "秒");
	   System.out.println("end");
	}

}
