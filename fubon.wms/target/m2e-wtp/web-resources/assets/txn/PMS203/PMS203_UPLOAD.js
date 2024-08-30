/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS203_UPLOADController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$controller('PMS203Controller', {$scope: $scope});
		$scope.controllerName = "PMS203_UPLOADController";
		
		if($scope.tp == 'SET')
			$scope.rptTitle = '理專職級設定';
		else if($scope.tp == 'TAR')
			$scope.rptTitle = '理專生產力目標';
		else if($scope.tp == 'INS')
			$scope.rptTitle = '分行投保計績收益目標';
		else if($scope.tp == 'SALE')
			$scope.rptTitle = '分行投保計績銷量目標';
		else if($scope.tp == 'PRD')
			$scope.rptTitle = '理專追蹤商品目標';
		
		$scope.init = function(){
			$scope.inputVO = {
				reportDate: $scope.ym,
				tgtType: $scope.tp
        	};
			$scope.confirm = false;
        };
        $scope.init();
        
        $scope.inquireInit = function(){
			$scope.initLimit();
			$scope.paramList = [];
		}
		$scope.inquireInit();
        
		/**download sample files**/
		$scope.downloadSample = function() {	
        	$scope.sendRecv("PMS203", "downloadSample", "com.systex.jbranch.app.server.fps.pms203.PMS203InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							return;
						}
			});
		};
		
		 /**upload csv files**/
		$scope.uploadFinshed = function(name, rname) {
        	if(name){
        		$scope.inputVO.fileName = name;
        		$scope.confirm = true;
        	}else
        		$scope.confirm = false;
        };
        
        $scope.checkCSVFile = function(){
        	if($scope.tp == 'INS'){
        		$scope.sendRecv("PMS203","checkBRANCH_NBR","com.systex.jbranch.app.server.fps.pms203.PMS203InputVO",$scope.inputVO,
        				function(totas,isError){
        			if(isError){
        				$scope.showErrorMsg(totas[0].body.msgData);
        			}
        			if(totas.length > 0){
        				$scope.inputVO.NBR_state = totas[0].body.NBR_state;
        				$scope.inputVO.branch_nbr = totas[0].body.BRANCH_NBR;
        				if($scope.inputVO.NBR_state == 0){
        					$confirm({text: '分行代碼'+$scope.inputVO.branch_nbr+'不存在!是否繼續上傳?'}, {size: 'sm'}).then(function() {
        	       	             $scope.save();
        					});
        				}else{
        					$scope.save();
        				}
        			}
        		});
        	}else{
        		$scope.save();
        	}
        }
        
        /** CSV 檔寫入資料庫 **/
        $scope.save = function(){
			debugger
        	if($scope.inputVO.reportDate == undefined){				
				$scope.showErrorMsg('報表年月欄位輸入後，才能上傳檔案！');
				return;		
			}
        	$scope.sendRecv("PMS203", "insertCSVFile", "com.systex.jbranch.app.server.fps.pms203.PMS203InputVO", $scope.inputVO,
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
});
