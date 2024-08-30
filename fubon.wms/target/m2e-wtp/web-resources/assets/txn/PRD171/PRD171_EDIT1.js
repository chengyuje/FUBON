/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD171_EDIT1Controller',
	function($rootScope,$scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD171_EDIT1Controller";

	     // date picker
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
			$scope.bgn_sDateOptions.maxDate = $scope.inputVO.EXPIRY_DATE || $scope.maxDate;
			$scope.bgn_eDateOptions.minDate = $scope.inputVO.EFFECT_DATE || $scope.minDate;
		};


		  $scope.mappingSet['type']=[];
          $scope.mappingSet['type'].push({
				LABEL : '是',
				DATA :'Y'
  		},{
				LABEL : '否',
				DATA :'N'
  		});

          $scope.mappingSet['type1']=[];
          $scope.mappingSet['type1'].push({
				LABEL : '基本保費',
				DATA :'1'
  		},{
				LABEL : '增額',
				DATA :'2'
  		});

		$scope.mappingSet['COMPANY'] = $scope.companies;

        $scope.mappingSet['prd_risk']=[];
        $scope.mappingSet['prd_risk'].push({
			LABEL:'P1',
			DATA :'P1'
  		},{
			LABEL:'P2',
			DATA :'P2'
  		},{
			LABEL:'P3',
			DATA :'P3'
  		},{
			LABEL:'P4',
			DATA :'P4'
  		});

		$scope.init = function(){
			$scope.Q_ID_TYPE = false;
			if($scope.title_type == 'Add'){
				$scope.title='新增進件壽險產品參數';
				$scope.inputVO={
						TYPE_TABLE:'1',
				}

			}

			if($scope.title_type == 'Update'){
				$scope.title='修改進件壽險產品參數';
				$scope.Q_ID_TYPE=true;
				$scope.inputVO={
						TYPE_TABLE:'1',
						INSPRD_KEYNO    :$scope.row_data.INSPRD_KEYNO  ,
						INSPRD_TYPE     :$scope.row_data.INSPRD_TYPE   ,
						INSPRD_CLASS    :$scope.row_data.INSPRD_CLASS  ,
						INSPRD_STYLE    :$scope.row_data.SPECIAL_CONDITION   ,
						INSPRD_ID       :$scope.row_data.INSPRD_ID     ,
						INSPRD_NAME     :$scope.row_data.INSPRD_NAME   ,
						INSPRD_ANNUAL   :$scope.row_data.INSPRD_ANNUAL ,
						MAIN_RIDER      :$scope.row_data.MAIN_RIDER    ,
						PAY_TYPE        :$scope.row_data.PAY_TYPE      ,
						CURR_CD         :$scope.row_data.CURR_CD       ,
						FEE_STATE       :$scope.row_data.FEE_STATE     ,
						PRD_RATE        :$scope.row_data.PRD_RATE      ,
						CNR_RATE        :$scope.row_data.CNR_RATE      ,
						COEFFICIENT     :$scope.row_data.COEFFICIENT   ,
						NEED_MATCH      :$scope.row_data.NEED_MATCH    ,
						PRD_RISK      	:$scope.row_data.PRD_RISK	   ,
						CERT_TYPE       :$scope.row_data.CERT_TYPE     ,
						TRAINING_TYPE   :$scope.row_data.TRAINING_TYPE ,
						EXCH_RATE       :$scope.row_data.EXCH_RATE     ,
						AB_EXCH_RATE    :$scope.row_data.AB_EXCH_RATE  ,
						EFFECT_DATE     :$scope.toJsDate($scope.row_data.EFFECT_DATE)   ,
						EXPIRY_DATE     :$scope.toJsDate($scope.row_data.EXPIRY_DATE)   ,
						APPROVER        :$scope.row_data.APPROVER      ,
						APP_DATE        :$scope.toJsDate($scope.row_data.APP_DATE)      ,
						COMPANY_NUM		:$scope.row_data.COMPANY_NUM
				}
			}
		}
		$scope.init();



		$scope.btnSubmit = function(){
			if($scope.parameterTypeEditForm.$invalid) {
				$scope.showErrorMsgInDialog('欄位檢核錯誤:檢視必要輸入欄位');
				return;
			}

			//非投資型商品風險值，需適配則須有商品風險值
			if($scope.inputVO.INSPRD_TYPE == "1" && $scope.inputVO.NEED_MATCH == "Y") {
				if($scope.inputVO.PRD_RISK == undefined || $scope.inputVO.PRD_RISK == null || $scope.inputVO.PRD_RISK == "") {
					$scope.showErrorMsgInDialog('需適配商品請選擇商品風險值');
					return;
				}
			}

			if($scope.title_type == 'Add' || $scope.title_type == 'Copy'){

					$scope.sendRecv("PRD171","addData","com.systex.jbranch.app.server.fps.prd171.PRD171EDITInputVO",
		        			$scope.inputVO,function(tota,isError){
	                	if (isError) {
	                		$scope.showErrorMsg(tota[0].body.msgData);
	                	}
	                	if (tota.length > 0) {
	                		$scope.showSuccessMsg('ehl_01_common_001');
	                		$scope.closeThisDialog('successful');
	                	};
					});

			}
			if($scope.title_type == 'Update'){
					$scope.sendRecv("PRD171","updateData","com.systex.jbranch.app.server.fps.prd171.PRD171EDITInputVO",
		        			$scope.inputVO,function(tota,isError){
	                	if (isError) {
	                		$scope.showErrorMsg(tota[0].body.msgData);
	                	}
	                	if (tota.length > 0) {
	                		$scope.showSuccessMsg('ehl_01_common_006');
	                		$scope.closeThisDialog('successful');
	                	}
	                	});
					}
			}


	}
);
