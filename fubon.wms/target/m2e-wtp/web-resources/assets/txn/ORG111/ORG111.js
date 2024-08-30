/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('ORG111Controller', 
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "ORG111Controller";
        
        //===filter
        getParameter.XML(["ORG.SALES_GROUP_TYPE", "ORG.RESIGN_HANDOVER", "COMMON.YES_NO"], function(totas) {
			if (totas) {
				$scope.mappingSet['ORG.SALES_GROUP_TYPE'] = totas.data[totas.key.indexOf('ORG.SALES_GROUP_TYPE')];
				$scope.mappingSet['ORG.RESIGN_HANDOVER'] = totas.data[totas.key.indexOf('ORG.RESIGN_HANDOVER')];
				$scope.mappingSet['COMMON.YES_NO'] = totas.data[totas.key.indexOf('COMMON.YES_NO')];
			}
		});
        //===
        
        /*****以下是待覆核員工資料*****/
        $scope.confirmEmp = function(){
	    	  $scope.sendRecv("ORG111", "confirmEmp", "com.systex.jbranch.app.server.fps.org111.ORG111InputVO", $scope.inputVO,
                  function(tota, isError) {
                      if (!isError) {
                    	  $scope.empDataLst = tota[0].body.empDataLst;
                          $scope.inputVO.EMP_CELL_NUM = $scope.empDataLst[0].EMP_CELL_NUM;   
                          $scope.inputVO.EMP_DEPT_EXT = $scope.empDataLst[0].EMP_DEPT_EXT;
                          $scope.inputVO.REMARK = $scope.empDataLst[0].REMARK;  
                          $scope.inputVO.loginPrivilegeID = tota[0].body.loginPrivilegeID;
                          return;
                      }
	    	  });
	     }
        
        /*****以下是查詢員工資料*****/
        $scope.inquireEMP = function(){
	    	  $scope.sendRecv("ORG111", "getEmpData", "com.systex.jbranch.app.server.fps.org111.ORG111InputVO", $scope.inputVO,
                  function(tota, isError) {
                      if (!isError) {
                    	  $scope.empDataLst = tota[0].body.empDataLst;
                          $scope.inputVO.EMP_CELL_NUM = $scope.empDataLst[0].EMP_CELL_NUM;   
                          $scope.inputVO.EMP_DEPT_EXT = $scope.empDataLst[0].EMP_DEPT_EXT;
                          $scope.inputVO.REMARK = $scope.empDataLst[0].REMARK;  
                          $scope.inputVO.loginPrivilegeID = tota[0].body.loginPrivilegeID;
                          return;
                      }
	    	  });
	     }
     
        $scope.init = function(){
        	$scope.mappingSet['connect'] = [];
        	
            /****初始化資料*****/
        	if ($scope.connector('get','ORG110_tnsfData') != undefined) {
        		$scope.mappingSet['connect'].push($scope.connector('get','ORG110_tnsfData'));
        	} else if($scope.connector('get','MAO151_PARAMS') != undefined){
        		$scope.mappingSet['connect'].push({EMP_ID : $scope.connector('get','MAO151_PARAMS').EMP_ID, REVIEW_STATUS: 'W'});
        	}
        	
    		$scope.inputVO = {
    				/****初始化資料主畫面*****/
    				EMP_ID: $scope.mappingSet['connect'][0].EMP_ID,
    				EMP_CELL_NUM: '',   
    				EMP_DEPT_EXT: '',    
    				REMARK: '', 
    				ROLE_ID: '',
    				EMP_PHOTO: '',
    				pictureName: '',
    				realpictureName: '',
    				picFlag: '0',
    				loginPrivilegeID: ''
    		};

        	$scope.paramList = []; 
        	if($scope.mappingSet['connect'][0].REVIEW_STATUS === 'W'){
                
        		$scope.confirmEmp();
        		$scope.inquireReviewPO();
        		$scope.inquireTi();
        		$scope.inquireRO();
        		
        	}else{
        		$scope.inquireEMP();       		
        		$scope.inquirePO();
        		$scope.inquireTi();
        		$scope.inquireRO();
        	}
        }
   
        /*****以下是照片*****/
        $scope.inquirePO = function() {
        	$scope.sendRecv("ORG111", "getPoData", "com.systex.jbranch.app.server.fps.org111.ORG111InputVO", $scope.inputVO,
    				function(tota, isError) {
    					if (!isError) {
    						if(tota[0].body.resultList[0]) {
    							$scope.pictureSrc = "";
    							if(tota[0].body.resultList[0].EMP_PHOTO) {
    								var bufView = new Uint8Array(tota[0].body.resultList[0].EMP_PHOTO);
        							var length = bufView.length;
        						    var result = '';
        						    var addition = Math.pow(2,16)-1;
        						    for(var i = 0; i<length; i += addition) {
        						    	if(i + addition > length)
        						    		addition = length - i;
        						    	result += String.fromCharCode.apply(null, bufView.subarray(i,i+addition));
        						    }
        						    $scope.pictureSrc = "data:image/png;base64,"+btoa(result);
    							}
    						}
    						return;
    					}
    		});
        }
        
        $scope.inquireReviewPO = function() {
        	$scope.sendRecv("ORG111", "getReviewPhoto", "com.systex.jbranch.app.server.fps.org111.ORG111InputVO", $scope.inputVO,
    				function(tota, isError) {
    					if (!isError) {
    						if(tota[0].body.resultList[0]) {
    							$scope.pictureSrc = "";
    							if(tota[0].body.resultList[0].EMP_PHOTO) {
    								var bufView = new Uint8Array(tota[0].body.resultList[0].EMP_PHOTO);
        							var length = bufView.length;
        						    var result = '';
        						    var addition = Math.pow(2,16)-1;
        						    for(var i = 0; i<length; i += addition) {
        						    	if(i + addition > length)
        						    		addition = length - i;
        						    	result += String.fromCharCode.apply(null, bufView.subarray(i,i+addition));
        						    }
        						    $scope.pictureSrc = "data:image/png;base64,"+btoa(result);
    							}
    						}
    						return;
    					}
    		});
        }
        
        /*****以下是證照*****/
        $scope.inquireTi = function(){
	    	  $scope.sendRecv("ORG111", "getTiData", "com.systex.jbranch.app.server.fps.org111.ORG111InputVO", $scope.inputVO,
                  function(tota, isError) {
                      if (!isError) {
                    	  $scope.Ti = tota[0].body.empCertLst;     //證照類
                    	  $scope.ins = tota[0].body.insCertLst;     //證照類
                          return;
                      }
	    	  });
        }
        
        /*****以下是角色*****/
        $scope.inquireRO = function(){
	    	  $scope.sendRecv("ORG111", "getRoData", "com.systex.jbranch.app.server.fps.org111.ORG111InputVO", $scope.inputVO,
                  function(tota, isError) {
                      if (!isError) {
                    	  $scope.empRoleLst = tota[0].body.empRoleLst;
                          return;
                      }
	    	  });
        }
        
        //上傳圖片
        $scope.uploadFinshed = function(name, rname) {
        	if(name){
        		$scope.inputVO.picFlag = '1';
        		$scope.inputVO.pictureName = name;
            	$scope.inputVO.realpictureName = rname;
            	$scope.addData();
        	}
        };
        
        //上傳圖片
        $scope.addData = function() {
        		$scope.sendRecv("ORG111", "addData", "com.systex.jbranch.app.server.fps.org111.ORG111InputVO", $scope.inputVO,
					function(totas, isError) {
	                	if (isError) {
	                		$scope.showErrorMsg(totas[0].body.msgData);
	                	}
	                	if (totas.length > 0) {
	                		$scope.inquireReviewPO();
	                	};
    			});       	
	    };
        
        $scope.del = function(idx){
        	$scope.inputVO.ROLE_ID = idx.ROLE_ID;
        	$scope.inputVO.IS_PRIMARY_ROLE = idx.IS_PRIMARY_ROLE;
        	
        	if(idx.IS_PRIMARY_ROLE != null){
		        	$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
			        	$scope.sendRecv("ORG111", "delRole", "com.systex.jbranch.app.server.fps.org111.ORG111InputVO", $scope.inputVO,
			    			function(totas, isError) {
				                if (isError) {
				                	$scope.showErrorMsgInDialog(totas.body.msgData);
				                	$scope.inquireRO();
				                    return;
				                }
				                if (totas.length > 0) {
				                	$scope.empRoleLst = [];
					       		    $scope.inquireRO();
					       		    $scope.inquireEMP();
					       		    $scope.showSuccessMsg("ehl_01_common_003");
				                }
			        	});
		        	});
    		}
        }
        
        $scope.upda = function(){
              	$scope.sendRecv("ORG111", "updateEmpData", "com.systex.jbranch.app.server.fps.org111.ORG111InputVO", $scope.inputVO,
	    			function(totas, isError) {
		                if (isError) {
		                	$scope.showErrorMsgInDialog(totas.body.msgData);
		                    return;
		                }
		                if (totas.length > 0) {
		                	$scope.showMsg("ehl_01_common_019");
		                }
		                $scope.inquireEMP();
		                $scope.reBack();
              	});
        }
        
        /*****以下是修改建一開始視窗畫面'*****/
        $scope.edit = function () {
        	var row = $scope.empDataLst[0];
        	var loginPrivilegeID = $scope.inputVO.loginPrivilegeID;
        	var dialog = ngDialog.open({
        		template: 'assets/txn/ORG111/ORG111_EDIT.html',
        		className: 'ORG111_DETAIL',
        		showClose: false,
        		controller: ['$scope', function($scope) {
        			$scope.row = row;
        			$scope.loginPrivilegeID = loginPrivilegeID;
        		}]
        	});
        	
        	dialog.closePromise.then(function (data) {
        		
        	});
        }
          
          
        /*****以下是修改鍵2開啟視窗******/
        $scope.roedit = function () {
        	var row = $scope.empDataLst[0];
        	var dialog = ngDialog.open({
        		template: 'assets/txn/ORG111/ORG111_EDIT2.html',
				className: 'ORG111_DETAIL2',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
        	});
        	
        	dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					$scope.inquireRO();	
					$scope.inquireEMP();
				}
			});
        }

        $scope.save = function(){
          	if($scope.parameterTypeEditForm.$invalid){
          		$scope.showErrorMsgInDialog("ehl_01_common_022");
          		return;
          	}
          
          	var methodName = 'insert';
          	if($scope.row.PARAM_TYPE != undefined){
          		methodName = 'update'
          	}
          	$scope.sendRecv("ORG111", methodName, "com.systex.jbranch.app.server.fps.org111.ORG111InputVO2", $scope.inputVO,
      			function(totas, isError) {
  	                if (isError) {
  	                	$scope.showErrorMsgInDialog(totas.body.msgData);
  	                    return;
  	                }
  	                if (totas.length > 0) {
  	                	$scope.showMsg((methodName = 'insert') ? "ehl_01_common_001" : "ehl_01_common_006");	       		
  	                }
          	});
        }
        
        $scope.reBack = function() {
        	if ($scope.connector('get', 'ORG110_tnsfData') != undefined) {
        		$scope.connector('set', 'ORG110_queryCondition', $scope.connector('get', 'ORG110_queryCondition'));
        		$rootScope.menuItemInfo.url = "assets/txn/ORG110/ORG110.html";
        	} else if($scope.connector('get','MAO151_PARAMS') != undefined){
        		$rootScope.menuItemInfo.url = "assets/txn/MAO151/MAO151.html";
        	}
    	}
        
        $scope.review = function (row, status) {
        	var row = $scope.empDataLst[0];
    		$confirm({text: '是否' + ((status == 'N') ? '退回 ' : '核可 ') + row.EMP_ID + "-" + row.EMP_NAME + ' 資料'}, {size: 'sm'}).then(function() {
    			$scope.sendRecv("ORG111", "reviewEmp", "com.systex.jbranch.app.server.fps.org111.ORG111InputVO", {EMP_ID: row.EMP_ID, 
    																										      REVIEW_STATUS: status,
    																										      SEQNO: row.SEQNO}, 
    					function(totas, isError) {
    						if (isError) {
    			            	$scope.showErrorMsgInDialog(totas.body.msgData);
    				    		$scope.reBack();
    				    		return;
    			            }
    						if (totas.length > 0) {
    							$scope.showMsg((status == 'N') ? "ehl_01_common_020" : "ehl_01_common_021");
    			       		}
    					}
    			);
    			
	    		$scope.reBack();
    		});
    	}
        
        $scope.init();
});
