package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
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
import model.entities.Sale;
import model.services.ProductService;
import model.services.SaleService;

public class SaleListController  implements Initializable,DataChangeListeners
{
	private SaleService services;
	
	@FXML
	private TableView<Sale> tableViewSale;
	@FXML
	private TableColumn<Sale, Integer > tableColumId;
	@FXML
	private TableColumn<Sale, Date > tableColumSaleDate;
	@FXML
	private TableColumn<Sale, String > tableColumClientName;
	@FXML
	private TableColumn<Sale, String > tableColumDeliveryAddres;
	@FXML
	private TableColumn<Sale,Double > tableColumSalePrice;

	@FXML
	private TableColumn<Sale, Sale> tableColumnREMOVE;
	@FXML
	private TableColumn<Sale, String> tableColumnProductSale;
	
	@FXML
	private Button BtNewSale;
	
	private ObservableList<Sale> obsList;
	
	public void setSaleService(SaleService services)
	{
		this.services=services;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		initializeNodes();
		
		 Stage stage=(Stage)Main.getMainScene().getWindow();	
		 tableViewSale.prefHeightProperty().bind(stage.heightProperty());
		
	}
	
	public void onBtNewSale(ActionEvent event)
	{
		Stage parentStage=Utils.currentStage(event);
		
		Sale sale=new Sale();
		
		createDialogForm(sale, "/gui/SaleForm.fxml", parentStage);
	}
	
	private void createDialogForm(Sale obj,String absoluteName, Stage parentStage)
    {
		try 
		{
            FXMLLoader loader=new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane=loader.load();
			

			SaleFormController controller=loader.getController();

			controller.setSale(obj);

			controller.setService(new ProductService(),new SaleService());	

			
			Stage dialogStage=new Stage();
			dialogStage.setTitle("Nova venda");
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
	
	private void initializeNodes() 
	{
		tableColumId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumSaleDate.setCellValueFactory(new PropertyValueFactory<>("saleDate"));
		Utils.formatTableColumnDate(tableColumSaleDate, "dd/MM/yyyy");
		
		tableColumClientName.setCellValueFactory(new PropertyValueFactory<>("clientName"));
		tableColumDeliveryAddres.setCellValueFactory(new PropertyValueFactory<>("deliveryAddress"));
		
		tableColumnProductSale.setCellValueFactory(new PropertyValueFactory<>("productName"));
		
		tableColumSalePrice.setCellValueFactory(new PropertyValueFactory<>("total"));

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewSale.prefHeightProperty().bind(stage.heightProperty());
		
	}
	
	public void updateTableView()
	{
		if(services==null)
		{
			throw new IllegalStateException("Services was null");
		}
		
		List<Sale>list=services.findAll();
		obsList=FXCollections.observableArrayList(list);
		
		tableViewSale.setItems(obsList);
		initRemoveButtons();

	}

	private void initRemoveButtons() 
	{ 
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue())); 
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Sale, Sale>() 
		{ 
		 private final Button button = new Button("Remove"); 
		 @Override
		 protected void updateItem(Sale obj, boolean empty) 
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

	private void removeEntity(Sale obj) 
	{
		Optional<ButtonType>result= Alerts.showConfirmation("Confirmation", "Are you sure to delete");
		
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
				Alerts.showAlert("Error remove object", null, e.getMessage(), AlertType.ERROR);
			}
			
		}
	} 

	@Override
	public void onChanged() 
	{
		updateTableView();
		
	} 

	

}
