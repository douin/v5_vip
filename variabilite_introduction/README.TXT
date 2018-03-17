#
verbose=true

bean.id.1=conditionAncienneté
conditionAncienneté.class=variabilite_introduction.ConditionAncienneté
conditionAncienneté.property.1=nombreDAnnéesDAnciennetéRequis
conditionAncienneté.property.1.param.1=15

bean.id.2=operationAncienneté
operationAncienneté.class=variabilite_introduction.OpérationAddition
operationAncienneté.property.1=opérande
operationAncienneté.property.1.param.1=10

bean.id.3=conditionPénibilité
conditionPénibilité.class=variabilite_introduction.ConditionPénibilitéDuService
conditionPénibilité.property.1=service
conditionPénibilité.property.1.param.1=Voierie

bean.id.4=operationPénibilité
operationPénibilité.class=variabilite_introduction.OpérationAddition
operationPénibilité.property.1=opérande
operationPénibilité.property.1.param.1=5

# instruction : agent ancienneté
bean.id.5=commandeAncienneté
commandeAncienneté.class=commandes.Commande
commandeAncienneté.property.1=operation
commandeAncienneté.property.1.param.1=operationAncienneté
commandeAncienneté.property.2=condition
commandeAncienneté.property.2.param.1=conditionAncienneté

# instruction : agent pénibilité
bean.id.6=commandePénibilité
commandePénibilité.class=commandes.Commande
commandePénibilité.property.1=operation
commandePénibilité.property.1.param.1=operationPénibilité
commandePénibilité.property.2=condition
commandePénibilité.property.2.param.1=conditionPénibilité


# instruction : trace sur la console
bean.id.7=commandeAffichage
commandeAffichage.class=commandes.Commande
commandeAffichage.property.1=operation
commandeAffichage.property.1.param.1=traceSurLaConsole
commandeAffichage.property.2=condition
commandeAffichage.property.2.param.1=toujoursVrai
#commandeAffichage.property.2.param.1=uneDesConditions
#commandeAffichage.property.2.param.1=toutesLesConditions

# instructions multiples 
bean.id.8=commandes_test
commandes_test.class=commandes.MacroCommande
commandes_test.property.1=commandes

## démo 1: juste un affichage
#commandes_test.property.1.param.1=commandeAffichage
## démo 2: ancienneté + affichage
#commandes_test.property.1.param.1=commandeAncienneté commandeAffichage
## démo 3: pénibilité + affichage
#commandes_test.property.1.param.1=commandePénibilité commandeAffichage 
## démo 4: ancienneté + pénibilité + affichage
commandes_test.property.1.param.1=commandeAncienneté commandePénibilité commandeAffichage
## démo 5: affichage  + ancienneté + pénibilité + jours du maire + affichage
#commandes_test.property.1.param.1=commandeAffichage commandeAncienneté commandePénibilité commandeJoursDuMaire commandeAffichage
## démo 6: affichage  + ancienneté + pénibilité + exception + jours du maire + affichage
#commandes_test.property.1.param.1=commandeAffichage commandeAncienneté commandePénibilité commandeException commandeJoursDuMaire commandeAffichage
## démo 7:
#commandes_test.property.1.param.1=commandeException commandeAffichage commandeAncienneté commandePénibilité  commandeJoursDuMaire commandeAffichage


bean.id.9=traceSurLaConsole
traceSurLaConsole.class=operations.TraceSurLaConsole
traceSurLaConsole.property.1=message
traceSurLaConsole.property.1.param.1=##Trace## 
traceSurLaConsole.property.2=avecTrace
#traceSurLaConsole.property.2.param.1=false
traceSurLaConsole.property.2.param.1=true

bean.id.10=commandeException
commandeException.class=commandes.Commande
commandeException.property.1=operation
commandeException.property.1.param.1=operationException
commandeException.property.2=condition
commandeException.property.2.param.1=toujoursVrai
#commandeException.property.2.param.1=toujoursFaux
#commandeException.property.2.param.1=peutEtre

# invoker
bean.id.11=invocateur
invocateur.class=commandes.Invocateur
invocateur.property.1=commande
invocateur.property.1.param.1=commandes_test


# vrai
bean.id.12=toujoursVrai
toujoursVrai.class=conditions.VRAI

# vrai
bean.id.13=toujoursFaux
toujoursFaux.class=conditions.FAUX

# peutEtre
bean.id.14=peutEtre
peutEtre.class=conditions.PEUT_ETRE

# toutes les conditions doivent être satisfaites
bean.id.15=toutesLesConditions
toutesLesConditions.class=conditions.ToutesLesConditionsSontSatisfaites
toutesLesConditions.property.1=conditions
toutesLesConditions.property.1.param.1=conditionAncienneté conditionPénibilité

# une des conditions doit être satisfaite
bean.id.16=uneDesConditions
uneDesConditions.class=conditions.UneDesConditionsEstSatisfaite
uneDesConditions.property.1=conditions
uneDesConditions.property.1.param.1=conditionAncienneté conditionPénibilité

