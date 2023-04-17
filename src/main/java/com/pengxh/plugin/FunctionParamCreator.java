package com.pengxh.plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.pengxh.plugin.ui.ParamCreatorDialog;
import org.jetbrains.annotations.NotNull;

class FunctionParamCreator extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);

        new ParamCreatorDialog(project).show();
    }
}
