/**================================================================================================
 							 custom class service for get Platform XML Parameter										  
===================================================================================================
 @LastUpdate:
 			 2016/08/12 ArthurKO fixed function CUSTOM for replace
 			 2016/08/11 ArthurKO add replace
 			 2016/07/16 ArthurKO created
===================================================================================================
=================================================================================================*/
eSoafApp.factory('getParameter', ['$rootScope', '$http', '$q', '$timeout', 'sysInfoService', 'socketService',
    function($rootScope, $http, $q, $timeout, sysInfoService, socketService) {
		return {
			
			'XML' : function(oJSON, oCallbackFunc, replace) {
				if(!oJSON)return;
//				alert(Object.prototype.toString.call(oJSON));
				var check = function () {
  					var deferred = $q.defer();
  					if(!replace){
	  					if(angular.isDefined($rootScope.server)){
	  						if($rootScope.server.id.length>0){
	  							if(Object.prototype.toString.call(oJSON) === '[object String]'){
	  								if($rootScope.server.id.indexOf(oJSON)!==-1){
	  									deferred.resolve(angular.copy({key:[oJSON], data:[$rootScope.server.data[$rootScope.server.id.indexOf(oJSON)]]}));
	  	  								return deferred.promise;
	  								}
	  							}else if(Object.prototype.toString.call(oJSON) === '[object Object]'){
	  								if($rootScope.server.id.indexOf(oJSON.param_type)!==-1){
	  									deferred.resolve(angular.copy({key:[oJSON.param_type], data:[$rootScope.server.data[$rootScope.server.id.indexOf(oJSON.param_type)]]}));
	  	  								return deferred.promise;
	  								}
	  							}else if(Object.prototype.toString.call(oJSON) === '[object Array]'){
	  								var tmp = {
	  										key:[],
	  									   data:[]
	  								};
	  								for(var i=0; i<oJSON.length; i++){
	  									if($rootScope.server.id.indexOf(oJSON[i]) !== -1){
	  										tmp.key.push($rootScope.server.id[$rootScope.server.id.indexOf(oJSON[i])]);
	  										tmp.data.push($rootScope.server.data[$rootScope.server.id.indexOf(oJSON[i])]);
	  									}
	  								}
	  								if(tmp.key.length>0){
	  									deferred.resolve(angular.copy(tmp));
		  								return deferred.promise;
	  								}
	  							}
	  						}
	  					}
  					}
  					deferred.reject();
  					return deferred.promise;
  				}
  				check().then(
  					function(data){
						debugger;
  						var tmpData=data, deferred=$q.defer();
  						if(Object.prototype.toString.call(oJSON)==='[object Array]'){
  							if(oJSON.length!==data.key.length){
  								function getRawdata(){
  									deferred.resolve(
	  									socketService.sendRecv("XMLInfo", "getXMLInfo", "com.systex.jbranch.common.xmlInfo.XMLInfoInputVO", {xmlInfoList:oJSON}).then(		
  	  		        						function(oResp){
  	  		        							if (oCallbackFunc) {
  	  		        								if(!oResp || !oResp[0].body.xmlInfoList[0] || oResp[0].body.xmlInfoList[0].length===0)return oCallbackFunc(false);
  	  		        								var result = oResp[0].body.xmlInfoList[0];
  	  		        								for(var i=0; i<oJSON.length; i++){
  	  		        	  								if(data.key.indexOf(oJSON[i])===-1){
  	  		        	  									tmpData.key.push(oJSON[i]);
  	  		        	  									tmpData.data.push(result.data[result.key.indexOf(oJSON[i])]);
  	  		        	  								}
  	  		        	  	  						}
  	  		        							}
	  	  		        				})
	  	  		        			);
  									return deferred.promise;
  								}
  								return (getRawdata().then(
  					  					function(){
  					  						oCallbackFunc(angular.copy(tmpData));
  					  					}));
  							}
  						}
  						oCallbackFunc(angular.copy(tmpData));
  					},
  					function(){
						  debugger;
          				if(Object.prototype.toString.call(oJSON) === '[object String]'){
        					//1. single parameter(enter as PKey, format:string)
        					socketService.sendRecv("COMBOBOX", "query", "com.systex.jbranch.commons.ap.combobox.QueryInputVO", {'param_type': oJSON,'desc': false}).then(		
        							function(oResp) {
        								if (oCallbackFunc) {
        									if(!oResp || !oResp[0].body.result || oResp[0].body.result.length === 0)return oCallbackFunc(false);
        									var i = $rootScope.server.id.indexOf(oJSON);
        									if(i === -1){
    											$rootScope.server.id.push(angular.copy(oJSON));
	        			          				$rootScope.server.data.push(angular.copy(oResp[0].body.result));
    										}else{
    											$rootScope.server.id[i] = angular.copy(oJSON);
	        			          				$rootScope.server.data[i] = angular.copy(oResp[0].body.result);
    										}
        									oCallbackFunc(angular.copy({key: [oJSON], data: [oResp[0].body.result]}));
        								}
        							},
        							function(oErr) {
//	        								$rootScope.showErrorMsg(oErr);
        							}
        						);
        				}else if(Object.prototype.toString.call(oJSON) === '[object Object]'){
        					//2. single parameter(enter as {param_type:PKey, desc:sort}, format:object)
        					socketService.sendRecv("COMBOBOX", "query", "com.systex.jbranch.commons.ap.combobox.QueryInputVO", oJSON).then(		
        						function(oResp) {
        							if (oCallbackFunc) {
        								if(!oResp || !oResp[0].body.result || oResp[0].body.result.length === 0)return oCallbackFunc(false);
        								var i = $rootScope.server.id.indexOf(oJSON.param_type);
        								if(!replace){
    										if($rootScope.server.id.indexOf(i) === -1){
    											$rootScope.server.id.push(angular.copy(oJSON.param_type));
	        			          				$rootScope.server.data.push(angular.copy(oResp[0].body.result));
    										}else{
    											$rootScope.server.id[i] = angular.copy(oJSON.param_type);
	        			          				$rootScope.server.data[i] = angular.copy(oResp[0].body.result);
    										}	        									
    									}
        								oCallbackFunc(angular.copy({key: [oJSON.param_type], data: [oResp[0].body.result]}));
        							}
        						},
        						function(oErr) {
//	        							$rootScope.showErrorMsg(oErr);
        						}
        					);
        				}else if(Object.prototype.toString.call(oJSON) === '[object Array]'){
        					//3. multiple parameter(enter as PKey, format:array)
        					socketService.sendRecv("XMLInfo", "getXMLInfo", "com.systex.jbranch.common.xmlInfo.XMLInfoInputVO", {xmlInfoList:oJSON}).then(		
        						function(oResp) {
        							if (oCallbackFunc) {
        								if(!oResp || !oResp[0].body.xmlInfoList[0] || oResp[0].body.xmlInfoList[0].length === 0)return oCallbackFunc(false);
    	  								var tota = oResp[0].body.xmlInfoList[0], //new: xmlList, old: xmlInfoList
    	  									tmp = {
    	  										key: [],
    	  										data: []
    	  									};
    	  								for(var i=0; i<tota.key.length; i++){					// 1. got total keys
    	  									if($rootScope.server.id.indexOf(tota.key[i])!==-1){	// 2. match key
    	  										if(!replace){
    	  											tmp.key.push($rootScope.server.id[$rootScope.server.id.indexOf(tota.key[i])]);
    		  										tmp.data.push($rootScope.server.data[$rootScope.server.id.indexOf(tota.key[i])]);
    	  										}else{
    	  											$rootScope.server.data[$rootScope.server.id.indexOf(tota.key[i])] = angular.copy(tota.data[i]);
    	  											tmp.key.push(tota.key[i]);
    		  										tmp.data.push(tota.data[i]);
    	  										}
    	  									}else{												// 3. no match key
    	  										$rootScope.server.id.push(angular.copy(tota.key[i]));
    	  										$rootScope.server.data.push(angular.copy(tota.data[i]));
    	  										tmp.key.push(tota.key[i]);
		  										tmp.data.push(tota.data[i]);
    	  									}
    	  								}    	
    	  								oCallbackFunc(angular.copy(tmp));
        							}
        						},
        						function(oErr) {
//	        							$rootScope.showErrorMsg(oErr);
        						}
        					);
        				}
  					}
      			);
			},
			
			'CUSTOM' : function(sTxnCode, bizCode, inputVOClass, inputVO, replace) {
//					alert(Object.prototype.toString.call(inputVO));
					inputVO.pageCount = inputVO.pageCount || sysInfoService.$paging.rowCountLimit;
		      		var oTIOA = {};
		      		oTIOA.header = {};
		      		oTIOA.header.InputVOClass = inputVOClass;
		      		oTIOA.header.TxnCode = sTxnCode;
		      		oTIOA.header.BizCode = bizCode;
		      		oTIOA.header.StampTime = true;
		      		oTIOA.header.SupvPwd = '';
		      		oTIOA.header.TXN_DATA = {};
		      		oTIOA.header.SupvID = '';
		      		oTIOA.header.CustID = '';
		      		oTIOA.header.ApplicationID = $rootScope.ApplicationID;
		      		oTIOA.header.BranchID = $rootScope.BranchID;
		      		oTIOA.header.REQUEST_ID = '';
		      		oTIOA.header.TlrID = $rootScope.TlrID;
		      		oTIOA.header.ClientTransaction = true;
		      		oTIOA.header.DevMode = false;
		      		oTIOA.header.WsID = $rootScope.WsID;
		      		oTIOA.header.SectionID = 'esoaf';
		      		oTIOA.header.TRMSEQ = '32';
		      		oTIOA.header["X-Content-Type-Options"] = 'nosniff';
		      		oTIOA.header["X-XSS-Protection"] = '1; mode=block';
		      		oTIOA.header["Content-Security-Policy"] = "default-src 'none';";
		      		oTIOA.header["Referrer-Policy"] = "no-referrer";
		      		oTIOA.body = inputVO;
		      		var tita = angular.copy(JSON.stringify(oTIOA));
		      		
		      		//checking server temper data
	      			var getData = function(){
      					var deferred = $q.defer();
      					if(!replace){
	      					if(angular.isDefined($rootScope.server)){
	      						if($rootScope.server.id.length>0){
	      							for(var i=0; i<$rootScope.server.id.length; i++){
	    	          					if(Object.prototype.toString.call($rootScope.server.id[i]) === '[object Object]'){
	    	          						if($rootScope.server.id[i].name == sTxnCode && $rootScope.server.id[i].method == bizCode){
	    	          							deferred.resolve(angular.copy($rootScope.server.data[i]));
	    	          							return deferred.promise;
	    									}
	    	          					}
	    	          				}
	      						}
	      					}
      					}
      					deferred.reject();
						return deferred.promise;
      				}
		      		
		      		//select data
		      		return (getData().then(
  						function(tempData){
  							return angular.copy(tempData);
  						},
  						function(){
  							var request = $http({ 
							    method: 'POST',
								url: './EsoafDispatcher',
          						data: tita,
          						timeout: 120000
          					});
  							return request.then(
  				          			function(oResp) {
  				          				var chk = function(){
  				          					if(!oResp || !oResp.data || !oResp.data[0].body || oResp.data[0].body.length === 0){
	  				          					deferred.resolve(false);
	  				          					return deferred.promise;
  				          					}
  				          					var deferred = $q.defer(), flag=false;
  			          						for(var i=0; i<$rootScope.server.id.length; i++){
  					          					if(Object.prototype.toString.call($rootScope.server.id[i]) === '[object Object]'){
  					          						if($rootScope.server.id[i].name == sTxnCode && $rootScope.server.id[i].method == bizCode){
  					          							flag = true;
  							          					$rootScope.server.data[i] = oResp.data;
  													}
  					          					}
  					          				}
  			          						if(!flag){
  			          							$rootScope.server.id.push({name: sTxnCode, method: bizCode});
  					          					$rootScope.server.data.push(oResp.data);
  			          						}
  				          					deferred.resolve(oResp.data);
  				          					return deferred.promise;
  				          				}
  				          				return (chk().then(
  			          						function(dbData){
  			          							return angular.copy(dbData[0].body);
  			          						}
  					          			));
  				          			},
  				          			function(oErr){
  				          				return false;
  				          			}
  				          		);
  						}
	          		))
			}
			
		}; //father function END
}]);