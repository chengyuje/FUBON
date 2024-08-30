/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CMMGR013_Controller',
				function($scope, $controller, socketService, ngDialog,projInfoService, sysInfoService) {
					$controller('BaseController', {$scope : $scope});
					$scope.controllerName = "CMMGR013_Controller";
					// 初始化
					$scope.init = function() {
						$scope.inputVO = {
							cmbBRCHID : '',
							empID : '',
							cmbRoleID:'',
							radioChange:1
						};
					}
					$scope.init();
					
					$scope.mappingSet['brchID'] = [];
					
					// 取得區域&分行CMB
					$scope.getDataSource = function() {
						$scope.mappingSet['avilBranchList'] = [];
						$scope.mappingSet['avilAreaList'] = [];

						$scope.branchList = sysInfoService.getAvailBranch();
						for (var i = 0; i < $scope.branchList.length; i++) {
							$scope.mappingSet['brchID'].push({
								DATA : $scope.branchList[i].BRANCH_NBR,
								LABEL : $scope.branchList[i].BRANCH_NAME
							});
						}
					}
					$scope.getDataSource();
					
					

					// 匯出
					$scope.exportData = function() {
						$scope.sendRecv("CMMGR013","export","com.systex.jbranch.app.server.fps.cmmgr013.CMMGR013InputVO",
										$scope.inputVO);
					}

					// 列印
					$scope.report = function() {
						$scope.sendRecv("CMMGR013","report","com.systex.jbranch.app.server.fps.cmmgr013.CMMGR013InputVO",
										$scope.inputVO);
					}
					
//			     	   初始分頁資訊
			        $scope.inquireInit = function(){
			        	$scope.initLimit();
			        	$scope.paramList = [];
			        }
			        $scope.inquireInit();
			        
					// 查詢
					$scope.inquire = function() {
						$scope.sendRecv("CMMGR013","inquire","com.systex.jbranch.app.server.fps.cmmgr013.CMMGR013InputVO",
										$scope.inputVO,
										function(tota, isError) {
											if (!isError) {
						                    	  $scope.pagingList($scope.paramList, tota[0].body.dataList);
						                          $scope.outputVO = tota[0].body;
												return;
											}
										});
					}
					
					//角色
					$scope.getHeadRoleList = function() {
						$scope.sendRecv("CMMGR013", "getRoleList", "com.systex.jbranch.app.server.fps.cmmgr013.CMMGR013InputVO", $scope.inputVO,
				                  function(tota, isError) {
				                      if (!isError) {	
				                    	  $scope.RoleList = tota[0].body.RoleList;
				                          return;
				                      }
				                  });
					};
					$scope.getHeadRoleList();
					
					//rediobox
					$scope.userChange = function() {
						if($scope.inputVO.radioChange == 1){
							$scope.inputVO.cmbRoleID = '';
						}else{
							$scope.inputVO.empID = '';
						}
					}
					
					//end
				});