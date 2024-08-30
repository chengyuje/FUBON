/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM411_INSERTController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM411_INSERTController";
		
		console.log($scope.setupType);
		$scope.inputVO = {
			roleList: [],
			roleList_a: [],
			roleList_b: [],
			roleList_c: [],
			prodType: [],
			conList: []
		}
		
		$scope.roleList=[];
		$scope.roleList = [];
    	$scope.roleList_a = [];
    	$scope.roleList_b = [];
    	$scope.roleList_c =[];
    	$scope.LV1list = [];
		$scope.LV2list = [];
		$scope.LV3list = [];
		$scope.LV4list = [];
		
		// combobox
		getParameter.XML(["SOT.PROD_TYPE", "CRM.CON_DEGREE", "CRM.DISCOUNT", "CRM.DISCOUNT_RNG_TYPE", 
		                  "CRM.BRG_ROLEID_LV1", "CRM.BRG_ROLEID_LV2", "CRM.BRG_ROLEID_LV3", "CRM.BRG_ROLEID_LV4", 
		                  "CRM.BRG_ROLEID_UHRM_LV1", "CRM.BRG_ROLEID_UHRM_LV2", "CRM.BRG_ROLEID_UHRM_LV3", "CRM.BRG_ROLEID_UHRM_LV4"], 
			function(totas) {
				if (totas) {
					$scope.mappingSet['SOT.PROD_TYPE'] = totas.data[totas.key.indexOf('SOT.PROD_TYPE')];
					$scope.mappingSet['CRM.CON_DEGREE'] = totas.data[totas.key.indexOf('CRM.CON_DEGREE')];
					$scope.mappingSet['CRM.DISCOUNT'] = totas.data[totas.key.indexOf('CRM.DISCOUNT')];
					$scope.mappingSet['CRM.DISCOUNT_RNG_TYPE'] = totas.data[totas.key.indexOf('CRM.DISCOUNT_RNG_TYPE')];
					if ($scope.setupType == '0') {
						$scope.LV1list = totas.data[totas.key.indexOf('CRM.BRG_ROLEID_LV1')];
						$scope.LV2list = totas.data[totas.key.indexOf('CRM.BRG_ROLEID_LV2')];
						$scope.LV3list = totas.data[totas.key.indexOf('CRM.BRG_ROLEID_LV3')];
						$scope.LV4list = totas.data[totas.key.indexOf('CRM.BRG_ROLEID_LV4')];
					} else {
						$scope.LV1list = totas.data[totas.key.indexOf('CRM.BRG_ROLEID_UHRM_LV1')];
						$scope.LV2list = totas.data[totas.key.indexOf('CRM.BRG_ROLEID_UHRM_LV2')];
						$scope.LV3list = totas.data[totas.key.indexOf('CRM.BRG_ROLEID_UHRM_LV3')];
						$scope.LV4list = totas.data[totas.key.indexOf('CRM.BRG_ROLEID_UHRM_LV4')];
					}
					$scope.init();
				}
			}
		);
		
		$scope.init = function() {
			
			$scope.mappingSet['CRM.ROLEID_LV1_A'] = angular.copy($scope.LV1list);
			$scope.mappingSet['CRM.ROLEID_LV1_B'] = angular.copy($scope.LV1list);
			$scope.mappingSet['CRM.ROLEID_LV1_C'] = angular.copy($scope.LV1list);
			$scope.mappingSet['CRM.ROLEID_LV1_D'] = angular.copy($scope.LV1list);
			
			$scope.mappingSet['CRM.ROLEID_LV2_A'] = angular.copy($scope.LV2list);
			$scope.mappingSet['CRM.ROLEID_LV2_B'] = angular.copy($scope.LV2list);
			$scope.mappingSet['CRM.ROLEID_LV2_C'] = angular.copy($scope.LV2list);
			$scope.mappingSet['CRM.ROLEID_LV2_D'] = angular.copy($scope.LV2list);
			
			$scope.mappingSet['CRM.ROLEID_LV3_A'] = angular.copy($scope.LV3list);
			$scope.mappingSet['CRM.ROLEID_LV3_B'] = angular.copy($scope.LV3list);
			$scope.mappingSet['CRM.ROLEID_LV3_C'] = angular.copy($scope.LV3list);
			$scope.mappingSet['CRM.ROLEID_LV3_D'] = angular.copy($scope.LV3list);
			
			$scope.mappingSet['CRM.ROLEID_LV4_A'] = angular.copy($scope.LV4list);
			$scope.mappingSet['CRM.ROLEID_LV4_B'] = angular.copy($scope.LV4list);
			$scope.mappingSet['CRM.ROLEID_LV4_C'] = angular.copy($scope.LV4list);
			$scope.mappingSet['CRM.ROLEID_LV4_D'] = angular.copy($scope.LV4list);
		};
		
		//全選
    	$scope.checkrow = function() {
    		if ($scope.clickAll) {
    			angular.forEach($scope.mappingSet['SOT.PROD_TYPE'], function(row) {
    				row.SELECTED = true;
    			});
    		} else {
    			angular.forEach($scope.mappingSet['SOT.PROD_TYPE'], function(row) {
    				row.SELECTED = false;
    			});
    		}
        };
       
       
		//層級List - 第一階段
    	$scope.roletype = function (data) {
 	    		$scope.roleList.push({'data':data});
	    		
	    		if($scope.roleList.length >0){
		    		var uniq = _.uniqBy($scope.roleList,function(o){
		    			return (o.data);
		    		})
		    	    $scope.list = uniq;
		    		$scope.inputVO.roleList = _.sortBy($scope.list,['data']); 
		    		
	    		}
	    		
	    		//連動
	    		var tempLista  = angular.copy($scope.LV1list);
	    		var tempListb  = angular.copy($scope.LV1list);
	    		var tempListc  = angular.copy($scope.LV1list);
	    		var tempListd  = angular.copy($scope.LV1list);
	    		
	    		if(data == $scope.ROLE_a || ($scope.ROLE_a != null && $scope.ROLE_a != '')) {
    				 	
    				  var index1 = tempListb.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_a);
    				  var index2 = tempListc.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_a);
    				  var index3 = tempListd.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_a);
    				 
    				  if (index1 > -1) {
    		   	    		 tempListb.splice(index1,1);
    		   	    	}
    				 
    				  if (index2 > -1) {
 		   	    		 tempListc.splice(index2,1);
 		   	    		}
    				  
    				  if (index3 > -1) {
 		   	    		 tempListd.splice(index3,1);
 		   	    		}
    				  
    				$scope.mappingSet['CRM.ROLEID_LV1_B'] =[];
			   		angular.forEach( tempListb, function(row, index, objs){
			   			
			   			$scope.mappingSet['CRM.ROLEID_LV1_B'].push({LABEL: row.LABEL, DATA: row.DATA});
			   		});
			   		$scope.mappingSet['CRM.ROLEID_LV1_C'] =[];
			   		angular.forEach( tempListc, function(row, index, objs){
			   			
			   			$scope.mappingSet['CRM.ROLEID_LV1_C'].push({LABEL: row.LABEL, DATA: row.DATA});
			   		});
			   		$scope.mappingSet['CRM.ROLEID_LV1_D'] =[];
			   		angular.forEach( tempListd, function(row, index, objs){
			   			
			   			$scope.mappingSet['CRM.ROLEID_LV1_D'].push({LABEL: row.LABEL, DATA: row.DATA});
			   		});
    			}
    			
    			if(data == $scope.ROLE_b || ($scope.ROLE_b != null && $scope.ROLE_b != '')) {
    				 		
    				  var index1 = tempLista.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_b);
    				  var index2 = tempListc.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_b);
    				  var index3 = tempListd.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_b);
    				 
    				  if (index1 > -1) {
    		   	    	 tempLista.splice(index1,1);
    		   	    	}
    				 
    				  if (index2 > -1) {
 		   	    		 tempListc.splice(index2,1);
 		   	    		}
    				  
    				  if (index3 > -1) {
 		   	    		 tempListd.splice(index3,1);
 		   	    		}
    				  
    				$scope.mappingSet['CRM.ROLEID_LV1_A'] =[];
			   		angular.forEach( tempLista, function(row, index, objs){
			   			if(row.DATA != $scope.ROLE_b )
			   			$scope.mappingSet['CRM.ROLEID_LV1_A'].push({LABEL: row.LABEL, DATA: row.DATA});
			   		});
			   		$scope.mappingSet['CRM.ROLEID_LV1_C'] =[];
			   		angular.forEach( tempListc, function(row, index, objs){
			   			if(row.DATA != $scope.ROLE_b )
			   			$scope.mappingSet['CRM.ROLEID_LV1_C'].push({LABEL: row.LABEL, DATA: row.DATA});
			   		});
			   		$scope.mappingSet['CRM.ROLEID_LV1_D'] =[];
			   		angular.forEach( tempListd, function(row, index, objs){
			   			if(row.DATA != $scope.ROLE_b )
			   			$scope.mappingSet['CRM.ROLEID_LV1_D'].push({LABEL: row.LABEL, DATA: row.DATA});
			   		});
    			}
    				
    			if(data == $scope.ROLE_c || ($scope.ROLE_c != null && $scope.ROLE_c != '')) {
    				  	
    				  var index1 = tempLista.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_c);
    				  var index2 = tempListb.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_c);
    				  var index3 = tempListd.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_c);
    				 
    				  if (index1 > -1) {
    		   	    	 tempLista.splice(index1,1);
    		   	    	}
    				 
    				  if (index2 > -1) {
 		   	    		 tempListb.splice(index2,1);
 		   	    		}
    				  
    				  if (index3 > -1) {
 		   	    		 tempListd.splice(index3,1);
 		   	    		}
    				$scope.mappingSet['CRM.ROLEID_LV1_A'] =[];
			   		angular.forEach( tempLista, function(row, index, objs){
			   			if(row.DATA != $scope.ROLE_c )
			   			$scope.mappingSet['CRM.ROLEID_LV1_A'].push({LABEL: row.LABEL, DATA: row.DATA});
			   		});
			   		$scope.mappingSet['CRM.ROLEID_LV1_B'] =[];
			   		angular.forEach( tempListb, function(row, index, objs){
			   			if(row.DATA != $scope.ROLE_c )
			   			$scope.mappingSet['CRM.ROLEID_LV1_B'].push({LABEL: row.LABEL, DATA: row.DATA});
			   		});
			   		$scope.mappingSet['CRM.ROLEID_LV1_D'] =[];
			   		angular.forEach( tempListd, function(row, index, objs){
			   			if(row.DATA != $scope.ROLE_c )
			   			$scope.mappingSet['CRM.ROLEID_LV1_D'].push({LABEL: row.LABEL, DATA: row.DATA});
			   		});
    			}
    			
    			if(data == $scope.ROLE_d || ($scope.ROLE_d != null && $scope.ROLE_d != '')) {
    				  	
    				  var index1 = tempLista.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_d);
    				  var index2 = tempListb.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_d);
    				  var index3 = tempListc.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_d);
    				 
    				  if (index1 > -1) {
    		   	    	 tempLista.splice(index1,1);
    		   	    	}
    				 
    				  if (index2 > -1) {
 		   	    		 tempListb.splice(index2,1);
 		   	    		}
    				  
    				  if (index3 > -1) {
 		   	    		 tempListc.splice(index3,1);
 		   	    		}
    				$scope.mappingSet['CRM.ROLEID_LV1_A'] =[];
			   		angular.forEach( tempLista, function(row, index, objs){
			   			if(row.DATA != $scope.ROLE_d )
			   			$scope.mappingSet['CRM.ROLEID_LV1_A'].push({LABEL: row.LABEL, DATA: row.DATA});
			   		});
			   		$scope.mappingSet['CRM.ROLEID_LV1_B'] =[];
			   		angular.forEach( tempListb, function(row, index, objs){
			   			if(row.DATA != $scope.ROLE_d )
			   			$scope.mappingSet['CRM.ROLEID_LV1_B'].push({LABEL: row.LABEL, DATA: row.DATA});
			   		});
			   		$scope.mappingSet['CRM.ROLEID_LV1_C'] =[];
			   		angular.forEach( tempListc, function(row, index, objs){
			   			if(row.DATA != $scope.ROLE_d )
			   			$scope.mappingSet['CRM.ROLEID_LV1_C'].push({LABEL: row.LABEL, DATA: row.DATA});
			   		});
    			}
			$scope.tempLista = tempLista;
			$scope.tempListb = tempListb;
			$scope.tempListc = tempListc;
			$scope.tempListd = tempListd;
	   	};
    	
    	//層級List - 第二階段
    	$scope.roletype_a= function (data) {
    		$scope.roleList_a.push({'data':data});
    		if($scope.roleList_a.length >0){
	    		var uniq_a = _.uniqBy($scope.roleList_a,function(o){
		    	  	return (o.data);
		    	   	})
		    		
		    		  $scope.list_a = uniq_a;
	    		$scope.inputVO.roleList_a = _.sortBy($scope.list_a,['data']);
	    		
    		}
    		//連動
    	
    		var tempLista  = angular.copy($scope.LV2list);
    		var tempListb  = angular.copy($scope.LV2list);
    		var tempListc  = angular.copy($scope.LV2list);
    		var tempListd  = angular.copy($scope.LV2list);
    			
			
			if(data == $scope.ROLE_e || ($scope.ROLE_e != null && $scope.ROLE_e != '')) {
					
				  var index1 = tempListb.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_e);
				  var index2 = tempListc.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_e);
				  var index3 = tempListd.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_e);
				 
				  if (index1 > -1) {
		   	    	 tempListb.splice(index1,1);
		   	    	}
				 
				  if (index2 > -1) {
	   	    		 tempListc.splice(index2,1);
	   	    		}
				  
				  if (index3 > -1) {
	   	    		 tempListd.splice(index3,1);
	   	    		}
				
				
				$scope.mappingSet['CRM.ROLEID_LV2_B'] =[];
		   		angular.forEach( tempListb, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV2_B'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
		   		
		   		$scope.mappingSet['CRM.ROLEID_LV2_C'] =[];
		   		angular.forEach( tempListc, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV2_C'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
		   		
		   		$scope.mappingSet['CRM.ROLEID_LV2_D'] =[];
		   		angular.forEach( tempListd, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV2_D'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
			}
		
			if(data == $scope.ROLE_f || ($scope.ROLE_f != null && $scope.ROLE_f != '')) {
				
				  var index1 = tempLista.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_f);
				  var index2 = tempListc.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_f);
				  var index3 = tempListd.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_f);
				 
				  if (index1 > -1) {
		   	    	 tempLista.splice(index1,1);
		   	    	}
				 
				  if (index2 > -1) {
	   	    		 tempListc.splice(index2,1);
	   	    		}
				  
				  if (index3 > -1) {
	   	    		 tempListd.splice(index3,1);
	   	    		}
				
				
				$scope.mappingSet['CRM.ROLEID_LV2_A'] =[];
		   		angular.forEach( tempLista, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV2_A'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
		   		
		   		$scope.mappingSet['CRM.ROLEID_LV2_C'] =[];
		   		angular.forEach( tempListc, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV2_C'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
		   		
		   		$scope.mappingSet['CRM.ROLEID_LV2_D'] =[];
		   		angular.forEach( tempListd, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV2_D'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
			}
			
			if(data == $scope.ROLE_g || ($scope.ROLE_g != null && $scope.ROLE_g != '')) {
				
				  var index1 = tempLista.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_g);
				  var index2 = tempListc.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_g);
				  var index3 = tempListd.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_g);
				 
				  if (index1 > -1) {
		   	    	 tempLista.splice(index1,1);
		   	    	}
				 
				  if (index2 > -1) {
	   	    		 tempListb.splice(index2,1);
	   	    		}
				  
				  if (index3 > -1) {
	   	    		 tempListd.splice(index3,1);
	   	    		}
				
				
				$scope.mappingSet['CRM.ROLEID_LV2_A'] =[];
		   		angular.forEach( tempLista, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV2_A'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
		   		
		   		$scope.mappingSet['CRM.ROLEID_LV2_B'] =[];
		   		angular.forEach( tempListb, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV2_B'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
		   		
		   		$scope.mappingSet['CRM.ROLEID_LV2_D'] =[];
		   		angular.forEach( tempListd, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV2_D'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
			}
			if(data == $scope.ROLE_h || ($scope.ROLE_h != null && $scope.ROLE_h != '')) {
				
				  var index1 = tempLista.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_h);
				  var index2 = tempListb.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_h);
				  var index3 = tempListc.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_h);
				 
				  if (index1 > -1) {
		   	    	 tempLista.splice(index1,1);
		   	    	}
				 
				  if (index2 > -1) {
	   	    		 tempListb.splice(index2,1);
	   	    		}
				  
				  if (index3 > -1) {
	   	    		 tempListc.splice(index3,1);
	   	    		}
				
				
				$scope.mappingSet['CRM.ROLEID_LV2_A'] =[];
		   		angular.forEach( tempLista, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV2_A'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
		   		
		   		$scope.mappingSet['CRM.ROLEID_LV2_B'] =[];
		   		angular.forEach( tempListb, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV2_B'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
		   		
		   		$scope.mappingSet['CRM.ROLEID_LV2_C'] =[];
		   		angular.forEach( tempListc, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV2_C'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
			}
			$scope.tempListe = tempLista;
			$scope.tempListf = tempListb;
			$scope.tempListg = tempListc;
			$scope.tempListh = tempListd;
    	}
    	
    	//層級List - 第三階段
    	$scope.roletype_b= function (data) {
    		$scope.roleList_b.push({'data':data});
    		if($scope.roleList_b.length >0){
	    		var uniq_b = _.uniqBy($scope.roleList_b,function(o){
		    	   	return (o.data);
		    	   })
		    		
		    		  $scope.list_b = uniq_b;
	    		$scope.inputVO.roleList_b = _.sortBy($scope.list_b,['data']);
	    	}
    		//連動
        	
    		var tempLista  = angular.copy($scope.LV3list);
    		var tempListb  = angular.copy($scope.LV3list);
    		var tempListc  = angular.copy($scope.LV3list);
    		var tempListd  = angular.copy($scope.LV3list);
    			
			
			if(data == $scope.ROLE_i || ($scope.ROLE_i != null && $scope.ROLE_i != '')) {
					
				  var index1 = tempListb.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_i);
				  var index2 = tempListc.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_i);
				  var index3 = tempListd.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_i);
				 
				  if (index1 > -1) {
		   	    	 tempListb.splice(index1,1);
		   	    	}
				 
				  if (index2 > -1) {
	   	    		 tempListc.splice(index2,1);
	   	    		}
				  
				  if (index3 > -1) {
	   	    		 tempListd.splice(index3,1);
	   	    		}
				
				
				$scope.mappingSet['CRM.ROLEID_LV3_B'] =[];
		   		angular.forEach( tempListb, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV3_B'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
		   		
		   		$scope.mappingSet['CRM.ROLEID_LV3_C'] =[];
		   		angular.forEach( tempListc, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV3_C'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
		   		
		   		$scope.mappingSet['CRM.ROLEID_LV3_D'] =[];
		   		angular.forEach( tempListd, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV3_D'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
			}
			
			if(data == $scope.ROLE_j || ($scope.ROLE_j != null && $scope.ROLE_j != '')) {
				
				  var index1 = tempLista.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_j);
				  var index2 = tempListc.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_j);
				  var index3 = tempListd.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_j);
				 
				  if (index1 > -1) {
		   	    	 tempLista.splice(index1,1);
		   	    	}
				 
				  if (index2 > -1) {
	   	    		 tempListc.splice(index2,1);
	   	    		}
				  
				  if (index3 > -1) {
	   	    		 tempListd.splice(index3,1);
	   	    		}
				
				
				$scope.mappingSet['CRM.ROLEID_LV3_A'] =[];
		   		angular.forEach( tempLista, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV3_A'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
		   		
		   		$scope.mappingSet['CRM.ROLEID_LV3_C'] =[];
		   		angular.forEach( tempListc, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV3_C'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
		   		
		   		$scope.mappingSet['CRM.ROLEID_LV3_D'] =[];
		   		angular.forEach( tempListd, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV3_D'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
			}
			
			if(data == $scope.ROLE_k || ($scope.ROLE_k != null && $scope.ROLE_k != '')) {
				
				  var index1 = tempLista.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_k);
				  var index2 = tempListb.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_k);
				  var index3 = tempListd.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_k);
				 
				  if (index1 > -1) {
		   	    	 tempLista.splice(index1,1);
		   	    	}
				 
				  if (index2 > -1) {
	   	    		 tempListb.splice(index2,1);
	   	    		}
				  
				  if (index3 > -1) {
	   	    		 tempListd.splice(index3,1);
	   	    		}
				
				
				$scope.mappingSet['CRM.ROLEID_LV3_A'] =[];
		   		angular.forEach( tempLista, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV3_A'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
		   		
		   		$scope.mappingSet['CRM.ROLEID_LV3_B'] =[];
		   		angular.forEach( tempListb, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV3_B'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
		   		
		   		$scope.mappingSet['CRM.ROLEID_LV3_D'] =[];
		   		angular.forEach( tempListd, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV3_D'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
			}
			
			if(data == $scope.ROLE_l || ($scope.ROLE_l != null && $scope.ROLE_l != '')) {
				
				  var index1 = tempLista.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_l);
				  var index2 = tempListb.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_l);
				  var index3 = tempListc.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_l);
				 
				  if (index1 > -1) {
		   	    	 tempLista.splice(index1,1);
		   	    	}
				 
				  if (index2 > -1) {
	   	    		 tempListb.splice(index2,1);
	   	    		}
				  
				  if (index3 > -1) {
	   	    		 tempListc.splice(index3,1);
	   	    		}
				
				
				$scope.mappingSet['CRM.ROLEID_LV3_A'] =[];
		   		angular.forEach( tempLista, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV3_A'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
		   		
		   		$scope.mappingSet['CRM.ROLEID_LV3_B'] =[];
		   		angular.forEach( tempListb, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV3_B'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
		   		
		   		$scope.mappingSet['CRM.ROLEID_LV3_C'] =[];
		   		angular.forEach( tempListc, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV3_C'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
			}
			$scope.tempListi = tempLista;
			$scope.tempListj = tempListb;
			$scope.tempListk = tempListc;
			$scope.tempListl = tempListd;
    	}
    	
    	//層級List - 第四階段
    	$scope.roletype_c= function (data) {
    		$scope.roleList_c.push({'data':data});
    		if($scope.roleList_c.length >0){
	    		var uniq_c = _.uniqBy($scope.roleList_c,function(o){
		    	  	return (o.data);
		    	  })
		    		
		    		  $scope.list_c = uniq_c;
	    		$scope.inputVO.roleList_c = _.sortBy($scope.list_c,['data']);
	    		
    		}
    		//連動
        	
    		var tempLista  = angular.copy($scope.LV4list);
    		var tempListb  = angular.copy($scope.LV4list);
    		var tempListc  = angular.copy($scope.LV4list);
    		var tempListd  = angular.copy($scope.LV4list);
    			
			
			if(data == $scope.ROLE_m || ($scope.ROLE_m != null && $scope.ROLE_m != '')) {
					
				  var index1 = tempListb.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_m);
				  var index2 = tempListc.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_m);
				  var index3 = tempListd.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_m);
				 
				  if (index1 > -1) {
		   	    	 tempListb.splice(index1,1);
		   	    	}
				 
				  if (index2 > -1) {
	   	    		 tempListc.splice(index2,1);
	   	    		}
				  
				  if (index3 > -1) {
	   	    		 tempListd.splice(index3,1);
	   	    		}
				
				
				$scope.mappingSet['CRM.ROLEID_LV4_B'] =[];
		   		angular.forEach( tempListb, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV4_B'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
		   		
		   		$scope.mappingSet['CRM.ROLEID_LV4_C'] =[];
		   		angular.forEach( tempListc, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV4_C'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
		   		
		   		$scope.mappingSet['CRM.ROLEID_LV4_D'] =[];
		   		angular.forEach( tempListd, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV4_D'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
			}
			
			if(data == $scope.ROLE_n || ($scope.ROLE_n != null && $scope.ROLE_n != '')) {
				
				  var index1 = tempLista.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_n);
				  var index2 = tempListc.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_n);
				  var index3 = tempListd.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_n);
				 
				  if (index1 > -1) {
		   	    	 tempLista.splice(index1,1);
		   	    	}
				 
				  if (index2 > -1) {
	   	    		 tempListc.splice(index2,1);
	   	    		}
				  
				  if (index3 > -1) {
	   	    		 tempListd.splice(index3,1);
	   	    		}
				
				
				$scope.mappingSet['CRM.ROLEID_LV4_A'] =[];
		   		angular.forEach( tempLista, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV4_A'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
		   		
		   		$scope.mappingSet['CRM.ROLEID_LV4_C'] =[];
		   		angular.forEach( tempListc, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV4_C'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
		   		
		   		$scope.mappingSet['CRM.ROLEID_LV4_D'] =[];
		   		angular.forEach( tempListd, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV4_D'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
			}
		
			if(data == $scope.ROLE_o || ($scope.ROLE_o != null && $scope.ROLE_o != '')) {
				
				  var index1 = tempLista.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_o);
				  var index2 = tempListb.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_o);
				  var index3 = tempListd.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_o);
				 
				  if (index1 > -1) {
		   	    	 tempLista.splice(index1,1);
		   	    	}
				 
				  if (index2 > -1) {
	   	    		 tempListb.splice(index2,1);
	   	    		}
				  
				  if (index3 > -1) {
	   	    		 tempListd.splice(index3,1);
	   	    		}
				
				
				$scope.mappingSet['CRM.ROLEID_LV4_A'] =[];
		   		angular.forEach( tempLista, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV4_A'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
		   		
		   		$scope.mappingSet['CRM.ROLEID_LV4_B'] =[];
		   		angular.forEach( tempListb, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV4_B'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
		   		
		   		$scope.mappingSet['CRM.ROLEID_LV4_D'] =[];
		   		angular.forEach( tempListd, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV4_D'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
			}
			
			if(data == $scope.ROLE_p || ($scope.ROLE_p != null && $scope.ROLE_p != '')) {
				
				  var index1 = tempLista.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_p);
				  var index2 = tempListb.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_p);
				  var index3 = tempListc.map(function(e) { return e.DATA; }).indexOf($scope.ROLE_p);
				 
				  if (index1 > -1) {
		   	    	 tempLista.splice(index1,1);
		   	    	}
				 
				  if (index2 > -1) {
	   	    		 tempListb.splice(index2,1);
	   	    		}
				  
				  if (index3 > -1) {
	   	    		 tempListc.splice(index3,1);
	   	    		}
				
				
				$scope.mappingSet['CRM.ROLEID_LV4_A'] =[];
		   		angular.forEach( tempLista, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV4_A'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
		   		
		   		$scope.mappingSet['CRM.ROLEID_LV4_B'] =[];
		   		angular.forEach( tempListb, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV4_B'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
		   		
		   		$scope.mappingSet['CRM.ROLEID_LV4_C'] =[];
		   		angular.forEach( tempListc, function(row, index, objs){
		   			$scope.mappingSet['CRM.ROLEID_LV4_C'].push({LABEL: row.LABEL, DATA: row.DATA});
		   		});
			}
			$scope.tempListm = tempLista;
			$scope.tempListn = tempListb;
			$scope.tempListo = tempListc;
			$scope.tempListp = tempListd;
    	}
    	
    	$scope.list = [];
    	
    	$scope.insert = function () {
    		
    		var ans = $scope.mappingSet['SOT.PROD_TYPE'].filter(function(obj){
           		return (obj.SELECTED == true);
           	});
    		var ans1 = $scope.mappingSet['CRM.CON_DEGREE'].filter(function(obj){
           		return (obj.SELECTED == true);
           	});
    		
    		if(ans.length == 0 && ans1.length){
        		return;
        	}
    		
    		$scope.ans = ans;
    		$scope.ans1 = ans1;
    		$scope.inputVO.prodType = $scope.ans;
    		$scope.inputVO.conList = $scope.ans1;
    		$scope.inputVO.setupType = $scope.setupType;
    		
    		$scope.sendRecv("CRM411", "insert", "com.systex.jbranch.app.server.fps.crm411.CRM411InputVO", $scope.inputVO,
     				function(tota, isError) {
        				if (isError) {
     						$scope.showErrorMsg(tota[0].body.msgData);
     					}
        				
     					if(tota.length > 0) {
     						$scope.init();
     						$scope.closeThisDialog('successful');
     					}else{
     						$scope.showErrorMsg("ehl_01_common_005");
     					}
     			 	});
    	}
		
    	
});
