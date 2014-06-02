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
        LaserControlII testctrl = new LaserControlII();
        testctrl.setSetPointPct("50");
        stage.setScene(new Scene(testctrl));
        stage.setTitle("Custom Control");
        stage.setWidth(300);
        stage.setHeight(200);
        stage.show();
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
