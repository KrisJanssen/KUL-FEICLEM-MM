/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package KUL.FluoSEMFX;

import mmcorej.CMMCore;
import org.micromanager.api.MMPlugin;
import org.micromanager.api.ScriptInterface;
import static KUL.FluoSEMFX.FluoSEMFXLaserControl.tooltipDescription;

/**
 *
 * @author supervisor
 */
public class FluoSEMFXLaserControl implements MMPlugin {
   public static String menuName = "FluoSEM Laser Control";
   public static String tooltipDescription = "FluoSEM Laser Control of Lighthub.";
   private CMMCore core_;
   private ScriptInterface gui_;
   private FluoSEMFXLaserControlFrame myFrame_;

   public void setApp(ScriptInterface app) {
      gui_ = app;                                        
      core_ = app.getMMCore();
      if (myFrame_ == null)
         myFrame_ = new FluoSEMFXLaserControlFrame(gui_);
      myFrame_.setVisible(true);
   }

   public void dispose() {
      // nothing todo:
   }

   public void show() {
         String ig = "KUL FluoSEM Laser Control";
   }

   public void configurationChanged() {
   }

   public String getInfo () {
      return "KUL FluoSEM Laser Control Plugin";
   }

   public String getDescription() {
      return tooltipDescription;
   }
   
   public String getVersion() {
      return "0.0.0.1";
   }
   
   public String getCopyright() {
      return "KU Leuven, 2014";
   }
}
