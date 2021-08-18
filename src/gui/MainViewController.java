package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;

public class MainViewController implements Initializable
{
	@FXML
	private MenuItem menuIteminventory;
	@FXML
	private MenuItem menuItemCategoryProducts;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) 
	{
		// TODO Auto-generated method stub
		
	}
	
	//Abrir tela de estoque
	@FXML
	public void onMenuItemInventory() throws IOException
	{
		
	}
	
	//Abrir tela de categoria de produtos
	@FXML
	public void onMenuItemCategoryProducts()
	{
		
	}

}
