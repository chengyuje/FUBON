// I load the "Lazy" module and resolve the returned Promise
// when the components and the relevant templates have been
// loaded.
eSoafApp.factory("withLazyModule",['$rootScope', '$templateCache','$q',
	function( $rootScope, $templateCache, $q ) {
		var deferred = $q.defer();
		var promise = null;
		var lstJS = [];

		///======================
		/// 重設變數
		///======================
		function resetPromise() {
			deferred = $q.defer();
			promise = null;
			console.log('resetPromise');
		}
		///======================
		/// 
		///======================
		function loadModule(scriptJS, successCallback, errorCallback ) {
			successCallback = ( successCallback || angular.noop );
			errorCallback = ( errorCallback || angular.noop );
			promise = deferred.promise;

			// If the module has already been loaded then
			// simply bind the handlers to the existing promise.
			// No need to try and load the files again.	
			// 
			//====
			// IE8 不支援 indexOf
			//====		
			// lstJS.indexOf = lstJS.indexOf || function(){};
			// if (lstJS.indexOf(scriptJS) >= 0) {
			// 	//暫不使用，應判斷 JS 是否已載入，而略過後續流程

			// 	return(
			// 		promise.then( successCallback, errorCallback )
			// 	);
			// }			
			lstJS.push(scriptJS);
			// Wire the callbacks into the deferred outcome.
			promise.then( successCallback, errorCallback );
			// Load the module templates and components.
			// --
			// The first dependency here is an HTML file which
			// is loaded using the text! plugin. This will pass
			// the value through as an HTML string.
			require(
				[
					scriptJS
				],
				function requrieSuccess( templatesHtml ) {

					// Fill the template cache. The file content
					// is expected to be a list of top level
					// Script tags.
					$( templatesHtml ).each(
						function() {
							var template = $( this );
							var id = template.attr( "id" );
							var content = template.html();

							$templateCache.put( id, content );
						}
					);

					// Module loaded, resolve deferred.
					$rootScope.$apply(
						function() {
							console.log('deferred.resolve');
							deferred.resolve();
							resetPromise();
						}
					);
				},
				function requireError( error ) {

					// Module load failed, reject deferred.
					$rootScope.$apply(
						function() {
							deferred.reject( error );
							resetPromise();
						}
					);
				}
			);
			return( promise );
			
		}
		return (loadModule);
	}
]);