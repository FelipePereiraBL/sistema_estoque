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
					+ "(saleDate, clientName, customerPhone, deliveryAddress, typeOfSale, totalSale) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			
			st.setDate(1,new java.sql.Date( obj.getSaleDate().getTime()));
			st.setString(2, obj.getClientName());
			st.setString(3, obj.getCustomerPhone());
			st.setString(4, obj.getDeliveryAddress());
			st.setString(5, obj.getTypeOfSale());
			st.setDouble(6, obj.getSaleValue());
			
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

			while (rs.next()) 
			{				
				Sale obj = new Sale();
				obj.setId(rs.getInt("id"));
				obj.setSaleDate(rs.getDate("saleDate"));
				obj.setClientName(rs.getString("clientName"));
				obj.setCustomerPhone(rs.getString("customerPhone"));
				obj.setDeliveryAddress(rs.getString("deliveryAddress"));
				obj.setTypeOfSale(rs.getString("typeOfSale"));
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
}