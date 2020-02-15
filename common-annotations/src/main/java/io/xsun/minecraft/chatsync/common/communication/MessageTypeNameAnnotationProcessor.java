package io.xsun.minecraft.chatsync.common.communication;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.Properties;
import java.util.Set;

@SupportedAnnotationTypes("io.xsun.minecraft.chatsync.common.communication.MessageTypeName")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class MessageTypeNameAnnotationProcessor extends AbstractProcessor {

    private Writer mappingFile;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        try {
            messager = processingEnv.getMessager();
            mappingFile = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT,
                    "io.xsun.minecraft.chatsync.common.communication.insideprotocol.message",
                    "MessageTypeMapping.properties").openWriter();
        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Properties mapping = new Properties();
        for (TypeElement annotation : annotations) {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element element : elements) {
                String qualifiedName = ((TypeElement) element).getQualifiedName().toString();
                String typeName = element.getAnnotation(MessageTypeName.class).value();
                mapping.setProperty(typeName, qualifiedName);
            }
        }
        if (annotations.size() > 0) {
            try {
                mapping.store(mappingFile, "Made with love by xsun2001");
            } catch (IOException e) {
                messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            }
        }
        return true;
    }
}
