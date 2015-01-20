///////////////////////////////////////////////////////////////////////////////
// FILE:          DemoCamera.cpp
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
// COPYRIGHT:     University of California, San Francisco, 2006
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
// CVS:           $Id$
//

#include "FEISEM.h"
#include <cstdio>
#include <string>
#include <math.h>
#include "../../../micromanager/MMDevice/ModuleInterface.h"
#include "../../../micromanager/MMCore/Error.h"
#include <sstream>
#include <algorithm>
#include "WriteCompactTiffRGB.h"
#include <iostream>

#define _AFXDLL

//#include "stdafx.h"





#define MACHINE		_T("192.168.0.1")

/*#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif*/

using namespace std;
//const double FEISEM::nominalPixelSizeUm_ = 1.0;
double g_IntensityFactor_ = 1.0;

const double g_DistanceMultiplier = 1000000;
const double g_StigmationMultiplier = 1000;
const double g_BeamShiftMultiplier = 100000;
const double g_TimeMultiplier = 1000000;
const double g_ScanRotationMultiplier = 180/3.141582;

// External names used used by the rest of the system
// to load particular device from the "DemoCamera.dll" library
const char* g_CameraDeviceName = "FEISEM Camera";
const char* g_ControllerDeviceName = "FEISEM Controller";
const char* g_HubDeviceName = "FEISEM Hub";

// constants for naming pixel types (allowed values of the "PixelType" property)
const char* g_MODE_HIVAC = "Hi-Vac";
const char* g_MODE_CHARGENEUT = "Low Vacuum";
const char* g_MODE_ENVIRONMENTAL = "Environmental";

const char* g_STATE_ALL_AIR = "Vented";
const char* g_STATE_PUMPING = "Pumping - Pumping";
const char* g_STATE_PREVAC = "Pumping - Prevac";
const char* g_STATE_VACUUM = "Vacuum";
const char* g_STATE_VENTING = "Venting";
const char* g_STATE_ERROR = "Error";

const char* g_SCAN_NONE = "None";
const char* g_SCAN_EXTERNAL = "External";
const char* g_SCAN_FULLFRAME = "Full Frame";
const char* g_SCAN_SPOT = "Spot";
const char* g_SCAN_LINE = "Line";


const char* g_HTSTATE_ON = "ON";
const char* g_HTSTATE_OFF = "OFF";
const char* g_HTSTATE_RAMPING_UP = "Ramping Up";
const char* g_HTSTATE_RAMPING_DOWN = "Ramping Down";
const char* g_HTSTATE_INITIALIZING = "Initialising";

// TODO: linux entry code

// windows DLL entry code
#ifdef WIN32
BOOL APIENTRY DllMain( HANDLE /*hModule*/, 
                      DWORD  ul_reason_for_call, 
                      LPVOID /*lpReserved*/
                      )
{
   switch (ul_reason_for_call)
   {
   case DLL_PROCESS_ATTACH:
   case DLL_THREAD_ATTACH:
   case DLL_THREAD_DETACH:
   case DLL_PROCESS_DETACH:
      break;
   }
   return TRUE;
}
#endif




///////////////////////////////////////////////////////////////////////////////
// Exported MMDevice API
///////////////////////////////////////////////////////////////////////////////

/**
 * List all suppoerted hardware devices here
 * Do not discover devices at runtime.  To avoid warnings about missing DLLs, Micro-Manager
 * maintains a list of supported device (MMDeviceList.txt).  This list is generated using 
 * information supplied by this function, so runtime discovery will create problems.
 */
MODULE_API void InitializeModuleData()
{
  // AddAvailableDeviceName(g_CameraDeviceName, "FEISEM Camera");
  // AddAvailableDeviceName(g_ControllerDeviceName, "FEISEM Controller");
   RegisterDevice(g_ControllerDeviceName, MM::GenericDevice, "FEISEM Controller");
/*   AddAvailableDeviceName(g_WheelDeviceName, "Demo filter wheel");
   AddAvailableDeviceName(g_StateDeviceName, "Demo State Device");
   AddAvailableDeviceName(g_ObjectiveDeviceName, "Demo objective turret");
   AddAvailableDeviceName(g_StageDeviceName, "Demo stage");
   AddAvailableDeviceName(g_XYStageDeviceName, "Demo XY stage");
   AddAvailableDeviceName(g_LightPathDeviceName, "Demo light path");
   AddAvailableDeviceName(g_AutoFocusDeviceName, "Demo auto focus");
   AddAvailableDeviceName(g_ShutterDeviceName, "Demo shutter");
   AddAvailableDeviceName(g_DADeviceName, "Demo DA");
   AddAvailableDeviceName(g_MagnifierDeviceName, "Demo Optovar");
   AddAvailableDeviceName("TransposeProcessor", "TransposeProcessor");
   AddAvailableDeviceName("ImageFlipX", "ImageFlipX");
   AddAvailableDeviceName("ImageFlipY", "ImageFlipY");
   AddAvailableDeviceName("MedianFilter", "MedianFilter");*/
   RegisterDevice(g_HubDeviceName, MM::HubDevice, "FEISEM Hub");


}

MODULE_API MM::Device* CreateDevice(const char* deviceName)
{
   if (deviceName == 0)
      return 0;

 /*  if (strcmp(deviceName, g_CameraDeviceName) == 0)
   {
      // create camera
      return new FEISEMCamera();
   }*/
   if (strcmp(deviceName, g_ControllerDeviceName) == 0)
   {
	  return new FEISEMController();
   }
  /* else if (strcmp(deviceName, g_CameraDeviceName) == 0)
   {
	  return new FEISEMCamera();
   }*/
   else if (strcmp(deviceName, g_HubDeviceName) == 0)
   {
	  return new FEISEMHub();
   }

   // ...supplied name not recognized
   return 0;
}

MODULE_API void DeleteDevice(MM::Device* pDevice)
{
   delete pDevice;
}



FEISEMHub::FEISEMHub():initialized_(false),busy_(false)
{
		CreateProperty(MM::g_Keyword_Description, "FEISEM Hub", MM::String, true);
}
FEISEMController::FEISEMController():initialized_(false),busy_(false)
{
		hub = NULL;
		pIBeamControl = NULL;
        pIElectronBeamControl = NULL;
		pIVideoControl = NULL;
		pIChannels = NULL;
        pIMicroscopeControl = NULL;
		pIScanControl = NULL;
		pIVacSystemControl = NULL;
		pIChannel = NULL;
		pIConnectionPointContainer = NULL;
		pIDispatch = NULL; 

		CreateProperty(MM::g_Keyword_Description, "FEISEM Controller", MM::String, true);
		int ret = CreateProperty(MM::g_Keyword_Description, "FEISEM Controller", MM::String, true);

   // Name
   ret = CreateProperty(MM::g_Keyword_Name, g_ControllerDeviceName, MM::String, true);
   CreateHubIDProperty();
}

void FEISEMHub::LogFEIError(HRESULT hresult)
{
	LogMessage("XTLib Error: " + hresult, true);
}




int FEISEMHub::Shutdown()
{


      //  cout << _T( "Uninitializing COM library" ) << endl;
        
		//return DEVICE_OK;
	initialized_ = false;
	return DEVICE_OK;
}

int FEISEMController::Shutdown()
{
		if(pIElectronBeamControl)
		{
			pIElectronBeamControl->Release();
            pIElectronBeamControl = NULL;
		}
		if(pIBeamControl)
		{
			pIBeamControl->Release();
            pIBeamControl = NULL;
		}
		if(pIScanControl)
		{
			pIScanControl->Release();
            pIScanControl = NULL;
		}
		if(pIVacSystemControl)
		{
			pIVacSystemControl->Release();
            pIVacSystemControl = NULL;
		}

        if ( pIMicroscopeControl )
        {
        //    cout << _T( "Releasing Microscope control" ) << endl;
            pIMicroscopeControl->Disconnect();
            pIMicroscopeControl->Release();
            pIMicroscopeControl = NULL;
        }

      //  cout << _T( "Uninitializing COM library" ) << endl;
        CoUninitialize();
		//return DEVICE_OK;
	initialized_ = false;
	return DEVICE_OK;
}

FEISEMHub::~FEISEMHub()
{
        Shutdown();
}

FEISEMController::~FEISEMController()
{
        Shutdown();
}


int FEISEMHub::Initialize()
{
  // FEISEMHub* pHub = static_cast<FEISEMHub*>(GetParentHub());
   
	return DEVICE_OK;
}

