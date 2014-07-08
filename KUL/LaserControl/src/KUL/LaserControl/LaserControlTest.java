/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package KUL.LaserControl;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

/**
 *
 * @author supervisor
 */
public class LaserControlTest extends Application {
    
    
    
    @Override
    public void start(Stage stage) throws Exception {
        LaserControlII testctrl = new LaserControlII(565.0,200.0);
        testctrl.setSetPointPct("50");
        stage.setScene(new Scene(testctrl));
        stage.setTitle("Custom Control");
        stage.setWidth(250);
        stage.setHeight(500);
        stage.show();
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
