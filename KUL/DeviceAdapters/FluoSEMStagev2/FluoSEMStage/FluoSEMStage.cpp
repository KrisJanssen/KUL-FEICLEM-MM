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

#include "FluoSEMStage.h"
#include "../../../../micromanager/MMDevice/ModuleInterface.h"
#include <sstream>
#include <cstdio>
#include <time.h>


#ifdef WIN32
   #define WIN32_LEAN_AND_MEAN
   #include <windows.h>
   #define snprintf _snprintf 
#endif

const char* g_DeviceNameFluoSEMStageHub = "FluoSEM-Stage-Hub";
const char* g_DeviceNameFluoSEMStageXY = "FluoSEM-Stage-XY";
const char* g_DeviceNameFluoSEMStageLR = "FluoSEM-Stage-LR";
const char* g_DeviceNameFluoSEMStageZ = "FluoSEM-Stage-Z";


//const char* g_DeviceNameFluoSEMStageE861 = "FluoSEM-Stage-E861";
//const char* g_DeviceNameFluoSEMStageC867 = "FluoSEM-Stage-C867";


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


///////////////////////////////////////////////////////////////////////////////
// Exported MMDevice API
///////////////////////////////////////////////////////////////////////////////
MODULE_API void InitializeModuleData()
{
	RegisterDevice(g_DeviceNameFluoSEMStageHub, MM::HubDevice, "Hub (required)");
	RegisterDevice(g_DeviceNameFluoSEMStageXY, MM::XYStageDevice, "Stage XY");
	RegisterDevice(g_DeviceNameFluoSEMStageLR, MM::XYStageDevice, "Objective LR");
	RegisterDevice(g_DeviceNameFluoSEMStageZ, MM::StageDevice, "Objective Z");
   
  // AddAvailableDeviceName(g_DeviceNameFluoSEMStageE861, "E861 Objective Controller");
  // AddAvailableDeviceName(g_DeviceNameFluoSEMStageC867, "C867 Stage Controller");
}

MODULE_API MM::Device* CreateDevice(const char* deviceName)
{
   if (deviceName == 0)
      return 0;

   if (strcmp(deviceName, g_DeviceNameFluoSEMStageHub) == 0)
   {
      return new CFluoSEMStageHub;
   }
   else if (strcmp(deviceName, g_DeviceNameFluoSEMStageXY) == 0)
   {
      return new CFluoSEMStageXY;
   }
   else if (strcmp(deviceName, g_DeviceNameFluoSEMStageLR) == 0)
   {
      return new CFluoSEMStageXY;
   }
   else if (strcmp(deviceName, g_DeviceNameFluoSEMStageZ) == 0)
   {
      return new CFluoSEMStageZ; // channel 1
   }

   return 0;
}

MODULE_API void DeleteDevice(MM::Device* pDevice)
{
   delete pDevice;
}

///////////////////////////////////////////////////////////////////////////////
// CArduinoHUb implementation
//
CFluoSEMStageHub::CFluoSEMStageHub() :
initialized_ (false), _isSerialBusy(false) 
{
   portAvailable_ = false;
   
   InitializeDefaultErrorMessages();

   SetErrorText(COM_ERROR, "COM Error");
   SetErrorText(PI_CNTR_NO_ERROR, "No Error");
   SetErrorText(PI_CNTR_UNKNOWN_COMMAND, "Unknown Command");

   CPropertyAction* pAct = new CPropertyAction(this, &CFluoSEMStageHub::OnPort);
   CreateProperty(MM::g_Keyword_Port, "Undefined", MM::String, false, pAct, true);
}

CFluoSEMStageHub::~CFluoSEMStageHub()
{
   Shutdown();
}

void CFluoSEMStageHub::GetName(char* name) const
{
   CDeviceUtils::CopyLimitedString(name, g_DeviceNameFluoSEMStageHub);
}

bool CFluoSEMStageHub::Busy()
{
   return false;
}

MM::DeviceDetectionStatus CFluoSEMStageHub::DetectDevice(void)
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
        // MMThreadGuard myLock(lock_);
       
   
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

