## 一、什么是APT

`APT(Annotation Processing Tool)` 注解处理器，是 `javac` 的一个工具，他可以在源码生成class的时候,处理Java语法树。
我们用他可以干什么呢?

1. lombok的原理,在编译期修改字节码,生成 `get` 和 `set` 方法。


[](https://github.com/lxchinesszz/MyLombok.git)

## 二、实战演示

## 2.1 定义处理器

继承 `AbstractProcessor`

```java 
@AutoService(Processor.class)
@SupportedAnnotationTypes({"cn.lxchinesszz.MyData","cn.lxchinesszz.MyGetter","cn.lxchinesszz.MySetter"})
// 这个注解处理器是处理哪个注解的
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class MyLombokProcessor extends AbstractProcessor {

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {}

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {}
}
```

- @AutoService 谷歌提供的SPI工具。当使用这个注解会自定生成Java SPI文件, 当然如果不想用谷歌的工具,我们也可以自己来写配置文件

``` 
├── classes
│   ├── META-INF
│   │   └── services
│   │       └── javax.annotation.processing.Processor
```

- @SupportedAnnotationTypes({"cn.lxchinesszz.MyData","cn.lxchinesszz.MyGetter","cn.lxchinesszz.MySetter"})

支持的注解类型

- @SupportedSourceVersion(SourceVersion.RELEASE_8)

支持的源码类型

## 2.2 Element 体系

- `roundEnv.getElementsAnnotatedWith(MyData.class)` 可以获取被该注解修饰的类或者字段或者方法。

下面我们看下 `Element` 的类型。

![](https://img.springlearn.cn/blog/ca3e47d8d1707db1afb001febfd70c5a.png)

```java 
public class User{ // TypeElement

    private String name; // VariableElement
    
    private Interge age; // VariableElement
    
    public String getName(){ // ExecutableElement
        return this.name;
    }
    
    public void setName( // ExecutableElement
    String name // VariableElement
    ){
        this.name = name;
    }
}
```

如何知道Element 的类型呢。

- `Element#getKind`

![](https://img.springlearn.cn/blog/df6b6e790bcc452c9e9552ddca4e1969.png)

### 2.2.1 获取字段信息

这里我们先自定义一个字段类型来获取基础信息,来学习 `Element`

```java  
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
 }   
```

- 首先先判断是字段类型

```java 
   public static FieldElement toFiledElement(Element enclosedElement) {
        if (ElementKind.FIELD.equals(enclosedElement.getKind())) {
            VariableElement fieldElement = (VariableElement) enclosedElement;
            Name simpleName = fieldElement.getSimpleName();
            return new FieldElement(simpleName.toString(), fieldElement);
        } else {
            throw new RuntimeException("enclosedElement 不是字段类型:" + enclosedElement);
        }
    }
    
```

### 2.2.1 获取方法信息

- 方法包括方法参数和返回值,这里我们自定义一个方法参数。

```java 
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
}    
```

- 生成方法

```java 
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
```

### 2.2.2 获取类信息

类信息包括字段和方法

```java 
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
}    
```

生成类信息

```java 
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

```

## 2.3 日志打印

APT方法中日志的打印,要使用工具。在初始化方法中获取消息打印实例。

```java 
public class MyLombokProcessor extends AbstractProcessor {
    private Messager message;

     @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        message = processingEnvironment.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        //	扫描所有被@MyData注解的元素
        processingEnv.getMessager().printMessage(NOTE, "------------MyData-----------" + roundEnv.getElementsAnnotatedWith(MyData.class));
    }
}
```

就想log日志一样,他也是有消息类型的,如: 提示、异常、警告等。如下枚举

```java 
/**
 * 诊断类型，例如错误或警告。诊断的类型可用于确定应如何将诊断呈现给用户。例如，错误可能被涂成红色或以“错误”一词为前缀，
 * 而警告可能被涂成黄色或以“警告”一词为前缀。没有要求 Kind 应该对诊断消息暗示任何固有的语义含义：例如，一个工具可能会
 * 提供一个选项来将所有警告报告为错误。
 */
enum Kind {
   /**
    * 阻止工具正常完成编译
    */
   ERROR,
   /**
    * 警告
    */
   WARNING,
   /**
    * 类似于警告的问题，但由工具规范强制要求。例如，Java™ 语言规范要求对某些未经检查的操作和使用过时的方法发出警告。
    */
   MANDATORY_WARNING,
   /**
    * 来自该工具的信息性消息。
    */
   NOTE,
   /**
    * 其他类型的诊断
    */
   OTHER,
    }
```

## 2.4 字节码修改

字节码修改首先我们要拿到字节码语法树对象,通过观察者模式类进行修改。这里也在初始化时候获取工具。
如下我们先定义一个工具。

```java 
public class ClassElementBuilder {
    
    private ProcessingEnvironment processingEnv;

    private JavacTrees trees;

    protected Names names;

    protected TreeMaker treeMaker;


    public ClassElementBuilder(ProcessingEnvironment processingEnv) {
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.processingEnv = processingEnv;
        this.trees = JavacTrees.instance(processingEnv);
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);
    }
}    
```

处理器初始化方法进行工具的实例化。

```java 
 @Override
 public synchronized void init(ProcessingEnvironment processingEnvironment) {
     super.init(processingEnvironment);
     this.classElementBuilder = new ClassElementBuilder(processingEnvironment);
 }
```

此时我们就能对添加和修改语法树了。但是这里我们先不着急, 我们在先学习一下语法树的API。



## 2.5 JCTree 语法树


### 2.5.1 定义字段

**定义变量使用**

- TreeMaker#VarDef(JCTree.JCModifiers 字段修饰符,Names 字段名,JCExpression 字段类型,JCExpression 赋值语句)

```java 
private String ${fieldName};
private JCTree.JCVariableDecl generateStringField(JCTree.JCClassDecl jcClassDecl, String fieldName) {
    JCTree.JCVariableDecl var = treeMaker.VarDef(
            treeMaker.Modifiers(Flags.PRIVATE),
            names.fromString(fieldName),
            treeMaker.Ident(names.fromString("String")),
            null);
    jcClassDecl.defs = jcClassDecl.defs.prepend(var);
    return var;
}

private String ${fieldName} = ${fieldName}
private JCTree.JCVariableDecl generateStringField(JCTree.JCClassDecl jcClassDecl, String fieldName) {
    // 字段的赋值语句
    JCTree.JCVariableDecl var = treeMaker.VarDef(
            treeMaker.Modifiers(Flags.PRIVATE),
            names.fromString(fieldName),
            treeMaker.Ident(names.fromString("String")),
            treeMaker.Literal(fieldName));

    jcClassDecl.defs = jcClassDecl.defs.prepend(var);
    return var;
}
```

要想理解这个API,实现要分析字段是由什么构成的,正如下图。

![](https://img.springlearn.cn/blog/9575d00387bdb30b09432288524deba4.png)


标示符三种处理方式。

1. 包装类型，不用引入包，可以直接使用

- TreeMaker#Ident
  JCExpression
```java  
treeMaker.Ident(names.fromString("String"))
```

2. 基本类型，不用引入包，可以直接使用

- TreeMaker#TypeIdent
```java 
treeMaker.TypeIdent(TypeTag.INT)
```

3. 引用类型，需要引入包后再直接使用

- 先引入包，然后就向包装类型那样进行处理。

```java 
// import package
private JCTree.JCImport genImportPkg(String packageName, String className) {
    JCTree.JCIdent ident = treeMaker.Ident(names.fromString(packageName));
    return treeMaker.Import(treeMaker.Select(
            ident, names.fromString(className)), false);
}
```


### 2.5.2 定义方法

生成set方法,方法是由

- 方法修饰符 `treeMaker.Modifiers(Flags.PUBLIC)`
- 方法名 `names.fromString("setName")`
- 方法返回值 `treeMaker#Type、treeMaker#TypeIdent`

```java 
/**
 * public void setName(String name){
 *      this.name = name;
 * }
 *
 * @param jcClassDecl 类
 * @param f           字段
 * @param fieldName   字段名
 */
private void generateSetMethod(JCTree.JCClassDecl jcClassDecl, JCTree.JCVariableDecl f, String fieldName) {
    // 方法体内容
    ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
    // this.MyDate
    JCTree.JCFieldAccess aThis = treeMaker.Select(treeMaker.Ident(names.fromString("this")), names.fromString(fieldName));
    // this.MyDate = MyDate;
    JCTree.JCExpressionStatement exec = treeMaker.Exec(treeMaker.Assign(aThis, treeMaker.Ident(names.fromString(fieldName))));
    statements.add(exec);
    JCTree.JCBlock body = treeMaker.Block(0, statements.toList());

    // 方法参数
    JCTree.JCVariableDecl param = treeMaker.VarDef(treeMaker.Modifiers(Flags.PARAMETER), names.fromString(fieldName), f.vartype, null);
    com.sun.tools.javac.util.List<JCTree.JCVariableDecl> parameters = com.sun.tools.javac.util.List.of(param);

    JCTree.JCMethodDecl getNameMethod = treeMaker.MethodDef(
            treeMaker.Modifiers(Flags.PUBLIC),  // 方法修饰符
            names.fromString("set" + capRename(fieldName)),  // 方法名,capName转驼峰
            treeMaker.Type(new Type.JCVoidType()),  // 方法返回值类型
            List.nil(),
            parameters, // 方法参数
            List.nil(),
            body,// 方法体
            null
    );
    // 插入到语法树中
    jcClassDecl.defs = jcClassDecl.defs.prepend(getNameMethod);
}

/**
 * public void getName(){
 *    return this.name;
 * }
 *
 * @param jcClassDecl 类
 * @param f           字段
 * @param fieldName   字段名
 */
private void generateGetMethod(JCTree.JCClassDecl jcClassDecl, JCTree.JCVariableDecl f, String fieldName) {
    // 方法体内容
    ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
    // this.name
    JCTree.JCFieldAccess select = treeMaker.Select(treeMaker.Ident(names.fromString("this")),
            names.fromString(fieldName));
    // 生成return代码 return this.name
    JCTree.JCReturn jcReturn = treeMaker.Return(select);
    statements.add(jcReturn);
    // 方法体
    JCTree.JCBlock body = treeMaker.Block(0, statements.toList());
    // 生成方法
    JCTree.JCMethodDecl getNameMethod = treeMaker.MethodDef(
            treeMaker.Modifiers(Flags.PUBLIC), // 方法修饰符
            names.fromString("get" + capRename(fieldName)), // 方法名
            f.vartype, // 方法返回值类型
            List.nil(),
            List.nil(), // 方法参数
            List.nil(),
            body, // 方法体
            null
    );
    // 插入到语法树中
    jcClassDecl.defs = jcClassDecl.defs.prepend(getNameMethod);
}
```

### 2.5.3 赋值语句

```java 
    // this.MyDate
    JCTree.JCFieldAccess aThis = treeMaker.Select(treeMaker.Ident(names.fromString("this")), names.fromString(fieldName));
    // this.MyDate = MyDate;
    JCTree.JCExpressionStatement exec = treeMaker.Exec(treeMaker.Assign(aThis, treeMaker.Ident(names.fromString(fieldName))));
```


[参考文章](https://www.cnblogs.com/javastack/p/15386924.html)
