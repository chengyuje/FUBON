/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD290Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD290Controller";
		
		// combobox
		getParameter.XML(["PRD.RETURN_TARGET"], function(totas) {
			if (totas) {
				if(totas.data[totas.key.indexOf('PRD.RETURN_TARGET')][0])
					$scope.RETURN_TARGET = totas.data[totas.key.indexOf('PRD.RETURN_TARGET')][0].DATA;
				else
					$scope.RETURN_TARGET = 10;
			}
		});
		$scope.mappingSet['Type'] = [{LABEL: '基金', DATA: '1'}];
		//
		
		// init
		$scope.init = function() {
			$scope.inputVO = {};
			$scope.checkVO = {};
		};
		$scope.init();
		$scope.inquireInit = function() {
			$scope.totalList = [];
			$scope.outputVO = {};
		}
		$scope.inquireInit();
		
		// inquire
		$scope.inquire = function() {
			// toUpperCase
			if($scope.inputVO.prd_id)
				$scope.inputVO.prd_id = $scope.inputVO.prd_id.toUpperCase();
			$scope.inputVO.target = $scope.RETURN_TARGET;
			$scope.sendRecv("PRD290", "inquire", "com.systex.jbranch.app.server.fps.prd290.PRD290InputVO", $scope.inputVO,
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
		
		$scope.$watchCollection('paramList', function(newNames, oldNames) {
			$scope.checkVO.clickAll = false;
        });
		$scope.checkrow = function() {
        	if ($scope.checkVO.clickAll) {
        		angular.forEach($scope.paramList, function(row) {
        			row.CHECK = true;
    			});
        	} else {
        		angular.forEach($scope.paramList, function(row) {
        			row.CHECK = false;
    			});
        	}
        };
		
		$scope.setting = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD290/PRD290_Setting.html',
				className: 'PRD290',
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
		
		$scope.compare = function() {
			var ans = $scope.totalList.filter(function(obj){
	    		return (obj.CHECK == true);
	    	});
			if(ans.length != 2) {
	    		$scope.showErrorMsg('ehl_02_prd290_001');
        		return;
        	}
			else {
				var dialog = ngDialog.open({
					template: 'assets/txn/PRD290/PRD290_Compare.html',
					className: 'PRD290',
					showClose: false,
	                controller: ['$scope', function($scope) {
	                	$scope.ans = ans;
	                }]
				});
			}
		};
		
		$scope.detail = function (row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD290/PRD290_Linear.html',
				className: 'PRD290_Linear',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					$scope.inquireInit();
					$scope.inquire();
				}
			});
		};
		
		
});
