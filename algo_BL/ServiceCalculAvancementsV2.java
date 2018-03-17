package algo_BL;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.sedit.core.exception.TechnicalException;
import fr.sedit.core.tools.Duration;
import fr.sedit.core.tools.UtilsDate;
import fr.sedit.grh.ca.ave.model.enums.EnumTypeRetard;
import fr.sedit.grh.ca.ave.services.IServiceCalculAvancementsV2;
import fr.sedit.grh.ca.ave.services.impl.moteur.CalculPresenceRetardMoteurAE;
import fr.sedit.grh.ca.ave.services.impl.moteur.MoteurAvException;
import fr.sedit.grh.ca.ave.services.impl.moteur.MoteurAveResultDTO;
import fr.sedit.grh.ca.par.model.ParamCaCollect;
import fr.sedit.grh.ca.par.model.enums.EnumModeCreation;
import fr.sedit.grh.ca.par.model.enums.EnumTypeAvancement;
import fr.sedit.grh.coeur.ab.dao.IDaoPointageTransforme;
import fr.sedit.grh.coeur.ab.model.PointageTransforme;
import fr.sedit.grh.coeur.ca.ave.dao.IDaoPropositionAE;
import fr.sedit.grh.coeur.ca.ave.dao.IDaoTableauAE;
import fr.sedit.grh.coeur.ca.ave.model.ParametrageAE;
import fr.sedit.grh.coeur.ca.ave.model.PropositionAE;
import fr.sedit.grh.coeur.ca.ave.model.TableauAE;
import fr.sedit.grh.coeur.ca.ave.model.enums.EnumEtatCalculTableau;
import fr.sedit.grh.coeur.ca.ave.model.enums.EnumTypeInjection;
import fr.sedit.grh.coeur.ca.ave.model.enums.EnumTypeProposition;
import fr.sedit.grh.coeur.ca.ave.model.enums.EnumVerifier;
import fr.sedit.grh.coeur.ca.par.dao.IDaoParamCaOrga;
import fr.sedit.grh.coeur.ca.par.model.ParamCaOrga;
import fr.sedit.grh.coeur.ca.par.model.RegleSpecifiqueAE;
import fr.sedit.grh.coeur.ca.par.model.enums.EnumDecalage;
import fr.sedit.grh.coeur.ca.par.model.enums.EnumTypeArrondi;
import fr.sedit.grh.coeur.ca.par.model.enums.EnumTypeImpacte;
import fr.sedit.grh.coeur.cs.dao.IDaoAgent;
import fr.sedit.grh.coeur.cs.dao.IDaoCollectivite;
import fr.sedit.grh.coeur.cs.dao.IDaoFicheArriveeDepart;
import fr.sedit.grh.coeur.cs.dao.IDaoFichePositionAdmin;
import fr.sedit.grh.coeur.cs.dao.IDaoGrilleIndiciaire;
import fr.sedit.grh.coeur.cs.dao.IDaoIndiceBrutMajore;
import fr.sedit.grh.coeur.cs.model.Agent;
import fr.sedit.grh.coeur.cs.model.Carriere;
import fr.sedit.grh.coeur.cs.model.EchelonOrdonne;
import fr.sedit.grh.coeur.cs.model.FicheArriveeDepart;
import fr.sedit.grh.coeur.cs.model.FicheGradeEmploi;
import fr.sedit.grh.coeur.cs.model.FichePositionAdmin;
import fr.sedit.grh.coeur.cs.model.Grade;
import fr.sedit.grh.coeur.cs.model.IndiceBrutMajore;
import fr.sedit.grh.coeur.cs.model.dto.SituationAgentMoteurAvancDTO;
import fr.sedit.grh.coeur.cs.model.enums.EnumTypeEchelon;
import fr.sedit.grh.coeur.cs.model.enums.EnumTypePropoAEAgent;
import fr.sedit.grh.coeur.cs.model.enums.EnumTypeStatut;
import fr.sedit.grh.coeur.cs.model.parametrage.Collectivite;
import fr.sedit.grh.coeur.cs.services.IServiceFichePositionAdmin;
import fr.sedit.grh.coeur.no.dao.IDaoFicheNotationAgent;
import fr.sedit.grh.coeur.nr.model.TypePropositionAgent;
import fr.sedit.grh.common.UtilsDateGRH;
import fr.sedit.grh.common.UtilsDureeCarriere;
import fr.sedit.sedit.common.model.AbstractDatedPersistentObjectCS;

/**
* Service de calcul des avancements d'échêlons
* TODO Revoir les indices majorés afin de cacher les données a l'avance
* TODO Revoir (uniquement en simulation) le chargement des nouvelles grille/echelon gestion des caches.
* TODO Revoir le flag EtatCalcul -> Calcul en cours
* TODO Le calcul en CALENDAIRE n'est pas géré (@see ServiceCalculAvancementsV2#initProposition())
*
 * @author cyril.malosse
*/
@Transactional
public class ServiceCalculAvancementsV2 implements IServiceCalculAvancementsV2 {

   private static final Log log = LogFactory.getLog(ServiceCalculAvancementsV2.class);

   private static final Runtime runtime = Runtime.getRuntime();

   private IDaoAgent daoAgent;
   public void setDaoAgent(IDaoAgent daoAgent) {
      this.daoAgent = daoAgent;
   }

   private IDaoPropositionAE daoPropositionAE;
   public void setDaoPropositionAE(IDaoPropositionAE daoPropositionAE) {
      this.daoPropositionAE = daoPropositionAE;
   }

   private IDaoTableauAE daoTableauAE;
   public void setDaoTableauAE(IDaoTableauAE daoTableauAE) {
      this.daoTableauAE = daoTableauAE;
   }

   private IDaoIndiceBrutMajore daoIndiceBrutMajore;
   public void setDaoIndiceBrutMajore(IDaoIndiceBrutMajore daoIndiceBrutMajore) {
      this.daoIndiceBrutMajore = daoIndiceBrutMajore;
   }

   private IDaoFichePositionAdmin daoFichePositionAdmin;
   public void setDaoFichePositionAdmin(IDaoFichePositionAdmin daoFichePositionAdmin) {
      this.daoFichePositionAdmin = daoFichePositionAdmin;
   }

   private IDaoPointageTransforme daoPointageTransforme;
   public void setDaoPointageTransforme(IDaoPointageTransforme daoPointageTransforme) {
      this.daoPointageTransforme = daoPointageTransforme;
   }

   private IDaoGrilleIndiciaire daoGrilleIndiciaire;
   public void setDaoGrilleIndiciaire(IDaoGrilleIndiciaire daoGrilleIndiciaire) {
      this.daoGrilleIndiciaire = daoGrilleIndiciaire;
   }

   private IDaoFicheNotationAgent daoFicheNotationAgent;
   public void setDaoFicheNotationAgent(IDaoFicheNotationAgent daoFicheNotationAgent) {
      this.daoFicheNotationAgent = daoFicheNotationAgent;
   }

   private IDaoFicheArriveeDepart daoFicheArriveeDepart;
   public void setDaoFicheArriveeDepart(IDaoFicheArriveeDepart daoFicheArriveeDepart) {
      this.daoFicheArriveeDepart = daoFicheArriveeDepart;
   }
  
   private IDaoParamCaOrga daoParamCaOrga;
    public void setDaoParamCaOrga(IDaoParamCaOrga daoParamCaOrga) {
        this.daoParamCaOrga = daoParamCaOrga;
    }
   
    private IDaoCollectivite daoCollectivite;
   public void setDaoCollectivite(IDaoCollectivite daoCollectivite) {
      this.daoCollectivite = daoCollectivite;
   }

    @Autowired
    @Qualifier("serviceFichePositionAdmin")
    private IServiceFichePositionAdmin serviceFichePositionAdmin;

   // Le mode de calcul d'AVE est trentième
   private static final Boolean trentieme = true;

   /**
    * @see IServiceCalculAvancementsV2#calculAvancementMaxi(Date, Date, List, boolean)
    */
   @Override
    @Transactional(propagation = Propagation.REQUIRED)
   public MoteurAveResultDTO calculAvancementMaxi(final Date paramDateDebut, final Date paramDateFin,
         final List<Collectivite> paramListCollectivites, final boolean paramRetournerListe) {
      ParametrageAE parametrageAE =  new ParametrageAE();

      parametrageAE.getParamMoteur().setModeCreation(EnumModeCreation.MAXI);

      parametrageAE.getParamMoteur().setDateDebut(paramDateDebut);
      parametrageAE.getParamMoteur().setDateFin(paramDateFin);
      parametrageAE.getParamMoteur().setTypeAvancement(EnumTypeAvancement.MAXI);
      parametrageAE.getParamMoteur().setListCollectivites(paramListCollectivites);
      parametrageAE.getParamMoteur().setRetournerListe(paramRetournerListe);

      this.launchProcess(parametrageAE);
      return parametrageAE.getMoteurAveResultDTO();
   }

   /**
    * @see IServiceCalculAvancementsV2#calculAvancementMaxi(Date, Date, List, boolean)
    */
   @Override
    @Transactional(propagation = Propagation.REQUIRED)
   public MoteurAveResultDTO calculAvancementMaxiListCarriere(final Date paramDateDebut, final Date paramDateFin,
         final List<Carriere> paramListCarrirere, final List<Collectivite> paramListCollectivites, final boolean paramRetournerListe,
         final boolean paramRecalculerPourInjection) {
      ParametrageAE parametrageAE =  new ParametrageAE();
      parametrageAE.getParamMoteur().setModeCreation(EnumModeCreation.MAXI);
      parametrageAE.getParamMoteur().setDateDebut(paramDateDebut);
      parametrageAE.getParamMoteur().setDateFin(paramDateFin);
      parametrageAE.getParamMoteur().setTypeAvancement(EnumTypeAvancement.MAXI);
      parametrageAE.getParamMoteur().setListCollectivites(paramListCollectivites);
      parametrageAE.getParamMoteur().setListCarriere(paramListCarrirere);
      parametrageAE.getParamMoteur().setRetournerListe(paramRetournerListe);
      parametrageAE.getParamMoteur().setRecalculerPourInjection(paramRecalculerPourInjection);

      this.launchProcess(parametrageAE);
      return parametrageAE.getMoteurAveResultDTO();
   }

   /**
    * @see IServiceCalculAvancementsV2#calculAvancementSimulationAgent(Agent, int, Date, EnumTypeAvancement,
    *      RegleSpecifiqueAE, boolean)
    */
   @Override
    @Transactional(propagation = Propagation.REQUIRED)
   public MoteurAveResultDTO calculAvancementSimulationAgent(final Agent paramAgent, final int paramNbAvctACalculer, final Date paramDateFin,
         final EnumTypeAvancement paramModeAvancement, final RegleSpecifiqueAE paramRegleSpecifique, final boolean paramRetournerListe) {
      ParametrageAE parametrageAE =  new ParametrageAE();

      parametrageAE.getParamMoteur().setModeCreation(EnumModeCreation.SIMULATION);

      List<Agent> listAgent = new ArrayList<Agent>();
      if (paramAgent != null) {
         listAgent.add(paramAgent);
      }
      parametrageAE.getParamMoteur().setListAgent(listAgent);
      parametrageAE.getParamMoteur().setNbAvctACalculer(paramNbAvctACalculer);

      parametrageAE.getParamMoteur().setDateDebut(new Date());
      parametrageAE.getParamMoteur().setDateFin(paramDateFin);
      parametrageAE.getParamMoteur().setTypeAvancement(paramModeAvancement);
      parametrageAE.getParamMoteur().setRegleSpecifique(paramRegleSpecifique);
      parametrageAE.getParamMoteur().setRetournerListe(paramRetournerListe);

      if (listAgent != null && listAgent.isEmpty()) {
         log.debug("Aucun agent selectionné pour la simulation des avancements d'echelon.");
      } else {
         this.launchProcess(parametrageAE);
      }
      return parametrageAE.getMoteurAveResultDTO();
   }
  
   /**
    * @see IServiceCalculAvancementsV2#calculAvancementSimulationAgent(Carriere, int, Date, EnumTypeAvancement,
    *      RegleSpecifiqueAE, boolean)
    */
   @Override
    @Transactional(propagation = Propagation.REQUIRED)
   public MoteurAveResultDTO calculAvancementSimulationAgent(final Carriere paramCarriere, final int paramNbAvctACalculer, final Date paramDateFin,
         final EnumTypeAvancement paramModeAvancement, final RegleSpecifiqueAE paramRegleSpecifique, final boolean paramRetournerListe) {
      ParametrageAE parametrageAE =  new ParametrageAE();

      parametrageAE.getParamMoteur().setModeCreation(EnumModeCreation.SIMULATION);

      List<Carriere> listCarriere = new ArrayList<Carriere>();
      if (paramCarriere != null) {
         listCarriere.add(paramCarriere);
      }
      parametrageAE.getParamMoteur().setListCarriere(listCarriere);
      parametrageAE.getParamMoteur().setNbAvctACalculer(paramNbAvctACalculer);

      parametrageAE.getParamMoteur().setDateDebut(new Date());
      parametrageAE.getParamMoteur().setDateFin(paramDateFin);
      parametrageAE.getParamMoteur().setTypeAvancement(paramModeAvancement);
      parametrageAE.getParamMoteur().setRegleSpecifique(paramRegleSpecifique);
      parametrageAE.getParamMoteur().setRetournerListe(paramRetournerListe);

      if (listCarriere != null && listCarriere.isEmpty()) {
         log.debug("Aucune carrière selectionnée pour la simulation des avancements d'echelon.");
      } else {
         this.launchProcess(parametrageAE);
      }
      return parametrageAE.getMoteurAveResultDTO();
   }

   /**
    * @see IServiceCalculAvancementsV2#calculAvancementSimulationListAgent(List, int, Date, EnumTypeAvancement,
    *      RegleSpecifiqueAE, boolean)
    */
   @Override
    @Transactional(propagation = Propagation.REQUIRED)
   public MoteurAveResultDTO calculAvancementSimulationListAgent(final List<Agent> paramListAgent, final int paramNbAvctACalculer,
         final Date paramDateFin, final EnumTypeAvancement paramModeAvancement, final RegleSpecifiqueAE paramRegleSpecifique,
         final boolean paramRetournerListe) {
      ParametrageAE parametrageAE =  new ParametrageAE();

      parametrageAE.getParamMoteur().setModeCreation(EnumModeCreation.SIMULATION);

      parametrageAE.getParamMoteur().setListAgent(paramListAgent);
      parametrageAE.getParamMoteur().setNbAvctACalculer(paramNbAvctACalculer);

      parametrageAE.getParamMoteur().setDateDebut(new Date());
      parametrageAE.getParamMoteur().setDateFin(paramDateFin);
      parametrageAE.getParamMoteur().setTypeAvancement(paramModeAvancement);
      parametrageAE.getParamMoteur().setRegleSpecifique(paramRegleSpecifique);
      parametrageAE.getParamMoteur().setRetournerListe(paramRetournerListe);

      if (paramListAgent != null && paramListAgent.isEmpty()) {
         log.debug("Aucun agent selectionné pour la simulation des avancements d'echelon.");
      } else {
         this.launchProcess(parametrageAE);
      }

      return parametrageAE.getMoteurAveResultDTO();
   }

   /**
    * @see IServiceCalculAvancementsV2#calculAvancementTableauAE(TableauAE, boolean)
    */
   @Override
    @Transactional(propagation = Propagation.REQUIRED)
   public MoteurAveResultDTO calculAvancementTableauAE(final TableauAE paramTableauAE, boolean paramRetournerListe) {
      ParametrageAE parametrageAE =  new ParametrageAE();

      parametrageAE.getParamMoteur().setModeCreation(EnumModeCreation.CAP);

      parametrageAE.getParamMoteur().setTableauAE(paramTableauAE);

      final Date dateDuJour = new Date();
      if (paramTableauAE.getDateDebut() != null && dateDuJour.after(paramTableauAE.getDateDebut())) {
         // MANTIS 34039 SROM 09/2016 : si la date du jour est après la date de fin du tableau, on borne à la date de fin du tableau
         if (paramTableauAE.getDateFin() != null && dateDuJour.after(paramTableauAE.getDateFin())) {
            parametrageAE.getParamMoteur().setDateDebut(paramTableauAE.getDateFin());
         } else {
            parametrageAE.getParamMoteur().setDateDebut(dateDuJour);
         }
      } else {
         parametrageAE.getParamMoteur().setDateDebut(paramTableauAE.getDateDebut());
      }

      parametrageAE.getParamMoteur().setDateFin(paramTableauAE.getDateFin());
      parametrageAE.getParamMoteur().setTypeAvancement(paramTableauAE.getModeAvancement());
      parametrageAE.getParamMoteur().setRegleSpecifique(paramTableauAE.getRegleSpecifiqueAE());
      parametrageAE.getParamMoteur().setRetournerListe(paramRetournerListe);

      this.launchProcess(parametrageAE);

      return parametrageAE.getMoteurAveResultDTO();
   }

   /**
    * @see IServiceCalculAvancementsV2#calculAvancementTableauAEAndListCarriere(List, TableauAE, boolean, boolean))
    */
   @Override
    @Transactional(propagation = Propagation.REQUIRED)
   public MoteurAveResultDTO calculAvancementTableauAEAndListCarriere(final List<Carriere> paramListCarrirere, final TableauAE paramTableauAE,
         boolean paramRetournerListe, final boolean paramRecalculerPourInjection) {
      ParametrageAE parametrageAE =  new ParametrageAE();

      parametrageAE.getParamMoteur().setModeCreation(EnumModeCreation.CAP);

      parametrageAE.getParamMoteur().setListCarriere(paramListCarrirere);
      parametrageAE.getParamMoteur().setTableauAE(paramTableauAE);

      final Date dateDuJour = new Date();
      if (paramTableauAE.getDateDebut() != null && dateDuJour.after(paramTableauAE.getDateDebut())) {
         // MANTIS 34039 SROM 09/2016 : si la date du jour est après la date de fin du tableau, on borne à la date de fin du tableau
         if (paramTableauAE.getDateFin() != null && dateDuJour.after(paramTableauAE.getDateFin())) {
            parametrageAE.getParamMoteur().setDateDebut(paramTableauAE.getDateFin());
         } else {
            parametrageAE.getParamMoteur().setDateDebut(dateDuJour);
         }
      } else {
         parametrageAE.getParamMoteur().setDateDebut(paramTableauAE.getDateDebut());
      }

      parametrageAE.getParamMoteur().setDateFin(paramTableauAE.getDateFin());
      parametrageAE.getParamMoteur().setTypeAvancement(paramTableauAE.getModeAvancement());
      parametrageAE.getParamMoteur().setRegleSpecifique(paramTableauAE.getRegleSpecifiqueAE());
      parametrageAE.getParamMoteur().setRetournerListe(paramRetournerListe);
      parametrageAE.getParamMoteur().setRecalculerPourInjection(paramRecalculerPourInjection);
      this.launchProcess(parametrageAE);

      return parametrageAE.getMoteurAveResultDTO();
   }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public MoteurAveResultDTO calculChangementChevron(Date paramDateDebut, Date paramDateFin, List<Collectivite> paramListCollectivites, boolean paramRetournerListe) {
       ParametrageAE parametrageAE =  new ParametrageAE();

       parametrageAE.getParamMoteur().setModeCreation(EnumModeCreation.CHEVRON);

       parametrageAE.getParamMoteur().setDateDebut(paramDateDebut);
       parametrageAE.getParamMoteur().setDateFin(paramDateFin);
        // Pour le changement de chevron, on se base sur les durées maxi et la date calculée maxi
        // car le changement de chevron est un avancement de droit comme le maxi.
       parametrageAE.getParamMoteur().setTypeAvancement(EnumTypeAvancement.MAXI);
       parametrageAE.getParamMoteur().setListCollectivites(paramListCollectivites);
       parametrageAE.getParamMoteur().setRetournerListe(paramRetournerListe);

        this.launchProcess(parametrageAE);
        return parametrageAE.getMoteurAveResultDTO();
    }
   // --------------------------------- Partie Calcul ----------------------------//

