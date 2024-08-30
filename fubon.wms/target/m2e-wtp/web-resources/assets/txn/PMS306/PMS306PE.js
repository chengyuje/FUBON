/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS306PEController',
	function($rootScope, $scope, $controller, $confirm, sysInfoService,socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS306PEController";
		//  幣別
		var vo = {'param_type': 'FPS.CURRENCY', 'desc': false};
        if(!projInfoService.mappingSet['FPS.CURRENCY']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['FPS.CURRENCY'] = totas[0].body.result;
        			$scope.mappingSet['FPS.CURRENCY'] = projInfoService.mappingSet['FPS.CURRENCY'];
        		}
        	});
        } else {
        	$scope.mappingSet['FPS.CURRENCY'] = projInfoService.mappingSet['FPS.CURRENCY'];
        }
		
		//繳別
		var vo = {'param_type': 'PMS.PAY_YQD', 'desc': false};
        if(!projInfoService.mappingSet['PMS.PAY_YQD']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['PMS.PAY_YQD'] = totas[0].body.result;
        			$scope.mappingSet['PMS.PAY_YQD'] = projInfoService.mappingSet['PMS.PAY_YQD'];
        		}
        	});
        } else {
        	$scope.mappingSet['PMS.PAY_YQD'] = projInfoService.mappingSet['PMS.PAY_YQD'];
        }
			
	
		// 預設的filter
		$scope.init = function(){
			$scope.inputVO = {					
					INS_NBR:'',   //代碼
					PAY_TYPE:'',      //沖正繳費年期 
					REAL_PREMIUM :'',     //沖正實收保費(原幣)
					CURR_CD :'',     //CURR_CD 
					INSFNO:''  //保險文件編號
        	};
			$scope.paramList = [];	
			$scope.originalList = [];
			$scope.outputVO = {};
		};
        $scope.init();
        
        /*****檢查原資料是否被編輯過******/
        $scope.checked = function(){
//        	debugger
        	var oary = $scope.originalList;
        	var pary= $scope.paramList;
        	var nary = oary.sort(function(a, b) {return a.INS_ID > b.INS_ID?1:-1;});
        	
        	for(var p = 0 ; p < pary.length-1 ; p++){
    			if(pary[p+1].TX_TYPE == 'A' && (pary[p+1].INS_ID == pary[p].INS_ID) ){
    				pary[p].AD = '0';
    			}
        	}
        	
        	for(var i = 0 ; i < nary.length-1 ; i++){
    			if( i-1 >= 0 ){
    				if((nary[i].INS_ID == nary[i-1].INS_ID || nary[i].INS_ID == nary[i+1].INS_ID) && nary[i].TX_TYPE == 'B'){
    					nary[i].AD = '4';
    				}
    			}
        	}
        	
        	for(var j = 0 ; j < nary.length ; j++){
        		if(nary[j].AD == '4'){
        			for(var k = 0 ; k < pary.length ; k++){
        				if(nary[j].INS_ID == pary[k].INS_ID){
        					pary[k].FF = '4';
        				}
        			}
        		}
        	}
        }
        	
        /*****查詢資料******/
        $scope.inquire = function(){
        	$scope.paramList = [];	
			$scope.originalList = [];
			$scope.outputVO = {};
			
        	if($scope.parameterTypeEditForm.$invalid){
        		$scope.showErrorMsg("ehl_01_common_022");
        		return;
        	}
        	
			$scope.sendRecv("PMS306", "querype", "com.systex.jbranch.app.server.fps.pms306.PMS306InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.personlist.length == 0) {
								$scope.paramList=[];
								$scope.showMsg("ehl_01_common_009");								
	                			return;
	                		}
							
							$scope.paramList = tota[0].body.personlist;
							$scope.originalList = tota[0].body.totalList;
							$scope.outputVO = tota[0].body;
							
							angular.forEach($scope.paramList,function(row,index,objs){
							   if(row.TX_TYPE=='M'){
								   row.FF='1';
							   }
							   
							});
							$scope.checked();
							return;
						}
			});
		};
