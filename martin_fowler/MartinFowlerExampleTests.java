// # tp_injection, Le fichier de configuration en version properties
// 
// ## le fichier de configuration spring.xml
// #<beans>
// #    <bean id="MovieLister" class="MovieLister">
// #        <property name="finder">
// #            <ref local="MovieFinder"/>
// #        </property>
// #    </bean>
// #    <bean id="MovieFinder" class="ColonMovieFinder">
// #        <property name="filename">
// #            <value>movies1.txt</value>
// #        </property>
// #    </bean>
// #</beans>
// 
// 
// # MovieLister est l'identifiant du bean
// bean.id.1=MovieLister
// # à quelle classe java ce bean est-il associé ?
// MovieLister.class=question1.MovieLister
// # Quelle propriété est à affecter, ici une seule
// MovieLister.property.1=finder
// # Le mutateur n'a qu'un paramètre
// MovieLister.property.1.param.1=MovieFinder
// 
// bean.id.2=MovieFinder
// MovieFinder.class=question1.ColonMovieFinder
// MovieFinder.property.1=filename
// MovieFinder.property.1.param.1=movies1.txt
// 
// 

package martin_fowler;
import java.util.*;
import container.*;

public class MartinFowlerExampleTests extends junit.framework.TestCase{

    public void testAvecInjection(){
        try{
            ApplicationContext ctx = Factory.createApplicationContext("./martin_fowler/README.TXT");
            MovieLister lister = (MovieLister) ctx.getBean("movieLister");
            List<Movie> movies = lister.moviesDirectedBy("Sergio Leone");
            assertEquals("Once Upon a Time in the West",movies.get(0).getTitle());
        }catch(Exception e){
            fail("Exception inattendue ? " + e.getMessage());
        }
    }

    public void testSansInjection(){
        MovieFinder finder = new ColonMovieFinder();
        finder.setFilename("movies1.txt");
        MovieLister lister = new MovieLister();
        lister.setFinder(finder);
        List<Movie> movies = lister.moviesDirectedBy("Sergio Leone");
        assertEquals("Once Upon a Time in the West",movies.get(0).getTitle());
    }
}