   /**
    * Lance d'un calcul
    */
   private void launchProcess(ParametrageAE parametrageAE) {
      try {
         // Si on a déja un traitement sur le tableauAE
         if (EnumModeCreation.CAP.equals(parametrageAE.getParamMoteur().getModeCreation())) {
            if (EnumEtatCalculTableau.EN_COURS_CALCUL.equals(parametrageAE.getParamMoteur().getTableauAE().getEtatCalcul())) {
               log.debug("Le moteur est déjà lancé pour ce tableau d'avancement.");
               throw new MoteurAvException(MoteurAvException.Type.AVE_MOTEUR_ALREADY_IN_PROCESS);
            }
         }
         if (parametrageAE.getParamMoteur().getTableauAE()!=null && parametrageAE.getParamMoteur().getTableauAE().getOrganisme()!=null){
               ParamCaOrga paramCaOrga = daoParamCaOrga.findByOrganisme(parametrageAE.getParamMoteur().getTableauAE().getOrganisme());
               if (paramCaOrga == null)
                   throw new MoteurAvException(MoteurAvException.Type.AVE_MISSING_PARAMCAORGA, parametrageAE.getParamMoteur().getTableauAE().getOrganisme().getLibelle());
         }
          
         // En cours de calcul
         // FIXME transactions imbriquées non supportées, changer l'état par appel à une autre méthode de l'uc depuis la couche service de gwt
         this.changeEtatCalculTableauAE(EnumEtatCalculTableau.EN_COURS_CALCUL, parametrageAE);
         this.daoTableauAE.flush(parametrageAE.getParamMoteur().getTableauAE());
         // Selon la mémoire disponible on lance le garbage collector (par prévention)
         this.cleanProcess(parametrageAE);
        
         parametrageAE.setSimulation(EnumModeCreation.SIMULATION.equals(parametrageAE.getParamMoteur().getModeCreation())
               || EnumModeCreation.MAXI.equals(parametrageAE.getParamMoteur().getModeCreation())
                    // // Modif NCHE le 19/06/08 : Nouvelle méthode pour le calcul du changement de chevron
               || EnumModeCreation.CHEVRON.equals(parametrageAE.getParamMoteur().getModeCreation()));
        
            
         parametrageAE.setMoteurAveResultDTO(new MoteurAveResultDTO());
         parametrageAE.setNbAvctCompteur(0);
         parametrageAE.setNbPropositionOffMax(0);
         parametrageAE.setNbSituations(0);

            parametrageAE.setExecTimes(new StringBuffer(1024));
         parametrageAE.setTimeStart(System.currentTimeMillis());
        
            // Modif NCHE le 19/06/08 : Nouvelle méthode pour le calcul du changement de chevron
            boolean calculerAvctEchelon = true;
            if( EnumModeCreation.CHEVRON.equals(parametrageAE.getParamMoteur().getModeCreation()) ) {
                calculerAvctEchelon = false;
            }

            // JBAR - Mantis 14812 - 03/2012
            // Si aucune collectivité n'est renseignée, utilise toute les collectivité employeur de l'organisme
            if ((parametrageAE.getParamMoteur().getListCollectivites() == null || parametrageAE.getParamMoteur().getListCollectivites().isEmpty())
               && parametrageAE.getParamMoteur().getTableauAE()!=null
               && parametrageAE.getParamMoteur().getTableauAE().getOrganisme()!=null  ) {
               parametrageAE.getParamMoteur().setListCollectivites(daoCollectivite.findListCollectiviteEmployeurAndPlanPaieByOrganisme(parametrageAE.getParamMoteur().getTableauAE().getOrganisme().getId()));
            }
        
         // Chargement curseur Situtation + grille indiciaire et echelon suivants
            parametrageAE.setCursorSitutation(this.daoAgent.getCursorFicheGradeEmploi(parametrageAE.getParamMoteur().getListAgent(),
                    parametrageAE.getParamMoteur().getListCarriere(),
                    parametrageAE.getParamMoteur().getModeCreation(),
                    parametrageAE.getParamMoteur().getTableauAE(),
                    parametrageAE.getParamMoteur().getDateDebut(),
                    parametrageAE.getParamMoteur().getListCollectivites(),
                    calculerAvctEchelon));
           
            parametrageAE.setTimeEnd(System.currentTimeMillis());
         log.debug("[CURSEUR FICHE GRADE EMPLOI] Temps d'exécution : " + ((parametrageAE.getTimeEnd() - parametrageAE.getTimeStart()) / 1000) + "s");
         parametrageAE.getExecTimes().append("[CURSEUR FICHE GRADE EMPLOI] Temps d'exécution : " + ((parametrageAE.getTimeEnd() - parametrageAE.getTimeStart()) / 1000) + "s\n");
         // On positiionne les curseurs sur la première ligne
         if (!parametrageAE.getCursorSitutation().next()) {
            log.debug("Il n'y a aucun calcul d'avancement a effectuer");
            // Modif NCHE le 07/02/08 : il faut mettre à jour le top etatCalcul à calculé
            changeEtatCalculTableauAE(EnumEtatCalculTableau.CALCULE, parametrageAE);
            // Fin Modif
            return; // rien a traité
         }
         parametrageAE.setTimeStart(System.currentTimeMillis());
         // Chargement curseur Position Admin.
         parametrageAE.setCursorPositionAdmin(this.daoFichePositionAdmin.getCursorFichePositionAdmin(parametrageAE.getParamMoteur().getListAgent(),
                    parametrageAE.getParamMoteur().getListCarriere(),
                    parametrageAE.getParamMoteur().getModeCreation(),
                    parametrageAE.getParamMoteur().getTableauAE(),
                    parametrageAE.getParamMoteur().getDateDebut(),
                    parametrageAE.getParamMoteur().getListCollectivites(),
                    calculerAvctEchelon));
         parametrageAE.setTimeEnd(System.currentTimeMillis());
         parametrageAE.getExecTimes().append("[CURSEUR FICHE POSITION] Temps d'exécution : " + ((parametrageAE.getTimeEnd() - parametrageAE.getTimeStart()) / 1000) + "s\n");
         log.info("[CURSEUR FICHE POSITION] Temps d'exécution : " + ((parametrageAE.getTimeEnd() - parametrageAE.getTimeStart()) / 1000) + "s");
         // On positiionne les curseurs sur la première ligne
         if (!parametrageAE.getCursorPositionAdmin().next()) {
            log.debug("Il n'y a aucun calcul d'avancement a effectuer car aucune fiche position administraive n'est disponible.");
            // Modif NCHE le 07/02/08 : il faut mettre à jour le top etatCalcul à calculé
            changeEtatCalculTableauAE(EnumEtatCalculTableau.CALCULE, parametrageAE);
            // Fin Modif
            return; // rien a traité
         }

         // Positionnement des variables de rupture "suivant" et "courant"
         // provenant de la première ligne du curseur Situtation
         parametrageAE.setMainSituationCur((SituationAgentMoteurAvancDTO) parametrageAE.getCursorSitutation().getFirstObject());
         parametrageAE.setMainAgentCur(parametrageAE.getMainSituationCur().getAgent());
         parametrageAE.setMainFicGradEmpCur(parametrageAE.getMainSituationCur().getFicheGradeEmploi());

         parametrageAE.setPosAdmSituationCur((SituationAgentMoteurAvancDTO) parametrageAE.getCursorPositionAdmin().getFirstObject());
         parametrageAE.setPosAdmAgentCur(parametrageAE.getPosAdmSituationCur().getAgent());
         parametrageAE.setPosAdmFicGradEmpCur(parametrageAE.getPosAdmSituationCur().getFicheGradeEmploi());
         parametrageAE.setPosAdmFicPosAdminCur(parametrageAE.getPosAdmSituationCur().getFichePositionAdmin());

         // Correction Mantis N°701 (En mode Injection on recalcule la proposition
         // en ne tenant pas compte des propositions officialisées/Maxi
         if (!parametrageAE.getParamMoteur().isRecaculerPourInjection()) {
            parametrageAE.setTimeStart(System.currentTimeMillis());
            // Chargement curseur des Propositions officialisées et maxi
            parametrageAE.setCursorPropositions(this.daoPropositionAE.getCursorPropositions(
                        parametrageAE.getParamMoteur().getListAgent(), parametrageAE.getParamMoteur().getListCarriere(), parametrageAE.getParamMoteur().getModeCreation(),
                        parametrageAE.getParamMoteur().getTableauAE(), parametrageAE.getParamMoteur().getDateDebut(), 
                        parametrageAE.getParamMoteur().getListCollectivites(), calculerAvctEchelon));
            parametrageAE.setTimeEnd(System.currentTimeMillis());
            parametrageAE.getExecTimes().append("[CURSEUR PROPOSITION OFF/MAX] Temps d'exécution : " + ((parametrageAE.getTimeEnd() - parametrageAE.getTimeStart()) / 1000) + "s\n");
            log.info("[CURSEUR PROPOSITION OFF/MAX] Temps d'exécution : " + ((parametrageAE.getTimeEnd() - parametrageAE.getTimeStart()) / 1000) + "s");
            // S'il n'existe pas de propositions officialisées ou au maxi (ou proposition non off mais avec paramétrage "Tenir compte des avancements mini / moyens non officialisés")
            if (parametrageAE.getCursorPropositions().next()) {
               parametrageAE.setPropOffSituationCur((SituationAgentMoteurAvancDTO) parametrageAE.getCursorPropositions().getFirstObject());
               parametrageAE.setPropOffAgentCur(parametrageAE.getPropOffSituationCur().getAgent());
               parametrageAE.setPropOffFicGradEmpCur(parametrageAE.getPropOffSituationCur().getFicheGradeEmploi());
            }
         }
         // Correction Mantis N°701
         if (log.isDebugEnabled()) {
            log.debug("-----------------------------------------");
            log.debug("|    Nouvelle Situtation/Agent           |");
            log.debug("-----------------------------------------");

            log.debug("----- Informations Situtation/Agent ----------------------------------------");
            log.debug("Carriere courante :" + parametrageAE.getMainSituationCur().getCarriere().getId() + " Regroup = "
                  + parametrageAE.getMainFicGradEmpCur().getRegroupement());
            log.debug("Agent courant :" + parametrageAE.getMainAgentCur().getId().trim() + " Matricule : " + parametrageAE.getMainAgentCur().getMatricule() + " Nom :"
                  + parametrageAE.getMainAgentCur().getNom() + " - " + parametrageAE.getMainAgentCur().getPrenom());
            log.debug("Fiche Grade Emploi courante :" + parametrageAE.getMainFicGradEmpCur().getId().trim() + " Date Début : "
                  + UtilsDate.dateToString(parametrageAE.getMainFicGradEmpCur().getDateDebut()) + " Date Fin : "
                  + UtilsDate.dateToString(parametrageAE.getMainFicGradEmpCur().getDateFin()));
            // MANTIS 23433 : gestion emploi
            if (parametrageAE.getMainFicGradEmpCur().getGrade() != null) {
               log.debug("Grade courant:" + parametrageAE.getMainFicGradEmpCur().getGrade().getCode() + " - "
                     + parametrageAE.getMainFicGradEmpCur().getGrade().getLibelleMoyen());
            }
            log.debug("-----------------------------------------");
            log.debug("|    Début Traitment Fiches Grades      |");
            log.debug("-----------------------------------------");
         }
         Boolean hasRupture = false;
         do { // Tratement du curseur Situtation
            parametrageAE.setNbFicheGradEmp(parametrageAE.getNbFicheGradEmp()+1);
            // On avance le curseur des situations
            if (parametrageAE.getCursorSitutation().next()) {
               parametrageAE.setMainSituationNext((SituationAgentMoteurAvancDTO) parametrageAE.getCursorSitutation().getFirstObject());
               parametrageAE.setMainAgentNext(parametrageAE.getMainSituationNext().getAgent());
               parametrageAE.setMainFicGradEmpNext(parametrageAE.getMainSituationNext().getFicheGradeEmploi());
               // Test la s'il y'a rupture ou non
               hasRupture = parametrageAE.getMainAgentNext() == null || parametrageAE.getMainFicGradEmpNext() == null || !parametrageAE.getMainAgentCur().getId().equals(parametrageAE.getMainAgentNext().getId())
                     || !parametrageAE.getMainFicGradEmpCur().getId().equals(parametrageAE.getMainFicGradEmpNext().getId());
            } else {
               hasRupture = true;
               parametrageAE.setMainSituationNext(null);
               parametrageAE.setMainAgentNext(null);
               parametrageAE.setMainFicGradEmpNext(null);
            }
            // Calcul date entrée echelon et reliquat
            this.calculDateEntreeEchelon(parametrageAE, hasRupture);
            // S'il y a rupture
            if (hasRupture) {
               if (log.isDebugEnabled()) {
                  log.debug("----- Date Entrée Echelon calculée ----------------------------------------");
                  log.debug("Date Entrée Echelon " + UtilsDate.dateToString(parametrageAE.getDateEntreeEchelon()));
                  log.debug("Reliquat :" + parametrageAE.getReliquat());
                  log.debug("Date Forcée :" + UtilsDate.dateToString(parametrageAE.getDateForceeEchelon()));
                  // Modif NCHE le 07/02/08 : Ajout d'une date de base
                  log.debug("Date de base de calcul :" + UtilsDate.dateToString(parametrageAE.getDateBase()));
                  // Fin Modif NCHE

               }
               parametrageAE.setNbSituations(parametrageAE.getNbSituations()+1);

               try {
                  if (log.isDebugEnabled()) {
                     log.debug(parametrageAE.getNbFicheGradEmp() + " Fiches Grade Emploi traitées.");
                     log.debug("---------------------------------------");
                     log.debug("|    Fin Traitment Fiches Grades     |");
                     log.debug("---------------------------------------");
                  }
                  // Regarde si la ligne courante du curseur de proposition offic. ou max
                  // si la proposition est déja calculée pour cette situation
                  this.searchPropositionWithDateAvancementMax(parametrageAE);
                  //MANTIS 0016751 Test lors de l'init de la proposition sur l'échelon et l'échelon nouveau
                  //Dans le cas classique, si l'echelon maxi est déjà atteint, on ne rentre pas dans le process de dessus
                  //Dans le cas de proposition existante, si l'echelon maxi est déjà atteint, on ne sauvegarde pas la simulation
                  //mais obliger d'aller jusqu'au bout pour garder les curseurs synchronisés...
                  boolean propositionWithEchelonMaxiAtteint = this.preInitPropositionOfficialiseeMaxi(parametrageAE);
                   //Initialise la proposition avec les données déjà connues
                        this.initProposition(parametrageAE);
                        // Exécute le calcul sur les fiches positions administratives
                        this.calculFichePositionAdmin(parametrageAE);
                        this.finalizeProposition(parametrageAE);
                        if (propositionWithEchelonMaxiAtteint){
                            this.saveOrSimulateProposition(parametrageAE);   
                  }
               } catch (MoteurAvException e) {
                  parametrageAE.setPropositionErreur(true);
                  this.finalizeProposition(parametrageAE);
                  this.saveProposition(parametrageAE);
               } finally {
                  // Réinitialisation des variables d'instance
                  this.cleanProcess(parametrageAE);
               }
               if (parametrageAE.getMainSituationNext() != null) {
                  if (log.isDebugEnabled()) {
                     log.debug("-----------------------------------------");
                     log.debug("|    Nouvelle Situtation/Agent           |");
                     log.debug("-----------------------------------------");

                     log.debug("----- Informations Situtation/Agent ----------------------------------------");
                     log.debug("Carriere courante :" + parametrageAE.getMainSituationNext().getCarriere().getId() + " Regroup = "
                           + parametrageAE.getMainFicGradEmpCur().getRegroupement());
                     log.debug("Agent courante :" + parametrageAE.getMainAgentNext().getId().trim() + " Matricule : " + parametrageAE.getMainAgentNext().getMatricule()
                           + " Nom :" + parametrageAE.getMainAgentNext().getNom() + " - " + parametrageAE.getMainAgentNext().getPrenom());
                     log.debug("Fiche Grade Emploi courante :" + parametrageAE.getMainFicGradEmpNext().getId().trim() + " Date Début : "
                           + UtilsDate.dateToString(parametrageAE.getMainFicGradEmpNext().getDateDebut()) + " Date Fin : "
                           + UtilsDate.dateToString(parametrageAE.getMainFicGradEmpNext().getDateFin()));
                     log.debug("Grade courant:" + parametrageAE.getMainFicGradEmpNext().getGrade().getCode() + " - "
                           + parametrageAE.getMainFicGradEmpNext().getGrade().getLibelleMoyen());

                     log.debug("-----------------------------------------");
                     log.debug("|    Début Traitment Fiches Grades      |");
                     log.debug("-----------------------------------------");
                  }
               }
            }
            // Sauvegarde la situation et fiche précédente
            parametrageAE.setMainSituationPrev(parametrageAE.getMainSituationCur());
            parametrageAE.setMainSituationCur(parametrageAE.getMainSituationNext());
            parametrageAE.setMainFicGradEmpCur(parametrageAE.getMainFicGradEmpNext());
            parametrageAE.setMainAgentCur(parametrageAE.getMainAgentNext());

         } while (!parametrageAE.getCursorSitutation().isAfterLast());
         parametrageAE.getParamMoteur().logIt();
         this.logIt(parametrageAE);
         changeEtatCalculTableauAE(EnumEtatCalculTableau.CALCULE, parametrageAE);
      } catch (MoteurAvException e) {
         throw e;
      } catch (Exception e) {
            log.error(e.getMessage(), e);
         this.changeEtatCalculTableauAE(parametrageAE.getEtatCalculOrigine(), parametrageAE);
         throw new TechnicalException(TechnicalException.Type.UNEXPECTED_EXCEPTION, e.getCause());
      } finally {
         this.closeAllCurseurs(parametrageAE);
      }
   }

   /**
    * Fermeture des curseurs
    */
   private void closeAllCurseurs(ParametrageAE parametrageAE) {
      // Fermture des curseurs
      if (parametrageAE.getCursorSitutation() != null) {
         try {
            parametrageAE.getCursorSitutation().close();
            log.info("[CURSEUR FICHE GRADE EMPLOI] Fermé");
         } catch (Exception e) {
            log.error("[CURSEUR FICHE GRADE EMPLOI] Impossible de fermer le curseur");
         }
         parametrageAE.setCursorSitutation(null);
     }
      if (parametrageAE.getCursorPositionAdmin() != null) {
         try {
            parametrageAE.getCursorPositionAdmin().close();
            log.info("[CURSEUR FICHE POSITION] Fermé");
         } catch (Exception e) {
            log.error("[CURSEUR FICHE  POSITION] Impossible de fermer le curseur");
         }
         parametrageAE.setCursorPositionAdmin(null);
      }
      if (parametrageAE.getCursorPropositions() != null) {
         try {
            parametrageAE.getCursorPropositions().close();
            log.info("[CURSEUR PROPOSITION OFF/MAX] Fermé");
         } catch (Exception e) {
            log.error("[CURSEUR PROPOSITION OFF/MAX] Impossible de fermer le curseur");
         }
         parametrageAE.setCursorPropositions(null);
      }
   }

   /**
    * Change l'état de calcul du tableau AE en paramètre du moteur
    *
    * @param etatCalcul
    */
   private void changeEtatCalculTableauAE(final EnumEtatCalculTableau etatCalcul, ParametrageAE parametrageAE) {
      if (etatCalcul == null)
         return;
       if (EnumModeCreation.CAP.equals(parametrageAE.getParamMoteur().getModeCreation())) {
            // Si calcul d'un tableau ae et que l'on n'est pas en RecalculerPourInjection,
            // on change l'état
            if (parametrageAE.getParamMoteur().getTableauAE() != null) {
                if (!parametrageAE.getParamMoteur().isRecaculerPourInjection()) {
                    TableauAE tableauAE= parametrageAE.getParamMoteur().getTableauAE();
                    // Récupère l'ancienne valeur Etat Calcul
                    parametrageAE.setEtatCalculOrigine(tableauAE.getEtatCalcul());

                    //MANTIS 14478 - MAJ du statut en une seule fois et avec le meme objet de la session hibernate
                    //Integer idTableauAe = tableauAE.getId();
                    //Modifie l'état calcul du tableau AE
                    //this.daoTableauAE.changeEtatCalculTableauAE(idTableauAe, etatCalcul);
                    //// Supprime dans le cache le tableau (puisque mis a jour)
                    //this.daoTableauAE.flush(tableauAE);
                    //this.parametrageAE.getParamMoteur().setTableauAE(null);

                    // Récupère la nouvelle version du tableauAE
                    tableauAE.setEtatCalcul(etatCalcul);
                    this.daoTableauAE.save(tableauAE);
                    tableauAE= this.daoTableauAE.loadTableauAE(tableauAE.getId());
                    parametrageAE.getParamMoteur().setTableauAE(tableauAE);
                }
            }
        }   
   }

   /**
    * Mets à null toutes les variables de travail et teste la mémoire courante Le garbage collector sera exécuté si la
    * mémoire disponible max est inférieur a 15% de la mémoire max
    */
   private void cleanProcess(ParametrageAE parametrageAE) {
     
      parametrageAE.getListFicPosAdm().clear();
      parametrageAE.setNbAvctCompteur(0);
      parametrageAE.setNbFicheGradEmp(0);
      parametrageAE.setPropositionAE(null);
     parametrageAE.setPropositionPrev(null);
      parametrageAE.setDateEntreeEchelon(null);
      parametrageAE.setReliquat(new Duration());
      parametrageAE.setDateForceeEchelon(null);
      // Modif NCHE le 07/02/08 : Ajout d'une date de base
      parametrageAE.setDateBase(null);
      parametrageAE.setSaveProposition(true);
      parametrageAE.setPropositionErreur(false);
      parametrageAE.setFindWithMainCarriere(false);
      parametrageAE.setTraiterCarriere(true);
      parametrageAE.setDateAvancementMiniImpossible(false);
      parametrageAE.setDateAvancementMoyenImpossible(false);

      parametrageAE.setPresenceRetardMoteur(null);
      parametrageAE.setMsgErreurProposition(null);
      parametrageAE.setGrindActuelle(null);
      parametrageAE.setGrindNouvelle(null);
      parametrageAE.setEchelonActuel(null);
      parametrageAE.setEchelonNouveau(null);
      parametrageAE.setChevronActuel(null);
      parametrageAE.setChevronNouveau(null);
      parametrageAE.setIndiceBrutAcutel(null);
      parametrageAE.setIndiceMajoreAcutel(null);
      parametrageAE.setMontantHorsEchelleAcutel(null);
      parametrageAE.setNbJoursAbsences(BigDecimal.ZERO);
      parametrageAE.setNbJoursAbsencesMin(BigDecimal.ZERO);
      parametrageAE.setNbJoursAbsencesMoy(BigDecimal.ZERO);
      parametrageAE.setNbJoursConditionAvctMaxi(BigDecimal.ZERO);
      parametrageAE.setNbJoursConditionAvctMini(BigDecimal.ZERO);
      parametrageAE.setNbJoursConditionAvctMoyen(BigDecimal.ZERO);
      parametrageAE.setNbJoursPresencesGlobalMin(BigDecimal.ZERO);
      parametrageAE.setNbJoursPresencesGlobalMoy(BigDecimal.ZERO);
      parametrageAE.setNbJoursRecrutement(BigDecimal.ZERO);
      parametrageAE.setNbJoursRecrutementMoy(BigDecimal.ZERO);
      parametrageAE.setNbJoursRecrutementMoy(BigDecimal.ZERO);

      long freeMemoryMax = runtime.maxMemory() - runtime.totalMemory() + runtime.freeMemory();
      long pctFreeMemory = (freeMemoryMax * 100 / runtime.maxMemory());
      log.info("------------------ Stats Mémoire ---------------------------");
      log.info("Mémoire disponible : " + freeMemoryMax / 1024 + "ko");
      log.info("Mémoire maximale : " + runtime.maxMemory() / 1024 + "ko");
      log.info("Pourcentage mémoire disponible : " + pctFreeMemory + "%");
      log.info("------------------------------------------------------------");

      if (pctFreeMemory < 15) {
         log.warn("Mémoire faible, le garbage collector est forcé");
         System.gc();
      }

   }

   /**
    * Termine le processus sur la proposition actuelle
    */
   private void saveOrSimulateProposition(ParametrageAE parametrageAE) {
      // Lancement de la simulation uniquement en SIMULATION et en MAXI
      if (checkContinueToSimulateProposition(parametrageAE)) {
         // Finalise la proposition
         this.saveProposition(parametrageAE);

         if (parametrageAE.getSimulation()) {
            if (!parametrageAE.getPropositionErreur()) {
               this.launchSimulation(parametrageAE); // lancement simulation
            }
         }
      }
   }


