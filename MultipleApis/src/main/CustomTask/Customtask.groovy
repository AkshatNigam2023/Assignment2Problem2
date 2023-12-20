import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod

task scanDocumentation {
    doLast {
        // Specify the package or classpath to scan
        String packageName = 'custom_Annotation'

        // Create a ClassPool
        ClassPool classPool = ClassPool.getDefault()
        classPool.insertClassPath(project.sourceSets.main.runtimeClasspath.asPath)

        // Scan classes
        project.sourceSets.main.output.classesDirs.each { classDir ->
            classPool.appendClassPath(classDir)
            classPool.appendClassPath(project.sourceSets.main.output.resourcesDir)
        }

        // Scan methods annotated with @Annotations.MethodDocumentation
        classPool.scan { ctClass ->
            if (ctClass.hasAnnotation(Annotations.MethodDocumentation)) {
                println "Method Documentation found in class: ${ctClass.name}"
                ctClass.methods.each { CtMethod ctMethod ->
                    if (ctMethod.hasAnnotation(Annotations.MethodDocumentation)) {
                        println "  Method: ${ctMethod.name}"
                    }
                }
            }
        }

        // Scan classes annotated with @Annotations.ClassDocumentation
        classPool.scan { ctClass ->
            if (ctClass.hasAnnotation(Annotations.ClassDocumentation)) {
                println "Class Documentation found: ${ctClass.name}"
            }
        }
    }
}
