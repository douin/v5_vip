#
verbose=true

bean.id.1=invocateur
invocateur.class=commandes.Invocateur
invocateur.property.1=commande
invocateur.property.1.param.1=congeAnnuelCommande

#invocateur.property.1.param.1=plus2
#invocateur.property.1.param.1=macroPlusPlus
#invocateur.property.2=exception
#invocateur.property.2.param.1=conges_exception

bean.id.2=macroPlusPlus
macroPlusPlus.class=commandes.Commande
macroPlusPlus.property.1.param.1=commande
macroPlusPlus.property.1=macroPlusPlusCommand
macroPlusPlus.property.2=condition
macroPlusPlus.property.2.param.1=toujoursVrai
#macroPlusPlus.property.2.param.1=une_condition
#macroPlusPlus.property.2.param.1=toutes_les_conditions
macroPlusPlus.property.3=exception
macroPlusPlus.property.3.param.1=conges_exception
macroPlusPlus.property.4=operation
macroPlusPlus.property.4.param.1=set0

bean.id.3=macroPlusPlusCommand
macroPlusPlusCommand.class=commandes.MacroCommande
# une exception est levée avec plus100
macroPlusPlusCommand.property.1.param.1=plus2 plus2 plus2 plus100 plus plus plus
#macroPlusPlus.property.1.param.1=plus2 plus2 plus2 plus plus plus plus2

# commande plus sans condition
bean.id.4=plus
plus.class=commandes.Commande
plus.property.1=operation
plus.property.1.param.1=operationPlus1
plus.property.2=condition
plus.property.2.param.1=toujoursVrai

# commande plus2 sans condition
bean.id.5=plus2
plus2.class=commandes.Commande
plus2.property.1=operation
plus2.property.1.param.1=operationPlus2
plus2.property.2=condition
plus2.property.2.param.1=toujoursVrai

# commande plus100 sans condition
bean.id.6=plus100
plus100.class=commandes.Commande
plus100.property.1=operation
plus100.property.1.param.1=operationPlus100
plus100.property.2=condition
plus100.property.2.param.1=toujoursVrai

bean.id.7=operationPlus100
operationPlus100.class=conges_v1.OperationPlus
operationPlus100.property.1=operande
operationPlus100.property.1.param.1=100

bean.id.8=operationPlus2
operationPlus2.class=conges_v1.OperationPlus
operationPlus2.property.1=operande
operationPlus2.property.1.param.1=2

bean.id.9=conditionEstUltraMarin
conditionEstUltraMarin.class=conges_v1.ConditionEstUltraMarin

bean.id.10=conditionAnciennete
conditionAnciennete.class=conges_v1.ConditionAnciennete
conditionAnciennete.property.1=anciennete
conditionAnciennete.property.1.param.1=3

bean.id.11=conditionFractionnement
conditionFractionnement.class=conges_v1.ConditionFractionnement

bean.id.12=operationPlus1
operationPlus1.class=conges_v1.OperationPlus
operationPlus1.property.1=operande
operationPlus1.property.1.param.1=1

bean.id.13=operationPlus10
operationPlus10.class=conges_v1.OperationPlus
operationPlus10.property.1=operande
operationPlus10.property.1.param.1=10

bean.id.14=toutes_les_conditions
toutes_les_conditions.class=conditions.ToutesLesConditionsSontSatisfaites
toutes_les_conditions.property.1=conditions
#toutes_les_conditions.property.1.param.1=conditionEstUltraMarin conditionAnciennete
toutes_les_conditions.property.1.param.1=toujoursVrai toujoursFaux

bean.id.15=une_condition
une_condition.class=conditions.UneDesConditionsEstSatisfaite
une_condition.property.1=conditions
#une_condition.property.1.param.1=conditionEstUltraMarin conditionAnciennete
une_condition.property.1.param.1=toujoursVrai toujoursFaux

bean.id.16=conges_exception
conges_exception.class=conges_v1.CongesException
conges_exception.property.1=message
conges_exception.property.1.param.1=conges exception
conges_exception.property.2=runtimeException
conges_exception.property.2.param.1=runtimeException
conges_exception.property.3=operation
conges_exception.property.3.param.1=set1000
conges_exception.property.4=condition
conges_exception.property.4.param.1=toujoursVrai

bean.id.17=toujoursVrai
toujoursVrai.class=conditions.VRAI

bean.id.18=toujoursFaux
toujoursFaux.class=conditions.FAUX

bean.id.19=set0
set0.class=conges_v1.OperationSet
set0.property.1=operande
set0.property.1.param.1=0

bean.id.20=setMoins1
setMoins1.class=conges_v1.OperationSet
setMoins1.property.1=operande
setMoins1.property.1.param.1=-1

bean.id.21=runtimeException
runtimeException.class=java.lang.RuntimeException

bean.id.22=invokerException
invokerException.class=commandes.ExceptionCommande

bean.id.23=set1000
set1000.class=conges_v1.OperationSet
set1000.property.1=operande
set1000.property.1.param.1=1000

bean.id.24=congeAnnuelCommande
congeAnnuelCommande.class=commandes.Commande
congeAnnuelCommande.property.1=operation
congeAnnuelCommande.property.1.param.1=congeAnnuel
congeAnnuelCommande.property.2=condition
congeAnnuelCommande.property.2.param.1=toujoursVrai

bean.id.25=congeAnnuel
congeAnnuel.class=conges_v1.CongeAnnuel

