package application;

import java.util.ArrayList;
import java.util.List;

public class Table {

	private String name;
	private List<String> columns = new ArrayList<String>();
	private List<List<String>> data = new ArrayList<List<String>>();

	public Table(String Name, List<String> columns) {
		this.name = Name;
		this.columns = columns;
	}

	public void addRow(List<String> row) {
		data.add(row);
	}

	public List<String> getColumns(){
		return columns;
	}
	
	public List<List<String>> getData() {
		return data;
	}

	public String getName() {
		return name;
	}
}
