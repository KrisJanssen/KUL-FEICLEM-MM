 //////////////////////////////////////////////////////////////////////////////
// FILE:          FluoSEMStage.h
// PROJECT:       Micro-Manager
// SUBSYSTEM:     DeviceAdapters
//-----------------------------------------------------------------------------
// DESCRIPTION:   Adapter for PIController
//                Needs accompanying firmware to be installed on the board
// COPYRIGHT:     University of California, San Francisco, 2008
// LICENSE:       LGPL
//
// AUTHOR:        Nico Stuurman, nico@cmp.ucsf.edu, 11/09/2008
//                automatic device detection by Karl Hoover.
//				  Adaptation to KUL Quanta FEG 250 CLEM stage by Thomas Franklin, 2013
//

#ifndef _VariSpec_H_
#define _VariSpec_H_

#define COM_ERROR -1L
#define PI_CNTR_NO_ERROR  0L
#define PI_CNTR_UNKNOWN_COMMAND 2L

#include "../../../../micromanager/MMDevice/MMDevice.h"
#include "../../../../micromanager/MMDevice/DeviceBase.h"
#include <string>
#include <map>

//////////////////////////////////////////////////////////////////////////////
// Error codes
//

//class ArduinoInputMonitorThread;

class CVariSpec : public CStateDeviceBase<CVariSpec>  
{
public:
   CVariSpec();
   ~CVariSpec();

   int Initialize();
   int Shutdown();
   void GetName(char* pszName) const;
   bool Busy();

   MM::DeviceDetectionStatus DetectDevice(void);
 //  int DetectInstalledDevices();

   // property handlers
   int OnPort(MM::PropertyBase* pPropt, MM::ActionType eAct);

   //int OnLogic(MM::PropertyBase* pPropt, MM::ActionType eAct);
   //int OnVersion(MM::PropertyBase* pPropt, MM::ActionType eAct);

   // custom interface for child devices
   bool IsPortAvailable() {return portAvailable_;}
   //bool IsLogicInverted() {return invertedLogic_;}
   //bool IsTimedOutputActive() {return timedOutputActive_;}
   //void SetTimedOutput(bool active) {timedOutputActive_ = active;}

   
   bool SendCommandWithAnswer(const std::string command, std::vector<std::string>& answer, int nExpectedLines = -1); 
   bool SendCommand(const std::string command);  
   bool SendCommand(unsigned char command); 
   bool ReadCommand(unsigned char& command);
   bool ResetError();
   bool HaltFilter();
   bool ExerciseFilter();
   bool InitializeFilter();
   bool ReadAnswer(std::vector<std::string>& answer, int nExpectedLines = -1);
   int GetLastError() const { return lastError_; };

   unsigned long GetNumberOfPositions()const {return numPos_;}
	virtual int 	SetPosition (long pos);
 	virtual int 	GetPosition (long &pos);
   static MMThreadLock& GetLock() {return lock_;}

   int OnWavelength (MM::PropertyBase* pProp, MM::ActionType eAct);

private:
   //int GetControllerVersion(int&);
   std::string port_;
   bool initialized_;
   bool portAvailable_;
   int lastError_;
   int numPos_;
   std::string baud_;
   //bool invertedLogic_;
   //bool timedOutputActive_;
   //int version_;
   static MMThreadLock lock_;
   long answerTimeoutMs_;
   std::string serialnum_;
};

#endif //_Arduino_H_
