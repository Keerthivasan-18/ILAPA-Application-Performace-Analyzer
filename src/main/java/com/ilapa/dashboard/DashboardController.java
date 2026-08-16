package com.ilapa.dashboard;

import com.ilapa.analysis.AnalysisEngine;
import com.ilapa.database.EventDao;
import com.ilapa.database.MetricDao;
import com.ilapa.database.SessionDao;
import com.ilapa.model.MetricSample;
import com.ilapa.model.PerformanceEvent;
import com.ilapa.model.Session;
import com.ilapa.monitor.MonitoringEngine;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import oshi.software.os.OSProcess;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

public class DashboardController {

    private static final int MAX_CHART_POINTS = 60;

    private final MonitoringEngine monitoringEngine = new MonitoringEngine();
    private final SessionDao sessionDao = new SessionDao();
    private final MetricDao metricDao = new MetricDao();
    private final EventDao eventDao = new EventDao();

    private AnalysisEngine analysisEngine = new AnalysisEngine();
    private Session currentSession;

    private ComboBox<String> processCombo;
    private Button startBtn;
    private Button stopBtn;
    private Label cpuLabel;
    private Label memoryLabel;
    private Label threadsLabel;
    private Label networkLabel;
    private Label scoreLabel;
    private ListView<String> eventsList;

    private XYChart.Series<Number, Number> cpuSeries;
    private XYChart.Series<Number, Number> memorySeries;
    private int tickCounter = 0;

    public void show(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        root.setTop(buildTopBar());
        root.setCenter(buildCenter());
        root.setBottom(buildBottomBar());

        Scene scene = new Scene(root, 1000, 650);
        stage.setTitle("ILAPA - Application Performance Analyzer");
        stage.setScene(scene);
        stage.show();

        refreshProcessList();
    }

    private HBox buildTopBar() {
        processCombo = new ComboBox<>();
        processCombo.setPrefWidth(300);

        Button refreshBtn = new Button("Refresh List");
        refreshBtn.setOnAction(e -> refreshProcessList());

        startBtn = new Button("Start Monitoring");
        startBtn.setOnAction(e -> startMonitoring());

        stopBtn = new Button("Stop Monitoring");
        stopBtn.setDisable(true);
        stopBtn.setOnAction(e -> stopMonitoring());

        HBox bar = new HBox(10, new Label("Application:"), processCombo, refreshBtn, startBtn, stopBtn);
        bar.setPadding(new Insets(0, 0, 10, 0));
        return bar;
    }

    private void refreshProcessList() {
        // using a set so we dont end up with the same process name listed 20 times
        Set<String> names = new LinkedHashSet<>();
        for (OSProcess p : monitoringEngine.getRunningProcesses()) {
            names.add(p.getName());
        }
        processCombo.getItems().setAll(names);
    }

    private VBox buildCenter() {
        NumberAxis cpuXAxis = new NumberAxis();
        NumberAxis cpuYAxis = new NumberAxis(0, 100, 10);
        LineChart<Number, Number> cpuChart = new LineChart<>(cpuXAxis, cpuYAxis);
        cpuChart.setTitle("CPU Usage (%)");
        cpuChart.setAnimated(false);
        cpuChart.setCreateSymbols(false);
        cpuSeries = new XYChart.Series<>();
        cpuChart.getData().add(cpuSeries);

        NumberAxis memXAxis = new NumberAxis();
        NumberAxis memYAxis = new NumberAxis();
        LineChart<Number, Number> memChart = new LineChart<>(memXAxis, memYAxis);
        memChart.setTitle("Memory Usage (MB)");
        memChart.setAnimated(false);
        memChart.setCreateSymbols(false);
        memorySeries = new XYChart.Series<>();
        memChart.getData().add(memorySeries);

        VBox charts = new VBox(10, cpuChart, memChart);

        cpuLabel = new Label("CPU: --");
        memoryLabel = new Label("Memory: --");
        threadsLabel = new Label("Threads: --");
        networkLabel = new Label("Network: --");
        scoreLabel = new Label("Performance Score: --");
        scoreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        VBox liveStats = new VBox(6, cpuLabel, memoryLabel, threadsLabel, networkLabel, new Separator(), scoreLabel);
        liveStats.setPadding(new Insets(10));

        eventsList = new ListView<>();
        eventsList.setPrefHeight(200);

        VBox rightPanel = new VBox(10, new Label("Live Stats"), liveStats, new Label("Detected Events"), eventsList);
        rightPanel.setPrefWidth(280);
        rightPanel.setPadding(new Insets(0, 0, 0, 10));

        HBox center = new HBox(10, charts, rightPanel);
        HBox.setHgrow(charts, Priority.ALWAYS);

        return new VBox(center);
    }

