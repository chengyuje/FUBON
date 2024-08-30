/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('INS440Controller',
	function($rootScope, $scope, $controller, $confirm, sysInfoService, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "INS440Controller";
		
		$scope.mapping = {};
		$scope.defaultVal = {};
		 
//		 $scope.init();
        // 勞保btn.國民年金btn.公保btn.勞工退休金btn.公職退休金btn.公職退休金btn
        getParameter.XML(['INS.INSURANCE_REF_LINK'], function (totas) {
            if (totas) {
            	$scope.mapping.links = totas.data[totas.key.indexOf('INS.INSURANCE_REF_LINK')].map(function (link) {
                return link.LABEL;
                });
                // console.log($scope.mapping.links);
            }
        });
        
        $scope.inquire = function(){
        	console.log($scope.inputVO);
        	$scope.sendRecv("INS450","retiredInit","com.systex.jbranch.app.server.fps.ins450.INS450InputVO",$scope.inputVO,
        			function(tota,isError){
		        		if(!isError){
//		        			var othList = tota[0].body.outputList[0];
		        			if(tota[0].body.outputList.length > 0){
			        			for (var i = 0; i < tota[0].body.outputList.length; i++) {
			        				console.log(tota[0].body);
			        				if(tota[0].body.outputList[i].TYPE =='A'){
			        					console.log(tota[0].body.outputList[i].ARTL_DEBT_AMT_MONTHLY);
			        					$scope.inputVO.onceTotal = parseInt(tota[0].body.outputList[i].ARTL_DEBT_AMT_MONTHLY,10) || 0;
										$scope.inputVO.othAMT2 = parseInt(tota[0].body.outputList[i].ARTL_DEBT_AMT_ONCE,10) || 0;
			        				}else{
			        					$scope.inputVO.othINSAMT1 = parseInt(tota[0].body.outputList[i].ARTL_DEBT_AMT_ONCE,10) || 0 ;
					        			$scope.inputVO.othINSAMT2 = parseInt(tota[0].body.outputList[i].ARTL_DEBT_AMT_MONTHLY,10) || 0;
			        				}
			        			}
		        			}
		        			console.log(tota[0].body.outputList1.length);
		        			if(tota[0].body.outputList1.length >=1 ){
		        			$scope.inputVO.laborINSAMT1 = tota[0].body.outputList1[0].LABOR_INS_AMT1;
		        			$scope.inputVO.laborINSAMT2 = tota[0].body.outputList1[0].LABOR_INS_AMT2;
		        			$scope.inputVO.person1 = tota[0].body.outputList1[0].PENSION1;
		        			$scope.inputVO.person2 = tota[0].body.outputList1[0].PENSION2;
		        			}
//			        			$scope.inputVO.othINSAMT1 = parseInt(othList.PRD_MONTHLY,10) || 0 ;
//			        			$scope.inputVO.othINSAMT2 = parseInt(othList.PRD_ONCE,10) || 0;
//			        			$scope.inputVO.onceTotal = parseInt(othList.ARTL_DEBT_AMT_MONTHLY,10) || 0;
//			        			$scope.inputVO.othAMT2 = parseInt(othList.ARTL_DEBT_AMT_ONCE,10) || 0;
//			        			$scope.defaultVal = othList;
			        			$scope.sumMonth();
			        			$scope.sumOneTime();
	        			}
        	})
        }
        $scope.totallyMonth = function(){
        	if($scope.inputVO.othINSAMT1 == 0 || $scope.inputVO.othINSAMT1 == ''){
        		$scope.inputVO.othINSAMT1 = $scope.defaultVal.ARTL_DEBT_AMT_ONCE || 0;
        	}

        	if($scope.inputVO.onceTotal == 0 || $scope.inputVO.onceTotal == ''){
        		$scope.inputVO.onceTotal = $scope.defaultVal.ARTL_DEBT_AMT_MONTHLY || 0;
        	}
        	$scope.sumMonth();
        	
        }
        $scope.totalOnce = function(){
        	if($scope.inputVO.othINSAMT2 == 0 || $scope.inputVO.othINSAMT2 == ''){
        		$scope.inputVO.othINSAMT2 = $scope.defaultVal.ARTL_DEBT_AMT_MONTHLY || 0;
        	}
        	if($scope.inputVO.othAMT2 == 0 || $scope.inputVO.othAMT2 == ''){
        		$scope.inputVO.othAMT2 = $scope.defaultVal.ARTL_DEBT_AMT_ONCE || 0;
        	}
        	$scope.sumOneTime();
        }

		$scope.busIns = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/INS430/INS441.html',
				className: 'INS441',
				showClose: false,
                controller: ['$scope', function($scope) {
                }]
			});
			dialog.closePromise.then(function (data) {
//				console.log(data);
				var onceTotal = 0;
				var tempOnceNum = 0;
////				每月
				for (var i = 0; i < data.value.length; i++) {
					var once = parseInt(data.value[i].ARTL_DEBT_AMT_MONTHLY || 0);
					if (once != undefined) {
						onceTotal = onceTotal + once;
					}
				}

				console.log("====" + onceTotal);
//				一次
				for (var i = 0; i < data.value.length; i++) {
					var tempOnce = parseInt(data.value[i].ARTL_DEBT_AMT_ONCE || 0);
					if (tempOnce != undefined) {
						tempOnceNum = tempOnceNum + tempOnce;
					}
				}
				$scope.totallyMonth();
				$scope.totalOnce();
				$scope.inputVO.othINSAMT1 = tempOnceNum;
				$scope.inputVO.othINSAMT2 = onceTotal;
				if(data.value != 'cancel'){
					$scope.connector('set', 'INS442_TOTAL', data.value[0])
				}
				$scope.sumMonth();
				$scope.sumOneTime();
			});
		}
		
		$scope.otherPay = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/INS430/INS442.html',
				className: 'INS442',
				showClose: false,
                controller: ['$scope', function($scope) {
                }]
			});
			dialog.closePromise.then(function (data) {
				console.log(data);
				var onceTotal = 0;
				var tempOnceNum = 0;
				for (var i = 0; i < data.value.length; i++) {
					var once = parseInt(data.value[i].ARTL_DEBT_AMT_MONTHLY || 0);
					if (once != undefined) {
						onceTotal = onceTotal + once;
					}
				}
				$scope.inputVO.onceTotal = onceTotal;
				console.log("====" + onceTotal);
				for (var i = 0; i < data.value.length; i++) {
					var tempOnce = parseInt(data.value[i].ARTL_DEBT_AMT_ONCE || 0);
					if (once != undefined) {
						tempOnceNum = tempOnceNum + tempOnce;
					}
				}
				$scope.totallyMonth();
				$scope.totalOnce();
				$scope.inputVO.othAMT2 = tempOnceNum;
				$scope.inputVO.onceTotal = onceTotal;
				if(data.value != 'cancel'){
					$scope.connector('set', 'INS442_TOTAL', data.value[0])
				}
				$scope.sumMonth();
				$scope.sumOneTime();
			});

		}
		$scope.inquire();

});
