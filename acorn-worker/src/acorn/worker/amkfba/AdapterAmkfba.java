/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.worker.amkfba;

/**
 *
 * @author kuba
 */
import acorn.worker.method.FbaOutput;
import acorn.db.*;
import acorn.db.EBounds;
import acorn.db.EModel;
import acorn.db.EProduct;
import acorn.db.EReactant;
import acorn.db.EReaction;
import acorn.db.ESpecies;
import acorn.worker.main.AcornLogger;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.Collection;

/**
 *
 * @author lukasz
 */
public class AdapterAmkfba {

    private static String amkfbaPath = null;
    private static final AdapterAmkfba singleton = new AdapterAmkfba();

    public static AdapterAmkfba getInstance() {
        return singleton;
    }

    private String getReactionDescription(EReaction reaction, EBounds bounds, String genes) {
        String line;
        Boolean first;
        Collection<EReactant> reactants;
        Collection<EProduct> products;
        ESpecies species;
        line = reaction.getSid() + "\t";

        reactants = reaction.getEReactantCollection();
        products = reaction.getEProductCollection();
        first = true;
        for (EReactant reactant : reactants) {
            if (!first) {
                line += " + ";
            }
            species = reactant.getSpecies();
            line += reactant.getStoichiometry() + " " + species.getSid();
            if (species.getBoundaryCondition()) {
                line += "_xt";
            }

            first = false;
        }
        line += " = ";
        first = true;
        for (EProduct product : products) {
            if (!first) {
                line += " + ";
            }
            species = product.getSpecies();
            line += product.getStoichiometry() + " " + species.getSid();
            if (species.getBoundaryCondition()) {
                line += "_xt";
            }
            first = false;
        }

        line += "\t|\t#\t" + bounds.getLowerBound() + "\t" + bounds.getUpperBound() + "\t";
        line += "<GENES>  " + genes + " </GENES>\tcomment";
        line += "\r\n";

        return line;
    }

    public void writeModel(BufferedWriter out, EModel model, Collection<EBounds> bounds, Boolean isRmodel) throws IOException {
        String react;

        int bytesWritten = 0;
        for (EBounds bound : bounds) {
            EReaction reaction = bound.getReaction();
            if (isRmodel) {
                react = getReactionDescription(reaction, bound, reaction.getSid());
            } else {
                if (reaction.getGenes().equals("nogene")) {
                    react = getReactionDescription(reaction, bound, "");
                } else {
                    react = getReactionDescription(reaction, bound, reaction.getGenes());
                }
            }
            out.write(react);
            bytesWritten += react.length();

            /*
             * For some reason, BufferedWriter used to write to StringWriter discards data
             * after reading 72kB. The instruction below flushes each 4kB of data
             * (assuming each line is not longer than 500)
             */
            if(bytesWritten > 4*1024 - 500) {
                out.flush();
                bytesWritten = 0;
            }
        }
    }

    private String getFbaOptimizationStatus(String line) throws AmkfbaException {
        if (line == null) {
            throw new AmkfbaException("getFbaOptimizationStatus: unexpected end of output");
        }
        if (line.length() <= 8 || !line.substring(0, 8).equals("Status:\t")) {
            throw new AmkfbaException("getFbaOptimizationStatus: 'Status: ' expected");
        }
        String status;
        status = getDBStatus(line.substring(8));
        if (status == null) {
            throw new AmkfbaException("getFbaOptimizationStatus: got status =" + line.substring(8));
        }
        return status;
    }

    private Float getFbaGrowthRate(String line) throws AmkfbaException {
        if (line == null) {
            throw new AmkfbaException("getFbaGrowthRate: unexpected end of output");
        }
        if (line.length() <= 26) {
            throw new AmkfbaException("getFbaGrowthRate: Cannot read objective function value");
        }
        try {
            return Float.valueOf(line.substring(26));
        } catch (NumberFormatException e) {
            throw new AmkfbaException("getFbaGrowthRate: NumberFormatException");
        }
    }

    private String getDBStatus(String amkfbaStatus) {
        if (amkfbaStatus.equals("OPTIMAL")) {
            return ECommonResults.statusOptimal;
        } else if (amkfbaStatus.equals("UNDEFINED")) {
            return ECommonResults.statusUndefined;
        } else if (amkfbaStatus.equals("FEASIBLE")) {
            return ECommonResults.statusFeasible;
        } else if (amkfbaStatus.equals("INFEASIBLE")) {
            return ECommonResults.statusInfeasible;
        } else if (amkfbaStatus.equals("NON-FEASIBLE")) {
            return ECommonResults.statusNonFeasible;
        } else if (amkfbaStatus.equals("UNBOUNDED")) {
            return ECommonResults.statusUnbounded;
        } else if (amkfbaStatus.equals("FAILED")) {
            // Dodane przez Andrzeja Kierzka. Tutaj najlepiej byloby dodac do kodu
            // obsluge statusu FAILED, ktory wprowadzilem, zeby oznaczyc blad
            // funkcji simplex w GLPK
            return ECommonResults.statusUndefined;
        } else {
            return null;
        }

    }

    private String getStatus(String line) throws AmkfbaException {
        String[] out = line.split("\t");
        if (out.length < 2) {
            throw new AmkfbaException("getStatus: out.length < 2, amkfba returned '" + line + "'");
        }
        String status = getDBStatus(out[1]);
        if (status == null) {
            throw new AmkfbaException("getStatus: got status: " + out[1]);
        }
        return status;
    }

