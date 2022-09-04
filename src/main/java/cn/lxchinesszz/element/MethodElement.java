package cn.lxchinesszz.element;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeMirror;
import java.util.List;

/**
 * @author liuxin
 * 2022/9/4 16:00
 */
public class MethodElement extends ModifierElement{

    /**
     * 方法参数名
     */
    private final String methodName;

    /**
     * 返回值
     */
    private Class<?> returnType;

    /**
     * 方法原始信息
     */
    private final ExecutableElement methodElement;

    /**
     * 方法参数
     */
    private final List<MethodParamElement> methodParamElements;

    public MethodElement(ExecutableElement methodElement, List<MethodParamElement> methodParamElements) {
        super(methodElement);
        this.methodName = methodElement.getSimpleName().toString();
        try {
            TypeMirror returnTypeMirror = methodElement.getReturnType();
            if (returnTypeMirror instanceof NoType) {
                this.returnType = Void.TYPE;
            } else {
                this.returnType = Class.forName(methodElement.getReturnType().toString());
            }
        } catch (ClassNotFoundException e) {
            this.returnType = Void.TYPE;
        }
        this.methodElement = methodElement;
        this.methodParamElements = methodParamElements;
    }

    /**
     * 是否可变参数
     *
     * @return boolean
     */
    public boolean isVarArgs() {
        return methodElement.isVarArgs();
    }

    public String getMethodName() {
        return methodName;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public ExecutableElement getMethodElement() {
        return methodElement;
    }

    public List<MethodParamElement> getMethodParamElements() {
        return methodParamElements;
    }

    @Override
    public String toString() {
        return "MethodElement{" + "methodName='" + methodName + '\'' + ", returnType=" + returnType + ", methodElement=" + methodElement + ", methodParamElements=" + methodParamElements + ", enclosedElement=" + enclosedElement + '}';
    }
}
