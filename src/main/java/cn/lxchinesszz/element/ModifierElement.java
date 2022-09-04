package cn.lxchinesszz.element;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

/**
 * @author liuxin
 * 2022/9/4 15:58
 */
public class ModifierElement {
    Element enclosedElement;

    public ModifierElement(Element enclosedElement) {
        this.enclosedElement = enclosedElement;
    }

    public boolean isPublic() {
        return enclosedElement.getModifiers().contains(Modifier.PUBLIC);
    }

    public boolean isPrivate() {
        return enclosedElement.getModifiers().contains(Modifier.PRIVATE);
    }

    public boolean isProtected() {
        return enclosedElement.getModifiers().contains(Modifier.PROTECTED);
    }

    public boolean isStatic() {
        return enclosedElement.getModifiers().contains(Modifier.STATIC);
    }

    public boolean isFinal() {
        return enclosedElement.getModifiers().contains(Modifier.FINAL);
    }
}
