package com.systex.jbranch.platform.common.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.platform.common.dataexchange.FileUtil;
import com.systex.jbranch.platform.common.properties.Big5ToUTF8;

public class StringUtil {
	// ------------------------------ FIELDS ------------------------------

	public static final byte TIEM_FORMAT_EEMMDD = 2;
	public static final byte TIEM_FORMAT_HHmmss = 3;

	public static final byte TIEM_FORMAT_yyyyMMdd = 1;
	public static final byte TIME_FORMAT_yyyyMMddHHmmss = 4;
	private static Logger logger = LoggerFactory.getLogger(StringUtil.class);
	private static String[] TYPE3_BIG5_TO_UTF8_DES_CODE = {};

	private static String[] TYPE3_BIG5_TO_UTF8_SRC_CODE = {};
	private static String df_HHmmss = "HHmmss";
	private static String df_MMdd = "MMdd";
	private static String df_yyyy = "yyyy";

	private static String df_yyyyMMdd = "yyyyMMdd";
	private static String df_yyyyMMddHHmmss = "yyyyMMddHHmmss";
	private static String lineSeparator = System.getProperty("line.separator");

	// -------------------------- STATIC METHODS --------------------------

	static {
		try {
			Big5ToUTF8 bu = (Big5ToUTF8) PlatformContext.getBean("big5ToUTF8");
			String srcCode = bu.getSrcCode();
			String desCode = bu.getDesCode();
			if (srcCode != null && !"".equals(srcCode)) {
				TYPE3_BIG5_TO_UTF8_SRC_CODE = srcCode.split(",");
			}
			if (desCode != null && !"".equals(desCode)) {
				TYPE3_BIG5_TO_UTF8_DES_CODE = desCode.split(",");
			}
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}
	}

	public static String getCurrentDateFormat(byte format) {
		return getDataFormat(format, new Timestamp(System.currentTimeMillis()));
	}

	public static String getDataFormat(byte format, Timestamp time) {
		switch (format) {
		case TIEM_FORMAT_yyyyMMdd:
			return DateFormatUtils.format(time, df_yyyyMMdd);

		case TIEM_FORMAT_EEMMDD:
			int ee = Integer.parseInt(DateFormatUtils.format(time, df_yyyy)) - 1911;
			StringBuffer formatTemp = new StringBuffer();
			formatTemp.delete(0, formatTemp.length());
			formatTemp.append(String.valueOf(ee)).append(DateFormatUtils.format(time, df_MMdd));
			return formatTemp.toString();

		case TIEM_FORMAT_HHmmss:
			return DateFormatUtils.format(time, df_HHmmss);

		case TIME_FORMAT_yyyyMMddHHmmss:
			return DateFormatUtils.format(time, df_yyyyMMddHHmmss);

		default:
			return time.toString();
		}
	}

	public static Timestamp parseDateFormat(byte format, String dateString) throws ParseException {
		switch (format) {
		case TIEM_FORMAT_yyyyMMdd:
			return new Timestamp(DateUtils.parseDate(dateString, new String[] { df_yyyyMMdd }).getTime());

		case TIEM_FORMAT_EEMMDD:
			String eeStr = dateString.substring(0, 2);
			String MMddStr = dateString.substring(2);
			int year = Integer.parseInt(eeStr) + 1911;
			return new Timestamp(DateUtils.parseDate(year + MMddStr, new String[] { df_yyyyMMdd }).getTime());

		case TIEM_FORMAT_HHmmss:
			return new Timestamp(DateUtils.parseDate(dateString, new String[] { df_HHmmss }).getTime());

		case TIME_FORMAT_yyyyMMddHHmmss:
			return new Timestamp(DateUtils.parseDate(dateString, new String[] { df_yyyyMMddHHmmss }).getTime());

		default:
			return Timestamp.valueOf(dateString);
		}
	}

	/**
	 * 回傳byte array的Hex字串
	 * 
	 * @param data
	 * @return
	 */
	public static String toHex(byte[] data) {
		return new String(Hex.encodeHex(data));
	}

	/**
	 * 將Hex字串轉回byte array
	 * 
	 * @param hexString
	 * @return
	 */
	public static byte[] fromHex(String hexString) throws DecoderException {
		return Hex.decodeHex(hexString.toCharArray());
	}

