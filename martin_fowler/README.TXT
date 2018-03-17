## le fichier de configuration spring.xml
#<beans>
#    <bean id="MovieLister" class="MovieLister">
#        <property name="finder">
#            <ref local="MovieFinder"/>
#        </property>
#    </bean>
#    <bean id="MovieFinder" class="ColonMovieFinder">
#        <property name="filename">
#            <value>movies1.txt</value>
#        </property>
#    </bean>
#</beans>


# MovieLister est l'identifiant du bean
bean.id.1=movieLister
# à quelle classe java ce bean est-il associé ?
movieLister.class=martin_fowler.MovieLister

# Quelle propriété est à affecter, ici une seule
movieLister.property.1=finder
# Le mutateur a un seul paramètre
movieLister.property.1.param.1=movieFinder

bean.id.2=movieFinder
movieFinder.class=martin_fowler.ColonMovieFinder
movieFinder.property.1=filename
movieFinder.property.1.param.1=movies1.txt

