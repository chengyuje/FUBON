package com.systex.jbranch.app.server.fps.cmjlb014;
 
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math.analysis.solvers.UnivariateRealSolver;
import org.apache.commons.math.analysis.solvers.UnivariateRealSolverFactory;
import org.apache.commons.math.distribution.NormalDistributionImpl;
import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealMatrix;

//import edu.northwestern.at.utils.linprog.Simplex;
//import edu.northwestern.at.utils.quadprog.ConvexQuad;

/**
 * 數學運算模組——陳志斌
 * 
 * @author 陳志斌
 * @date 2010-04-06
 * @since Version
 * @spec API文件.doc
 * =====================================================================
 * History
 * ---------------------------------------------------------------------
 * 2013/07/15 by Ben Chen. Add pipAccumRtn() to Calcuate 定期定額累積報酬率.
 */
public class CMJLB014 {

//	private final IServiceFacade serviceFacade;

	private static final String[] MASK_DATE = { "yyyy/MM/dd" }; // fvx 用的日期格式
	private static final double NEGATIVE_MIN = -1E100;// regPay 的訊息
	private static final String[] ERR_MSG_00 = new String[] { "00", "Success." };
	private static final String[] ERR_MSG_01 = new String[] { "01",
			"Return must greater than -1!" };
	private static final String[] ERR_MSG_02 = new String[] { "02",
			"Weight must greater than 0!" };
	private static final String[] ERR_MSG_03 = new String[] { "03",
			"Sum of asset weights must equal to 1." };
	private static final String[] ERR_MSG_04 = new String[] { "04",
			"Matrix dimension must agree." };
	private static final String[] ERR_MSG_05 = new String[] { "05",
			"Time interval error." };
	private static final String[] ERR_MSG_06 = new String[] { "06",
			"date formate error." };
	private static final String[] ERR_MSG_07 = new String[] { "07",
			"date series error." };
	private static final String[] ERR_MSG_08 = new String[] { "08",
			"standard deviation or variance must greater than 0." };
	private static final String[] ERR_MSG_09 = new String[] { "09",
			"Matrix must have content." };
	private static final String[] ERR_MSG_10 = new String[] { "10",
			"Error:(1+portfolio(i)-benchmark(i)) should be greater than 0." };
	private static final String[] ERR_MSG_11 = new String[] { "11",
			"Standard Deviation should be greater than 0." };
	private static final String[] ERR_MSG_22 = new String[] { "22",
			"Out of Range-UpperLimmit." };
	private static final String[] ERR_MSG_23 = new String[] { "23",
			"Out of Range-LowerLimmit." };
	private static final String[] ERR_MSG_24 = new String[] { "24",
			"Initial salary must be greater than 0." };
	private static final String[] ERR_MSG_25 = new String[] { "25",
			"Withdraw rate must be greater than 0." };
	private static final String[] ERR_MSG_26 = new String[] { "26",
			"Work year must be greater than 0." };
	private static final String[] ERR_MSG_27 = new String[] {
			"27",
			"The maximum iteration count is exceeded or the solver detects convergence problems otherwise." };
	private static final String[] ERR_MSG_28 = new String[] { "28",
			"No feasible solution." };
	private static final String[] ERR_MSG_29 = new String[] { "29",
			"Number of points must be greater than 0." };
	private static final String[] ERR_MSG_30 = new String[] { "30",
			"Growth  Rate must be greater than -1." };
	private static final String[] ERR_MSG_31 = new String[] { "31",
			"Original Inssurance Year  must be greater than 0." };
	private static final String[] ERR_MSG_32 = new String[] { "32",
			"TimeShift must be in -5~5." };
	private static final String[] ERR_MSG_33 = new String[] { "33",
			"Inssurance Year  must be greater than 0." };
	private static final String[] ERR_MSG_34 = new String[] { "34",
			"Simulation Period or Simulation Times must be greater than 0." };
	private static final String[] ERR_MSG_40 = new String[] { "40",
		    "NAV matrix cannot be empty! You must give the monthly NAV for every month." };
	private static final String[] ERR_MSG_41 = new String[] { "41",
		    "Cashflow matrix cannot be empty! You have to assign the amount for each investment frequency." };
	private static final String[] ERR_MSG_99 = new String[] { "99",
			"System failure - " };

	// 因應勞委會提出60個薪資級距
	private static final double[] SALARY_RANGE_60 = new double[] { 3000, 4500,
			6000, 7500, 8700, 9900, 11100, 12300, 13500, 15840, 16500, 17400,
			18300, 19200, 20100, 21000, 21900, 22800, 24000, 25200, 26400,
			27600, 28800, 30300, 31800, 33300, 34800, 36300, 38200, 40100,
			42000, 43900, 45800, 48200, 50600, 53000, 55400, 57800, 60800,
			63800, 66800, 69800, 72800, 76500, 80200, 83900, 87600, 92100,
			96600, 101100, 105600, 110100, 115500, 120900, 126300, 131700,
			137100, 142500, 147900, 150000 };

