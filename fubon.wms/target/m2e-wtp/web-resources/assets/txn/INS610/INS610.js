/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('INS610Controller',
	function($rootScope, $scope, $controller, $confirm, sysInfoService, socketService, ngDialog, projInfoService, getParameter, $q) {
		$controller('BaseController', {$scope: $scope});
		$controller('RegionController', {$scope: $scope});
		$scope.controllerName = "INS610Controller";
		
		var log = console.log;
		$scope.isFC = sysInfoService.getRoleName().substr(0,2) == 'FC';
		
		//xml參數初始化
//		getParameter.XML(["INS.STATUS", "INS.SPP_TYPE", "INS.PLAN_TYPE"], function(totas) {
//			if (totas) {
//				$scope.mappingSet['INS.STATUS'] = totas.data[totas.key.indexOf('INS.STATUS')];
//			}
//		});
//		debugger
		
        // date picker
        // 活動起迄日
        $scope.sDateOptions = {};
		$scope.eDateOptions = {};
		
		// date picker end
		$scope.nowDate = new Date();
		$scope.nowDate.setHours(0, 0, 0, 0);
		$scope.eDateOptions.maxDate = $scope.nowDate;
		$scope.sDateOptions.maxDate = $scope.nowDate;
		
		// 最大日限制 : 今天
		// 起訖日 相隔最多三個月
		$scope.limitDate = function() {
			if($scope.inputVO.ED != undefined) {
				var tmpED = new Date($scope.inputVO.ED);
				tmpED.setMonth(tmpED.getMonth()-3);
				$scope.sDateOptions.minDate = tmpED;
			}
			if($scope.inputVO.SD != undefined) {
				var tmpSD = new Date($scope.inputVO.SD);
				tmpSD.setMonth(tmpSD.getMonth()+3);
				tmpSD = (tmpSD > $scope.nowDate) ? $scope.nowDate : tmpSD;
				$scope.eDateOptions.maxDate = tmpSD;
			}
			$scope.sDateOptions.maxDate = $scope.inputVO.ED || $scope.nowDate;
			$scope.eDateOptions.minDate = $scope.inputVO.SD || undefined;
//			log('s:Max->'+$scope.sDateOptions.maxDate);
//			log('s:Min->'+$scope.sDateOptions.minDate);
//			log('e:Max->'+$scope.eDateOptions.maxDate);
//			log('e:Min->'+$scope.eDateOptions.minDate);
		};
		
		$scope.queryInit = function() {
			$scope.resultList = [];
        	$scope.outputVO = {};
        	$scope.data = [];
		}
		
		$scope.initial = function() {
			//組織連動
			$scope.inputVO.branchAreaId = ("null" == projInfoService.getAreaID() ? "" : projInfoService.getAreaID());
	        $scope.inputVO.branchId = ("000" == projInfoService.getBranchID() ? "" : projInfoService.getBranchID());
	        $scope.region = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
	        $scope.RegionController_setName($scope.region);
	        
			$scope.queryInit();
        	$scope.planRatio = undefined;
        	
        	$scope.inputVO.custId = undefined;
        	$scope.inputVO.insPrdId = undefined;
        	$scope.inputVO.SD = undefined;
        	$scope.inputVO.ED = undefined;
        	$scope.inputVO.status = undefined;
        	$scope.inputVO.custName = undefined;
        	$scope.inputVO.APPLY_DATE = new Date();
        	$scope.inputVO.empId = $scope.isFC ? projInfoService.getUserID() : undefined;
        };
		
		/* helping function */
        var dateConvert = function (date, b, c, time) {
            date = date.toString();
            date = time ? date : date.split(' ')[0];
            return date.replace(new RegExp(b, 'g'), c);
        };

        $scope.open = function ($event, dataPicker, parent) {
            $event.preventDefault();
            $event.stopPropagation();
            if (parent === undefined) {
                $scope[dataPicker] = true;
            } else {
                parent[dataPicker] = true;
            }
        };
		
        //確認險種代號是否與險種名稱一致
		$scope.checkCNCTData = function(){
			if($scope.check_insCode != $scope.inputVO.insCode){
				$scope.INSPRD_Lock = false;
				$scope.inputVO.cnctName = undefined;
				$scope.inputVO.currCd = undefined;
				$scope.inputVO.insAnnual = undefined;
				$scope.inputVO.payType = undefined;
				$scope.inputVO.exRatech = undefined;
				$scope.inputVO.prdRate = undefined;
				$scope.inputVO.cnrRate = undefined;
				$scope.inputVO.AB_EXCH_RATE = undefined;
				$scope.inputVO.PRODUCT_TYPE = undefined;
			}
		}
        
		//主約險種資料查詢(待PM專區完成後才能開發)
		$scope.Ins_query = function(){
				var INSPRD_ID=$scope.inputVO.insCode
				var dialog = ngDialog.open({
					template: 'assets/txn/IOT910/IOT910.html',
					className: 'IOT910',
					controller:['$scope',function($scope){
						if(INSPRD_ID!=undefined){
							$scope.INSPRD_ID = INSPRD_ID.toUpperCase();
						}else{
							$scope.INSPRD_ID = INSPRD_ID;
						}
					}]
				});
				dialog.closePromise.then(function(data) {
//					$scope.cnctdataList = $scope.connector('get','IOT910');
					if(data.value != undefined){
						if(data.value != 'cancel'){
							//證照類型
							$scope.inputVO.CERT_TYPE = data.value.CERT_TYPE;
							//教育訓練
							$scope.inputVO.TRAINING_TYPE = data.value.TRAINING_TYPE;
							//招攬人員員編
							$scope.inputVO.EMP_ID = $scope.inputVO.empId;
							$scope.inputVO.insCode = data.value.INSPRD_ID;
						}
					}
				});
				
		}
        
        // 取得使用者資訊	
//		$scope.getUserInfo = function(in_column){
//			if($scope.inputVO.custId != undefined && $scope.inputVO.custId != ''){
//				$scope.inputVO.in_column = in_column;
//				$scope.inputVO.CUST_ID = $scope.inputVO.custId.toUpperCase();
//				$scope.sendRecv("IOT920","getCUSTInfo","com.systex.jbranch.app.server.fps.iot920.IOT920InputVO", $scope.inputVO,
//				function(tota,isError){
//					if(!isError) {
//						if(tota[0].body.CUST_NAME != null) {
//							if(tota[0].body.CUST_NAME.length>0) {
//								if($scope.inputVO.custId !='') {
//									$scope.inputVO.custName = tota[0].body.CUST_NAME[0].CUST_NAME;
//								}
//							} else {
//								$scope.showErrorMsg("ehl_02_SOT_001");		//客戶ID輸入錯誤，或此客戶並非本行客戶
//								$scope.inputVO.custId = undefined ;
//								$scope.inputVO.custName = undefined ;
//							}
//						}
//					} else {
//						if($scope.inputVO.in_column == 'CUST'){
//							$scope.inputVO.custId = undefined ;
//							$scope.inputVO.custName = undefined ;
//						}
//					}
//				});
//			}
//		}
		
		$scope.getCustName = function(){
			$scope.sendRecv("INS610", "getCustName", "com.systex.jbranch.app.server.fps.ins610.INS610InputVO", {'custId': $scope.inputVO.custId},
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length > 0){
								$scope.inputVO.custName = tota[0].body.resultList[0].CUST_NAME;								
							} else {
								$scope.inputVO.custName = undefined;
							}
						}
			});
		}
        
        $scope.query = function() {
        	if($scope.inputVO.insCode != undefined && $scope.inputVO.insCode != ''){
        		$scope.inputVO.insCode = $scope.inputVO.insCode.toUpperCase();
        	}
        	
        	if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg("ehl_01_common_022");		//欄位檢核錯誤：*為必要輸入欄位,請輸入後重試
	    		return;
		    }
        	$scope.queryInit();
        	$scope.sendRecv("INS610", "query", "com.systex.jbranch.app.server.fps.ins610.INS610InputVO", $scope.inputVO,
			function(tota, isError) {
				if (!isError) {
					if(tota[0].body.resultList.length == 0) {
						$scope.resultList = [];
						$scope.outputVO={};
						$scope.data = [];
						$scope.showMsg("ehl_01_common_009");	//查無資料
	                	return;
	                }
					$scope.resultList = tota[0].body.resultList;
					$scope.planRatio = tota[0].body.planRatio;
//					alert(JSON.stringify($scope.paramList));
					$scope.outputVO = tota[0].body;		
					return;
				}						
			});
        };
        
		$scope.initial();
});
