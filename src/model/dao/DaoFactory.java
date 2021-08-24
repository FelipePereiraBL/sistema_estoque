package model.dao;

import db.DB;
import model.dao.impl.CategoryProductDaoJDBC;
import model.dao.impl.ProductDaoJDBC;
import model.dao.impl.SaleDaoJDBC;

public class DaoFactory {

	public static ProductDao createProductDao()
	{
		return new ProductDaoJDBC(DB.getConnection());
	}
	
	public static CategoryProductDao createCategoryProductDao() 
	{
		return new CategoryProductDaoJDBC(DB.getConnection());
	}
	
	public static SaleDao createSaleDao() 
	{
		return new SaleDaoJDBC(DB.getConnection());
	}
}
