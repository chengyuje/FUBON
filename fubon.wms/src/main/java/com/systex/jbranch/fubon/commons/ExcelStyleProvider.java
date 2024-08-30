package com.systex.jbranch.fubon.commons;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;

/**
 * @author: eli
 * @date 20190415 旨在提供 Excel 標準樣式。減少重複的 Code 與集中管理 Style。
 */
public class ExcelStyleProvider {
    /**
     * 取得預設 Header 樣式
     *
     * @param style
     * @return
     */
    public static XSSFCellStyle renderDefaultHeader(XSSFCellStyle style) {
        style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        style.setFillForegroundColor(new XSSFColor(new java.awt.Color(123, 189, 224)));
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderBottom((short) 1);
        style.setBorderTop((short) 1);
        style.setBorderLeft((short) 1);
        style.setBorderRight((short) 1);
        style.setWrapText(true);
        return style;
    }

    /**
     * 取得預設 Header 字體
     *
     * @param font
     * @return
     */
    public static XSSFFont renderDefaultHeaderFont(XSSFFont font) {
        font.setColor(HSSFColor.WHITE.index);
        font.setBold(true);
        return font;
    }


}
