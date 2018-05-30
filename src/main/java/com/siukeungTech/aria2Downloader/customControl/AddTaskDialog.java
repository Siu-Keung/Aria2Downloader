package com.siukeungTech.aria2Downloader.customControl;

import com.pepperonas.fxiconics.FxIconicsButton;
import com.pepperonas.fxiconics.FxIconicsLabel;
import com.pepperonas.fxiconics.awf.FxFontAwesome;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dylan Wei
 * @date 2018-05-28 09:24
 */
public class AddTaskDialog extends Dialog<Map<String, String>> {
    private final GridPane pane;
    private final TextField urlInput;
    private final Label urlLabel;
    private final TextField fileNameInput;
    private final Label fileNameLabel;
    private final Label dstLabel;
    private final TextField dstInput;
    private final Label chooseFileLabel;

    public AddTaskDialog() {
        this.setTitle("新增任务");
        this.setHeaderText("请输入任务信息：");
        ButtonType confirmButton = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, confirmButton);
        pane = new GridPane();
        urlLabel = new Label("请输入下载链接：");
        urlInput = new TextField();
        fileNameLabel = new Label("文件名：");
        fileNameInput = new TextField();
        urlInput.textProperty().addListener((textProperty, oldValue, newValue)-> {
            String fileName = newValue.substring(newValue.lastIndexOf("/") + 1);
            fileNameInput.setText(fileName);
        });

        dstLabel = new Label("请输入保存路径：");
        chooseFileLabel = (Label) new FxIconicsLabel.Builder(FxFontAwesome.Icons.faw_folder_open_o).size(20).build();
        dstInput = new TextField();
        chooseFileLabel.setOnMouseClicked(event -> {
            DirectoryChooser chooser = new DirectoryChooser();
            File saveDir = chooser.showDialog(null);
            try {
                dstInput.setText(saveDir.getCanonicalPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        pane.addRow(0, urlLabel, urlInput);
        pane.addRow(1, fileNameLabel, fileNameInput);
        pane.addRow(2, dstLabel, dstInput, chooseFileLabel);
        pane.setVgap(10);
        pane.setHgap(20);
        this.getDialogPane().setContent(pane);
        this.setResultConverter(buttonType -> {
            if (buttonType == confirmButton) {
                Map<String, String> map = new HashMap<>();
                map.put("fileName", fileNameInput.getText());
                map.put("url", urlInput.getText());
                map.put("dst", dstInput.getText());
                return map;
            } else
                return null;
        });
        urlInput.setPrefWidth(300);
        pane.setPrefWidth(500);

    }


}
