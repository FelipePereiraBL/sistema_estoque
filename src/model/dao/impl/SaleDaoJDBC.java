package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.SaleDao;
import model.entities.Sale;

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
					+ "(saleDate, clientName, deliveryAddress, productName, totalSale) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			
			st.setDate(1,new java.sql.Date( obj.getSaleDate().getTime()));
			st.setString(2, obj.getClientName());
			st.setString(3, obj.getDeliveryAddress());
			st.setString(4, obj.getProductName());
			st.setDouble(5, obj.getTotal());
			
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
//

	//	@Override
//	public void update(Product obj)
//	{
//		PreparedStatement st = null;
//		try 
//		{
//			st = conn.prepareStatement(
//					"UPDATE inventory "
//					+ "SET nome = ?, marca = ?, quantidade = ?, cor = ?, codigo = ?, precoFabrica = ?, precoVenda = ?, CategoryProductId = ? "
//					+ "WHERE Id = ?");
//			
//			st.setString(1, obj.getName());
//			st.setString(2, obj.getBrand());
//			st.setInt(3, obj.getQuantity());
//			st.setString(4, obj.getColor());
//			st.setString(5, obj.getCode());
//			st.setDouble(6, obj.getFactoryPrice());
//			st.setDouble(7, obj.getSalePrice());
//			st.setInt(8, obj.getCategory().getId());
//			st.setInt(9, obj.getId());
//
//			
//			st.executeUpdate();
//		}
//		catch (SQLException e) 
//		{
//			throw new DbException(e.getMessage());
//		}
//		finally 
//		{
//			DB.closeStatement(st);
//		}
//	}
//
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
//
//	@Override
//	public Product findById(Integer id)
//	{
//		PreparedStatement st = null;
//		ResultSet rs = null;
//		try 
//		{
//			st = conn.prepareStatement(
//					"SELECT inventory.*,category.name as CatName "
//					+ "FROM inventory INNER JOIN category "
//					+ "ON inventory.CategoryProductId = category.id "
//					+ "WHERE inventory.id = ?");
//			
//			st.setInt(1, id);
//			rs = st.executeQuery();
//			if (rs.next()) 
//			{
//				CategoryProduct cat = instantiateCategoryProduct(rs);
//				Product obj = instantiateProduct(rs, cat);
//				return obj;
//			}
//			return null;
//		}
//		catch (SQLException e) 
//		{
//			throw new DbException(e.getMessage());
//		}
//		finally 
//		{
//			DB.closeStatement(st);
//			DB.closeResultSet(rs);
//		}
//	}
//
//	private Product instantiateProduct(ResultSet rs, CategoryProduct cat) throws SQLException 
//	{
//		Product obj = new Product();
//		obj.setId(rs.getInt("id"));
//		obj.setName(rs.getString("nome"));
//		obj.setBrand(rs.getString("marca"));
//		obj.setQuantity(rs.getInt("quantidade"));
//		obj.setColor(rs.getString("cor"));
//		obj.setCode(rs.getString("codigo"));
//		obj.setFactoryPrice(rs.getDouble("precoFabrica")); 
//		obj.setSalePrice(rs.getDouble("precoVenda")); 
//		obj.setCategory(cat);
//		
//		return obj;
//	}
//
//	private CategoryProduct instantiateCategoryProduct(ResultSet rs) throws SQLException 
//	{
//		CategoryProduct cat = new CategoryProduct();
//		cat.setId(rs.getInt("CategoryProductId"));
//		cat.setName(rs.getString("CatName"));
//		return cat;
//	}


	@Override
	public void update(Sale obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteById(Sale id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Sale findById(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Sale> findAll() 
	{
		PreparedStatement st = null;
		ResultSet rs = null;
		try
		{
			st = conn.prepareStatement(
				"SELECT * FROM sale ORDER BY clientName");
			rs = st.executeQuery();

			List<Sale> list = new ArrayList<>();

			while (rs.next()) 
			{
				Sale obj = new Sale();
				obj.setId(rs.getInt("id"));
				obj.setSaleDate(rs.getDate("saleDate"));
				obj.setClientName(rs.getString("clientName"));
				obj.setDeliveryAddress(rs.getString("deliveryAddress"));
				obj.setProductName(rs.getString("productName"));
				obj.setTotal(rs.getDouble("totalSale"));
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

}
