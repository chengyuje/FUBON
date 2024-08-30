/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM671_addController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM671_addController";
		
		//xml
		getParameter.XML(['CAM.VST_REC_TEXT_FORMAT','CAM.VST_REC_CMU_TYPE','CAM.DISABLE_TEXT_FORMAT'], function(totas) {
			if(len(totas)>0) {
				$scope.mappingSet['CAM.VST_REC_TEXT_FORMAT'] = totas.data[totas.key.indexOf('CAM.VST_REC_TEXT_FORMAT')];
				$scope.mappingSet['CAM.VST_REC_CMU_TYPE'] = totas.data[totas.key.indexOf('CAM.VST_REC_CMU_TYPE')];
				$scope.mappingSet['CAM.DISABLE_TEXT_FORMAT'] = totas.data[totas.key.indexOf('CAM.DISABLE_TEXT_FORMAT')];
			}
		});
		
		$scope.custVO = {
				CUST_ID :  $scope.cust_id,
				CUST_NAME :$scope.cust_name	
		};
		
		$scope.init = function() {
			var nowDate = new Date();
			var defDate = new Date(nowDate.getFullYear(), nowDate.getMonth()+1, nowDate.getDate(), nowDate.getHours(), nowDate.getMinutes());

			$scope.inputVO.cust_id = $scope.cust_id;
			$scope.inputVO.cust_name = $scope.cust_name;
			$scope.inputVO.cmu_type = '';
			$scope.inputVO.visit_memo = '';
			$scope.inputVO.rec_text_format = '';
			$scope.inputVO.visit_date = nowDate;
			$scope.inputVO.visit_time = defDate;
			$scope.inputVO.visit_creply = '';
			
			//通知客戶內容
			$scope.disableTextFormatStatus = true; 		//禁用字串使用狀態
	        $scope.tempDisableText = ""; 				//使用哪個禁用字串
	        
	        //客戶回應內容
			$scope.disableTextFormatStatusBYcrp = true; //禁用字串使用狀態
	        $scope.tempDisableTextBYcrp = ""; 			//使用哪個禁用字串
		}
		$scope.init();
		
		// dateTime ===
		$scope.visit_date_DateOptions = {
       		maxDate: $scope.maxDate
   	    };
		$scope.model = {};
		
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
		$scope.limitDate = function() {
			$scope.visit_date_DateOptions.maxDate =  $scope.maxDate;
		};
		// dateTime ===
		
		$scope.write = function() {
			angular.forEach($scope.mappingSet['CAM.VST_REC_TEXT_FORMAT'], function(row){
				if($scope.inputVO.rec_text_format == row.DATA){
					$scope.inputVO.visit_memo = row.LABEL;
				}
			});
			$scope.inputVO.visit_memo = ($scope.inputVO.visit_memo == undefined ? "" : $scope.inputVO.visit_memo);
		};
		
		$scope.checkDisableText = function(word, type) {
			if(type == 'visitMemo') $scope.disableTextFormatStatus = true;
			if(type == 'visitCreply') $scope.disableTextFormatStatusBYcrp = true;
			
        	if (undefined != word) {
        		$scope.esbData = $scope.connector('get','CRM611_esbData'); //FP032675Data
        		//客戶為專業投資人或有簽信託推介客戶，則不阻擋禁用文字
        		if($scope.esbData && $scope.esbData.custTxFlag == 'N' && $scope.esbData.custProFlag == 'N') {       
	            	angular.forEach($scope.mappingSet['CAM.DISABLE_TEXT_FORMAT'], function(value) {
	            		if (word.indexOf(value.LABEL) != -1) {
	            			if (type == 'visitMemo') {
	            				$scope.tempDisableText = "：" + value.LABEL;
	            				$scope.disableTextFormatStatus = false;
	            			} else if (type == 'visitCreply') {
	            				$scope.tempDisableTextBYcrp = "：" + value.LABEL;
	            				$scope.disableTextFormatStatusBYcrp = false;
	            			}
	            		}
	            	});
        		}
        	}
        }
		
		//save
		$scope.save = function() {
			$scope.sendRecv("CRM671", "saveRecord", "com.systex.jbranch.app.server.fps.crm671.CRM671InputVO", $scope.inputVO,
					function(tota, isError) {
						if (isError) {
							$scope.showErrorMsgInDialog(tota.body.msgData);
							return;
						}
						if (tota.length > 0) {
							$scope.showMsg('ehl_01_common_001');
		                	$scope.closeThisDialog('successful');
		                };
			});
		};
});