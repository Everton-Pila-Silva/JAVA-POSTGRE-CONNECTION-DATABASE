package app;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import db.DB;
import entites.Order;
import entites.OrderStatus;
import entites.Product;

public class Program {

	public static void main(String[] args) throws SQLException {
		
		
		Connection conn = DB.getConnection();
	
		Statement st = conn.createStatement();
			
		ResultSet rs = st.executeQuery("SELECT * FROM tb_order "
				+ "INNER JOIN tb_order_product ON tb_order.id = tb_order_product.order_id "
				+ "INNER JOIN tb_product ON tb_product.id = tb_order_product.product_id");
			
		Map<Long, Order> map = new HashMap<>();
		Map<Long, Product> prods = new HashMap<>();
		
		while (rs.next()) { //comando next é para percorrer as linhas da tabela
			
			Long orderId = rs.getLong("order_id");
			if(map.get(orderId) == null) {
			Order order = instantiateOrder(rs);
			map.put(orderId, order);
			}
			
			Long productId = rs.getLong("product_id");
			
			if(prods.get(productId) == null) {
				Product p = instantiateProduct(rs);
				prods.put(productId, p);
			}
		map.get(orderId).getProducts().add(prods.get(productId));
		}
		
		for (Long orderId : map.keySet()) {
			System.out.println(map.get(orderId));
			for(Product p : map.get(orderId).getProducts()) {
				System.out.println(p);
			}
			System.out.println();
		}
		
		
	}
	
	private static Order instantiateOrder(ResultSet rs) throws SQLException {
		Order order = new Order();
		
		order.setId(rs.getLong("order_id"));
		order.setLatitude(rs.getDouble("latitude"));
		order.setLongitude(rs.getDouble("longitude"));
		order.setMoment(rs.getTimestamp("moment").toInstant()); //converte valores tipo data
		order.setStatus(OrderStatus.values()[rs.getInt("status")]);
		
		return order;
	}
	
	private static Product instantiateProduct(ResultSet rs) throws SQLException {
		Product p = new Product();
		
		p.setId(rs.getLong("product_id"));
		p.setDescription(rs.getString("description"));
		p.setName(rs.getString("name"));
		p.setImageUri(rs.getString("image_uri"));
		p.setPrice(rs.getDouble("price"));
		
		return p;
	}
	
}
