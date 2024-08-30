/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('KYC320_KYCebankController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "KYC320_KYCebankController";
		
		$scope.init = function(){
			$scope.inputVO = {	
				KYCcustID : "",
				KYCcreateDate : ""
        	};		
			/*
			$scope.outputVO={
					resultList:[],
					list:[],
					rolelist:[],
					AoCntLst:[],
					KYCList:[]
			};*/
		
		};		
		$scope.init();	
		
		//英文字母轉大寫
		$scope.text_toUppercase = function(text,type){
			var toUppercase_text = text.toUpperCase();
			switch (type) {
			case 'CUST_ID':
				$scope.inputVO.KYCcustID= toUppercase_text;
				break;
			default:
				break;
			}
		}
		
		
		//時間
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
		
		$scope.queryKYC = function(){
			var id = $scope.inputVO.KYCcustID;
			var cd = $scope.inputVO.KYCcreateDate;

			if(id == "" || id == null || cd == "" || cd == null){

				$scope.showErrorMsg("客戶ID及建立日期都須填寫");
			}else{
			
				var y = $scope.inputVO.KYCcreateDate.getFullYear();
				var m = $scope.inputVO.KYCcreateDate.getMonth()+1;
				var d = $scope.inputVO.KYCcreateDate.getDate();
			
				cd = y.toString() + (m>9 ? '' : '0') + m.toString() + (d>9 ? '' : '0') + d.toString();
				$scope.inputVO.KYCcreateDate = cd;
			
				$scope.sendRecv("KYC320", "inquireKYC", "com.systex.jbranch.app.server.fps.kyc320.KYC320InputVO", $scope.inputVO ,
						function(tota, isError) {
							if (!isError) {
								var dialog = ngDialog.open({
									 template:  'assets/txn/KYC320/KYC320_KYCebankPrint.html',
									 className : "KYC320_KYCebank",  //
									 controller: ['$scope', function($scope) {
										 	
											$scope.KYCList = tota[0].body.KYCList;  
											$scope.outputVO = tota[0].body;
											
											if(tota[0].body.KYCList == 0) {
												$scope.showMsg("ehl_01_common_009");
												$scope.closeThisDialog('successful');
						            		}
									 }]
								});
								$scope.closeThisDialog('successful');
							}else{
								$scope.showErrorMsg("查詢失敗");
							}				
							
					});
			}
				
		};
		
		$scope.exportKYC = function(){
			$scope.sendRecv("KYC320", "export", "com.systex.jbranch.app.server.fps.kyc320.KYC320OutputVO", $scope.outputVO,
			function(tota, isError){						
				if(!isError){
					$scope.closeThisDialog('successful');
					
				}else{
					$scope.showErrorMsg("匯出錯誤");
				}
					
			
			});
		};   
       	
});