    /**
     * Retient la date d'entrée dans l'echelon et le reliquat si les variables d'instances dateEntreeEchelon et reliquat
     * ne sont pas déjà renseignées (Méthode se basant sur les données provenant du curseur traité par le service)
     */
    private void calculDateEntreeEchelon(ParametrageAE parametrageAE, Boolean hasRupture) {
        FicheGradeEmploi ficGradeEmpPrincipal = parametrageAE.getMainSituationCur().getFicheGradeEmploi();
        FicheGradeEmploi ficGradeEmpHCur = parametrageAE.getMainSituationCur().getFicheGradeEmploiHisto();

        FicheGradeEmploi ficGradeEmpHPrev = null;
        if (parametrageAE.getMainSituationPrev() != null) {
            ficGradeEmpHPrev = parametrageAE.getMainSituationPrev().getFicheGradeEmploiHisto();
        }

        boolean diffGrade = true;
        boolean diffEchelon = true;
        boolean diffCarrieres = true;
        boolean diffRegroupement = true;

        FicheGradeEmploi ficGradeEmpHNext = null;
        if (parametrageAE.getMainSituationNext() != null && !hasRupture) {
            ficGradeEmpHNext = parametrageAE.getMainSituationNext().getFicheGradeEmploiHisto();

            // MANTIS 16363 SROM 10/2012 : diffGrade doit prendre en compte les emplois secondaires (sinon plantage NPE plus tard sur des dates à null)
            diffGrade = ((ficGradeEmpHCur.getGrade() != null
                        && ficGradeEmpHNext.getGrade() != null
                          && !ficGradeEmpHCur.getGrade().getCode().equals(ficGradeEmpHNext.getGrade().getCode()))
                       || (ficGradeEmpHCur.getEmploiSecondaire() != null
                        && ficGradeEmpHNext.getEmploiSecondaire() != null
                          && !ficGradeEmpHCur.getEmploiSecondaire().getCode().equals(ficGradeEmpHNext.getEmploiSecondaire().getCode()))
                      || (ficGradeEmpHCur.getEmploiSecondaire() != null && ficGradeEmpHNext.getGrade() != null)
                       || (ficGradeEmpHCur.getGrade() != null && ficGradeEmpHNext.getEmploiSecondaire() != null));

            // Modif NCHE le 19/06/08 : Nouvelle méthode pour le calcul du changement de chevron
            if( EnumModeCreation.CHEVRON.equals(parametrageAE.getParamMoteur().getModeCreation()) ) {
                boolean diffChevron = (ficGradeEmpHCur.getChevron() != null && ficGradeEmpHNext.getChevron() != null
                        && !ficGradeEmpHCur.getChevron().getCode().equals(ficGradeEmpHNext.getChevron().getCode()));
               
                diffEchelon = (ficGradeEmpHCur.getEchelon() != null && ficGradeEmpHNext.getEchelon() != null
                        && !ficGradeEmpHCur.getEchelon().getCode().equals(ficGradeEmpHNext.getEchelon().getCode()));
                
                diffEchelon = diffEchelon || diffChevron;
            }
            else {
                diffEchelon = (ficGradeEmpHCur.getEchelon() != null && ficGradeEmpHNext.getEchelon() != null
                        && !ficGradeEmpHCur.getEchelon().getCode().equals(ficGradeEmpHNext.getEchelon().getCode()));
            }
            // Fin Modif NCHE le 19/06/08

            diffCarrieres = ficGradeEmpHCur.getCarriere() != null && ficGradeEmpHNext.getCarriere() != null
                    && !ficGradeEmpHCur.getCarriere().getId().equals(ficGradeEmpHNext.getCarriere().getId());

            diffRegroupement = (ficGradeEmpHCur.getRegroupement() == null && ficGradeEmpHNext.getRegroupement() == null)
                    || (ficGradeEmpHCur.getRegroupement() != null && ficGradeEmpHNext.getRegroupement() != null
                            && !ficGradeEmpHCur.getRegroupement().equals(ficGradeEmpHNext.getRegroupement()));

        }

        boolean samePrevCarriere = ficGradeEmpHPrev != null
                && ficGradeEmpHCur.getCarriere() != null
                && ficGradeEmpHPrev.getCarriere() != null
                && ficGradeEmpHCur.getCarriere().getId().equals(ficGradeEmpHPrev.getCarriere().getId())
                && (ficGradeEmpHCur.getRegroupement() == null && ficGradeEmpHPrev.getRegroupement() == null || ficGradeEmpHCur.getRegroupement() != null
                        && ficGradeEmpHPrev.getRegroupement() != null
                        && ficGradeEmpHCur.getRegroupement().equals(ficGradeEmpHPrev.getRegroupement()));

        // Si il existe une rupture entre la fiche précédente et la fiche courante
        // ->on traitera la fiche de la carrière courante
        if (!samePrevCarriere) {
           parametrageAE.setTraiterCarriere(true);
        }
        if (log.isDebugEnabled()) {
            log.debug("----- Fiche Grade Emploi Histo courant -----------------------------------");
            log.debug("id fiche curr : " + ficGradeEmpHCur.getId());
            log.debug("id Carriere curr : " + ficGradeEmpHCur.getCarriere().getId());
            log.debug("Regroupement curr : " + ficGradeEmpHCur.getRegroupement());
            log.debug("Date Debut curr :" + UtilsDate.dateToString(ficGradeEmpHCur.getDateDebut()) + " - Date Fin :"
                    + UtilsDate.dateToString(ficGradeEmpHCur.getDateFin()));
            log.debug("Date Entree Echelon curr :" + UtilsDate.dateToString(ficGradeEmpHCur.getDateEntreeEchelon()));
            log.debug("Reliquat curr :" + new Duration(ficGradeEmpHCur.getReliquat()).toString());
            if (ficGradeEmpHCur.getGrade() != null) {
                log.debug("Code Grade :" + ficGradeEmpHCur.getGrade().getCode());
            }
            if (ficGradeEmpHCur.getEchelon() != null) {
                log.debug("Code Echelon :" + ficGradeEmpHCur.getEchelon().getCodeEchelonWithChevron(ficGradeEmpHCur.getChevron()));
            }
            log.debug("----- Fiche Grade Emploi Histo suivante -----------------------------------");
            if (ficGradeEmpHNext != null) {
                log.debug("id fiche next : " + ficGradeEmpHNext.getId());
                log.debug("id Carriere next : " + ficGradeEmpHNext.getCarriere().getId());
                log.debug("Regroupement next : " + ficGradeEmpHNext.getRegroupement());
                log.debug("Date Debut next :" + UtilsDate.dateToString(ficGradeEmpHNext.getDateDebut()) + " - Date Fin :"
                        + UtilsDate.dateToString(ficGradeEmpHNext.getDateFin()));
                log.debug("Date Entree Echelon next :" + UtilsDate.dateToString(ficGradeEmpHNext.getDateEntreeEchelon()));
                log.debug("Reliquat next :" + new Duration(ficGradeEmpHNext.getReliquat()).toString());
                if (ficGradeEmpHNext.getGrade() != null) {
                    log.debug("Code Grade :" + ficGradeEmpHNext.getGrade().getCode());
                }
                if (ficGradeEmpHNext.getEchelon() != null) {
                    log.debug("Code Echelon :" + ficGradeEmpHNext.getEchelon().getCodeEchelonWithChevron(ficGradeEmpHNext.getChevron()));
                }
            }
        }
       
        // STH+NCHE - 16/04/2009 - Plantage du moteur si fiche sans grade ou statut sans avancement
        if (!parametrageAE.getMainSituationCur().getStatutAutoriseAvEch() || ficGradeEmpHCur.getGrade() == null || ficGradeEmpHCur.getEchelon() == null) {
            log.debug(">>>>>>  Statut n'autorisant pas l'avancement ou pas de grade/échelon <<<<<<<<< \n");
            parametrageAE.setTraiterCarriere(false);
        }

        // Doublon
        // STH+NCHE - 16/04/2009 - Plantage du moteur si fiche sans grade ou statut sans avancement
//        if (!mainSituationCur.getStatutAutoriseAvEch() || ficGradeEmpHCur.getGrade() == null || ficGradeEmpHCur.getEchelon() == null) {
//            log.debug(">>>>>>  Statut n'autorisant pas l'avancement ou pas de grade/échelon <<<<<<<<< \n");
//            this.traiterCarriere = false;
//        }

        // Si on a déjà la DEE ou Reliquat, on shunt
        // ou si on ne doit pas traiter la fiche grade de cette carrière
        if (parametrageAE.isFindWithMainCarriere() || !parametrageAE.isTraiterCarriere()) {
            log.debug(">>>>>>  On ne traite pas cette carrière <<<<<<<<< \n");
            return;
        }

        boolean sameMainCarriere = ficGradeEmpPrincipal.getCarriere() != null && ficGradeEmpHCur.getCarriere() != null
                && ficGradeEmpPrincipal.getCarriere().getId().equals(ficGradeEmpHCur.getCarriere().getId());

        boolean sameMainRegroupement = ((ficGradeEmpPrincipal.getRegroupement() == null && ficGradeEmpHCur.getRegroupement() == null) || (ficGradeEmpPrincipal.getRegroupement() != null
                && ficGradeEmpHCur.getRegroupement() != null && ficGradeEmpPrincipal.getRegroupement()
                .equals(ficGradeEmpHCur.getRegroupement())));

        // Modif NCHE le 19/06/08 : Nouvelle méthode pour le calcul du changement de chevron
        boolean sameMainGrade = false;
        if( EnumModeCreation.CHEVRON.equals(parametrageAE.getParamMoteur().getModeCreation()) ) {
            sameMainGrade = ficGradeEmpPrincipal.getGrade().getCode().equals(ficGradeEmpHCur.getGrade().getCode())
                && ficGradeEmpPrincipal.getEchelon().getCode().equals(ficGradeEmpHCur.getEchelon().getCode())
                && ficGradeEmpPrincipal.getChevron().getCode().equals(ficGradeEmpHCur.getChevron().getCode());
        }
        else {
            sameMainGrade = ficGradeEmpPrincipal.getGrade().getCode().equals(ficGradeEmpHCur.getGrade().getCode())
                && ficGradeEmpPrincipal.getEchelon().getCode().equals(ficGradeEmpHCur.getEchelon().getCode());
        }
        // Fin Modif NCHE le 19/06/08
       
        // STH - 22/12/2010 - Le test était faux --> Problème sur les agents multicarrières
        boolean sameMainEchelon = ficGradeEmpPrincipal.getGrade() != null && ficGradeEmpHCur.getGrade() != null
                && ficGradeEmpPrincipal.getEchelon() != null && ficGradeEmpHCur.getEchelon() != null
                && ficGradeEmpPrincipal.getEchelon().getCode().equals(ficGradeEmpHCur.getEchelon().getCode());

        Duration dureeReliquat = null;
        if(ficGradeEmpHCur.getReliquat() != null &&
              !"".equals(ficGradeEmpHCur.getReliquat().trim())){
          try {
               // Conversion reliquat CS en durée
              dureeReliquat = new Duration(ficGradeEmpHCur.getReliquat());
           } catch (TechnicalException e) {
               log.debug("Attention le reliquat est incorrecte, " + e.getMessage());
               log.debug("Fiche Grade Emploi Historisée ID : " + ficGradeEmpHCur.getId());
               log.debug("Le Reliquat sera considéré comme null");
           }
        }

        // Si vérifie si on traite la carrière principale
        // MANTIS 22420 SROM 01/2014 : si la situation est future par rapport à la date de calcul (max(dateJour, dateDebutTableau)), on la prend pas en compte
        Date dateCalcul = UtilsDateGRH.getDateMaxEntreDeuxDates(new Date(), parametrageAE.getParamMoteur().getDateDebut());
        if (sameMainCarriere && sameMainRegroupement) {
           if (UtilsDate.compareByDateOnly(ficGradeEmpHCur.getDateDebut(), dateCalcul) <= 0) {
               log.debug(">> On est sur les fiche de la carrière principale \n");
               // Si le reliquat est renseigné
               if (dureeReliquat != null) {
                   // On récupère la date entrée échelon la plus récente
                  parametrageAE.setDateEntreeEchelon(ficGradeEmpHCur.getDateDebut());
                  parametrageAE.setReliquat(new Duration(ficGradeEmpHCur.getReliquat()));
                  parametrageAE.setDateForceeEchelon(null);
                  parametrageAE.setFindWithMainCarriere(true);
                   // Modif NCHE le 07/02/08 : Ajout d'une date de base
                   // Il n'y a pas de date forcée mais un reliquat donc on prendre les positions admin à partir de la date
                   // de début - reliquat
                   // MANTIS 17312 : SROM 11/2012 : la date de base d'échelon n'est à calculer en trentième
                   parametrageAE.setDateBase(UtilsDureeCarriere.subDureeStringOnDate(parametrageAE.getDateEntreeEchelon(), parametrageAE.getReliquat().toDataBase(), false));
                   // Fin Modif NCHE
               } else if (ficGradeEmpHCur.getDateEntreeEchelon() != null) {
                  parametrageAE.setDateForceeEchelon(ficGradeEmpHCur.getDateEntreeEchelon());
                  parametrageAE.setReliquat(new Duration());
                   parametrageAE.setDateEntreeEchelon(ficGradeEmpHCur.getDateDebut());
                   // Modif NCHE le 07/02/08 : Ajout d'une date de base
                   // Il y a une date forcée donc on prendre les positions admin à partir de la date de début
                   parametrageAE.setDateBase(ficGradeEmpHCur.getDateDebut());
                   // Fin Modif NCHE
                   parametrageAE.setFindWithMainCarriere(true);
                   // Si le reliquat est renseigné
               } else if (diffGrade || diffEchelon || diffCarrieres || diffRegroupement
                       || (parametrageAE.getMainSituationNext() != null && !parametrageAE.getMainSituationNext().getStatutAutoriseAvEch())) {
                   // On récupère la date entrée échelon la plus ancienne
                   if (parametrageAE.getDateEntreeEchelon() == null
                           || (parametrageAE.getDateEntreeEchelon() != null && ficGradeEmpHCur.getDateDebut().before(parametrageAE.getDateEntreeEchelon()))) {
                      parametrageAE.setDateEntreeEchelon(ficGradeEmpHCur.getDateDebut());
                      parametrageAE.setReliquat(new Duration());
                       // Modif NCHE le 07/02/08 : pas de date forcée ni reliquat, date base = date début
                      parametrageAE.setDateBase(ficGradeEmpHCur.getDateDebut());
                       // Fin modif NCHE
                      parametrageAE.setTraiterCarriere(false);
                   }
                   // STH - 22/12/2010 - Problème sur les agents multicarrières : on ne doit pas traiter le suivant si échelon différent, même si la date était déjà renseignée
                   if (!diffCarrieres && !diffRegroupement
                           && (diffGrade || diffEchelon || (parametrageAE.getMainSituationNext() != null && !parametrageAE.getMainSituationNext().getStatutAutoriseAvEch()))) {
                      parametrageAE.setFindWithMainCarriere(true);
                   }
               }
           }
        } else { // Dans les cas des autres carrières
            log.debug(">> On est sur les fiches d'une autre carrière \n");
            // Si on a le même couple Grade/Echelon que la fiche principale
            // il faut tenir compte de cette fiche
            if (sameMainGrade && sameMainEchelon && parametrageAE.getMainSituationCur().getStatutAutoriseAvEch()) {
                // Si la date d'entrée échelon est renseigné
                if (ficGradeEmpHCur.getDateEntreeEchelon() != null) {

                    // On récupère la date entrée échelon la plus récente
                    if (parametrageAE.getDateForceeEchelon() == null
                            || (parametrageAE.getDateForceeEchelon() != null && ficGradeEmpHCur.getDateEntreeEchelon().after(parametrageAE.getDateForceeEchelon()))) {
                       parametrageAE.setDateForceeEchelon(ficGradeEmpHCur.getDateEntreeEchelon());
                       parametrageAE.setReliquat(new Duration());
                       parametrageAE.setDateEntreeEchelon(ficGradeEmpHCur.getDateDebut());
                        // Modif NCHE le 07/02/08 : Ajout d'une date de base
                        // Il y a une date forcée donc on prendre les positions admin à partir de la date de début
                       parametrageAE.setDateBase(ficGradeEmpHCur.getDateDebut());
                        // Fin Modif NCHE
                       parametrageAE.setTraiterCarriere(false);
                    }
                    // Si le reliquat est renseigné
                } else if (dureeReliquat != null) {
                    // On récupère la date entrée échelon la plus récente
                    if (parametrageAE.getDateEntreeEchelon() == null
                            || (parametrageAE.getDateEntreeEchelon() != null && ficGradeEmpHCur.getDateDebut().after(parametrageAE.getDateEntreeEchelon()))) {
                       parametrageAE.setDateEntreeEchelon(ficGradeEmpHCur.getDateDebut());
                       parametrageAE.setReliquat(new Duration(ficGradeEmpHCur.getReliquat()));
                        // Modif NCHE le 07/02/08 : Ajout d'une date de base
                        // Il n'y a pas de date forcée mais un reliquat donc on prendre les positions admin à partir de
                        // la date de début - reliquat
                        // MANTIS 17312 : SROM 11/2012 : la date de base d'échelon n'est à calculer en trentième
                       parametrageAE.setDateBase(UtilsDureeCarriere.subDureeStringOnDate(parametrageAE.getDateEntreeEchelon(), parametrageAE.getReliquat().toDataBase(), false));
                       // Fin Modif NCHE
                       parametrageAE.setTraiterCarriere(false);
                    }
                } else {
                    // On récupère la date entrée échelon la plus ancienne
                    if (parametrageAE.getDateEntreeEchelon() == null
                            || (parametrageAE.getDateEntreeEchelon() != null && ficGradeEmpHCur.getDateDebut().before(parametrageAE.getDateEntreeEchelon()))) {
                       parametrageAE.setDateEntreeEchelon(ficGradeEmpHCur.getDateDebut());
                       parametrageAE.setReliquat(new Duration());
                        // Modif NCHE le 07/02/08 : pas de date forcée ni reliquat, date base = date début
                       parametrageAE.setDateBase(ficGradeEmpHCur.getDateDebut());
                        // Fin modif NCHE
                    }
                    // STH - 22/12/2010 - Problème sur les agents multicarrières : on ne doit pas traiter le suivant si échelon différent, même si la date était déjà renseignée
                    // On ne traite pas la fiche suivante si le couple grade echelon différent
                    if (diffEchelon || diffGrade) {
                       parametrageAE.setTraiterCarriere(false);
                    }
                }
            } else { // aucune fiche de cette carrière ne sera traitée
               parametrageAE.setTraiterCarriere(false);
            }
        }
    }

   /**
    * On se position sur la proposition suivante si et seulement si la situation coinside avec la situtation de la
    * proposition officialisée/maxi (ou proposition non off mais avec paramétrage "Tenir compte des avancements mini / moyens non officialisés")
    */
   private void searchPropositionWithDateAvancementMax(ParametrageAE parametrageAE) {
      if (log.isDebugEnabled()) {
         log.debug("-----------------------------------------------------");
         log.debug("|   Recherche de proposition officialisées/Maxi  (ou proposition non off mais avec paramétrage 'Tenir compte des avancements mini / moyens non officialisés')    |");
         log.debug("-----------------------------------------------------");
      }
     
      // MANTIS 16363 SROM 10/2012 : Sécurisation des curseurs - si le roo_ima_ref de l'agent courant est > au roo_ima_ref de l'agent de la prop actuelle,
      // MANTIS 23773 SROM 05/2014 : Modification du compare, on ne se base plus sur l'id mais sur nom, prenom, matricule
      if (parametrageAE.getPropOffAgentCur() != null
            && parametrageAE.getPropOffFicGradEmpCur() != null
            && parametrageAE.getMainAgentCur().compareNomPrenomMatriculeAsc(parametrageAE.getPropOffAgentCur()) > 0) {
         // MANTIS 25508 SROM 10/2014 : Suite du MANTIS 23773... Modification du compare, on ne se base plus sur l'id mais sur nom, prenom, matricule
         while (!parametrageAE.getCursorPropositions().isAfterLast() && parametrageAE.getMainAgentCur().compareNomPrenomMatriculeAsc(parametrageAE.getPropOffAgentCur()) > 0) {
            if (parametrageAE.getCursorPropositions().next()) {
               parametrageAE.setPropOffSituationNext((SituationAgentMoteurAvancDTO) parametrageAE.getCursorPropositions().getFirstObject());
               parametrageAE.setPropOffAgentNext(parametrageAE.getPropOffSituationNext().getAgent());
               parametrageAE.setPropOffFicGradEmpNext(parametrageAE.getPropOffSituationNext().getFicheGradeEmploi());
            } else {
               parametrageAE.setPropOffSituationNext(null);
               parametrageAE.setPropOffAgentNext(null);
               parametrageAE.setPropOffFicGradEmpNext(null);
            }
            // Sauvegarde la situtation et fiche précédente
            parametrageAE.setPropOffSituationCur(parametrageAE.getPropOffSituationNext());
            parametrageAE.setPropOffFicGradEmpCur(parametrageAE.getPropOffFicGradEmpNext());
            parametrageAE.setPropOffAgentCur(parametrageAE.getPropOffAgentNext());
         }
      }
     
      // On vérfie s'il y a rupture donc pas de proposition officialisée/maxi
      if (parametrageAE.getPropOffAgentCur() == null
            || parametrageAE.getPropOffFicGradEmpCur() == null
            || !parametrageAE.getMainAgentCur().getId().equals(parametrageAE.getPropOffAgentCur().getId())
            || !parametrageAE.getMainFicGradEmpCur().getId().equals(parametrageAE.getPropOffFicGradEmpCur().getId())) {
         // msgErreurProposition.append("Pas de proposition officialisée ou au maxi pour sur cette sitatution.");
         log.debug("Pas de proposition officialisée ou au maxi pour sur cette sitatution.");
         return;
      }

        // GIFT FR 19804 - STH - 04/11/2009 - Ne traiter une proposition officialisée que si même grade que grade courant
        // 1. Récupérer grade actuel
        Grade gradeActuel = parametrageAE.getMainFicGradEmpCur() != null ? parametrageAE.getMainFicGradEmpCur().getGrade() : null;
        Grade gradePropOff = null;
        if (log.isDebugEnabled()) {
            log.debug("gradeActuel = " + (gradeActuel != null ? gradeActuel.getCode() : "<null>"));
        }
     
      PropositionAE propositionMax = null;
      Boolean hasRupture = false;
      do { // Tratement du curseur Situtation
         // On avance le curseur des situations
         parametrageAE.setNbPropositionOffMaxTotal(parametrageAE.getNbPropositionOffMaxTotal()+1);
         if (parametrageAE.getCursorPropositions().next()) {
            parametrageAE.setPropOffSituationNext((SituationAgentMoteurAvancDTO) parametrageAE.getCursorPropositions().getFirstObject());
            parametrageAE.setPropOffAgentNext(parametrageAE.getPropOffSituationNext().getAgent());
            parametrageAE.setPropOffFicGradEmpNext(parametrageAE.getPropOffSituationNext().getFicheGradeEmploi());
            // Test la s'il y'a rupture ou non
            hasRupture = parametrageAE.getPropOffAgentNext() == null || parametrageAE.getPropOffFicGradEmpNext() == null
                  || !parametrageAE.getPropOffAgentCur().getId().equals(parametrageAE.getPropOffAgentNext().getId())
                  || !parametrageAE.getPropOffFicGradEmpCur().getId().equals(parametrageAE.getPropOffFicGradEmpNext().getId());
         } else {
            hasRupture = true;
            parametrageAE.setPropOffSituationNext(null);
            parametrageAE.setPropOffAgentNext(null);
            parametrageAE.setPropOffFicGradEmpNext(null);
         }

            // GIFT FR 19804 - STH - 04/11/2009 - Ne traiter une proposition officialisée que si même grade que grade courant
         // 2. Récupérer grade proposition off
           gradePropOff = parametrageAE.getPropOffSituationCur() != null && parametrageAE.getPropOffSituationCur().getPropositionAE() != null ? parametrageAE.getPropOffSituationCur().getPropositionAE().getGrade() : null;
           if (log.isDebugEnabled()) {
               log.debug("gradePropOff = " + (gradePropOff != null ? gradePropOff.getCode() : "<null>"));
           }
        
            // GIFT FR 19804 - STH - 04/11/2009 - Ne traiter une proposition officialisée que si même grade que grade courant
         // 3. Comparer
            if (gradeActuel != null && gradeActuel.equals(gradePropOff)) {
               
             // On recherche la date d'avancement max
             if (propositionMax == null
                   || (propositionMax.getDateAvancement().before(parametrageAE.getPropOffSituationCur().getPropositionAE().getDateAvancement()))) {
                
                propositionMax = parametrageAE.getPropOffSituationCur().getPropositionAE();
                parametrageAE.setGrindActuelle(parametrageAE.getPropOffSituationCur().getGrilleIndiciaire());
                    // Modif NCHE le 20/10/08 : gestion des hors échelle
                    // Stocke les echelon/chevron actuels et nouveaux
                parametrageAE.setEchelonActuel(parametrageAE.getGrindActuelle().getEchelonOrdonne().getEchelon());
                parametrageAE.setChevronActuel(parametrageAE.getGrindActuelle().getEchelonOrdonne().getChevron());
                    // Si le chevron suivant est indiqué sur la grille indiciaire, on prendra celui-ci, sinon on prendra celui de l'échelon ordonné
                    if( parametrageAE.getGrindActuelle() != null && parametrageAE.getGrindActuelle().getChevronSuivant() != null ) {
                       parametrageAE.setEchelonNouveau(parametrageAE.getGrindActuelle().getChevronSuivant().getEchelon());
                       parametrageAE.setChevronNouveau(parametrageAE.getGrindActuelle().getChevronSuivant().getChevron());
                        // il faut aussi récupérer la nouvelle grille indiciaire par rapport au nouvel echelon
                       parametrageAE.setGrindNouvelle(daoGrilleIndiciaire.findGrilleIndiciaireByEchelle(parametrageAE.getGrindActuelle().getEchelle(), parametrageAE.getEchelonNouveau(), parametrageAE.getChevronNouveau()));
                    } else {
                       parametrageAE.setGrindNouvelle(parametrageAE.getPropOffSituationCur().getNewGrilleIndiciaire());
                       parametrageAE.setEchelonNouveau(parametrageAE.getGrindNouvelle().getEchelonOrdonne().getEchelon());
                       parametrageAE.setChevronNouveau(parametrageAE.getGrindNouvelle().getEchelonOrdonne().getChevron());
                    }
                    // Fin Modif NCHE le 20/10/08            
             }
            }

         // S'il y a rupture
         if (hasRupture) {
            if (propositionMax == null) {
               log.debug("####>>>>Aucune proposition max trouvée sur le même grade.<<<<####");
            } else if (propositionMax.getDateAvancement() == null) {
                    log.debug("####>>>>Proposition trouvée avec une date Avancement nulle : id " + propositionMax.getId() + ".<<<<####");
                } else {
                   parametrageAE.setPropositionPrev(propositionMax);
               log.debug("Proposition trouvée : Id = " + propositionMax.getId()
                            + ", Date Avancement = " + UtilsDate.dateToString(propositionMax.getDateAvancement())
                            + ", sur l'échelon " + propositionMax.getEchelonNouveau().getCodeEchelonWithChevron(propositionMax.getChevronNouveau()));
            }
            parametrageAE.setNbPropositionOffMax(parametrageAE.getNbPropositionOffMax()+1);
         }
         // Sauvegarde la situtation et fiche précédente
         parametrageAE.setPropOffSituationCur(parametrageAE.getPropOffSituationNext());
         parametrageAE.setPropOffFicGradEmpCur(parametrageAE.getPropOffFicGradEmpNext());
         parametrageAE.setPropOffAgentCur(parametrageAE.getPropOffAgentNext());
      } while (!hasRupture && !parametrageAE.getCursorPropositions().isAfterLast());
   }

