package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ExporterSQL extends Exporter {


	@Override
	public void export(File file, Database db) throws IOException {
		FileWriter writer = new FileWriter(file);
		for (Table t : db.getTables()) {
			writer.write("CREATE TABLE " + t.getName());
			writer.write(" (");
			StringBuilder sb = new StringBuilder();
			for (String col : t.getColumns()) {
				if (col != t.getColumns().get(0)) {
					sb.append(",");
				}
				sb.append(col);
			}
			writer.write(sb.toString());
			writer.write(");\n");
			writer.write("INSERT INTO " + t.getName());
			writer.write(" (");
			writer.write(sb.toString());
			writer.write(") VALUES");
			for (List<String> row : t.getData()) {
				if (row != t.getData().get(0)) writer.write(",");
				writer.write(" (");
				for (String s : row) {
					if (s != row.get(0)) {
						writer.write(",");
					}
					writer.write(s);
				}
				writer.write(")");
			}
			writer.write(";\n");
		}
		writer.flush();
		writer.close();
	}

	@Override
	public Database inport(File file) throws FileNotFoundException {
		//Database db = new Database(file.getName().substring(0, file.getName().indexOf(".")));
		Database db = new Database("SQL");
		Scanner reader = new Scanner(file);
		
		File tmp = new File("DO_NOT_TOUCH.txt");
		ExporterKekec ex = new ExporterKekec();
		try {
			ex.export(tmp, db);
		} catch (IOException e) {
		}
		tmp = null;
		
		while (reader.hasNextLine()) {
			String s = reader.nextLine();
			s = s.replace('\n', ' ');
			s = s.replace(";", "");
			s = s.replaceAll(" ,", ",");
			new JNI().execute(s);
		}
		
		tmp = new File("DO_NOT_TOUCH.txt");
		try {
			db = ex.inport(tmp);
		} catch (FileNotFoundException e) {
		}
		
		return db;
	}

}