int FEISEMController::Initialize()
{

   hub = static_cast<FEISEMHub*>(GetParentHub());

   char hubLabel[MM::MaxStrLength];
   hub->GetLabel(hubLabel);
   SetParentID(hubLabel); // for backward comp.

   double min, max;
   long lmin, lmax;
   int nRetCode = 0;

        HRESULT hr = E_FAIL;
        
        CComBSTR sMachine( MACHINE );

        if ( SUCCEEDED( hr = CoInitialize( NULL ) ) )
        {
  //          cout << _T( "COM library initialized successfully" ) << endl;

            if ( SUCCEEDED( hr = CoCreateInstance( CLSID_MicroscopeControl, NULL, CLSCTX_INPROC_SERVER, IID_IMicroscopeControl, reinterpret_cast < void ** > (&pIMicroscopeControl) ) ) )
            {
  //              cout << _T( "Create instance of MicroscopeControl object succeeded" ) << endl;

                if ( SUCCEEDED( hr = pIMicroscopeControl->Connect( sMachine ) ) )
                {
  //                  cout << _T( "Connect to microscope server succeeded" ) << endl;

                    if ( SUCCEEDED( hr = pIMicroscopeControl->BeamControl( &pIBeamControl ) ) && (pIBeamControl != NULL) )
                    {
  //                      cout << _T( "BeamControl object created successfully" ) << endl;

                        if ( SUCCEEDED( hr = pIBeamControl->ElectronBeamControl( &pIElectronBeamControl ) ) && (pIElectronBeamControl != NULL) )
                        {
    //                        cout << _T( "ElectronBeamControl object created successfully" ) << endl;

                           

                            // Get High voltage value 
							/*
                            if ( SUCCEEDED( hr = pIElectronBeamControl->get_HTVoltage( &dHV ) ) )
                            {
      //                          cout << _T( "HTVoltage = " ) << dHV << endl;
                            }
                            else
                            {
       //                         cout << _T( "ERROR: Cannot get value of HTVoltage" ) << endl;
                            }*/   
                        }
                        else
                        {
             //               cout << _T( "ERROR: Cannot create ElectronBeamControl object" ) << endl;
                        }
						
                    }
                    else
                    {
               //         cout << _T( "ERROR: Cannot create BeamControl object" ) << endl;
                    }
					if( SUCCEEDED( hr = pIMicroscopeControl->ScanControl( &pIScanControl ) ) && (pIScanControl != NULL) )
					{					
					}
					else
					{
					}
					if( SUCCEEDED( hr = pIMicroscopeControl->VacSystemControl( &pIVacSystemControl ) ) && (pIVacSystemControl != NULL) )
					{
					}
					else
					{
					}
					if(SUCCEEDED( hr = pIMicroscopeControl->VideoControl( &pIVideoControl ) ) && (pIVideoControl != NULL) )
					{
						if(SUCCEEDED( hr = pIVideoControl->Channels( &pIChannels ) ) && (pIChannels != NULL) )
						{
							CComVariant varChannel( 0 );
							if(SUCCEEDED( hr = pIChannels->get_Item(varChannel, &pIChannel) ) && (pIChannel != NULL) )
							{
							}
							else
							{
							}
						}
						else
						{
						}
					}
					else
					{
                 //   cout << _T( "ERROR: Cannot connect to microscope server" ) << endl;
					}
            }
            else
            {
       //         cout << _T( "ERROR: Cannot create instance of MicroscopeControl object" ) << endl;
            }
        }
        else
        {
       //     cout << _T( "ERROR: Cannot initialize COM library" ) << endl;
        }
	}

		vector<string> BOOLOPT;
		BOOLOPT.push_back("true");
		BOOLOPT.push_back("false");
		//VacSystemControl
		CPropertyAction* pAct = new CPropertyAction (this, &FEISEMController::OnPressure);
		int ret = CreateProperty("Pressure (Pa)", "1000.0" , MM::Float, false, pAct);
		assert(nRet == DEVICE_OK);
        

	//	ret = CreateProperty("Pressure Range (Pa)", "0.0" , MM::Float, true);
	//	assert(DEVICE_OK == ret);
		pAct = new CPropertyAction (this, &FEISEMController::OnChamberState);
		ret = CreateProperty("Chamber State", "unknown" , MM::String, true, pAct);
		assert(nRet == DEVICE_OK);
        

		vector<string> StateValues;
		StateValues.push_back(g_STATE_ALL_AIR);
		StateValues.push_back(g_STATE_PUMPING);
		StateValues.push_back(g_STATE_PREVAC);
		StateValues.push_back(g_STATE_VACUUM);
		StateValues.push_back(g_STATE_VENTING);
		StateValues.push_back(g_STATE_ERROR);
		ret = SetAllowedValues("Chamber State", StateValues);
		if (DEVICE_OK != ret)
        return ret;

		pAct = new CPropertyAction (this, &FEISEMController::OnChamberMode);
		ret = CreateProperty("Chamber Mode", "unknown" , MM::String, true, pAct);
		assert(nRet == DEVICE_OK);
        	

		vector<string> ModeValues;
		ModeValues.push_back(g_MODE_HIVAC);
		ModeValues.push_back(g_MODE_CHARGENEUT);
		ModeValues.push_back(g_MODE_ENVIRONMENTAL);
		ret = SetAllowedValues("Chamber Mode", ModeValues);
		if (DEVICE_OK != ret)
        return ret;		
		hr = pIVacSystemControl->get_PressureRange((XTLibRange)XTLIB_RANGE_MIN, &min);
		if(hr != S_OK)
		{
			hub->LogFEIError(hr);
		}
		hr = pIVacSystemControl->get_PressureRange((XTLibRange)XTLIB_RANGE_MAX, &max);
		if(hr != S_OK)
		{
			hub->LogFEIError(hr);
		}
		SetPropertyLimits("Pressure (Pa)", min, max);

		//ScanControl
		pAct = new CPropertyAction (this, &FEISEMController::OnScanMode);
		ret = CreateProperty("Scan Mode", g_SCAN_NONE , MM::String, false, pAct);
				assert(nRet == DEVICE_OK);
        

		vector<string> ScanValues;
		ScanValues.push_back(g_SCAN_NONE);
		ScanValues.push_back(g_SCAN_EXTERNAL);
		ScanValues.push_back(g_SCAN_FULLFRAME);
		ScanValues.push_back(g_SCAN_SPOT);
		ScanValues.push_back(g_SCAN_LINE);
		ret = SetAllowedValues("Scan Mode", ScanValues);
		if (DEVICE_OK != ret)
        return ret;

		pAct = new CPropertyAction (this, &FEISEMController::OnDwellTime);
		ret = CreateProperty("Dwell Time (us)", "0.0" , MM::Float, false, pAct);
				assert(nRet == DEVICE_OK);
        
//		ret = CreateProperty("Dwell Time Max", "0.0" , MM::Float, true);
//		assert(DEVICE_OK == ret);
//		ret = CreateProperty("Dwell Time Min", "0.0" , MM::Float, true);
//		assert(DEVICE_OK == ret);

		hr = pIScanControl->get_DwellTimeRange((XTLibRange)XTLIB_RANGE_MIN, &min);
		if(hr != S_OK)
		{
			hub->LogFEIError(hr);
		}
		hr = pIScanControl->get_DwellTimeRange((XTLibRange)XTLIB_RANGE_MAX, &max);
		if(hr != S_OK)
		{
			hub->LogFEIError(hr);
		}
		ret = SetPropertyLimits("Dwell Time (us)", min*g_TimeMultiplier, max*g_TimeMultiplier);
				assert(nRet == DEVICE_OK);
        

		pAct = new CPropertyAction (this, &FEISEMController::OnVPixels);
		ret = CreateProperty("Vertical Pixels", "442" , MM::Integer, false, pAct);
				assert(nRet == DEVICE_OK);
        

		vector<string> VertPixelValues;
		VertPixelValues.push_back("442");
		VertPixelValues.push_back("884");
		VertPixelValues.push_back("1768");
		VertPixelValues.push_back("3536");
		ret = SetAllowedValues("Vertical Pixels", VertPixelValues);
				if (DEVICE_OK != ret)
        return ret;

		pAct = new CPropertyAction (this, &FEISEMController::OnHPixels);
		ret = CreateProperty("Horizontal Pixels", "512" , MM::Integer, false, pAct);
		assert(DEVICE_OK == ret);

		vector<string> HoriPixelValues;
		HoriPixelValues.push_back("512");
		HoriPixelValues.push_back("1024");
		HoriPixelValues.push_back("2048");
		HoriPixelValues.push_back("4096");
		ret = SetAllowedValues("Horizontal Pixels", HoriPixelValues);
				if (DEVICE_OK != ret)
        return ret;

/*		ret = CreateProperty("Vertical Pixels Max", "3536" , MM::Integer, true);
		assert(DEVICE_OK == ret);
		ret = CreateProperty("Vertical Pixels Min", "442" , MM::Integer, true);
		assert(DEVICE_OK == ret);
		ret = CreateProperty("Horizontal Pixels Max", "4096" , MM::Integer, true);
		assert(DEVICE_OK == ret);
		ret = CreateProperty("Horizontal Pixels Min", "512" , MM::Integer, true);
		assert(DEVICE_OK == ret);*/
		
		hr = pIScanControl->get_NrOfVertPixelsRange((XTLibRange)XTLIB_RANGE_MIN, &lmin);
		if(hr != S_OK)
		{
			hub->LogFEIError(hr);
		}
		hr = pIScanControl->get_NrOfVertPixelsRange((XTLibRange)XTLIB_RANGE_MAX, &lmax);
		if(hr != S_OK)
		{
			hub->LogFEIError(hr);
		}
		ret = SetPropertyLimits("Vertical Pixels", static_cast<double>(lmin), static_cast<double>(lmax));
				assert(nRet == DEVICE_OK);
        
		hr = pIScanControl->get_NrOfHorPixelsRange((XTLibRange)XTLIB_RANGE_MIN, &lmin);
		if(hr != S_OK)
		{
			hub->LogFEIError(hr);
		}
		hr = pIScanControl->get_NrOfHorPixelsRange((XTLibRange)XTLIB_RANGE_MAX, &lmax);
		if(hr != S_OK)
		{
			hub->LogFEIError(hr);
		}
		ret = SetPropertyLimits("Horizontal Pixels", static_cast<double>(lmin), static_cast<double>(lmax));
				assert(nRet == DEVICE_OK);
        

		pAct = new CPropertyAction (this, &FEISEMController::OnSelectedAreaXStart);
		ret = CreateProperty("Selected Area X Start", "0" , MM::Integer, false, pAct);
				assert(nRet == DEVICE_OK);
        
		pAct = new CPropertyAction (this, &FEISEMController::OnSelectedAreaYStart);
		ret = CreateProperty("Selected Area Y Start", "0" , MM::Integer, false, pAct);
				assert(nRet == DEVICE_OK);
        
		pAct = new CPropertyAction (this, &FEISEMController::OnSelectedAreaX);
		ret = CreateProperty("Selected Area X Size", "512" , MM::Integer, false, pAct);
				assert(nRet == DEVICE_OK);
        
		pAct = new CPropertyAction (this, &FEISEMController::OnSelectedAreaY);
		ret = CreateProperty("Selected Area Y Size", "442" , MM::Integer, false, pAct);
				assert(nRet == DEVICE_OK);
        
		//512 x 442
		pAct = new CPropertyAction (this, &FEISEMController::OnFOV);
		ret = CreateProperty("FOV X (um)", "1" , MM::Float, false, pAct);
				assert(nRet == DEVICE_OK);
        
		pAct = new CPropertyAction (this, &FEISEMController::OnFOV);
		ret = CreateProperty("FOV Y (um)", "0.86328125" , MM::Float, true, pAct);
				assert(nRet == DEVICE_OK);
        
/*		ret = CreateProperty("FOV X Min", "0.0" , MM::Float, true);
		assert(DEVICE_OK == ret);
		ret = CreateProperty("FOV Y Min", "0.0" , MM::Float, true);
		assert(DEVICE_OK == ret);
		ret = CreateProperty("FOV X Max", "0.0" , MM::Float, true);
		assert(DEVICE_OK == ret);
		ret = CreateProperty("FOV Y Max", "0.0" , MM::Float, true);
		assert(DEVICE_OK == ret);*/

		hr = pIScanControl->get_ScanFieldXRange((XTLibRange)XTLIB_RANGE_MIN, &min);
		if(hr != S_OK)
		{
			hub->LogFEIError(hr);
		}
		hr = pIScanControl->get_ScanFieldXRange((XTLibRange)XTLIB_RANGE_MAX, &max);
		if(hr != S_OK)
		{
			hub->LogFEIError(hr);
		}
		ret = SetPropertyLimits("FOV X (um)", min*g_DistanceMultiplier, max*g_DistanceMultiplier);
				assert(nRet == DEVICE_OK);
        
		hr = pIScanControl->get_ScanFieldYRange((XTLibRange)XTLIB_RANGE_MIN, &min);
		if(hr != S_OK)
		{
			hub->LogFEIError(hr);
		}
		hr = pIScanControl->get_ScanFieldYRange((XTLibRange)XTLIB_RANGE_MAX, &max);
		if(hr != S_OK)
		{
			hub->LogFEIError(hr);
		}
		ret=SetPropertyLimits("FOV Y (um)", min*g_DistanceMultiplier, max*g_DistanceMultiplier);
				assert(nRet == DEVICE_OK);
        
		pAct = new CPropertyAction (this, &FEISEMController::OnRotation);
		ret = CreateProperty("Rotation", "0.0" , MM::Float, false, pAct);
				assert(nRet == DEVICE_OK);
        
/*		ret = CreateProperty("Rotation Min", "0.0" , MM::Float, true);
		assert(DEVICE_OK == ret);
		ret = CreateProperty("Rotation Max", "0.0" , MM::Float, true);
		assert(DEVICE_OK == ret);*/

		hr = pIScanControl->get_RotationRange((XTLibRange)XTLIB_RANGE_MIN, &min);
		if(hr != S_OK)
		{
			hub->LogFEIError(hr);
		}
		hr = pIScanControl->get_RotationRange((XTLibRange)XTLIB_RANGE_MAX, &max);
		if(hr != S_OK)
		{
			hub->LogFEIError(hr);
		}
		ret=SetPropertyLimits("Rotation", min*g_ScanRotationMultiplier, max*g_ScanRotationMultiplier);
				assert(nRet == DEVICE_OK);
        
		pAct = new CPropertyAction (this, &FEISEMController::OnSelectedEnabled);
		ret = CreateProperty("Selected Area Enabled", "false" , MM::String, false, pAct);
				assert(nRet == DEVICE_OK);
        

		ret = SetAllowedValues("Selected Area Enabled", BOOLOPT);
		if (DEVICE_OK != ret)
        return ret;
		pAct = new CPropertyAction (this, &FEISEMController::OnSpotX);
		ret = CreateProperty("Spot X", "256" , MM::Integer, false, pAct);
				assert(nRet == DEVICE_OK);
        
		pAct = new CPropertyAction (this, &FEISEMController::OnSpotY);
		ret = CreateProperty("Spot Y", "221" , MM::Integer, false, pAct);
				assert(nRet == DEVICE_OK);
        
		//ElectronBeamControl	
		pAct = new CPropertyAction (this, &FEISEMController::OnHTState);
		ret = CreateProperty("HTState", "unknown" , MM::String, false, pAct);
				assert(nRet == DEVICE_OK);
        

		vector<string> HTStateValues;
		HTStateValues.push_back(g_HTSTATE_ON);
		HTStateValues.push_back(g_HTSTATE_OFF);
		ret = SetAllowedValues("HTState", HTStateValues);
				assert(nRet == DEVICE_OK);
        
		 
		pAct = new CPropertyAction (this, &FEISEMController::OnAcceleratingVoltage);
		ret = CreateProperty("Acceleration Voltage (V)", "20000.0" , MM::Float, false, pAct);
				assert(nRet == DEVICE_OK);
        
		pAct = new CPropertyAction (this, &FEISEMController::OnSpotSize);
		ret = CreateProperty("Spot Size", "2.0" , MM::Float, false, pAct);
				assert(nRet == DEVICE_OK);
        
	/*	ret = CreateProperty("Spot Min", "1.0" , MM::Float, true);
		assert(DEVICE_OK == ret);
		ret = CreateProperty("Spot Max", "7.0" , MM::Float, true);
		assert(DEVICE_OK == ret);*/

		hr = pIElectronBeamControl->get_SpotSizeRange((XTLibRange)XTLIB_RANGE_MIN, &min);
		if(hr != S_OK)
		{
			hub->LogFEIError(hr);
		}
		hr = pIElectronBeamControl->get_SpotSizeRange((XTLibRange)XTLIB_RANGE_MAX, &max);
		if(hr != S_OK)
		{
			hub->LogFEIError(hr);
		}
		ret = SetPropertyLimits("Spot Size", min, max);
				assert(nRet == DEVICE_OK);
        
		pAct = new CPropertyAction (this, &FEISEMController::OnBeamBlank);
		ret = CreateProperty("Beam Blank", "false" , MM::String, false, pAct);
				assert(nRet == DEVICE_OK);
        

		ret = SetAllowedValues("Beam Blank", BOOLOPT);
				if (DEVICE_OK != ret)
        return ret;
		pAct = new CPropertyAction (this, &FEISEMController::OnStigmationX);
		ret = CreateProperty("Stigmation X", "0.0" , MM::Float, false, pAct);
				assert(nRet == DEVICE_OK);
        
		pAct = new CPropertyAction (this, &FEISEMController::OnStigmationY);
		ret = CreateProperty("Stigmation Y", "0.0" , MM::Float, false, pAct);
				assert(nRet == DEVICE_OK);
        
	/*	ret = CreateProperty("Stigmation X Min", "0.0" , MM::Float, true);
		assert(DEVICE_OK == ret);
		ret = CreateProperty("Stigmation Y Min", "0.0" , MM::Float, true);
		assert(DEVICE_OK == ret);
		ret = CreateProperty("Stigmation X Max", "0.0" , MM::Float, true);
		assert(DEVICE_OK == ret);
		ret = CreateProperty("Stigmation Y Max", "0.0" , MM::Float, true);
		assert(DEVICE_OK == ret);*/

		hr = pIElectronBeamControl->get_StigmatorXRange((XTLibRange)XTLIB_RANGE_MIN, &min);
		if(hr != S_OK)
		{
			hub->LogFEIError(hr);
		}
		hr = pIElectronBeamControl->get_StigmatorXRange((XTLibRange)XTLIB_RANGE_MAX, &max);
		if(hr != S_OK)
		{
			hub->LogFEIError(hr);
		}
		ret = SetPropertyLimits("Stigmation X", min*g_StigmationMultiplier, max*g_StigmationMultiplier);
				assert(nRet == DEVICE_OK);
        
		hr = pIElectronBeamControl->get_StigmatorYRange((XTLibRange)XTLIB_RANGE_MIN, &min);
		if(hr != S_OK)
		{
			hub->LogFEIError(hr);
		}
		hr = pIElectronBeamControl->get_StigmatorYRange((XTLibRange)XTLIB_RANGE_MAX, &max);
		if(hr != S_OK)
		{
			hub->LogFEIError(hr);
		}
        ret = SetPropertyLimits("Stigmation Y", min*g_StigmationMultiplier, max*g_StigmationMultiplier);
				assert(nRet == DEVICE_OK);
        
		//BeamControl
		pAct = new CPropertyAction (this, &FEISEMController::OnBeamShiftX);
		ret = CreateProperty("Beam Shift X", "0.0" , MM::Float, false, pAct);
				assert(nRet == DEVICE_OK);
        
		pAct = new CPropertyAction (this, &FEISEMController::OnBeamShiftY);
		ret = CreateProperty("Beam Shift Y", "0.0" , MM::Float, false, pAct);
				assert(nRet == DEVICE_OK);
        
	/*	ret = CreateProperty("Beam Shift X Min", "0.0" , MM::Float, true);
		assert(DEVICE_OK == ret);
		ret = CreateProperty("Beam Shift Y Min", "0.0" , MM::Float, true);
		assert(DEVICE_OK == ret);
		ret = CreateProperty("Beam Shift X Max", "0.0" , MM::Float, true);
		assert(DEVICE_OK == ret);
		ret = CreateProperty("Beam Shift Y Max", "0.0" , MM::Float, true);
		assert(DEVICE_OK == ret);*/
		
		hr = pIBeamControl->get_BeamShiftXRange((XTLibRange)XTLIB_RANGE_MIN, &min);
		if(hr != S_OK)
		{
			hub->LogFEIError(hr);
		}
		hr = pIBeamControl->get_BeamShiftXRange((XTLibRange)XTLIB_RANGE_MAX, &max);
		if(hr != S_OK)
		{
			hub->LogFEIError(hr);
		}
		ret = SetPropertyLimits("Beam Shift X", min*g_BeamShiftMultiplier, max*g_BeamShiftMultiplier);
				assert(nRet == DEVICE_OK);
        
		hr = pIBeamControl->get_BeamShiftYRange((XTLibRange)XTLIB_RANGE_MIN, &min);
		if(hr != S_OK)
		{
			hub->LogFEIError(hr);
		}
		hr = pIBeamControl->get_BeamShiftYRange((XTLibRange)XTLIB_RANGE_MAX, &max);
		if(hr != S_OK)
		{
			hub->LogFEIError(hr);
		}
		ret = SetPropertyLimits("Beam Shift Y", min*g_BeamShiftMultiplier, max*g_BeamShiftMultiplier);
				assert(nRet == DEVICE_OK);
        

		pAct = new CPropertyAction (this, &FEISEMController::OnSaveImage);
		ret = CreateProperty("Save Image", "false" , MM::String, false, pAct);
				assert(nRet == DEVICE_OK);
        

		ret = SetAllowedValues("Save Image", BOOLOPT);
				if (DEVICE_OK != ret)
        return ret;
		ret = CreateProperty("Image Filename", "D:\\Imaging\\MMSave\\filename" , MM::String, false);
				assert(nRet == DEVICE_OK);
        
		

				ret = CreateProperty("Image Counter", "0" , MM::Integer, false);
				assert(nRet == DEVICE_OK);
        

  	initialized_ = true;
	return DEVICE_OK;
}



