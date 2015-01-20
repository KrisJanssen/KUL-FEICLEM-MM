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

#include "VariSpec.h"
#include "../../../../micromanager/MMDevice/ModuleInterface.h"
#include "../../../micromanager/MMCore/Error.h"
#include <sstream>
#include <cstdio>

#ifdef WIN32
   #define WIN32_LEAN_AND_MEAN
   #include <windows.h>
   #define snprintf _snprintf 
#endif

const char* g_DeviceNameVariSpec = "VariSpec";
const char* g_TxTerm            = "\r"; //unique termination
const char* g_RxTerm            = "\r"; //unique termination

const char* g_BaudRate_key        = "Baud Rate";
const char* g_Baud9600            = "9600";
const char* g_Baud115200          = "115200";

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
MMThreadLock CVariSpec::lock_;

///////////////////////////////////////////////////////////////////////////////
// Exported MMDevice API
///////////////////////////////////////////////////////////////////////////////
MODULE_API void InitializeModuleData()
{
	RegisterDevice(g_DeviceNameVariSpec, MM::StateDevice, "VariSpec LC Filter");
  // AddAvailableDeviceName(g_DeviceNameFluoSEMStageE861, "E861 Objective Controller");
  // AddAvailableDeviceName(g_DeviceNameFluoSEMStageC867, "C867 Stage Controller");
}

MODULE_API MM::Device* CreateDevice(const char* deviceName)
{
   if (deviceName == 0)
      return 0;

   if (strcmp(deviceName, g_DeviceNameVariSpec) == 0)
   {
      return new CVariSpec();
   }

   return 0;
}

MODULE_API void DeleteDevice(MM::Device* pDevice)
{
   delete pDevice;
}

int ClearPort(MM::Device& device, MM::Core& core, std::string port)
{
   // Clear contents of serial port 
   const int bufSize = 2048;
   unsigned char clear[bufSize];                                                        
   unsigned long read = bufSize;                                               
   int ret;                                                                    
   while ((int) read == bufSize)                                                     
   {                                                                           
      ret = core.ReadFromSerial(&device, port.c_str(), clear, bufSize, read); 
      if (ret != DEVICE_OK)                                                    
         return ret;                                                           
   }                                                                           
   return DEVICE_OK;                                                           
}

///////////////////////////////////////////////////////////////////////////////
// CArduinoHUb implementation
// ~~~~~~~~~~~~~~~~~~~~~~~~~~
//
CVariSpec::CVariSpec() :
initialized_ (false),
	answerTimeoutMs_(1000),
	numPos_(0),
	baud_("115200"), exercising_(false)

{
   portAvailable_ = false;

   InitializeDefaultErrorMessages();

   SetErrorText(COM_ERROR, "COM Error");
   SetErrorText(PI_CNTR_NO_ERROR, "No Error");
   SetErrorText(PI_CNTR_UNKNOWN_COMMAND, "Unknown Command");

   CPropertyAction* pAct = new CPropertyAction(this, &CVariSpec::OnPort);
   CreateProperty(MM::g_Keyword_Port, "Undefined", MM::String, false, pAct, true);
}

CVariSpec::~CVariSpec()
{
   Shutdown();
}

void CVariSpec::GetName(char* name) const
{
   CDeviceUtils::CopyLimitedString(name, g_DeviceNameVariSpec);
}

bool CVariSpec::Busy()
{
   return false;
}

