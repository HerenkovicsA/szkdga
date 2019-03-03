package com.szakdolgozat.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.szakdolgozat.service.OrderService;
import com.szakdolgozat.components.ProductAndQuantityResponse;
import com.szakdolgozat.domain.Delivery;
import com.szakdolgozat.domain.Order;
import com.szakdolgozat.domain.Product;
import com.szakdolgozat.domain.ProductsToOrders;
import com.szakdolgozat.domain.User;
import com.szakdolgozat.repository.DeliveryRepository;
import com.szakdolgozat.repository.OrderRepository;

import javafx.util.Pair;

@Service
public class OrderServiceImpl implements OrderService {

	private OrderRepository or;
	private DeliveryRepository dr;
	private UserService us;
	private ProductsToOrdersService ptos;
	private ProductService ps;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	public OrderServiceImpl(OrderRepository or, DeliveryRepository dr, UserService us, ProductsToOrdersService ptos, ProductService ps) {
		this.or = or;
		this.dr = dr;
		this.us	= us;
		this.ptos = ptos;
		this.ps = ps;
	}
	
	@Override
	public List<Order> findAll() throws Exception {
		List<Order> orderList = new ArrayList<Order>();
		for (Order order : or.findAllByOrderByDeadLineAsc()) {
			orderList.add(order);
		}
		if(orderList.isEmpty()) throw new Exception("No order found!");
		return orderList;
	}

	@Override
	public List<Order> findOrdersOfDelivery(long deliveryId) throws Exception {
		List<Order> orderList = new ArrayList<Order>();
		for (Order order : or.findAllByDelivery(dr.findById(deliveryId).get())) {
			orderList.add(order);
		}
		if(orderList.isEmpty()) throw new Exception("No order found for delivery id!");
		return orderList;
	}

	@Override
	public Set<ProductsToOrders> findProductsOfOrder(long orderId) throws Exception{
		Order order = or.findById(orderId).get();
		Set<ProductsToOrders> productsToOrderSet = order.getProductsToOrder();
		if(productsToOrderSet.isEmpty()) throw new Exception("No products to order");
		return productsToOrderSet;
	}

	@Override
	@Transactional
	public String deleteOrder(long id) {
		Optional<Order> orderToRemove = or.findById(id);
		if(orderToRemove.isPresent()) {
			removeOrderFromUser(orderToRemove.get());
			removeOrderFromDelivery(orderToRemove.get());
			removeOrderFromProductsToOrders(orderToRemove.get());
			or.deleteById(id);
			return "deleted";
		}else {
			return "not exists";
		}
	}
	
	private void removeOrderFromProductsToOrders(Order order) {
		Set<ProductsToOrders> ptoIterbale =  order.getProductsToOrder();
		for (ProductsToOrders productsToOrders : ptoIterbale) {
			ptos.removeProductsToOrdersFromProducts(productsToOrders, productsToOrders.getProduct());
			ptos.deletePtO(productsToOrders);
		}
	}

	private void removeOrderFromUser(Order orderToRemove) {
		User user = orderToRemove.getUser();
		Set<Order> orderSet = user.getOrdersOfUser();
		Set<Order> newOrderSet = new HashSet<Order>();
		boolean hasChanged = false;
		for (Order order : orderSet) {
			if(!order.equals(orderToRemove)) {
				newOrderSet.add(order);
			}else {
				hasChanged = true;
			}
		}
		if(!hasChanged) {
			log.error("Order with " + orderToRemove.getId() + " does not belong to user: " + user.getName());
		}else {
			user.clearOrdersOfUser();
			for (Order order : newOrderSet) {
				user.addToOrdersOfUser(order);
			}
		}
	}
	
	private void removeOrderFromDelivery(Order orderToRemove) {
		Delivery delivery = orderToRemove.getDelivery();
		if(delivery != null) {
			Set<Order> orderSet = delivery.getOrdersOfDelivery();
			if(orderSet.contains(orderToRemove)) {
				orderSet.remove(orderToRemove);
				delivery.setOrdersOfDelivery(orderSet);
			} else log.error("Order with " + orderToRemove.getId() + " does not belong to delivery with: " + delivery.getId() + " id");
		}		
	}

