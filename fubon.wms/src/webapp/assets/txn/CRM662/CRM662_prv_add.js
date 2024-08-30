/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM662_prv_addController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM662_prv_addController";
		
		getParameter.XML(["CRM.FAMILY_DEGREE_AUM",'CRM.FAMILY_REL'],function(totas){
			if(totas){
				$scope.mappingSet['CRM.FAMILY_DEGREE_AUM'] = totas.data[0];
				$scope.mappingSet['CRM.FAMILY_REL'] = totas.data[1];
			}
		});

		//vip_degree
        var vo = {'param_type': 'CRM.VIP_DEGREE', 'desc': false};
        if(!projInfoService.mappingSet['CRM.VIP_DEGREE']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['CRM.VIP_DEGREE'] = totas[0].body.result;
        			$scope.mappingSet['CRM.VIP_DEGREE'] = projInfoService.mappingSet['CRM.VIP_DEGREE'];
        		}
        	});
        } else {
        	$scope.mappingSet['CRM.VIP_DEGREE'] = projInfoService.mappingSet['CRM.VIP_DEGREE'];
        }
		
		//初始化
		$scope.init = function(){
			$scope.inputVO.cust_id ='';
			$scope.inputVO.cust_name ='';
			$scope.inputVO.cust_id_m = $scope.cust_id;
			$scope.inputVO.ao_code = $scope.ao_code;
			$scope.inputVO.emp_id = sysInfoService.getUserID();
			//row = {};
			$scope.resultList = [];
		}
		$scope.init();
		
		//查詢轄下客戶
		$scope.queryCustomer = function(){
			//檢核
			if($scope.inputVO.cust_id == '' && $scope.inputVO.cust_name == ''){
				$scope.showErrorMsgInDialog("請輸入查詢條件")
				return;
			}
			if($scope.inputVO.cust_id != ''){
				$scope.inputVO.cust_id = $scope.inputVO.cust_id.replace(/[ ]/g,"");
				$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase();
			}
			if($scope.inputVO.cust_name != ''){
				$scope.inputVO.cust_name = $scope.inputVO.cust_name.replace(/[ ]/g,"");
			}
//			$scope.inputVO.cust_id = $scope.inputVO.cust_id.replace(/[ ]/g,"");
//			$scope.inputVO.cust_name = $scope.inputVO.cust_name.replace(/[ ]/g,"");

			$scope.prvList = [];
			//先清空再寫入
			$scope.resultList = [];
			$scope.sendRecv("CRM662", "queryCustomer", "com.systex.jbranch.app.server.fps.crm662.CRM662InputVO", $scope.inputVO,
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsg(tota[0].body.msgData);
					}
					if(tota[0].body.resultList_prv_add.length == 0) {
						$scope.showMsg("ehl_01_common_009");
	               		return;
					}
					$scope.resultList = tota[0].body.resultList_prv_add;
					$scope.prvList = $scope.resultList;
			});
		}

		$scope.relList = []; 
		//查詢現有關係戶
		$scope.queryRelCustomer = function(){
			//先清空再寫入
			$scope.resultList = [];
			$scope.sendRecv("CRM662", "queryRelCustomer", "com.systex.jbranch.app.server.fps.crm662.CRM662InputVO", $scope.inputVO,
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsg(tota[0].body.msgData);
					}
					if(tota[0].body.resultList_rel_add.length == 0) {
						$scope.showMsg("ehl_01_common_009");
	               		return;
					}
					$scope.resultList = tota[0].body.resultList_rel_add;
					$scope.relList = $scope.resultList;
			});
		}
		
		$scope.doubleCHK_existFAM = function(row){
			var deferred = $q.defer();
			//若已經為主戶或其他客戶的從戶(現有/申請中家庭戶)，則不可再加入
			if (row.EXIST_FAM == "Y") {
				$scope.sendRecv("CRM662", "doubleChk", "com.systex.jbranch.app.server.fps.crm662.CRM662InputVO", {cust_id_m_dc : row.CUST_ID}, function(tota, isError) {
					row.EXIST_FAM = tota[0].body.resultList_prv_rpy_dc[0].EXIST_FAM;
					
					deferred.resolve();
				});
			}
			return deferred.promise;
		};

		function check_prv_add(row){
			if(row.CUST_ID != undefined && row.CUST_ID != ''){
				//=====================================================================================//
				//判斷ID長度是否小於10位
				//自然人ID長度10位,若小於10位則為非自然人,不可加入家庭戶
				if(row.CUST_ID.length < 10){
					$scope.showMsg(row.CUST_ID + " " + row.CUST_NAME + "非自然人不可加入家庭戶");
					return false;
				}
				
				//若已經為主戶或其他客戶的從戶(現有/申請中家庭戶)，則不可再加入
				if (row.EXIST_FAM == "Y") {
					$scope.showMsg(row.CUST_ID + " " + row.CUST_NAME + " 此客戶為現有家庭戶成員，不可再加入");
					return false;
				}
				
				return true;
			}else{
				return true;
			}
		}

		function check_rel_add(row){
			if(row.CUST_ID != undefined && row.CUST_ID != ''){									
				//=====================================================================================//
				//判斷ID長度是否小於10位
				//自然人ID長度10位,若小於10位則為非自然人,不可加入家庭戶
				if(row.CUST_ID.length < 10){
					$scope.showMsg(row.CUST_ID + " " + row.CUST_NAME + " 非自然人不可加入家庭戶");
					return false;
				}
				
				//若已經為主戶或其他客戶的從戶(現有/申請中家庭戶)，則不可再加入
				if (row.EXIST_FAM == "Y") {
					$scope.showMsg(row.CUST_ID + " " + row.CUST_NAME + " 此客戶為現有家庭戶成員，不可再加入");
					return false;
				}
				
				//=====================================================================================//
				//判斷關係是否為為配偶/直系血親關係
				//不是則跳出，若是則帶回
				var listFamilyRel = $scope.mappingSet['CRM.FAMILY_REL'][0].LABEL.split(","); //抓出參數中符合家庭戶關係的參數
				var relType = row.REL_TYPE; //抓出目前選擇的參數
				var isRelType = true; //先預設為符合家庭戶關係
				
				//利用迴圈將目前選擇的參數跟符合家庭戶關係的參數做比對
				//若比對到則跳出帶回資料,反之為不符合此家庭戶關係則不帶回並跳出提示訊息
				for(var i = 0; i < listFamilyRel.length; i++){
					if(relType == listFamilyRel[i]){
						isRelType = true;
						//$scope.closeThisDialog($scope.inputVO.choose);
						return true;
					}else{
						isRelType = false;
					}
				}			
				if(!isRelType){
					let warningMsg = "須為配偶/直系血親/兄弟姊妹/姻親關係，方可申請家庭會員";
					$scope.showMsg(row.CUST_ID + " " + row.CUST_NAME + warningMsg);
					return false;
				}
				//=====================================================================================//				
			}else{
				//$scope.closeThisDialog($scope.inputVO.choose);
				return true;
			}
		}

		$scope.addList = []; 
		$scope.prv_add = function(){
			if($scope.prvList != null){
				angular.forEach($scope.prvList,function(row, index, objs){
					if(row.select == true){
						var flag = check_prv_add(row);
						if(flag == true){
							$scope.addList.push(row);
						}						
					}
				});
				$scope.closeThisDialog($scope.addList);
				return;
			}

			if($scope.relList != null){
				angular.forEach($scope.relList,function(row, index, objs){
					if(row.select == true){
						var flag = check_rel_add(row);
						if(flag == true){
							$scope.addList.push(row);
						}

					}
				});

				$scope.closeThisDialog($scope.addList);
			}
		}		
	}
);