function formatWebViewParam(){
	debugger
	this.getScope().paramMap = this.getParam();
	
	// 450 or 210
	var from = this.getScope().paramMap.from;
	this.getScope().from450 = (from == 'INS450');
	this.getScope().from210 = (from == 'INS210');
	
	if(this.getScope().from450) {
		this.getScope().FROM450_CUST_ID = this.getScope().paramMap.custID;
		this.getScope().FROM450_CUST_NAME = this.getScope().paramMap.custName;
		this.getScope().FROM450_PRD_ID = this.getScope().paramMap.prdID;
		this.getScope().FROM450_BIRTHDAY = this.getScope().paramMap.birthday; // yyyy-MM-dd HH:mm:ss
		this.getScope().FROM450_POLICYFEE = this.getScope().paramMap.realPremium;		
	} else if (this.getScope().from210) {
		this.getScope().FROM210_CUST_ID = this.getScope().paramMap.custID;
		this.getScope().FROM210_CUST_NAME = this.getScope().paramMap.custName;
		this.getScope().FROM210_PRD_ID = this.getScope().paramMap.prdID;
		this.getScope().FROM210_PREMIUM = this.getScope().paramMap.realPremium;
	}
}	

function formatWebViewResParam(data){
	this.setResult(data);
}