	/*
	 * 此為勞工保險投保薪資分級表，出處為中華民國九十五年五月一日 行政院勞工委員會勞保二字第0950022407號令修正發布自九十五年七月一日施行
	 */
	private static final double[] SALARY_RANGE = new double[] { 15840, 16500,
			17400, 18300, 19200, 20100, 21000, 21900, 22800, 24000, 25200,
			26400, 27600, 28800, 30300, 31800, 33300, 34800, 36300, 38200,
			40100, 42000, 43900 };

//	public static CMJLB014 newInstance(IServiceFacade serviceFacade) {
//		return new CMJLB014(serviceFacade);
//	}

//	private CMJLB014(IServiceFacade serviceFacade) {
//		this.serviceFacade = serviceFacade;
//	}
	/**
	 * 檢核報酬率是否大於-1&矩陣得是矩形矩陣。
	 * 
	 * @param Ret
	 *            [][] 標的物各期報酬率，不含百分比。
	 * @return ResultObj if error;boolean if success.
	 */
	private ResultObj chkRet(double[][] ret) {

		try {

			for (int indexRow_i = 0; indexRow_i < ret.length; indexRow_i++) {
				if (indexRow_i != 0
						&& ret[indexRow_i].length != ret[indexRow_i - 1].length) {
					return new ResultObj(ERR_MSG_04[0], ERR_MSG_04[1]
							+ "Error at row " + (indexRow_i + 1) + ".");
				}
				for (int indexCol_i = 0; indexCol_i < ret[indexRow_i].length; indexCol_i++) {
					if (Double.isNaN(ret[indexRow_i][indexCol_i])){
							//| ret[indexRow_i][indexCol_i] < -1) {

						return new ResultObj(ERR_MSG_01[0], ERR_MSG_01[1]
								+ " Error at row " + (indexRow_i + 1)
								+ " column " + (indexCol_i + 1) + ".");
					}
				}
			}
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1]);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 檢核權重是否大於0且權重合是否大於1
	 * 
	 * @param weight
	 *            投資組合內各商品的投資權重
	 * @return ResultObj if err occured ; boolean else.
	 */
	private ResultObj chkWeight(double[] weight) {
		double tmp_d = 0;
		try {
			for (int index_i = 0; index_i < weight.length; index_i++) {
				tmp_d += weight[index_i];
				if (Double.isNaN(weight[index_i]) || weight[index_i] < 0
						|| weight[index_i] > 1) {
					return new ResultObj(ERR_MSG_02[0], ERR_MSG_02[1]
							+ " Error occurred at " + (index_i + 1) + ".");
				}
			}
			if (Math.abs(tmp_d - 1) > 0.0000001) {
				return new ResultObj(ERR_MSG_03[0], ERR_MSG_03[1]);
			}
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1]);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 檢核標準差是否為負數
	 * 
	 * @param std
	 *            資產標準差
	 * @return
	 */
	private ResultObj chkStd(double[][] std) {
		try {
			for (int indexRow_i = 0; indexRow_i < std.length; indexRow_i++) {
				for (int indexCol_i = 0; indexCol_i < std[indexRow_i].length; indexCol_i++) {
					if (std[indexRow_i][indexCol_i] < 0) {
						return new ResultObj(ERR_MSG_08[0], ERR_MSG_08[1]
								+ " Error at row " + indexRow_i + ", column "
								+ indexCol_i + ".");
					}
				}
			}
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1]);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 計算幾何平均報酬
	 * 
	 * @param ret
	 *            標的物各期報酬率[N][M],表示有N檔資產M個期間報酬率
	 * @return ResultObj
	 */
	public ResultObj util_GeoRet(double[][] ret) {
		try {
			ResultObj tmpObj = chkRet(ret);
			if (!tmpObj.getResultCode().equals(ERR_MSG_00[0])) {
				return tmpObj;
			}
			double[] resultArr = new double[ret.length];
			for (int indexRow_i = 0; indexRow_i < ret.length; indexRow_i++) {
				double lnResult_d = 0;
				double result_d = 0;
				for (int indexCol_i = 0; indexCol_i < ret[indexRow_i].length; indexCol_i++) {
					lnResult_d += Math.log(1 + ret[indexRow_i][indexCol_i]);
				}
				result_d = (Math.exp(lnResult_d / ret[indexRow_i].length) - 1);
				resultArr[indexRow_i] = result_d;
			}
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], resultArr);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 計算標準差
	 * 
	 * @param ret
	 *            標的物各期報酬率[N][M],表示有N檔資產M個期間報酬率
	 * @return ResultObj
	 */
	public ResultObj util_STD(double[][] ret) {
		ResultObj tmpObj = chkRet(ret);
		if (!tmpObj.getResultCode().equals(ERR_MSG_00[0])) {
			return tmpObj;
		}
		try {
			double[] std = new double[ret.length];
			for (int index_i = 0; index_i < std.length; index_i++) {
				double tmpVar = util_Covar(index_i, index_i, ret,
						util_GetMean(ret));
				/* ret[i].length/(ret[i].length-1) */
				std[index_i] = Math.sqrt(tmpVar);
			}
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], std);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 月投資報酬率轉換為年投資報酬率，即年化報酬率
	 * 
	 * @param ret
	 *            ，欲轉換的報酬率。
	 * @return
	 */
	public ResultObj convtRet_MToY(double[] ret) {
		ResultObj tmpObj = chkRet(new double[][] { ret });
		if (!tmpObj.getResultCode().equals(ERR_MSG_00[0])) {
			return tmpObj;
		}
		try {
			double yRet[] = new double[ret.length];
			for (int index_i = 0; index_i < ret.length; index_i++) {
				yRet[index_i] = Math.pow(1 + ret[index_i], 12) - 1;
			}
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], yRet);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}
	
	 
	public ResultObj convtRet_DToY(double[] ret) {
		return convtRet_DToY(ret , 365);
	}

	/**
	  * 日投資報酬率轉換為年投資報酬率，即年化報酬率
	  * 
	  * @param ret ，欲轉換的報酬率。
	  *            
	  * @return
	  */
	 public ResultObj convtRet_DToY(double[] ret , int year) {
		 ResultObj tmpObj = chkRet(new double[][] { ret });
		 if (!tmpObj.getResultCode().equals(ERR_MSG_00[0])) {
			 return tmpObj;
		 }
		 
		 try {
			 double yRet[] = new double[ret.length];
			 
			 for (int index_i = 0; index_i < ret.length; index_i++) {
				 yRet[index_i] = Math.pow(1 + ret[index_i], year) - 1;
			 }
			 return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], yRet);
		 } 
		 catch (Exception e) {
			 return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		 }
	 }
	 
	/**
	 * 公式有錯(保留)
	 *
	 * 將月投資報酬率標準差轉換為年投資報酬率標準差，即年化標準差
	 * 
	 * @param std
	 *            資產的月標準差
	 * @return
	 */
	public ResultObj convtStd_MToY(double[] std) {
		// 進行標準差檢核
		ResultObj tmpObj = chkStd(new double[][] { std });
		if (!tmpObj.getResultCode().equals(ERR_MSG_00[0])) {
			return tmpObj;
		}
		double[] yStd = new double[std.length];
		try {
			for (int index_i = 0; index_i < yStd.length; index_i++) {
				yStd[index_i] = Math.sqrt(12) * std[index_i];
			}
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], yStd);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}
	
	/**
	 * 
	 * 將月投資報酬率轉換為年投資報酬率標準差，即年化標準差
	 * 
	 * @param ret 資產的月投資報酬率(檢核用)
	 * @param retM 資產的月投資報酬率
	 * @return
	 * @date 2012-08-09 
	 * @modifier Jason
	 */	
	public ResultObj convtStd_MToY(double[] ret,double[] retM) {
		
		ResultObj tmpObj = chkRet(new double[][] { ret });
		if (!tmpObj.getResultCode().equals(ERR_MSG_00[0])) {
			return tmpObj;
		}
		
		double pow_sum = 0.0; //月投資報酬率平方合
		double pow_avg = 0.0; //月投資報酬率平均合平方
		double yStd[] = new double[1]; //年化標準差
		try {
			for (int index_i = 0; index_i < retM.length; index_i++) {
				pow_sum += Math.pow(retM[index_i], 2.0);
				pow_avg += retM[index_i];
			}
			pow_avg = Math.pow(pow_avg, 2.0)/retM.length; 
			//(月投資報酬率平方合-月投資報酬率平均合平方)除以平均月份*12的平方根
			yStd[0] = Math.sqrt(12) * (Math.sqrt((pow_sum - pow_avg)/retM.length)); 
			
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], yStd);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
		
	}

	/**
	 * 計算超額報酬率
	 * 
	 * @param portRet
	 *            投資組合報酬率
	 * @param benchRet
	 *            標竿指數報酬率
	 * @return
	 */
	public ResultObj perform_ExRet(double[] portRet, double[] benchRet) {
		try {
			ResultObj tmpObj = perform_CumExRet(portRet, benchRet);
			double rtn = 0.0;
			if (tmpObj.getResultCode().equals(ERR_MSG_00[0])) {
				for(int index=0; index < portRet.length ;index++){
					rtn += (portRet[index] - benchRet[index]);
				}
				return new ResultObj(tmpObj.getResultCode(), tmpObj
						.getResultStr(), rtn);
			} else {
				return tmpObj;
			}
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}
	
	

	/**
	 * 計算超額風險(有問題)
	 * 
	 * @param portRet
	 *            投資組合報酬率
	 * @param benchRet
	 *            標竿指數報酬率
	 * @return
	 */
	public ResultObj perform_ExVar(double[] portRet, double[] benchRet) {
		double[][] tmpRet = new double[][] { portRet, benchRet };
		ResultObj tmp = chkRet(tmpRet);
		if (!tmp.getResultCode().equals(ERR_MSG_00[0])) {
			StringBuffer errStr = new StringBuffer(((ResultObj) tmp)
					.getResultStr());
			errStr.append("\nPortfolio return at 1st row,Benchmark at 2nd.");
			return new ResultObj(((ResultObj) tmp).getResultCode(), errStr
					.toString());
		}
		try {
			double[] mean = util_GetMean(tmpRet);
			double tmpRisk = 0;
			for (int i = 0; i < portRet.length; i++) {
				tmpRisk += Math.pow(
						((portRet[i] - benchRet[i]) - (mean[0] - mean[1])), 2);
			}
			tmpRisk = tmpRisk / (((double) portRet.length) - 1);
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], Math
					.sqrt(tmpRisk));
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}
	
	/**
	 * Overload
	 * 計算超額風險
	 * 
	 * @param portRet
	 *            投資組合報酬率
	 * @param benchRet
	 *            標竿指數報酬率
	 * @return
	 */
	public ResultObj perform_ExVar(double[] portRet, double[] benchRet,int count) {
			
		double pow_sum = 0.0; //月投資報酬率平方合
		double pow_avg = 0.0; //月投資報酬率平均合平方
		double pow_sum1 = 0.0; //標竿指數月投資報酬率平方合
		double pow_avg1 = 0.0; //標竿指數月投資報酬率平均合平方
		
		double exRet1= 0.0; //投資組合
		double exRet2 = 0.0; //標竿指數
		double[] exRet = new double[1];
		try {
			for (int index_i = 0; index_i < portRet.length; index_i++) {
				pow_sum += Math.pow(portRet[index_i], 2.0);
				pow_avg += portRet[index_i];
			}
			pow_avg = Math.pow(pow_avg, 2.0)/portRet.length;
			
			for (int index_i = 0; index_i < benchRet.length; index_i++) {
				pow_sum1 += Math.pow(benchRet[index_i], 2.0);
				pow_avg1 += benchRet[index_i];
			}
			pow_avg1 = Math.pow(pow_avg1, 2.0)/benchRet.length;
			
			exRet1 = Math.sqrt((pow_sum - pow_avg)/portRet.length);
			exRet2 = Math.sqrt((pow_sum1 - pow_avg1)/benchRet.length); 
			
			exRet[0] = exRet1 - exRet2;
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], exRet);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
			
		
	}

	/**
	 * 
	 * 計算標的物與市場指數的資訊比例(計算結果有問題)
	 * 
	 * @param portRet
	 *            投資組合報酬率
	 * @param benchRet
	 *            標竿指數報酬率
	 * @return
	 */
	public ResultObj perform_InfoRatio(double[] portRet, double[] benchRet) {
		try {
			ResultObj tmpRet = perform_ExRet(portRet, benchRet);
			ResultObj tmpRisk = perform_ExVar(portRet, benchRet);
			double tmp = 0;
			if (tmpRet.getResultCode().equals(ERR_MSG_00[0])
					&& tmpRisk.getResultCode().equals(ERR_MSG_00[0])) {
				tmp = tmpRet.getResultArray()[0][0]
						/ tmpRisk.getResultArray()[0][0];
				return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], tmp);
			} else {
				return tmpRet;
			}
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}
	
	/**
	 * Overloading
	 * 
	 * 計算標的物與市場指數的資訊比例
	 * 
	 * @param portRet
	 *            投資組合報酬率
	 * @param benchRet
	 *            標竿指數報酬率
	 * @return
	 */
	public ResultObj perform_InfoRatio(double[] portRet, double[] benchRet,double exRisk) {
			
		double[] infoScale = new double[1];   //資訊比例
		double portRet_sum = 0.0; //投資組合報酬率合
		double benchRet_sum = 0.0; //標竿指數報酬率合
		
		try {
			for (int index_i = 0; index_i < portRet.length; index_i++) {
				portRet_sum += portRet[index_i];
			}
			
			for (int index_i = 0; index_i < benchRet.length; index_i++) {
				benchRet_sum += benchRet[index_i];
			}
			infoScale[0] = ((portRet_sum - benchRet_sum)/benchRet.length)/exRisk;
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], infoScale);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
			
	}

	/**
	 * 計算各期累積報酬率
	 * 
	 * @param ret
	 *            報酬率矩陣,double[N][M],表示有N檔資產M個期間報酬率
	 * @return
	 */
	public ResultObj util_CumRet(double[][] ret) {
		ResultObj tmpObj = chkRet(ret);
		if (!tmpObj.getResultCode().equals(ERR_MSG_00[0])) {
			return tmpObj;
		}
		try {
			double[][] temp = new double[ret.length][ret[0].length];
			double[][] result = new double[ret.length][ret[0].length];
			for (int indexRow_i = 0; indexRow_i < ret.length; indexRow_i++) {
				temp[indexRow_i][0] = 1 + ret[indexRow_i][0];
				result[indexRow_i][0] = ret[indexRow_i][0];
				for (int indexCol_i = 1; indexCol_i < ret[indexRow_i].length; indexCol_i++) {
					temp[indexRow_i][indexCol_i] = (1 + ret[indexRow_i][indexCol_i])
							* temp[indexRow_i][indexCol_i - 1];
					result[indexRow_i][indexCol_i] = temp[indexRow_i][indexCol_i] - 1;
				}
			}
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], result);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 計算累積超額報酬
	 * 
	 * @param portRet 投資組合報酬
	 * @param benchRet 標竿指數報酬
	 * @param month overload 參數沒用到
	 * @return
	 */
	public ResultObj perform_CumExRet(double[] portRet, double[] benchRet,int month) {
		double[][] result = new double[1][portRet.length];
		try {
			for (int index_i = 0; index_i < portRet.length; index_i++) {
				if(index_i == 0){
					result[0][index_i] = (1.0) * ((portRet[index_i]-benchRet[index_i])+1);
				}else{
					result[0][index_i] = (result[0][index_i-1]+1) * ((portRet[index_i]-benchRet[index_i])+1);
				}
				result[0][index_i] = result[0][index_i] - 1;
			}
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], result);

		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}
	
	/**
	 * 計算累積超額報酬(公式有問題)
	 * 
	 * @param portRet
	 *            ，投資組合報酬
	 * @param benchRet
	 *            ，標竿指數報酬
	 * @return
	 */
	public ResultObj perform_CumExRet(double[] portRet, double[] benchRet) {
		double[][] tmpRet = new double[][] { portRet, benchRet };
		ResultObj tmpObj = chkRet(tmpRet);
		if (!tmpObj.getResultCode().equals(ERR_MSG_00[0])) {
			StringBuffer errStr = new StringBuffer(((ResultObj) tmpObj)
					.getResultStr());
			errStr.append("\nPortfolio return at 1st row,Benchmark at 2nd.");
			return new ResultObj(((ResultObj) tmpObj).getResultCode(), errStr
					.toString());
		}
		try {

			double[][] temp = new double[1][tmpRet[0].length];
			for (int index_i = 0; index_i < temp[0].length; index_i++) {
				temp[0][index_i] = portRet[index_i] - benchRet[index_i];
				//if (1 + temp[0][index_i] < 0) {
                //
				//	return new ResultObj(ERR_MSG_10[0], ERR_MSG_10[1]
				//			+ "Error at " + (index_i + 1) + "th element.");
                //
				//}
			}

			return util_CumRet(temp);

		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 計算標的物各期報酬率與該標的物「平均報酬率」的距離之平方和
	 * 
	 * @param ret
	 *            報酬率
	 * @return
	 */
	public ResultObj perform_TSS(double[] ret) {
		ResultObj tmpObj = chkRet(new double[][] { ret });
		if (!tmpObj.getResultCode().equals(ERR_MSG_00[0])) {
			return ((ResultObj) tmpObj);
		}
		try {
			double[] average = util_GetMean(new double[][] { ret });
			double tmp_d = 0.0;
			for (int index_i = 0; index_i < ret.length; index_i++) {
				tmp_d += Math.pow(ret[index_i] - average[0], 2);
			}
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], tmp_d);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 計算未解釋變異
	 * 
	 * @param portRet
	 *            投資組合報酬率
	 * @param benchRet
	 *            標竿指數報酬率
	 * @return
	 */
	public ResultObj perform_SSE(double[] portRet, double[] benchRet) {
		double[][] tmpRet = new double[][] { portRet, benchRet };
		ResultObj tmpObj = chkRet(tmpRet);
		if (!tmpObj.getResultCode().equals(ERR_MSG_00[0])) {
			StringBuilder errStr_sb = new StringBuilder(((ResultObj) tmpObj)
					.getResultStr());
			errStr_sb.append("\nPortfolio return at 1st row,Benchmark at 2nd.");
			return new ResultObj(tmpObj.getResultCode(), errStr_sb.toString());
		}
		try {
			ResultObj betaObj = perform_Beta(portRet, benchRet);
			ResultObj alpha = perform_Alpha(portRet, benchRet);
			double mse = 0;
			for (int i = 0; i < portRet.length; i++) {
				mse += Math.pow(portRet[i]
						- (alpha.getResultArray()[0][0] + benchRet[i]
								* betaObj.getResultArray()[0][0]), 2);
			}
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], mse);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 將年報酬率轉換為各種時距的報酬率
	 * 
	 * @param ret
	 *            報酬率
	 * @param period
	 *            表示一年之內有幾個期間，如period=3表示季報酬
	 * @return
	 */
	public ResultObj convtRet_YToAny(double[] ret, int period) {
		ResultObj tmp = chkRet(new double[][] { ret });
		if (!tmp.getResultCode().equals(ERR_MSG_00[0]))
			return ((ResultObj) tmp);
		if (period <= 0)
			return new ResultObj(ERR_MSG_05[0], ERR_MSG_05[1]);
		double[] result = new double[ret.length];
		try {
			for (int i = 0; i < ret.length; i++) {
				result[i] = Math.pow(1 + ret[i], (double) period / 12) - 1;
			}
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], result);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 計算年金現值
	 * 
	 * @param ret
	 *            報酬率
	 * @param period
	 *            一年的期間數
	 * @return
	 */
	public ResultObj cf_PVIFA(double[] ret, int period) {
		ResultObj tmp = chkRet(new double[][] { ret });
		if (!tmp.getResultCode().equals(ERR_MSG_00[0]))
			return ((ResultObj) tmp);
		if (period <= 0)
			return new ResultObj(ERR_MSG_05[0], ERR_MSG_05[1]);
		try {
			double[] result = new double[ret.length];
			for (int i = 0; i < ret.length; i++) {
				result[i] = (1 - Math.pow(1 / (1 + ret[i]), period)) / ret[i];
			}
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], result);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 計算投資組合報酬率
	 * 
	 * @param ret
	 *            資產報酬率,[N][M]N檔資產M個期間報酬率
	 * @param weight
	 *            [N]N檔資產的投資比重
	 * @return
	 */
	public ResultObj port_Ret(double[][] ret, double[] weight) {
		ResultObj tmp = chkRet(ret);
		if (!tmp.getResultCode().equals(ERR_MSG_00[0]))
			return ((ResultObj) tmp);
		tmp = chkWeight(weight);
		if (!tmp.getResultCode().equals(ERR_MSG_00[0]))
			return ((ResultObj) tmp);
		if (ret.length != weight.length)
			return new ResultObj(ERR_MSG_04[0], ERR_MSG_04[1]);
		try {
			double[] portRet = new double[ret[0].length];
			for (int i = 0; i < ret[0].length; i++) {
				for (int j = 0; j < ret.length; j++) {
					portRet[i] += ret[j][i] * weight[j];
				}
			}
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], portRet);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 計算投資組合報酬率(幾何平均)
	 * 
	 * @param ret
	 *            報酬率矩陣
	 * @param weight
	 *            權重矩陣
	 * @return
	 */
	public ResultObj port_GeoRet(double[][] ret, double[] weight) {
		ResultObj tmp = chkRet(ret);
		if (!tmp.getResultCode().equals(ERR_MSG_00[0]))
			return tmp;
		tmp = chkWeight(weight);
		if (!tmp.getResultCode().equals(ERR_MSG_00[0]))
			return tmp;
		if (ret.length != weight.length) {
			return new ResultObj(ERR_MSG_04[0], ERR_MSG_04[1]);
		}
		try {
			ret = util_CumGeoMean(ret);
			double[] portRet = new double[ret[0].length];
			for (int i = 0; i < ret[0].length; i++) {
				for (int j = 0; j < ret.length; j++) {
					portRet[i] += ret[j][i] * weight[j];
				}
			}
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], portRet);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 計算投資組合共變異數矩陣
	 * 
	 * @param ret
	 *            報酬率矩陣
	 * @return
	 */
	public ResultObj port_CovMtx(double[][] ret) {
		ResultObj tmp = chkRet(ret);
		if (!tmp.getResultCode().equals(ERR_MSG_00[0]))
			return tmp;
		try {
			double[][] result = new double[ret.length][ret.length];
			for (int i = 0; i < ret.length; i++) {
				for (int j = 0; j < ret.length; j++) {
					result[i][j] = util_Covar(i, j, ret, util_GetMean(ret));
					if (i != j) {
						result[j][i] = result[i][j];
					}/*
					 * else{
					 * result[i][j]=result[i][j]*ret.length/(ret.length-1); }
					 */
				}
			}
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], result);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 計算投資組合總風險
	 * 
	 * @param ret
	 *            報酬率
	 * @param weight
	 *            投資權重
	 * @return
	 */
	public ResultObj port_Variance(double[][] ret, double[] weight) {
		ResultObj tmp = port_CovMtx(ret);
		if (!tmp.getResultCode().equals(ERR_MSG_00[0]))
			return ((ResultObj) tmp);
		if (ret.length != weight.length)
			return new ResultObj(ERR_MSG_04[0], ERR_MSG_04[1]);
		try {
			double[][] portCov = tmp.getResultArray();
			double portVar = 0;
			for (int i = 0; i < portCov.length; i++) {
				for (int j = 0; j < portCov.length; j++) {
					portVar += portCov[i][j] * weight[i] * weight[j];
				}
			}
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], portVar);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 轉換年報酬為月報酬
	 * 
	 * @param ret
	 *            報酬率
	 * @return
	 */
	public ResultObj convtRet_YToM(double[] ret) {
		ResultObj tmp = chkRet(new double[][] { ret });
		if (!tmp.getResultCode().equals(ERR_MSG_00[0]))
			return ((ResultObj) tmp);
		try {
			double[] result = new double[ret.length];
			for (int i = 0; i < ret.length; i++) {
				result[i] = Math.pow(1 + ret[i], (double) 1 / 12) - 1;
			}
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], result);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 將月報酬轉換成任意報酬期間
	 * 
	 * @param ret
	 * @param period
	 *            如period = 6,則表示半年報酬轉換成年報酬
	 * @return
	 */
	public ResultObj convtRet_AnyToY(double[] ret, int period) {
		ResultObj tmp = chkRet(new double[][] { ret });
		if (!tmp.getResultCode().equals(ERR_MSG_00[0]))
			return ((ResultObj) tmp);
		if (period <= 0)
			return new ResultObj(ERR_MSG_05[0], ERR_MSG_05[1]);
		try {
			double[] result = new double[ret.length];
			for (int i = 0; i < ret.length; i++) {
				result[i] = Math.pow(1 + ret[i], 12 / period) - 1.0;
			}
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], result);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 計算現金(Amount)的終值
	 * 
	 * @param nowDate
	 *            起始日期
	 * @param endDate
	 *            結束日期
	 * @param amount
	 *            計算金額
	 * @param rate
	 *            年利率
	 * @return
	 */
	public ResultObj cf_FV(String nowDate, String endDate, double amount,
			double rate) {
		ResultObj tmpObj = chkRet(new double[][] { { rate } });
		if (!tmpObj.getResultCode().equals(ERR_MSG_00[0])) {
			return tmpObj;
		}
		Object tmp = convtStrToDate(MASK_DATE, nowDate);
		if (tmp instanceof ResultObj) {
			return ((ResultObj) tmp);
		}
		Date now = (Date) tmp;
		tmp = convtStrToDate(MASK_DATE, endDate);
		if (tmp instanceof ResultObj) {
			return ((ResultObj) tmp);
		}
		Date end = (Date) tmp;
		tmpObj = util_GetMonRange(now, end);
		if (!tmpObj.getResultCode().equals(ERR_MSG_00[0])) {
			return ((ResultObj) tmp);
		}
		int monthRange = (int) tmpObj.getResultArray()[0][0];

		try {
			double[][] yearRate = convtRet_YToM(new double[] { rate })
					.getResultArray();
			double result = Math.pow(yearRate[0][0] + 1.0, monthRange) * amount;
			return new ResultObj("00", "Success", result);
		} catch (Exception e) {
			return new ResultObj("99", "System Failure. " + e.toString());
		}
	}

	/**
	 * 計算經過 period 個月後的現金現值
	 * 
	 * @param period
	 * @param amount
	 * @param rate
	 * @return
	 */
	public ResultObj cf_PV(int period, double amount, double rate) {
		ResultObj tmp = chkRet(new double[][] { { rate } });
		if (!tmp.getResultCode().equals(ERR_MSG_00[0]))
			return ((ResultObj) tmp);
		if (period < 0)
			return new ResultObj(ERR_MSG_05[0], ERR_MSG_05[1]);
		try {
			double result = amount / Math.pow(rate + 1.0, period);
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], result);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 計算年金現金終值
	 * 
	 * @param period
	 *            int[],現金流量發生期間
	 * @param amount
	 *            double[],現金流量
	 * @param rate
	 * @return
	 */
	public ResultObj cf_PVA(int[] period, double[] amount, double rate) {
		if (period.length == 0 || amount.length == 0)
			return new ResultObj(ERR_MSG_09[0], ERR_MSG_09[1]);
		ResultObj tmp = chkRet(new double[][] { { rate } });
		if (!tmp.getResultCode().equals(ERR_MSG_00[0]))
			return ((ResultObj) tmp);
		if (amount.length != period.length)
			return new ResultObj(ERR_MSG_04[0], ERR_MSG_04[1]);
		double result = 0;
		try {
			for (int i = 0; i < amount.length; i++) {
				ResultObj tmpResult = cf_PV(period[i], amount[i], rate);
				if (tmpResult.getResultCode().equals("00")) {
					result += tmpResult.getResultArray()[0][0];
				} else {
					return tmpResult;
				}
			}
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], result);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 計算定期定額所需金額，在給定現金流量期間與金額、年利率、投資起始月份時。
	 * 
	 * @param period
	 *            現金流量期間
	 * @param amount
	 *            現金流量
	 * @param rate
	 *            年利率
	 * @param startMon
	 *            投資起始月份
	 * @param endMon
	 *            投資終了月份
	 * @return
	 */
	public ResultObj cf_RegPay(int[] period, double[] amount, double rate,
			int startMon, int endMon) {
		ResultObj dollarAmount = cf_PVA(period, amount, rate);
		if (!dollarAmount.getResultCode().equals(ERR_MSG_00[0]))
			return dollarAmount;
		if (endMon < startMon)
			return new ResultObj(ERR_MSG_07[0], ERR_MSG_07[1]);
		double result = 0;
		try {
			result = dollarAmount.getResultArray()[0][0]
					/ ((Math.pow(rate + 1.0, endMon - startMon + 1) - 1) / (rate * Math
							.pow(rate + 1, endMon)));
			if (result > 0) {
				return new ResultObj(ERR_MSG_22[0], ERR_MSG_22[1]);
			} else if (result < NEGATIVE_MIN) {
				return new ResultObj(ERR_MSG_23[0], ERR_MSG_23[1]);
			}
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], result);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 計算各標的物和投資組合比較的Beta
	 * 
	 * @param covMtx
	 *            投資組合共變異數矩陣
	 * @param weight
	 *            各投資標的權重
	 * @return
	 */
	public ResultObj perform_BetaIndv(double[][] covMtx, double[] weight) {
		ResultObj tmp = chkWeight(weight);
		if (!tmp.getResultCode().equals(ERR_MSG_00[0]))
			return ((ResultObj) tmp);
		tmp = chkStd(covMtx);
		if (!tmp.getResultCode().equals(ERR_MSG_00[0]))
			return ((ResultObj) tmp);
		if (covMtx.length != weight.length)
			return new ResultObj(ERR_MSG_04[0], ERR_MSG_04[1]);
		try {
			RealMatrix cov = MatrixUtils.createRealMatrix(covMtx);
			RealMatrix rowW = MatrixUtils.createRowRealMatrix(weight);
			RealMatrix colW = MatrixUtils.createColumnRealMatrix(weight);
			RealMatrix tmpColMtx = null;
			RealMatrix tmpColW = null;
			RealMatrix portVar = null;
			double[] result = new double[weight.length];
			for (int i = 0; i < weight.length; i++) {
				tmpColMtx = cov.getColumnMatrix(i);
				tmpColW = rowW.multiply(tmpColMtx);
				portVar = rowW.multiply(cov).multiply(colW);
				result[i] = tmpColW.getData()[0][0] / portVar.getData()[0][0];
			}
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], result);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 計算 beta 指標
	 * 
	 * @param portRet
	 *            投資組合報酬率
	 * @param benchRet
	 *            標竿指數報酬率
	 * @return
	 */
	public ResultObj perform_Beta(double[] portRet, double[] benchRet) {
		double[][] tmpRet = new double[][] { portRet, benchRet };
		ResultObj tmpObj = chkRet(tmpRet);
		if (!tmpObj.getResultCode().equals(ERR_MSG_00[0])) {
			StringBuilder errStr_sb = new StringBuilder(((ResultObj) tmpObj)
					.getResultStr());
			errStr_sb.append("\nPortfolio return at 1st row,Benchmark at 2nd.");
			return new ResultObj(((ResultObj) tmpObj).getResultCode(),
					errStr_sb.toString());
		}
		try {
			double[] tmpAvg_d = util_GetMean(tmpRet);
			double cov_d = util_Covar(0, 1, tmpRet, tmpAvg_d);
			double benchVar_d = util_Covar(1, 1, tmpRet, tmpAvg_d);
			double res_d = cov_d / benchVar_d;
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], res_d);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 計算 alpha = y_avg - x_avg*beta
	 * 
	 * @param portRet
	 *            投資組合報酬率
	 * @param benchRet
	 *            標竿指數報酬率
	 * @return
	 */
	public ResultObj perform_Alpha(double[] portRet, double[] benchRet) {
		double[][] tmpRet = new double[][] { portRet, benchRet };
		ResultObj tmpObj = chkRet(tmpRet);
		if (!tmpObj.getResultCode().equals(ERR_MSG_00[0])) {
			StringBuilder errStr_sb = new StringBuilder(((ResultObj) tmpObj)
					.getResultStr());
			errStr_sb.append("\nPortfolio return at 1st row,Benchmark at 2nd.");
			return new ResultObj(((ResultObj) tmpObj).getResultCode(),
					errStr_sb.toString());
		}
		ResultObj tmpBetaObj = perform_Beta(portRet, benchRet);
		if (tmpBetaObj.getResultCode().equals(ERR_MSG_00[0])) {
			double[] tmpMean = util_GetMean(new double[][] { portRet, benchRet });
			double alpha_d = tmpMean[0] - tmpMean[1]
					* tmpBetaObj.getResultArray()[0][0];
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], alpha_d);
		} else {
			return tmpBetaObj;
		}
	}

	/**
	 * 計算風格座標
	 * 
	 * @param xlyl
	 *            XLowYLow下的風格指數配置比例
	 * @param xhyl
	 *            XHighYLow下的風格指數配置比例
	 * @param xlyh
	 *            XLowYHigh下的風格指數配置比例
	 * @param xhyh
	 *            XHighYHigh下的風格指數配置比例
	 * @return position[2],position[0]→X座標; position[1]→Y座標
	 */
	public ResultObj perform_StylePoint(double xlyl, double xhyl, double xlyh,
			double xhyh) {
		double[] position = new double[2];
		try {
			// position x
			position[0] = xlyl * -1.0 + xhyl * 1.0 + xlyh * -1.0 + xhyh * 1.0;
			// position y
			position[1] = xlyl * -1.0 + xhyl * -1.0 + xlyh * 1.0 + xhyh * 1.0;
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], position);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 計算調整後判定係數(解釋變異)
	 * 
	 * @param portRet
	 *            各績金的報酬率矩陣
	 * @param benchRet
	 * @return
	 */
	public ResultObj perform_AdjRSquared(double[][] portRet, double[] benchRet) {
		ResultObj tmpObj = chkRet(portRet);
		if (!tmpObj.getResultCode().equals(ERR_MSG_00[0])) {
			return new ResultObj(tmpObj.getResultCode(), tmpObj.getResultStr()
					+ "Error at portRet.");
		}
		tmpObj = chkRet(new double[][] { benchRet });
		if (!tmpObj.getResultCode().equals(ERR_MSG_00[0])) {
			return new ResultObj(tmpObj.getResultCode(), tmpObj.getResultStr()
					+ "Error at benchRet.");
		}
		if (portRet[0].length != benchRet.length) {
			return new ResultObj(ERR_MSG_04[0], ERR_MSG_04[1]);
		}
		double[] result = new double[portRet.length];
		double[] tmpRet;
		try {

			for (int index_i = 0; index_i < portRet.length; index_i++) {
				tmpRet = portRet[index_i];
				ResultObj SSE = perform_SSE(tmpRet, benchRet);
				if (!SSE.getResultCode().equals(ERR_MSG_00[0])) {
					return SSE;
				}
				ResultObj sstObj = perform_TSS(tmpRet);
				if (!sstObj.getResultCode().equals(ERR_MSG_00[0])) {
					return sstObj;
				}
				double sst_d = sstObj.getResultArray()[0][0] / tmpRet.length;
				double sse_d = SSE.getResultArray()[0][0] / tmpRet.length;
				result[index_i] = 1 - (sse_d / (tmpRet.length - 1 - 1))
						/ (sst_d / (tmpRet.length - 1));
			}
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], result);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 計算跨期年化報酬率分析
	 * 
	 * @param ret
	 *            double[n][m]，表示有n檔資產，m個期間報酬率
	 * @param period_i
	 *            指一次要減少的期間數，如為6，則表示每次減少半年資料計算
	 * @param date
	 *            每個報酬率的時間點
	 * @return
	 */
	public ResultObj util_CrosRet(double[][] ret, int period_i, String[] date) {
		ResultObj tmpObj = chkRet(ret);
		if (!tmpObj.getResultCode().equals(ERR_MSG_00[0])) {
			return tmpObj;
		}
		if (period_i <= 0) {
			return new ResultObj(ERR_MSG_05[0], ERR_MSG_05[1]);
		}
		double[] tmpArr;
		int nArrayLen = ret[0].length / period_i;
		
		if (ret[0].length % period_i > 0) {
			++nArrayLen;
		}
		
		String[] resultStr = new String[nArrayLen];
		try {
			double[][] result = new double[ret.length][nArrayLen];
			
			for (int indexCol_i = 0; indexCol_i < ret[0].length; indexCol_i = indexCol_i
					+ period_i) {
				tmpArr = util_AnnualRet(ret, indexCol_i, ret[0].length);
				for (int indexRow_i = 0; indexRow_i < ret.length; indexRow_i++) {
					result[indexRow_i][(int) (indexCol_i / period_i)] = tmpArr[indexRow_i];
				}
				resultStr[(int) (indexCol_i / period_i)] = date[indexCol_i];
			}
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], result,
					resultStr);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 計算年化報酬率數值(for grid & chart)
	 * 
	 * @param ret
	 *            報酬率矩陣
	 * @return
	 */
	public ResultObj util_AnnualRet(double[][] ret) {
		ResultObj tmpObj = chkRet(ret);
		if (!tmpObj.getResultCode().equals(ERR_MSG_00[0]))
			return tmpObj;
		try {
			double[] result = util_AnnualRet(ret, 0, ret[0].length);
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], result);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 支援AnnualRet chart ,start&end 為index數值，不含end數值 ,引用前得先自行做資料檢查
	 * 
	 * @param ret
	 * @param start
	 * @param end
	 * 
	 * @return
	 * */
	private double[] util_AnnualRet(double[][] ret, int start, int end) {
		double[][] tmpRet = util_ArrayCopy(ret, start, Math.min(ret[0].length,
				end));
		ResultObj tmpObj = util_GeoRet(tmpRet);
		tmpObj = convtRet_MToY(tmpObj.getResultArray()[0]);
		return tmpObj.getResultArray()[0];
	}

	/**
	 * 計算年化標準差 (for grid & chart)
	 * 
	 * @param ret
	 * @return
	 */
	public ResultObj util_AnnualStd(double[][] ret) {
		ResultObj tmpObj = chkRet(ret);
		if (!tmpObj.getResultCode().equals(ERR_MSG_00[0])) {
			return tmpObj;
		}
		try {
			double[] result = util_AnnualStd(ret, 0, ret[0].length);
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], result);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 支援AnnualStd chart ,start&end 為index數值，不含end數值 ,引用前得先自行做資料檢查
	 * 
	 * @param ret
	 * @param start
	 * @param end
	 * @return
	 * */
	private double[] util_AnnualStd(double[][] ret, int start, int end) {
		double[][] tmpRet = util_ArrayCopy(ret, start, Math.min(ret[0].length,
				end));
		ResultObj tmpObj = util_STD(tmpRet);
		tmpObj = convtStd_MToY(tmpObj.getResultArray()[0]);
		return tmpObj.getResultArray()[0];
	}

	/**
	 * 計算年化報酬率、標準差，以便作數據、圖形分析
	 * 
	 * @param ret
	 *            各檔基金報酬率 ,double[n][m] ,表n檔資產,m個期間報酬
	 * @return 數據為2-row n-col array ;Array[0]為各檔年化報酬率,Array[1]為各檔年化標準差
	 */
	public ResultObj util_AnnualRetStd(double[][] ret) {
		try {
			ResultObj tmpRetObj = util_AnnualRet(ret);
			if (!tmpRetObj.getResultCode().equals(ERR_MSG_00[0])) {
				return tmpRetObj;
			}
			ResultObj tmpStdObj = util_AnnualStd(ret);
			if (!tmpStdObj.getResultCode().equals(ERR_MSG_00[0])) {
				return tmpRetObj;
			}
			double[][] result = new double[2][ret[0].length];
			result[0] = tmpRetObj.getResultArray()[0];
			result[1] = tmpStdObj.getResultArray()[0];
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], result);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 計算移動區間的超額風險及報酬
	 * 
	 * @param ret
	 *            各檔基金報酬率 double[n][m]，表示有n檔基金，m個期間報酬率
	 * @param period
	 *            每次移動的區間
	 * @param date
	 *            每次報酬率的日期
	 * @return 回傳ResultObj Array，第一個element為各檔基金的移動平均報酬率，第二個為移動平均標準差
	 */
	public ResultObj[] util_MovAvgRetStd(double[][] ret, int period,
			String[] date) {
		ResultObj[] result = new ResultObj[2];
		ResultObj tmpObj = chkRet(ret);
		if (!tmpObj.getResultCode().equals(ERR_MSG_00[0])) {
			result[0] = tmpObj;
			result[1] = result[0];
			return result;
		}
		if (period <= 0) {
			result[0] = new ResultObj(ERR_MSG_05[0], ERR_MSG_05[1]);
			result[1] = result[0];
			return result;
		}
		if (ret[0].length != date.length) {
			result[0] = new ResultObj(ERR_MSG_04[0], ERR_MSG_04[1]);
			result[1] = result[0];
			return result;
		}
		
		double[] tmpArr;
		String[] resultStr = new String[ret[0].length - period + 1];
		try {
			double[][] resultRet = new double[ret.length][ret[0].length
					- period + 1];
			for (int indexCol_i = 0; indexCol_i < ret[0].length - period + 1; indexCol_i++) {
				tmpArr = util_AnnualRet(ret, indexCol_i, indexCol_i + period);
				for (int indexRow_i = 0; indexRow_i < ret.length; indexRow_i++) {
					resultRet[indexRow_i][indexCol_i] = tmpArr[indexRow_i];
				}
			}
			double[][] resultStd = new double[ret.length][ret[0].length
					- period + 1];
			for (int index_i = 0; index_i < ret[0].length - period + 1; index_i++) {
				tmpArr = util_AnnualStd(ret, index_i, index_i + period);
				for (int indexRow_i = 0; indexRow_i < ret.length; indexRow_i++) {
					resultStd[indexRow_i][index_i] = tmpArr[indexRow_i];
				}
				resultStr[index_i] = date[index_i];
			}
			result[0] = new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], resultRet,
					resultStr);
			result[1] = new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], resultStd,
					resultStr);
			return result;
		} catch (Exception e) {
			result[0] = new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1]
					+ e.toString());
			result[1] = result[0];
			return result;
		}
		
		
