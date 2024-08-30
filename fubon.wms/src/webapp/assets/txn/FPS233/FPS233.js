/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('FPS233Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "FPS233Controller";
		

		$scope.paramList = [ {
				'PRD_TYPE' : '基金',
				'PRD_NUM' : 'XXXXXX富邦中國新平平衡入息基金-A類型(人民幣)',
				'CUS':'P2',
				'CUR' : 'CNY',
				'CUR_TYPE' : '1',
				'WEATHER' : '1',
				'ORG_AMT' : 162000,
				'U_AMT' : 500000,
				'PER' : 10,
				'AMT' : ''
			}, {
				'PRD_TYPE' : '基金',
				'PRD_NUM' : 'XXXXXX翰亞股價入息組合基金',
				'CUS':'P1',
				'CUR' : 'TWD',
				'CUR_TYPE' : '1',
				'WEATHER' : '1',
				'ORG_AMT' : 162000,
				'U_AMT' : 100000,
				'PER' : 10,
				'AMT' : ''
			}, {
				'PRD_TYPE' : '基金',
				'PRD_NUM' : 'XXXXXX野村全球美元投資級',
				'CUS':'P1',
				'CUR' : 'USD',
				'CUR_TYPE' : '1',
				'WEATHER' : '1',
				'ORG_AMT' : 162000,
				'U_AMT' : 100000,
				'PER' : 10,
				'AMT' : ''
			}];
		
		
		$scope.paramListCopy = [  {
			'PRD_TYPE' : '基金',
			'PRD_NUM' : 'XXXXXX富邦中國新平平衡入息基金-A類型(人民幣)',
			'CUS':'P2',
			'CUR' : 'CNY',
			'CUR_TYPE' : '1',
			'WEATHER' : '1',
			'ORG_AMT' : 162000,
			'U_AMT' : 500000,
			'PER' : 10,
			'AMT' : ''
		}, {
			'PRD_TYPE' : '基金',
			'PRD_NUM' : 'XXXXXX翰亞股價入息組合基金',
			'CUS':'P1',
			'CUR' : 'TWD',
			'CUR_TYPE' : '1',
			'WEATHER' : '1',
			'ORG_AMT' : 162000,
			'U_AMT' : 100000,
			'PER' : 10,
			'AMT' : ''
		}, {
			'PRD_TYPE' : '基金',
			'PRD_NUM' : 'XXXXXX野村全球美元投資級',
			'CUS':'P1',
			'CUR' : 'USD',
			'CUR_TYPE' : '1',
			'WEATHER' : '1',
			'ORG_AMT' : 162000,
			'U_AMT' : 100000,
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
		
		
		
		$scope.mappingSet['CUR_TYPE'] = [];
	    $scope.mappingSet['CUR_TYPE'].push({LABEL: "原幣", DATA: "1"},
	    		{LABEL: "外幣", DATA: "2"});
		
		//列印
		$scope.curs = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/FPS233/FPS233_ROUTE.html',
				className: 'FPS233_CUR',
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
		
		$scope.returnGreat = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/FPS233/FPS233_ROUTE2.html',
				className: 'FPS233_GREAT',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = {};
                }]
			});
        }
		
		
		
});