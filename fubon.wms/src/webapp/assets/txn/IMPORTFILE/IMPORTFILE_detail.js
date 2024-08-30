/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('IMPORTFILE_detailController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $timeout, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "IMPORTFILE_detailController";

		
		$scope.mappingSet['TAR_DATA_TYPE']=[];
		$scope.mappingSet['TAR_DATA_TYPE'].push({LABEL:"varchar2(字串)" , DATA: '1'},
												{LABEL:"Number(整數)" , DATA: '2'},
												{LABEL:"Number(浮點數)" , DATA: '4'},
												{LABEL:"Date(日期)" , DATA: '3'});
		
		var set_names = {};
		//init
		$scope.init = function(){
			$scope.Sheets = $scope.Sheets || [];
			set_names = $scope.set_names || {};
			if ($scope.Sheets.length == 0) {
				$scope.add_name = '';
				$scope.add_code = '';
			} else {
				$scope.add_name = $scope.Sheets[0].NAME;
				$scope.add_code = $scope.Sheets[0].CODE;
			}
			$scope.activeTabIndex = 0;
			$scope.add_sheet = '';
			for (var i = 0; i < set_names.length; i++ ){
				if (set_names[i].NAME == $scope.add_name){
					set_names.splice(i, 1);
				}
			}
		}
		$scope.init();
	    
		//add sheet
		$scope.addSheet = function(add_sheet) {
			console.log(JSON.stringify($scope.set_names));
			//check duplication (sheet)
			for (var i = 0; i <$scope.Sheets.length; i++ ){
				if ($scope.add_sheet == $scope.Sheets[i].SHEET_IDX) {
					$scope.showErrorMsg('工作表不可重複');
				    return;
				}
			}
			//check duplication (set_name)
			for (var i = 0; i <$scope.set_names.length; i++ ){
				if ($scope.add_name == $scope.set_names[i].NAME) {
					$scope.showErrorMsg('設定名稱不可重複');
				    return;
				}
				
				if ($scope.add_code == $scope.set_names[i].CODE) {
					$scope.showErrorMsg('設定代碼不可重複');
				    return;
				}
			}
			
			var sets = [{TABLE_NAME: '', HAS_HEAD: 0}];
			var datas = [];
			var newSheet = {
				NAME : $scope.add_name,
				CODE : $scope.add_code,
				SHEET_IDX : $scope.add_sheet,
				settings : sets,
				content : datas
			};
			$scope.Sheets.push(newSheet); 
			$timeout(function(){
		          $scope.activeTabIndex = ($scope.Sheets.length - 1);
		    });
			$scope.add_sheet ='';
		};
		
		//remove sheet
		$scope.removeSheet = function (sheet) {
		      var index = $scope.Sheets.indexOf(sheet);
		      $scope.Sheets.splice(index, 1);
		};

		//sort
		$scope.sorttableOptions = {
				stop: function(e, ui) {
					angular.forEach(ui.item.sortable.droptargetModel, function(row, index){
						row.ORDER_NO = index + 1;
					});
				}
		};
		
	    //add
	    $scope.add = function(sheet) {
	        sheet.content.push({
	        	SRC_COL_IDX: '',
	        	TAR_COL_NAME: '',
	        	TAR_DATA_TYPE: ''
	        });
	    };
	    
	    //delete
		$scope.del = function(sheet, row) {
			var index = sheet.content.indexOf(row);
			sheet.content.splice(index,1);

	    };
	    
	    //save
	    $scope.save = function(){
	    	// check duplication (set_name)
	    	for (var i = 0; i <set_names.length; i++ ){
				if ($scope.add_name == set_names[i].NAME) {
					$scope.showErrorMsg('設定名稱不可重複');
				    return;
				}
				
				if ($scope.add_code == set_names[i].CODE) {
					$scope.showErrorMsg('設定代碼不可重複');
				    return;
				}
			}
	    	// check null
	    	if($scope.Sheets.length == 0) {
	    		$scope.showErrorMsg('ehl_01_crm911_001');
	    		return;
	    	}
	    	var check = false;
	    	angular.forEach($scope.Sheets, function(row, index){
	    		//set set_name
	    		row.NAME = $scope.add_name;
	    		if(row.content.length == 0) {
	    			check = true;
	    		}
				
			});
	    	if(check) {
	    		$scope.showErrorMsg('ehl_01_crm911_002');
	    		return;
	    	}
	    	
	    	//save
	    	$scope.sendRecv(
	    			"IMPORTFILE", 
	    			"save",
	    			"com.systex.jbranch.app.server.fps.importfile.IMPORTFILEInputVO",
	    			{'set_name':$scope.add_name, 'set_code':$scope.add_code, 'list': $scope.Sheets},
	    			function(totas, isError) {
	                	if (isError) {
	                		$scope.showErrorMsgInDialog(totas.body.msgData);
	        				return;
	                	}
	                	if (totas.length > 0) {
	                		$scope.showMsg('儲存成功');
		                	$scope.closeThisDialog('successful');
	                	};
					}
			);
		};
		
});