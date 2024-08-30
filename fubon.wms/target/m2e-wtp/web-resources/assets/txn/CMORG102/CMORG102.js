/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('CMORG102Controller', 	
    function($scope, $controller, socketService, ngDialog, projInfoService, $rootScope, $confirm) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CMORG102Controller";
        
        //初始化
        $scope.init = function(){
        	$scope.inputVO = {
        			action: '',
        			roleID: '',
        			roleName: '',
        			orgType: '',
        			isAdmin: false,
        			isAO: false,
        			isOP: false,
        			isSales: false,
        			isOO: false,
        			changeSelect :'',
        			updateChange:false
        	};
        }
        $scope.init();
        $scope.inquireInit = function(){
        	$scope.action = 'ADD';  //Default
        	$scope.initLimit();
        	$scope.lstRoleOrgSetting = [];
        }
        $scope.inquireInit();
        
        //查詢
        $scope.inquire = function(){
	    	  $scope.sendRecv("CMORG102", "getRoleOrgSetting", "com.systex.jbranch.app.server.fps.cmorg102.CMORG102InputVO", $scope.inputVO,
	                  function(tota, isError) {
	                      if (!isError) {
//	                    	  $scope.lstRoleOrgSetting = tota[0].body.lstRoleOrgSetting;
	                    	  $scope.pagingList($scope.lstRoleOrgSetting, tota[0].body.lstRoleOrgSetting);
	                    	  $scope.outputVO = tota[0].body;
	                          return;
	                      }
	                  });
	      }
        $scope.inquire();
        
        //新增
        $scope.saveRoleOrgSetting = function(){
        	$scope.inputVO.roleID=$scope.inputVO.tipRoleID;
        	$scope.inputVO.roleName=$scope.inputVO.tipRoleName;
        	for(var i=0;i<projInfoService.mappingSet['CM.ORG_TYPE'].length;i++){
    			if($scope.inputVO.cmbOrgType == projInfoService.mappingSet['CM.ORG_TYPE'][i].DATA.trim()){
    				$scope.inputVO.orgType = projInfoService.mappingSet['CM.ORG_TYPE'][i].LABEL;
    			}
    		}
        	
        	$scope.inputVO.action = $scope.action
        	
        	$scope.inputVO.isAdmin=$scope.inputVO.chkAdmin;
        	$scope.inputVO.isAO=$scope.inputVO.chkAO;
        	$scope.inputVO.isOP=$scope.inputVO.chkOP;
        	$scope.inputVO.isSales=$scope.inputVO.chkSales;
        	$scope.inputVO.isOO=$scope.inputVO.chkOO;
        	$scope.sendRecv("CMORG102", "saveRoleOrgSetting", "com.systex.jbranch.app.server.fps.cmorg102.CMORG102InputVO", $scope.inputVO,
        	function(tota, isError) {
        		if (!isError) {
        			if("ADD" === $scope.action){
        				$scope.showMsg('ehl_02_common_026'); //新增成功
        			}
        			else{
        				$scope.showMsg('ehl_02_common_027'); //修改成功
        			}
        			
        			$scope.inquireInit();
        			$scope.inquire();
        		}
        	});
        }
        
        //update
        $scope.update = function(row){
        	//修改
        	$scope.inputVO.updateChange = true;
        	$scope.inputVO.changeSelect = row.updorDel;
        	if(row.updorDel == 'update'){
        		for(var i=0;i<projInfoService.mappingSet['CM.ORG_TYPE'].length;i++){
        			if(row.ORG_TYPE == projInfoService.mappingSet['CM.ORG_TYPE'][i].LABEL.trim()){
        				$scope.inputVO.cmbOrgType = projInfoService.mappingSet['CM.ORG_TYPE'][i].DATA;
        			}
        		}
        		$scope.inputVO.tipRoleID = row.ROLE_ID;
        		$scope.inputVO.tipRoleName = row.ROLE_NAME;
        		//checkbox
        		$scope.inputVO.chkAdmin = row.IS_ADM;
        		$scope.inputVO.chkAO = row.IS_AO;
        		$scope.inputVO.chkOP = row.IS_OP;
        		$scope.inputVO.chkSales = row.IS_SALES;
        		$scope.inputVO.chkOO = row.IS_OO;
        		$scope.action = "UPD";
            }
        	//刪除
        	if(row.updorDel == 'delete'){
             	$confirm({text: '請確定是否刪除此筆資料？'}) //ehl_02_common_004 請確定是否刪除此筆資料？
                 .then(function() {
                	$scope.inputVO.roleID=row.ROLE_ID;
                	$scope.action = "DEL";
//         			$scope.sendRecv("CMORG102", "delRoleOrgSetting", "com.systex.jbranch.app.server.fps.cmorg102.CMORG102InputVO", $scope.inputVO);
// 					$scope.inquireInit();
// 					$scope.inquire();
// 					$scope.showMsg('ehl_02_common_028 (刪除成功)');
 					
 					$scope.sendRecv("CMORG102","delRoleOrgSetting","com.systex.jbranch.app.server.fps.cmorg102.CMORG102InputVO",
 							$scope.inputVO,
							function(tota, isError) {
								if (!isError) {
									$scope.inquireInit();
									$scope.init();
				 					$scope.inquire();
				 					$scope.showMsg('ehl_02_common_028');
									return;
								}
							});
 					
                 });      	
        	}
        }
        
        //checkboxChange
        $scope.chkAO = function(chkAO){
        	chkAO == true ? $scope.inputVO.chkSales = true : $scope.inputVO.chkSales = false;
        }
        $scope.chkOP = function(chkOP){
        	chkOP == true ? $scope.inputVO.chkOO = true : $scope.inputVO.chkOO = false;
        }
        $scope.chkSales = function(chkSales){
        	chkSales == true ? $scope.inputVO.chkSales = true : $scope.inputVO.chkAO = false;
        }
        $scope.chkOO = function(chkOO){
        	chkOO == true ? $scope.inputVO.chkOO = true : $scope.inputVO.chkOP = false;
        }
        
        // mapping
		$scope.orgType = function() {
			var comboboxInputVO = {'param_type' : 'CM.ORG_TYPE','desc' : false};
			$scope.requestComboBox(comboboxInputVO,function(totas) {
				if (totas[totas.length - 1].body.result === 'success') {
					projInfoService.mappingSet['CM.ORG_TYPE'] = totas[0].body.result;
				}
			});
		};
		$scope.orgType();
        
    });