//		int nArrayLen = (ret[0].length / period);
//		
//		if ((ret[0].length % period) > 0) {
//			nArrayLen++;
//		}
//		
//		int nArrayOriginalLen = ret[0].length;
//		
//		String[] resultStr = new String[nArrayLen];
//		try {
//			double[][] resultRet = new double[ret.length][nArrayLen];
//			double[][] resultStd = new double[ret.length][nArrayLen];
//			
//			for (int indexCol_i = 0; indexCol_i < nArrayLen; indexCol_i++) {
//				int nStartIndex = indexCol_i * period;
//				int nEndIndex = (indexCol_i + 1) * period - 1;
//				
//				if (nEndIndex >= nArrayOriginalLen) {
//					nEndIndex = nArrayOriginalLen - 1;
//				}
//				
//				double[] tmpRet = util_AnnualRet(ret, nStartIndex, nEndIndex);
//				double[] tmpStd = util_AnnualStd(ret, nStartIndex, nEndIndex);
//				
//				for (int indexRow_i = 0; indexRow_i < ret.length; indexRow_i++) {
//					resultRet[indexRow_i][indexCol_i] = tmpRet[indexRow_i];
//					resultStd[indexRow_i][indexCol_i] = tmpStd[indexRow_i];				
//				}
//				
//				resultStr[indexCol_i] = date[nStartIndex] + '-' + date[nEndIndex];
//			}
//			
//			result[0] = new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], resultRet,
//					resultStr);
//			result[1] = new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], resultStd,
//					resultStr);
//			return result;
//		} 
//		catch (Exception e) {
//			result[0] = new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1]
//					+ e.toString());
//			result[1] = result[0];
//			return result;
//		}
	}

	/**
	 * 給 crosRet,AnnualRet用，用以複製 array[][start~end], start&end 為index數值,
	 * 不含end數值故[start,start+1,...,end-1]
	 * 
	 * @param src
	 * @param start
	 * @param end
	 * @return
	 * */
	private double[][] util_ArrayCopy(double[][] src, int start, int end) {
		double[][] result = new double[src.length][end - start];
		for (int index_i = 0; index_i < src.length; index_i++) {
			result[index_i] = ArrayUtils.subarray(src[index_i], start, end);
		}
		return result;
	}

	/**
	 * 計算基金分析的資料表，內容如後 標的物名稱 年化報酬率% 年化標準差 年化超額報酬率% 超額風險% 資訊比例% 累積超額報酬率% 累積報酬率%
	 * 解釋變異%
	 * 
	 * @param ret
	 *            所有選擇的基金報酬率
	 * @param benchRet
	 *            標竿指數報酬率
	 * @return
	 */
	public ResultObj util_GridPresent(double[][] ret, double[] benchRet) {
		ResultObj tmpObj = chkRet(ret);
		if (!tmpObj.getResultCode().equals(ERR_MSG_00[0])) {
			return tmpObj;
		}
		tmpObj = chkRet(new double[][] { benchRet });
		if (!tmpObj.getResultCode().equals(ERR_MSG_00[0])) {
			return tmpObj;
		}
		if (ret[0].length != benchRet.length) {
			return new ResultObj(ERR_MSG_05[0], ERR_MSG_05[1]);
		}
		double[][] resultArr = new double[ret.length][8];
		double[] tmpArr;
		double[] portRet;
		try {
			for (int index_i = 0; index_i < ret.length; index_i++) {
				portRet = ret[index_i];
				tmpArr = util_AnnualRet(new double[][] { portRet }, 0,
						portRet.length);
				resultArr[index_i][0] = tmpArr[0];
				tmpArr = util_AnnualStd(new double[][] { portRet }, 0,
						portRet.length);
				resultArr[index_i][1] = tmpArr[0];
				tmpObj = perform_ExRet(portRet, benchRet);
				resultArr[index_i][2] = tmpObj.getResultArray()[0][0];
				tmpObj = perform_ExVar(portRet, benchRet);
				resultArr[index_i][3] = tmpObj.getResultArray()[0][0];
				tmpObj = perform_InfoRatio(portRet, benchRet);
				resultArr[index_i][4] = tmpObj.getResultArray()[0][0];
				tmpObj = perform_CumExRet(portRet, benchRet);
				resultArr[index_i][5] = tmpObj.getResultArray()[0][0];
				tmpObj = util_CumRet(new double[][] { portRet });
				resultArr[index_i][6] = tmpObj.getResultArray()[0][0];
				tmpObj = perform_SSE(portRet, benchRet);
				resultArr[index_i][7] = tmpObj.getResultArray()[0][0];
			}
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], resultArr);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 本函式用以計算勞退新制後的退休金，其相關資料請參考http://web.cla.gov.tw/trial/
	 * personal_account_frame.asp
	 * 
	 * @param initSalary_d
	 *            初始月薪
	 * @param investRet_d
	 *            資金報酬率
	 * @param salaryGrowth_d
	 *            薪資成長率
	 * @param withdrawRate_d
	 *            薪資提撥率
	 * @param workYear_i
	 *            工作年資
	 * @param origPrincipal
	 *            原始從勞退舊制移入專戶之退休金至退休時累積本金及收益
	 * @return 回傳值將會是double[1][2] ，element1為一次性退休金，element2為退休年金
	 */
	public ResultObj cf_LaborPension(double initSalary_d, double investRet_d,
			double salaryGrowth_d, double withdrawRate_d, int workYear_i,
			double origPrincipal) {

		double realSalary_d = 0.0; // 計算每年經薪資成長後的實際月薪資
		double withdrawSalary_d = 0.0; // 每月應提撥薪資
		double yearWithdraw_d = 0.0; // 計算每年提撥數
		double interest_d = 0.0; // 計算每年投資收益
		double cumWithdraw_d = 0.0; // 累計提撥本金
		double cumPrincipal_d = 0.0; // 累計本金及收益
		double annuity_d = 0.0; // 計算最後每月年金
		double mInvestRet_d = investRet_d / 12; // 計算月投資報酬率
		realSalary_d = initSalary_d;

		if (initSalary_d < 0)
			return new ResultObj(ERR_MSG_24[0], ERR_MSG_24[1]);
		if (investRet_d < -1)
			return new ResultObj(ERR_MSG_01[0], ERR_MSG_01[1]);
		if (withdrawRate_d < 0)
			return new ResultObj(ERR_MSG_25[0], ERR_MSG_25[1]);
		if (workYear_i < 0)
			return new ResultObj(ERR_MSG_26[0], ERR_MSG_26[1]);
		if (salaryGrowth_d < -1)
			return new ResultObj(ERR_MSG_30[0], ERR_MSG_30[1]);

		try {
			for (int index_i = 0; index_i < workYear_i; index_i++) {
				if (index_i != 0)
					realSalary_d *= (1 + salaryGrowth_d); // 每年薪資成長
				int tmpIndex_i = 0;
				while (realSalary_d >= SALARY_RANGE_60[tmpIndex_i] + 1e-5) {
					tmpIndex_i += 1;
				}
				if (tmpIndex_i == SALARY_RANGE_60.length) {
					tmpIndex_i = SALARY_RANGE_60.length;
				}
				withdrawSalary_d = SALARY_RANGE_60[tmpIndex_i];
				yearWithdraw_d = withdrawSalary_d * withdrawRate_d * 12;
				interest_d = interest_d
						+ (withdrawSalary_d * withdrawRate_d * 5.5 + cumPrincipal_d)
						* investRet_d;
				cumWithdraw_d = cumWithdraw_d + yearWithdraw_d;
				cumPrincipal_d = cumWithdraw_d + interest_d;
			}
			cumPrincipal_d = Math.max(0, cumPrincipal_d + origPrincipal);
			annuity_d = (cumPrincipal_d * Math.pow(1 + mInvestRet_d, 240))
					* mInvestRet_d
					/ ((1 + mInvestRet_d) * (Math.pow((1 + mInvestRet_d), 240) - 1));
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1],
					new double[][] { { Math.round(cumPrincipal_d),
							Math.round(annuity_d) } });
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}

	}

	/**
	 * 本函數用以試算勞保老年年金，其相關資料請參考 http://www.bli.gov.tw/oldPay_Compare.asp
	 * 
	 * @param initSalary_d
	 *            初始薪資
	 * @param salaryGrowth_d
	 *            薪資成長率
	 * @param origInsYear_d
	 *            已投保勞保的期間
	 * @param workYear_d
	 *            預計工作年限=預計保險年限
	 * @param timeShift_d
	 *            提前或延後領取年金的年限,正數表延後領,-5<timeShift<5
	 * @return 回傳值將會是double[1][3] ，element1為一次性退休金，element2為公式A年金，element3為公式B年金
	 */
	public ResultObj cf_LabInsAnnuity(double initSalary_d,
			double salaryGrowth_d, double origInsYear_d, double workYear_d,
			int timeShift_d) {
		if (initSalary_d < 0) {
			return new ResultObj(ERR_MSG_24[0], ERR_MSG_24[1]);
		}
		if (salaryGrowth_d < -1) {
			return new ResultObj(ERR_MSG_30[0], ERR_MSG_30[1]);
		}
		if (origInsYear_d < 0) {
			return new ResultObj(ERR_MSG_31[0], ERR_MSG_31[1]);
		}
		if (workYear_d < 0) {
			return new ResultObj(ERR_MSG_26[0], ERR_MSG_26[1]);
		}
		if (timeShift_d < -5 || timeShift_d > 5) {
			return new ResultObj(ERR_MSG_32[0], ERR_MSG_32[1]);
		}

		double realSalary_d = initSalary_d; // 計算每年經薪資成長後的實際月薪資，先設定為初始薪資
		double avgInsSalary_d = 0; // 平均最高60個月之月投保薪資
		double avg3InsSalary_d = 0; // 退休前3年之月投保薪資
		double cumPrincipal_d = 0; // 一次金
		double annuityA_d = 0; // 年金，公式A
		double annuityB_d = 0; // 年金，公式B
		double multiplierA_d = 0.00775; // 公式A的乘數
		double multiplierB_d = 0.0155; // 公式B的乘數
		double baseA_d = 3000; // 公式A的基數
		double shiftMultiplier_d = 0.04; // 若提前或延後領取年金則每年減增領取百分比
		double totalYear_d = origInsYear_d + workYear_d; // 總投保年資

		try {
			for (int index_i = 0; index_i < workYear_d; index_i++) {
				realSalary_d *= (1 + salaryGrowth_d); // 每年薪資成長
				int tmpIndex_i = 0;
				while (realSalary_d >= SALARY_RANGE[tmpIndex_i] + 1e-5)
					tmpIndex_i += 1;
				if (tmpIndex_i == SALARY_RANGE.length)
					tmpIndex_i = SALARY_RANGE.length;
				if (workYear_d - index_i < 6)
					avgInsSalary_d = avgInsSalary_d + SALARY_RANGE[tmpIndex_i];
				if (workYear_d - index_i < 4)
					avg3InsSalary_d = avg3InsSalary_d
							+ SALARY_RANGE[tmpIndex_i];
			}
			avgInsSalary_d = avgInsSalary_d / 5;
			avg3InsSalary_d = avg3InsSalary_d / 3;
			if (totalYear_d > 15) {
				annuityA_d = avgInsSalary_d * (totalYear_d) * multiplierA_d
						+ baseA_d;
				annuityA_d = annuityA_d * (1 + shiftMultiplier_d * timeShift_d);
				annuityB_d = avgInsSalary_d * (totalYear_d) * multiplierB_d;
				annuityB_d = annuityB_d * (1 + shiftMultiplier_d * timeShift_d);
				cumPrincipal_d = avg3InsSalary_d
						* Math.min(45, 15 + (totalYear_d - 15) * 2);// 一次金最高只給45個月
			} else {
				cumPrincipal_d = avg3InsSalary_d * totalYear_d;// 一次金最高只給45個月
			}

			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1],
					new double[][] { { Math.round(cumPrincipal_d),
							Math.round(annuityA_d), Math.round(annuityB_d) } });
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 本函數用以試算國民老年年金，其相關資料請參考 http://www.bli.gov.tw/oldPay_Compare.asp
	 * 
	 * @param insYear
	 *            投保年資
	 * @return 回傳值將會是double[1][2] ，element1為公式A年金，element2為公式B年金
	 */
	public ResultObj cf_NationalPension(double insYear) {
		if (insYear < 0)
			return new ResultObj(ERR_MSG_33[0], ERR_MSG_33[1]);
		double insPremium_d = 17280; // 投保金額，目前以行政院最低工資為投保金額
		double annuityA_d = 0; // 計算公式A年金
		double annuityB_d = 0; // 計算公式B年金
		double multiplierA_d = 0.0065; // 公式A的乘數
		double multiplierB_d = 0.013; // 公式B的乘數
		double baseA_d = 3000; // 公式A的基數

		try {
			annuityA_d = insPremium_d * multiplierA_d * insYear + baseA_d;
			annuityB_d = insPremium_d * multiplierB_d * insYear;
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1],
					new double[][] { { Math.round(annuityA_d),
							Math.round(annuityB_d) } });
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 本函數用以牛頓法計算 IRR 數值
	 * 
	 * @param occurMon
	 *            現金流發生月份，以正數表示未來
	 * @param cashflow
	 *            現金流量，以正表示收入、負表示支出
	 * @param maxIter_i
	 *            最大猜測次數
	 * @return
	 */
	public ResultObj cf_IRR(int[] occurMon, double[] cashflow, int maxIter_i) {

		try {
			int[] sortMon = ArrayUtils.clone(occurMon);
			Arrays.sort(sortMon);
			int maxLenMon_i = sortMon[sortMon.length - 1];
			int minMon_i = sortMon[0];
			maxIter_i = Math.max(maxIter_i, 100);
			minMon_i = Math.min(minMon_i, 0);
			double[] coeff = new double[(maxLenMon_i - minMon_i) + 1];
			for (int i = 0; i < occurMon.length; i++) {
				coeff[maxLenMon_i - occurMon[i]] = cashflow[i];
			}
			PolynomialFunction function = new PolynomialFunction(coeff);// 依序為x^0
			// , x^1
			// ,
			// x^2,
			// ....
			// , x^n 的係數
			UnivariateRealSolverFactory factory = UnivariateRealSolverFactory
					.newInstance();
			UnivariateRealSolver solver = factory.newNewtonSolver();
			// solver.solve(function, Double.MIN_VALUE, Double.MAX_VALUE, 1.1);
			// newNewtonSolver(function);
			solver.setMaximalIterationCount(maxIter_i);
			double irr_d = solver.solve(function, Double.MIN_VALUE,
					Double.MAX_VALUE, 1.1);
			// solver.solve(Double.MIN_VALUE, Double.MAX_VALUE, 1.1);
			// 若算出的irr小於1，則無合理的可行解
			if (irr_d < 1) {
				return new ResultObj(ERR_MSG_28[0], ERR_MSG_28[1]);
			}
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], irr_d - 1);
		} catch (ConvergenceException e) {
			return new ResultObj(ERR_MSG_27[0], ERR_MSG_27[1]);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 計算 normal dist. 下的cdf
	 * 
	 * @param mu
	 *            mean
	 * @param sigma
	 *            std
	 * @param target
	 *            CDF(X<target)
	 * @return
	 */
	public ResultObj prob_NormalCDF(double mu, double sigma, double target) {
		try {
			NormalDistributionImpl instance = new NormalDistributionImpl(mu,
					sigma);
			double result_d = instance.cumulativeProbability(target);
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], result_d);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 計算 normal dist. 下的 1-cdf
	 * 
	 * @param mu
	 *            mean
	 * @param sigma
	 *            std
	 * @param target
	 *            CDF(X>target)
	 * @return
	 */
	public ResultObj prob_CompNormalCDF(double mu, double sigma, double target) {
		try {
			NormalDistributionImpl instance = new NormalDistributionImpl(mu,
					sigma);
			double result_d = 1 - instance.cumulativeProbability(target);
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], result_d);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 計算 normal dist. 下的 inverse-CDF
	 * 
	 * @param mu
	 *            mean
	 * @param sigma
	 *            std
	 * @param probability
	 *            where prob = CDF(X<target)
	 * @return
	 */
	public ResultObj prob_InvNormalCDF(double mu, double sigma,
			double probability) {
		try {
			NormalDistributionImpl instance = new NormalDistributionImpl(mu,
					sigma);
			double result_d = instance
					.inverseCumulativeProbability(probability);
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], result_d);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 計算定期定額累積報酬率。利用基金歷史淨值資料，計算區間內的累積報酬率。
	 * 
	 * @param nav
	 *            每月歷史淨值
	 * @param cashflow
	 *            每期投資金額
	 * @return
	 * 
	 * ==================================================================
	 * @author 1100178 Ben Chen
	 * @createDate 2013/07/15
	 * @version 1.0
	 * ==================================================================
	 */
	public ResultObj pipAccumRtn(double[] nav, int[] cashflow) {
		try {
			// Check input parameters before calculation.
			if (nav == null || nav.length <= 0) {
				return new ResultObj(ERR_MSG_40[0], ERR_MSG_40[1]);
			}
			
			if (cashflow == null || cashflow.length <= 0) {
				return new ResultObj(ERR_MSG_41[0], ERR_MSG_41[1]);
			}
			
			int nInvestTimes = nav.length;
			double[][] accumRtnMtx = new double[1][nInvestTimes];
			int accumInvestAmt = 0;
			double investUnit = 0;
						
			// 計算各期定期定額累積報酬率
			for (int i=0; i < nInvestTimes; i++) {
				accumInvestAmt += cashflow[i];				
				investUnit += cashflow[i] / nav[i];
				
				accumRtnMtx[0][i] = (investUnit * nav[i] - accumInvestAmt) / accumInvestAmt;
			}
			
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], accumRtnMtx);
		} 
		catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}
	
	/**
	 * 計算定期定額的蒙地卡羅模擬法；本函數會根據現金流量期數來決定模擬期數，兩者相等。
	 * 
	 * @param ret
	 *            報酬率矩陣
	 * @param weight
	 *            投資權重
	 * @param cashflow
	 *            每期的現金流量
	 * @param simTimes
	 *            模擬次數
	 * @return
	 */
	public ResultObj cf_MonteCarloReg(double[][] ret, double[] weight,
			double[] cashflow, int simTimes) {
		// 取得BrownianMotion 中的mu
		ResultObj tmpObj = port_Ret(ret, weight);
		if (!tmpObj.getResultCode().equals(ERR_MSG_00[0]))
			return tmpObj;
		double[] tmpMean = tmpObj.getResultArray()[0];
		tmpMean = util_GetMean(new double[][] { tmpMean });
		double mu_d = tmpMean[0];
		// double mu = 0.05;
		// 取得 sigma
		tmpObj = port_Variance(ret, weight);
		if (!tmpObj.getResultCode().equals(ERR_MSG_00[0])) {
			return tmpObj;
		}
		double sigma_d = Math.sqrt(tmpObj.getResultArray()[0][0]);
		if (simTimes < 0) {
			return new ResultObj(ERR_MSG_34[0], ERR_MSG_34[1]);
		}
		tmpObj = cf_MonteCarloSim(mu_d, sigma_d, cashflow, simTimes);
		return tmpObj;
	}

	/**
	 * 計算定期定額的蒙地卡羅模擬法；本函數會根據現金流量期數來決定模擬期數，兩者相等。 本函數用在當只知道報酬率、標準差時，可以以此做運算。
	 * 
	 * @param mu
	 *            報酬率
	 * @param sigma
	 *            波動率
	 * @param cashflow
	 *            每期的現金流量
	 * @param simTimes
	 *            模擬次數
	 * @return
	 */
	public ResultObj cf_MonteCarloSim(double mu, double sigma,
			double[] cashflow, int simTimes) {
		try {
			// generate simulation matrix
			if (mu < 0) {
				return new ResultObj(ERR_MSG_01[0], ERR_MSG_01[1]);
			}
			if (sigma < 0) {
				return new ResultObj(ERR_MSG_11[0], ERR_MSG_11[1]);
			}
			if (simTimes < 0) {
				return new ResultObj(ERR_MSG_34[0], ERR_MSG_34[1]);
			}

			double[][] simMtx = new double[simTimes][cashflow.length];
			Random random = new Random();
			double rndNum_d;
			for (int indexRow_i = 0; indexRow_i < simTimes; indexRow_i++) {
				simMtx[indexRow_i][0] = cashflow[0];
				for (int indexCol_i = 1; indexCol_i < cashflow.length; indexCol_i++) {
					rndNum_d = mu + sigma * random.nextGaussian();
					simMtx[indexRow_i][indexCol_i] = simMtx[indexRow_i][indexCol_i - 1]
							* Math.exp(mu - 0.5 * sigma * sigma + sigma
									* rndNum_d) + cashflow[indexCol_i];
				}
			}
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], simMtx);
		} catch (Exception e) {
			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
		}
	}

	/**
	 * 計算單筆投資的蒙地卡羅模擬法
	 * 
	 * @param ret
	 *            報酬率矩陣
	 * @param weight
	 *            投資權重
	 * @param cashflow
	 *            單筆的現金流量
	 * @param simPeriod
	 *            模擬期數
	 * @param simTimes
	 *            模擬次數
	 * @return
	 */
	public ResultObj cf_MonteCarlo(double[][] ret, double[] weight,
			double cashflow, int simPeriod, int simTimes) {
		if (simPeriod < 0 || simTimes < 0) {
			return new ResultObj(ERR_MSG_34[0], ERR_MSG_34[1]);
		}
		double[] cfArray = new double[simPeriod];
		cfArray[0] = cashflow;
		return cf_MonteCarloReg(ret, weight, cfArray, simTimes);
	}

	/**
	 * 給定報酬限制式下，求解最小風險時的權重
	 * 
	 * @param aCov
	 *            Quadratic coefficient
	 * @param aRet
	 *            indivisual asset return
	 * @param targetRet
	 *            that makes portfolio return greater than.
	 * @param inEqu
	 *            [inEqu][x] <= [rhs]
	 * @param rhs
	 * @return
	 */
