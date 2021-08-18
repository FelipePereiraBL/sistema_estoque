package gui;

import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.CategoryProduct;

public class CategoryProductListController implements Initializable
{
	@FXML
	private TableView<CategoryProduct> tableViewCategoriesProducts;
	@FXML
	private TableColumn<CategoryProduct, Integer > tableColumId;
	@FXML
	private TableColumn<CategoryProduct, String > tableColumName;
	
	@FXML
	private Button btNewCategoryProduct;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) 
	{
		initializeNodes();
		
		//Ajusta o tableView em relação a janeta
         Stage stage=(Stage)Main.getMainScene().getWindow();	
         tableViewCategoriesProducts.prefHeightProperty().bind(stage.heightProperty());
		
	}
	
	public void onBtNewCategoryProduct()
	{
		System.out.println("action");
	}
	
	//Inicializa  as colunas da tableView
	private void initializeNodes() 
	{
		tableColumId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumName.setCellValueFactory(new PropertyValueFactory<>("name"));
	}

}
