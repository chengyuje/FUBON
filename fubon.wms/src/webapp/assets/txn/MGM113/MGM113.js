/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('MGM113Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		// include
		$controller('RegionController', {$scope: $scope});
		
		$scope.mappingSet['MGM.ACTION'] = [];
    	$scope.mappingSet['MGM.ACTION'].push({LABEL: '覆核', DATA: '1'},
    										 {LABEL: '退回', DATA: '2'},
    										 {LABEL: '檢視簽署表單', DATA: '3'});
		
    	$scope.inquire = function() {
    		$scope.sendRecv("MGM113", "inquire", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");		//查無資料
	                			return;
	                		}
							$scope.resultList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							return;
						}
			});
    	}
    	
		//初始化
		$scope.init = function() {
			$scope.resultList = [];
			$scope.outputVO = [];
			
			if($scope.connector('get', 'MGM110_inquireVO') != null){
				$scope.inputVO = $scope.connector('get', 'MGM110_inquireVO');
				if($scope.inputVO.act_seq != undefined && $scope.inputVO.act_seq != null && $scope.inputVO.act_seq != ''){
					
					$scope.inputVO.role = '';
					$scope.ao_list = String(sysInfoService.getAoCode());
					if ($scope.ao_list != '' &&  $scope.ao_list != undefined) {
						$scope.inputVO.role = 'ao';
						$scope.inputVO.ao_list = $scope.ao_list;
					}
					
					$scope.inquire();
					
//					$scope.sendRecv("MGM113", "inquire", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", $scope.inputVO,
//							function(tota, isError) {
//								if (!isError) {
//									if(tota[0].body.resultList.length == 0) {
//										$scope.showMsg("ehl_01_common_009");		//查無資料
//			                			return;
//			                		}
//									$scope.resultList = tota[0].body.resultList;
//									$scope.outputVO = tota[0].body;
//									return;
//								}
//					});
				}
			}
		}
		$scope.init();
		
		$scope.action = function(type, flag, row) {
//			alert(type);
//			alert(flag);
//			alert(JSON.stringify(row));
			if(type == '3'){
				var formType = undefined;
				flag == '1' ? formType = 'R' : formType = 'B';
				$scope.sendRecv("MGM110", "signFormView", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", 
					{'formType': formType, 'seq' : row.SEQ},
	    				function(tota, isError) {
	    					if (!isError) {
	    						var description = tota[0].body.pdfUrl;
	    						window.open('./INS300_PDF.html?pdfurl=' + description, 'Description');
	    						if(flag == '1'){
	    							row.action1 = undefined;
	    						} else if (flag == '2'){
	    							row.action2 = undefined;
	    						}
	    						return;
	    					}
	    		});
			} else if (type == '1' || type == '2') {
				$confirm({text: '是否確認' + (type == '1' ? '覆核?' : '退回?')}, {size: 'sm'}).then(function() {
					$scope.inputVO.seq = row.SEQ;
					$scope.inputVO.action_type = type;
					$scope.inputVO.mgm_flag = flag;
					
					$scope.sendRecv("MGM113", "action", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", $scope.inputVO,
							function(tota, isError) {
								if (!isError) {
									$scope.init();
									$scope.showMsg('ehl_01_common_023');	//執行成功
									return;
								}
					});
				});				
			}
		}
		
		//刪除
		$scope.deleteRow = function(row) {
			$confirm({text: '是否確認刪除案件序號：' + row.SEQ + ' ?'}, {size: 'sm'}).then(function() {
				$scope.sendRecv("MGM113", "delete", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", {'seq':row.SEQ},
						function(tota, isError) {
							if (!isError) {
								$scope.init();
								$scope.showMsg('ehl_01_common_003');	//刪除成功
								return;
							}
				});
			});	
		}
		
		$scope.$on('MGM110_inquire', function(){
			$scope.init();
		});
		
});
		