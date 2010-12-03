package acorn.model;

import acorn.db.ECommonResults;
import acorn.db.EMethod;
import acorn.db.EReaction;
import acorn.db.ETask;
import acorn.db.ETaskController;
import acorn.db.EfbaResultElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author lukasz
 */
public class ResultParser extends DefaultHandler {

  Integer taskId;

  private class Bound {

    public String reactionId;
    public Float lower;
    public Float upper;

    public Bound(String reactionId, Float lower, Float upper) {
      this.reactionId = reactionId;
      this.lower = lower;
      this.upper = upper;
    }
  }

  private class Simulation {

    public SimulationParameters params;
    public SimulationResults results;

    Simulation(SimulationParameters params, SimulationResults results) {
      this.params = params;
      this.results = results;
    }
  }

  private class SimulationParameters {

    public String disableReaction = null;
    public String disableGene = null;
    public String objective = null;
    public Boolean minimize = Boolean.FALSE;
    public Boolean printFlux = Boolean.FALSE;

    SimulationParameters() {
    }
  }

  private class SimulationResults {

    public String status;
    public Float objectiveFunctionValue;
    public List<Flux> listOfFluxValues;

    SimulationResults(String status, Float value) {
      this.status = status;
      this.objectiveFunctionValue = value;
      listOfFluxValues = new ArrayList<Flux>();
    }
  }

  private class Flux {

    public String reaction;
    public Float fluxValue;

    Flux(String reaction, Float fluxValue) {
      this.reaction = reaction;
      this.fluxValue = fluxValue;
    }
  }
  private String method;
  private List<Bound> listOfBounds;
  private List<Simulation> listOfSimulations;
  private SimulationParameters params = null;
  private SimulationResults results = null;
  private String line = null;

  /**
   * Creates a new instance of a ResultParser.
   */
  public ResultParser(Integer taskId) {
    super();
    this.taskId = taskId;
  }

  /**
   * Begin parsing
   */
  @Override
  public void startDocument() {
  }

  private void endDocumentDebug() {
    System.err.println("METHOD: " + method);
    /*
    System.err.println("BOUNDS:");
    for (Bound b : listOfBounds) {
      System.err.println(b.reactionId + " " + b.lower + "  " + b.upper);
    }
     */
    for (Simulation s : listOfSimulations) {
      System.err.println("SIMULATION:");
      System.err.println("PARAMETERS:");
      System.err.println("disableReaction: " + debug(s.params.disableReaction));
      System.err.println("disableGene: " + debug(s.params.disableGene));
      System.err.println("objective: " + debug(s.params.objective));
      System.err.println("minimize: " + s.params.minimize);
      System.err.println("printFlux: " + s.params.printFlux);
      System.err.println("RESULTS:");
      System.err.println("status: " + debug(s.results.status));
      System.err.println("objectiveFunctionValue: " + s.results.objectiveFunctionValue);
      for (Flux f : s.results.listOfFluxValues) {
        System.err.println("flux:");
        System.err.println("  reactionId: " + f.reaction);
        System.err.println("  value: " + f.fluxValue);
      }
    }
  }

  /**
   * End parsing.
   */
  @Override
  public void endDocument() {
    ETaskController tc = new ETaskController();
    ETask task = tc.getTask(taskId);

    Collection<EReaction> reactions;
    Hashtable<String, EReaction> reactionsMap = new Hashtable<String, EReaction>();
    reactions = task.getModel().getMetabolism().getEReactionCollection();
    for (EReaction er : reactions) {
      reactionsMap.put(er.getSid(), er);
    }

    if (method.equals(EMethod.fba)) {
      /*
       * Poniższy kod niby, działa. Wykonuje się. Chyba coś miesza w bazie danych.
       * Ale niestety jeszcze nie tak jakbyśmy chcieli. Po doddaniu za jego pomocą
       * wyników symulacji strona danego taska przestaje się ładować. Wyświetla się
       * jedynie niewiele mówiący komunikat, żeby skontaktować się z administratorem.
       * Ale ważne jest, że nie jest to jakiś niezłapany wyjątek, ale diagnostyczna
       * strona Acorna, napisana przez nas.
       * Wątpliwości co do tego kodu:
       * 1) dobrze reprezentujemy wyniki FBA?
       * 2) Wystarczy wykonać merge na tasku? Mi się wydaje, że cały graf nowych obiektów
       * podczepionych pod taska powinien się zapisać w bazie danych...
       */
      SimulationResults res = listOfSimulations.get(0).results;

      ECommonResults commonResults = new ECommonResults();
      commonResults.setTask(task);
      task.setCommonResults(commonResults);
      commonResults.setStatus(res.status);
      commonResults.setGrowthRate(res.objectiveFunctionValue);

      for (Flux f : res.listOfFluxValues) {
        EfbaResultElement fbaResultElem = new EfbaResultElement();
        task.getEfbaResultElementCollection().add(fbaResultElem);
        fbaResultElem.setTask(task);
        fbaResultElem.setReaction(reactionsMap.get(f.reaction));
        fbaResultElem.setFlux(f.fluxValue);
      }
    } else if (method.equals(EMethod.fva)) {
    } else if (method.equals(EMethod.rscan)) {
    } else if (method.equals(EMethod.kgene)) {
    }

    endDocumentDebug();

    tc.mergeTask(task);
  }

