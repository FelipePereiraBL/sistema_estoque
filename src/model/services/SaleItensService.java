package model.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.dao.DaoFactory;
import model.dao.SaleDao;
import model.entities.Sale;

public class SaleItensService 
{
	private SaleDao dao=DaoFactory.createSaleDao();
	
	public List<Sale> findAll()
	{
		List<Sale>list=new ArrayList<>();
		
		list.add(new Sale(1, new Date(), "Felipe", "Rua tal",124.00));
		
		return list;
		//return dao.findAll();
	}
	
	public void saveOrUpdate(Sale obj)
	{
		if(obj.getId()==null)
		{
			dao.insert(obj);
		}
		else
		{
			dao.update(obj);
		}
	}
	
	public void remove(Sale obj)
	{
		//dao.deleteById(obj.getId());
	}
	
}
