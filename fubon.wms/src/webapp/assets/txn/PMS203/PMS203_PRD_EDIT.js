/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS203_PRD_EDITController',
    function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
        $controller('BaseController', {
            $scope: $scope
        });
        $controller('PMS203Controller', {
            $scope: $scope
        });
        $scope.controllerName = "PMS203_PRD_EDITController";
        $scope.init = function() {
            if ($scope.row !== undefined)
                $scope.isUpdate = true;
            $scope.inputVO = {
                reportDate: $filter('date')(new Date(), 'yyyyMM'),
                region_center_id: '',
                rc_name: '',
                branch_area_id: '',
                op_name: '',
                branch_nbr: '',
                br_name: '',
                emp_id: '',
                emp_name: '',
                jobTitleId: '',
                firstDate: undefined,
                mainPrdId: '',
                AO_CODE: '',
                tarAmount: 0,
                sCreDate:$scope.ym
            };
            $scope.fstDate = '';
            $scope.inputVO.reportDate = $scope.inputVO.sCreDate;
            if ($scope.isUpdate) {
            	$scope.inputVO.AO_CODE = $scope.row.AO_CODE;
                $scope.inputVO.mainPrdId = $scope.row.MAIN_PRD_ID;
                $scope.inputVO.tarAmount = $scope.row.TAR_AMT;
            }
            $scope.RegionController_getORG($scope.inputVO);
        };
        $scope.init();


        //職級
        $scope.getJobTitle = function() {
        	if($scope.inputVO.ao_code != '' && !$scope.isUpdate){
        		$scope.getEmpInfo();
        	}    	
        	if($scope.isUpdate){
        		$scope.getEmpInfo();
        	}
        }

        $scope.getEmpInfo = function() {
        	//編輯
        	if ($scope.isUpdate) {
        		$scope.inputVO.ao_code = $scope.row.AO_CODE;
        	}        	
            var deferred = $q.defer();           
            $scope.sendRecv("PMS203", "getEmpInfo", "com.systex.jbranch.app.server.fps.pms203.PMS203InputVO", $scope.inputVO,
                function(totas, isError) {
                    var row = totas[0].body.totalList[0];
                    if (row !== undefined) {
                        $scope.inputVO.emp_id = row.EMP_ID;
                        $scope.inputVO.rc_name = row.REGION_CENTER_NAME;
                        $scope.inputVO.op_name = row.BRANCH_AREA_NAME;
                        $scope.inputVO.br_name = row.BRANCH_NAME;
                        $scope.inputVO.emp_name = row.EMP_NAME;
                        $scope.inputVO.jobTitleId = row.AO_JOB_RANK;
                        console.log($scope.inputVO.jobTitleId);
                        console.log(row.AO_JOB_RANK);
                        $scope.inputVO.firstDate = Date.parse(row.ONBOARD_DATE);
                        var dd = row.ONBOARD_DATE;
                        $scope.fstDate = dd.substr(0, 10);
                        $scope.inputVO.jobTitle = row.JOB_TITLE_NAME;
                    } else {
                        $scope.inputVO.jobTitleId = '';
                        $scope.fstDate = '';
                        $scope.inputVO.jobTitle = '';
                    }
                    deferred.resolve($scope.inputVO);
                });

            return deferred.promise;

        };
        
        var saveOrNext = function(val) {
            $scope.getEmpInfo().then(function() {
                if ($scope.isUpdate) {
                    $scope.sendRecv("PMS203", "updatePRD", "com.systex.jbranch.app.server.fps.pms203.PMS203InputVO", $scope.inputVO,
                        function(totas, isError) {
                            if (isError) {
                                $scope.showErrorMsg(totas[0].body.msgData);
                            }
                            if (totas.length > 0) {
                                $scope.showSuccessMsg('ehl_01_common_004');
                                $scope.closeThisDialog('successful');
                            };
                        }
                    );
                } else {
                    $scope.sendRecv("PMS203", "insertPRD", "com.systex.jbranch.app.server.fps.pms203.PMS203InputVO", $scope.inputVO,
                        function(totas, isError) {
                            if (isError) {
                            	$scope.showErrorMsg(totas[0].body.msgData);
                            }
                        	if(totas[0].body==='duplicate'){
                       		 $scope.showErrorMsg('ehl_01_common_016');
                       		 return
                    		}
                            if (totas.length > 0) {
                                $scope.showSuccessMsg('ehl_01_common_004');
                                $scope.closeThisDialog('successful');
                            };
                            if(val==='next')
                            	$scope.edit(undefined,'PRD');
                        }
                    );
                }
            });
        }

        $scope.save = function() {
            if ($scope.parameterTypeEditForm.$invalid) {
                $scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
                return;
            }
            if ($scope.inputVO.jobTitleId == "") {
                $scope.showErrorMsg('欄位檢核錯誤:查無此理專職級');
                return;
            }

            
          //$scope.getEmpInfo().then(function(){
            saveOrNext();
          //});

        };

        $scope.addNext = function(value) {
            if ($scope.parameterTypeEditForm.$invalid) {
                $scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
                return;
            }
          //$scope.getEmpInfo().then(function(){
            $scope.isUpdate = false;
            saveOrNext(value);
          //});     	
        };

    });