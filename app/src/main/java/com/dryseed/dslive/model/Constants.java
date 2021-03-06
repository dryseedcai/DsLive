package com.dryseed.dslive.model;

import com.tencent.livesdk.ILVLiveConstants;

/**
 * Created by caiminming on 2017/11/8.
 */
public class Constants {
    //自定义发送列表聊天
    public static final int CMD_CHAT_MSG_LIST = ILVLiveConstants.ILVLIVE_CMD_CUSTOM_LOW_LIMIT + 1;
    //自定义发送弹幕聊天
    public static final int CMD_CHAT_MSG_DANMU = ILVLiveConstants.ILVLIVE_CMD_CUSTOM_LOW_LIMIT + 2;
    //自定义发送礼物
    public static final int CMD_CHAT_GIFT = ILVLiveConstants.ILVLIVE_CMD_CUSTOM_LOW_LIMIT + 3;
}