    private HBox buildBottomBar() {
        Button historyBtn = new Button("View Past Sessions");
        historyBtn.setOnAction(e -> new SessionHistoryDialog().show());

        HBox bar = new HBox(10, historyBtn);
        bar.setPadding(new Insets(10, 0, 0, 0));
        return bar;
    }

    private void startMonitoring() {
        String appName = processCombo.getValue();
        if (appName == null || appName.isBlank()) {
            showAlert("Pick an application from the list first.");
            return;
        }

        boolean attached = monitoringEngine.attachToApplication(appName);
        if (!attached) {
            showAlert("Could not find a running process with that name. Try refreshing the list.");
            return;
        }

        currentSession = new Session(appName);
        long sessionId = sessionDao.insertSession(currentSession);
        currentSession.setSessionId(sessionId);

        analysisEngine = new AnalysisEngine();
        cpuSeries.getData().clear();
        memorySeries.getData().clear();
        eventsList.getItems().clear();
        tickCounter = 0;

        monitoringEngine.start(sessionId, sample -> Platform.runLater(() -> handleSample(sample)));

        startBtn.setDisable(true);
        stopBtn.setDisable(false);
        processCombo.setDisable(true);
    }

    private void stopMonitoring() {
        monitoringEngine.stop();

        if (currentSession != null) {
            LocalDateTime end = LocalDateTime.now();
            long durationSeconds = Duration.between(currentSession.getStartTime(), end).getSeconds();
            sessionDao.closeSession(currentSession.getSessionId(), end, durationSeconds);
        }

        startBtn.setDisable(false);
        stopBtn.setDisable(true);
        processCombo.setDisable(false);
    }

    private void handleSample(MetricSample sample) {
        cpuLabel.setText(String.format("CPU: %.1f%%", sample.getCpuUsage()));
        memoryLabel.setText("Memory: " + sample.getMemoryUsageMb() + " MB");
        threadsLabel.setText("Threads: " + sample.getThreadCount());
        networkLabel.setText(String.format("Network: %d KB/s down, %d KB/s up",
                sample.getNetworkReceived() / 1024, sample.getNetworkSent() / 1024));

        tickCounter++;
        cpuSeries.getData().add(new XYChart.Data<>(tickCounter, sample.getCpuUsage()));
        memorySeries.getData().add(new XYChart.Data<>(tickCounter, sample.getMemoryUsageMb()));

        // keep charts from growing forever, only show the last minute or so
        if (cpuSeries.getData().size() > MAX_CHART_POINTS) {
            cpuSeries.getData().remove(0);
        }
        if (memorySeries.getData().size() > MAX_CHART_POINTS) {
            memorySeries.getData().remove(0);
        }

        metricDao.insertMetric(sample);

        for (PerformanceEvent event : analysisEngine.processSample(sample)) {
            eventDao.insertEvent(event);
            eventsList.getItems().add(0, "[" + event.getEventType() + "] " + event.getDescription());
        }

        scoreLabel.setText("Performance Score: " + analysisEngine.getCurrentScore() + " / 100");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.showAndWait();
    }
}
