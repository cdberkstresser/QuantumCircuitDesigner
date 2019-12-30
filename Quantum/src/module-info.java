module quantum {
	requires javafx.base;
	requires transitive javafx.graphics;
	requires javafx.fxml;
	requires javafx.controls;
	requires java.xml;

	exports application to javafx.graphics;

	opens controller to javafx.fxml, javafx.base;
	opens model to javafx.base;
}