//	private ResultObj port_QPOpt(double[][] aCov, double[] aRet,
//			double targetRet, double[][] inEqu, double[] rhs) {
//		ResultObj tmpObj = chkRet(inEqu);
//		if (!tmpObj.getResultCode().equals(ERR_MSG_00[0])) {
//			return tmpObj;
//		}
//		if (inEqu.length != rhs.length) {
//			return new ResultObj(ERR_MSG_04[0], ERR_MSG_04[1]);
//		}
//		int nAsset_i = aRet.length;
//		double[] tmp = new double[nAsset_i];
//		Arrays.fill(tmp, 1);
//		double[] cZero = new double[aCov.length];
//		ConvexQuad aSolver = new ConvexQuad(aCov, cZero);
//		// 設定權重為一
//		aSolver.addConstraint(tmp, 1, 1);
//		aSolver.addConstraint(aRet, 2, targetRet);
//		for (int i = 0; i < rhs.length; i++) {
//			aSolver.addConstraint(inEqu[i], 0, rhs[i]);
//		}
//		// if int=0 means lhs < rhs
//		// if int=1 means lhs = rhs
//		// if int=2 means lhs > rhs
//		// 設定上下界
//		for (int index_i = 0; index_i < nAsset_i; index_i++) {
//			aSolver.setHighBound(index_i, 1);
//			aSolver.setLowBound(index_i, 0);
//		}
//		aSolver.setTolerance(1e-9);
//		aSolver.solve();
//		if (aSolver.haveSolution()) {
//			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], aSolver
//					.getSolution());
//		} else {
//			return new ResultObj(ERR_MSG_28[0], ERR_MSG_28[1]);
//		}
//	}

	/**
	 * 計算效率前緣
	 * 
	 * @param ret
	 *            ret 報酬率矩陣 ret[i][j] 各標的物各期報酬率，i 表示有i檔基金，j 表示有j個期間報酬率
	 * @param nPoints
	 *            計算點數
	 * @param inEqu
	 *            [inEqu][x] <= [rhs]
	 * @param rhs
	 * @return [[標準差,報酬率,各標的資產權重],[標準差,報酬率,各標的資產權重],...]
	 */
