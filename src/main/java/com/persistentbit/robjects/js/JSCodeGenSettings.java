package com.persistentbit.robjects.js;

import com.persistentbit.core.utils.BaseValueClass;


/**
 * Created by petermuys on 9/09/16.
 */
public class JSCodeGenSettings extends BaseValueClass{
    public enum ModuleType{
        none, commonJs, systemJs
    }
    public enum CodeType {
        js5,js6,typeScript
    }
    private final ModuleType moduleType;
    private final CodeType  codeType;
    private final String    utilsClassName = "RObjectsUtils";

    public JSCodeGenSettings(ModuleType moduleType, CodeType codeType) {
        this.moduleType = moduleType;
        this.codeType = codeType;
    }

    public String getUtilsClassName() {
        return utilsClassName;
    }

    public ModuleType getModuleType() {
        return moduleType;
    }

    public CodeType getCodeType() {
        return codeType;
    }
}
