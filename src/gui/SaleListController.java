package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
import model.entities.ItenSale;
import model.entities.Product;
import model.entities.Sale;
import model.services.ItenSaleService;
import model.services.ProductService;
import model.services.SaleService;

public class SaleListController  implements Initializable,DataChangeListeners
{
	private SaleService services;
	private ItenSaleService itenSaleService;
	
	//TableView da lista de vendas
	@FXML
	private TableView<Sale> tableViewSale;
	@FXML
	private TableColumn<Sale, Integer > tableColumId;
	@FXML
	private TableColumn<Sale, Date > tableColumSaleDate;
	@FXML
	private TableColumn<Sale, String > tableColumClientName;
	@FXML
	private TableColumn<Sale, String > tableColumCustomerPhone;
	@FXML
	private TableColumn<Sale, String > tableColumDeliveryAddres;
	@FXML
	private TableColumn<Sale,Double > tableColumSalePrice;
	@FXML
	private TableColumn<Sale, String > tableColumTypeOfSale;
	@FXML
	private TableColumn<Sale, Sale> tableColumnREMOVE;
	@FXML
	private TableColumn<Sale, Sale > tableColumnLISTITENS;
	
	//TableView da lista de itens de caad venda
	@FXML
	private TableView<ItenSale> tableViewItenSale;
	@FXML
	private TableColumn<ItenSale, Integer > tableColumIdItenSale;
	@FXML
	private TableColumn<ItenSale, Double > tableColumPrice;
	@FXML
	private TableColumn<ItenSale, Integer > tableColumQuantity;
	@FXML
	private TableColumn<ItenSale, Product > tableColumProduct;
	@FXML
	private TableColumn<ItenSale, Double > tableColumSubtotal;
	
	private ObservableList<ItenSale> obsListItenSale;	
	private ObservableList<Sale> obsList;
	
	@FXML
	private Button BtNewSale;
	
	//Depedencias
	public void setSaleService(SaleService services)
	{
		this.services=services;
	}
	public void setItenSaleService(ItenSaleService itenSaleService) 
	{
		this.itenSaleService = itenSaleService;
	}	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		initializeNodes();
		setItenSaleService(new ItenSaleService());

		Stage stage = (Stage) Main.getMainScene().getWindow();
		
		tableViewSale.prefHeightProperty().bind(stage.heightProperty());
		tableViewItenSale.prefHeightProperty().bind(stage.heightProperty());
	}
	
	//Nova venda
	public void onBtNewSale(ActionEvent event)
	{
		Stage parentStage=Utils.currentStage(event);
		
		Sale sale=new Sale();
		
		createDialogForm(sale, "/gui/SaleForm.fxml", parentStage);
	}
	
	//Abrir formulario para criar nova venda
	private void createDialogForm(Sale obj,String absoluteName, Stage parentStage)
    {
		try 
		{
            FXMLLoader loader=new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane=loader.load();
			

			SaleFormController controller=loader.getController();

			controller.setSale(obj);

			controller.setService(new ProductService(),new SaleService());	

			controller.subscribleChangeListener(this);
			
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
	
	//Seleciona uma venda da lista para exibir os itens associados a ela
	public void SelectSale(Sale saleId)
	{
		if(services==null)
		{
			throw new IllegalStateException("Services was null");
		}
		
		 List<ItenSale> list=itenSaleService.findAll();
		 List<ItenSale> resultList=new ArrayList<>();
		 
		 for (ItenSale itenSale : list)
			{
			 if(itenSale.getSale().getId()==saleId.getId())
			 {
				 resultList.add(itenSale);
			 }

			}
		 obsListItenSale=FXCollections.observableArrayList(resultList);
		 tableViewItenSale.setItems(obsListItenSale);

	}
	
	private void initializeNodes() 
	{
		tableColumId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumSaleDate.setCellValueFactory(new PropertyValueFactory<>("saleDate"));
		Utils.formatTableColumnDate(tableColumSaleDate, "dd/MM/yyyy");
		
		tableColumClientName.setCellValueFactory(new PropertyValueFactory<>("clientName"));
		tableColumCustomerPhone.setCellValueFactory(new PropertyValueFactory<>("customerPhone"));
		tableColumDeliveryAddres.setCellValueFactory(new PropertyValueFactory<>("deliveryAddress"));	
		tableColumSalePrice.setCellValueFactory(new PropertyValueFactory<>("saleValue"));
		tableColumTypeOfSale.setCellValueFactory(new PropertyValueFactory<>("typeOfSale"));

		tableColumIdItenSale.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
		tableColumQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		tableColumProduct.setCellValueFactory(new PropertyValueFactory<>("product"));
		tableColumSubtotal.setCellValueFactory(new PropertyValueFactory<>("subTotal"));
		
	}
	
	//Atualiza a tableview de lista de vendas
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
		initLISTITENSButton();
	}

	//Inrere um botao de remover na frente de cada objeto da lista de vendas
	private void initRemoveButtons() 
	{ 
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue())); 
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Sale, Sale>() 
		{ 
		 private final Button button = new Button("Remover"); 
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

	//Remove obj venda da lista e do banco de dados
	private void removeEntity(Sale obj) 
	{
		Optional<ButtonType>result= Alerts.showConfirmation("Confirmação", "Tem certeza que deseja apagar o registro dessa venda ?");
		
		if(result.get()==ButtonType.OK)
		{
			if(services==null)
			{
				throw new IllegalStateException("Service was null");
			}
			
			try
			{
				RemoveItensOfSale(obj);
				services.remove(obj);					
				updateTableView();			
			} 
			catch (DbIntegrityException e)
			{
				Alerts.showAlert("Error remove object", null, e.getMessage(), AlertType.ERROR);
			}
			
		}
	} 
	
	//Remove os itens associado a determinada venda e atualiza a tableView de itens de venda
	private void RemoveItensOfSale(Sale sale)
	{
		List<ItenSale>list=itenSaleService.findAll();
		
		for (ItenSale itenSale : list) 
		{
			if(itenSale.getSale().getId()==sale.getId())
			{
				itenSaleService.remove(itenSale);
				SelectSale(sale);
			}	
		}		
		
	}
	
	//Insere um botao de selecionar em cada objeto venda da lista
	private void initLISTITENSButton()
	{ 
		tableColumnLISTITENS.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue())); 
		tableColumnLISTITENS.setCellFactory(param -> new TableCell<Sale, Sale>()
		
		{ 
		 private final Button button = new Button("Selecionar"); 
		 
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
		   button.setOnAction(event -> SelectSale(obj));

		  } 
		  }); 
		}
	
	//Implementação de DataChangeListeners
	@Override
	public void onChanged() 
	{
		updateTableView();
		
	} 
}
