/**
 * Insert license here.
 */

package carmesi.internal;

import carmesi.Before;
import carmesi.URL;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
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
import javax.swing.JOptionPane;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

/**
 * Generates automatically the config file that Carmesi requires.
 *
 * See the help of your IDE for configuring the processor. If you do manual compilation, see (TODO javac annotation configuration link).
 *
 * @author Victor Hugo Herrera Maldonado
 * @see RegistratorListener
 */
@SupportedAnnotationTypes("carmesi.*")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class CarmesiAnnotationsProcessor extends AbstractProcessor{
    private Set<String> classNames=new TreeSet<String>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if(annotations.isEmpty()){
            return false;
        }
        Set<Element> elements=new HashSet<Element>();
        elements.addAll(roundEnv.getElementsAnnotatedWith(Before.class));
        elements.addAll(roundEnv.getElementsAnnotatedWith(URL.class));
        
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
        FileObject resource = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", RegistratorListener.CONFIG_FILE_PATH, new Element[]{});
        writer=resource.openWriter();
        for(String className:classNames){
            writer.write(className);
            writer.write("\n");
        }
        writer.close();
        writer=null;
    }
    
}
