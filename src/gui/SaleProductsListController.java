package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Product;
import model.services.ProductService;

public class SaleProductsListController implements Initializable
{
	//Dependencia 
	private ProductService services;
	
	@FXML
	private TableView<Product> tableViewProducts;
	@FXML
	private TableColumn<Product, Integer > tableColumId;
	@FXML
	private TableColumn<Product, String > tableColumName;
	@FXML
	private TableColumn<Product, Integer > tableColumCatewgory;
	@FXML
	private TableColumn<Product, String > tableColumBrand;
	@FXML
	private TableColumn<Product, String > tableColumColor;
	@FXML
	private TableColumn<Product, String > tableColumNCode;

	@FXML
	private TableColumn<Product, Double > tableColumSalePrice;
	
	@FXML
	private TableColumn<Product, Product > tableColumnADDPRODUCT;
	
	
	private ObservableList<Product> obsList;
	

	//Injeção de dependencia 
	public void setServices(ProductService services)
	{
		this.services = services;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) 
	{
		initializeNodes();
		
		setServices(new ProductService());
		
		//Ajusta o tableView em relação a janeta
         Stage stage=(Stage)Main.getMainScene().getWindow();	
         tableViewProducts.prefHeightProperty().bind(stage.heightProperty());
		
         updateTableView();
	}
	
	//Inicializa  as colunas da tableView
	private void initializeNodes() 
	{
		tableColumId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumCatewgory.setCellValueFactory(new PropertyValueFactory<>("category"));
		tableColumBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
		tableColumColor.setCellValueFactory(new PropertyValueFactory<>("color"));
		tableColumNCode.setCellValueFactory(new PropertyValueFactory<>("code"));
		tableColumSalePrice.setCellValueFactory(new PropertyValueFactory<>("salePrice"));
	
	}
	
	public void updateTableView()
	{
		//Verifica se a dependencia foi injetada
		if(services==null)
		{
			throw new IllegalStateException("Services was null");
		}
		
		 List<Product> list=services.findAll();
		 obsList=FXCollections.observableArrayList(list);
		 tableViewProducts.setItems(obsList);
		 
	}
	
	
}