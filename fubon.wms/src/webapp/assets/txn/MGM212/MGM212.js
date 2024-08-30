/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('MGM212Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, sysInfoService, $q, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "MGM212Controller";
		
		getParameter.XML(["MGM.POINTS_TYPE", "MGM.RELEASE_STATUS"], function(totas) {
			if (totas) {
				$scope.mappingSet['MGM.POINTS_TYPE'] = totas.data[totas.key.indexOf('MGM.POINTS_TYPE')];				//MGM活動點數類型
				$scope.mappingSet['MGM.RELEASE_STATUS'] = totas.data[totas.key.indexOf('MGM.RELEASE_STATUS')];			//MGM活動點數放行狀態
			}
		});
		
		//查詢
		$scope.inquire = function(){
			//查詢該活動中所有核點狀態已達門檻案件
			$scope.sendRecv("MGM212", "inquire", "com.systex.jbranch.app.server.fps.mgm212.MGM212InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.resultList = [];
								$scope.outputVO = [];
								$scope.showMsg("ehl_01_common_009");		//查無資料
	                			return;
	                		}
//							alert(JSON.stringify(tota[0].body.resultList));
							$scope.resultList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							return;
						}
			});
		}
		
		//初始化
		$scope.init = function(){
//			$scope.inputVO.seq_list = [];
			$scope.pageControlVO = [];
			$scope.pageControlVO.allChoice = false;
			$scope.pageControlVO.pageChoice = false;
			
			$scope.resultList = [];
			$scope.outputVO = [];
			$scope.inputVO.act_seq = $scope.row.ACT_SEQ;
			$scope.inputVO.mgm_cust_id = undefined;
			$scope.inputVO.seq  = undefined;
			$scope.inputVO.points_type = undefined;
			$scope.inputVO.release_status = undefined;
			$scope.inputVO.branch_nbr = undefined;
			
			//設定分行下拉
			$scope.BRANCH_LIST = [];
			angular.forEach(projInfoService.getAvailBranch(), function(row) {
				$scope.BRANCH_LIST.push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
			});
			
			$scope.inquire();
			
		}
		$scope.init();
		
		//本頁全選
		$scope.page = function() {
        	if ($scope.pageControlVO.pageChoice) {
        		angular.forEach($scope.paramList, function(row) {
        			if(row.RELEASE_YN != 'Y')
        				row.SELECTED = true;
    			});
        	} else {
        		angular.forEach($scope.paramList, function(row) {
        			row.SELECTED = false;
    			});
        	}
        };
        
        //全選
        $scope.all = function() {
        	if ($scope.pageControlVO.allChoice) {
        		$scope.pageControlVO.pageChoice = true;
        		angular.forEach($scope.resultList, function(row) {
        			if(row.RELEASE_YN != 'Y')
        				row.SELECTED = true;
    			});
        	} else {
        		$scope.pageControlVO.pageChoice = false;
        		angular.forEach($scope.resultList, function(row) {
        			row.SELECTED = false;
    			});
        	}
        };
        
        //點數修改
	    $scope.modifyPoints = function (seq) {
//	    	alert(seq);
	    	var act_seq = $scope.row.ACT_SEQ;
			var dialog = ngDialog.open({
				template: 'assets/txn/MGM212/MGM212_MODIFY.html',
				className: 'MGM212_MODIFY',
				showClose: false,
				 controller: ['$scope', function($scope) {
					 	$scope.mgm212_seq = seq;
					 	$scope.act_seq = act_seq;
	             }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					$scope.init();
				}
			});
	    }
	    
	    //下載
	    $scope.download = function () {
	    	if($scope.resultList.length > 0){
	    		$scope.sendRecv("MGM212", "download", "com.systex.jbranch.app.server.fps.mgm212.MGM212InputVO", 
	    			{'resultList' : $scope.resultList, 'act_name' : $scope.row.ACT_NAME},
	    				function(tota, isError) {
	    			if (!isError) {
	    				
	    			}
	    		});
	    	} else {
	    		$scope.showErrorMsg("請重新查詢。");
				return;
	    	}
	    }
	    
	    //給點
	    $scope.release = function () {
	    	$scope.inputVO.seq_list = [];
	    	angular.forEach($scope.resultList, function(row){
	    		if (row.SELECTED == true) {
	    			$scope.inputVO.seq_list.push(row.SEQ);
	    		}
	    	});
//	    	alert(JSON.stringify($scope.inputVO.seqList));
	    	if($scope.inputVO.seq_list.length == 0){
	    		$scope.showErrorMsg("請勾選案件。");
				return;
	    	} else {
	    		$scope.sendRecv("MGM212", "release", "com.systex.jbranch.app.server.fps.mgm212.MGM212InputVO", 
	    			{'seq_list' : $scope.inputVO.seq_list},
						function(tota, isError) {
							if (!isError) {
								$scope.showSuccessMsg("ehl_01_common_023");		//執行成功
								$scope.init();
								return;
							}
				});
	    	}
	    }
	    
	    //預覽修改憑證
	    $scope.eviView = function (row) {
	    	$scope.seq = row.SEQ;
			$scope.sendRecv("MGM212", "getEviView", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", {'seq': $scope.seq},
    				function(tota, isError) {
    					if (!isError) {
    						var description = tota[0].body.pdfUrl;
    						window.open('./INS300_PDF.html?pdfurl=' + description, 'Description');
    						return;
    					}
    		});
	    }
});
