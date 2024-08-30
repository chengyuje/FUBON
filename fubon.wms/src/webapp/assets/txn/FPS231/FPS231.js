/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('FPS231Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "FPS231Controller";
		

		$scope.paramList = [ {
				'PRD_TYPE' : '存款',
				'PRD_NUM' : '台幣存款',
				'CUR' : 'TWD',
				'ORG_AMT' : 500000,
				'U_AMT' : 500000,
				'PER' : 10,
				'AMT' : ''
			}, {
				'PRD_TYPE' : '存款',
				'PRD_NUM' : '外幣存款',
				'CUR' : 'USD',
				'ORG_AMT' : 16200,
				'U_AMT' : 500000,
				'PER' : 10,
				'AMT' : ''
			}, {
				'PRD_TYPE' : '儲蓄型保險',
				'PRD_NUM' : 'ADJ1 儲蓄險功能',
				'CUR' : 'TWD',
				'ORG_AMT' : 16200,
				'U_AMT' : 500000,
				'PER' : 10,
				'AMT' : ''
			}, {
				'PRD_TYPE' : '儲蓄型保險',
				'PRD_NUM' : 'ADJ1 儲蓄險功能',
				'CUR' : 'TWD',
				'ORG_AMT' : 16200,
				'U_AMT' : 500000,
				'PER' : 10,
				'AMT' : ''
			} ];
		
		
		$scope.paramListCopy = [ {
			'PRD_TYPE' : '存款',
			'PRD_NUM' : '台幣存款',
			'CUR' : 'TWD',
			'ORG_AMT' : 500000,
			'U_AMT' : 500000,
			'PER' : 10,
			'AMT' : ''
		}, {
			'PRD_TYPE' : '存款',
			'PRD_NUM' : '外幣存款',
			'CUR' : 'USD',
			'ORG_AMT' : 16200,
			'U_AMT' : 500000,
			'PER' : 10,
			'AMT' : ''
		}, {
			'PRD_TYPE' : '儲蓄型保險',
			'PRD_NUM' : 'ADJ1 儲蓄險功能',
			'CUR' : 'TWD',
			'ORG_AMT' : 16200,
			'U_AMT' : 500000,
			'PER' : 10,
			'AMT' : ''
		}, {
			'PRD_TYPE' : '儲蓄型保險',
			'PRD_NUM' : 'ADJ1 儲蓄險功能',
			'CUR' : 'TWD',
			'ORG_AMT' : 16200,
			'U_AMT' : 500000,
			'PER' : 10,
			'AMT' : ''
		} ];
//		$scope.paramListCopy=[];
//		$scope.paramListCopy= angular.copy($scope.paramList);
		$scope.save=function(){
			$scope.showSuccessMsg("儲存成功");
		}
		
		$scope.del = function() {
			$confirm({text: '請確定是否刪除此筆資料？'}, {size: 'sm'}).then(function() {
			
			});
        }
		
		//列印
		$scope.curs = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/FPS231/FPS231_ROUTE.html',
				className: 'FPS231_CUR',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = {};
                }]
			});
		};
		
		
		$scope.returnCopy = function() {
			$confirm({text: '是否匯入原始建議？'}, {size: 'sm'}).then(function() {
				$scope.paramList=[];
				$scope.paramList=angular.copy($scope.paramListCopy);
			});
        }
});