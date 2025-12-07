package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Application class for database management.
 * @author Andrej
 * @version 1.0
 */

public class Aplikacija extends Application {

	private Stage window;
	private FileChooser choice = new FileChooser();
	private TreeView<String> tree;
	private String currTable = null;
	private TableView table = new TableView<>();
	private Database db;
	private BorderPane layout = new BorderPane();
	private File file = null;
	private boolean saved = true;
	private TextArea query = new TextArea();
	private Label message = new Label("");

	/**
	 * Method for javafx graphics
	 * @param stage Main Stage
     * @throws Exception If an error occurs during application startup.
	 */
	@Override
	public void start(Stage stage) throws Exception {
		window = stage;
		window.setTitle("Aplikacija");
		window.setOnCloseRequest(e -> {
			e.consume();
			Exit();
		});
		choice.getExtensionFilters().addAll(new ExtensionFilter(".txt", "*.txt"), new ExtensionFilter(".sql", "*.sql"));

		// meni
		// fajl meni
		Menu fileMenu = new Menu("File");

		MenuItem _new_ = new MenuItem("New");
		_new_.setOnAction(e -> New());
		fileMenu.getItems().add(_new_);

		MenuItem open = new MenuItem("Open");
		open.setOnAction(e -> {
			file = choice.showOpenDialog(window);
			if (file != null)
				Open(file);
		});
		fileMenu.getItems().add(open);

		MenuItem save = new MenuItem("Save");
		save.setOnAction(e -> {
			if (file == null)
				file = choice.showSaveDialog(window);
			if (file != null)
				Save(file);
		});
		fileMenu.getItems().add(save);

		fileMenu.getItems().add(new SeparatorMenuItem());

		MenuItem exit = new MenuItem("Exit");
		exit.setOnAction(e -> Exit());
		fileMenu.getItems().add(exit);

		// tabela meni
		Menu tableMenu = new Menu("Table");

		MenuItem show = new MenuItem("Show");
		show.setOnAction(e -> Show());
		tableMenu.getItems().add(show);

		MenuItem delete = new MenuItem("Delete");
		delete.setOnAction(e -> {
			db.deleteTable(currTable);
			TreeShow();
			saved = false;
		});
		tableMenu.getItems().add(delete);

		// run meni
		Menu runMenu = new Menu("Run");

		MenuItem run = new MenuItem("Run");
		run.setOnAction(e -> Run());
		runMenu.getItems().add(run);

		MenuItem clear = new MenuItem("Clear");
		clear.setOnAction(e -> query.clear());
		runMenu.getItems().add(clear);

		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(fileMenu, tableMenu, runMenu);

		/// Upit
		VBox text = new VBox();
		query.setFont(new Font(20));
		query.setWrapText(true);
		query.setPrefSize(200, 300);
		message.setFont(new Font(15));
		text.getChildren().addAll(query, message);

		layout.setCenter(text);
		layout.setRight(tree);
		layout.setBottom(table);
		layout.setTop(menuBar);

		Scene scena = new Scene(layout, 800, 600);
		window.setScene(scena);
		window.show();
	}

	/**
	 * Create a new database.
	 */
	
	private void New() {
		TextInputDialog dialog = new TextInputDialog("Database");
		dialog.setTitle("Database creation");
		dialog.setHeaderText(null);
		dialog.setContentText("Please enter the name of database:");
		dialog.initStyle(StageStyle.UTILITY);
		dialog.initModality(Modality.APPLICATION_MODAL);

		Optional<String> result = dialog.showAndWait();
		result.ifPresent(name -> {
			db = new Database(name);
			TreeShow();
			saved = false;
		});
	}

	/**
	 * Show the database tree.
	 */
	
	private void TreeShow() {
		tree = new TreeView<String>(db.makeTree());
		tree.setShowRoot(true);
		tree.setMaxWidth(150);
		tree.getSelectionModel().selectedItemProperty().addListener((v, oldVal, newVal) -> {
			if (newVal != null && newVal.getParent() == tree.getRoot()) {
				currTable = newVal.getValue();
			}
		});
		layout.setRight(tree);
	}

