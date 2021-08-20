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
	//Dependencia 
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

	//Injeção de dependencia 
	public void setService(CategoryProductService services) 
	{
		this.services = services;
	}
	
	public void onBtNewCategoryProduct(ActionEvent event)
	{
		Stage parentStage=Utils.currentStage(event);
		//Cria um CategoryProduct vazio para ser passado para o 
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
	
	//Inicializa  as colunas da tableView
	private void initializeNodes() 
	{
		tableColumId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumName.setCellValueFactory(new PropertyValueFactory<>("name"));
	}
	
	public void updateTableView()
	{
		//Verifica se a dependencia foi injetada
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
			
			//Pega o controller de CategoryProductForm
			CategoryProductFormController controller=loader.getController();
			//Manda o obj(CategoryProductForm vazio para o formulario)
			controller.setCategoryProduct(obj);
			//Injeção de dependencia
			controller.setService(new CategoryProductService());			
			//Inscreve essa classe para receber o evento
			controller.subscribleChangeListener(this);
			//Carrega os dados do entity  nos testFields do formulario
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

	//Isere um botão para remover os botões associados a categoria de produtos removida
	private void initRemoveButtons() 
	{ 
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue())); 
		tableColumnREMOVE.setCellFactory(param -> new TableCell<CategoryProduct, CategoryProduct>() 
		{ 
		 private final Button button = new Button("Remove"); 
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

	//Remove a categoria de produtos
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