/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SIMGR009Controller', function($scope, $controller,
		socketService, ngDialog, projInfoService, $timeout) {
	$controller('BaseController', {$scope : $scope});
	
	$scope.controllerName = "SIMGR009Controller";

	$scope.init = function() {
		$scope.inputVO = {
			cmbPtypeBuss : 'BU'
		};
		$scope.paramStatusDesc = [
				{DATA: 0, LABEL: '--'},
				{DATA: 1, LABEL: '刪除'},
				{DATA: 2, LABEL: '修改'},
				{DATA: 3, LABEL: '新增'}
		];
	}
	$scope.init();
	
	$scope.sortingLog = [];

	  $scope.sortableOptions = {
	    // called after a node is dropped
	    stop: function(e, ui) {
//	      var logEntry = {
//	        ID: $scope.sortingLog.length + 1,
//	        Text: 'Moved element: ' + ui.item.scope().item.text
//	      };
//	      $scope.sortingLog.push(logEntry);
//	    	console.log('adgParameter=' + JSON.stringify($scope.adgParameter));
	    }
	  };
	$scope.predicate = 'PARAM_TYPE';
	$scope.reverse = true;

	$scope.inquireParamType = function() {

		$scope.paramList = [];
		
		$scope.sendRecv("SIMGR009", "inquireParamType",
				"com.systex.jbranch.app.server.fps.simgr009.SIMGR009InputVO",
				$scope.inputVO, function(tota, isError) {					
					if (!isError) {
						$scope.paramList = tota[0].body.lstParameter;
						$('.slim-scroll').each(function () {
					        var $this = $(this);
					        $this.slimScroll({
					            height: $this.data('height'),
					            railVisible: true,
					            alwaysVisible: true
					        });
					    });
						return;
					}
				});
	}
	
	
	
	$scope.adgParameterPredicate = '';
	$scope.adgParameterReverse = false;
//	$scope.adgParameterPredicateOrderby = function(adgParameterPredicate) {
//		console.log('adgParameterPredicate=' + adgParameterPredicate + ', adgParameterReverse=' + $scope.adgParameterReverse);
//		$scope.adgParameterReverse = ($scope.adgParameterPredicate === adgParameterPredicate) ? !$scope.adgParameterReverse : false;
//		$scope.adgParameterPredicate = adgParameterPredicate;
//    };
	
	$scope.sortableOptions = {
          stop: function(e, ui) {
            angular.forEach($scope.adgParameter, function(row, index){
            	row.PARAM_ORDER = index;
            });
          }
        };
	
	$scope.detail = function(row){
		
		$scope.currentDetailRow = {};
		$scope.currentRow = row;
		$scope.inputVO.selectedParameter = row.PARAM_TYPE;
//		console.log('PARAM_TYPE='+row.PARAM_TYPE);
		$scope.sendRecv("SIMGR009", "inquireParamDetail",
				"com.systex.jbranch.app.server.fps.simgr009.SIMGR009InputVO",
				$scope.inputVO, function(tota, isError) {									
					if (!isError) {
						$scope.ptype_desc = tota[0].body.adgParameter[0].PTYPE_DESC;						
						$scope.adgParameter = tota[0].body.adgParameter;						
						if($scope.adgParameter[0].PARAM_NAME==null){							
							$scope.adgParameter = [];
						}						
//						console.log('$scope.adgParameter='+JSON.stringify($scope.adgParameter));
						return;
					}
				});
	};

	$scope.$watch('inputVO.cmbPtypeBuss', function(newValue, oldValue) {
		if (newValue == oldValue) {
			return;
		}

		$scope.inquireParamType();
	});

	$scope.$watch('inputVO.orderType', function(newValue, oldValue) {
		if (newValue == oldValue) {
			return;
		}
//		switch(newValue){
//			case '1':
//				$scope.predicate = 'PARAM_TYPE';
//			break;
//			case '2':
//				$scope.predicate = 'PARAM_NAME';
//			break;
//		}
//		console.log('predicate='+$scope.predicate);
	});

	

	// 自動選擇combobox第一筆
	var watchOrderTypeDatasource = $scope.$watch('orderTypeDatasource', function(newValue, oldValue) {
		if (newValue == oldValue) {
			return;
		}
		if ($scope.orderTypeDatasource.length > 0) {
			$scope.inputVO.cmbOrderType = $scope.orderTypeDatasource[1].DATA;
			watchOrderTypeDatasource();
		}
	});

	$scope.show = function(row, index) {
		$scope.currentDetailRow = row;
		$scope.currentDetailRowIndex = index;
	};
	
	$scope.edit = function(row) {
//		console.log('edit data=' + JSON.stringify(row));
		row = row || {};
		row.PARAM_TYPE = row.PARAM_TYPE || $scope.inputVO.selectedParameter;
		var adgParameter = $scope.adgParameter;
		var dialog = ngDialog.open({
			template : 'assets/txn/SIMGR009/SIMGR009_EDIT.html',
			className : 'SIMGR009',
			controller : [ '$scope', function($scope) {
				$scope.row = row;
				$scope.adgParameter = adgParameter;
			} ]
		});
		dialog.closePromise.then(function(data) {
//			console.log('data='+JSON.stringify(data.value));
			if(data.value.PARAM_STATUS == '3'){//新增時				
				$scope.adgParameter.push(data.value);				
			}
//			console.log('$scope.adgParameter='+JSON.stringify($scope.adgParameter));
		});
	};
	
	$scope.del = function(){
		if($scope.currentDetailRow.PARAM_STATUS == '0' || $scope.currentDetailRow.PARAM_STATUS == '2'){
			$scope.currentDetailRow.PARAM_STATUS = '1';
			return;
		}
		if($scope.currentDetailRow.PARAM_STATUS == '3'){
			$scope.adgParameter.splice($scope.currentDetailRowIndex, 1);//刪除剛新增的資料
		}
    }
	
	$scope.exportData = function(){
		$scope.inputVO.varCsvType = $scope.currentRow.CSV_TYPE;
		$scope.sendRecv("SIMGR009", "export",
				"com.systex.jbranch.app.server.fps.simgr009.SIMGR009InputVO",
				$scope.inputVO, function(tota, isError) {
					if (!isError) {
						$scope.adgParameter = tota[0].body.adgParameter;
						return;
					}
				});
	}
	
	$scope.uploadFinshed = function(url){
		
		$scope.inputVO.csvFileName = url.replace("temp/", "");
		$scope.inputVO.varWorksType = "2";
		$scope.inputVO.varCsvType = "0";
		$scope.sendRecv("SIMGR009", "importCSV",
				"com.systex.jbranch.app.server.fps.simgr009.SIMGR009InputVO",
				$scope.inputVO, function(tota, isError) {
					if (!isError) {
						$scope.detail($scope.currentRow);
						return;
					}
				});
	}
	
	$scope.save = function(){
		$scope.inputVO.adgParameter = $scope.adgParameter;		
		$scope.sendRecv("SIMGR009", "save",
				"com.systex.jbranch.app.server.fps.simgr009.SIMGR009InputVO",
				$scope.inputVO, function(tota, isError) {					
					if (!isError) {
						$scope.detail($scope.currentRow);
						return;
					}
				});
	}
	
	$scope.reset = function(){
		if(confirm('確定要清除？')){
			$scope.detail($scope.currentRow);
		}
	}
	
});
