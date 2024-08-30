'use strict';
eSoafApp.controller('PMS711_houNewSetTSController', function($rootScope, $scope,
		$controller, $confirm, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "PMS711_houNewSetTSController";
	$scope.mappingSet['TS'] = [];
	$scope.mappingSet['TS'].push(
			{LABEL: '非購屋', DATA: 1},
			{LABEL: '額度式', DATA: 2}
			);
	$scope.init = function(){
		$scope.inputVO = {
				date_year : $scope.date_year
		};
		if($scope.subProjectSeqId=='12'){
			$scope.inputVO.tsType ='0';
		}
		else if($scope.subProjectSeqId=='25'){
			$scope.inputVO.tsType ='1';
		}
	};
	$scope.init();
	$scope.TSChange = function () {
		$scope.showList = [];
		$scope.queryHouNewTS();
	}
	$scope.inquireInit = function(){
		$scope.showList = [];
	}
	$scope.inquireInit();
    //查詢T/S設定
    $scope.queryHouNewTS = function(){
    	$scope.inputVO.personType = $scope.personType;
    	$scope.inputVO.subProjectSeqId = $scope.subProjectSeqId;
        $scope.paramList = [];
		$scope.sendRecv("PMS711", "queryHouNewTS", "com.systex.jbranch.app.server.fps.pms711.PMS711InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.showList.length == 0) {
							$scope.ifShow = false;
							$scope.showMsg("ehl_01_common_009");
                			return;
                		}
						$scope.showList =  tota[0].body.showList;;
						$scope.outputVO = tota[0].body;
						return;
					}
		});
    }
    $scope.queryHouNewTS();
    
  //校驗輸入數據不能為空
	$scope.checkNull = function(){
		$scope.inputVO.showList = $scope.showList;
		for (var i = 0; i < $scope.inputVO.showList.length; i++) {
			if (String($scope.inputVO.showList[i].CEN_LONG_BUY) == "") {
				$scope.showMsg("請檢驗各個欄位，輸入不能為空！");
				return;
			}
		}
		$scope.save();
	}
	//储存页面输入数据
	$scope.save = function() {
		$scope.inputVO.date_year = $scope.date_year;
		$scope.inputVO.personType = $scope.personType;
		$scope.inputVO.subProjectSeqId = $scope.subProjectSeqId;
		$scope.sendRecv("PMS711", "saveHonNewTs",
				"com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",
				$scope.inputVO, function(tota, isError) {
					if (!isError) {
						$scope.showMsg("ehl_01_common_002");
					}
					else
					{
						$scope.showMsg("ehl_01_common_007");
					}
				});
	}
	
    $scope.uploadTS = function() {
    	var date_year = $scope.date_year;
		var personType = $scope.personType;
		var subProjectSeqId = $scope.subProjectSeqId;
		var tsType = $scope.inputVO.tsType;
		
		var dialog = ngDialog.open({
			template: 'assets/txn/PMS711/PMS711_UPLOAD.html',
			className: 'PMS711_UPLOAD',
			showClose : true,
    		controller: ['$scope', function($scope) {
				$scope.date_year = date_year,
				$scope.personType = personType,
				$scope.subProjectSeqId = subProjectSeqId,
				$scope.tsType = tsType;
            }]
		});
		
		dialog.closePromise.then(function(data) {
    		if(data.value == 'cancel'){
    			 $scope.queryHouNewTS();
				}
    	});
	}
});