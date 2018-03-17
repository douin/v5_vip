#
verbose=true

bean.id.1=exec
exec.class=instructions.TantQue
exec.property.1=condition
exec.property.1.param.1=conditionTantQue
exec.property.2=instruction
#exec.property.2.param.1=intructionInc
exec.property.2.param.1=selection


bean.id.2=conditionTantQue
conditionTantQue.class=variabilite_introduction_2.ConditionTantQue
conditionTantQue.property.1=operateur
conditionTantQue.property.1.param.1=<
conditionTantQue.property.2=operande1
conditionTantQue.property.2.param.1=index
conditionTantQue.property.3=operande2
conditionTantQue.property.3.param.1=10

bean.id.3=intructionInc
intructionInc.class=variabilite_introduction_2.InstructionInc

bean.id.4=selection
selection.class=instructions.Selection
selection.property.1=condition
selection.property.1.param.1=conditionEstPair
selection.property.2=siAlors
#selection.property.2.param.1=intructionInc
selection.property.2.param.1=sequence
#selection.property.3=sinon
#selection.property.3.param.1=intructionInc
#selection.property.3.param.1=calculConges

bean.id.5=sequence
sequence.class=instructions.Sequence
sequence.property.1=i1
sequence.property.1.param.1=instructionCommandes
sequence.property.2=i2
sequence.property.2.param.1=intructionInc

bean.id.6=conditionEstPair
conditionEstPair.class=variabilite_introduction_2.ConditionEstPair


bean.id.7=calculs
calculs.class=commandes.MacroCommande
calculs.property.1=commandes
calculs.property.1.param.1=calculConges

bean.id.8=calculConges
calculConges.class=commandes.Commande
calculConges.property.1=condition
calculConges.property.1.param.1=conditionConges
calculConges.property.2=operation
calculConges.property.2.param.1=operationConges

bean.id.9=conditionConges
conditionConges.class=variabilite_introduction_2.ConditionConges

bean.id.10=operationConges
operationConges.class=variabilite_introduction_2.CommandeConges

bean.id.11=instructionCommandes
instructionCommandes.class=instructions.CommandeEnInstruction
instructionCommandes.property.1=commande
instructionCommandes.property.1.param.1=calculs






