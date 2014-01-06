/*!

  \fei_info         This is automatically generated comment header.
                    Only item 'brief' is possible to update.

  \file             xt/automation/xtlibcheck/src/xtliberror.h
  \brief            

  \copyright        Copyright (c) 1995-2008 by FEI Company
                    All rights reserved. This file includes confidential and
                    proprietary information of FEI Company.

  \fei_owner        mnov

  \fei_coowners     mmanders
                    dryb

  \fei_xt_ach_main  

  \fei_xt_bno_main  

  \fei_xt_hbo_main  

 */
 /* COMMON errors */
//
//  Values are 32 bit values layed out as follows:
//
//   3 3 2 2 2 2 2 2 2 2 2 2 1 1 1 1 1 1 1 1 1 1
//   1 0 9 8 7 6 5 4 3 2 1 0 9 8 7 6 5 4 3 2 1 0 9 8 7 6 5 4 3 2 1 0
//  +---+-+-+-----------------------+-------------------------------+
//  |Sev|C|R|     Facility          |               Code            |
//  +---+-+-+-----------------------+-------------------------------+
//
//  where
//
//      Sev - is the severity code
//
//          00 - Success
//          01 - Informational
//          10 - Warning
//          11 - Error
//
//      C - is the Customer code flag
//
//      R - is a reserved bit
//
//      Facility - is the facility code
//
//      Code - is the facility's status code
//
//
// Define the facility codes
//
#define FACILITY_XTLIB                   0x224
#define FACILITY_SYSTEM                  0x1
#define FACILITY_STUBS                   0x3
#define FACILITY_RUNTIME                 0x2
#define FACILITY_IO_ERROR_CODE           0x4


//
// Define the severity codes
//
#define STATUS_SEVERITY_WARNING          0x2
#define STATUS_SEVERITY_SUCCESS          0x0
#define STATUS_SEVERITY_INFORMATIONAL    0x1
#define STATUS_SEVERITY_ERROR            0x3


//
// MessageId: XTLIB_E_UNKNOWN_ERROR
//
// MessageText:
//
//  Unknown Error.
//
#define XTLIB_E_UNKNOWN_ERROR            ((DWORD)0xC2240000L)

//
// MessageId: XTLIB_E_NOT_INITIALIZED
//
// MessageText:
//
//  Object %1 isn't initialized.
//
#define XTLIB_E_NOT_INITIALIZED          ((DWORD)0xC2240001L)

//
// MessageId: XTLIB_E_INVALID_ENUM_VALUE
//
// MessageText:
//
//  Value is an invalid value for enumtype : %1.
//
#define XTLIB_E_INVALID_ENUM_VALUE       ((DWORD)0xC2240002L)

//
// MessageId: XTLIB_E_INVALID_POINTER
//
// MessageText:
//
//  Invalid inputpointer(s).
//
#define XTLIB_E_INVALID_POINTER          ((DWORD)0xC2240003L)

//
// MessageId: XTLIB_E_INVALID_VALUE_TYPE
//
// MessageText:
//
//  the invalid valuetype.
//
#define XTLIB_E_INVALID_VALUE_TYPE       ((DWORD)0xC2240004L)

//
// MessageId: XTLIB_E_INVALID_VALUE
//
// MessageText:
//
//  invalid value %1
//
#define XTLIB_E_INVALID_VALUE            ((DWORD)0xC2240005L)

//
// MessageId: XTLIB_E_INVALID_RANGE_VALUE
//
// MessageText:
//
//  value %1 out of range.
//
#define XTLIB_E_INVALID_RANGE_VALUE      ((DWORD)0xC2240006L)

//
// MessageId: XTLIB_E_CONNECTION_ERROR
//
// MessageText:
//
//  Can't connect to microscope.
//
#define XTLIB_E_CONNECTION_ERROR         ((DWORD)0xC2240007L)

//
// MessageId: XTLIB_E_INSTRUMENT_ERROR
//
// MessageText:
//
//  Error on instrument level : %1.
//
#define XTLIB_E_INSTRUMENT_ERROR         ((DWORD)0xC2240008L)

//
// MessageId: XTLIB_E_ITEM_NOT_FOUND
//
// MessageText:
//
//  Item not found in collection.
//
#define XTLIB_E_ITEM_NOT_FOUND           ((DWORD)0xC2240009L)

//
// MessageId: XTLIB_E_LOWLEVEL_OBJECT_NOT_INITIALIZED
//
// MessageText:
//
//  Low level object : %1 not initialized.
//
#define XTLIB_E_LOWLEVEL_OBJECT_NOT_INITIALIZED ((DWORD)0xC224000AL)

//
// MessageId: XTLIB_E_INVALID_FILENAME
//
// MessageText:
//
//  Invalid filename.
//
#define XTLIB_E_INVALID_FILENAME         ((DWORD)0xC224000BL)

//
// MessageId: XTLIB_E_INVALID_CHANNELSTATE
//
// MessageText:
//
//  Invalid channelstate for this action.
//
#define XTLIB_E_INVALID_CHANNELSTATE     ((DWORD)0xC224000CL)

//
// MessageId: XTLIB_E_TOO_LATE_TO_SWITCH_OFF_EVENT_REGISTRATION
//
// MessageText:
//
//  Too late for disabling event registration.
//
#define XTLIB_E_TOO_LATE_TO_SWITCH_OFF_EVENT_REGISTRATION ((DWORD)0xC224000DL)

