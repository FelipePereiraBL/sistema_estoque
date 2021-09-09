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
import db.DbIntegrityException;
import model.dao.ItenSaleDao;
import model.entities.ItenSale;
import model.entities.Product;
import model.entities.Sale;
import model.services.ProductService;
import model.services.SaleService;

public class ItenSaleDaoJDBC implements ItenSaleDao
{
private Connection conn;
	
	public ItenSaleDaoJDBC(Connection conn) 
	{
		this.conn = conn;
	}
	
	@Override
	public List<ItenSale> findAll()
	{
		PreparedStatement st = null;
		ResultSet rs = null;
		try
		{
			st = conn.prepareStatement(
				"SELECT * FROM itenSale ORDER BY id");
			rs = st.executeQuery();

			List<ItenSale> list = new ArrayList<>();
			Map<Integer,Product> map = new HashMap<>();
			Map<Integer,Sale> mapSale= new HashMap<>();
			while (rs.next()) 
			{
				Product prod=map.get(rs.getInt("productId"));
				Sale sale=mapSale.get(rs.getInt("saleId"));
				if(sale==null)
				{
					sale=instantiateSale(rs);
					mapSale.put(rs.getInt("saleId"), sale);
				}
				if (prod == null) 
				{
					prod = instantiateProduct(rs);
					map.put(rs.getInt("productId"), prod);
				}
				
				ItenSale obj = new ItenSale();
				obj.setId(rs.getInt("id"));
				obj.setPrice(rs.getDouble("price"));
				obj.setQuantity(rs.getInt("quantity"));
				obj.subTotal(rs.getDouble("price"), rs.getInt("quantity"));
				obj.setProduct(prod);
				obj.setSale(sale);
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
	public void insert(ItenSale obj)
	{
		PreparedStatement st = null;
		try 
		{
			st = conn.prepareStatement(
				"INSERT INTO itenSale " +
				"(price, quantity, subTotal, productId, saleId) " +
				"VALUES " +
				"(?, ?, ?, ?, ?)", 
				Statement.RETURN_GENERATED_KEYS);

			st.setDouble(1, obj.getPrice());
			st.setInt(2, obj.getQuantity());
			st.setDouble(3, obj.getSubTotal());
			st.setInt(4, obj.getProduct().getId());
			st.setInt(5, obj.getSale().getId());

			int rowsAffected = st.executeUpdate();
			
			if (rowsAffected > 0)
{
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next())
				{
					int id = rs.getInt(1);
					obj.setId(id);
				}
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
			st = conn.prepareStatement(
				"DELETE FROM itenSale WHERE Id = ?");

			st.setInt(1, id);

			st.executeUpdate();
		}
		catch (SQLException e) 
		{
			throw new DbIntegrityException(e.getMessage());
		} 
		finally 
		{
			DB.closeStatement(st);
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
	
	private Sale instantiateSale(ResultSet rs) throws SQLException
	{
		List<Sale>list=new SaleService().findAll();
		Sale obj=new Sale();
		for (Sale sale : list) 
		{
			if(sale.getId()==rs.getInt("saleId"))
			{
				obj=sale;
			}
			
		}
		return obj;
	}

}