//	private ResultObj port_EffFrontier(double[][] ret, int nPoints,
//			double[][] inEqu, double[] rhs) {
//		ResultObj tmpObj = port_CovMtx(ret);
//		if (!tmpObj.getResultCode().equals(ERR_MSG_00[0])) {
//			return tmpObj;
//		}
//		if (nPoints < 1) {
//			return new ResultObj(ERR_MSG_29[0], ERR_MSG_29[1]);
//		}
//		double[][] aCov = tmpObj.getResultArray();
//		double[] aRet = util_GetMean(ret);
//		int nAsset_i = aRet.length;
//		try {
//			// search for minimium risk return
//			DoubleMatrix1D aRetMtx = DoubleFactory1D.dense.make(aRet);
//			DoubleMatrix2D aCovMtx = new DenseDoubleMatrix2D(aCov);
//			tmpObj = port_QPOpt(aCov, aRet, 0, inEqu, rhs);
//			if (!tmpObj.getResultCode().equals(ERR_MSG_00[0])) {
//				return tmpObj;
//			}
//			DoubleMatrix1D minVarWeight = new DenseDoubleMatrix1D(tmpObj
//					.getResultArray()[0]);
//			double minVarRet_d = minVarWeight.zDotProduct(aRetMtx);
//			// search for max risk return
//			double[] tmp = new double[nAsset_i];
//			Arrays.fill(tmp, 1);
//			Simplex aSolver = new Simplex(true, aRet);
//			// 設定權重為一
//			aSolver.addConstraint(tmp, 1, 1);
//			// if int=0 means lhs < rhs
//			// if int=1 means lhs = rhs
//			// if int=2 means lhs > rhs
//			int index_i = 0;
//			for (; index_i < rhs.length; index_i++) {
//				aSolver.addConstraint(inEqu[index_i], 0, rhs[index_i]);
//			}
//			// 設定上下界
//			index_i = 0;
//			for (; index_i < nAsset_i; index_i++) {
//				aSolver.setHighBound(index_i, 1);
//				aSolver.setLowBound(index_i, 0);
//			}
//			aSolver.solve();
//			if (!aSolver.haveSolution()) {
//				return new ResultObj(ERR_MSG_28[0], ERR_MSG_28[1]);
//			}
//			DoubleMatrix1D maxRetWeight = new DenseDoubleMatrix1D(aSolver
//					.getSolutionVariableValues());
//			double maxRet_d = maxRetWeight.zDotProduct(aRetMtx);
//
//			DoubleMatrix1D tmpWeight;
//			DoubleMatrix1D tmpMtx = new DenseDoubleMatrix1D(nAsset_i);
//			DoubleMatrix2D tmpMtx2D = new DenseDoubleMatrix2D(0, nAsset_i + 2);
//			double tmpStd, tmpRet;
//			index_i = 0;
//			for (; index_i < nPoints; index_i++) {
//				double targetRet = minVarRet_d + index_i
//						* (maxRet_d - minVarRet_d) / (nPoints - 1);
//				tmpObj = port_QPOpt(aCov, aRet, targetRet, inEqu, rhs);
//				if (!tmpObj.getResultCode().equals(ERR_MSG_00[0])) {
//					return tmpObj;
//				}
//				tmpWeight = new DenseDoubleMatrix1D(tmpObj.getResultArray()[0]);
//				tmpMtx = aCovMtx.zMult(tmpWeight, tmpMtx);
//				tmpStd = Math.sqrt(tmpMtx.zDotProduct(tmpWeight));
//				tmpRet = aRetMtx.zDotProduct(tmpWeight);
//				tmpMtx = DoubleFactory1D.dense.append(new DenseDoubleMatrix1D(
//						new double[] { tmpStd, tmpRet }), tmpWeight);
//				tmpMtx2D = DoubleFactory2D.dense.appendRows(tmpMtx2D,
//						new DenseDoubleMatrix2D(new double[][] { tmpMtx
//								.toArray() }));
//				tmpObj = null;
//				tmpMtx = null;
//
//			}
//			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], tmpMtx2D
//					.toArray());
//		} catch (Exception e) {
//			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1] + e.toString());
//		}
//	}
	/**
	 * 計算效率前緣
	 * 
	 * @param ret
	 *            ret 報酬率矩陣 ret[i][j] 各標的物各期報酬率，i 表示有i檔基金，j 表示有j個期間報酬率
	 * @param nPoints
	 *            計算點數
	 * @return [[標準差,報酬率,各標的資產權重],[標準差,報酬率,各標的資產權重],...]
	 */
