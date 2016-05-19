package gui;

import java.util.Calendar;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

import controllers.MainWindowController;
import entities.Offer;
import entities.Price;
import entities.Rating;
import entities.Station;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class ContextMenuFactory {
	private MainWindowController mwc;
	
	public ContextMenuFactory(MainWindowController mwc){
		this.mwc = mwc;
	}
	
	/* Creates a new context menu for adding, deleting and
	 * updating gas price offers in main table view */
	public ContextMenu getOffersContextMenu(){
		ContextMenu primaryMenu = new ContextMenu();
		// Add required user actions to context menu
		primaryMenu.getItems().add(new MenuItem("Ohodnotiù stanicu"));
		primaryMenu.getItems().add(new MenuItem("Aktualizovaù cenu"));
		// Restrict low level users from deleting records
		MenuItem recordDeletion = new MenuItem("Vymazaù stanicu a s˙visiace z·znamy");
		recordDeletion.setDisable(this.mwc.getActiveUser().getUserLevel() < 2);
		primaryMenu.getItems().add(recordDeletion);
		
		// Add all required event handlers
		this.addOffersHandlers(primaryMenu);
		// Return fully initialized context menu
		return primaryMenu;
	}
	
	// Adds all necessary handlers for primary context menu
	private void addOffersHandlers(ContextMenu primaryMenu){
		this.addRatingHandler(primaryMenu);
		this.addPriceHandler(primaryMenu);
		this.addStationDeleteHandler(primaryMenu);
	}
	
	// Enables editing of ratings for users
	private void addRatingHandler(ContextMenu primaryMenu){
		// Add a handler to primary menu to enable ratings of stations for users
		primaryMenu.getItems().get(0).setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {

				Offer offer = mwc.getTableView().getSelectionModel().getSelectedItem();
				// If no offer is selected do nothing
				if(offer == null){
					return;
				}
				Rating rating = new Rating();
				rating.setStation(offer.getStation());
				rating.setUser(mwc.getActiveUser());

				TextInputDialog dialog = new TextInputDialog("5.0");
				dialog.setContentText("Vaöe hodnotenie:");
				dialog.setHeaderText("ProsÌm, zvoæte hodnotu medzi 0.0-5.0");
				dialog.setTitle("Hodnotenie Ëerpacej stanice");

				String response;
				CustomAlert wrongInputAlert = new CustomAlert(
						Alert.AlertType.ERROR,
						"Error",
						"Wrong input format",
						"Please enter a valid floating point value (double)");
				try {
					response = dialog.showAndWait().get();
					double ratingValue = Double.parseDouble(response);
					if (ratingValue >= 0.0 && ratingValue <= 5.0) {
						rating.setRate(ratingValue);
					} else {
						wrongInputAlert.showAndWait();
						return;
					}
				} catch (NoSuchElementException nse) {
					// User did not provide the rating
					Logger.getLogger(EventHandler.class)
						.warn("User provided no input while rating station", nse);
					return;
				} catch (NumberFormatException nfe) {
					wrongInputAlert.showAndWait();
					Logger.getLogger(EventHandler.class)
						.warn("User entered invalid number format input while rating station", nfe);
					return;
				}
				// Insert new user rating or update if already exists
				if (!mwc.getRatingDao().updateRating(rating)) {
					mwc.getRatingDao().insert(rating);
				}
				// Update the main table view
				mwc.populateTable();
			}
		});
	}
	
	// Enables editing of price for users
	private void addPriceHandler(ContextMenu primaryMenu){
		primaryMenu.getItems().get(1).setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				Offer offer = mwc.getTableView().getSelectionModel().getSelectedItem();

				Price price = new Price();
				price.setOffer(offer);
				price.setUser(mwc.getActiveUser());

				TextInputDialog dialog = new TextInputDialog("");
				dialog.setContentText("Zadajte nov˙ cenu:");
				dialog.setHeaderText("ProsÌm, zadajte cenu v tvare desatinnÈho ËÌsla");
				dialog.setTitle("Aktualiz·cia ponuky Ëerpacej stanice");

				String response;
				CustomAlert wrongInputAlert = new CustomAlert(Alert.AlertType.ERROR, "Error", "Wrong input format",
						"Please enter a valid floating point value (double)");
				try {
					response = dialog.showAndWait().get();
					double priceValue = Double.parseDouble(response);
					if (priceValue > 0.0) {
						price.setPrice(priceValue);
					} else {
						wrongInputAlert.showAndWait();
						return;
					}
				} catch (NoSuchElementException nse) {
					// User did not provide the rating
					return;
				} catch (NumberFormatException nfe) {
					wrongInputAlert.showAndWait();
					//Logger.getLogger(MainWindowController.class.getName()).log(Level.WARNING, null, nfe);
					return;
				}
				// Assign date and insert new price
				price.setDate(Calendar.getInstance().getTime());
				mwc.getPriceDao().insert(price);
				// Refresh main window data
				mwc.populateCharts();
				mwc.populateLabels();
				// Update affected station row
				offer.addPrice(price);
				mwc.getOffers().set(mwc.getOffers().indexOf(offer), offer);
				mwc.getTableView().getSelectionModel().select(offer);
			}
		});
	}
	
	// Enables deletion of gas stations for users
	private void addStationDeleteHandler(ContextMenu primaryMenu){
		primaryMenu.getItems().get(2).setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				Station station = mwc.getTableView().getSelectionModel().getSelectedItem().getStation();

				for (int i = mwc.getOffers().size() - 1; i >= 0; i--) {
					if (mwc.getOffers().get(i).getStation().getId() == station.getId())
						mwc.getOffers().remove(mwc.getOffers().get(i));
				}
				mwc.getStationDao().deleteRecord(station.getId());
			}
		});
	}
	
	/* Creates a new context menu which serves as a single station information display.
	 * Menu is added at the very right border of current application screen and is triggered
	 * by selecting an offer in main table view - fake tooltip style */
	public ContextMenu getSideContextMenu(){
		ContextMenu secondaryMenu = new ContextMenu();
		secondaryMenu.setStyle("-fx-background-color: #0096c9");
		return secondaryMenu;
	}
	
	public MenuItem getFakeTooltip(){
		MenuItem fakeTooltip = new MenuItem();
		try {

			Label label = new Label(mwc.getTableView()
					.getSelectionModel()
					.getSelectedItem()
					.toString()
			);
			label.setPrefWidth(230);
			label.setWrapText(true);
			label.setTextAlignment(TextAlignment.JUSTIFY);
			label.setTextFill(Color.web("#ffffff"));
			fakeTooltip.setGraphic(label);
		} catch (NullPointerException npe) {
			Logger.getLogger(EventHandler.class)
				.fatal("Failed to create secondary (fake tooltip) context menu", npe);
		}
		return fakeTooltip;
	}
}