MM::DeviceDetectionStatus CVariSpec::DetectDevice(void)
{
MM::DeviceDetectionStatus result = MM::Misconfigured;

   try
   {	   
	   long baud = 115200;
	   

      std::string transformed = port_;
      for( std::string::iterator its = transformed.begin(); its != transformed.end(); ++its)
      {
         *its = (char)tolower(*its);
      }	  	     

      if( 0< transformed.length() &&  0 != transformed.compare("undefined")  && 0 != transformed.compare("unknown") )
      {
		int ret = 0;	  
		MM::Device* pS;

		
			 // the port property seems correct, so give it a try
			 result = MM::CanNotCommunicate;
			 // device specific default communication parameters
			 GetCoreCallback()->SetDeviceProperty(port_.c_str(), MM::g_Keyword_AnswerTimeout, "2000.0");			 
			 GetCoreCallback()->SetDeviceProperty(port_.c_str(), MM::g_Keyword_BaudRate, baud_.c_str() );
			 GetCoreCallback()->SetDeviceProperty(port_.c_str(), MM::g_Keyword_DelayBetweenCharsMs, "0.0");
			 GetCoreCallback()->SetDeviceProperty(port_.c_str(), MM::g_Keyword_Handshaking, "Off");
			 GetCoreCallback()->SetDeviceProperty(port_.c_str(), MM::g_Keyword_Parity, "None");
			 GetCoreCallback()->SetDeviceProperty(port_.c_str(), MM::g_Keyword_StopBits, "1");
			 GetCoreCallback()->SetDeviceProperty(port_.c_str(), "Verbose", "1");
			 pS = GetCoreCallback()->GetDevice(this, port_.c_str());
			 pS->Initialize();
	         
			 ClearPort(*this, *GetCoreCallback(), port_);
			 ret = SendSerialCommand(port_.c_str(), "V?", "\r");     
			 GetSerialAnswer (port_.c_str(), "\r", serialnum_);
			 GetSerialAnswer (port_.c_str(), "\r", serialnum_);
				 if (ret!=DEVICE_OK || serialnum_.length() < 5)
				 {
					LogMessageCode(ret,true);
					LogMessage(std::string("VariLC not found on ")+port_.c_str(), true);
					LogMessage(std::string("VariLC serial no:")+serialnum_, true);
					ret = 1;
					serialnum_ = "0";
					pS->Shutdown();	
				 } else
				 {
					// to succeed must reach here....
					LogMessage(std::string("VariLC found on ")+port_.c_str(), true);
					LogMessage(std::string("VariLC serial no:")+serialnum_, true);
					result = MM::CanCommunicate;	
					GetCoreCallback()->SetSerialProperties(port_.c_str(),
											  "600.0",
											  baud_.c_str(),
											  "0.0",
											  "Off",
											  "None",
											  "1");
					serialnum_ = "0";
					pS->Initialize();
					ret = SendSerialCommand(port_.c_str(), "R 1", "\r");
					ret = SendSerialCommand(port_.c_str(), "C 0", "\r");
					pS->Shutdown();					
				}
      }
   }
   catch(...)
   {
      LogMessage("Exception in DetectDevice!",false);
   }
   return result;
}

int CVariSpec::Initialize()
{
   // Name
   int ret = CreateProperty(MM::g_Keyword_Name, g_DeviceNameVariSpec, MM::String, true);
   std::vector<std::string> answer;

   // The first second or so after opening the serial port, the Arduino is waiting for firmwareupgrades.  Simply sleep 1 second.
  

  // MMThreadGuard myLock(lock_);

   // Check that we have a controller:
   try{
	ClearPort(*this, *GetCoreCallback(), port_);
	ResetError();
	SendCommandWithAnswer("B 0", answer, 1);
    SendCommandWithAnswer("V?", answer, 2);
   }
   catch(...)
   {
	   LogMessage("Cannot Communicate with filter",false);
   }

   if(answer[1][0] != 'V')
   {
      return DEVICE_SERIAL_COMMAND_FAILED;
   }
   // turn off verbose serial debug messages
   GetCoreCallback()->SetDeviceProperty(port_.c_str(), "Verbose", "0");
   
   InitializeFilter();
   std::vector<std::string> BOOLOPT;
	BOOLOPT.push_back("true");
	BOOLOPT.push_back("false");

   CPropertyAction* pAct = new CPropertyAction (this, &CVariSpec::OnWavelength);
   ret = CreateProperty("Wavelength", "400.0", MM::Float, false, pAct); 
   assert(DEVICE_OK == ret);
   SetPropertyLimits("Wavelength", 400., 720.);
   pAct = new CPropertyAction (this, &CVariSpec::OnConditioning);
   ret = CreateProperty("Conditioning", "false", MM::String, false, pAct);
   assert(DEVICE_OK == ret);
   ret = SetAllowedValues("Conditioning", BOOLOPT);
   assert(DEVICE_OK == ret);

   initialized_ = true;
   return DEVICE_OK;
}

int CVariSpec::Shutdown()
{
   initialized_ = false;
   return DEVICE_OK;
}

bool CVariSpec::SendCommand(const std::string command)
{
   int ret = SendSerialCommand(port_.c_str(), command.c_str(), "\r");
   if (ret != DEVICE_OK)
   {
	   lastError_ = ret;
      return false;
   }
	return true;
}
bool CVariSpec::SendCommand(unsigned char command)
{
	
   int ret = WriteToComPort(port_.c_str(), &command, 1);
   if (ret != DEVICE_OK)
   {
	   lastError_ = ret;
      return false;
   }
	return true;
}

