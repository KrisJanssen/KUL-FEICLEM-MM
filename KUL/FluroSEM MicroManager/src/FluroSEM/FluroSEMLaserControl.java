/**
 * StageControl plugin
 * 
 * This Micro-Manager plugin provides a simple interface to the currently 
 * active XY stage and Z (focus) drive
 *
 * Created on Aug 19, 2010, 10:04:49 PM
 * Nico Stuurman, copyright UCSF, 2010
 * 
 * LICENSE:      This file is distributed under the BSD license.
 *               License text is included with the source distribution.
 *
 *               This file is distributed in the hope that it will be useful,
 *               but WITHOUT ANY WARRANTY; without even the implied warranty
 *               of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 *               IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *               CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *               INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES.
 **/

package FluroSEM;

import mmcorej.CMMCore;
import org.micromanager.api.MMPlugin;
import org.micromanager.api.ScriptInterface;
import static FluroSEM.FluroSEMLaserControl.tooltipDescription;


public class FluroSEMLaserControl implements MMPlugin {
   public static String menuName = "FluroSEM Laser Control";
   public static String tooltipDescription = "FluroSEM Laser Control of Lighthub.";
   private CMMCore core_;
   private ScriptInterface gui_;
   private FluroSEMLaserControlFrame myFrame_;

   public void setApp(ScriptInterface app) {
      gui_ = app;                                        
      core_ = app.getMMCore();
      if (myFrame_ == null)
         myFrame_ = new FluroSEMLaserControlFrame(gui_);
      myFrame_.setVisible(true);
   }

   public void dispose() {
      // nothing todo:
   }

   public void show() {
         String ig = "FluroSEM Laser Control";
   }

   public void configurationChanged() {
   }

   public String getInfo () {
      return "FluroSEM Laser Control Plugin";
   }

   public String getDescription() {
      return tooltipDescription;
   }
   
   public String getVersion() {
      return "First version";
   }
   
   public String getCopyright() {
      return "KU Leuven, 2013";
   }
}