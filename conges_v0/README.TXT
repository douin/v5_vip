#
verbose=true

bean.id.1=conditionEstUltraMarin
conditionEstUltraMarin.class=conges.ConditionEstUltraMarin

bean.id.2=operationPlus1
operationPlus1.class=conges.OperationPlus
operationPlus1.property.1=operande
operationPlus1.property.1.param.1=1

bean.id.3=operationPlus10
operationPlus10.class=conges.OperationPlus
operationPlus10.property.1=operande
operationPlus10.property.1.param.1=10


# commande plus10 si ultra marin et travaillant en métropole
bean.id.4=commandePlus10
commandePlus10.class=conges.CommandAgent
commandePlus10.property.1=operation
commandePlus10.property.1.param.1=operationPlus10
commandePlus10.property.2=conditions
commandePlus10.property.2.param.1=conditionEstUltraMarin

bean.id.5=macroPlus
macroPlus.class=conges.MacroCommandAgent
macroPlus.property.1=commandes
macroPlus.property.1.param.1=plus plus plus plus plus
macroPlus.property.2=conditions
macroPlus.property.2.param.1=toujoursVrai conditionEstUltraMarin

# commande plus 1 sans condition
bean.id.6=plus
plus.class=conges.CommandAgent
plus.property.1=operation
plus.property.1.param.1=operationPlus1
plus.property.2=conditions
plus.property.2.param.1=toujoursVrai

bean.id.7=toujoursVrai
toujoursVrai.class=conges.VRAI

# commande plus2 sans condition
bean.id.8=plus2
plus2.class=conges.CommandAgent
plus2.property.1=operation
plus2.property.1.param.1=operationPlus2
plus2.property.2=conditions
plus2.property.2.param.1=toujoursVrai

bean.id.9=operationPlus2
operationPlus2.class=conges.OperationPlus
operationPlus2.property.1=operande
operationPlus2.property.1.param.1=2

bean.id.10=macroPlusPlus
macroPlusPlus.class=conges.MacroCommandAgent
macroPlusPlus.property.1=commandes
macroPlusPlus.property.1.param.1=plus2 plus2 plus2 plus100 plus plus plus plus2
macroPlusPlus.property.2=conditions
macroPlusPlus.property.2.param.1=toujoursVrai toujoursVrai
macroPlusPlus.property.3=exception
macroPlusPlus.property.3.param.1=conges_exception


bean.id.11=invoker
invoker.class=conges.Invoker
invoker.property.1=command
invoker.property.1.param.1=macroPlusPlus

bean.id.12=operationPlus100
operationPlus100.class=conges.OperationPlus
operationPlus100.property.1=operande
operationPlus100.property.1.param.1=100

# commande plus100 sans condition
bean.id.13=plus100
plus100.class=conges.CommandAgent
plus100.property.1=operation
plus100.property.1.param.1=operationPlus100
plus100.property.2=conditions
plus100.property.2.param.1=toujoursVrai

bean.id.14=conges_exception
conges_exception.class=conges.CongesException
conges_exception.property.1=message
conges_exception.property.1.param.1=conges exception


