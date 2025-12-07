package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ExporterKekec extends Exporter {

	@Override
	public void export(File file, Database db) throws IOException {
		FileWriter writer = new FileWriter(file);
		writer.write(db.getName()+"\n");
		for (Table t : db.getTables()) {
			writer.write(t.getName()+" (");
			for (String s: t.getColumns()) {
				if (s != t.getColumns().get(0)) writer.write(",");
				writer.write(s);
			}
			writer.write(")");
			for (List<String> row : t.getData()) {
				writer.write(" (");
				for (String s: row) {
					if (s != row.get(0)) writer.write(",");
					writer.write(s);
				}
				writer.write(")");
			}
			writer.write("\n");
		}
		writer.flush();
		writer.close();
	}

	@Override
	public Database inport(File file) throws FileNotFoundException {
		Scanner reader = new Scanner(file);
		Database db = new Database(reader.nextLine());
		while (reader.hasNextLine()) {
			Scanner slicer = new Scanner(reader.nextLine());
			String name = slicer.next();
			Scanner columnsSlicer = new Scanner(slicer.next());
			columnsSlicer.useDelimiter("[(), ]+");
			List<String> columns = new ArrayList<String>();
			while (columnsSlicer.hasNext()) columns.add(columnsSlicer.next());
			Table tab = new Table(name, columns);
			while (slicer.hasNext()) {
				columnsSlicer = new Scanner(slicer.next());
				columnsSlicer.useDelimiter("[(), ]+");
				columns = new ArrayList<String>();
				while (columnsSlicer.hasNext()) columns.add(columnsSlicer.next());
				tab.addRow(columns);
			}
			db.addTabel(tab);
			slicer.close();
		}
		reader.close();
		return db;
	}
}
