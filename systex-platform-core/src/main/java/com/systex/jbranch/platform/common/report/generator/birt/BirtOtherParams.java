package com.systex.jbranch.platform.common.report.generator.birt;

import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.model.api.CellHandle;
import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.ElementFactory;
import org.eclipse.birt.report.model.api.ImageHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.StructureFactory;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.elements.DesignChoiceConstants;
import org.eclipse.birt.report.model.api.elements.structures.EmbeddedImage;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.generator.jfreechart.ChartModel;
import com.systex.jbranch.platform.common.util.ImageUtil;

public class BirtOtherParams {

	private List<String> dropList = new ArrayList<String>();
	private List<String[]> propertypeList = new ArrayList<String[]>();
	private List<String[]> imageList = new ArrayList<String[]>();
	private Map<String, Image> imageMap = new HashMap<String, Image>();
	private ChartModel chartModel = new ChartModel();
	
	/**
	 * 刪除指定的element id
	 * @param elementId 欲刪除的元件
	 */
	public void appendDrop(String elementId){
		dropList.add(elementId);
	}
	
	/**
	 * 改變指定element id屬性
	 * @param elementId 欲改變的元件
	 * @param type 欲改變的屬性
	 * @param value 欲改變的屬性值
	 */
	public void changePropertype(String elementId, String type, String value){
		String[] propertype = new String[3];
		propertype[0] = elementId;
		propertype[1] = type;
		propertype[2] = value;
		propertypeList.add(propertype);
	}
	
	/**
	 * 在指定的element id中，填加圖檔
	 * @param elementId 欲填加的元件
	 * @param path 欲填加的圖檔
	 * @param width 圖檔寬(ex.10 or 10pt)
	 * @param height 圖檔高(ex.10 or 10pt)
	 */
	public void appendImage(String elementId, String path, String width, String height){
		String[] image = new String[4];
		image[0] = elementId;
		image[1] = path;
		image[2] = width;
		image[3] = height;
		imageList.add(image);
	}
	
	/**
	 * 在指定的element id中，填加圖檔
	 * @param elementId 欲填加的元件
	 * @param bufImage 欲填加的影像
	 */
	public void appendImage(String elementId, Image image){
		imageMap.put(elementId, image);
	}
	
	public void process(IReportRunnable design) throws JBranchException{
		try{
			ReportDesignHandle reportHandle = (ReportDesignHandle) design.getDesignHandle();
			
			//移除元件
			for (String elementId : dropList) {
				DesignElementHandle handle = getElementHandle(reportHandle, elementId);
				handle.drop();
			}
			
			//改變屬性
			for (String[] propertype : propertypeList) {
				String elementId = propertype[0];
				String type = propertype[1];
				String value = propertype[2];
				DesignElementHandle handle = getElementHandle(reportHandle, elementId);
				handle.setProperty(type, value);
			}
			
			//填加圖檔
			for (String[] imageParams : imageList) {
				String elementId = imageParams[0];
				String path = imageParams[1];
				String width = imageParams[2];
				String height = imageParams[3];
				
				CellHandle cell = (CellHandle) getElementHandle(reportHandle, elementId);

				ElementFactory df = reportHandle.getElementFactory();
                ImageHandle image = df.newImage(null);
                image.setFile(path);
                image.setWidth(width);
                image.setHeight(height);
                cell.getContent().add(image);
			}
			
			Iterator<Entry<String, Image>> it = imageMap.entrySet().iterator();
			while(it.hasNext()){
				Entry<String, Image> entry = it.next();
				String elementId = entry.getKey();
				Image image = entry.getValue();
				embeddedImage(design, elementId, image);
				
				CellHandle cell = (CellHandle) getElementHandle(reportHandle, elementId);
				ElementFactory df = reportHandle.getElementFactory();
                ImageHandle imageHandle = df.newImage(null);
                imageHandle.setSource(DesignChoiceConstants.IMAGE_REF_TYPE_EMBED);
                imageHandle.setImageName(elementId);
                cell.getContent().add(imageHandle);
			}
			
		}catch(Exception e){
			throw new JBranchException(e.getMessage(), e);
		}
	}
	
	private DesignElementHandle getElementHandle(ReportDesignHandle reportHandle, String id){
		Long eId = 0L;
		try{
			eId = Long.parseLong(id);
		}catch(Exception e){
			throw new RuntimeException("[" + id + "]需為數字");
		}
		DesignElementHandle handle = reportHandle.getElementByID(eId);
		if(handle == null){
			throw new RuntimeException("[" + id + "] not exists.");
		}

		return handle;
	}

	public ChartModel getChartModel() {
		return chartModel;
	}

	public void setChartModel(ChartModel chartModel) {
		this.chartModel = chartModel;
	}
	
    public void embeddedImage(IReportRunnable design, String elementId, Image image) throws IOException, SemanticException{
    	EmbeddedImage embeddedImage = StructureFactory.createEmbeddedImage( );
    	embeddedImage.setType(DesignChoiceConstants.IMAGE_TYPE_IMAGE_JPEG);
    	embeddedImage.setData(ImageUtil.imageToByteArr(image));
    	embeddedImage.setName(elementId);
    	ReportDesignHandle reportDesignHandle = (ReportDesignHandle) design.getDesignHandle();
    	reportDesignHandle.addImage(embeddedImage);
    }
}
