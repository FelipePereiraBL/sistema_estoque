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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Product;
import model.services.CategoryProductService;
import model.services.ProductService;

public class InventoryListController implements Initializable,DataChangeListeners
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
	private TableColumn<Product, Integer > tableColumQiantity;
	@FXML
	private TableColumn<Product, String > tableColumBrand;
	@FXML
	private TableColumn<Product, String > tableColumColor;
	@FXML
	private TableColumn<Product, String > tableColumNCode;
	@FXML
	private TableColumn<Product, Double > tableColumFactoryPrice;
	@FXML
	private TableColumn<Product, Double > tableColumSalePrice;
	
	
	@FXML
	private TableColumn<Product, Product > tableColumnEDIT;
	@FXML
	private TableColumn<Product, Product > tableColumnREMOVE;
	
	
	@FXML
	private Button btNewProduct;
	@FXML
	private Button btNewSearch;
	
	private ObservableList<Product> obsList;
	
	@FXML
	private TextField txtSearch;

	//Injeção de dependencia 
	public void setServices(ProductService services)
	{
		this.services = services;
	}
	
	public void onBtNewProduct(ActionEvent event)
	{
		Stage parentStage=Utils.currentStage(event);
		//Cria um Product vazio para ser passado para o formulario
		Product obj=new Product();
		createDialogForm(obj,"/gui/InventoryForm.fxml",parentStage);

	}

	@Override
	public void initialize(URL url, ResourceBundle rb) 
	{
		initializeNodes();
		
		//Ajusta o tableView em relação a janeta
         Stage stage=(Stage)Main.getMainScene().getWindow();	
         tableViewProducts.prefHeightProperty().bind(stage.heightProperty());
		
	}
	
	//Inicializa  as colunas da tableView
	private void initializeNodes() 
	{
		tableColumId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumCatewgory.setCellValueFactory(new PropertyValueFactory<>("category"));
		tableColumBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
		tableColumQiantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		tableColumColor.setCellValueFactory(new PropertyValueFactory<>("color"));
		tableColumNCode.setCellValueFactory(new PropertyValueFactory<>("code"));
		tableColumFactoryPrice.setCellValueFactory(new PropertyValueFactory<>("factoryPrice"));
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
		 
		 initEditButtons();
		 initRemoveButtons();
	}
	
	private void createDialogForm(Product obj,String absoluteName, Stage parentStage)
    {
		try 
		{
            FXMLLoader loader=new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane=loader.load();
			
			//Pega o controller de InventoryForm
			InventoryFormController controller=loader.getController();
			//Manda o obj(ProductForm vazio para o formulario)
			controller.setProduct(obj);
			//Injeção de dependencia
			controller.setService(new ProductService(),new CategoryProductService());	
			controller.loadAssociateObjects();
			//Inscreve essa classe para receber o evento
			controller.subscribleChangeListener(this);
			//Carrega os dados do entity  nos testFields do formulario
			controller.updateFormData();
			
			Stage dialogStage=new Stage();
			dialogStage.setTitle("Insira ou editar produto");
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

	//Executa o evento
	@Override
	public void onChanged() 
	{
		updateTableView();
		
	}
	
	//Inserir um botão de editar em cada linha da tableView
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

	//Isere um botão para remover os botões associados a categoria de produtos removida
	private void initRemoveButtons() 
	{ 
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue())); 
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Product, Product>() 
		{ 
		 private final Button button = new Button("Remove"); 
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

	//Remove a categoria de produtos
	private void removeEntity(Product obj) 
	{
		Optional<ButtonType>result= Alerts.showConfirmation("Confirmação", "Tem certeza que deseja remover esse produtos");
		
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
				Alerts.showAlert("Erro ao remover categoria de produtos", null, e.getMessage(), AlertType.ERROR);
			}
			
		}

	}
	
}