/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM671_detailController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM671_detailController";
		
		// bra
		$scope.mappingSet['branchsDesc'] = [];
		angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){
			$scope.mappingSet['branchsDesc'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
		});
		//xml
		getParameter.XML(['CRM.VIP_DEGREE','CAM.TASK_STATUS','CAM.VST_REC_CMU_TYPE'], function(totas) {
			if(len(totas)>0) {
				$scope.mappingSet['CRM.VIP_DEGREE'] = totas.data[totas.key.indexOf('CRM.VIP_DEGREE')];
				$scope.mappingSet['CAM.TASK_STATUS'] = totas.data[totas.key.indexOf('CAM.TASK_STATUS')];
				$scope.mappingSet['CAM.VST_REC_CMU_TYPE'] = totas.data[totas.key.indexOf('CAM.VST_REC_CMU_TYPE')];
			} 
		});
		
        //初始化
		$scope.init = function() {
			$scope.inputVO.cust_id = $scope.custVO.CUST_ID;
			$scope.pri_id = String(sysInfoService.getPriID());
			$scope.pri = '';		
			if($scope.pri_id == '009' || $scope.pri_id == '010' || $scope.pri_id == '011' || $scope.pri_id == '012' || $scope.pri_id == '013'){
				$scope.pri = 'A1'
			} else if ($scope.pri_id == 'UHRM002' || $scope.pri_id == 'UHRM012' || $scope.pri_id == 'UHRM013'){
				$scope.pri = 'U1'
			} else {
				$scope.pri = String(sysInfoService.getPriID());
			}
			$scope.statusList = [];
			$scope.FCResult = [];
			$scope.FC_outputVO = {};
			
			//===
			$scope.PSsResult = [];
			$scope.PAOResult  = []
			$scope.PSResult = [];
			$scope.PS_outputVO = {};
			//===
			
			$scope.FAResult =[];
			$scope.FA_outputVO = {};
			$scope.delList_ao = [];
			$scope.delList_ps = [];
			$scope.delList_fi = [];
			$scope.delList_uhrm = [];
		}
		$scope.init();
		
		//查詢-訪談紀錄
		$scope.inquire = function() {
	    	$scope.sendRecv("CRM671", "inquire", "com.systex.jbranch.app.server.fps.crm671.CRM671InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList2.length == 0) {
							$scope.showMsg("ehl_01_common_009");
                			return;
                		}
						$scope.resultList2 = tota[0].body.resultList2;
						// 2017/9/8 russle手動去重複 , will cry delete not work becauese same data
						$scope.FCResult = $filter('filter')($scope.resultList2,{PRI : null});
						$scope.FCResult = _.uniqBy($scope.FCResult, function (e) {
							return e.VISITOR_ROLE + ' ' + e.CMU_TYPE + ' ' + e.VISIT_MEMO + ' ' + e.MODIFIER + ' ' + e.LASTUPDATE;
						});
						$scope.FC_outputVO = {'data':$scope.FCResult};
						
						$scope.UHRMResult = $filter('filter')($scope.resultList2,{PRI : 'UHRM'});
						$scope.UHRMResult = _.uniqBy($scope.UHRMResult, function (e) {
							return e.VISITOR_ROLE + ' ' + e.CMU_TYPE + ' ' + e.VISIT_MEMO + ' ' + e.MODIFIER + ' ' + e.LASTUPDATE;
						});
						$scope.UHRM_outputVO = {'data':$scope.UHRMResult};
						
						//===
						$scope.PSsResult = $filter('filter')($scope.resultList2,{PRI : 'PS'});
						$scope.PAOResult = $filter('filter')($scope.resultList2,{PRI : 'PAO'});
						
						angular.forEach($scope.PSsResult, function(row, index, objs){
							$scope.PSResult.push(row);
						});
						
						angular.forEach($scope.PAOResult, function(row, index, objs){
							$scope.PSResult.push(row);
						});
						
						$scope.PSResult = _.uniqBy($scope.PSResult, function (e) {
							return e.VISITOR_ROLE + ' ' + e.CMU_TYPE + ' ' + e.VISIT_MEMO + ' ' + e.MODIFIER + ' ' + e.LASTUPDATE;
						});
						
						$scope.PS_outputVO = {'data':$scope.PSResult};
						//===
						
						$scope.FAResult = $filter('filter')($scope.resultList2,{PRI : 'FA'});
						$scope.FAResult = _.uniqBy($scope.FAResult, function (e) {
							return e.VISITOR_ROLE + ' ' + e.CMU_TYPE + ' ' + e.VISIT_MEMO + ' ' + e.MODIFIER + ' ' + e.LASTUPDATE;
						});
						$scope.FA_outputVO = {'data':$scope.FAResult};
					}
	    	});
		};
		$scope.inquire();
		
		//查詢-交辦事項
		$scope.inquire2 = function(){
	    	$scope.sendRecv("CRM671", "inquire2", "com.systex.jbranch.app.server.fps.crm671.CRM671InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.resultList3 = tota[0].body.resultList3;
						$scope.outputVO3 = tota[0].body;
					}
				})
		};
		$scope.inquire2();
		
		$scope.getList = function (seq, status) {
			$scope.statusList.push({'SEQ':seq,'STATUS':status});
		};
		
		$scope.save = function () {
			$scope.inputVO.statusList = $scope.statusList;
			$scope.sendRecv("CRM671", "saveStatus", "com.systex.jbranch.app.server.fps.crm671.CRM671InputVO", $scope.inputVO,
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsg(tota[0].body.msgData);
					}
					if(tota.length > 0) {
						$scope.showMsg("ehl_01_common_006");
						$scope.inquire2();
					}
				}
			)
		};
		
		//更多>>前往CUS160
		$scope.toCUS160 = function () {
			$scope.connector("set", "CRM671", true);
			var path = "assets/txn/CUS160/CUS160.html";
			$scope.connector("set", "CRM610URL", path);
			$scope.$emit("CRM610VO", {action:"set", type:"URL", data:path});
		};
		
		//新增訪談紀錄
        $scope.add = function() {
        	var cust_id = $scope.custVO.CUST_ID;
        	var cust_name = $scope.custVO.CUST_NAME;

        	var dialog = ngDialog.open({
				template: 'assets/txn/CRM671/CRM671_add.html',
				className: 'CRM671_add',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.cust_id = cust_id;
                	$scope.cust_name = cust_id;
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					$scope.init();
					$scope.inquire();
				}
			});
        };
        
      //刪除勾選陣列
        Array.prototype.remove = function () {
        	var what, a = arguments, L = a.length, ax;
        	
        	while (L && this.length) {
	        	what = a[--L];
	        	
	        	while ((ax = this.indexOf(what)) !== -1) {
	        		this.splice(ax, 1);
	        	}
        	}
        	return this;
        };
        
        /**理專 -- 刪除資料勾選**/        
        $scope.delrow_ao = function(row){
        	if(row.CHECK_AO == 'Y'){
        		$scope.delList_ao.push(row.VISIT_SEQ);
        	}else{
        		$scope.delList_ao.remove(row.VISIT_SEQ);
        	}
        }
        /**消金 -- 刪除資料勾選**/        
        $scope.delrow_ps = function(row){
        	if(row.CHECK_PS == 'Y'){
        		$scope.delList_ps.push(row.VISIT_SEQ);
        	}else{
        		$scope.delList_ps.remove(row.VISIT_SEQ);
        	}
        } 
        /**輔銷 -- 刪除資料勾選**/        
        $scope.delrow_fi = function(row){
        	if(row.CHECK_FI == 'Y'){
        		$scope.delList_fi.push(row.VISIT_SEQ);
        	}else{
        		$scope.delList_fi.remove(row.VISIT_SEQ);
        	}
        } 	
        /**UHRM -- 刪除資料勾選**/        
        $scope.delrow_uhrm = function(row){
        	if(row.CHECK_UHRM == 'Y'){
        		$scope.delList_uhrm.push(row.VISIT_SEQ);
        	}else{
        		$scope.delList_uhrm.remove(row.VISIT_SEQ);
        	}
        }
		/**刪除資料**/
		$scope.delData = function(row, type){			
			//理專訪談紀錄
			if(type == '1'){
				$scope.inputVO.seqList = $scope.delList_ao;
			}
			//消金訪談紀錄
			else if(type == '2'){
				$scope.inputVO.seqList = $scope.delList_ps;
			}
			//輔銷訪談紀錄
			else if(type == '3'){
				$scope.inputVO.seqList = $scope.delList_fi;
			}
			//UHRM訪談紀錄
			else if(type == '4'){
				$scope.inputVO.seqList = $scope.delList_uhrm;
			}
			$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
				$scope.sendRecv("CRM671", "deleteData", "com.systex.jbranch.app.server.fps.crm671.CRM671InputVO", $scope.inputVO,
					function(tota, isError) {
						if(!isError){
							$scope.showSuccessMsg('刪除成功');
							$scope.init();
							$scope.inquire();
						}
				});
			});
			
		};
});