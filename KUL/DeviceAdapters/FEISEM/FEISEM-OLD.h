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

   int OnPressure(MM::PropertyBase* pProp, MM::ActionType eAct);
   int OnChamberState(MM::PropertyBase* pProp, MM::ActionType eAct);
   int OnChamberMode(MM::PropertyBase* pProp, MM::ActionType eAct);
   int OnScanMode(MM::PropertyBase* pProp, MM::ActionType eAct);
   int OnDwellTime(MM::PropertyBase* pProp, MM::ActionType eAct);
   int OnHPixels(MM::PropertyBase* pProp, MM::ActionType eAct);
   int OnVPixels(MM::PropertyBase* pProp, MM::ActionType eAct);
   int OnSelectedAreaX(MM::PropertyBase* pProp, MM::ActionType eAct);
   int OnSelectedAreaY(MM::PropertyBase* pProp, MM::ActionType eAct);
   int OnSelectedAreaXStart(MM::PropertyBase* pProp, MM::ActionType eAct);
   int OnSelectedAreaYStart(MM::PropertyBase* pProp, MM::ActionType eAct);
   int OnRotation(MM::PropertyBase* pProp, MM::ActionType eAct);
   int OnFOV(MM::PropertyBase* pProp, MM::ActionType eAct);
   int OnSelectedEnabled(MM::PropertyBase* pProp, MM::ActionType eAct);
   int OnSpotX(MM::PropertyBase* pProp, MM::ActionType eAct);
   int OnSpotY(MM::PropertyBase* pProp, MM::ActionType eAct);
   int OnHTState(MM::PropertyBase* pProp, MM::ActionType eAct);
   int OnAcceleratingVoltage(MM::PropertyBase* pProp, MM::ActionType eAct);
   int OnSpotSize(MM::PropertyBase* pProp, MM::ActionType eAct);
   int OnBeamBlank(MM::PropertyBase* pProp, MM::ActionType eAct);
   int OnStigmationX(MM::PropertyBase* pProp, MM::ActionType eAct);
   int OnStigmationY(MM::PropertyBase* pProp, MM::ActionType eAct);
   int OnBeamShiftX(MM::PropertyBase* pProp, MM::ActionType eAct);
   int OnBeamShiftY(MM::PropertyBase* pProp, MM::ActionType eAct);
   int OnSaveImage(MM::PropertyBase* pProp, MM::ActionType eAct);

