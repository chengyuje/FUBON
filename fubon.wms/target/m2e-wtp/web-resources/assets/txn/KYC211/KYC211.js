/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('KYC211Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "KYC211Controller";
		var date = new Date();
		// date picker
		$scope.bgn_sDateOptions = {
				maxDate: $scope.maxDate,
				minDate: date
			};

		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
        $scope.mapData = function(){
			//是/否
        	$scope.ngDatasource = projInfoService.mappingSet["COMMON.YES_NO"];
			var comboboxInputVO = {'param_type': "COMMON.YES_NO", 'desc': false};
			$scope.requestComboBox(comboboxInputVO, function(totas) {
					$scope.YNlist = totas[0].body.result;
			});

        }
        $scope.mapData();
		
        getParameter.XML(["SYS.QUESTION_TYPE"], function(totas) {
 			if (totas) {
 				$scope.Q_TYPE = totas.data[totas.key.indexOf('SYS.QUESTION_TYPE')];
			}
 		});
        
        $scope.mappingSet['SCORE_TYPE'] = [];
	    $scope.mappingSet['SCORE_TYPE'].push({LABEL:"W",DATA:"W"},{LABEL:"C",DATA:"C"})
		
        $scope.editCancel = function(){
        	$scope.inputVO.QUEST_TYPE='';
        	$scope.inputVO.EXAM_NAME='';
        	$scope.inputVO.ACTIVE_DATE=undefined;
        	
        }
        
		$scope.init = function(){
			$scope.pr_date=true;
			$scope.pr_date_type=true;
			$scope.ae=true;
			switch ($scope.titleType) {
			case 'P':
				$scope.inputVO = $scope.row;
				$scope.inputVO.copy=false;
				$scope.preview();
				$scope.title='預覽問卷';
				$scope.field_type=true;
				$scope.pr_date_type=false;
				$scope.pr_date=false;
				break;
			case 'A':
				$scope.title='新增問卷';
				$scope.inputVO.EXAM_VERSION=$scope.exam_version;
				$scope.inputVO.copy=false;
				$scope.pr_date=false;
				$scope.ae=false;
				$scope.inputVO.DEL_QUESTION = [];
				break;
			case 'U':
				$scope.inputVO = $scope.row;
				$scope.inputVO.copy=false;
				$scope.update();
				$scope.title='修改問卷';
				$scope.ae=false;
				$scope.save_update = true;
				$scope.inputVO.DEL_QUESTION = [];
				break;
			case 'C':
				$scope.inputVO = $scope.row;
				$scope.inputVO.copy=true;
				$scope.update();
				$scope.inputVO.EXAM_VERSION=$scope.exam_version;
				$scope.inputVO.ACTIVE_DATE=undefined;
				$scope.title='修改問卷';
				$scope.ae=false;
				$scope.inputVO.DEL_QUESTION = [];
				break;
			default:
				break;
			}
		}
		//預覽問卷
		$scope.preview = function(){
			var date = $scope.inputVO.ACTIVE_DATE;
			var active_date = new Date(date);
			$scope.inputVO.ACTIVE_DATE = active_date;
			$scope.sendRecv("KYC211","addQuestionnaireInitial","com.systex.jbranch.app.server.fps.kyc211.KYC211InputVO",
					$scope.inputVO,function(tota,isError){
					$scope.preview_data = tota[0].body.questionList;
					$scope.inputVO.QUEST_TYPE = $scope.preview_data[0].QUEST_TYPE;
					$scope.Remark = $scope.preview_data[0].Remark;
					$scope.inputVO.INT_SCORE = $scope.preview_data[0].INT_SCORE;
	
					$scope.inputVO.W_VAL = [];
					$scope.inputVO.C_VAL = [];
					$scope.inputVO.CUST_RL_ID = [];
					
					angular.forEach($scope.preview_data[0].W_VAL,function(row,index){
						$scope.inputVO.W_VAL[index] = row.W_VAL;
					});
					
					angular.forEach($scope.preview_data[0].C_VAL,function(row,index){
						$scope.inputVO.C_VAL[index] = row.C_VAL;
					});
					
					angular.forEach($scope.preview_data[0].CW_CUST_RL_ID,function(row){
						$scope.inputVO.CUST_RL_ID[row.W_LEVEL] = {};
					});
					angular.forEach($scope.preview_data[0].CW_CUST_RL_ID,function(row){
						$scope.inputVO.CUST_RL_ID[row.W_LEVEL][row.C_LEVEL] = row.CUST_RL_ID;
					});							

			});
		}
		//修改問卷
		$scope.update = function(){
			var date = $scope.inputVO.ACTIVE_DATE;
			var active_date = new Date(date);
			$scope.inputVO.ACTIVE_DATE = active_date;
			$scope.sendRecv("KYC211","addQuestionnaireInitial","com.systex.jbranch.app.server.fps.kyc211.KYC211InputVO",
					$scope.inputVO,function(tota,isError){
					$scope.preview_data = tota[0].body.questionList;
					if($scope.preview_data.length>0){
						if($scope.inputVO.copy){
							$scope.inputVO.RL_VERSION = $scope.preview_data[0].RL_VERSION;
							$scope.inputVO.RS_VERSION = $scope.preview_data[0].RS_VERSION;
							$scope.inputVO.RLR_VERSION = $scope.preview_data[0].RLR_VERSION;
							$scope.inputVO.QUEST_TYPE = $scope.preview_data[0].QUEST_TYPE;
							$scope.inputVO.RISK_LEVEL = $scope.preview_data[0].risk;
							$scope.inputVO.LRATE_RISK_LEVEL = $scope.preview_data[0].lRate;
							$scope.inputVO.INT_SCORE = $scope.preview_data[0].INT_SCORE;
							
							$scope.inputVO.W_VAL = [];
							$scope.inputVO.C_VAL = [];
							$scope.inputVO.CUST_RL_ID = [];
							angular.forEach($scope.preview_data[0].W_VAL,function(row,index){
								$scope.inputVO.W_VAL[index] = row.W_VAL;
							});
							
							angular.forEach($scope.preview_data[0].C_VAL,function(row,index){
								$scope.inputVO.C_VAL[index] = row.C_VAL;
							});
							
							angular.forEach($scope.preview_data[0].CW_CUST_RL_ID,function(row){
								$scope.inputVO.CUST_RL_ID[row.W_LEVEL] = {};
							});
							angular.forEach($scope.preview_data[0].CW_CUST_RL_ID,function(row){
								$scope.inputVO.CUST_RL_ID[row.W_LEVEL][row.C_LEVEL] = row.CUST_RL_ID;
							});							

						}else{
							$scope.inputVO.RL_VERSION = $scope.preview_data[0].RL_VERSION;
							$scope.inputVO.RS_VERSION = $scope.preview_data[0].RS_VERSION;
							$scope.inputVO.RLR_VERSION = $scope.preview_data[0].RLR_VERSION;
							$scope.inputVO.QUEST_TYPE = $scope.preview_data[0].QUEST_TYPE;
							$scope.inputVO.INT_SCORE = $scope.preview_data[0].INT_SCORE;

						}

					}
					
					angular.forEach($scope.preview_data,function(row,index,objs){
						row.edit = [];
						row.edit.push({LABEL: "設定權重",DATA: "W"});
						row.edit.push({LABEL: "刪除題目",DATA: "D"});

					})
			});
		}
		
		//設定權重/刪除題目
		$scope.edit = function(row,index){
			switch (row.editto) {
			case 'W':
				$scope.setUpWeight(row);
				break;
			case 'D':
				$scope.delRow(row,index);
			default:
				break;
			}
		}
		//權重設定
		$scope.setUpWeight = function(row){
			var copy = $scope.inputVO.copy;
			var dialog = ngDialog.open({
				template:'assets/txn/KYC212/KYC212.html',
				className: 'KYC212',
				controller: ['$scope',function($scope){
					$scope.copy = copy;
					$scope.row = row;
				}]
			});
			dialog.closePromise.then(function(data){
				if(data.value == 'cancel'){
					row.editto='';
					row.answer = $scope.connector('get','KYC212_temp');
					$scope.connector('set','KYC212_temp','');
				}else{
					row.editto='';
				}

			});
		}
		
		//刪除題目
		$scope.delRow = function(row,index){
        	$confirm({text: '是否刪除此筆資料!!'},{size: 'sm'}).then(function(){
    			$scope.inputVO.DEL_QUESTION.push(row);
    			$scope.preview_data.splice(index,1);
        	});
		}
		
		//設定風險級距
		$scope.popRisckLevel  = function(){
			var updateRisk = false;
			if($scope.inputVO.RL_VERSION == null){
	        	var rl_version ='RL'+$filter('date')(new Date,'yyyyMMdd')+(String)(Math.floor((Math.random() * 10000) + 1));
	        	if(rl_version.length<14){
	        		rl_version = rl_version+1;
	        	}
//	        	$scope.inputVO.RL_VERSION = rl_version;
			}else{
				updateRisk = true;
				var rl_version = $scope.inputVO.RL_VERSION;
			}
			var dialog = ngDialog.open({
				template:'assets/txn/KYC213/KYC213.html',
				className: 'KYC213',
				controller: ['$scope',function($scope){
					$scope.RL_VERSION = rl_version;
					$scope.updateRisk = updateRisk;
					
				}]
			});
			dialog.closePromise.then(function(data){
				if(data.value == 'cancel'){
					if($scope.inputVO.RL_VERSION == null){
						$scope.inputVO.RL_VERSION = null;
					}
				}else{
					$scope.inputVO.RL_VERSION = data.value;
				}
//				$scope.update();
			});
		}
		
		//設定可承受風險損失率
		$scope.RiskRate  = function(){
			var updateRisk = false;
			var type = $scope.titleType;
			if($scope.inputVO.RLR_VERSION == null){
	        	var rlr_version ='RLR'+$filter('date')(new Date,'yyyyMMdd')+(String)(Math.floor((Math.random() * 10000) + 1));
	        	if(rlr_version.length<15){
	        		rlr_version = rlr_version+1;
	        	}
			}else{
				updateRisk = true;
				var rlr_version = $scope.inputVO.RLR_VERSION;
			}
			var dialog = ngDialog.open({
				template:'assets/txn/KYC216/KYC216.html',
				className: 'KYC216',
				controller: ['$scope',function($scope){
					$scope.RLR_VERSION = rlr_version;
					$scope.updateRisk = updateRisk;
					$scope.titleType = type;
				}]
			});
			dialog.closePromise.then(function(data){
				if(data.value == 'cancel'){
					if($scope.inputVO.RLR_VERSION == null){
						$scope.inputVO.RLR_VERSION = null;
					}
				}else{
					$scope.inputVO.RLR_VERSION = data.value;
				}
			});
		}
		
		//設定風險承受能力
		$scope.WCLevel = function(){
			var updateRisk = false;
			if($scope.inputVO.RS_VERSION == null){
	        	var rs_version ='RS'+$filter('date')(new Date,'yyyyMMdd')+(String)(Math.floor((Math.random() * 10000) + 1));
	        	if(rs_version.length<14){
	        		rs_version = rs_version+1;
	        	}
			}else{
				updateRisk = true;
				var rs_version = $scope.inputVO.RS_VERSION;
			}
			
			var dialog = ngDialog.open({
				template:'assets/txn/KYC215/KYC215.html',
				className: 'KYC215',
				controller: ['$scope',function($scope){
					$scope.RS_VERSION = rs_version;
					$scope.updateRisk = updateRisk;
					
				}]
			});
			dialog.closePromise.then(function(data){
				if(data.value == 'cancel'){
					if($scope.inputVO.RS_VERSION == null){
						$scope.inputVO.RS_VERSION = null;
					}
				}else{
					$scope.inputVO.RS_VERSION = data.value;
				}
			});
		}
		
		$scope.addQuest = function(){
			var EXAM_VERSION = $scope.inputVO.EXAM_VERSION;
			var preview_data = $scope.preview_data
			var dialog = ngDialog.open({
				template:'assets/txn/KYC214/KYC214.html',
				className:'KYC214',
				controller: ['$scope',function($scope){
					$scope.EXAM_VERSION = EXAM_VERSION;
					$scope.preview_data = preview_data;
				}]
			});
			dialog.closePromise.then(function(data){
				if(data.value == 'cancel'){
					
				}else{
					if($scope.preview_data){
						angular.forEach(data.value,function(row,index,objs){
							row.ESSENTIAL_FLAG = 'Y'
							row.edit = [];
							row.edit.push({LABEL: "設定權重",DATA: "W"});
							row.edit.push({LABEL: "刪除題目",DATA: "D"});
							$scope.preview_data.push(row);
						})
					}else{
						$scope.preview_data = data.value;
						angular.forEach($scope.preview_data,function(row,index,objs){
							row.ESSENTIAL_FLAG = 'Y';
							if(row.QUESTION_TYPE == 'S'){
								row.Q_TYPE = '單選';
							}
							if(row.QUESTION_TYPE == 'N'){
								row.Q_TYPE = '問答題<br>數字';
							}
							if(row.QUESTION_TYPE == 'T'){
								row.Q_TYPE = '問答題<br>文字';
							}
							if(row.QUESTION_TYPE == 'M'){
								row.Q_TYPE = '複選';
							}
							row.edit = [];
							row.edit.push({LABEL: "設定權重",DATA: "W"});
							row.edit.push({LABEL: "刪除題目",DATA: "D"});
						});
					}
				}
//				$scope.update();
			});
		}
		
		$scope.editSaveData = function(){
			var risk = $scope.connector('get','KYC213_addRISK');
			var WCrisk = $scope.connector('get','KYC215_addRISK');
			var LRate = $scope.connector('get','KYC216_addRISK');
			$scope.inputVO.update_preview_data = $scope.preview_data
			if($scope.add.$invalid){
				$scope.showErrorMsg('請輸入問卷類別、問卷名稱、風險級距、風險承受能力、問卷啟用日期、截距分')
			}else{
				
				if(risk){
					$scope.inputVO.RISK_LEVEL = risk.RISK_LEVEL;
					if(risk.DEL_CUST_RL_ID){
						$scope.inputVO.DEL_CUST_RL_ID = risk.DEL_CUST_RL_ID;
					}
				}
				if(WCrisk){
					$scope.inputVO.C_VAL = WCrisk.C_VAL;
					$scope.inputVO.W_VAL = WCrisk.W_VAL;
					$scope.inputVO.CUST_RL_ID = WCrisk.CUST_RL_ID;
					console.log($scope.inputVO.CUST_RL_ID);
				}
				debugger
				if(LRate){
					$scope.inputVO.LRATE_RISK_LEVEL = LRate.LRATE_RISK_LEVEL;
					if(LRate.LRATE_DEL_CUST_RL_ID){
						$scope.inputVO.LRATE_DEL_CUST_RL_ID = LRate.LRATE_DEL_CUST_RL_ID;
					}
				}
				if($scope.inputVO.copy){
					var rl_version ='RL'+$filter('date')(new Date,'yyyyMMdd')+(String)(Math.floor((Math.random() * 10000) + 1));
		        	if(rl_version.length<14){
		        		rl_version = rl_version+1;
		        	}
		        	
		        	var rs_version ='RS'+$filter('date')(new Date,'yyyyMMdd')+(String)(Math.floor((Math.random() * 10000) + 1));
		        	if(rs_version.length<14){
		        		rs_version = rs_version+1;
		        	}
		        	
		        	var rlr_version ='RLR'+$filter('date')(new Date,'yyyyMMdd')+(String)(Math.floor((Math.random() * 10000) + 1));
		        	if(rlr_version.length<15){
		        		rlr_version = rlr_version+1;
		        	}
		        	
		        	$scope.inputVO.RL_VERSION = rl_version;
		        	$scope.inputVO.RS_VERSION = rs_version;
		        	$scope.inputVO.RLR_VERSION = rlr_version;
				}
				if($scope.inputVO.update_preview_data != undefined){
					$scope.sendRecv("KYC211","editSaveData","com.systex.jbranch.app.server.fps.kyc211.KYC211InputVO",
					$scope.inputVO,function(tota,isError){
						if (tota[1].body) {
							if($scope.save_update){
								$scope.showSuccessMsg('ehl_01_common_002');
							}else{
								$scope.showSuccessMsg('ehl_01_common_001');
							}
			        		
							$scope.connector('set','KYC213_addRISK','');
							$scope.connector('set','KYC215_addRISK','');
							$scope.connector('set','KYC216_addRISK','');
			        		$scope.closeThisDialog('successful');
			        	};
					});
				}else{
					$scope.showErrorMsg('請新增題目')
				}
			}
		}
	
		
		$scope.init();

});