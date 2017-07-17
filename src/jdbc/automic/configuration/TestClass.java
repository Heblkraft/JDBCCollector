package jdbc.automic.configuration;

import java.io.File;
import java.io.IOException;

public class TestClass {
	
	public static void main(String[] args) {
		DummyConfiguration config = new DummyConfiguration("./kek.txt");
		config.load();
	}
}
