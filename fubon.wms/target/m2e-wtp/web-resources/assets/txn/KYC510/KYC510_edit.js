/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('KYC510EditController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "KYC510EditController";
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
		$scope.STATUS_TYPE = [{'LABEL':'修改待審核', 'DATA': '01'},{'LABEL':'已放行', 'DATA': '02'},{'LABEL':'主管退回', 'DATA': '05'},{'LABEL':'已刪除', 'DATA': '05'}];				

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
        
        $scope.editCancel = function(){
        	$scope.inputVO.PRO_TYPE='';
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
				$scope.field_type = $scope.inputVO.STATUS=='01'?false:true;
				$scope.save_update = true;
				$scope.inputVO.DEL_QUESTION = [];
				break;
			case 'C':
				$scope.inputVO = $scope.row;
				$scope.inputVO.copy=true;
				$scope.update();
				$scope.inputVO.EXAM_VERSION=$scope.exam_version;
				$scope.inputVO.ACTIVE_DATE=undefined;
				$scope.inputVO.EXPIRY_DATE=undefined;
				$scope.title='複製問卷';
				$scope.ae=false;
				$scope.inputVO.DEL_QUESTION = [];
				break;
			default:
				break;
			}
		}

		//CORR_ANS display format
		var ansDisplayFormat = function(input) {
			var result = "";
			var splitAns = input.CORR_ANS.trim().split(" ");
			splitAns.forEach(function(corrAns) {
				result = result.concat("("+corrAns+")");
			}) 
			return result;
		}
		//預覽問卷
		$scope.preview = function(){
			$scope.inputVO.ACTIVE_DATE = new Date($scope.inputVO.ACTIVE_DATE);
			$scope.inputVO.EXPIRY_DATE = new Date($scope.inputVO.EXPIRY_DATE);
			$scope.inputVO.LASTUPDATE = new Date($scope.inputVO.LASTUPDATE);
			$scope.sendRecv("KYC510","addQuestionnaireInitial","com.systex.jbranch.app.server.fps.kyc510.KYC510InputVO",
					$scope.inputVO,function(tota,isError){
					$scope.preview_data = tota[0].body.questionList;
					angular.forEach($scope.preview_data , function(row,index,objs) {
						row.CORR_ANS = ansDisplayFormat(row);
					})
					$scope.inputVO.PRO_TYPE = $scope.preview_data[0].QUEST_TYPE;
					$scope.Remark = $scope.preview_data[0].Remark;
			});
		}
		//修改問卷 || 複製問卷
		$scope.update = function(){
			$scope.inputVO.ACTIVE_DATE = new Date($scope.inputVO.ACTIVE_DATE);
			$scope.inputVO.EXPIRY_DATE = new Date($scope.inputVO.EXPIRY_DATE);
			$scope.inputVO.LASTUPDATE = new Date($scope.inputVO.LASTUPDATE);
			
			$scope.sendRecv("KYC510","addQuestionnaireInitial","com.systex.jbranch.app.server.fps.kyc510.KYC510InputVO",
					$scope.inputVO,function(tota,isError){
					$scope.preview_data = tota[0].body.questionList;
					if($scope.preview_data.length>0){
						if($scope.inputVO.copy){
							$scope.inputVO.RL_VERSION = $scope.preview_data[0].RL_VERSION;
							$scope.inputVO.PRO_TYPE = $scope.preview_data[0].QUEST_TYPE;
							$scope.inputVO.RISK_LEVEL = $scope.preview_data[0].risk;

						}else{
							$scope.inputVO.RL_VERSION = $scope.preview_data[0].RL_VERSION;
							$scope.inputVO.PRO_TYPE = $scope.preview_data[0].QUEST_TYPE;
						}

						angular.forEach($scope.preview_data , function(row,index,objs) {
							row.CORR_ANS = ansDisplayFormat(row);
						})

					}
					
					angular.forEach($scope.preview_data,function(row,index,objs){
						row.edit = [];
						row.edit.push({LABEL: "新增題目",DATA: "W"});
						row.edit.push({LABEL: "刪除題目",DATA: "D"});

					})
			});
		}
		
		//設定答案/刪除題目
		$scope.edit = function(row,index){
			switch (row.editto) {
//			case 'W':
//				$scope.setAns(row);
//				break;
			case 'D':
				$scope.delRow(row,index);
			default:
				break;
			}
		}		
		
		//刪除題目
		$scope.delRow = function(row,index){
        	$confirm({text: '是否刪除此筆資料!!'},{size: 'sm'}).then(function(){
    			$scope.inputVO.DEL_QUESTION.push(row);
    			$scope.preview_data.splice(index,1);
        	});
		}
		
		$scope.addQuest = function(){
			var EXAM_VERSION = $scope.inputVO.EXAM_VERSION;
			var preview_data = $scope.preview_data
			var dialog = ngDialog.open({
				template:'assets/txn/KYC510/KYC510_qst.html',
				className:'KYC510',
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
//							row.edit.push({LABEL: "新增題目",DATA: "W"});
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
							if(row.QUESTION_TYPE == 'M'){
								row.Q_TYPE = '複選';
							}
							row.edit = [];
//							row.edit.push({LABEL: "新增題目",DATA: "W"});
							row.edit.push({LABEL: "刪除題目",DATA: "D"});
						});
					}
				}
//				$scope.update();
			});
		}
		
		$scope.editSaveData = function(){
			$scope.inputVO.update_preview_data = $scope.preview_data
			if($scope.add.$invalid){
				$scope.showErrorMsg('請輸入問卷類別、問卷名稱、問卷啟用日期,問卷失效日期');
				return;
			}

			if ($scope.inputVO.ACTIVE_DATE >= $scope.inputVO.EXPIRY_DATE) {
				$scope.showErrorMsg('啟用日不可大於等於失效日');
				return;
			}
			if($scope.inputVO.copy){
				var rl_version ='RL'+$filter('date')(new Date,'yyyyMMdd')+(String)(Math.floor((Math.random() * 10000) + 1));
		      	if(rl_version.length<14){
		      		rl_version = rl_version+1;
		      	}
		      	$scope.inputVO.RL_VERSION = rl_version;
			}
			if($scope.inputVO.update_preview_data != undefined){
				$scope.sendRecv("KYC510","editSaveData","com.systex.jbranch.app.server.fps.kyc510.KYC510InputVO",
				$scope.inputVO,function(tota,isError){
					if (tota[1].body) {
						if($scope.save_update){
							$scope.showSuccessMsg('ehl_01_common_002');
						}else{
							$scope.showSuccessMsg('ehl_01_common_001');
						}
			      		
//						$scope.connector('set','KYC513_addRISK','');
			      		$scope.closeThisDialog('successful');
			      	};
				});
			}else{
				$scope.showErrorMsg('請新增題目')
			}
			
		}
		$scope.init();
});