	@Override
	public List<ProductAndQuantityResponse> getProductsOfOrderList(long orderId) throws Exception {
		List<ProductAndQuantityResponse> resultList = new ArrayList<ProductAndQuantityResponse>();
		Order order = or.findById(orderId).get();
		Set<ProductsToOrders> productsToOrderSet = order.getProductsToOrder();
		if(productsToOrderSet.isEmpty()) throw new Exception("No products to order");
		for (ProductsToOrders productToOrder : productsToOrderSet) {
			resultList.add(new ProductAndQuantityResponse(productToOrder.getProduct().getId(), 
					productToOrder.getProduct().getName(), productToOrder.getQuantity(), productToOrder.getProduct().getPrice()));
		}
		return resultList;
	}

	@Override
	public void editOrder(Map<Object, Object> map) {
		if(map.isEmpty()) log.error("Map is empty");
		Order orderToEdit = or.findById(Long.valueOf(map.get("orderId").toString())).get();
		if(orderToEdit.getUser().getId() != Long.parseLong(map.get("userId").toString())) {
			User newUser;
			try {
				newUser = us.findUserById(Long.parseLong(map.get("userId").toString()));
				orderToEdit.setUser(newUser);
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
		orderToEdit.setValue(Double.parseDouble(map.get("value").toString()));
		String[] fullDate = map.get("deadLine").toString().split("-");
		int year = Integer.parseInt(fullDate[0]);
		int month = Integer.parseInt(fullDate[1]);
		int day = Integer.parseInt(fullDate[2]);
		orderToEdit.setDeadLine(LocalDate.of(year, month, day));
		orderToEdit.setDone(Boolean.parseBoolean(map.get("done").toString()));
		
		ArrayList<String> productInfos = (ArrayList)map.get("products");
		String[] productArrayInfo; // values are: productId, piece, delete?
		for (String productInfo : productInfos) {
			productArrayInfo = productInfo.split(";");
			
			if(Boolean.parseBoolean(productArrayInfo[2])) {
				try {
					ptos.deleteByOrderIdAndProductId(Long.valueOf(map.get("orderId").toString()) ,Long.parseLong(productArrayInfo[0]));
				} catch (Exception e) {
					log.error(e.getMessage());
				}
				
			}else {
				Set<ProductsToOrders> productsToOrders = orderToEdit.getProductsToOrder();
				for (ProductsToOrders productsToOrder : productsToOrders) {
					if(productsToOrder.getProduct().getId() == Long.parseLong(productArrayInfo[0]) 
							&& productsToOrder.getQuantity() != Integer.parseInt(productArrayInfo[1])) {
						updatePtO(productsToOrder, Integer.parseInt(productArrayInfo[1]));
					}
				}
			}
		}
		or.save(orderToEdit);
	}

	private void updatePtO(ProductsToOrders pto, int quantity) {
		int diff = pto.getQuantity() - quantity;
		if(diff != 0) {
			ps.updateProductOnStock(pto.getProduct(), diff);
			pto.setQuantity(quantity);
			pto.updateProdActValue();
			ptos.updatePtO(pto);
		}
	}

	@Override
	public List<Order> findOrdersForDelivery() {
		List<Order> orderList = or.findTop20ByDoneFalseAndDeliveryIsNullOrderByDeadLineAsc();
		if(orderList == null || orderList.isEmpty()) return null;
		return orderList;
	}
	
	@Override
	public List<Order> findOrdersForDelivery2(Double cargoSize, double cargoLimit, Delivery delivery) {
		double spaceLeft = cargoSize.doubleValue();
		double limit = spaceLeft * cargoLimit;
		boolean over = false;
		List<Order> finalOrderList = new ArrayList<Order>();
		List<Order> orderList;
		List<Order> lastList = new ArrayList<Order>();
		while(limit >= (cargoSize - spaceLeft) && !over) {
			orderList = or.findTop20ByDoneFalseAndDeliveryIsNullOrderByDeadLineAsc();
			//exit if there is no more order in db or the actual list is the same as the last one was
			if(orderList == null || orderList.isEmpty() || lastList.containsAll(orderList)) {
				over = true;
			} else {
				for (Order order : orderList) {
					order.countVolume();
					if( spaceLeft - order.getVolume() >= 0  && !over) {
						spaceLeft -= order.getVolume();
						finalOrderList.add(order);
						order.setDelivery(delivery);
						if(spaceLeft <= cargoSize*0.1 ) over = true;
					}
				}
				lastList.clear();
				lastList.addAll(orderList);
				updateListOfOrders(finalOrderList);
			}
		}
		
		return finalOrderList;
	}

	/**
	 * Updates the orders in db - setting delivery to not null value
	 * @param finalOrderList List of Orders to save
	 */
	private void updateListOfOrders(List<Order> finalOrderList) {
		or.saveAll(finalOrderList);
		
	}

	@Override
	public Order getOrderById(long orderId) {
		Optional<Order> oOrder = or.findById(orderId);
		if(oOrder.isPresent()) {
			return oOrder.get();
		}
		return null;
	}

	@Override
	public int setOrderDone(long orderId, boolean b) {
		Order order= getOrderById(orderId);
		if(order == null) return 0;
		if(order.isDone() == b) return 0;
		order.setDone(b);
		or.save(order);
		return 1;
	}

	@Override
	public String makeAnOrder(String email, HashMap<Product, Integer> items) {
		User user = us.findByEmail(email);
		Order order = new Order();
		order.setDeadLine(LocalDate.now().plusWeeks(1));
		order.setUser(user);
		Set<ProductsToOrders> ptoSet = new HashSet<ProductsToOrders>();
		double value = 0;
		int amount;
		for (Product product : items.keySet()) {
			amount = items.get(product);
			if((product.getOnStock() - amount) >= 0) {
				ptoSet.add(new ProductsToOrders(product, order, amount));
				value +=  amount * product.getPrice();
				product.setOnStock(product.getOnStock() - amount);
			} else {
				return product.getName() + " termékből csak " + product.getOnStock() + " db van.";
			}
		}
		for (Product product : items.keySet()) {
			ps.saveProduct(product);
		}
		order.setValue(value);
		order.setProductsToOrder(ptoSet);
		order.setDone(false);
		or.save(order);
		return "ok";
	}

	@Override
	public List<Pair<Product, Integer>> getProductsOfOrder(long orderId) {
		Order order = getOrderById(orderId);
		if (order == null) return null;
		List<Pair<Product, Integer>> resultList = new ArrayList<Pair<Product, Integer>>();
		Set<ProductsToOrders> ptoSet = order.getProductsToOrder();
		if(ptoSet.isEmpty()) return null;
		for (ProductsToOrders pto : ptoSet) {
			resultList.add(new Pair<Product, Integer>(pto.getProduct(),pto.getQuantity()));
		}
		return resultList;
	}

	@Override
	public List<Pair<Product, Integer>> getMissingProductList(HashMap<Product, Integer> cart) {
		List<Pair<Product, Integer>> missingList = new ArrayList<Pair<Product, Integer>>();
		for (Product product : cart.keySet()) {
			if(product.getOnStock() < cart.get(product)) {
				missingList.add(new Pair<Product, Integer>(product,product.getOnStock()));
			}
		}
		return missingList;
	}

	@Override
	public boolean hasEnoughForOneDelivery(double cargoSize) {
		List<Order> orders = or.findByDoneFalseAndDeliveryIsNull();
		double volume = 0;
		for (Order order : orders) {
			order.countVolume();
			volume += order.getVolume();
		}
		if(volume >= cargoSize) return true;
		log.info("There is no enough Order for a delivery. Only (" + volume + ")");
		return false;
	}

	private List<Order> findUrgentOrders() {
		LocalDate tomorrow = LocalDate.now().plusDays(1);
		List<Order> orders = or.findAllByDeliveryIsNullAndDeadLineBefore(tomorrow);
		if(orders != null) return orders;
		return null;
	}
	
	@Override
	public boolean hasUrgentOrder() {
		List<Order> orders = findUrgentOrders();
		if(orders.isEmpty()) return false;
		return true;
	}

	@Override
	public String deleteProductFromOrder(long productId, long orderId) {
		Product product = ps.getProductById(productId);
		Order order = getOrderById(orderId);
		if(product != null && order != null) {
			removeProductFromOrder(product, order);
			if(!isAnyProductLeft(order)) {
				or.delete(order);
				return "removed";
			};
			return "deleted";
		}else {
			return "not exists";
		}
	}

	private boolean isAnyProductLeft(Order order) {
		if(order.getProductsToOrder().isEmpty()) return false;
		return true;
	}

	private void removeProductFromOrder(Product product, Order order) {
		Set<ProductsToOrders> ptoSet = order.getProductsToOrder();
		for (ProductsToOrders pto : ptoSet) {
			if(pto.getProduct().equals(product)) {
				product.getProductstoOrder().remove(pto);
				product.setOnStock(product.getOnStock() + pto.getQuantity());
				ptoSet.remove(pto);
				ptos.deletePtO(pto);
				ps.saveProduct(product);
				or.save(order);
			}
		}
		
	}

}
