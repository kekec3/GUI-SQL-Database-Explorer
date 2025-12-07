package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class Exporter {
	public abstract void export(File file, Database db) throws IOException;
	public abstract Database inport(File file) throws FileNotFoundException;
}