int FEISEMController::OnPressure(MM::PropertyBase* pProp, MM::ActionType eAct)
{
	HRESULT hresult;
	double pressure;
   if (eAct == MM::BeforeGet)
   {
	  
	  hresult = pIVacSystemControl->get_Pressure(&pressure);
	  if(hresult == S_OK)
	  {
		pProp->Set(pressure);
	  }
	  else
	  {
		  hub->LogFEIError(hresult);
	  }
   }
   else if (eAct == MM::AfterSet)
   {
      pProp->Get(pressure);
	  
	  if(pressure > pProp->GetUpperLimit())
	  {
		  pressure = pProp->GetUpperLimit();
	  }
	  else if(pressure < pProp->GetLowerLimit())
	  {
		  pressure = pProp->GetLowerLimit();
	  }
	  hresult = pIVacSystemControl->put_Pressure(pressure);
	  if(hresult == S_OK)
	  {
	  }
	  else
	  {	
		   hub->LogFEIError(hresult);
	  }
   }
   return DEVICE_OK;
}
int FEISEMController::OnChamberState(MM::PropertyBase* pProp, MM::ActionType eAct)
{
	HRESULT hresult;
	XTLibVacuumUserState state; 
   if (eAct == MM::BeforeGet)
   {
	   hresult = pIVacSystemControl->get_State(&state);	  
	  if(hresult == S_OK)
	  {
		switch (state) {
		  case XTLIB_VACUUM_USER_STATE_ALL_AIR:
			  pProp->Set(g_STATE_ALL_AIR);
			  break;

		  case XTLIB_VACUUM_USER_STATE_PUMPING: 
			  pProp->Set(g_STATE_PUMPING);
			  break;

		  case XTLIB_VACUUM_USER_STATE_PREVAC: 
			  pProp->Set(g_STATE_PREVAC);
			  break;
		  case XTLIB_VACUUM_USER_STATE_VACUUM:
			  pProp->Set(g_STATE_VACUUM);
			  break;
		  case XTLIB_VACUUM_USER_STATE_VENTING: 
			  pProp->Set(g_STATE_VENTING);
			  break;
		  case XTLIB_VACUUM_USER_STATE_ERROR: 
			  pProp->Set(g_STATE_ERROR);
			  break;
		  default:pProp->Set(g_STATE_ERROR);
		}
	  }
	  else
	  {
		  hub->LogFEIError(hresult);
	  }
   }
   return DEVICE_OK;
}
int FEISEMController::OnChamberMode(MM::PropertyBase* pProp, MM::ActionType eAct)
{
	HRESULT hresult;
	XTLibVacuumUserMode mode; 
   if (eAct == MM::BeforeGet)
   {
	   hresult = pIVacSystemControl->get_Mode(&mode);	  
	  if(hresult == S_OK)
	  {
		switch (mode) {
		  case XTLIB_VACUUMUSER_MODE_HIVAC:
			  pProp->Set(g_MODE_HIVAC);
			  break;

		  case XTLIB_VACUUMUSER_MODE_CHARGENEUT: 
			  pProp->Set(g_MODE_CHARGENEUT);
			  break;

		  case XTLIB_VACUUMUSER_MODE_ENVIRONMENTAL: 
			  pProp->Set(g_MODE_ENVIRONMENTAL);
			  break;
		  default:pProp->Set(g_MODE_ENVIRONMENTAL);
		}
	  }
	  else
	  {
		  hub->LogFEIError(hresult);
	  }
   }
   return DEVICE_OK;
}
int FEISEMController::OnScanMode(MM::PropertyBase* pProp, MM::ActionType eAct)
{
	  HRESULT hresult;
	  XTLibScanMode mode;
	  string modestring;
   if (eAct == MM::BeforeGet)
   {
	  hresult = pIScanControl->get_ScanMode(&mode);	  
	  if(hresult == S_OK)
	  {
		  switch (mode) 
		  {
		  case XTLIB_SCAN_NONE:
				pProp->Set(g_SCAN_NONE);
			  break;

		  case XTLIB_SCAN_EXTERNAL: 
			  pProp->Set(g_SCAN_EXTERNAL);
			  break;

		  case XTLIB_SCAN_FULLFRAME: 
			  pProp->Set(g_SCAN_FULLFRAME);
			  break;

		  case XTLIB_SCAN_SPOT:
			  pProp->Set(g_SCAN_SPOT);
			  break;

		  case XTLIB_SCAN_LINE: 
			  pProp->Set(g_SCAN_LINE);
			  break;
		  default:pProp->Set(g_SCAN_LINE);
		  }
	  }
	  else
	  {
		  hub->LogFEIError(hresult);
	  }
   }
   else if (eAct == MM::AfterSet)
   {
      pProp->Get(modestring);
	  if(modestring == g_SCAN_NONE)
	  {
			hresult = pIScanControl->put_ScanMode(XTLIB_SCAN_NONE);
	  }
	  else if(modestring == g_SCAN_EXTERNAL)
	  {
			hresult = pIScanControl->put_ScanMode(XTLIB_SCAN_EXTERNAL);
	  }
	  else if(modestring == g_SCAN_FULLFRAME)
	  {
			hresult = pIScanControl->put_ScanMode(XTLIB_SCAN_FULLFRAME);
	  }
	  else if(modestring == g_SCAN_SPOT)
	  {
			hresult = pIScanControl->put_ScanMode(XTLIB_SCAN_SPOT);
	  }
	  else if(modestring == g_SCAN_LINE)
	  {
			hresult = pIScanControl->put_ScanMode(XTLIB_SCAN_LINE);
	  }
	  if(hresult == S_OK)
	  {
		  
	  }
	  else
	  {	
		   hub->LogFEIError(hresult);
	  }
   }
   return DEVICE_OK;
}
int FEISEMController::OnDwellTime(MM::PropertyBase* pProp, MM::ActionType eAct)
{
	HRESULT hresult;
	double dwelltime;
   if (eAct == MM::BeforeGet)
   {
	  hresult = pIScanControl->get_DwellTime(&dwelltime);
	  if(hresult == S_OK)
	  {
		pProp->Set(dwelltime*g_TimeMultiplier);
	  }
	  else
	  {
		  hub->LogFEIError(hresult);
	  }
   }
   else if (eAct == MM::AfterSet)
   {
	   pProp->Get(dwelltime);
	   dwelltime= dwelltime/g_TimeMultiplier;
/*	   if(dwelltime > pProp->GetUpperLimit())
	  {
		  dwelltime = pProp->GetUpperLimit();
	  }
	  else if(dwelltime < pProp->GetLowerLimit())
	  {
		  dwelltime = pProp->GetLowerLimit();
	  }*/
	   hresult = pIScanControl->put_DwellTime(dwelltime);
	   if(hresult == S_OK)
	   {
	   }
	   else
	   {	
		   hub->LogFEIError(hresult);
	   }
   }
   return DEVICE_OK;
}
int FEISEMController::OnHPixels(MM::PropertyBase* pProp, MM::ActionType eAct)
{
	HRESULT hresult;
	long Pixels;
   if (eAct == MM::BeforeGet)
   {
	  
	  hresult = pIScanControl->get_NrOfHorPixels(&Pixels);
	  if(hresult == S_OK)
	  {
		pProp->Set(Pixels);
	  }
	  else
	  {
		  hub->LogFEIError(hresult);
	  }
   }
   else if (eAct == MM::AfterSet)
   {
      pProp->Get(Pixels);
	  hresult = pIScanControl->put_NrOfHorPixels(Pixels);
	  if(hresult == S_OK)
	  {
	  }
	  else
	  {	
		   hub->LogFEIError(hresult);
	  }
   }
   return DEVICE_OK;
}
int FEISEMController::OnVPixels(MM::PropertyBase* pProp, MM::ActionType eAct)
{
	HRESULT hresult;
	long Pixels;
   if (eAct == MM::BeforeGet)
   {
	  
	  hresult = pIScanControl->get_NrOfVertPixels(&Pixels);
	  if(hresult == S_OK)
	  {
		pProp->Set(Pixels);
	  }
	  else
	  {
		  hub->LogFEIError(hresult);
	  }
   }
   else if (eAct == MM::AfterSet)
   {
      pProp->Get(Pixels);
	  hresult = pIScanControl->put_NrOfVertPixels(Pixels);
	  if(hresult == S_OK)
	  {
	  }
	  else
	  {	
		   hub->LogFEIError(hresult);
	  }
   }
   return DEVICE_OK;
}
int FEISEMController::OnSelectedAreaX(MM::PropertyBase* pProp, MM::ActionType eAct)
{
	HRESULT hresult;
	long XStart, YStart, X, Y;
   if (eAct == MM::BeforeGet)
   {	  
	  hresult = pIScanControl->GetSelectedArea(&XStart, &YStart, &X, &Y);
	  if(hresult == S_OK)
	  {
		pProp->Set(X);
	  }
	  else
	  {
		  hub->LogFEIError(hresult);
	  }
   }
   else if (eAct == MM::AfterSet)
   {
		hresult = pIScanControl->GetSelectedArea(&XStart, &YStart, &X, &Y);
		if(hresult == S_OK)
		{
			pProp->Get(X);
			hresult = pIScanControl->SetSelectedArea(XStart, YStart, X, Y);
			if(hresult == S_OK)
			{
			}
			else
			{	
				hub->LogFEIError(hresult);
			}
	  }
	  else
	  {	
		   hub->LogFEIError(hresult);
	  }
   }
   return DEVICE_OK;
}
int FEISEMController::OnSelectedAreaY(MM::PropertyBase* pProp, MM::ActionType eAct)
{
	HRESULT hresult;
	long XStart, YStart, X, Y;
   if (eAct == MM::BeforeGet)
   {	  
	  hresult = pIScanControl->GetSelectedArea(&XStart, &YStart, &X, &Y);
	  if(hresult == S_OK)
	  {
		pProp->Set(Y);
	  }
	  else
	  {
		  hub->LogFEIError(hresult);
	  }
   }
   else if (eAct == MM::AfterSet)
   {
		hresult = pIScanControl->GetSelectedArea(&XStart, &YStart, &X, &Y);
		if(hresult == S_OK)
		{
			pProp->Get(Y);
			hresult = pIScanControl->SetSelectedArea(XStart, YStart, X, Y);
			if(hresult == S_OK)
			{
			}
			else
			{	
				hub->LogFEIError(hresult);
			}
	  }
	  else
	  {	
		   hub->LogFEIError(hresult);
	  }
   }
   return DEVICE_OK;
}
int FEISEMController::OnSelectedAreaXStart(MM::PropertyBase* pProp, MM::ActionType eAct)
{
	HRESULT hresult;
	long XStart, YStart, X, Y;
   if (eAct == MM::BeforeGet)
   {	  
	  hresult = pIScanControl->GetSelectedArea(&XStart, &YStart, &X, &Y);
	  if(hresult == S_OK)
	  {
		pProp->Set(XStart);
	  }
	  else
	  {
		  hub->LogFEIError(hresult);
	  }
   }
   else if (eAct == MM::AfterSet)
   {
		hresult = pIScanControl->GetSelectedArea(&XStart, &YStart, &X, &Y);
		if(hresult == S_OK)
		{
			pProp->Get(XStart);
			hresult = pIScanControl->SetSelectedArea(XStart, YStart, X, Y);
			if(hresult == S_OK)
			{
			}
			else
			{	
				hub->LogFEIError(hresult);
			}
	  }
	  else
	  {	
		   hub->LogFEIError(hresult);
	  }
   }
   return DEVICE_OK;
}
int FEISEMController::OnSelectedAreaYStart(MM::PropertyBase* pProp, MM::ActionType eAct)
{
	HRESULT hresult;
	long XStart, YStart, X, Y;
   if (eAct == MM::BeforeGet)
   {	  
	  hresult = pIScanControl->GetSelectedArea(&XStart, &YStart, &X, &Y);
	  if(hresult == S_OK)
	  {
		pProp->Set(YStart);
	  }
	  else
	  {
		  hub->LogFEIError(hresult);
	  }
   }
   else if (eAct == MM::AfterSet)
   {
		hresult = pIScanControl->GetSelectedArea(&XStart, &YStart, &X, &Y);
		if(hresult == S_OK)
		{
			pProp->Get(YStart);
			hresult = pIScanControl->SetSelectedArea(XStart, YStart, X, Y);
			if(hresult == S_OK)
			{
			}
			else
			{	
				hub->LogFEIError(hresult);
			}
	  }
	  else
	  {	
		   hub->LogFEIError(hresult);
	  }
   }
   return DEVICE_OK;
}
int FEISEMController::OnFOV(MM::PropertyBase* pProp, MM::ActionType eAct)
{
HRESULT hresult;
	double XFOV, YFOV;
   if (eAct == MM::BeforeGet)
   {
	  
	  hresult = pIScanControl->GetScanFieldSize(&XFOV, &YFOV);
	  if(hresult == S_OK)
	  {
		pProp->Set(XFOV*g_DistanceMultiplier);
	  }
	  else
	  {
		  hub->LogFEIError(hresult);
	  }
   }
   else if (eAct == MM::AfterSet)
   {
      pProp->Get(XFOV);
	  
	  if(XFOV > pProp->GetUpperLimit())
	  {
		  XFOV = pProp->GetUpperLimit();
	  }
	  else if(XFOV < pProp->GetLowerLimit())
	  {
		  XFOV = pProp->GetLowerLimit();
	  }
	  hresult = pIScanControl->SetScanFieldSize(XFOV/g_DistanceMultiplier, (0.86328125*XFOV)/g_DistanceMultiplier);
	  if(hresult == S_OK)
	  {
	  }
	  else
	  {	
		   hub->LogFEIError(hresult);
	  }
   }
   return DEVICE_OK;
}
int FEISEMController::OnRotation(MM::PropertyBase* pProp, MM::ActionType eAct)
{
	HRESULT hresult;
	double rotation;
   if (eAct == MM::BeforeGet)
   {
	  
	  hresult = pIScanControl->get_Rotation(&rotation);
	  if(hresult == S_OK)
	  {
		pProp->Set(rotation*g_ScanRotationMultiplier);
	  }
	  else
	  {
		  hub->LogFEIError(hresult);
	  }
   }
   else if (eAct == MM::AfterSet)
   {
      pProp->Get(rotation);
	  if(rotation > pProp->GetUpperLimit())
	  {
		  rotation = pProp->GetUpperLimit();
	  }
	  else if(rotation < pProp->GetLowerLimit())
	  {
		  rotation = pProp->GetLowerLimit();
	  }
	  hresult = pIScanControl->put_Rotation(rotation/g_ScanRotationMultiplier);
	  if(hresult == S_OK)
	  {
	  }
	  else
	  {	
		   hub->LogFEIError(hresult);
	  }
   }
   return DEVICE_OK;
}
int FEISEMController::OnSelectedEnabled(MM::PropertyBase* pProp, MM::ActionType eAct)
{
	VARIANT_BOOL enabled;
	string stringenabled;
	HRESULT hresult;
	
   if (eAct == MM::BeforeGet)
   {
	  
	  hresult = pIScanControl->get_SelectedAreaState(&enabled);
	  if(hresult == S_OK)
	  {
		if(enabled == 0)	
		{
			pProp->Set("false");
		}
		else
		{
			pProp->Set("true");
		}
		
	  }
	  else
	  {
		  hub->LogFEIError(hresult);
	  }
   }
   else if (eAct == MM::AfterSet)
   {
      pProp->Get(stringenabled);
	  if(stringenabled == "true")
	  {
		  enabled = 1;
	  }
	  else
	  {
		  enabled = 0;
	  }
	  hresult = pIScanControl->put_SelectedAreaState(enabled);
	  if(hresult == S_OK)
	  {
		  
	  }
	  else
	  {	
		   hub->LogFEIError(hresult);
	  }
   }
   return DEVICE_OK;
}
int FEISEMController::OnSpotX(MM::PropertyBase* pProp, MM::ActionType eAct)
{
	HRESULT hresult;
	long X,Y;
   if (eAct == MM::BeforeGet)
   {
	  
	  hresult = pIScanControl->GetSpot(&X, &Y);
	  if(hresult == S_OK)
	  {
		pProp->Set(X);
	  }
	  else
	  {
		  hub->LogFEIError(hresult);
	  }
   }
   else if (eAct == MM::AfterSet)
   {
	  hresult = pIScanControl->GetSpot(&X, &Y);
	  if(hresult == S_OK)
	  {
		pProp->Get(X);
		hresult = pIScanControl->SetSpot(X, Y);
		if(hresult == S_OK)
		{
		}
	  else
			{	
			hub->LogFEIError(hresult);
			}
	  }
	  else
	  {	
		   hub->LogFEIError(hresult);
	  }
	  
   }
   return DEVICE_OK;
}
int FEISEMController::OnSpotY(MM::PropertyBase* pProp, MM::ActionType eAct)
{
	HRESULT hresult;
	long X,Y;
   if (eAct == MM::BeforeGet)
   {
	  
	  hresult = pIScanControl->GetSpot(&X, &Y);
	  if(hresult == S_OK)
	  {
		pProp->Set(Y);
	  }
	  else
	  {
		  hub->LogFEIError(hresult);
	  }
   }
   else if (eAct == MM::AfterSet)
   {
	  hresult = pIScanControl->GetSpot(&X, &Y);
	  if(hresult == S_OK)
	  {
		pProp->Get(Y);
		hresult = pIScanControl->SetSpot(X, Y);
		if(hresult == S_OK)
		{
		}
	  else
			{	
			hub->LogFEIError(hresult);
			}
	  }
	  else
	  {	
		   hub->LogFEIError(hresult);
	  }
	  
   }
   return DEVICE_OK;
}
int FEISEMController::OnHTState(MM::PropertyBase* pProp, MM::ActionType eAct)
{
HRESULT hresult;
	  HTState state;
	  string statestring;
   if (eAct == MM::BeforeGet)
   {
	  hresult = pIElectronBeamControl->get_HTState(&state);	  
	  if(hresult == S_OK)
	  {
		  switch (state) 
		  {
		  case HT_ON:
				pProp->Set(g_HTSTATE_ON);
			  break;

		  case HT_OFF: 
			  pProp->Set(g_HTSTATE_OFF);
			  break;

		  case HT_RAMPING_UP: 
			  pProp->Set(g_HTSTATE_RAMPING_UP);
			  break;

		  case HT_RAMPING_DOWN:
			  pProp->Set(g_HTSTATE_RAMPING_DOWN);
			  break;

		  case HT_INITIALIZING: 
			  pProp->Set(g_HTSTATE_INITIALIZING);
			  break;
		  default:pProp->Set("Unknown");
		  }
	  }
	  else
	  {
		  hub->LogFEIError(hresult);
	  }
   }
   else if (eAct == MM::AfterSet)
   {
      pProp->Get(statestring);
	  if(statestring == g_HTSTATE_ON)
	  {
			hresult = pIElectronBeamControl->put_HTOnOff((short)(1));
	  }
	  else if(statestring == g_HTSTATE_OFF)
	  {
			hresult = pIElectronBeamControl->put_HTOnOff((short)(0));
	  }
	  if(hresult == S_OK)
	  {
		  
	  }
	  else
	  {	
		   hub->LogFEIError(hresult);
	  }
   }
   return DEVICE_OK;
}
int FEISEMController::OnAcceleratingVoltage(MM::PropertyBase* pProp, MM::ActionType eAct)
{
	HRESULT hresult;
	double voltage;
   if (eAct == MM::BeforeGet)
   {
	  
	  hresult = pIElectronBeamControl->get_HTVoltage(&voltage);
	  if(hresult == S_OK)
	  {
		pProp->Set(voltage);
	  }
	  else
	  {
		  hub->LogFEIError(hresult);
	  }
   }
   else if (eAct == MM::AfterSet)
   {
      pProp->Get(voltage);
	  hresult = pIElectronBeamControl->put_HTVoltage(voltage);
	  if(hresult == S_OK)
	  {
	  }
	  else
	  {	
		   hub->LogFEIError(hresult);
	  }
   }
   return DEVICE_OK;
}
int FEISEMController::OnSpotSize(MM::PropertyBase* pProp, MM::ActionType eAct)
{
	HRESULT hresult;
	double spotsize;
   if (eAct == MM::BeforeGet)
   {
	  
	  hresult = pIElectronBeamControl->get_SpotSize(&spotsize);
	  if(hresult == S_OK)
	  {
		pProp->Set(spotsize);
	  }
	  else
	  {
		  hub->LogFEIError(hresult);
	  }
   }
   else if (eAct == MM::AfterSet)
   {
      pProp->Get(spotsize);
	  hresult = pIElectronBeamControl->put_SpotSize(spotsize);
	  if(hresult == S_OK)
	  {
	  }
	  else
	  {	
		   hub->LogFEIError(hresult);
	  }
   }
   return DEVICE_OK;
}
int FEISEMController::OnBeamBlank(MM::PropertyBase* pProp, MM::ActionType eAct)
{
VARIANT_BOOL enabled;
	string stringenabled;
	HRESULT hresult;
	
   if (eAct == MM::BeforeGet)
   {
	  
	  hresult = pIElectronBeamControl->get_BeamIsBlanked(&enabled);
	  if(hresult == S_OK)
	  {
		if(enabled == 0)	
		{
			pProp->Set("false");
			
		}
		else
		{
			pProp->Set("true");
		}
	  }
	  else
	  {
		  hub->LogFEIError(hresult);
	  }
   }
   else if (eAct == MM::AfterSet)
   {
      pProp->Get(stringenabled);
	  if(stringenabled == "true")
	  {
		  enabled = 1;
	  }
	  else
	  {
		  enabled = 0;
	  }
	  hresult = pIElectronBeamControl->put_BeamBlank(enabled);
	  if(hresult == S_OK)
	  {
		  
	  }
	  else
	  {	
		   hub->LogFEIError(hresult);
	  }
   }
   return DEVICE_OK;
}
int FEISEMController::OnStigmationX(MM::PropertyBase* pProp, MM::ActionType eAct)
{
HRESULT hresult;
	double X,Y;
   if (eAct == MM::BeforeGet)
   {
	  
	  hresult = pIElectronBeamControl->GetStigmator(&X, &Y);
	  if(hresult == S_OK)
	  {
		pProp->Set(X*g_StigmationMultiplier);
	  }
	  else
	  {
		  hub->LogFEIError(hresult);
	  }
   }
   else if (eAct == MM::AfterSet)
   {
	  hresult = pIElectronBeamControl->GetStigmator(&X, &Y);
	  if(hresult == S_OK)
	  {
		pProp->Get(X);
		X = X/g_StigmationMultiplier;
			  if(X > pProp->GetUpperLimit())
	  {
		  X = pProp->GetUpperLimit();
	  }
	  else if(X < pProp->GetLowerLimit())
	  {
		  X = pProp->GetLowerLimit();
	  }
		hresult = pIElectronBeamControl->SetStigmator(X, Y);
		if(hresult == S_OK)
		{
		}
	  else
			{	
			hub->LogFEIError(hresult);
			}
	  }
	  else
	  {	
		   hub->LogFEIError(hresult);
	  }
	  
   }
   return DEVICE_OK;
}
int FEISEMController::OnStigmationY(MM::PropertyBase* pProp, MM::ActionType eAct)
{
HRESULT hresult;
	double X,Y;
   if (eAct == MM::BeforeGet)
   {
	  
	  hresult = pIElectronBeamControl->GetStigmator(&X, &Y);
	  if(hresult == S_OK)
	  {
		pProp->Set(Y*g_StigmationMultiplier);
	  }
	  else
	  {
		  hub->LogFEIError(hresult);
	  }
   }
   else if (eAct == MM::AfterSet)
   {
	  hresult = pIElectronBeamControl->GetStigmator(&X, &Y);
	  if(hresult == S_OK)
	  {
		pProp->Get(Y);
		Y = Y/g_StigmationMultiplier;
		if(Y > pProp->GetUpperLimit())
	  {
		  Y = pProp->GetUpperLimit();
	  }
	  else if(Y < pProp->GetLowerLimit())
	  {
		  Y = pProp->GetLowerLimit();
	  }
		hresult = pIElectronBeamControl->SetStigmator(X, Y);
		if(hresult == S_OK)
		{
		}
	  else
			{	
			hub->LogFEIError(hresult);
			}
	  }
	  else
	  {	
		   hub->LogFEIError(hresult);
	  }
	  
   }
   return DEVICE_OK;
}
int FEISEMController::OnBeamShiftX(MM::PropertyBase* pProp, MM::ActionType eAct)
{
HRESULT hresult;
	double X,Y;
   if (eAct == MM::BeforeGet)
   {
	  
	  hresult = pIBeamControl->GetBeamShift(&X, &Y);
	  if(hresult == S_OK)
	  {
		pProp->Set(X*g_BeamShiftMultiplier);
	  }
	  else
	  {
		  hub->LogFEIError(hresult);
	  }
   }
   else if (eAct == MM::AfterSet)
   {
	  hresult = pIBeamControl->GetBeamShift(&X, &Y);
	  if(hresult == S_OK)
	  {
		pProp->Get(X);
		X = X/g_BeamShiftMultiplier;
		if(X > pProp->GetUpperLimit())
	  {
		  X = pProp->GetUpperLimit();
	  }
	  else if(X < pProp->GetLowerLimit())
	  {
		  X = pProp->GetLowerLimit();
	  }
		hresult = pIBeamControl->SetBeamShift(X, Y);
		if(hresult == S_OK)
		{
		}
	  else
			{	
			hub->LogFEIError(hresult);
			}
	  }
	  else
	  {	
		   hub->LogFEIError(hresult);
	  }
	  
   }
   return DEVICE_OK;
}
int FEISEMController::OnBeamShiftY(MM::PropertyBase* pProp, MM::ActionType eAct)
{
HRESULT hresult;
	double X,Y;
   if (eAct == MM::BeforeGet)
   {
	  
	  hresult = pIBeamControl->GetBeamShift(&X, &Y);
	  if(hresult == S_OK)
	  {
		pProp->Set(Y*g_BeamShiftMultiplier);
	  }
	  else
	  {
		  hub->LogFEIError(hresult);
	  }
   }
   else if (eAct == MM::AfterSet)
   {
	  hresult = pIBeamControl->GetBeamShift(&X, &Y);
	  if(hresult == S_OK)
	  {
		pProp->Get(Y);
		Y = Y/g_BeamShiftMultiplier;
		if(Y > pProp->GetUpperLimit())
	  {
		  Y = pProp->GetUpperLimit();
	  }
	  else if(Y < pProp->GetLowerLimit())
	  {
		  Y = pProp->GetLowerLimit();
	  }
		hresult = pIBeamControl->SetBeamShift(X, Y);
		if(hresult == S_OK)
		{
		}
	  else
			{	
			hub->LogFEIError(hresult);
			}
	  }
	  else
	  {	
		   hub->LogFEIError(hresult);
	  }
	  
   }
   return DEVICE_OK;
}
int FEISEMController::OnSaveImage(MM::PropertyBase* pProp, MM::ActionType eAct)
{
HRESULT hresult;
//	double X,Y;
   if (eAct == MM::BeforeGet)
   {
   }
   else if (eAct == MM::AfterSet)
   {
	   

	  XTLibFileFormat format = XTLibFileFormat::XTLIB_TIF16;
	  
	  XTLibChannelState state;
	  int count;
	  
	  ostringstream ss;
	  string isTrue;
	  BSTR userdata = SysAllocString(L"");
	  pProp->Get(isTrue);
	  char file[MM::MaxStrLength];
	  char countvalue[MM::MaxStrLength];
	  if(isTrue == "true")
	  {
	  GetProperty("Image Filename", file);
	  string filename = string(file);
	  GetProperty("Image Counter", countvalue);
	  string counter = string(countvalue);
	  ss << counter;
	  filename += "-" + counter + ".tif";
	 // 
	  pIChannel->get_ChannelState(&state);
	  if ( state != XTLIB_CHANNELSTATE_RUN)
	  {
                pIChannel->put_ChannelState(XTLIB_CHANNELSTATE_RUN);
	  }

            // indicate we want to stop scanning at the end of frame
      pIChannel->put_ChannelState(XTLIB_CHANNELSTATE_STOP);
	  do
	  {
		  pIChannel->get_ChannelState(&state);
	  }
	  while(state != XTLIB_CHANNELSTATE_STOP);
	  hresult = pIChannel->SaveImage(CComBSTR(filename.c_str()).Detach(), format, 0, 0, userdata);
	  if(hresult == S_OK)
	  {
		pProp->Set("false");
		istringstream ( counter ) >> count;
		
		count++;
		ss.str("");
		ss << count;
		counter = ss.str();;
		const char * countchar = counter.c_str();
		SetProperty("Image Counter", countchar);
	  }
	  else
	  {	
		   hub->LogFEIError(hresult);
	  }
	  }
	  
   }
   return DEVICE_OK;
}

