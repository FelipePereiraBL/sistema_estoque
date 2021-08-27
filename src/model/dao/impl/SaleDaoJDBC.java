package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SaleDao;
import model.entities.Product;
import model.entities.Sale;
import model.services.ProductService;

public class SaleDaoJDBC implements SaleDao
{

	private Connection conn;
	
	public SaleDaoJDBC(Connection conn) 
	{
		this.conn = conn;
	}
	
	@Override
	public void insert(Sale obj) 
	{
		PreparedStatement st = null;
		try 
		{
			st = conn.prepareStatement(
					"INSERT INTO sale "
					+ "(saleDate, clientName, customerPhone, deliveryAddress, totalSale, productId) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			
			st.setDate(1,new java.sql.Date( obj.getSaleDate().getTime()));
			st.setString(2, obj.getClientName());
			st.setInt(3, obj.getCustomerPhone());
			st.setString(4, obj.getDeliveryAddress());
			st.setDouble(5, obj.getSaleValue());
			st.setInt(6, obj.getProduct().getId());
			
			int rowsAffected = st.executeUpdate();
			
			if (rowsAffected > 0) 
			{
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next())
				{
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			}
			else 
			{
				throw new DbException("Unexpected error! No rows affected!");
			}
		}
		catch (SQLException e) 
		{
			throw new DbException(e.getMessage());
		}
		finally 
		{
			DB.closeStatement(st);
		}
	}


	@Override
	public void deleteById(Integer id)
	{
		PreparedStatement st = null;
		try
		{
			st = conn.prepareStatement("DELETE FROM sale WHERE id = ?");
			
			st.setInt(1, id);
			
			st.executeUpdate();
		}
		catch (SQLException e) 
		{
			throw new DbException(e.getMessage());
		}
		finally
		{
			DB.closeStatement(st);
		}
	}
	
	

	@Override
	public List<Sale> findAll() 
	{
		
		PreparedStatement st = null;
		ResultSet rs = null;
		try
		{
			st = conn.prepareStatement(
				"SELECT * FROM sale ORDER BY id");
			rs = st.executeQuery();

			List<Sale> list = new ArrayList<>();
			Map<Integer,Product> map = new HashMap<>();

			while (rs.next()) 
			{
				Product prod=map.get(rs.getInt("productId"));
				if (prod == null) 
				{
					prod = instantiateProduct(rs);
					map.put(rs.getInt("productId"), prod);
				}
				
				Sale obj = new Sale();
				obj.setId(rs.getInt("id"));
				obj.setSaleDate(rs.getDate("saleDate"));
				obj.setClientName(rs.getString("clientName"));
				obj.setCustomerPhone(rs.getInt("customerPhone"));
				obj.setDeliveryAddress(rs.getString("deliveryAddress"));
				obj.setProduct(prod);
				obj.setSaleValue(rs.getDouble("totalSale"));
				list.add(obj);
			}
			return list;
		}
		catch (SQLException e)
		{
			throw new DbException(e.getMessage());
		}
		finally 
		{
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	private Product instantiateProduct(ResultSet rs) throws SQLException
	{
		List<Product>list=new ProductService().findAll();
		Product prod=new Product();
		for (Product product : list) 
		{
			if(product.getId()==rs.getInt("productId"))
			{
				prod=product;
			}
			
		}
		return prod;
	}
		
}