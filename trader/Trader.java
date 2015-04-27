package pkg.trader;

import java.util.ArrayList;
import pkg.exception.StockMarketException;
import pkg.exception.StockMarketAssert;
import pkg.market.Market;
import pkg.order.BuyOrder;
import pkg.order.SellOrder;
import pkg.order.Order;
import pkg.order.OrderType;
import pkg.stock.Holding;

public class Trader {
	String name;
	double cashInHand;
	ArrayList<Holding> position;   // Stocks owned by the trader
	ArrayList<Order> ordersPlaced; // Orders placed by the trader

	public Trader(String name, double cashInHand) {
		super();
		this.name = name;
		this.cashInHand = cashInHand;
		this.position = new ArrayList<Holding>();
		this.ordersPlaced = new ArrayList<Order>();
	}

	public String getName() {return this.name;}
	public double getCashInHand() {return this.cashInHand;}
	public ArrayList<Holding> getPosition() {return this.position;}
	public ArrayList<Order> getOrdersPlaces() {return this.ordersPlaced;}

	public void buyFromBank(Market m, String symbol, int volume)
			throws StockMarketException {

		if (m.getStockForSymbol(symbol) == null) {
			throw new StockMarketException("Stock not present (" + symbol + ")");
		}

		double stockPrice = m.getStockForSymbol(symbol).getPrice();
		double costOfPurchase = stockPrice * volume;
		StockMarketAssert.AssertCashIsAvailable(symbol, this.name, costOfPurchase, cashInHand);
		position.get(this.getHoldingIndexForSymbol(symbol)).addToHolding(volume);
		cashInHand = cashInHand - costOfPurchase;
	}

	public void placeNewOrder(Market m, String symbol, int volume,
			double price, OrderType orderType) throws StockMarketException {

		double costOfPurchase = price * volume;

		switch (orderType){
		case BUY:
                  	placeNewBuyOrder(m, symbol, volume, price);
			break;
		case SELL:
                  	placeNewSellOrder(m, symbol, volume, price);
			break;
		}
	}

  	private void placeNewBuyOrder(Market m, String symbol, int volume, double price) {
  		StockMarketAssert.AssertOrderDoesNotExist(symbol, this.name, ordersPlaced);
		StockMarketAssert.AssertCashIsAvailable(symbol, this.name, costOfPurchase, cashInHand);
		BuyOrder newBuyOrder = new BuyOrder(symbol, volume, price, this);
		ordersPlaced.add(newBuyOrder);
		m.addBuyOrder(newBuyOrder);
        }

    	private void placeNewSellOrder(Market m, String symbol, int volume, double price) {
  		StockMarketAssert.AssertOrderDoesNotExist(symbol, this.name, ordersPlaced);
		StockMarketAssert.AssertTraderOwnsEnoughStock(symbol, this.name, this.position, volume);
		SellOrder newSellOrder = new SellOrder(symbol, volume, price, this);
		ordersPlaced.add(newSellOrder);
		m.addSellOrder(newSellOrder);
        }

	public void placeNewMarketOrder(Market m, String symbol, int volume,
			double price, OrderType orderType) throws StockMarketException {

		double costOfPurchase = price * volume;

		switch (orderType){
		case BUY:
			StockMarketAssert.AssertOrderDoesNotExist(symbol, this.name, ordersPlaced);
			StockMarketAssert.AssertCashIsAvailable(symbol, this.name, costOfPurchase, cashInHand);
			BuyOrder newBuyOrder = new BuyOrder(symbol, volume, true, this);
			ordersPlaced.add(newBuyOrder);
			m.addBuyOrder(newBuyOrder);
			break;
		case SELL:
			StockMarketAssert.AssertOrderDoesNotExist(symbol, this.name, ordersPlaced);
			StockMarketAssert.AssertTraderOwnsEnoughStock(symbol, this.name, this.position, volume);
			SellOrder newSellOrder = new SellOrder(symbol, volume, true, this);
			ordersPlaced.add(newSellOrder);
			m.addSellOrder(newSellOrder);
			break;
		}
	}

	public void tradePerformed(Order o, double matchPrice)
			throws StockMarketException {

		StockMarketAssert.AssertTraderPlacedOrder(o, this.name, ordersPlaced);
		double orderValue = o.getSize() * matchPrice;
		int holdingIndex = this.getHoldingIndexForSymbol(o.getStockSymbol());

		if (o instanceof SellOrder) {
			StockMarketAssert.AssertTraderOwnsEnoughStock(o.getStockSymbol(), this.name, this.position, o.getSize());
			this.position.get(holdingIndex).removeFromHolding(o.getSize());

			if (this.position.get(holdingIndex).holdingDepleted()) {
				this.position.remove(holdingIndex);
			}

			this.cashInHand += orderValue;
			this.ordersPlaced.remove(this.ordersPlaced.indexOf(o));
		}
		else if (o instanceof BuyOrder) {
			StockMarketAssert.AssertCashIsAvailable(o.getStockSymbol(), this.name, orderValue, this.cashInHand);
			this.cashInHand -= orderValue;
			this.position.get(holdingIndex).addToHolding(o.getSize());
			this.ordersPlaced.remove(this.ordersPlaced.indexOf(o));
		}
	}

	public void printTrader() {
		System.out.println("Trader Name: " + name);
		System.out.println("=====================");
		System.out.println("Cash: " + cashInHand);
		System.out.println("Stocks Owned: ");

		for (Holding h : this.position) {
			h.printHolding();
		}

		System.out.println("Stocks Desired: ");
		for (Order o : ordersPlaced) {
			o.printOrder();
		}
		System.out.println("+++++++++++++++++++++");
		System.out.println("+++++++++++++++++++++");
	}

	public int getHoldingIndexForSymbol(String symbol)
	{
		//Find the holding in position, or create it if there isn't one
		boolean positionContainsHolding = false;
		Holding hold = null;
		for (Holding h : this.position){
			if (h.getStockSymbol().equals(symbol)) {
				hold = h;
				positionContainsHolding = true;
				break;
			}
		}

		if (!positionContainsHolding) {
			hold = new Holding(symbol, 0);
			this.position.add(hold);
		}

		return position.indexOf(hold);
	}
}
