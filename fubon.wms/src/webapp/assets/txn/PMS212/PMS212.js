'use strict';
eSoafApp.controller('PMS212Controller', function($rootScope, $scope,
		$controller, $confirm, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "PMS212Controller";

	$scope.init = function() {
		$scope.currentPageIndex = '';
		$scope.inputVO = {
				userId  :projInfoService.getUserID()
		};
		$scope.largeAgrList = [];
	};
	$scope.init();
	
	/** 上傳數據窗口彈出 **/
	$scope.adddata = function(inputVO){
		var dialog = ngDialog.open({
			template: 'assets/txn/PMS212/PMS212_UPLOAD.html',
			className: 'PMS212_UPLOAD',
			showClose: false,
			controller: ['$scope', function($scope) {
				$scope.custID   = inputVO.custID;
            }]
		});
	}
	
	/** 查詢 * */
	$scope.query = function() {
		if($scope.parameterTypeEditForm.$invalid){
    		$scope.showMsg('欄位檢核錯誤:客戶ID必填');
    		return;
    	}
		$scope.sendRecv("PMS212", "queryData",
				"com.systex.jbranch.app.server.fps.pms212.PMS212InputVO",
				$scope.inputVO, function(tota, isError) {
					if (!isError) {
						if (tota[0].body.largeAgrList == null
								|| tota[0].body.largeAgrList.length == 0) {
							$scope.largeAgrList = [];
							$scope.showMsg("ehl_01_common_009");
							return;
						}
						/*for(var i=0;i<$(".cb").length;i++){
							$(".cb")[i].checked = false;
						}*/
						$scope.inputList = [];
						$scope.largeAgrList = tota[0].body.largeAgrList;
						$scope.resultList   = tota[0].body.resultList;
						$scope.outputVO = tota[0].body;
						$scope.currentPageIndex = tota[0].body.currentPageIndex;
					}
				});
	}
	
	
	/*初始化ADJ为1时checkbox勾选，點選checkbox勾选狀態*/
	$scope.changeCheck = function(index){
		if($scope.largeAgrList[index].ADJ=='0')
			$scope.largeAgrList[index].ADJ='1'
		else if($scope.largeAgrList[index].ADJ=='1')
			$scope.largeAgrList[index].ADJ='0'
	}
	
	/*內容改變checkbox勾选*/
	$scope.choose = function(ROWNUM){
		var rownum = (ROWNUM-1)%10;
		//$(".cb")[rownum].checked = true;
		$scope.largeAgrList[rownum].ADJ='1'
	}
	
	/*批量更新*/
	$scope.update = function(){
		
		for(var i=0;i<$(".cb").length;i++){
			if($(".cb")[i].checked==true){
				if($scope.largeAgrList[i].ADJAOCODE==null||$scope.largeAgrList[i].ADJAOCODE==''){
					$scope.showMsg('欄位檢核錯誤:欄位調整後AOCODE必輸');
					return;
				}
				if($scope.largeAgrList[i].ADJAOCODE==$scope.largeAgrList[i].AOCODE){
					$scope.showMsg('欄位檢核錯誤:欄位調整後AOCODE不可與原AOCODE相同');
					return;
				}
			}
			$scope.inputList[i] = $scope.largeAgrList[i];
		}
		
		/*if($scope.inputList==0){
			$scope.showMsg("沒有勾選數據");
			return;
		}*/
		
		$scope.inputVO.inputList = $scope.inputList;
		$scope.sendRecv("PMS212", "updateData", "com.systex.jbranch.app.server.fps.pms212.PMS212InputVO", $scope.inputVO,
				function(tota, isError) {
					if (tota[0].body.errorMessage!==null || tota[0].body.errorMessage!=='') {
						$scope.showErrorMsg(tota[0].body.errorMessage);
					}else{
						$scope.showMsg("ehl_01_common_006");
						$scope.inputVO.currentPageIndex = $scope.currentPageIndex;
						$scope.query();
					}
				}
		);
		
	}

});