package com.graly.mes.prd.workflow.context.exe.converter;

import com.graly.mes.prd.workflow.context.exe.Converter;

public class BooleanToStringConverter implements Converter {

	private static final long serialVersionUID = 1L;

	public static final String TRUE_TEXT = "T";
	public static final String FALSE_TEXT = "F";

	public boolean supports(Object value) {
		if (value == null)
			return true;
		return (value.getClass() == Boolean.class);
	}

	public Object convert(Object o) {
		String convertedValue = FALSE_TEXT;
		if (((Boolean) o).booleanValue()) {
			convertedValue = TRUE_TEXT;
		}
		return convertedValue;
	}

	public Object revert(Object o) {
		Boolean revertedValue = Boolean.FALSE;
		if (TRUE_TEXT.equals(o)) {
			revertedValue = Boolean.TRUE;
		}
		return revertedValue;
	}
}
