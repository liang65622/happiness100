package com.happiness100.app.utils;

public final class Constants {

    public static final String CONTACTS = "contacts";
    public static final class URL {
        //        http://192.168.1.78:8080/xf100/web/reg_vercode
//        public static final String HOST = "http://100.64.13.86:8889/xf100/web";
 //       public static final String HOST = "http://119.29.102.152:8889/web";

//       public static final String HOST = "http://192.168.2.78:8889/xf100/web";
        public static final String HOST = "http://119.29.102.152:8889/web";

//       public static final String HOST = "http://192.168.2.78:8080/xf100/web";
//        public static final String HOST = "http://119.29.102.152:8889/web";

//       public static final String HOST = "http://192.168.2.78:8889/xf100/web";

//        public static final String HOST = "http://192.168.2.78:8889/xf100/web";
        public static final String IMG_URI = "img_uri";
        public static final String REQ_VERCODE = HOST + "/reg_vercode";
        public static final String LOGIN_MSG_VERCODE = HOST + "/login_msg_vercode";

        public static final String REQ_VALID_VERCODE = HOST + "/reg_valid_vercode";
        public static final String LOGIN_MSG_VALID_VERCODE = HOST + "/login_msg_valid_vercode";

        public static final String REGISTER = HOST + "/reg_confirm";
        public static final String LOGIN = HOST + "/login_confirm";
        public static final String LOGIN_AUTO = HOST + "/login_auto";

        public static final String UPDATE_NICK_NAME = HOST + "/modify_nickname";
        public static final String UPDATE_BIRTHDAY = HOST + "/modify_birthday";
        public static final String UPDATE_HEADVIEW = HOST + "/head_image_upload";
        public static final String UPDATE_SEX = HOST + "/modify_gender";
        public static final String UPDATE_PERSON_SIGN = HOST + "/modify_person_sign";
        public static final String UPDATE_QUERY_ADDRESS = HOST + "/query_address";
        public static final String UPDATE_ADD_ADDRESS = HOST + "/add_address";
        public static final String UPDATE_MODIFY_ADDRESS = HOST + "/modify_address";
        public static final String UPDATE_DELETE_ADDRESS = HOST + "/delete_address";
        public static final String EX_MOBILE_BIND_VERCODE = HOST + "/ex_mobile_bind_vercode";
        public static final String EX_MOBILE_BIND_CONFIRM = HOST + "/ex_mobile_bind_confirm";
        public static final String UPDATE_ZONE = HOST + "/modify_zone";
        public static final String MODIFY_PASSWORD = HOST + "/modify_password";
        public static final String CHECK_PASSWORD = HOST + "/check_password";
        public static final String BIND_EMAIL = HOST + "/bind_mail";
        public static final String UNBIND_EMAIL = HOST + "/unbind_mail";
        public static final String CHECK_BIND_EMAIL = HOST + "/check_bind_mail";

        public static final String FIND_PASSWORD_MAIL = HOST + "/mail_find_password";

        public static final String GET_EMAIL_STATUS = HOST + "/get_mail_chk_status";
        //通信
        public static final String GET_FRIENDS = HOST + "/tx/friends";
        public static final String SEARCH_FRIENDS = HOST + "/tx/friend_search";
        public static final String APPLY_FRIENDS = HOST + "/tx/friend_app";
        public static final String AGREE_FRIENDS = HOST + "/tx/friend_agree";
        public static final String REMOVE_FRIENDS = HOST + "/tx/friend_remove";
        public static final String INFO_FRIENDS = HOST + "/tx/friend_info";
        public static final String ALL_HEAD_IMAGE = HOST + "/tx/discus_all_head_image";
        public static final String SYN_GROUP = HOST + "/tx/discus_list_syn";
        public static final String GET_GROUP_LIST = HOST + "/tx/discus_list";
        public static final String FRIEND_MODIFY_NOTE = HOST + "/tx/friend_modify_note";
        public static final String USER_NOTE = HOST + "/user_note";

