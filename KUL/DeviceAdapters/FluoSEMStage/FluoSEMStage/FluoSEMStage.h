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

#ifndef _FluoSEMStage_H_
#define _FluoSEMStage_H_

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

class CFluoSEMStageHub : public HubBase<CFluoSEMStageHub>  
{
public:
	CFluoSEMStageHub();
	 ~CFluoSEMStageHub();

	int Initialize();
	int Shutdown();
	void GetName(char* pszName) const;
	bool Busy();

	MM::DeviceDetectionStatus DetectDevice(void);
	int DetectInstalledDevices();

	// property handlers
	int OnPort(MM::PropertyBase* pPropt, MM::ActionType pAct);

	//int OnLogic(MM::PropertyBase* pPropt, MM::ActionType pAct);
	//int OnVersion(MM::PropertyBase* pPropt, MM::ActionType pAct);

	// custom interface for child devices
	bool IsPortAvailable() {return portAvailable_;}
	//bool IsLogicInverted() {return invertedLogic_;}
	//bool IsTimedOutputActive() {return timedOutputActive_;}
	//void SetTimedOutput(bool active) {timedOutputActive_ = active;}

	bool DoReset(int device);
	bool GCSCommandWithAnswer(const std::string command, std::vector<std::string>& answer, int nExpectedLines = -1);
	bool GCSCommandWithAnswer(unsigned char singleByte, std::vector<std::string>& answer, int nExpectedLines = -1);
	bool SendGCSCommand(const std::string command);
	bool SendGCSCommand(unsigned char singlebyte);
	bool ReadGCSAnswer(std::vector<std::string>& answer, int nExpectedLines = -1);
	int GetLastError() const { return lastError_; };



	// static MMThreadLock& GetLock() {return lock_;}

private:
	//int GetControllerVersion(int&);
	std::string port_;
	bool initialized_;
	bool portAvailable_;
	int lastError_;
	bool _isSerialBusy;
	//bool invertedLogic_;
	//bool timedOutputActive_;
	//int version_;

};

class CFluoSEMStageXY : public CXYStageBase<CFluoSEMStageXY>  
{
public:
	CFluoSEMStageXY();
	~CFluoSEMStageXY();

	// MMDevice API
	// ------------
	int Initialize();
	int Shutdown();

	void GetName(char* pszName) const;
	bool Busy();

	// Stage API

	virtual double GetStepSize();
	virtual int SetPositionSteps(long x, long y);
	virtual int GetPositionSteps(long &x, long &y);
	virtual int Home();
	virtual int Stop();
	virtual int SetOrigin();
	
	virtual int GetLimitsUm(double& xMin, double& xMax, double& yMin, double& yMax);
	virtual int GetStepLimits(long& xMin, long& xMax, long& yMin, long& yMax);
	virtual double GetStepSizeXUm();
	virtual double GetStepSizeYUm();
	virtual int SetRelativePositionUm(double dx, double dy);
	virtual int GetPositionUm(double& x, double& y);
	virtual int SetPositionUm(double x, double y);
	int IsXYStageSequenceable(bool& isSequenceable) const {isSequenceable = false; return DEVICE_OK;}

	// action interface
	// ----------------
	int OnReset(MM::PropertyBase* pPropt, MM::ActionType pAct);
	int OnFeedback(MM::PropertyBase* pPropt, MM::ActionType pAct);
private:
	bool initialized_;
	std::string name_;
};

class CFluoSEMStageLR : public CXYStageBase<CFluoSEMStageLR>  
{
public:
	CFluoSEMStageLR();
	~CFluoSEMStageLR();

	// MMDevice API
	// ------------
	int Initialize();
	int Shutdown();

	void GetName(char* pszName) const;
	bool Busy();

	// Stage API

	virtual double GetStepSize();
	virtual int SetPositionSteps(long x, long y);
	virtual int GetPositionSteps(long &x, long &y);
	virtual int Home();
	virtual int Stop();
	virtual int SetOrigin();
	virtual int GetLimitsUm(double& xMin, double& xMax, double& yMin, double& yMax);
	virtual int GetStepLimits(long& xMin, long& xMax, long& yMin, long& yMax);
	virtual double GetStepSizeXUm();
	virtual double GetStepSizeYUm();
	virtual int SetRelativePositionUm(double dx, double dy);

	int IsXYStageSequenceable(bool& isSequenceable) const {isSequenceable = false; return DEVICE_OK;}

	// action interface
	// ----------------
	//int CFluoSEMStageLR::OnStepVoltage(MM::PropertyBase* pProp, MM::ActionType pAct);
	int CFluoSEMStageLR::OnFineVoltageL(MM::PropertyBase* pProp, MM::ActionType pAct);
	int CFluoSEMStageLR::OnFineVoltageR(MM::PropertyBase* pProp, MM::ActionType pAct);
	int OnReset(MM::PropertyBase* pPropt, MM::ActionType pAct);
private:
	bool initialized_;
	std::string name_;
	double _umPerStepUp, _umPerStepDown, _umPerStepLeft, _umPerStepRight;
	double _xPos, _yPos;
	bool _wasLastMoveStepL;
	bool _isFirstMoveL;
	bool _wasLastMoveStepR;
	bool _isFirstMoveR;
	
};

class CFluoSEMStageZ : public CStageBase<CFluoSEMStageZ>  
{
public:
	CFluoSEMStageZ();
	~CFluoSEMStageZ();

	// MMDevice API
	// ------------
	int Initialize();
	int Shutdown();

	void GetName(char* pszName) const;
	bool Busy();

	// Stage API

	int SetPositionUm(double pos);
	/*int GetPositionUm(double& pos);
	//virtual int SetRelativePositionUm(double d);
	double GetStepSize();
	virtual int SetPositionSteps(long steps);
	virtual int GetPositionSteps(long& steps);
	virtual int SetOrigin();
	virtual int GetLimits(double& min, double& max);
	*/


	int GetPositionUm(double& pos) {pos = 0.0; return DEVICE_OK;}
	double GetStepSize() {return 1.0;}
	int GetPositionSteps(long& steps)
	{
		steps = 0;
		return DEVICE_OK;
	}
	int SetPositionSteps(long steps);
	int SetOrigin() { return DEVICE_OK; }
	int GetLimits(double& lower, double& upper)
	{
		lower = -1.0;
		upper = 1.0;
		return DEVICE_OK;
	}
	int Move(double /*v*/) {return DEVICE_OK;}



	int IsStageSequenceable(bool& isSequenceable) const {isSequenceable = false; return DEVICE_OK;}
	bool IsContinuousFocusDrive() const {return false;} 

	// action interface
	// ----------------
	int CFluoSEMStageZ::OnFineVoltage(MM::PropertyBase* pProp, MM::ActionType pAct);
	int CFluoSEMStageZ::OnStepVoltage(MM::PropertyBase* pProp, MM::ActionType pAct);
	int OnReset(MM::PropertyBase* pPropt, MM::ActionType pAct);
private:
	bool initialized_;
	std::string name_;
	double _umPerStepUp, _umPerStepDown;
	bool _wasLastMoveStep;
	bool _isFirstMove;
	double _Pos;
};

#endif //_Arduino_H_
