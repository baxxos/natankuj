package controllers;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.naming.NamingException;
import org.apache.log4j.Logger;
import dao.CityDaoAPI;
import dao.FuelTypeDaoAPI;
import dao.OfferDaoAPI;
import dao.PriceDaoAPI;
import dao.RatingDaoAPI;
import dao.StationBrandsDaoAPI;
import dao.StationDaoAPI;
import entities.Offer;
import entities.Price;
import entities.Station;
import entities.User;
import gui.ContextMenuFactory;
import gui.CustomAlert;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import main.Main;
import misc.MonthlyAverage;
import misc.PdfCreator;
import misc.PropLoader;
import parsing.DatabaseControl;

public class MainWindowController implements Initializable {
	// DAO references required for database access
	private RatingDaoAPI ratingDao;
	private StationDaoAPI stationDao;
	private StationBrandsDaoAPI stationBrandsDao;
	private PriceDaoAPI priceDao;
	private FuelTypeDaoAPI fuelTypeDao;
	private OfferDaoAPI offerDao;
	private CityDaoAPI cityDao;
	// Language resource bundle
	private ResourceBundle rb;
	// Controller private attributes
	private User activeUser;
	private ObservableList<String> brands;
	private ObservableList<String> cities;
	private ObservableList<Offer> offers;
	private List<MonthlyAverage> yearlyGasolineData;
	private List<MonthlyAverage> yearlyDieselData;
	private final ObservableList<String> actions = FXCollections.observableArrayList();
	private final ObservableList<String> actionTargets = FXCollections.observableArrayList();
	// Table related components
	@FXML
	private TableView<Offer> tableView;
	@FXML
	private TabPane tabPaneMain;
	@FXML
	protected TableColumn<Station, String> colFuelBrand;
	@FXML
	protected TableColumn<Station, String> colBrand;
	@FXML
	protected TableColumn<Station, String> colCity;
	@FXML
	protected TableColumn<Station, String> colLocation;
	@FXML
	protected TableColumn<Station, Double> colRating;
	@FXML
	protected TableColumn<Station, String> colFuelName;
	@FXML
	protected TableColumn<Station, Double> colPrice;
	@FXML
	protected TableColumn<Station, String> colDate;
	// Button GUI components
	@FXML
	protected Button fireUserActionButton;
	@FXML
	protected Button filterButton;
	@FXML
	protected Button updateButton;
	@FXML
	protected Button dropCreateButton;
	@FXML
	protected Button cancelFilterButton;
	@FXML
	protected Button buttonLangEn;
	@FXML
	protected Button buttonLang;
	// Combo Boxes
	@FXML
	private ComboBox<String> brandsComboBox;
	@FXML
	private ComboBox<String> cityComboBox;
	@FXML
	private ComboBox<String> actionComboBox;
	@FXML
	private ComboBox<String> actionTargetComboBox;
	@FXML
	private ComboBox<String> ratingComboBox;
	@FXML
	private ComboBox<String> fuelTypeComboBox;
	// Charts
	@FXML
	private LineChart<?, ?> chartGasoline;
	@FXML
	private LineChart<?, ?> chartDiesel;
	// Information labels - changing values
	@FXML
	protected Label labelAvgGasoline;
	@FXML
	protected Label labelAvgDiesel;
	@FXML
	protected Label labelLowestGasoline;
	@FXML
	protected Label labelLowestDiesel;
	@FXML
	protected Label labelStationsInDb;
	@FXML
	protected Label labelLastUpdate;
	@FXML
	protected Label userNameLabel;
	@FXML
	protected Label labelTotalRatings;
	@FXML
	protected Label labelTotalRatingsUser;
	@FXML
	protected Label labelAvgUser;
	// Information labels - static
    @FXML
    protected Label labelSearch;
    @FXML
    protected Label labelSearchBrand;
    @FXML
    protected Label labelSearchCity;
    @FXML
    protected Label labelSearchFuel;
    @FXML
    protected Label labelSearchRating;
    @FXML 
    protected Label labelUserArea;
	// Misc GUI components
	@FXML
	private ProgressIndicator updateIndicator;
	@FXML
	private Tab tabCharts;
	@FXML
	private Tab tabSearchResults;
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// Load resource language bundle
		this.rb = ResourceBundle.getBundle("resources.lang", Main.getPrefLang());
		// Populate content in all user accessible components
		this.populateLabels();
		this.populateTable();
		this.populateComboBoxes();
		this.populateCharts();
		this.tabPaneMain.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
		// Adjust language settings
		this.updateLanguage();
		// Log the window initialization
		Logger.getLogger(MainWindowController.class).info("Main window controller initialized");
	}
	
	public MainWindowController(){
		// Attempt to load remote objects needed for class logic
		try {
			this.ratingDao = (RatingDaoAPI)
					Main.ctx.lookup(PropLoader.load().getProperty("RatingDao"));
			this.stationDao = (StationDaoAPI)
					Main.ctx.lookup(PropLoader.load().getProperty("StationDao"));
			this.stationBrandsDao = (StationBrandsDaoAPI)
					Main.ctx.lookup(PropLoader.load().getProperty("StationBrandsDao"));
			this.priceDao = (PriceDaoAPI)
					Main.ctx.lookup(PropLoader.load().getProperty("PriceDao"));
			this.fuelTypeDao = (FuelTypeDaoAPI)
					Main.ctx.lookup(PropLoader.load().getProperty("FuelTypeDao"));
			this.offerDao = (OfferDaoAPI)
					Main.ctx.lookup(PropLoader.load().getProperty("OfferDao"));
			this.cityDao = (CityDaoAPI)
					Main.ctx.lookup(PropLoader.load().getProperty("CityDao"));
		} catch (NamingException e) {
			Logger.getLogger(MainWindowController.class)
				.fatal("Failed to load a custom DAO instance", e);
		} catch (NullPointerException npe) {
			Logger.getLogger(MainWindowController.class)
				.fatal("Failed to load a custom DAO instance - missing file path in config.properties", npe);
		}
	}

	// Method fetches all data required for displayed labels from the database
	public void populateLabels() {
		this.labelAvgGasoline.setText(this.rb.getString("avgPrice") + ": " + priceDao.getAvgLatest(fuelTypeDao.getFuelType("95")));
		this.labelAvgDiesel.setText(this.rb.getString("avgPrice") + ": " + priceDao.getAvgLatest(fuelTypeDao.getFuelType("Diesel")));
		this.labelStationsInDb.setText(this.rb.getString("totalStations") + ": " + stationDao.getNumberOfStations());
		this.labelLastUpdate.setText(this.rb.getString("lastUpdate") + ": " + new SimpleDateFormat("dd.MM.yyyy").format(priceDao.getLatestPrice()));
		this.labelTotalRatings.setText(this.rb.getString("totalRatings") + ": " + ratingDao.getNumberOfRatingsTotal());

		Price price = priceDao.getCheapestPrice(fuelTypeDao.getFuelType("95"));
		this.labelLowestGasoline.setText(this.rb.getString("historicMin") + ": " + price.getPrice() + "€ ("
				+ new SimpleDateFormat("dd.MM.yyyy").format(price.getDate()) + ")");
		price = priceDao.getCheapestPrice(fuelTypeDao.getFuelType("Diesel"));
		this.labelLowestDiesel.setText(this.rb.getString("historicMin") + ": " + price.getPrice() + "€ ("
				+ new SimpleDateFormat("dd.MM.yyyy").format(price.getDate()) + ")");

		if (this.activeUser != null) {
			this.labelTotalRatingsUser
					.setText(this.rb.getString("numberOfRatings") + ": " + ratingDao.getNumberOfRatings(this.activeUser));
			this.labelAvgUser.setText(this.rb.getString("avgRatings") + ": " + ratingDao.getAverage(this.activeUser));
		}
	}

	// Method fetches all data required for comboBox selections from the database
	public void populateComboBoxes() {
		//Fetch brands and cities
		this.brands = FXCollections.observableArrayList(stationBrandsDao.getStationBrandsString());
		this.cities = FXCollections.observableArrayList(cityDao.getCitiesAsString());
		
		// Populate possible station ratings - from 0.0 to 5.0
		for (double i = 0.0; i < 5.0; i++) {
			this.ratingComboBox.getItems().add("<" + i + "-" + (i + 1) + ">");
		}
		// Fetch fuel types
		this.fuelTypeComboBox.setItems(FXCollections.observableArrayList(fuelTypeDao.getTypesAsString()));
		this.actionComboBox.setItems(this.actions);
		this.actionTargetComboBox.setItems(this.actionTargets);
		this.cityComboBox.setItems(this.cities);
		this.brandsComboBox.setItems(this.brands);
		// Set action combo box language
		this.actionComboBox.setPromptText(rb.getString("action"));
		this.actionTargetComboBox.setPromptText(rb.getString("target"));
	}

	public void populateTable() {
		// Display loading indicator (spinner)
		this.updateIndicator.setVisible(true);
		// Restore selected offer after update so that user doesnt lose focus
		int selected = this.tableView.getSelectionModel().getSelectedIndex();
		
		Task<Void> task = new Task<Void>() {
		    @Override
		    public Void call() {
				offers = FXCollections.observableArrayList(offerDao.getAllAsObjects());

				colBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
				colCity.setCellValueFactory(new PropertyValueFactory<>("city"));
				colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
				colRating.setCellValueFactory(new PropertyValueFactory<>("ratings"));
				colFuelName.setCellValueFactory(new PropertyValueFactory<>("fuelType"));
				colFuelBrand.setCellValueFactory(new PropertyValueFactory<>("fuelBrand"));
				colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
				colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
				
				// If something was selected before update restore the selection
				if(selected >= 0){
					tableView.getSelectionModel().select(selected);
				}
				else {
					tableView.getSelectionModel().clearSelection();
				}
				/* Update the main table - if not done via Platform.runlater there is an
				 * "Not on FX application thread" exception*/
				Platform.runLater(new Runnable() {
	                 @Override public void run() {
	                	 tableView.setItems(offers);
	                 }
	             });
				// Hide the loading spinner
				updateIndicator.setVisible(false);
		        return null;
		    }
		};
		new Thread(task).start();
	}

	// Method fetches all data required for displayed charts from the database
	public void populateCharts() {
		// Populate gasoline 95 monthly average prices via PriceDao
		XYChart.Series gasolineSeries = new XYChart.Series();
		List<MonthlyAverage> yearlyGasolineData = priceDao
				.getAvgMonthPrices(fuelTypeDao.getFuelType("95"));

		for (MonthlyAverage month : yearlyGasolineData) {
			if (month.getMonthAvg() > 0.0) {
				gasolineSeries.getData().add(new XYChart.Data(month.getMonthName(), month.getMonthAvg()));
			}
		}
		chartGasoline.getData().add(gasolineSeries);
		
		// Populate diesel monthly average prices via PriceDao
		XYChart.Series dieselSeries = new XYChart.Series();
		List<MonthlyAverage> yearlyDieselData = priceDao
				.getAvgMonthPrices(fuelTypeDao.getFuelType("Diesel"));

		for (MonthlyAverage month : yearlyDieselData) {
			if (month.getMonthAvg() > 0.0) {
				dieselSeries.getData().add(new XYChart.Data(month.getMonthName(), month.getMonthAvg()));
			}
		}
		chartDiesel.getData().add(dieselSeries);
		// Render charts empty if there is no price data
		if (dieselSeries.getData().isEmpty()) {
			dieselSeries.getData().add(new XYChart.Data("No records", 0.0));
		}
		if (gasolineSeries.getData().isEmpty()) {
			gasolineSeries.getData().add(new XYChart.Data("No records", 0.0));
		}

		// Modify chart styles so that only lines are visible
		chartGasoline.setCreateSymbols(false);
		chartDiesel.setCreateSymbols(false);
		
		// Make monthly averages accessible
		this.yearlyGasolineData = yearlyGasolineData;
		this.yearlyDieselData = yearlyDieselData;
	}
	
	private void addContextMenu(Region region) {
		
		ContextMenuFactory menuFactory = new ContextMenuFactory(this);
		ContextMenu primaryMenu = menuFactory.getOffersContextMenu();
		ContextMenu secondaryMenu = menuFactory.getSideContextMenu();
		
		// Display secondary context menu at specified position at the edge of current screen
		region.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent t) {
				MenuItem fakeTooltip = menuFactory.getFakeTooltip();

				final Scene scene = tableView.getScene();
				final Point2D windowCoord = new Point2D(scene.getWindow().getX(), scene.getWindow().getY());
				final Point2D sceneCoord = new Point2D(scene.getX(), scene.getY());
				final Point2D nodeCoord = tableView.localToScene(0.0, 0.0);
				final double clickX = Math
						.round(windowCoord.getX() + sceneCoord.getX() + nodeCoord.getX() + tableView.getWidth());
				final double clickY = Math.round(windowCoord.getY() + sceneCoord.getY() + nodeCoord.getY());

				if (t.getButton() == MouseButton.PRIMARY) {
					secondaryMenu.getItems().setAll(fakeTooltip);
					secondaryMenu.show(tableView, clickX, clickY);
				} else {
					secondaryMenu.hide();
				}
			}
		});

		region.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent t) {
				if (t.getButton() == MouseButton.SECONDARY) {
					primaryMenu.show(tableView, t.getScreenX(), t.getScreenY());
				} else {
					primaryMenu.hide();
				}
			}
		});
	}

	private void addUsersTab() throws IOException {
		Properties prop = PropLoader.load();
		FXMLLoader loader = new FXMLLoader(getClass().getResource(prop.getProperty("UsersTabPath")));
		loader.load();
		UsersTabController usersTabController = loader.getController();
		// Make tables editable if admin user is logged in
		if (this.activeUser.getUserLevel() > 1) {
			usersTabController.enableEditing();
		}
		// Add user list tab to current view and make it active
		this.tabPaneMain.getTabs().add(usersTabController.getUserListTab());
		this.tabPaneMain.getSelectionModel().select(this.tabPaneMain.getTabs().size() - 1);
	}
	
	@FXML
	private void changeLanguageEn(ActionEvent event){
		Main.setPrefLang(Locale.ENGLISH);
		this.updateLanguage();
	}
	
	@FXML
	private void changeLanguageSk(ActionEvent event){
		Main.setPrefLang(new Locale("sk"));
		this.updateLanguage();
	}
	
	public void updateLanguage(){
		new LanguageChanger(this).changeWholeScreen();
		// Initialise resource bundle with properties file
		this.rb = ResourceBundle.getBundle("resources.lang", Main.getPrefLang());

		// Load required phrases - tabs
		this.tabCharts.setText(rb.getString("overview"));
		this.tabSearchResults.setText(rb.getString("searchResults"));
		
		// Edit action and actionTarget comboboxes
		if(!this.actions.isEmpty()) {
			this.actionComboBox.setPromptText(rb.getString("action"));
			this.actionComboBox.getSelectionModel().select(-1);
			this.actions.set(0, rb.getString("add"));
			this.actions.set(1, rb.getString("edit"));
			
			this.actionTargetComboBox.setPromptText(rb.getString("target"));
			this.actionTargetComboBox.getSelectionModel().select(-1);
			this.actionTargets.set(0, rb.getString("gasStation"));
			this.actionTargets.set(1, rb.getString("user"));
			
			if(this.actions.size() > 2){
				this.actions.set(2, rb.getString("delete"));
			}
		}
	}

	public void enableEditing() {
		// Add context menu for record deletion
		this.addContextMenu(tableView);
		// Make selected table fields editable if needed
		this.tableView.setEditable(this.activeUser.getUserLevel() > 0);
		this.colLocation.setEditable(this.activeUser.getUserLevel() > 0);

		// Handle editing actions performed by user
		this.colLocation.setCellFactory(TextFieldTableCell.forTableColumn());
		this.colLocation.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Station, String>>() {
			@Override
			public void handle(TableColumn.CellEditEvent<Station, String> t) {

				Offer offer = tableView.getSelectionModel().getSelectedItem();
				Station station = offer.getStation();
				station.setLocation(t.getNewValue());
				stationDao.updateStation(station);
				MainWindowController.this.offers.set(MainWindowController.this.offers.indexOf(offer), offer);
			}
		});
	}

	@FXML
	private void filterButtonAction(ActionEvent event) throws IOException {
		// Filter button main action - repopulate offers in the table
		String brandFilter = this.brandsComboBox.getValue();
		String cityFilter = this.cityComboBox.getValue();
		String fuelTypeFilter = this.fuelTypeComboBox.getValue();

		this.offers.setAll(offerDao.getByAttributes(
				brandFilter == null ? -1 : stationBrandsDao.getStationBrand(brandFilter).getId(),
				cityFilter == null ? -1 : cityDao.getCity(cityFilter).getId(),
				fuelTypeFilter == null ? -1 : fuelTypeDao.getFuelType(fuelTypeFilter).getId(),
				this.ratingComboBox.getSelectionModel().getSelectedIndex()));

		this.tabPaneMain.getSelectionModel().select(1);
	}

	@FXML
	private void updateDatabase(ActionEvent event) {
		DatabaseControl dbc = new DatabaseControl();
		// Display loading indicator (spinner)
		this.updateIndicator.setVisible(true);
		// Parse prices in new thread
		Task<Void> task = new Task<Void>() {
			@Override
			public Void call() {
				dbc.updateTables();
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						populateComboBoxes();
						populateCharts();
						populateLabels();
						populateTable();
						return;
					}
				});
				// Hide the loading spinner
				updateIndicator.setVisible(false);
				return null;
			}
		};
		new Thread(task).start();
	}

	@FXML
	private void dropCreateDatabase(ActionEvent event) throws InterruptedException {
		DatabaseControl dbc = new DatabaseControl();
		// Display loading indicator (spinner)
		this.updateIndicator.setVisible(true);
		// Parse prices in new thread
		Task<Void> task = new Task<Void>() {
			@Override
			public Void call() {
				dbc.recreateTables();
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						populateComboBoxes();
						populateCharts();
						populateLabels();
						populateTable();
						return;
					}
				});
				// Hide the loading spinner
				updateIndicator.setVisible(false);
				return null;
			}
		};
		new Thread(task).start();
	}
	
	@FXML
	private void pdfGenerate(ActionEvent event){
		PdfCreator pdf = new PdfCreator(this);
		// Display loading indicator (spinner)
		this.updateIndicator.setVisible(true);
		// Parse prices in new thread
		Task<Void> task = new Task<Void>() {
			@Override
			public Void call() {
				try {
					// Attempt to export the data
					pdf.exportPdfData();
				} catch (NullPointerException e) {
					/* If the operation is unsuccessful let the user know - but
					 * showing dialog window from another thread requires Platform.runlater */
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							new CustomAlert(Alert.AlertType.ERROR,
								"Error",
								"PDF export unsuccessful",
								"Failed to open/replace the specified PDF file. Please close the file first")
							.showAndWait();
						}
			        });
					// Log the event
					Logger.getLogger(MainWindowController.class)
						.error("Failed to generate PDF export - the specified file is still open", e);
				}
				// Hide the loading spinner
				updateIndicator.setVisible(false);
				return null;
			}
		};
		new Thread(task).start();
	}

	@FXML
	private void fireUserAction(ActionEvent event) throws IOException {
		// Determine the type of user action requested
		try {
			String action = this.actionComboBox.getValue();
			String actionTarget = this.actionTargetComboBox.getValue();

			if (actionTarget.equals(rb.getString("user"))) {
				if (action.equals(rb.getString("add"))) {
					goToRegWindowScene();
				} else if ((action.equals(rb.getString("edit")) || action.equals(rb.getString("delete")))
						&& this.tabPaneMain.getTabs().size() < 3) {
					addUsersTab();
				}
			} else if (actionTarget.equals(rb.getString("gasStation"))) {
				if (action.equals(rb.getString("add"))) {
					goToStationWindowScene();
				} else if (action.equals(rb.getString("edit")) || action.equals(rb.getString("delete"))) {
					this.tabPaneMain.getSelectionModel().select(1);
				}
			}
		} catch (NullPointerException e) {
			// Nothing selected in combo boxes
			new CustomAlert(Alert.AlertType.WARNING, "Warning",
					"Action unsuccessful",
					"Please select a valid action and action target")
			.showAndWait();
			// Log the event
			Logger.getLogger(MainWindowController.class)
				.error("Unknown user action with or without target", e);
		}
	}

	public void goToStationWindowScene() {
		Properties prop = PropLoader.load();
		// Continue to user registration screen
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(prop.getProperty("StationWindowPath")));
			Parent root = (Parent) loader.load();

			Stage stationWindowStage = new Stage();
			stationWindowStage.setTitle("New gas station addition");
			stationWindowStage.setMinHeight(stationWindowStage.getHeight());
			stationWindowStage.setMinWidth(stationWindowStage.getWidth());
			stationWindowStage.setScene(new Scene(root));

			loader.getController();
			stationWindowStage.show();
		} catch (IOException ex) {
			Logger.getLogger(MainWindowController.class).fatal("Failed to read config.properties file", ex);
		} catch (NullPointerException npe) {
    		Logger.getLogger(MainWindowController.class)
				.fatal("Station window - missing file path in config.properties", npe);
		}
	}

	public void goToRegWindowScene() {
		Properties prop = PropLoader.load();
		// Continue to user registration screen
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(prop.getProperty("RegWindowPath")));
			Parent root = (Parent) loader.load();

			Stage regWindowStage = new Stage();
			regWindowStage.setTitle("New user registration");
			regWindowStage.setMinHeight(regWindowStage.getHeight());
			regWindowStage.setMinWidth(regWindowStage.getWidth());
			regWindowStage.setScene(new Scene(root));

			RegistrationWindowController regWindowController = loader.getController();
			regWindowController.userLevelAvailable(this.activeUser.getUserLevel() > 1);
			regWindowController.setTriggerMainWindow(false);
			regWindowController.setActStage(regWindowStage);

			regWindowStage.show();
		} catch (IOException ex) {
			Logger.getLogger(MainWindowController.class).fatal("Failed to read config.properties file", ex);
		} catch (NullPointerException npe) {
    		Logger.getLogger(MainWindowController.class)
				.fatal("Registration window - missing file path in config.properties", npe);
		}
	}
	
	@FXML
	private void cancelFilter(ActionEvent event) {
		// Clear all filter fields and re-populate the table
		this.brandsComboBox.valueProperty().set(null);
		this.cityComboBox.valueProperty().set(null);
		this.ratingComboBox.valueProperty().set(null);
		this.fuelTypeComboBox.valueProperty().set(null);
		this.populateTable();
	}

	public void setActiveUser(User activeUser) {
		// Assign active user - needed for future database operations etc.
		this.activeUser = activeUser;
		this.userNameLabel.setText(rb.getString("loggedInAs") + ": " + 
				(this.activeUser == null ? "-" : this.activeUser.getUsername()));
		this.labelTotalRatingsUser.setText(rb.getString("numberOfRatings") + ": " + 
				ratingDao.getNumberOfRatings(this.activeUser));
		this.labelAvgUser.setText(rb.getString("avgRatings") + ": " + 
				ratingDao.getAverage(this.activeUser));
		
		// Enable editing - editing level is handled by method itself
		this.enableEditing();
		// Control the user actions based on userLevel
		if (this.activeUser.getUserLevel() > 0) {
			this.actions.add(rb.getString("add"));
			this.actions.add(rb.getString("edit"));
			this.actionTargets.add(rb.getString("gasStation"));
			this.actionTargets.add(rb.getString("user"));
		} else {
			this.fireUserActionButton.setDisable(true);
			this.actionComboBox.setDisable(true);
			this.actionTargetComboBox.setDisable(true);
		}
		// Enable superUser (admin) actions
		if (this.activeUser.getUserLevel() > 1) {
			this.actions.add(rb.getString("delete"));
		}
	}

	public User getActiveUser() {
		return activeUser;
	}

	public RatingDaoAPI getRatingDao() {
		return ratingDao;
	}

	public StationDaoAPI getStationDao() {
		return stationDao;
	}

	public PriceDaoAPI getPriceDao() {
		return priceDao;
	}

	public TableView<Offer> getTableView() {
		return tableView;
	}

	public ObservableList<Offer> getOffers() {
		return offers;
	}

	public Label getLabelAvgGasoline() {
		return labelAvgGasoline;
	}

	public Label getLabelAvgDiesel() {
		return labelAvgDiesel;
	}

	public Label getLabelStationsInDb() {
		return labelStationsInDb;
	}

	public Label getLabelLastUpdate() {
		return labelLastUpdate;
	}

	public List<MonthlyAverage> getYearlyGasolineData() {
		return yearlyGasolineData;
	}

	public List<MonthlyAverage> getYearlyDieselData() {
		return yearlyDieselData;
	}

	public TabPane getTabPaneMain() {
		return tabPaneMain;
	}
}