package pkg.exception;
public class StockMarketException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message;

	public StockMarketException() {
		super();
	}

	public StockMarketException(String message) {
		super();
		this.message = message;
	}

	@Override
	public void printStackTrace() {
		System.out.println("Stock Market Violation Exception");
		System.out.println(message);
	}
}
