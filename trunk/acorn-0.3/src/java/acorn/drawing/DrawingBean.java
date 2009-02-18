/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.drawing;

import acorn.db.EReaction;
import acorn.db.ETask;
import acorn.db.EVisArcProduct;
import acorn.db.EVisArcReactant;
import acorn.db.EVisPlace;
import acorn.db.EVisTransition;
import acorn.db.EVisualization;
import acorn.db.EVisualizationController;
import acorn.db.EfbaResultElement;
import acorn.exception.DotFileException;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

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

    public DrawingBean() {
        this.format = "jpg";
        this.scale = "80";
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
        this.scale = "80";
        this.dir = "drawing";
    }

    public String draw() throws DotFileException {

        String pathFile = createEmptyFile();
        System.out.println(pathFile);
        writeDotFile(pathFile);
        String pathGraphFile = runNeato(pathFile);

        return pathGraphFile;
    }

    public String createEmptyFile() throws DotFileException {
        String curDir = System.getProperty("user.dir");
        String userHome = System.getProperty("user.home");
        String dir = userHome + "/" + this.dir;
        String filePath = dir + "/" + vis.getName() + ".dot";
        if (!new File(dir).exists() && !(new File(dir)).mkdir()) {
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

    /* @params reactionSid - sid of reaction for which flux is returned
     * 
     * 
     * @returns flux for pointed reaction and task
     */
    public String getFlux(String reactionSid) {
        String flux = "0.0";
        List<EfbaResultElement> fbaList = (List<EfbaResultElement>) task.getEfbaResultElementCollection();
        for (EfbaResultElement fba : fbaList) {
            EReaction react = fba.getReaction();

            if (react.getSid().equals(reactionSid)) {
                return Float.toString(fba.getFlux());
            }
        }
        return flux;
    }

    public void writeDotFile(String filePath) {
        int corner_num = 0;
        EVisualizationController vc = new EVisualizationController();
        List<EVisPlace> lp = vc.getPlaces(vis.getId());
        List<EVisTransition> lt = vc.getTransitions(vis.getId());
        List<EVisArcProduct> lap = vc.getArcProducts(vis.getId());
        List<EVisArcReactant> lar = vc.getArcReactants(vis.getId());
        String placeBack = "\", pin=true, width=\"0.5\", height=\"0.50\"];\n";
        String smalPlaceBack = "\", pin=true, width=\"0.1\", height=\"0.1\"];\n";
        String transitionBack = "\", shape=box, style=bold, pin=true, width=\"0.2\", height=\"0.70\"];\n";
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
            out.write("Digraph " + vis.getName() + " {\n");
            for (EVisPlace place : lp) {
                String firstName = place.getName().split(";")[0];
                //String firstName = place.getName();
                out.write("\t" + firstName + " [pos=\"" + place.getX() + "," + place.getY() + placeBack);
            }
            out.write("\n");
            for (EVisTransition trans : lt) {
                String name = trans.getName();
                String flux = getFlux(name);
                out.write("\t" + name + " [pos=\"" + trans.getX() + "," + trans.getY() + "\",label=\"" + name + " " + flux + transitionBack);
            }
            out.write("\n");
            for (EVisArcProduct product : lap) {
                EVisPlace place = product.getTarget();
                EVisTransition transition = product.getSource();
                //  List<EVisArcpath> apl = (List<EVisArcpath>) product.getArcpathList();
                String target = place.getName().split(";")[0];
                String source = transition.getName();
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
                String source = place.getName().split(";")[0];
                String target = transition.getName();
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
