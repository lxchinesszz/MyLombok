package cn.lxchinesszz.element;


import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * @author liuxin
 * 2022/9/4 15:59
 */
public class FieldElement extends ModifierElement {

    /**
     * 字段名
     */
    private final String fieldName;

    /**
     * 字段类型
     */
    private Class<?> fieldType;

    /**
     * 资源原始类型
     */
    private final VariableElement fieldElement;

    /**
     * 基本类型提示
     */
    private String remark;

    /**
     * 字段所属类
     */
    private final TypeElement classElement;

    public FieldElement(String fieldName, VariableElement fieldElement) {
        super(fieldElement);
        this.fieldName = fieldName;
        this.fieldElement = fieldElement;
        this.classElement = (TypeElement) fieldElement.getEnclosingElement();
        try {
            if (isPrimitive()) {
                fieldType = null;
                this.remark = "基本类型:" + fieldElement.asType().toString();
            } else {
                this.fieldType = Class.forName(fieldElement.asType().toString());
            }
        } catch (ClassNotFoundException e) {
            // 如果还报错说明是一个泛型 根据泛型类型来进行处理 fieldElement.asType()
            // DeclaredType    Set<String>
            // WildcardType
            //    ?
            //    ? extends Number
            //    ? super T
            this.fieldType = Object.class;
        }
    }

    public TypeElement getClassElement() {
        return classElement;
    }

    public VariableElement getFieldElement() {
        return fieldElement;
    }

    /**
     * 是否基本类型
     *
     * @return 基本类型
     */
    public boolean isPrimitive() {
        return fieldElement.asType().getKind().isPrimitive();
    }

    public Object getDefaultValue() {
        if (isFinal()) {
            return fieldElement.getConstantValue();
        } else {
            return null;
        }
    }

    public String getFieldName() {
        return fieldName;
    }

    public Class<?> getFieldType() {
        return fieldType;
    }

    @Override
    public String toString() {
        return "FieldElement{" + "fieldName='" + fieldName + '\'' + ", fieldType=" + fieldType + ", fieldElement=" + fieldElement + ", remark='" + remark + '\'' + ", enclosedElement=" + enclosedElement + '}';
    }
}
