package pkg.exception;

import java.util.ArrayList;

import pkg.order.Order;

public final class StockMarketAssert {
	
	private StockMarketAssert(){
		
	}
	
	public static void AssertCashIsAvailable(String symbol, String traderName, double costOfPurchase, double cashInHand)
			throws StockMarketException {
		
		if(costOfPurchase >= cashInHand){
			String msg = "Cannot place the order for stock: " 
					+ symbol 
					+  " since there is not enough money. "
					+ "Trader: " 
					+ traderName
					+ "\n";
			
			throw new StockMarketException(msg);
		}
	}
	
	public static void AssertOrderDoesNotExist(String symbol, String traderName, ArrayList<Order> ordersPlaced) 
			throws StockMarketException {
		
		boolean stockAlreadyOrdered = false;
		
		int i = 0;
		while(i < ordersPlaced.size() && !stockAlreadyOrdered){
			if (ordersPlaced.get(i).getStockSymbol() == symbol){
				stockAlreadyOrdered = true;
			}
			
			i = i + 1;
		}
		
		if(stockAlreadyOrdered){
			String msg = "Cannot place the order for stock: " 
					+ symbol 
					+  " since an ordered has already been placed for that stock. "
					+ "Trader: " 
					+ traderName
					+ "\n";
			
			throw new StockMarketException(msg);
		}
	}
	
	public static void AssertTraderOwnsEnoughStock(String symbol, String traderName, ArrayList<Order> ordersPlaced, int volume)
			throws StockMarketException{
		
		boolean traderOwnsEnoughStock = true;
		boolean stockFound = false;
		
		int i = 0;
		while(i < ordersPlaced.size() && !stockFound){
			if (ordersPlaced.get(i).getStockSymbol() == symbol){
				stockFound = true;
				int sizeOfOrder = ordersPlaced.get(i).getSize();
				
				if(sizeOfOrder < volume){
					traderOwnsEnoughStock = false;
				}
			}
			
			i = i + 1;
		}
		
		if(!traderOwnsEnoughStock){
			String msg = "Cannot place the order for stock: " 
					+ symbol 
					+  " since trader does not own enough stock. "
					+ "Trader: " 
					+ traderName
					+ "\n";
			
			throw new StockMarketException(msg);
		}
	}
}
