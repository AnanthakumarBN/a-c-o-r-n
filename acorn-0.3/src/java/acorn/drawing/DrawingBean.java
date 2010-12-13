/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.drawing;

import acorn.db.EMethod;
import acorn.db.ETask;
import acorn.db.ETaskController;
import acorn.db.EVisArcProduct;
import acorn.db.EVisArcReactant;
import acorn.db.EVisPlace;
import acorn.db.EVisTransition;
import acorn.db.EVisualization;
import acorn.db.EVisualizationController;
import acorn.exception.DotFileException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mateusza
 */
public class DrawingBean {

    private String format;
    private String scale;
    private String dir;
    private EVisualization vis;
    private ETask task;
    private static final String placeWidth = "0.0";
    private static final String placeHeight = "0.0";
    private static final String transitionWidth = "0.1";
    private static final String transitionHeight = "0.1";
    private static final String drawingScale = "70";

    public DrawingBean() {
        this.format = "jpg";
        this.scale = drawingScale;
        this.dir = "drawing";
    }

    public DrawingBean(String format, String scale, String dir) {
        this.format = format;
        this.scale = scale;
        this.dir = dir;
    }

    public DrawingBean(EVisualization vis, ETask task) {
        this.vis = vis;
        this.task = task;
        this.format = "jpg";
        this.scale = drawingScale;
        this.dir = "drawing";
    }

    public String draw() throws DotFileException {
        String pathFile = createEmptyFile();
        writeTotFile(pathFile);
        String pathGraphFile = runNeato(pathFile);

        return pathGraphFile;
    }

    public String createEmptyFile() throws DotFileException {
        //String curDir = System.getProperty("user.dir");
        String userHome = System.getProperty("user.home");
        String dirr = userHome + "/" + this.dir;
        //!!!we need to qualify the vis name with the user name but not use "." rather "_"
        String filePath = dirr + "/" + vis.getQualifiedNameForDot() + ".dot";
        if (!new File(dirr).exists() && !(new File(dirr)).mkdir()) {
            throw new DotFileException("can't create path for dot file");
        }
        if ((new File(filePath)).exists() && !(new File(filePath)).delete()) {
            throw new DotFileException("Can't delete old DOT file: " + filePath + " . May be in use.");
        }
        try {
            File f = new File(filePath);
            if (!f.createNewFile()) {
                throw new DotFileException("Can't create file: " + filePath);
            }
        } catch (Exception e) {
        }
        return filePath;
    }

    public void writeTotFile(String filePath) {
        int corner_num = 0;

        EVisualizationController vc = new EVisualizationController();
        ETaskController tc = new ETaskController();

        List<EVisPlace> lp = vc.getPlaces(vis.getId());
        List<EVisTransition> lt = vc.getTransitions(vis.getId());
        List<EVisArcProduct> lap = vc.getArcProducts(vis.getId());
        List<EVisArcReactant> lar = vc.getArcReactants(vis.getId());
        String placeBack = "\", pin=true, width=\"" + placeWidth + "\", height=\"" + placeHeight + "\"];\n";
        String transitionBack = "\", shape=box, style=bold, pin=true, width=\"" + transitionWidth + "\", height=\"" + transitionHeight + "\"];\n";
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
            out.write("Digraph " + vis.getName() + " {\n");
            for (EVisPlace place : lp) {
                String firstName = place.getSpeciesSid();
                //String firstName = place.getName();
                out.write("\t" + firstName + "_" + place.getId() + " [label=\"" + firstName + "\", pos=\"" + place.getX() + "," + place.getY() + placeBack);
            }
            out.write("\n");
            if (EMethod.fba.equals(task.getMethod().getIdent())) {
                for (EVisTransition trans : lt) {
                    String name = trans.getReactionSid();
                    String flux = Float.toString(tc.getFlux(task, name));
                    out.write("\t" + name + "_" + trans.getId() + " [label=\"" + name + "\", pos=\"" + trans.getX() + "," + trans.getY() + "\",label=\"" + name + " " + flux + transitionBack);
                }
            } else if (EMethod.fva.equals(task.getMethod().getIdent())) {
                for (EVisTransition trans : lt) {
                    String name = trans.getReactionSid();
                    String flux = tc.getFluxFVA(task, name);
                    out.write("\t" + name + "_" + trans.getId() + " [label=\"" + name + "\", pos=\"" + trans.getX() + "," + trans.getY() + "\",label=\"" + name + " " + flux + transitionBack);
                }
            }

            out.write("\n");
            for (EVisArcProduct product : lap) {
                EVisPlace place = product.getTarget();
                EVisTransition transition = product.getSource();
                //  List<EVisArcpath> apl = (List<EVisArcpath>) product.getArcpathList();
                String target = place.getSpeciesSid() + "_" + place.getId();
                String source = transition.getReactionSid() + "_" + transition.getId();
//                String corner1 = source;
//                String corner2 = "ccc_"+corner_num;
//
//                if (apl.size() == 2){
                out.write(source + "->" + target + ";\n");
//                } else{
//                    for(EVisArcpath ap: apl.subList(1, apl.size()-2)){
//                    /* TODO
//                     * WSKAZANIE Z KORNER1 NA KORNER2
//                     */
//                    corner_num++;

            }
//                    corner1=corner2;
//                    corner2= target;
//                    /*TODO
//                     * OSTANIE WSKAZANIE W GRAFIE KORNER NA TARGET
//                     */
//
//                }
//            }
            out.write("\n");
            for (EVisArcReactant react : lar) {
                EVisPlace place = react.getSource();
                EVisTransition transition = react.getTarget();
                String source = place.getSpeciesSid() + "_" + place.getId();
                String target = transition.getReactionSid() + "_" + transition.getId();
                out.write(source + "->" + target + ";\n");
            }
            out.write("\n");
            out.write("}");
            out.close();
        } catch (IOException e) {
        }
    }

    public String runNeato(String filePath) throws DotFileException {
        try {
            Runtime rt = Runtime.getRuntime();
            String firstRun = "neato -y -s" + scale + " " + filePath + " -o" + filePath + ".dot";
            Process p = rt.exec(firstRun);
            for (int i = 0; i <= 10; i++) {
                if ((new File(filePath + ".dot")).exists()) {
                    break;
                }
                if (i == 10) {
                    throw new DotFileException("Generation of visualization failed.");
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(DrawingBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            String secondRun = "neato -s" + scale + " -T" + format + " " + filePath + ".dot -o" + filePath + ".dot." + format;

            Process p2 = rt.exec(secondRun);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DotFileException("Can't create graphic file.");
        }
        return filePath + ".dot." + format;
    }

    public EVisualization getVis() {
        return vis;
    }

    public void setVis(EVisualization vis) {
        this.vis = vis;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }
}
