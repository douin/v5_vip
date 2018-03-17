package martin_fowler;

import container.*;
import java.util.*;

public class Assembler{
  public static void main(String[] args){
    ApplicationContext ctx = Factory.createApplicationContext("martin_fowler/README.TXT");
    MovieLister lister = (MovieLister) ctx.getBean("movieLister");
    List<Movie> movies = lister.moviesDirectedBy("Sergio Leone");
    System.out.println(movies.get(0).getTitle());
  }
}
