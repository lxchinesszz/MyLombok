package cn.lxchinesszz.element;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * @author liuxin
 * 2022/9/4 15:59
 */
public class MethodParamElement extends ModifierElement {

    private final String paramName;

    private final VariableElement paramElement;

    private final ExecutableElement methodElement;

    private final TypeElement classElement;


    public MethodParamElement(String fieldName, VariableElement paramElement) {
        super(paramElement);
        this.paramName = fieldName;
        this.paramElement = paramElement;
        // 参数拿到的是方法，方法拿到类
        this.methodElement = (ExecutableElement) paramElement.getEnclosingElement();
        this.classElement = (TypeElement) paramElement.getEnclosingElement().getEnclosingElement();
    }

    public String getParamName() {
        return paramName;
    }

    public VariableElement getParamElement() {
        return paramElement;
    }

    public ExecutableElement getMethodElement() {
        return methodElement;
    }

    public TypeElement getClassElement() {
        return classElement;
    }

    @Override
    public String toString() {
        return "MethodParamElement{" +
                "paramName='" + paramName + '\'' +
                ", paramElement=" + paramElement +
                ", methodElement=" + methodElement +
                ", classElement=" + classElement +
                ", enclosedElement=" + enclosedElement +
                '}';
    }
}
