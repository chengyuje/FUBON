/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('MGM211_FILEController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, sysInfoService, $q, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "MGM211_FILEController";
		
		$scope.init = function() {
			if($scope.fileList != []){
				$scope.delFileSeq = [];
				if($scope.connector('get', 'MGM211_delFileSeq') != null){
					$scope.delFileSeq = $scope.connector('get', 'MGM211_delFileSeq');
				}
				$scope.inputVO.fileList = $scope.fileList;
			}else{
				$scope.inputVO.fileList = [];				
			}
		}
		$scope.init();
		
		$scope.upload = function(name, rname) {
//			$scope.inputVO.file_name = name;
//			$scope.inputVO.actual_file_name = rname;
			$scope.inputVO.fileList.push({'name':name, 'rname':rname});
		}
		
		$scope.deleteRow = function(index) {
			if($scope.fileList != []){
				$scope.delFileSeq.push({'seq':$scope.inputVO.fileList[index].SEQ});
			}
			$scope.inputVO.fileList.splice(index, 1);
		}
		
		$scope.saveFile = function() {
//			alert(JSON.stringify($scope.inputVO.fileList));
//			alert(JSON.stringify($scope.delFileSeq));
			$scope.connector('set', 'MGM211_fileList', $scope.inputVO.fileList);
			$scope.connector('set', 'MGM211_delFileSeq', $scope.delFileSeq);
			$scope.closeThisDialog('successful');
		}
});