   /**
    * Traite le curseur des fiches position admin.
    */
   private void calculFichePositionAdmin(ParametrageAE parametrageAE) {
      // On vérfie s'il y a rupture donc pas de fiche position admin
      if (parametrageAE.getPosAdmAgentCur() == null || parametrageAE.getPosAdmFicGradEmpCur() == null || !parametrageAE.getMainAgentCur().getId().equals(parametrageAE.getPosAdmAgentCur().getId())
            || !parametrageAE.getMainFicGradEmpCur().getId().equals(parametrageAE.getPosAdmFicGradEmpCur().getId())) {
         parametrageAE.getMsgErreurProposition().append("Pas de fiche position administrative au " + UtilsDate.dateToString(parametrageAE.getParamMoteur().getDateDebut())
               + " à traiter.");
         log.debug("Pas de fiche position administrative au " + UtilsDate.dateToString(parametrageAE.getParamMoteur().getDateDebut()) + " à traiter.");
         throw new MoteurAvException(MoteurAvException.Type.AVE_MOTEUR_NO_FICHE_POS_ADM, parametrageAE.getParamMoteur().getDateDebut());
      }
      Boolean hasRupture = false;
      do {
         // Tratement du curseur Situtation
         // On stocke les fiches pos. adm. pour la simulation
         parametrageAE.getListFicPosAdm().add(parametrageAE.getPosAdmFicPosAdminCur());
         // Positionnement du cursuer des fiches position admin au suivant
         if (parametrageAE.getCursorPositionAdmin().next()) {
            parametrageAE.setPosAdmSituationNext((SituationAgentMoteurAvancDTO) parametrageAE.getCursorPositionAdmin().getFirstObject());
            parametrageAE.setPosAdmAgentNext(parametrageAE.getPosAdmSituationNext().getAgent());
            parametrageAE.setPosAdmFicGradEmpNext(parametrageAE.getPosAdmSituationNext().getFicheGradeEmploi());
            parametrageAE.setPosAdmFicPosAdminNext(parametrageAE.getPosAdmSituationNext().getFichePositionAdmin());
            // Test la s'il y'a rupture ou non
            hasRupture = parametrageAE.getPosAdmAgentNext() == null || parametrageAE.getPosAdmFicGradEmpNext() == null
                  || !parametrageAE.getPosAdmAgentCur().getId().equals(parametrageAE.getPosAdmAgentNext().getId())
                  || !parametrageAE.getPosAdmFicGradEmpCur().getId().equals(parametrageAE.getPosAdmFicGradEmpNext().getId());
         } else {
            hasRupture = true;
            parametrageAE.setPosAdmSituationNext(null);
            parametrageAE.setPosAdmAgentNext(null);
            parametrageAE.setPosAdmFicGradEmpNext(null);
         }
         // Sauvegarde la situtation et fiche précédente
         parametrageAE.setPosAdmSituationCur(parametrageAE.getPosAdmSituationNext());
         parametrageAE.setPosAdmAgentCur(parametrageAE.getPosAdmAgentNext());
         parametrageAE.setPosAdmFicGradEmpCur(parametrageAE.getPosAdmFicGradEmpNext());
         parametrageAE.setPosAdmFicPosAdminCur(parametrageAE.getPosAdmFicPosAdminNext());

      } while (!hasRupture && !parametrageAE.getCursorPositionAdmin().isAfterLast());
      log.debug(parametrageAE.getListFicPosAdm().size() + " Fiches Position Administrative ont été trouvées et stockées.");

      this.calculFichePositionAdminList(parametrageAE);
   }

   /**
    * Traite le curseur des fiches position admin.
    */
   private void calculFichePositionAdminList(ParametrageAE parametrageAE) {
        // Modif NCHE le 19/06/08 : Nouvelle méthode pour le calcul du changement de chevron
        Long dureeMinAA = parametrageAE.getGrindActuelle().getDureeMiniAA();
        Long dureeMinMM = parametrageAE.getGrindActuelle().getDureeMiniMM();
        Long dureeMoyAA = parametrageAE.getGrindActuelle().getDureeMoyenAA();
        Long dureeMoyMM = parametrageAE.getGrindActuelle().getDureeMoyenMM();
        Long dureeMaxAA = parametrageAE.getGrindActuelle().getDureeMaxiAA();
        Long dureeMaxMM = parametrageAE.getGrindActuelle().getDureeMaxiMM();

        if(  EnumModeCreation.CHEVRON.equals(parametrageAE.getParamMoteur().getModeCreation()) ) {
            dureeMinAA = parametrageAE.getGrindActuelle().getDureeMiniAAChevron();
            dureeMinMM = parametrageAE.getGrindActuelle().getDureeMiniMMChevron();
            dureeMoyAA = parametrageAE.getGrindActuelle().getDureeMoyenAAChevron();
            dureeMoyMM = parametrageAE.getGrindActuelle().getDureeMoyenMMChevron();
            dureeMaxAA = parametrageAE.getGrindActuelle().getDureeMaxiAAChevron();
            dureeMaxMM = parametrageAE.getGrindActuelle().getDureeMaxiMMChevron();
        }
        parametrageAE.setNbJoursConditionAvctMini(UtilsDureeCarriere.getNbJours(dureeMinAA, dureeMinMM, 0L));
        parametrageAE.setNbJoursConditionAvctMoyen(UtilsDureeCarriere.getNbJours(dureeMoyAA, dureeMoyMM, 0L));
        parametrageAE.setNbJoursConditionAvctMaxi(UtilsDureeCarriere.getNbJours(dureeMaxAA, dureeMaxMM, 0L));
        // Fin Modif NCHE le 19/06/08
       
      if (parametrageAE.getNbJoursConditionAvctMini().compareTo(BigDecimal.ZERO) <= 0 || parametrageAE.getNbJoursConditionAvctMoyen().compareTo(BigDecimal.ZERO) <= 0
            || parametrageAE.getNbJoursConditionAvctMaxi().compareTo(BigDecimal.ZERO) <= 0) {
         parametrageAE.getMsgErreurProposition().append("Pas de durées d'avancement sur l'échelon ");
        parametrageAE.getMsgErreurProposition().append(parametrageAE.getEchelonActuel().getCodeEchelonWithChevron(parametrageAE.getChevronActuel()) + " du grade ");
         parametrageAE.getMsgErreurProposition().append(parametrageAE.getMainFicGradEmpCur().getGrade().getCode() + " - " + parametrageAE.getMainFicGradEmpCur().getGrade().getLibelleMoyen());
         parametrageAE.getMsgErreurProposition().append(" à la date du " + UtilsDate.dateToString(parametrageAE.getParamMoteur().getDateDebut()) + ".");
         log.debug("####>>>>Pas de durées limites sur la grille indiciaire pour l'avancement.<<<<####");
         throw new MoteurAvException(MoteurAvException.Type.AVE_MOTEUR_NO_LIMIT_TO_ADVANCE, parametrageAE.getEchelonActuel().getCodeEchelonWithChevron(parametrageAE.getChevronActuel()),
               parametrageAE.getMainFicGradEmpCur().getGrade().getCode(), parametrageAE.getMainFicGradEmpCur().getGrade().getLibelleMoyen(), parametrageAE.getParamMoteur().getDateDebut());
      }
      BigDecimal nbJoursConditionAcvtMiniReliq = parametrageAE.getNbJoursConditionAvctMini();
      BigDecimal nbJoursConditionAcvtMoyenReliq = parametrageAE.getNbJoursConditionAvctMoyen();
      BigDecimal nbJoursConditionAcvtMaxiReliq = parametrageAE.getNbJoursConditionAvctMaxi();

      // Si le reliquat est différent de 00.00.00
      if (!parametrageAE.getReliquat().equalsToZero()) {
         // On soustrait du nb de jours de condition d'avancement au maxi le reliquat
         nbJoursConditionAcvtMaxiReliq = nbJoursConditionAcvtMaxiReliq.subtract(new BigDecimal(parametrageAE.getReliquat().getNbJours()));
         // Si le nb de jours d'avancement au maxi après déduction du reliquat est négatif
         if (nbJoursConditionAcvtMaxiReliq.compareTo(BigDecimal.ZERO) < 0) {
            // On leve une erreur
            parametrageAE.getMsgErreurProposition().append("Le reliquat (" + parametrageAE.getReliquat().toString() + ") est supérieur au condition d'avancement du MAXI ("
                  + new Duration(parametrageAE.getNbJoursConditionAvctMaxi().intValue()).toString() + ")");
            log.debug("####>>>>Le reliquat (" + parametrageAE.getReliquat().toString() + ") est supérieur au condition d'avancement du MAXI ("
                  + new Duration(parametrageAE.getNbJoursConditionAvctMaxi().intValue()).toString() + ")<<<<####");
            throw new MoteurAvException(MoteurAvException.Type.AVE_MOTEUR_RELIQUAT_TOO_BIG_MAXI, parametrageAE.getReliquat().toString(), new Duration(
                  parametrageAE.getNbJoursConditionAvctMaxi().intValue()).toString());
         }
         // On soustrait du nb de jours de condition d'avancement au moyen le reliquat
         nbJoursConditionAcvtMoyenReliq = nbJoursConditionAcvtMoyenReliq.subtract(new BigDecimal(parametrageAE.getReliquat().getNbJours()));
         if (nbJoursConditionAcvtMoyenReliq.compareTo(BigDecimal.ZERO) < 0) {
            // On leve une erreur
            parametrageAE.getMsgErreurProposition().append("Le reliquat (" + parametrageAE.getReliquat().toString() + ") est supérieur au condition d'avancement du MOYEN ("
                  + new Duration(parametrageAE.getNbJoursConditionAvctMoyen().intValue()).toString() + ")");
            log.debug("####>>>>Le reliquat (" + parametrageAE.getReliquat().toString() + ") est supérieur au condition d'avancement du MOYEN ("
                  + new Duration(parametrageAE.getNbJoursConditionAvctMoyen().intValue()).toString() + ")<<<<####");
            // MANTIS 15257 SROM 05/2012 : on ne soulève plus d'exception dans ce cas là, on gère le fait qu'il peut y avoir une date d'avancement mini ou moyen qui n'existe pas
            parametrageAE.setDateAvancementMoyenImpossible(true);
            parametrageAE.setDateAvancementMiniImpossible(true);
//          throw new MoteurAvException(MoteurAvException.Type.AVE_MOTEUR_RELIQUAT_TOO_BIG_MOYEN, reliquat.toString(), new Duration(
//                nbJoursConditionAvctMoyen.intValue()).toString());
         }
         // On soustrait du nb de jours de condition d'avancement au mini le reliquat
         nbJoursConditionAcvtMiniReliq = nbJoursConditionAcvtMiniReliq.subtract(new BigDecimal(parametrageAE.getReliquat().getNbJours()));
         if (nbJoursConditionAcvtMiniReliq.compareTo(BigDecimal.ZERO) < 0) {
            // On leve une erreur
            parametrageAE.getMsgErreurProposition().append("Le reliquat (" + parametrageAE.getReliquat().toString() + ") est supérieur au condition d'avancement du MINI ("
                  + new Duration(parametrageAE.getNbJoursConditionAvctMini().intValue()).toString() + ")");
            log.debug("####>>>>Le reliquat (" + parametrageAE.getReliquat().toString() + ") est supérieur au condition d'avancement du MINI ("
                  + new Duration(parametrageAE.getNbJoursConditionAvctMini().intValue()).toString() + ")<<<<####");
            // MANTIS 15257 SROM 05/2012 : on ne soulève plus d'exception dans ce cas là, on gère le fait qu'il peut y avoir une date d'avancement mini ou moyen qui n'existe pas
            parametrageAE.setDateAvancementMiniImpossible(true);
//          throw new MoteurAvException(MoteurAvException.Type.AVE_MOTEUR_RELIQUAT_TOO_BIG_MINI, reliquat.toString(), new Duration(
//                nbJoursConditionAvctMini.intValue()).toString());
         }

         // NCHE le 05/02/08 : il ne faut pas diminuer le reliquat des conditions d'avancmt car il doit être pris en
         // compte dans le calcule des positions admin
         // On récupère les nouvelles conditions d'avancement
         // nbJoursConditionAvctMaxi = nbJoursConditionAcvtMaxiReliq;
         // nbJoursConditionAvctMoyen = nbJoursConditionAcvtMoyenReliq;
         // nbJoursConditionAvctMini = nbJoursConditionAcvtMiniReliq;
         // Fin NCHE
      }

      // NCHE le 08/02/08 : Dans le cas d'une date forcée, on doit tenir compte des positions admin à partir de la
      // date de début
      // donc il faut diminuer des conditions d'avancement la durée entre la date forcée et la date de début
      // MANTIS 15952 SROM 07/2012 : s'il existe une date forcée, on la prend en compte dans tous les cas
      BigDecimal nbJoursConditionAcvtMiniDateForcee = parametrageAE.getNbJoursConditionAvctMini();
      BigDecimal nbJoursConditionAcvtMoyenDateForcee = parametrageAE.getNbJoursConditionAvctMoyen();
      BigDecimal nbJoursConditionAcvtMaxiDateForcee = parametrageAE.getNbJoursConditionAvctMaxi();
      if (parametrageAE.getDateForceeEchelon() != null) {
         Integer nbJours = UtilsDureeCarriere.getNbJoursDateToDate(parametrageAE.getDateForceeEchelon(), parametrageAE.getDateEntreeEchelon(), this.trentieme);
         nbJours = nbJours - 1;
        
         /* MANTIS 16520 SROM 11/2012 :
          *   CAS PARTICULIER si la date forcée d'échelon est entre le 17 et le 31 d'un mois à 31 jours,
          *   on retranche un jour supplémentaire */
         boolean moisEn31concerne = false;
         Calendar calDateForceEchelon = Calendar.getInstance();
         calDateForceEchelon.setTime(parametrageAE.getDateForceeEchelon());
         if (calDateForceEchelon.getActualMaximum(Calendar.DAY_OF_MONTH) == 31
               && calDateForceEchelon.get(Calendar.DAY_OF_MONTH) >= 17) {
            moisEn31concerne = true;
         }
         if (moisEn31concerne) {
            nbJours = nbJours - 1;
         }
         if (nbJours > 0) {
            if (parametrageAE.getDateForceeEchelon().before(parametrageAE.getDateEntreeEchelon())) {
               nbJoursConditionAcvtMaxiDateForcee = nbJoursConditionAcvtMaxiDateForcee.subtract(new BigDecimal(nbJours));
               nbJoursConditionAcvtMoyenDateForcee = nbJoursConditionAcvtMoyenDateForcee.subtract(new BigDecimal(nbJours));
               nbJoursConditionAcvtMiniDateForcee = nbJoursConditionAcvtMiniDateForcee.subtract(new BigDecimal(nbJours));
            } else if (parametrageAE.getDateForceeEchelon().after(parametrageAE.getDateEntreeEchelon())) {
               nbJoursConditionAcvtMaxiDateForcee = nbJoursConditionAcvtMaxiDateForcee.add(new BigDecimal(nbJours));
               nbJoursConditionAcvtMoyenDateForcee = nbJoursConditionAcvtMoyenDateForcee.add(new BigDecimal(nbJours));
               nbJoursConditionAcvtMiniDateForcee = nbJoursConditionAcvtMiniDateForcee.add(new BigDecimal(nbJours));
            }
         }
        
         // MANTIS 19310 SROM 04/2013 : comme pour le reliquat, si la date d'entrée dans l'échelon est forcée, on force la date de base
         parametrageAE.setDateBase((Date)parametrageAE.getDateForceeEchelon().clone());
      }
     
      // MANTIS 12689 - LDEC - 23 août 2011 - Si on ne fait pas le calcu d'avancement en maxi. Indiquer en erreur la proposition si
      // la date d'avancement mini est inférieure à la date d'entrée dans l'échelon précedent
        if (!EnumModeCreation.MAXI.equals(parametrageAE.getParamMoteur().getModeCreation())) {
           //Mantis 0028335: e-Carrières - Pb CAP Avct echelon sur Avct Maxi
           if(nbJoursConditionAcvtMiniDateForcee.intValue() < 0){
               String dateAvancementMini = UtilsDate.dateToString(UtilsDate.addDays(parametrageAE.getDateEntreeEchelon(), nbJoursConditionAcvtMiniDateForcee.intValue()));
               if(!parametrageAE.getMsgErreurProposition().toString().isEmpty()){
                  parametrageAE.getMsgErreurProposition().append("<br>");
               }
               parametrageAE.getMsgErreurProposition().append("La date d'avancement mini (" + dateAvancementMini
                       + ") est inférieure à la date d'entrée dans l'échelon précédent ("
                       + UtilsDate.dateToString(parametrageAE.getDateEntreeEchelon())
                       + ")");
               parametrageAE.setPresenceRetardMoteur(null);
               parametrageAE.setPropositionErreur(true);
               parametrageAE.getPropositionAE().setDateCalculeeMini(null);
               parametrageAE.setDateAvancementMiniImpossible(true);
           }
          
           if(nbJoursConditionAcvtMoyenDateForcee.intValue() < 0){
               String dateAvancementMoy = UtilsDate.dateToString(UtilsDate.addDays(parametrageAE.getDateEntreeEchelon(), nbJoursConditionAcvtMoyenDateForcee.intValue()));
               if(!parametrageAE.getMsgErreurProposition().toString().isEmpty()){
                  parametrageAE.getMsgErreurProposition().append("<br>");
               }
               parametrageAE.getMsgErreurProposition().append("La date d'avancement moyen (" + dateAvancementMoy
                       + ") est inférieure à la date d'entrée dans l'échelon précédent ("
                       + UtilsDate.dateToString(parametrageAE.getDateEntreeEchelon())
                       + ")");
               parametrageAE.setPresenceRetardMoteur(null);
               parametrageAE.setPropositionErreur(true);
               parametrageAE.getPropositionAE().setDateCalculeeMoyen(null);
               parametrageAE.setDateAvancementMoyenImpossible(true);
           }
          
           if(nbJoursConditionAcvtMaxiDateForcee.intValue() < 0){
               String dateAvancementMaxi = UtilsDate.dateToString(UtilsDate.addDays(parametrageAE.getDateEntreeEchelon(), nbJoursConditionAcvtMaxiDateForcee.intValue()));
               if(!parametrageAE.getMsgErreurProposition().toString().isEmpty()){
                  parametrageAE.getMsgErreurProposition().append("<br>");
               }
              parametrageAE.getMsgErreurProposition().append("La date d'avancement maxi (" + dateAvancementMaxi
                       + ") est inférieure à la date d'entrée dans l'échelon précédent ("
                       + UtilsDate.dateToString(parametrageAE.getDateEntreeEchelon())
                       + ")");
               parametrageAE.setPresenceRetardMoteur(null);
               parametrageAE.setPropositionErreur(true);
               parametrageAE.getPropositionAE().setDateCalculeeMaxi(null);
          }
         //   throw new MoteurAvException(MoteurAvException.Type.AVE_MOTEUR_MINI_INF_ENTREE_ECHELON, dateAvancementMini, UtilsDate.dateToString(parametrageAE.getDateEntreeEchelon()));
      }

      // On instancie le calculateur de presence et de retards
        parametrageAE.setPresenceRetardMoteur(new CalculPresenceRetardMoteurAE(parametrageAE.getParamMoteur(), parametrageAE.getPropositionAE(), parametrageAE.getNbJoursConditionAvctMini(),
              parametrageAE.getNbJoursConditionAvctMoyen(), parametrageAE.getNbJoursConditionAvctMaxi(), trentieme, parametrageAE.getDateBase()));
      // Correction Mantis N° 685
      // Mode date forcée echelon
      // if ( dateForceeEchelon != null ) {
      // presenceRetardMoteur.calculDateForceeEchelon();
      // }
      // Correction Mantis N° 685

      FichePositionAdmin ficPosAdminCur = null;
      if (log.isDebugEnabled()) {
         log.debug("---------------------------------------------------");
         log.debug("| Début Traitement Fiches Position Administrative |");
         log.debug("---------------------------------------------------");
      }
      parametrageAE.getPresenceRetardMoteur().logIt("Valeurs a atteindre : ", false, false, false, false);
      parametrageAE.setNbFichePosAdm(0);
        // GIFT 93843 - STH - 31/12/2010 - On initialise les flags à continuer --> on s'arrêtera quand on aura une date d'avancement dans une période autorisant l'avancement
      parametrageAE.getPresenceRetardMoteur().setMustContinued(true);
     
      /* SROM 01/2016 : US Gérer correctement l'impact des positions sur le calcul des reliquats (AVE) {SRH_RECLAST}
       *        Positionnement de la date d'entrée dans l'échelon pour forcer une partie de l'avancement à 100% si besoin.
       *        Le reste de l'algo est fait dans parametrageAE.getPresenceRetardMoteur().calculPresenceRetard() */
      if (parametrageAE.getReliquat() != null && !parametrageAE.getReliquat().equalsToZero()) {
         parametrageAE.getPresenceRetardMoteur().setDateEntreeEchelonSansApplicationReliquat(parametrageAE.getDateEntreeEchelon());
      }
      
      // On créé une liste de fiche position admin de travail, sur laquelle on va rajouter toutes les fiches position admin des carrière histo de l'agent dont on a besoin (tant que dateDebut n'est pas couverte)
      List<FichePositionAdmin> listFichePositionAdminComplete = new ArrayList<FichePositionAdmin>();
      listFichePositionAdminComplete.addAll(parametrageAE.getListFicPosAdm());
      if (listFichePositionAdminComplete.size() > 0) {
         AbstractDatedPersistentObjectCS.ComparatorDatedCsAsc comp = new AbstractDatedPersistentObjectCS.ComparatorDatedCsAsc();
         Collections.sort(listFichePositionAdminComplete, comp);
        FichePositionAdmin fpPlusAncienne = listFichePositionAdminComplete.get(0);
         Date dateDebutPlusAncienneFicPos = fpPlusAncienne.getDateDebut();
         if (UtilsDate.compareByDateOnly(parametrageAE.getPresenceRetardMoteur().getDateDeDebut(), dateDebutPlusAncienneFicPos) < 0) {
            // Chargement des historiques de carrière
            List<FichePositionAdmin> listFicHistoCarriere = serviceFichePositionAdmin.findListFichePositionAdminFromHistoCarriere(fpPlusAncienne.getCarriere().getAgent().getId(), fpPlusAncienne.getCarriere().getId(), parametrageAE.getPresenceRetardMoteur().getDateDeDebut());
            if (listFicHistoCarriere != null && listFicHistoCarriere.size() > 0) {
               listFichePositionAdminComplete.addAll(listFicHistoCarriere);
               Collections.sort(listFichePositionAdminComplete, comp);
            }
         }
        
         for (Iterator<FichePositionAdmin> itFic = listFichePositionAdminComplete.iterator(); itFic.hasNext();) {
            ficPosAdminCur = itFic.next();
            parametrageAE.setNbFichePosAdm(parametrageAE.getNbFichePosAdm()+1);
  
            // Correction Mantis N° 685
            // A voir avec la méthode presenceRetardMoteur.allAreReached()
            // Il faudrait voir s'il ne faut pas arreter la boucle dès que les
            // dates d'avancement sont calculées
            // Mode date forcée echelon
            // if ( dateForceeEchelon != null ) {
            // if ( ficPosAdminCur.getDateFin().before(presenceRetardMoteur.getDateAvancementPlusPetite() ) ) {
            // continue;
            // }
            // }
            // Correction Mantis N° 685
            if (log.isDebugEnabled()) {
               log.debug("------------------------------------------");
               log.debug("| Nouvelle Fiche Position Administrative |");
               log.debug("------------------------------------------");
               log.debug("Fiche posistion admin ID : " + ficPosAdminCur.getId());
               log.debug("Code - libellé :" + ficPosAdminCur.getPositionAdmin().getCode() + " - "
                     + ficPosAdminCur.getPositionAdmin().getLibelle());
               log.debug("Date Début : " + UtilsDate.dateToString(ficPosAdminCur.getDateDebut()) + " Date Fin : "
                     + UtilsDate.dateToString(ficPosAdminCur.getDateFin()));
               log.debug("Date Fin Prévu : " + UtilsDate.dateToString(ficPosAdminCur.getDateFinPrevue()));
              log.debug("Date Entrée Echelon : " + UtilsDate.dateToString(parametrageAE.getPropositionAE().getDateEntreeEchelon()));
               log.debug("Date de Base : " + UtilsDate.dateToString(parametrageAE.getPropositionAE().getDateBaseEchelon()));
            }
  
            // On a pas de position Admin un erreur est levée
            if (ficPosAdminCur.getPositionAdmin() == null) {
               parametrageAE.getMsgErreurProposition().append("Pas de position administrative sur la fiche du ");
                   // Correction MANTIS 1823
               parametrageAE.getMsgErreurProposition().append(ficPosAdminCur.getDateDebut());
               parametrageAE.getMsgErreurProposition().append(" au ");
               parametrageAE.getMsgErreurProposition().append(ficPosAdminCur.getDateFin());
               throw new MoteurAvException(MoteurAvException.Type.AVE_MOTEUR_NO_POSITION_ADM, ficPosAdminCur.getDateDebut(),
                     ficPosAdminCur.getDateFin());
            }
            // On fournit au traitement s'il y a encore un calcul a faire
            parametrageAE.getPresenceRetardMoteur().setHasNextToDo(itFic.hasNext());
            parametrageAE.getPresenceRetardMoteur().setIsFirstToDo(parametrageAE.getNbFichePosAdm() == 1);
            parametrageAE.getPresenceRetardMoteur().setFichePositionAdmin(ficPosAdminCur);
              // GIFT 93843 - STH - 31/12/2010 - Mise en commentaire : on laisse le moteur s'arrêter seul
            // presenceRetardMoteur.setMustContinued(!ficPosAdminCur.getPositionAdmin().getAutoriseAvEch());
  
            // Calculs
            // Le calcul a été bien effectué sans erreur
            parametrageAE.getPresenceRetardMoteur().calculPresenceRetard();
            parametrageAE.setLocalRetardSurPosition(parametrageAE.getPresenceRetardMoteur().getHasRetardPosition());
            if (parametrageAE.getPresenceRetardMoteur().getHasError()) {
               parametrageAE.getMsgErreurProposition().append("Calcul impossible pour la fiche position admin du ");
                   // Correction MANTIS 1823
               parametrageAE.getMsgErreurProposition().append(UtilsDate.dateToString(ficPosAdminCur.getDateDebut()));
               parametrageAE.getMsgErreurProposition().append(" au ");
               parametrageAE.getMsgErreurProposition().append(UtilsDate.dateToString(ficPosAdminCur.getDateFin()));
               parametrageAE.getMsgErreurProposition().append(" (date de fin prévu au " + UtilsDate.dateToString(ficPosAdminCur.getDateFinPrevue())
                     + "). ");
               parametrageAE.getMsgErreurProposition().append("L'avancement n'est pas autorisé ou/et la date de fin ou la date de fin prévue est incorrecte.");
               log.debug("####>>>>Calcul impossible pour la fiche position admin.<<<<####");
               throw new MoteurAvException(MoteurAvException.Type.AVE_MOTEUR_ERROR_CALC, ficPosAdminCur.getDateDebut(),
                     ficPosAdminCur.getDateFin(), ficPosAdminCur.getDateFinPrevue());
            }
         }
      }
      if (log.isDebugEnabled()) {
         log.debug("-------------------------------------------------");
         log.debug("| Fin Traitement Fiches Position Administrative |");
         log.debug("-------------------------------------------------");
      }
     parametrageAE.setNbFichePosAdm(0);
   }

