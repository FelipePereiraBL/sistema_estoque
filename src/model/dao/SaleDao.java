package model.dao;

import java.util.List;

import model.entities.Sale;

public interface SaleDao 
{
	void insert(Sale obj);
	void update(Sale obj);
	void deleteById(Sale id);
	List<Sale> findAll();
	Sale findById(Integer id);

}
