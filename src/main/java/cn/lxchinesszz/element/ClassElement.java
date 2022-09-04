package cn.lxchinesszz.element;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liuxin
 * 2022/9/4 16:01
 */
public class ClassElement extends ModifierElement {

    /**
     * 类名称
     */
    private final String className;

    /**
     * 包名称
     */
    private final String packageName;

    /**
     * 类原始信息
     */
    private final TypeElement classElement;

    /**
     * 字段信息
     */
    private final List<FieldElement> fieldElements;

    /**
     * 方法信息
     */
    private final List<MethodElement> methodElements;

    public ClassElement(Element enclosedElement, List<FieldElement> fieldElements, List<MethodElement> methodElements) {
        super(enclosedElement);
        this.classElement = (TypeElement) enclosedElement;
        this.fieldElements = fieldElements;
        this.methodElements = methodElements;
        this.className = classElement.getSimpleName().toString();
        this.packageName = classElement.getQualifiedName().toString().replaceAll("\\." + classElement.getSimpleName().toString(), "");
    }

    public String getClassName() {
        return className;
    }

    public String getPackageName() {
        return packageName;
    }

    public TypeElement getClassElement() {
        return classElement;
    }

    public List<FieldElement> getFieldElements() {
        return fieldElements;
    }

    public List<MethodElement> getMethodElements() {
        return methodElements;
    }

    @Override
    public String toString() {
        return "ClassElement{" + "className='" + className + '\'' + ", packageName='" + packageName + '\'' + ", classElement=" + classElement + ", fieldElements=" + fieldElements + ", methodElements=" + methodElements + ", enclosedElement=" + enclosedElement + '}';
    }
}
