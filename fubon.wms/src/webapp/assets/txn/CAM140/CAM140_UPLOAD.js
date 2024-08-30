/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CAM140_UPLOADController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CAM140_UPLOADController";
		
		$scope.inputVO = {};
		$scope.inputVO.finFileList = $scope.current || [];
		
		$scope.uploadFinshed = function(name, rname) {
			if ($scope.inputVO.finFileList.length < 5 ) {
    			$scope.inputVO.finFileList.push({'TYPE':'1','DOC_NAME':rname,'OTHER':name});
    		}
        };
        
        $scope.addWeb = function() {
        	if ($scope.inputVO.finFileList.length < 5 ) {
        		$scope.inputVO.finFileList.push({'TYPE':'2','DOC_NAME':$scope.inputVO.web,'OTHER':$scope.inputVO.web});
        	}
		};
        
        $scope.select = function() {
        	var nowCount = $scope.inputVO.finFileList.length;
        	var dialog = ngDialog.open({
				template: 'assets/txn/CAM140/CAM140_SELECT.html',
				className: 'CAM140_SELECT',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.count = nowCount;
                }]
			});
        	dialog.closePromise.then(function (data) {
        		if(data.value != 'cancel') {
        			angular.forEach(data.value, function(row) {
            			$scope.inputVO.finFileList.push({'TYPE':'3','DOC_NAME':row.DOC_NAME,'OTHER':row.DOC_ID});
    				});
        		}
			});
        };
        
        $scope.removeUpload = function(index) {
        	if($scope.flag) {
        		var delI = $scope.delList.indexOf($scope.inputVO.finFileList[index].DOC_ID);
    			if(delI == -1 && $scope.inputVO.finFileList[index].DOC_ID != '') {
    				$scope.delList.push($scope.inputVO.finFileList[index].DOC_ID);
    			}
        	}
        	$scope.inputVO.finFileList.splice(index,1);
        };
        
        $scope.save = function() {
        	if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
        	if($scope.inputVO.finFileList.length > 5) {
        		$scope.showErrorMsg('欄位檢核錯誤:最多上傳五筆');
        		return;
        	}
        	var ans = [];
        	ans.push($scope.inputVO.finFileList);
        	ans.push($scope.delList);
        	$scope.closeThisDialog(ans);
        };
        
		
		
});