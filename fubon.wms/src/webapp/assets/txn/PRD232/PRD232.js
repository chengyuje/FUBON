/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD232Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD232Controller";

		// combobox
		getParameter.XML(["COMMON.YES_NO", "FPS.CURRENCY", "PRD.MKT_TIER3"], function(totas) {
			if (totas) {
				$scope.YES_NO = totas.data[totas.key.indexOf('COMMON.YES_NO')];
				$scope.CURRENCY = totas.data[totas.key.indexOf('FPS.CURRENCY')];
				$scope.MKT_TIER3 = totas.data[totas.key.indexOf('PRD.MKT_TIER3')];
			}
		});
		// 整批更新欄位  {LABEL: '建議售出', DATA: '1'}
		$scope.mappingSet['edit_col'] = [
			{LABEL: 'Tier Level', DATA: '2'},
			{LABEL: '轉換手續費%', DATA: '3'},
			{LABEL: '轉換手續費(本行)%', DATA: '4'},
			// {LABEL: '獎勵金%', DATA: '5'},
			// {LABEL: '定期定額銷售獎勵金', DATA: '6'},
			{LABEL: '教育訓練獎勵金(萬)', DATA: '7'}
//			,{LABEL: '其他報酬', DATA: '8'}
			,{LABEL: '其他報酬_投信', DATA: '9'},
			{LABEL: '其他報酬_總代理人', DATA: '10'},
			{LABEL: '其他報酬_境外基金結構', DATA: '11'},
			{LABEL: '12B-1 FEE', DATA: '12'},
			{LABEL: '其他經銷費', DATA: '13'}];

		
		// init
		$scope.init = function() {
			$scope.inputVO = {};
			$scope.checkVO = {};
			$scope.conDis = false;
		};
		$scope.init();
		$scope.inquireInit = function() {
			$scope.paramList = [];
			$scope.outputVO = {};
		}
		$scope.inquireInit();
		
		$scope.setMAX = function() {
			$scope.inputVO.col = '';
		};
		
		// inquire
		$scope.currUser = projInfoService.getUserID();
		$scope.inquire = function(){
			// toUpperCase
			if($scope.inputVO.prd_id)
				$scope.inputVO.prd_id = $scope.inputVO.prd_id.toUpperCase();
			$scope.sendRecv("PRD232", "inquire", "com.systex.jbranch.app.server.fps.prd232.PRD232InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.clickAll = false;
							$scope.clickAll2 = false;
							if(tota[0].body.resultList.length == 0) {
								$scope.rowCount="0";
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
//							console.log("paramList", $scope.paramList);
							$scope.rowCount=tota[0].body.rowCount;
							$scope.outputVO = tota[0].body;
							$scope.conDis = false;
							$scope.inputVO.passParams='';
							$scope.connector('set','MAO151_PARAMS',undefined);
							angular.forEach($scope.paramList, function(row, index, objs) {
								if(row.REVIEW_STATUS == 'W' && row.CREATOR != $scope.currUser)
									$scope.conDis = true;
							});
							return;
						}
			});
		};
		
		
		/*****首頁登入判斷覆核查詢*****/   
		if($scope.connector('get','MAO151_PARAMS')!=undefined){
			//擷取HOME參數
			$scope.inputVO.passParams=$scope.connector('get','MAO151_PARAMS').PAGE;
			//查詢首頁近來覆核人員  為Home才會查詢
			if($scope.inputVO.passParams=='FUNDBONUSINFO')
				$scope.inquire();
		}
		
		// click all
		// 2017/7/24 非覆核或覆核中自己
		$scope.checkrow = function() {
			if ($scope.checkVO.clickAll) {
        		angular.forEach($scope.paramList, function(row) {
        			if(row.REVIEW_STATUS != 'W' || (row.REVIEW_STATUS == 'W' && row.CREATOR == $scope.currUser))
        				row.CHECK = true;
    			});
        	} else {
        		angular.forEach($scope.paramList, function(row) {
        			if(row.REVIEW_STATUS != 'W' || (row.REVIEW_STATUS == 'W' && row.CREATOR == $scope.currUser))
        				row.CHECK = false;
    			});
        	}
        };
        $scope.checkrow2 = function() {
        	if ($scope.checkVO.clickAll2) {
        		angular.forEach($scope.paramList, function(row) {
        			if(row.REVIEW_STATUS == 'W' && row.CREATOR != $scope.currUser)
        				row.SELECTED = true;
    			});
        	} else {
        		angular.forEach($scope.paramList, function(row){
        			if(row.REVIEW_STATUS == 'W' && row.CREATOR != $scope.currUser)
        				row.SELECTED = false;
    			});
        	}
        };
        
		$scope.save = function() {
			debugger;
			var ans = $scope.paramList.filter(function(obj){
	    		return (obj.CHECK == true);
	    	}).map(function(e) { return e.PRD_ID; });
			if(!$scope.inputVO.edit_col || !$scope.inputVO.col || ans.length == 0) {
				$scope.showErrorMsg('欄位檢核錯誤:無選擇或輸入欲更新欄位');
				return;
			}
			$scope.inputVO.id_map = ans;
			$scope.sendRecv("PRD232", "editData", "com.systex.jbranch.app.server.fps.prd232.PRD232InputVO", $scope.inputVO,
					function(tota, isError) {
						if (isError) {
	                		$scope.showErrorMsg(tota[0].body.msgData);
	                	}
	                	if (tota.length > 0) {
	                		$scope.showSuccessMsg('ehl_01_common_004');
	                		$scope.inquireInit();
	    					$scope.inquire();
	                	};
			});
		};
		//
		
		$scope.upload = function() {
			debugger;
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD232/PRD232_UPLOAD.html',
				className: 'PRD232',
				showClose: false,
                controller: ['$scope', function($scope) {
                	
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					$scope.inquireInit();
					$scope.inquire();
				}
			});
		};
		
		$scope.download = function() {
			$scope.sendRecv("PRD232", "download", "com.systex.jbranch.app.server.fps.prd232.PRD232InputVO", $scope.inputVO,
					function(tota, isError) {
							if(!isError){
								$scope.showMsg("下載成功");
								return;
							}else{
								$scope.showErrorMsg("下載失敗");
							}
			});
		};
		
		
		$scope.review = function (status) {
			// get select
			var ans = $scope.paramList.filter(function(obj) {
	    		return (obj.SELECTED == true);
	    	});
        	if(ans.length == 0){
        		return;
        	}
			$confirm({text: '是否' + ((status == 'N') ? '退回' : '核可')}, {size: 'sm'}).then(function() {
				$scope.sendRecv("PRD232", "review", "com.systex.jbranch.app.server.fps.prd232.PRD232InputVO", {'review_list': ans,'status': status},
						function(tota, isError) {
							if (isError) {
		                		$scope.showErrorMsg(tota[0].body.msgData);
		                	}
		                	if (tota.length > 0) {
		                		$scope.showMsg((status == 'N') ? "ehl_01_common_020" : "ehl_01_common_021");
		                	};
		                	$scope.inquireInit();
							$scope.inquire();
				});
			});
		};
		
		
});