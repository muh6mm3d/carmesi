/* Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0) */

package carmesi.internal;

import carmesi.BeforeURL;
import carmesi.URL;
import carmesi.convert.ConverterFor;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

/**
 * Generates automatically the config file that Carmesi requires.
 *
 * See the help of your IDE for configuring the processor. If you do manual compilation, see (TODO javac annotation configuration link).
 *
 * @author Victor Hugo Herrera Maldonado
 * @see CarmesiInitializer
 */
@SupportedAnnotationTypes("carmesi.*")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class CarmesiAnnotationsProcessor extends AbstractProcessor{
    private Set<String> classNames=new TreeSet<String>();

    @Override
    public final boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        assert annotations != null;
        assert roundEnv != null;
        if(annotations.isEmpty()){
            return false;
        }
        Set<Element> elements=new HashSet<Element>();
        elements.addAll(roundEnv.getElementsAnnotatedWith(BeforeURL.class));
        elements.addAll(roundEnv.getElementsAnnotatedWith(URL.class));
        elements.addAll(roundEnv.getElementsAnnotatedWith(ConverterFor.class));
        Writer writer=null;
        try{
            for(Element e:elements){
                if(e instanceof TypeElement){
                    TypeElement tElement=(TypeElement)e;
                    classNames.add(tElement.getQualifiedName().toString());
                }
            }
            if(!roundEnv.processingOver()){
                writeConfigFile();
            }
        }catch(IOException ex){
            processingEnv.getMessager().printMessage(Kind.MANDATORY_WARNING, "Can not create configuration file: "+ ex);
        }finally{
            try{
                if(writer != null){
                    writer.close();
                }
            }catch(IOException ex){
                
            }
        }
        return true;
    }
    
    private void writeConfigFile() throws IOException{
        Writer writer=null;
        FileObject resource = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", CarmesiInitializer.CONFIG_FILE_PATH, new Element[]{});
        writer=resource.openWriter();
        for(String className:classNames){
            writer.write(className);
            writer.write("\n");
        }
        writer.close();
        writer=null;
    }
    
}
