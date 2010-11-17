/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.model;

import javax.faces.context.FacesContext;
import acorn.db.*;
import acorn.errorHandling.ErrorBean;
import acorn.task.TaskQueue;
import acorn.userManagement.UserManager;
import javax.jms.JMSException;
import javax.servlet.http.*;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.Date;
import java.util.Collections;
import java.util.Comparator;
import java.util.Calendar;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Set;

/**
 *
 * @author dl236088
 * TODO usuwanie zbednych pol z data 
 * (a4j:poll bedzie odswiezal czasy, 
 * a ogolny timer bedzie usuwal nieuzywane daty)
 */
public class TaskBean {

    /**
     * Checks if model @m is accessible by current user.
     * @return true or flase
     */
    public static Boolean modelSecurityCheck(EModel m) {
        if (m == null) {
            return false;
        }
        if (m.isShared()) {
            return true;
        }
        EUser user = UserManager.getCurrentUser();
        if (user == null) {
            return false;
        } else if (user.getStatus().equals(EUser.statusAdmin)) {
            return true;
        } else if (m.getOwner() == null) {
            return false; // TODO is this allright?
        } else {
            return m.getOwner().getId().equals(user.getId());
        }
    }
    private Map<Integer, TaskBeanData> data;
    private int displayRowMax;
    private int displaySpeciesRowMax;
    private int displayGenesRowMax;
    /* How many TaskBeanData entries can be kept in a data map */
    private int dataCountMax;

    /** Creates a new instance of TaskBean */
    public TaskBean() {
        super();
        dataCountMax = 20;
        displayGenesRowMax = displaySpeciesRowMax = displayRowMax = 50;
        data = new TreeMap();
    }

    /** Initializes data for current model before usage
     * @param id
     */
    private Boolean initData(Integer id) {
        if (data.containsKey(id)) {
            if (!modelSecurityCheck(data.get(id).getModel())) {
                return false;
            }
            if (data.get(id).conditions == null) {
                data.get(id).conditions =
                        justFetchConditions(data.get(id).getModel());
            }
            if (data.get(id).species == null) {
                data.get(id).species =
                        justFetchSpecies(data.get(id).getModel());
            }
            if (data.get(id).genes == null) {
                data.get(id).genes =
                        justFetchGenes(data.get(id).getModel());
            }
            data.get(id).touch();

            return true;
        }

        while (data.size() > dataCountMax - 1) {
            Date a = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, -60);
            TaskBeanData oldest = Collections.min(data.values(),
                    new Comparator<Object>() {

                        public int compare(Object a, Object b) {
                            return ((TaskBeanData) a).lastUse.compareTo(
                                    ((TaskBeanData) b).lastUse);
                        }
                    });
            if (oldest.lastUse.compareTo(calendar.getTime()) < 0) {
                data.remove(oldest.getModel().getId());
            }
        }

        EModel m = null;

        try {
            EModelController mc = new EModelController();
            m = mc.getModel(id);
        } catch (Exception e) {
            /* there is no ErrorBean call here because Exception means
             * that there's no such model so we should return false */
            return false;
        }

        if (m == null) {
            return false;
        }

        if (!modelSecurityCheck(m)) {
            return false;
        }

        TaskBeanData d = new TaskBeanData(m);

        d.originalConditions = justFetchConditions(m);

        d.conditions = copyConditionList(d.originalConditions);

        d.filteredConditions = new LinkedList<Condition>();
        for (Condition c : d.conditions) {
            d.filteredConditions.add(c);
        }
        d.filteredSpecies = d.species = justFetchSpecies(m);
        d.filteredGenes = d.genes = justFetchGenes(m);

        d.reactionsStart = 0;
        d.speciesStart = 0;
        d.genesStart = 0;

        data.put(id, d);