        //设置搜索掩码
        public static final String UPDATE_SEARCH_MASK = HOST + "/update_search_mask";

        public static final String EALTH_INDEX = HOST + "/health_index";//健康数据
        public static final String EALTH_DETAIL = HOST + "/health_detail";//健康详情

        //家族模块
        public static final String GET_CLIAN_LIST = HOST + "/clan/get_clan_list";//获取单个用户所属家族列表
        public static final String CREATE_CLAN = HOST + "/clan/create_clan";//获取单个用户所属家族列表
        public static final String TXL_MATCH_CLAN = HOST + "/clan/txl_match_clan";//通讯录匹配家族
        public static final String TXT_SEARCH_CLAN = HOST + "/clan/search_clan";//搜索家庭列表
        public static final String CLAN_INDEX = HOST + "/clan/clan_index";//家族首页获取家族成员
        public static final String PREVIEW_CLAN = HOST + "/clan/preview_clan";//家族预览
        public static final String APP_JOIN_CLAN = HOST + "/clan/app_join_clan";//申请加入家族
        public static final String TXL_MATCH_ACCOUNT = HOST + "/clan/txl_match_account";//申请加入家族
        public static final String CLAN_APP_LIST = HOST + "/clan/clan_app_list";//申请加入家族
        public static final String CLAN_RANK = HOST + "/clan/clan_rank";//排行榜
        public static final String CLAN_INVITE_JOIN_CLAN = HOST + "/clan/invite_join_clan";//邀请 加入家族
        public static final String CLAN_INVITE_CFM_JOIN_CLAN = HOST + "/clan/invite_cfm_join_clan";//确认邀请 加入家族

        public static final String CLAN_ACCEPT_JOIN_CLAN = HOST + "/clan/accept_join_clan";//同意 用户的加入家族申请
        public static final String CLAN_MODIFY_CLAN_LOGO = HOST + "/clan/modify_clan_logo";//修改公告头像

        public static final String CLAN_XING_FU_DTON_TAI = HOST + "/clan/xing_fu_dong_tai";//家族动态
        public static final String CLAN_TICK_OUT_CLAN = HOST + "/clan/r_tick_out_clan";//移出家族
        public static final String CLAN_ROLE_SET_UP = HOST + "/clan/clan_role_set_up";//提升为管理员
        public static final String CLAN_ROLE_SET_DOWN = HOST + "/clan/clan_role_set_down";//降为普通成员

        public static final String CLAN_APP_DEFUSED = HOST + "/clan/app_defused";//拒绝加入家族
        public static final String CLAN_R_QUIT_CLAN = HOST + "/clan/r_quit_clan";//退出家族
        public static final String CLAN_MODIFY_CLAN_GONG_GAO = HOST + "/clan/modify_clan_gong_gao";//退出家族
        public static final String CLAN_VIEW_OTHER_USER = HOST + "/clan/view_other_user";//成员健康信息
        public static final String CLAN_MODIFY_CW_NOTE = HOST + "/clan/modify_cw_note";//修改称谓
        public static final String CLAN_INVITE_AGREE_JOIN = HOST + "/clan/invite_agree_join_clan";//邀请同意加入家族
        public static final String CLAN_VIEW_OTHER_USER_DIAN_ZAN = HOST + "/clan/view_other_user_dian_zan";//邀请同意加入家族


        //族谱模块
        public static final String CLAN_PU_ADD_MEMBER = HOST + "/clan/pu_add_member";//增加家谱成员
        public static final String CLAN_PU_EDIT_MEMBER = HOST + "/clan/pu_edit_member";//增加家谱成员
        public static final String APP_UPDATE = "http://119.29.102.152:8080/version/a_version.txt";//更新


        public static final String CLAN_PU_VIEW = HOST + "/clan/pu_view";//
    }