MM::DeviceDetectionStatus FEISEMHub::DetectDevice(void)
{
	if (initialized_)
      return MM::CanCommunicate;
	
	 MM::DeviceDetectionStatus result = MM::Misconfigured;
	int nRetCode = 0;
	try{
    pIMicroscopeControl = NULL;
    busy_ = true;
 
        HRESULT hr = E_FAIL;
        
        CComBSTR sMachine( MACHINE );

        if ( SUCCEEDED( hr = CoInitialize( NULL ) ) )
        {
  //          cout << _T( "COM library initialized successfully" ) << endl;

            if ( SUCCEEDED( hr = CoCreateInstance( CLSID_MicroscopeControl, NULL, CLSCTX_INPROC_SERVER, IID_IMicroscopeControl, reinterpret_cast < void ** > (&pIMicroscopeControl) ) ) )
            {
  //              cout << _T( "Create instance of MicroscopeControl object succeeded" ) << endl;

                if ( SUCCEEDED( hr = pIMicroscopeControl->Connect( sMachine ) ) )
                {
  //                  cout << _T( "Connect to microscope server succeeded" ) << endl;
					 pIMicroscopeControl->Disconnect();
					 pIMicroscopeControl->Release();
					 pIMicroscopeControl = NULL;
					 CoUninitialize();
					 result = MM::CanCommunicate;
                }
                else
                {
					 pIMicroscopeControl->Release();
					 pIMicroscopeControl = NULL;
					 CoUninitialize();
                 //   cout << _T( "ERROR: Cannot connect to microscope server" ) << endl;
                }
            }
            else
            {
				CoUninitialize();
       //         cout << _T( "ERROR: Cannot create instance of MicroscopeControl object" ) << endl;
            }
        }
        else
        {
       //     cout << _T( "ERROR: Cannot initialize COM library" ) << endl;
        }
	}
	catch(...)
	{
		LogMessage("Exception in DetectDevice!",false);
	}
		busy_ = false;
		return result; 
}

