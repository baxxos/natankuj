package misc;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import controllers.MainWindowController;
import entities.Offer;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import main.Main;

public class PdfCreator {
	
	// File related attributes
	private final String filePath;
	private final Document document;
	private Properties props;
	private ResourceBundle rb;
	private PdfWriter writer;
	// Content related attribute
	private MainWindowController mwc;
	// Various document fonts
	private final Font headingFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
	private final Font smallBoldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
	private final Font smallItalicFont = new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC);
	private final Font tinyBoldFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);
	private final Font tinyNormalFont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
	private Font blueFont = new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC, BaseColor.BLUE);
	
	//Chart creation constants
	private enum FuelType {
		GASOLINE, DIESEL
	}
	
	public PdfCreator(MainWindowController mwc){
		// Set the private content attributes
		this.props = PropLoader.load();
		// Load the desired export path from config.properties
		this.filePath = props.getProperty("pdfExportPath");
		// Initialize the pdf writer and document
		this.document = new Document();
		try {
			this.mwc = mwc;
			this.writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
			this.rb = ResourceBundle.getBundle("resources.lang", Main.getPrefLang());
		} catch (FileNotFoundException e) {
			Logger.getLogger(PdfCreator.class)
				.fatal("Failed to initialize PDF export - file path not found", e);
		} catch (DocumentException e) {
			Logger.getLogger(PdfCreator.class)
				.fatal("Unspecified PDF document exception", e);
		} finally {
			Logger.getLogger(PdfCreator.class)
				.info("PdfCreator instance created");
		}
	}
	
	public void exportPdfData() {
		// Compose and save the whole document
			document.open();
			this.addMetaData();
			this.addTitlePage();
			this.addContent();
			document.close();
	}
	
	// Pdf file -> properties meta data
	private void addMetaData(){
		document.addTitle(props.getProperty("pdfTitle"));
	    document.addSubject(props.getProperty("pdfSubject"));
	    document.addKeywords("Gas, price, fuel, petrol, diesel, data");
	    document.addAuthor(props.getProperty("pdfAuthor"));
	    document.addCreator(props.getProperty("pdfAuthor"));
	}
	
	// Generate title page
	private void addTitlePage(){
		// Initialize the first page of document
		Paragraph preface = new Paragraph();
		this.addEmptyLine(preface, 1);
		
		// Set the title, author and date of generated document
		preface.add(new Paragraph(rb.getString("pdfTitle"), this.headingFont));
		preface.add(new Paragraph(
				this.rb.getString("pdfReportBy") + ": " + this.mwc.getActiveUser().getName() + 
				" " + this.mwc.getActiveUser().getSurname() + ", " + this.rb.getString("pdfDate") + " " +
				new Date(), smallItalicFont)
		);
		// Set the subtitle (description of the document)
		this.addEmptyLine(preface, 3);
		preface.add(new Paragraph(rb.getString("pdfSubTitle"), this.smallBoldFont));
		
		// Add the data accuracy disclaimer
		this.addEmptyLine(preface, 1);
		preface.add(new Paragraph(rb.getString("pdfDisclaimer"), this.blueFont));
		
		try {
			// Try to commit changes
			document.add(preface);
		} catch (DocumentException e) {
			Logger.getLogger(PdfCreator.class)
				.fatal("Failed to write the title page into PDF document", e);
		}
		// Start a new page
		document.newPage();
	}
	
	private void addContent(){
		// Generated table and its containing paragraph
		PdfPTable table = this.createTable();
		Paragraph tableParagraph = new Paragraph();
		// Compose and draw two PDF viewable charts - gasoline and diesel in separate paragraphs
		Paragraph gasolineParagraph = new Paragraph();
		Paragraph dieselParagraph = new Paragraph();
		
		// Create JFreeCharts for each of the enum fuel types
		JFreeChart chartGasoline = this.createChart(FuelType.GASOLINE);
		JFreeChart chartDiesel = this.createChart(FuelType.DIESEL);
		
		BufferedImage bufferedImageGasoline = chartGasoline.createBufferedImage(250, 200);
		BufferedImage bufferedImageDiesel = chartDiesel.createBufferedImage(250, 200);
        try {
        	// Create gasoline chart pdf image
        	Image imageGasoline = Image.getInstance(writer, bufferedImageGasoline, 1.0f);
        	imageGasoline.setAlignment(Element.ALIGN_CENTER);
        	// Create diesel cart pdf image
        	Image imageDiesel = Image.getInstance(writer, bufferedImageDiesel, 1.0f);
        	imageDiesel.setAlignment(Element.ALIGN_CENTER);
        	
        	gasolineParagraph.add(imageGasoline);
        	this.addEmptyLine(gasolineParagraph, 1);
			
			dieselParagraph.add(imageDiesel);
			this.addEmptyLine(dieselParagraph, 1);
			
			this.addEmptyLine(tableParagraph, 1);
			tableParagraph.add(table);

    		// Create containing PDF anchor/chapter
    		Anchor anchor = new Anchor(rb.getString("pdfChapter"), this.headingFont);
    		anchor.setName(rb.getString("pdfChapter"));
    		Chapter mainChapter = new Chapter(new Paragraph(anchor), 1);
    		
    		// Create info labels chapter
    		Paragraph sub = new Paragraph(rb.getString("pdfStatistic"), this.smallBoldFont);
    	    Section subSection = mainChapter.addSection(sub);
    	    // Write the labels into the document
    	    try {
	    	    String temp = mwc.getLabelAvgDiesel().getText();
	    	    
	    	    subSection.add(new Paragraph(rb.getString("pdfAvgDiesel") 
	    	    		+ ": " + temp.substring(temp.indexOf(":") + 2)));
	    	    temp = mwc.getLabelAvgGasoline().getText();
	    	    subSection.add(new Paragraph(rb.getString("pdfAvgGasoline") + 
	    	    		": " + temp.substring(temp.indexOf(":") + 2)));
	    	    temp = mwc.getLabelLastUpdate().getText();
	    	    subSection.add(new Paragraph(rb.getString("lastUpdate") + 
	    	    		": " + temp.substring(temp.indexOf(":") + 2)));
	    	    temp = mwc.getLabelStationsInDb().getText();
	    	    subSection.add(new Paragraph(rb.getString("pdfStationsTotal") + 
	    	    		": " + temp.substring(temp.indexOf(":") + 2 ))
	    	    );
    	    } catch (IndexOutOfBoundsException e) {
    	    	subSection.add(new Paragraph(rb.getString("pdfStatsError")));
    	    	Logger.getLogger(PdfCreator.class)
    	    		.error("Failed to load fuel database stats labels into pdf", e);
    	    } finally {
    	    	subSection.add(new Paragraph("\n"));
    	    }
    		// Create graph images chapter - gasoline
    		sub = new Paragraph(this.rb.getString("pdfChapterGasoline"), this.smallBoldFont);
    	    subSection = mainChapter.addSection(sub);
    	    subSection.add(gasolineParagraph);
    	    
    	    // Create graph images chapter - diesel
    	    sub = new Paragraph(this.rb.getString("pdfChapterDiesel"), this.smallBoldFont);
    	    subSection = mainChapter.addSection(sub);
    	    subSection.add(dieselParagraph);
    	    
    		// Create table chapter
    	    sub = new Paragraph(this.rb.getString("pdfChapterTable"), this.smallBoldFont);
    	    subSection = mainChapter.addSection(sub);
    	    subSection.add(tableParagraph);
    	    
    	    // Commit additions
    	    document.add(mainChapter);
		} catch (BadElementException e) {
			Logger.getLogger(PdfCreator.class)
				.error("Failed to obtain iText PDF image instance", e);
		} catch (IOException e) {
			Logger.getLogger(PdfCreator.class)
				.error("Failed to obtain iText PDF image instance from PDF writer", e);
		} catch (DocumentException e) {
			Logger.getLogger(PdfCreator.class)
				.error("Failed to append main content to the PDF document", e);
		}
	}
	
	private JFreeChart createChart(FuelType ft){
		// Initialize required data sets
		DefaultCategoryDataset dataSetGasoline = new DefaultCategoryDataset();
		DefaultCategoryDataset dataSetDiesel = new DefaultCategoryDataset();
		// Choose appropriate fuel type and create chart
		if(ft.equals(FuelType.GASOLINE)) {
			// Fill gasoline data set
			for(MonthlyAverage month : mwc.getYearlyGasolineData()){
				dataSetGasoline.addValue(
						month.getMonthAvg(),
						this.rb.getString("chartGasoline"),
						month.getMonthName()
				);
			}
		} else if (ft.equals(FuelType.DIESEL)) {
			// Fill diesel data set
			for(MonthlyAverage month : mwc.getYearlyDieselData()){
				dataSetDiesel.addValue(
						month.getMonthAvg(),
						this.rb.getString("chartDiesel"),
						month.getMonthName()
				);
			}
		}
		JFreeChart chart = ChartFactory.createLineChart(
				"",
				this.rb.getString("price"),
				this.rb.getString("month"),
				ft.equals(FuelType.GASOLINE) ? dataSetGasoline : dataSetDiesel
		);
		
		return chart;
	}
	
	private PdfPTable createTable(){
		// Main data source
		TableView<Offer> dataTable = mwc.getTableView();
		// Table to be generated
		PdfPTable table = new PdfPTable(dataTable.getColumns().size());
		
		// Populate table heading (first row)
		for(TableColumn<?, ?> col : dataTable.getColumns()){
			PdfPCell cell = new PdfPCell(new Phrase(this.removeAccents(col.getText()), this.tinyBoldFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		}
		// Set the first row as header type
		table.setHeaderRows(1);
		PdfPCell cell;
		
		// Populate the rest of the table (all columns) from given data 
		for(Offer o : dataTable.getItems()){
			// Station brand
			cell = new PdfPCell(new Phrase(this.removeAccents(o.getBrand()), this.tinyNormalFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
			// City
			cell = new PdfPCell(new Phrase(this.removeAccents(o.getCity()), this.tinyNormalFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
			// Location
			cell = new PdfPCell(new Phrase(this.removeAccents(o.getLocation()), this.tinyNormalFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
			// Average rating
			cell = new PdfPCell(new Phrase(this.removeAccents(o.getRatings().toString()), this.tinyNormalFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
			// Fuel type
			cell = new PdfPCell(new Phrase(this.removeAccents(o.getFuelType()), this.tinyNormalFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
			// Fuel brand
			cell = new PdfPCell(new Phrase(this.removeAccents(o.getFuelBrand()), this.tinyNormalFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
			// Latest price
			cell = new PdfPCell(new Phrase(this.removeAccents(o.getPrice().toString()), this.tinyNormalFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
			// Last update
			cell = new PdfPCell(new Phrase(this.removeAccents(o.getDate()), this.tinyNormalFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		}
		
		return table;
	}
	
	// Add an empty line(s) to paragraph
	private void addEmptyLine(Paragraph paragraph, int number) {
	    for (int i = 0; i < number; i++) {
	      paragraph.add(new Paragraph(" "));
	    }
	}
	
	// Remove accents from characters in given string
	private String removeAccents(String s){
	    s = Normalizer.normalize(s, Normalizer.Form.NFD);
	    s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
	    return s;
	}
}