  private String debug(String s) {
    return (s == null) ? "" : s;
  }

  @Override
  public void startElement(String uri, String name, String qName, Attributes atts) {
    if (name.equals("fbaTask")) {
      fbaTask(atts);
    } else if (name.equals("listOfBounds")) {
      listOfBounds();
    } else if (name.equals("bound")) {
      bound(atts);
    } else if (name.equals("listOfSimulations")) {
      listOfSimulations();
    } else if (name.equals("simulationParameters")) {
      startSimulationParameters();
    } else if (name.equals("disableReaction") || name.equals("disableGene") || name.equals("objective") || name.equals("minimize") || name.equals("printFlux")) {
      startParam();
    } else if (name.equals("simulationResults")) {
      startSimulationResults(atts);
    } else if (name.equals("flux")) {
      flux(atts);
    }
  }

  private void fbaTask(Attributes atts) {
    method = atts.getValue("method");
  }

  private void listOfBounds() {
    listOfBounds = new ArrayList<Bound>();
  }

  private void bound(Attributes atts) {
    String reactionId = atts.getValue("reactionId");
    Float lower = Float.valueOf(atts.getValue("lowerBound"));
    Float upper = Float.valueOf(atts.getValue("upperBound"));
    listOfBounds.add(new Bound(reactionId, lower, upper));
  }

  private void listOfSimulations() {
    listOfSimulations = new ArrayList<Simulation>();
  }

  private void startSimulationParameters() {
    params = new SimulationParameters();
  }

  private void startParam() {
    line = new String();
  }

  private void startSimulationResults(Attributes atts) {
    String status = atts.getValue("status");
    Float value = Float.valueOf(atts.getValue("objectiveFunctionValue"));
    results = new SimulationResults(status, value);
  }

  private void flux(Attributes atts) {
    String reactionId = atts.getValue("reaction");
    Float fluxValue = Float.valueOf(atts.getValue("value"));
    results.listOfFluxValues.add(new Flux(reactionId, fluxValue));
  }

  @Override
  public void endElement(String uri, String name, String qName) {
    if (name.equals("disableReaction") || name.equals("disableGene") || name.equals("objective") || name.equals("minimize") || name.equals("printFlux")) {
      endParam(name);
    } else if (name.equals("simulation")) {
      endSimulation();
    }
  }

  private void endParam(String name) {
    line = line.replace("\n", "");
    if (name.equals("disableReaction")) {
      params.disableReaction = line;
    } else if (name.equals("disableGene")) {
      params.disableGene = line;
    } else if (name.equals("objective")) {
      params.objective = line;
    } else if (name.equals("minimize")) {
      params.minimize = Boolean.valueOf(line);
    } else if (name.equals("printFlux")) {
      params.printFlux = Boolean.valueOf(line);
    }
    line = null;
  }

  private void endSimulation() {
    listOfSimulations.add(new Simulation(params, results));
    params = null;
    results = null;
  }

  @Override
  public void characters(char ch[], int start, int length) {
    if (line != null) {
      line = line + (new String(ch, start, length));
    }
  }

  boolean isError() {
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  public String getMessage() {
    throw new UnsupportedOperationException("Not yet implemented.");
  }
}
