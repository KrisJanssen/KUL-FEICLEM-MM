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

// External names used used by the rest of the system
// to load particular device from the "DemoCamera.dll" library
const char* g_CameraDeviceName = "DCam";
const char* g_HubDeviceName = "FEISEM Hub";

// constants for naming pixel types (allowed values of the "PixelType" property)
const char* g_MODE_HIVAC = "Hi-Vac";
const char* g_MODE_CHARGENEUT = "Low Vacuum";
const char* g_MODE_ENVIRONMENTAL = "Environmental";

const char* g_STATE_ALL_AIR = "Vented";
const char* g_STATE_PUMPING = "Pumping - Pumping";
const char* g_STATE_PREVAC = "Pumping - Prevac";
const char* g_STATE_VACUUM = "At Vacuum";
const char* g_STATE_VENTING = "Venting";
const char* g_STATE_ERROR = "Error";

const char* g_SCAN_NONE = "None";
const char* g_SCAN_EXTERNAL = "External";
const char* g_SCAN_FULLFRAME = "Full Frame";
const char* g_SCAN_SPOT = "Spot";
const char* g_SCAN_LINE = "Line";


const char* g_HTSTATE_ON = "ON";
const char* g_HTSTATE_OFF = "OFF";

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
   AddAvailableDeviceName(g_CameraDeviceName, "Demo camera");
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
   AddAvailableDeviceName(g_HubDeviceName, "FEISEM Hub");
}

