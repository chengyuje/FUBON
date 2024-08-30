/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD171_EDIT3Controller',
	function($rootScope,$scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD171_EDIT3Controller";
		
		
		getParameter.XML(["IOT.PAY_TYPE","PRD.INS_ANCDOC_Q_TYPE"], function(totas) {
			   if (totas) {
				//腳別
			    $scope.mappingSet['IOT.PAY_TYPE'] = totas.data[totas.key.indexOf('IOT.PAY_TYPE')];
			  //題目類型
			    $scope.typelist = totas.data[totas.key.indexOf('PRD.INS_ANCDOC_Q_TYPE')];
			   }
			  });
		
		$scope.idck= function(){
			//$scope.inputVO.INSPRD_ID
			$scope.sendRecv("PRD171","queryData2","com.systex.jbranch.app.server.fps.prd171.PRD171EDITInputVO",
        			$scope.inputVO,function(tota,isError){
            	if (isError) {
            		
            		$scope.inputVO.Q_NAME='';
            		$scope.inputVO.Q_TYPE='';
            		$scope.showErrorMsg(tota[0].body.msgData);
            		
            	}
            	if (tota.length > 0) {
            		if(tota[0].body.DILOGList.length>=1)
            			{
            			$scope.DILOGList = tota[0].body.DILOGList;
	            		$scope.inputVO.Q_NAME=tota[0].body.DILOGList[0].Q_NAME || '';
	            		$scope.inputVO.Q_TYPE=tota[0].body.DILOGList[0].Q_TYPE || '';
	            		$scope.inputVO.INSPRD_NAME = tota[0].body.DILOGList[0].INSPRD_NAME || '';
	            		$scope.inputVO.PAY_TYPE = tota[0].body.DILOGList[0].PAY_TYPE || '';
            			}
            		
            	};
			});
		}
	     // date picker
		// 有效起始日期
		$scope.bgn_sDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		$scope.bgn_eDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.bgn_sDateOptions.maxDate = $scope.inputVO.EXPIRY_DATE || $scope.maxDate;
			$scope.bgn_eDateOptions.minDate = $scope.inputVO.EFFECT_DATE || $scope.minDate;
		};	
		
		
		  $scope.mappingSet['type']=[];
          $scope.mappingSet['type'].push({
				LABEL : '是',
				DATA :'1'
  		},{
				LABEL : '否',
				DATA :'2'
  		});
		
		$scope.init = function(){
			$scope.updateData = false;
			$scope.Q_ID_TYPE = false;
			$scope.inputVO.DeleteList=[];
			$scope.DILOGList=[];
			if($scope.title_type == 'Add'){
				$scope.title='新增權益書設定';
				$scope.inputVO={
						TYPE_TABLE:'3'
				}

			}
		
			if($scope.title_type == 'Update'){
				$scope.title='修改權益書設定';
				$scope.Q_ID_TYPE=true;
				$scope.updateData = true;
				$scope.inputVO={
						TYPE_TABLE:'3',
						KEY_NO            :$scope.row_data.KEY_NO          || '',
						INSPRD_ID         :$scope.row_data.INSPRD_ID       || '',
						INSPRD_ANNUAL     :$scope.row_data.INSPRD_ANNUAL   || '',
						Q_ID              :$scope.row_data.Q_ID            || '',
						Q_SEQ             :$scope.row_data.Q_SEQ           || '',
						DeleteList		  :[]
				}
				$scope.idck();
			}
		}
		$scope.init();
		
		
		$scope.btnSubmit = function(){
			$scope.inputVO.DILOGList = $scope.DILOGList;
			 if($scope.parameterTypeEditForm.$invalid) {
		          $scope.showErrorMsgInDialog('欄位檢核錯誤:檢視頻率必要輸入欄位');
		          return;
		         }
//			 if($scope.inputVO.Q_TYPE=='' || $scope.inputVO.Q_NAME=='')
//			 {
//			  $scope.showErrorMsgInDialog('ehl_01_prd171_004');
//			  return;
//			 }
			if($scope.title_type == 'Add' || $scope.title_type == 'Copy'){
				
					$scope.sendRecv("PRD171","addData","com.systex.jbranch.app.server.fps.prd171.PRD171EDITInputVO",
		        			$scope.inputVO,function(tota,isError){
	                	if (isError) {
	                		$scope.showErrorMsg(tota[0].body.msgData);
	                	}
	                	if (tota.length > 0) {
	                		$scope.showSuccessMsg('ehl_01_common_001');
	                		$scope.closeThisDialog('successful');
	                	};
					});
				
			}
			if($scope.title_type == 'Update'){
				
					$scope.sendRecv("PRD171","updateData","com.systex.jbranch.app.server.fps.prd171.PRD171EDITInputVO",
		        			$scope.inputVO,function(tota,isError){
	                	if (isError) {
	                		$scope.showErrorMsg(tota[0].body.msgData);
	                	}
	                	if (tota.length > 0) {
	                		$scope.showSuccessMsg('ehl_01_common_006');
	                		$scope.closeThisDialog('successful');
	                	}
	                	});
					}
			}
		
		
		$scope.queryQData = function(row){
			$scope.inputVO.temp_Q_ID = row.Q_ID;
			$scope.sendRecv("PRD171","queryAncDoc","com.systex.jbranch.app.server.fps.prd171.PRD171EDITInputVO",
        			$scope.inputVO,function(tota,isError){
            	if (isError) {
            		row.Q_NAME='';
            		row.Q_TYPE='';
            		$scope.showErrorMsg(tota[0].body.msgData);
            		
            	}
            	if (tota.length > 0) {
            		if(tota[0].body.tempList.length>=1)
            			{
            			$scope.tempList = tota[0].body.tempList;
	            		row.Q_NAME=tota[0].body.tempList[0].Q_NAME || '';
	            		row.Q_TYPE=tota[0].body.tempList[0].Q_TYPE || '';
            			}else{
            				row.Q_NAME='';
            				row.Q_TYPE='';
            			}
            		
            	}
			});
		}
		
		$scope.queryINSPRD_NAME = function(){
			$scope.sendRecv("PRD171","queryINSPRD_NAME","com.systex.jbranch.app.server.fps.prd171.PRD171EDITInputVO",
					$scope.inputVO,function(tota,isError){
					if(isError){
						$scope.inputVO.INSPRD_NAME=undefined;
						$scope.showErrorMsg(tota[0].body.msgData);
					}
					if(tota.length>0){
						if(tota[0].body.INSPRD_NAMEList.length>=1){
							$scope.INSPRD_NAMEList = tota[0].body.INSPRD_NAMEList;
							$scope.inputVO.INSPRD_NAME = $scope.INSPRD_NAMEList[0].INSPRD_NAME;
						}else{
							$scope.showErrorMsg('ehl_01_prd171_001');
						}
					}else{
						$scope.inputVO.INSPRD_NAME = undefined;
						
					}
			});
		}
		
		
		$scope.addRow = function(){
			$scope.DILOGList.push({});
			angular.forEach($scope.DILOGList,function(row,index){
				row.Display_order = index + 1;
			});
		}
		
		$scope.deleteRow = function(index,row){
			if(row.KEY_NO){
				$scope.inputVO.DeleteList.push(row.KEY_NO);
			}
			$scope.DILOGList.splice(index,1);
			angular.forEach($scope.DILOGList,function(row,index){
				row.Display_order = index +1;
			})
		}
	}
);