int FEISEMHub::DetectInstalledDevices()
{  
	if (MM::CanCommunicate == DetectDevice()) 
   {
      std::vector<std::string> peripherals; 
      peripherals.clear();
     // peripherals.push_back(g_CameraDeviceName);
      peripherals.push_back(g_ControllerDeviceName);
      for (size_t i=0; i < peripherals.size(); i++) 
      {
         MM::Device* pDev = ::CreateDevice(peripherals[i].c_str());
         if (pDev) 
         {
            AddInstalledDevice(pDev);
         }
      }
	}
   return DEVICE_OK; 
}

void FEISEMHub::GetName(char* pName) const
{
   CDeviceUtils::CopyLimitedString(pName, g_HubDeviceName);
}

void FEISEMController::GetName(char* pName) const
{
   CDeviceUtils::CopyLimitedString(pName, g_ControllerDeviceName);
}

//Camera

//FEISEMController::FEISEMController():initialized_(false),busy_(false)
//{
//		hub = NULL;
//		//pIBeamControl = NULL;
//        //pIElectronBeamControl = NULL;
//        pIMicroscopeControl = NULL;
//		pIScanControl = NULL;
//		pIVideoControl = NULL;
//		pIChannels = NULL;
//		pIDetectorGroup = NULL;
//		pIDetectorGroups = NULL;
//		pIChannel = NULL;
//		//pIVacSystemControl = NULL;
//		pIConnectionPointContainer = NULL;
//		pIDispatch = NULL; 
//
//		CreateProperty(MM::g_Keyword_Description, "FEISEM Controller", MM::String, true);
//		int ret = CreateProperty(MM::g_Keyword_Description, "FEISEM Controller", MM::String, true);
//		assert(DEVICE_OK == ret);
//
//   // Name
//   ret = CreateProperty(MM::g_Keyword_Name, g_ControllerDeviceName, MM::String, true);
//   assert(DEVICE_OK == ret);
//   CreateHubIDProperty();
//}