    public static final class UserInfo {
        public static final String APP_USER_ID = "user_id";
        public static final String PHONE = "phone";
        public static final String HEAD_IMG = "headImage";
        public static final String HEAD_IMG_URI = "headImageUri";
        public static final String STATIC_URI = "staticUri";
        public static final String NICK_NAME = "nickname";
        public static final String HAPPINESS_NUM = "xf";
        public static final String SEX = "sex";
        public static final String EMAIL = "email";
        public static final String ZONE = "zone";
        public static final String PERSON_SIGN = "personSign";
        public static final String SESSION_ID = "session_id";
        public static final String IS_PWD_NULL = "is_pwd_null";
        public static final String LOGOUT = "logout";
        public static final String PASSWORD = "password";
        public static final String SEARCHMASK = "SearchMask";
    }

    public static final class SpKey {
        public static final String HAS_APPLY_TIPS = "has_apply_tips";
        public static final String HAS_NEW_VERSION = "HAS_NEW_VERSION";
        public static final String VERSION_APP_URL = "VERSION_APP_URL";

    }


    public static final class Function {
        public static final String AUDIO = "audio";//声音
        public static final String SHAKE = "shake";//震动
        public static final String NODISTURBING = "nodisturbing";//免打扰模式

        public static final String RECVNOTIFY = "recvnotify";//接受新消息通知
        public static final String SHOWNOTIFYCONTENT = "shownotifycontent";//显示新消息通知内容
        public static final String RECOMMEND = "recommend";//推荐本地通讯录里能添加为好友的人到新的动态页面
        public static final String SEARCHBYMOBILE = "searchbymobile";//是否能通过电话查找到自己
        public static final String SEARCHBYXF = "searchbyxf";//是否能通过幸福号查找到自己
        public static final String BACKGROUD = "backgroud";//背景图片
        public static final int RESULT_MODIFYPICTURE = 0x123;//修改图片
    }



    public static final class NetWork {
        public static final int SUCCESS = 0;
        public static final int ERROR = 0;
        public static final int LOGIN_OTHER_DEVICE = -2;
        public static final String TRUE = "true";
        public static final String FALSE = "false";
        public static final int LOGIN = 1;
        public static final int REGISTER = 2;

        public static final int ALREADY_INVITE = 102;//已经邀请
    }

    public static final class NoDisturbStatus {
        public static final int CLOSE = 0;
        public static final int OPEN = 1;
        public static final int OPENLIMITE = 2;
    }

    public static final class FriendHandle {
        public static final int None = 0x100;
        public static final int Delete = 0x101;
        public static final int Modify = 0x102;
    }

    public static final class ActivityIntentIndex {
        public static final int FriendDetailActivityIndex = 0x1001;
        public static final int ConversationDetailedActivtyIndex = 0x1002;
        public static final int FriendDataSetActivityIndex = 0x1003;
        public static final int ConversationActivityIndex = 0x1004;
        public static final int GroupDetailActivityIndex = 0x1005;
        public static final int GroupModifyNameActivityIndex = 0x1006;
        public static final int GroupAddMemberActivityIndex = 0x1007;
        public static final int GroupRemoveMemberActivityIndex = 0x1008;
        public static final int GroupQRCodeActivityIndex = 0x1009;
        public static final int UserInfoActivityIndex = 0x100A;
        public static final int RemarkActivityIndex = 0x100B;
        public static final int AddFriendVerificationActivityIndex = 0x100C;
        public static final int ConversationModifyBackGroudActivityIndex = 0x100D;
    }

    public static final class RequestCode {
        public static final int REQUEST_SETTINGS = 0x100;
        public static final int _SETTINGS = 0x100;
        public static final int CHANGE_FAMILY = 0x200;
        public static final int FAMILY_INFO = 0x200;
    }

    public static final class ResultCode {
        public static final int LOG_OUT = 0x100;
        public static final int EXIT_FAMILY = 0x100;
    }


    public static final class QrCode {
        public static final String QrCodeIndex = "happness_love_";
    }
}
