/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
  'use strict';
  eSoafApp.controller('SQM110Controller', function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService ,getParameter) {
      $controller('BaseController', {$scope: $scope});
      $controller('RegionController', {$scope: $scope});
          
      $scope.pri_id = projInfoService.getPriID();
      $scope.controllerName = "SQM110Controller";
  //	$scope.QTN_LIST = [{'LABEL':'投資/保險', 'DATA': 'WMS01'},{'LABEL':'理專', 'DATA': 'WMS02'},{'LABEL':'開戶', 'DATA': 'WMS03'},{'LABEL':'櫃檯', 'DATA': 'WMS04'},{'LABEL':'簡訊', 'DATA': 'WMS05'}];
      $scope.STATUS_LIST = [{'LABEL':'未處理', 'DATA': '0'},{'LABEL':'結案扣分', 'DATA': '1'},{'LABEL':'結案不扣分', 'DATA': '2'},{'LABEL':'處理中', 'DATA': '3'},{'LABEL':'退件', 'DATA': '4'}];
      // XML
      getParameter.XML(["SQM.ANS_TYPE","SQM.QTN_TYPE","FUBONSYS.HEADMGR_ROLE"], function(totas) {
          if (totas) {
              $scope.ANS_TYPE = totas.data[totas.key.indexOf('SQM.ANS_TYPE')];
              $scope.QTN_LIST = totas.data[totas.key.indexOf('SQM.QTN_TYPE')];
              $scope.HEADMGR_ROLE = totas.data[totas.key.indexOf('FUBONSYS.HEADMGR_ROLE')];
              $scope.role_id = projInfoService.getRoleID();
              $scope.headmgr_flag = false;//判斷是否為總行人員flag
              angular.forEach($scope.HEADMGR_ROLE, function(row, index, objs){
                  if(row.DATA == $scope.role_id ){
                      $scope.headmgr_flag = true;
                      return;	
                  }
              });	
          }	
      });	
      
      var currDate = new Date(2018, 8, 1, 0, 0, 0); //只能選擇20180901以後資料
      // date picker
	  $scope.optionsInit = function() {
		  $scope.bgn_sDateOptions = {
			  maxDate: $scope.maxDate,
			  minDate: currDate
		  };
		  $scope.bgn_eDateOptions = {
			  maxDate: $scope.maxDate,
			  minDate: currDate
		  };
	  }
      // config
      $scope.model = {};
      $scope.open = function($event, elementOpened) {
          $event.preventDefault();
          $event.stopPropagation();
          $scope.model[elementOpened] = !$scope.model[elementOpened];
      };
	  $scope.limitDate = function() {
		  $scope.bgn_sDateOptions.maxDate = $scope.inputVO.eCreDate || $scope.maxDate;
		  if ($scope.inputVO.eCreDate) {
			  let y = $scope.inputVO.eCreDate.getFullYear() - 1;
			  let m = $scope.inputVO.eCreDate.getMonth();
			  let d = $scope.inputVO.eCreDate.getDate();
			  $scope.bgn_sDateOptions.minDate = new Date(y, m, d);
		  }
		  $scope.bgn_eDateOptions.minDate = $scope.inputVO.sCreDate || currDate;
		  if ($scope.inputVO.sCreDate) {
			  let y = $scope.inputVO.sCreDate.getFullYear() + 1;
			  let m = $scope.inputVO.sCreDate.getMonth();
			  let d = $scope.inputVO.sCreDate.getDate();
			  $scope.bgn_eDateOptions.maxDate = new Date(y, m, d);
		  }
	  };
      // date picker end
      
          
      $scope.init = function(){
          $scope.inputVO = {
                  sCreDate: new Date(new Date().getFullYear(),new Date().getMonth(),1),
                  eCreDate: new Date(),
                  region_center_id: '',   //區域中心
                  branch_area_id: '' ,    //營運區
                  branch_nbr: ''				//分行				
          };
          $scope.rptDate = '';
          $scope.totalData = [];
          $scope.paramList = [];
          $scope.outputVO={totalList:[]};
          $scope.optionsInit();	
          $scope.limitDate();
          $scope.region = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
          $scope.RegionController_setName($scope.region).then(function(data) {
              //檢核需不需要預設員編	
              if ($scope.EMP_LIST.length >= 3)
                  $scope.inputVO.emp_id = '';
          }); 
          $scope.only_one_window = false;
          
      };
      $scope.init();
      $scope.inquireInit = function(){
          $scope.initLimit();
          $scope.paramList = [];
          $scope.originalList = [];
          $scope.outputVO={totalList:[]};
      }
      $scope.inquireInit();	
          
      //資料查詢
      $scope.query = function() {
          $scope.sendRecv("SQM110", "queryData", "com.systex.jbranch.app.server.fps.sqm110.SQM110InputVO", $scope.inputVO, function(tota, isError) {
              if (!isError) {
                  if(tota[0].body.totalList.length == 0) {
                      $scope.paramList = [];
                      $scope.totalData = [];
                      $scope.outputVO={};
                      $scope.showMsg("ehl_01_common_009");	
                      return;
                  }
                  $scope.originalList = angular.copy(tota[0].body.totalList);
                  $scope.paramList = tota[0].body.totalList;
  //								alert(JSON.stringify($scope.paramList));
                  $scope.totalData = tota[0].body.totalList;
                  $scope.outputVO = tota[0].body;		
                  return;
              }						
          });
      };
          
      $scope.openContents = function(row){
          var dialog = ngDialog.open({
              template: 'assets/txn/SQM110/SQM110_contents.html',
              className: 'SQM110',
              showClose: false,
              controller: ['$scope', function($scope) {
                  $scope.row = row;
              }]
          });
          dialog.closePromise.then(function (data) {
              if(data.value === 'successful'){
                  $scope.query();
              }
          });	
      };	
      
      $scope.openDetail = function(row,type){
          if($scope.only_one_window){
              
          }else{
              $scope.only_one_window=true;
          }
          var dialog = ngDialog.open({
              template: 'assets/txn/SQM110/SQM110_detail.html',
              className: 'SQM110',
              showClose: false,
              controller: ['$scope', function($scope) {
                  $scope.type = type,
                  $scope.row = row;
              }]
          });
          dialog.closePromise.then(function (data) {
              if(data.value === 'successful'){
                  $scope.query();
                  $scope.only_one_window = false;
              }
              if(data.value === 'cancel'){
                  $scope.query();
                  $scope.only_one_window = false;
              }
          });
      };
          
      $scope.queryFlowDetail = function(row){
          var dialog = ngDialog.open({
              template: 'assets/txn/SQM110/SQM110_flowDetail.html',
              className: 'SQM110',
              showClose: false,
              controller: ['$scope', function($scope) {
                  $scope.case_no = row.CASE_NO;
              }]
          });
          dialog.closePromise.then(function (data) {
              if(data.value === 'successful'){
                  $scope.query();
              }
          });
      };
          
      //匯出
      $scope.exportRPT = function(){
  //    		var print_list = [];
  //    		angular.forEach($scope.totalData, function(row, index, objs){
  //				if(row.DEDUCTION_FINAL != null){
  //					print_list.push(row);
  //				}
  //			});	
  //    		$scope.inputVO.checkList=print_list;
          $scope.inputVO.checkList=$scope.totalData;
          if($scope.inputVO.checkList.length == 0){
  //				$scope.showMsg("無(不)扣分資料匯出。");
              $scope.showMsg("ehl_01_common_009");	
              return;
          }
              
          $scope.sendRecv("SQM110", "exportRPT", "com.systex.jbranch.app.server.fps.sqm110.SQM110InputVO", $scope.inputVO, function(tota, isError) {						
              if (isError) {
                  $scope.showErrorMsgInDialog(tota[0].body.msgData);
                  return;
              }
          });
      };
          
      $scope.printPdf = function(row) {
          $scope.inputVO.checkList=[{"CASE_NO": row.CASE_NO}];
              $scope.sendRecv("SQM110", "download", "com.systex.jbranch.app.server.fps.sqm110.SQM110InputVO", $scope.inputVO, function(tota, isError) {
                  if (!isError) {
                      $scope.showSuccessMsg('下載成功');
                      $scope.closeThisDialog('successful');
                  }						
              });
              
      };
          
          
      $scope.exportRPT_ALL = function(row) {
          var print_list = [];
          angular.forEach($scope.totalData, function(row, index, objs){
              if(row.CASE_NO != null){
                  print_list.push(row);
              }
          });	
          
          $scope.inputVO.checkList=print_list;
          
          if($scope.inputVO.checkList.length == 0){
              $scope.showMsg("無需要列印資料。");
              return;
          }
              
          $scope.sendRecv("SQM110", "download", "com.systex.jbranch.app.server.fps.sqm110.SQM110InputVO", $scope.inputVO, function(tota, isError) {
              if (!isError) {
                  $scope.showSuccessMsg('下載成功');
              }						
          });
      };
          
  });
  