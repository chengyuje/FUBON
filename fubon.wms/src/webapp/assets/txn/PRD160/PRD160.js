'use strict';
eSoafApp.controller('PRD160Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, $q) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD160Controller";
		
		// combobox
		getParameter.XML(["PRD.INS_CURRENCY", "PRD.INS_TYPE", "COMMON.YES_NO"], function(totas) {
			if (totas) {
				$scope.INS_CURRENCY = totas.data[totas.key.indexOf('PRD.INS_CURRENCY')];
				$scope.INS_TYPE = totas.data[totas.key.indexOf('PRD.INS_TYPE')];
				$scope.COM_YN = totas.data[totas.key.indexOf('COMMON.YES_NO')];
			}
		});
		// date picker
		$scope.sale_sdateOptions = {};
		$scope.sale_edateOptions = {};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.sale_sdateOptions.maxDate = $scope.inputVO.sale_edate;
			$scope.sale_edateOptions.minDate = $scope.inputVO.sale_sdate;
		};
		// date picker end
		
		// init
		$scope.inputVO = {};
        $scope.inputVO.type = '1';
		$scope.init = function() {
			var oritype = $scope.inputVO ? $scope.inputVO.type : ''; 
			$scope.inputVO = {};
			$scope.inputVO.type = oritype;
			// 2017/2/20 add
			if($scope.is910) {
				$scope.inputVO.type = '2';
				$scope.inputVO.ins_type = $scope.is910_ins_type;
				if($scope.is910_ins_type == '2')
					$scope.inputVO.is_inv = "Y";
			}
		};
		$scope.init();
		$scope.inquireInit = function() {
			$scope.paramList = [];
			$scope.totalList = [];
			$scope.outputVO = {};
		}
		$scope.inquireInit();
		
		$scope.getName = function() {
			var deferred = $q.defer();
			if($scope.inputVO.ins_id) {
				$scope.inputVO.ins_id = $scope.inputVO.ins_id.toUpperCase();
				$scope.sendRecv("PRD160", "getInsName", "com.systex.jbranch.app.server.fps.prd160.PRD160InputVO", {'ins_id':$scope.inputVO.ins_id},
						function(tota, isError) {
							if (!isError) {
								if (tota[0].body.ins_name) {
									$scope.inputVO.ins_name = tota[0].body.ins_name;
								}
								deferred.resolve();
							}
				});
			} else
				deferred.resolve();
			return deferred.promise;
		};
		
		// inquire
		$scope.inquire = function(){
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			// toUpperCase
			if($scope.inputVO.cust_id)
				$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase();
			if($scope.inputVO.ins_name)
				$scope.inputVO.ins_name = $scope.inputVO.ins_name.toUpperCase();
			if($scope.inputVO.ins_id) {
				$scope.inputVO.ins_id = $scope.inputVO.ins_id.toUpperCase();
				$scope.getName().then(function(data) {
					$scope.reallyInquire();
				});
			} else
				$scope.reallyInquire();
		};
		$scope.reallyInquire = function() {
			$scope.sendRecv("PRD160", "inquire", "com.systex.jbranch.app.server.fps.prd160.PRD160InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.totalList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
						}
			});
		};
		
		$scope.detail = function(row) {
			var id = $scope.inputVO.cust_id;
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD160/PRD160_DETAIL.html',
				className: 'PRD160_DETAIL',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                	$scope.cust_id = id;
                }]
			});
		};
		
		$scope.jump = function(row) {
			if($scope.isPop)
				$scope.closeThisDialog(row);
		};
		
		
});