bool CVariSpec::ReadCommand(unsigned char& command)
{
	unsigned long read = 0; 
	unsigned int counter = 0; 
	int ret;
	while (read == 0  && counter < 1000) 
	{ 
		ret = ReadFromComPort(port_.c_str(), &command, 1, read);  
		CDeviceUtils::SleepMs(5); 
		counter++; 
	} 
    if (ret != DEVICE_OK)
	{
	   lastError_ = ret;
      return false;
	}
	return true;
}

bool CVariSpec::SendCommandWithAnswer(const std::string command, std::vector<std::string>& answer, int nExpectedLines)
{
	if (!SendCommand(command))
		return false;
	return ReadAnswer(answer, nExpectedLines);
}

bool CVariSpec::ReadAnswer(std::vector<std::string>& answer, int nExpectedLines)
{
	answer.clear();
   std::string line;
   do
   {
	   // block/wait for acknowledge, or until we time out;
	   int ret = GetSerialAnswer(port_.c_str(), "\r", line);
	   if (ret != DEVICE_OK)
	   {
		   lastError_ = ret;
		  return false;
	   }
	   answer.push_back(line);
   } while( !line.empty());
   if ((unsigned) nExpectedLines >=0 && answer.size() != (unsigned ) nExpectedLines)
	   return false;
	return true;
}

bool CVariSpec::ResetError()
{
	std::vector<std::string> answer;
	unsigned char ans;
	SendCommand('\x01B');
	ReadCommand(ans);
	SendCommandWithAnswer("R 1",answer);
	return true;
}
bool CVariSpec::InitializeFilter()
{
	std::vector<std::string> answer;
	SendCommandWithAnswer("I 1", answer);
	return true;
}
bool CVariSpec::ExerciseFilter()
{
	std::vector<std::string> answer;
	unsigned char busy = '<';
	unsigned int counter = 0; 
	SendCommandWithAnswer("E 3", answer);
	while(busy == '<' && counter < 1000)
	{
		SendCommand('!');
		ReadCommand(busy);
		ReadCommand(busy);
		counter++;
	}
	return true;
}
bool CVariSpec::HaltFilter()
{
	unsigned char ans;
	std::vector<std::string> answer;
	SendCommand('\x01B');
	ReadCommand(ans);
	SendCommandWithAnswer("R 1", answer);
	return true;
}

int CVariSpec::OnPort(MM::PropertyBase* pProp, MM::ActionType pAct)
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

int CVariSpec::OnWavelength (MM::PropertyBase* pProp, MM::ActionType eAct)
 {
   if (eAct == MM::BeforeGet)
   {
     int ret = SendSerialCommand(port_.c_str(), "W?", "\r");
         if (ret!=DEVICE_OK)return DEVICE_SERIAL_COMMAND_FAILED;
     std::string ans;
	 GetSerialAnswer (port_.c_str(), "\r", ans);
	 GetSerialAnswer (port_.c_str(), "\r", ans);  
     pProp->Set(atof(ans.substr(2, 7).c_str()));
   }
   else if (eAct == MM::AfterSet)
   {
	  double wavelength;
      // read value from property
      pProp->Get(wavelength);
      // write wavelength out to device....
	  std::ostringstream cmd;
	  cmd.precision(5);
	  cmd << "W " << wavelength;
     int ret = SendSerialCommand(port_.c_str(), cmd.str().c_str(), "\r");
     if (ret!=DEVICE_OK)
	     return DEVICE_SERIAL_COMMAND_FAILED;
     std::string ans;
     GetSerialAnswer (port_.c_str(), "\r", ans);
	 
    // wavelength_ = wavelength;
// Clear palette elements after change of wavelength
   }
   return DEVICE_OK;
 }

int CVariSpec::OnConditioning (MM::PropertyBase* pProp, MM::ActionType eAct)
 {
   if (eAct == MM::BeforeGet)
   {
	   if(exercising_ == true)
	   {
		   pProp->Set("true");
	   }
	   else
	   {
		   pProp->Set("false");
	   }
   }
   else if (eAct == MM::AfterSet)
   {
	   std::string state;
	   pProp->Get(state);
	   if(state == "true")
	   {
		   exercising_ = true;
		   ExerciseFilter();
		   exercising_ = false;
		   pProp->Set("false");
	   }
   }
   return DEVICE_OK;
 }


int 	CVariSpec::SetPosition (long pos)
{
	std::vector<std::string> answer;
	SendCommandWithAnswer("W " + pos, answer);
	return DEVICE_OK;
}
int 	CVariSpec::GetPosition (long &pos)
{
	std::string line;
	std::vector<std::string> response;
	SendCommandWithAnswer("W ?", response, 2);
	line = response[1];
	pos = atoi(line.substr(2, 3).c_str());
	return DEVICE_OK;
}




