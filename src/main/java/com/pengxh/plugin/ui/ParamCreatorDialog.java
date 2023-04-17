package com.pengxh.plugin.ui;

import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Set;

public class ParamCreatorDialog extends DialogWrapper {
    private JPanel contentPane;
    private JButton copyButton;
    private JButton generateButton;
    private JTextArea jsonTextArea;
    private JTextArea paramsTextArea;
    private JRadioButton jsRadioButton;
    private JRadioButton javaRadioButton;
    private JRadioButton kotlinRadioButton;

    public ParamCreatorDialog(@Nullable Project project) {
        super(project);
        init();
        setTitle("形参构造器");

        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (jsonTextArea.getText().isBlank()) {
                    Messages.showErrorDialog("参数为空，无法生成形参", "温馨提示");
                    return;
                }

                JSONObject jsonObject = JSONObject.parseObject(jsonTextArea.getText());

                Set<String> keySet = jsonObject.keySet();
                if (keySet.isEmpty() || keySet.contains("")) {
                    Messages.showErrorDialog("JSON含有空的Key，无法生成形参", "温馨提示");
                    return;
                }

                if (jsRadioButton.isSelected()) {
                    generateJavaScriptParam(jsonObject, keySet);
                } else if (javaRadioButton.isSelected()) {
                    generateJavaParam(jsonObject, keySet);
                } else if (kotlinRadioButton.isSelected()) {
                    generateKotlinParam(jsonObject, keySet);
                } else {
                    throw new IllegalArgumentException();
                }
            }
        });

        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (paramsTextArea.getText().isBlank()) {
                    Messages.showErrorDialog("无法复制空参数", "温馨提示");
                    return;
                }
                //复制到剪贴板
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                //封装为粘贴板对象
                Transferable ts = new StringSelection(paramsTextArea.getText());
                clipboard.setContents(ts, null);
                Messages.showInfoMessage("形参复制成功", "温馨提示");
            }
        });
    }

    private void generateJavaScriptParam(JSONObject jsonObject, Set<String> keySet) {
        //保存Json里面的Key
        ArrayList<String> keys = new ArrayList<>(keySet);

        //判断Json里面的Value类型
        ArrayList<String> types = new ArrayList<>();

        //TODO 生成JavaScript形参
        Messages.showInfoMessage("未实现", "温馨提示");
    }

    private void generateJavaParam(JSONObject jsonObject, Set<String> keySet) {
        //保存Json里面的Key
        ArrayList<String> keys = new ArrayList<>(keySet);

        //判断Json里面的Value类型
        ArrayList<String> types = new ArrayList<>();

        jsonObject.values().forEach(value -> {
            if (value.toString().isEmpty()) {
                types.add("String");
            } else {
                if (value instanceof String) {
                    types.add("String");
                } else if (value instanceof Short) {
                    types.add("short");
                } else if (value instanceof Integer) {
                    types.add("int");
                } else if (value instanceof Long) {
                    types.add("long");
                } else if (value instanceof Float) {
                    types.add("float");
                } else if (value instanceof Double) {
                    types.add("double");
                } else if (value instanceof Boolean) {
                    types.add("boolean");
                } else {
                    types.add("String[]");
                }
            }
        });

        //组合成请求参数
        StringBuilder paramBuilder = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            StringBuilder builder = new StringBuilder();
            builder.append(types.get(i)).append(" ").append(keys.get(i));
            if (i != keys.size() - 1) {
                builder.append(",").append("\r\n");
            }
            paramBuilder.append(builder);
        }
        paramsTextArea.setText(paramBuilder.toString());
    }

    private void generateKotlinParam(JSONObject jsonObject, Set<String> keySet) {
        //保存Json里面的Key
        ArrayList<String> keys = new ArrayList<>(keySet);

        //判断Json里面的Value类型
        ArrayList<String> types = new ArrayList<>();
        jsonObject.values().forEach(value -> {
            if (value.toString().isEmpty()) {
                types.add("String");
            } else {
                if (value instanceof String) {
                    types.add("String");
                } else if (value instanceof Short) {
                    types.add("Short");
                } else if (value instanceof Integer) {
                    types.add("Integer");
                } else if (value instanceof Long) {
                    types.add("Long");
                } else if (value instanceof Float) {
                    types.add("Float");
                } else if (value instanceof Double) {
                    types.add("Double");
                } else if (value instanceof Boolean) {
                    types.add("Boolean");
                } else {
                    types.add("Array<String>");
                }
            }
        });

        //组合成请求参数
        StringBuilder paramBuilder = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            StringBuilder builder = new StringBuilder();
            builder.append(keys.get(i)).append(": ").append(types.get(i));
            if (i != keys.size() - 1) {
                builder.append(",").append("\r\n");
            }
            paramBuilder.append(builder);
        }
        paramsTextArea.setText(paramBuilder.toString());
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        contentPane.setPreferredSize(new Dimension(700, 700));
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(jsRadioButton);
        buttonGroup.add(javaRadioButton);
        buttonGroup.add(kotlinRadioButton);
        return contentPane;
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[]{new CancelAction()};
    }

    /**
     * 自定义默认按钮Action，去掉OK按钮
     */
    private class CancelAction extends DialogWrapperAction {
        public CancelAction() {
            super("Cancel");
        }

        @Override
        protected void doAction(ActionEvent e) {
            close(CANCEL_EXIT_CODE);
        }
    }
}
