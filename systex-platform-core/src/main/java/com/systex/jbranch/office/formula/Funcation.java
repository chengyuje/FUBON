package com.systex.jbranch.office.formula;

public class Funcation implements DataProcessFormula<String>, FuncationFormula {

	public String process(String data, String args) throws Exception {
		String expresion = args;
		if (expresion == null) {
			throw new IllegalArgumentException(
					"未設置DataProcessFormula[Funcation.expresion]參數");
		}

		return String.format(expresion, data);
	}
}
