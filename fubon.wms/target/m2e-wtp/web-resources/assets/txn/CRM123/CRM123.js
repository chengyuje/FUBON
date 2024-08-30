/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM123Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM123Controller";
		$scope.dofirst = true;
		//=== CAM.PRD_TYPE(商品類別) filter
		var vo = {'param_type': 'CAM.PRD_TYPE', 'desc': false};
		if(!projInfoService.mappingSet['CAM.PRD_TYPE']) {
	    	$scope.requestComboBox(vo, function(totas) {
	    		if (totas[totas.length - 1].body.result === 'success') {
	    			projInfoService.mappingSet['CAM.PRD_TYPE'] = totas[0].body.result;
	    			$scope.mappingSet['CAM.PRD_TYPE'] = projInfoService.mappingSet['CAM.PRD_TYPE'];
	    		}
	    	});
	    } else {
	    	$scope.mappingSet['CAM.PRD_TYPE'] = projInfoService.mappingSet['CAM.PRD_TYPE'];
	    }
		
		// init
		$scope.calendarView = 'month';
        $scope.viewDate = new Date();
		
        //左側日期點
		$scope.initdetail = function(){
			
			$scope.data = [];
			$scope.resultList = [];
			$scope. inputVO = {
				date : undefined
				
			}
			$scope.inputVO.date =  $scope.viewDate;
			
			$scope.sendRecv("CRM123", "initial", "com.systex.jbranch.app.server.fps.crm123.CRM123InputVO", {'date':$scope.viewDate},
					function(totas, isError) {
						if (isError) {
		            		$scope.showErrorMsg(totas[0].body.msgData);
		            	}
	
						$scope.events = [];
						//募集
						if (totas[0].body.countInvestList.length > 0) {
			           		$scope.countInvestList = totas[0].body.countInvestList;
	                		angular.forEach($scope.countInvestList, function(row, index, objs){
		               			if( $scope.toJsDate(row.BGN_DATE_OF_INVEST) != undefined && $scope.toJsDate(row.END_DATE_OF_INVEST) != undefined){
		               				var sDate = $scope.toJsDate(row.BGN_DATE_OF_INVEST);
		               				var eDate = $scope.toJsDate(row.END_DATE_OF_INVEST);
		                			$scope.events.push({'color': {primary:'#e3bc08',secondary:'#fdf1ba'},'title':row.PRD_NAME,'type':'info','startsAt':sDate,'endsAt':eDate,'NAME':row.PRD_NAME,'TYPE':row.PTYPE});
			               		}
	               			});
	               		};
	               		//休市
						if (totas[0].body.countRestList.length > 0) {
			           		$scope.countRestList = totas[0].body.countRestList;
	                		angular.forEach($scope.countRestList, function(row, index, objs){
		               			if( $scope.toJsDate(row.DATE_OF_REST) != undefined){
		               				var jsDate = $scope.toJsDate(row.DATE_OF_REST);
		                			$scope.events.push({'color': {primary:'#e3bc08',secondary:'#fdf1ba'},'title':row.MKT_NAME,'type':'info','startsAt':jsDate,'endsAt':jsDate,'NAME':row.MKT_NAME,'TYPE':row.PTYPE});
			               		}
	               			});
	               		};
	               		//配息
						if (totas[0].body.countDividendList.length > 0) {
			           		$scope.countDividendList = totas[0].body.countDividendList;
	                		angular.forEach($scope.countDividendList, function(row, index, objs){
		               			if( $scope.toJsDate(row.DIV_DATE) !=undefined){
		               				var jsDate = $scope.toJsDate(row.DIV_DATE);
		                			$scope.events.push({'color': {primary:'#e3bc08',secondary:'#fdf1ba'},'title':row.PRD_NAME,'type':'info','startsAt':jsDate,'endsAt':jsDate,'NAME':row.PRD_NAME,'TYPE':row.PTYPE});
			               		}
	               			});
	               		};
						//到期
						if (totas[0].body.countExpiryList.length > 0) {
			           		$scope.countExpiryList = totas[0].body.countExpiryList;
	                		angular.forEach($scope.countExpiryList, function(row, index, objs){
		               			if( $scope.toJsDate(row.DATE_OF_MATURITY) !=undefined){
		               				var jsDate = $scope.toJsDate(row.DATE_OF_MATURITY);
		                			$scope.events.push({'color': {primary:'#09cbe5',secondary:'#fdf1ba'},'title':row.PRD_NAME,'type':'info','startsAt':jsDate,'endsAt':jsDate,'NAME':row.PRD_NAME,'TYPE':row.PTYPE});
			               		}
	               			});
	               		};
	               		$scope.dateClicked($scope.viewDate);
			});
		};
		
        

        // change view
        $scope.changeView = function(data) {
        	$scope.calendarView = data;
        };
        
        // click
        $scope.dateClicked = function(date) {
        	$scope.resultListInvest = [];
        	$scope.outputVOInvest = [];
        	$scope.resultListRest = []; 
        	$scope.outputVORest = []; 	
        	$scope.resultListDividend = []; 	
        	$scope.outputVODividend = []; 	
        	$scope.resultListExpiry = []; 	
        	$scope.outputVOExpiry = []; 	
        	$scope.inquire(date);       	
        };
     
		//募集
		$scope.showInvest = function(){
			$scope.sendRecv("CRM123", "showInvest", "com.systex.jbranch.app.server.fps.crm123.CRM123InputVO", $scope.inputVO,
					function(totas, isError) {
						if (isError) {
		            		$scope.showErrorMsg(totas[0].body.msgData);
		            	}
						if (totas[0].body.resultList.length > 0) {
			           		$scope.resultListInvest = totas[0].body.resultList;
			           		$scope.outputVOInvest = totas[0].body;
						}
			});
		}
		//休市
		$scope.showRest = function(){
			$scope.sendRecv("CRM123", "showRest", "com.systex.jbranch.app.server.fps.crm123.CRM123InputVO", $scope.inputVO,
					function(totas, isError) {
						if (isError) {
		            		$scope.showErrorMsg(totas[0].body.msgData);
		            	}
						if (totas[0].body.resultList.length > 0) {
			           		$scope.resultListRest = totas[0].body.resultList;
			           		$scope.outputVORest = totas[0].body;
						}
			});
		}
		//配息
		$scope.showDividend = function(){
			$scope.sendRecv("CRM123", "showDividend", "com.systex.jbranch.app.server.fps.crm123.CRM123InputVO", $scope.inputVO,
					function(totas, isError) {
						if (isError) {
		            		$scope.showErrorMsg(totas[0].body.msgData);
		            	}
						if (totas[0].body.resultList.length > 0) {
			           		$scope.resultListDividend = totas[0].body.resultList;
	                		$scope.outputVODividend = totas[0].body;
						}
			});
		}
		//到期
		$scope.showexpiry = function(){
			$scope.sendRecv("CRM123", "showexpiry", "com.systex.jbranch.app.server.fps.crm123.CRM123InputVO", $scope.inputVO,
					function(totas, isError) {
						if (isError) {
		            		$scope.showErrorMsg(totas[0].body.msgData);
		            	}
						if (totas[0].body.resultList.length > 0) {
			           		$scope.resultListExpiry = totas[0].body.resultList;
	                		$scope.outputVOExpiry = totas[0].body;
						}
			});
		}
		
		//主查詢
		$scope.inquire = function (date){
			$scope.inputVO.date = date;
			$scope.resultList = [];
			
			$scope.showInvest();
			$scope.showRest();
			$scope.showDividend();
			$scope.showexpiry();
		}
		
		//跨頁重整方法
        $scope.$on("CRM123.init", function(event) {
        	if($scope.dofirst){
        		$scope.initdetail();
        		$scope.dofirst = false;
        	}
		});
		
});
