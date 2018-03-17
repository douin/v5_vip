
#
verbose=true

# conges_metropole
bean.id.1=conges_metropole
conges_metropole.class=container.FileSystemPropsApplicationContext
conges_metropole.property.1=fileName
conges_metropole.property.1.param.1=./conges_metropole/README.TXT
conges_metropole.property.2=containerName
conges_metropole.property.2.param.1=metropole

# conges_dom_tom
bean.id.2=conges_dom_tom
conges_dom_tom.class=container.FileSystemPropsApplicationContext
conges_dom_tom.property.1=fileName
conges_dom_tom.property.1.param.1=./conges_dom_tom/README.TXT
conges_dom_tom.property.2=containerName
conges_dom_tom.property.2.param.1=dom_tom

bean.id.3=serviceLocator
serviceLocator.class=service_locator.ServiceLocator
serviceLocator.property.1=container
serviceLocator.property.1.param.1=conges_metropole
serviceLocator.property.2=container
serviceLocator.property.2.param.1=conges_dom_tom


#serviceLocator.property.3=container
#serviceLocator.property.3.param.1=conges_alsace_moselle
#serviceLocator.property.4=container
#serviceLocator.property.4.param.1=
#serviceLocator.property.5=container
#serviceLocator.property.5.param.1=
#serviceLocator.property.6=container
#serviceLocator.property.6.param.1=


