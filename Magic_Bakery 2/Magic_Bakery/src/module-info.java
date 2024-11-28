module Magic_Bakery {
	requires javafx.controls;
	requires javafx.fxml;
	
	opens application to javafx.graphics, javafx.fxml;
	opens Main to javafx.graphics, javafx.fxml;
}
