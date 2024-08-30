/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('INS400Controller',
	function($rootScope, $scope, $controller, $confirm, sysInfoService, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "INS400Controller";
		
		getParameter.XML(['KYC.EDUCATION'], function(totas) {
			if(len(totas)>0){
				$scope.mappingSet['KYC.EDUCATION'] = totas.data[totas.key.indexOf('KYC.EDUCATION')];
			}
			 
		});
	    $scope.setPrint = function(type){
	    	$scope.print = type;
	    };
        $scope.arrowPath = {
                up: 'assets/images/ic-up.svg',
                down: 'assets/images/ic-down.svg'
              };
              $scope.arrowUp = true;
  		$scope.chgStep = function (flag) {
	        var subStepNum = flag;
	        for (var i = 0; i < 3; i++) {
	          $scope['isChangeStep' + (i + 1)] = subStepNum > i;
	        }
	      };
		
		/** 分頁 **/
		$scope.pagechose = function(chose){
        	switch(chose){
	        	/* 歷史規劃 */
	        	case 1:
	        		$scope.page_inde = "assets/txn/INS410/INS410.html";
	        		break;
	    		/* 保險規劃建議 */
	        	case 2:
	        		$scope.page_inde = "assets/txn/INS420/INS420.html";
	        		$scope.chgStep(1);
	        }
//        	$scope.setPrint(true);
        	$scope.setPrint(false); //正常狀況下，用此。上面是測試用
		}
		
		/** 查詢初始化 **/
		$scope.inquireInit = function(){
			$scope.custData = [];
			$scope.pagechose(2);
			$scope.activeJustified = 1;
			$scope.isShow = false;
		}
		
		/** 初始化 **/
		$scope.init = function(){
//			$scope.inputVO = {};
			$scope.inputVO.cust_id = ''
			$scope.inquireInit();
		}

		
		/** 查詢客戶 **/
		$scope.inquire = function(){
			$scope.inquireInit();
			if ($scope.inputVO.cust_id == '') {
				$scope.connector('set','INS400_cust_id', '');
				$scope.showErrorMsg("ehl_01_KYC310_004");	//請輸入客戶ID
				$scope.isShow = false;
				return;
			}else{
				$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase();
				$scope.connector('set','INS400_cust_id', $scope.inputVO.cust_id);
			}
			
			$scope.sendRecv("INS400", "inquire", "com.systex.jbranch.app.server.fps.ins400.INS400InputVO", {'cust_id':$scope.inputVO.cust_id},
					function(tota, isError) {
						if(tota[0].body.resultList.length > 0){
							$scope.custData = tota[0].body.resultList[0];
							
							//客戶註記：禁銷戶只要顯示"NS"(COMM_NS_YN)、拒銷戶只要顯示"RS"(COMM_RS_YN)、特定客戶(SP_CUST_YN)要顯示"特定客戶"
							$scope.cust_note = "";
							if($scope.custData.COMM_NS_YN == "Y"){
								$scope.cust_note = $scope.cust_note + "NS/";
							}
							if($scope.custData.COMM_RS_YN == "Y"){
								$scope.cust_note = $scope.cust_note + "RS/";
							}
							if($scope.custData.SP_CUST_YN == "Y"){
								$scope.cust_note = $scope.cust_note + "特定客戶/";
							}
							if($scope.cust_note != ""){
								$scope.cust_note = $scope.cust_note.substring(0,$scope.cust_note.length-1);
							}
							$scope.isShow = true;
						}else{
							$scope.showErrorMsg('ehl_01_cus130_002',[$scope.inputVO.cust_id]);		//無此客戶ID：{0}
							return;
						}
						
					}
			);
		}
		
		$scope.goINS430 = function(index){
			console.log($scope.custData);
			if ($scope.inputVO.cust_id == '' || $scope.inputVO.cust_id == undefined) {
				$scope.connector('set','INS400_cust_id', '');
				$scope.showErrorMsg("ehl_01_KYC310_004");	//請輸入客戶ID
				$scope.isShow = false;
				return;
			}else if($scope.custData == ''){
				$scope.showErrorMsg('ehl_01_cus130_002',[$scope.inputVO.cust_id]);		//無此客戶ID：{0}
				return;				
			}
			$scope.hisPlan = "";
			switch(index){
				case 1: 
					$scope.hisPlan = "子女教育";
					break;
				case 2:
					$scope.hisPlan = "退休規劃";
					break;
				case 3:
					$scope.hisPlan = "購屋";
					break;
				case 4:
					$scope.hisPlan = "購車";
					break;
				case 5:
					$scope.hisPlan = "結婚";
					break;
				case 6:
					$scope.hisPlan = "留遊學";
					break;
				case 7:
					$scope.hisPlan = "旅遊";
					break;
				case 8:
					$scope.hisPlan = "其他";
					break;
			}
//			$scope.hisPlanURL = 'assets/txn/INS430/INS430.html';
			$scope.page_inde = 'assets/txn/INS430/INS430.html';
		}
		
		$scope.myFunct = function(keyEvent) {
			  if (keyEvent.which === 13)
				  $scope.inquire();
		}

		$scope.chgStep(1);
		$scope.init();
		if($scope.connector('get','INS400')){
			$scope.inputVO.cust_id = $scope.connector('get','INS400').custID;
			$scope.inquire();
		}

});
