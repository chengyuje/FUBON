
'use strict';
eSoafApp.controller('PPAPController', function($rootScope, $scope, $controller, socketService,ngDialog, sysInfoService, $q, $confirm, $filter) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "PPAPController";	
	
	///共用檢核DILOG
	$scope.empList = [];
	
	/*****以下是修改建一開始視窗畫面'*****/
	//mode = 'add' : 新增 , 'upd' : 修改
	$scope.ppap = function (salePlanRow, SEQ, CUST_ID, CUST_NAME, src_type, mode) {

		if (sysInfoService.getPriID() == '011' || sysInfoService.getPriID() == '009') {
			$scope.inputVO.PRD = "manager"; 
		} else if (sysInfoService.getPriID() == '001' || sysInfoService.getPriID() == '002' || sysInfoService.getPriID() == '003') {
			$scope.inputVO.PRD = "aocode"; 
		}
		
		// 2017/3/8 直接帶出該客戶的所屬專員，不可修改，若沒有專員的資料則跳錯誤訊息不可新增
		function PPAPgetAoCode() {
			var deferred = $q.defer();
			$scope.sendRecv("PMS109", "getAoCode", "com.systex.jbranch.app.server.fps.pms109.PMS109InputVO", {'PMSROW': CUST_ID}, function(tota, isError) {
				if (!isError) {
					if(tota[0].body.resultList.length == 0 || !tota[0].body.resultList[0].AO_CODE)
						deferred.reject();
					else
						deferred.resolve(tota[0].body.resultList[0]);
				}
			});
			return deferred.promise;
		};
		
		PPAPgetAoCode().then(function(cust_vo) {
			var dialog = ngDialog.open({
				template: 'assets/txn/PMS109/PMS109.html',
				className: 'PMS109',
				controller: ['$scope', function($scope) {
					$scope.salePlanRow = salePlanRow;  	// PMS103  的 ROW
					$scope.SEQ = SEQ;
					$scope.cust_id = CUST_ID;   		
					$scope.cust_name = CUST_NAME;   	// 2017/06/06  新增  CUST_name 由前端帶入
					$scope.SRC_TYPE = src_type;
					$scope.mode = mode;
					
					//2017/3/8 add
					$scope.cust_ao = cust_vo.AO_CODE;
					$scope.cust_aoName = cust_vo.EMP_NAME;
				}]
			});
			
			//判斷是否由客管進入
			switch (src_type) {
				case "3":
					dialog.closePromise.then(function (data) {								  
						if (data.value == "successful" && mode == "upd") {
							$scope.inquire();
						}
						
						$rootScope.IamInitFunction();//#0001958:由待辦行事曆打開的任何是窗關閉後做行事曆頁面的重新整理
					});
					
					break;
				default:
					dialog.closePromise.then(function (data) {								  
						if (data.value == "successful" && mode == "upd") {
							$scope.inquire();
						}
					});
				
					break;
			}
    	}, function(reason) {
    		$scope.showErrorMsg('該客戶無所屬專員');
    	});
	}
});