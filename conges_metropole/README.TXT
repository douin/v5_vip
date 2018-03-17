#verbose=true

bean.id.1=metropole.estimation_conges_restants
metropole.estimation_conges_restants.class=commandes.Invocateur
metropole.estimation_conges_restants.property.1=commande
#metropole.estimation_conges_restants.property.1.param.1=plus2
metropole.estimation_conges_restants.property.1.param.1=macroPlusPlus
metropole.estimation_conges_restants.property.2=exception
metropole.estimation_conges_restants.property.2.param.1=conges_exception

bean.id.2=macroPlusPlus
macroPlusPlus.class=commandes.Commande
macroPlusPlus.property.1=commande
macroPlusPlus.property.1.param.1=macroPlusPlusCommande
macroPlusPlus.property.2=condition
macroPlusPlus.property.2.param.1=toujoursVrai
#macroPlusPlus.property.2.param.1=une_condition
#macroPlusPlus.property.2.param.1=toutes_les_conditions
macroPlusPlus.property.3=exception
macroPlusPlus.property.3.param.1=conges_exception
macroPlusPlus.property.4=operation
macroPlusPlus.property.4.param.1=set0

bean.id.3=macroPlusPlusCommande
macroPlusPlusCommande.class=commandes.MacroCommande
##### une exception est levée avec plus100
#macroPlusPlusCommandes.property.1.param.1=plus2 plus2 plus2 plus100 plus plus plus
macroPlusPlusCommande.property.1.param.1=plus2 plus2 plus2 plus plus plus plus2

# commande plus sans condition
bean.id.3=plus
plus.class=commandes.Commande
plus.property.1=operation
plus.property.1.param.1=operationPlus1
plus.property.2=condition
plus.property.2.param.1=toujoursVrai

# commande plus2 sans condition
bean.id.4=plus2
plus2.class=commandes.Commande
plus2.property.1=operation
plus2.property.1.param.1=operationPlus2
plus2.property.2=condition
plus2.property.2.param.1=toujoursVrai

# commande plus100 sans condition
bean.id.5=plus100
plus100.class=commandes.Commande
plus100.property.1=operation
plus100.property.1.param.1=operationPlus100
plus100.property.2=condition
plus100.property.2.param.1=toujoursVrai

bean.id.6=operationPlus100
operationPlus100.class=conges_metropole.OperationPlus
operationPlus100.property.1=operande
operationPlus100.property.1.param.1=100

bean.id.7=operationPlus2
operationPlus2.class=conges_metropole.OperationPlus
operationPlus2.property.1=operande
operationPlus2.property.1.param.1=2

bean.id.8=conditionEstUltraMarin
conditionEstUltraMarin.class=conges_metropole.ConditionEstUltraMarin

bean.id.9=conditionAnciennete
conditionAnciennete.class=conges_metropole.ConditionAnciennete
conditionAnciennete.property.1=anciennete
conditionAnciennete.property.1.param.1=3

bean.id.10=conditionFractionnement
conditionFractionnement.class=conges_metropole.ConditionFractionnement

bean.id.11=operationPlus1
operationPlus1.class=conges_metropole.OperationPlus
operationPlus1.property.1=operande
operationPlus1.property.1.param.1=1

bean.id.12=operationPlus10
operationPlus10.class=conges_metropole.OperationPlus
operationPlus10.property.1=operande
operationPlus10.property.1.param.1=10

bean.id.13=toutes_les_conditions
toutes_les_conditions.class=conditions.ToutesLesConditionsSontSatisfaites
toutes_les_conditions.property.1=conditions
#toutes_les_conditions.property.1.param.1=conditionEstUltraMarin conditionAnciennete
toutes_les_conditions.property.1.param.1=toujoursVrai toujoursFaux

bean.id.14=une_condition
une_condition.class=conditions.UneDesConditionsEstSatisfaite
une_condition.property.1=conditions
#une_condition.property.1.param.1=conditionEstUltraMarin conditionAnciennete
une_condition.property.1.param.1=toujoursVrai toujoursFaux

bean.id.15=conges_exception
conges_exception.class=conges_metropole.CongesException
conges_exception.property.1=message
conges_exception.property.1.param.1=conges exception
conges_exception.property.2=runtimeException
conges_exception.property.2.param.1=runtimeException


bean.id.16=toujoursVrai
toujoursVrai.class=conditions.VRAI

bean.id.17=toujoursFaux
toujoursFaux.class=conditions.FAUX

bean.id.18=set0
set0.class=conges_metropole.OperationSet
set0.property.1=operande
set0.property.1.param.1=0

bean.id.19=setMoins1
setMoins1.class=conges_metropole.OperationSet
setMoins1.property.1=operande
setMoins1.property.1.param.1=-1

bean.id.20=runtimeException
runtimeException.class=java.lang.RuntimeException

bean.id.21=invokerException
invokerException.class=commandes.ExceptionCommande


