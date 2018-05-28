package com.siukeungTech.aria2Downloader.customControl;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dylan Wei
 * @date 2018-05-28 09:24
 */
public class AddTaskDialog extends Dialog<Map<String, String>> {

    public AddTaskDialog(){
        this.setTitle("新增任务");
        this.setHeaderText("请输入任务信息：");
        ButtonType confirmButton = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, confirmButton);
        GridPane pane = new GridPane();
        Label fileNameLabel = new Label("文件名：");
        TextField fileNameInput = new TextField();
        Label urlLabel = new Label("请输入下载链接：");
        TextField urlInput = new TextField();
        Label dstLabel = new Label("请输入保存路径：");
        Button chooseFileButton = new Button("open");
        TextField dstInput = new TextField();
        chooseFileButton.setOnMouseClicked(event -> {
            DirectoryChooser chooser = new DirectoryChooser();
            File saveDir = chooser.showDialog(null);
            try {
                dstInput.setText(saveDir.getCanonicalPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        pane.addRow(0, fileNameLabel, fileNameInput);
        pane.addRow(1, urlLabel, urlInput);
        pane.addRow(2, dstLabel, dstInput, chooseFileButton);
        pane.setVgap(10);
        pane.setHgap(20);
        this.getDialogPane().setContent(pane);
        this.setResultConverter(buttonType -> {
            if(buttonType == confirmButton){
                Map<String, String> map = new HashMap<>();
                map.put("fileName", fileNameInput.getText());
                map.put("url", urlInput.getText());
                map.put("dst", dstInput.getText());
                return map;
            }
            else
                return null;
        });
    }



}
