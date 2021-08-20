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
import model.dao.ProductDao;
import model.entities.CategoryProduct;
import model.entities.Product;

public class ProductDaoJDBC implements ProductDao
{
private Connection conn;
	
	public ProductDaoJDBC(Connection conn) 
	{
		this.conn = conn;
	}
	
	@Override
	public void insert(Product obj) 
	{
		PreparedStatement st = null;
		try 
		{
			st = conn.prepareStatement(
					"INSERT INTO inventory "
					+ "(nome, marca, cor, codigo, precoFabrica, precoVenda) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getBrand());
			st.setString(3, obj.getColor());
			st.setInt(4, obj.getCode());
			st.setDouble(5, obj.getFactoryPrice());
			st.setDouble(6, obj.getSalePrice());
			
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
	public void update(Product obj)
	{
		PreparedStatement st = null;
		try 
		{
			st = conn.prepareStatement(
					"UPDATE inventory "
					+ "SET nome = ?, marca = ?, cor = ?, codigo = ?, precoFabrica = ?, precoVenda = ? "
					+ "WHERE id = ?");
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getBrand());
			st.setString(3, obj.getColor());
			st.setInt(4, obj.getCode());
			st.setDouble(5, obj.getFactoryPrice());
			st.setDouble(6, obj.getSalePrice());
			
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
	public void deleteById(Integer id)
	{
		PreparedStatement st = null;
		try
		{
			st = conn.prepareStatement("DELETE FROM inventory WHERE id = ?");
			
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
	public Product findById(Integer id)
	{
		PreparedStatement st = null;
		ResultSet rs = null;
		try 
		{
			st = conn.prepareStatement(
					"SELECT inventory.*,category.nome as DepName "
					+ "FROM inventory INNER JOIN category "
					+ "ON inventory.CategoryProductId = category.id "
					+ "WHERE inventory.id = ?");
			
			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) 
			{
				CategoryProduct cat = instantiateCategoryProduct(rs);
				Product obj = instantiateProduct(rs, cat);
				return obj;
			}
			return null;
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

	private Product instantiateProduct(ResultSet rs, CategoryProduct cat) throws SQLException 
	{
		Product obj = new Product();
		obj.setId(rs.getInt("id"));
		obj.setName(rs.getString("nome"));
		obj.setBrand(rs.getString("marca"));
		obj.setColor(rs.getString("cor"));
		obj.setFactoryPrice(rs.getDouble("precoFabrica")); 
		obj.setSalePrice(rs.getDouble("precoVenda")); 
		obj.setCategory(cat);
		
		return obj;
	}

	private CategoryProduct instantiateCategoryProduct(ResultSet rs) throws SQLException 
	{
		CategoryProduct cat = new CategoryProduct();
		cat.setId(rs.getInt("CategoryProductId"));
		cat.setName(rs.getString("CatName"));
		return cat;
	}

	@Override
	public List<Product> findAll()
	{
		PreparedStatement st = null;
		ResultSet rs = null;
		try 
		{
			st = conn.prepareStatement(
					"SELECT inventory.*,category.name as CatName "
					+ "FROM inventory INNER JOIN category "
					+ "ON inventory.CategoryProductId = category.id "
					+ "ORDER BY name");
			
			rs = st.executeQuery();
			
			List<Product> list = new ArrayList<>();
			Map<Integer, CategoryProduct> map = new HashMap<>();
			
			while (rs.next()) 
			{
				
				CategoryProduct cat = map.get(rs.getInt("CategoryProductId"));
				
				if (cat == null) 
				{
					cat = instantiateCategoryProduct(rs);
					map.put(rs.getInt("CategoryProductId"), cat);
				}
				
				Product obj = instantiateProduct(rs, cat);
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

	@Override
	public List<Product> findByCategory(CategoryProduct category) 
	{
		PreparedStatement st = null;
		ResultSet rs = null;
		try
		{
			st = conn.prepareStatement(
					"SELECT inventory.*,category.nome as CatName "
					+ "FROM inventory INNER JOIN category "
					+ "ON inventory.CategoryProductId = category.id "
					+ "WHERE CategoryProductId = ? "
					+ "ORDER BY nome");
			
			st.setInt(1, category.getId());
			
			rs = st.executeQuery();
			
			List<Product> list = new ArrayList<>();
			Map<Integer, CategoryProduct> map = new HashMap<>();
			
			while (rs.next())
			{
				
				CategoryProduct cat = map.get(rs.getInt("CategoryProductId"));
				
				if (cat == null) 
				{
					cat = instantiateCategoryProduct(rs);
					map.put(rs.getInt("CategoryProductId"), cat);
				}
				
				Product obj = instantiateProduct(rs, cat);
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
