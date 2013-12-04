///////////////////////////////////////////////////////////////////////////////
// FILE:          Arduino.cpp
// PROJECT:       Micro-Manager
// SUBSYSTEM:     DeviceAdapters
//-----------------------------------------------------------------------------
// DESCRIPTION:   Arduino adapter.  Needs accompanying firmware
// COPYRIGHT:     University of California, San Francisco, 2008
// LICENSE:       LGPL
// 
// AUTHOR:        Nico Stuurman, nico@cmp.ucsf.edu 11/09/2008
//                automatic device detection by Karl Hoover
//
//

#include "FluroSEMStage.h"
#include "../../../../micromanager/MMDevice/ModuleInterface.h"
#include <sstream>
#include <cstdio>

#ifdef WIN32
   #define WIN32_LEAN_AND_MEAN
   #include <windows.h>
   #define snprintf _snprintf 
#endif

const char* g_DeviceNameFluroSEMStageHub = "FluroSEM-Stage-Hub";
const char* g_DeviceNameFluroSEMStageXY = "FluroSEM-Stage-XY";
const char* g_DeviceNameFluroSEMStageLR = "FluroSEM-Stage-LR";
const char* g_DeviceNameFluroSEMStageZ = "FluroSEM-Stage-Z";


//const char* g_DeviceNameFluroSEMStageE861 = "FluroSEM-Stage-E861";
//const char* g_DeviceNameFluroSEMStageC867 = "FluroSEM-Stage-C867";


/*/ Global info about the state of the Arduino.  This should be folded into a class
unsigned g_switchState = 0;
unsigned g_shutterState = 0;
const int g_Min_MMVersion = 1;
const int g_Max_MMVersion = 2;
const char* g_versionProp = "Version";
const char* g_normalLogicString = "Normal";
const char* g_invertedLogicString = "Inverted";

const char* g_On = "On";
const char* g_Off = "Off";
*/
// static lock
MMThreadLock CFluroSEMStageHub::lock_;

///////////////////////////////////////////////////////////////////////////////
// Exported MMDevice API
///////////////////////////////////////////////////////////////////////////////
MODULE_API void InitializeModuleData()
{
   AddAvailableDeviceName(g_DeviceNameFluroSEMStageHub, "Hub (required)");
   AddAvailableDeviceName(g_DeviceNameFluroSEMStageXY, "Stage XY");
   AddAvailableDeviceName(g_DeviceNameFluroSEMStageLR, "Objective LR");
   AddAvailableDeviceName(g_DeviceNameFluroSEMStageZ, "Objective Z");
  // AddAvailableDeviceName(g_DeviceNameFluroSEMStageE861, "E861 Objective Controller");
  // AddAvailableDeviceName(g_DeviceNameFluroSEMStageC867, "C867 Stage Controller");
}

MODULE_API MM::Device* CreateDevice(const char* deviceName)
{
   if (deviceName == 0)
      return 0;

   if (strcmp(deviceName, g_DeviceNameFluroSEMStageHub) == 0)
   {
      return new CFluroSEMStageHub;
   }
   else if (strcmp(deviceName, g_DeviceNameFluroSEMStageXY) == 0)
   {
      return new CFluroSEMStageXY;
   }
   else if (strcmp(deviceName, g_DeviceNameFluroSEMStageLR) == 0)
   {
      return new CFluroSEMStageLR;
   }
   else if (strcmp(deviceName, g_DeviceNameFluroSEMStageZ) == 0)
   {
      return new CFluroSEMStageZ; // channel 1
   }

   return 0;
}

MODULE_API void DeleteDevice(MM::Device* pDevice)
{
   delete pDevice;
}

///////////////////////////////////////////////////////////////////////////////
// CArduinoHUb implementation
// ~~~~~~~~~~~~~~~~~~~~~~~~~~
//
CFluroSEMStageHub::CFluroSEMStageHub() :
initialized_ (false)
{
   portAvailable_ = false;

   InitializeDefaultErrorMessages();

   SetErrorText(COM_ERROR, "COM Error");
   SetErrorText(PI_CNTR_NO_ERROR, "No Error");
   SetErrorText(PI_CNTR_UNKNOWN_COMMAND, "Unknown Command");

   CPropertyAction* pAct = new CPropertyAction(this, &CFluroSEMStageHub::OnPort);
   CreateProperty(MM::g_Keyword_Port, "Undefined", MM::String, false, pAct, true);
}

