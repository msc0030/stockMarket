package pkg.util;

import java.util.ArrayList;
import pkg.order.BuyOrder;
import pkg.order.Order;
import pkg.order.SellOrder;

public class OrderUtility {
	public static boolean isAlreadyPresent(ArrayList<Order> ordersPlaced,
			Order newOrder) {
          
          	bool orderIsBuyOrder;
          	bool newOrderIsBuyOrder;
          	bool orderIsSellOrder;
          	bool newOrderIsSellOrder;

		for (Order orderPlaced : ordersPlaced) {
			orderIsBuyOrder = orderPlaced instanceof BuyOrder;
                  	newOrderIsBuyOrder = newOrder instanceof BuyOrder;
                  	orderIsSellOrder = orderPlaced instanceof SellOrder;
                  	newOrderIsSellOrder = newOrder instanceof SellOrder;
                  
                  	if ((orderIsBuyOrder && newOrderIsBuyOrder)
					|| (orderIsSellOrder && newOrderIsSellOrder)) {
				if (orderPlaced.getStockSymbol().equals(
						newOrder.getStockSymbol())) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean owns(ArrayList<Order> position, String symbol) {
		for (Order stock : position) {
			if (stock.getStockSymbol().equals(symbol)) {
				return true;
			}
		}
		return false;
	}

	public static Order findAndExtractOrder(ArrayList<Order> position,
			String symbol) {
		for (Order stock : position) {
			if (stock.getStockSymbol().equals(symbol)) {
				position.remove(stock);
				return stock;
			}
		}
		return null;
	}

	public static int ownedQuantity(ArrayList<Order> position, String symbol) {
		int ownedQuantity = 0;
		for (Order stock : position) {
			if (stock.getStockSymbol().equals(symbol)) {
				ownedQuantity += stock.getSize();
			}
		}
		return ownedQuantity;
	}
}
