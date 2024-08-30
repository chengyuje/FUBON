/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS321Controller',
    function($scope, $controller, socketService, alerts, projInfoService, $q, getParameter) {
        $controller('BaseController', {
            $scope: $scope
        });
        $scope.controllerName = "FPS321Controller";
        var isDelete = false;
//        console.log('');
        
        $scope.mapping = {};

        
        $scope.getDefault = function(school){
        	if ($scope.schoolPara) {        		
    			switch (school) {
                case 'university_1':
                	$scope.inputVO.UNIVERSITY_FEE_EDU = $scope.schoolPara.UNIVERSITY_FEE_1;
                	$scope.inputVO.UNIVERSITY_FEE_LIFE = $scope.schoolPara.UNIVERSITY_COST_1;
                	$scope.inputVO.UNIVERSITY_YEAR = '4';
                  break;
                case 'university_2':
                	$scope.inputVO.UNIVERSITY_FEE_EDU = $scope.schoolPara.UNIVERSITY_FEE_2;
                	$scope.inputVO.UNIVERSITY_FEE_LIFE = $scope.schoolPara.UNIVERSITY_COST_2;
                	$scope.inputVO.UNIVERSITY_YEAR = '4';
                  break;
                case 'university_3':
                	$scope.inputVO.UNIVERSITY_FEE_EDU = $scope.schoolPara.UNIVERSITY_FEE_3;
                	$scope.inputVO.UNIVERSITY_FEE_LIFE = $scope.schoolPara.UNIVERSITY_COST_3;
                	$scope.inputVO.UNIVERSITY_YEAR = '4';
                  break;
                case 'graduate_1':
                	$scope.inputVO.MASTER_FEE_EDU = $scope.schoolPara.GRADUATED_FEE_1;
                	$scope.inputVO.MASTER_FEE_LIFE = $scope.schoolPara.GRADUATED_COST_1;
                	$scope.inputVO.MASTER_YEAR = '2';
                  break;
                case 'graduate_2':
                	$scope.inputVO.MASTER_FEE_EDU = $scope.schoolPara.GRADUATED_FEE_2;
                	$scope.inputVO.MASTER_FEE_LIFE = $scope.schoolPara.GRADUATED_COST_2;
                	$scope.inputVO.MASTER_YEAR = '2';
                  break;
                case 'graduate_3':
                	$scope.inputVO.MASTER_FEE_EDU = $scope.schoolPara.GRADUATED_FEE_3;
                	$scope.inputVO.MASTER_FEE_LIFE = $scope.schoolPara.GRADUATED_COST_3;
                	$scope.inputVO.MASTER_YEAR = '2';
                  break;
                case 'doctor_1':
                	$scope.inputVO.PHD_FEE_EDU = $scope.schoolPara.DOCTORAL_FEE_1;
                	$scope.inputVO.PHD_FEE_LIFE = $scope.schoolPara.DOCTORAL_COST_1;
                	$scope.inputVO.PHD_YEAR = '5';
                  break;
                case 'doctor_2':
                	$scope.inputVO.PHD_FEE_EDU = $scope.schoolPara.DOCTORAL_FEE_2;
                	$scope.inputVO.PHD_FEE_LIFE = $scope.schoolPara.DOCTORAL_COST_2;
                	$scope.inputVO.PHD_YEAR = '5';
                  break;
                case 'doctor_3':
                	$scope.inputVO.PHD_FEE_EDU = $scope.schoolPara.DOCTORAL_FEE_3;
                	$scope.inputVO.PHD_FEE_LIFE = $scope.schoolPara.DOCTORAL_COST_3;
                	$scope.inputVO.PHD_YEAR = '5';
                  break;
    			}            	
        	}
        	$scope.sum();

        };
        
        // init 
        $scope.init = function() {
            $scope.inputVO = {
            	planID: $scope.planID,
            	UNIVERSITY: '',
            	UNIVERSITY_FEE_EDU: 0,
            	UNIVERSITY_FEE_LIFE: 0,
            	UNIVERSITY_YEAR: 0,
                MASTER: '',
                MASTER_FEE_EDU: 0,
                MASTER_FEE_LIFE: 0,
                MASTER_YEAR: 0,
                PHD: '',
                PHD_FEE_EDU: 0,
                PHD_FEE_LIFE: 0,
                PHD_YEAR: 0,
                total: 0
            }
            
//            if ($scope.fromFPS320) {
            	$scope.initFPS321();
//            } else {
//            	$scope.queryFPS321();
//            }            
        }
        
        $scope.initFPS321 = function() {
        	$scope.inputVO.UNIVERSITY = $scope.fromFPS320.UNIVERSITY ? $scope.fromFPS320.UNIVERSITY :'';
        	$scope.inputVO.UNIVERSITY_FEE_EDU = $scope.fromFPS320.UNIVERSITY_FEE_EDU ? $scope.fromFPS320.UNIVERSITY_FEE_EDU :0;
        	$scope.inputVO.UNIVERSITY_FEE_LIFE = $scope.fromFPS320.UNIVERSITY_FEE_LIFE ? $scope.fromFPS320.UNIVERSITY_FEE_LIFE :0;
        	$scope.inputVO.UNIVERSITY_YEAR = $scope.fromFPS320.UNIVERSITY_YEAR ? $scope.fromFPS320.UNIVERSITY_YEAR :0;
        	$scope.inputVO.MASTER = $scope.fromFPS320.MASTER ? $scope.fromFPS320.MASTER :'';
        	$scope.inputVO.MASTER_FEE_EDU = $scope.fromFPS320.MASTER_FEE_EDU ? $scope.fromFPS320.MASTER_FEE_EDU :0;
        	$scope.inputVO.MASTER_FEE_LIFE = $scope.fromFPS320.MASTER_FEE_LIFE ? $scope.fromFPS320.MASTER_FEE_LIFE :0;
        	$scope.inputVO.MASTER_YEAR = $scope.fromFPS320.MASTER_YEAR ? $scope.fromFPS320.MASTER_YEAR :0;
        	$scope.inputVO.PHD = $scope.fromFPS320.PHD ? $scope.fromFPS320.PHD :'';
        	$scope.inputVO.PHD_FEE_EDU = $scope.fromFPS320.PHD_FEE_EDU ? $scope.fromFPS320.PHD_FEE_EDU :0;
        	$scope.inputVO.PHD_FEE_LIFE = $scope.fromFPS320.PHD_FEE_LIFE ? $scope.fromFPS320.PHD_FEE_LIFE :0;
        	$scope.inputVO.PHD_YEAR = $scope.fromFPS320.PHD_YEAR ? $scope.fromFPS320.PHD_YEAR :0;
        	$scope.inputVO.total = $scope.fromFPS320.total ? $scope.fromFPS320.total :0;      
        	$scope.sum();
        }
        
        $scope.queryFPS321 = function() {
        	$scope.sendRecv('FPS324', 'queryFPS321', 'com.systex.jbranch.app.server.fps.fps324.FPS324InputVO', {planID: $scope.inputVO.planID},
		        function (tota, isError) {
	        		if (!isError) {
	        			if (tota[0].body.outputList) {
	        				if (tota[0].body.outputList.length > 0) {
		        				$scope.outputList = tota[0].body.outputList[0];
		        				$scope.inputVO.UNIVERSITY = $scope.outputList.UNIVERSITY;
		        				$scope.inputVO.UNIVERSITY_FEE_EDU = $scope.outputList.UNIVERSITY_FEE_EDU;
		        				$scope.inputVO.UNIVERSITY_FEE_LIFE = $scope.outputList.UNIVERSITY_FEE_LIFE;
		        				$scope.inputVO.UNIVERSITY_YEAR = $scope.outputList.UNIVERSITY_YEAR;
		        				$scope.inputVO.MASTER = $scope.outputList.MASTER;
		        				$scope.inputVO.MASTER_FEE_EDU = $scope.outputList.MASTER_FEE_EDU;
		        				$scope.inputVO.MASTER_FEE_LIFE = $scope.outputList.MASTER_FEE_LIFE;
		        				$scope.inputVO.MASTER_YEAR = $scope.outputList.MASTER_YEAR;
		        				$scope.inputVO.PHD = $scope.outputList.PHD;
		        				$scope.inputVO.PHD_FEE_EDU = $scope.outputList.PHD_FEE_EDU;
		        				$scope.inputVO.PHD_FEE_LIFE = $scope.outputList.PHD_FEE_LIFE;
		        				$scope.inputVO.PHD_YEAR = $scope.outputList.PHD_YEAR;
		        				$scope.sum();
	        				}
	        			}
	        		}
	        	});
        }
        
        $scope.getSchoolPara = function() {
        	$scope.sendRecv('FPS324', 'getSchoolPara', 'com.systex.jbranch.app.server.fps.fps324.FPS324InputVO', {},
		        function (tota, isError) {
	        		if (!isError) {
	        			if (tota[0].body.outputList.length > 0) {
	        				$scope.schoolPara = tota[0].body.outputList[0];
	        			} else {
	        				$scope.showMsg('ehl_01_common_009');
	        			}
	        		}
	        	});
        }
        
        $scope.sum = function() {
        	isDelete = false;
        	$scope.inputVO.total = ((parseInt($scope.inputVO.UNIVERSITY_FEE_EDU) + parseInt($scope.inputVO.UNIVERSITY_FEE_LIFE)) * parseInt($scope.inputVO.UNIVERSITY_YEAR)) 
        		+ ((parseInt($scope.inputVO.MASTER_FEE_EDU) + parseInt($scope.inputVO.MASTER_FEE_LIFE)) * parseInt($scope.inputVO.MASTER_YEAR)) 
                + ((parseInt($scope.inputVO.PHD_FEE_EDU) + parseInt($scope.inputVO.PHD_FEE_LIFE)) * parseInt($scope.inputVO.PHD_YEAR));
        }
        
        // back FPS320
        $scope.backFPS320 = function() {
        	$scope.inputVO.isFPS321 = true;
        	$scope.inputVO.isDelete = isDelete;
            $scope.closeThisDialog($scope.inputVO);
        };
        
        $scope.chgMomey = function() {
        	$scope.inputVO.UNIVERSITY_YEAR = $scope.inputVO.UNIVERSITY_YEAR ? Math.floor($scope.inputVO.UNIVERSITY_YEAR) : 0;
        	$scope.inputVO.MASTER_YEAR = $scope.inputVO.MASTER_YEAR ? Math.floor($scope.inputVO.MASTER_YEAR) : 0;
        	$scope.inputVO.PHD_YEAR = $scope.inputVO.PHD_YEAR ? Math.floor($scope.inputVO.PHD_YEAR) : 0;
        	$scope.inputVO.UNIVERSITY_FEE_EDU = $scope.inputVO.UNIVERSITY_FEE_EDU ? Math.floor($scope.inputVO.UNIVERSITY_FEE_EDU) : 0;
        	$scope.inputVO.UNIVERSITY_FEE_LIFE = $scope.inputVO.UNIVERSITY_FEE_LIFE ? Math.floor($scope.inputVO.UNIVERSITY_FEE_LIFE) : 0;
        	$scope.inputVO.MASTER_FEE_EDU = $scope.inputVO.MASTER_FEE_EDU ? Math.floor($scope.inputVO.MASTER_FEE_EDU) : 0;
        	$scope.inputVO.MASTER_FEE_LIFE = $scope.inputVO.MASTER_FEE_LIFE ? Math.floor($scope.inputVO.MASTER_FEE_LIFE) : 0;
        	$scope.inputVO.PHD_FEE_EDU = $scope.inputVO.PHD_FEE_EDU ? Math.floor($scope.inputVO.PHD_FEE_EDU) : 0;
        	$scope.inputVO.PHD_FEE_LIFE = $scope.inputVO.PHD_FEE_LIFE ? Math.floor($scope.inputVO.PHD_FEE_LIFE) : 0;
        }
        
        $scope.init();
        $scope.getSchoolPara();
        
        $scope.clear = function() {
        	isDelete = true;
        	$scope.inputVO = {
                	planID: $scope.planID,
                	UNIVERSITY: '',
                	UNIVERSITY_FEE_EDU: 0,
                	UNIVERSITY_FEE_LIFE: 0,
                	UNIVERSITY_YEAR: 0,
                    MASTER: '',
                    MASTER_FEE_EDU: 0,
                    MASTER_FEE_LIFE: 0,
                    MASTER_YEAR: 0,
                    PHD: '',
                    PHD_FEE_EDU: 0,
                    PHD_FEE_LIFE: 0,
                    PHD_YEAR: 0,
                    total: 0
                }
        }
    });