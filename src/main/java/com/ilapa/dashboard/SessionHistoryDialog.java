package com.ilapa.dashboard;

import com.ilapa.database.SessionDao;
import com.ilapa.model.Session;
import com.ilapa.reports.ReportGenerator;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class SessionHistoryDialog {

    private final SessionDao sessionDao = new SessionDao();
    private final ReportGenerator reportGenerator = new ReportGenerator();
    private TableView<Session> table;

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Session History");

        table = new TableView<>();
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        TableColumn<Session, String> appCol = new TableColumn<>("Application");
        appCol.setCellValueFactory(new PropertyValueFactory<>("applicationName"));

        TableColumn<Session, LocalDateTime> startCol = new TableColumn<>("Start Time");
        startCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));

        TableColumn<Session, Long> durationCol = new TableColumn<>("Duration (sec)");
        durationCol.setCellValueFactory(new PropertyValueFactory<>("durationSeconds"));

        table.getColumns().addAll(appCol, startCol, durationCol);

        List<Session> sessions = sessionDao.getAllSessions();
        table.getItems().addAll(sessions);

        Button reportBtn = new Button("Generate Report");
        reportBtn.setOnAction(e -> showReport());

        Button csvBtn = new Button("Export CSV");
        csvBtn.setOnAction(e -> exportCsv(stage));

        Button compareBtn = new Button("Compare Selected Two");
        compareBtn.setOnAction(e -> compareSelected());

        HBox buttons = new HBox(10, reportBtn, csvBtn, compareBtn);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setCenter(table);
        root.setBottom(buttons);

        stage.setScene(new Scene(root, 650, 450));
        stage.show();
    }

    private void showReport() {
        Session selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.INFORMATION, "Select a session first.").showAndWait();
            return;
        }

        String report = reportGenerator.generateSessionReport(selected.getSessionId());

        TextArea area = new TextArea(report);
        area.setEditable(false);
        area.setWrapText(true);

        Stage reportStage = new Stage();
        reportStage.setTitle("Session Report");
        reportStage.setScene(new Scene(new BorderPane(area), 500, 500));
        reportStage.show();
    }

    private void exportCsv(Stage owner) {
        Session selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.INFORMATION, "Select a session first.").showAndWait();
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setInitialFileName("session_" + selected.getSessionId() + ".csv");
        File file = chooser.showSaveDialog(owner);

        if (file != null) {
            try {
                reportGenerator.exportMetricsToCsv(selected.getSessionId(), file.getAbsolutePath());
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Export failed: " + e.getMessage()).showAndWait();
            }
        }
    }

    private void compareSelected() {
        List<Session> selected = table.getSelectionModel().getSelectedItems();
        if (selected.size() != 2) {
            new Alert(Alert.AlertType.INFORMATION, "Select exactly two sessions to compare.").showAndWait();
            return;
        }

        String comparison = reportGenerator.compareSessions(
                selected.get(0).getSessionId(), selected.get(1).getSessionId());

        TextArea area = new TextArea(comparison);
        area.setEditable(false);

        Stage compareStage = new Stage();
        compareStage.setTitle("Session Comparison");
        compareStage.setScene(new Scene(new BorderPane(area), 400, 300));
        compareStage.show();
    }
}