CFluroSEMStageHub::~CFluroSEMStageHub()
{
   Shutdown();
}

void CFluroSEMStageHub::GetName(char* name) const
{
   CDeviceUtils::CopyLimitedString(name, g_DeviceNameFluroSEMStageHub);
}

bool CFluroSEMStageHub::Busy()
{
   return false;
}

MM::DeviceDetectionStatus CFluroSEMStageHub::DetectDevice(void)
{
   if (initialized_)
      return MM::CanCommunicate;

   // all conditions must be satisfied...
   MM::DeviceDetectionStatus result = MM::Misconfigured;
   char answerTO[MM::MaxStrLength];
   
   try
   {
      std::string portLowerCase = port_;
      for( std::string::iterator its = portLowerCase.begin(); its != portLowerCase.end(); ++its)
      {
         *its = (char)tolower(*its);
      }
      if( 0< portLowerCase.length() &&  0 != portLowerCase.compare("undefined")  && 0 != portLowerCase.compare("unknown") )
      {
         result = MM::CanNotCommunicate;
         // record the default answer time out
         GetCoreCallback()->GetDeviceProperty(port_.c_str(), "AnswerTimeout", answerTO);

         // device specific default communication parameters
         // for Arduino Duemilanova
         GetCoreCallback()->SetDeviceProperty(port_.c_str(), MM::g_Keyword_Handshaking, "Off");
         GetCoreCallback()->SetDeviceProperty(port_.c_str(), MM::g_Keyword_BaudRate, "115200" );
         GetCoreCallback()->SetDeviceProperty(port_.c_str(), MM::g_Keyword_StopBits, "1");
         // Arduino timed out in GetControllerVersion even if AnswerTimeout  = 300 ms
         GetCoreCallback()->SetDeviceProperty(port_.c_str(), "AnswerTimeout", "500.0");
         GetCoreCallback()->SetDeviceProperty(port_.c_str(), "DelayBetweenCharsMs", "0");
         MM::Device* pS = GetCoreCallback()->GetDevice(this, port_.c_str());
         pS->Initialize();
         // The first second or so after opening the serial port, the Arduino is waiting for firmwareupgrades.  Simply sleep 1 second
         MMThreadGuard myLock(lock_);
       
   
            // to succeed must reach here....
            result = MM::CanCommunicate;
         
         pS->Shutdown();
         // always restore the AnswerTimeout to the default
         GetCoreCallback()->SetDeviceProperty(port_.c_str(), "AnswerTimeout", answerTO);

      }
   }
   catch(...)
   {
      LogMessage("Exception in DetectDevice!",false);
   }

   return result;
}

int CFluroSEMStageHub::Initialize()
{
   // Name
   int ret = CreateProperty(MM::g_Keyword_Name, g_DeviceNameFluroSEMStageHub, MM::String, true);
   std::vector<std::string> answer;

   // The first second or so after opening the serial port, the Arduino is waiting for firmwareupgrades.  Simply sleep 1 second.
  

   MMThreadGuard myLock(lock_);

   // Check that we have a controller:
   try{
   GCSCommandWithAnswer("*IDN?", answer);
   }
   catch(...)
   {
	   LogMessage("Cannot Communicate with stages",false);
   }

   if(answer[0] != "(")
      return ret;
   // turn off verbose serial debug messages
   GetCoreCallback()->SetDeviceProperty(port_.c_str(), "Verbose", "0");

   initialized_ = true;
   return DEVICE_OK;
}

