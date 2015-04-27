package pkg.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import pkg.exception.StockMarketException;
import pkg.market.api.PriceSetter;
import pkg.market.Market;
import pkg.market.MarketHistory;

public class OrderBook {
	Market m;
	HashMap<String, ArrayList<Order>> buyOrders;
	HashMap<String, ArrayList<Order>> sellOrders;

	public OrderBook(Market m) {
		this.m = m;
		buyOrders = new HashMap<String, ArrayList<Order>>();
		sellOrders = new HashMap<String, ArrayList<Order>>();
	}

	public void addBuyOrderToOrderBook(BuyOrder buyOrder){
		ArrayList<Order> listOrders = buyOrders.get(buyOrder.getStockSymbol());
		if (listOrders == null) {
			listOrders = new ArrayList<Order>();
			buyOrders.put(buyOrder.getStockSymbol(), listOrders);
		}
		listOrders.add(buyOrder);
	}

	public void addSellOrderToOrderBook(SellOrder sellOrder){
		ArrayList<Order> listOrders = sellOrders.get(sellOrder.getStockSymbol());
		if (listOrders == null) {
			listOrders = new ArrayList<Order>();
			sellOrders.put(sellOrder.getStockSymbol(), listOrders);
		}
		listOrders.add(sellOrder);
	}

	public void trade() throws StockMarketException{
		String stockSymbol = "SBUX";
		ArrayList<Order> stockBuyOrders = buyOrders.get(stockSymbol);
		ArrayList<Order> stockSellOrders = sellOrders.get(stockSymbol);

		int lowestPrice = Integer.MAX_VALUE;
		int highestPrice = 0;

		//Get The Range of Prices
		for (int i = 0; i < stockBuyOrders.size(); i++)
		{
			if (!stockBuyOrders.get(i).isMarketOrder) {
				if (stockBuyOrders.get(i).getPrice() > highestPrice)
					highestPrice = (int)stockBuyOrders.get(i).getPrice();
				else if (stockBuyOrders.get(i).getPrice() < lowestPrice)
					lowestPrice = (int)stockBuyOrders.get(i).getPrice();
			}
		}
		for (int i = 0; i < stockSellOrders.size(); i++)
		{
			if (!stockSellOrders.get(i).isMarketOrder) {
				if (stockSellOrders.get(i).getPrice() > highestPrice)
					highestPrice = (int)stockSellOrders.get(i).getPrice();
				else if (stockSellOrders.get(i).getPrice() < lowestPrice)
					lowestPrice = (int)stockSellOrders.get(i).getPrice();
			}
		}

		highestPrice++;

		int bestTransaction = 0;
		int challengeTransaction = 0;
		double bestPrice = (double)highestPrice;
		double bidPrice = lowestPrice;

		while (bidPrice <= highestPrice)
		{
			challengeTransaction = this.calculateTransactionAmount(bidPrice, stockSellOrders, stockBuyOrders);
			if (challengeTransaction > bestTransaction) {
				bestTransaction = challengeTransaction;
				bestPrice = bidPrice;
			}
			bidPrice += 0.5;
		}

		PriceSetter priceSetter = new PriceSetter();
		m.getMarketHistory().setSubject(priceSetter);
		priceSetter.registerObserver(m.getMarketHistory());
		priceSetter.setNewPrice(m, stockSymbol, bestPrice);

		this.commenceTrading(bestPrice, stockSymbol);
		this.trimOrdersByPrice(bestPrice, stockSymbol);
	}

	public int calculateTransactionAmount(double price, ArrayList<Order> sellOrders, ArrayList<Order> buyOrders)
	{
		int sellers = 0;
		int buyers = 0;
		int transactions = 0;

		for (Order o : sellOrders)
		{
			if (o.getPrice() <= price || o.isMarketOrder)
				sellers += o.getSize();
		}
		for (Order o : buyOrders)
		{
			if (o.getPrice() >= price || o.isMarketOrder)
				buyers += o.getSize();
		}

		transactions = buyers > sellers ? sellers : buyers;
		return transactions;
	}

	public void trimOrdersByPrice(double price, String symbol)
	{
		ArrayList<Order> newSellOrders = new ArrayList<Order>();
		ArrayList<Order> newBuyOrders = new ArrayList<Order>();

		for (Order o : this.sellOrders.get(symbol))
		{
			if (o.getPrice() > price && !o.isMarketOrder)
			{
				newBuyOrders.add(o);
			}
		}
		for (Order o : this.buyOrders.get(symbol))
		{
			if (o.getPrice() < price && !o.isMarketOrder)
			{
				newSellOrders.add(o);
			}
		}

		this.buyOrders.remove(symbol);
		this.buyOrders.put(symbol, newBuyOrders);
		this.sellOrders.remove(symbol);
		this.sellOrders.put(symbol, newSellOrders);
	}

	public void commenceTrading(double price, String symbol) throws StockMarketException
        {
		for (Order o : this.sellOrders.get(symbol))
		{
			if (o.getPrice() <= price || o.isMarketOrder)
			{
				o.getTrader().tradePerformed(o, price);
			}
		}
		for (Order o : this.buyOrders.get(symbol))
		{
			if (o.getPrice() >= price || o.isMarketOrder)
			{
				o.getTrader().tradePerformed(o, price);
			}
		}
	}
}