//int FEISEMCamera::Initialize()
//{
//   hub = static_cast<FEISEMHub*>(GetParentHub());
//
//   char hubLabel[MM::MaxStrLength];
//   hub->GetLabel(hubLabel);
//   SetParentID(hubLabel); // for backward comp.
//
//   double min, max;
//   long lmin, lmax;
//   int nRetCode = 0;
//
//        HRESULT hr = E_FAIL;
//        
//        CComBSTR sMachine( MACHINE );
//
//        if ( SUCCEEDED( hr = CoInitialize( NULL ) ) )
//        {
//  //          cout << _T( "COM library initialized successfully" ) << endl;
//
//            if ( SUCCEEDED( hr = CoCreateInstance( CLSID_MicroscopeControl, NULL, CLSCTX_INPROC_SERVER, IID_IMicroscopeControl, reinterpret_cast < void ** > (&pIMicroscopeControl) ) ) )
//            {
//  //              cout << _T( "Create instance of MicroscopeControl object succeeded" ) << endl;
//
//                if ( SUCCEEDED( hr = pIMicroscopeControl->Connect( sMachine ) ) )
//                {
//  //                  cout << _T( "Connect to microscope server succeeded" ) << endl;
//
//                    if ( SUCCEEDED( hr = pIMicroscopeControl->VideoControl( &pIVideoControl ) ) && (pIVideoControl != NULL) )
//                    {
//  //                      cout << _T( "BeamControl object created successfully" ) << endl;
//
//                        if ( SUCCEEDED( hr = pIVideoControl->Channels( &pIChannels ) ) && (pIChannels != NULL) )
//                        {
//    //                        cout << _T( "ElectronBeamControl object created successfully" ) << endl;
//
//                           
//
//                            // Get High voltage value 
//							/*
//                            if ( SUCCEEDED( hr = pIElectronBeamControl->get_HTVoltage( &dHV ) ) )
//                            {
//      //                          cout << _T( "HTVoltage = " ) << dHV << endl;
//                            }
//                            else
//                            {
//       //                         cout << _T( "ERROR: Cannot get value of HTVoltage" ) << endl;
//                            }*/   
//                        }
//                        else
//                        {
//             //               cout << _T( "ERROR: Cannot create ElectronBeamControl object" ) << endl;
//                        }
//						
//                    }
//                    else
//                    {
//               //         cout << _T( "ERROR: Cannot create BeamControl object" ) << endl;
//                    }
//					if( SUCCEEDED( hr = pIMicroscopeControl->ScanControl( &pIScanControl ) ) && (pIScanControl != NULL) )
//					{
//
//					}
//					else
//					{
//					}
//                }
//                else
//                {
//                 //   cout << _T( "ERROR: Cannot connect to microscope server" ) << endl;
//                }
//            }
//            else
//            {
//       //         cout << _T( "ERROR: Cannot create instance of MicroscopeControl object" ) << endl;
//            }
//        }
//        else
//        {
//       //     cout << _T( "ERROR: Cannot initialize COM library" ) << endl;
//        }
//
//		vector<string> BOOLOPT;
//		BOOLOPT.push_back("true");
//		BOOLOPT.push_back("false");
//
//		pAct = new CPropertyAction (this, &FEISEMCamera::OnDwellTime);
//		ret = CreateProperty("Frame Time", "0.0" , MM::Float, false, pAct);
//		assert(DEVICE_OK == ret);
////		ret = CreateProperty("Dwell Time Max", "0.0" , MM::Float, true);
////		assert(DEVICE_OK == ret);
////		ret = CreateProperty("Dwell Time Min", "0.0" , MM::Float, true);
////		assert(DEVICE_OK == ret);
//
//		hr = pIScanControl->get_DwellTimeRange((XTLibRange)XTLIB_RANGE_MIN, &min);
//		if(hr != S_OK)
//		{
//			hub->LogFEIError(hr);
//		}
//		hr = pIScanControl->get_DwellTimeRange((XTLibRange)XTLIB_RANGE_MAX, &max);
//		if(hr != S_OK)
//		{
//			hub->LogFEIError(hr);
//		}
//		SetPropertyLimits("Frame Time", min, max);
//
//
//		pAct = new CPropertyAction (this, &FEISEMCamera::OnVPixels);
//		ret = CreateProperty("Image Height", "442" , MM::Integer, false, pAct);
//		assert(DEVICE_OK == ret);
//
//		vector<string> VertPixelValues;
//		VertPixelValues.push_back("442");
//		VertPixelValues.push_back("884");
//		VertPixelValues.push_back("1768");
//		VertPixelValues.push_back("3536");
//		ret = SetAllowedValues("Image Height", VertPixelValues);
//		assert(DEVICE_OK == ret);
//
//		pAct = new CPropertyAction (this, &FEISEMCamera::OnHPixels);
//		ret = CreateProperty("Image Width", "512" , MM::Integer, false, pAct);
//		assert(DEVICE_OK == ret);
//
//		vector<string> HoriPixelValues;
//		HoriPixelValues.push_back("512");
//		HoriPixelValues.push_back("1024");
//		HoriPixelValues.push_back("2048");
//		HoriPixelValues.push_back("4096");
//		ret = SetAllowedValues("Image Width", HoriPixelValues);
//		assert(DEVICE_OK == ret);
//
///*		ret = CreateProperty("Vertical Pixels Max", "3536" , MM::Integer, true);
//		assert(DEVICE_OK == ret);
//		ret = CreateProperty("Vertical Pixels Min", "442" , MM::Integer, true);
//		assert(DEVICE_OK == ret);
//		ret = CreateProperty("Horizontal Pixels Max", "4096" , MM::Integer, true);
//		assert(DEVICE_OK == ret);
//		ret = CreateProperty("Horizontal Pixels Min", "512" , MM::Integer, true);
//		assert(DEVICE_OK == ret);*/
//		
//		hr = pIScanControl->get_NrOfVertPixelsRange((XTLibRange)XTLIB_RANGE_MIN, &lmin);
//		if(hr != S_OK)
//		{
//			hub->LogFEIError(hr);
//		}
//		hr = pIScanControl->get_NrOfVertPixelsRange((XTLibRange)XTLIB_RANGE_MAX, &lmax);
//		if(hr != S_OK)
//		{
//			hub->LogFEIError(hr);
//		}
//		SetPropertyLimits("Image Height", static_cast<double>(lmin), static_cast<double>(lmax));
//
//		hr = pIScanControl->get_NrOfHorPixelsRange((XTLibRange)XTLIB_RANGE_MIN, &lmin);
//		if(hr != S_OK)
//		{
//			hub->LogFEIError(hr);
//		}
//		hr = pIScanControl->get_NrOfHorPixelsRange((XTLibRange)XTLIB_RANGE_MAX, &lmax);
//		if(hr != S_OK)
//		{
//			hub->LogFEIError(hr);
//		}
//		SetPropertyLimits("Image Width", static_cast<double>(lmin), static_cast<double>(lmax));
//
//
//		pAct = new CPropertyAction (this, &FEISEMCamera::OnSelectedAreaXStart);
//		ret = CreateProperty("Selected Area X Start", "0" , MM::Integer, false, pAct);
//		assert(DEVICE_OK == ret);
//		pAct = new CPropertyAction (this, &FEISEMCamera::OnSelectedAreaYStart);
//		ret = CreateProperty("Selected Area Y Start", "0" , MM::Integer, false, pAct);
//		assert(DEVICE_OK == ret);
//		pAct = new CPropertyAction (this, &FEISEMCamera::OnSelectedAreaX);
//		ret = CreateProperty("Selected Area X Size", "512" , MM::Integer, false, pAct);
//		assert(DEVICE_OK == ret);
//		pAct = new CPropertyAction (this, &FEISEMCamera::OnSelectedAreaY);
//		ret = CreateProperty("Selected Area Y Size", "442" , MM::Integer, false, pAct);
//		assert(DEVICE_OK == ret);
//        pAct = new CPropertyAction (this, &FEISEMCamera::OnSelectedEnabled);
//		ret = CreateProperty("Selected Area Enabled", "false" , MM::String, false, pAct);
//		assert(DEVICE_OK == ret);
//
//		ret = SetAllowedValues("Selected Area Enabled", BOOLOPT);
//
//		
//
//  	initialized_ = true;
// 
//	return DEVICE_OK;
//}
//
//void FEISEMCamera::GetName(char* pName) const
//{
//   CDeviceUtils::CopyLimitedString(pName, g_CameraDeviceName);
//}
//
//int SnapImage();
//
//const unsigned char* GetImageBuffer()
//{
//}
//
//const unsigned char* GetImageBuffer(unsigned channelNr)
//{
//}
//
//const unsigned int* GetImageBufferAsRGB32()
//{
//}
//
//unsigned GetNumberOfComponents()
//{
//}
//
// int GetComponentName(unsigned component, char* name)
// {
// }
//
// int unsigned GetNumberOfChannels()
// {
// }
//
// int GetChannelName(unsigned channel, char* name)
// {
// }
//
// long GetImageBufferSize()
// {
// }
//
// unsigned GetImageWidth()
// {
//	 int ImageWidth;
//	 GetProperty("Image Width", ImageWidth);
//	 return unsigned(ImageWidth);
// }
//
// unsigned GetImageHeight()
// {
//	 int ImageHeight;
//	 GetProperty("Image Height", ImageHeight);
//	 return unsigned(ImageHeight);
// }
//
// unsigned GetImageBytesPerPixel()
// {
// }
//
// unsigned GetBitDepth()
// {
// }
//
// double GetPixelSizeUm()
// {
//	 double FOV; 
//	 int ImageWidth;
//	 GetProperty("Image Width", ImageWidth);
//	 GetProperty("FOV", FOV);
//	 return (FOV/ImageWidth)*1000000;
// }