    private Float getGrowthRate(String line) throws AmkfbaException {
        try {
            return Float.valueOf(line.split("\t")[0]);
        } catch (NumberFormatException e) {
            throw new AmkfbaException("getGrowthRate: NumberFormatException");
        }
    }

    Process execAmkfba(String arguments) throws AmkfbaException {
        Process p;
        try {
            System.out.print("Running amkfba... " + arguments + " ");
            p = Runtime.getRuntime().exec(amkfbaPath + " -i - " + arguments, null);
        } catch (IOException e) {
            throw new AmkfbaException("execAmkfba: IOException");
        }
        return p;
    }

    private void returnAmkfbaStderr(Process amkfbaProcess) throws AmkfbaException {
        String errorMsg = new String();
        String out;
        BufferedReader input = new BufferedReader(new InputStreamReader(amkfbaProcess.getErrorStream()));
        try {
            while (true) {
                out = input.readLine();
                if (out == null) {
                    break;
                }
                errorMsg += out;
            }
            input.close();
        } catch (IOException e) {
            throw new AmkfbaException("returnAmkfbaStderr: IOException");
        }
        throw new AmkfbaException("Amkfba returned an error message:\n" + errorMsg);
    }

    private AmkfbaOutput parseAmkfbaOutput(Process amkfbaProcess) throws AmkfbaException {
        String out;
        AmkfbaOutput retval = new AmkfbaOutput();
        BufferedReader input = new BufferedReader(new InputStreamReader(amkfbaProcess.getInputStream()));
        try {
            out = input.readLine();
            if (out == null) {
                returnAmkfbaStderr(amkfbaProcess);
            }
            retval.setOptimizationStatus(getStatus(out));
            retval.setGrowthRate(getGrowthRate(out));
            input.close();
            System.out.println("OK");
            return retval;
        } catch (IOException e) {
            throw new AmkfbaException("parseAmkfbaOutput: IOException");
        }
    }

    void dumpModel(EModel model, Collection<EBounds> bounds, Boolean useRmodel) {
        StringWriter loggerWriter = new StringWriter();
        BufferedWriter loggerOutput = new BufferedWriter(loggerWriter);
        try {
            writeModel(loggerOutput, model, bounds, useRmodel);
            AcornLogger.getInstance().logInput(loggerWriter.toString());
            loggerWriter.close();
        } catch (IOException e) {
            AcornLogger.getInstance().logError("Failed to dump model to file");
        }
    }

    public AmkfbaOutput runObjstat(EModel model, Collection<EBounds> bounds, String objectiveFunction, Boolean useRmodel, Boolean maximum) throws AmkfbaException {
        Process p;
        String args = "-p objstat -obj " + objectiveFunction;
        if (!maximum) {
            args += " -min";
        }
        
        dumpModel(model, bounds, useRmodel);

        p = execAmkfba(args);
        
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
        
        try {
            writeModel(output, model, bounds, useRmodel);
            output.close();
        } catch (IOException e) {
            throw new AmkfbaException("runObjstat: IOException");
        }
        return parseAmkfbaOutput(p);
    }

    public FbaOutput runFba(EModel model, Collection<EBounds> bounds, String objectiveFunction) throws AmkfbaException {
        FbaOutput retval = new FbaOutput();
        String out;
        String line;

        dumpModel(model, bounds, false);

        Process p = execAmkfba("-p fba -obj " + objectiveFunction);

        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        
        try {
            writeModel(output, model, bounds, false);
            output.close();
            out = input.readLine();
            if (out == null) {
                returnAmkfbaStderr(p);
            }

            retval.setOptimizationStatus(getFbaOptimizationStatus(out));
            if (!retval.getCommonResults().getStatus().equals(ECommonResults.statusOptimal)) {
                return retval;
            }

            out = input.readLine();
            if (out == null) {
                returnAmkfbaStderr(p);
            }
            retval.setGrowthRate(getFbaGrowthRate(out));

            while ((line = input.readLine()) != null) {
                String[] fields;
                fields = line.trim().split("[ \t]+");

                if (fields.length < 2) {
                    break;
                }
                retval.addLine(fields[0], Float.valueOf(fields[1]));
            }
            input.close();
            System.out.println("OK");
        } catch (java.io.IOException err) {
            throw new AmkfbaException("runFba: IOException");
        } catch (java.lang.NumberFormatException e) {
            throw new AmkfbaException("runFba: NumberFormatException");
        }
        return retval;
    }

    public AmkfbaOutput runKgene(EModel model, Collection<EBounds> bounds, String objectiveFunction, String gene, Boolean useRmodel) throws AmkfbaException {
        Process p;
        String args = "-p kogene -obj " + objectiveFunction + " -ko " + gene;

        dumpModel(model, bounds, useRmodel);

        p = execAmkfba(args);

        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));

        try {
            writeModel(output, model, bounds, useRmodel);
            output.close();
        } catch (IOException e) {
            throw new AmkfbaException("runKgene: IOException");
        }
        return parseAmkfbaOutput(p);
    }

    public static String getAmkfbaPath() {
        return amkfbaPath;
    }

    public static void setAmkfbaPath(String aAmkfbaDir) {
        amkfbaPath = aAmkfbaDir;
    }
}


