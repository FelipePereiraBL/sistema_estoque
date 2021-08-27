package model.dao;

import java.util.List;

import model.entities.Sale;

public interface SaleDao 
{
	void insert(Sale obj);
	List<Sale> findAll();
	void deleteById(Integer id);

}
