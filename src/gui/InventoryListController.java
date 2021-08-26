package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListeners;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Product;
import model.services.CategoryProductService;
import model.services.ProductService;

public class InventoryListController implements Initializable,DataChangeListeners
{
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
	private TableColumn<Product, Integer > tableColumQiantity;
	@FXML
	private TableColumn<Product, String > tableColumBrand;
	@FXML
	private TableColumn<Product, String > tableColumColor;
	@FXML
	private TableColumn<Product, String > tableColumReference;
	@FXML
	private TableColumn<Product, Double > tableColumSpotCostPrice;
	@FXML
	private TableColumn<Product, Double > tableColumForwardCostPrice;
	@FXML
	private TableColumn<Product, Double > tableColumCashSalePrice;
	@FXML
	private TableColumn<Product, Double > tableColumForwardSellingPrice;
	
	@FXML
	private TableColumn<Product, Product > tableColumnEDIT;
	@FXML
	private TableColumn<Product, Product > tableColumnREMOVE;
	
	
	@FXML
	private Button btNewProduct;
	
	private ObservableList<Product> obsList;

	public void setServices(ProductService services)
	{
		this.services = services;
	}
	
	public void onBtNewProduct(ActionEvent event)
	{
		Stage parentStage=Utils.currentStage(event);
		Product obj=new Product();
		createDialogForm(obj,"/gui/InventoryForm.fxml",parentStage);
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) 
	{
		initializeNodes();
		
         Stage stage=(Stage)Main.getMainScene().getWindow();	
         tableViewProducts.prefHeightProperty().bind(stage.heightProperty());
		
	}
	
	private void initializeNodes() 
	{
		tableColumId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumCatewgory.setCellValueFactory(new PropertyValueFactory<>("category"));
		tableColumBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
		tableColumQiantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		tableColumColor.setCellValueFactory(new PropertyValueFactory<>("color"));
		tableColumReference.setCellValueFactory(new PropertyValueFactory<>("reference"));
		tableColumSpotCostPrice.setCellValueFactory(new PropertyValueFactory<>("spotCostPrice"));
		tableColumForwardCostPrice.setCellValueFactory(new PropertyValueFactory<>("forwardCostPrice"));
		tableColumCashSalePrice.setCellValueFactory(new PropertyValueFactory<>("cashSalePrice"));
		tableColumForwardSellingPrice.setCellValueFactory(new PropertyValueFactory<>("forwardSellingPrice"));
	}
	
	public void updateTableView()
	{
		if(services==null)
		{
			throw new IllegalStateException("Services was null");
		}
		
		 List<Product> list=services.findAll();
		 obsList=FXCollections.observableArrayList(list);
		 tableViewProducts.setItems(obsList);
		 
		 initEditButtons();
		 initRemoveButtons();
	}
	
	private void createDialogForm(Product obj,String absoluteName, Stage parentStage)
    {
		try 
		{
            FXMLLoader loader=new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane=loader.load();
			
			InventoryFormController controller=loader.getController();
			
			controller.setProduct(obj);
			controller.setService(new ProductService(),new CategoryProductService());	
			controller.loadAssociateObjects();
			controller.subscribleChangeListener(this);
			controller.updateFormData();
			
			Stage dialogStage=new Stage();
			
			dialogStage.setTitle("Inserir ou editar produto");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} 
		catch (IOException e) 
		{
	     e.getStackTrace();
		 Alerts.showAlert("IOException", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
		
    }

	@Override
	public void onChanged() 
	{
		updateTableView();
		
	}
	
	private void initEditButtons()
	{ 
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue())); 
		tableColumnEDIT.setCellFactory(param -> new TableCell<Product, Product>()
		
		{ 
		 private final Button button = new Button("Editar"); 
		 
		 @Override
		 protected void updateItem(Product obj, boolean empty) 
		 { 
		   super.updateItem(obj, empty); 
		 
		   if (obj == null) 
		   { 
		     setGraphic(null); 
		     return; 
		   } 
		   setGraphic(button); 
		 
		   button.setOnAction( 
		   event -> createDialogForm( obj, "/gui/InventoryForm.fxml",Utils.currentStage(event))); 
		  } 
		  }); 
		} 

	private void initRemoveButtons() 
	{ 
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue())); 
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Product, Product>() 
		{ 
		 private final Button button = new Button("Remover"); 
		 @Override
		 protected void updateItem(Product obj, boolean empty) 
		 { 
		 super.updateItem(obj, empty); 
		 if (obj == null) 
		 { 
		 setGraphic(null); 
		 return; 
		 } 
		 setGraphic(button); 
		 button.setOnAction(event -> removeEntity(obj)); 
		 } 
		 }); 
		}

	private void removeEntity(Product obj) 
	{
		Optional<ButtonType>result= Alerts.showConfirmation("Confirmação", "Tem certeza que deseja remover esse produto ?");
		
		if(result.get()==ButtonType.OK)
		{
			if(services==null)
			{
				throw new IllegalStateException("Service was null");
			}
			
			try
			{
				services.remove(obj);
				updateTableView();
				
			} 
			catch (DbIntegrityException e)
			{
				Alerts.showAlert("Erro ao remover produto", null, e.getMessage(), AlertType.ERROR);
			}
			
		}

	}
	
}