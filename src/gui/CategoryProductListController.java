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
import model.entities.CategoryProduct;
import model.services.CategoryProductService;

public class CategoryProductListController implements Initializable,DataChangeListeners
{
	private CategoryProductService services;
	
	@FXML
	private TableView<CategoryProduct> tableViewCategoriesProducts;
	@FXML
	private TableColumn<CategoryProduct, Integer > tableColumId;
	@FXML
	private TableColumn<CategoryProduct, String > tableColumName;
	
	@FXML
	private TableColumn<CategoryProduct, CategoryProduct > tableColumnEDIT;
	@FXML
	private TableColumn<CategoryProduct, CategoryProduct > tableColumnREMOVE;
	
	
	@FXML
	private Button btNewCategoryProduct;
	
	private ObservableList<CategoryProduct> obsList;

	public void setService(CategoryProductService services) 
	{
		this.services = services;
	}
	
	public void onBtNewCategoryProduct(ActionEvent event)
	{
		Stage parentStage=Utils.currentStage(event);
		CategoryProduct obj=new CategoryProduct();
		createDialogForm(obj,"/gui/CategoryProductForm.fxml",parentStage);
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) 
	{
		initializeNodes();
		
		//Ajusta o tableView em relação a janeta
         Stage stage=(Stage)Main.getMainScene().getWindow();	
         tableViewCategoriesProducts.prefHeightProperty().bind(stage.heightProperty());
		
	}
	
	private void initializeNodes() 
	{
		tableColumId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumName.setCellValueFactory(new PropertyValueFactory<>("name"));
	}
	
	public void updateTableView()
	{
		if(services==null)
		{
			throw new IllegalStateException("Services was null");
		}
		
		 List<CategoryProduct> list=services.findAll();
		 obsList=FXCollections.observableArrayList(list);
		 tableViewCategoriesProducts.setItems(obsList);
		 
		 initEditButtons();
		 initRemoveButtons();
	}
	
	private void createDialogForm(CategoryProduct obj,String absoluteName, Stage parentStage)
	{
		try 
		{
			FXMLLoader loader=new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane=loader.load();
			
			CategoryProductFormController controller=loader.getController();

			controller.setCategoryProduct(obj);
			controller.setService(new CategoryProductService());			
			controller.subscribleChangeListener(this);
			controller.updateFormData();
			
			Stage dialogStage=new Stage();
			
			dialogStage.setTitle("Insira nova categoria de produto");
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
		tableColumnEDIT.setCellFactory(param -> new TableCell<CategoryProduct, CategoryProduct>()
		
		{ 
		 private final Button button = new Button("Editar"); 
		 
		 @Override
		 protected void updateItem(CategoryProduct obj, boolean empty) 
		 { 
		   super.updateItem(obj, empty); 
		 
		   if (obj == null) 
		   { 
		     setGraphic(null); 
		     return; 
		   } 
		   setGraphic(button); 
		 
		   button.setOnAction( 
		   event -> createDialogForm( obj, "/gui/CategoryProductForm.fxml",Utils.currentStage(event))); 
		  } 
		  }); 
		} 

	private void initRemoveButtons() 
	{ 
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue())); 
		tableColumnREMOVE.setCellFactory(param -> new TableCell<CategoryProduct, CategoryProduct>() 
		{ 
		 private final Button button = new Button("Remover"); 
		 @Override
		 protected void updateItem(CategoryProduct obj, boolean empty) 
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

	private void removeEntity(CategoryProduct obj) 
	{
		Optional<ButtonType>result= Alerts.showConfirmation("Confirmação", "Tem certeza que deseja remover essa categoria de produtos");
		
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