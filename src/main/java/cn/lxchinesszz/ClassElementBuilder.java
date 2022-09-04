package cn.lxchinesszz;

import com.sun.source.util.TreePath;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.util.ArrayList;
import java.util.Locale;

/**
 * https://www.cnblogs.com/javastack/p/15386924.html
 *
 * @author liuxin
 * 2022/9/2 20:19
 */
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

    public void createGetMethod(Element classSymbol) {
        JCTree jcTree = trees.getTree(classSymbol);
        jcTree.accept(new TreeTranslator() {
            @Override
            public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                for (JCTree def : jcClassDecl.defs) {
                    // 字段
                    if (def instanceof JCTree.JCVariableDecl) {
                        JCTree.JCVariableDecl field = (JCTree.JCVariableDecl) def;
                        String name = field.getName().toString();
                        // 生成get方法
                        generateGetMethod(jcClassDecl, field, name);
                        // 生成set方法
                        generateSetMethod(jcClassDecl, field, name);
                    }
                }
            }
        });
    }

    public void createSetMethod() {

    }

    public void addField(Element classSymbol) {
        JCTree jcTree = trees.getTree(classSymbol);
        jcTree.accept(new TreeTranslator() {

            @Override
            public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                // 添加一个变量name
                JCTree.JCVariableDecl nameField = generateStringField(jcClassDecl, "lx");

                generateDateMethod(jcClassDecl);
//                generateIntegerField(jcClassDecl, "age3");
//
//                generateIntField(jcClassDecl, "age4");
//
//                generateBigBooleanField(jcClassDecl, "BigBoolean");
//
//                generateSmailBooleanField(jcClassDecl, "smailBoolean");
//
//                generateDateField(jcClassDecl, "java.util", "List", "MyList", classSymbol);
//                JCTree.JCVariableDecl jcVariableDecl = generateDateField(jcClassDecl, "java.util", "Date", "MyDate", classSymbol);
//
//                generateGetMethod(jcClassDecl, jcVariableDecl, "MyDate");
//
//                generateSetMethod(jcClassDecl, jcVariableDecl, "MyDate");
//                // 生成构造器
//                generateConstructor(jcClassDecl, classSymbol);
//
//                // 生成 instance 变量
//                JCTree.JCVariableDecl jcVariableDecl = generateVariable(jcClassDecl, classSymbol);
//
//                // 生成 getInstance 方法
//                generateInstanceMethod(jcClassDecl, classSymbol, jcVariableDecl);
//
//                // 生成 getName 方法
//                generateGetterMethod(jcClassDecl, classSymbol, nameField);

                super.visitClassDef(jcClassDecl);
            }
        });
    }


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

    private JCTree.JCVariableDecl generateIntegerField(JCTree.JCClassDecl jcClassDecl, String fieldName) {
        JCTree.JCVariableDecl var = treeMaker.VarDef(
                treeMaker.Modifiers(Flags.PRIVATE),
                names.fromString(fieldName),
                treeMaker.Ident(names.fromString("Integer")),
                null);
        jcClassDecl.defs = jcClassDecl.defs.prepend(var);
        return var;
    }

    private JCTree.JCVariableDecl generateIntField(JCTree.JCClassDecl jcClassDecl, String fieldName) {
        JCTree.JCVariableDecl var = treeMaker.VarDef(
                treeMaker.Modifiers(Flags.PRIVATE),
                names.fromString(fieldName),
                treeMaker.TypeIdent(TypeTag.INT),
                null);
        jcClassDecl.defs = jcClassDecl.defs.prepend(var);
        return var;
    }


    private JCTree.JCVariableDecl generateBigBooleanField(JCTree.JCClassDecl jcClassDecl, String fieldName) {
        JCTree.JCVariableDecl var = treeMaker.VarDef(
                treeMaker.Modifiers(Flags.PRIVATE),
                names.fromString(fieldName),
                treeMaker.Ident(names.fromString("Boolean")),
                null);
        jcClassDecl.defs = jcClassDecl.defs.prepend(var);
        return var;
    }

    private JCTree.JCVariableDecl generateSmailBooleanField(JCTree.JCClassDecl jcClassDecl, String fieldName) {
        JCTree.JCVariableDecl var = treeMaker.VarDef(
                treeMaker.Modifiers(Flags.PRIVATE),
                names.fromString(fieldName),
                treeMaker.TypeIdent(TypeTag.BOOLEAN),
                null);
        jcClassDecl.defs = jcClassDecl.defs.prepend(var);
        return var;
    }

    private void generateField(JCTree.JCClassDecl jcClassDecl, String fieldName, Element element) {
        TreePath treePath = trees.getPath(element);
        java.util.List<JCTree> trees = new ArrayList<>();
        JCTree.JCCompilationUnit jccu = (JCTree.JCCompilationUnit) treePath.getCompilationUnit();
        trees.addAll(jccu.defs);

    }

    // import package
    private JCTree.JCImport genImportPkg(String packageName, String className) {
        JCTree.JCIdent ident = treeMaker.Ident(names.fromString(packageName));
        return treeMaker.Import(treeMaker.Select(
                ident, names.fromString(className)), false);
    }

    private JCTree.JCVariableDecl generateDateField(JCTree.JCClassDecl jcClassDecl, String packageName, String className, String fieldName, Element element) {
        genImportPkg(packageName, className);

        TreePath treePath = trees.getPath(element);
        JCTree.JCCompilationUnit jccu = (JCTree.JCCompilationUnit) treePath.getCompilationUnit();
        java.util.List<JCTree> trees = new ArrayList<>(jccu.defs);
        JCTree.JCImport jcImport = genImportPkg(packageName, className);
        // 防止重复导入
        if (!trees.contains(jcImport)) {
            trees.add(0, jcImport);
        }
        jccu.defs = List.from(trees);

        // 生成字段
        JCTree.JCVariableDecl var = treeMaker.VarDef(
                treeMaker.Modifiers(Flags.PRIVATE),
                names.fromString(fieldName),
                treeMaker.Ident(names.fromString(className)),
                null);
        jcClassDecl.defs = jcClassDecl.defs.prepend(var);
        trees.add(jcImport);
        return var;
    }

    private String capRename(String fieldName) {
        return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    /**
     * public void getName(){
     * return this.name;
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

    /**
     * public void setName(String name){
     * this.name = name;
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
     * public Date getCustomerDate(){
     * return new Date();
     * }
     *
     * @param jcClassDecl
     */
    private void generateDateMethod(JCTree.JCClassDecl jcClassDecl) {
        // 方法体内容
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();

        JCTree.JCNewClass date = treeMaker.NewClass(
                null,
                List.nil(),
                treeMaker.Ident(names.fromString("Date")),
                List.nil(),
                null
        );
        JCTree.JCReturn returnCode = treeMaker.Return(date);
        statements.add(returnCode);
        JCTree.JCBlock body = treeMaker.Block(0, statements.toList());

        JCTree.JCMethodDecl getDateMethod = treeMaker.MethodDef(
                treeMaker.Modifiers(Flags.PUBLIC),  // 方法修饰符
                names.fromString("getCustomerDate"),  // 方法名,capName转驼峰
                treeMaker.Type(new Type.JCVoidType()),  // 方法返回值类型
                List.nil(),
                List.nil(), // 方法参数
                List.nil(),
                body,// 方法体
                null
        );
        // 插入到语法树中
        jcClassDecl.defs = jcClassDecl.defs.prepend(getDateMethod);
    }
}
