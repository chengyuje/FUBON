/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM151Controller', 
	function($rootScope, $scope,$controller, $confirm, socketService, ngDialog, projInfoService,sysInfoService) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "CRM151Controller";
	$scope.result = [];
	$scope.IPO = [];
	$scope.PSMTD = [];
	$scope.PSYTD = [];
	$scope.key1 = '';
	$scope.key2 = '';
	
	
	var empList = '';
	$scope.pri = '';
	$scope.pri = String(sysInfoService.getPriID());

	$scope.init = function() {
		$scope.inputVO = {
			Role : ''
		}
	};
	$scope.init();

	$scope.loginrole = function() {
		$scope.sendRecv("CRM151", "login","com.systex.jbranch.app.server.fps.crm151.CRM151InputVO", {},
				function(tota, isError) {
					if (!isError) {
						$scope.inputVO.Role = tota[0].body.login[0].COUNTS;
						$scope.inputVO.priID = tota[0].body.login[0].PRIVILEGEID;
						$scope.query();
					}
				})
	};
	$scope.loginrole();
	// 0-理專、1-主管 、2-輔銷 、3-消金PS、4-PM(未談)
	$scope.query = function() {
		$scope.sendRecv("CRM151", "initial","com.systex.jbranch.app.server.fps.crm151.CRM151InputVO",$scope.inputVO, 
				function(tota, isError) {
					if (!isError) {
						$scope.result = tota[0].body.resultList;
						
//						$scope.IPO = tota[0].body.IPOList;
//						$scope.PSMTD = tota[0].body.MTDList;
//						$scope.PSYTD = tota[0].body.YTDList;
						
						//長條圖
						$scope.nvd();
					}
				});
	};

	//彈跳視窗:近一年達成狀況
	$scope.detail = function(set) {
		//非理專用:近一年未達標理專數
		if($scope.inputVO.Role != '0'){
			empList = JSON.stringify(Object.keys($scope.result[0].MON_NOT_ACH_AO_D));
		}else{
			empList = '';
		}
		var dialog = ngDialog.open({
			template : 'assets/txn/CRM151/CRM151_DETAIL.html',
			className : 'CRM151_DETAIL',
			showClose : false,
			controller : [ '$scope', function($scope) {
				$scope.emp = empList ,
				$scope.set = set;	
			} ]
		});
	};

	//====================長條圖====================//
	
	$scope.nvd = function(){
		//理專、主管用:本月/年度 達成狀況
		if($scope.inputVO.Role == '0' || $scope.inputVO.Role == '1'){
			$scope.options_month = {
				chart : {
					type : 'discreteBarChart',
					margin : {top : 5,right : 0,bottom : 40,left : 0},
					x : function(d) {return d.label},
					y : function(d) {return d.value},
					showValues : true,
					valueFormat : function(d) {return d3.format(',.2f')(d)},
					duration : 500,
					xAxis : {axisLabel : '(萬元)'},
					yAxis : {axisLabel : ''}
				}
			};
			$scope.options_year = {
				chart : {
					type : 'discreteBarChart',
					margin : {top : 5,right : 0,bottom : 40,left : 0},
					x : function(d) {return d.label},
					y : function(d) {return d.value},
					showValues : true,
					valueFormat : function(d) {return d3.format(',.2f')(d)},
					duration : 500,
					xAxis : {axisLabel : '(萬元)'},
					yAxis : {axisLabel : ''}
				}
			};
		}
		//輔銷用:本月 投保/投資 達成狀況
//		if($scope.inputVO.Role == '2'){
//			$scope.options_ins = {
//				chart : {
//					type : 'discreteBarChart',
//					margin : {top : 5,right : 5,bottom : 40,left : 30},
//					x : function(d) {return d.label},
//					y : function(d) {return d.value},
//					showValues : true,
//					valueFormat : function(d) {return d3.format(',.2f')(d);},
//					duration : 500,
//					xAxis : {axisLabel : '(萬元)',width : 10},
//					yAxis : {axisLabel : ''}
//				}
//			};
//			$scope.options_inv = {
//				chart : {
//					type : 'discreteBarChart',
//					margin : {top : 5,right : 5,bottom : 40,left : 30},
//					x : function(d) {return d.label},
//					y : function(d) {return d.value},
//					showValues : true,
//					valueFormat : function(d) {return d3.format(',.2f')(d)},
//					duration : 500,
//					xAxis : {axisLabel : '(萬元)',width : 10},
//					yAxis : {axisLabel : ''}
//				}
//			};
//		}
//		//消金用:本月/年度 達成狀況
//		if($scope.inputVO.Role == '3'){
//			$scope.options_month = {
//				chart : {
//					type : 'discreteBarChart',
//					margin : {top : 5,right : 0,bottom : 40,left : 0},
//					x : function(d) {return d.label},
//					y : function(d) {return d.value},
//					showValues : true,
//					valueFormat : function(d) {return d + '%'},
//					duration : 500,
//					xAxis : {axisLabel : ''},
//					yAxis : {axisLabel : ''}
//				}
//			};
//			$scope.options_year = {
//				chart : {
//					type : 'discreteBarChart',
//					margin : {top : 5,right : 0,bottom : 40,left : 0},
//					x : function(d) {return d.label},
//					y : function(d) {return d.value},
//					showValues : true,
//					valueFormat : function(d) {return d + '%'},
//					duration : 500,
//					xAxis : {axisLabel : ''},
//					yAxis : {axisLabel : ''}
//				}
//			};
//		}		
		//========================長條圖數據========================
		//理專用:本月/年度 達成狀況
		if($scope.result.length == 0){
			$scope.data_month = [ {
				key : "Cumulative Return",
				values : [ 
				           {"label" : "實際收益", "value" : 0},
				           {"label" : "目標收益", "value" : 0}, 
				           {"label" : "同仁平均", "value" : 0} 
				         ]
			} ];
			$scope.data_year = [ {
				key : "Cumulative Return",
				values : [ 
				           {"label" : "實際收益", "value" : 0}, 
				           {"label" : "目標收益", "value" : 0},
				           {"label" : "同仁平均", "value" : 0} 
				         ]
			} ];
		}else{
			if($scope.inputVO.Role == '0'){
				$scope.data_month = [ {
					key : "Cumulative Return",
					values : [ 
					           {"label" : "實際收益", "value" : $scope.result[0].MTD_SUM_FEE/10000 || 0},
					           {"label" : "目標收益", "value" :  $scope.result[0].MTD_SUM_GOAL/10000 || 0}, 
					           {"label" : "同仁平均", "value" :  $scope.result[0].AVG_MTD/10000 || 0} 
					         ]
				} ];
				$scope.data_year = [ {
					key : "Cumulative Return",
					values : [ 
					           {"label" : "實際收益", "value" : $scope.result[0].YTD_SUM_FEE/10000 || 0}, 
					           {"label" : "目標收益", "value" : $scope.result[0].YTD_SUM_GOAL/10000 || 0},
					           {"label" : "同仁平均", "value" : $scope.result[0].AVG_YTD/10000 || 0} 
					         ]
				} ];
			}
			//主管用:本月/年度 達成狀況
			if($scope.inputVO.Role == '1'){
				$scope.data_month = [ {
					key : "Cumulative Return",
					values : [ 
					           {"label" : "實際收益","value" : $scope.result[0].MTD_ALL_FEE/10000 || 0},
					           {"label" : "目標收益","value" :  $scope.result[0].MTD_ALL_FEE_GOAL/10000 || 0},
					           {"label" : "同仁平均", "value" : $scope.result[0].AVG_MTD_ALL_FEE/10000 || 0} 
					         ]
				} ];
				$scope.data_year = [ {
					key : "Cumulative Return",
					values : [ 
					           {"label" : "實際收益","value" : $scope.result[0].YTD_ALL_FEE/10000 || 0}, 
					           {"label" : "目標收益","value" : $scope.result[0].YTD_ALL_FEE_GOAL/10000 || 0},
					           {"label" : "同仁平均", "value" : $scope.result[0].AVG_YTD_ALL_FEE/10000 || 0} 
					         ]
				} ];
			}
			//輔銷用:本月 投保/投資 達成狀況
//			if($scope.inputVO.Role == '2'){
//				$scope.data_ins = [ {
//					key : "Cumulative Return",
//					values : [ {"label" : "實際收益","value" : $scope.result[0].INS_FEE/10000 || 0},
//					           {"label" : "目標收益","value" : $scope.result[0].INS_TAR_AMT/10000 || 0} 
//							   ]
//				} ];
//				$scope.data_inv = [ {
//					key : "Cumulative Return",
//					values : [ 
//					           {"label" : "實際收益","value" : $scope.result[0].INV_FEE/10000 || 0}, 
//					           {"label" : "目標收益","value" : $scope.result[0].INV_TAR_AMT/10000 || 0} 
//					         ]
//				} ];
//			}
			//消金用:本月/年度 達成狀況
//			if($scope.inputVO.Role == '3'){
//				$scope.data_month = [ {
//					key : "Cumulative Return",
//					values : [ 
//					           {"label" : "房貸","value" : $scope.PSMTD[0].RATE1},
//					           {"label" : "信貸","value" : $scope.PSMTD[0].RATE2}, 
//					           {"label" : "好運貸","value" : $scope.PSMTD[0].RATE3}
////					           {"label" : "信用卡","value" : $scope.PSMTD[0].RATE4} 
//					         ]
//				} ];
//				$scope.data_year = [ {
//					key : "Cumulative Return",
//					values : [ 
//								{"label" : "房貸","value" : $scope.PSYTD[0].RATE1},
//								{"label" : "信貸","value" : $scope.PSYTD[0].RATE2}, 
//								{"label" : "好運貸","value" : $scope.PSYTD[0].RATE3}
////								{"label" : "信用卡","value" : $scope.PSYTD[0].RATE4} 
//					         ]
//				} ];
//			}
			
		}
		
	}
	
});