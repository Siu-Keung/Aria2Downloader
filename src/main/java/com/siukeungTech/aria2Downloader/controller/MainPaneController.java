package com.siukeungTech.aria2Downloader.controller;

import com.siukeungTech.aria2Downloader.customControl.AddTaskDialog;
import com.siukeungTech.aria2Downloader.dto.Response;
import com.siukeungTech.aria2Downloader.service.AppService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author Dylan Wei
 * @date 2018-05-24 13:58
 */
public class MainPaneController implements Initializable {
    private AppService appService = AppService.getInstance();

    @FXML
    private TableView<TaskStatus> tasksTable;
    @FXML
    private TableColumn<TaskStatus, Integer> seqCol;
    @FXML
    private TableColumn<TaskStatus, String> fileNameCol;
    @FXML
    private TableColumn<TaskStatus, Double> progressCol;
    @FXML
    private TableColumn<TaskStatus, Double> speedCol;
    @FXML
    private TableColumn<TaskStatus, String> statusCol;
    @FXML
    private TableColumn operationCol;

    @FXML
    private TableView<TaskStatus> leftTable;
    @FXML
    private TableColumn<TaskStatus, String> fileNameCol2;
    @FXML
    private TableColumn<TaskStatus, String> lengthCol;
    @FXML
    private TableColumn<TaskStatus, String> operationCol2;
    private ObservableList<TaskStatus> leftTableItems = FXCollections.observableArrayList();
    private void initializeLeftTable(){
        this.fileNameCol2.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        this.lengthCol.setCellValueFactory(new PropertyValueFactory<>("length"));

        this.leftTable.setRowFactory(table -> {
            return new TableRow(){
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setBackground(null);
                }
            };
        });

