package Core;

public interface Opcode
{
    final byte CMSG_LOGIN                   = 0x01;
    final byte CMSG_LOGOUT                  = 0x02;
    final byte SMSG_LOGIN_SUCCESS           = 0x03;
    final byte SMSG_LOGIN_FAILED            = 0x04;
    final byte SMSG_MULTI_LOGIN             = 0x05;
    final byte CMSG_GET_CONTACT_LIST        = 0x06;
    final byte CMSG_ADD_CONTACT             = 0x07;
    final byte CMSG_REMOVE_CONTACT          = 0x08;
    final byte SMSG_ADD_CONTACT_SUCCESS     = 0x09;
    final byte CMSG_STATUS_CHANGED          = 0x0A;
    final byte SMSG_CONTACT_DETAIL          = 0x0B;
    final byte SMSG_CONTACT_LIST_ENDED      = 0x0C;
    final byte CMSG_SEND_CHAT_MESSAGE       = 0x0D;
    final byte SMSG_SEND_CHAT_MESSAGE       = 0x0E;
    final byte SMSG_STATUS_CHANGED          = 0x0F;
    final byte SMSG_CONTACT_ALREADY_IN_LIST = 0x10;
    final byte SMSG_CONTACT_NOT_FOUND       = 0x11;
    final byte SMSG_CONTACT_REQUEST         = 0x12;
    final byte CMSG_CONTACT_ACCEPT          = 0x13;
    final byte CMSG_CONTACT_DECLINE         = 0x14;
    final byte CMSG_TIME_SYNC_RESP          = 0x15;
    final byte CMSG_PING                    = 0x16;
    final byte SMSG_PING                    = 0x17;
    final byte SMSG_LOGOUT_COMPLETE         = 0x18;
    final byte CMSG_TITLE_CHANGED           = 0x19;
    final byte SMSG_TITLE_CHANGED           = 0x1A;
    final byte CMSG_PSM_CHANGED             = 0x1B;
    final byte SMSG_PSM_CHANGED             = 0x1C;
    final byte CMSG_CREATE_ROOM             = 0x1D;
    final byte SMSG_CREATE_ROOM_FAILED      = 0x1E;
    final byte CMSG_JOIN_ROOM               = 0x1F;
    final byte CMSG_LEAVE_ROOM              = 0x20;
    final byte SMSG_JOIN_ROOM               = 0x21;
    final byte SMSG_LEAVE_ROOM              = 0x22;
    final byte SMSG_JOIN_ROOM_SUCCESS       = 0x23;
    final byte SMSG_LEAVE_ROOM_SUCCESS      = 0x24;
    final byte CMSG_ROOM_CHAT               = 0x25;
    final byte SMSG_ROOM_CHAT               = 0x26;
    final byte SMSG_ROOM_NOT_FOUND          = 0x27;
    final byte SMSG_WRONG_ROOM_PASSWORD     = 0x28;
    final byte SMSG_ROOM_MEMBER_DETAIL      = 0x29;
    final byte SMSG_ALREADY_IN_ROOM         = 0x2A;
    final byte CMSG_REGISTRATION            = 0x2B;
    final byte SMSG_REGISTRATION_SUCCESS    = 0x2C;
    final byte SMSG_REGISTRATION_ALREADY    = 0x2D;
    final byte CMSG_GET_CHAT_MESSAGE        = 0x2E;
    final byte SMSG_GET_CHAT_MESSAGE        = 0x2F;
    final byte СMSG_ADD_NEW_SUBSCRIBE       = 0x30;
    final byte CMSG_GET_SUBLIST             = 0x31;
    final byte SMSG_GET_SUBLIST_SUCCESS     = 0x32;
    final byte CMSG_SUBSCRIBE               = 0x33;
    final byte CMSG_UNSUBSCRIBE             = 0x34;
    final byte SMSG_SUBSCRIBE_SUCCESS       = 0x35;
    final byte SMSG_UNSUBSCRIBE_SUCCESS     = 0x36;
    final byte СMSG_SEND_IN_SUB             = 0x37;
    final byte SMSG_SEND_IN_SUB_SUCCESS     = 0x38;
    final byte SMSG_SEND_IN_SUB             = 0x39;
    final byte CMSG_GET_IN_SUB              = 0x3A;
    final byte CMSG_GET_OFFLINE_MSG         = 0x3B;
    
    enum SessionStatus
    {
        NEVER,         // Opcode is never send by a client, maybe a server opcode, or an invalid opcode.
        NOTLOGGEDIN,   // Client is not logged in.
        LOGGEDIN       // CLient is currently logged in, ClientList contain this client detail.
    }
    
