package com.mybank.gui;

import com.mybank.domain.Bank;
import com.mybank.domain.CheckingAccount;
import com.mybank.domain.SavingsAccount;
import com.mybank.domain.Customer;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.util.Scanner;

public class FXDemo extends Application {

    private Text title;
    private Text details;
    private Text reportText;
    private ComboBox clients;

    @Override
    public void start(Stage primaryStage) {

        BorderPane border = new BorderPane();

        HBox top = addHBox();
        VBox left = addVBox();
        VBox bottom = addBottom();

        border.setTop(top);
        border.setLeft(left);
        border.setBottom(bottom);

        addStackPane(top);

        Scene scene = new Scene(border, 600, 400);

        primaryStage.setTitle("MyBank Clients");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static void loadData(String fileName) {
        try (Scanner sc = new Scanner(new File(fileName))) {

            sc.useLocale(java.util.Locale.US);

            int n = sc.nextInt();

            for (int i = 0; i < n; i++) {

                String first = sc.next();
                String last = sc.next();
                int accCount = sc.nextInt();

                Bank.addCustomer(first, last);
                Customer c = Bank.getCustomer(i);

                for (int j = 0; j < accCount; j++) {

                    String type = sc.next();

                    if (type.equals("S")) {
                        double bal = sc.nextDouble();
                        double rate = sc.nextDouble();
                        c.addAccount(new SavingsAccount(bal, rate));
                    } else {
                        double bal = sc.nextDouble();
                        double od = sc.nextDouble();
                        c.addAccount(new CheckingAccount(bal, od));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public VBox addVBox() {

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(8);

        title = new Text("Client Name");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Line separator = new Line();

        details = new Text("Choose a client...");
        details.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        vbox.getChildren().addAll(title, separator, details);

        return vbox;
    }

    public VBox addBottom() {

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(5);
        vbox.setStyle("-fx-background-color: #eeeeee;");

        Button reportBtn = new Button("Report");
        reportBtn.setPrefWidth(120);

        reportText = new Text("");
        reportText.setFont(Font.font("Arial", FontWeight.NORMAL, 12));

        reportBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < Bank.getNumberOfCustomers(); i++) {

                    Customer c = Bank.getCustomer(i);

                    sb.append(c.getLastName()).append(", ")
                            .append(c.getFirstName()).append("\n");

                    for (int j = 0; j < c.getNumberOfAccounts(); j++) {

                        String type = (c.getAccount(j) instanceof CheckingAccount)
                                ? "Checking"
                                : "Savings";

                        sb.append("  Account #").append(j)
                                .append(" [").append(type).append("] ")
                                .append("Balance: $")
                                .append(c.getAccount(j).getBalance())
                                .append("\n");
                    }

                    sb.append("\n");
                }

                reportText.setText(sb.toString());
            }
        });

        vbox.getChildren().addAll(reportBtn, reportText);

        return vbox;
    }

    public HBox addHBox() {

        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color: #336699;");

        ObservableList<String> items = FXCollections.observableArrayList();

        for (int i = 0; i < Bank.getNumberOfCustomers(); i++) {
            items.add(Bank.getCustomer(i).getLastName() + ", " + Bank.getCustomer(i).getFirstName());
        }

        clients = new ComboBox(items);
        clients.setPrefSize(180, 20);
        clients.setPromptText("Select client...");

        Button buttonShow = new Button("Show");
        buttonShow.setPrefSize(100, 20);

        buttonShow.setOnAction(event -> {

            try {
                int i = clients.getSelectionModel().getSelectedIndex();

                if (i < 0) {
                    throw new Exception("No client selected");
                }

                Customer c = Bank.getCustomer(i);

                title.setText(c.getLastName() + ", " + c.getFirstName());

                StringBuilder sb = new StringBuilder();

                for (int j = 0; j < c.getNumberOfAccounts(); j++) {

                    String type = (c.getAccount(j) instanceof CheckingAccount)
                            ? "Checking"
                            : "Savings";

                    sb.append("Account #").append(j).append("\n")
                            .append("Type: ").append(type).append("\n")
                            .append("Balance: $")
                            .append(c.getAccount(j).getBalance())
                            .append("\n\n");
                }

                details.setText(sb.toString());

            } catch (Exception e) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        });

        Button refreshBtn = new Button("Report");

        hbox.getChildren().addAll(clients, buttonShow, refreshBtn);

        return hbox;
    }

    public void addStackPane(HBox hb) {

        StackPane stack = new StackPane();

        Rectangle helpIcon = new Rectangle(30.0, 25.0);
        helpIcon.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop[]{
                    new Stop(0, Color.web("#4977A3")),
                    new Stop(0.5, Color.web("#B0C6DA")),
                    new Stop(1, Color.web("#9CB6CF"))
                }));

        Text helpText = new Text("?");

        helpIcon.setOnMouseClicked(e -> showAbout());
        helpText.setOnMouseClicked(e -> showAbout());

        stack.getChildren().addAll(helpIcon, helpText);
        stack.setAlignment(Pos.CENTER_RIGHT);
        StackPane.setMargin(helpText, new Insets(0, 10, 0, 0));

        hb.getChildren().add(stack);
        HBox.setHgrow(stack, Priority.ALWAYS);
    }

    private void showAbout() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText(null);
        alert.setContentText("Just a simple JavaFX demo.");
        alert.showAndWait();
    }

    public static void main(String[] args) {
        loadData("test.dat");
        launch(args);
    }
}
