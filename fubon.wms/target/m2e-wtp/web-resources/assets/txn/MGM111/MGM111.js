/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('MGM111Controller',
	function($rootScope, $scope, $controller, $confirm, $http, $sce, socketService, ngDialog, projInfoService, sysInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		// include
		$controller('RegionController', {$scope: $scope});
		
		$scope.controllerName = "MGM111Controller";
		
		getParameter.XML(["MGM.ACT_TYPE"], function(totas) {
			if (totas) {
				$scope.mappingSet['MGM.ACT_TYPE'] = totas.data[totas.key.indexOf('MGM.ACT_TYPE')];			//活動類型
			}
		});
		
		$scope.inquire = function() {
			$scope.sendRecv("MGM111", "inquire", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", {'act_seq': $scope.inputVO.act_seq},
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");		//查無資料
	                			return;
	                		}
							$scope.resultList = tota[0].body.resultList;
							$scope.mgm111.ACT_TYPE = $scope.resultList[0].ACT_TYPE;
							$scope.mgm111.EFF_DATE = $scope.resultList[0].EFF_DATE;
							$scope.mgm111.DEADLINE = $scope.resultList[0].DEADLINE;
							$scope.mgm111.EXC_DEADLINE = $scope.resultList[0].EXC_DEADLINE;
							$scope.mgm111.ACT_SEQ = $scope.resultList[0].ACT_SEQ;
							$scope.mgm111.ACT_NAME = $scope.resultList[0].ACT_NAME;
							$scope.mgm111.ACT_CONTENT = $scope.resultList[0].ACT_CONTENT;
							$scope.mgm111.ACT_APPROACH = $scope.resultList[0].ACT_APPROACH;
							$scope.mgm111.PRECAUTIONS = $scope.resultList[0].PRECAUTIONS;
							
							$scope.mgm111_file = tota[0].body.fileList;
							$scope.mgm111_gift = tota[0].body.giftList;
							$scope.mgm111_gift_outputVO = tota[0].body;
							return;
						}
			});
			
			//查詢活動附件
			$scope.fileList = [];
			$scope.sendRecv("MGM211", "getActFile", "com.systex.jbranch.app.server.fps.mgm211.MGM211InputVO", {'act_seq': $scope.inputVO.act_seq}, 
					function(tota, isError) {
						if (!isError) {
							$scope.fileList = tota[0].body.resultList;
						}
			});
		}
		
		//初始化
		$scope.init = function() {
			$scope.today = new Date();
			$scope.resultList = [];
			$scope.outputVO = [];
			
			if($scope.connector('get', 'MGM110_inquireVO') != null){
				$scope.inputVO = $scope.connector('get', 'MGM110_inquireVO');
				if($scope.inputVO.act_seq != undefined && $scope.inputVO.act_seq != null && $scope.inputVO.act_seq != ''){
					$scope.inquire();
				}
			}
		}
		$scope.init();
		
		$scope.fileView = function(row){
			$scope.seq = row.SEQ;
			$scope.sendRecv("MGM111", "getFileView", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", {'seq': $scope.seq},
    				function(tota, isError) {
    					if (!isError) {
    						var description = tota[0].body.pdfUrl;
    						window.open('./INS300_PDF.html?pdfurl=' + description, 'Description');
    						return;
    					}
    		});
		}

		$scope.$on('MGM110_inquire', function(){
//			debugger
			$scope.init();
		});
});
		