        return true;
    }

    public List<Condition> copyConditionList(List<Condition> originalList) {
        List<Condition> newList = new LinkedList<Condition>();
        if (originalList.size() > 0) {
            /* each Condition in a list contains a BoundID to EBound Map
             * shared between all elements. It have to be copied (can't be
             * shared between two diffrent lists) but also have to be shared
             * between conditions */
            Map originalBoundsMap = originalList.get(0).getBoundsMap();
            Map newBoundsMap = new HashMap<Integer, EBounds>();

            Set<Map.Entry<Integer, EBounds>> boundsMapEntrySet = originalBoundsMap.entrySet();

            for (Map.Entry<Integer, EBounds> mapEntry : boundsMapEntrySet) {
                newBoundsMap.put(mapEntry.getKey(), mapEntry.getValue().copy());
            }

            for (Condition c : originalList) {
                newList.add(c.copy(newBoundsMap));
            }
        }
        return newList;
    }

    /** Returns title for the page, 
     * it's called at the very begging of rendering the page
     * @return
     */
    public String getTitleModelDetails() {
        FacesContext fc = FacesContext.getCurrentInstance();
        dataCountMax = Integer.parseInt((String) fc.getExternalContext().getInitParameter("dataCountMax"));
        displayGenesRowMax = displaySpeciesRowMax = displayRowMax = Integer.parseInt((String) fc.getExternalContext().getInitParameter("displayRowMax"));
        getIsModelValid();
        //wczesniej bylo tu if (getIsModelValid()) { fetchowanie conditions, species i genes}
        //najprawdopodobniej to zle, gdyz za kazdym wyswietleniem strony fetchuja nam sie od nowa te rzeczy
        //a fetching jesli jest potrzebny i tak jest wykonywany w initData (ktore jest wykonywane wewnatrz getIsModelValid) (Szymon)
        return "Model details";
    }

    public String getParametersInit() {
        FacesContext fc = FacesContext.getCurrentInstance();
        dataCountMax = Integer.parseInt((String) fc.getExternalContext().getInitParameter("dataCountMax"));
        displayGenesRowMax = displaySpeciesRowMax = displayRowMax = Integer.parseInt((String) fc.getExternalContext().getInitParameter("displayRowMax"));
        //to najprawdopodobniej nie jest potrzebne - zamazuje zmienione dane z poprzedniej strony (Szymon)
        /*if (getIsModelValid()) 
        {
        fetchAndFilterConditions();
        fetchAndFilterSpecies();
        fetchAndFilterGenes();
        }*/
        return "";
    }

    /** Checkes if the modelID given in param is valid
     * jsf calls it with every request (because of "rendered" field)
     * check getModelID and deleteModel after changing
     * @return
     */
    public Boolean getIsModelValid() {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            Integer id = Integer.parseInt((String) fc.getExternalContext().
                    getRequestParameterMap().get("modelID"));
            return initData(id);
        } catch (Exception e) {
            /* there should be no ErrorBean.printStackTrace(e); here */
            return false;
        }
    }

    /** Get current model ID form the context (address or AJAX request)
     * see getIsModelValid and deleteModel after changing
     * @return
     */
    public int getModelID() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc.getExternalContext().getRequestParameterMap().containsKey("modelID")) {
            Integer id = Integer.parseInt((String) fc.getExternalContext().
                    getRequestParameterMap().get("modelID"));
            initData(id);
            return (int) id;
        } else { // in case of incorrect call
            return 0;
        }
    }

    /**
     * Get current model
     * @return current model
     * @author lukasz
     */
    private EModel getCurrentModel() {
        Integer id = getModelID();
        return data.get(id).getModel();
    }

    /**
     * Get current conditions
     * @return conditions binded to current model
     * @author lukasz
     */
    private List<Condition> getCurrentConditions() {
        Integer id = getModelID();
        return data.get(id).conditions;
    }

    /**
     * Is updating model allowed?
     * @return true or false
     * @author lukasz
     */
    public boolean getUpdateAllowed() {
        return !getCurrentModel().getReadOnly() || UserManager.getUserStatus().equals(EUser.statusAdmin);
    }

    /**
     * Is saving model allowed?
     * @return true or false
     * @author lukasz
     */
    public boolean getSaveAllowed() {
        return true;
    }

    /**
     * Creates collection of bounds for @model
     * @param model - model for which collections in prepared
     */
    private void prepareBoundsCollection(EModel model) {
        TaskBeanData d = data.get(getModelID());

        model.setEBoundsCollection(new LinkedList<EBounds>());

        Collections.sort(d.conditions, new ConditionReactionComparator());
        Collections.sort(d.originalConditions, new ConditionReactionComparator());

        ListIterator cit = d.conditions.listIterator();
        ListIterator ocit = d.originalConditions.listIterator();

        while (cit.hasNext() && ocit.hasNext()) {

            Condition c = (Condition) cit.next();
            Condition oc = (Condition) ocit.next();

            /* what if we got two diffrent reactions' bounds?
             * what shall we do? Even if it seems impossible,s
             * one more assertion does not cost that much ;-) */
            if (!c.getReaction().getId().equals(oc.getReaction().getId())) {
                ErrorBean.printStackTrace(new IllegalArgumentException("Got two diffrent reactions!"));
                return;
            }

            if (!c.equals(oc)) {
                EBounds b = new EBounds();

                b.setLowerBound(c.getBounds().getLowerBound());
                b.setUpperBound(c.getBounds().getUpperBound());
                b.setModel(model);
                b.setReaction(c.getReaction());

                model.getEBoundsCollection().add(b);
            }
        }

        if (cit.hasNext() != ocit.hasNext()) {
            ErrorBean.printStackTrace(new IllegalArgumentException("Got diffrent number of reactions!"));
            return;
        }
    }

    /**
     * Prepare new model from @mo.
     * @return new model
     * @author lukasz
     */
    private EModel prepareNewModel(EModel model) {
        TaskBeanData d = data.get(model.getId());

        EModel modelx = new EModel();

        modelx.setName(model.getName());
        modelx.setDate(new Date());
        modelx.setLastChange(new Date());
        if (model.getParent() == null) {
            modelx.setParent(model);
        } else {
            modelx.setParent(model.getParent());
        }
        modelx.setMetabolism(model.getMetabolism());
        modelx.setOwner(UserManager.getCurrentUser()); // We assume that someone is logged in
        modelx.setReadOnly(true);
        modelx.setShared(false);

        prepareBoundsCollection(modelx);

        return modelx;
    }

    /** 
     * Merge current model with database
     * @return navigation string
     * @author lukasz
     */
    public String updateModel() {
        EModel model = getCurrentModel();
        TaskBeanData d = data.get(model.getId());

        try {
            EModelController mo = new EModelController();

            mo.removeBounds(model);

            model.setEBoundsCollection(new LinkedList<EBounds>());
            for (Condition c : d.conditions) {
                EBounds b = new EBounds();

                b.setLowerBound(c.getBounds().getLowerBound());
                b.setUpperBound(c.getBounds().getUpperBound());
                b.setModel(model);
                b.setReaction(c.getReaction());

                model.getEBoundsCollection().add(b);

                // update LastChange date
                model.setLastChange(new Date());
            }

            EMetabolismController me = new EMetabolismController();
            me.mergeMetabolism(model.getMetabolism());

            mo.mergeModel(model);
        } catch (Exception e) {
            ErrorBean.printStackTrace(e);
            return "error";
        }

        d.originalConditions = copyConditionList(d.conditions);

        EVisualizationController vc = new EVisualizationController();
        vc.removeDetachedReactions(model.getId());
        return "";
    }

    /**
     * Creates and saves new model and redirects to its page.
     * @return navigation string
     * @author lukasz
     */
    public String saveModel() {
        try {
            EModel modelx = prepareNewModel(getCurrentModel());
            modelx.setReadOnly(false);

            EMetabolismController me = new EMetabolismController();
            me.mergeMetabolism(modelx.getMetabolism());

            EModelController mo = new EModelController();
            mo.addModel(modelx);

            modelx.setName(modelx.getName() + "_" + modelx.getId());
            mo.mergeModel(modelx);
            /* Redirect */
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.responseComplete();
            HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
            response.sendRedirect(response.encodeRedirectURL("modelDetails.jsf?modelID=" + modelx.getId().toString()));
        } catch (Exception e) {
            ErrorBean.printStackTrace(e);
            return "error";
        }

        return "";
    }

    /** Unwated changes done in the info section 
     * are to be discarded by this method
     * FIXME(Nie ruszajcie tego, sam to zrobie -- Darek): 
     * czy my chcemy tutaj usunac zmiany tylko z infopanel,
     * czy z calego modelu, usuniecie wpisu z mapy data to dobry sposob,
     * ale za duzo usuwa
     */
    public String discardChanges() {
        Integer id = getModelID();
        data.remove(id);
        initData(id);
        return "";
    }

    /**
     * Deletes current model.
     * @return navigation String
     * @author lukasz
     */
    public String deleteModel() {
        FacesContext fc = FacesContext.getCurrentInstance();

        if (fc.getExternalContext().getRequestParameterMap().containsKey("modelID")) {
            Integer modelId = Integer.parseInt((String) fc.getExternalContext().
                    getRequestParameterMap().
                    get("modelID"));

            try {
                EModelController mc = new EModelController();
                mc.removeModel(modelId);
                return "modelDeleted";
            } catch (Exception e) {
                ErrorBean.printStackTrace(e);
                return "error";
            }
        } else {
            return "error";
        }
    }

    public Boolean getEditable() {
        if (UserManager.getIsGuestS()) {
            return true;
        }
        EUser user = UserManager.getCurrentUser();
        //probably not needed
        if (user == null) {
            return false;
        }
        return true;
    }

    public Boolean getCanEditModels() {
        if (UserManager.getIsGuestS()) {
            return false;
        }
        EUser user = UserManager.getCurrentUser();
        //probably not needed
        if (user == null) {
            return false;
        }
        return true;
    }

    public Boolean getCanChangeGeneLink() {
        return UserManager.getUserStatus().equals(EUser.statusAdmin);
    }

    public Boolean getCanDelete() {
        EUser user = UserManager.getCurrentUser();
        if (user == null) {
            return false;
        }
        if (UserManager.getUserStatus().equals(UserManager.GUEST_STATUS)) {
            return false;
        }
        if (user.getStatus().equals(EUser.statusAdmin)) {
            return true;
        }
        
        EUser owner = data.get(getModelID()).getModel().getOwner();
        if (owner == null) {
            return false;
        } else {
            return owner.getId().equals(user.getId());
        }
    }

    /** 
     * Fetches EBounds and EReaction rows from database
     * @return
     */
    private List<Condition> justFetchConditions(EModel model) {
        try {
            EBoundsController bc = new EBoundsController();
            List<EBounds> boundsList = bc.getBounds(model);

            List<Condition> list = new LinkedList<Condition>();
            Map<Integer, EBounds> bounds_map = new HashMap<Integer, EBounds>();

            for (EBounds b : boundsList) {
                if (b.getReaction().getEReactantCollection().isEmpty()) {
                    /* Look for reactants */
                    EReactantController rc = new EReactantController();
                    b.getReaction().setEReactantCollection(rc.getReactants(b.getReaction()));
                }

                if (b.getReaction().getEProductCollection().isEmpty()) {
                    /* Look for products */
                    EProductController pc = new EProductController();
                    b.getReaction().setEProductCollection(pc.getProducts(b.getReaction()));
                }

                EReaction reaction = b.getReaction();
                String reactionFormula = reaction.getDivedReactionFormula();
                String undividedReactionFormula = reaction.getUndividedReactionFormula();
                String geneFormula = reaction.getGeneFormula();

                /*  because of structure of a database joints
                it is important (we believe it is :D) to leave
                each reaction entity with the ProductCollection
                and ReactantCollectoin set to null
                reaction.setEProductCollection(null);
                reaction.setEReactantCollection(null);
                 */

                list.add(new Condition(bounds_map, b.getId(), reaction, reaction.getId(), reactionFormula, undividedReactionFormula, geneFormula));
                bounds_map.put(b.getId(), b);
            }

            return list;
        } catch (Exception e) {
            ErrorBean.printStackTrace(e);
            return null;
        }
    }

    /** Fetches EBounds and EReaction rows from database
     * and assignes the variables
     * @return
     */
    private List<Condition> fetchConditions() {
        TaskBeanData d = data.get(getModelID());

        d.conditions = justFetchConditions(d.getModel());
        d.reactionsStart = 0;
        d.filteredConditions = new LinkedList<Condition>();

        for (Condition c : d.conditions) {
            d.filteredConditions.add(c);
        }
        return d.conditions;
    }

    /** 
     * Fetches ESpecies from database
     * @return
     */
    private List<Species> justFetchSpecies(EModel m) {
        try {
            ESpeciesController sc = new ESpeciesController();
            List<ESpecies> res = sc.getSpecies(m.getId());

            List<Species> list = new LinkedList<Species>();
            int ii = 0;
            for (ESpecies species : res) {
                list.add(new Species(species, ii));
                ii++;
            }
            return list;
        } catch (Exception e) {
            ErrorBean.printStackTrace(e);
            return null;
        }
    }

    private List<String> justFetchGenes(EModel m) {
        return m.getGenesList();
    }

    /** Fetches ESpecies from database and assignes the variables
     * @return
     */
    private List<Species> fetchSpecies() {
        Integer id = getModelID();

        data.get(id).species = justFetchSpecies(data.get(id).getModel());
        data.get(id).speciesStart = 0;
        data.get(id).filteredSpecies = data.get(id).species;
        return data.get(id).species;
    }

    private List<String> fetchGenes() {
        Integer id = getModelID();

        data.get(id).genes = justFetchGenes(data.get(id).getModel());
        data.get(id).genesStart = 0;
        data.get(id).filteredGenes = data.get(id).genes;
        return data.get(id).genes;
    }

    /** Filters already fetched Species list
     * @return
     */
    public String filterSpecies() {
        Integer id = getModelID();

        data.get(id).filteredSpecies = new LinkedList();
        for (Species row : data.get(id).species) {
            if (data.get(id).speciesNameFilter == null ||
                    row.getName().toLowerCase().contains(data.get(id).speciesNameFilter.toLowerCase())) {
                data.get(id).filteredSpecies.add(row);
            }
        }

        return "";
    }

    public List<Species> getSpecies() {
        Integer id = getModelID();

        Collections.sort(data.get(id).filteredSpecies, new SpeciesComparator());

        return data.get(id).filteredSpecies.subList(
                java.lang.Math.max(0, java.lang.Math.min(data.get(id).speciesStart, data.get(id).filteredSpecies.size())),
                java.lang.Math.max(0, java.lang.Math.min(data.get(id).speciesStart + displaySpeciesRowMax, data.get(id).filteredSpecies.size())));
    }

    public String fetchAndFilterSpecies() {
        fetchSpecies();
        return filterSpecies();
    }

    /** Filters already fetched Genes list
     * @return
     */
    public String filterGenes() {
        Integer id = getModelID();

        data.get(id).filteredGenes = new LinkedList();
        for (String row : data.get(id).genes) {
            if (data.get(id).genesNameFilter == null ||
                    row.toLowerCase().contains(data.get(id).genesNameFilter.toLowerCase())) {
                data.get(id).filteredGenes.add(row);
            }
        }

        return "";
    }

    public List<String> getGenes() {
        Integer id = getModelID();

        Collections.sort(data.get(id).filteredGenes);

        return data.get(id).filteredGenes.subList(
                java.lang.Math.max(0, java.lang.Math.min(data.get(id).genesStart, data.get(id).filteredGenes.size())),
                java.lang.Math.max(0, java.lang.Math.min(data.get(id).genesStart + displayGenesRowMax, data.get(id).filteredGenes.size())));
    }

    public String fetchAndFilterGenes() {
        fetchGenes();
        return filterGenes();
    }

    /** Filters already fetched Condition list
     * @return
     */
    public String filterConditions() {
        Integer id = getModelID();
        data.get(id).lock();

        data.get(id).filteredConditions = new LinkedList();
        for (Condition row : data.get(id).conditions) {
            if (data.get(id).reactionNameFilter == null ||
                    row.getReactionName().toLowerCase().contains(data.get(id).reactionNameFilter.toLowerCase())) {
                data.get(id).filteredConditions.add(row);
            }
        }
        data.get(id).unlock();
        return null;
    }

    public String fetchAndFilterConditions() {
        fetchConditions();
        return filterConditions();
    }

    /** Returns right page chunk of filtered condition list
     * @return
     */
    public List<Condition> getConditions() {
        Integer id = getModelID();
        List<Condition> ret;
        data.get(id).lock();
        //Collections.sort(data.get(id).filteredConditions, new ConditionReactionComparator());
        ret = data.get(id).filteredConditions.subList(
                java.lang.Math.max(0, java.lang.Math.min(data.get(id).reactionsStart, data.get(id).filteredConditions.size())),
                java.lang.Math.max(0, java.lang.Math.min(data.get(id).reactionsStart + displayRowMax, data.get(id).filteredConditions.size())));
        data.get(id).unlock();

        return ret;
    }

    /** Sets currentReactionPage to firstReactionPage
     * @return
     */
    public String reactionFirstPage() {
        Integer id = getModelID();

        data.get(id).reactionsStart = 0;
        return null;
    }

    public String reactionNextPage() {
        Integer id = getModelID();

        data.get(id).reactionsStart += displayRowMax;
        if (data.get(id).reactionsStart >= data.get(id).filteredConditions.size()) {
            data.get(id).reactionsStart -= displayRowMax;
        }
        return null;
    }

    public String reactionPrevPage() {
        Integer id = getModelID();

        data.get(id).reactionsStart -= displayRowMax;
        if (data.get(id).reactionsStart < 0) {
            data.get(id).reactionsStart = 0;
        }
        return null;
    }

    public String reactionLastPage() {
        Integer id = getModelID();

        int n = data.get(id).filteredConditions.size();
        if (n > 0 && n % displayRowMax == 0) {
            data.get(id).reactionsStart = (n / displayRowMax - 1) * displayRowMax;
        } else {
            data.get(id).reactionsStart = (n / displayRowMax) * displayRowMax;
        }

        return null;
    }

    /** Sets currentSpeciesPage to firstSpeciesPage
     * @return
     */
    public String speciesFirstPage() {
        Integer id = getModelID();

        data.get(id).speciesStart = 0;
        return null;
    }

    public String speciesNextPage() {
        Integer id = getModelID();

        data.get(id).speciesStart += displaySpeciesRowMax;
        if (data.get(id).speciesStart >= data.get(id).filteredSpecies.size()) {
            data.get(id).speciesStart -= displaySpeciesRowMax;
        }
        return null;
    }

    public String speciesPrevPage() {
        Integer id = getModelID();

        data.get(id).speciesStart -= displaySpeciesRowMax;
        if (data.get(id).speciesStart < 0) {
            data.get(id).speciesStart = 0;
        }
        return null;
    }

    public String speciesLastPage() {
        Integer id = getModelID();

        int n = data.get(id).filteredSpecies.size();
        if (n > 0 && n % displaySpeciesRowMax == 0) {
            data.get(id).speciesStart = (n / displaySpeciesRowMax - 1) * displaySpeciesRowMax;
        } else {
            data.get(id).speciesStart = (n / displaySpeciesRowMax) * displaySpeciesRowMax;
        }

        return null;
    }

    /** Sets currentGenesPage to firstGenesPage
     * @return
     */
    public String genesFirstPage() {
        Integer id = getModelID();

        data.get(id).genesStart = 0;
        return null;
    }

    public String genesNextPage() {
        Integer id = getModelID();

        data.get(id).genesStart += displayGenesRowMax;
        if (data.get(id).genesStart >= data.get(id).filteredGenes.size()) {
            data.get(id).genesStart -= displayGenesRowMax;
        }
        return null;
    }

    public String genesPrevPage() {
        Integer id = getModelID();

        data.get(id).genesStart -= displayGenesRowMax;
        if (data.get(id).genesStart < 0) {
            data.get(id).genesStart = 0;
        }
        return null;
    }

    public String genesLastPage() {
        Integer id = getModelID();

        int n = data.get(id).filteredGenes.size();
        if (n > 0 && n % displayGenesRowMax == 0) {
            data.get(id).genesStart = (n / displayGenesRowMax - 1) * displayGenesRowMax;
        } else {
            data.get(id).genesStart = (n / displayGenesRowMax) * displayGenesRowMax;
        }

        return null;
    }

    /** Sets given number to displaySpeciesRowMax field
     * @return
     */
    public String Rows() {
        FacesContext fc = FacesContext.getCurrentInstance();
        displayRowMax = Integer.parseInt((String) fc.getExternalContext().getInitParameter("displayRowMax"));
        if (fc.getExternalContext().getRequestParameterMap().containsKey("rows")) {
            displayRowMax = (int) Integer.parseInt((String) fc.getExternalContext().
                    getRequestParameterMap().get("rows"));
        }
        return null;
    }

    public String speciesRows() {
        FacesContext fc = FacesContext.getCurrentInstance();
        displaySpeciesRowMax = Integer.parseInt((String) fc.getExternalContext().getInitParameter("displayRowMax"));
        if (fc.getExternalContext().getRequestParameterMap().containsKey("rows")) {
            displaySpeciesRowMax = (int) Integer.parseInt((String) fc.getExternalContext().
                    getRequestParameterMap().get("rows"));
        }
        return null;
    }

    public String genesRows() {
        FacesContext fc = FacesContext.getCurrentInstance();
        displayGenesRowMax = Integer.parseInt((String) fc.getExternalContext().getInitParameter("displayRowMax"));
        if (fc.getExternalContext().getRequestParameterMap().containsKey("rows")) {
            displayGenesRowMax = (int) Integer.parseInt((String) fc.getExternalContext().
                    getRequestParameterMap().get("rows"));
        }
        return null;
    }

    /** Returns string with number of total fetched results and
     * some pagination data
     * @return
     */
    public String getReactionResultsString() {
        Integer id = getModelID();

        Integer from = new Integer(java.lang.Math.max(0, java.lang.Math.min(
                data.get(id).reactionsStart,
                data.get(id).filteredConditions.size())) + 1);
        Integer to = new Integer(java.lang.Math.max(0, java.lang.Math.min(
                data.get(id).reactionsStart + displayRowMax,
                data.get(id).filteredConditions.size())));
        Integer total = new Integer(data.get(id).filteredConditions.size());

        if (total > 0) {
            return from.toString() +
                    " .. " +
                    to.toString() +
                    " of " + total.toString();
        } else {
            return "No results found.";
        }
    }

    /** Returns string with number of total fetched results and
     * some pagination data
     * @return
     */
    public String getSpeciesResultsString() {
        Integer id = getModelID();

        Integer from = new Integer(java.lang.Math.max(0, java.lang.Math.min(
                data.get(id).speciesStart,
                data.get(id).filteredSpecies.size())) + 1);
        Integer to = new Integer(java.lang.Math.max(0, java.lang.Math.min(
                data.get(id).speciesStart + displaySpeciesRowMax,
                data.get(id).filteredSpecies.size())));
        Integer total = new Integer(data.get(id).filteredSpecies.size());
        if (total > 0) {
            return from.toString() +
                    " .. " + to.toString() +
                    " of " + total.toString();
        } else {
            return "No results found.";
        }
    }

    public String getGenesResultsString() {
        Integer id = getModelID();

        Integer from = new Integer(java.lang.Math.max(0, java.lang.Math.min(
                data.get(id).genesStart,
                data.get(id).filteredGenes.size())) + 1);
        Integer to = new Integer(java.lang.Math.max(0, java.lang.Math.min(
                data.get(id).genesStart + displayGenesRowMax,
                data.get(id).filteredGenes.size())));
        Integer total = new Integer(data.get(id).filteredGenes.size());
        if (total > 0) {
            return from.toString() +
                    " .. " + to.toString() +
                    " of " + total.toString();
        } else {
            return "No results found.";
        }
    }

    public String getReactionNameFilter() {
        return data.get(getModelID()).reactionNameFilter;
    }

    public void setReactionNameFilter(String in) {
        Integer id = getModelID();

        if (!in.equals(data.get(id).reactionNameFilter)) {
            data.get(id).reactionsStart = 0;
        }
        data.get(id).reactionNameFilter = in;
    }

    public String getSpeciesNameFilter() {
        Integer id = getModelID();
        if (!data.containsKey(id)) {
            initData(id);
        }
        return data.get(id).speciesNameFilter;
    }

    public void setSpeciesNameFilter(String in) {
        Integer id = getModelID();

        if (!in.equals(data.get(id).speciesNameFilter)) {
            data.get(id).speciesStart = 0;
        }
        data.get(id).speciesNameFilter = in;
    }

    public String getGenesNameFilter() {
        Integer id = getModelID();
        if (!data.containsKey(id)) {
            initData(id);
        }
        return data.get(id).genesNameFilter;
    }

    public void setGenesNameFilter(String in) {
        Integer id = getModelID();

        if (!in.equals(data.get(id).genesNameFilter)) {
            data.get(id).genesStart = 0;
        }
        data.get(id).genesNameFilter = in;
    }

    public String getSelectedReaction() {

        return data.get(getModelID()).getSelectedReaction();
    }

    public void setSelectedReaction(String in) {
        Integer id = getModelID();

        try {
            if (Integer.decode(in) > 0) {
                data.get(id).setSelectedReaction(in);
            }
        } catch (Exception e) {
            ErrorBean.printStackTrace(e);
            return;
        }
    }

    public String getSelectedSpecies() {
        Integer id = getModelID();

        return data.get(id).getSelectedSpecies();
    }

    public void setSelectedSpecies(String in) {
        Integer id = getModelID();

        try {
            if (Integer.decode(in) < 0) {
                data.get(id).setSelectedSpecies(in);
            }
        } catch (Exception e) {
            ErrorBean.printStackTrace(e);
            return;
        }

    }

    public String getSelectedGene() {
        Integer id = getModelID();

        return data.get(id).getSelectedGene();
    }

    public void setSelectedGene(String in) {
        Integer id = getModelID();

        data.get(id).setSelectedGene(in);
    }

    public void setModel(EModel model) {
        Integer id = getModelID();

        data.get(id).model = model;
    }

    public EModel getModel() {
        Integer id = getModelID();

        return data.get(id).model;
    }

    public String taskFBA() {
//        if (UserManager.getIsGuest()) {
//            return "login";
//        }
        Integer id = getModelID();

        if (data.get(id).parameters == null) {
            data.get(id).parameters = new FBAParams();
        } else if (!(data.get(id).parameters instanceof FBAParams)) {
            data.get(id).parameters = new FBAParams();
        }
        data.get(id).errorMessage = "";
        return "FBA";
    }

    public String taskFVA() {
//        if (UserManager.getIsGuest()) {
//            return "login";
//        }
        Integer id = getModelID();

        if (data.get(id).parameters == null) {
            data.get(id).parameters = new FVAParams();
        } else if (!(data.get(id).parameters instanceof FVAParams)) {
            data.get(id).parameters = new FVAParams();
        }
        data.get(id).errorMessage = "";
        if (data.get(id).getPerformLocally()) {
          return this.prepareParameters();
        } else {
          return this.calcTask();
        }
    }

    public String taskRSCAN() {
//        if (UserManager.getIsGuest()) {
//            return "login";
//        }
        Integer id = getModelID();

        if (data.get(id).parameters == null) {
            data.get(id).parameters = new RSCANParams();
        } else if (!(data.get(id).parameters instanceof RSCANParams)) {
            data.get(id).parameters = new RSCANParams();
        }
        data.get(id).errorMessage = "";
        return "RSCAN";
    }

    public String taskKGENE() {
//        if (UserManager.getIsGuest()) {
//            return "login";
//        }
        Integer id = getModelID();

        if (data.get(id).parameters == null) {
            data.get(id).parameters = new KGENEParams();
        } else if (!(data.get(id).parameters instanceof KGENEParams)) {
            data.get(id).parameters = new KGENEParams();
        }
        data.get(id).errorMessage = "";
        return "KGENE";
    }

    /* IF YOU WANT TO ADD NEW METHOD -> put here function similar to the above one */
    public Params getParameters() {
        Integer id = getModelID();

        return data.get(id).parameters;
    }

    public void setParameters(Params parameters) {
        Integer id = getModelID();

        data.get(id).parameters = parameters;
    }

    public String getTaskName() {
        Integer id = getModelID();

        return data.get(id).taskName;
    }

    public boolean getPerformLocally() {
      Integer id = getModelID();
      return data.get(id).getPerformLocally();
    }

    public void setPerformLocally(boolean value) {
      Integer id = getModelID();
      data.get(id).setPerformLocally(value);
    }

    public void setTaskName(String name) {
        Integer id = getModelID();

        data.get(id).taskName = name;
    }

    public EMethod getFBAMethod() {

        List<EMethod> resultMethod;
        EMethod method = new EMethod();

        EMethodController mc = new EMethodController();
        resultMethod = mc.findByIdent(EMethod.fba);

        if (resultMethod.size() == 0) {
            method.setName("Single Flux Balance Analisys");
            method.setIdent(EMethod.fba);
            method.setDescr("Single Flux Balance Analisys");

            /* Update database */
            try {
                mc.addMethod(method);
            } catch (Exception e) {
                ErrorBean.printStackTrace(e);
                return null;
            }
        } else {
            method = resultMethod.get(0);
        }

        return method;
    }

    public EMethod getRSCANMethod() {

        List<EMethod> resultMethod;
        EMethod method = new EMethod();

        EMethodController mc = new EMethodController();
        resultMethod = mc.findByIdent(EMethod.rscan);

        if (resultMethod.size() == 0) {
            method.setName("Reaction Essentiality Scan");
            method.setIdent(EMethod.rscan);
            method.setDescr("Reaction Essentiality Scan");

            /* Update database */
            try {
                mc.addMethod(method);
            } catch (Exception e) {
                ErrorBean.printStackTrace(e);
                return null;
            }
        } else {
            method = resultMethod.get(0);
        }

        return method;
    }

    public EMethod getKGENEMethod() {

        List<EMethod> resultMethod;
        EMethod method = new EMethod();

        EMethodController mc = new EMethodController();
        resultMethod = mc.findByIdent(EMethod.kgene);

        if (resultMethod.size() == 0) {
            method.setName("Single Gene Knockout");
            method.setIdent(EMethod.kgene);
            method.setDescr("Single Gene Knockout");

            /* Update database */
            try {
                mc.addMethod(method);
            } catch (Exception e) {
                ErrorBean.printStackTrace(e);
                return null;
            }
        } else {
            method = resultMethod.get(0);
        }

        return method;
    }

    public EMethod getFVAMethod() {

        List<EMethod> resultMethod;
        EMethod method = new EMethod();

        EMethodController mc = new EMethodController();
        resultMethod = mc.findByIdent(EMethod.fva);

        if (resultMethod.size() == 0) {
            method.setName("Flux Variability Analysis");
            method.setIdent(EMethod.fva);
            method.setDescr("Flux Variability Analysis");

            /* Update database */
            try {
                mc.addMethod(method);
            } catch (Exception e) {
                ErrorBean.printStackTrace(e);
                return null;
            }
        } else {
            method = resultMethod.get(0);
        }

        return method;
    }

    /* IF YOU WANT TO ADD NEW METHOD -> put here function similar to the above one */
    public EFbaData getFBAMethodData(Integer id) {
        EFbaData methodData = new EFbaData();
        if (((FBAParams) (data.get(id).parameters)).isReactionTarget()) {
            EReaction targetReaction = null;
            String target = ((FBAParams) (data.get(id).parameters)).getTarget();
            for (Condition c : data.get(id).conditions) {
                if (c.getIndex().equals(target)) {
                    targetReaction = c.getReaction();
                    break;
                }
            }
            if (targetReaction == null) {
                ErrorBean.printStackTrace(new java.lang.IllegalArgumentException("targetReaction is null!"));
                return null;
            } else {
                methodData.setReaction(targetReaction);
            }
        } else {
            ESpecies targetSpecies = null;
            String target = ((FBAParams) (data.get(id).parameters)).getTarget();
            for (Species s : data.get(id).species) {
                if (s.getIndex().equals(target)) {
                    targetSpecies = s.getSpecies();
                    break;
                }
            }
            if (targetSpecies == null) {
                ErrorBean.printStackTrace(new java.lang.IllegalArgumentException("targetReaction is null!"));
                return null;
            } else {
                methodData.setSpecies(targetSpecies);
            }
        }
        return methodData;
    }

    public ERscanData getRSCANMethodData(Integer id) {
        ERscanData methodData = new ERscanData();
        if (((RSCANParams) (data.get(id).parameters)).isReactionTarget()) {
            EReaction targetReaction = null;
            String target = ((RSCANParams) (data.get(id).parameters)).getTarget();
            for (Condition c : data.get(id).conditions) {
                if (c.getIndex().equals(target)) {
                    targetReaction = c.getReaction();
                    break;
                }
            }
            if (targetReaction == null) {
                ErrorBean.printStackTrace(new java.lang.IllegalArgumentException("targetReaction is null!"));
                return null;
            } else {
                methodData.setReaction(targetReaction);
            }
        } else {
            ESpecies targetSpecies = null;
            String target = ((RSCANParams) (data.get(id).parameters)).getTarget();
            for (Species s : data.get(id).species) {
                if (s.getIndex().equals(target)) {
                    targetSpecies = s.getSpecies();
                    break;
                }
            }
            if (targetSpecies == null) {
                ErrorBean.printStackTrace(new java.lang.IllegalArgumentException("targetReaction is null!"));
                return null;
            } else {
                methodData.setSpecies(targetSpecies);
            }
        }
        return methodData;
    }

    public EKgeneData getKGENEMethodData(Integer id) {
        EKgeneData methodData = new EKgeneData();
        if (((KGENEParams) (data.get(id).parameters)).isReactionTarget()) {
            EReaction targetReaction = null;
            String target = ((KGENEParams) (data.get(id).parameters)).getTarget();
            for (Condition c : data.get(id).conditions) {
                if (c.getIndex().equals(target)) {
                    targetReaction = c.getReaction();
                    break;
                }
            }
            if (targetReaction == null) {
                ErrorBean.printStackTrace(new java.lang.IllegalArgumentException("targetReaction is null!"));
                return null;
            } else {
                methodData.setReaction(targetReaction);
            }
        } else {
            ESpecies targetSpecies = null;
            String target = ((KGENEParams) (data.get(id).parameters)).getTarget();
            for (Species s : data.get(id).species) {
                if (s.getIndex().equals(target)) {
                    targetSpecies = s.getSpecies();
                    break;
                }
            }
            if (targetSpecies == null) {
                ErrorBean.printStackTrace(new java.lang.IllegalArgumentException("targetReaction is null!"));
                return null;
            } else {
                methodData.setSpecies(targetSpecies);
            }
        }
        methodData.setGene(((KGENEParams) (data.get(id).parameters)).gene);
        return methodData;
    }

    /* IF YOU WANT TO ADD NEW METHOD (with parameters) -> put here function similar to the above one */
    public boolean isTaskReady() {
        Integer id = getModelID();

        float lower, upper;

        //funkcja sprawdzajaca czy zadanie jest juz gotowe, tzn czy mozemy je zlecic
        //do wykonania. (nie mozemy jesli nie wszystkie parametry zostaly ustalone poprawnie)
        if (data.get(id).parameters == null) {
            data.get(id).errorMessage = "None of the parameters has been set";
            return false;
        } else if (data.get(id).parameters.isEmpty()) {
            data.get(id).errorMessage = "Some of the parameters have not been set";
            return false;
        }
        if (data.get(id).conditions == null) {
            data.get(id).errorMessage = "Conditions are null. Try again to make new task";
            return false;
        }
        for (int i = 0; i < data.get(id).conditions.size(); i++) {
            lower = data.get(id).conditions.get(id).getBounds().getLowerBound();
            upper = data.get(id).conditions.get(id).getBounds().getUpperBound();
            if ((lower < -999999) || (upper > 999999) || (lower > upper)) {
                data.get(id).errorMessage = "Conditions aren't correct";
                return false;
            }
        }

        data.get(id).errorMessage = "";
        return true;
    }

    public boolean isError() {
        Integer id = getModelID();

        if (data.get(id).errorMessage.length() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public String getErrorMessage() {
        Integer id = getModelID();

        return data.get(id).errorMessage;
    }

    private void createTaskAndModel(boolean enqueueTask) throws JMSException, Exception {
      Integer id = getModelID();
      
      /* Set task name */
      if (data.get(id).taskName.contentEquals("")
              || data.get(id).taskName.contentEquals("Enter new task name")) {
        data.get(id).taskName = "[" + data.get(id).model.getName() + " - TASK]";
      }

      /* Create new model */
      EModel model = prepareNewModel(getCurrentModel());
      model.setName(data.get(id).taskName + " task's model");

      /* Create new task */
      ETask task = new ETask();

      task.setName(data.get(id).taskName);
      task.setDate(new Date());
      task.setLastChange(new Date());
      task.setStatus(ETask.statusQueued);
      if (UserManager.getIsGuestS()) {
        task.setShared(true);
        model.setShared(true);
      } else {
        task.setShared(false);
        model.setShared(false);
      }
      task.setModel(model);
      model.setTask(task);

      EMethod method = null;

      EMethodData methodData = new EMethodData();

      if (data.get(id).parameters instanceof FBAParams) {
        method = getFBAMethod();
        methodData = getFBAMethodData(id);
      } else if (data.get(id).parameters instanceof RSCANParams) {
        method = getRSCANMethod();
        methodData = getRSCANMethodData(id);
      } else if (data.get(id).parameters instanceof KGENEParams) {
        method = getKGENEMethod();
        methodData = getKGENEMethodData(id);
      } else if (data.get(id).parameters instanceof FVAParams) {
        method = getFVAMethod();
      }

      /* IF YOU WANT TO ADD NEW METHOD -> put here another "else if" condition and piece of code similar to the above one */

      task.setMethod(method);

      if (!(data.get(id).parameters instanceof FVAParams)) {
        /* IF YOU WANT TO ADD NEW METHOD (without parameters) -> put "& !(data.get(id).parameters instanceof MYMETHODParams) into conditon (where MYMETHOD is a name of method) */
        task.setMethodData(methodData);
        methodData.setTask(task);
      }

      /* Update database */

      EModelController mc = new EModelController();

      /* Extremly required! */
      data.get(id).model = mc.getModel(id);

      mc.addModel(model);

      if (enqueueTask) {
        TaskQueue.getInstance().enqueueTask(task);
      }
      
      //discardChanges is necessary!!! - next time this model will be choosen original data appear in the table
      discardChanges();
    }

    /* Function is called when user presses "RUN" button */
    public String calcTask() {
        if (this.isTaskReady()) {
            try {
              createTaskAndModel(true);
            } catch (Exception e) {
                ErrorBean.printStackTrace(e);
                return null;
            }
            return "TaskOK";
        } else {
            return "TaskNotOK";
        }
    }

    /* Function is called when user presses "DOWNLOAD DATA" button. */
    public String prepareParameters() {
      if (isTaskReady()) {
        try {
          createTaskAndModel(false);
        } catch (Exception e) {
          ErrorBean.printStackTrace(e);
          return null;
        }
        return "ParamsOK";
      } else {
        return "ParamsNotOK";
      }
    }
}
