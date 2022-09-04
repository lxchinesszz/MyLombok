package cn.lxchinesszz;

import cn.lxchinesszz.element.ClassElement;
import cn.lxchinesszz.element.FieldElement;
import cn.lxchinesszz.element.MethodElement;
import cn.lxchinesszz.element.MethodParamElement;

import javax.lang.model.element.*;
import java.util.ArrayList;
import java.util.List;

/**
 * | Java类型        | 处理器Element        |
 * | --------------- | -------------------- |
 * | TYPE            | TypeElement          |
 * | FIELD           | VariableElement      |
 * | METHOD          | ExecutableElement    |
 * | PARAMETER       | VariableElement      |
 * | CONSTRUCTOR     | ExecutableElement    |
 * | LOCAL_VARIABLE  |                      |
 * | ANNOTATION_TYPE | TypeElement          |
 * | PACKAGE         | PackageElement       |
 * | TYPE_PARAMETER  | TypeParameterElement |
 * | TYPE_USE        |                      |
 * getSimpleName：获取该元素的名字;
 * getModifiers：获取该元素的访问权限，返回一个Set;
 * asType: 获取该元素的类型，比如String会返回java.lang.String，TextView会返回android.widget.TextView；
 * getEnclosingElement：获取父级元素，比如参数的父级是方法，方法的父级是类或者接口;
 * getQualifiedName：获取全限定名，如果是类的话，包含完整的报名路径;
 *
 * @author liuxin
 * 2022/9/1 22:02
 */
public class ElementUtils {

    public static ClassElement toClassElement(Element enclosedElement) {
        if (ElementKind.CLASS.equals(enclosedElement.getKind())) {
            List<? extends Element> enclosedElements = enclosedElement.getEnclosedElements();
            List<FieldElement> fieldElements = new ArrayList<>();
            List<MethodElement> methodElements = new ArrayList<>();
            for (Element element : enclosedElements) {
                if (ElementKind.FIELD.equals(element.getKind())) {
                    fieldElements.add(toFiledElement(element));
                }
                if (ElementKind.METHOD.equals(element.getKind())) {
                    methodElements.add(toMethodElement(element));
                }
            }
            return new ClassElement(enclosedElement, fieldElements, methodElements);
        } else {
            throw new RuntimeException("enclosedElement 不是字段类型:" + enclosedElement);
        }
    }

    public static FieldElement toFiledElement(Element enclosedElement) {
        if (ElementKind.FIELD.equals(enclosedElement.getKind())) {
            VariableElement fieldElement = (VariableElement) enclosedElement;
            Name simpleName = fieldElement.getSimpleName();
            return new FieldElement(simpleName.toString(), fieldElement);
        } else {
            throw new RuntimeException("enclosedElement 不是字段类型:" + enclosedElement);
        }
    }

    public static MethodParamElement toMethodParamElement(Element enclosedElement) {
        if (ElementKind.PARAMETER.equals(enclosedElement.getKind())) {
            VariableElement fieldElement = (VariableElement) enclosedElement;
            Name simpleName = fieldElement.getSimpleName();
            return new MethodParamElement(simpleName.toString(), fieldElement);
        } else {
            throw new RuntimeException("enclosedElement 不是字段类型:" + enclosedElement);
        }
    }

    public static MethodElement toMethodElement(Element enclosedElement) {
        if (ElementKind.METHOD.equals(enclosedElement.getKind())) {
            ExecutableElement methodElement = (ExecutableElement) enclosedElement;
            List<? extends VariableElement> parameters = methodElement.getParameters();
            List<MethodParamElement> paramElements = new ArrayList<>();
            for (VariableElement parameter : parameters) {
                paramElements.add(toMethodParamElement(parameter));
            }
            return new MethodElement(methodElement, paramElements);
        } else {
            throw new RuntimeException("enclosedElement 不是方法类型:" + enclosedElement.getClass());
        }
    }


}
