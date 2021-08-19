package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.CategoryProductService;

public class MainViewController implements Initializable
{
	@FXML
	private MenuItem menuIteminventory;
	@FXML
	private MenuItem menuItemNewProduct;
	
	@FXML
	private MenuItem menuItemCategoryProducts;
	@FXML
	private MenuItem menuItemNewCategoryProducts;

	
	//Abrir tela de estoque
	@FXML
	public void onMenuItemInventory()
	{
		loadView("/gui/InventoryList.fxml",x->{});
	}
	//Abre formulario para adicionar produto ao inventario
	@FXML
	public void onMenuNewProduct()
	{
		
	}
	
	//Abrir tela de categoria de produtos
	@FXML
	public void onMenuItemCategoryProducts()
	{
		loadView("/gui/CategoryProductList.fxml",(CategoryProductListController controller)->
		{
			//Injeta a dependencia com serviço de Categoria de produtos
			controller.setService(new CategoryProductService());
			//Atualiza a tabela
			controller.updateTableView();
		});
	}
	//Abre formulario para adicionar categoria de produto
	@FXML
	public void onMenuItemNewCategoryProducts()
	{
		
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) 
	{
		// TODO Auto-generated method stub
		
	}
	
	private synchronized  <T> void loadView(String absoluteName,Consumer<T> initializingAction)
	{
		try
		{
		   FXMLLoader loader=new FXMLLoader(getClass().getResource(absoluteName));
		   VBox newVbox=loader.load();
		   
		   Scene mainScene=Main.getMainScene();
		   
		   VBox mainVbox=(VBox)((ScrollPane)mainScene.getRoot()).getContent();
		   
		   Node mainMenu=mainVbox.getChildren().get(0);
		   
		   mainVbox.getChildren().clear();
		   mainVbox.getChildren().add(mainMenu);
		   mainVbox.getChildren().addAll(newVbox.getChildren());
		   
		   T controller=loader.getController();
		   initializingAction.accept(controller);
		   
		}
		catch (IOException e)
		{
			Alerts.showAlert("IOExeption", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

}
