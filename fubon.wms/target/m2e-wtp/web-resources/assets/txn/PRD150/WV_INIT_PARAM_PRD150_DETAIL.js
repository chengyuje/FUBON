function formatWebViewParam() {
	var productId = this.getParam().productId;
	var productName = this.getParam().productName;
	
	var obj = {
		'PRD_ID': productId,
		'SI_CNAME': productName
	};
	this.getScope().row = obj;
}

function formatWebViewResParam(data) {
	this.setResult(data);
}