        this.lengthCol.setCellFactory(col -> {
            TableCell cell = new TableCell(){
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setBorder(null);
                    if(empty){
                        this.setText(null);
                        this.setGraphic(null);
                    }else{
                        DecimalFormat format = new DecimalFormat("#.##");
                        double length = (double)item / 1024 / 1024;
                        this.setText(format.format(length) + "MB");
                        this.setGraphic(null);
                    }
                }
            };
            return cell;
        });
        this.operationCol2.setCellFactory(col -> {
            return new TableCell(){
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setBorder(null);
                    if(empty){
                        this.setText(null);
                        this.setGraphic(null);
                    }else{
                        Button button = new Button("打开");
                        this.setGraphic(button);
                        this.setText(null);
                    }
                }
            };
        });
        this.fileNameCol2.setCellFactory(col -> {
            return new TableCell(){
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setBorder(null);
                    if(empty){
                        this.setText(null);
                        this.setGraphic(null);
                    }else{
                        this.setText(item.toString());
                        this.setGraphic(null);
                    }
                }
            };
        });
        leftTable.setItems(this.leftTableItems);
    }


    @FXML
    private Button createTaskButton;
    @FXML
    private void onCreateTaskButtonClicked(MouseEvent event){
        AddTaskDialog dialog = new AddTaskDialog();
        Optional<Map<String, String>> result = dialog.showAndWait();
        result.ifPresent(map -> {
            Map<String, String> options = new HashMap<>();
            String fileName = map.get("fileName");
            String url = map.get("url");
            String dst = map.get("dst");
            if(fileName != null && !fileName.isEmpty())
                options.put("out", fileName);
            if(url == null || url.isEmpty())
                throw new RuntimeException("url必须输入！！！");
            if(dst != null && !dst.isEmpty())
                options.put("dir", dst);
            Task<Response<String>> task = new Task<Response<String>>() {
                @Override
                protected Response<String> call() throws Exception {
                    Response<String> response = appService.addTask(url, options);
                    return response;
                }
            };
            task.setOnSucceeded(workerStateEvent -> {
                System.out.println("onSucceeded方法所在的线程：" + Thread.currentThread());
                Response<String> response = task.getValue();
                System.out.println(response);
            });
            new Thread(task).start();
        });


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeLeftTable();
        initializeRightTable();

        Thread thread = new Thread(new PendStatusTask(leftTableItems, rightTableItems));
        thread.setDaemon(true);
        thread.setName("加载任务状态线程");
        thread.start();
    }

    private final ObservableList<TaskStatus> rightTableItems = FXCollections.observableArrayList();
    private void initializeRightTable(){
        tasksTable.setItems(this.rightTableItems);
        this.fileNameCol.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        this.progressCol.setCellValueFactory(new PropertyValueFactory<>("progress"));
        this.speedCol.setCellValueFactory(new PropertyValueFactory<>("speed"));
        this.statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        this.operationCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        this.seqCol.setCellFactory(col -> {
            return new TableCell(){
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty){
                        this.setText(null);
                        this.setGraphic(null);
                    }else{
                        this.setText((this.getIndex() + 1) + "");
                        this.setGraphic(null);
                    }
                }
            };
        });
        this.progressCol.setCellFactory(task -> {
            TableCell<TaskStatus, Double> cell = new TableCell(){
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty){
                        this.setGraphic(null);
                        this.setText(null);
                    }else{
                        ProgressBar progressBar = new ProgressBar((Double) item);
                        progressBar.setPrefWidth(140);
                        this.setGraphic(progressBar);
                        DecimalFormat format = new DecimalFormat("#.#");
                        String percent = format.format((double) item * 100);
                        this.setText(percent + "%");
                    }
                }
            };
            return cell;
        });
        this.speedCol.setCellFactory(col -> {
            return new TableCell(){
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty){
                        this.setText(null);
                        this.setGraphic(null);
                    }else{
                        DecimalFormat format = new DecimalFormat("#.##");
                        if(item == null)
                            item = 0;
                        // TODO: 2018-05-25 奇怪之处
                        String speed = format.format(Double.valueOf(item.toString()) / (1024 * 1024));
                        this.setText(speed + " MB/S");
                        this.setGraphic(null);
                    }
                }
            };
        });
        this.operationCol.setCellFactory(col -> {
            return new TableCell<TaskStatus, String>(){
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty){
                        this.setText(null);
                        this.setGraphic(null);
                    }else{
                        this.setText(null);
                        if(item.equals("active")){
                            Button button = new Button("暂停");
                            this.setGraphic(button);
                            button.setOnMouseClicked(mouseEvent -> {
                                String gid = rightTableItems.get(this.getIndex()).getGid();
                                Task<Response<String>> task = new Task<Response<String>>() {
                                    @Override
                                    protected Response<String> call() throws Exception {
                                        appService.pause(gid);
                                        return null;
                                    }
                                };
                                new Thread(task).start();
                            });
                        }else if(item.equals("paused")){
                            Button button = new Button("开始");
                            this.setGraphic(button);
                            button.setOnMouseClicked(mouseEvent -> {
                                String gid = rightTableItems.get(this.getIndex()).getGid();
                                Task<Response<String>> task = new Task<Response<String>>() {
                                    @Override
                                    protected Response<String> call() throws Exception {
                                        appService.resume(gid);
                                        return null;
                                    }
                                };
                                new Thread(task).start();
                            });
                        }
                    }
                }
            };

        });

    }

    private class PendStatusTask extends Task<Response<List>> {
        private final ObservableList<TaskStatus> rightTableData;
        private final ObservableList<TaskStatus> leftTableData;
        private AppService appService = AppService.getInstance();

        public PendStatusTask(ObservableList<TaskStatus> leftTableData, ObservableList<TaskStatus> rightTableData){
            this.rightTableData = rightTableData;
            this.leftTableData = leftTableData;
        }

        @Override
        protected Response<List> call() throws Exception {
            while(true){
                Response<List<Map<String, Object>>> activeResponse = appService.getActive();
                Response<List<Map<String, Object>>> waitingResponse = appService.getWaiting();
                Response<List<Map<String, Object>>> stoppedResponse = appService.getStopped();
                List<TaskStatus> activeList = this.extractTaskStatus(activeResponse.getResult());
                List<TaskStatus> waitingList = this.extractTaskStatus(waitingResponse.getResult());
                List<TaskStatus> stoppedList = this.extractTaskStatus(stoppedResponse.getResult());
                List<TaskStatus> completedList = new ArrayList<>();
                List<TaskStatus> pausedList = new ArrayList<>();
                Iterator<TaskStatus> iterator = stoppedList.iterator();
                while(iterator.hasNext()){
                    TaskStatus taskStatus = iterator.next();
                    if(taskStatus.getStatus().equals("paused"))
                        pausedList.add(taskStatus);
                    else if(taskStatus.getStatus().equals("complete"))
                        completedList.add(taskStatus);
                }
                this.rightTableData.clear();
                this.rightTableData.addAll(activeList);
                this.rightTableData.addAll(waitingList);
                this.rightTableData.addAll(pausedList);
                this.leftTableData.clear();
                this.leftTableData.addAll(completedList);
                Thread.currentThread().sleep(500);
            }
        }


        private List<TaskStatus> extractTaskStatus(List<Map<String, Object>> results){
            List<TaskStatus> list = new ArrayList<>();
            for(Map<String, Object> map : results){
                String fileName = (String) ((List<Map<String, Object>>)map.get("files")).get(0).get("path");
                fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                Double length = Double.valueOf((String)map.get("totalLength"));
                String gid = (String) map.get("gid");
                Double completedLength = Double.valueOf((String) map.get("completedLength"));
                Double totalLength = Double.valueOf((String) map.get("totalLength"));
                Double progress = totalLength != 0 ? (completedLength / totalLength) : 0;
                Double speed = Double.valueOf((String)map.get("downloadSpeed"));
                String status = (String) map.get("status");
                TaskStatus taskStatus = new TaskStatus(null, fileName, progress, speed, status, length,gid);
                list.add(taskStatus);
            }
            return list;
        }
    }

    public static  class TaskStatus{
        private Integer seq;
        private String fileName;
        private Double progress;
        private Double speed;
        private String status;
        private Double length;

        private String gid;

        @Override
        public String toString() {
            return "TaskStatus{" +
                    "seq=" + seq +
                    ", fileName='" + fileName + '\'' +
                    ", progress=" + progress +
                    ", speed=" + speed +
                    ", status='" + status + '\'' +
                    ", length=" + length +
                    ", gid='" + gid + '\'' +
                    '}';
        }

        public Double getLength() {
            return length;
        }

        public void setLength(Double length) {
            this.length = length;
        }

        public String getGid() {
            return gid;
        }

        public void setGid(String gid) {
            this.gid = gid;
        }

        public TaskStatus() {
        }

        public TaskStatus(Integer seq, String fileName, Double progress, Double speed, String status, Double length, String gid) {
            this.seq = seq;
            this.fileName = fileName;
            this.progress = progress;
            this.speed = speed;
            this.status = status;
            this.length = length;
            this.gid = gid;
        }

        public Integer getSeq() {
            return seq;
        }

        public void setSeq(Integer seq) {
            this.seq = seq;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public Double getProgress() {
            return progress;
        }

        public void setProgress(Double progress) {
            this.progress = progress;
        }

        public Double getSpeed() {
            return speed;
        }

        public void setSpeed(Double speed) {
            this.speed = speed;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

}
