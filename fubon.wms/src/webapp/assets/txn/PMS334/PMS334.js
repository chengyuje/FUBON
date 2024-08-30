/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS334Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS334Controller";
		
		/*** 可示範圍  JACKY共用版  START ***/
		$controller('PMSRegionController', {$scope: $scope});
		$scope.dateChange = function(){
        	$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
        	$scope.RegionController_getORG($scope.inputVO);
		};
		//月報選單
        var NowDate = new Date();
        if(NowDate.getMonth()<10){
        	var ym = NowDate.getFullYear() + '0' + NowDate.getMonth();
        }else if(NowDate.getMonth() >= 10){
        	var ym = NowDate.getFullYear() + '' + NowDate.getMonth();
        }
        $scope.initLoad = function(){
			$scope.sendRecv("PMS000", "getLastYMlist", "com.systex.jbranch.app.server.fps.pms000.PMS000InputVO", {},
					   function(totas, isError) {
				             	if (totas.length > 0) {
				               		$scope.ymList = totas[0].body.ymList;
				               		$scope.inputVO.sCreDate = $scope.ymList[1].DATA;
				               	};
					   }
			);
		}
		$scope.initLoad();
		
		
		$scope.init = function(){
			$scope.inputVO = {
					sCreDate : ym,
					region_center_id : '',
					branch_area_id : '',
					branch_nbr : '',
					ao_code : '',
					emp_id : ''			
			};
			$scope.dateChange();
			$scope.disableRegionCombo = false;
			$scope.disableAreaCombo = false;
			$scope.disableBranchCombo = false;
			$scope.disableAoCombo = false;
            $scope.paramList = [];  
		};
		$scope.init();
		
		$scope.numGroups = function(input){
        	if(input == null)
        		return;
            return Object.keys(input).length;
           }
         
        $scope.getSum = function(group, key) {
            var sum = 0;
            for (var i = 0; i < group.length; i++){
             sum += group[i][key];
            }  
            return sum;
           }
		
		/*** 可示範圍  JACKY共用版  END***/
		
		$scope.inquire = function(){
			if($scope.inputVO.sCreDate == ''){
				$scope.showMsg("欄位檢核錯誤：報表月份為必要欄位");
				return;
			}
			$scope.sendRecv("PMS334", "inquire", "com.systex.jbranch.app.server.fps.pms334.PMS334InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
							
								$scope.paramList = [];
								$scope.csvList=[];
								$scope.outputVO = [];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
//							$scope.paramList = tota[0].body.csvList;
							$scope.csvList=tota[0].body.csvList;
							$scope.outputVO = tota[0].body;
							
						
							$scope.MAX_CUST_CNT_TOT       =0;
							$scope.MAX_AUM            =0;
							$scope.TOT_CUST_CNT       =0;
							$scope.TOT_CUST_PCTG      =0;
							$scope.TOT_NOT_IN_CTRL    =0;
							$scope.TOT_AUM            =0;
							$scope.TOT_CUST_DIFF      =0;
							$scope.V_CUST_CNT     =0;
							$scope.V_NOT_IN_CTRL  =0;
							$scope.V_AUM          =0;
							$scope.V_CUST_DIFF    =0;
							$scope.A_CUST_CNT        =0;
							$scope.A_NOT_IN_CTRL     =0;
							$scope.A_AUM             =0;
							$scope.A_CUST_DIFF       =0;
							$scope.B_CUST_CNT       =0;
							$scope.B_NOT_IN_CTRL    =0;
							$scope.B_AUM            =0;
							$scope.B_CUST_DIFF      =0;
							$scope.M_CUST_CNT       =0;
							$scope.M_NOT_IN_CTRL    =0;
							$scope.M_AUM            =0;
							$scope.M_CUST_DIFF      =0;
							var inddex=0;
	                		angular.forEach($scope.paramList, function(row, index, objs){
	                			$scope.MAX_CUST_CNT_TOT      +=row.MAX_CUST_CNT_TOT      ;  
	                			$scope.MAX_AUM           +=row.MAX_AUM           ;
	                			$scope.TOT_CUST_CNT      +=row.TOT_CUST_CNT      ;
	                			$scope.TOT_CUST_PCTG     +=row.TOT_CUST_PCTG     ;
	                			$scope.TOT_NOT_IN_CTRL   +=row.TOT_NOT_IN_CTRL   ;
	                			$scope.TOT_AUM           +=row.TOT_AUM           ;
	                			$scope.TOT_CUST_DIFF     +=row.TOT_CUST_DIFF     ;
	                			$scope.V_CUST_CNT    +=row.V_CUST_CNT    ;
	                			$scope.V_NOT_IN_CTRL +=row.V_NOT_IN_CTRL ;
	                			$scope.V_AUM         +=row.V_AUM         ;
	                			$scope.V_CUST_DIFF   +=row.V_CUST_DIFF   ;
	                			$scope.A_CUST_CNT       +=row.A_CUST_CNT       ;
	                			$scope.A_NOT_IN_CTRL    +=row.A_NOT_IN_CTRL    ;
	                			$scope.A_AUM            +=row.A_AUM            ;
	                			$scope.A_CUST_DIFF      +=row.A_CUST_DIFF      ;
	                			$scope.B_CUST_CNT      +=row.B_CUST_CNT      ;
	                			$scope.B_NOT_IN_CTRL   +=row.B_NOT_IN_CTRL   ;
	                			$scope.B_AUM           +=row.B_AUM           ;
	                			$scope.B_CUST_DIFF     +=row.B_CUST_DIFF     ;
	                			$scope.M_CUST_CNT      +=row.M_CUST_CNT      ;
	                			$scope.M_NOT_IN_CTRL   +=row.M_NOT_IN_CTRL   ;
	                			$scope.M_AUM           +=row.M_AUM           ;
	                			$scope.M_CUST_DIFF     +=row.M_CUST_DIFF     ;
	                			inddex=index;
	            			});
	                		$scope.TOT_CUST_PCTG=$scope.MAX_CUST_CNT_TOT/$scope.TOT_CUST_CNT*100;
						
							return;
						}
			});
		};
		
		/**匯出報表**/
		$scope.exportRPT = function(){
			$scope.sendRecv("PMS334", "export", "com.systex.jbranch.app.server.fps.pms334.PMS334InputVO", $scope.inputVO,
					function(tota, isError) {						
						if (isError) {
		            		$scope.showErrorMsgInDialog(tota[0].body.msgData);		            		
		            	}
						if (tota.length > 0) {
	                		if(tota[0].body.resultList && tota[0].body.resultList.length == 0) {
	                			$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
	                	};
			});
		};
		/*	
		$scope.detail = function (ind,row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/PMS334/PMS334_DETAIL.html',
				className: 'PMS334_DETAIL',
				showClose: false,
                controller: ['$scope', function($scope) {
                	row.ind=ind;
                	$scope.row = row;
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
				
					$scope.inquireInit();
					$scope.inquire();
				
				}
			});
		};
		*/
		
		// 17-08-29 Juan: delete for no use
		// document.getElementById("wrap").addEventListener("scroll",function(){
		// 	   var translate = "translate(0,"+this.scrollTop+"px)";
		// 	   if(/MSIE 9/.test(navigator.userAgent))
		// 	      this.querySelector("tr").style.msTransform = translate;
		// 	   else
		// 	      this.querySelector("tr").style.transform = translate;
		// });
		
});
