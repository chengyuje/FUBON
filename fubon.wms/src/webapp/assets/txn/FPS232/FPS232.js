/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('FPS232Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "FPS232Controller";
		

		$scope.paramList = [ {
				'PRD_TYPE' : 'SN',
				'PRD_NUM' : '瑞士信貸倫敦分行發五年期澳幣計價目標提前贖回及發行機構',
				'CUS':'P2',
				'CUR' : 'AUD',
				'ORG_AMT' : 500000,
				'U_AMT' : 500000,
				'PER' : 10,
				'AMT' : ''
			}];
		
		
		$scope.paramListCopy = [  {
			'PRD_TYPE' : 'SN',
			'PRD_NUM' : '瑞士信貸倫敦分行發五年期澳幣計價目標提前贖回及發行機構',
			'CUS':'P2',
			'CUR' : 'AUD',
			'ORG_AMT' : 500000,
			'U_AMT' : 500000,
			'PER' : 10,
			'AMT' : ''
		}];
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
				template: 'assets/txn/FPS232/FPS232_ROUTE.html',
				className: 'FPS232_CUR',
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