int CFluroSEMStageHub::DetectInstalledDevices()
{
   if (MM::CanCommunicate == DetectDevice()) 
   {
      std::vector<std::string> peripherals; 
      peripherals.clear();
      peripherals.push_back(g_DeviceNameFluroSEMStageXY);
      peripherals.push_back(g_DeviceNameFluroSEMStageLR);
      peripherals.push_back(g_DeviceNameFluroSEMStageZ);
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

int CFluroSEMStageHub::Shutdown()
{
   initialized_ = false;
   return DEVICE_OK;
}

bool CFluroSEMStageHub::SendGCSCommand(unsigned char singlebyte)
{
   int ret = WriteToComPort(port_.c_str(), &singlebyte, 1);
   if (ret != DEVICE_OK)
   {
	   lastError_ = ret;
      return false;
   }
	return true;
}

bool CFluroSEMStageHub::SendGCSCommand(const std::string command)
{
   int ret = SendSerialCommand(port_.c_str(), command.c_str(), "\n");
   if (ret != DEVICE_OK)
   {
	   lastError_ = ret;
      return false;
   }
	return true;
}

bool CFluroSEMStageHub::GCSCommandWithAnswer(const std::string command, std::vector<std::string>& answer, int nExpectedLines)
{
	if (!SendGCSCommand(command))
		return false;
	return ReadGCSAnswer(answer, nExpectedLines);
}

bool CFluroSEMStageHub::GCSCommandWithAnswer(unsigned char singlebyte, std::vector<std::string>& answer, int nExpectedLines)
{
	if (!SendGCSCommand(singlebyte))
		return false;
	return ReadGCSAnswer(answer, nExpectedLines);
}

bool CFluroSEMStageHub::ReadGCSAnswer(std::vector<std::string>& answer, int nExpectedLines)
{
	answer.clear();
   std::string line;
   do
   {
	   // block/wait for acknowledge, or until we time out;
	   int ret = GetSerialAnswer(port_.c_str(), "\n", line);
	   if (ret != DEVICE_OK)
	   {
		   lastError_ = ret;
		  return false;
	   }
	   answer.push_back(line);
   } while( !line.empty() && line[line.length()-1] == ' ' );
   if ((unsigned) nExpectedLines >=0 && answer.size() != (unsigned ) nExpectedLines)
	   return false;
	return true;
}

int CFluroSEMStageHub::OnPort(MM::PropertyBase* pProp, MM::ActionType pAct)
{
   if (pAct == MM::BeforeGet)
   {
      pProp->Set(port_.c_str());
   }
   else if (pAct == MM::AfterSet)
   {
      pProp->Get(port_);
      portAvailable_ = true;
   }
   return DEVICE_OK;
}

CFluroSEMStageXY::CFluroSEMStageXY():initialized_(false)
{
   InitializeDefaultErrorMessages();

   // Description
   int ret = CreateProperty(MM::g_Keyword_Description, "FluroSEM XY Stage", MM::String, true);
   assert(DEVICE_OK == ret);

   // Name
   ret = CreateProperty(MM::g_Keyword_Name, g_DeviceNameFluroSEMStageXY, MM::String, true);
   assert(DEVICE_OK == ret);

   ret = CreateProperty("Stage Voltage", "9500", MM::Integer, false);
   assert(DEVICE_OK == ret);

  // ret = CreateProperty("Stage Delay", "20", MM::Integer, false);
  // assert(DEVICE_OK == ret);

   // parent ID display
   CreateHubIDProperty();

}

CFluroSEMStageXY::~CFluroSEMStageXY()
{
   Shutdown();
}

void CFluroSEMStageXY::GetName(char* name) const
{
   CDeviceUtils::CopyLimitedString(name, g_DeviceNameFluroSEMStageXY);
}

int CFluroSEMStageXY::Initialize()
{
   CFluroSEMStageHub* hub = static_cast<CFluroSEMStageHub*>(GetParentHub());
 //  if (!hub || !hub->IsPortAvailable()) {
 //     return ERR_NO_PORT_SET;
 //  }
   char hubLabel[MM::MaxStrLength];
   hub->GetLabel(hubLabel);
   SetParentID(hubLabel); // for backward comp.

   initialized_ = true;

   return DEVICE_OK;
}





int CFluroSEMStageXY::Shutdown()
{
   initialized_ = false;
   return DEVICE_OK;
}

bool CFluroSEMStageXY::Busy()
{
   return false;
}

int CFluroSEMStageXY::SetPositionSteps(long x, long y)
{
	int i = 0;
	long voltage = 0;
	long delay = 0;
	GetProperty("Stage Voltage", voltage);
	//GetProperty("Stage Delay", delay);
	std::ostringstream command;
   CFluroSEMStageHub* hub = static_cast<CFluroSEMStageHub*>(GetParentHub());
  // if (!hub || !hub->IsPortAvailable())
  //    return ERR_NO_PORT_SET;
   if(x > 0)
   {
	    command << "5 MAC START OLSTEP " << voltage << " " << x;
	   hub->SendGCSCommand(command.str());
	  
   }
   else if(x < 0)
   {
	   command << "5 MAC START OLSTEP -" << voltage << " " << -1*x;
	       
	   hub->SendGCSCommand(command.str());
	   
	   
   }

   if(y > 0)
   {
	   command << "4 MAC START OLSTEP " << voltage << " " << y;  
	        
	   hub->SendGCSCommand(command.str());
	  
   }
   else if(y < 0)
   {
	    command << "4 MAC START OLSTEP -" << voltage << " " << -1*y;  	   	     
		hub->SendGCSCommand(command.str());
   }
   
   

   return DEVICE_OK;
}

int CFluroSEMStageXY::GetPositionSteps(long& x, long& y)
{
	x = 0;
	y = 0;

   return DEVICE_OK;
}

double CFluroSEMStageXY::GetStepSize()
{
	return 1.0;
}

int CFluroSEMStageXY::Home()
{
	return DEVICE_UNSUPPORTED_COMMAND;
}

int CFluroSEMStageXY::Stop()
{
		return DEVICE_UNSUPPORTED_COMMAND;
}

int CFluroSEMStageXY::SetOrigin()
{

	return DEVICE_UNSUPPORTED_COMMAND;
}

int CFluroSEMStageXY::GetLimitsUm(double& xMin, double& xMax, double& yMin, double& yMax)
{
	return DEVICE_UNSUPPORTED_COMMAND;
}

int CFluroSEMStageXY::GetStepLimits(long& xMin, long& xMax, long& yMin, long& yMax)
{
	return DEVICE_UNSUPPORTED_COMMAND;
}

double CFluroSEMStageXY::GetStepSizeXUm()
{
	return 1.0;
}

double CFluroSEMStageXY::GetStepSizeYUm()
{
	return 1.0;
}

int CFluroSEMStageXY::SetRelativePositionUm(double dx, double dy)
{
	SetPositionSteps(long(dx), long(dy));
	return DEVICE_OK;
}

CFluroSEMStageLR::CFluroSEMStageLR():initialized_(false)
{
   InitializeDefaultErrorMessages();

   // Description
   int ret = CreateProperty(MM::g_Keyword_Description, "FluroSEM LR Objective Stage", MM::String, true);
   assert(DEVICE_OK == ret);

   // Name
   ret = CreateProperty(MM::g_Keyword_Name, g_DeviceNameFluroSEMStageXY, MM::String, true);
   assert(DEVICE_OK == ret);

   CPropertyAction* pAct = new CPropertyAction(this, &CFluroSEMStageLR::OnStepVoltage);
   ret = CreateProperty("Step Voltage", "30", MM::Integer, false, pAct);
   assert(DEVICE_OK == ret);

   // parent ID display
   CreateHubIDProperty();

}

CFluroSEMStageLR::~CFluroSEMStageLR()
{
   Shutdown();
}

void CFluroSEMStageLR::GetName(char* name) const
{
   CDeviceUtils::CopyLimitedString(name, g_DeviceNameFluroSEMStageLR);
}

int CFluroSEMStageLR::Initialize()
{
   CFluroSEMStageHub* hub = static_cast<CFluroSEMStageHub*>(GetParentHub());
 //  if (!hub || !hub->IsPortAvailable()) {
 //     return ERR_NO_PORT_SET;
 //  }
   char hubLabel[MM::MaxStrLength];
   hub->GetLabel(hubLabel);
   SetParentID(hubLabel); // for backward comp.

   initialized_ = true;

   return DEVICE_OK;
}

int CFluroSEMStageLR::Shutdown()
{
   initialized_ = false;
   return DEVICE_OK;
}

bool CFluroSEMStageLR::Busy()
{
   return false;
}

int CFluroSEMStageLR::OnStepVoltage(MM::PropertyBase* pProp, MM::ActionType eAct)
{
   if (eAct == MM::BeforeGet) {
      // Nothing to do, let the caller use cached property
   } else if (eAct ==MM::AfterSet) {
      std::ostringstream command;
		CFluroSEMStageHub* hub = static_cast<CFluroSEMStageHub*>(GetParentHub());
		long val;
		pProp->Get(val);
  // if (!hub || !hub->IsPortAvailable())
  //    return ERR_NO_PORT_SET;
	   command.str("");
	   command.clear();
	   command << "1 SSA 1 " << val;  
	   hub->SendGCSCommand(command.str());
	   command.str("");
	   command.clear();
	   command << "2 SSA 1 " << val;
	   hub->SendGCSCommand(command.str());
   }

   return DEVICE_OK;
}
int CFluroSEMStageLR::SetPositionSteps(long x, long y)
{
	std::ostringstream command;
   CFluroSEMStageHub* hub = static_cast<CFluroSEMStageHub*>(GetParentHub());
  // if (!hub || !hub->IsPortAvailable())
  //    return ERR_NO_PORT_SET;
   

   if(x != 0)
   {
//	   command.str("");
//	   command.clear();
//	   command << "2 RNP 1 0";  
//	   hub->SendGCSCommand(command.str());
	   command.str("");
	   command.clear();
	   command << "2 OSM 1 " << x;  
	   hub->SendGCSCommand(command.str());
   }
   if(y != 0)
   {
//	   command.str("");
//	   command.clear();
//	   command << "1 RNP 1 0";  
//	   hub->SendGCSCommand(command.str());
	   command.str("");
	   command.clear();
	   command << "1 OSM 1 " << y;  
	   hub->SendGCSCommand(command.str());
   }

   return DEVICE_OK;
}

int CFluroSEMStageLR::GetPositionSteps(long& x, long& y)
{
	x = 0;
	y = 0;

   return DEVICE_OK;
}

double CFluroSEMStageLR::GetStepSize()
{
	return 1.0;
}

int CFluroSEMStageLR::Home()
{
	return DEVICE_UNSUPPORTED_COMMAND;
}

int CFluroSEMStageLR::SetRelativePositionUm(double dx, double dy)
{
	SetPositionSteps(long(dx), long(dy));
	return DEVICE_OK;
}

int CFluroSEMStageLR::Stop()
{
		return DEVICE_UNSUPPORTED_COMMAND;
}

int CFluroSEMStageLR::SetOrigin()
{

	return DEVICE_UNSUPPORTED_COMMAND;
}

int CFluroSEMStageLR::GetLimitsUm(double& xMin, double& xMax, double& yMin, double& yMax)
{
	return DEVICE_UNSUPPORTED_COMMAND;
}

int CFluroSEMStageLR::GetStepLimits(long& xMin, long& xMax, long& yMin, long& yMax)
{
	return DEVICE_UNSUPPORTED_COMMAND;
}

double CFluroSEMStageLR::GetStepSizeXUm()
{
	return 1.0;
}

double CFluroSEMStageLR::GetStepSizeYUm()
{
	return 1.0;
}


CFluroSEMStageZ::CFluroSEMStageZ():initialized_(false),_umPerStepDown(1.0),_umPerStepUp(1.0)
{
   InitializeDefaultErrorMessages();

   // Description
   int ret = CreateProperty(MM::g_Keyword_Description, "FluroSEM OBjective Z Stage", MM::String, true);
   assert(DEVICE_OK == ret);
   CPropertyAction* pAct = new CPropertyAction(this, &CFluroSEMStageZ::OnFineVoltage);
   ret = CreateProperty("Fine Voltage", "0", MM::Integer, false, pAct);
   assert(DEVICE_OK == ret);
   pAct = new CPropertyAction(this, &CFluroSEMStageZ::OnStepVoltage);
   ret = CreateProperty("Step Voltage", "30", MM::Integer, false, pAct);
   assert(DEVICE_OK == ret);
  // pAct = new CPropertyAction(this, &CFluroSEMStageZ::OnStepVoltage);
   ret = CreateProperty("Relative Position", "0.0", MM::Float, false);
   assert(DEVICE_OK == ret);

   // Name
   ret = CreateProperty(MM::g_Keyword_Name, g_DeviceNameFluroSEMStageZ, MM::String, true);
   assert(DEVICE_OK == ret);

   // parent ID display
   CreateHubIDProperty();

}

CFluroSEMStageZ::~CFluroSEMStageZ()
{
   Shutdown();
}

int CFluroSEMStageZ::OnFineVoltage(MM::PropertyBase* pProp, MM::ActionType eAct)
{
   if (eAct == MM::BeforeGet) {
      // Nothing to do, let the caller use cached property
   } else if (eAct ==MM::AfterSet) {
      std::ostringstream command;
		CFluroSEMStageHub* hub = static_cast<CFluroSEMStageHub*>(GetParentHub());
		long val;
		pProp->Get(val);
  // if (!hub || !hub->IsPortAvailable())
  //    return ERR_NO_PORT_SET;
	   command.str("");
	   command.clear();
	   command << "3 RNP 1 0";  
	   hub->SendGCSCommand(command.str());
	   command.str("");
	   command.clear();
	   command << "3 OAD 1 " << val;  
	   hub->SendGCSCommand(command.str());
   }

   return DEVICE_OK;
}

int CFluroSEMStageZ::OnStepVoltage(MM::PropertyBase* pProp, MM::ActionType eAct)
{
   if (eAct == MM::BeforeGet) {
      // Nothing to do, let the caller use cached property
   } else if (eAct ==MM::AfterSet) {
      std::ostringstream command;
		CFluroSEMStageHub* hub = static_cast<CFluroSEMStageHub*>(GetParentHub());
		long val;
		pProp->Get(val);
  // if (!hub || !hub->IsPortAvailable())
  //    return ERR_NO_PORT_SET;
	   command.str("");
	   command.clear();
	   command << "3 SSA 1 " << val;  
	   hub->SendGCSCommand(command.str());
   }

   return DEVICE_OK;
}

void CFluroSEMStageZ::GetName(char* name) const
{
   CDeviceUtils::CopyLimitedString(name, g_DeviceNameFluroSEMStageZ);
}

int CFluroSEMStageZ::Initialize()
{
   CFluroSEMStageHub* hub = static_cast<CFluroSEMStageHub*>(GetParentHub());
 //  if (!hub || !hub->IsPortAvailable()) {
 //     return ERR_NO_PORT_SET;
 //  }
   char hubLabel[MM::MaxStrLength];
   hub->GetLabel(hubLabel);
   SetParentID(hubLabel); // for backward comp.

   initialized_ = true;

   return DEVICE_OK;
}

int CFluroSEMStageZ::Shutdown()
{
   initialized_ = false;
   return DEVICE_OK;
}

bool CFluroSEMStageZ::Busy()
{
   return false;
}

int CFluroSEMStageZ::SetPositionSteps(long steps)
{
	std::ostringstream command;
	long stepvoltage;
	GetProperty("Step Voltage", stepvoltage); 
   CFluroSEMStageHub* hub = static_cast<CFluroSEMStageHub*>(GetParentHub());
  // if (!hub || !hub->IsPortAvailable())
  //    return ERR_NO_PORT_SET;
	   command.str("");
	   command.clear();
	   command << "3 RNP 1 0";  
	   hub->SendGCSCommand(command.str());
	   command.str("");
	   command.clear();
	   command << "3 SSA 1 " << stepvoltage;
	   hub->SendGCSCommand(command.str());
	   command.str("");
	   command.clear();
	   command << "3 OSM 1 " << steps;  
	   hub->SendGCSCommand(command.str());
	   return DEVICE_OK;
}

int CFluroSEMStageZ::GetPositionSteps(long& steps)
{

   return DEVICE_OK;
}
  
int CFluroSEMStageZ::SetPositionUm(double pos)
{
 return DEVICE_UNSUPPORTED_COMMAND;
}

int CFluroSEMStageZ::SetRelativePositionUm(double pos)
{
	long stepconvert;
	double oldpos; 
	std::string posstring;
	GetPositionUm(oldpos);
	oldpos += pos;
	posstring = oldpos;
	SetProperty("Relative Position", posstring.c_str());
	if(pos > 0.0)
	{
		stepconvert = long(pos*_umPerStepUp);
		SetPositionSteps(stepconvert);

	}
	else if(pos < 0)
	{
		stepconvert = long(pos*_umPerStepDown);
		SetPositionSteps(stepconvert);
	}

	return DEVICE_OK;
}

int CFluroSEMStageZ::GetPositionUm(double& pos)
{ 
GetProperty("Relative Position", pos);
 return DEVICE_OK;
}

int CFluroSEMStageZ::SetOrigin()
{
	double pos = 0.0;
	std::string posstring;
	posstring = pos;
	SetProperty("Relative Position", posstring.c_str());
   return DEVICE_OK;
}

int CFluroSEMStageZ::GetLimits(double& min, double& max)
{
   return DEVICE_UNSUPPORTED_COMMAND;
}