   /**
    * Pré-initialise les données pour le prochain calcul avec les données de la propositions déja existante. S'il
     * n'existe pas de proposition déja calculée, on traite normalement CAS PROPOSITION OFFICIALISEE OU MAXI TROUVEE EN
     * BASE
     * Return true: calcul du nouvel échelon ok, on continue la procédure
     *        false: echelon et echelon nouveau identique --> echelon maxi atteint, donc pas de proposition a généré
    */
   private boolean preInitPropositionOfficialiseeMaxi(ParametrageAE parametrageAE) {
      // Si on traite la meme situtation entre le curseur Situtations et
      // le curseur Proposition officialisée Maxi => on récupère les infos de
      // la proposition déjà calculée afin de calculer la prochaine
      // proposition
      if (parametrageAE.getPropositionPrev() != null) {
         parametrageAE.setDateForceeEchelon(null);
         parametrageAE.setDateEntreeEchelon(parametrageAE.getPropositionPrev().getDateAvancement());
         parametrageAE.setReliquat(new Duration(parametrageAE.getPropositionPrev().getReliquatAvancementAA(), parametrageAE.getPropositionPrev().getReliquatAvancementMM(),
               parametrageAE.getPropositionPrev().getReliquatAvancementJJ()));
         // Modif NCHE le 07/02/08 : Ajout d'une date de base
         // MANTIS 17312 : SROM 11/2012 : la date de base d'échelon n'est à calculer en trentième
         parametrageAE.setDateBase(UtilsDureeCarriere.subDureeStringOnDate(parametrageAE.getDateEntreeEchelon(), parametrageAE.getReliquat().toDataBase(), false));
         // Fin Modif NCHE
         parametrageAE.setEchelonActuel(parametrageAE.getPropositionPrev().getEchelonNouveau());
         parametrageAE.setChevronActuel(parametrageAE.getPropositionPrev().getChevronNouveau());
            if (parametrageAE.getGrindNouvelle() != null ) {
               parametrageAE.setEchelonNouveau(parametrageAE.getGrindNouvelle().getEchelonOrdonne().getEchelon());
               parametrageAE.setChevronNouveau(parametrageAE.getGrindNouvelle().getEchelonOrdonne().getChevron());
            }
            parametrageAE.setIndiceBrutAcutel(parametrageAE.getPropositionPrev().getIndiceBrutNouveau());
            parametrageAE.setIndiceMajoreAcutel(parametrageAE.getPropositionPrev().getIndiceMajoreNouveau());
            parametrageAE.setMontantHorsEchelleAcutel(parametrageAE.getPropositionPrev().getMontantHorsEchelleNouveau());
         //MANTIS 0016751 Cas ou se base sur la dernière proposition (maxi ou non off avec paramétrage orga/collec)
         //Le cursorProposition ramene la proposition avec grilleInd=grilleIndNouveau si on est déjà à l'echelon maxi
         //Donc si echelon identique alors l'échelon max est atteint --> pas de nouvelles propositions
         if (parametrageAE.getEchelonActuel().equals(parametrageAE.getEchelonNouveau())){
             return false;
         }
      } else {
         this.preInitDefault(parametrageAE); // On initialise normalement suivant la situation
      }
      return true;
   }

   /**
    * On préInitialise par la situtation courante (et non pas avec la proposition Offic. ou Maxi) la grille indiciaire
    * actuelle/nouvelle l'echelon actuel/nouveau et l'indice brut actuel, majoré actuel et le montant hors echelle
    * actuel CAS STANDARD
    */
   private void preInitDefault(ParametrageAE parametrageAE) {
      // Stocke la grille indiciare acutelle et nouvelle
      parametrageAE.setGrindActuelle(parametrageAE.getMainSituationCur().getGrilleIndiciaire());
      parametrageAE.setGrindNouvelle(parametrageAE.getMainSituationCur().getNewGrilleIndiciaire());
      // Stocke les echelon/chevron actuels et nouveaux
      parametrageAE.setEchelonActuel(parametrageAE.getGrindActuelle().getEchelonOrdonne().getEchelon());
      parametrageAE.setChevronActuel(parametrageAE.getGrindActuelle().getEchelonOrdonne().getChevron());
        // Modif NCHE le 19/06/08 : Nouvelle méthode pour le calcul du changement de chevron       
        // Si le chevron suivant est indiqué sur la grille indiciaire, on prendre celui-ci, sinon on prendra celui de l'échelon ordonné
        if( parametrageAE.getGrindActuelle() != null && parametrageAE.getGrindActuelle().getChevronSuivant() != null ) {
           parametrageAE.setEchelonNouveau(parametrageAE.getGrindActuelle().getChevronSuivant().getEchelon());
           parametrageAE.setChevronNouveau(parametrageAE.getGrindActuelle().getChevronSuivant().getChevron());
            // il faut aussi récupérer la nouvelle grille indiciaire par rapport au nouvel echelon
           parametrageAE.setGrindNouvelle(daoGrilleIndiciaire.findGrilleIndiciaireByEchelle(parametrageAE.getGrindActuelle().getEchelle(), parametrageAE.getEchelonNouveau(), parametrageAE.getChevronNouveau()));
        } else {
           parametrageAE.setEchelonNouveau(parametrageAE.getGrindNouvelle().getEchelonOrdonne().getEchelon());
           parametrageAE.setChevronNouveau(parametrageAE.getGrindNouvelle().getEchelonOrdonne().getChevron());
       }
        // Fin Modif NCHE le 19/06/08
      // Stocke les valeurs indice brut, majoré et montant hors echelle actuel
        parametrageAE.setIndiceBrutAcutel(parametrageAE.getMainFicGradEmpCur().getIndiceBrut());
        parametrageAE.setIndiceMajoreAcutel(parametrageAE.getMainFicGradEmpCur().getIndiceMajore());
        parametrageAE.setMontantHorsEchelleAcutel(parametrageAE.getMainFicGradEmpCur().getMontantHorsEchelle());
   }

   /**
    * Lance la simulation
    */
   private void launchSimulation(ParametrageAE parametrageAE) {

      log.debug("----------------------------------------------------------------------------");
      log.debug("| Lancement de la " + parametrageAE.getParamMoteur().getModeCreation() + " pour : " + parametrageAE.getParamMoteur().getNbAvctACalculer()
            + " itérations ou jusqu'a la date " + UtilsDate.dateToString(parametrageAE.getParamMoteur().getDateFin()) + "|");
      log.debug("----------------------------------------------------------------------------");

      parametrageAE.setNbAvctCompteur(parametrageAE.getNbAvctCompteur()+1); // Compteur gérant les itérations en mode SIMULATION
      log.debug("\nCalcul déja effectué : "
            + parametrageAE.getNbAvctCompteur()
            + "/"
            + (parametrageAE.getParamMoteur().getNbAvctACalculer() > 0 ? parametrageAE.getParamMoteur().getNbAvctACalculer() : "(jusqu'à date "
                  + UtilsDate.dateToString(parametrageAE.getParamMoteur().getDateFin()) + ")") + "\n");
     
      while (checkContinueToSimulateProposition(parametrageAE)) {
         parametrageAE.setPropositionPrev(parametrageAE.getPropositionAE());
         parametrageAE.setEchelonActuel(parametrageAE.getEchelonNouveau());
         parametrageAE.setChevronActuel(parametrageAE.getChevronNouveau());
         parametrageAE.setGrindActuelle(parametrageAE.getGrindNouvelle());
         parametrageAE.setEchelonNouveau(null);
         parametrageAE.setChevronNouveau(null);
         parametrageAE.setGrindNouvelle(null);

            // Modif NCHE le 19/06/08 : Nouvelle méthode pour le calcul du changement de chevron
            // Si le chevron suivant est indiqué sur la grille indiciaire, on prendre celui-ci, sinon on prendra celui de l'échelon ordonné
            if( parametrageAE.getGrindActuelle() != null && parametrageAE.getGrindActuelle().getChevronSuivant() != null ) {
               parametrageAE.setEchelonNouveau(parametrageAE.getGrindActuelle().getChevronSuivant().getEchelon());
               parametrageAE.setChevronNouveau(parametrageAE.getGrindActuelle().getChevronSuivant().getChevron());
            } else {
                EchelonOrdonne echOrdNv = null;
               
                if( EnumModeCreation.CHEVRON.equals(parametrageAE.getParamMoteur().getModeCreation()) ) {
                    echOrdNv = daoGrilleIndiciaire.findChevronSuivant(parametrageAE.getGrindActuelle());
                }
                else {
                    // Recuperation du nouvel echelon par rapport a la nouvelle grille indiciaire
                    echOrdNv = daoGrilleIndiciaire.findEchelonSuivant(parametrageAE.getGrindActuelle());               
                }
               
                if( echOrdNv != null ) {
                   parametrageAE.setEchelonNouveau(echOrdNv.getEchelon());
                   parametrageAE.setChevronNouveau(echOrdNv.getChevron());
                }
            }
            Enum<MoteurAvException.Type> typeExeption = null;
       
            if (EnumModeCreation.CHEVRON.equals(parametrageAE.getParamMoteur().getModeCreation()) && parametrageAE.getChevronNouveau() == null) {
               parametrageAE.getMsgErreurProposition().append("\nPas de chevron suivant sur l'échelon ");
               parametrageAE.getMsgErreurProposition().append(parametrageAE.getEchelonActuel().getCodeEchelonWithChevron(parametrageAE.getChevronActuel()) + " du grade " + parametrageAE.getMainFicGradEmpCur().getGrade().getLibelleMoyen());
               parametrageAE.getMsgErreurProposition().append(" au " + UtilsDate.dateToString(parametrageAE.getParamMoteur().getDateDebut()) + ".");
                log.debug("####>>>>Nouveau chevron non trouvé.<<<<####");
                parametrageAE.setSaveProposition(false); // Il ne faut pas sauver
               
                typeExeption = MoteurAvException.Type.AVE_MOTEUR_NO_NEXT_CHEVRON;
            }else if (parametrageAE.getEchelonNouveau() == null) {
               parametrageAE.getMsgErreurProposition().append("\nPas d'échelon suivant sur l'échelon ");
               parametrageAE.getMsgErreurProposition().append(parametrageAE.getEchelonActuel().getCodeEchelonWithChevron(parametrageAE.getChevronActuel()) + " du grade " + parametrageAE.getMainFicGradEmpCur().getGrade().getLibelleMoyen());
               parametrageAE.getMsgErreurProposition().append(" au " + UtilsDate.dateToString(parametrageAE.getParamMoteur().getDateDebut()) + ".");
            log.debug("####>>>>Nouvel echelon non trouvé.<<<<####");
               
            parametrageAE.setSaveProposition(false); // Il ne faut pas sauver
                typeExeption = MoteurAvException.Type.AVE_MOTEUR_NO_NEXT_ECHELON;
         }
         // Recuperation de la nouvelle grille indiciaire par rapport au nouvel echelon
            if (typeExeption == null) {
               parametrageAE.setGrindNouvelle(daoGrilleIndiciaire.findGrilleIndiciaireByEchelle(parametrageAE.getGrindActuelle().getEchelle(), parametrageAE.getEchelonNouveau(), parametrageAE.getChevronNouveau()));
             // /!\ Si pas de nouvelle grille indiciaire, on change de situtation
             if (parametrageAE.getGrindNouvelle() == null) {
                parametrageAE.getMsgErreurProposition().append("\nPas de grille indiciaire sur le grade ");
                parametrageAE.getMsgErreurProposition().append(parametrageAE.getMainFicGradEmpCur().getGrade().getCode() + " - " + parametrageAE.getMainFicGradEmpCur().getGrade().getLibelleMoyen());
                parametrageAE.getMsgErreurProposition().append(" à la date du " + UtilsDate.dateToString(parametrageAE.getParamMoteur().getDateDebut()) + ".");
                log.debug("####>>>>Nouvelle grille indiciaire non trouvée.<<<<####");
                   
                    typeExeption = MoteurAvException.Type.AVE_MOTEUR_NO_NEXT_GRILLE_IND;
             }
         }
            parametrageAE.setDateEntreeEchelon(parametrageAE.getPropositionPrev().getDateAvancement());
            parametrageAE.setReliquat(new Duration(parametrageAE.getPropositionPrev().getReliquatAvancementAA(), parametrageAE.getPropositionPrev().getReliquatAvancementMM(), parametrageAE.getPropositionPrev().getReliquatAvancementJJ()));
         // Modif NCHE le 07/02/08 : Ajout date de base pour le calcul des positions admin
         // MANTIS 17312 : SROM 11/2012 : la date de base d'échelon n'est à calculer en trentième
            parametrageAE.setDateBase(UtilsDureeCarriere.subDureeStringOnDate(parametrageAE.getDateEntreeEchelon(), parametrageAE.getReliquat().toDataBase(), false));
         // Fin Modif NCHE
        
         //MANTIS 0016751 Test lors de l'init de la proposition sur l'échelon et l'échelon nouveau
            //Dans le cas classique, si l'echelon maxi est déjà atteint, on ne rentre pas dans le process de dessus
            //Dans le cas de proposition existante, si l'echelon maxi est déjà atteint, on ne sauvegarde pas la simulation
            //mais obliger d'aller jusqu'au bout pour garder les curseurs synchronisés...
            boolean propositionWithEchelonMaxiNonAtteint = this.preInitPropositionOfficialiseeMaxi(parametrageAE);
         this.initProposition(parametrageAE);

            // Modif NCHE le 30/10/08 :
            // Les levées d'exception doivent être faîtes après this.initProposition (c'est ds cette méthode qu'on crée la nouvelle proposition)
            // car sinon, on met en erreur la précédente proposition nouvellement créée
            if (typeExeption != null) {
                if (typeExeption.equals(MoteurAvException.Type.AVE_MOTEUR_NO_NEXT_CHEVRON)) {
                    throw new MoteurAvException(typeExeption, parametrageAE.getEchelonActuel().getCodeEchelonWithChevron(parametrageAE.getChevronActuel()),
                          parametrageAE.getMainFicGradEmpCur().getGrade().getLibelleMoyen(), parametrageAE.getParamMoteur().getDateDebut());                   
                } else if (typeExeption.equals(MoteurAvException.Type.AVE_MOTEUR_NO_NEXT_ECHELON)) {
                    throw new MoteurAvException(typeExeption, parametrageAE.getEchelonActuel().getCodeEchelonWithChevron(parametrageAE.getChevronActuel()),
                          parametrageAE.getMainFicGradEmpCur().getGrade().getLibelleMoyen(), parametrageAE.getParamMoteur().getDateDebut());
                } else if (typeExeption.equals(MoteurAvException.Type.AVE_MOTEUR_NO_NEXT_GRILLE_IND)) {
                    throw new MoteurAvException(typeExeption, parametrageAE.getMainFicGradEmpCur().getGrade().getCode(),
                          parametrageAE.getMainFicGradEmpCur().getGrade().getLibelleMoyen(), parametrageAE.getParamMoteur().getDateDebut());                   
                }else {
                    throw new MoteurAvException(typeExeption);                   
                }
            }
           
            parametrageAE.getPropositionAE().setEchelonAncien(parametrageAE.getPropositionPrev().getEchelonNouveau());
            parametrageAE.getPropositionAE().setChevronAncien(parametrageAE.getPropositionPrev().getChevronNouveau());
            parametrageAE.getPropositionAE().setIndiceBrutAncien(parametrageAE.getPropositionPrev().getIndiceBrutNouveau());
            parametrageAE.getPropositionAE().setIndiceMajoreAncien(parametrageAE.getPropositionPrev().getIndiceMajoreNouveau());
            parametrageAE.getPropositionAE().setMontantHorsEchelleAncien(parametrageAE.getPropositionPrev().getMontantHorsEchelleNouveau());

         this.calculFichePositionAdminList(parametrageAE);
         this.finalizeProposition(parametrageAE);
         //AMA mantis 0018924: RHW2-AVE – Simulation – Impossible d’afficher plus d’un avancement
         // le test propositionWithEchelonMaxi etait inversé, du coup on n'enregistrait pas ttes les propositions
         if (checkContinueToSimulateProposition(parametrageAE) && propositionWithEchelonMaxiNonAtteint) {
            this.saveProposition(parametrageAE);
         }
         parametrageAE.setNbAvctCompteur(parametrageAE.getNbAvctCompteur()+1); // Compteur gérant les itérations en mode SIMULATION
         log.debug("\nSimulation nb traitées : "
               + parametrageAE.getNbAvctCompteur()
               + "/"
               + (parametrageAE.getParamMoteur().getNbAvctACalculer() > 0 ? parametrageAE.getParamMoteur().getNbAvctACalculer() : "(jusqu'à date "
                     + UtilsDate.dateToString(parametrageAE.getParamMoteur().getDateFin()) + ")") + "\n");
      }
      parametrageAE.setPropositionPrev(null);
      log.debug("-----------------------");
      log.debug("|Fin de la simulation |");
      log.debug("-----------------------");
   }

   /**
    * Vérifie si on doit continuer ou pas a calculer (simulation maxi)
    */
   private Boolean checkContinueToSimulateProposition(ParametrageAE parametrageAE) {
      log.debug("Date d'avancement : " + UtilsDate.dateToString(parametrageAE.getPropositionAE().getDateAvancement()) + " Date fin paramétrée : "
            + UtilsDate.dateToString(parametrageAE.getParamMoteur().getDateFin()));

      // Si en CAP on calcul
      if (EnumModeCreation.CAP.equals(parametrageAE.getParamMoteur().getModeCreation())) {
         return true;
      }

      // Si la date d'avancement calculée est avant la date de fin paramétrée, on continue
      if (parametrageAE.getParamMoteur().getDateFin() != null && parametrageAE.getPropositionAE().getDateAvancement() != null
            && parametrageAE.getParamMoteur().getDateFin().after(parametrageAE.getPropositionAE().getDateAvancement())) {

         return true;
      }

      // Si on est en simulation et que la date de fin n'est pas paramétré
      // On vérifie le nombre d'itérations
      if (EnumModeCreation.SIMULATION.equals(parametrageAE.getParamMoteur().getModeCreation()) && parametrageAE.getParamMoteur().getDateFin() == null
            && parametrageAE.getNbAvctCompteur() < parametrageAE.getParamMoteur().getNbAvctACalculer()) {
         return true;
      }
      // Sinon on arrete
      log.debug("La proposition calculée ne sera pas enregistrer, la date de fin est inférieure a la date d'avancement.");
      return false;
   }

