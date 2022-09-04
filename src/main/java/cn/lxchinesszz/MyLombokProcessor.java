package cn.lxchinesszz;

import cn.lxchinesszz.element.ClassElement;
import com.google.auto.service.AutoService;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.util.Context;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
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
@AutoService(Processor.class)
@SupportedAnnotationTypes({"cn.lxchinesszz.MyData", "cn.lxchinesszz.MyGetter", "cn.lxchinesszz.MySetter"})
// 这个注解处理器是处理哪个注解的
@SupportedSourceVersion(SourceVersion.RELEASE_8) // Java版本
public class MyLombokProcessor extends AbstractProcessor {

    private Types mTypeUtils;

    private Messager mMessager;

    private Filer mFiler;

    private Elements mElementUtils;

    private ClassElementBuilder classElementBuilder;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.classElementBuilder = new ClassElementBuilder(processingEnvironment);
        mTypeUtils = processingEnvironment.getTypeUtils();
        mMessager = processingEnvironment.getMessager();
        mFiler = processingEnvironment.getFiler();
        mElementUtils = processingEnvironment.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        //	扫描所有被@Factory注解的元素
        processingEnv.getMessager().printMessage(NOTE, "------------MyData-----------" + roundEnv.getElementsAnnotatedWith(MyData.class));
        MessageUtils messageUtils = MessageUtils.getMessageUtils(mMessager);
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(MyData.class)) {
            ElementKind kind = annotatedElement.getKind();
            if (ElementKind.CLASS.equals(kind)) {
                // 拿到所有的字段
                ClassElement classElement = ElementUtils.toClassElement(annotatedElement);
                messageUtils.info("Class:{}", classElement.toString());
                classElementBuilder.createGetMethod(annotatedElement);
            }
        }
        for (Element getFieldElement : roundEnv.getElementsAnnotatedWith(MyGetter.class)) {
            ElementKind kind = getFieldElement.getKind();
            if (ElementKind.FIELD.equals(kind)) {
                messageUtils.info("Class:{},Field:{}", getFieldElement.getEnclosingElement(), getFieldElement.toString());
            }
        }
        processingEnv.getMessager().printMessage(NOTE, "------------MyLombok-----------");
        return false;
    }
}
