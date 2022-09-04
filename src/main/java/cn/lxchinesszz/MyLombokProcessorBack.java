package cn.lxchinesszz;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ReferenceType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.Set;

import static javax.tools.Diagnostic.Kind.NOTE;

/**
 * 生成字节码: https://gitcode.net/mirrors/square/javapoet?utm_source=csdn_github_accelerator
 * 生成字节码: https://blog.csdn.net/Mr_wzc/article/details/119491650
 *
 * @author liuxin
 * 2022/8/31 17:45
 * @AutoService 就是Java SPI 自动为我们生成 javax.annotation.processing.Processor的SPI文件
 */
@SupportedAnnotationTypes("cn.lxchinesszz.MyData")
// 这个注解处理器是处理哪个注解的
@SupportedSourceVersion(SourceVersion.RELEASE_8) // Java版本
public class MyLombokProcessorBack extends AbstractProcessor {

    private Types mTypeUtils;
    private Messager mMessager;
    private Filer mFiler;
    private Elements mElementUtils;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mTypeUtils = processingEnvironment.getTypeUtils();
        mMessager = processingEnvironment.getMessager();
        mFiler = processingEnvironment.getFiler();
        mElementUtils = processingEnvironment.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        //	扫描所有被@Factory注解的元素
        processingEnv.getMessager().printMessage(NOTE, "------------MyData-----------" + roundEnv.getElementsAnnotatedWith(MyData.class));

        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(MyData.class)) {
            // 根据Element获取类信息
            // 使用修改类信息
            ElementKind kind = annotatedElement.getKind();
            if (ElementKind.CLASS.equals(kind)) {
                processingEnv.getMessager().printMessage(NOTE, "--------MyLombok修饰Class--------" + annotatedElement);
                List<? extends Element> enclosedElements = annotatedElement.getEnclosedElements();
                for (Element enclosedElement : enclosedElements) {
                    if (ElementKind.FIELD.equals(enclosedElement.getKind())) {
                        VariableElement fieldElement = ElementUtils.toFiledElement(enclosedElement).getFieldElement();
                        List<? extends TypeMirror> typeArguments = ((DeclaredType) fieldElement.asType()).getTypeArguments();
                        processingEnv.getMessager().printMessage(NOTE, "--------MyLombok修饰Field Param--------" + ElementUtils.toFiledElement(enclosedElement) + "泛型:" + typeArguments);
                    } else if (ElementKind.METHOD.equals(enclosedElement.getKind())) {
                        TypeMirror returnType = ((ExecutableElement) enclosedElement).getReturnType();
                        List<? extends VariableElement> parameters = ((ExecutableElement) enclosedElement).getParameters();
                        for (VariableElement parameter : parameters) {
                            if (parameter.asType() instanceof ReferenceType) {
                                parameter.asType().getKind();
                                processingEnv.getMessager().printMessage(NOTE, "--------MyLombok修饰方法参数引用类型" + parameter.asType() + ":" + parameter.asType().getClass());
                            }
                        }
                        processingEnv.getMessager().printMessage(NOTE, "--------MyLombok修饰方法returnType" + returnType);
                        processingEnv.getMessager().printMessage(NOTE, "--------MyLombok修饰方法returnType" + ((ExecutableElement) enclosedElement).getReceiverType());
                        processingEnv.getMessager().printMessage(NOTE, "--------MyLombok修饰方法" + ElementUtils.toMethodElement(enclosedElement));
                    }
                }
            }
        }
        processingEnv.getMessager().printMessage(NOTE, "------------MyLombok-----------");
        /**
         * 1. 获取字段类型和字段名称 生成set方法
         * private String name;
         *
         * public void setName(String name){
         *     this.name = name;
         * }
         * public String getName(){
         *     return this.name;
         * }
         */
        return false;
    }
}
