
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class CarParkingManagementSystem extends Application {
    private Car[] cars = new Car[10];
    private int count = 0;
    private TextArea displayArea;
    private String password = null;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Car Parking Management");
        primaryStage.setResizable(false);

        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane, 600, 400);

        displayArea = new TextArea();
        displayArea.setEditable(false);
        displayArea.setWrapText(true);
        displayArea.setStyle("-fx-font-family: 'Courier New';-fx-font-size: 14px; -fx-padding: 10px;");
        borderPane.setCenter(displayArea);

        HBox buttonHBox = new HBox(200);
        buttonHBox.setPadding(new Insets(10, 20, 10, 20));

        VBox leftButtonVBox = new VBox(10);
        Button parkButton = createStyledButton("Park Car");
        Button displayButton = createStyledButton("Display Cars");

        leftButtonVBox.getChildren().addAll(parkButton, displayButton);

        VBox rightButtonVBox = new VBox(10);
        Button removeCarButton = createStyledButton("Remove Car");
        Button setPasswordButton = createStyledButton("Password");

        rightButtonVBox.getChildren().addAll(removeCarButton, setPasswordButton);

        buttonHBox.getChildren().addAll(leftButtonVBox, rightButtonVBox);

        borderPane.setBottom(buttonHBox);

        parkButton.setOnAction(e -> {
            clearScreen();
            parkCar();
        });
        displayButton.setOnAction(e -> {
            clearScreen();
            if (password == null || checkPassword()) {
                displayCars();
            }
        });
        setPasswordButton.setOnAction(e -> {
            clearScreen();
            setPassword();
        });
        removeCarButton.setOnAction(e -> {
            clearScreen();
            if (password == null || checkPassword()) {
                removeCar();
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createStyledButton(String label) {
        Button button = new Button(label);
        button.setStyle("-fx-background-color: #3F51B5; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 18px; -fx-border-radius: 4px;");
        button.setPrefWidth(200);
        return button;
    }

    private void clearScreen() {
        displayArea.clear();
    }

    private void parkCar() {
        TextInputDialog regDialog = createInputDialog("Park Car", "Enter License plate of the Vehicle:");
        String licensePlate = regDialog.showAndWait().orElse("").trim();

        if (licensePlate.isEmpty()) {
            showAlert("Error", "License plate cannot be empty.");
            return;
        }

        for (int i = 0; i < count; i++) {
            if (cars[i].getLicensePlate().equals(licensePlate)) {
                showAlert("Error", "This car is already parked.");
                return;
            }
        }

        TextInputDialog modelDialog = createInputDialog("Park Car", "Enter car model:");
        String model = modelDialog.showAndWait().orElse("").trim();

        if (model.isEmpty()) {
            showAlert("Error", "Car model cannot be empty.");
            return;
        }

        if (parkCar(new Car(licensePlate, model))) {
            showAlert("Success", "Car parked successfully.");
        } else {
            showAlert("Error", "Parking lot is full. Cannot park the car.");
        }
    }

    private boolean parkCar(Car car) {
        if (count < cars.length) {
            cars[count++] = car;
            return true;
        }
        return false;
    }

    private void displayCars() {
        StringBuilder carsInfo = new StringBuilder("Cars in the parking lot:\n");
        for (Car car : cars) {
            if (car != null) {
                carsInfo.append(car).append("\n");
            }
        }
        displayArea.setText(carsInfo.toString());
    }

    private boolean checkPassword() {
        TextInputDialog dialog = createInputDialog("Password Required", "Enter password:");
        String enteredPassword = dialog.showAndWait().orElse("");
        if (password.equals(enteredPassword)) {
            return true;
        } else {
            showAlert("Error", "Invalid password.");
            return false;
        }
    }

    private void setPassword() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(password == null ? "Create Password" : "Change Password", "Create Password", "Change Password");
        dialog.setTitle("Set Password");
        dialog.setHeaderText(null);
        dialog.setContentText("Choose an option:");

        dialog.showAndWait().ifPresent(choice -> {
            if ("Create Password".equals(choice)) {
                if (password != null) {
                    showAlert("Error", "Password has already been set.");
                } else {
                    createPassword();
                }
            } else if ("Change Password".equals(choice)) {
                changePassword();
            }
        });
    }

    private void createPassword() {
        String newPassword = promptForPassword("Create Password", "Enter new password:");
        if (newPassword != null) {
            String confirmPassword = promptForPassword("Create Password", "Confirm new password:");
            if (newPassword.equals(confirmPassword)) {
                password = newPassword;
                showAlert("Success", "Password created successfully.");
            } else {
                showAlert("Error", "Passwords do not match.");
            }
        }
    }

    private void changePassword() {
        if (password == null) {
            showAlert("Error", "Please create a password first.");
            return;
        }
        String oldPassword = promptForPassword("Change Password", "Enter current password:");
        if (!password.equals(oldPassword)) {
            showAlert("Error", "Current password is incorrect.");
            return;
        }
        String newPassword = promptForPassword("Change Password", "Enter new password:");
        if (newPassword != null) {
            String confirmPassword = promptForPassword("Change Password", "Confirm new password:");
            if (newPassword.equals(confirmPassword)) {
                password = newPassword;
                showAlert("Success", "Password changed successfully.");
            } else {
                showAlert("Error", "Passwords do not match.");
            }
        }
    }

    private String promptForPassword(String title, String content) {
        TextInputDialog dialog = createInputDialog(title, content);
        return dialog.showAndWait().orElse("");
    }

    private TextInputDialog createInputDialog(String title, String content) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(content);
        dialog.getDialogPane().setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14px;");
        return dialog;
    }

    private void removeCar() {
        TextInputDialog regDialog = createInputDialog("Remove Car", "Enter License plate of the Vehicle to remove:");
        String licensePlate = regDialog.showAndWait().orElse("").trim();

        if (licensePlate.isEmpty()) {
            showAlert("Error", "License plate cannot be empty.");
            return;
        }

        boolean found = false;
        for (int i = 0; i < count; i++) {
            if (cars[i].getLicensePlate().equals(licensePlate)) {
                found = true;
                for (int j = i; j < count - 1; j++) {
                    cars[j] = cars[j + 1];
                }
                cars[count - 1] = null;
                count--;
                showAlert("Success", "Car removed successfully.");
                break;
            }
        }

        if (!found) {
            showAlert("Error", "Car with given license plate not found.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14px;");
        alert.showAndWait();
    }

    private static class Car {
        private final String licensePlate;
        private final String model;

        public Car(String licensePlate, String model) {
            this.licensePlate = licensePlate;
            this.model = model;
        }

        public String getLicensePlate() {
            return licensePlate;
        }

        public String getModel() {
            return model;
        }

        @Override
        public String toString() {
            return "Licence Plate: " + licensePlate + ", Model: " + model;
        }
    }
}
