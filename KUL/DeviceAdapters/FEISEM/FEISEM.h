///////////////////////////////////////////////////////////////////////////////
// FILE:          DemoCamera.h
// PROJECT:       Micro-Manager
// SUBSYSTEM:     DeviceAdapters
//-----------------------------------------------------------------------------
// DESCRIPTION:   The example implementation of the demo camera.
//                Simulates generic digital camera and associated automated
//                microscope devices and enables testing of the rest of the
//                system without the need to connect to the actual hardware. 
//                
// AUTHOR:        Nenad Amodaj, nenad@amodaj.com, 06/08/2005
//                
//                Karl Hoover (stuff such as programmable CCD size  & the various image processors)
//                Arther Edelstein ( equipment error simulation)
//
// COPYRIGHT:     University of California, San Francisco, 2006
//                100X Imaging Inc, 2008
//
// LICENSE:       This file is distributed under the BSD license.
//                License text is included with the source distribution.
//
//                This file is distributed in the hope that it will be useful,
//                but WITHOUT ANY WARRANTY; without even the implied warranty
//                of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//
//                IN NO EVENT SHALL THE COPYRIGHT OWNER OR
//                CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
//                INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES.
//
// CVS:           $Id$
//

#ifndef _FEISEM_H_
#define _FEISEM_H_

#include "../../../micromanager/MMDevice/DeviceBase.h"
#include "../../../micromanager/MMDevice/ImgBuffer.h"
#include "../../../micromanager/MMDevice/DeviceThreads.h"
#include <string>
#include <map>
#include <algorithm>

#include <atlbase.h>

#include "xtlib2_i.c"	// XTLib includes (constants CLSID,IDD)
#include "xtlib2.h"    // XTLib includes (interface definitions)

//////////////////////////////////////////////////////////////////////////////
// Error codes
//
#define ERR_UNKNOWN_MODE         102
#define ERR_UNKNOWN_POSITION     103
#define ERR_IN_SEQUENCE          104
#define ERR_SEQUENCE_INACTIVE    105
#define ERR_STAGE_MOVING         106
#define SIMULATED_ERROR          200
#define HUB_NOT_AVAILABLE        107

const char* NoHubError = "Parent Hub not defined.";

////////////////////////
// DemoHub
//////////////////////

class FEISEMHub : public HubBase<FEISEMHub>
{
public:
   FEISEMHub();
   ~FEISEMHub();

   // Device API
   // ---------
   int Initialize();
   int Shutdown();
   void GetName(char* pName) const; 
   bool Busy() { return busy_;} ;
   //bool GenerateRandomError();

   // HUB api
  // MM::DeviceDetectionStatus DetectDevice(void);
   int DetectInstalledDevices();
   void LogFEIError(HRESULT hresult);
   // action interface
 //  int OnErrorRate(MM::PropertyBase* pProp, MM::ActionType eAct);
 //  int OnDivideOneByMe(MM::PropertyBase* pProp, MM::ActionType eAct);
   int OnPressure(MM::PropertyBase* pProp, MM::ActionType eAct);
   int OnChamberState(MM::PropertyBase* pProp, MM::ActionType pAct);
   int OnChamberMode(MM::PropertyBase* pProp, MM::ActionType pAct);
   int OnScanMode(MM::PropertyBase* pProp, MM::ActionType pAct);
   int OnDwellTime(MM::PropertyBase* pProp, MM::ActionType pAct);
   int OnPixels(MM::PropertyBase* pProp, MM::ActionType pAct);
   int OnSelectedArea(MM::PropertyBase* pProp, MM::ActionType pAct);
   int OnRotation(MM::PropertyBase* pProp, MM::ActionType pAct);
   int OnFOV(MM::PropertyBase* pProp, MM::ActionType pAct);
   int OnSelectedEnabled(MM::PropertyBase* pProp, MM::ActionType pAct);
   int OnSpot(MM::PropertyBase* pProp, MM::ActionType pAct);
   int OnHTState(MM::PropertyBase* pProp, MM::ActionType pAct);
   int OnAcceleratingVoltage(MM::PropertyBase* pProp, MM::ActionType pAct);
   int OnSpotSize(MM::PropertyBase* pProp, MM::ActionType pAct);
   int OnBeamBlank(MM::PropertyBase* pProp, MM::ActionType pAct);
   int OnStigmation(MM::PropertyBase* pProp, MM::ActionType pAct);
   int OnBeamShift(MM::PropertyBase* pProp, MM::ActionType pAct);

private:
 //  void GetPeripheralInventory();

   //std::vector<std::string> peripherals_;
   bool initialized_;
   bool busy_;
   int lastError_;
   static MMThreadLock lock_;
   IBeamControl * pIBeamControl;
        IElectronBeamControl * pIElectronBeamControl;
        IMicroscopeControl * pIMicroscopeControl;
		IScanControl * pIScanControl;
		IVacSystemControl * pIVacSystemControl;
};


//////////////////////////////////////////////////////////////////////////////
// CDemoCamera class
// Simulation of the Camera device
//////////////////////////////////////////////////////////////////////////////




#endif //_FEISEM_H_