   private void initProposition(ParametrageAE parametrageAE) {
      log.debug("Intialisation de la proposition");

        // Si le paramétrage courant de la collectivité est renseigné
        if (parametrageAE.getMainSituationCur().getParamCaCollect() != null) {
            // CRISTAL 1-28362072 - LDEC - 13 déc. 2010 - Ne pas stocker en attribut de la classe le paramétrage Collect ou organisme. Car si entre
            // temps il était modifié des nouveaux appel RPC pour le recalcul de proposition se basait toujours sur l'ancien parametrage...
            ParamCaCollect paramCaCollect = parametrageAE.getMainSituationCur().getParamCaCollect();
            parametrageAE.setLocalDateCapMinEgalMax(paramCaCollect.getAeFlagDateCapMinEgalMax());
            parametrageAE.setLocalMotifAvancment(paramCaCollect.getMotifAvancementMaxi());
            // Modif NCHE le 19/06/08 : Nouvelle méthode pour le calcul du changement de chevron
            parametrageAE.setLocalMotifChgtChevron(paramCaCollect.getMotifChangementChevron());
        } // Si le paramétrage courant de l'organisme est renseigné
        else if (parametrageAE.getMainSituationCur().getParamCaOrga() != null) {
            ParamCaOrga paramCaOrga = parametrageAE.getMainSituationCur().getParamCaOrga();
            parametrageAE.setLocalDateCapMinEgalMax(paramCaOrga.getAeFlagDateCapMinEgalMax());
            parametrageAE.setLocalMotifAvancment(paramCaOrga.getMotifAvancementMaxi());
            // Modif NCHE le 19/06/08 : Nouvelle méthode pour le calcul du changement de chevron
            parametrageAE.setLocalMotifChgtChevron(paramCaOrga.getMotifChangementChevron());
        }

        // MANTIS 14683 SROM 03/2012 : si le calcul est en MAXI, le booléen localDateCapMinEgalMax est forcément à true
        // MANTIS 34917 SROM 11/2016 : si le calcul est en CHEVRON, le booléen localDateCapMinEgalMax est forcément à true
        if (parametrageAE.getParamMoteur() != null
              && (EnumModeCreation.MAXI.equals(parametrageAE.getParamMoteur().getModeCreation())
                    || EnumModeCreation.CHEVRON.equals(parametrageAE.getParamMoteur().getModeCreation()))) {
           parametrageAE.setLocalDateCapMinEgalMax(true);
        }
       
      // // On recupere le mode calcul sur le paramétrage organisme courant
      // if ( localSituationCurrent.getParamCaOrga() != null ) {
      // localModeCalcul = localSituationCurrent.getParamCaOrga().getAeModeCalcul();
      // }
      // Initialisation de la proposition
      // Initialisation
        parametrageAE.setPropositionAE(new PropositionAE());
        parametrageAE.setMsgErreurProposition(new StringBuffer());

      // Initialisation des valeurs constantes par défaut
        parametrageAE.getPropositionAE().setPromouvable(true);
        parametrageAE.getPropositionAE().setTypeProposition(EnumTypeProposition.PROMOUVABLE);
        parametrageAE.getPropositionAE().setVerifiee(EnumVerifier.NON);
        parametrageAE.getPropositionAE().setInjection(EnumTypeInjection.NON_INJECTEE);
        parametrageAE.getPropositionAE().setRetardMiniAA(0);
        parametrageAE.getPropositionAE().setRetardMiniMM(0);
        parametrageAE.getPropositionAE().setRetardMiniJJ(0);
        parametrageAE.getPropositionAE().setRetardMoyenAA(0);
        parametrageAE.getPropositionAE().setRetardMoyenMM(0);
        parametrageAE.getPropositionAE().setRetardMoyenJJ(0);
        parametrageAE.getPropositionAE().setRetardMaxiAA(0);
        parametrageAE.getPropositionAE().setRetardMaxiMM(0);
        parametrageAE.getPropositionAE().setRetardMaxiJJ(0);
        parametrageAE.getPropositionAE().setAbsenceAA(0);
        parametrageAE.getPropositionAE().setAbsenceMM(0);
        parametrageAE.getPropositionAE().setAbsenceJJ(0);
        parametrageAE.getPropositionAE().setPresenceAA(0);
        parametrageAE.getPropositionAE().setPresenceMM(0);
        parametrageAE.getPropositionAE().setPresenceJJ(0);

      // Données par rapport au paramètres du moteur
        parametrageAE.getPropositionAE().setModeCreation(parametrageAE.getParamMoteur().getModeCreation());
        parametrageAE.getPropositionAE().setPropositionInitiale(parametrageAE.getParamMoteur().getTypeAvancement());
        parametrageAE.getPropositionAE().setTableauAE(parametrageAE.getParamMoteur().getTableauAE());

        parametrageAE.getPropositionAE().setDateEntreeEchelon(parametrageAE.getDateEntreeEchelon());
        parametrageAE.getPropositionAE().setReliquat(parametrageAE.getReliquat().toDataBase());
      if (parametrageAE.getDateForceeEchelon() != null) {
         parametrageAE.getPropositionAE().setDateBaseEchelon(parametrageAE.getDateForceeEchelon());
      } else {
         // MANTIS 17312 : SROM 11/2012 : la date de base d'échelon n'est à calculer en trentième
         parametrageAE.getPropositionAE().setDateBaseEchelon(UtilsDureeCarriere.subDureeStringOnDate(parametrageAE.getDateEntreeEchelon(), parametrageAE.getReliquat().toDataBase(), false));
      }
        // Modif NCHE le 19/06/08 : Nouvelle méthode pour le calcul du changement de chevron
        Long dureeMinAA = parametrageAE.getGrindActuelle().getDureeMiniAA();
        Long dureeMinMM = parametrageAE.getGrindActuelle().getDureeMiniMM();
        Long dureeMoyAA = parametrageAE.getGrindActuelle().getDureeMoyenAA();
        Long dureeMoyMM = parametrageAE.getGrindActuelle().getDureeMoyenMM();
        Long dureeMaxAA = parametrageAE.getGrindActuelle().getDureeMaxiAA();
        Long dureeMaxMM = parametrageAE.getGrindActuelle().getDureeMaxiMM();

        if(  EnumModeCreation.CHEVRON.equals(parametrageAE.getParamMoteur().getModeCreation()) ) {
            dureeMinAA = parametrageAE.getGrindActuelle().getDureeMiniAAChevron();
            dureeMinMM = parametrageAE.getGrindActuelle().getDureeMiniMMChevron();
            dureeMoyAA = parametrageAE.getGrindActuelle().getDureeMoyenAAChevron();
            dureeMoyMM = parametrageAE.getGrindActuelle().getDureeMoyenMMChevron();
            dureeMaxAA = parametrageAE.getGrindActuelle().getDureeMaxiAAChevron();
            dureeMaxMM = parametrageAE.getGrindActuelle().getDureeMaxiMMChevron();
        }
       parametrageAE.getPropositionAE().setDateTheoriqueMini(UtilsDureeCarriere.addDureeOnDate(parametrageAE.getPropositionAE().getDateBaseEchelon(), dureeMinAA, dureeMinMM, 0, trentieme));
        parametrageAE.getPropositionAE().setDateTheoriqueMoyen(UtilsDureeCarriere.addDureeOnDate(parametrageAE.getPropositionAE().getDateBaseEchelon(), dureeMoyAA, dureeMoyMM, 0, trentieme));
        parametrageAE.getPropositionAE().setDateTheoriqueMaxi(UtilsDureeCarriere.addDureeOnDate(parametrageAE.getPropositionAE().getDateBaseEchelon(), dureeMaxAA, dureeMaxMM, 0, trentieme));
        // Fin Modif NCHE le 19/06/08
       
      // Modif NCHE le 15/04/08 : Sauvegarde le timestamp de calcul pour pourvoir récupérer la liste des propositions
      // calculées par la suite
      // propositionAE.setDateDeCalcul( UtilsDate.getCurrentDay().getTime() );
        parametrageAE.getPropositionAE().setDateDeCalcul(parametrageAE.getMoteurAveResultDTO().getTimestampDeCalcul());

      // Initialisation suivant la situation initiale de l'agent
        parametrageAE.getPropositionAE().setCarriere(parametrageAE.getMainSituationCur().getCarriere());
        parametrageAE.getPropositionAE().setRegroupement(parametrageAE.getMainFicGradEmpCur().getRegroupement());
        parametrageAE.getPropositionAE().setGrade(parametrageAE.getMainFicGradEmpCur().getGrade());
        parametrageAE.getPropositionAE().setStatut(parametrageAE.getMainSituationCur().getStatut());

      IndiceBrutMajore indiceBrutMajoreObject = null;

      // Anciennes données
      parametrageAE.getPropositionAE().setEchelonAncien(parametrageAE.getEchelonActuel());
      parametrageAE.getPropositionAE().setChevronAncien(parametrageAE.getChevronActuel());
      // Si HORS ECHELLE
      if (parametrageAE.getChevronActuel().getType().equals(EnumTypeEchelon.HORS_ECHELLE)) {
         // Si pas forcée -> on recherche par rapport a la grille indic actuel
         if (parametrageAE.getMontantHorsEchelleAcutel() == null) {
            parametrageAE.setMontantHorsEchelleAcutel(new Float(parametrageAE.getGrindActuelle().getMontantBrut().doubleValue()));
         }
         parametrageAE.getPropositionAE().setMontantHorsEchelleAncien(parametrageAE.getMontantHorsEchelleAcutel().floatValue());
      } else {
         // Si pas forcée -> on recherche par rapport a la grille indic actuel
         if ((parametrageAE.getIndiceBrutAcutel() == null || parametrageAE.getIndiceBrutAcutel().trim().isEmpty())
               && (parametrageAE.getIndiceMajoreAcutel() == null || parametrageAE.getIndiceMajoreAcutel().trim().isEmpty())) {
            parametrageAE.setIndiceBrutAcutel(parametrageAE.getGrindActuelle().getIndiceBrut());
            indiceBrutMajoreObject = daoIndiceBrutMajore.findIndiceBrutMajoreValidByIndiceBrutAndCadreStatutaire(parametrageAE.getGrindActuelle().getIndiceBrut(), parametrageAE.getGrindActuelle().getCadreStatutaire().getId(), null);
            if (indiceBrutMajoreObject != null) {
               parametrageAE.setIndiceMajoreAcutel(indiceBrutMajoreObject.getIndiceMajore());
            }
         }
         parametrageAE.getPropositionAE().setIndiceBrutAncien(parametrageAE.getIndiceBrutAcutel());
         parametrageAE.getPropositionAE().setIndiceMajoreAncien(parametrageAE.getIndiceMajoreAcutel());
      }
      // Nouvelles données
        if( parametrageAE.getGrindNouvelle() != null && parametrageAE.getChevronNouveau() != null ) {
           parametrageAE.getPropositionAE().setEchelonNouveau(parametrageAE.getEchelonNouveau());
           parametrageAE.getPropositionAE().setChevronNouveau(parametrageAE.getChevronNouveau());
          if (parametrageAE.getChevronNouveau().getType().equals(EnumTypeEchelon.HORS_ECHELLE)) {
             parametrageAE.getPropositionAE().setMontantHorsEchelleNouveau(new Float(parametrageAE.getGrindNouvelle().getMontantBrut().doubleValue()));
          } else {
             parametrageAE.getPropositionAE().setIndiceBrutNouveau(parametrageAE.getGrindNouvelle().getIndiceBrut());
             indiceBrutMajoreObject = daoIndiceBrutMajore.findIndiceBrutMajoreValidByIndiceBrutAndCadreStatutaire(parametrageAE.getGrindNouvelle().getIndiceBrut(), parametrageAE.getGrindNouvelle().getCadreStatutaire().getId(), null);
             if (indiceBrutMajoreObject != null) {
                parametrageAE.getPropositionAE().setIndiceMajoreNouveau(indiceBrutMajoreObject.getIndiceMajore());
             }
          }
        }
      // Au MAXI
      if (EnumModeCreation.MAXI.equals(parametrageAE.getParamMoteur().getModeCreation())) {
         if (parametrageAE.getLocalMotifAvancment() != null) {
            parametrageAE.getPropositionAE().setMotifAvancement(parametrageAE.getLocalMotifAvancment());
         } else {
            parametrageAE.getMsgErreurProposition().append("\nLe motif d'avancement Maxi doit être renseigné dans le paramétrage de l'organisme ou de la collectivité.");
            log.debug("####>>>>Le motif d'avancement Maxi doit être renseigné dans le paramétrage de l'organisme ou de la collectivité.<<<<####");
            throw new MoteurAvException(MoteurAvException.Type.AVE_MOTEUR_NO_MOTIF_MAXI);
         }
      } else if (EnumModeCreation.CHEVRON.equals(parametrageAE.getParamMoteur().getModeCreation())) {
          // Modif NCHE le 19/06/08 : Nouvelle méthode pour le calcul du changement de chevron
            if (parametrageAE.getLocalMotifChgtChevron() != null) {
               parametrageAE.getPropositionAE().setMotifAvancement(parametrageAE.getLocalMotifChgtChevron());
            } else {
               parametrageAE.getMsgErreurProposition().append("\nLe motif de changement de chevron doit être renseigné dans le paramétrage de l'organisme ou de la collectivité.");
                log.debug("####>>>>Le motif de changement de chevron doit être renseigné dans le paramétrage de l'organisme ou de la collectivité.<<<<####");
                throw new MoteurAvException(MoteurAvException.Type.AVE_MOTEUR_NO_MOTIF_MAXI);
            }
        }
   }

   /**
    * Fin de l'initialisation de la proposition avec les calculs effectués Enregistrement en base de la proposition
    */
   private void finalizeProposition(ParametrageAE parametrageAE) {
      log.debug("Fin d'initialisation de la proposition");
     
      // GIFT FR19771 - STH - 06/10/2009 - On calcule la date d'entrée dans la collectivité dans tous les cas
      this.calculeDateEntreeCollectivite(parametrageAE);
     
      // Si on a une date forcée echelon on n'applique pas les regles spécif.
        // Modif NCHE le 06/06/08 : plantage du calcul car presenceRetardMoteur est à null
        // if ( this.dateForceeEchelon== null ) {
        if ( parametrageAE.getPresenceRetardMoteur() != null && parametrageAE.getDateForceeEchelon()== null ) {
        // Fin Modif NCHE        
         // Applique les regles spécifiques
         this.applySpecificRules(parametrageAE);
      }
      // Si pas de calcul
      if (parametrageAE.getPresenceRetardMoteur() == null) {
         return;
      }
      // -> Initialisation finale
      Date dateAvancement = null;
      // MANTIS 15257 SROM 05/2012 : on calcule la date moyen uniquement si le reliquat est correct
      if (!parametrageAE.getDateAvancementMiniImpossible()) {
         dateAvancement = parametrageAE.getPresenceRetardMoteur().getDateRecherchee(CalculPresenceRetardMoteurAE.EnumTypes.MINI);
         Duration reliquat = UtilsDureeCarriere.getDureeDateToNbJours(dateAvancement, parametrageAE.getPresenceRetardMoteur().getReliquat(CalculPresenceRetardMoteurAE.EnumTypes.MINI), trentieme);
         if (dateAvancement != null) {
            parametrageAE.getPropositionAE().setReliquatCalculeMiniAA(reliquat.getYears());
            parametrageAE.getPropositionAE().setReliquatCalculeMiniMM(reliquat.getMonths());
            parametrageAE.getPropositionAE().setReliquatCalculeMiniJJ(reliquat.getDays());
         } else {
            dateAvancement = parametrageAE.getPresenceRetardMoteur().getDateAvancement(CalculPresenceRetardMoteurAE.EnumTypes.MINI);
         }
         parametrageAE.getPropositionAE().setDateCalculeeMini(dateAvancement);
      } else if (!EnumModeCreation.MAXI.equals(parametrageAE.getParamMoteur().getModeCreation())) {
         // MANTIS 15259 SROM 05/2012 : si le mode de création n'est pas MAXI, on passe la proposition "incomplète" en erreur. L'utilisateur devra alors saisir les dates manquantes avant de la réintégrer.
         parametrageAE.setPropositionErreur(true);
      }

      // MANTIS 15257 SROM 05/2012 : on calcule la date mini uniquement si le reliquat est correct
      if (!parametrageAE.getDateAvancementMoyenImpossible()) {
         dateAvancement = parametrageAE.getPresenceRetardMoteur().getDateRecherchee(CalculPresenceRetardMoteurAE.EnumTypes.MOYEN);
         parametrageAE.setReliquat(UtilsDureeCarriere.getDureeDateToNbJours(dateAvancement, parametrageAE.getPresenceRetardMoteur().getReliquat(CalculPresenceRetardMoteurAE.EnumTypes.MOYEN), trentieme));
         if (dateAvancement != null) {
            parametrageAE.getPropositionAE().setReliquatCalculeMoyenAA(parametrageAE.getReliquat().getYears());
            parametrageAE.getPropositionAE().setReliquatCalculeMoyenMM(parametrageAE.getReliquat().getMonths());
            parametrageAE.getPropositionAE().setReliquatCalculeMoyenJJ(parametrageAE.getReliquat().getDays());
         } else {
            dateAvancement = parametrageAE.getPresenceRetardMoteur().getDateAvancement(CalculPresenceRetardMoteurAE.EnumTypes.MOYEN);
         }
         parametrageAE.getPropositionAE().setDateCalculeeMoyen(dateAvancement);
      } else if (!EnumModeCreation.MAXI.equals(parametrageAE.getParamMoteur().getModeCreation())) {
         // MANTIS 15259 SROM 05/2012 : si le mode de création n'est pas MAXI, on passe la proposition "incomplète" en erreur. L'utilisateur devra alors saisir les dates manquantes avant de la réintégrer.
         parametrageAE.setPropositionErreur(true);
      }
      dateAvancement = parametrageAE.getPresenceRetardMoteur().getDateRecherchee(CalculPresenceRetardMoteurAE.EnumTypes.MAXI);
      parametrageAE.setReliquat(UtilsDureeCarriere.getDureeDateToNbJours(dateAvancement, parametrageAE.getPresenceRetardMoteur().getReliquat(CalculPresenceRetardMoteurAE.EnumTypes.MAXI), trentieme));
      if (dateAvancement != null) {
         parametrageAE.getPropositionAE().setReliquatCalculeMaxiAA(parametrageAE.getReliquat().getYears());
         parametrageAE.getPropositionAE().setReliquatCalculeMoyenMM(parametrageAE.getReliquat().getMonths());
         parametrageAE.getPropositionAE().setReliquatCalculeMaxiJJ(parametrageAE.getReliquat().getDays());
      } else {
         dateAvancement = parametrageAE.getPresenceRetardMoteur().getDateAvancement(CalculPresenceRetardMoteurAE.EnumTypes.MAXI);
      }
      parametrageAE.getPropositionAE().setDateCalculeeMaxi(dateAvancement);
      parametrageAE.getPresenceRetardMoteur().logIt("Finalisation de la proposition", false, false, false, true);
      // Mise a jour du type de proposition et de la date d'avancement
     this.updateTypeProposition(parametrageAE);
      this.updateProposition4SpecifCas(parametrageAE);
   }

   /**
    * Sauvegarde la proposition
    */
   private void saveProposition(ParametrageAE parametrageAE) {
      if (parametrageAE.getPropositionErreur()) {// en erreur

         parametrageAE.getPropositionAE().setTypeProposition(EnumTypeProposition.ERREUR);
         parametrageAE.getPropositionAE().setPromouvable(false);
         parametrageAE.getPropositionAE().setMsgControle(parametrageAE.getMsgErreurProposition().toString().substring(0,  parametrageAE.getMsgErreurProposition().length()>499?499:parametrageAE.getMsgErreurProposition().length()));
      }
      // Si on calcul pour l'injection, on ne doit pas enregistrer le proposition
      if (!parametrageAE.getParamMoteur().isRecaculerPourInjection() && parametrageAE.getSaveProposition()) {
         this.daoPropositionAE.saveCascadeJustifAndRetard(parametrageAE.getPropositionAE());
         log.debug("Enregistrement de la proposition en base  ID : " + parametrageAE.getPropositionAE().getId());
      }

      if (!parametrageAE.getSaveProposition()) {
         parametrageAE.setNbPropositionsNonSauvees(parametrageAE.getNbPropositionsNonSauvees()+1);
      } else if (!parametrageAE.getPropositionErreur()) {
         if (parametrageAE.getParamMoteur().isRetournerListe()) {
            parametrageAE.getMoteurAveResultDTO().addPropositionValide(parametrageAE.getPropositionAE());
         }
         log.debug("Proposition est valide.");
      } else {
         if (parametrageAE.getParamMoteur().isRetournerListe()) {
            parametrageAE.getMoteurAveResultDTO().addPropositionErreur(parametrageAE.getPropositionAE());
         }
         log.debug("Proposition est en erreur.");
      }
      // Sauvegarde
      if (log.isDebugEnabled()) {
         log.debug("Code Regroupement : " + parametrageAE.getPropositionAE().getRegroupement());
         log.debug("Type de proposition : " + parametrageAE.getPropositionAE().getTypeProposition());
            log.debug("Echelon ancien : " + parametrageAE.getPropositionAE().getEchelonAncien().getCodeEchelonWithChevron(parametrageAE.getPropositionAE().getChevronAncien()));
            if (parametrageAE.getPropositionAE().getEchelonNouveau() != null ) {
                log.debug(" Echelon nouveau : " + parametrageAE.getPropositionAE().getEchelonNouveau().getCodeEchelonWithChevron(parametrageAE.getPropositionAE().getChevronNouveau()));
            }
         log.debug("Date entrée echelon : " + UtilsDate.dateToString(parametrageAE.getPropositionAE().getDateEntreeEchelon()));
         log.debug("Date de base : " + UtilsDate.dateToString(parametrageAE.getPropositionAE().getDateBaseEchelon()));
         log.debug("Reliquat : " + new Duration(parametrageAE.getPropositionAE().getReliquat()));
         log.debug("Date mini calculée : " + UtilsDate.dateToString(parametrageAE.getPropositionAE().getDateCalculeeMini()));
         log.debug("Date moy  calculée : " + UtilsDate.dateToString(parametrageAE.getPropositionAE().getDateCalculeeMoyen()));
         log.debug("Date maxi calculée : " + UtilsDate.dateToString(parametrageAE.getPropositionAE().getDateCalculeeMaxi()));
      }

   }

