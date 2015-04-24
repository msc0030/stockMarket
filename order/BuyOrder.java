package pkg.order;

import pkg.exception.StockMarketException;
import pkg.trader.Trader;

public class BuyOrder extends Order {
	
	public BuyOrder(String stockSymbol, int size, double price, Trader trader) {
		this.stockSymbol = stockSymbol;
		this.size = size;
		this.price = price;
		this.trader = trader;
	}

	public BuyOrder(String stockSymbol, int size, boolean isMarketOrder,
			Trader trader) throws StockMarketException {

		this.stockSymbol = stockSymbol;
		this.size = size;
		this.price = 0.0;
		this.trader = trader;
		this.isMarketOrder = true;
		
		if(!isMarketOrder){
			String msg = "An order has been placed without a valid  price.";
			throw new StockMarketException(msg);
		}
	}

	public void printOrder() {
		System.out.println("Stock: " + stockSymbol + " $" + price + " x "
				+ size + " (Buy)");
	}

}
