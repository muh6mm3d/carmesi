/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package carmesi.internal;

import carmesi.BeforeView;
import carmesi.URL;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;
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
 *
 * @author Victor
 */
@SupportedAnnotationTypes("carmesi.*")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class CarmesiAnnotationsProcessor extends AbstractProcessor{


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if(annotations.isEmpty()){
            return false;
        }
        Set<Element> elements=new HashSet<Element>();
        elements.addAll(roundEnv.getElementsAnnotatedWith(BeforeView.class));
        elements.addAll(roundEnv.getElementsAnnotatedWith(URL.class));
        
        Writer writer=null;
        try{
            FileObject resource = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "", RegistratorListener.CONFIG_FILE_PATH, elements.toArray(new Element[0]));
            writer=resource.openWriter();
            for(Element e:elements){
                if(e instanceof TypeElement){
                    TypeElement tElement=(TypeElement)e;
                    System.out.println(tElement.getQualifiedName());
                    writer.write(tElement.getQualifiedName().toString());
                    writer.write("\n");
                }
            }
            writer.close();
            writer=null;
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

}
