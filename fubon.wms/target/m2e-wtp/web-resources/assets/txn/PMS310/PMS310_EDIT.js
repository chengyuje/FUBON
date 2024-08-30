/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS310_EDITController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS310_EDITController";
	
		$scope.mappingSet['tarType'] = [];
		$scope.mappingSet['tarType'].push({LABEL:'PS業績目標', DATA:'1'},{LABEL:'分行業績目標', DATA:'2'});
		var NowDate = new Date();
        var yr = NowDate.getFullYear();
        var mm = NowDate.getMonth()+2;
        var strmm='';
        $scope.mappingSet['timeE'] = [];
        for(var i=0; i<12; i++){
        	mm = mm -1;
        	if(mm == 0){
        		mm = 12;
        		yr = yr-1;
        	}
        	if(mm<10)
        		strmm = '0' + mm;
        	else
        		strmm = mm;        		
        	$scope.mappingSet['timeE'].push({
        		LABEL: yr+'/'+strmm,
        		DATA: yr +''+ strmm
        	});        
        }
		
		$scope.init = function(){
			$scope.inputVO = {
					reportDate: $scope.input.reportDate,										
					tarType: $scope.input.tarType,
					branch_nbr: $scope.input.branch_nbr,
					emp_id: $scope.input.emp_id
        	};						    		    		
        };
        $scope.init();
        
        /**upload csv files**/
		$scope.uploadFinshed = function(name, rname) {
        	if(name){
        		$scope.inputVO.fileName = name;
        	}
        };
 	    
        /**上傳檔案**/
        $scope.save = function() {        	
        	if($scope.parameterTypeEditForm.$invalid || $scope.inputVO.fileName == undefined){
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位＊');
        		return;
        	}  
	        $confirm({text: '上傳檔案會將原查詢結果相同的['+($filter('filter')($scope.mappingSet['tarType'], $scope.inputVO.tarType))[0].LABEL+']資料清除!! 確定要上傳嗎?'}, {size: 'sm'}).then(function() {
	             $scope.checkUploadFiles();
	        });	                			
	                	
        };
        
        /**檢查檔案內的PS員編以及分行是否存在**/
        $scope.checkUploadFiles = function(){
        	$scope.sendRecv("PMS310", "queryPsNbr", "com.systex.jbranch.app.server.fps.pms310.PMS310InputVO", $scope.inputVO,
        			function(totas, isError) {
        		if (isError) {        			
        			$scope.showErrorMsg(totas[0].body.msgData);
        		}        		
        		if (totas.length > 0) {
        			$scope.inputVO.NBR_state = totas[0].body.NBR_state;
//    				$scope.inputVO.branch_nbr = totas[0].body.BRANCH_NBR;
    				$scope.inputVO.PS_state = totas[0].body.PS_state;
//    				$scope.inputVO.PSEmpId = totas[0].body.PSEmpId;
//    				$scope.inputVO.YEARMON = totas[0].body.YEARMON;
    				
    				if($scope.inputVO.NBR_state == 0){
    					$confirm({text: 'PS員編"'+totas[0].body.PSEmpId+'"於"'+totas[0].body.YEARMON+'"的分行代碼"'
    						+totas[0].body.BRANCH_NBR+'"內資料不存在!是否繼續上傳?'}, {size: 'sm'}).then(function() {
    	       	             $scope.uploadFiles();
    					});
    				}
    				if($scope.inputVO.PS_state == 0){
    					$confirm({text: 'PS員編'+totas[0].body.PSEmpId+'不存在!是否繼續上傳?'}, {size: 'sm'}).then(function() {
   	       	             $scope.uploadFiles();
    					});
    				}
    				if($scope.inputVO.NBR_state != 0 && $scope.inputVO.PS_state != 0)
    					$scope.uploadFiles();
       	        };
        		});
        	}; 
        
        
        /**檔案新增至DB**/
        $scope.uploadFiles = function(){       
        	$scope.sendRecv("PMS310", "insertCSVFile", "com.systex.jbranch.app.server.fps.pms310.PMS310InputVO", $scope.inputVO,
        			function(totas, isError) {
        		if (isError) {        			
        			$scope.showErrorMsg(totas[0].body.msgData);
        		}        		
        		if (totas.length > 0) {
        			$scope.showSuccessMsg('ehl_01_common_004');
        			$scope.closeThisDialog('successful');
        		};
        	});        	
        };
        
        /**刪除DB中已存在的資料並寫入新資料**/
        $scope.delDataAddNew = function(){        	
        	$scope.sendRecv("PMS310", "delData", "com.systex.jbranch.app.server.fps.pms310.PMS310InputVO", $scope.inputVO,
					function(totas, isError) {
				if (isError) {					
					$scope.showErrorMsg(totas[0].body.msgData);
				}else{
					$scope.uploadFiles();
				}				
			});
        }
            	
});