# instruction : agent et les jours du maire
bean.id.17=commandeJoursDuMaire
commandeJoursDuMaire.class=variabilite_introduction.CommandeJoursDuMaire
commandeJoursDuMaire.property.1=conditions
commandeJoursDuMaire.property.1.param.1=paris boulogne neuilly avalon bastia

bean.id.18=paris
paris.class=variabilite_introduction.ConditionJoursDuMaire
paris.property.1=nombreDAnnéesDAnciennetéRequis
paris.property.1.param.1=3
paris.property.2=nomDeLaMairie
paris.property.2.param.1=Paris
paris.property.3=nombreDeJoursSupplémentaires
paris.property.3.param.1=2

bean.id.19=boulogne
boulogne.class=variabilite_introduction.ConditionJoursDuMaire
boulogne.property.1=nombreDAnnéesDAnciennetéRequis
boulogne.property.1.param.1=3
boulogne.property.2=nomDeLaMairie
boulogne.property.2.param.1=Boulogne-Billancourt
boulogne.property.3=nombreDeJoursSupplémentaires
boulogne.property.3.param.1=3

bean.id.20=neuilly
neuilly.class=variabilite_introduction.ConditionJoursDuMaire
neuilly.property.1=nombreDAnnéesDAnciennetéRequis
neuilly.property.1.param.1=3
neuilly.property.2=nomDeLaMairie
neuilly.property.2.param.1=Neuilly
neuilly.property.3=nombreDeJoursSupplémentaires
neuilly.property.3.param.1=4

bean.id.21=avalon
avalon.class=variabilite_introduction.ConditionJoursDuMaire
avalon.property.1=nombreDAnnéesDAnciennetéRequis
avalon.property.1.param.1=3
avalon.property.2=nomDeLaMairie
avalon.property.2.param.1=Avalon
avalon.property.3=nombreDeJoursSupplémentaires
avalon.property.3.param.1=5

bean.id.22=bastia
bastia.class=variabilite_introduction.ConditionJoursDuMaire
bastia.property.1=nombreDAnnéesDAnciennetéRequis
bastia.property.1.param.1=3
bastia.property.2=nomDeLaMairie
bastia.property.2.param.1=Bastia
bastia.property.3=nombreDeJoursSupplémentaires
bastia.property.3.param.1=6


# 
bean.id.23=maillonPénibilité
maillonPénibilité.class=commandes.MaillonCdR
maillonPénibilité.property.1=commande
maillonPénibilité.property.1.param.1=commandePénibilité
maillonPénibilité.property.2=successeur
#maillonPénibilité.property.2.param.1=maillonAffichage
maillonPénibilité.property.2.param.1=maillonStop

# 
bean.id.24=maillonAffichage
maillonAffichage.class=commandes.MaillonCdR
maillonAffichage.property.1=commande
maillonAffichage.property.1.param.1=commandeAffichage
maillonAffichage.property.2=successeur
maillonAffichage.property.2.param.1=maillon_commandes_test
#maillonAffichage.property.2.param.1=null

bean.id.25=maillon_commandes_test
maillon_commandes_test.class=commandes.MaillonCdR
maillon_commandes_test.property.1=commande
maillon_commandes_test.property.1.param.1=commandes_test
maillon_commandes_test.property.2=successeur
maillon_commandes_test.property.2.param.1=null

bean.id.26=commandesCdR
commandesCdR.class=commandes.MacroCommandeCdR
commandesCdR.property.1=commande
commandesCdR.property.1.param.1=commandeAncienneté
commandesCdR.property.2=successeur
commandesCdR.property.2.param.1=maillonPénibilité

bean.id.27=maillonStop
maillonStop.class=commandes.MaillonCdR
maillonStop.property.1=commande
maillonStop.property.1.param.1=commandeStop
maillonStop.property.2=successeur
maillonStop.property.2.param.1=maillonAffichage

# invoker
bean.id.28=invocateurCdR
invocateurCdR.class=commandes.Invocateur
invocateurCdR.property.1=commande
invocateurCdR.property.1.param.1=commandesCdR

bean.id.29=commandeStop
commandeStop.class=commandes.Commande
commandeStop.property.1=operation
commandeStop.property.1.param.1=operationStop
commandeStop.property.2=condition
commandeStop.property.2.param.1=toujoursFaux
#commandeStop.property.2.param.1=toujoursVrai

bean.id.30=commandeRien
commandeRien.class=commandes.Commande
commandeRien.property.1=operation
commandeRien.property.1.param.1=operationRien

bean.id.31=operationStop
operationStop.class=operations.GLOBAL_STOP
#operationStop.class=operations.LOCAL_STOP

bean.id.32=operationRien
operationRien.class=operations.RIEN

bean.id.33=invocateurAvecUndo
invocateurAvecUndo.class=commandes.Invocateur
invocateurAvecUndo.property.1=commande
invocateurAvecUndo.property.1.param.1=commandesCdR
invocateurAvecUndo.property.2=avecSauvegarde
invocateurAvecUndo.property.2.param.1=true
#invocateurAvecUndo.property.2.param.1=false