	/**
     * Open a database file.
     * @param file The file to open.
     */
	private void Open(File file) {
		Exporter ex;
		String extension = file.toString().substring(file.toString().indexOf('.') + 1);
		if (extension.equals("txt")) {
			ex = new ExporterKekec();
		} else if (extension.equals("sql")) {
			ex = new ExporterSQL();
		} else
			return;
		try {
			db = ex.inport(file);
		} catch (FileNotFoundException e) {
		}
		TreeShow();
		saved = true;
	}

	/**
     * Save the database to a file.
     * @param file The file to save to.
     */
	private void Save(File file) {
		Exporter ex;
		String extension = file.toString().substring(file.toString().indexOf('.') + 1);
		if (extension.equals("txt")) {
			ex = new ExporterKekec();
		} else if (extension.equals("sql")) {
			ex = new ExporterSQL();
		} else
			return;
		try {
			ex.export(file, db);
		} catch (IOException e) {
		}
		saved = true;
	}

	/**
     * Exit the application.
     */
	private void Exit() {
		if (!saved) {
			ButtonType yes = new ButtonType("Save");
			ButtonType no = new ButtonType("Don't Save");

			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Database is not saved!");
			alert.setHeaderText(db.getName() + " is not Saved!");
			alert.setContentText("Do you want to save you're database:");
			alert.initStyle(StageStyle.UTILITY);
			alert.initModality(Modality.APPLICATION_MODAL);
			alert.getButtonTypes().remove(0);
			alert.getButtonTypes().addAll(yes, no);

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == yes) {
				if (file == null) {
					file = choice.showSaveDialog(window);
				}
				if (file != null) {
					Save(file);
				} else
					return;
			} else if (result.get() == no) {
			} else {
				return;
			}
		}
		window.close();

	}

	/**
     * Show the selected table in tableView.
     */
	private void Show() {
		if (currTable == null)
			return;
		table = db.getTable(currTable);
		layout.setBottom(table);
	}

	/**
     * Execute the query, using JNI.
     */
	private void Run() {
		if (db == null)
			return;
		File tmp = new File("DO_NOT_TOUCH.txt");

		// Saljem bazu
		ExporterKekec ex = new ExporterKekec();
		try {
			ex.export(tmp, db);
		} catch (IOException e) {
		}
		tmp = null;

		/// Obradjujem
		String str = query.getText();
		str = str.replace('\n', ' ');
		str = str.replace(";", "");
		String ret = new JNI().execute(str);

		if (!ret.contains("SELECT")) {
			message.setText(" " + ret);
		} else {
			int cnt = 0;
			message.setText(" SELECTED");
			table = new TableView<List<StringProperty>>();
			ObservableList<List<StringProperty>> data = FXCollections.observableArrayList();
			String[] lines = ret.split("\\n");
			for (String line : lines) {
				String[] row = line.split(" ");
				if (line == lines[1]) {
					for (String s : row) {
						final int x = cnt;
						TableColumn<List<StringProperty>, String> col = new TableColumn<>(s);
						col.setCellValueFactory(param -> param.getValue().get(x));
						table.getColumns().add(col);
						cnt++;
					}
				} else if (line == lines[0]) {
					continue;
				} else {
					List<StringProperty> rowP = new ArrayList<StringProperty>();
					for (String s : row) {
						rowP.add(new SimpleStringProperty(s));
					}
					data.add(rowP);
				}
			}
			table.setItems(data);
			table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
			table.setMaxHeight(200);
			layout.setBottom(table);
		}

		// Ucitavam bazu
		tmp = new File("DO_NOT_TOUCH.txt");
		try {
			db = ex.inport(tmp);
		} catch (FileNotFoundException e) {
		}
		TreeShow();
		saved = false;
	}

	/**
     * Main method for launching the application.
     * @param args Command-line arguments.
     */
	public static void main(String[] args) {
		launch(args);
	}

}