//		
		//更換資料
		 $scope.changes=function(row,index){
			 
			row.index='1';  //設定
			
			var original = $scope.paramList[index];
			original.AD = '0';
			
			row.INS_NBR=row.PRD_ID;

			var coy=angular.copy($scope.paramList[index]);
			coy.AD='1';

				$scope.paramList.splice(index+1,0,coy);
				return true;
		 }
		 
		 $scope.copys=function(row,index){
			 
			row.index='2';  //設定
			
			row.INS_NBR=row.PRD_ID;
			
			var coy=angular.copy($scope.paramList[index]);
			coy.AD='2';
			
			var coy2 = angular.copy($scope.paramList[index-1]);
			coy2.AD = '3';
			
			$scope.paramList.splice(index+1,0,coy2);
			$scope.paramList.splice(index+2,0,coy);
		 }
		 
		 $scope.getRecruitInfo = function(row) {
			 $scope.sendRecv("PMS306", "getRecruitInfo", "com.systex.jbranch.app.server.fps.pms306.PMS306InputVO", {'recruitId':row.RECRUIT_ID, 'keyinDate':row.KEYIN_DATE},
				function(tota, isError) {
					if (!isError) {
						debugger
						if(tota[0].body.recruitList.length == 0) {
							row.RECRUIT_ID = "";
							$scope.showMsg("ehl_01_common_009");								
		           			return;
		           		}
								
						row.RECRUIT_IDNBR = tota[0].body.recruitList[0].CUST_ID;
						row.RECRUIT_NAME = tota[0].body.recruitList[0].EMP_NAME;
						row.BRANCH_NBR = tota[0].body.recruitList[0].DEPT_ID;
						row.BRANCH_NAME = tota[0].body.recruitList[0].DEPT_NAME;
						row.AO_CODE = tota[0].body.recruitList[0].AO_CODE;
					}
				}); 
		 };
		 
		   /*****儲存人壽資料*****/
	        $scope.save = function(row,index){  
	        	$scope.inputVO.INSFNO=row.INS_ID;
	        	$scope.inputVO.PRD_ID=row.PRD_ID;
	        	$scope.inputVO.MOP2 = row.MOP2;
	        	$scope.inputVO.REAL_PREMIUM=row.REAL_PREMIUM;
	        	$scope.inputVO.CURR_CD=row.CURR_CD;    //CURR_CD
	        	
	        	$scope.inputVO.RECRUIT_ID = row.RECRUIT_ID;
	        	$scope.inputVO.RECRUIT_IDNBR = row.RECRUIT_IDNBR;
	        	$scope.inputVO.RECRUIT_NAME = row.RECRUIT_NAME;
	        	$scope.inputVO.BRANCH_NBR = row.BRANCH_NBR;
	        	$scope.inputVO.BRANCH_NAME = row.BRANCH_NAME;
	        	$scope.inputVO.AO_CODE = row.AO_CODE;
				
	        	if(($scope.paramList[index-1].INS_ID==row.INS_ID)&&($scope.paramList[index-1].TX_TYPE=='M')){
	        		
	        		$scope.inputVO.TX_TYPE=$scope.paramList[index-1].TX_TYPE;
	        	}else{
		        	$scope.inputVO.TX_TYPE=row.TX_TYPE;
	        	}
	        
	        	
	        	if($scope.inputVO.PRD_ID == undefined || $scope.inputVO.PRD_ID == null || $scope.inputVO.PRD_ID == "") {
	        		$scope.showErrorMsgInDialog("險種代碼為必填");	
	        		return;
	            }	
	        	
	        	if($scope.inputVO.RECRUIT_ID == undefined || $scope.inputVO.RECRUIT_ID == null || $scope.inputVO.RECRUIT_ID == ""){
	        		
	        		$scope.showErrorMsgInDialog("招攬人員為必填");	
	        		return;
	            }	
	        		
				$scope.sendRecv("PMS306", "savePe", "com.systex.jbranch.app.server.fps.pms306.PMS306InputVO", {'listper':$scope.inputVO, 'list2':$scope.originalList},
						function(tota, isError) {
					
							if (isError) {
			            		$scope.showErrorMsgInDialog(tota[0].body.msgData);
			            		return;
			            	}
							
			            	if (tota.length > 0) {
			            		
			            		$scope.showMsg('儲存成功');
			            		
			            		row.FF='3';
			            		row.index='0';
			            		$scope.inquire();  //關掉後重新查詢
			            	};		
				});
			};
			
			$scope.clean=function(row,index){
				row.AD='';
				row.index='0';	
				$scope.paramList.splice(index,1);
			}
			
			$scope.clnce=function(row,index){
				row.AD='';
				row.index='0';	
				$scope.paramList.splice(index,1);
				var original = $scope.paramList[index-1];
				original.AD = null;
			}
			
			$scope.clnce2=function(row,index){
				row.AD='';
				row.index='0';	
				$scope.paramList.splice(index,1);
				$scope.paramList.splice(index-1,1);
				var original = $scope.paramList[index-2];
				original.AD = null;
			}
		
			/** ====== 刪除資料庫人工調整作業資料 ======= */
			$scope.delPe = function(row){
				$scope.inputVO.INSFNO=row.INS_ID;
				$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function(){
					$scope.sendRecv("PMS306", "delPeData", "com.systex.jbranch.app.server.fps.pms306.PMS306InputVO",{'listper':$scope.inputVO, 'list2':$scope.originalList},
							function(totas, isError) {
												
								if (isError) {
									$scope.showErrorMsg(totas[0].body.msgData);
									return;
								}
                    	
								if (totas.length > 0) {
									
									$scope.showSuccessMsg('刪除成功');
									$scope.inquire();
                    	};
                    	
    				})
				});
			}
		
});
