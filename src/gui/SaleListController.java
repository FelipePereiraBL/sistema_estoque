package gui;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Utils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Sale;

public class SaleListController  implements Initializable
{
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

	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		initializeNodes();
		
	}
	
	private void initializeNodes() 
	{
		tableColumId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumSaleDate.setCellValueFactory(new PropertyValueFactory<>("saleDate"));
		Utils.formatTableColumnDate(tableColumSaleDate, "dd/MM/yyyy");
		
		tableColumClientName.setCellValueFactory(new PropertyValueFactory<>("clientName"));
		tableColumDeliveryAddres.setCellValueFactory(new PropertyValueFactory<>("deliveryAddres"));
		
		tableColumSalePrice.setCellValueFactory(new PropertyValueFactory<>("salePrice"));
		Utils.formatTableColumnDouble(tableColumSalePrice, 2);

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewSale.prefHeightProperty().bind(stage.heightProperty());
		
	}
	
	

}
