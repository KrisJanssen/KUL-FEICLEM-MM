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
#include "../../../../micromanager/MMDevice/ModuleInterface.h"
#include "../../../micromanager/MMCore/Error.h"
#include <sstream>
#include <cstdio>

#ifdef WIN32
#define WIN32_LEAN_AND_MEAN
#include <windows.h>
#define snprintf _snprintf 
#endif

#define MACHINE		_T("192.168.0.1")

using namespace std;

double g_IntensityFactor_ = 1.0;

const double g_DistanceMultiplier = 1000000;
const double g_StigmationMultiplier = 1000;
const double g_BeamShiftMultiplier = 100000;
const double g_TimeMultiplier = 1000000;
const double g_ScanRotationMultiplier = 180/3.141582;

// External names used used by the rest of the system
//const char* g_CameraDeviceName = "FEISEM Camera";
const char* g_ControllerDeviceName = "FEISEM-ST";

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
	RegisterDevice(g_ControllerDeviceName, MM::GenericDevice, "FEISEM-ST");
}

MODULE_API MM::Device* CreateDevice(const char* deviceName)
{
	if (deviceName == 0)
		return 0;

	if (strcmp(deviceName, g_ControllerDeviceName) == 0)
	{
		return new FEISEMController;
	}

	return 0;
}

MODULE_API void DeleteDevice(MM::Device* pDevice)
{
	delete pDevice;
}

FEISEMController::FEISEMController():initialized_(false),busy_(false)
{
	pIVideoControl = NULL;
	pIChannels = NULL;
	pIMicroscopeControl = NULL;
	pIChannel = NULL;
	pIConnectionPointContainer = NULL;
	pIDispatch = NULL; 

	CreateProperty(MM::g_Keyword_Description, "FEISEM-ST", MM::String, true);
	int ret = CreateProperty(MM::g_Keyword_Description, "FEISEM-ST", MM::String, true);
	assert(DEVICE_OK == ret);
	// Name
	ret = CreateProperty(MM::g_Keyword_Name, g_ControllerDeviceName, MM::String, true);
	assert(DEVICE_OK == ret);



}

void FEISEMController::LogFEIError(HRESULT hresult)
{
	LogMessage("XTLib Error: " + hresult, true);
}

int FEISEMController::Shutdown()
{

	if(pIChannel)
	{
		pIChannel->Release();
		pIChannel = NULL;
	}

	if(pIChannels)
	{
		pIChannels->Release();
		pIChannels = NULL;
	}

	if(pIVideoControl)
	{
		pIVideoControl->Release();
		pIVideoControl = NULL;
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



FEISEMController::~FEISEMController()
{
	Shutdown();
}

void FEISEMController::COMInit()
{
	if ( pIMicroscopeControl )
	{
	}
	else
	{

	}
}

int FEISEMController::Initialize()
{	
	double min, max;
	long lmin, lmax;
	int nRetCode = 0;
	HRESULT hr = E_FAIL;
	bool COMOK = true;
	CComBSTR sMachine( MACHINE );
	if ( SUCCEEDED( hr = CoInitialize(NULL) ) )
	{
		//          cout << _T( "COM library initialized successfully" ) << endl;

		if ( SUCCEEDED( hr = CoCreateInstance( CLSID_MicroscopeControl, NULL, CLSCTX_INPROC_SERVER, IID_IMicroscopeControl, reinterpret_cast < void ** > (&pIMicroscopeControl) ) ) )
		{
			//              cout << _T( "Create instance of MicroscopeControl object succeeded" ) << endl;

			if ( SUCCEEDED( hr = pIMicroscopeControl->Connect( sMachine ) ) )
			{
				//                  cout << _T( "Connect to microscope server succeeded" ) << endl;

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
							COMOK = false;
						}
					}
					else
					{
						COMOK = false;
					}
				}
				else
				{
					//   cout << _T( "ERROR: Cannot connect to microscope server" ) << endl;
					COMOK = false;
				}
			}
			else
			{
				COMOK = false;//         cout << _T( "ERROR: Cannot create instance of MicroscopeControl object" ) << endl;
			}
		}
		else
		{
			COMOK = false;//     cout << _T( "ERROR: Cannot initialize COM library" ) << endl;
		}
	}    
	if(COMOK)
	{

	vector<string> BOOLOPT;
	BOOLOPT.push_back("true");
	BOOLOPT.push_back("false");
	//VacSystemControl
	
	CPropertyAction* pAct = new CPropertyAction (this, &FEISEMController::OnSaveImage);
	int ret = CreateProperty("Save Image", "false" , MM::String, false, pAct);
	assert(DEVICE_OK == ret);


	ret = SetAllowedValues("Save Image", BOOLOPT);
	assert(DEVICE_OK == ret);
	ret = CreateProperty("Image Filename", "D:\\Imaging\\MMSave\\filename" , MM::String, false);
	assert(DEVICE_OK == ret);



	ret = CreateProperty("Image Counter", "0" , MM::Integer, false);
	assert(DEVICE_OK == ret);
	initialized_ = true;
	}
	else
	{
	initialized_ = false;	}
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
				//LogFEIError(hresult);
			}
		}

	}
	return DEVICE_OK;
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
//			LogFEIError(hr);
//		}
//		hr = pIScanControl->get_DwellTimeRange((XTLibRange)XTLIB_RANGE_MAX, &max);
//		if(hr != S_OK)
//		{
//			LogFEIError(hr);
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
//			LogFEIError(hr);
//		}
//		hr = pIScanControl->get_NrOfVertPixelsRange((XTLibRange)XTLIB_RANGE_MAX, &lmax);
//		if(hr != S_OK)
//		{
//			LogFEIError(hr);
//		}
//		SetPropertyLimits("Image Height", static_cast<double>(lmin), static_cast<double>(lmax));
//
//		hr = pIScanControl->get_NrOfHorPixelsRange((XTLibRange)XTLIB_RANGE_MIN, &lmin);
//		if(hr != S_OK)
//		{
//			LogFEIError(hr);
//		}
//		hr = pIScanControl->get_NrOfHorPixelsRange((XTLibRange)XTLIB_RANGE_MAX, &lmax);
//		if(hr != S_OK)
//		{
//			LogFEIError(hr);
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