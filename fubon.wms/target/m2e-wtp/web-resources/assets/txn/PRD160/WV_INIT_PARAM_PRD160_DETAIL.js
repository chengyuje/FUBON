function formatWebViewParam() {
	var custId = this.getParam().custId;
	var keyNo = this.getParam().keyNo;
	var productId = this.getParam().productId;
	var productName = this.getParam().productName;
	
	var obj = {
		'KEY_NO': keyNo,
		'PRD_ID': productId,
		'INSPRD_NAME': productName
	};
	this.getScope().row = obj;
	this.getScope().cust_id = custId;
}

function formatWebViewResParam(data) {
	this.setResult(data);
}