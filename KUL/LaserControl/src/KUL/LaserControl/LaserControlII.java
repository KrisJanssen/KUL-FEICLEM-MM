/**
 * Sample Skeleton for 'LaserControlII.fxml' Controller Class
 */

package KUL.LaserControl;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LaserControlII extends VBox  {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="powerSettingPct"
    private TextField powerSettingPct; // Value injected by FXMLLoader

    @FXML // fx:id="mWIndicator"
    private Label mWIndicator; // Value injected by FXMLLoader

    @FXML // fx:id="powerSettingmW"
    private TextField powerSettingmW; // Value injected by FXMLLoader

    @FXML // fx:id="pctIndicator"
    private ProgressBar pctIndicator; // Value injected by FXMLLoader

    @FXML // fx:id="laserID"
    private Label laserID; // Value injected by FXMLLoader

    @FXML // fx:id="btnONOFF"
    private Button btnONOFF; // Value injected by FXMLLoader

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert powerSettingPct != null : "fx:id=\"powerSettingPct\" was not injected: check your FXML file 'LaserControlII.fxml'.";
        assert mWIndicator != null : "fx:id=\"mWIndicator\" was not injected: check your FXML file 'LaserControlII.fxml'.";
        assert powerSettingmW != null : "fx:id=\"powerSettingmW\" was not injected: check your FXML file 'LaserControlII.fxml'.";
        assert pctIndicator != null : "fx:id=\"pctIndicator\" was not injected: check your FXML file 'LaserControlII.fxml'.";
        assert laserID != null : "fx:id=\"laserID\" was not injected: check your FXML file 'LaserControlII.fxml'.";
        assert btnONOFF != null : "fx:id=\"btnONOFF\" was not injected: check your FXML file 'LaserControlII.fxml'.";

    }
    
    private double dCurrentSetPointPct;
    private double dRatedPower;
    
    private double dWavelength;
    private String strCurrentValue;
    
    public LaserControlII(double _dWavelength, double _dRatedPower) {
        
        // Try and load the fxml
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LaserControlII.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
        // Set up our variables correctly
        dWavelength = _dWavelength;
        dRatedPower = _dRatedPower;
        
        strCurrentValue = "0";
        
        mWIndicator.setText(strCurrentValue);
    }
    
    public String getSetPointPct() {
        return mWIndicator.getText();
    }
    
    public void setSetPointPct(String Value) {
        mWIndicator.setText(Value);
        pctIndicator.setProgress(0.5);
    }
    
    
    
    @FXML
    protected void doSomething() {
        System.out.println("The Button Was Clicked!!");
    }
}
