/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('IOT190Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "IOT190Controller";
		
		$scope.memLoginFlag = String(sysInfoService.getMemLoginFlag()).toUpperCase();
			
		/*
		 * 取得UHRM人員清單(由員工檔+角色檔)
		 */
		$scope.sendRecv("ORG260", "getUHRMList", "com.systex.jbranch.app.server.fps.org260.ORG260InputVO", $scope.inputVO, 
				function(tota, isError) {
					if (isError) {
						return;
					}
					if (tota.length > 0) {
						$scope.mappingSet['UHRM_LIST'] = tota[0].body.uhrmList;
						if ($scope.mappingSet['UHRM_LIST'].length >= 1 && $scope.priID == 'UHRM002') {
							$scope.inputVO.uEmpID = $scope.mappingSet['UHRM_LIST'][0].DATA;
						} else {
							$scope.inputVO.uEmpID = '';
						}
					}
		});
		
		// filter
		
		//IOT.OTH_TYPE
		
		$scope.mappingSet['IOT.OTH_TYPE']=[];
        var vo = {'param_type': 'IOT.OTH_TYPE', 'desc': false};
        if(!projInfoService.mappingSet['IOT.OTH_TYPE']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['IOT.OTH_TYPE'] = totas[0].body.result;	        		
        			$scope.mappingSet['IOT.OTH_TYPE'] = projInfoService.mappingSet['IOT.OTH_TYPE'];	        			
        		}
        	});
        } else{	        
        	$scope.mappingSet['IOT.OTH_TYPE'] = projInfoService.mappingSet['IOT.OTH_TYPE']; 
        }
	        
        $scope.mappingSet['IOT.MAIN_STATUS']=[];
        var vo = {'param_type': 'IOT.MAIN_STATUS', 'desc': false};
        if(!projInfoService.mappingSet['IOT.MAIN_STATUS']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['IOT.MAIN_STATUS'] = totas[0].body.result;
        			$scope.mappingSet['IOT.MAIN_STATUS'] = projInfoService.mappingSet['IOT.MAIN_STATUS'];
        		}
        	});
        } else{
        	$scope.mappingSet['IOT.MAIN_STATUS'] = projInfoService.mappingSet['IOT.MAIN_STATUS'];        
        }
        
        //要保書類型/戶況檢核
    	getParameter.XML(["IOT.PREMIUM_USAGE", "IOT.PAYER_REL_PROPOSER", "IOT.PAY_WAY"],function(totas){
    		if(totas){
    			//戶況檢核
    			$scope.mappingSet['IOT.PREMIUM_USAGE'] = totas.data[totas.key.indexOf('IOT.PREMIUM_USAGE')];
    			$scope.mappingSet['IOT.PAYER_REL_PROPOSER'] = totas.data[totas.key.indexOf('IOT.PAYER_REL_PROPOSER')];
    			$scope.mappingSet['IOT.PAY_WAY'] = totas.data[totas.key.indexOf('IOT.PAY_WAY')];
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
		
		$scope.init = function(){
		
			$scope.inputVO = {
					BRANCH_AREA_ID   :'',          //區別
					BRANCH_AREA_NAME :'',         //區別名
					INSPRD_ID        :'',        //險種代碼
					BRANCH_NBR       :'',        //分行
					BRANCH_NAME      :'',        //分行名稱
					KEYIN_DATE_B     :undefined, //鍵機日(起)
					KEYIN_DATE_E     :undefined, //鍵機日(迄)
					INS_ID           :'',        //保險文件編號
					REG_TYPE         :'',        //送件類型
					STATUS           :'',        //文件簽收狀態
					CUST_ID          :'',        //要保人ID
					POLICY_NO1       :'',        //保單號碼1
					POLICY_NO2       :'',        //保單號碼2
					POLICY_NO3       :'',        //保單號碼3
					INSURED_ID       :'',        //被保人/立約人ID
					FXD_PRODNAME     :'',        //適用專案
					PROD_PERIOD      :'',         //商品檔期
					FB_COM_YN		 :'',
					COMPANY_NUM	 	 :''
			};
				
			if ($scope.memLoginFlag.startsWith('UHRM') && $scope.memLoginFlag != 'UHRM') {
				$scope.inputVO.region_center_id = '031';
				$scope.inputVO.branch_area_id = '031A';
			}
		};
		$scope.init();
		
		//分行資訊
		$scope.genBranch = function() {
			$scope.inputVO.BRANCH_NBR='';
			$scope.mappingSet['branch'] = [];
			angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){
				if(row.BRANCH_AREA_ID == $scope.inputVO.BRANCH_AREA_ID){
					$scope.mappingSet['branch'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
				}if($scope.inputVO.BRANCH_AREA_ID == ''){
					$scope.mappingSet['branch'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
				}
			});		
			
			if($scope.mappingSet['branch'].length == 1 && $scope.inputVO.BRANCH_AREA_ID != '')
				$scope.inputVO.BRANCH_NBR = $scope.mappingSet['branch'][0].DATA;
       };
        
   	
       //區域資訊
       $scope.genArea = function() {
    	   $scope.inputVO.BRANCH_AREA_ID='';
    	   $scope.mappingSet['op'] = [];
    	   angular.forEach(projInfoService.getAvailArea(), function(row, index, objs){					
    		   $scope.mappingSet['op'].push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});			
    	   });
			
    	   if($scope.mappingSet['op'].length == 1)
    		   $scope.inputVO.BRANCH_AREA_ID = $scope.mappingSet['op'][0].DATA;

    	   $scope.genBranch();
      };	
      $scope.genArea();
		
      $scope.clear = function(){
    	  $scope.inputVO.BRANCH_AREA_NAME='';
    	  $scope.inputVO.INSPRD_ID='';
    	  $scope.inputVO.BRANCH_NAME='';
    	  $scope.inputVO.KEYIN_DATE_B=undefined;
    	  $scope.inputVO.KEYIN_DATE_E=undefined;
    	  $scope.inputVO.INS_ID='';
    	  $scope.inputVO.REG_TYPE='';
    	  $scope.inputVO.STATUS='';
    	  $scope.inputVO.CUST_ID='';
    	  $scope.inputVO.POLICY_NO1='';
    	  $scope.inputVO.POLICY_NO2='';
    	  $scope.inputVO.POLICY_NO3='';
    	  $scope.inputVO.INSURED_ID='';
    	  $scope.inputVO.FXD_PRODNAME='';
    	  $scope.inputVO.PROD_PERIOD='';
    	  $scope.inputVO.FB_COM_YN='';
    	  $scope.inputVO.COMPANY_NUM='';
      }
      
      /**==類型年月==**/
      $scope.mappingSet['type']=[];
      $scope.mappingSet['type'].push({
    	  DATA : '999',
    	  LABEL : '網銀'
      },{
    	  DATA : '998',
    	  LABEL : '行銀'
      },{
    	  DATA : '3',
    	  LABEL : '分行(代號+名稱)'
      });
      
      // 有效起始日期
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
    	  $scope.bgn_sDateOptions.maxDate = $scope.inputVO.KEYIN_DATE_E || $scope.maxDate;
    	  $scope.bgn_eDateOptions.minDate = $scope.inputVO.KEYIN_DATE_B || $scope.minDate;
      };	
       
      $scope.inquireInit = function(){
    	  $scope.paramList = [];
      }
      $scope.inquireInit();
		
      //欄位檢核
      $scope.check_Field = function(){
    	  if($scope.inputVO.INS_ID != '' || $scope.inputVO.CUST_ID != '' ||	//保險文件編號 || 要保人ID
    		 $scope.inputVO.POLICY_NO1 != '' || 	//保單號碼
    		 ($scope.inputVO.KEYIN_DATE_B != undefined && $scope.inputVO.KEYIN_DATE_E != undefined)){	//鍵機日
    		  if($scope.inputVO.KEYIN_DATE_B != undefined && $scope.inputVO.KEYIN_DATE_E != undefined && parseInt(($scope.inputVO.KEYIN_DATE_E-$scope.inputVO.KEYIN_DATE_B)/86400000) > 90){
    			  $scope.showErrorMsg('日期起迄區間不得超過90天！');
    			  return false;
    		  }else{
    			  return true;
    		  }
    	  }else{
    		  $scope.showErrorMsg('ehl_01_common_022');
    		  return false;
    	  }
      }

      /********查詢********/
      $scope.query = function(){
    	  if($scope.check_Field()){
    		  $scope.paramList = [];	
    		  $scope.printList = [];
    		  $scope.outputVO = [];
    		  $scope.sendRecv("IOT190", "inquire", "com.systex.jbranch.app.server.fps.iot190.IOT190InputVO", $scope.inputVO, function(tota, isError) {
    			  if (!isError) {
    				  if(tota[0].body.resultList.length == 0) {
    					  $scope.showMsg("ehl_01_common_009");
    					  return;
    				  }							
    				  $scope.paramList = tota[0].body.resultList;	
    				  $scope.printList = tota[0].body.list;
    				  $scope.outputVO = tota[0].body;			
								
    				  return;
    			  }					
    		  });
    	  }
      };
		
      //列印 產生PDF檔
      $scope.print = function() {
    	  angular.forEach($scope.printList, function(row, index, objs){	            
    		  angular.forEach($scope.mappingSet['IOT.MAIN_STATUS'], function(rows, indexs, objss){
    			  if(rows.DATA==row.STATUS){
    				  //設定狀態資訊
    				  row.STATUS=rows.LABEL;
    			  }
    		  });
    	  });
    		
    	  $scope.sendRecv("IOT190", "print", "com.systex.jbranch.app.server.fps.iot190.IOT190OutputVO", {'list':$scope.printList}, function(tota, isError) {
    		  if (!isError) {		
    			  return;
    		  }
    	  });
      };
});