MODULE_API MM::Device* CreateDevice(const char* deviceName)
{
   if (deviceName == 0)
      return 0;

   // decide which device class to create based on the deviceName parameter
  /* if (strcmp(deviceName, g_CameraDeviceName) == 0)
   {
      // create camera
      return new CDemoCamera();
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



FEISEMHub::FEISEMHub()
{
		pIBeamControl = NULL;
        pIElectronBeamControl = NULL;
        pIMicroscopeControl = NULL;
		pIScanControl = NULL;
		pIVacSystemControl = NULL;
		pIConnectionPointContainer = NULL;
		pIDispatch = NULL; 
}

void FEISEMHub::LogFEIError(HRESULT hresult)
{
	LogMessage("XTLib Error: " + hresult, true);
}

int FEISEMHub::OnPressure(MM::PropertyBase* pProp, MM::ActionType pAct)
{
	HRESULT hresult;
	double pressure;
   if (pAct == MM::BeforeGet)
   {
	  
	  hresult = pIVacSystemControl->get_Pressure(&pressure);
	  if(hresult == S_OK)
	  {
		pProp->Set(pressure);
	  }
	  else
	  {
		  LogFEIError(hresult);
	  }
   }
   else if (pAct == MM::AfterSet)
   {
      pProp->Get(pressure);
	  hresult = pIVacSystemControl->put_Pressure(pressure);
	  if(hresult == S_OK)
	  {
	  }
	  else
	  {	
		   LogFEIError(hresult);
	  }
   }
   return DEVICE_OK;
}
int FEISEMHub::OnChamberState(MM::PropertyBase* pProp, MM::ActionType pAct)
{
	HRESULT hresult;
   if (pAct == MM::BeforeGet)
   {
   }
   else if (pAct == MM::AfterSet)
   {
   }
   return DEVICE_OK;
}
int FEISEMHub::OnChamberMode(MM::PropertyBase* pProp, MM::ActionType pAct)
{
	HRESULT hresult;
	XTLibVacuumUserMode mode; 
   if (pAct == MM::BeforeGet)
   {
	   hresult = pIVacSystemControl->get_Mode(&mode);	  
	  if(hresult == S_OK)
	  {
		switch (mode) {
		  case XTLIB_VACUUMUSER_MODE_HIVAC:
			  pProp->Set(g_MODE_HIVAC);

		  case XTLIB_VACUUMUSER_MODE_CHARGENEUT: 
			  pProp->Set(g_MODE_CHARGENEUT);

		  case XTLIB_VACUUMUSER_MODE_ENVIRONMENTAL: 
			  pProp->Set(g_MODE_ENVIRONMENTAL);
		  default:;
		}
	  }
	  else
	  {
		  LogFEIError(hresult);
	  }
   }
   return DEVICE_OK;
}
int FEISEMHub::OnScanMode(MM::PropertyBase* pProp, MM::ActionType pAct)
{
	HRESULT hresult;
   if (pAct == MM::BeforeGet)
   {
   }
   else if (pAct == MM::AfterSet)
   {
   }
   return DEVICE_OK;
}
int FEISEMHub::OnDwellTime(MM::PropertyBase* pProp, MM::ActionType pAct)
{
	HRESULT hresult;
   if (pAct == MM::BeforeGet)
   {
   }
   else if (pAct == MM::AfterSet)
   {
   }
   return DEVICE_OK;
}
int FEISEMHub::OnPixels(MM::PropertyBase* pProp, MM::ActionType pAct)
{
	HRESULT hresult;
   if (pAct == MM::BeforeGet)
   {
   }
   else if (pAct == MM::AfterSet)
   {
   }
   return DEVICE_OK;
}
int FEISEMHub::OnSelectedArea(MM::PropertyBase* pProp, MM::ActionType pAct)
{
	HRESULT hresult;
   if (pAct == MM::BeforeGet)
   {
   }
   else if (pAct == MM::AfterSet)
   {
   }
   return DEVICE_OK;
}
int FEISEMHub::OnFOV(MM::PropertyBase* pProp, MM::ActionType pAct)
{
	HRESULT hresult;
   if (pAct == MM::BeforeGet)
   {
   }
   else if (pAct == MM::AfterSet)
   {
   }
   return DEVICE_OK;
}
int FEISEMHub::OnRotation(MM::PropertyBase* pProp, MM::ActionType pAct)
{
	HRESULT hresult;
   if (pAct == MM::BeforeGet)
   {
   }
   else if (pAct == MM::AfterSet)
   {
   }
   return DEVICE_OK;
}
int FEISEMHub::OnSelectedEnabled(MM::PropertyBase* pProp, MM::ActionType pAct)
{
	HRESULT hresult;
   if (pAct == MM::BeforeGet)
   {
   }
   else if (pAct == MM::AfterSet)
   {
   }
   return DEVICE_OK;
}
int FEISEMHub::OnSpot(MM::PropertyBase* pProp, MM::ActionType pAct)
{
	HRESULT hresult;
   if (pAct == MM::BeforeGet)
   {
   }
   else if (pAct == MM::AfterSet)
   {
   }
   return DEVICE_OK;
}
int FEISEMHub::OnHTState(MM::PropertyBase* pProp, MM::ActionType pAct)
{
	HRESULT hresult;
   if (pAct == MM::BeforeGet)
   {
   }
   else if (pAct == MM::AfterSet)
   {
   }
   return DEVICE_OK;
}
int FEISEMHub::OnAcceleratingVoltage(MM::PropertyBase* pProp, MM::ActionType pAct)
{
	HRESULT hresult;
   if (pAct == MM::BeforeGet)
   {
   }
   else if (pAct == MM::AfterSet)
   {
   }
   return DEVICE_OK;
}
int FEISEMHub::OnSpotSize(MM::PropertyBase* pProp, MM::ActionType pAct)
{
	HRESULT hresult;
   if (pAct == MM::BeforeGet)
   {
   }
   else if (pAct == MM::AfterSet)
   {
   }
   return DEVICE_OK;
}
int FEISEMHub::OnBeamBlank(MM::PropertyBase* pProp, MM::ActionType pAct)
{
	HRESULT hresult;
   if (pAct == MM::BeforeGet)
   {
   }
   else if (pAct == MM::AfterSet)
   {
   }
   return DEVICE_OK;
}
int FEISEMHub::OnStigmation(MM::PropertyBase* pProp, MM::ActionType pAct)
{
	HRESULT hresult;
   if (pAct == MM::BeforeGet)
   {
   }
   else if (pAct == MM::AfterSet)
   {
   }
   return DEVICE_OK;
}
int FEISEMHub::OnBeamShift(MM::PropertyBase* pProp, MM::ActionType pAct)
{
	HRESULT hresult;
   if (pAct == MM::BeforeGet)
   {
   }
   else if (pAct == MM::AfterSet)
   {
   }
   return DEVICE_OK;
}


int FEISEMHub::Shutdown()
{
	if ( pIElectronBeamControl )
        {
       //     cout << _T( "Releasing ElectronBeamControl" ) << endl;

            pIElectronBeamControl->Release();
            pIElectronBeamControl = NULL;
        }

        if ( pIBeamControl )
        {
       //     cout << _T( "Releasing BeamControl" ) << endl;
            pIBeamControl->Release();
            pIBeamControl = NULL;
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


int FEISEMHub::Initialize()
{
   FEISEMHub* pHub = static_cast<FEISEMHub*>(GetParentHub());
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

		//VacSystemControl
		CPropertyAction* pAct = new CPropertyAction (this, &FEISEMHub::OnPressure);
		int ret = CreateProperty("Pressure (Pa)", "1000.0" , MM::Float, false, pAct, true);
		assert(DEVICE_OK == ret);
	//	ret = CreateProperty("Pressure Range (Pa)", "0.0" , MM::Float, true);
	//	assert(DEVICE_OK == ret);
		pAct = new CPropertyAction (this, &FEISEMHub::OnChamberState);
		ret = CreateProperty("Chamber State", "unknown" , MM::String, true, pAct, true);
		assert(DEVICE_OK == ret);

		vector<string> StateValues;
		StateValues.push_back(g_STATE_ALL_AIR);
		StateValues.push_back(g_STATE_PUMPING);
		StateValues.push_back(g_STATE_PREVAC);
		StateValues.push_back(g_STATE_VACUUM);
		StateValues.push_back(g_STATE_VENTING);
		StateValues.push_back(g_STATE_ERROR);
		ret = SetAllowedValues("Chamber State", StateValues);
		assert(DEVICE_OK == ret);

		pAct = new CPropertyAction (this, &FEISEMHub::OnChamberMode);
		ret = CreateProperty("Chamber Mode", "unknown" , MM::String, true, pAct, true);
		assert(DEVICE_OK == ret);

		vector<string> ModeValues;
		ModeValues.push_back(g_MODE_HIVAC);
		ModeValues.push_back(g_MODE_CHARGENEUT);
		ModeValues.push_back(g_MODE_ENVIRONMENTAL);
		ret = SetAllowedValues("Chamber Mode", ModeValues);
		assert(DEVICE_OK == ret);
		
		hr = pIVacSystemControl->get_PressureRange((XTLibRange)XTLIB_RANGE_MIN, &min);
		if(hr != S_OK)
		{
			LogFEIError(hr);
		}
		hr = pIVacSystemControl->get_PressureRange((XTLibRange)XTLIB_RANGE_MAX, &max);
		if(hr != S_OK)
		{
			LogFEIError(hr);
		}
		SetPropertyLimits("Pressure (Pa)", min, max);

		//ScanControl
		pAct = new CPropertyAction (this, &FEISEMHub::OnScanMode);
		ret = CreateProperty("Scan Mode", g_SCAN_NONE , MM::String, false, pAct, true);
		assert(DEVICE_OK == ret);

		vector<string> ScanValues;
		ScanValues.push_back(g_SCAN_NONE);
		ScanValues.push_back(g_SCAN_EXTERNAL);
		ScanValues.push_back(g_SCAN_FULLFRAME);
		ScanValues.push_back(g_SCAN_SPOT);
		ScanValues.push_back(g_SCAN_LINE);
		ret = SetAllowedValues("Scan Mode", ScanValues);
		assert(DEVICE_OK == ret);

		pAct = new CPropertyAction (this, &FEISEMHub::OnDwellTime);
		ret = CreateProperty("Dwell Time (s)", "0.0" , MM::Float, false, pAct, true);
		assert(DEVICE_OK == ret);
//		ret = CreateProperty("Dwell Time Max", "0.0" , MM::Float, true);
//		assert(DEVICE_OK == ret);
//		ret = CreateProperty("Dwell Time Min", "0.0" , MM::Float, true);
//		assert(DEVICE_OK == ret);

		hr = pIScanControl->get_DwellTimeRange((XTLibRange)XTLIB_RANGE_MIN, &min);
		if(hr != S_OK)
		{
			LogFEIError(hr);
		}
		hr = pIScanControl->get_DwellTimeRange((XTLibRange)XTLIB_RANGE_MAX, &max);
		if(hr != S_OK)
		{
			LogFEIError(hr);
		}
		SetPropertyLimits("Dwell Time (s)", min, max);


		pAct = new CPropertyAction (this, &FEISEMHub::OnPixels);
		ret = CreateProperty("Vertical Pixels", "442" , MM::Integer, false, pAct, true);
		assert(DEVICE_OK == ret);

		vector<string> VertPixelValues;
		VertPixelValues.push_back("442");
		VertPixelValues.push_back("884");
		VertPixelValues.push_back("1768");
		VertPixelValues.push_back("3536");
		ret = SetAllowedValues("Vertical Pixels", VertPixelValues);
		assert(DEVICE_OK == ret);

		pAct = new CPropertyAction (this, &FEISEMHub::OnPixels);
		ret = CreateProperty("Horizontal Pixels", "512" , MM::Integer, false, pAct, true);
		assert(DEVICE_OK == ret);

		vector<string> HoriPixelValues;
		HoriPixelValues.push_back("512");
		HoriPixelValues.push_back("1024");
		HoriPixelValues.push_back("2048");
		HoriPixelValues.push_back("4096");
		ret = SetAllowedValues("Horizontal Pixels", HoriPixelValues);
		assert(DEVICE_OK == ret);

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
			LogFEIError(hr);
		}
		hr = pIScanControl->get_NrOfVertPixelsRange((XTLibRange)XTLIB_RANGE_MAX, &lmax);
		if(hr != S_OK)
		{
			LogFEIError(hr);
		}
		SetPropertyLimits("Vertical Pixels", static_cast<double>(lmin), static_cast<double>(lmax));

		hr = pIScanControl->get_NrOfHorPixelsRange((XTLibRange)XTLIB_RANGE_MIN, &lmin);
		if(hr != S_OK)
		{
			LogFEIError(hr);
		}
		hr = pIScanControl->get_NrOfHorPixelsRange((XTLibRange)XTLIB_RANGE_MAX, &lmax);
		if(hr != S_OK)
		{
			LogFEIError(hr);
		}
		SetPropertyLimits("Horizontal Pixels", static_cast<double>(lmin), static_cast<double>(lmax));


		pAct = new CPropertyAction (this, &FEISEMHub::OnSelectedArea);
		ret = CreateProperty("Selected Area X Start", "0" , MM::Integer, false, pAct, true);
		assert(DEVICE_OK == ret);
		pAct = new CPropertyAction (this, &FEISEMHub::OnSelectedArea);
		ret = CreateProperty("Selected Area Y Start", "0" , MM::Integer, false, pAct, true);
		assert(DEVICE_OK == ret);
		pAct = new CPropertyAction (this, &FEISEMHub::OnSelectedArea);
		ret = CreateProperty("Selected Area X Size", "512" , MM::Integer, false, pAct, true);
		assert(DEVICE_OK == ret);
		pAct = new CPropertyAction (this, &FEISEMHub::OnSelectedArea);
		ret = CreateProperty("Selected Area Y Size", "442" , MM::Integer, false, pAct, true);
		assert(DEVICE_OK == ret);
		//512 x 442
		pAct = new CPropertyAction (this, &FEISEMHub::OnFOV);
		ret = CreateProperty("FOV X (m)", "0.000001" , MM::Float, false, pAct, true);
		assert(DEVICE_OK == ret);
		pAct = new CPropertyAction (this, &FEISEMHub::OnFOV);
		ret = CreateProperty("FOV Y (m)", "0.00000086328125" , MM::Float, false, pAct, true);
		assert(DEVICE_OK == ret);
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
			LogFEIError(hr);
		}
		hr = pIScanControl->get_ScanFieldXRange((XTLibRange)XTLIB_RANGE_MAX, &max);
		if(hr != S_OK)
		{
			LogFEIError(hr);
		}
		SetPropertyLimits("FOV X (m)", min, max);

		hr = pIScanControl->get_ScanFieldYRange((XTLibRange)XTLIB_RANGE_MIN, &min);
		if(hr != S_OK)
		{
			LogFEIError(hr);
		}
		hr = pIScanControl->get_ScanFieldYRange((XTLibRange)XTLIB_RANGE_MAX, &max);
		if(hr != S_OK)
		{
			LogFEIError(hr);
		}
		SetPropertyLimits("FOV Y (m)", min, max);

		pAct = new CPropertyAction (this, &FEISEMHub::OnRotation);
		ret = CreateProperty("Rotation", "0.0" , MM::Float, false, pAct, true);
		assert(DEVICE_OK == ret);
/*		ret = CreateProperty("Rotation Min", "0.0" , MM::Float, true);
		assert(DEVICE_OK == ret);
		ret = CreateProperty("Rotation Max", "0.0" , MM::Float, true);
		assert(DEVICE_OK == ret);*/

		hr = pIScanControl->get_RotationRange((XTLibRange)XTLIB_RANGE_MIN, &min);
		if(hr != S_OK)
		{
			LogFEIError(hr);
		}
		hr = pIScanControl->get_RotationRange((XTLibRange)XTLIB_RANGE_MAX, &max);
		if(hr != S_OK)
		{
			LogFEIError(hr);
		}
		SetPropertyLimits("Rotation", min, max);

		pAct = new CPropertyAction (this, &FEISEMHub::OnSelectedEnabled);
		ret = CreateProperty("Selected Area Enabled", "false" , MM::String, false, pAct, true);
		assert(DEVICE_OK == ret);
		pAct = new CPropertyAction (this, &FEISEMHub::OnSpot);
		ret = CreateProperty("Spot X", "256" , MM::Integer, false, pAct, true);
		assert(DEVICE_OK == ret);
		pAct = new CPropertyAction (this, &FEISEMHub::OnSpot);
		ret = CreateProperty("Spot Y", "221" , MM::Integer, false, pAct, true);
		assert(DEVICE_OK == ret);
		//ElectronBeamControl	
		pAct = new CPropertyAction (this, &FEISEMHub::OnHTState);
		ret = CreateProperty("HTState", "unknown" , MM::String, false, pAct, true);
		assert(DEVICE_OK == ret);

		vector<string> HTStateValues;
		HTStateValues.push_back(g_HTSTATE_ON);
		HTStateValues.push_back(g_HTSTATE_OFF);
		ret = SetAllowedValues("HTState", HTStateValues);
		assert(DEVICE_OK == ret);
		 
		pAct = new CPropertyAction (this, &FEISEMHub::OnAcceleratingVoltage);
		ret = CreateProperty("Acceleration Voltage (V)", "0.0" , MM::Float, false, pAct, true);
		assert(DEVICE_OK == ret);
		pAct = new CPropertyAction (this, &FEISEMHub::OnSpotSize);
		ret = CreateProperty("Spot Size", "2.0" , MM::Float, false, pAct, true);
		assert(DEVICE_OK == ret);
	/*	ret = CreateProperty("Spot Min", "1.0" , MM::Float, true);
		assert(DEVICE_OK == ret);
		ret = CreateProperty("Spot Max", "7.0" , MM::Float, true);
		assert(DEVICE_OK == ret);*/

		hr = pIElectronBeamControl->get_SpotSizeRange((XTLibRange)XTLIB_RANGE_MIN, &min);
		if(hr != S_OK)
		{
			LogFEIError(hr);
		}
		hr = pIElectronBeamControl->get_SpotSizeRange((XTLibRange)XTLIB_RANGE_MAX, &max);
		if(hr != S_OK)
		{
			LogFEIError(hr);
		}
		SetPropertyLimits("Spot Size", min, max);

		pAct = new CPropertyAction (this, &FEISEMHub::OnBeamBlank);
		ret = CreateProperty("Beam Blank", "false" , MM::String, false, pAct, true);
		assert(DEVICE_OK == ret);
		pAct = new CPropertyAction (this, &FEISEMHub::OnStigmation);
		ret = CreateProperty("Stigmation X", "1.0" , MM::Float, false, pAct, true);
		assert(DEVICE_OK == ret);
		pAct = new CPropertyAction (this, &FEISEMHub::OnStigmation);
		ret = CreateProperty("Stigmation Y", "7.0" , MM::Float, false, pAct, true);
		assert(DEVICE_OK == ret);
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
			LogFEIError(hr);
		}
		hr = pIElectronBeamControl->get_StigmatorXRange((XTLibRange)XTLIB_RANGE_MAX, &max);
		if(hr != S_OK)
		{
			LogFEIError(hr);
		}
		SetPropertyLimits("Stigmation X", min, max);

		hr = pIElectronBeamControl->get_StigmatorYRange((XTLibRange)XTLIB_RANGE_MIN, &min);
		if(hr != S_OK)
		{
			LogFEIError(hr);
		}
		hr = pIElectronBeamControl->get_StigmatorYRange((XTLibRange)XTLIB_RANGE_MAX, &max);
		if(hr != S_OK)
		{
			LogFEIError(hr);
		}
		SetPropertyLimits("Stigmation Y", min, max);

		//BeamControl
		pAct = new CPropertyAction (this, &FEISEMHub::OnBeamShift);
		ret = CreateProperty("Beam Shift X", "1.0" , MM::Float, false, pAct, true);
		assert(DEVICE_OK == ret);
		pAct = new CPropertyAction (this, &FEISEMHub::OnBeamShift);
		ret = CreateProperty("Beam Shift Y", "7.0" , MM::Float, false, pAct, true);
		assert(DEVICE_OK == ret);
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
			LogFEIError(hr);
		}
		hr = pIBeamControl->get_BeamShiftXRange((XTLibRange)XTLIB_RANGE_MAX, &max);
		if(hr != S_OK)
		{
			LogFEIError(hr);
		}
		SetPropertyLimits("Beam Shift X", min, max);

		hr = pIBeamControl->get_BeamShiftYRange((XTLibRange)XTLIB_RANGE_MIN, &min);
		if(hr != S_OK)
		{
			LogFEIError(hr);
		}
		hr = pIBeamControl->get_BeamShiftYRange((XTLibRange)XTLIB_RANGE_MAX, &max);
		if(hr != S_OK)
		{
			LogFEIError(hr);
		}
		SetPropertyLimits("Beam Shift Y", min, max);

  	initialized_ = true;
 
	return DEVICE_OK;
}

int FEISEMHub::DetectInstalledDevices()
{  

   return DEVICE_OK; 
}

/*MM::Device* FEISEMHub::CreatePeripheralDevice(const char* adapterName)
{
   for (unsigned i=0; i<GetNumberOfInstalledDevices(); i++)
   {
      MM::Device* d = GetInstalledDevice(i);
      char name[MM::MaxStrLength];
      d->GetName(name);
      if (strcmp(adapterName, name) == 0)
         return CreateDevice(adapterName);

   }
   return 0; // adapter name not found
}*/


void FEISEMHub::GetName(char* pName) const
{
   CDeviceUtils::CopyLimitedString(pName, g_HubDeviceName);
}


