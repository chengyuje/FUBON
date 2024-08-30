/**
 * 
 */
'use strict';
eSoafApp.controller('KYC610Controller', 
    function($rootScope,$scope,$controller, $confirm, sysInfoService,socketService, ngDialog, projInfoService,$filter,$timeout,getParameter) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "KYC610Controller";
        
        
     // filter
		getParameter.XML(["KYC.PRO_TYPE"], function(totas) {
			if (totas) {
				$scope.mappingSet['KYC.PRO_TYPE'] = totas.data[totas.key.indexOf('KYC.PRO_TYPE')];
			}
		});
		$scope.QUEST_TYPE = [{'LABEL':'專業投資人金融(含債券)專業知識評估', 'DATA': '04'},{'LABEL':'結構型商品專業知識評估', 'DATA': '05'}];				
        
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
		//初始化
        $scope.init = function(){
        	$scope.majorSick = true;
        	$scope.QEight_four = false;
        	$scope.QEight_five = false;
        	$scope.QEight_six = false;
        	$scope.company = false;
        	$scope.noupdatedata = false;
        	$scope.querybirthday = false;
        	$scope.temp_QNineAns = undefined;
			$scope.EDUCATION_LABEL = '';
			$scope.CHILD_NO_LABEL = '';
			$scope.MARRAGE_LABEL = '';
        	$scope.inputVO={
        			CUST_ID:'',
        			CUST_NAME:'',
        			QUEST_TYPE:'',
        			COMP_NAME:'',
        			COMP_NBR:''
        	}
        };
        $scope.init();
        

        //分頁初始化
        $scope.inquireInit = function(){
			$scope.quest_list = [];
			$scope.persional = [];
        };
//        $scope.inquireInit();
        
        //基本資料初始化
        $scope.inipersional = function(){
			$scope.inputVO.cust_name = '';
			$scope.inputVO.cust_id = '';
        }
        
        $scope.Initial = function(){
        	$scope.init();
        	$scope.inipersional();
        	$scope.inquireInit()
        }
        
        $scope.getCustName = function(input , fieldName) {
        	if (input != "") {
    			$scope.sendRecv("KYC610","getCustName","com.systex.jbranch.app.server.fps.kyc610.KYC610InputVO",
    					{"CUST_ID":input},function(tota,isError){
    				if (!isError) {
    					$scope.inputVO[fieldName] = tota[0].body.custName;
    				} else {
    					if (fieldName=='CUST_NAME')
    						$scope.showMsg("取得客戶姓名錯誤");
    					if (fieldName=='COMP_NAME')
    						$scope.showMsg("取得公司名稱錯誤");
    	        		return
    				}
    			});
        		
        	}
        }
        
        //Print quest order by selected quest type
        $scope.printQst = function(printQst){
        	if($scope.inputVO.QUEST_TYPE=="") {
        		$scope.showMsg("至少勾選一種問卷種類");        		
        		return;
        	}
        	if($scope.inputVO.CUST_ID ==''){
        		$scope.showMsg("ehl_01_KYC310_004");
        	}else{
        		$scope.sendRecv("KYC610","printQst","com.systex.jbranch.app.server.fps.kyc610.KYC610InputVO",
	        		$scope.inputVO,function(tota,isError){
        				if (!isError) {
        					$scope.inputVO.resultMap = tota[0].body.resultMap;
        					$scope.inputVO.EXAM_VERSION = tota[0].body.examVersion; 
        	        		$scope.sendRecv("KYC610","getQstPdf","com.systex.jbranch.app.server.fps.kyc610.KYC610InputVO",
        	                		$scope.inputVO,function(tota,isError){
        	        				$scope.showMsg("成功產生問卷");
        	        		});
//        	        		$scope.sendRecv("KYC610","delTmpPdf","com.systex.jbranch.app.server.fps.kyc610.KYC610InputVO",
//        	                		$scope.inputVO,function(tota,isError){
//        	        		});
        				}
        			});

        	}
        }
        
        $scope.ansClear = function(ansList,row){
        	//清除前項選擇項目
        	for(var a=0;a<ansList.length;a++){
        		if(ansList[a].ANSWER_SEQ != row.ANSWER_SEQ){
        			ansList[a].select=undefined;
        		}
        	}
        }
     //end   
	}     
);