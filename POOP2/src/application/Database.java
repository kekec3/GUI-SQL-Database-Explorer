package application;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.TextFieldTableCell;

public class Database {

	private String name;
	private List<Table> tables = new ArrayList<Table>();

	public Database(String name) {
		this.name = name;
	}

	public void addTabel(Table t) {
		for (Table tin : tables)
			if (tin.getName() == t.getName())
				return;
		tables.add(t);
	}

	public void deleteTable(String name) {
		tables.removeIf((Table t) -> {
			return t.getName().equals(name);
		});
	}

	public String getName() {
		return name;
	}

	public List<Table> getTables() {
		return tables;
	}

	public TreeItem<String> makeTree() {
		TreeItem<String> root = new TreeItem<String>(this.name);
		root.setExpanded(true);
		for (Table tab : tables) {
			TreeItem<String> node = new TreeItem<String>(tab.getName());
			for (String s : tab.getColumns()) {
				TreeItem<String> leaf = new TreeItem<String>(s);
				node.getChildren().add(leaf);
			}
			root.getChildren().add(node);
		}
		return root;
	}

	public TableView<List<StringProperty>> getTable(String name) {
		Table table = null;
		for (Table t : tables) {
			if (name.equals(t.getName())) {
				table = t;
				break;
			}
		}
		if (table == null)
			return null;

		TableView<List<StringProperty>> tab = new TableView<List<StringProperty>>();
		List<String> columns = table.getColumns();
		for (String s : columns) {
			TableColumn<List<StringProperty>, String> col = new TableColumn<>(s);
			col.setCellValueFactory(param -> param.getValue().get(columns.indexOf(s)));
			tab.getColumns().add(col);
		}
		ObservableList<List<StringProperty>> data = FXCollections.observableArrayList();
		for (List<String> row : table.getData()) {
			List<StringProperty> rowP = new ArrayList<StringProperty>();
			for (String s : row)
				rowP.add(new SimpleStringProperty(s));
			data.add(rowP);
		}
		tab.setItems(data);

		tab.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

		tab.setMaxHeight(200);

		return tab;
	}
}
