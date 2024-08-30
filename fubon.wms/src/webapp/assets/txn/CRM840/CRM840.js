/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM840Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService , getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM840Controller";
		$scope.login = String(sysInfoService.getPriID());
		$scope.activeJustified = $scope.connector("get","CRM840_tab");
		
		$scope.inputVO = {};
		$scope.inputVO.cust_id = $scope.custVO.CUST_ID;

		
		//===================================CRM841===================================
		$scope.CRM841_inquire = function() {
			debugger;
			$scope.resultList841 = [];
			console.log(1);
			$scope.sendRecv("CRM841", "inquire", "com.systex.jbranch.app.server.fps.crm841.CRM841InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
//								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							
							$scope.resultList841 = tota[0].body.resultList;
							$scope.outputVO841 = tota[0].body;
							
							//分期型房貸
							$scope.resulttest = [];
							angular.forEach($scope.resultList841, function(row) {
								$scope.resulttest.push({LABEL: '分期型房貸', DATA: row.LOAN_TYP});
							});
							
							for(var i = 0; i < $scope.resultList841.length; i++) {								
								var dur = 0 , yy = 0 , mm = 0 ;
								var strY = '' , strM = '' , strD = '';
								//寬限期
								if($scope.resultList841[i].PENL_STR_DATE.length == 8){
									/**到期日 = (起算年+寬限期年) + (起算月+寬限期月) + 起算日**/			
									strY = Number($scope.resultList841[i].PENL_STR_DATE.substring(0, 4));
									strM = Number($scope.resultList841[i].PENL_STR_DATE.substring(4, 6));
									strD = $scope.resultList841[i].PENL_STR_DATE.substring(6, 8);
									dur = Number($scope.resultList841[i].PRN_ALW_DUR);
									yy = strY + Math.floor(dur / 12);
									mm = strM + dur - (Math.floor(dur / 12) * 12) ;
									//大於12進位
									if(mm > 12){
										mm = mm - 12 ;
										yy = yy + 1 ;
									}
									//小於10補0
									if(mm < 10){	
										$scope.resultList841[i].PENL_STR_DATE =  yy.toString() + "/0" + mm.toString() + "/" + strD;
									}else{
										$scope.resultList841[i].PENL_STR_DATE =  yy.toString() + "/" + mm.toString() + "/" + strD;
									}	
								}
							}			
							debugger;
							return;
						}
			});		
	    };
	    
		//============================================================================
		//===================================CRM842===================================
	    
	    $scope.CRM842_inquire = function(){
	    	debugger;
	    	$scope.resultList842 = [];
	    	$scope.resultList842_1 = [];
	    	$scope.resultList842_2 = [];
	    	$scope.resultList842_3 = [];
	    	$scope.resultList842_4 = [];
	    	$scope.resultList842_5 = [];
	    	console.log(2);
			$scope.sendRecv("CRM842", "inquire", "com.systex.jbranch.app.server.fps.crm842.CRM842InputVO", $scope.inputVO,
					function(tota, isError) {
						if (isError) {
							$scope.showErrorMsg(totas[0].body.msgData);
						}
						
						//循環型信貸 循環型房貸(額度式)
						if(tota[0].body.resultList != null && tota[0].body.resultList.length > 0){
							$scope.resultList842 = tota[0].body.resultList;
						}					
						$scope.mappingSet['resultList'] = [];
						//循環型信貸 循環型房貸(回復式)
						if(tota[0].body.resultList1 != null && tota[0].body.resultList1.length > 0){
							$scope.resultList842_1 = tota[0].body.resultList1;
							
						}					
						$scope.mappingSet['resultList1'] = [];
						//綜存質借(台幣)
						if(tota[0].body.resultList2 != null && tota[0].body.resultList2.length > 0){
							$scope.resultList842_2 = tota[0].body.resultList2;
						}
						$scope.mappingSet['resultList2'] = [];
						//綜存質借(非台幣　一本萬利外幣)
						if(tota[0].body.resultList3 != null && tota[0].body.resultList3.length > 0){
							$scope.resultList842_3 = tota[0].body.resultList3;
						}
						$scope.mappingSet['resultList3'] = [];
						//綜存質借(非台幣　一般外幣外幣)
						if(tota[0].body.resultList4 != null && tota[0].body.resultList4.length > 0){
							$scope.resultList842_4 = tota[0].body.resultList4;
						}
						$scope.mappingSet['resultList4'] = [];
						//信託質借(循環型)
						if(tota[0].body.resultList5 != null && tota[0].body.resultList5.length > 0){
							$scope.resultList842_5 = tota[0].body.resultList5;
						}
						$scope.mappingSet['resultList5'] = [];
						
						angular.forEach($scope.resultList842, function(row, index, objs){
							$scope.mappingSet['resultList'].push({LABEL: row.TYPE, DATA: '循環型房貸(額度式)'});
						});
						angular.forEach($scope.resultList842_1, function(row, index, objs){
							$scope.mappingSet['resultList1'].push({LABEL: row.TYPE, DATA: '循環型房貸(回復式)'});
						});
						angular.forEach($scope.resultList842_2, function(row, index, objs){
							$scope.mappingSet['resultList2'].push({LABEL: row.LOAN_TYP, DATA: '綜存質借(台幣)'});
						});
						angular.forEach($scope.resultList842_3, function(row, index, objs){
							$scope.mappingSet['resultList3'].push({LABEL: row.LOAN_TYP, DATA: '綜存質借(一本萬利外幣)'});
						});
						angular.forEach($scope.resultList842_4, function(row, index, objs){
							$scope.mappingSet['resultList4'].push({LABEL: row.LOAN_TYP, DATA: '綜存質借(一般外幣)'});
						});
						angular.forEach($scope.resultList842_5, function(row, index, objs){
							$scope.mappingSet['resultList5'].push({LABEL: row.LOAN_TYP, DATA: '信託質借(台幣)'});
						});
						
						//循環型信貸 循環型房貸(額度式)					
						for(var i = 0; i < $scope.resultList842.length; i++) {								
							var dur = 0 , yy = 0 , mm = 0 ;
							var strY = '' , strM = '' , strD = '';
							
							//到期日
							if($scope.resultList842[i].DUE_DATE.length == 8){
								strY = $scope.resultList842[i].DUE_DATE.substring(0, 4);
								strM = $scope.resultList842[i].DUE_DATE.substring(4, 6);
								strD = $scope.resultList842[i].DUE_DATE.substring(6, 8);							
								$scope.resultList842[i].DUE_DATE =  strY + "/" + strM + "/" + strD;							
							}
						}
						//循環型信貸 循環型房貸(回復式)
						for(var i = 0; i < $scope.resultList842_1.length; i++) {								
							var dur = 0 , yy = 0 , mm = 0 ;
							var strY = '' , strM = '' , strD = '';
							
							//到期日
							if($scope.resultList842_1[i].DUE_DATE.length == 8){
								strY = $scope.resultList842_1[i].DUE_DATE.substring(0, 4);
								strM = $scope.resultList842_1[i].DUE_DATE.substring(4, 6);
								strD = $scope.resultList842_1[i].DUE_DATE.substring(6, 8);							
								$scope.resultList842_1[i].DUE_DATE =  strY + "/" + strM + "/" + strD;							
							}
						}
						//綜存質借(台幣)
						for(var i = 0; i < $scope.resultList842_2.length; i++) {								
							var dur = 0 , yy = 0 , mm = 0 ;
							var strY = '' , strM = '' , strD = '';
							
							//到期日
							if($scope.resultList842_2[i].DUE_DATE.length == 8){
								strY = $scope.resultList842_2[i].DUE_DATE.substring(0, 4);
								strM = $scope.resultList842_2[i].DUE_DATE.substring(4, 6);
								strD = $scope.resultList842_2[i].DUE_DATE.substring(6, 8);							
								$scope.resultList842_2[i].DUE_DATE =  strY + "/" + strM + "/" + strD;							
							}
						}
						//綜存質借(非台幣　一本萬利外幣)
						for(var i = 0; i < $scope.resultList842_3.length; i++) {								
							var dur = 0 , yy = 0 , mm = 0 ;
							var strY = '' , strM = '' , strD = '';
							
							//到期日
							if($scope.resultList842_3[i].DUE_DATE.length == 8){
								strY = $scope.resultList842_3[i].DUE_DATE.substring(0, 4);
								strM = $scope.resultList842_3[i].DUE_DATE.substring(4, 6);
								strD = $scope.resultList842_3[i].DUE_DATE.substring(6, 8);							
								$scope.resultList842_3[i].DUE_DATE =  strY + "/" + strM + "/" + strD;							
							}
						}
						//綜存質借(非台幣　一般外幣外幣)
						for(var i = 0; i < $scope.resultList842_4.length; i++) {								
							var dur = 0 , yy = 0 , mm = 0 ;
							var strY = '' , strM = '' , strD = '';
							
							//到期日
							if($scope.resultList842_4[i].DUE_DATE.length == 8){
								strY = $scope.resultList842_4[i].DUE_DATE.substring(0, 4);
								strM = $scope.resultList842_4[i].DUE_DATE.substring(4, 6);
								strD = $scope.resultList842_4[i].DUE_DATE.substring(6, 8);							
								$scope.resultList842_4[i].DUE_DATE =  strY + "/" + strM + "/" + strD;							
							}
						}
						//信託質借(循環型)
						for(var i = 0; i < $scope.resultList842_5.length; i++) {								
							var dur = 0 , yy = 0 , mm = 0 ;
							var strY = '' , strM = '' , strD = '';
							
							//到期日
							if($scope.resultList842_5[i].DUE_DATE.length == 8){
								strY = $scope.resultList842_5[i].DUE_DATE.substring(0, 4);
								strM = $scope.resultList842_5[i].DUE_DATE.substring(4, 6);
								strD = $scope.resultList842_5[i].DUE_DATE.substring(6, 8);							
								$scope.resultList842_5[i].DUE_DATE =  strY + "/" + strM + "/" + strD;							
							}
						}
						
						$scope.outputVO842 = tota[0].body;
						debugger;
						return;
				}		
		)};
			
		//============================================================================
		//===================================CRM843===================================
		
		$scope.CRM843_inquire = function(){
			debugger;
			$scope.resultList843 = [];
			console.log(3);
			$scope.sendRecv("CRM843", "inquire", "com.systex.jbranch.app.server.fps.crm843.CRM843InputVO", $scope.inputVO,
					function(tota, isError) {
						if (isError) {
							$scope.showErrorMsg(totas[0].body.msgData);
						}
						if(tota[0].body.resultList.length == 0) {
//							$scope.showMsg("ehl_01_common_009");
	                		return;
						}
						$scope.resultList843 = tota[0].body.resultList;
						$scope.outputVO843 = tota[0].body;					
						
						//信用貸款
						$scope.resulttest = [];
						angular.forEach($scope.resultList, function(row) {
							$scope.resulttest.push({LABEL: '信用貸款', DATA: row.LOAN_TYP});
						});
					
						for(var i = 0; i < $scope.resultList843.length; i++) {								
							var dur = 0 , yy = 0 , mm = 0 ;
							var strY = '' , strM = '' , strD = '';
							//寬限期
							if($scope.resultList843[i].PENL_STR_DATE.length == 8){
								/**到期日 = (起算年+寬限期年) + (起算月+寬限期月) + 起算日**/			
								strY = Number($scope.resultList843[i].PENL_STR_DATE.substring(0, 4));
								strM = Number($scope.resultList843[i].PENL_STR_DATE.substring(4, 6));
								strD = $scope.resultList843[i].PENL_STR_DATE.substring(6, 8);
								dur = Number($scope.resultList843[i].PRN_ALW_DUR);
								yy = strY + Math.floor(dur / 12);
								mm = strM + dur - (Math.floor(dur / 12) * 12) ;
								//大於12進位
								if(mm > 12){
									mm = mm - 12 ;
									yy = yy + 1 ;
								}
								//小於10補0
								if(mm < 10){	
									$scope.resultList843[i].PENL_STR_DATE =  yy.toString() + "/0" + mm.toString() + "/" + strD;
								}else{
									$scope.resultList843[i].PENL_STR_DATE =  yy.toString() + "/" + mm.toString() + "/" + strD;
								}	
							}
						}
						debugger;
						return;
				}
		)};
			
		//============================================================================
		//===================================CRM844===================================
			
		$scope.CRM844_inquire = function(){
			$scope.resultList844 = [];
			console.log(4);
			$scope.sendRecv("CRM844", "inquire", "com.systex.jbranch.app.server.fps.crm844.CRM844InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
		               			return;
		               		}
							$scope.resultList844 = tota[0].body.resultList;
							$scope.outputVO844 = tota[0].body;
							debugger;
							//就學貸款
							$scope.resulttest = [];
							angular.forEach($scope.resultList844, function(row) {
								$scope.resulttest.push({LABEL: '就學貸款', DATA: row.LOAN_TYP});
							});
							
							for(var i = 0; i < $scope.resultList844.length; i++) {								
								var dur = 0 , yy = 0 , mm = 0 ;
								var strY = '' , strM = '' , strD = '';
								var TYPE = '';
								//#0002377 : 學期別
								if($scope.resultList844[i].DOC_NO.length >= 4){
									debugger;
									/**前3碼為學期年，後1碼 1:上學期、2:下學期**/
									/** 20200303新增 0:學期**/
									TYPE = $scope.resultList844[i].DOC_NO.substring(3, 4);
									$scope.resultList844[i].DOC_NO = $scope.resultList844[i].DOC_NO.substring(0, 3);
									if(TYPE == '1'){
										$scope.resultList844[i].DOC_TYPE = '上學期';
									}else if(TYPE == '2'){
										$scope.resultList844[i].DOC_TYPE = '下學期';
									}else if(TYPE == '0'){
										$scope.resultList844[i].DOC_TYPE = '學期';
									}
										
									
								}
								
								//寬限期
								if($scope.resultList844[i].PENL_STR_DATE.length == 8){
									/**到期日 = (起算年+寬限期年) + (起算月+寬限期月) + 起算日**/			
									strY = Number($scope.resultList844[i].PENL_STR_DATE.substring(0, 4));
									strM = Number($scope.resultList844[i].PENL_STR_DATE.substring(4, 6));
									strD = $scope.resultList844[i].PENL_STR_DATE.substring(6, 8);
									dur = Number($scope.resultList844[i].PRN_ALW_DUR);
									yy = strY + Math.floor(dur / 12);
									mm = strM + dur - (Math.floor(dur / 12) * 12) ;
									//大於12進位
									if(mm > 12){
										mm = mm - 12 ;
										yy = yy + 1 ;
									}
									//小於10補0
									if(mm < 10){	
										$scope.resultList844[i].PENL_STR_DATE =  yy.toString() + "/0" + mm.toString() + "/" + strD;
									}else{
										$scope.resultList844[i].PENL_STR_DATE =  yy.toString() + "/" + mm.toString() + "/" + strD;
									}	
								}
								//綁約到期日
								//好像用不到暫時隱蔽 2019.08.27 SamTu
//								if($scope.resultList844[i].PENL_TERMS.length == 8){
//									strY = $scope.resultList844[i].PENL_TERMS.substring(0, 4);
//									strM = $scope.resultList844[i].PENL_TERMS.substring(4, 6);
//									strD = $scope.resultList844[i].PENL_TERMS.substring(6, 8);							
//									$scope.resultList844[i].PENL_TERMS =  strY + "/" + strM + "/" + strD;							
//								}
							}
							
							return;
						}
				});
		};
		
		//============================================================================
		//===================================CRM845===================================
		
		$scope.CRM845_inquire = function(){
			$scope.resultList845 = [];
			console.log(5);
			$scope.sendRecv("CRM845", "inquire", "com.systex.jbranch.app.server.fps.crm845.CRM845InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
	                			return;
	                		}
							$scope.resultList845 = tota[0].body.resultList;
							$scope.outputVO845 = tota[0].body;
							
							//留學貸款
							$scope.resulttest = [];
							angular.forEach($scope.resultList845, function(row) {
								$scope.resulttest.push({LABEL: '留學貸款', DATA: row.LOAN_TYP});
							});	
							return;
						}
			});
	    };
	    
		//============================================================================
		//===================================CRM846===================================
	    
		//xml參數初始化
		getParameter.XML(["CRM.CREDIT_CARD_REVTYP", "CRM.CREDIT_CARD_ACCSTS", "CRM.CREDIT_CARD_TYPE2","CRM.CREDIT_CARD_TYPE","CRM.CREDIT_CARD_STATUS"], function(totas) {
			if (totas) {
				$scope.mappingSet['CRM.CREDIT_CARD_REVTYP'] = totas.data[totas.key.indexOf('CRM.CREDIT_CARD_REVTYP')];
				$scope.mappingSet['CRM.CREDIT_CARD_ACCSTS'] = totas.data[totas.key.indexOf('CRM.CREDIT_CARD_ACCSTS')];
				$scope.mappingSet['CRM.CREDIT_CARD_TYPE2'] = totas.data[totas.key.indexOf('CRM.CREDIT_CARD_TYPE2')];
				$scope.mappingSet['CRM.CREDIT_CARD_TYPE'] = totas.data[totas.key.indexOf('CRM.CREDIT_CARD_TYPE')];
				$scope.mappingSet['CRM.CREDIT_CARD_STATUS'] = totas.data[totas.key.indexOf('CRM.CREDIT_CARD_STATUS')];
			}
		});		
		//查詢		
		$scope.CRM846_inquire = function(){	
			console.log(6);
			//信用卡帳務資料
			$scope.sendRecv("CRM846", "initial", "com.systex.jbranch.app.server.fps.crm846.CRM846InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList == null || tota[0].body.resultList.length == 0) {
	//							$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.dataList = tota[0].body.resultList;
							return;
						}
			});		
			//信用卡清單
			$scope.sendRecv("CRM846", "inquire", "com.systex.jbranch.app.server.fps.crm846.CRM846InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							
							$scope.mappingSet['CRM.CREDIT_CARD_NAME'] = tota[0].body.resultList3;

							if(tota[0].body.resultList == null || tota[0].body.resultList.length == 0) {
	//							$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.dataList2 = tota[0].body.resultList;
							
							if(tota[0].body.resultList2 == null || tota[0].body.resultList2.length == 0) {
								return;
	                		}
							$scope.dataList2_data = tota[0].body.resultList2;
							
							var card_bousav = 0;
//							CCM002的下行電文中CARD_TBBNCD=0000及9000的CARD_BOUSAV要加總起來顯示(#4247)
							angular.forEach(tota[0].body.resultList2, function(row) {
								if(row.CARD_TBBNCD == '0000' || row.CARD_TBBNCD == '9000')
									card_bousav += parseInt(row.CARD_BOUSAV);
							});
							
//							$scope.dataList[0].CARD_BOUSAV = $scope.dataList2_data[0].CARD_BOUSAV;
							$scope.dataList[0].CARD_BOUSAV = card_bousav;
							
							
							
							return;
						}
			});		
		};
		
		//轉STR YYYYMMDD TO DATE
		$scope.transDate = function(row) {
			if (isNaN(row))
				return row;
			if (row == '00000000')
				return '';
			var pattern = /(\d{4})(\d{2})(\d{2})/;
			return new Date(row.replace(pattern, '$1-$2-$3'));
		}
		$scope.$on("CRM846.inquire", function(event) {
			$rootScope.CRM846_inquire();
		});	
		
		//============================================================================
		//===================================CRM847===================================
		$scope.CRM847_inquire = function(){
			$scope.resultList847 = [];
			console.log(7);
			$scope.sendRecv("CRM847", "inquire", "com.systex.jbranch.app.server.fps.crm847.CRM847InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.outputVO847 = tota[0].body;
							
							if(tota[0].body.resultList != null && tota[0].body.resultList.length > 0) {
								$scope.resultList847 = tota[0].body.resultList;								
								
								//存單質借
								$scope.resulttest = [];
								angular.forEach($scope.resultList847, function(row) {
									$scope.resulttest.push({LABEL: '存單質借', DATA: row.LOAN_TYP});
								});
								
								for(var i = 0; i < $scope.resultList847.length; i++) {								
									var dur = 0 , yy = 0 , mm = 0 ;
									var strY = '' , strM = '' , strD = '';
									
									//到期日
									if($scope.resultList847[i].DUE_DATE.length == 8){
										strY = $scope.resultList847[i].DUE_DATE.substring(0, 4);
										strM = $scope.resultList847[i].DUE_DATE.substring(4, 6);
										strD = $scope.resultList847[i].DUE_DATE.substring(6, 8);							
										$scope.resultList847[i].DUE_DATE =  strY + "/" + strM + "/" + strD;							
									}
								}
	                		}
							
							if(tota[0].body.resultList1 != null && tota[0].body.resultList1.length > 0) {
								$scope.resultList847_1 = tota[0].body.resultList1;
								
								//存單質借
								$scope.resulttest1 = [];
								angular.forEach($scope.resultList847_1, function(row) {
									$scope.resulttest1.push({LABEL: '信託質借', DATA: row.LOAN_TYP});
								});
								
								for(var i = 0; i < $scope.resultList847_1.length; i++) {								
									var dur = 0 , yy = 0 , mm = 0 ;
									var strY = '' , strM = '' , strD = '';
									
									//到期日
									if($scope.resultList847_1[i].DUE_DATE.length == 8){
										strY = $scope.resultList847_1[i].DUE_DATE.substring(0, 4);
										strM = $scope.resultList847_1[i].DUE_DATE.substring(4, 6);
										strD = $scope.resultList847_1[i].DUE_DATE.substring(6, 8);							
										$scope.resultList847_1[i].DUE_DATE =  strY + "/" + strM + "/" + strD;							
									}
								}
	                		}
							
							return;
						}
			});
	    };
		//============================================================================
});
		