/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('IOT370Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,$filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "IOT370Controller";
	
		$scope.init = function(){
			$scope.branch_lock = false;
			var Today=new Date("2016/06/01");
			$scope.inputVO = {
					INS_ID           :'',
					KEYIN_DATE_FROM  :undefined,
					KEYIN_DATE_TO    :undefined,
					BRANCH_NBR       :'',
					CUST_ID          :'',
					POLICY_NO    :'',
					INSURED_ID       :'',
					APPLY_DATE_FROM       :undefined,
					APPLY_DATE_TO       :undefined,
					PPT_TYPE         :''
					
			};
			$scope.startMaxDate = $scope.maxDate;
            $scope.endMinDate = $scope.minDate;
            $scope.paramList2 = [];
            $scope.paramList = [];
		};
		$scope.init();
		
		//英文字母轉大寫
		$scope.text_toUppercase = function(text,type){
			var toUppercase_text = text.toUpperCase();
			switch (type) {
			case 'INSURED_ID':
				$scope.inputVO.INSURED_ID = toUppercase_text;
				break;
			case 'CUST_ID':
				$scope.inputVO.CUST_ID = toUppercase_text;
				break;
			case 'POLICY_NO':
				$scope.inputVO.POLICY_NO = toUppercase_text;
				break;
			case 'INS_ID':
				$scope.inputVO.INS_ID = toUppercase_text;
				break;
			default:
				break;
			}
		}
		
		//險種下拉式選單
		$scope.PPT_TYPE = function(){
			$scope.sendRecv("IOT920","getPPTID","com.systex.jbranch.app.server.fps.iot920.PPTIDDataVO",
        			{},function(tota,isError){
					$scope.mappingSet['PPT_TYPE']=[];
						angular.forEach(tota[0].body.PPTIDData, function(row, index, objs){
							$scope.mappingSet['PPT_TYPE'].push({DATA: row.INSPRD_ID, LABEL: row.INSPRD_NAME});
						});
				});
		}
		$scope.PPT_TYPE();
		
		$scope.inquireInit = function(){
			$scope.initLimit();
			$scope.paramList = [];
			$scope.originalList = [];
		}
		$scope.inquireInit();	
		
		//清除
		$scope.clear = function(){
			$scope.inputVO.INS_ID='';
			$scope.inputVO.KEYIN_DATE_FROM=undefined;
			$scope.inputVO.KEYIN_DATE_TO=undefined;
			$scope.inputVO.CUST_ID='';
			$scope.inputVO.POLICY_NO='';
			$scope.inputVO.INSURED_ID='';
			$scope.inputVO.APPLY_DATE_FROM=undefined;
			$scope.inputVO.APPLY_DATE_TO=undefined;
			$scope.inputVO.PPT_TYPE='';
			if(projInfoService.getAvailBranch().length>1){
				$scope.inputVO.BRANCH_NBR = '';
			}
			$scope.limitDate();
			$scope.limitDate2();
		}
		
		
		  var vo = {'param_type': 'IOT.MAIN_STATUS', 'desc': false};
	        if(!projInfoService.mappingSet['IOT.MAIN_STATUS']) {
	        	$scope.requestComboBox(vo, function(totas) {
	        		if (totas[totas.length - 1].body.result === 'success') {
	        			projInfoService.mappingSet['IOT.MAIN_STATUS'] = totas[0].body.result;
	        			$scope.mappingSet['IOT.MAIN_STATUS'] = projInfoService.mappingSet['IOT.MAIN_STATUS'];
	        		}
	        	});
	        } else {
	        	$scope.mappingSet['IOT.MAIN_STATUS'] = projInfoService.mappingSet['IOT.MAIN_STATUS'];
	        }
	        
		$scope.bgn_sDateOptions2 = {
				maxDate: $scope.maxDate2,
				minDate: $scope.minDate2
			};
			$scope.bgn_eDateOptions2 = {
					maxDate: $scope.maxDate2,
					minDate: $scope.minDate2
				};
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
			
//			$scope.open2 = function($event, elementOpened) {
//				$event.preventDefault();
//				$event.stopPropagation();
//				$scope.model[elementOpened] = !$scope.model[elementOpened];
//			};
			$scope.limitDate = function() {
				$scope.bgn_sDateOptions.maxDate = $scope.inputVO.KEYIN_DATE_TO || $scope.maxDate;
				$scope.bgn_eDateOptions.minDate = $scope.inputVO.KEYIN_DATE_FROM || $scope.minDate;
			};
		
			$scope.limitDate2 = function() {
				$scope.bgn_sDateOptions2.maxDate = $scope.inputVO.APPLY_DATE_TO || $scope.maxDate2;
				$scope.bgn_eDateOptions2.minDate = $scope.inputVO.APPLY_DATE_FROM || $scope.minDate2;
			};
		
	$scope.model = {};
	    	$scope.open = function($event, elementOpened) {
				$event.preventDefault();
				$event.stopPropagation();
				$scope.model[elementOpened] = !$scope.model[elementOpened];
			};
	        
			//分行資訊
			$scope.genBranch = function() {
				$scope.inputVO.branch='';
				$scope.mappingSet['branch'] = [];
				angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){					
						$scope.mappingSet['branch'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
						if(projInfoService.getAvailBranch().length<=1){
							$scope.inputVO.BRANCH_NBR=row.BRANCH_NBR;
							$scope.branch_lock = true;
						}
				});				
	        };
	        $scope.genBranch();
	     
	    	$scope.export = function() {
	    		angular.forEach($scope.csvList, function(row, index, objs){			
	    			var STATUS  =$filter('filter')($scope.mappingSet['IOT.MAIN_STATUS'], {DATA: row.STATUS})[0].LABEL;
	    			var PPT_TYPE=$filter('filter')($scope.mappingSet['PPT_TYPE']   , {DATA: row.PPT_TYPE})[0].LABEL;
	    			row.STATUS=STATUS;
	    			row.PPT_TYPE=PPT_TYPE;
	    		});
	    		$scope.inquire();
				$scope.sendRecv("IOT370", "export",
						"com.systex.jbranch.app.server.fps.iot370.IOT370OutputVO",
						{'list':$scope.csvList}, function(tota, isError) {
							if (!isError) {							
//								$scope.paramList = tota[0].body.resultList;
//								$scope.outputVO = tota[0].body;							
								return;
							}
						});
			};
		
			$scope.inquire = function(){
				$scope.paramList = [];
				$scope.csvList=[];
				$scope.outputVO =[];
				$scope.sendRecv("IOT370", "inquire", "com.systex.jbranch.app.server.fps.iot370.IOT370InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								$scope.paramList = tota[0].body.resultList;
								$scope.csvList=tota[0].body.csvList;
								$scope.outputVO = tota[0].body;
							
								return;
							}
				});
			};
			
		$scope.chnum = function (ind) {		
			$scope.mappingSet[ind.leng]=0;		
			$scope.mappingSet[ind.leng]=ind.leng;
		
		};
		
});