private:
   bool initialized_;
   bool busy_;
   int counter_;
   

      IBeamControl * pIBeamControl;
        IElectronBeamControl * pIElectronBeamControl;
        IMicroscopeControl * pIMicroscopeControl;
		IScanControl * pIScanControl;
		IVideoControl * pIVideoControl;
		IChannels * pIChannels;
		IChannel* pIChannel;
	  IVacSystemControl * pIVacSystemControl;
	  IConnectionPointContainer * pIConnectionPointContainer;
	  IDispatch * pIDispatch;
	  FEISEMHub* hub;
};
//
////////////////////////////////////////////////////////////////////////////////
//// CDemoCamera class
//// Simulation of the Camera device
////////////////////////////////////////////////////////////////////////////////
///*
//class FEISEMCamera : public CCameraBase<FEISEMCamera>
//{
//public:
//	FEISEMCamera();
//	~FEISEMCamera();
//
//	int Initialize();
//    int Shutdown();
//    void GetName(char* pszName) const;
//    bool Busy(){ return busy_;} ;
//
//	  int SnapImage();
//      /**
//       * Returns pixel data.
//       * Required by the MM::Camera API.
//       * GetImageBuffer will be called shortly after SnapImage returns.  
//       * Use it to wait for camera read-out and transfer of data into memory
//       * Return a pointer to a buffer containing the image data
//       * The calling program will assume the size of the buffer based on the values
//       * obtained from GetImageBufferSize(), which in turn should be consistent with
//       * values returned by GetImageWidth(), GetImageHight() and GetImageBytesPerPixel().
//       * The calling program allso assumes that camera never changes the size of
//       * the pixel buffer on its own. In other words, the buffer can change only if
//       * appropriate properties are set (such as binning, pixel type, etc.)
//       * Multi-Channel cameras should return the content of the first channel in this call.
//       *
//       */
//        const unsigned char* GetImageBuffer();
//      /**
//       * Returns pixel data for cameras with multiple channels.
//       * See description for GetImageBuffer() for details.
//       * Use this overloaded version for cameras with multiple channels
//       * When calling this function for a single channel camera, this function
//       * should return the content of the imagebuffer as returned by the function
//       * GetImageBuffer().  This behavior is implemented in the DeviceBase.
//       * When GetImageBuffer() is called for a multi-channel camera, the 
//       * camera adapter should return the ImageBuffer for the first channel
//       * @param channelNr Number of the channel for which the image data are requested.
//       */
//        const unsigned char* GetImageBuffer(unsigned channelNr);
//      /**
//       * Returns pixel data with interleaved RGB pixels in 32 bpp format
//       */
//        const unsigned int* GetImageBufferAsRGB32();
//      /**
//       * Returns the number of components in this image.  This is '1' for grayscale cameras,
//       * and '4' for RGB cameras.
//       */
//        unsigned GetNumberOfComponents() const;
//      /**
//       * Returns the name for each component 
//       */
//        int GetComponentName(unsigned component, char* name);
//      /**
//       * Returns the number of simultaneous channels that camera is capaable of.
//       * This should be used by devices capable of generating mutiple channels of imagedata simultanuously.
//       * Note: this should not be used by color cameras (use getNumberOfComponents instead).
//       */
//        int unsigned GetNumberOfChannels() const;
//      /**
//       * Returns the name for each Channel.
//       * An implementation of this function is provided in DeviceBase.h.  It will return an empty string
//       */
//        int GetChannelName(unsigned channel, char* name);
//      /**
//       * Returns the size in bytes of the image buffer.
//       * Required by the MM::Camera API.
//       * For multi-channel cameras, return the size of a single channel
//       */
//        long GetImageBufferSize()const;
//      /**
//       * Returns image buffer X-size in pixels.
//       * Required by the MM::Camera API.
//       */
//        unsigned GetImageWidth() const;
//      /**
//       * Returns image buffer Y-size in pixels.
//       * Required by the MM::Camera API.
//       */
//        unsigned GetImageHeight() const;
//      /**
//       * Returns image buffer pixel depth in bytes.
//       * Required by the MM::Camera API.
//       */
//        unsigned GetImageBytesPerPixel() const;
//      /**
//       * Returns the bit depth (dynamic range) of the pixel.
//       * This does not affect the buffer size, it just gives the client application
//       * a guideline on how to interpret pixel values.
//       * Required by the MM::Camera API.
//       */
//        unsigned GetBitDepth() const;
//      /**
//       * Returns binnings factor.  Used to calculate current pixelsize
//       * Not appropriately named.  Implemented in DeviceBase.h
//       */
//        double GetPixelSizeUm() const;
//      /**
//       * Returns the current binning factor.
//       */
//        int GetBinning() const;
//      /**
//       * Sets binning factor.
//       */
//        int SetBinning(int binSize);
//      /**
//       * Sets exposure in milliseconds.
//       */
//        void SetExposure(double exp_ms);
//      /**
//       * Returns the current exposure setting in milliseconds.
//       */
//        double GetExposure() const;
//      /**
//       * Sets the camera Region Of Interest.
//       * Required by the MM::Camera API.
//       * This command will change the dimensions of the image.
//       * Depending on the hardware capabilities the camera may not be able to configure the
//       * exact dimensions requested - but should try do as close as possible.
//       * If the hardware does not have this capability the software should simulate the ROI by
//       * appropriately cropping each frame.
//       * @param x - top-left corner coordinate
//       * @param y - top-left corner coordinate
//       * @param xSize - width
//       * @param ySize - height
//       */
//        int SetROI(unsigned x, unsigned y, unsigned xSize, unsigned ySize); 
//      /**
//       * Returns the actual dimensions of the current ROI.
//       */
//        int GetROI(unsigned& x, unsigned& y, unsigned& xSize, unsigned& ySize);
//      /**
//       * Resets the Region of Interest to full frame.
//       */
//        int ClearROI();
//      /**
//       * Starts continuous acquisition.
//       */
//        int StartSequenceAcquisition(long numImages, double interval_ms, bool stopOnOverflow);
//      /**
//       * Starts Sequence Acquisition with given interval.  
//       * Most camera adapters will ignore this number
//       * */
//        int StartSequenceAcquisition(double interval_ms);
//      /**
//       * Stops an ongoing sequence acquisition
//       */
//        int StopSequenceAcquisition();
//      /**
//       * Sets up the camera so that Sequence acquisition can start without delay
//       */
//        int PrepareSequenceAcqusition();
//      /**
//       * Flag to indicate whether Sequence Acquisition is currently running.
//       * Return true when Sequence acquisition is activce, false otherwise
//       */
//        bool IsCapturing();
//
//      /**
//       * Get the metadata tags stored in this device.
//       * These tags will automatically be add to the metadata of an image inserted 
//       * into the circular buffer
//       *
//       */
//        void GetTags(char* serializedMetadata);
//
//      /**
//       * Adds new tag or modifies the value of an existing one 
//       * These will automatically be added to images inserted into the circular buffer.
//       * Use this mechanism for tags that do not change often.  For metadata that
//       * change often, create an instance of metadata yourself and add to one of 
//       * the versions of the InsertImage function
//       */
//        void AddTag(const char* key, const char* deviceLabel, const char* value);
//
//      /**
//       * Removes an existing tag from the metadata assoicated with this device
//       * These tags will automatically be add to the metadata of an image inserted 
//       * into the circular buffer
//       */
//        void RemoveTag(const char* key);
//
//      /*
//       * Returns whether a camera's exposure time can be sequenced.
//       * If returning true, then a Camera adapter class should also inherit
//       * the SequenceableExposure class and implement its methods.
//       */
//        int IsExposureSequenceable(bool& isSequenceable) const;
//
//      // Sequence functions
//      // Sequences can be used for fast acquisitions, sycnchronized by TTLs rather than
//      // computer commands. 
//      // Sequences of exposures can be uploaded to the camera.  The camera will cycle through
//      // the uploaded list of exposures (triggered by either an internal or 
//      // external trigger).  If the device is capable (and ready) to do so isSequenceable will
//      // be true. If your device can not execute this (true for most cameras)
//      // simply set IsExposureSequenceable to false
//        int GetExposureSequenceMaxLength(long& nrEvents) const;
//        int StartExposureSequence();
//        int StopExposureSequence();
//      // Remove all values in the sequence
//        int ClearExposureSequence( );
//      // Add one value to the sequence
//        int AddToExposureSequence(double exposureTime_ms);
//      // Signal that we are done sending sequence values so that the adapter can send the whole sequence to the device
//        int SendExposureSequence() const;
//
//
//
//
//
//   int OnPressure(MM::PropertyBase* pProp, MM::ActionType eAct);
//   int OnChamberState(MM::PropertyBase* pProp, MM::ActionType eAct);
//   int OnChamberMode(MM::PropertyBase* pProp, MM::ActionType eAct);
//   int OnScanMode(MM::PropertyBase* pProp, MM::ActionType eAct);
//   int OnDwellTime(MM::PropertyBase* pProp, MM::ActionType eAct);
//   int OnHPixels(MM::PropertyBase* pProp, MM::ActionType eAct);
//   int OnVPixels(MM::PropertyBase* pProp, MM::ActionType eAct);
//   int OnSelectedAreaX(MM::PropertyBase* pProp, MM::ActionType eAct);
//   int OnSelectedAreaY(MM::PropertyBase* pProp, MM::ActionType eAct);
//   int OnSelectedAreaXStart(MM::PropertyBase* pProp, MM::ActionType eAct);
//   int OnSelectedAreaYStart(MM::PropertyBase* pProp, MM::ActionType eAct);
//   int OnRotation(MM::PropertyBase* pProp, MM::ActionType eAct);
//   int OnFOV(MM::PropertyBase* pProp, MM::ActionType eAct);
//   int OnSelectedEnabled(MM::PropertyBase* pProp, MM::ActionType eAct);
//   int OnSpotX(MM::PropertyBase* pProp, MM::ActionType eAct);
//   int OnSpotY(MM::PropertyBase* pProp, MM::ActionType eAct);
//   int OnHTState(MM::PropertyBase* pProp, MM::ActionType eAct);
//   int OnAcceleratingVoltage(MM::PropertyBase* pProp, MM::ActionType eAct);
//   int OnSpotSize(MM::PropertyBase* pProp, MM::ActionType eAct);
//   int OnBeamBlank(MM::PropertyBase* pProp, MM::ActionType eAct);
//   int OnStigmationX(MM::PropertyBase* pProp, MM::ActionType eAct);
//   int OnStigmationY(MM::PropertyBase* pProp, MM::ActionType eAct);
//   int OnBeamShiftX(MM::PropertyBase* pProp, MM::ActionType eAct);
//   int OnBeamShiftY(MM::PropertyBase* pProp, MM::ActionType eAct);
//
//private:
//   bool initialized_;
//   bool busy_;
//   std::string name_;
//
//        IMicroscopeControl * pIMicroscopeControl;
//		IScanControl * pIScanControl;
//		IVideoControl * pIVideoControl;
//	  IVideoGroups * pIVideoGroups;
//	  IChannel * pIChannel;
//	  IChannels * pIChannels;
//	  IVideoGroup * pIVideoGroup;
//	  IConnectionPointContainer * pIConnectionPointContainer;
//	  IDispatch * pIDispatch;
//	  FEISEMHub* hub;
//	  ImgBuffer img_;
//};



#endif //_FEISEM_H_
