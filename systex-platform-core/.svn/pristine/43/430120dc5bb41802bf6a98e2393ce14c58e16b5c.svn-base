package com.systex.jbranch.platform.common.report.generator.jfreechart;

import com.systex.jbranch.platform.common.dataManager.DataManager;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class Chart {
// ------------------------------ FIELDS ------------------------------

    private static Font plotFont = new Font("新明細體", Font.PLAIN, 14);
    private static Font titleFont = new Font("新明細體", Font.PLAIN, 18);

    private static List<TexturePaint> texturePaints = new ArrayList<TexturePaint>();
    private static Logger logger = LoggerFactory.getLogger(Chart.class);

    private static int SEQ = 0;
    protected boolean legend = true;
    protected String noData = "NO DATA";

// -------------------------- STATIC METHODS --------------------------

    static {
        DataManager dataManager = new DataManager();
        String separator = (String) System.getProperty("file.separator");
        String jBranchRootPath = dataManager.getSystem().getRoot();
//		String jBranchRootPath = "C:\\JBranch";	//單機測試時使用
        String patternPath = jBranchRootPath + separator + "Platform" + separator + "Report" + separator + "pattern" + separator;
        File f = new File(patternPath);
        if (f.isDirectory()) {
            File[] patterFiles = f.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    if (name.endsWith(".jpg") || name.endsWith("png")) {
                        return true;
                    }
                    return false;
                }
            });

            for (int i = 0; i < patterFiles.length; i++) {
                try {
                    BufferedImage bi = ImageIO.read(patterFiles[i]);
                    TexturePaint image = new TexturePaint(bi, new Rectangle(200, 200));
                    texturePaints.add(image);
                }
                catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * @return texturePaints 取得網點圖庫
     */
    protected static List<TexturePaint> getTexturePaints() {
        return texturePaints;
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * @param title  圖表標題
     * @param width  產生圖表寬度
     * @param height 產生圖表高度
     * @param path   儲存圖表路徑
     * @return 產生完圖表完整路徑
     * @throws Exception
     */
    abstract public String create(String title, int width, int height, String path) throws Exception;

    /**
     * @return plotFont 回傳內容文字預設字型
     */
    protected Font getDefaultPlotFont() {
        return plotFont;
    }

    /**
     * @return titleFont 取得標題預設字型
     */
    protected Font getDefaultTitleFont() {
        return titleFont;
    }

    /**
     * @param chart  jfreechart圖表
     * @param path   儲存路徑
     * @param width  產生圖表寬度
     * @param height 產生圖表高度
     * @return 產生完圖表完整路徑
     * @throws IOException
     */
    protected String save(JFreeChart chart, String path, int width, int height) throws IOException {
        File f = new File(path + "temp_" + getNexSEQ() + ".jpg");
        ChartUtilities.saveChartAsPNG(f, chart, width, height);
        return f.getAbsolutePath();
    }

    /**
     * @return SEQ 取得下個序號
     */
    protected synchronized static int getNexSEQ() {
        SEQ++;
        return SEQ;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    /**
     * @return 取得無資料時顯示文字
     */
    protected String getNoData() {
        return noData;
    }

    /**
     * @param noData 指定無資料時顯示文字
     */
    protected void setNoData(String noData) {
        this.noData = noData;
    }

    /**
     * @return 是否顯示圖例
     */
    protected boolean isLegend() {
        return legend;
    }

    /**
     * @param legend 指定使用圖例與否
     */
    protected void setLegend(boolean legend) {
        this.legend = legend;
    }
}
