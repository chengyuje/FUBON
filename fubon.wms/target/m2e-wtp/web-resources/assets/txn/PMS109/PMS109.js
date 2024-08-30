'use strict';
eSoafApp.controller('PMS109Controller', function(sysInfoService, $rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $interval, $confirm, $filter, getParameter) {
	
	$scope.controllerName = "PMS109Controller";	
	
	$controller('BaseController', {$scope : $scope});
	$controller('CRM210Controller', {$scope: $scope});
    
	//filter
	getParameter.XML(["PMS.SALE_PLAN_PTYPE", "PMS.SALE_PLAN_SRC"], function(totas) {
		if (totas) {				
			$scope.mappingSet['PMS.SALE_PLAN_PTYPE'] = totas.data[totas.key.indexOf('PMS.SALE_PLAN_PTYPE')];
			$scope.mappingSet['PMS.SALE_PLAN_SRC'] = totas.data[totas.key.indexOf('PMS.SALE_PLAN_SRC')];
		}
	});
	
	// 計算 預計承作商品-預計承作金額-預估收益率
	$scope.getRate = function (type) {
		switch (type) {
			case "EST_PRD":
				if ($scope.inputVO.EST_PRD != null || $scope.inputVO.EST_PRD != '' || $scope.inputVO.EST_PRD != undefined) {
					angular.forEach($scope.estPrdList, function(row) {
						if (row.EST_PRD == $scope.inputVO.EST_PRD) {
							$scope.inputVO.EST_EARNINGS_RATE = row.EST_RETURN_RATE;
						}
	    			});
					$scope.inputVO.EST_EARNINGS = Math.floor($scope.inputVO.EST_AMT * ($scope.inputVO.EST_EARNINGS_RATE / 100));
				}
				
				break;
			case "EST_AMT":
				if ($scope.inputVO.EST_PRD != null || $scope.inputVO.EST_PRD != '' || $scope.inputVO.EST_PRD != undefined) {
					$scope.inputVO.EST_EARNINGS = Math.floor($scope.inputVO.EST_AMT * ($scope.inputVO.EST_EARNINGS_RATE / 100));
				} else {
					$scope.inputVO.EST_EARNINGS = 0;
				}
				
				break;
			case "EST_EARNINGS_RATE":
				if ($scope.inputVO.EST_PRD != null || $scope.inputVO.EST_PRD != '' || $scope.inputVO.EST_PRD != undefined) {
					$scope.inputVO.EST_EARNINGS = Math.floor($scope.inputVO.EST_AMT * ($scope.inputVO.EST_EARNINGS_RATE / 100));
				} else {
					$scope.inputVO.EST_EARNINGS = 0;
				}
				
				break;
		}
	}
	
	/** ppap找 case_vo這段, 專門給cam200使用 *****/
	function PPAPgetAoCode() {
        var deferred = $q.defer();

		$scope.sendRecv("PMS109", "getAoCode", "com.systex.jbranch.app.server.fps.pms109.PMS109InputVO", {'PMSROW': $scope.cust_id}, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.resultList.length == 0 || !tota[0].body.resultList[0].AO_CODE){
					deferred.reject();							
				} else {
					$scope.inputVO.AO_CODE = tota[0].body.resultList[0].AO_CODE;
					$scope.cust_aoName = tota[0].body.resultList[0].EMP_NAME;
					$scope.cust_ao = tota[0].body.resultList[0].AO_CODE;
					
					deferred.resolve("success");
				}
			}
		});

        return deferred.promise;
	};
	
	/** 初始 **/
	$scope.init = function() {
		$scope.estPrdList = [];
		// 取得 預計承作商品-預估收益率
		$scope.sendRecv("PMS501", "query", "com.systex.jbranch.app.server.fps.pms501.PMS501InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				$scope.estPrdList = tota[0].body.resultList;
			}
		});
		
		if ($scope.mode == 'upd') {
			$scope.inputVO = {
				SEQ                 : $scope.salePlanRow.SEQ,         						// 銷售計劃序號
				PLAN_YEARMON        : $scope.salePlanRow.PLAN_YEARMON,						// 銷售計劃月份
				BRANCH_NBR          : $scope.salePlanRow.BRANCH_NBR,    					// 分行代碼
				AO_CODE             : $scope.salePlanRow.AO_CODE,    						// AO CODE
				EMP_ID              : $scope.salePlanRow.EMP_ID,    						// 員工編號
				EMP_NAME            : $scope.salePlanRow.EMP_NAME,    						// 員工姓名
				CUST_ID             : $scope.salePlanRow.CUST_ID,    						// 客戶ID
				CUST_NAME           : $scope.salePlanRow.CUST_NAME,    						// 客戶姓名
				SRC_TYPE            : $scope.salePlanRow.SRC_TYPE,    						// 來源種類
				EST_PRD             : $scope.salePlanRow.EST_PRD,    						// 預計承作商品
				EST_AMT             : $scope.salePlanRow.EST_AMT,    						// 預計承作金額
				EST_EARNINGS_RATE   : $scope.salePlanRow.EST_EARNINGS_RATE, 				// 預估收益率
				EST_EARNINGS        : $scope.salePlanRow.EST_EARNINGS,    					// 預估收益
				ACTION_DATE         : $scope.toJsDate($scope.salePlanRow.ACTION_DATE),     	// 預計約訪日期(A)
				MEETING_DATE        : $scope.toJsDate($scope.salePlanRow.MEETING_DATE),     // 預計見面日期(M)
				CLOSE_DATE          : $scope.toJsDate($scope.salePlanRow.CLOSE_DATE)     	// 預計成交日期(C)
			};
		} else {
			$scope.sendRecv("PMS103", "getYmList", "com.systex.jbranch.app.server.fps.pms103.PMS103InputVO", $scope.inputVO, function(tota, isError) {
				if (!isError) {
					$scope.ymList = tota[0].body.ymList;
					if ($scope.ymList != null && $scope.ymList.length > 0) {
			           $scope.currentYM = tota[0].body.currentYM;
			           
			           // 報表留存時間 未來三個月&過去三個月
			           $scope.ymList.splice(0, 2);
			           $scope.ymList.splice(7);
			        }
				}
		    });
			
			$scope.sendRecv("PMS109", 'queryData', "com.systex.jbranch.app.server.fps.pms109.PMS109InputVO", {CUST_ID: $scope.cust_id, CUST_NAME: $scope.cust_name, SRC_TYPE: $scope.SRC_TYPE}, function(tota, isError) {
				if (tota[0].body.resultList.length > 0) {	
			    	var sTimeMM = (new Date().getMonth() + 1) < 10 ? '0' + (new Date().getMonth() + 1) : (new Date().getMonth() + 1);
	
					var row = tota[0].body.resultList[0];
					$scope.inputVO = {
						SEQ                 : row.NEWSEQ || '0',         						// 銷售計劃序號
						PLAN_YEARMON        : new Date().getFullYear() + "" + sTimeMM,			// 銷售計劃月份
						BRANCH_NBR          : '',    											// 分行代碼
						AO_CODE             : '',    											// AO CODE
						EMP_ID              : projInfoService.getUserID(),    					// 員工編號
						EMP_NAME            : '',    											// 員工姓名
						CUST_ID             : row.CUST_ID || '',    							// 客戶ID
						CUST_NAME           : row.CUST_NAME || '',    							// 客戶姓名
						SRC_TYPE            : $scope.SRC_TYPE,    								// 來源種類
						EST_PRD             : '',    											// 預計承作商品
						EST_AMT             : 0,    											// 預計承作金額
						EST_EARNINGS_RATE   : 0,    											// 預估收益率
						EST_EARNINGS        : 0,    											// 預估收益
						ACTION_DATE         : undefined,     									// 預計約訪日期(A)
						MEETING_DATE        : undefined,     									// 預計見面日期(M)
						CLOSE_DATE          : undefined     									// 預計成交日期(C)
					};
				} 
			});
		}
		
		if ($scope.SRC_TYPE == '5'){
			PPAPgetAoCode().then(function() {
				$scope.SRC_TYPE = '3';
				console.log("導入完成");
			});
		} else if ($scope.inputVO.AO_CODE == '' || $scope.inputVO.AO_CODE == undefined || $scope.inputVO.AO_CODE == null) {
			PPAPgetAoCode().then(function() {
				console.log("導入完成");
			});
		}
	}
	
	$scope.init();
	
	$scope.action = function (type) {
		$scope.inputVO.queryModType = type;
		
		switch (type) {
			case "A": 	// 儲存: PMS109/PMS103
				// 畫面欄位檢核
				if ($scope.parameterTypeEditForm.$invalid) {
		            $scope.showErrorMsg('欄位檢核錯誤:請輸入必填欄位！');
		            return;
		        }
				
				$scope.sendRecv("PMS109", "queryMod", "com.systex.jbranch.app.server.fps.pms109.PMS109InputVO", $scope.inputVO, function(tota, isError) {				
					if (!isError) {
						$scope.showMsg('儲存成功');
						$scope.closeThisDialog('successful');
					}
				});	
				
				break;
			case "M": // 修改: PMS103
				$scope.sendRecv("PMS109", "queryMod", "com.systex.jbranch.app.server.fps.pms109.PMS109InputVO", $scope.inputVO, function(tota, isError) {				
					if (!isError) {
						$scope.showMsg('更新成功');
						$scope.closeThisDialog('successful');
					}
				});	
				
				break;
			
			case "C":
				$scope.closeThisDialog('cancel');
				break;
		}
	}					
});