//	public ResultObj port_EffFrontier(double[][] ret, int nPoints){
//		try {
//			double[]  rhs = {1};
//			double[][] inEqu = new double[1][ret.length];
//			Arrays.fill(inEqu[0], 0);
//			
//			return port_EffFrontier(ret, nPoints, inEqu, rhs);
//		} catch (Exception e) {
//			return new ResultObj(ERR_MSG_99[0], ERR_MSG_99[1]+e.toString());
//		}
//
//	}

	/**
	 * 進行四捨五入進位動作，以取得整數位或者五倍數的投資權重
	 * 
	 * @param weights
	 *            各標的投資權重
	 * @param precision
	 *            解析度，若為0.01則代表四捨五入到小數以下第二位，表達成百分比為 1% 若為0.05 則代表每個權重為 5% 的倍數
	 * @return
	 */
	private String[] rounder(double[] weights, String precision) {
		String[] strWeight = new String[weights.length];
		BigDecimal bigPrecision_bd = new BigDecimal(precision);
		BigDecimal tmpBig_bd;
		BigDecimal lastBig_bd = BigDecimal.ONE;
		for (int index_i = 0; index_i < strWeight.length; index_i++) {
			tmpBig_bd = new BigDecimal(weights[index_i]);
			tmpBig_bd = tmpBig_bd.multiply(BigDecimal.ONE
					.divide(bigPrecision_bd));
			tmpBig_bd = tmpBig_bd.setScale(0, BigDecimal.ROUND_HALF_UP);
			tmpBig_bd = tmpBig_bd.multiply(bigPrecision_bd);
			if (index_i == strWeight.length - 1) {
				strWeight[index_i] = lastBig_bd.toString();
			} else {
				lastBig_bd = lastBig_bd.subtract(tmpBig_bd);
				strWeight[index_i] = tmpBig_bd.toString();
			}
		}
		return strWeight;
	}

	/**
	 * 給定報酬率，求最佳化配置比率
	 * 
	 * @param ret
	 *            各檔資產的各期報酬率,ret[i][j] 各標的物各期報酬率，i 表示有i檔基金，j 表示有j個期間報酬率
	 * @param targetRet
	 *            目標報酬率
	 * @param precision
	 *            權重解析度
	 * @param inEqu
	 *            [inEqu][x] <= [rhs]
	 * @param rhs
	 * @return resultArr=[[Std,Ret]] , getResultStr()Arr = [各標的物權重]
	 */
