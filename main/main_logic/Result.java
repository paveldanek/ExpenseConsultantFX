package main_logic;

/**
 * A child class of the DataTransfer class. Adds a result code to a DataTransfer.
 */
public class Result extends DataTransfer {

	public enum Code {
		// result codes can be expanded to meet the needs of additional functionalities
		// of the front/back end of the house.
		SUCCESS, IO_ERROR, WRONG_FILE, NO_ITEMS_TO_READ, NO_CODE
	} // ...

	private Code resultCode;

	public Result() {
		super.reset();
		resultCode = Code.NO_CODE;
	}

	public Code getCode() {
		return resultCode;
	}

	public void setCode(Code resultCode) {
		this.resultCode = resultCode;
	}

}
