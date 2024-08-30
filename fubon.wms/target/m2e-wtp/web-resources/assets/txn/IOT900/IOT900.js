/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('IOT900Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "IOT900Controller";
		
		$scope.init = function(){
			$scope.showOther = false;
			$scope.inputVO={
					IN_TYPE:$scope.doc_qcList.in_type,
					INS_KEYNO:$scope.doc_qcList.INS_KEYNO,
					INSPRD_ID:$scope.doc_qcList.INSPRD_ID,
					OPR_STATUS:$scope.doc_qcList.OPR_STATUS,
					SIGN_INC:'',
					in_REGTYPE:$scope.doc_qcList.in_REGTYPE,
					in_othtype:$scope.doc_qcList.oth_type,
					inList:undefined,
					outList:undefined
			}
			if($scope.doc_qcList.in_type == '1'){
				$scope.title = '分行留存文件檢核';
				$scope.IN=true;
				$scope.OUT=false;
				
				if($scope.connector('get','IOT900inListtemp') != ''){
					$scope.inList=$scope.connector('get','IOT900inListtemp');
				}
			}
			if($scope.doc_qcList.in_type == '2'){
				$scope.title = '保險送件文件檢核';
				$scope.OUT=true;
				$scope.IN=false;
				if($scope.connector('get','IOT900outListtemp') != ''){
					$scope.outList=$scope.connector('get','IOT900outListtemp');
				}
			}
			$scope.sendRecv("IOT900","Initial","com.systex.jbranch.app.server.fps.iot900.IOT900InputVO",
					$scope.inputVO,function(tota,isError){
					if($scope.inputVO.IN_TYPE == '1' && ($scope.inList ==undefined || $scope.inList =='')){
						$scope.inList = tota[0].body.INList;
						angular.forEach($scope.inList,function(row,index,objs){
							if(row.DOC_CHK == 'Y'){
								row.select=true;
							}else{
								row.select=false;
							}
						});
					}
					if($scope.inputVO.IN_TYPE == '2' && ($scope.outList == undefined || $scope.outList =='')){
						$scope.outList = tota[0].body.OUTList;
						angular.forEach($scope.outList,function(row,index,objs){
							if(row.DOC_CHK == 'Y'){
								row.select=true;
							}else{
								row.select=false;
							}
						});
					}
			});
		}
		$scope.init();

		
        $scope.mapData = function(){
        	//文件重要性
        	$scope.ngDatasource = projInfoService.mappingSet["IOT.DOC_CHK_LEVEL"];
			var comboboxInputVO = {'param_type': "IOT.DOC_CHK_LEVEL", 'desc': false};
			$scope.requestComboBox(comboboxInputVO, function(totas) {
					$scope.implist = totas[0].body.result;
			});

        }
        $scope.mapData();
		
		$scope.submit  = function(){
			if($scope.inputVO.OPR_STATUS=='new'){
				if($scope.inputVO.IN_TYPE == '1'){
					angular.forEach($scope.inList,function(row,index,objs){
						debugger
						if(row.select==true){
							row.DOC_CHK = 'Y';
						}

						if(row.DOC_NAME_OTH != undefined){
							row.DOC_NAME_OTH = row.DOC_NAME_OTH.replace(/,/g, "，");
						}						
					});
					$scope.connector('set','IOT900inListtemp',$scope.inList);
					$scope.connector('set','IOT900inList',$scope.inList);
					$scope.closeThisDialog('in');
				}
				if($scope.inputVO.IN_TYPE == '2'){
					angular.forEach($scope.outList,function(row,index,objs){
						debugger
						if(row.select==true){
							row.DOC_CHK = 'Y';
						}

						if(row.DOC_NAME_OTH != undefined){
							row.DOC_NAME_OTH = row.DOC_NAME_OTH.replace(/,/g, "，");
						}						
					});
					$scope.connector('set','IOT900outListtemp',$scope.outList);
					$scope.connector('set','IOT900outList',$scope.outList);
					$scope.closeThisDialog('out');
				}
				
			}
			if($scope.inputVO.OPR_STATUS=='UPDATE'){
				$scope.inputVO.inList = $scope.inList;
				$scope.inputVO.outList = $scope.outList;
				$scope.sendRecv("IOT900","submit","com.systex.jbranch.app.server.fps.iot900.IOT900InputVO",
				$scope.inputVO,function(tota,isError){
					if (isError) {
						$scope.showErrorMsg(tota[0].body.msgData);
						}
					if (tota.length > 0) {
		        		$scope.showSuccessMsg('ehl_01_common_006');
		        		if($scope.inputVO.IN_TYPE == '1'){
		        			$scope.inList = tota[0].body.INList;
		        			$scope.connector('set','IOT900inList',$scope.inList);
		        			$scope.closeThisDialog('in');
		        		}
		        		if($scope.inputVO.IN_TYPE == '2'){
		        			$scope.outList = tota[0].body.OUTList;
		        			$scope.connector('set','IOT900outList',$scope.outList);
		        			$scope.closeThisDialog('out');
		        		}
			        	};
			        });
			}
		}

	}
);