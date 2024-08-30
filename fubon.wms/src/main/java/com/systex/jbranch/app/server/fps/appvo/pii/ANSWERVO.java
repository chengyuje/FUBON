package com.systex.jbranch.app.server.fps.appvo.pii;

import java.io.Serializable;
import java.math.BigDecimal;

public class ANSWERVO implements Serializable {

	public ANSWERVO() {
		super();
	}

	private String qanswer_1; // 問題1答案	(01:短期 02:長期)
	private String qanswer_2; // 問題2答案	(01:需要 02:不需要)

	public String getQanswer_1() {
		return qanswer_1;
	}

	public void setQanswer_1(String qanswer_1) {
		this.qanswer_1 = qanswer_1;
	}

	public String getQanswer_2() {
		return qanswer_2;
	}

	public void setQanswer_2(String qanswer_2) {
		this.qanswer_2 = qanswer_2;
	}
}
