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

#include "../../../micromanager/MMDevice/MMDevice.h"
#include "../../../micromanager/MMDevice/DeviceBase.h"
#include "../../../micromanager/MMDevice/ImgBuffer.h"
#include "../../../micromanager/MMDevice/DeviceThreads.h"
#include <string>
#include <map>


#include <atlbase.h>
#include "FEI\xtlib2.h"   // XTLib includes (interface definitions)
#include "FEI\xtlib2_i.c" // XTLib includes (constants CLSID,IDD)

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
   MM::DeviceDetectionStatus DetectDevice(void);
   int DetectInstalledDevices();
   void LogFEIError(HRESULT hresult);
   // action interface
 //  int OnErrorRate(MM::PropertyBase* pProp, MM::ActionType eAct);
 //  int OnDivideOneByMe(MM::PropertyBase* pProp, MM::ActionType eAct);

private:
 //  void GetPeripheralInventory();

   //std::vector<std::string> peripherals_;
   bool initialized_;
   bool busy_;
   int lastError_;
   static MMThreadLock lock_;
   IMicroscopeControl * pIMicroscopeControl;
};

/*class FEISEMCamera : public CCameraBase<FEISEMCamera>
{

}*/

class FEISEMController : public CGenericBase<FEISEMController>
{
public:
	FEISEMController();
	~FEISEMController();

	int Initialize();
    int Shutdown();
    void GetName(char* pszName) const;
    bool Busy(){ return busy_;} ;

   int OnSaveImage(MM::PropertyBase* pProp, MM::ActionType eAct);
   void LogFEIError(HRESULT hresult);
   void COMInit();
private:
   bool initialized_;
   bool busy_;
   int counter_;
   

    
        IMicroscopeControl * pIMicroscopeControl;
	
		
		IVideoControl * pIVideoControl;
		IChannels * pIChannels;
		IChannel* pIChannel;
	  
	  IConnectionPointContainer * pIConnectionPointContainer;
	  IDispatch * pIDispatch;
	  //FEISEMHub* hub;
};

#endif //_FEISEM_H_