   /**
    * Mise à jour du type de la proposition suivant différents paramètres
    */
   private void updateTypeProposition(ParametrageAE parametrageAE) {
      EnumTypePropoAEAgent typePropoAEAgent = EnumTypePropoAEAgent.DEFAUT;
      // Correction suite à mantis N°676
      final Set<TypePropositionAgent> listTypePropAgt = parametrageAE.getMainAgentCur().getListTypePropositionAgent();
      TypePropositionAgent typePropAgt = null;
      if (listTypePropAgt != null && !listTypePropAgt.isEmpty()) {
         for (final Iterator<TypePropositionAgent> it = listTypePropAgt.iterator(); it.hasNext();) {
            typePropAgt = it.next();
            if ((typePropAgt.getDateDebut().before(parametrageAE.getParamMoteur().getDateDebut()) || typePropAgt.getDateDebut()
                  .equals(parametrageAE.getParamMoteur().getDateDebut()))
                  && (typePropAgt.getDateFin() == null || (typePropAgt.getDateFin() != null && (typePropAgt.getDateFin()
                        .after(parametrageAE.getParamMoteur().getDateDebut()) || typePropAgt.getDateFin().equals(parametrageAE.getParamMoteur().getDateDebut()))))) {
               typePropoAEAgent = typePropAgt.getTypePropositionAE();
               break;
            }
         }
      }
      // Fin de correction suite à mantis N°676

      if (typePropoAEAgent != null && !typePropoAEAgent.equals(EnumTypePropoAEAgent.DEFAUT)) {
         parametrageAE.getPropositionAE().setPropositionInitiale(typePropoAEAgent.getTypeAvancement());
      } else {
         parametrageAE.getPropositionAE().setPropositionInitiale(parametrageAE.getParamMoteur().getTypeAvancement());
      }
      // La proposition peut encore changer suivant la note de l'agent
      RegleSpecifiqueAE regleSpecifiqueAE = parametrageAE.getParamMoteur().getRegleSpecifique();
      if (regleSpecifiqueAE != null && regleSpecifiqueAE.getNotationActiver() && regleSpecifiqueAE.getNotationImpacte()) {

         Date date = parametrageAE.getParamMoteur().getDateDebut();
         if (EnumModeCreation.CAP.equals(parametrageAE.getParamMoteur().getModeCreation())) {
            date = parametrageAE.getParamMoteur().getTableauAE().getDateSession();
         }
         // Modif NCHE le 18/04/08 : on sélectionnait la note de l'année de la CAP
         parametrageAE.getPropositionAE().setNoteAgent(this.daoFicheNotationAgent.findByNoteDefinitive(parametrageAE.getMainAgentCur(), (UtilsDate.getAnnee(date).longValue())
               - regleSpecifiqueAE.getNotationAnnee()));

         // Modif NCHE le 18/04/08 : plantage de l'appli si notes non renseignées dans la règle spécifique
         Integer noteMini = regleSpecifiqueAE.getNotationMini();
         Integer noteMoyen = regleSpecifiqueAE.getNotationMoyen();

            // CRISTAL 291314 - LDEC - 16 déc. 2010 - Si la notation n’existe pas, il faut considérer que la note est de 0.
         final float noteAgent = parametrageAE.getPropositionAE().getNoteAgent() != null ? parametrageAE.getPropositionAE().getNoteAgent() : 0;
        
            if (noteMini != null || noteMoyen != null) {
                if (noteMini != null) {
                    // il y a une note mini qui a été saisie sur la règle spécifique
                    if (noteAgent >= noteMini) {
                        // La note de l'agent est supérieure ou égale à la note mini ==> au mini
                       parametrageAE.getPropositionAE().setPropositionInitiale(EnumTypeAvancement.MINI);
                    } else if (noteMoyen != null && noteAgent < noteMoyen) {
                        // note de l'agent < à note mini et < à note moyenne ==> au MAXI
                       parametrageAE.getPropositionAE().setPropositionInitiale(EnumTypeAvancement.MAXI);
                    } else {
                        // note de l'agent < à note mini et ( >= à note moyenne ou pas de note moyenne) ==> moyen
                       parametrageAE.getPropositionAE().setPropositionInitiale(EnumTypeAvancement.MOYEN);
                    }
                } else {
                    // Pas de note mini ==> la note moyenne est donc renseignée sinon on ne serait pas là...
                    if (noteAgent < noteMoyen) {
                        // note de l'agent < à note moyenne ==> au MAXI
                       parametrageAE.getPropositionAE().setPropositionInitiale(EnumTypeAvancement.MAXI);
                    } else {
                        // note de l'agent >= à note moyenne ==> au MOYEN
                       parametrageAE.getPropositionAE().setPropositionInitiale(EnumTypeAvancement.MOYEN);
                    }
                }
            } else {
               parametrageAE.getPropositionAE().setPropositionInitiale(EnumTypeAvancement.MAXI);
            }
      }

      // Si c'est un Stagiaire, avancement toujours au maxi
      if (parametrageAE.getPropositionAE().getStatut().getType() != null && parametrageAE.getPropositionAE().getStatut().getType().equals(EnumTypeStatut.STAGIAIRE)) {
         parametrageAE.getPropositionAE().setPropositionInitiale(EnumTypeAvancement.MAXI);
      }

      // On recupere la proposition initiale ainsi intialisee
      if (parametrageAE.getPropositionAE().getPropositionInitiale().equals(EnumTypeAvancement.MINI)) {
         parametrageAE.getPropositionAE().setDateAvancement(parametrageAE.getPropositionAE().getDateCalculeeMini());
         parametrageAE.getPropositionAE().setReliquatAvancementAA(parametrageAE.getPropositionAE().getReliquatCalculeMiniAA());
         parametrageAE.getPropositionAE().setReliquatAvancementMM(parametrageAE.getPropositionAE().getReliquatCalculeMiniMM());
         parametrageAE.getPropositionAE().setReliquatAvancementJJ(parametrageAE.getPropositionAE().getReliquatCalculeMiniJJ());
      } else if (parametrageAE.getPropositionAE().getPropositionInitiale().equals(EnumTypeAvancement.MOYEN)) {
         parametrageAE.getPropositionAE().setDateAvancement(parametrageAE.getPropositionAE().getDateCalculeeMoyen());
         parametrageAE.getPropositionAE().setReliquatAvancementAA(parametrageAE.getPropositionAE().getReliquatCalculeMoyenAA());
         parametrageAE.getPropositionAE().setReliquatAvancementMM(parametrageAE.getPropositionAE().getReliquatCalculeMoyenMM());
         parametrageAE.getPropositionAE().setReliquatAvancementJJ(parametrageAE.getPropositionAE().getReliquatCalculeMoyenJJ());
      } else if (parametrageAE.getPropositionAE().getPropositionInitiale().equals(EnumTypeAvancement.MAXI)) {
         parametrageAE.getPropositionAE().setDateAvancement(parametrageAE.getPropositionAE().getDateCalculeeMaxi());
         parametrageAE.getPropositionAE().setReliquatAvancementAA(parametrageAE.getPropositionAE().getReliquatCalculeMaxiAA());
         parametrageAE.getPropositionAE().setReliquatAvancementMM(parametrageAE.getPropositionAE().getReliquatCalculeMaxiMM());
         parametrageAE.getPropositionAE().setReliquatAvancementJJ(parametrageAE.getPropositionAE().getReliquatCalculeMaxiJJ());
      } else if (parametrageAE.getPropositionAE().getPropositionInitiale().equals(EnumTypeAvancement.FORCE)) {
         parametrageAE.getPropositionAE().setDateAvancement(parametrageAE.getPropositionAE().getDateForcee());
      }

      // Mise a jour du type proposition et promouvable
      if (parametrageAE.getParamMoteur().getDateFin() != null
            && parametrageAE.getPropositionAE().getDateAvancement() != null
            && (parametrageAE.getPropositionAE().getDateAvancement().equals(parametrageAE.getParamMoteur().getDateFin()) || parametrageAE.getPropositionAE().getDateAvancement()
                  .before(parametrageAE.getParamMoteur().getDateFin()))) {
         //RCAS mantis 33576 : on ne teste que les positions ayant la même carrière que la proposition
         if (parametrageAE.getPropositionAE().getFichePositionAdmin() != null && parametrageAE.getPropositionAE().getFichePositionAdmin().getPositionAdmin() != null
               && parametrageAE.getPropositionAE().getFichePositionAdmin().getPositionAdmin().getAutoriseAvEch() != null
               && (!parametrageAE.getPropositionAE().getFichePositionAdmin().getPositionAdmin().getAutoriseAvEch()
                     && parametrageAE.getPropositionAE().getCarriere() != null
                     && parametrageAE.getPropositionAE().getCarriere().equals(parametrageAE.getPropositionAE().getFichePositionAdmin().getCarriere()))) {
           parametrageAE.getPropositionAE().setTypeProposition(EnumTypeProposition.BLOQUEE);
            parametrageAE.getPropositionAE().setPromouvable(false);
         //MANTIS 21157 getDateCalculeeMini = null si dateAvancementMiniImpossible=true (cas d'un reliquat initial supérieur au mini --> voir finalizeProposition())
         } else if (!parametrageAE.getDateAvancementMiniImpossible() && !parametrageAE.getLocalDateCapMinEgalMax() && UtilsDate.compareByDateOnly(parametrageAE.getPropositionAE().getDateCalculeeMini(), parametrageAE.getPropositionAE().getDateCalculeeMaxi()) == 0) {
            parametrageAE.getPropositionAE().setTypeProposition(EnumTypeProposition.EXCLUEDATEMINIMAXI);
            parametrageAE.getPropositionAE().setPromouvable(false);
         } else {
            parametrageAE.getPropositionAE().setTypeProposition(EnumTypeProposition.PROMOUVABLE);
            parametrageAE.getPropositionAE().setPromouvable(true);
         }
      } else {
         parametrageAE.getPropositionAE().setPromouvable(false);
         if (parametrageAE.getLocalRetardSurAbsence() || parametrageAE.getLocalRetardSurRecrutement() || parametrageAE.getLocalRetardSurPresence()) {
            parametrageAE.getPropositionAE().setTypeProposition(EnumTypeProposition.EXCLUE);
         } else if (parametrageAE.getLocalRetardSurPosition()) {
            parametrageAE.getPropositionAE().setTypeProposition(EnumTypeProposition.RETARDEE);
         } else {
            parametrageAE.getPropositionAE().setTypeProposition(EnumTypeProposition.FUTURE);
         }
      }
     
      // GIFT FR19771 - STH - 06/10/2009 - On calcule la date d'entrée dans la collectivité dans tous les cas
      if (parametrageAE.getPropositionAE().getDateAvancement() != null
            && parametrageAE.getPropositionAE().getDateEntreeCollect() != null
            && parametrageAE.getPropositionAE().getDateAvancement().before(parametrageAE.getPropositionAE().getDateEntreeCollect())) {
         parametrageAE.setPropositionErreur(true);
         parametrageAE.getMsgErreurProposition().append("\nLa date d'avancement retenue doit être supérieure ou égale à la date d'entrée dans la collectivité.");
         log.debug("####>>>>Le motif d'avancement Maxi doit être renseigné dans le paramétrage de l'organisme ou de la collectivité.<<<<####");
      }
   }

   private void updateProposition4SpecifCas(ParametrageAE parametrageAE) {
      // Cas au MAXI
      if (EnumModeCreation.MAXI.equals(parametrageAE.getParamMoteur().getModeCreation())) {
         // Modif NCHE le 03/04/08 : il faut systématiquement initialiser les données ci-dessous si MAXI
         // if ( propositionAE.getDateCalculeeMaxi() != null &&
         // propositionAE.getDateCalculeeMaxi().equals( this.parametrageAE.getParamMoteur().getDateFin())
         // && propositionAE.getDateCalculeeMaxi().before( this.parametrageAE.getParamMoteur().getDateFin() ) ) {
         parametrageAE.getPropositionAE().setPropositionInitiale(EnumTypeAvancement.MAXI);
         parametrageAE.getPropositionAE().setPropositionCAP(EnumTypeAvancement.MAXI);
         parametrageAE.getPropositionAE().setDecisionCAP(EnumTypeAvancement.MAXI.getLibelle());
         parametrageAE.getPropositionAE().setDateAvancement(parametrageAE.getPropositionAE().getDateCalculeeMaxi());
         parametrageAE.getPropositionAE().setReliquatAvancementAA(parametrageAE.getPropositionAE().getReliquatCalculeMaxiAA());
         parametrageAE.getPropositionAE().setReliquatAvancementMM(parametrageAE.getPropositionAE().getReliquatCalculeMaxiMM());
         parametrageAE.getPropositionAE().setReliquatAvancementJJ(parametrageAE.getPropositionAE().getReliquatCalculeMaxiJJ());
         if (parametrageAE.getMainSituationCur().getParamCaOrga() != null) {
            parametrageAE.getPropositionAE().setMotifAvancement(parametrageAE.getMainSituationCur().getParamCaOrga().getMotifAvancementMaxi());
         }
         // }
         // Cas a la Simulation
      } else if (EnumModeCreation.SIMULATION.equals(parametrageAE.getParamMoteur().getModeCreation())) {
         // Traitement specifique pour SIMULATION
         parametrageAE.getPropositionAE().setPropositionCAP(parametrageAE.getPropositionAE().getPropositionInitiale());
         parametrageAE.getPropositionAE().setDecisionCAP(parametrageAE.getPropositionAE().getPropositionInitiale().getLibelle());
         parametrageAE.getPropositionAE().setPromouvable(false);
      } else if(EnumModeCreation.CHEVRON.equals(parametrageAE.getParamMoteur().getModeCreation())) {
          // Modif NCHE le 19/06/08 : Nouvelle méthode pour le calcul du changement de chevron     
         parametrageAE.getPropositionAE().setPropositionInitiale(EnumTypeAvancement.MAXI);
         parametrageAE.getPropositionAE().setPropositionCAP(EnumTypeAvancement.MAXI);
         parametrageAE.getPropositionAE().setDecisionCAP(EnumTypeAvancement.MAXI.getLibelle());
         parametrageAE.getPropositionAE().setDateAvancement(parametrageAE.getPropositionAE().getDateCalculeeMaxi());
         parametrageAE.getPropositionAE().setReliquatAvancementAA(parametrageAE.getPropositionAE().getReliquatCalculeMaxiAA());
         parametrageAE.getPropositionAE().setReliquatAvancementMM(parametrageAE.getPropositionAE().getReliquatCalculeMaxiMM());
         parametrageAE.getPropositionAE().setReliquatAvancementJJ(parametrageAE.getPropositionAE().getReliquatCalculeMaxiJJ());
            if (parametrageAE.getMainSituationCur().getParamCaOrga() != null) {
               parametrageAE.getPropositionAE().setMotifAvancement( parametrageAE.getMainSituationCur().getParamCaOrga().getMotifChangementChevron());
            }
        }
        // Cas mode CAP
   }

   /**
    * Calcule la date d'entrée de l'agent dans la collectivité
    */
   private void calculeDateEntreeCollectivite(ParametrageAE parametrageAE) {
      // On récupère la collectivité de l'agent courant
      Collectivite agentCollect = parametrageAE.getMainSituationCur().getCarriere().getCollectivite();
     
      // On récupère les fiches "Arrivée Départ" de l'agent
      List<FicheArriveeDepart> listFicheArriveeDepart = daoFicheArriveeDepart.findByAgentOrderByDescDateDebut(parametrageAE.getMainAgentCur());
      FicheArriveeDepart ficheArriveeDepart = null;
      Date dateEntreeCollect = null;
     
      // Recherche la date d'entrée collectivté
      // La liste est triée par date début, la rupture se faire sur la collectivité
      for (Iterator<FicheArriveeDepart> itFic = listFicheArriveeDepart.iterator(); itFic.hasNext();) {
         ficheArriveeDepart = itFic.next();
         // Si rupture sur la collectivité, on récup la date début de la fiche
         if (agentCollect == null || !ficheArriveeDepart.getCollectivite().getId().equals(agentCollect.getId())) {
             //AMA bug constaté par DSAM sur des tests si on a deux fiche ArrDep ouvertes sur 2 collect
             //et la 1ere concerne la mauvaise collectivité, on sort a tord et dateEntreeCollect est nulle
             if (dateEntreeCollect!=null){
                parametrageAE.getPropositionAE().setDateEntreeCollect(dateEntreeCollect);
                 break;
             }
         }
         dateEntreeCollect = ficheArriveeDepart.getDateDebut();
      }
     
      // Si la rupture n'a pas été faite, et qu'on a une date d'entrée collect
      // -> On lister toute les occurences et c'est la dernière occurence qui est la bonne
      if (parametrageAE.getPropositionAE().getDateEntreeCollect() == null && dateEntreeCollect != null) {
         parametrageAE.getPropositionAE().setDateEntreeCollect(dateEntreeCollect);
      }else{
          log.debug("Attention, la date d'entrée dans la collectivité calculée est nulle");
      }
   }
  
   /**
    * Applique les regles spécifiques
    */
   private void applySpecificRules(ParametrageAE parametrageAE) {
      if (parametrageAE.getParamMoteur().getRegleSpecifique() == null) {
         return; // Pas de regleSpecifique
      }
      parametrageAE.setParamDateFin((parametrageAE.getParamMoteur().getDateFin() != null ? parametrageAE.getParamMoteur().getDateFin() : UtilsDate.INFINITE_DATE_CS));
      if (EnumTypeArrondi.SUPERIEUR.equals(parametrageAE.getParamMoteur().getRegleSpecifique().getTypeArrondi())) {
         parametrageAE.setRoundingMode(RoundingMode.UP);
      } else if (EnumTypeArrondi.INFERIEUR.equals(parametrageAE.getParamMoteur().getRegleSpecifique().getTypeArrondi())) {
         parametrageAE.setRoundingMode(RoundingMode.DOWN);
      }

      parametrageAE.setMathContext(new MathContext(1, parametrageAE.getRoundingMode()));

      this.regleSpeciqueAbsence(parametrageAE);
      this.regleSpeciqueRecrutement(parametrageAE);
      this.regleSpeciquePresence(parametrageAE);
      this.impacteDateAvancement(parametrageAE);
   }

   /**
    * Applique la regle spécifique sur l'absence
    */
   private void regleSpeciqueAbsence(ParametrageAE parametrageAE) {
      if (parametrageAE.getParamMoteur().getRegleSpecifique().getAbsencesActiver()) {
         // Absences
         Date dateDebut = null;
         Date dateFin = null;
         // Récupération des la liste des pointage de l'agent courant entre la DEE et date de fin (paramètre moteur)
         // Modif NCHE 02/05/08 : ajout date de base
         // List<PointageTransforme> listPointage =
         // this.daoPointageTransforme.findPointageTransformeAgentValidDontAvancEch(mainAgentCur, dateEntreeEchelon,
         // paramDateFin);
         List<PointageTransforme> listPointage = this.daoPointageTransforme.findPointageTransformeAgentValidDontAvancEch(parametrageAE.getMainAgentCur(), parametrageAE.getDateBase(), parametrageAE.getParamDateFin());
         PointageTransforme pointage = null;
         for (Iterator<PointageTransforme> itPointage = listPointage.iterator(); itPointage.hasNext();) {
            pointage = itPointage.next();

            dateDebut = pointage.getDateDebut();
            // Modif NCHE 02/05/08 : ajout date de base
            // if ( dateDebut.before( dateEntreeEchelon ) ) {
            // dateDebut = (Date) dateEntreeEchelon.clone();
            // }
            if (dateDebut.before(parametrageAE.getDateBase())) {
               dateDebut = (Date) parametrageAE.getDateBase().clone();
            }

            dateFin = pointage.getDateFin();
            if (dateFin.after(parametrageAE.getParamDateFin())) {
               dateFin = (Date) parametrageAE.getParamDateFin().clone();
            }
            // Calcul de la durée
            BigDecimal duree = new BigDecimal(UtilsDureeCarriere.getNbJoursDateToDate(dateDebut, dateFin, trentieme));
            parametrageAE.setNbJoursAbsences(parametrageAE.getNbJoursAbsences().add(duree));
            parametrageAE.setNbJoursAbsencesMin(parametrageAE.getNbJoursAbsencesMin().add(duree.multiply(new BigDecimal(pointage.getCodeAbsence().getPctMinAvEch()).divide(new BigDecimal(
                  100)))));
            parametrageAE.setNbJoursAbsencesMoy(parametrageAE.getNbJoursAbsencesMoy().add(duree.multiply(new BigDecimal(pointage.getCodeAbsence().getPctMoyAvEch()).divide(new BigDecimal(
                  100)))));
         }
         // Si Impacte la date d'avancement
         if (EnumTypeImpacte.DATE.equals(parametrageAE.getParamMoteur().getRegleSpecifique().getAbsencesImpacte())) {
            if (parametrageAE.getParamMoteur().getRegleSpecifique().getAbsencesGlobal() != null
                  && parametrageAE.getParamMoteur().getRegleSpecifique().getAbsencesGlobal() > 0) {
               parametrageAE.setNbJoursAbsencesMin(parametrageAE.getNbJoursAbsencesMin().subtract(new BigDecimal(parametrageAE.getParamMoteur().getRegleSpecifique().getAbsencesGlobal())));
               parametrageAE.setNbJoursAbsencesMoy(parametrageAE.getNbJoursAbsencesMoy().subtract(new BigDecimal(parametrageAE.getParamMoteur().getRegleSpecifique().getAbsencesGlobal())));
            } else if (parametrageAE.getParamMoteur().getRegleSpecifique().getAbsencesAnnuel() != null
                  && parametrageAE.getParamMoteur().getRegleSpecifique().getAbsencesAnnuel() > 0) {
               parametrageAE.setNbJoursAbsencesMin(parametrageAE.getNbJoursAbsencesMin().subtract(new BigDecimal(parametrageAE.getParamMoteur().getRegleSpecifique().getAbsencesAnnuel()).multiply(parametrageAE.getNbJoursConditionAvctMini())));
               parametrageAE.setNbJoursAbsencesMoy(parametrageAE.getNbJoursAbsencesMoy().subtract(new BigDecimal(parametrageAE.getParamMoteur().getRegleSpecifique().getAbsencesAnnuel()).multiply(parametrageAE.getNbJoursConditionAvctMoyen())));
            }

            if (parametrageAE.getNbJoursAbsencesMin().compareTo(BigDecimal.ZERO) < 0) {
               parametrageAE.setNbJoursAbsencesMin(BigDecimal.ZERO);
            }

            if (parametrageAE.getNbJoursAbsencesMoy().compareTo(BigDecimal.ZERO) < 0) {
               parametrageAE.setNbJoursAbsencesMoy(BigDecimal.ZERO);
            }

            if (parametrageAE.getParamMoteur().getRegleSpecifique().getImpacteMini() && parametrageAE.getNbJoursAbsencesMin().compareTo(BigDecimal.ZERO) > 0) {
               // Création de retard pour l'absence au mini
               // Creation du commentaire pour le retard
               StringBuffer commentaire = new StringBuffer("Retard sur absence : ");
               // Modif NCHE le 07/02/08 : ajout date base
               // commentaire.append( " du " ).append( UtilsDate.dateToString( dateEntreeEchelon ) );
               commentaire.append(" du ").append(UtilsDate.dateToString(parametrageAE.getDateBase()));
               commentaire.append(" au ").append(UtilsDate.dateToString(parametrageAE.getParamDateFin()));
               parametrageAE.getPresenceRetardMoteur().addRetardOnProposition(CalculPresenceRetardMoteurAE.EnumTypes.MINI, parametrageAE.getPresenceRetardMoteur().buildRetard(commentaire.toString(), parametrageAE.getNbJoursAbsencesMin(), EnumTypeRetard.ABSENCE));
               parametrageAE.setLocalRetardSurAbsence(true);
            }

            if (parametrageAE.getParamMoteur().getRegleSpecifique().getImpacteMoyen() && parametrageAE.getNbJoursAbsencesMoy().compareTo(BigDecimal.ZERO) > 0) {
               // Création de retard pour l'absence au moyen
               // Creation du commentaire pour le retard
               StringBuffer commentaire = new StringBuffer("Retard sur absence : ");
               // Modif NCHE le 07/02/08 : ajout date base
               // commentaire.append( " du " ).append( UtilsDate.dateToString( dateEntreeEchelon ) );
               commentaire.append(" du ").append(UtilsDate.dateToString(parametrageAE.getDateBase()));
               commentaire.append(" au ").append(UtilsDate.dateToString(parametrageAE.getParamDateFin()));
               parametrageAE.getPresenceRetardMoteur().addRetardOnProposition(CalculPresenceRetardMoteurAE.EnumTypes.MOYEN, parametrageAE.getPresenceRetardMoteur().buildRetard(commentaire.toString(), parametrageAE.getNbJoursAbsencesMoy(), EnumTypeRetard.ABSENCE));
               parametrageAE.setLocalRetardSurAbsence(true);
            }
         } else if (EnumTypeImpacte.PRESENCE.equals(parametrageAE.getParamMoteur().getRegleSpecifique().getAbsencesImpacte())) {
            if (parametrageAE.getNbJoursAbsencesMin().compareTo(BigDecimal.ZERO) > 0) {
               parametrageAE.setNbJoursPresencesGlobalMin(parametrageAE.getNbJoursPresencesGlobalMin().add(parametrageAE.getNbJoursAbsencesMin()));
            }

            if (parametrageAE.getNbJoursAbsencesMoy().compareTo(BigDecimal.ZERO) > 0) {
               parametrageAE.setNbJoursPresencesGlobalMoy(parametrageAE.getNbJoursPresencesGlobalMoy().add(parametrageAE.getNbJoursAbsencesMoy()));
            }
         }
         // Applique l'arrondi définit
         parametrageAE.getNbJoursAbsencesMin().round(parametrageAE.getMathContext());
         parametrageAE.getNbJoursAbsencesMoy().round(parametrageAE.getMathContext());

         Duration dureeAbsences = UtilsDureeCarriere.getDureeDateToNbJours(parametrageAE.getParamDateFin(), parametrageAE.getNbJoursAbsences(), trentieme);
         parametrageAE.getPropositionAE().setAbsenceAA(dureeAbsences.getYears());
         parametrageAE.getPropositionAE().setAbsenceMM(dureeAbsences.getMonths());
         parametrageAE.getPropositionAE().setAbsenceJJ(dureeAbsences.getDays());
      }
   }