int CFluoSEMStageHub::Initialize()
{
   // Name
   int ret = CreateProperty(MM::g_Keyword_Name, g_DeviceNameFluoSEMStageHub, MM::String, true);
   std::vector<std::string> answer;

   // The first second or so after opening the serial port, the Arduino is waiting for firmwareupgrades.  Simply sleep 1 second.
  

  // MMThreadGuard myLock(lock_);

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

int CFluoSEMStageHub::DetectInstalledDevices()
{
   if (MM::CanCommunicate == DetectDevice()) 
   {
      std::vector<std::string> peripherals; 
      peripherals.clear();
      peripherals.push_back(g_DeviceNameFluoSEMStageXY);
      peripherals.push_back(g_DeviceNameFluoSEMStageLR);
      peripherals.push_back(g_DeviceNameFluoSEMStageZ);
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

int CFluoSEMStageHub::Shutdown()
{
   initialized_ = false;
   return DEVICE_OK;
}

bool CFluoSEMStageHub::SendGCSCommand(unsigned char singlebyte)
{
	int ret = 1;
	try{
	ret = WriteToComPort(port_.c_str(), &singlebyte, 1);
	}		catch(...)
		{
			LogMessage("Serial Error",false);
		}
   if (ret != DEVICE_OK)
   {
	   lastError_ = ret;
      return false;
   }
	return true;
}

bool CFluoSEMStageHub::SendGCSCommand(const std::string command)
{
	int ret = 1;
	try{
		ret = SendSerialCommand(port_.c_str(), command.c_str(), "\n");
		}		catch(...)
		{
			LogMessage("Serial Error",false);
		}
	
   if (ret != DEVICE_OK)
   {
	   lastError_ = ret;
      return false;
   }
	return true;
}

bool CFluoSEMStageHub::GCSCommandWithAnswer(const std::string command, std::vector<std::string>& answer, int nExpectedLines)
{
	if (!SendGCSCommand(command))
		return false;
	return ReadGCSAnswer(answer, nExpectedLines);
}

bool CFluoSEMStageHub::GCSCommandWithAnswer(unsigned char singlebyte, std::vector<std::string>& answer, int nExpectedLines)
{
	if (!SendGCSCommand(singlebyte))
		return false;
	return ReadGCSAnswer(answer, nExpectedLines);
}

bool CFluoSEMStageHub::DoReset(int device)
{
	int count = -1;
	std::ostringstream command;
	std::vector<std::string> answer;
	command << device << " ERR?";
	do
	{
		GCSCommandWithAnswer(command.str(), answer, 1);
		count++;
	}
	while(count < 10 && answer[0][0] != '0');
	if(count < 10)
	{
		return false;
	}
	else
	{
		return true;
	}

}

bool CFluoSEMStageHub::ReadGCSAnswer(std::vector<std::string>& answer, int nExpectedLines)
{
	answer.clear();
   std::string line;
   int ret = 1;
   do
   {
	   // block/wait for acknowledge, or until we time out;
	   try{
	   ret = GetSerialAnswer(port_.c_str(), "\n", line);
	   	}		catch(...)
		{
			LogMessage("Serial Error",false);
		}
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

int CFluoSEMStageHub::OnPort(MM::PropertyBase* pProp, MM::ActionType pAct)
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


CFluoSEMStageLR::CFluoSEMStageLR():initialized_(false)
{
   InitializeDefaultErrorMessages();

   // parent ID display
   CreateHubIDProperty();

}

CFluoSEMStageLR::~CFluoSEMStageLR()
{
   Shutdown();
}

void CFluoSEMStageLR::GetName(char* name) const
{
   CDeviceUtils::CopyLimitedString(name, g_DeviceNameFluoSEMStageLR);
}

int CFluoSEMStageLR::Initialize()
{
   CFluoSEMStageHub* hub = static_cast<CFluoSEMStageHub*>(GetParentHub());
 //  if (!hub || !hub->IsPortAvailable()) {
 //     return ERR_NO_PORT_SET;
 //  }
   char hubLabel[MM::MaxStrLength];
   hub->GetLabel(hubLabel);
   SetParentID(hubLabel); // for backward comp.


   int ret = CreateProperty(MM::g_Keyword_Description, "FluoSEM LR Objective Stage", MM::String, true);
   assert(DEVICE_OK == ret);

   // Name
   ret = CreateProperty(MM::g_Keyword_Name, g_DeviceNameFluoSEMStageLR, MM::String, true);
   assert(DEVICE_OK == ret);

   CPropertyAction* pAct = new CPropertyAction(this, &CFluoSEMStageLR::OnFineVoltageL);
   ret = CreateProperty("Fine Voltage L", "0", MM::Float, false, pAct);
   assert(DEVICE_OK == ret);
   pAct = new CPropertyAction(this, &CFluoSEMStageLR::OnFineVoltageR);
   ret = CreateProperty("Fine Voltage R", "0", MM::Float, false, pAct);
   assert(DEVICE_OK == ret);
   pAct = new CPropertyAction(this, &CFluoSEMStageLR::OnReset);
   ret = CreateProperty("Reset?", "0", MM::Integer, false, pAct);
   assert(DEVICE_OK == ret);

   //pAct = new CPropertyAction(this, &CFluoSEMStageLR::OnStepVoltage);
   ret = CreateProperty("Step Voltage RP", "30.0", MM::Float, false);
   assert(DEVICE_OK == ret);
   ret = CreateProperty("Step Voltage RN", "30.0", MM::Float, false);
   assert(DEVICE_OK == ret);
   ret = CreateProperty("Step Voltage LP", "30.0", MM::Float, false);
   assert(DEVICE_OK == ret);
   ret = CreateProperty("Step Voltage LN", "30.0", MM::Float, false);
   assert(DEVICE_OK == ret);

   initialized_ = true;

   return DEVICE_OK;
}

int CFluoSEMStageLR::Shutdown()
{
   initialized_ = false;
   return DEVICE_OK;
}

bool CFluoSEMStageLR::Busy()
{
   return false;
}

int CFluoSEMStageLR::OnFineVoltageL(MM::PropertyBase* pProp, MM::ActionType pAct)
{
   if (pAct == MM::BeforeGet) {
      // Nothing to do, let the caller use cached property
   } else if (pAct ==MM::AfterSet) {
      std::ostringstream command;
		CFluoSEMStageHub* hub = static_cast<CFluoSEMStageHub*>(GetParentHub());
		double val;
		pProp->Get(val);
  // if (!hub || !hub->IsPortAvailable())
  //    return ERR_NO_PORT_SET;
		if(_isFirstMoveL)
		{
			_wasLastMoveStepL = true;
			_isFirstMoveL = false;
		}
		if(_wasLastMoveStepL)
		{
			command.str("");
		    command.clear();
			command << "2 RNP 1 0";  
			hub->SendGCSCommand(command.str());
		}  
	   command.str("");
	   command.clear();
	   command << "2 OAD 1 " << val;  
	   hub->SendGCSCommand(command.str());
	   _wasLastMoveStepL = false;
   }

   return DEVICE_OK;
}

int CFluoSEMStageLR::OnFineVoltageR(MM::PropertyBase* pProp, MM::ActionType pAct)
{
   if (pAct == MM::BeforeGet) {
      // Nothing to do, let the caller use cached property
   } else if (pAct ==MM::AfterSet) {
      std::ostringstream command;
		CFluoSEMStageHub* hub = static_cast<CFluoSEMStageHub*>(GetParentHub());
		double val;
		pProp->Get(val);
  // if (!hub || !hub->IsPortAvailable())
  //    return ERR_NO_PORT_SET;
		if(_isFirstMoveR)
		{
			_wasLastMoveStepR = true;
			_isFirstMoveR = false;
		}
		if(_wasLastMoveStepR)
		{
			command.str("");
		    command.clear();
			command << "1 RNP 1 0";  
			hub->SendGCSCommand(command.str());
		}  
	   command.str("");
	   command.clear();
	   command << "1 OAD 1 " << val;  
	   hub->SendGCSCommand(command.str());
	   _wasLastMoveStepR = false;
   }

   return DEVICE_OK;
}

int CFluoSEMStageLR::OnReset(MM::PropertyBase* pProp, MM::ActionType pAct)
{
	CFluoSEMStageHub* hub = static_cast<CFluoSEMStageHub*>(GetParentHub());
   if (pAct == MM::AfterSet)
   {
      pProp->Set((long)(0));
      hub->DoReset(1);
	  hub->DoReset(2);
   }
   return DEVICE_OK;
}
/*
int CFluoSEMStageLR::OnStepVoltage(MM::PropertyBase* pProp, MM::ActionType pAct)
{
   if (pAct == MM::BeforeGet) {
      // Nothing to do, let the caller use cached property
   } else if (pAct ==MM::AfterSet) {
      std::ostringstream command;
		CFluoSEMStageHub* hub = static_cast<CFluoSEMStageHub*>(GetParentHub());
		double val;
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
*/
int CFluoSEMStageLR::SetPositionSteps(long x, long y)
{
	std::ostringstream command;
   CFluoSEMStageHub* hub = static_cast<CFluoSEMStageHub*>(GetParentHub());
  // if (!hub || !hub->IsPortAvailable())
  //    return ERR_NO_PORT_SET;
   
   double val;
   if(x != 0)
   {
	   if(x > 0)
	   {
		   GetProperty("Step Voltage LP", val);
		   command.str("");
		   command.clear();
		   command << "2 SSA 1 " << val;  
		   hub->SendGCSCommand(command.str());
	   }
	   else if(x < 0)
	   {
		   GetProperty("Step Voltage LN", val);
		   command.str("");
		   command.clear();
		   command << "2 SSA 1 " << val;  
		   hub->SendGCSCommand(command.str());
	   }
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
	   	   
	   if(y > 0)
	   {
		   GetProperty("Step Voltage RP", val);
		   command.str("");
		   command.clear();
		   command << "1 SSA 1 " << val;  
		   hub->SendGCSCommand(command.str());
	   }
	   else if(y < 0)
	   {
		   GetProperty("Step Voltage RN", val);
		   command.str("");
		   command.clear();
		   command << "1 SSA 1 " << val;  
		   hub->SendGCSCommand(command.str());
	   }
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

int CFluoSEMStageLR::GetPositionSteps(long& x, long& y)
{
	x = 0;
	y = 0;

   return DEVICE_OK;
}

double CFluoSEMStageLR::GetStepSize()
{
	return 1.0;
}

int CFluoSEMStageLR::Home()
{
	return DEVICE_UNSUPPORTED_COMMAND;
}

int CFluoSEMStageLR::SetRelativePositionUm(double dx, double dy)
{
	SetPositionSteps(long(dx), long(dy));
	return DEVICE_OK;
}

int CFluoSEMStageLR::Stop()
{
		return DEVICE_UNSUPPORTED_COMMAND;
}

int CFluoSEMStageLR::SetOrigin()
{

	return DEVICE_UNSUPPORTED_COMMAND;
}

int CFluoSEMStageLR::GetLimitsUm(double& xMin, double& xMax, double& yMin, double& yMax)
{
	return DEVICE_UNSUPPORTED_COMMAND;
}

int CFluoSEMStageLR::GetStepLimits(long& xMin, long& xMax, long& yMin, long& yMax)
{
	return DEVICE_UNSUPPORTED_COMMAND;
}

double CFluoSEMStageLR::GetStepSizeXUm()
{
	return 1.0;
}

double CFluoSEMStageLR::GetStepSizeYUm()
{
	return 1.0;
}


CFluoSEMStageXY::CFluoSEMStageXY():initialized_(false)
{
   InitializeDefaultErrorMessages();

   // Description
   

   // parent ID display
   CreateHubIDProperty();

}

CFluoSEMStageXY::~CFluoSEMStageXY()
{
   Shutdown();
}

void CFluoSEMStageXY::GetName(char* name) const
{
   CDeviceUtils::CopyLimitedString(name, g_DeviceNameFluoSEMStageXY);
}

int CFluoSEMStageXY::Initialize()
{
   CFluoSEMStageHub* hub = static_cast<CFluoSEMStageHub*>(GetParentHub());
 //  if (!hub || !hub->IsPortAvailable()) {
 //     return ERR_NO_PORT_SET;
 //  }
   char hubLabel[MM::MaxStrLength];
   hub->GetLabel(hubLabel);
   SetParentID(hubLabel); // for backward comp.


   int ret = CreateProperty(MM::g_Keyword_Description, "FluoSEM XY Stage", MM::String, true);
   assert(DEVICE_OK == ret);

   // Name
   ret = CreateProperty(MM::g_Keyword_Name, g_DeviceNameFluoSEMStageLR, MM::String, true);
   assert(DEVICE_OK == ret);

   CPropertyAction* pAct = new CPropertyAction(this, &CFluoSEMStageXY::OnFineVoltageL);
   ret = CreateProperty("Fine Voltage L", "0", MM::Float, false, pAct);
   assert(DEVICE_OK == ret);
   pAct = new CPropertyAction(this, &CFluoSEMStageXY::OnFineVoltageR);
   ret = CreateProperty("Fine Voltage R", "0", MM::Float, false, pAct);
   assert(DEVICE_OK == ret);
   pAct = new CPropertyAction(this, &CFluoSEMStageXY::OnReset);
   ret = CreateProperty("Reset?", "0", MM::Integer, false, pAct);
   assert(DEVICE_OK == ret);

   //pAct = new CPropertyAction(this, &CFluoSEMStageXY::OnStepVoltage);
   ret = CreateProperty("Step Voltage RP", "30.0", MM::Float, false);
   assert(DEVICE_OK == ret);
   ret = CreateProperty("Step Voltage RN", "30.0", MM::Float, false);
   assert(DEVICE_OK == ret);
   ret = CreateProperty("Step Voltage LP", "30.0", MM::Float, false);
   assert(DEVICE_OK == ret);
   ret = CreateProperty("Step Voltage LN", "30.0", MM::Float, false);
   assert(DEVICE_OK == ret);

   initialized_ = true;

   return DEVICE_OK;
}

int CFluoSEMStageXY::Shutdown()
{
   initialized_ = false;
   return DEVICE_OK;
}

bool CFluoSEMStageXY::Busy()
{
   return false;
}

int CFluoSEMStageXY::OnFineVoltageL(MM::PropertyBase* pProp, MM::ActionType pAct)
{
   if (pAct == MM::BeforeGet) {
      // Nothing to do, let the caller use cached property
   } else if (pAct ==MM::AfterSet) {
      std::ostringstream command;
		CFluoSEMStageHub* hub = static_cast<CFluoSEMStageHub*>(GetParentHub());
		double val;
		pProp->Get(val);
  // if (!hub || !hub->IsPortAvailable())
  //    return ERR_NO_PORT_SET;
		if(_isFirstMoveL)
		{
			_wasLastMoveStepL = true;
			_isFirstMoveL = false;
		}
		if(_wasLastMoveStepL)
		{
			command.str("");
		    command.clear();
			command << "5 RNP 1 0";  
			hub->SendGCSCommand(command.str());
		}  
	   command.str("");
	   command.clear();
	   command << "5 OAD 1 " << val;  
	   hub->SendGCSCommand(command.str());
	   _wasLastMoveStepL = false;
   }

   return DEVICE_OK;
}

int CFluoSEMStageXY::OnFineVoltageR(MM::PropertyBase* pProp, MM::ActionType pAct)
{
   if (pAct == MM::BeforeGet) {
      // Nothing to do, let the caller use cached property
   } else if (pAct ==MM::AfterSet) {
      std::ostringstream command;
		CFluoSEMStageHub* hub = static_cast<CFluoSEMStageHub*>(GetParentHub());
		double val;
		pProp->Get(val);
  // if (!hub || !hub->IsPortAvailable())
  //    return ERR_NO_PORT_SET;
		if(_isFirstMoveR)
		{
			_wasLastMoveStepR = true;
			_isFirstMoveR = false;
		}
		if(_wasLastMoveStepR)
		{
			command.str("");
		    command.clear();
			command << "4 RNP 1 0";  
			hub->SendGCSCommand(command.str());
		}  
	   command.str("");
	   command.clear();
	   command << "4 OAD 1 " << val;  
	   hub->SendGCSCommand(command.str());
	   _wasLastMoveStepR = false;
   }

   return DEVICE_OK;
}

int CFluoSEMStageXY::OnReset(MM::PropertyBase* pProp, MM::ActionType pAct)
{
	CFluoSEMStageHub* hub = static_cast<CFluoSEMStageHub*>(GetParentHub());
   if (pAct == MM::AfterSet)
   {
      pProp->Set((long)(0));
      hub->DoReset(4);
	  hub->DoReset(5);
   }
   return DEVICE_OK;
}

int CFluoSEMStageXY::SetPositionSteps(long x, long y)
{
	std::ostringstream command;
   CFluoSEMStageHub* hub = static_cast<CFluoSEMStageHub*>(GetParentHub());
  // if (!hub || !hub->IsPortAvailable())
  //    return ERR_NO_PORT_SET;
   
   double val;
   if(x != 0)
   {
	   if(x > 0)
	   {
		   GetProperty("Step Voltage LP", val);
		   command.str("");
		   command.clear();
		   command << "2 SSA 1 " << val;  
		   hub->SendGCSCommand(command.str());
	   }
	   else if(x < 0)
	   {
		   GetProperty("Step Voltage LN", val);
		   command.str("");
		   command.clear();
		   command << "2 SSA 1 " << val;  
		   hub->SendGCSCommand(command.str());
	   }
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
	   	   
	   if(y > 0)
	   {
		   GetProperty("Step Voltage RP", val);
		   command.str("");
		   command.clear();
		   command << "1 SSA 1 " << val;  
		   hub->SendGCSCommand(command.str());
	   }
	   else if(y < 0)
	   {
		   GetProperty("Step Voltage RN", val);
		   command.str("");
		   command.clear();
		   command << "1 SSA 1 " << val;  
		   hub->SendGCSCommand(command.str());
	   }
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

int CFluoSEMStageXY::GetPositionSteps(long& x, long& y)
{
	x = 0;
	y = 0;

   return DEVICE_OK;
}

double CFluoSEMStageXY::GetStepSize()
{
	return 1.0;
}

int CFluoSEMStageXY::Home()
{
	CFluoSEMStageHub* hub = static_cast<CFluoSEMStageHub*>(GetParentHub());
	std::ostringstream command;
	command.str("");
	command.clear();
	command << "4 POS 1 " << _xOrigin/1000;
	hub->SendGCSCommand("4 SVO 1 1");
	hub->SendGCSCommand("5 SVO 1 1");
	hub->SendGCSCommand("4 FRF 1");
	hub->SendGCSCommand("5 FRF 1");
	hub->SendGCSCommand(command.str());
	command.str("");
	command.clear();
	command << "5 POS 1 " << _yOrigin/1000;
	hub->SendGCSCommand(command.str());
	return DEVICE_OK;
}

int CFluoSEMStageXY::SetRelativePositionUm(double dx, double dy)
{
	 CFluoSEMStageHub* hub = static_cast<CFluoSEMStageHub*>(GetParentHub());
	 std::ostringstream command;
	 bool mirrorX, mirrorY;
     char val[MM::MaxStrLength];
      int ret = this->GetProperty(MM::g_Keyword_Transpose_MirrorX, val);
      assert(ret == DEVICE_OK);
      mirrorX = strcmp(val, "1") == 0 ? true : false;

      ret = this->GetProperty(MM::g_Keyword_Transpose_MirrorY, val);
      assert(ret == DEVICE_OK);
      mirrorY = strcmp(val, "1") == 0 ? true : false;
	if(mirrorX == true)
	{
		dx = -1*dx;
	}
	if(mirrorY == true)
	{
		dy = -1*dy;
	}

	dx = dx/1000;
	dy = dy/1000;
	command.str("");
	command.clear();
	command << "4 MVR 1 " << dx;
	hub->SendGCSCommand(command.str());
	command.str("");
	command.clear();
	command << "5 MVR 1 " << dy;
	hub->SendGCSCommand(command.str());
	
	return DEVICE_OK;
}

int CFluoSEMStageXY::SetPositionUm(double dx, double dy)
{
	CFluoSEMStageHub* hub = static_cast<CFluoSEMStageHub*>(GetParentHub());
	std::ostringstream command;
	dx = dx/1000;
	dy = dy/1000;
	command.str("");
	command.clear();
	command << "4 MOV 1 " << dx;
	hub->SendGCSCommand(command.str());
	command.str("");
	command.clear();
	command << "5 MOV 1 " << dy;
	hub->SendGCSCommand(command.str());
	
	return DEVICE_OK;
}

int CFluoSEMStageXY::GetPositionUm(double &dx, double &dy)
{
	CFluoSEMStageHub* hub = static_cast<CFluoSEMStageHub*>(GetParentHub());
	std::vector<std::string> answer;
	hub->GCSCommandWithAnswer("4 POS? 1", answer, 1);
	int length = answer[0].length();
	if(length > 6)
	{
		try{
			dx = (stod(answer[0].substr(6,length-1)))*1000;
		}
		catch(...)
		{
			return DEVICE_SERIAL_INVALID_RESPONSE;
		}
	}
	else
	{
		return DEVICE_SERIAL_INVALID_RESPONSE;
	}
	hub->GCSCommandWithAnswer("5 POS? 1", answer, 1);
	length = answer[0].length();
	if(length > 6)
	{
		try{
			dy = (stod(answer[0].substr(6,length-1)))*1000;
		}
		catch(...)
		{
			return DEVICE_SERIAL_INVALID_RESPONSE;
		}
	}
	else
	{
		return DEVICE_SERIAL_INVALID_RESPONSE;
	}
	return DEVICE_OK;
}

int CFluoSEMStageXY::Stop()
{
		return DEVICE_UNSUPPORTED_COMMAND;
}

int CFluoSEMStageXY::SetOrigin()
{

	GetPositionUm(_xOrigin,_yOrigin);
	return DEVICE_OK;
}

int CFluoSEMStageXY::GetLimitsUm(double& xMin, double& xMax, double& yMin, double& yMax)
{
	return DEVICE_UNSUPPORTED_COMMAND;
}

int CFluoSEMStageXY::GetStepLimits(long& xMin, long& xMax, long& yMin, long& yMax)
{
	return DEVICE_UNSUPPORTED_COMMAND;
}

double CFluoSEMStageXY::GetStepSizeXUm()
{
	return 1.0;
}

double CFluoSEMStageXY::GetStepSizeYUm()
{
	return 1.0;
}


CFluoSEMStageZ::CFluoSEMStageZ():initialized_(false),_umPerStepDown(1.0),_umPerStepUp(1.0),_wasLastMoveStep(false),_isFirstMove(true)
{
   InitializeDefaultErrorMessages();

   // Description
   

   // parent ID display
   CreateHubIDProperty();

}

CFluoSEMStageZ::~CFluoSEMStageZ()
{
   Shutdown();
}

int CFluoSEMStageZ::OnFineVoltage(MM::PropertyBase* pProp, MM::ActionType pAct)
{
   if (pAct == MM::BeforeGet) {
      // Nothing to do, let the caller use cached property
   } else if (pAct ==MM::AfterSet) {
      std::ostringstream command;
		CFluoSEMStageHub* hub = static_cast<CFluoSEMStageHub*>(GetParentHub());
		double val;
		pProp->Get(val);
  // if (!hub || !hub->IsPortAvailable())
  //    return ERR_NO_PORT_SET;
		if(_isFirstMove)
		{
			_wasLastMoveStep = true;
			_isFirstMove = false;
		}
		if(_wasLastMoveStep)
		{
			command.str("");
		    command.clear();
			command << "3 RNP 1 0";  
			hub->SendGCSCommand(command.str());
		}  
	   command.str("");
	   command.clear();
	   command << "3 OAD 1 " << val;  
	   hub->SendGCSCommand(command.str());
	   _wasLastMoveStep = false;
   }

   return DEVICE_OK;
}

int CFluoSEMStageZ::OnReset(MM::PropertyBase* pProp, MM::ActionType pAct)
{
	CFluoSEMStageHub* hub = static_cast<CFluoSEMStageHub*>(GetParentHub());
   if (pAct == MM::AfterSet)
   {
      pProp->Set((long)(0));
      hub->DoReset(3);
   }
   return DEVICE_OK;
}

void CFluoSEMStageZ::GetName(char* name) const
{
   CDeviceUtils::CopyLimitedString(name, g_DeviceNameFluoSEMStageZ);
}

int CFluoSEMStageZ::Initialize()
{
   CFluoSEMStageHub* hub = static_cast<CFluoSEMStageHub*>(GetParentHub());
 //  if (!hub || !hub->IsPortAvailable()) {
 //     return ERR_NO_PORT_SET;
 //  }
   char hubLabel[MM::MaxStrLength];
   hub->GetLabel(hubLabel);
   SetParentID(hubLabel); // for backward comp.

   int ret = CreateProperty(MM::g_Keyword_Description, "FluoSEM Objective Z Stage", MM::String, true);
   assert(DEVICE_OK == ret);
   CPropertyAction* pAct = new CPropertyAction(this, &CFluoSEMStageZ::OnFineVoltage);
   ret = CreateProperty("Fine Voltage", "0", MM::Float, false, pAct);
   assert(DEVICE_OK == ret);
   pAct = new CPropertyAction(this, &CFluoSEMStageZ::OnReset);
   ret = CreateProperty("Reset?", "0", MM::Integer, false, pAct);
   assert(DEVICE_OK == ret);
  // pAct = new CPropertyAction(this, &CFluoSEMStageZ::OnStepVoltage);
   ret = CreateProperty("Step Voltage P", "30", MM::Float, false);
   assert(DEVICE_OK == ret);
   ret = CreateProperty("Step Voltage N", "30", MM::Float, false);
   assert(DEVICE_OK == ret);
   pAct = new CPropertyAction(this, &CFluoSEMStageZ::OnHome);
   assert(DEVICE_OK == ret);
   ret = CreateProperty("Home?", "false", MM::String, false, pAct);
   assert(DEVICE_OK == ret);
   pAct = new CPropertyAction(this, &CFluoSEMStageZ::OnOrigin);
   assert(DEVICE_OK == ret);
   ret = CreateProperty("SetOrigin?", "false", MM::String, false, pAct);
   assert(DEVICE_OK == ret);

   // Name
   ret = CreateProperty(MM::g_Keyword_Name, g_DeviceNameFluoSEMStageZ, MM::String, true);
   assert(DEVICE_OK == ret);

   initialized_ = true;

   return DEVICE_OK;
}

int CFluoSEMStageZ::Shutdown()
{
   initialized_ = false;
   return DEVICE_OK;
}

bool CFluoSEMStageZ::Busy()
{
   return false;
}

int CFluoSEMStageZ::SetPositionSteps(long steps)
{
	std::ostringstream command;
	double stepvoltage;
	GetProperty("Step Voltage", stepvoltage); 
   CFluoSEMStageHub* hub = static_cast<CFluoSEMStageHub*>(GetParentHub());
  // if (!hub || !hub->IsPortAvailable())
  //    return ERR_NO_PORT_SET;

		if(_isFirstMove)
		{
			_wasLastMoveStep = false;
			_isFirstMove = false;
		}
		if(!_wasLastMoveStep)
		{
			command.str("");
		    command.clear();
			command << "3 RNP 1 0";  
			hub->SendGCSCommand(command.str());
		}  
		if(steps> 0)
	   {
		   GetProperty("Step Voltage P", stepvoltage);
		   command.str("");
		   command.clear();
		   command << "3 SSA 1 " << stepvoltage;  
		   hub->SendGCSCommand(command.str());
	   }
	   else if(steps < 0)
	   {
		   GetProperty("Step Voltage N", stepvoltage);
		   command.str("");
		   command.clear();
		   command << "3 SSA 1 " << stepvoltage;  
		   hub->SendGCSCommand(command.str());
	   }
	   command.str("");
	   command.clear();
	   command << "3 OSM 1 " << steps;  
	   hub->SendGCSCommand(command.str());
	   _wasLastMoveStep = true;
	   return DEVICE_OK;
}

int CFluoSEMStageZ::SetPositionUm(double pos)
{
	CFluoSEMStageHub* hub = static_cast<CFluoSEMStageHub*>(GetParentHub());
	std::ostringstream command;
	pos = pos/1000;
	command.str("");
	command.clear();
	command << "3 MOV 1 " << pos;
	hub->SendGCSCommand(command.str());
	return DEVICE_OK;
}

int CFluoSEMStageZ::SetRelativePositionUm(double &pos)
{
	CFluoSEMStageHub* hub = static_cast<CFluoSEMStageHub*>(GetParentHub());
	std::ostringstream command;
	pos = pos/1000;
	command.str("");
	command.clear();
	command << "3 MVR 1 " << pos;
	hub->SendGCSCommand(command.str());
	return DEVICE_OK;
}

int CFluoSEMStageZ::GetPositionUm(double &pos)
{
	CFluoSEMStageHub* hub = static_cast<CFluoSEMStageHub*>(GetParentHub());
	std::vector<std::string> answer;
	hub->GCSCommandWithAnswer("3 POS? 1", answer, 1);
	int length = answer[0].length();
	if(length > 6)
	{
		try{
			pos = (stod(answer[0].substr(6,length-1)))*1000;
		}
		catch(...)
		{
			return DEVICE_SERIAL_INVALID_RESPONSE;
		}
		return DEVICE_OK;
	}
	else
	{
		return DEVICE_SERIAL_INVALID_RESPONSE;
	}
}

int CFluoSEMStageZ::OnHome(MM::PropertyBase* pProp, MM::ActionType pAct)
{
	std::string state; 
	if (pAct == MM::AfterSet)
	{
		pProp->Get(state);
		if(state == "true")
		{
		CFluoSEMStageHub* hub = static_cast<CFluoSEMStageHub*>(GetParentHub());
		hub->SendGCSCommand("3 SVO 1 1");
		hub->SendGCSCommand("3 GOH 1");
		pProp->Set("false");
		}
	}
	return DEVICE_OK;
}

int CFluoSEMStageZ::OnOrigin(MM::PropertyBase* pProp, MM::ActionType pAct)
{
	std::string state; 
	if (pAct == MM::AfterSet)
	{
		pProp->Get(state);
		if(state == "true")
		{
		CFluoSEMStageHub* hub = static_cast<CFluoSEMStageHub*>(GetParentHub());
		hub->SendGCSCommand("3 POS 1 0.0");
		pProp->Set("false");
		}
	}
	return DEVICE_OK;
}