    final OpcodeDetail[] opcodeTable = 
    {
        new OpcodeDetail /* 0x00 */ ("UNKNOWN",                      SessionStatus.NEVER,       0, null                             ),
        new OpcodeDetail /* 0x01 */ ("CMSG_LOGIN",                   SessionStatus.NOTLOGGEDIN, 0, null                             ),
        new OpcodeDetail /* 0x02 */ ("CMSG_LOGOUT",                  SessionStatus.LOGGEDIN,    0, "HandleLogoutOpcode"             ),
        new OpcodeDetail /* 0x03 */ ("SMSG_LOGIN_SUCCESS",           SessionStatus.NEVER,       0, null                             ),
        new OpcodeDetail /* 0x04 */ ("SMSG_LOGIN_FAILED",            SessionStatus.NEVER,       0, null                             ),
        new OpcodeDetail /* 0x05 */ ("SMSG_MULTI_LOGIN",             SessionStatus.NEVER,       0, null                             ),
        new OpcodeDetail /* 0x06 */ ("CMSG_GET_CONTACT_LIST",        SessionStatus.LOGGEDIN,    0, "HandleGetContactListOpcode"     ),
        new OpcodeDetail /* 0x07 */ ("CMSG_ADD_CONTACT",             SessionStatus.LOGGEDIN,    1, "HandleAddContactOpcode"         ),
        new OpcodeDetail /* 0x08 */ ("CMSG_REMOVE_CONTACT",          SessionStatus.LOGGEDIN,    1, "HandleRemoveContactOpcode"      ),
        new OpcodeDetail /* 0x09 */ ("SMSG_ADD_CONTACT_SUCCESS",     SessionStatus.NEVER,       0, null                             ),
        new OpcodeDetail /* 0x0A */ ("CMSG_STATUS_CHANGED",          SessionStatus.LOGGEDIN,    1, "HandleStatusChangedOpcode"      ),
        new OpcodeDetail /* 0x0B */ ("SMSG_CONTACT_DETAIL",          SessionStatus.NEVER,       0, null                             ),
        new OpcodeDetail /* 0x0C */ ("SMSG_CONTACT_LIST_ENDED",      SessionStatus.NEVER,       0, null                             ),
        new OpcodeDetail /* 0x0D */ ("CMSG_SEND_CHAT_MESSAGE",       SessionStatus.LOGGEDIN,    2, "HandleChatMessageOpcode"        ),
        new OpcodeDetail /* 0x0E */ ("SMSG_SEND_CHAT_MESSAGE",       SessionStatus.NEVER,       0, null                             ),
        new OpcodeDetail /* 0x0F */ ("SMSG_STATUS_CHANGED",          SessionStatus.NEVER,       0, null                             ),
        new OpcodeDetail /* 0x10 */ ("SMSG_CONTACT_ALREADY_IN_LIST", SessionStatus.NEVER,       0, null                             ),
        new OpcodeDetail /* 0x11 */ ("SMSG_CONTACT_NOT_FOUND",       SessionStatus.NEVER,       0, null                             ),
        new OpcodeDetail /* 0x12 */ ("SMSG_CONTACT_REQUEST",         SessionStatus.NEVER,       0, null                             ),
        new OpcodeDetail /* 0x13 */ ("CMSG_CONTACT_ACCEPT",          SessionStatus.LOGGEDIN,    1, "HandleContactAcceptOpcode"      ),
        new OpcodeDetail /* 0x14 */ ("CMSG_CONTACT_DECLINE",         SessionStatus.LOGGEDIN,    1, "HandleContactDeclineOpcode"     ),
        new OpcodeDetail /* 0x15 */ ("CMSG_TIME_SYNC_RESP",          SessionStatus.LOGGEDIN,    2, "HandleTimeSyncRespOpcode"       ),
        new OpcodeDetail /* 0x16 */ ("CMSG_PING",                    SessionStatus.LOGGEDIN,    0, "HandlePingOpcode"               ),
        new OpcodeDetail /* 0x17 */ ("SMSG_PING",                    SessionStatus.LOGGEDIN,    0, null                             ),
        new OpcodeDetail /* 0x18 */ ("SMSG_LOGOUT_COMPLETE",         SessionStatus.NEVER,       0, null                             ),
        new OpcodeDetail /* 0x19 */ ("CMSG_TITLE_CHANGED",           SessionStatus.LOGGEDIN,    1, "HandleClientDetailChangedOpcode"),
        new OpcodeDetail /* 0x1A */ ("SMSG_TITLE_CHANGED",           SessionStatus.NEVER,       0, null                             ),
        new OpcodeDetail /* 0x1B */ ("CMSG_PSM_CHANGED",             SessionStatus.LOGGEDIN,    1, "HandleClientDetailChangedOpcode"),
        new OpcodeDetail /* 0x1C */ ("SMSG_PSM_CHANGED",             SessionStatus.NEVER,       0, null                             ),
        new OpcodeDetail /* 0x1D */ ("CMSG_CREATE_ROOM",             SessionStatus.LOGGEDIN,    2, "HandleCreateRoomOpcode"         ),
        new OpcodeDetail /* 0x1E */ ("SMSG_CREATE_ROOM_FAILED",      SessionStatus.NEVER,       0, null                             ),
        new OpcodeDetail /* 0x1F */ ("CMSG_JOIN_ROOM",               SessionStatus.LOGGEDIN,    2, "HandleJoinRoomOpcode"           ),
        new OpcodeDetail /* 0x20 */ ("CMSG_LEAVE_ROOM",              SessionStatus.LOGGEDIN,    1, "HandleLeaveRoomOpcode"          ),
        new OpcodeDetail /* 0x21 */ ("SMSG_JOIN_ROOM",               SessionStatus.NEVER,       0, null                             ),
        new OpcodeDetail /* 0x22 */ ("SMSG_LEAVE_ROOM",              SessionStatus.NEVER,       0, null                             ),
        new OpcodeDetail /* 0x23 */ ("SMSG_JOIN_ROOM_SUCCESS",       SessionStatus.NEVER,       0, null                             ),
        new OpcodeDetail /* 0x24 */ ("SMSG_LEAVE_ROOM_SUCCESS",      SessionStatus.NEVER,       0, null                             ),
        new OpcodeDetail /* 0x25 */ ("CMSG_ROOM_CHAT",               SessionStatus.LOGGEDIN,    2, "HandleRoomChatOpcode"           ),
        new OpcodeDetail /* 0x26 */ ("SMSG_ROOM_CHAT",               SessionStatus.NEVER,       0, null                             ),
        new OpcodeDetail /* 0x27 */ ("SMSG_ROOM_NOT_FOUND",          SessionStatus.NEVER,       0, null                             ),
        new OpcodeDetail /* 0x28 */ ("SMSG_WRONG_ROOM_PASSWORD",     SessionStatus.NEVER,       0, null                             ),
        new OpcodeDetail /* 0x29 */ ("SMSG_ROOM_MEMBER_DETAIL",      SessionStatus.NEVER,       0, null                             ),
        new OpcodeDetail /* 0x2A */ ("SMSG_ALREADY_IN_ROOM",         SessionStatus.NEVER,       0, null                             ),
        new OpcodeDetail /* 0x2B */ ("CMSG_REGISTRATION",            SessionStatus.NEVER,       0, null                             ),
        new OpcodeDetail /* 0x2C */ ("SMSG_REGISTRATION_SUCCESS",    SessionStatus.NEVER,       0, null                             ),
        new OpcodeDetail /* 0x2D */ ("SMSG_REGISTRATION_ALREADY",    SessionStatus.NEVER,       0, null                             ),
        new OpcodeDetail /* 0x2E */ ("CMSG_GET_CHAT_MESSAGE",        SessionStatus.LOGGEDIN,    1, "HandleChatMessageApprove"       ),
        new OpcodeDetail /* 0x2F */ ("SMSG_GET_CHAT_MESSAGE",        SessionStatus.NEVER,       0, null                             ),
        new OpcodeDetail /* 0x30 */ ("СMSG_ADD_NEW_SUBSCRIBE",       SessionStatus.LOGGEDIN,    1, "HandleAddNewSubOpcode"          ),
        new OpcodeDetail /* 0x31 */ ("CMSG_GET_SUBLIST",             SessionStatus.LOGGEDIN,    0, "HandleGetSubListOpcode"         ),
        new OpcodeDetail /* 0x32 */ ("SMSG_GET_SUBLIST_SUCCESS",     SessionStatus.LOGGEDIN,    0, null                             ),
        new OpcodeDetail /* 0x33 */ ("CMSG_SUBSCRIBE",               SessionStatus.LOGGEDIN,    1, "HandleSubscribeOpcode"          ),
        new OpcodeDetail /* 0x34 */ ("CMSG_UNSUBSCRIBE",             SessionStatus.LOGGEDIN,    1, "HandleUnSubscribeOpcode"        ),
        new OpcodeDetail /* 0x35 */ ("SMSG_SUBSCRIBE_SUCCESS",       SessionStatus.LOGGEDIN,    0, null                             ),
        new OpcodeDetail /* 0x36 */ ("SMSG_UNSUBSCRIBE_SUCCESS",     SessionStatus.LOGGEDIN,    0, null                             ),
        new OpcodeDetail /* 0x37 */ ("СMSG_SEND_IN_SUB",             SessionStatus.LOGGEDIN,    2, "HandleSendInSubOpcode"          ),
        new OpcodeDetail /* 0x38 */ ("SMSG_SEND_IN_SUB_SUCCESS",     SessionStatus.LOGGEDIN,    0, null                             ),
        new OpcodeDetail /* 0x39 */ ("SMSG_SEND_IN_SUB",             SessionStatus.LOGGEDIN,    6, null                             ),
        new OpcodeDetail /* 0x3A */ ("CMSG_GET_IN_SUB",              SessionStatus.LOGGEDIN,    1, "HandleGetInSubOpcode"           ),
        new OpcodeDetail /* 0x3B */ ("CMSG_GET_OFFLINE_MSG",         SessionStatus.LOGGEDIN,    0, "HandleGetOfflineMsgOpcode"      )
    };
    
    final class OpcodeDetail
    {
        final String name;
        final SessionStatus sessionStatus;
        final int length;
        final String handler;
        
        public OpcodeDetail(String name, SessionStatus status, int length, String Handler)
        {
            this.name = name;
            this.sessionStatus = status;
            this.length = length;
            this.handler = Handler;
        }
    }
}