import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Control;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel; 
import org.jfree.chart.JFreeChart; 
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset; 
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset; 
import org.jfree.ui.ApplicationFrame; 
import org.jfree.ui.RefineryUtilities;
import javafx.scene.control.MenuItem;
import javax.swing.JFrame;
import javax.swing.*;
import java.io.*;
import java.util.*;


public class qPCR extends Application {

	private Text actionStatus;
   private ImageView imv;
   private Button btn5;
   private HBox btn5HBox;
	private Stage savedStage;
   private TextField txtField;
   private TextArea txtArea;
   private List<File> selectedFiles;
   private String txt;
   private String netSamples;
   private Set<String> geneList;
   private List<String> genesAverage;
   private List<String> sampleNamesAverage;
   private List<Double> averageCtData;
   private List<Double> error;
   
	private static final String titleTxt = "qPCR Basic Analysis Platform";

	public static void main(String [] args) throws FileNotFoundException {

		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
	   //defines check mark image view
//       imv = new ImageView();
//       Image image = new Image(getClass().getResourceAsStream("green-checkmark.png"));
//       imv.setImage(image);
//       imv.setFitHeight(15);
//       imv.setFitWidth(15);
		
      primaryStage.setTitle(titleTxt);
      
      // Text field and label for taking in the number of replicates used
		Label txtFieldLabel = new Label("Enter number of replicates:");
		txtFieldLabel.setFont(Font.font("Calibri", FontWeight.NORMAL, 14));
		txtField = new TextField();
		txtField.setPrefColumnCount(2);
      HBox txtFieldVbox = new HBox(10);
		
      
      // Text Area and label for taking in net Sample names
		Label txtAreaLabel = new Label("Enter experimental sample groups (comma separated):");
		txtAreaLabel.setFont(Font.font("Calibri", FontWeight.NORMAL, 14));
		txtArea = new TextArea();
		txtArea.setWrapText(true);
		ScrollPane scroll = new ScrollPane();
		scroll.setContent(txtArea);
		scroll.setFitToWidth(true);
		scroll.setPrefHeight(50);
		scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
      HBox txtAreaLabelHBox = new HBox(10);
      HBox txtAreaHBox = new HBox(10);
		txtAreaLabelHBox.getChildren().addAll(txtAreaLabel);
      txtAreaHBox.getChildren().addAll(scroll);
      
      // Buttons
		Button btn1 = new Button("Analyze");
		btn1.setOnAction(new AnalyzeButtonListener());
		
      Button btn2 = new Button("Browse");
		btn2.setOnAction(new MultipleFcButtonListener());

      
      Button btn4 = new Button("Plot");
      btn4.setOnAction(new PlotButtonListener());
      
      HBox buttonsHb = new HBox(10);
      buttonsHb.getChildren().addAll(btn2, btn1, btn4);
      buttonsHb.setAlignment(Pos.BOTTOM_CENTER);
      
      Button btn3 = new Button("Submit");
		btn3.setOnAction(new SubmitButtonListener());
		txtFieldVbox.getChildren().addAll(txtFieldLabel, txtField, btn3);
      txtFieldVbox.setAlignment(Pos.BOTTOM_CENTER);
      
      Button btn5 = new Button("Submit");
      btn5HBox = new HBox(10);
//       btn5HBox.getChildren().addAll(btn5, imv);
      btn5HBox.getChildren().addAll(btn5);
      btn5HBox.setAlignment(Pos.BOTTOM_LEFT);
      btn5.setOnAction(new TextAreaListener());
      
     
      // Text area Vbox
      VBox vboxArea = new VBox(10);
		vboxArea.setPadding(new Insets(25, 25, 25, 25));
		vboxArea.getChildren().addAll(txtAreaLabelHBox, txtAreaHBox, btn5HBox);
		
      MenuBar menuBar = new MenuBar();
      Menu menuFile = new Menu("File");
      MenuItem saveMenuItem = new MenuItem("Export");
      saveMenuItem.setOnAction(new saveMenuListener());
      MenuItem exitMenuItem = new MenuItem("Exit");
      exitMenuItem.setOnAction(actionEvent -> primaryStage.close());
      menuFile.getItems().addAll(saveMenuItem, exitMenuItem);
      menuBar.getMenus().addAll(menuFile);
      VBox menuBarVbox = new VBox(10);
		menuBarVbox.setPadding(new Insets(0, 0, 0, 0));
      menuBarVbox.getChildren().addAll(menuBar);

      
		
      // Vbox
		VBox vbox = new VBox(20);
		vbox.setPadding(new Insets(0, 0, 25, 0));
		vbox.getChildren().addAll(menuBarVbox, txtFieldVbox, vboxArea, buttonsHb);
 

      
      // Scene
		Scene scene = new Scene(vbox, 450, 300); // w x h
	   primaryStage.setScene(scene);
		primaryStage.show();
      primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("bar-chart-md.png")));

		savedStage = primaryStage;
      
	}
   private class saveMenuListener implements EventHandler<ActionEvent> {
      
      @Override
      public void handle(ActionEvent e){
      
   
   
   
      }
   
   }
   
   private class AnalyzeButtonListener implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent e) {
         if(selectedFiles == null){
               Alert alert = new Alert(AlertType.ERROR);
               alert.setTitle("Error");
               alert.setHeaderText("No Files Selected");
               alert.setContentText("Browse and select files");
               alert.showAndWait();
         } else  {
            try {
               Analyze(selectedFiles, txt);
            } catch (FileNotFoundException fnfe) {
               Alert alert = new Alert(AlertType.ERROR);
               alert.setTitle("Exception");
               alert.setHeaderText("File Not Found");
               
               Exception ex = new FileNotFoundException();
               
               // Create expandable Exception.
               StringWriter sw = new StringWriter();
               PrintWriter pw = new PrintWriter(sw);
               ex.printStackTrace(pw);
               String exceptionText = sw.toString();
               
               Label label = new Label("The exception stacktrace was:");
               
               TextArea textArea = new TextArea(exceptionText);
               textArea.setEditable(false);
               textArea.setWrapText(true);
               
               textArea.setMaxWidth(Double.MAX_VALUE);
               textArea.setMaxHeight(Double.MAX_VALUE);
               GridPane.setVgrow(textArea, Priority.ALWAYS);
               GridPane.setHgrow(textArea, Priority.ALWAYS);
               
               GridPane expContent = new GridPane();
               expContent.setMaxWidth(Double.MAX_VALUE);
               expContent.add(label, 0, 0);
               expContent.add(textArea, 0, 1);
               
               // Set expandable Exception into the dialog pane.
               alert.getDialogPane().setExpandableContent(expContent);
               
               alert.showAndWait();

		      }
         }
	   }
   }

	private class MultipleFcButtonListener implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent e) {

			showMultipleFileChooser();

		}
	}
   
   private class SubmitButtonListener implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent e) {
         txt = txtField.getText();
		}
	}
   
   private class TextAreaListener implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent e) {
         netSamples = txtArea.getText();
         imv = new ImageView();
         Image image = new Image(getClass().getResourceAsStream("green-checkmark.png"));
         imv.setImage(image);
         btn5HBox.getChildren().addAll(btn5, imv);
		}
	}
   //generate bar chart
   private class PlotButtonListener implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent e) {
         if(netSamples == null){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No Sample Names Given");
            alert.setContentText("Enter List of Names");
            alert.showAndWait();
         } else if (sampleNamesAverage == null){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No Analysis Data");
            alert.setContentText("Execute Analysis Before Plotting");
            alert.showAndWait();
         } else {
            Plot();
         }		
      }
	}
   
   //browse button functionality
	private void showMultipleFileChooser() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select txt files");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.txt"));
		selectedFiles = fileChooser.showOpenMultipleDialog(savedStage);
	}
   
   //Compiles all text files and then Analyzes data
   private void Analyze(List<File> files, String replicates) throws FileNotFoundException {
      File newFile = new File("compiledTXT");
      PrintStream writeToFile = new PrintStream(newFile);
      for(int i = 1; i < files.size(); i++){
         Scanner lineScan = new Scanner(files.get(i));
         while(lineScan.hasNextLine()){
         writeToFile.println(lineScan.nextLine());
         }
      }

      
      Scanner fileReader = new Scanner(newFile);
      geneList = new HashSet<String>();
      List<String> genes = new ArrayList<String>();
      List<String> sampleNames = new ArrayList<String>();
      List<String> ctData = new ArrayList<String>();

      while(fileReader.hasNextLine()){
         String line = fileReader.nextLine();
         if(line.length() > 0 && line.charAt(0) == 'W') {
            String secondLine = fileReader.nextLine();
            while(secondLine.charAt(0) != 'S'){
               String[] pieces = secondLine.split("\\s+");
               for(int i = 0; i < pieces.length; i++){
                  if(pieces[i].equals("SYBR") || pieces[i].equals("FAM")){
                     String gene = pieces[i - 1];
                     geneList.add(gene);
                     genes.add(gene);
                     String ct = pieces[i + 2];
                     ctData.add(ct);
                     String sample = "";
                     for(int j = 1; j < i - 1; j++){
                        sample = sample + " " + pieces[j];
                     }
                     sampleNames.add(sample.trim());
                     i = pieces.length;
                  }
               }
               secondLine = fileReader.nextLine();
            }
               
         }
      
      }
      
      //Finds all samples that are undetermined and replaces their Ct with their
      //respective duplicate data. Works for triplicates and duplicates.

      int n = Integer.parseInt(replicates);
      for(int i = 0; i < ctData.size(); i = i + n){
         double data = 0.0;
         List<Integer> ind = new ArrayList<Integer>();
         int dataCount = 0;
         for(int j = i; j < i + n; j++){
            if(ctData.get(j).equalsIgnoreCase("Undetermined")){
               ind.add(j);
            } else {
               data += Double.parseDouble(ctData.get(j));
               dataCount++;
            }
         }
         double replacement = data / dataCount;
         if(!ind.isEmpty()){
            for(int k = 0; k < ind.size(); k++){
               int indice = ind.get(k);
               ctData.remove(indice);
               ctData.add(indice,  Double.toString(replacement));
            }
         }
      }

      //Converts all string entries into double entries to improve efficiency of
      //math operations.
      List<Double> ctDataInt = new ArrayList<Double>();
      for(int i = 0; i < ctData.size(); i++){
         ctDataInt.add(Double.parseDouble(ctData.get(i)));
      }

      //Water sample elimination. Removes all sample names that are water,
      //and also eliminates their corresponding Ct values and gene markers
      List<Integer> waterIndex = new ArrayList<Integer>();
      for(int i = 0; i < sampleNames.size(); i++){
         if(sampleNames.get(i).equalsIgnoreCase("water")){
            waterIndex.add(i);
         }
      }
      for(int i = 0; i < waterIndex.size(); i++){
            int indice = waterIndex.get(i);
            sampleNames.remove(indice);
            ctDataInt.remove(indice);
            genes.remove(indice);
      }
      //Gets the average of the Ct data of all the sample replicates
      genesAverage = new ArrayList<String>();
      sampleNamesAverage = new ArrayList<String>();
      averageCtData = new ArrayList<Double>();
      n = Integer.parseInt(replicates);
      for(int i = 0; i < ctDataInt.size(); i = i + n){
         double sum = 0.0;
         for(int j = i; j < i + n; j++){
            sum += ctDataInt.get(j);
         }
         averageCtData.add(i / n, sum / n);
         genesAverage.add(genes.get(i));
         sampleNamesAverage.add(sampleNames.get(i));
      }   
	}
   
   private void Plot(){
   
      String[] sampleGroups = netSamples.split(",");
      for(int i = 0; i < sampleGroups.length; i++){
         sampleGroups[i] = sampleGroups[i].trim();
      }
      
      //generates data sample list of net sample names
      List<String> groupDesignation = new ArrayList<String>();
      for(String str : sampleNamesAverage){
         for(int i = 0; i < sampleGroups.length; i++){
            for(int j = 0; j < str.length(); j++){
               if(!(j + (sampleGroups[i].length()) > str.length() - 1)){
                  String sub = str.substring(j, j + (sampleGroups[i].length()));
                  if(sub.equalsIgnoreCase(sampleGroups[i])){
                     groupDesignation.add(sampleGroups[i]);
                  }
               }
            }
         }
      }
      
      //generates data to normalize to
      double[] normData = new double[sampleGroups.length];
      int[] normCount = new int[sampleGroups.length];
      for(int i = 0; i < genesAverage.size(); i++){
         if(genesAverage.get(i).equalsIgnoreCase("hHPRT")){
            for(int j = 0; j < sampleGroups.length; j++){
               int count = 0;
               if(groupDesignation.get(i).equalsIgnoreCase(sampleGroups[j])){
                  normData[j] += averageCtData.get(i);
                  normCount[j]++;
               }
            }
         }
      }
      
      double[] normalize = new double[normData.length];
      for(int i = 0; i < normData.length; i++){
         normalize[i] = normData[i] / normCount[i];
      } 
      //Calculate ploting data
      for(String g : geneList){
         Map<String, List<Double>> dat = new HashMap<String, List<Double>>();
         List<Double> val = new ArrayList<Double>();
         double[] sampleData = new double[sampleGroups.length];
         int[] sampleCount = new int[sampleGroups.length];
         for(int i = 0; i < genesAverage.size(); i++){
            if(genesAverage.get(i).equalsIgnoreCase(g)){
               for(int j = 0; j < sampleGroups.length; j++){
                  dat.put(sampleGroups[j], val);
                  if(groupDesignation.get(i).equalsIgnoreCase(sampleGroups[j])){
                     dat.get(groupDesignation.get(i)).add(averageCtData.get(i));
                     sampleData[j] += averageCtData.get(i);
                     sampleCount[j]++;
                  }
               }
            }
         }
            
         //calculate initial plot data to help find outliers
         double[] plotData = new double[sampleData.length];
         double[] plotAverage = new double[sampleData.length];
         for(int i = 0; i < sampleData.length; i++){
            plotAverage[i] = sampleData[i] / sampleCount[i];
            plotData[i] = Math.pow(2, (normalize[i] - (sampleData[i] / sampleCount[i]))); 
         }
         
         //remove outliers before calculating error
         for(String str : dat.keySet()){
            int indice = 0;
            for(int i = 0; i < sampleGroups.length; i++){
               if(sampleGroups[i].equalsIgnoreCase(str)){
                  indice = i;
               }
            }
            //sorts list in map such that outliers are either the last or the first entry generally
            Collections.sort(dat.get(str));
            if(dat.get(str).get(dat.get(str).size() - 1) > 2 * (dat.get(str).get(dat.get(str).size() - 2))){
               sampleData[indice]-= dat.get(str).get(dat.get(str).size() - 1);
               sampleCount[indice]--;
               dat.get(str).remove(dat.get(str).size() - 1);
            }
            if(2 * dat.get(str).get(0) < dat.get(str).get(1)) {
               sampleData[indice]-= dat.get(str).get(0);
               sampleCount[indice]--;
               dat.get(str).remove(0);
            }      
            
         }
         //recalculate ploting data after removing outliers 
         for(int i = 0; i < sampleData.length; i++){
            plotAverage[i] = sampleData[i] / sampleCount[i];
            plotData[i] = Math.pow(2, (normalize[i] - (sampleData[i] / sampleCount[i]))); 
         }
         
         //calculate error with outliers removed
         double[] error = new double[sampleData.length];
         for(int i = 0; i < sampleData.length; i++){
            for(String key : dat.keySet()){
               if(key.equalsIgnoreCase(sampleGroups[i])){
                  double diffSquared = 0.0;
                  for(Double value : dat.get(key)){
                     diffSquared += Math.pow(value - plotAverage[i], 2);
                  }
                  error[i] = Math.sqrt(diffSquared / dat.get(key).size());    
               }
            }
         }
         
         //ERROR BARS ARENT SMART... the removal of outliers is not working.. fix this
         //then work on the following
         
         //export to excel
         
         
         //write plot images to pdf page
         
         //option to save plots as picture files
         
             
         if(!g.equalsIgnoreCase("hHPRT")){
            StatisticalBarCharter chart = new StatisticalBarCharter(g, sampleGroups, plotData, error);
            chart.pack();
            RefineryUtilities.centerFrameOnScreen(chart);
            chart.setVisible(true);; 
         }
      }
   }   
}