//	public ResultObj port_RetOpt(double[][] ret, double targetRet,
//			String precision, double[][] inEqu, double[] rhs) {
//		ResultObj tmpObj = port_CovMtx(ret);
//		double[][] aCov = tmpObj.getResultArray();
//		DoubleMatrix2D aCovMtx = new DenseDoubleMatrix2D(aCov);
//		double[] aRet = util_GetMean(ret);
//		DoubleMatrix1D aRetMtx = DoubleFactory1D.dense.make(aRet);
//		int nAsset_i = aRet.length;
//		try {
//			tmpObj = port_QPOpt(aCov, aRet, targetRet, inEqu, rhs);
//			if (!tmpObj.getResultCode().equals(ERR_MSG_00[0])) {
//				return tmpObj;
//			}
//			DoubleMatrix1D optWeight = new DenseDoubleMatrix1D(tmpObj
//					.getResultArray()[0]);
//			if (Math.abs(1 - optWeight.zSum()) > 0.001) {
//				throw new ArithmeticException();
//			}
//			double optRet_d = optWeight.zDotProduct(aRetMtx);
//			DoubleMatrix1D tmpMtx = new DenseDoubleMatrix1D(nAsset_i);
//			tmpMtx = aCovMtx.zMult(optWeight, tmpMtx);
//			double optStd_d = Math.sqrt(tmpMtx.zDotProduct(optWeight));
//			String[] strWeight = rounder(optWeight.toArray(), precision);
//			return new ResultObj(CMJLB014.ERR_MSG_00[0],
//					CMJLB014.ERR_MSG_00[1], new double[][] { { optStd_d,
//							optRet_d } }, strWeight);
//		} catch (ArithmeticException e) {
//			return new ResultObj(CMJLB014.ERR_MSG_28[0], CMJLB014.ERR_MSG_28[1]);
//		} catch (Exception e) {
//			return new ResultObj(CMJLB014.ERR_MSG_99[0], CMJLB014.ERR_MSG_99[1]
//					+ e.toString());
//		}
//	}

	/**
	 * 給定標準差，求最佳化配置比率
	 * 
	 * @param ret
	 *            各檔資產的各期報酬率,ret[i][j] 各標的物各期報酬率，i 表示有i檔基金，j 表示有j個期間報酬率
	 * @param targetStd
	 *            目標標準差
	 * @param precision
	 *            權重解析度
	 * @param inEqu
	 *            [inEqu][x] <= [rhs]
	 * @param rhs
	 * @return resultArr=[[Std,Ret]] , getResultStr()Arr = [各標的物權重]
	 */
