package com.systex.jbranch.platform.formatter;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.DecimalFormat;

import com.systex.jbranch.platform.common.util.NumberUtil;
import com.systex.jbranch.platform.common.util.StringUtil;

public class N7Formatter  extends NumberFormatter implements INumberFormatter{
	
	private Logger logger = LoggerFactory.getLogger(N7Formatter.class);
	protected DecimalFormat decimalFormat=new DecimalFormat(); 
	
	public N7Formatter(int integerDigits, int fractionDigits)
	{
		this();
		setIntegerDigits(integerDigits);
		setFractionDigits(fractionDigits);
	}
	
	public N7Formatter()
	{
		decimalFormat.setGroupingUsed(false);
		decimalFormat.setNegativePrefix("-");
		decimalFormat.setNegativeSuffix("");
		decimalFormat.setPositivePrefix("");
		decimalFormat.setPositiveSuffix("");
	}
	
	
	
	/**
	 * Format amt String
	 * @param amt 12345.66 or 04.23456 or 00001234.00
	 * @return 12,345.66
	 */
	public String format(String amt) {
		String newAmt = "date error!";
		try {
			BigDecimal number = new BigDecimal(amt);
			int scale = number.scale();
			String fraction = "";
			if(scale != 0){
				fraction = "." + StringUtil.leftPad("", scale, "0");
			}
			newAmt = NumberUtil.format(number, "#,##0" + fraction);
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return newAmt;
//		int dot = amt.indexOf(".");
		
//		if (dot != -1) {
//			String dotStr = amt.substring(dot);
//			String befdot = amt.substring(0,dot);
//			befdot = String.valueOf(Integer.parseInt(befdot));
//			newAmt = parseCurrency(befdot + dotStr);
//		}
//		else {
//			newAmt = parseCurrency(String.valueOf(Integer.parseInt(amt)));
//		}
	}
	
	private String parseCurrency(String numValue) {
		String reCurrency = "";
		String reValue = "";
		double countMark = 0;
		int mod = 0;
		boolean isMinus = false;
		if (numValue != null
			&& numValue.length() != 0
			&& numValue.charAt(0) == '-') {
			numValue = numValue.substring(1);
			isMinus = true;
		}
		if (isNum(numValue)) {
			double len = numValue.length();
			if (numValue.indexOf('.') != -1) {
				len = numValue.substring(0, numValue.indexOf('.')).length();
				countMark = Math.ceil(len / 3);
				mod = numValue.substring(0, numValue.indexOf('.')).length() % 3;
			} else {
				countMark = Math.ceil(len / 3);
				mod = numValue.length() % 3;
			}

			for (int i = 0; i < countMark; i++) {
				if (mod != 0) {
					if (i == 0)
						reValue = numValue.substring(3 * i, mod);
					else
						reValue =
							reValue
								+ ","
								+ numValue.substring(
									3 * (i - 1) + mod,
									3 * i + mod);
				} else {
					if (i == 0)
						reValue = numValue.substring(0, 3 * (i + 1));
					else
						reValue =
							reValue
								+ ","
								+ numValue.substring(3 * i, 3 * (i + 1));
				}
			}
			if (numValue.indexOf('.') != -1)
				reValue = reValue + numValue.substring(numValue.indexOf('.'));
		} else {
			return "0";
		}
		if (isMinus)
			reValue = "-" + reValue;
		return reValue;
	}
	
	private boolean isNum(String strNum) {
		String num = "0123456789";
		int intPoint = 0;
		if (strNum != null && !strNum.equals("")) {
			if (strNum.length() == 0)
				return false;
			for (int i = 0; i < strNum.length(); i++) {
				if (i == 0 && strNum.charAt(i) == '-') {
				} else if (strNum.charAt(i) == '.') {
					intPoint++;
					if (intPoint > 1)
						return false;
				} else if (num.indexOf(strNum.charAt(i)) == -1) {
					return false;
				}
			}
			return true;
		} else
			return false;
	}
	
	public static void main(String [] args) {
		N7Formatter n7 = new N7Formatter();
		System.out.println(n7.format("12345.66"));
		System.out.println(n7.format("001234500"));
		System.out.println(n7.format("04.23456"));
		System.out.println(n7.format("00001234.00"));
	}

	@Override
	protected String format(BigDecimal value) {
		
		return this.format(value.toString());
	}

	@Override
	protected String formatNull() {
		return StringUtils.repeat(" ", this.getIntegerDigits()+this.getFractionDigits());
	}

	@Override
	public BigDecimal parse(String value) throws ParseException {
		try
		{
			if(value==null || "".equals(value.trim()))
				return null;
			
			int index=value.length()-this.getFractionDigits();
			if(index<0)index=0;

			if(index==0)
			{
				value="0."+value;
			}
			else
			{
				value=value.substring(0, index)+"."+value.substring(index);
			}
			return BigDecimal.valueOf(decimalFormat.parse(value).doubleValue());			
		}
		catch(Exception e)
		{
			throw new ParseException(e);
		}
	}

	@Override
	protected void onFractionDigitsChanged(int oldVal, int newVal)
	{
		decimalFormat.setMinimumFractionDigits(newVal);
		decimalFormat.setMaximumFractionDigits(newVal);
	}

	@Override
	protected void onIntegerDigitsChanged(int oldVal, int newVal)
	{
		decimalFormat.setMinimumIntegerDigits(newVal);
		decimalFormat.setMaximumIntegerDigits(newVal);	
	}
}