   /**
    * Applique la regle spécifique sur le recrutement
    */
   private void regleSpeciqueRecrutement(ParametrageAE parametrageAE) {
      // Recrutement
      if (parametrageAE.getParamMoteur().getRegleSpecifique().getRecrutementActiver()) {
        
         // GIFT FR19771 - STH - 06/10/2009 - On calcule la date d'entrée dans la collectivité dans tous les cas
         // // On récupère la collectvité de l'agent courant
         // Collectivite agentCollect = mainSituationCur.getCarriere().getCollectivite();
         // // On récupère les fiches "Arrivée Départ" de l'agent
         // List<FicheArriveeDepart> listFicheArriveeDepart = daoFicheArriveeDepart.findByAgentOrderByDescDateDebut(mainAgentCur);
         // FicheArriveeDepart ficheArriveeDepart = null;
         // Date dateEntreeCollect = null;
         // // Recherche la date d'entrée collectivté
         // // La liste est triée par date début, la rupture se faire sur la collectivité
         // for (Iterator<FicheArriveeDepart> itFic = listFicheArriveeDepart.iterator(); itFic.hasNext();) {
         // ficheArriveeDepart = itFic.next();
         // // Si rutpure sur la collectivité, on récup la date début de la fiche
         // if (agentCollect == null || !ficheArriveeDepart.getCollectivite().getId().equals(agentCollect.getId())) {
         // propositionAE.setDateEntreeCollect(dateEntreeCollect);
         // break;
         // }
         // dateEntreeCollect = ficheArriveeDepart.getDateDebut();
         // }
         // // Si la rupture n'a pas été faite, et qu'on une date d'entrée collect
         // // -> On lister toute les occurences et c'est la dernière occurence qui est la bonne
        // if (propositionAE.getDateEntreeCollect() == null && dateEntreeCollect != null) {
         // propositionAE.setDateEntreeCollect(dateEntreeCollect);
         // }
         Date dateEntreeCollect = parametrageAE.getPropositionAE().getDateEntreeCollect();

         if (parametrageAE.getPropositionAE().getDateBaseEchelon().before(dateEntreeCollect)) {
            parametrageAE.setNbJoursRecrutement(parametrageAE.getNbJoursRecrutement().subtract(new BigDecimal(
                  UtilsDureeCarriere.getNbJoursDateToDate(dateEntreeCollect, parametrageAE.getPropositionAE().getDateBaseEchelon(), trentieme))));
            if (EnumTypeImpacte.DATE.equals(parametrageAE.getParamMoteur().getRegleSpecifique().getRecrutementImpacte())) {
               if (parametrageAE.getParamMoteur().getRegleSpecifique().getRecrutementGlobal() != null
                     && parametrageAE.getParamMoteur().getRegleSpecifique().getRecrutementGlobal() > 0) {
                  parametrageAE.setNbJoursRecrutementMin(parametrageAE.getNbJoursRecrutement().subtract(new BigDecimal(parametrageAE.getParamMoteur().getRegleSpecifique()
                        .getRecrutementGlobal())));
                  if (parametrageAE.getNbJoursRecrutementMin().compareTo(BigDecimal.ZERO) < 0) {
                     parametrageAE.setNbJoursRecrutementMin(BigDecimal.ZERO);
                  }
                  parametrageAE.setNbJoursPresencesGlobalMoy(parametrageAE.getNbJoursRecrutementMin());
               } else if (parametrageAE.getParamMoteur().getRegleSpecifique().getRecrutementAnnuel() != null
                     && parametrageAE.getParamMoteur().getRegleSpecifique().getRecrutementAnnuel() > 0) {
                  parametrageAE.setNbJoursRecrutementMin(parametrageAE.getNbJoursRecrutement().subtract(new BigDecimal(parametrageAE.getParamMoteur().getRegleSpecifique()
                        .getRecrutementAnnuel()).multiply(parametrageAE.getNbJoursConditionAvctMini())));
                  parametrageAE.setNbJoursRecrutementMoy(parametrageAE.getNbJoursRecrutement().subtract(new BigDecimal(parametrageAE.getParamMoteur().getRegleSpecifique()
                        .getRecrutementAnnuel()).multiply(parametrageAE.getNbJoursConditionAvctMoyen())));
               }

               if (parametrageAE.getNbJoursRecrutementMin().compareTo(BigDecimal.ZERO) < 0) {
                  parametrageAE.setNbJoursRecrutementMin(BigDecimal.ZERO);
               }
               if (parametrageAE.getNbJoursRecrutementMoy().compareTo(BigDecimal.ZERO) < 0) {
                  parametrageAE.setNbJoursRecrutementMoy(BigDecimal.ZERO);
               }
            }

            if (EnumDecalage.PONDERE.equals(parametrageAE.getParamMoteur().getRegleSpecifique().getRecrutementDecalage())) {
               if (parametrageAE.getNbJoursRecrutementMin().compareTo(BigDecimal.ZERO) > 0) {
                  parametrageAE.setNbJoursRecrutementMin(parametrageAE.getNbJoursConditionAvctMaxi().subtract(parametrageAE.getNbJoursConditionAvctMaxi())
                        .multiply(parametrageAE.getNbJoursRecrutementMin().divide(parametrageAE.getNbJoursConditionAvctMini())));
               }
               if (parametrageAE.getNbJoursRecrutementMoy().compareTo(BigDecimal.ZERO) > 0) {
                  parametrageAE.setNbJoursRecrutementMoy(parametrageAE.getNbJoursConditionAvctMaxi().subtract(parametrageAE.getNbJoursConditionAvctMaxi())
                        .multiply(parametrageAE.getNbJoursRecrutementMoy().divide(parametrageAE.getNbJoursConditionAvctMini())));
               }
            }

            if (parametrageAE.getParamMoteur().getRegleSpecifique().getImpacteMini() && parametrageAE.getNbJoursRecrutementMin().compareTo(BigDecimal.ZERO) > 0) {
               // Création de retard pour le recrutement au mini
               // Creation du commentaire pour le retard
               StringBuffer commentaire = new StringBuffer("Retard sur recrutement : ");
               // Modif NCHE le 07/02/08 : ajout date base
               // commentaire.append( " du " ).append( UtilsDate.dateToString( dateEntreeEchelon ) );
               commentaire.append(" du ").append(UtilsDate.dateToString(parametrageAE.getDateBase()));
               commentaire.append(" au ").append(UtilsDate.dateToString(parametrageAE.getParamDateFin()));
               parametrageAE.getPresenceRetardMoteur().addRetardOnProposition(CalculPresenceRetardMoteurAE.EnumTypes.MINI, parametrageAE.getPresenceRetardMoteur().buildRetard(commentaire.toString(), parametrageAE.getNbJoursRecrutementMin(), EnumTypeRetard.RECRUTEMENT));
               parametrageAE.setLocalRetardSurRecrutement(true);
            }

            if (parametrageAE.getParamMoteur().getRegleSpecifique().getImpacteMoyen() && parametrageAE.getNbJoursRecrutementMoy().compareTo(BigDecimal.ZERO) > 0) {
               // Création de retard pour le recrutement au moyen
               // Creation du commentaire pour le retard
               StringBuffer commentaire = new StringBuffer("Retard sur recrutement : ");
               // Modif NCHE le 07/02/08 : ajout date base
               // commentaire.append( " du " ).append( UtilsDate.dateToString( dateEntreeEchelon ) );
               commentaire.append(" du ").append(UtilsDate.dateToString(parametrageAE.getDateBase()));
               commentaire.append(" au ").append(UtilsDate.dateToString(parametrageAE.getParamDateFin()));
               parametrageAE.getPresenceRetardMoteur().addRetardOnProposition(CalculPresenceRetardMoteurAE.EnumTypes.MOYEN, parametrageAE.getPresenceRetardMoteur().buildRetard(commentaire.toString(), parametrageAE.getNbJoursRecrutementMoy(), EnumTypeRetard.RECRUTEMENT));
               parametrageAE.setLocalRetardSurRecrutement(true);
            }
         } else if (EnumTypeImpacte.PRESENCE.equals(parametrageAE.getParamMoteur().getRegleSpecifique().getRecrutementImpacte())) {
            if (parametrageAE.getNbJoursRecrutementMin().compareTo(BigDecimal.ZERO) > 0) {
               parametrageAE.setNbJoursPresencesGlobalMin(parametrageAE.getNbJoursPresencesGlobalMin().add(parametrageAE.getNbJoursRecrutementMin()));
            }
            if (parametrageAE.getNbJoursRecrutementMoy().compareTo(BigDecimal.ZERO) > 0) {
               parametrageAE.setNbJoursPresencesGlobalMoy(parametrageAE.getNbJoursPresencesGlobalMoy().add(parametrageAE.getNbJoursRecrutementMoy()));
            }
         }
         // Applique l'arrondi définit
         parametrageAE.getNbJoursRecrutementMin().round(parametrageAE.getMathContext());
         parametrageAE.getNbJoursRecrutementMoy().round(parametrageAE.getMathContext());
      }
   }

   /**
    * Applique la regle spécifique sur la presence
    */
   private void regleSpeciquePresence(ParametrageAE parametrageAE) {
      // Presence
      if (parametrageAE.getParamMoteur().getRegleSpecifique().getPresenceActiver()) {
         if (parametrageAE.getParamMoteur().getRegleSpecifique().getPresenceGlobal() != null && parametrageAE.getParamMoteur().getRegleSpecifique().getPresenceGlobal() > 0) {
            parametrageAE.setNbJoursPresencesGlobalMin(parametrageAE.getNbJoursPresencesGlobalMin().subtract(new BigDecimal(parametrageAE.getParamMoteur().getRegleSpecifique()
                  .getPresenceGlobal())));
            parametrageAE.setNbJoursPresencesGlobalMoy(parametrageAE.getNbJoursPresencesGlobalMoy().subtract(new BigDecimal(parametrageAE.getParamMoteur().getRegleSpecifique()
                  .getPresenceGlobal())));

         } else if (parametrageAE.getParamMoteur().getRegleSpecifique().getPresenceAnnuel() != null
               && parametrageAE.getParamMoteur().getRegleSpecifique().getPresenceAnnuel() > 0) {
            parametrageAE.setNbJoursPresencesGlobalMin(parametrageAE.getNbJoursPresencesGlobalMin().subtract(new BigDecimal(parametrageAE.getParamMoteur().getRegleSpecifique()
                  .getPresenceAnnuel()).multiply(parametrageAE.getNbJoursConditionAvctMini())));
            parametrageAE.setNbJoursPresencesGlobalMoy(parametrageAE.getNbJoursPresencesGlobalMoy().subtract(new BigDecimal(parametrageAE.getParamMoteur().getRegleSpecifique()
                  .getPresenceAnnuel()).multiply(parametrageAE.getNbJoursConditionAvctMoyen())));
         }

         if (parametrageAE.getNbJoursPresencesGlobalMin().compareTo(BigDecimal.ZERO) < 0) {
            parametrageAE.setNbJoursPresencesGlobalMin(BigDecimal.ZERO);
         }
         if (parametrageAE.getNbJoursPresencesGlobalMoy().compareTo(BigDecimal.ZERO) < 0) {
            parametrageAE.setNbJoursPresencesGlobalMoy(BigDecimal.ZERO);
         }

         if (parametrageAE.getParamMoteur().getRegleSpecifique().getPresenceImpacte() && parametrageAE.getParamMoteur().getRegleSpecifique().getImpacteMini()
               && parametrageAE.getNbJoursPresencesGlobalMin().compareTo(BigDecimal.ZERO) > 0) {
            // Création de retard pour la présence au mini
            // Creation du commentaire pour le retard
            StringBuffer commentaire = new StringBuffer("Retard sur présence : ");
            // Modif NCHE le 07/02/08 : ajout date base
            // commentaire.append( " du " ).append( UtilsDate.dateToString( dateEntreeEchelon ) );
            commentaire.append(" du ").append(UtilsDate.dateToString(parametrageAE.getDateBase()));
            commentaire.append(" au ").append(UtilsDate.dateToString(parametrageAE.getParamDateFin()));
            parametrageAE.getPresenceRetardMoteur().addRetardOnProposition(CalculPresenceRetardMoteurAE.EnumTypes.MINI, parametrageAE.getPresenceRetardMoteur().buildRetard(commentaire.toString(), parametrageAE.getNbJoursPresencesGlobalMin(), EnumTypeRetard.PRESENCE));
            parametrageAE.setLocalRetardSurPresence(true);
         }

         if (parametrageAE.getParamMoteur().getRegleSpecifique().getPresenceImpacte() && parametrageAE.getParamMoteur().getRegleSpecifique().getImpacteMoyen()
               && parametrageAE.getNbJoursPresencesGlobalMoy().compareTo(BigDecimal.ZERO) > 0) {
            // Création de retard pour la présence au moyen
            // Creation du commentaire pour le retard
            StringBuffer commentaire = new StringBuffer("Retard sur présence : ");
            // Modif NCHE le 07/02/08 : ajout date base
            // commentaire.append( " du " ).append( UtilsDate.dateToString( dateEntreeEchelon ) );
            commentaire.append(" du ").append(UtilsDate.dateToString(parametrageAE.getDateBase()));
           commentaire.append(" au ").append(UtilsDate.dateToString(parametrageAE.getParamDateFin()));
            parametrageAE.getPresenceRetardMoteur().addRetardOnProposition(CalculPresenceRetardMoteurAE.EnumTypes.MOYEN, parametrageAE.getPresenceRetardMoteur().buildRetard(commentaire.toString(), parametrageAE.getNbJoursPresencesGlobalMoy(), EnumTypeRetard.PRESENCE));
            parametrageAE.setLocalRetardSurPresence(true);
         }
      }

      // Applique l'arrondi définit
      parametrageAE.getNbJoursPresencesGlobalMin().round(parametrageAE.getMathContext());
      parametrageAE.getNbJoursPresencesGlobalMoy().round(parametrageAE.getMathContext());

      BigDecimal nbJoursPresences = parametrageAE.getNbJoursRecrutement().add(parametrageAE.getNbJoursAbsences());
      Duration dureePresence = new Duration(nbJoursPresences.intValue());
      parametrageAE.getPropositionAE().setPresenceAA(dureePresence.getYears());
      parametrageAE.getPropositionAE().setPresenceMM(dureePresence.getMonths());
      parametrageAE.getPropositionAE().setPresenceJJ(dureePresence.getDays());
   }

   /**
    *
    */
   private void impacteDateAvancement(ParametrageAE parametrageAE) {
      // Impacte le mini
      if (parametrageAE.getParamMoteur().getRegleSpecifique().getImpacteMini()) {
         // Durée du retard 20 jours au mini | 10
         Duration dureeRetardsMini = new Duration(parametrageAE.getPresenceRetardMoteur().getDureeRetardsMini().intValue());
         if (!dureeRetardsMini.equalsToZero()) {
            // Exemple reliquat de 10 jours au mini | 20
            Duration reliquatCalculeMini = new Duration(parametrageAE.getPropositionAE().getReliquatCalculeMiniAA(),
                  parametrageAE.getPropositionAE().getReliquatCalculeMiniMM(), parametrageAE.getPropositionAE().getReliquatCalculeMiniJJ());
            // Restant a diminué sur la date = 20 - 10 = 10 j | 10 - 20 = 0j
            Duration resteDuree = dureeRetardsMini.subtract(reliquatCalculeMini);
            // Reliquat déduit : 0 j | 10j
            reliquatCalculeMini = reliquatCalculeMini.subtract(dureeRetardsMini);
            // S'il existe encore du retard a déduire : 10j | 0j
            if (resteDuree.equalsToZero()) {
               // Déduction immédiate sur la date calculée mini
               Date dateCalucleeMini = UtilsDureeCarriere.subDureeOnDate(parametrageAE.getPropositionAE().getDateCalculeeMini(), resteDuree.getYears(), resteDuree.getMonths(), resteDuree.getDays());
               // Si la date mini et plus grande que la date maxi alors mini = maxi
               if (dateCalucleeMini.after(parametrageAE.getPropositionAE().getDateCalculeeMaxi())) {
                  dateCalucleeMini = parametrageAE.getPropositionAE().getDateCalculeeMaxi();
               }
               parametrageAE.getPropositionAE().setDateCalculeeMini(dateCalucleeMini);
               parametrageAE.getPropositionAE().setReliquatCalculeMiniAA(0);
               parametrageAE.getPropositionAE().setReliquatCalculeMiniMM(0);
               parametrageAE.getPropositionAE().setReliquatCalculeMiniJJ(0);
            } else {
               parametrageAE.getPropositionAE().setReliquatCalculeMiniAA(resteDuree.getYears());
               parametrageAE.getPropositionAE().setReliquatCalculeMiniMM(resteDuree.getMonths());
               parametrageAE.getPropositionAE().setReliquatCalculeMiniJJ(resteDuree.getDays());
            }
         }
      }
      // Impacte le Moyen
      if (parametrageAE.getParamMoteur().getRegleSpecifique().getImpacteMoyen()) {
         // Durée du retard 20 jours au Moyen | 10
         Duration dureeRetardsMoyen = new Duration(parametrageAE.getPresenceRetardMoteur().getDureeRetardsMoyen().intValue());
         if (!dureeRetardsMoyen.equalsToZero()) {
            // Exemple reliquat de 10 jours au Moyen | 20
            Duration reliquatCalculeMoyen = new Duration(parametrageAE.getPropositionAE().getReliquatCalculeMoyenAA(),
                  parametrageAE.getPropositionAE().getReliquatCalculeMoyenMM(), parametrageAE.getPropositionAE().getReliquatCalculeMoyenJJ());
            // Restant a diminué sur la date = 20 - 10 = 10 j | 10 - 20 = 0j
            Duration resteDuree = dureeRetardsMoyen.subtract(reliquatCalculeMoyen);
            // Reliquat déduit : 0 j | 10j
            reliquatCalculeMoyen = reliquatCalculeMoyen.subtract(dureeRetardsMoyen);
            // S'il existe encore du retard a déduire : 10j | 0j
            if (resteDuree.equalsToZero()) {
               // Déduction immédiate sur la date calculée Moyen
               Date dateCalucleeMoyen = UtilsDureeCarriere.subDureeOnDate(parametrageAE.getPropositionAE().getDateCalculeeMoyen(), resteDuree.getYears(), resteDuree.getMonths(), resteDuree.getDays());
               // Si la date Moyen et plus grande que la date maxi alors Moyen = maxi
               if (dateCalucleeMoyen.after(parametrageAE.getPropositionAE().getDateCalculeeMaxi())) {
                  dateCalucleeMoyen = parametrageAE.getPropositionAE().getDateCalculeeMaxi();
               }
               parametrageAE.getPropositionAE().setDateCalculeeMoyen(dateCalucleeMoyen);
               parametrageAE.getPropositionAE().setReliquatCalculeMoyenAA(0);
               parametrageAE.getPropositionAE().setReliquatCalculeMoyenMM(0);
               parametrageAE.getPropositionAE().setReliquatCalculeMoyenJJ(0);
            } else {
               parametrageAE.getPropositionAE().setReliquatCalculeMoyenAA(resteDuree.getYears());
               parametrageAE.getPropositionAE().setReliquatCalculeMoyenMM(resteDuree.getMonths());
               parametrageAE.getPropositionAE().setReliquatCalculeMoyenJJ(resteDuree.getDays());
            }
         }
      }

   }

   /**
    * Log de fin de traitement
    */
   private void logIt(ParametrageAE parametrageAE) {
      if (log.isInfoEnabled()) {
         if (!parametrageAE.getParamMoteur().isRetournerListe()) {
            log.info("/!\\Les propositions n'ont pas été conservées en mémoire/!\\");
         }
         if (!parametrageAE.getParamMoteur().isRetournerListe()) {
            log.info("(/!\\Les propositions n'ont pas été sauvées en base/!\\");
         }

         log.info("[STATS] Nombre total de situations traitées: " + parametrageAE.getNbSituations());
         log.info("[STATS] Nombre de propositions valides : " + parametrageAE.getMoteurAveResultDTO().getListPropositionsValides().size());
         log.info("[STATS] Nombre de propositions en erreur   : " + parametrageAE.getMoteurAveResultDTO().getListPropositionsEnErreur().size());
         log.info("[STATS] Nombre de propositions non sauvées en base : " + parametrageAE.getNbPropositionsNonSauvees());
         log.info("[STATS] Nombre total de propositions Officialisées/Maxi avec dateAvancement au maxi trouvées : "
               + parametrageAE.getNbPropositionOffMax());
         log.info("[STATS] Nombre total de propositions Officialisées/Maxi totales: " + parametrageAE.getNbPropositionOffMaxTotal());

         log.info(parametrageAE.getExecTimes().toString());
         if (parametrageAE.getMoteurAveResultDTO().getListPropositionsEnErreur().size() > 0) {
            log.info("--Liste des propositions en erreur -----------------------------------------------------------------------");
            for (Iterator<PropositionAE> it = parametrageAE.getMoteurAveResultDTO().getListPropositionsEnErreur().iterator(); it.hasNext();) {
               PropositionAE propErreur = it.next();
               log.info("Proposition : " + propErreur.getId());
               log.info("Message erreur  : " + propErreur.getMsgControle());
            }
         }
      }
   }
}