//	public ResultObj port_StdOpt(double[][] ret, double targetStd,
//			String precision, double[][] inEqu, double[] rhs) {
//		int nPoints_i = 1000;
//		int count_i = 0;
//		int nAsset_i = ret.length;
//		ResultObj tmpObj = port_EffFrontier(ret, nPoints_i, inEqu, rhs);
//		if (!tmpObj.getResultCode().equals(CMJLB014.ERR_MSG_00[0])) {
//			return tmpObj;
//		}
//		double[][] frontierArr = tmpObj.getResultArray();
//		try {
//			// 若目標風險低於最小風險投資組合，則跳出
//			if (targetStd < frontierArr[count_i][1]
//					| targetStd > frontierArr[frontierArr.length - 1][1])
//				return new ResultObj(CMJLB014.ERR_MSG_28[0],
//						CMJLB014.ERR_MSG_28[1]);
//			for (int index_i = 0; index_i < nPoints_i; index_i++) {
//				if (targetStd > frontierArr[count_i][1]) {
//					count_i++;
//				}
//			}
//			count_i--;
//			// 取得報酬率與標準差
//			double[][] resultArr = new double[1][2];
//			resultArr[0][0] = frontierArr[count_i][0];
//			resultArr[0][1] = frontierArr[count_i][1];
//			double[] optWeight = new double[nAsset_i];
//			for (int index_i = 0; index_i < nAsset_i; index_i++)
//				optWeight[index_i] = resultArr[count_i][index_i + 2];
//			String[] strWeight = rounder(optWeight, precision);
//			return new ResultObj(CMJLB014.ERR_MSG_00[0],
//					CMJLB014.ERR_MSG_00[1], resultArr, strWeight);
//		} catch (Exception e) {
//			return new ResultObj(CMJLB014.ERR_MSG_99[0], CMJLB014.ERR_MSG_99[1]
//					+ e.toString());
//		}
//	}

	/**
	 * 本函數用途為當有多個計畫的現金流量且每組的起迄日期、頻率、金額、成長率不同時，方便一次計算總計劃的IRR
	 * 
	 * @param startMon
	 *            某計劃的現金流量起始日期
	 * @param freq
	 *            某計劃的現金流量頻率，以月為單位；如：每季付息則參數應為3，一季共有三個月
	 * @param endMon
	 *            某計劃的現金流量終止日期
	 * @param growth
	 *            金額的成長率
	 * @param amount
	 *            現金流量的起始值
	 * @return 
	 *         resultArr[0]為IRR，resultArr[1]為各時點的現金流量，getResultStr()Arr為現金流量的發生時點
	 * 
	 */
	public ResultObj cf_MultiIRR(int[] startMon, int[] freq, int[] endMon,
			double[] growth, double[] amount) {
		if (startMon.length != freq.length || startMon.length != endMon.length
				|| startMon.length != growth.length
				|| startMon.length != amount.length)
			return new ResultObj(ERR_MSG_04[0], ERR_MSG_04[1]);
		int varLen_i = startMon.length;
		int index_i = 0;
		for (; index_i < varLen_i; index_i++) {
			if (startMon[index_i] % 100 > 12 || endMon[index_i] % 100 > 12
					|| startMon[index_i] < 0 || endMon[index_i] < 0) {
				return new ResultObj(ERR_MSG_06[0], ERR_MSG_06[1]);
			}
			if (growth[index_i] < -1) {
				return new ResultObj(ERR_MSG_30[0], ERR_MSG_30[1]);
			}
		}
		try {
			int[] sortMon = ArrayUtils.clone(startMon);
			Arrays.sort(sortMon);
			int minMon_i = sortMon[0];// 取得最早月份
			sortMon = ArrayUtils.subarray(endMon, 0, varLen_i - 1);
			Arrays.sort(sortMon);
			int maxMon_i = sortMon[varLen_i - 1];// 取得最晚月份
			ArrayList<Integer> tmpList = new ArrayList<Integer>();// 儲存完整連續月份
			while (minMon_i <= maxMon_i) {
				if (minMon_i % 100 < 12) {
					tmpList.add(minMon_i);
					minMon_i++;
				} else {
					tmpList.add(minMon_i);
					minMon_i = minMon_i + 100 - 11;
				}
			}
			// 計算個別時點的現金流量
			double[] cashflow = new double[tmpList.size()]; // 儲存現金流量
			index_i = 0;
			for (; index_i < varLen_i; index_i++) {
				int tmpIndex_i = tmpList.indexOf(startMon[index_i]);
				double multiplier = 1;
				while (tmpIndex_i <= tmpList.size()
						&& (Integer) tmpList.get(tmpIndex_i) <= endMon[index_i]) {
					cashflow[tmpIndex_i] = cashflow[tmpIndex_i]
							+ amount[index_i] * multiplier;
					multiplier = multiplier * (1 + growth[index_i]);
					tmpIndex_i = tmpIndex_i + freq[index_i];
				}
			}
			// 計算時點及轉換資料型別以便輸出
			String[] dateString = new String[tmpList.size()];
			int[] occurMon = new int[tmpList.size()];
			index_i = 0;
			for (; index_i < tmpList.size(); index_i++) {
				occurMon[index_i] = index_i;
				dateString[index_i] = ((Integer) tmpList.get(index_i))
						.toString();
			}
			ResultObj tmpIrrObj = cf_IRR(occurMon, cashflow, 1000);
			if (!tmpIrrObj.getResultCode().equals(ERR_MSG_00[0])) {
				return tmpIrrObj;
			}
			double[][] result = new double[2][1];
			result[0] = tmpIrrObj.getResultArray()[0];
			result[1] = cashflow;
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], result,
					dateString);
		} catch (Exception e) {
			return new ResultObj(CMJLB014.ERR_MSG_99[0], CMJLB014.ERR_MSG_99[1]
					+ e.toString());
		}
	}

	/**
	 * 計算任意兩期間的日期差距
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private ResultObj util_GetMonRange(Date startDate, Date endDate) {
		Calendar calStart = Calendar.getInstance();
		calStart.setTime(startDate);
		Calendar calEnd = Calendar.getInstance();
		calEnd.setTime(endDate);
		int startYears_i = calStart.get(Calendar.YEAR);
		int startMonth_i = calStart.get(Calendar.MONTH);
		int endYears_i = calEnd.get(Calendar.YEAR);
		int endMonth_i = calEnd.get(Calendar.MONTH);
		int sMonths_i = startYears_i * 12 + startMonth_i;
		int eMonths_i = endYears_i * 12 + endMonth_i;
		if (eMonths_i - sMonths_i > 0) {
			return new ResultObj(ERR_MSG_00[0], ERR_MSG_00[1], eMonths_i
					- sMonths_i);
		} else {
			return new ResultObj("07", "date series error.");
		}
	}

	/**
	 * 將字串轉換成 Date 格式
	 * 
	 * @param aMask
	 *            可以由dateMask控制輸入格式
	 * @param strDate
	 *            字串日期
	 * @return
	 */
	private Object convtStrToDate(String[] aMask, String strDate) {
		try {
			Date date = DateUtils.parseDate(strDate, aMask);
			return date;
		} catch (ParseException pe) {
			return new ResultObj(ERR_MSG_06[0], ERR_MSG_06[1]);
		}
	}

	/**
	 * 計算算術平均報酬
	 * 
	 * @param ret
	 * @return
	 */
	double[] util_GetMean(double[][] ret) {
		double[] tmpArr = new double[ret.length];
		for (int indexRow_i = 0; indexRow_i < ret.length; indexRow_i++) {
			double tmp_d = 0.0;
			for (int indexCol_i = 0; indexCol_i < ret[indexRow_i].length; indexCol_i++) {
				tmp_d += ret[indexRow_i][indexCol_i];
			}
			tmpArr[indexRow_i] = tmp_d / ret[indexRow_i].length;
		}
		return tmpArr;
	}

	/**
	 * 計算累計幾何平均報酬
	 * 
	 * @param ret
	 * @return
	 */
	private static double[][] util_CumGeoMean(double[][] ret) {
		double[][] result = new double[ret.length][ret[0].length];
		for (int indexRow_i = 0; indexRow_i < ret.length; indexRow_i++) {
			double[] tmp = new double[ret[0].length];
			tmp[0] = 1 + ret[indexRow_i][0];
			result[indexRow_i][0] = ret[indexRow_i][0];
			for (int indexCol_i = 1; indexCol_i < tmp.length; indexCol_i++) {
				tmp[indexCol_i] = tmp[indexCol_i - 1]
						* (1 + ret[indexRow_i][indexCol_i]);
				result[indexRow_i][indexCol_i] = tmp[indexCol_i] - 1;
			}
			tmp = null;
		}
		return result;
	}

	/**
	 * 計算任意兩資產的共變異數
	 * 
	 * @param args
	 */
	private static double util_Covar(int row_i, int col_i, double[][] ret,
			double[] aritAverage) {
		double result_d = 0.0;
		int cols_i = ret[row_i].length;
		for (int index_i = 0; index_i < cols_i; index_i++) {
			result_d += (ret[row_i][index_i] - aritAverage[row_i])
					* (ret[col_i][index_i] - aritAverage[col_i]);
		}
		return result_d / (cols_i - 1);
	}

}
