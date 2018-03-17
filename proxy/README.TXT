verbose=true

bean.id.1=client
client.class=proxy.Client
client.property.1=requete
# variabilité ici
#client.property.1.param.1=proxy
client.property.1.param.1=proxy1

bean.id.2=proxy
proxy.class=proxy.Proxy
proxy.property.1=requete
proxy.property.1.param.1=reel
proxy.property.2=filtre
proxy.property.2.param.1=.cnam.fr

bean.id.3=proxy1
proxy1.class=proxy.Proxy
proxy1.property.1=requete
proxy1.property.1.param.1=reel
proxy1.property.2=filtre
proxy1.property.2.param.1=.fr

bean.id.4=reel
reel.class=proxy.Reel
