'use strict';
eSoafApp.controller('PMS712Controller', function($rootScope, $scope,
		$controller, $confirm, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "PMS712Controller";

	$scope.init = function() {
		$scope.inputVO = {
			modelName : ''
		};
		$scope.resultList = [];
		$scope.ifShow0 = false;
	};
	$scope.init();
	
	//初始化獲取所有的上傳文件格式模板
	$scope.modelName = function() {
		$scope.sendRecv("PMS712", "getModel", "com.systex.jbranch.app.server.fps.pms712.PMS712InputVO", $scope.inputVO,
				function(totas, isError) {
                	if (isError) {
                		$scope.showErrorMsg(totas[0].body.msgData);
                	}
                	if (totas.length > 0) {
                		$scope.mappingSet['modelName'] = [];
                		angular.forEach(totas[0].body.resultList, function(row, index, objs){
        				$scope.mappingSet['modelName'].push({LABEL: row.UPLOAD_ID,DATA: row.UPLOAD_ID});
                		});
                	}
				}
        )};	
    $scope.modelName();
    
    //下載上傳文件格式模板
    $scope.download = function(){
		$scope.sendRecv("PMS712", "downloadModel", "com.systex.jbranch.app.server.fps.pms712.PMS712InputVO",$scope.inputVO, 
				function(tota, isError) {
						if (!isError) {
							return;
						}
		});
    }

	//查看上傳文件格式模板
	$scope.query = function() {
		$scope.sendRecv("PMS712", "queryModel",
				"com.systex.jbranch.app.server.fps.pms712.PMS712InputVO",$scope.inputVO, 
				function(tota, isError) {
					if (!isError) {
						if (tota[0].body.resultList == null
								|| tota[0].body.resultList.length == 0) {
							$scope.resultList = [];
							$scope.showMsg("ehl_01_common_009");
							return;
						}
						$scope.resultList = [];
						$scope.resultList = tota[0].body.resultList;
						$scope.outputVO = tota[0].body;
						$scope.ifShow0 = true;
						$scope.ifShow();
					}
				});
	}
	
	//是否顯示列名，列類型
	$scope.ifShow = function(){
		$scope.ifShow1  = false;
		$scope.ifShow2  = false;
		$scope.ifShow3  = false;
		$scope.ifShow4  = false;
		$scope.ifShow5  = false;
		$scope.ifShow6  = false;
		$scope.ifShow7  = false;
		$scope.ifShow8  = false;
		$scope.ifShow9  = false;
		$scope.ifShow10 = false;
		$scope.ifShow11 = false;
		$scope.ifShow12 = false;
		$scope.ifShow13 = false;
		$scope.ifShow14 = false;
		$scope.ifShow15 = false;
		$scope.ifShow16 = false;
		$scope.ifShow17 = false;
		$scope.ifShow18 = false;
		$scope.ifShow19 = false;
		$scope.ifShow20 = false;
		if($scope.resultList[0].C1NAME!=''&&$scope.resultList[0].C1NAME!=null){
		$scope.ifShow1 = true;
		}
		if($scope.resultList[0].C2NAME!=''&&$scope.resultList[0].C2NAME!=null){
			$scope.ifShow2 = true;
		}
		if($scope.resultList[0].C3NAME!=''&&$scope.resultList[0].C3NAME!=null){
			$scope.ifShow3 = true;
		}
		if($scope.resultList[0].C4NAME!=''&&$scope.resultList[0].C4NAME!=null){
			$scope.ifShow4 = true;
		}
		if($scope.resultList[0].C5NAME!=''&&$scope.resultList[0].C5NAME!=null){
			$scope.ifShow5 = true;
		}
		if($scope.resultList[0].C6NAME!=''&&$scope.resultList[0].C6NAME!=null){
			$scope.ifShow6 = true;
		}
		if($scope.resultList[0].C7NAME!=''&&$scope.resultList[0].C7NAME!=null){
			$scope.ifShow7 = true;
		}
		if($scope.resultList[0].C8NAME!=''&&$scope.resultList[0].C8NAME!=null){
			$scope.ifShow8 = true;
		}
		if($scope.resultList[0].C9NAME!=''&&$scope.resultList[0].C9NAME!=null){
			$scope.ifShow9 = true;
		}
		if($scope.resultList[0].C10NAME!=''&&$scope.resultList[0].C10NAME!=null){
			$scope.ifShow10 = true;
		}
		if($scope.resultList[0].C11NAME!=''&&$scope.resultList[0].C11NAME!=null){
			$scope.ifShow11 = true;
		}
		if($scope.resultList[0].C12NAME!=''&&$scope.resultList[0].C12NAME!=null){
			$scope.ifShow12 = true;
		}
		if($scope.resultList[0].C13NAME!=''&&$scope.resultList[0].C13NAME!=null){
			$scope.ifShow13 = true;
		}
		if($scope.resultList[0].C14NAME!=''&&$scope.resultList[0].C14NAME!=null){
			$scope.ifShow14 = true;
		}
		if($scope.resultList[0].C15NAME!=''&&$scope.resultList[0].C15NAME!=null){
			$scope.ifShow15 = true;
		}
		if($scope.resultList[0].C16NAME!=''&&$scope.resultList[0].C16NAME!=null){
			$scope.ifShow16 = true;
		}
		if($scope.resultList[0].C17NAME!=''&&$scope.resultList[0].C17NAME!=null){
			$scope.ifShow17 = true;
		}
		if($scope.resultList[0].C18NAME!=''&&$scope.resultList[0].C18NAME!=null){
			$scope.ifShow18 = true;
		}
		if($scope.resultList[0].C19NAME!=''&&$scope.resultList[0].C19NAME!=null){
			$scope.ifShow19 = true;
		}
		if($scope.resultList[0].C20NAME!=''&&$scope.resultList[0].C20NAME!=null){
			$scope.ifShow20 = true;
		}
	}
	
	//選擇了上傳模板後才可以進行查看與下載操作
    $scope.checkModel = function(){
    	var check = true;
    	if($scope.inputVO.modelName != ''&&$scope.inputVO.modelName != undefined){
    		check = false;
    	}
    	return check;
    }
});