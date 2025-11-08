package com.a2y.salesHelper.exception;

public class ExcelValidationException extends RuntimeException {
    private final String fieldName;
    private final int rowNumber;
    private final String sheetName;

    public ExcelValidationException(String fieldName, int rowNumber, String sheetName) {
        super(String.format("Field '%s' cannot be null or empty at row %d in sheet '%s'",
                fieldName, rowNumber, sheetName));
        this.fieldName = fieldName;
        this.rowNumber = rowNumber;
        this.sheetName = sheetName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public String getSheetName() {
        return sheetName;
    }
}