	public static void replaceCodeAndEncoding(String srcFile, String srcEncoding, String desFile, String desEncoding)
			throws Exception {
		BufferedReader reader = null;
		BufferedWriter writer = null;
		String tempFileName = desFile + ".utf8";
		File srcF = new File(srcFile);
		File desF = new File(tempFileName);
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(srcF), srcEncoding));
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(desF), desEncoding));

			String temp = null;
			while ((temp = reader.readLine()) != null) {
				if ((srcEncoding + desEncoding).toUpperCase().equals("MS950UTF-8")
						|| (srcEncoding + desEncoding).toUpperCase().equals("BIG5UTF-8")) {
					temp = replaceCodeBig5ToUtf8(temp);
				}
				writer.write(temp);
				writer.write(lineSeparator);
				writer.flush();
			}
			FileUtil.openReadCommand(tempFileName);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
					// ingore
				}
			}
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {
					// ingore
				}
			}
			srcF.delete();
			desF.renameTo(srcF);
		}
	}

	public static String replaceCodeBig5ToUtf8(String str) {
		return replaceCode(TYPE3_BIG5_TO_UTF8_SRC_CODE, "MS950", TYPE3_BIG5_TO_UTF8_DES_CODE, "UTF-8", str);
	}

	public static String replaceCode(String[] ORIGINAL_CODE_TYPE, String srcEncoding, String[] DESTINATION_CODE_TYPE,
			String desEncoding, String str) {
		if (ORIGINAL_CODE_TYPE.length != DESTINATION_CODE_TYPE.length) {
			logger.warn("ingore replaceCode, ORIGINAL_CODE 與 DESTINATION_CODE陣列長度需相等");
			return str;
		}
		if (ORIGINAL_CODE_TYPE.length == 0) {
			return str;
		}
		for (int j = 0; j < ORIGINAL_CODE_TYPE.length; j++) {
			try {
				byte[] srcChar = Hex.decodeHex(ORIGINAL_CODE_TYPE[j].toCharArray());
				byte[] desChar = Hex.decodeHex(DESTINATION_CODE_TYPE[j].toCharArray());
				str = str.replace(new String(srcChar, srcEncoding), new String(desChar, desEncoding));
			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage(), e);
				break;
			} catch (DecoderException e) {
				logger.error(e.getMessage(), e);
				logger.warn("system-codeConvert-xxxToxxx.properties中定義錯誤");
				logger.warn("ORIGINAL_CODE_TYPE[" + j + "]=" + ORIGINAL_CODE_TYPE[j]);
				logger.warn("DESTINATION_CODE_TYPE[" + j + "]=" + DESTINATION_CODE_TYPE[j]);
			}
		}

		return str;
	}

	public static String getStackTraceAsString(Throwable t) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(bytes, true);
		t.printStackTrace(writer);
		return bytes.toString();
	}

	public static String rightPad(String str, int size, String padStr) {
		return StringUtils.rightPad(str, size, padStr);
	}

	public static String leftPad(String str, int size, String padStr) {
		return StringUtils.leftPad(str, size, padStr);
	}

	public static List<String> toLog(byte[] bytes, boolean isMask, String encoding) throws UnsupportedEncodingException {
		List<String> result = new ArrayList<String>();
		int displaySize = 8;
		int len = bytes.length / displaySize;
		len = bytes.length % displaySize == 0 ? len : len + 1;
		boolean isIn = false;
		byte[] firstTempBytes = null;
		byte[] lastTempBytes = null;
		for (int i = 0; i < len; i++) {
			StringBuffer row = new StringBuffer();
			int offset = i * displaySize;
			int endsite = offset + displaySize;
			if (endsite > bytes.length) {
				endsite = bytes.length;
			}

			if (isIn) {
				lastTempBytes = ArrayUtils.subarray(bytes, offset, endsite);
				row.append(" ");
				for (byte b : firstTempBytes) {
					row.append(Hex.encodeHexString(new byte[] { b }) + " ");
				}
				row.append(" ");
				if (isMask) {
					row.append(StringUtils.leftPad("", firstTempBytes.length + lastTempBytes.length, "."));
				} else {
					row.append(new String(ArrayUtils.addAll(firstTempBytes, lastTempBytes), encoding));
				}
				result.add(row.toString());
			} else {
				firstTempBytes = ArrayUtils.subarray(bytes, offset, endsite);
				for (byte b : firstTempBytes) {
					row.append(Hex.encodeHexString(new byte[] { b }) + " ");
				}
			}
		}

		return result;
	}

	/**
	 * trim全形空白
	 * 
	 * @param input
	 * @return
	 */
	public static String trimFullShapeEmpty(String input) {
		if (input != null && input != "") {
			input = input.replaceAll("^[　\\s]+|[　\\s]+$", "");
		}
		return input;
	}
	
	/**
	 * 基於UTF8計算字串長度-全形*2，半形*1
	 * @param str
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static int calcFulHalfLength(String str) throws UnsupportedEncodingException {
		byte[] bytes = str.getBytes("UTF-8");

		int totalLength = 0;
		int utf8Len = 0;

		for (int j = 0; j < bytes.length; j += utf8Len) {

			byte b = bytes[j];
			int len = 2;

			if (0x00 <= (b & 0xff) && (b & 0xff) <= 0x7f) {// 0XXXXXXX
				utf8Len = 1;
				len = 1;
			} else if (0xc0 <= (b & 0xff) && (b & 0xff) <= 0xdf) {// 110XXXXX
				utf8Len = 2;
			} else if (0xe0 <= (b & 0xff) && (b & 0xff) <= 0xef) {// 1110XXXX
				utf8Len = 3;
			} else if (0xf0 <= (b & 0xff) && (b & 0xff) <= 0xf7) {// 11110XXX
				utf8Len = 4;
			} else if (0xf8 <= (b & 0xff) && (b & 0xff) <= 0xfb) {// 111110XX
				utf8Len = 5;
			} else if (0xfc <= (b & 0xff) && (b & 0xff) <= 0xfd) {// 1111110X
				utf8Len = 6;
			}
			totalLength += len;
		}

		return totalLength;
	}

	public static void main(String[] args) throws IOException {
		byte[] bytes = FileUtils.readFileToByteArray(new File("C:\\Users\\Angus\\Dropbox\\tita.txt"));
		List result = toLog(bytes, false, "MS950");
		for (int i = 0; i < result.size(); i++) {
			System.out.println(result.get(i));
		}
	}
}
