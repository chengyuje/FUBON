'use strict';
eSoafApp.controller('CRM110Controller', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "CRM110Controller";
	
	$scope.role = sysInfoService.getPriID();
	$scope.ao_code = String(sysInfoService.getAoCode());
	
	$scope.mappingSet['cust_type'] = [];
	$scope.mappingSet['cust_type'].push({LABEL: '客戶ID' , DATA: 'ID'}, {LABEL: '客戶姓名' , DATA: 'NAME'});
	
	getParameter.XML(["CRM.NONSEARCH_ID"], function(totas) {
		if (totas) {
			$scope.mappingSet['CRM.NONSEARCH_ID'] = totas.data[totas.key.indexOf("CRM.NONSEARCH_ID")];
		}
	});
	
	//消金
	if ($scope.role == '004') {
		$scope.inputVO.role = 'ps';	
	}
	
	//個金AO
	else if ($scope.role == '004AO') {
		$scope.inputVO.role = 'pao';	
	}
	
	//輔銷FA
	else if ($scope.role == '014' || $scope.role == '015' ||$scope.role == '023' ||$scope.role == '024' ) {
		$scope.inputVO.role = 'faia';
	}
	
	//UHRM
	else if ($scope.role == 'UHRM002' || $scope.role == 'UHRM012' || $scope.role == 'UHRM013') {
		$scope.inputVO.role = 'UHRM';
	}
	
	//理專 : $scope.role == '001' ===>是AFC，AFC 應可從快查KEY ID或姓名來查詢同歸屬行內的任一客戶(#0002070)
	else if ($scope.ao_code != '' &&  $scope.ao_code != undefined && $scope.role != '001') {
		$scope.inputVO.role = 'ao';
		$scope.inputVO.ao_code = $scope.ao_code;
	} else {
		$scope.inputVO.role = 'other';
	}
	
	$scope.init = function() {
		$scope.inputVO.cust = '';
		$scope.inputVO.crm110Type = 'ID';
	};
	$scope.init();
	
	$scope.init2 = function() {
		$scope.inputVO.cust_name = '';
		$scope.inputVO.cust_id = '';
	};
	$scope.init2();
	
	$scope.myFunct = function(keyEvent) {
	  if (keyEvent.which === 13)
		  $scope.inquire();
	}
	
	$scope.inquire = function(){
		$scope.noQuery = false;

		if ($scope.inputVO.crm110Type == '') {
			$scope.showErrorMsg("請選取");
			return;
		}
		
		if ($scope.inputVO.crm110Type == 'NAME') {
			$scope.inputVO.cust_name = $scope.inputVO.cust;
		} else if ($scope.inputVO.crm110Type == 'ID') {		
			$scope.inputVO.cust_id = $scope.inputVO.cust.toUpperCase();	
			
			angular.forEach($scope.mappingSet['CRM.NONSEARCH_ID'], function(row) {
        		if($scope.inputVO.cust_id === row.DATA ){
        			$scope.showErrorMsg("ehl_01_CRM_001");
        			$scope.noQuery = true;
        			return;
        		}
			});		
			
			if($scope.noQuery) {
				return;
			}
		} else {
			return;
		}
		
		$scope.sendRecv("CRM110", "inquire", "com.systex.jbranch.app.server.fps.crm110.CRM110InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				/******查無資料時(查詢為何查無資料)******/
				if (tota[0].body.resultList.length == 0) {
					if ($scope.inputVO.crm110Type == 'NAME') {
						$scope.showErrorMsg('ehl_01_org130_001');		//查無此人，請重新輸入。
					} else if ($scope.inputVO.crm110Type == 'ID') {
						$scope.sendRecv("CRM110", "inquireCust", "com.systex.jbranch.app.server.fps.crm110.CRM110InputVO", $scope.inputVO, function(tota, isError) {
							if (!isError) {
								if(tota[0].body.resultList.length == 0) {
									//查無資料
									$scope.showErrorMsg('ehl_01_cus130_002',[$scope.inputVO.cust_id]);			//無此客戶ID：{0}
									$scope.init2();
									return;
								}
								
								if (tota[0].body.resultList.length == 1) {
									if (tota[0].body.resultList[0].BRA_NBR == null){
										//無歸屬行
										$scope.showErrorMsg('ehl_01_cus130_002',[$scope.inputVO.cust_id]);		//無此客戶ID：{0}
										$scope.init2();
										return;
									} else {
										if (tota[0].body.resultList[0].EMP_NAME == null) {
											//空code客戶
											var list = [tota[0].body.resultList[0].BRA_NBR, tota[0].body.resultList[0].BRANCH_NAME, tota[0].body.resultList[0].RM_NAME ? '、 RM' + tota[0].body.resultList[0].RM_NAME : ''];
											$scope.showErrorMsg('ehl_01_cus130_006', list);		// 客戶歸屬行：{0}-{1}{2}
											$scope.init2();
											return;	
										} else {
											//有歸屬行&所屬理專
											var list = [tota[0].body.resultList[0].BRA_NBR, tota[0].body.resultList[0].BRANCH_NAME, tota[0].body.resultList[0].AO_CODE, tota[0].body.resultList[0].EMP_NAME, tota[0].body.resultList[0].RM_NAME ? '、 RM' + tota[0].body.resultList[0].RM_NAME : ''];
											$scope.showErrorMsg('ehl_01_cus130_005', list);		// 客戶歸屬行：{0}-{1}，理專{2}{3}{4}
											$scope.init2();
											return;											
										}
									}											
								}									
							}								
						});								
					}
				}
				
				//單筆資料
				if (tota[0].body.resultList.length == 1) {
					
					angular.forEach($scope.mappingSet['CRM.NONSEARCH_ID'], function(row) {
		        		if (tota[0].body.resultList[0].CUST_ID === row.DATA){
		        			$scope.showErrorMsg("ehl_01_CRM_001");
		        			$scope.noQuery = true;
		        			return;
		        		}
					});		
					
					if ($scope.noQuery) {
						return;
					}
					
					var path = '';
					$scope.connector('set','CRM110_CUST_ID', tota[0].body.resultList[0].CUST_ID);
					$scope.connector('set','CRM110_CUST_NAME', tota[0].body.resultList[0].CUST_NAME);
					$scope.connector('set','CRM110_AOCODE', tota[0].body.resultList[0].AO_CODE);
					
					$scope.CRM_CUSTVO = {
						CUST_ID :  tota[0].body.resultList[0].CUST_ID,
						CUST_NAME :tota[0].body.resultList[0].CUST_NAME
					}
					
					$scope.connector('set','CRM_CUSTVO',$scope.CRM_CUSTVO)
					
					if ($scope.role == '004' || $scope.role == '004AO') {
						//消金首頁
						path = "assets/txn/CRM711/CRM711.html";
					}else {
						//一般客戶首頁
						path = "assets/txn/CRM610/CRM610_MAIN.html";
					}
					
					$scope.connector("set","CRM610URL",path);
//							$rootScope.menuItemInfo.url = "assets/txn/CRM610/CRM610.html";
					$scope.init2();
					var dialog = ngDialog.open({
						template: 'assets/txn/CRM610/CRM610.html',
						className: 'CRM610',
						showClose: false
					});
					return;
				}
				
				//多筆資料
				if(tota[0].body.resultList.length > 1) {
					var row = tota[0].body.resultList;
					var totaBody = tota[0].body;
					var role = $scope.inputVO.role;
					var cust_name = $scope.inputVO.cust;
					var ao_code = $scope.ao_code;
					var dialog = ngDialog.open({
						template: 'assets/txn/CRM110/CRM110_MultiData.html',
						className: 'CRM110_MultiData',
						showClose: false,
						controller: ['$scope', function($scope) {
		                	$scope.row = row;
		                	$scope.totaBody = totaBody;
		                	$scope.role = role;
		                	$scope.cust_name = cust_name;
		                	$scope.ao_code = ao_code;
		                }]
					});
					return;
				}
			}
		}
	)};
});
		