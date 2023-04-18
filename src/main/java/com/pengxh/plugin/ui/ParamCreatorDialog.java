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
import java.util.Arrays;
import java.util.Set;

public class ParamCreatorDialog extends DialogWrapper {
    private JPanel contentPane;
    private JButton generateButton;
    private JTextArea jsonTextArea;
    private JTextArea paramsTextArea;
    private JTextArea requestTextArea;
    private JRadioButton jsRadioButton;
    private JRadioButton javaRadioButton;
    private JRadioButton kotlinRadioButton;
    private JRadioButton gsonRadioButton;
    private JRadioButton fastJSONRadioButton;
    private JButton copyParamButton;
    private JButton copyBodyButton;
    private JCheckBox integerCheckBox;
    private JCheckBox booleanCheckBox;

    public ParamCreatorDialog(@Nullable Project project) {
        super(project);
        init();
        setTitle("形参构造器");
        setModal(false);

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

        copyParamButton.addActionListener(new ActionListener() {
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
                Messages.showInfoMessage("形参复制成功，无需关闭对话框", "温馨提示");
            }
        });

        copyBodyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (requestTextArea.getText().isBlank()) {
                    Messages.showErrorDialog("无法复制空RequestBody", "温馨提示");
                    return;
                }
                //复制到剪贴板
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                //封装为粘贴板对象
                Transferable ts = new StringSelection(requestTextArea.getText());
                clipboard.setContents(ts, null);
                Messages.showInfoMessage("RequestBody复制成功，无需关闭对话框", "温馨提示");
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

        //保存Json里面的值
        Object[] objects = jsonObject.values().toArray();
        ArrayList<Object> values = new ArrayList<>(Arrays.asList(objects));

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
                    if (integerCheckBox.isSelected()) {
                        types.add("String");
                    } else {
                        types.add("int");
                    }
                } else if (value instanceof Long) {
                    types.add("long");
                } else if (value instanceof Float) {
                    types.add("float");
                } else if (value instanceof Double) {
                    types.add("double");
                } else if (value instanceof Boolean) {
                    if (booleanCheckBox.isSelected()) {
                        types.add("String");
                    } else {
                        types.add("boolean");
                    }
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

        //生成RequestBody
        if (gsonRadioButton.isSelected()) {
            generateJavaGsonRequestBody(keys, values);
        } else if (fastJSONRadioButton.isSelected()) {
            generateJavaFastJsonRequestBody(keys);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private void generateJavaGsonRequestBody(ArrayList<String> keys, ArrayList<Object> values) {
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("JsonObject param = new JsonObject();").append("\r\n");
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            Object o = values.get(i);
            if (o instanceof String || o instanceof Short || o instanceof Integer || o instanceof Long ||
                    o instanceof Float || o instanceof Double || o instanceof Boolean) {
                bodyBuilder.append("param.addProperty(\"").append(key).append("\", ").append(key).append(");").append("\r\n");
            } else {
                bodyBuilder.append("param.add(\"").append(key).append("\", ").append("new Gson().toJsonTree(").append(key).append(", new TypeToken<String[]>() {}.getType()).getAsJsonArray()").append(");").append("\r\n");
            }
        }
        bodyBuilder.append("MediaType mediaType = MediaType.parse(\"application/json; charset=utf-8\");").append("\r\n");
        bodyBuilder.append("RequestBody body = RequestBody.create(mediaType, param.toString());").append("\r\n");
        requestTextArea.setText(bodyBuilder.toString());
    }

    private void generateJavaFastJsonRequestBody(ArrayList<String> keys) {
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("JSONObject param = new JSONObject();").append("\r\n");
        for (String key : keys) {
            bodyBuilder.append("param.put(\"").append(key).append("\", ").append(key).append(");").append("\r\n");
        }
        bodyBuilder.append("MediaType mediaType = MediaType.parse(\"application/json; charset=utf-8\");").append("\r\n");
        bodyBuilder.append("RequestBody body = RequestBody.create(mediaType, param.toString());").append("\r\n");
        requestTextArea.setText(bodyBuilder.toString());
    }

    private void generateKotlinParam(JSONObject jsonObject, Set<String> keySet) {
        //保存Json里面的Key
        ArrayList<String> keys = new ArrayList<>(keySet);

        //保存Json里面的值
        Object[] objects = jsonObject.values().toArray();
        ArrayList<Object> values = new ArrayList<>(Arrays.asList(objects));

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
                    if (integerCheckBox.isSelected()) {
                        types.add("String");
                    } else {
                        types.add("Int");
                    }
                } else if (value instanceof Long) {
                    types.add("Long");
                } else if (value instanceof Float) {
                    types.add("Float");
                } else if (value instanceof Double) {
                    types.add("Double");
                } else if (value instanceof Boolean) {
                    if (booleanCheckBox.isSelected()) {
                        types.add("String");
                    } else {
                        types.add("Boolean");
                    }
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

        //生成RequestBody
        if (gsonRadioButton.isSelected()) {
            generateKotlinGsonRequestBody(keys, values);
        } else if (fastJSONRadioButton.isSelected()) {
            generateKotlinFastJsonRequestBody(keys);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private void generateKotlinGsonRequestBody(ArrayList<String> keys, ArrayList<Object> values) {
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("val param = JsonObject()").append("\r\n");
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            Object o = values.get(i);
            if (o instanceof String || o instanceof Short || o instanceof Integer || o instanceof Long ||
                    o instanceof Float || o instanceof Double || o instanceof Boolean) {
                bodyBuilder.append("param.addProperty(\"").append(key).append("\", ").append(key).append(")").append("\r\n");
            } else {
                bodyBuilder.append("param.add(\"").append(key).append("\", ").append("Gson().toJsonTree(").append(key).append(", object : TypeToken<Array<String>>() {}.type).asJsonArray").append(")").append("\r\n");
            }
        }
        bodyBuilder.append("val requestBody = param.toString().toRequestBody(\"application/json;charset=UTF-8\".toMediaType())").append("\r\n");
        requestTextArea.setText(bodyBuilder.toString());
    }

    private void generateKotlinFastJsonRequestBody(ArrayList<String> keys) {
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("val param = JSONObject()").append("\r\n");
        for (String key : keys) {
            bodyBuilder.append("param.put(\"").append(key).append("\", ").append(key).append(")").append("\r\n");
        }
        bodyBuilder.append("val requestBody = param.toString().toRequestBody(\"application/json;charset=UTF-8\".toMediaType())").append("\r\n");
        requestTextArea.setText(bodyBuilder.toString());
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        contentPane.setPreferredSize(new Dimension(800, 500));
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(jsRadioButton);
        buttonGroup.add(javaRadioButton);
        buttonGroup.add(kotlinRadioButton);

        ButtonGroup requestButtonGroup = new ButtonGroup();
        requestButtonGroup.add(gsonRadioButton);
        requestButtonGroup.add(fastJSONRadioButton);
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
