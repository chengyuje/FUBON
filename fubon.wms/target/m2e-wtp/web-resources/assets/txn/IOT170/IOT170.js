/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('IOT170Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$controller('PMSRegionController', {$scope: $scope});
		$scope.controllerName = "IOT170Controller";
	
		getParameter.XML(['IOT.REPORT_TYPE','POLICY_STATUS','IOT.SEND_TYPE','IOT.FIRST_PAY_WAY','IOT.WRITE_REASON',
		                  'IOT.REG_TYPE12','IOT.PAY_TYPE','PMS.PAY_YQD','IOT.PRODUCT_TYPE','IOT.MAIN_STATUS','COMMON.YES_NO_12',
		                  'CRM.PAY_WAY','CRM.CRM239_CONTRACT_STATUS','IOT.REPORT_AUTH','COMMON.YES_NO', 'IOT.CM_FLAG','IOT.PAYER_REL_PROPOSER',
		                  'IOT.QC_APEC','IOT.QC_LOAN_CHK','IOT.QC_SIGNATURE'], function(tota) {
			if(tota){
				$scope.mappingSet['IOT.REPORT_AUTH'] = tota.data[tota.key.indexOf('IOT.REPORT_AUTH')];
				$scope.mappingSet['IOT.REPORT_TYPE'] = tota.data[tota.key.indexOf('IOT.REPORT_TYPE')];
				$scope.mappingSet['IOT.POLICY_STATUS'] = tota.data[tota.key.indexOf('IOT.POLICY_STATUS')];
				$scope.mappingSet['IOT.SEND_TYPE'] = tota.data[tota.key.indexOf('IOT.SEND_TYPE')];
				$scope.mappingSet['IOT.FIRST_PAY_WAY'] = tota.data[tota.key.indexOf('IOT.FIRST_PAY_WAY')];
				$scope.mappingSet['IOT.WRITE_REASON'] = tota.data[tota.key.indexOf('IOT.WRITE_REASON')];
				$scope.mappingSet['IOT.REG_TYPE12'] = tota.data[tota.key.indexOf('IOT.REG_TYPE12')];
				$scope.mappingSet['IOT.PAY_TYPE'] = tota.data[tota.key.indexOf('IOT.PAY_TYPE')];
				$scope.mappingSet['PMS.PAY_YQD'] = tota.data[tota.key.indexOf('PMS.PAY_YQD')];
				$scope.mappingSet['IOT.PRODUCT_TYPE'] = tota.data[tota.key.indexOf('IOT.PRODUCT_TYPE')];
				$scope.mappingSet['IOT.MAIN_STATUS'] = tota.data[tota.key.indexOf('IOT.MAIN_STATUS')];
				$scope.mappingSet['CRM.PAY_WAY'] = tota.data[tota.key.indexOf('CRM.PAY_WAY')];
				$scope.mappingSet['COMMON.YES_NO'] = tota.data[tota.key.indexOf('COMMON.YES_NO')];
				$scope.mappingSet['COMMON.YES_NO_12'] = tota.data[tota.key.indexOf('COMMON.YES_NO_12')];
				$scope.mappingSet['IOT.CM_FLAG'] = tota.data[tota.key.indexOf('IOT.CM_FLAG')];
				$scope.mappingSet['IOT.PAYER_REL_PROPOSER'] = tota.data[tota.key.indexOf('IOT.PAYER_REL_PROPOSER')];
				$scope.mappingSet['CRM.CRM239_CONTRACT_STATUS'] = tota.data[tota.key.indexOf('CRM.CRM239_CONTRACT_STATUS')];
				$scope.mappingSet['IOT.QC_APEC'] = tota.data[tota.key.indexOf('IOT.QC_APEC')];
				$scope.mappingSet['IOT.QC_LOAN_CHK'] = tota.data[tota.key.indexOf('IOT.QC_LOAN_CHK')];
				$scope.mappingSet['IOT.QC_SIGNATURE'] = tota.data[tota.key.indexOf('IOT.QC_SIGNATURE')];
				$scope.checkAuth();
			}
		});
		//進件來源下拉選單 INS_SOURCE
		$scope.mappingSet['INS_SOURCE'] = [];
		$scope.mappingSet['INS_SOURCE'].push({LABEL : '請選擇',DATA : ''},{LABEL : '富壽',DATA : 'Y'},{LABEL : '非富壽',DATA : 'N'});
		
		//保險公司下拉選單
		$scope.mappingSet['COMPANY'] = [];
		$scope.inquireCompany = function() {
            $scope.sendRecv("PRD176", "inquire", "com.systex.jbranch.app.server.fps.prd176.PRD176InputVO", {},
                function (tota, isError) {
                    if (!isError) {
                        $scope.mappingSet['COMPANY'] = _.chain(tota[0].body.resultList)
                            .map(each => ({LABEL: each.CNAME, DATA: each.SERIALNUM + ''}))
                            .filter(com => com.DATA !== '82') // 非富壽排除「富邦」
                            .value();
                    }
                });
        }
		$scope.inquireCompany();
		
	    $scope.checkAuth = function(){
		    $scope.mappingSet['Report'] = [];
		    $scope.mappingSet['Report'] = angular.copy($scope.mappingSet['IOT.REPORT_TYPE']);
			for(var i = 0; i< $scope.mappingSet['IOT.REPORT_AUTH'].length;i++){
				if(sysInfoService.getRoleID() == $scope.mappingSet['IOT.REPORT_AUTH'][i]['LABEL']){
					$scope.auth = "Y";
				}
			}
			if($scope.auth != "Y"){
				$scope.mappingSet['Report'].splice(3,1);  //核實報表只有特定權限才看得到
			}
	    }
	    
		$scope.mappingSet['Year'] = [];
		$scope.mappingSet['Month'] = [];
		var NowDate = new Date();
		var yr = NowDate.getFullYear();
		var mm = 1;
	    for(var i=0; i<10; i++){     		
	    	$scope.mappingSet['Year'].push({LABEL: yr +'年', DATA: yr});        
	    	yr--;
	    }
	    for(var i=0; i<12; i++){     		
	    	$scope.mappingSet['Month'].push({LABEL: mm +'月', DATA: mm});        
	    	mm++;
	    }
	    
	    $scope.yMonth = function(){
			var NowDate = new Date();
			var yr = NowDate.getFullYear();
			var mm = NowDate.getMonth() + 2;
			var strmm = '';
			var xm = '';
			$scope.mappingSet['time'] = [];
			for (var i = 0; i < 12; i++) {
				mm = mm - 1;
				if (mm == 0) {
					mm = 12;
					yr = yr - 1;
				}
				if (mm < 10)
					strmm = '0' + mm;
				else
					strmm = mm;
				$scope.mappingSet['time'].push({
					LABEL : yr + '/' + strmm,
					DATA : i+1
				});
			} 
			
			$scope.mappingSet['timeS'] = [];
			$scope.mappingSet['timeS'] =angular.copy($scope.mappingSet['time']);
			
			$scope.mappingSet['timeE'] = [];
			$scope.mappingSet['timeE'] =angular.copy($scope.mappingSet['time']);
			
			$scope.mappingSet['finals'] = [];
			$scope.mappingSet['finals'] =angular.copy($scope.mappingSet['time']);
		};
		$scope.yMonth();
	    
		/***===年月起===***/
	   	$scope.chtimeS = function(){
			var NowDate = new Date();
			var yr = NowDate.getFullYear();
			var mm = NowDate.getMonth() + 1;
			var strmm = '';
			var xm = '';
			if($scope.inputVO.sWorkDate!=0){
				$scope.mappingSet['timeE'] = 		[] ;
				for (var i = 0; i < $scope.inputVO.sWorkDate; i++) {
					$scope.mappingSet['timeE'].push(angular.copy($scope.mappingSet['time'][i]));	
				} 
			}
	
		};
		/***===年月訖===***/
	   	$scope.chtimeE = function(){
				var NowDate = new Date();
				var yr = NowDate.getFullYear();
				var mm = NowDate.getMonth() + 1;
				var strmm = '';
				var xm = '';
				if($scope.inputVO.eWorkDate!=0){
				$scope.mappingSet['timeS'] = angular.copy($scope.mappingSet['time']) ;
				for (var i = 0; i < $scope.inputVO.eWorkDate-1; i++) {
					$scope.mappingSet['timeS'].splice(0,1);	
				} 
			}
				
		};
		
		
        var curDate = new Date();
        /**取工作日**/
		$scope.getBusDt = function(){
			$scope.sendRecv("IOT170", "getBusDate", "com.systex.jbranch.app.server.fps.iot170.IOT170InputVO", {},
					function(tota, isError) {
						if (!isError) {																							
							$scope.busDate = tota[0].body.busDate[0].BUS_DATE;	
							$scope.inputVO.sCreDate = $scope.toJsDate($scope.busDate);
							$scope.inputVO.eCreDate = curDate;
							$scope.outputVO = tota[0].body;														
							return;
						}						
			});
		}
		$scope.getBusDt();
        
        $scope.init = function(){	
			$scope.inputVO = {
					region_center_id: '',   //區域中心
					branch_area_id: '' ,    //營運區
					branch_nbr: '',       //分行	
					emp_id:'',
					insPrdType: '',
					policyNo1: '',
					policyNo2: '',
					policyNo3: '',
					payType: '',
					fstPayWay: '',
					aocode: '',
					policyStatus: '',
					invInsId: '',
					insID: '',
					recruitID: '',
					docStatus: '',
					receiver: '',
					custID: '',					
					sCreDate : undefined,									
					eCreDate : undefined,
					insuredID: '',
					insPrdID: '',
					ReportType : '',
					estIncomeLowLimit : '', //預估  收益金額
					estIncomeUpLimit : '',
					actIncomeLowLimit : '', //實際 收益金額
					actIncomeUpLimit : '',
					WorkYear : '', //保險工作年月
					WorkMonth : '', //保險工作年月
					policyStatus : '', //人壽受理進度
					sWorkDate: '', //計績工作年月
					eWorkDate: '',
					sApplyDate: undefined, //要保書填寫日期
					eApplyDate: undefined,
					sCloseDate: undefined, //結案日區間
					eCloseDate: undefined
        	};
			$scope.disableRegionCombo = false;
			$scope.disableAreaCombo = false;
			$scope.disableBranchCombo = false;
			$scope.disableEmpCombo = false;
			var today = new Date();
			var date = today.getFullYear()+'-'+(today.getMonth()+1)+'-'+today.getDate();
			$scope.inputVO.reportDate = date;
			$scope.RegionController_getORG($scope.inputVO);

		};
		$scope.init();
		
		$scope.RptTypeChange = function(){

			$scope.inputVO.insPrdType= '';
			$scope.inputVO.policyNo1= '';
			$scope.inputVO.policyNo2= '';
			$scope.inputVO.policyNo3= '';
			$scope.inputVO.payType= '';
			$scope.inputVO.fstPayWay= '';
			$scope.inputVO.aocode= '';
			$scope.inputVO.policyStatus= '';
			$scope.inputVO.invInsId= '';
			$scope.inputVO.insID= '';
			$scope.inputVO.recruitID= '';
			$scope.inputVO.docStatus= '';
			$scope.inputVO.receiver= '';
			$scope.inputVO.custID= '';					
			$scope.inputVO.insuredID= '';
			$scope.inputVO.insPrdID= '';
			$scope.inputVO.estIncomeLowLimit = '';
			$scope.inputVO.estIncomeUpLimit = '';
			$scope.inputVO.actIncomeLowLimit = '';
			$scope.inputVO.actIncomeUpLimit = '';
			$scope.inputVO.WorkYear = '';
			$scope.inputVO.WorkMonth = '';
			$scope.inputVO.policyStatus = '';
			$scope.inputVO.sCreDate = undefined;									
			$scope.inputVO.eCreDate = undefined;
			$scope.inputVO.sWorkDate= '';
			$scope.inputVO.eWorkDate= '';
			$scope.inputVO.sApplyDate= undefined;
			$scope.inputVO.eApplyDate= undefined;
			$scope.inputVO.sCloseDate= undefined; 
			$scope.inputVO.eCloseDate= undefined;

			$scope.limitDate();
//			$scope.WorkDate();
			$scope.ApplyDate();
			$scope.CloseDate();
			$scope.paramList = [];
			$scope.export1ResultList = [];
			$scope.totalList = [];
			$scope.outputVO = [];
		}
		
		$scope.inquireInit = function(){
			$scope.paramList = [];
			$scope.export1ResultList = [];
			$scope.totalList = [];
			$scope.outputVO = [];
		}
		$scope.inquireInit();
		
		//英文字母轉大寫
		$scope.text_toUppercase = function(text,type){
			var toUppercase_text = text.toUpperCase();
			switch (type) {
			case 'policyNo1':
				$scope.inputVO.policyNo1 = toUppercase_text;
				break;
			case 'policyNo2':
				$scope.inputVO.policyNo2 = toUppercase_text;
				break;
			case 'policyNo3':
				$scope.inputVO.policyNo3 = toUppercase_text;
				break;
			case 'invInsId':
				$scope.inputVO.invInsId = toUppercase_text;
				break;
			case 'insID':
				$scope.inputVO.insID = toUppercase_text;
				break;
			case 'recruitID':
				$scope.inputVO.recruitID = toUppercase_text;
				break;
			case 'custID':
				$scope.inputVO.custID = toUppercase_text;
				break;
			case 'insuredID':
				$scope.inputVO.insuredID = toUppercase_text;
				break;
			case 'insPrdID':
				$scope.inputVO.insPrdID = toUppercase_text;
				break;
			default:
				break;
			}
		}
        
//		var newDate = new Date(curDate);
		
        /**** 下拉連動END ****/
        
		$scope.open = function($event, index) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope['opened'+index] = true;
		};					
		
		
		/** date picker **/
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
			$scope.bgn_sDateOptions.maxDate = $scope.inputVO.eCreDate || $scope.maxDate;
			$scope.bgn_eDateOptions.minDate = $scope.inputVO.sCreDate || $scope.minDate;
		};
		/*
		$scope.s_WorkDateOptions = {};
		$scope.e_WorkDateOptions = {};
		$scope.openW = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.WorkDate = function() {
			$scope.s_WorkDateOptions.maxDate = $scope.inputVO.eWorkDate;
			$scope.e_WorkDateOptions.minDate = $scope.inputVO.sWorkDate;
			if($scope.inputVO.eWorkDate < $scope.e_WorkDateOptions.minDate)
				$scope.inputVO.eWorkDate = "";
		};
		*/
		$scope.s_AppDateOptions = {};
		$scope.e_AppDateOptions = {};
		$scope.openA = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.ApplyDate = function() {
			$scope.s_AppDateOptions.maxDate = $scope.inputVO.eApplyDate;
			$scope.e_AppDateOptions.minDate = $scope.inputVO.sApplyDate;
			if($scope.inputVO.eApplyDate < $scope.e_AppDateOptions.minDate)
				$scope.inputVO.eApplyDate = "";
		};

		$scope.s_CDateOptions = {};
		$scope.e_CDateOptions = {};
		$scope.openC = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.CloseDate = function() {
			$scope.s_CDateOptions.maxDate = $scope.inputVO.eCloseDate;
			$scope.e_CDateOptions.minDate = $scope.inputVO.sCloseDate;
			if($scope.inputVO.eCloseDate < $scope.e_CDateOptions.minDate)
				$scope.inputVO.eCloseDate = "";
		};
		// date picker end
		
		
		$scope.query = function(){
			if($scope.inputVO.ReportType != "5"){
				//必輸入欄位須擇一填寫
				if( $scope.inputVO.insID=='' && $scope.inputVO.recruitID=='' && $scope.inputVO.insuredID=='' && $scope.inputVO.custID==''&& $scope.inputVO.insPrdID=='' && $scope.inputVO.invInsId=='' && $scope.inputVO.policyNo1 == '' &&
				   ($scope.inputVO.sCreDate == undefined || $scope.inputVO.eCreDate == undefined) && ($scope.inputVO.sApplyDate == undefined || $scope.inputVO.eApplyDate == undefined) &&
				   ($scope.inputVO.sWorkDate == '' || $scope.inputVO.eWorkDate == '') && ($scope.inputVO.sCloseDate == undefined || $scope.inputVO.eCloseDate == undefined) ){
							$scope.showErrorMsg('ehl_01_common_022');
							return;
				}			
				if($scope.inputVO.sCreDate != undefined && $scope.inputVO.eCreDate != undefined){
					//鍵機日不得超過三個月
					var sDate_add_month = new Date($scope.inputVO.sCreDate).setMonth(new Date($scope.inputVO.sCreDate).getMonth()+3);
					var sDate_add_day = new Date(sDate_add_month).setDate(new Date(sDate_add_month).getDate()+1);
					if($scope.inputVO.eCreDate >= sDate_add_day){
						$scope.showErrorMsg('日期起迄區間不得超過3個月！');
						return;
					}
				}
			} 

			var queryType = '';
			switch($scope.inputVO.ReportType){
				case "1" :
				case "2" :
					queryType = 'queryData';
					break;
				case "3":
					queryType = 'queryStatus';
					break;
				case "4":
					queryType = 'queryReport';
					break;
				case "5":
					queryType = 'queryExRate';
					break;
			}
			$scope.sendRecv("IOT170", queryType , "com.systex.jbranch.app.server.fps.iot170.IOT170InputVO", $scope.inputVO,
			    function(tota, isError) {
					if (!isError) {			
						if(tota[0].body.resultList.length == 0){
							$scope.showMsg("ehl_01_common_009");
							return;
						}
						$scope.paramList = tota[0].body.resultList;
						$scope.export1ResultList = tota[0].body.export1ResultList;
						$scope.regionCenterList = tota[0].body.regionList; //處
						$scope.branchAreaList = tota[0].body.areaList;	   //區
						$scope.totalList = tota[0].body.totalList;         //全行
						$scope.outputVO = tota[0].body;
						return;
					}						
			});
		}
		
		
		/** 開啟彈跳視窗 **/
		$scope.getDetail = function(row, type){			
			var tp, cn = '';				
			if(type == 'tar'){
				tp = 'assets/txn/IOT170/IOT170_INV_DETAIL.html';
				cn = 'IOT170_INV_DETAIL';
			}
			if(type == 'rider'){
				tp = 'assets/txn/IOT170/IOT170_RIDER_DETAIL.html';
				cn = 'IOT170_RIDER_DETAIL';
			}	
			var dialog = ngDialog.open({
				template: tp,
				className: cn,
				controller:['$scope', function($scope){
					$scope.row = row;
				}]
			});			
		};
		
		/** 開啟彈跳視窗 **/
		$scope.getRptDetail = function(row,val, brh,cls,type){

			if(val == 0){
				$scope.showMsg("ehl_01_common_009");
				return;
			}
			var dialog = ngDialog.open({
				template: 'assets/txn/IOT170/IOT170_REPORT_DETAIL.html',
				className: 'IOT170_REPORT_DETAIL',
				controller:['$scope', function($scope){
					$scope.row = row;
					$scope.type = type;
					$scope.brh = brh;
					$scope.cls = cls;
				}]
			});			
		};
		
		/** 匯出CSV **/
		$scope.exportRPT = function(){
			$scope.sendRecv("IOT170", "export", "com.systex.jbranch.app.server.fps.iot170.IOT170OutputVO", 
					{'resultList' : $scope.paramList,
					 'export1ResultList' : $scope.export1ResultList,
					 'totalList'  : $scope.totalList,
					 'areaList'   : $scope.branchAreaList,
					 'regionList' : $scope.regionCenterList,
					 'ReportType' : $scope.inputVO.ReportType },function(tota, isError) {
							if (isError) {			
			            		$scope.showErrorMsgInDialog(tota[0].body.msgData);	
			            		return;
			            	}
							return;
			});
		};	
		
		
        $scope.getRegionTotal = function(region, key){
        	if($scope.inputVO.region_center_id == ""){
        		switch(region){
        		case "個金分行業務一處":
        			return $scope.regionCenterList[0][key];
        		case "個金分行業務二處":
        			return $scope.regionCenterList[1][key];
        		case "個金分行業務三處":
        			return $scope.regionCenterList[2][key];
        		}
        	}else{
        		return $scope.regionCenterList[0][key];
        	}
        };
        
        $scope.getBranchTotal = function(region, key){
			if($scope.inputVO.branch_area_id != ""){
				return $scope.branchAreaList[0][key];
			}else{
				for(var i = 0; i < $scope.branchAreaList.length ; i++){
					if(region[0]['BRANCH_AREA_ID'] == $scope.branchAreaList[i]['BRANCH_AREA_ID']){
						return $scope.branchAreaList[i][key];
					}
				}
			}
		};
		
        $scope.numGroups = function(input){
  		  var i=0;    
              for(var key in input) i++;      
  			return i;
  		};
		
});
