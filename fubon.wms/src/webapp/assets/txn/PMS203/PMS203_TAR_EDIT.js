/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS203_TAR_EDITController',
    function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
        $controller('BaseController', { $scope: $scope });
        $controller('PMS203Controller', { $scope: $scope});
        
        
        $scope.controllerName = "PMS203_TAR_EDITController";

        //初始
        $scope.init = function() {
            if ($scope.row !== undefined){
                $scope.isUpdate = true;
            }

            $scope.inputVO = {
            	reportDate       : $filter('date')(new Date(), 'yyyyMM'),
                region_center_id : '',
                rc_name          : '',
                branch_area_id   : '',
                op_name          : '',
                branch_nbr       : '',
                BRANCH_NBR       : '',
                br_name          : '',
                AO_CODE          : '',
                emp_id           : '',
                emp_name         : '',
                jobTitleId       : '',
                firstDate        : undefined,
                mainPrdId        : '',
                tarAmount        : 0,
                fstDate          : $filter('date')(new Date(), 'yyyy-MM-dd'),
                sCreDate         : $scope.ym
            };
            $scope.inputVO.reportDate = $scope.inputVO.sCreDate;
            $scope.RegionController_getORG($scope.inputVO);

            if ($scope.isUpdate) {
                debugger
                $scope.inputVO.AO_CODE    = $scope.row.AO_CODE;
                $scope.inputVO.BRANCH_NBR = $scope.row.BRANCH_NBR;
                $scope.inputVO.fstDate    = $scope.row.FIRST_DATE;
                $scope.inputVO.jobTitle   = $scope.row.JOB_TITLE;
                $scope.inputVO.emp_id     = $scope.row.EMP_ID;            
                $scope.inputVO.tarAmount  = $scope.row.TAR_AMOUNT;
            }

            
            
        };

        $scope.init();
        

        $scope.UpdateGetNbr = function(){ 	
        	if($scope.isUpdate){
        		$scope.inputVO.branch_nbr = $scope.inputVO.BRANCH_NBR;
        	}
        }

        //職級
        $scope.getJobTitle = function() {

        	if($scope.inputVO.ao_code != '' && !$scope.isUpdate){
        		$scope.getEmpInfo();
        	} 

        	if($scope.isUpdate){
        		$scope.getEmpInfo();
        	}
        }
        
        //抓職級
        $scope.getEmpInfo = function() {
        	//編輯
        	if ($scope.isUpdate) {
                debugger
                $scope.inputVO.emp_id = $scope.row.EMP_ID;
        	}
            var deferred = $q.defer();
            $scope.sendRecv("PMS203", "getEmpInfo", "com.systex.jbranch.app.server.fps.pms203.PMS203InputVO", $scope.inputVO,
                function(totas, isError) {
                    debugger
            		$scope.total = totas[0].body.totalList;
                    if (totas[0].body.totalList.length > 0) {
                        debugger
                        $scope.inputVO.emp_id     = $scope.total[0].EMP_ID;
                        $scope.inputVO.rc_name    = $scope.total[0].REGION_CENTER_NAME;
                        $scope.inputVO.op_name    = $scope.total[0].BRANCH_AREA_NAME;
                        $scope.inputVO.br_name    = $scope.total[0].BRANCH_NAME;
                        $scope.inputVO.emp_name   = $scope.total[0].EMP_NAME;
                        $scope.inputVO.ao_code    = $scope.total[0].AO_CODE;
                        $scope.inputVO.jobTitleId = $scope.total[0].AO_JOB_RANK;
                        $scope.inputVO.firstDate  = Date.parse($scope.total[0].ONBOARD_DATE);
                        $scope.inputVO.jobTitle   = $scope.total[0].JOB_TITLE_NAME;

                        var onboard_date          = $scope.total[0].ONBOARD_DATE;                      
                        $scope.inputVO.fstDate    = onboard_date.substr(0, 10);
                    } else {
                        $scope.inputVO.jobTitleId = '';
                        $scope.inputVO.fstDate    = '';
                        $scope.inputVO.jobTitle   = '';
                    }
                    deferred.resolve($scope.inputVO);
                });

            return deferred.promise;

        };

       
        var saveOrNext = function(val) {
            $scope.getEmpInfo().then(function() {
                if ($scope.isUpdate) {
                    $scope.sendRecv("PMS203", "updateTAR", "com.systex.jbranch.app.server.fps.pms203.PMS203InputVO", $scope.inputVO,
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
                    debugger
                    $scope.sendRecv("PMS203", "insertTAR", "com.systex.jbranch.app.server.fps.pms203.PMS203InputVO", $scope.inputVO,
                        function(totas, isError) {
                            if (isError) {
                            	$scope.showErrorMsg(totas[0].body.msgData);
                            }
                            if(totas[0].body === 'duplicate'){
                          		 $scope.showErrorMsg('ehl_01_common_016');
                          		 return
                       		}
                            if (totas.length > 0) {
                                $scope.showSuccessMsg('ehl_01_common_004');
                                $scope.closeThisDialog('successful');
                            };
                            if(val === 'next')
                            	$scope.edit(undefined, 'TAR');
                            
                        }
                    );
                }
            });
        }

        //儲存
        $scope.save = function() {
            debugger
            if ($scope.parameterTypeEditForm.$invalid) {
                $scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
                return;
            }
            saveOrNext();
        };
        
        //新增下一筆
        $scope.addNext = function(value) {
            if ($scope.parameterTypeEditForm.$invalid) {
                $scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
                return;
            }
            $scope.isUpdate = false;
            saveOrNext(value);   	
        };

    });