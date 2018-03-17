verbose=true

bean.id.1=nombre1
nombre1.class=observer.Nombre
nombre1.property.1=valeur
nombre1.property.1.param.1=1
nombre1.property.2=observer
nombre1.property.2.param.1=observateur1
nombre1.property.3=observer
nombre1.property.3.param.1=observateur2

bean.id.2=observateur1
observateur1.class=observer.NombreObserver
observateur1.property.1=nom
observateur1.property.1.param.1=obs_1

bean.id.3=observateur2
observateur2.class=observer.NombreObserver
observateur2.property.1=nom
observateur2.property.1.param.1=obs_2

bean.id.4=conditionEstPair
conditionEstPair.class=observer.ConditionEstPair

bean.id.5=operationPlus1
operationPlus1.class=observer.OperationPlus
operationPlus1.property.1=operande
operationPlus1.property.1.param.1=1

# commande plus1 si pair
bean.id.6=commandePlus1
commandePlus1.class=observer.CommandNombre
commandePlus1.property.1=operation
commandePlus1.property.1.param.1=operationPlus1
commandePlus1.property.2=condition
commandePlus1.property.2.param.1=conditionEstPair

bean.id.7=macroPlus
macroPlus.class=observer.MacroCommandNombre
macroPlus.property.1=commandes
macroPlus.property.1.param.1=plus plus plus plus plus
macroPlus.property.2=condition
macroPlus.property.2.param.1=toujoursVrai


# commande plus1 sans condition
bean.id.8=plus
plus.class=observer.CommandNombre
plus.property.1=operation
plus.property.1.param.1=operationPlus1
plus.property.2=condition
plus.property.2.param.1=toujoursVrai

bean.id.9=toujoursVrai
toujoursVrai.class=conditions.VRAI

# commande plus2 sans condition
bean.id.10=plus2
plus2.class=observer.CommandNombre
plus2.property.1=operation
plus2.property.1.param.1=operationPlus2
plus2.property.2=condition
plus2.property.2.param.1=toujoursVrai

bean.id.11=operationPlus2
operationPlus2.class=observer.OperationPlus
operationPlus2.property.1=operande
operationPlus2.property.1.param.1=2

bean.id.12=macroPlusPlus
macroPlusPlus.class=observer.MacroCommandNombre
macroPlusPlus.property.1=commandes
macroPlusPlus.property.1.param.1=plus2 plus2 plus2 plus plus plus
macroPlusPlus.property.2=condition
macroPlusPlus.property.2.param.1=toujoursVrai

bean.id.13=invoker
invoker.class=observer.Invoker
invoker.property.1=command
invoker.property.1.param.1=macroPlusPlus



